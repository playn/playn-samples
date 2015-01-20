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
package playn.showcase.core.snake;

import pythagoras.f.Transform;

import react.Closeable;
import react.Slot;

import playn.core.Clock;
import playn.core.Image;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;

import playn.showcase.core.Showcase;

/**
 * Animates a chain of images undulating around the screen, using depth and scale to make them
 * appear to move in and out in the z-dimension.
 */
public class SnakeDemo extends Showcase.Demo {

  public SnakeDemo () { super("Snake"); }

  @Override public void create (final Showcase game, Closeable.Set onClose) {
    // create a group layer to hold everything
    GroupLayer layer = new GroupLayer();
    game.rootLayer.add(layer);
    onClose.add(layer);

    // create and add background image layer
    Image bgImage = game.plat.assets().getImage("background.png");
    layer.add(new ImageLayer(bgImage).setSize(game.plat.graphics().viewSize).setDepth(-1));

    // create our snake segments
    Image segImage = game.plat.assets().getImage("sprites/pea.png");
    final ImageLayer[] segments = new ImageLayer[25];
    for (int ii = 0; ii < segments.length; ii++) {
      segments[ii] = new ImageLayer(segImage);
      segments[ii].setDepth(50);
      layer.add(segments[ii]);
    }

    onClose.add(game.paint.connect(new Slot<Clock>() {
      private float dx = 10, dy = 5, dd = 1;

      public void onEmit (Clock clock) {
        // the tail segments play follow the leader
        for (int ii = segments.length-1; ii > 0; ii--) {
          ImageLayer cur = segments[ii], prev = segments[ii-1];
          Transform t1 = cur.transform(), t2 = prev.transform();
          t1.setTx(t2.tx());
          t1.setTy(t2.ty());
          t1.setUniformScale(t2.uniformScale());
          cur.setDepth(prev.depth());
        }

        // and the head segment leads the way
        ImageLayer first = segments[0];
        Transform t = first.transform();
        float nx = t.tx() + dx, ny = t.ty() + dy, nd = first.depth() + dd;
        if (nx < 0 || nx > game.plat.graphics().viewSize.width()) {
          dx *= -1;
          nx += dx;
        }
        if (ny < 0 || ny > game.plat.graphics().viewSize.height()) {
          dy *= -1;
          ny += dy;
        }
        if (nd < 25 || nd > 125) {
          dd *= -1;
          nd += dd;
        }
        t.setTx(nx);
        t.setTy(ny);
        t.setUniformScale(nd/50f);
        first.setDepth(nd);
      }
    }));
  }
}
