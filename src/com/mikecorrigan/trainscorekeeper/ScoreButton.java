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

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

public class ScoreButton extends Button {
    private JSONObject jsonButton;

    public ScoreButton(Context context) {
        super(context);
        setEnabled(false);
    }

    public JSONObject getButtonSpec() {
        return jsonButton;
    }

    public void setButtonSpec(JSONObject jsonButton) {
        this.jsonButton = jsonButton;

        String name = jsonButton.optString(JsonSpec.BUTTON_NAME, JsonSpec.DEFAULT_BUTTON_NAME);
        setText(name);

        int value = jsonButton.optInt(JsonSpec.BUTTON_VALUE, JsonSpec.DEFAULT_BUTTON_VALUE);
        if (value < 0) {
            setTextColor(Color.RED);
        }
    }
}
