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

import pythagoras.f.IDimension;

import react.Closeable;
import react.Slot;

import playn.core.Clock;
import playn.core.Image;
import playn.core.Platform;
import playn.core.Sound;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Pointer;

import playn.showcase.core.Showcase;

public class SpritesDemo extends Showcase.Demo {

  public SpritesDemo () { super("Sprites"); };

  @Override public void create (final Showcase game, Closeable.Set onClose) {
    // create a group layer to hold everything
    final GroupLayer layer = new GroupLayer();
    game.rootLayer.add(layer);
    onClose.add(layer);

    // load a sound that we'll play when placing sprites
    final Sound ding = game.plat.assets().getSound("sprites/ding");

    // create and add background image layer
    Image bgImage = game.plat.assets().getImage("background.png");
    IDimension viewSize = game.plat.graphics().viewSize;
    ImageLayer bg = new ImageLayer(bgImage).setSize(viewSize);
    layer.add(bg);

    final List<Pea> peas = new ArrayList<>();

    // add a listener for pointer (mouse, touch) input
    bg.events().connect(new Pointer.Listener() {
      public void onStart (Pointer.Interaction iact) {
        peas.add(newPea(game.plat, layer, ding, iact.event.x, iact.event.y));
      }
    });

    peas.add(newPea(game.plat, layer, ding, viewSize.width() / 2, viewSize.height() / 2));

    onClose.add(game.paint.connect(new Slot<Clock>() {
      public void onEmit (Clock clock) {
        for (Pea pea : peas) pea.update(clock);
      }
    }));
  }

  private Pea newPea(Platform plat, GroupLayer layer, Sound ding, float x, float y) {
    ding.play();
    return new Pea(plat, layer, x, y);
  }
}
