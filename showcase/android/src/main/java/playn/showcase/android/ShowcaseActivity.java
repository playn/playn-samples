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
package playn.showcase.android;

import android.app.ActivityManager;
import android.os.Build;
import android.graphics.Typeface;

import playn.android.GameActivity;
import playn.core.Font;

import playn.showcase.core.Showcase;

public class ShowcaseActivity extends GameActivity {

  @Override
  public void main(){
    Typeface face = platform().assets().getTypeface("text/Museo.otf");
    platform().graphics().registerFont(face, "Museo-300", Font.Style.PLAIN);
    platform().graphics().registerFont(face, "Museo-300", Font.Style.BOLD);
    platform().graphics().registerFont(face, "Museo-300", Font.Style.ITALIC);
    platform().graphics().registerFont(face, "Museo-300", Font.Style.BOLD_ITALIC);
    _game = new Showcase(platform(), new Showcase.DeviceService() {
      public String info() {
        Runtime rt = Runtime.getRuntime();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        return ("Android [model=" + Build.MODEL + ", cpu=" + Build.CPU_ABI +
                ", osver=" + Build.VERSION.RELEASE + ", mclass=" + am.getMemoryClass() +
                ", mem=" + (rt.freeMemory()/1024) + "k/" + (rt.totalMemory()/1024) + "k" +
                ", maxmem=" + (rt.maxMemory()/1024) + "k]");
      }
    });
  }

  @Override public void onBackPressed () {
    if (_game == null || _game.shouldExitOnBack()) super.onBackPressed();
  }

  protected Showcase _game;
}
