package playn.flurry.core;

import playn.core.*;
import static playn.core.PlayN.*;

public class FlurryExample extends Game.Default {

  public final Flurry flurry;

  public FlurryExample(Flurry flurry) {
    super(33); // call update every 33ms (30 times per second)
    this.flurry = flurry;
  }

  @Override
  public void init() {
    Image bgImage = assets().getImage("images/bg.png");
    ImageLayer bgLayer = graphics().createImageLayer(bgImage);
    graphics().rootLayer().add(bgLayer);

    TextFormat fmt = new TextFormat().withWrapWidth(graphics().width()).withFont(
      graphics().createFont("Helvetica", Font.Style.PLAIN, 32));
    TextLayout layout = graphics().layoutText("Click anywhere to log flurry event.", fmt);
    CanvasImage image = graphics().createImage(layout.width(), layout.height());
    image.canvas().fillText(layout, 0, 0);
    graphics().rootLayer().addAt(graphics().createImageLayer(image),
                                 (graphics().width()-image.width())/2,
                                 (graphics().height()-image.height())/2);

    bgLayer.addListener(new Pointer.Adapter() {
      public void onPointerStart(Pointer.Event event) {
        flurry.logEvent("pointer_clicked", "time", (int)event.time(),
                        "x", (int)event.x(), "y", (int)event.y());
      }
    });
  }

  @Override
  public void update(int delta) {
  }

  @Override
  public void paint(float alpha) {
  }
}
