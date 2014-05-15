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

import java.io.File;
import java.io.IOException;

import mikecorrigan.trainscorekeeper.R;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityNewGame extends Activity {
    private final static String TAG = ActivityNewGame.class.getSimpleName();
    private final static boolean VERBOSE = false;

    public final static String EXTRA_KEY_GAME_SPEC = "spec";

    private final static String ASSETS_BASE = "rules";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.vc(VERBOSE, TAG, "onCreate: savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);

        // Eula.show(this /* activity */);

        setContentView(R.layout.activity_new_game);

        addContent();
    }

    private void addContent() {
        Log.vc(VERBOSE, TAG, "addContent");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout);

        Resources res = getResources();
        AssetManager am = res.getAssets();
        String files[];
        try {
            files = am.list(ASSETS_BASE);
            if (files == null || files.length == 0) {
                Log.e(TAG, "addContent: empty asset list");
                return;
            }

            for (final String file : files) {
                final String path = ASSETS_BASE + File.separator + file;

                JSONObject jsonRoot = JsonUtils.readFromAssets(this /* context */, path);
                if (jsonRoot == null) {
                    Log.e(file, "addContent: failed to read read asset");
                    continue;
                }

                final String name = jsonRoot.optString(JsonSpec.ROOT_NAME);
                if (TextUtils.isEmpty(name)) {
                    Log.e(file, "addContent: failed to read asset name");
                    continue;
                }

                final String description = jsonRoot.optString(JsonSpec.ROOT_DESCRIPTION, name);

                TextView textView = new TextView(this /* context */);
                textView.setText(name);
                linearLayout.addView(textView);

                Button button = new Button(this /* context */);
                button.setText(description);
                button.setOnClickListener(new OnRulesClickListener(path));
                linearLayout.addView(button);
            }
        } catch (IOException e) {
            Log.th(TAG, e, "addContent: asset list failed");
        }
    }

    private class OnRulesClickListener implements OnClickListener {
        final String rules;

        public OnRulesClickListener(final String rules) {
            Log.vc(VERBOSE, TAG, "ctor: rules=" + rules);
            this.rules = rules;
        }

        @Override
        public void onClick(View v) {
            Log.vc(VERBOSE, TAG, "onClick: v=" + v);

            Intent intent = new Intent(ActivityNewGame.this /* context */, MainActivity.class);
            intent.putExtra(EXTRA_KEY_GAME_SPEC, rules);
            startActivity(intent);
            finish();
        }
    };
}
