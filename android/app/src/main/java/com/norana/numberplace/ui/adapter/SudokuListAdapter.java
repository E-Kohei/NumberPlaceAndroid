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

import com.norana.numberplace.R;
import com.norana.numberplace.ui.callback.CopyClickCallback;
import com.norana.numberplace.ui.callback.DeleteClickCallback;
import com.norana.numberplace.ui.callback.SudokuClickCallback;
import com.norana.numberplace.database.SudokuItem;
import com.norana.numberplace.databinding.SudokuItemBinding;

public class SudokuListAdapter
	extends RecyclerView.Adapter<SudokuListAdapter.SudokuItemViewHolder>{

	/* ViewHolder */
	public static class SudokuItemViewHolder extends RecyclerView.ViewHolder{

		final SudokuItemBinding binding;

		/* Constructor of ViewHolder */
		private SudokuItemViewHolder(SudokuItemBinding binding){
			super(binding.getRoot());
			this.binding = binding;
		}

		public SudokuItemBinding getBinding(){
			return binding;
		}
	}

	// cached copy of list of SudokuItems(Entity)
	private List<SudokuItem> sudokuItems;
	// used for represent status from as int to as String
	private static List<String> statusList;
	static{
		statusList = new ArrayList<>();
		statusList.add("Playing"); statusList.add("Making");
		statusList.add("Solved");
	}

	@Nullable
	private final SudokuClickCallback mSudokuClickCallback;

	@Nullable
	private final DeleteClickCallback mDeleteClickCallback;

	@Nullable
	private final CopyClickCallback mCopyClickCallback;

	/* Constructor of Adapter */
	public SudokuListAdapter(@Nullable SudokuClickCallback clickCallback,
			@Nullable DeleteClickCallback deleteClickCallback,
			@Nullable CopyClickCallback copyClickCallback){
		mSudokuClickCallback = clickCallback;
		mDeleteClickCallback = deleteClickCallback;
		mCopyClickCallback = copyClickCallback;
		setHasStableIds(true);
	}

	/* Methods of Adapter */
	@NonNull
	@Override
	public SudokuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
			int viewType){
		SudokuItemBinding binding = DataBindingUtil.inflate(
				LayoutInflater.from(parent.getContext()), R.layout.sudoku_item,
				parent, false);
		binding.setCallback(mSudokuClickCallback);
		binding.setDeleteCallback(mDeleteClickCallback);
		binding.setCopyCallback(mCopyClickCallback);
		return new SudokuItemViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(SudokuItemViewHolder holder, int position){
		holder.binding.setStatusList(statusList);
		holder.binding.setSudokuItem(sudokuItems.get(position));
		holder.binding.executePendingBindings();
	}

	public void setSudokus(List<SudokuItem> sudokus){
		sudokuItems = sudokus;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount(){
		if (sudokuItems != null)
			return sudokuItems.size();
		else
			return 0;

	}

	@Override
	public long getItemId(int position){
		return sudokuItems.get(position).getId();
	}
}

