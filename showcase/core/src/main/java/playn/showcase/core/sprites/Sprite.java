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
import java.util.HashMap;
import java.util.List;

import pythagoras.f.Rectangle;

import react.RFuture;

import playn.scene.ImageLayer;

/**
 * A Sprite is a collection of {@link SpriteImage}s.
 *
 * <p>Create a Sprite from an image and json data using
 * {@link SpriteLoader#getSprite(String imageUrl, String jsonUrl)}.
 *
 * <p>Create a Sprite from json data using {@link SpriteLoader#getSprite(String json)}.
 *
 * <p>To use, add {@link #layer()} to a {@link playn.core.Layer} in your game. To change sprites,
 * call {@link #setSprite(int)}.
 */
public class Sprite {

  private List<SpriteImage> spriteImages;
  private HashMap<String, Integer> spriteIdMap;
  private SpriteImage current;
  private int currentId = -1;
  private boolean imagesDone = false; // true when images have finished loading
  private boolean dataDone = false; // true when sprite data has finished loading

  /** This sprite's image layer. */
  public final ImageLayer layer = new ImageLayer();

  /** A future that completes when this sprite is fully loaded. */
  public final RFuture<Sprite> state;

  /**
   * Do not call directly. Create using {@link SpriteLoader#getSprite(String, String)}
   */
  Sprite (RFuture<Sprite> state) {
    this.state = state;
    layer.region = new Rectangle();
    spriteImages = new ArrayList<SpriteImage>(0);
    spriteIdMap = new HashMap<String, Integer>();
  }

  /** Return the number of sprites. */
  public int numSprites () { return spriteImages.size(); }
  /** Return the width of the current sprite. */
  public float width() { return (current != null) ? current.width() : 1; }
  /** Return the height of the current sprite. */
  public float height () { return (current != null) ? current.height() : 1; }

  /**
   * Set the current sprite via the index.
   * <p> The index is an integer between 0 and the number of sprites (@see {@link #numSprites()})
   */
  public void setSprite (int index) {
    assert index < spriteImages.size() : "Invalid sprite index";
    if (index != currentId) {
      current = spriteImages.get(index);
      currentId = index;
      if (current != null) {
        layer.setTile(current.image().texture());
        layer.region.setBounds(current.x(), current.y(), current.width(), current.height());
      }
    }
  }

  /**
   * Set the current sprite via the sprite's id.
   */
  public void setSprite (String id) {
    Integer index = spriteIdMap.get(id);
    assert index != null : "Invalid sprite id";
    setSprite(index);
  }

  /**
   * Add a {@link SpriteImage} to the sprites.
   */
  void addSpriteImage(String key, SpriteImage spriteImage) {
    spriteIdMap.put(key, spriteImages.size());
    spriteImages.add(spriteImage);
  }

  /** Returns the {@link SpriteImage}s associated with this Sprite. */
  List<SpriteImage> spriteImages() {
    return spriteImages;
  }
}
