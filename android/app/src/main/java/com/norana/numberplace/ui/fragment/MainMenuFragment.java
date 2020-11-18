/*
 * Copyright 2019, E-Kohei
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.norana.numberplace.R;
import com.norana.numberplace.databinding.MainMenuFragmentBinding;
import com.norana.numberplace.ui.activity.MainActivity;
import com.norana.numberplace.ui.adapter.MainMenuAdapter;
import com.norana.numberplace.ui.callback.MenuClickCallback;

/**
 * Fragment to show the main menu UI
 */
public class MainMenuFragment extends Fragment{

	public static final String TAG = "MainMenuFragemnt";

	private MainMenuAdapter mMainMenuAdapter;

	private MainMenuFragmentBinding mBinding;

	private boolean isClicked = false;;

	private final MenuClickCallback mMainMenuClickCallback =
		new MenuClickCallback(){
		@Override
		public void onClick(int stringId){
			if (getLifecycle().getCurrentState()
					.isAtLeast(Lifecycle.State.STARTED)){
				if (stringId == R.string.menu_play){
					((MainActivity) requireActivity()).showLevels();
				}
				else if (stringId == R.string.menu_make){
					((MainActivity) requireActivity())
						.startMakeSudokuActivity();
				}
				else if (stringId == R.string.menu_saved){
					((MainActivity) requireActivity()).showSavedSudokus();
				}
			}
		}
	};

	public MainMenuFragment(){
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		mBinding = DataBindingUtil.inflate(inflater,
				R.layout.main_menu_fragment, container, false);
		mMainMenuAdapter = new MainMenuAdapter(mMainMenuClickCallback);
		mBinding.mainMenuList.setAdapter(mMainMenuAdapter);

		return mBinding.getRoot();
	}

}
