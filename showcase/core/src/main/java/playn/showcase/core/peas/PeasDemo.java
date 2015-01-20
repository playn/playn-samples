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
package playn.showcase.core.peas;

import react.Closeable;
import react.Slot;

import playn.core.Image;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Pointer;

import playn.showcase.core.Showcase;
import playn.showcase.core.peas.entities.Pea;

public class PeasDemo extends Showcase.Demo {

  // scale difference between screen space (pixels) and world space (physics).
  public static float physUnitPerScreenUnit = 1 / 26.666667f;

  public PeasDemo () { super("Pea Physics"); }

  @Override public void create (Showcase game, final Closeable.Set onClose) {
    // load and show our background image
    Image bgImage = game.plat.assets().getImage("background.png");
    final ImageLayer bg = new ImageLayer(bgImage).setSize(game.plat.graphics().viewSize);
    game.rootLayer.add(bg);

    // create our world layer (scaled to "world space")
    GroupLayer worldLayer = new GroupLayer();
    worldLayer.setScale(1f / physUnitPerScreenUnit);
    game.rootLayer.add(worldLayer);
    onClose.add(worldLayer);

    final PeaWorld world = new PeaWorld(game, worldLayer);

    // load a level, and wait for it to be loaded before we wire up input
    world.loadLevel(game.plat, "peas/levels/level1.json").onSuccess(new Slot<PeaWorld>() {
      @Override public void onEmit (final PeaWorld world) {
        // since our background covers the whole screen, listen on it for events
        bg.events().connect(new Pointer.Listener() {
          @Override public void onStart(Pointer.Interaction iact) {
            Pea pea = new Pea(world, world.world, physUnitPerScreenUnit * iact.event.x,
                              physUnitPerScreenUnit * iact.event.y, 0);
            world.add(pea);
          }
        });
      }
    });
  }
}
