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

package com.norana.numberplace.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.norana.numberplace.R;
import com.norana.numberplace.databinding.ManualMenuItemBinding;
import com.norana.numberplace.ui.callback.MenuClickCallback;

import java.util.ArrayList;
import java.util.List;

public class ManualMenuAdapter
        extends RecyclerView.Adapter<ManualMenuAdapter.ManualMenuViewHolder> {

    static class ManualMenuViewHolder extends RecyclerView.ViewHolder {
        public ManualMenuItemBinding binding;

        public ManualMenuViewHolder(ManualMenuItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private MenuClickCallback mManualMenuClickCallback;

    private static List<Integer> menus;
    static{
        menus = new ArrayList<>();
        menus.add(R.string.menu_basic_operation);
        menus.add(R.string.menu_play_manual);
        menus.add(R.string.menu_make_manual);
        menus.add(R.string.menu_saved_manual);
    }

    public ManualMenuAdapter(@Nullable MenuClickCallback clickCallback){
        mManualMenuClickCallback = clickCallback;
    }

    @NonNull
    @Override
    public ManualMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        ManualMenuItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.manual_menu_item, parent, false);
        binding.setCallback(mManualMenuClickCallback);
        return new ManualMenuViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ManualMenuViewHolder holder, int position){
        holder.binding.setManualMenuId(menus.get(position));
        holder.binding.manualMenuText.setText(menus.get(position));
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
