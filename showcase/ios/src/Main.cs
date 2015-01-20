using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;

using playn.ios;
using playn.core;
using playn.showcase.core;

namespace playn.showcase.ios {

  [Register ("AppDelegate")]
  public partial class AppDelegate : UIApplicationDelegate {
    public override bool FinishedLaunching (UIApplication app, NSDictionary options) {
      var pf = IOSPlatform.register(app);
      PlayN.run(new Showcase(new IOSDeviceService()));
      return true;
    }
  }

  public class Application {
    static void Main (string[] args) {
      UIApplication.Main (args, null, "AppDelegate");
    }
  }

  public class IOSDeviceService : Showcase.DeviceService {
    public string info() {
      var device = UIDevice.CurrentDevice;
      return "iOS [model=" + device.Model +
        ", os=" + device.SystemName + "/" + device.SystemVersion +
        ", name=" + device.Name +
        ", orient=" + device.Orientation + "]";
    }
  }
}
