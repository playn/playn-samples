package io.playn.drop.android;

import playn.android.GameActivity;

import io.playn.drop.core.Drop;

public class DropActivity extends GameActivity {

  @Override public void main () {
    new Drop(platform());
  }
}
