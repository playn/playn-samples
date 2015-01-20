using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;

using FA = FlurryAnalytics;

using playn.ios;
using playn.core;
using playn.flurry.core;

namespace playn.flurry
{
  [Register ("AppDelegate")]
  public partial class AppDelegate : IOSApplicationDelegate {
    public override bool FinishedLaunching (UIApplication app, NSDictionary options) {
      app.SetStatusBarHidden(true, true);

      // configure our Flurry and SGAgent bits
      var flurryKey = "FLURRYKEY";
      // these can be useful if you have this info
      // FA.Flurry.SetAppVersion("app_version");
      // FA.Flurry.SetUserID("user_id");
      Flurry flurry = new IOSFlurry();
      try {
        Console.WriteLine("Initializing Flurry [vers=" + FA.Flurry.GetFlurryAgentVersion() + "]");
        FA.Flurry.StartSession(flurryKey);
      } catch (Exception e) {
        Console.WriteLine("Failed to init Flurry [key=" + flurryKey + "]");
        Console.WriteLine(e);
      }

      // initialize PlayN and start the game
      var pconfig = new IOSPlatform.Config();
      // use pconfig to customize iOS platform, if needed
      IOSPlatform.register(app, pconfig);
      PlayN.run(new FlurryExample(flurry));
      return true;
    }
  }

  public class Application {
    static void Main (string[] args) {
      UIApplication.Main (args, null, "AppDelegate");
    }
  }

  internal class IOSFlurry : Flurry {
    public void logEvent (string eventName, object[] args) {
      if (args.Length == 0) {
        try {
          FA.Flurry.LogEvent(eventName);
        } catch (Exception e) {
          PlayN.log().warn("Failed to log event to Flurry [event=" + eventName + "]", e);
        }
      } else {
        var dict = new NSMutableDictionary();
        for (int ii = 0; ii < args.Length; ii += 2) {
          var key = (string)args[ii];
          var value = args[ii+1];
          if (value is string) {
            dict.Add(new NSString(key), new NSString((string)value));
          } else if (value is java.lang.Boolean) {
            dict.Add(new NSString(key), new NSNumber(((java.lang.Boolean)value).booleanValue()));
          } else if (value is java.lang.Integer) {
            dict.Add(new NSString(key), new NSNumber(((java.lang.Integer)value).intValue()));
          } else {
            var vclass = (value == null) ? "null" : value.GetType().ToString();
            PlayN.log().warn("Got unknown Flurry event parameter type [key=" + key +
                             ", value=" + value + ", vclass=" + vclass + "]");
          }
        }
        try {
          FA.Flurry.LogEvent(eventName, dict);
        } catch (Exception e) {
          PlayN.log().warn("Failed to log event to Flurry [event=" + eventName +
                           ", argCount=" + args.Length + "]", e);
        }
      }
    }
  }
}
