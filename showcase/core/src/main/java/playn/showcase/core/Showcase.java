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

import playn.core.Game;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.PlayN;
import playn.core.Touch;

import playn.showcase.core.peas.PeasDemo;
import playn.showcase.core.snake.SnakeDemo;
import playn.showcase.core.sprites.SpritesDemo;
import playn.showcase.core.swirl.SwirlDemo;
import playn.showcase.core.text.TextDemo;

/**
 * The main entry point for the showcase "game".
 */
public class Showcase extends Game.Default {

  private final Set<Key> backKeys = EnumSet.of(Key.ESCAPE, Key.BACK);
  private final Demo menuDemo = new Menu(this);
  private Demo activeDemo;
  private long activeStamp;

  public interface DeviceService {
    /** Returns info on the device. */
    String info();
  }

  public final DeviceService deviceService;

  public final List<Demo> demos = new ArrayList<Demo>(); {
    // add your demo here to enable it in the showcase
    demos.add(new SpritesDemo());
    demos.add(new PeasDemo());
    demos.add(new SwirlDemo());
    demos.add(new SnakeDemo());
    demos.add(new TextDemo());
  }

  public Showcase(DeviceService deviceService) {
    super(Demo.UPDATE_RATE);
    this.deviceService = deviceService;
  }

  public void didRotate() {
    activeDemo.didRotate();
  }

  public boolean shouldExitOnBack() {
    // the BACK button will get procesesd by Android immediately *after* we move to the main menu,
    // so we want to debounce things so only if you press back after you're already on the main
    // menu do we allow the app to exit via the normal back button processing
    return (activeDemo == menuDemo) &&
      (System.currentTimeMillis() - activeStamp) > 500L;
  }

  public void activateDemo(Demo demo) {
    if (activeDemo != null) {
      activeDemo.shutdown();
    }
    if (activeDemo != demo) {
      activeDemo = demo;
      activeDemo.init();
      activeStamp = System.currentTimeMillis();
    }
  }

  @Override
  public void init() {
    PlayN.keyboard().setListener(new Keyboard.Adapter() {
      @Override
      public void onKeyDown(Keyboard.Event event) {
        if (backKeys.contains(event.key())) {
            activateDemo(menuDemo);
        } else {
          Keyboard.Listener delegate = activeDemo.keyboardListener();
          if (delegate != null) {
            delegate.onKeyDown(event);
          }
        }
      }

      @Override
      public void onKeyUp(Keyboard.Event event) {
        Keyboard.Listener delegate = activeDemo.keyboardListener();
        if (delegate != null) {
          delegate.onKeyUp(event);
        }
      }
    });

    try {
      PlayN.touch().setListener(new Touch.Adapter() {
        public void onTouchStart(Touch.Event[] touches) {
          if (touches.length > 1)
            activateDemo(menuDemo);
        }
      });
    } catch (UnsupportedOperationException e) {
      // no support for touch; no problem
    }

    activateDemo(menuDemo);
  }

  @Override
  public void update(int delta) {
    activeDemo.update(delta);
  }

  @Override
  public void paint(float alpha) {
    activeDemo.paint(alpha);
  }
}
