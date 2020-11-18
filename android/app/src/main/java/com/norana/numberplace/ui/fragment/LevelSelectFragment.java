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
import androidx.lifecycle.Lifecycle;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;


import com.norana.numberplace.R;
import com.norana.numberplace.databinding.LevelSelectFragmentBinding;
import com.norana.numberplace.ui.activity.MainActivity;
import com.norana.numberplace.ui.adapter.LevelListAdapter;
import com.norana.numberplace.ui.callback.LevelClickCallback;

/**
 * Fragment which shows levels of sudoku the user can solve
 */
public class LevelSelectFragment extends Fragment{

	private LevelSelectFragmentBinding mBinding;

	private LevelListAdapter mListAdapter;

	private final LevelClickCallback mLevelClickCallback =
		new LevelClickCallback(){
		@Override
		public void onClick(int stringId){
			String level = "";
			switch (stringId){
				case R.string.menu_legend:
					level = "Legend"; break;
				case R.string.menu_master:
					level = "Master"; break;
				case R.string.menu_medium:
					level = "Medium"; break;
				case R.string.menu_beginner:
					level = "Beginner"; break;
				default:
					level = "Newbie";
			}
			if (getLifecycle().getCurrentState()
					.isAtLeast(Lifecycle.State.STARTED)){
				((MainActivity) requireActivity())
					.startPlaySudokuActivity(level);
			}
		}
	};

	public LevelSelectFragment(){
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		// inflate this data binding layout
		mBinding = DataBindingUtil.inflate(inflater,
				R.layout.level_select_fragment, container, false);

		// Create and set the adapter for the RecyclerView
		mListAdapter = new LevelListAdapter(mLevelClickCallback);
		mBinding.levelList.setAdapter(mListAdapter);

		return mBinding.getRoot();
	}
}	

