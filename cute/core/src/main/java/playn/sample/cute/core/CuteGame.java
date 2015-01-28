/**
 * Copyright 2010 The PlayN Authors
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
package playn.sample.cute.core;

import java.util.HashMap;
import java.util.Map;

import playn.core.*;
import playn.scene.Layer;
import playn.scene.SceneGame;

import react.Slot;

public class CuteGame extends SceneGame {

  private static final int NUM_STARS = 10;

  private static Map<Key, Integer> ADD_TILE_KEYS = new HashMap<Key, Integer>();
  static {
    int idx = 0;
    for (Key key : new Key[] {
        Key.K1, Key.K2, Key.K3, Key.K4, Key.K5, Key.K6, Key.K7, Key.K8,
        Key.W, Key.D, Key.S, Key.A, Key.T, Key.Y, Key.H, Key.N, Key.B, Key.V, Key.F, Key.R }) {
      ADD_TILE_KEYS.put(key, idx++);
    }
  }

  private final Pointer pointer;
  private final Layer gameLayer;
  private float frameAlpha;

  private final CuteWorld world;
  private CuteObject catGirl;
  private final CuteObject[] stars;

  private boolean controlLeft, controlRight, controlUp, controlDown;
  private boolean controlJump;
  private float touchVectorX, touchVectorY;

  public CuteGame(Platform plat) {
    super(plat, 33);
    // graphics().setSize(800, 600);

    plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
      @Override public void onEmit (Keyboard.KeyEvent event) {
        if (event.down) {
          Integer tileIdx = ADD_TILE_KEYS.get(event.key);
          if (tileIdx != null) {
            addTile((int) catGirl.x, (int) catGirl.y, tileIdx);
            return;
          }
        }

        switch (event.key) {
          case SPACE:
            if (event.down) controlJump = true;
            break;
          case ESCAPE:
            if(event.down) removeTopTile((int) catGirl.x, (int) catGirl.y);
            break;
          case LEFT:
            controlLeft = event.down;
            break;
          case UP:
            controlUp = event.down;
            break;
          case RIGHT:
            controlRight = event.down;
            break;
          case DOWN:
            controlDown = event.down;
            break;
          default:
            break; // nada
        }
      }
    });

    pointer = new Pointer(plat);
    pointer.events.connect(new Slot<Pointer.Event>() {
      @Override public void onEmit (Pointer.Event event) {
        switch (event.kind) {
          case START: touchMove(event.x, event.y); break;
          case  DRAG: touchMove(event.x, event.y); break;
          default: // END/CANCEL
            touchVectorX = touchVectorY = 0;
            break;
        }
      }
    });

    world = new CuteWorld(plat, 16, 16);

    // Grass.
    for (int y = 0; y < 16; ++y) {
      for (int x = 0; x < 16; ++x) {
        world.addTile(x, y, 2);
      }
    }

    // And a little house.
    for (int i = 0; i < 2; ++i) {
      world.addTile(4, 4, 7);
      world.addTile(5, 4, 7);
      world.addTile(6, 4, 7);
      world.addTile(4, 5, 7);
      world.addTile(5, 5, 7);
      world.addTile(6, 5, 7);
      world.addTile(4, 6, 7);
      world.addTile(5, 6, 3);
      world.addTile(6, 6, 7);
    }

    world.addTile(4, 4, 19);
    world.addTile(5, 4, 12);
    world.addTile(6, 4, 13);
    world.addTile(4, 5, 18);
    world.addTile(5, 5,  5);
    world.addTile(6, 5, 14);
    world.addTile(4, 6, 17);
    world.addTile(5, 6, 16);
    world.addTile(6, 6, 15);

    // create an immediate layer that handles all of our rendering
    rootLayer.add(gameLayer = new Layer() {
      @Override protected void paintImpl (Surface surface) {
        if (catGirl != null) world.setViewOrigin(
          catGirl.x(frameAlpha), catGirl.y(frameAlpha), catGirl.z(frameAlpha));
        surface.clear();
        world.paint(surface, frameAlpha);
      }
    });

    plat.assets().getImage("images/character_cat_girl.png").state.onSuccess(new Slot<Image>() {
      public void onEmit (Image image) {
        catGirl = new CuteObject(image.texture());
        catGirl.setPos(2, 2, 1);
        catGirl.r = 0.3;
        world.addObject(catGirl);

        update.connect(new Slot<Clock>() {
          public void onEmit (Clock clock) {
            catGirl.setAcceleration(0, 0, 0);

            if (catGirl.isResting()) {
              // Keyboard control.
              if (controlLeft) catGirl.ax = -1.0;
              if (controlRight) catGirl.ax = 1.0;
              if (controlUp) catGirl.ay = -1.0;
              if (controlDown) catGirl.ay = 1.0;

              // Mouse Control.
              catGirl.ax += touchVectorX;
              catGirl.ay += touchVectorY;

              // Jump Control.
              if (controlJump) {
                catGirl.vz = 0.2;
                controlJump = false;
              }
            }
          }
        });
      }
    });

    stars = new CuteObject[NUM_STARS];
    plat.assets().getImage("images/star.png").state.onSuccess(new Slot<Image>() {
      public void onEmit (Image image) {
        for (int i = 0; i < NUM_STARS; ++i) {
          stars[i] = new CuteObject(image.texture());
          stars[i].setPos(Math.random() * world.worldWidth(),
                          Math.random() * world.worldHeight(), 10);
          world.addObject(stars[i]);
        }
      }
    });
  }

  @Override public void update(Clock clock) {
    super.update(clock);
    world.updatePhysics(clock.dt / 1000f);
  }

  @Override public void paint(Clock clock) {
    // save this, as we'll use it in our immediate layer renderer
    frameAlpha = clock.alpha;
    super.paint(clock);
  }

  private void touchMove(float x, float y) {
    float cx = plat.graphics().viewSize.width() / 2;
    float cy = plat.graphics().viewSize.height() / 2;

    touchVectorX = (x - cx) * 1.0f / cx;
    touchVectorY = (y - cy) * 1.0f / cy;
  }

  private void addTile(int x, int y, int type) {
    world.addTile(x, y, type);

    // Json.Writer w = plat.json().newWriter();
    // w.object();
    // w.value("op", "addTop");
    // w.value("x", x);
    // w.value("y", y);
    // w.value("type", type);
    // w.end();

    // post(w.write());
  }

  private void removeTopTile(int x, int y) {
    world.removeTopTile(x, y);

    // Json.Writer w = plat.json().newWriter();
    // w.object();
    // w.value("op", "removeTop");
    // w.value("x", x);
    // w.value("y", y);
    // w.end();

    // post(w.write());
  }

  private void post(String payload) {
    plat.net().post("/rpc", payload).onSuccess(new Slot<String>() {
      public void onEmit (String rsp) {} // TODO
    });
  }
}
