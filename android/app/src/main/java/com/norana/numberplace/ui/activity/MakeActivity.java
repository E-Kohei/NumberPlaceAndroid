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

package com.norana.numberplace.ui.activity;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;

import com.norana.numberplace.Constants;
import com.norana.numberplace.R;
import com.norana.numberplace.sudoku.Pair;
import com.norana.numberplace.sudoku.Sudoku;
import com.norana.numberplace.sudoku.Note;
import com.norana.numberplace.ui.dialog.ConfirmSolveDialogFragment;
import com.norana.numberplace.viewmodel.SudokuViewModel;
import com.norana.numberplace.database.SudokuItem;
import com.norana.numberplace.util.InputLog;
import com.norana.numberplace.databinding.ActivitySudokuBinding;

public class MakeActivity extends AppCompatActivity{

	// what the user input: number, note
	private int inputMode = Constants.MODE_NUMBER;

	// ViewModel to store UI data
	private SudokuViewModel viewModel;
	// ViewDataBinding
	private ActivitySudokuBinding mBinding;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		SudokuViewModel.Factory factory = new SudokuViewModel.Factory(
				getApplication(), -1, null);

		viewModel = 
			(new ViewModelProvider(this, factory)).get(SudokuViewModel.class);

		// initialize view
		mBinding 
			= DataBindingUtil.setContentView(this, R.layout.activity_sudoku);
		setSupportActionBar(mBinding.toolbar);
		mBinding.toolbarBackground.setImageResource(R.drawable.dripdrop_blue);
		mBinding.sudokuview.setColorOnTouch(true);


		viewModel.getBackgroundText().observe(this, new Observer<String>(){
			@Override
			public void onChanged(@Nullable final String newText){
				// set the background text
				mBinding.backgroundText.setText(newText);
				// and clear the text later
				if (!newText.endsWith("...")){
					Handler handler = new Handler(getMainLooper());
					handler.postDelayed(() -> {
						mBinding.backgroundText.setText("");
					}, 5000);
				}
			}
		});

		viewModel.getCurrentSudoku().observe(this, new Observer<Sudoku>(){
			@Override
			public void onChanged(@Nullable final Sudoku newCurrent){
				// set the current sudoku to the sudokuView
				mBinding.sudokuview.copySudoku(newCurrent);
			}
		});

		viewModel.getCurrentNote().observe(this, new Observer<Note>(){
			@Override
			public void onChanged(@Nullable final Note newNote){
				// set the current note to the sudokuView
				mBinding.sudokuview.copyNote(newNote);
			}
		});

		// if this is first creation, initialize sudoku the user make
		if (savedInstanceState == null){
			viewModel.setCurrentSudoku(new Sudoku(3));
			viewModel.setCurrentNote(new Note(3));
			viewModel.setLogStack(new ArrayDeque<InputLog>());
		}
		else{
			inputMode = savedInstanceState.getInt("inputMode");
			if (inputMode == Constants.MODE_NUMBER)
				((ImageButton)findViewById(R.id.mode)).setImageResource(R.drawable.sudoku_icon_number);
			else if (inputMode == Constants.MODE_NOTE)
				((ImageButton)findViewById(R.id.mode)).setImageResource(R.drawable.sudoku_icon_note);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("inputMode", inputMode);
	}

	@Override
	protected void onRestoreInstanceState( Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		inputMode = savedInstanceState.getInt("inputMode");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_make_only, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		switch (id){
		case R.id.action_save:
			// save the current sudoku
			saveSudoku();
			finish();
			return true;
		case R.id.action_undo:
			// undo
			undoInput();
			return true;
		case R.id.action_clear_note:
			// clear all notes
			clearAllNotes();
			return true;
		case R.id.action_check_solvability:
			// check whether the current sudoku is solvable
			Sudoku current1 = viewModel.getCurrentSudoku().getValue();
			viewModel.checkSudokuInBackground(current1);
			return true;
		case R.id.action_solve:
			// solve the current sudoku
			ConfirmSolveDialogFragment fragment = new ConfirmSolveDialogFragment();
			fragment.show(getSupportFragmentManager(), "confirmSolveDialog");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** callback function for background text */
	public void backgroundTextCallback(View v){
		viewModel.setBackgroundText("");
	}

	/** callback function for change inputMode button */
	public void changeModeCallback(View v){
		// change input mode
		if (inputMode == Constants.MODE_NUMBER){
			inputMode = Constants.MODE_NOTE;
			((ImageButton)findViewById(R.id.mode))
				.setImageResource(R.drawable.sudoku_icon_note);
		}
		else{  // inputMode == MODE_NOTE
			inputMode = Constants.MODE_NUMBER;
			((ImageButton)findViewById(R.id.mode))
				.setImageResource(R.drawable.sudoku_icon_number);
		}
	}

	/** callback function for number buttton */
	public void numberClickCallback(View v){
		Pair<Integer, Integer> cell = mBinding.sudokuview.getSelectedCell();
		if (cell != null){
			Integer n = (Integer) v.getTag();
			if (inputMode == Constants.MODE_NUMBER){
				// store log object
				int before = viewModel.getNumberInCurrentSudoku(cell);
				InputLog log = new InputLog(before, n, cell,
						Constants.MODE_NUMBER);
				viewModel.getLogStack().addFirst(log);
				// set number
				viewModel.setNumberInCurrentSudoku(cell, n);
			}
			else if (inputMode == Constants.MODE_NOTE){
				// store log object
				int before = viewModel.getNumberInCurrentNote(cell);
				int after = before ^ (1<<(n-1));
				InputLog log = new InputLog(before, after, cell,
						Constants.MODE_NOTE);
				viewModel.getLogStack().addFirst(log);
				// set note
				viewModel.toggleNumberInCurrentNote(cell, n);
			}
			mBinding.sudokuview.setSelectedCell(getNextCell(cell));
		}
	}

	private Pair<Integer, Integer> getNextCell(Pair<Integer, Integer> cell){
		int number = cell.getFirst() * 9 + cell.getSecond() ;
		int number_plus = (number + 1) % 81;
		return new Pair<Integer, Integer>(number_plus/9, number_plus%9);
	}

	private void undoInput(){
		try{
			InputLog log = viewModel.getLogStack().removeFirst();
			int mode = log.getInputMode();
			Pair<Integer, Integer> cell = log.getCell();
			int before = log.getBefore();
			if (mode == Constants.MODE_NUMBER){
				viewModel.setNumberInCurrentSudoku(cell, before);
			}
			else if (mode == Constants.MODE_NOTE){
				viewModel.toggleNumberInCurrentNote(cell, before);
			}
		}
		catch (NoSuchElementException e){}
	}

	// save the current sudoku
	private void saveSudoku(){
		Sudoku currentS = viewModel.getCurrentSudoku().getValue();
		if (currentS != null){
			SudokuItem sudokuItem =
				new SudokuItem(-1, Constants.STATUS_MAKING, currentS.copy());
			viewModel.insert(sudokuItem);
		}
	}

	// clear all notes
	private void clearAllNotes(){
		viewModel.setCurrentNote(new Note(3));
	}

	// solve sudoku
	public void solveSudoku(){
		Sudoku current = viewModel.getCurrentSudoku().getValue();
		viewModel.solveSudokuInBackground(current);
	}

}
