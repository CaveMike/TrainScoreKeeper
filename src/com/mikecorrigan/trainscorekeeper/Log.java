//   Copyright 2012-2014 Michael T. Corrigan
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mikecorrigan.trainscorekeeper;

import mikecorrigan.trainscorekeeper.BuildConfig;

public class Log {
    private static final String TAG = "trainscorekeeper";

    public static void e(final String tag, final String message) {
        android.util.Log.e(TAG + "." + tag, message);
    }

    public static void w(final String tag, final String message) {
        android.util.Log.w(TAG + "." + tag, message);
    }

    public static void i(final String tag, final String message) {
        android.util.Log.i(TAG + "." + tag, message);
    }

    public static void d(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.d(TAG + "." + tag, message);
        }
    }

    public static void v(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            android.util.Log.v(TAG + "." + tag, message);
        }
    }

    public static void vc(final boolean enabled, final String tag, final String message) {
        if (enabled && BuildConfig.DEBUG) {
            android.util.Log.v(TAG + "." + tag, message);
        }
    }

    public static void th(final String tag, final Throwable th, final String message) {
        android.util.Log.e(TAG + "." + tag, message +
                "\n" + android.util.Log.getStackTraceString(th));
    }
}
