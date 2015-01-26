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

import react.Slot;

import playn.core.Clock;
import playn.core.Platform;
import playn.scene.GroupLayer;
import playn.scene.Layer;

public class Pea {

  public static String IMAGE = "sprites/peasprites.png";
  public static String JSON = "sprites/peasprite.json";
  public static String JSON_WITH_IMAGE = "sprites/peasprite2.json";

  private Sprite sprite;
  private int spriteIndex = 0;
  private boolean hasLoaded = false; // set to true when resources have loaded and we can update

  public Pea (Platform plat, final GroupLayer peaLayer, float x, float y) {
    // Sprite method #1: use a sprite image and json data describing the sprites
    sprite = SpriteLoader.getSprite(plat, IMAGE, JSON);
    sprite.layer.setOrigin(Layer.Origin.CENTER);
    sprite.layer.setTranslation(x, y);

    // Sprite method #2: use json data describing the sprites and containing the image urls
    // sprite = SpriteLoader.getSprite(JSON_WITH_IMAGE);

    // Add a callback for when the image loads.
    // This is necessary because we can't use the width/height (to center the
    // image) until after the image has been loaded
    sprite.state.onSuccess(new Slot<Sprite>() {
      @Override public void onEmit(Sprite sprite) {
        sprite.setSprite(spriteIndex);
        peaLayer.add(sprite.layer);
        hasLoaded = true;
      }
    });
  }

  public void update(Clock clock) {
    if (hasLoaded) {
      if (Math.random() > 0.95) {
        spriteIndex = (spriteIndex + 1) % sprite.numSprites();
        sprite.setSprite(spriteIndex);
      }
      sprite.layer.setRotation(clock.tick/1000f);
    }
  }
}
