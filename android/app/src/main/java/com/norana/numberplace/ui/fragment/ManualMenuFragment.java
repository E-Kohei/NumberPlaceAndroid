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

package com.norana.numberplace.ui.fragment;

import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.norana.numberplace.R;
import com.norana.numberplace.databinding.ManualMenuFragmentBinding;
import com.norana.numberplace.ui.activity.MainActivity;
import com.norana.numberplace.ui.activity.ManualActivity;
import com.norana.numberplace.ui.adapter.ManualMenuAdapter;
import com.norana.numberplace.ui.callback.MenuClickCallback;

public class ManualMenuFragment extends Fragment {

    public static final String TAG = "ManualMenuFragemnt";

    private ManualMenuAdapter mManualMenuAdapter;
    private ManualMenuFragmentBinding mBinding;

    private final MenuClickCallback mManualMenuClickCallback =
            new MenuClickCallback() {
                @Override
                public void onClick(int stringId) {
                    if (getLifecycle().getCurrentState()
                            .isAtLeast(Lifecycle.State.STARTED)){
                        ((ManualActivity) requireActivity()).showManual(stringId);
                    }
                }
            };

    public ManualMenuFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.manual_menu_fragment, container, false);
        mManualMenuAdapter = new ManualMenuAdapter(mManualMenuClickCallback);
        mBinding.manualMenuList.setAdapter(mManualMenuAdapter);

        mBinding.manualMenuList.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener(){
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e){
                View v = rv.findChildViewUnder(e.getX(), e.getY());
                LinearLayout container = (v == null) ? null : (LinearLayout) rv.findContainingItemView(v);
                if (container == null)
                    return;
                TextView tv = ((TextView) container.getChildAt(0));
                switch (e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        container.setBackgroundColor(Color.LTGRAY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        container.setBackgroundColor(Color.TRANSPARENT);
                        break;
                    case MotionEvent.ACTION_UP:
                        container.setBackgroundColor(Color.TRANSPARENT);
                        int manual_string_id = (int) container.getTag();
                        ((ManualActivity) requireActivity()).showManual(manual_string_id);
                }
                if (e.getAction() == MotionEvent.ACTION_DOWN)
                    v.setBackgroundColor(Color.LTGRAY);
                else if (e.getAction() == MotionEvent.ACTION_UP)
                    v.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e){
                if (!rv.isAttachedToWindow() || rv.getAdapter() == null){
                    return false;
                }

                return true;
            }
        });

        return mBinding.getRoot();
    }

}
