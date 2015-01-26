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
package playn.showcase.core.peas.entities;

import react.Slot;

import playn.core.Clock;
import playn.core.Image;
import playn.scene.ImageLayer;

import playn.showcase.core.peas.PeaWorld;

public abstract class Entity {

  final PeaWorld world;
  public final ImageLayer layer;
  public final Image image;
  float x, y, angle;

  public Entity(final PeaWorld peaWorld, Image image, float px, float py, float pangle) {
    this.world = peaWorld;
    this.image = image;
    this.x = px;
    this.y = py;
    this.angle = pangle;
    layer = new ImageLayer(image);
    layer.setOrigin(ImageLayer.Origin.CENTER);
    layer.setTranslation(px, py);
    layer.setRotation(pangle);
    initPreLoad(peaWorld);
    image.state.onSuccess(new Slot<Image>() {
      @Override public void onEmit(Image image) {
        // now that the image is loaded, we can use its width and height
        layer.setScale(getWidth() / image.width(), getHeight() / image.height());
        initPostLoad(peaWorld);
      }
    });
  }

  /**
   * Perform pre-image load initialization (e.g., attaching to PeaWorld layers).
   */
  public void initPreLoad (PeaWorld peaWorld) {}

  /**
   * Perform post-image load initialization (e.g., attaching to PeaWorld layers).
   */
  public void initPostLoad (PeaWorld peaWorld) {}

  public void update (Clock clock) {}
  public void paint (Clock clock) {}

  public void setPos(float x, float y) {
    layer.setTranslation(x, y);
  }

  public void setAngle(float a) {
    layer.setRotation(a);
  }

  abstract float getWidth();
  abstract float getHeight();
}
