package playn.flurry.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import playn.flurry.core.Flurry;
import playn.flurry.core.FlurryExample;

public class FlurryExampleJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    JavaPlatform.register(config);
    PlayN.run(new FlurryExample(new Flurry() {
      public void logEvent (String eventName, Object... args) {
        log("FLURRY", eventName, args);
      }
      void log (String kind, String name, Object... args) {
        StringBuilder buf = new StringBuilder();
        buf.append(kind).append(":").append(name).append(" [");
        for (int ii = 0; ii < args.length; ii += 2) {
          if (ii > 0) buf.append(", ");
          buf.append(args[ii]).append("=").append(args[ii+1]);
        }
        buf.append("]");
        PlayN.log().info(buf.toString());
      }
    }));
  }
}
