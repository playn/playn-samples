/**
 * Copyright 2010 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package playn.sample.cute.java;

import playn.java.LWJGLPlatform;
import playn.sample.cute.core.CuteGame;

public class CuteGameJava {

  public static void main(String[] args) {
    LWJGLPlatform.Config config = new LWJGLPlatform.Config();
    config.width = 800;
    config.height = 600;
    LWJGLPlatform pf = new LWJGLPlatform(config);
    new CuteGame(pf);
    pf.start();
  }
}
