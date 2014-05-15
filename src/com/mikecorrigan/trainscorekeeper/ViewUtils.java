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

import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {
    private final static String TAG = ViewUtils.class.getSimpleName();
    private final static boolean VERBOSE = false;

    public static void setEnabledRecursive(ViewGroup viewGroup, boolean enabled) {
        Log.vc(VERBOSE, TAG, "setEnabledRecursive: viewGroup=" + viewGroup + ", enabled=" + enabled);
        final int children = viewGroup.getChildCount();
        for (int i = 0; i < children; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);

            if (view instanceof ViewGroup) {
                setEnabledRecursive((ViewGroup) view, enabled);
            }
        }
    }
}
