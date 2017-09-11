package io.playn.drop.html;

import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import io.playn.drop.core.Drop;

public class DropHtml implements EntryPoint {

  @Override public void onModuleLoad () {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    // use config to customize the HTML platform, if needed
    HtmlPlatform plat = new HtmlPlatform(config);
    plat.assets().setPathPrefix("drop/");
    new Drop(plat);
    plat.start();
  }
}
