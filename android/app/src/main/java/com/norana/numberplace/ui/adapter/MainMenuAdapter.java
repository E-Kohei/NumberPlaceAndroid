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
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.ArrayList;

import com.norana.numberplace.databinding.MainMenuItemBinding;
import com.norana.numberplace.R;
import com.norana.numberplace.ui.callback.MenuClickCallback;

public class MainMenuAdapter
	extends RecyclerView.Adapter<MainMenuAdapter.MainMenuViewHolder>{

	/* ViewHolder */
	static class MainMenuViewHolder extends RecyclerView.ViewHolder{

		private final MainMenuItemBinding binding;

		/* Constructor of ViewHolder */
		private MainMenuViewHolder(MainMenuItemBinding binding){
			super(binding.getRoot());
			this.binding = binding;
		}
	}

	// initialize menus to be displayed
	@Nullable
	private final MenuClickCallback mMainMenuClickCallback;
	private static List<Integer> menus;
	private static List<Integer> fabs;
	static{
		menus = new ArrayList<>();
		menus.add(R.string.menu_play); menus.add(R.string.menu_make);
		menus.add(R.string.menu_saved);
		fabs = new ArrayList<>();
		fabs.add(R.drawable.fab_red); fabs.add(R.drawable.fab_blue);
		fabs.add(R.drawable.fab_green);
	}

	/* Constructor of Adapter */
	public MainMenuAdapter(@Nullable MenuClickCallback clickCallback){
		mMainMenuClickCallback = clickCallback;
	}

	/* Methods of Adapter */
	@NonNull
	@Override
	public MainMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
			int viewType){
		MainMenuItemBinding binding = DataBindingUtil
			.inflate(LayoutInflater.from(parent.getContext()),
					R.layout.main_menu_item, parent, false);
		binding.setCallback(mMainMenuClickCallback);
		return new MainMenuViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(MainMenuViewHolder holder, int position){
		holder.binding.setMainMenuId(menus.get(position));
		holder.binding.menuText.setText(menus.get(position));
		holder.binding.menuBall.setImageResource(fabs.get(position));
		holder.binding.executePendingBindings();
	}

	@Override
	public int getItemCount(){
		return menus == null ? 0: menus.size();
	}

	@Override
	public long getItemId(int position){
		return position;
	}
}
