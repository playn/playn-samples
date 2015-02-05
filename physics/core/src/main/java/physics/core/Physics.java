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
package physics.core;

import pythagoras.f.IDimension;
import react.Slot;

import playn.core.*;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Pointer;
import playn.scene.SceneGame;

import physics.core.entities.Pea;

public class Physics extends SceneGame {

  // defines physics space: 12x10 physics "units"
  public static float physWidth = 24, physHeight = 18;
  public float physScaleX, physScaleY;

  public Physics (Platform plat) {
    super(plat, 25); // 25 simulation updates per second

    // wire up a layer pointer dispatcher
    new Pointer(plat, rootLayer, true);

    // load and show our background image
    Image bgImage = plat.assets().getImage("background.png");
    final ImageLayer bg = new ImageLayer(bgImage).setSize(plat.graphics().viewSize);
    rootLayer.add(bg);

    IDimension viewSize = plat.graphics().viewSize;
    physScaleX = physWidth / viewSize.width();
    physScaleY = physHeight/ viewSize.height();

    // create our world layer (scaled to "world space")
    GroupLayer worldLayer = new GroupLayer();
    worldLayer.setScale(1f / physScaleX, 1f / physScaleY);
    rootLayer.add(worldLayer);

    final PeaWorld world = new PeaWorld(this, worldLayer);

    // load a level, and wait for it to be loaded before we wire up input
    world.loadLevel(plat, "levels/level1.json").onSuccess(new Slot<PeaWorld>() {
      @Override public void onEmit (final PeaWorld world) {
        // since our background covers the whole screen, listen on it for events
        bg.events().connect(new Pointer.Listener() {
          @Override public void onStart(Pointer.Interaction iact) {
            float px = physScaleX * iact.event.x, py = physScaleY * iact.event.y;
            Pea pea = new Pea(world, world.world, px, py, 0);
            world.add(pea);
          }
        });
      }
    });
  }
}
