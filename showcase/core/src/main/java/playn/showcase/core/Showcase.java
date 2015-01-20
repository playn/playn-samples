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
package playn.showcase.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import react.Closeable;
import react.Signal;
import react.Slot;

import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Platform;
import playn.core.Touch;
import playn.scene.Pointer;
import playn.scene.SceneGame;

import playn.showcase.core.peas.PeasDemo;
import playn.showcase.core.snake.SnakeDemo;
import playn.showcase.core.sprites.SpritesDemo;
import playn.showcase.core.swirl.SwirlDemo;
import playn.showcase.core.text.TextDemo;

/**
 * The main entry point for the showcase "game".
 */
public class Showcase extends SceneGame {

  private final Set<Key> backKeys = EnumSet.of(Key.ESCAPE, Key.BACK);
  private final Demo menuDemo = new Menu();
  private Demo activeDemo;
  private Closeable activeHandle = Closeable.Util.NOOP;
  private int activeStamp;

  public interface DeviceService {
    /** Returns info on the device. */
    String info();
  }

  public static abstract class Demo {
    public final String name;
    public Demo (String name) {
      this.name = name;
    }
    public abstract void create (Showcase game, Closeable.Set onClose);
  }

  public final DeviceService deviceService;

  /** A signal emitted when the device is rotated. TODO: this should be part of the platform. */
  public final Signal<Showcase> rotate = Signal.create();

  public final List<Demo> demos = new ArrayList<Demo>(); {
    // add your demo here to enable it in the showcase
    demos.add(new SpritesDemo());
    demos.add(new PeasDemo());
    demos.add(new SwirlDemo());
    demos.add(new SnakeDemo());
    demos.add(new TextDemo());
  }

  public Showcase (Platform plat, DeviceService deviceService) {
    super(plat, 25); // 25 simulation updates per second
    this.deviceService = deviceService;

    // wire up a layer pointer dispatcher
    new Pointer(plat, rootLayer, true);

    plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
      @Override public void onEmit (Keyboard.KeyEvent event) {
        if (event.down && backKeys.contains(event.key)) activateDemo(menuDemo);
      }
    });
    plat.input().touchEvents.connect(new Slot<Touch.Event[]>() {
      public void onEmit (Touch.Event[] touches) {
        if (touches.length > 1 && touches[0].kind.isStart) activateDemo(menuDemo);
      }
    });
    activateDemo(menuDemo);
  }

  public boolean shouldExitOnBack () {
    // the BACK button will get procesesd by Android immediately *after* we move to the main menu,
    // so we want to debounce things so only if you press back after you're already on the main
    // menu do we allow the app to exit via the normal back button processing
    return (activeDemo == menuDemo) && (plat.tick() - activeStamp) > 500L;
  }

  public void activateDemo (Demo demo) {
    activeHandle = Closeable.Util.close(activeHandle);
    activeDemo = demo;
    Closeable.Set onClose = new Closeable.Set();
    demo.create(this, onClose);
    activeHandle = onClose;
    activeStamp = plat.tick();
  }
}
