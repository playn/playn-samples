/**
 * Copyright 2011 The ForPlay Authors
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

import playn.core.Clock;
import playn.core.Key;
import playn.core.Keyboard;
import playn.scene.GroupLayer;

import react.Closeable;
import react.UnitSlot;

import tripleplay.ui.Background;
import tripleplay.ui.Button;
import tripleplay.ui.Group;
import tripleplay.ui.Interface;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;

/**
 * A demo that displays a menu of the available demos.
 */
public class Menu extends Showcase.Demo {

  public Menu () { super("Menu"); }

  @Override public void create (final Showcase game, Closeable.Set onClose) {
    GroupLayer layer = new GroupLayer();
    game.rootLayer.add(layer);
    onClose.add(layer);

    // create our UI manager and configure it to process pointer events
    Interface iface = new Interface(game.plat, game.paint);
    onClose.add(iface);

    // create our demo interface
    final Root root = iface.createRoot(AxisLayout.vertical().gap(15),
                                       SimpleStyles.newSheet(game.plat.graphics()));
    root.setSize(game.plat.graphics().viewSize);
    root.addStyles(Style.BACKGROUND.is(Background.solid(0xFF99CCFF).inset(5)));
    layer.add(root.layer);

    Group buttons;
    root.add(new Label("PlayN Demos:"),
             buttons = new Group(AxisLayout.vertical().offStretch()),
             new Label("ESC/BACK key or two-finger tap returns to menu from demo").addStyles(
               Style.TEXT_WRAP.is(true)),
             new Label("(renderer: " + game.plat.graphics().getClass().getSimpleName() + " " +
                       game.plat.graphics().viewSize + ")"),
             new Label("(device: " + game.deviceService.info() + ")").addStyles(
               Style.TEXT_WRAP.is(true)));

    int key = 1;
    for (final Showcase.Demo demo : game.demos) {
      Button button = new Button(key++ + " - " + demo.name);
      buttons.add(button);
      button.clicked().connect(new UnitSlot() {
        @Override public void onEmit() { game.activateDemo(demo); }
      });
    }

    // wire up keyboard shortcuts
    onClose.add(game.plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
      public void onEmit (Keyboard.KeyEvent event) {
        // this is a bit hacky, but serves our purpose
        int demoIndex = event.key.ordinal() - Key.K1.ordinal();
        if (demoIndex >= 0 && demoIndex < game.demos.size()) {
          game.activateDemo(game.demos.get(demoIndex));
        }
      }
    }));

    // resize our root if the view rotates
    onClose.add(game.rotate.connect(new UnitSlot() {
      public void onEmit () { root.setSize(game.plat.graphics().viewSize); }
    }));
  }
}
