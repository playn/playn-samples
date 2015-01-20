/**
 * Copyright 2011 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package playn.showcase.core.sprites;

import java.util.ArrayList;
import java.util.List;

import react.Functions;
import react.RFuture;
import react.RPromise;
import react.Slot;

import playn.core.Image;
import playn.core.Json;
import playn.core.Platform;

/**
 * Class for loading and parsing sprite sheets. To use, call {@link #getSprite(String,String)} with
 * an image path and json data, or {@link #getSprite(String)} with json data containing image urls.
 *
 * <p>Json data should be in the following format: <pre>{@code
 * { "sprites": [
 *     {"id": "sprite_0", "x": 30, "y": 30, "w": 37, "h": 37},
 *     {"id": "sprite_1", "x": 67, "y": 30, "w": 37, "h": 37},
 *     {"id": "sprite_2", "x": 104, "y": 30, "w": 37, "h": 37},
 *     {"id": "sprite_3", "x": 141, "y": 30, "w": 37, "h": 37}
 * ]}
 * }</pre>
 */
public class SpriteLoader {

  /** Return a {@link Sprite}, given paths to the image and to the json sprite description. */
  public static Sprite getSprite (Platform plat, String imagePath, String jsonPath) {
    return getSprite(plat, jsonPath, new Image[] { plat.assets().getImage(imagePath) });
  }

  /** Return a {@link Sprite}, given a path to the json sprite description. */
  public static Sprite getSprite (Platform plat, String jsonPath) {
    return getSprite(plat, jsonPath, (Image[])null);
  }

  private static Sprite getSprite (final Platform plat, String jsonPath, final Image[] images) {
    final RPromise<Sprite> state = RPromise.create();
    final Sprite sprite = new Sprite(state);
    // load and parse json
    plat.assets().getText(jsonPath).onFailure(state.failer()).onSuccess(new Slot<String>() {
      @Override public void onEmit (String json) {
        try {
          loadSprite(plat, images, sprite, plat.json().parse(json), state);
        } catch (Throwable err) {
          err.printStackTrace(System.err);
          state.fail(err);
        }
      }
    });
    return sprite;
  }

  private static void loadSprite (Platform plat, Image[] images, Sprite sprite, Json.Object json,
                                  RPromise<Sprite> state) {
    // parse image urls, if necessary
    if (images == null) {
      Json.Array urls = json.getArray("urls");
      assert urls != null : "No urls provided for sprite images";
      images = new Image[urls.length()];
      for (int ii = 0; ii < urls.length(); ii++) {
        images[ii] = plat.assets().getImage(urls.getString(ii));
      }
    }

    // parse the sprite images
    Json.Array spriteImages = json.getArray("sprites");
    for (int i = 0; i < spriteImages.length(); i++) {
      Json.Object jsonSpriteImage = spriteImages.getObject(i);
      String id = jsonSpriteImage.getString("id");
      int imageId = jsonSpriteImage.getInt("url"); // will return 0 if not specified
      assert imageId < images.length : "URL must be an index into the URLs array";
      int x = jsonSpriteImage.getInt("x");
      int y = jsonSpriteImage.getInt("y");
      int width = jsonSpriteImage.getInt("w");
      int height = jsonSpriteImage.getInt("h");
      SpriteImage spriteImage = new SpriteImage(images[imageId], x, y, width, height);
      sprite.addSpriteImage(id, spriteImage);
    }

    // complete the sprite once the (zero or more) images have finished loading
    List<RFuture<Image>> states = new ArrayList<>();
    for (Image image : images) states.add(image.state);
    RFuture.collect(states).map(Functions.constant(sprite)).onComplete(state.completer());
  }
}
