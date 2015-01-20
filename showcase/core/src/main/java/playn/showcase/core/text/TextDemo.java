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
package playn.showcase.core.text;

import react.Closeable;

import playn.core.Canvas;
import playn.core.Font;
import playn.core.Graphics;
import playn.core.TextBlock;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.TextWrap;
import playn.core.Texture;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;
import playn.scene.Layer;

import playn.showcase.core.Showcase;

public class TextDemo extends Showcase.Demo {

  public TextDemo () { super("Text"); }

  @Override public void create (Showcase game, Closeable.Set onClose) {
    Graphics gfx = game.plat.graphics();
    GroupLayer base = new GroupLayer();
    game.rootLayer.add(base);
    onClose.add(base);

    // draw a soothing flat background
    Canvas bgtile = gfx.createCanvas(64, 64);
    bgtile.setFillColor(0xFFCCCCCC).fillRect(0, 0, 64, 64);
    bgtile.setStrokeColor(0xFFFFFFFF).strokeRect(0, 0, 64, 64);

    ImageLayer bg = new ImageLayer(bgtile.toTexture(Texture.Config.DEFAULT.repeat(true, true)));
    base.add(bg.setSize(gfx.viewSize));

    // add some text to said soothing background
    final float MARGIN = 10;
    float xpos = MARGIN, maxYPos = 0;
    for (String name : new String[] { "Helvetica", "Museo-300" }) {
      float ypos = MARGIN, maxWidth = 0;
      for (Font.Style style : Font.Style.values()) {
        for (float size : new float[] { 12f, 24f, 32f }) {
          Font font = new Font(name, style, size);
          TextFormat format = new TextFormat(font);
          TextLayout layout = gfx.layoutText("Hello PlayN World", format);
          Layer layer = createTextLayer(game, layout, 0xFF000000);
          layer.setTranslation(xpos, ypos);
          base.add(layer);
          ypos += layout.size.height();
          maxWidth = Math.max(maxWidth, layout.size.width());
          maxYPos = Math.max(ypos, maxYPos);
        }
      }
      xpos += (maxWidth + MARGIN);
    }

    // also add some wrapped text
    xpos = MARGIN;
    float ypos = maxYPos + MARGIN;
    Font font = new Font("Courier", 16);
    String text = "Text can also be wrapped at a specified width.\n\n" +
      "And wrapped manually at newlines.\nLike this.";
    TextFormat fmt = new TextFormat().withFont(font);
    TextWrap wrap = new TextWrap(200);
    TextBlock block = new TextBlock(gfx.layoutText(text, fmt, wrap));
    Layer layer = new ImageLayer(block.toCanvas(gfx, TextBlock.Align.LEFT, 0xFF660000).toTexture());
    layer.setTranslation(xpos, ypos);
    base.add(layer);
    xpos += block.textWidth() + MARGIN;
    ypos += MARGIN;

    text = "Wrapped text can be center-justified, if so desired.";
    block = new TextBlock(gfx.layoutText(text, fmt, wrap));
    layer = new ImageLayer(block.toCanvas(gfx, TextBlock.Align.CENTER, 0xFF006600).toTexture());
    layer.setTranslation(xpos, ypos);
    base.add(layer);
    xpos += block.textWidth() + MARGIN;
    ypos += MARGIN;

    text = "Or it can be flush to the right, if that's how you like to justify yourself.";
    block = new TextBlock(gfx.layoutText(text, fmt, wrap));
    layer = new ImageLayer(block.toCanvas(gfx, TextBlock.Align.RIGHT, 0xFF000066).toTexture());
    layer.setTranslation(xpos, ypos);
    base.add(layer);
    xpos += block.textWidth() + MARGIN;
    ypos += MARGIN;
  }

  protected Layer createTextLayer(Showcase game, TextLayout layout, int color) {
    Canvas canvas = game.plat.graphics().createCanvas(layout.size);
    canvas.setFillColor(color).fillText(layout, 0, 0);
    return new ImageLayer(canvas.toTexture());
  }
}
