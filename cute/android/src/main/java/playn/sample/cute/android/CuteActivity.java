/**
 * Copyright 2010 The PlayN Authors
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
package playn.sample.cute.android;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

import playn.android.GameActivity;
import playn.core.PlayN;
import playn.sample.cute.core.CuteGame;

public class CuteActivity extends GameActivity {

  private PowerManager.WakeLock wakeLock;

  @Override
  public void main(){
    PlayN.run(new CuteGame());
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // acquire our wake lock, to keep the screen from dimming while we're active
    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                              "playn.cute");
    wakeLock.acquire();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    wakeLock.release();
  }

  @Override
  protected void onPause() {
    super.onPause();
    wakeLock.release();
  }

  @Override
  protected void onResume() {
    super.onResume();
    wakeLock.acquire();
  }
}
