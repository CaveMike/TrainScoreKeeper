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
import android.widget.ToggleButton;

public class Player {
    private final static String TAG = Player.class.getSimpleName();
    private final static boolean VERBOSE = false;

    private final ToggleButton button;
    private final int id;
    private final String name;
    private boolean enabled;

    public Player(ToggleButton button, int id, final String name, boolean enabled) {
        Log.vc(VERBOSE, TAG, "ctor: button=" + button + ", id=" + id
                + ", name=" + name + ", enabled=" + enabled);

        this.button = button;
        this.id = id;
        this.name = name;
        this.enabled = enabled;
    }

    public int getButtonId() {
        return button.getId();
    }

    public String getName() {
        return name;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setChecked(boolean checked) {
        Log.vc(VERBOSE, TAG, "setChecked: checked=" + checked);
        button.setChecked(checked);
    }

    public void setScore(int score) {
        Log.vc(VERBOSE, TAG, "setScore: score=" + score);
        setText(Integer.toString(score));
    }

    private void setText(final String text) {
        Log.vc(VERBOSE, TAG, "setText: text=" + text);

        button.setText(text);
        button.setTextOn(text);
        button.setTextOff(text);
    }

    public void update(int selectedId) {
        // If there is no selection, assume visible.
        // Otherwise, assume invisible.
        int visibility = selectedId == -1 ? View.VISIBLE : View.INVISIBLE;

        if (this.id == selectedId) {
            visibility = View.VISIBLE;
        } else if (!enabled) {
            visibility = View.GONE;
        }

        Log.vc(VERBOSE, TAG, "update: selectedId=" + selectedId + ", id=" + id
                + ", visibility=" + visibility);
        button.setVisibility(visibility);
    }

    @Override
    public String toString() {
        return "Player [button=" + button + ", id=" + id + ", name=" + name + ", enabled="
                + enabled + "]";
    }
}
