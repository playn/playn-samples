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

import playn.core.CanvasImage;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.TextWrap;
import playn.core.util.TextBlock;
import static playn.core.PlayN.*;

import playn.showcase.core.Demo;

public class TextDemo extends Demo {
  private GroupLayer base;

  @Override
  public String name() {
    return "Text";
  }

  @Override
  public void init() {
    base = graphics().createGroupLayer();
    graphics().rootLayer().add(base);

    // draw a soothing flat background
    CanvasImage bgtile = graphics().createImage(64, 64);
    bgtile.canvas().setFillColor(0xFFCCCCCC);
    bgtile.canvas().fillRect(0, 0, 64, 64);
    bgtile.canvas().setStrokeColor(0xFFFFFFFF);
    bgtile.canvas().strokeRect(0, 0, 64, 64);
    bgtile.setRepeat(true, true);

    ImageLayer bg = graphics().createImageLayer(bgtile);
    bg.setWidth(graphics().width());
    bg.setHeight(graphics().height());
    base.add(bg);

    // add some text to said soothing background
    final float MARGIN = 10;
    float xpos = MARGIN, maxYPos = 0;
    for (String name : new String[] { "Helvetica", "Museo-300" }) {
      float ypos = MARGIN, maxWidth = 0;
      for (Font.Style style : Font.Style.values()) {
        for (float size : new float[] { 12f, 24f, 32f }) {
          Font font = graphics().createFont(name, style, size);
          TextFormat format = new TextFormat().withFont(font);
          TextLayout layout = graphics().layoutText("Hello PlayN World", format);
          Layer layer = createTextLayer(layout, 0xFF000000);
          layer.setTranslation(xpos, ypos);
          base.add(layer);
          ypos += layout.height();
          maxWidth = Math.max(maxWidth, layout.width());
          maxYPos = Math.max(ypos, maxYPos);
        }
      }
      xpos += (maxWidth + MARGIN);
    }

    // also add some wrapped text
    xpos = MARGIN;
    float ypos = maxYPos + MARGIN;
    Font font = graphics().createFont("Courier", Font.Style.PLAIN, 16);
    String text = "Text can also be wrapped at a specified width.\n\n" +
      "And wrapped manually at newlines.\nLike this.";
    TextFormat fmt = new TextFormat().withFont(font);
    TextWrap wrap = new TextWrap(200);
    TextBlock block = new TextBlock(graphics().layoutText(text, fmt, wrap));
    Layer layer = graphics().createImageLayer(block.toImage(TextBlock.Align.LEFT, 0xFF660000));
    layer.setTranslation(xpos, ypos);
    base.add(layer);
    xpos += block.textWidth() + MARGIN;
    ypos += MARGIN;

    text = "Wrapped text can be center-justified, if so desired.";
    block = new TextBlock(graphics().layoutText(text, fmt, wrap));
    layer = graphics().createImageLayer(block.toImage(TextBlock.Align.CENTER, 0xFF006600));
    layer.setTranslation(xpos, ypos);
    base.add(layer);
    xpos += block.textWidth() + MARGIN;
    ypos += MARGIN;

    text = "Or it can be flush to the right, if that's how you like to justify yourself.";
    block = new TextBlock(graphics().layoutText(text, fmt, wrap));
    layer = graphics().createImageLayer(block.toImage(TextBlock.Align.RIGHT, 0xFF000066));
    layer.setTranslation(xpos, ypos);
    base.add(layer);
    xpos += block.textWidth() + MARGIN;
    ypos += MARGIN;
  }

  @Override
  public void shutdown() {
    base.destroy();
    base = null;
  }

  protected Layer createTextLayer(TextLayout layout, int color) {
    CanvasImage image = graphics().createImage((int)Math.ceil(layout.width()),
                                               (int)Math.ceil(layout.height()));
    image.canvas().setFillColor(color);
    image.canvas().fillText(layout, 0, 0);
    return graphics().createImageLayer(image);
  }
}
