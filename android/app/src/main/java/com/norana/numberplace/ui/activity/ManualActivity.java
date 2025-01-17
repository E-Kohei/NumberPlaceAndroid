/*
 * Copyright 2020, E-Kohei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.norana.numberplace.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.norana.numberplace.R;
import com.norana.numberplace.ui.fragment.ManualFragment;
import com.norana.numberplace.ui.fragment.ManualMenuFragment;

public class ManualActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual);

        if (savedInstanceState == null) {
            ManualMenuFragment fragment = new ManualMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.manual_fragment_container, fragment, ManualMenuFragment.TAG)
                    .commit();
        }
    }

    public void showManual(int manual_string_id){
        ManualFragment fragment = ManualFragment.createInstance(manual_string_id);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.animator.animation_slide_in_from_right,  // enter of the second fragment
                        R.animator.animation_slide_out_to_left,    // exit of the first fragment
                        R.animator.animation_slide_in_from_left,   // pop enter of the first fragment
                        R.animator.animation_slide_out_to_right    // pop exit of the second fragment
                )
                .addToBackStack(null)
                .replace(R.id.manual_fragment_container, fragment)
                .commit();

    }
}
