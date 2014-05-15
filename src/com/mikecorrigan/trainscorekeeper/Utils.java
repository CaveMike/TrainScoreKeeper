//   Copyright 2014 Michael T. Corrigan
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

import java.util.Set;

import android.os.Bundle;

public class Utils {
    // private static final String TAG = Utils.class.getSimpleName();

    public static final String bundleToString(final Bundle bundle) {
        if (bundle == null) {
            return "<null bundle>";
        }

        StringBuilder sb = new StringBuilder();

        Set<String> keys = bundle.keySet();
        if (keys == null) {
            return "<empty bundle>";
        }

        for (final String key : keys) {
            sb.append("key=" + key + ", value=" + bundle.get(key) + "; ");
        }

        return sb.toString();
    }
}
