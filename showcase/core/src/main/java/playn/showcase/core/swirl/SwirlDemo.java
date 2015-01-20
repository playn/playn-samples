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
package playn.showcase.core.swirl;

import pythagoras.f.FloatMath;

import react.Closeable;
import react.Slot;

import playn.core.Clock;
import playn.core.Image;
import playn.core.Pointer;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;

import playn.showcase.core.Showcase;

public class SwirlDemo extends Showcase.Demo {

  public SwirlDemo () { super("Swirl"); }

  @Override public void create (Showcase game, Closeable.Set onClose) {
    Image bg = game.plat.assets().getImage("background.png");
    game.rootLayer.add(new ImageLayer(bg).setSize(game.plat.graphics().viewSize).setDepth(-1));

    final GroupLayer groupLayer = new GroupLayer();
    groupLayer.setOrigin(128, 128);
    groupLayer.transform().translate(256, 256);
    game.rootLayer.add(groupLayer);
    onClose.add(groupLayer);

    Image catgirl = game.plat.assets().getImage("swirl/girlcat.png");
    final ImageLayer layer0 = new ImageLayer(catgirl);
    final ImageLayer layer1 = new ImageLayer(catgirl);
    final ImageLayer layer2 = new ImageLayer(catgirl);
    final ImageLayer layer3 = new ImageLayer(catgirl);

    groupLayer.add(layer0);
    groupLayer.add(layer1);
    groupLayer.add(layer2);
    groupLayer.add(layer3);

    layer0.setOrigin(50, 100);
    layer1.setOrigin(50, 100);
    layer2.setOrigin(50, 100);
    layer3.setOrigin(50, 100);

    layer0.transform().translate(0, 0);
    layer1.transform().translate(256, 0);
    layer2.transform().translate(256, 256);
    layer3.transform().translate(0, 256);

    onClose.add(game.paint.connect(new Slot<Clock>() {
      public void onEmit (Clock clock) {
        float angle = clock.tick * FloatMath.PI / 5000;
        float scale = FloatMath.sin(angle) + 0.5f;

        layer0.transform().setRotation(angle);
        layer1.transform().setRotation(angle);
        layer2.transform().setRotation(angle);
        layer3.transform().setRotation(angle);

        layer0.transform().setUniformScale(scale);
        layer1.transform().setUniformScale(scale);
        layer2.transform().setUniformScale(scale);
        layer3.transform().setUniformScale(scale);

        groupLayer.transform().setRotation(angle);
        groupLayer.transform().setUniformScale(scale);
      }
    }));
  }
}
