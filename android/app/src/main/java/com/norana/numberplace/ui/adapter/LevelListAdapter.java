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

package com.norana.numberplace.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.databinding.DataBindingUtil;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.ArrayList;

import com.norana.numberplace.R;
import com.norana.numberplace.databinding.LevelItemBinding;
import com.norana.numberplace.ui.callback.LevelClickCallback;

public class LevelListAdapter
	extends RecyclerView.Adapter<LevelListAdapter.LevelViewHolder>{

	/* ViewHolder */
	static class LevelViewHolder extends RecyclerView.ViewHolder{

		private final LevelItemBinding binding;

		/* Constructor of ViewHolder */
		private LevelViewHolder(LevelItemBinding binding){
			super(binding.getRoot());
			this.binding = binding;
		}

	}

	// initialize levels to be displayed
	@Nullable
	private final LevelClickCallback mLevelClickCallback;
	private static List<Integer> levels;
	private static List<Integer> fabs;
	static{
		levels = new ArrayList<>();
		levels.add(R.string.menu_newbie); levels.add(R.string.menu_beginner);
		levels.add(R.string.menu_medium); levels.add(R.string.menu_master);
		levels.add(R.string.menu_legend);
		fabs = new ArrayList<>();
		fabs.add(R.drawable.fab_red); fabs.add(R.drawable.fab_red);
		fabs.add(R.drawable.fab_red); fabs.add(R.drawable.fab_red);
		fabs.add(R.drawable.fab_red);
	}

	/* Constructor of Adapter */
	public LevelListAdapter(@Nullable LevelClickCallback clickCallback){
		mLevelClickCallback = clickCallback;
	}

	/* Methods of Adapter */
	@NonNull
	@Override
	public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
			int viewType){
		LevelItemBinding binding = DataBindingUtil
			.inflate(LayoutInflater.from(parent.getContext()),
					R.layout.level_item, parent, false);
		binding.setCallback(mLevelClickCallback);
		return new LevelViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(LevelViewHolder holder, int position){
		holder.binding.setLevelId(levels.get(position));
		holder.binding.levelText.setText(levels.get(position));
		holder.binding.levelBall.setImageResource(fabs.get(position));
		holder.binding.executePendingBindings();
	}

	@Override
	public int getItemCount(){
		return levels == null ? 0: levels.size();

	}

	@Override
	public long getItemId(int position){
		return position;
	}
}

