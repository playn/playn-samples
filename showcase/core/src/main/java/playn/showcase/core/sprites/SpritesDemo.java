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

import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Pointer;
import playn.core.Sound;
import static playn.core.PlayN.*;

import playn.showcase.core.Demo;

public class SpritesDemo extends Demo {
  private GroupLayer layer;
  private List<Pea> peas = new ArrayList<Pea>(0);
  private Sound ding;

  @Override
  public String name() {
    return "Sprites";
  }

  @Override
  public void init() {
    // create a group layer to hold everything
    layer = graphics().createGroupLayer();
    graphics().rootLayer().add(layer);

    // load a sound that we'll play when placing sprites
    ding = assets().getSound("sprites/ding");

    // create and add background image layer
    Image bgImage = assets().getImage("background.png");
    ImageLayer bgLayer = graphics().createImageLayer(bgImage);
    bgLayer.setSize(graphics().screenWidth(), graphics().screenHeight());
    layer.add(bgLayer);

    // add a listener for pointer (mouse, touch) input
    pointer().setListener(new Pointer.Adapter() {
      @Override
      public void onPointerEnd(Pointer.Event event) {
        addPea(event.x(), event.y());
      }
    });

    addPea(graphics().width() / 2, graphics().height() / 2);
  }

  private void addPea(float x, float y) {
    Pea pea = new Pea(layer, x, y);
    peas.add(pea);
    ding.play();
  }

  @Override
  public void shutdown() {
    pointer().setListener(null);

    layer.destroy();
    layer = null;
  }

  @Override
  public void paint(float alpha) {
    // layers automatically paint themselves (and their children). The rootlayer
    // will paint itself, the background, and the pea group layer automatically
    // so no need to do anything here!
  }

  @Override
  public void update(int delta) {
    for (Pea pea : peas) {
      pea.update(delta);
    }
  }
}
