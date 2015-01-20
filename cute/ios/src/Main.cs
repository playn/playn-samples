using System;
using MonoTouch.Foundation;
using MonoTouch.UIKit;

using playn.ios;
using playn.core;
using playn.sample.cute.core;

namespace playn.sample.cute.ios
{
	[Register ("AppDelegate")]
	public partial class AppDelegate : UIApplicationDelegate
	{
		public override bool FinishedLaunching (UIApplication app, NSDictionary options)
		{
            IOSPlatform p = IOSPlatform.register(app);
		    PlayN.run(new CuteGame());
    		return true;
		}
	}

	public class Application
	{
		static void Main (string[] args)
		{
			UIApplication.Main (args, null, "AppDelegate");
		}
	}
}
