package playn.flurry.android;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;

import playn.android.GameActivity;
import playn.core.PlayN;

import com.flurry.android.FlurryAgent;

import playn.flurry.core.Flurry;
import playn.flurry.core.FlurryExample;

public class FlurryExampleActivity extends GameActivity {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // these can be useful if you have this info
    // FlurryAgent.setVersionName("app_version");
    // you can set user_id in main() if you need to do it after PlayN is initialized (like if you
    // have to read it from playn.Storage)
    // FlurryAgent.setUserId("user_id");
  }

  @Override protected void onStart() {
    super.onStart();
    try {
      FlurryAgent.onStartSession(this, "FLURRYKEY");
    } catch (Throwable t) {
      PlayN.log().warn("Error noting session start for Flurry", t);
    }
  }

  @Override protected void onStop() {
    super.onStop();
    try {
      FlurryAgent.onEndSession(this);
    } catch (Throwable t) {
      PlayN.log().warn("Error noting session end for Flurry", t);
    }
  }

  @Override
  public void main(){
    Flurry flurry = new Flurry() {
      public void logEvent (String eventName, Object... args) {
        if (args.length == 0) {
          try {
            FlurryAgent.logEvent(eventName);
          } catch (Exception e) {
            PlayN.log().warn("Failed to log Flurry event [event=" + eventName + "]", e);
          }
        } else {
          Map<String,String> argmap = new HashMap<String,String>();
          for (int ii = 0; ii < args.length; ii += 2) {
            argmap.put((String)args[ii], String.valueOf(args[ii+1]));
          }
          try {
            FlurryAgent.logEvent(eventName, argmap);
          } catch (Exception e) {
            PlayN.log().warn("Failed to log Flurry event [event=" + eventName +
                             ", args=" + argmap + "]", e);
          }
        }
      }
    };
    PlayN.run(new FlurryExample(flurry));
  }
}
