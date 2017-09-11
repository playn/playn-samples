package io.playn.drop.java;

import playn.java.LWJGLPlatform;

import io.playn.drop.core.Drop;

public class DropJava {

  public static void main (String[] args) {
    LWJGLPlatform.Config config = new LWJGLPlatform.Config();
    config.appName = "Drop";
    config.width = 800;
    config.height = 480;
    LWJGLPlatform plat = new LWJGLPlatform(config);
    new Drop(plat);
    plat.start();
  }
}
