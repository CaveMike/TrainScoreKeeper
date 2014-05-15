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

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class JsonUtils {
    private final static String TAG = JsonUtils.class.getSimpleName();
    private final static boolean VERBOSE = false;

    public static JSONObject readFromFile(final String filename) {
        Log.vc(VERBOSE, TAG, "readFromFile: filename=" + filename);
        return readFromFile(new File(filename));
    }

    public static JSONObject readFromFile(final File file) {
        Log.vc(VERBOSE, TAG, "readFromFile: file=" + file);
        String string = FileUtils.readStringFromFile(file);
        return readFromString(string);
    }

    public static JSONObject readFromAssets(final Context context, final String assetname) {
        Log.vc(VERBOSE, TAG, "readFromAssets: context=" + context + ", assetname=" + assetname);

        String string = FileUtils.readStringFromAssets(context, assetname);
        return readFromString(string);
    }

    public static JSONObject readFromString(final String string) {
        Log.vc(VERBOSE, TAG, "readFromString: string=" + string);

        if (string == null) {
            Log.w(TAG, "readFromString: null string");
            return null;
        }

        JSONObject jsonRoot = new JSONObject();

        try {
            jsonRoot = new JSONObject(string);
        } catch (JSONException e) {
            Log.th(TAG, e, "read: parse failed");
        }

        return jsonRoot;
    }

    public static boolean writeFormatted(final File file, final JSONObject jsonRoot) {
        Log.vc(VERBOSE, TAG, "writeFormatted: file=" + file);

        final int NUM_SPACES = 2;

        boolean result = false;

        try {
            String string = jsonRoot.toString(NUM_SPACES);
            FileUtils.writeStringToFile(file, string);
            result = true;
        } catch (JSONException e) {
            Log.th(TAG, e, "writeFormatted: failed to format");
        }

        return result;
    }
}
