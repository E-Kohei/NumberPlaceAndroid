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

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

import com.norana.numberplace.Constants;
import com.norana.numberplace.R;
import com.norana.numberplace.sudoku.Sudoku;
import com.norana.numberplace.sudoku.Note;
import com.norana.numberplace.sudoku.Pair;
import com.norana.numberplace.ui.dialog.ConfirmSolveDialogFragment;
import com.norana.numberplace.ui.dialog.NotSavedDialogFragment;
import com.norana.numberplace.ui.dialog.RestartDialogFragment;
import com.norana.numberplace.ui.dialog.SolvedDialogFragment;
import com.norana.numberplace.viewmodel.SudokuViewModel;
import com.norana.numberplace.database.SudokuItem;
import com.norana.numberplace.util.InputLog;
import com.norana.numberplace.databinding.ActivitySudokuBinding;


public class SavedActivity extends AppCompatActivity{

	// status of the sudoku: playing, making or solved (-1 means uninitialized)
	private int status = -1;
	// what the user inputs: number, note
	private int inputMode = Constants.MODE_NUMBER;
	// playing time (if playing)
	// (this property is not necessarily updated every time; this will be
	// updated at onSaveInstanceState, numberClickCallback(if solved),
	// saveSudoku and when observableSudoku is changed, and be initialized at
	// restart)
	private long playingTime = 0L;

	// ViewModel to store UI data
	private SudokuViewModel viewModel;
	// ViewDataBinding
	ActivitySudokuBinding mBinding;


	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		// if this is first creation, get the sudokuId of the saved sudoku
		long sudokuId = -1;
		Intent data = getIntent();
		if (data != null)
			sudokuId = data.getLongExtra("sudokuId", -1);

		SudokuViewModel.Factory factory = new SudokuViewModel.Factory(
				getApplication(), sudokuId, null);

		viewModel =
			(new ViewModelProvider(this, factory)).get(SudokuViewModel.class);

		// initialize view
		mBinding 
			= DataBindingUtil.setContentView(this, R.layout.activity_sudoku);
		setSupportActionBar(mBinding.toolbar);
		mBinding.toolbarBackground.setImageResource(R.drawable.dripdrop_green);
		mBinding.sudokuview.setColorOnTouch(true);

		// observe sudoku and etc.
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

		/*viewModel.getInitialSudoku().observe(this, new Observer<Sudoku>(){
			@Override
			public void onChanged(@Nullable final Sudoku newInitial){
				// set the initial sudoku to the sudokuView
				mBinding.sudokuview.setSudoku(newInitial);
			}
		});*/

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

		viewModel.getObservableSudoku()
			.observe(this, new Observer<SudokuItem>(){
			@Override
			public void onChanged(@Nullable final SudokuItem newItem){
				// set the initial sudoku and status when first loaded
				if (viewModel.getCurrentSudoku().getValue() == null && newItem != null){
					//viewModel.setInitialSudoku(newItem.getSudoku());
					viewModel.setCurrentSudoku(newItem.getSudoku().copy());
					initializeStatus(newItem.getStatus());
					if (newItem.getStatus() == Constants.STATUS_PLAYING){
						// show and start timer
						mBinding.setShowTimer(true);
						playingTime = newItem.getTime();
						mBinding.timeLabel
							.setBase(SystemClock.elapsedRealtime()-playingTime);
						mBinding.timeLabel.start();
					}
					else if (newItem.getStatus() == Constants.STATUS_SOLVED){
						// show timer
						mBinding.setShowTimer(true);
						playingTime = newItem.getTime();
						mBinding.timeLabel
							.setBase(SystemClock.elapsedRealtime()-playingTime);
					}
				}
			}
		});

		// if this is first creation, initialize the note and the log stack
		if (savedInstanceState == null){
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
	protected void onPause(){
		// stop timer when paused (even if the status is making)
		mBinding.timeLabel.stop();
		super.onPause();
	}

	@Override
	protected void onResume(){
		super.onResume();
		if (this.status == Constants.STATUS_PLAYING){
			// show, set and start timer if playing
			// (set showTimer true at each time since the default is false)
			mBinding.setShowTimer(true);
			mBinding.timeLabel
				.setBase(SystemClock.elapsedRealtime() - playingTime);
			mBinding.timeLabel.start();
		}
		else if (this.status == Constants.STATUS_SOLVED){
			// show and set timer if solved
			mBinding.setShowTimer(true);
			mBinding.timeLabel
				.setBase(SystemClock.elapsedRealtime() - playingTime);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		if (status == Constants.STATUS_PLAYING){
			// if playing, get playing time to save it to bundle
			// (if solved, you don't need to get updated playing time)
			playingTime = SystemClock.elapsedRealtime() -
				mBinding.timeLabel.getBase();
		}
		// save the properties (Before this, onPause is called)
		outState.putInt("status", status);
		outState.putInt("inputMode", inputMode);
		outState.putLong("playingTime", playingTime);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		// get saved properties (After this, onResumed is called)
		status = savedInstanceState.getInt("status");
		inputMode = savedInstanceState.getInt("inputMode");
		playingTime = savedInstanceState.getLong("playingTime");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		if (this.status == Constants.STATUS_PLAYING){
			getMenuInflater().inflate(R.menu.menu_play, menu);
		}
		else if (this.status == Constants.STATUS_MAKING){
			getMenuInflater().inflate(R.menu.menu_make, menu);
		}
		else if (this.status == Constants.STATUS_SOLVED){
			getMenuInflater().inflate(R.menu.menu_solved, menu);
		}
		else{
			getMenuInflater().inflate(R.menu.menu_default, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		if (this.status == Constants.STATUS_PLAYING){
			getMenuInflater().inflate(R.menu.menu_play, menu);
		}
		else if (this.status == Constants.STATUS_MAKING){
			getMenuInflater().inflate(R.menu.menu_make, menu);
		}
		else if (this.status == Constants.STATUS_SOLVED){
			getMenuInflater().inflate(R.menu.menu_solved, menu);
		}
		else{
			getMenuInflater().inflate(R.menu.menu_default, menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();

		switch (id){
			case R.id.action_save:
				// save the current sudoku
				saveSudoku();
				supportFinishAfterTransition();
				return true;
			case R.id.action_undo:
				// undo if status is not STATUS_SOLVED
				if (this.status != Constants.STATUS_SOLVED)
					undoInput();
				return true;
			case R.id.action_play:
				// play currently displayed sudoku
				playCurrentSudoku();
				return true;
			case R.id.action_make:
				// make currently displayed sudoku
				makeCurrentSudoku();
				return true;
			case R.id.action_restart:
				// restart from the initial sudoku
				RestartDialogFragment fragment1 = new RestartDialogFragment();
				fragment1.show(getSupportFragmentManager(), "restartDialog");
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
				ConfirmSolveDialogFragment fragment2 = new ConfirmSolveDialogFragment();
				fragment2.show(getSupportFragmentManager(), "confirmSolveDialog");
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed(){
		NotSavedDialogFragment fragment = new NotSavedDialogFragment();
		fragment.show(getSupportFragmentManager(), "notSavedDialog");
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

	/** callback function for number button */
	public void numberClickCallback(View v){
		Pair<Integer, Integer> cell = mBinding.sudokuview.getSelectedCell();
		if (cell != null){
			Integer n = (Integer) v.getTag();
			if (inputMode == Constants.MODE_NUMBER && !viewModel.isFixedCellInSudoku(cell)){
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
			if (this.status == Constants.STATUS_MAKING){
				mBinding.sudokuview.setSelectedCell(getNextCell(cell));
			}
			else if(this.status == Constants.STATUS_PLAYING &&
					viewModel.isCurrentSudokuSolved()){
				// get playing time and stop the timer
				playingTime = SystemClock.elapsedRealtime() - 
					mBinding.timeLabel.getBase();
				mBinding.timeLabel.stop();
				// update status and show solved dialog
				this.status = Constants.STATUS_SOLVED;
				SolvedDialogFragment newFragment =
					SolvedDialogFragment.createInstance(
							mBinding.timeLabel.getText().toString());
				newFragment.show(getSupportFragmentManager(), "solvedDialog");
			}
		}
	}

	private void initializeStatus(int status){
		this.status = status;
	}

	private Pair<Integer, Integer> getNextCell(Pair<Integer, Integer> cell){
		int number = cell.getFirst() * 9 + cell.getSecond() ;
		int number_plus = (number + 1) % 81;
		return new Pair<Integer, Integer>(number_plus/9, number_plus%9);
	}

	// undo
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

	// play currently displayed sudoku
	private void playCurrentSudoku(){
		// change the status to play mode if initialized
		if (this.status != -1){
			this.status = Constants.STATUS_PLAYING;
			viewModel.fixNumbersInSudoku();
			mBinding.setShowTimer(true);
			mBinding.timeLabel.setBase(SystemClock.elapsedRealtime());
			mBinding.timeLabel.start();
		}
	}

	// make currently displayed sudoku
	private void makeCurrentSudoku(){
		// change the status to make mode if initialized
		if (this.status != -1){
			this.status = Constants.STATUS_MAKING;
			viewModel.resetFixedNumbersInSudoku();
			mBinding.timeLabel.stop();
			mBinding.setShowTimer(false);
		}
	}

	/** save the current sudoku **/
	public void saveSudoku(){
		Sudoku current = viewModel.getCurrentSudoku().getValue();
		if (current != null && viewModel.getSudokuId() != -1){
			if (this.status == Constants.STATUS_PLAYING ||
					this.status == Constants.STATUS_SOLVED){
				// if status is playing or solved, save current playing time
				playingTime = SystemClock.elapsedRealtime() -
					mBinding.timeLabel.getBase();
				SudokuItem sudokuItem = 
					new SudokuItem(viewModel.getSudokuId(), playingTime,
									this.status, current.copy());
				viewModel.insert(sudokuItem);
			}
			else{  // if status is making, save sudoku without timer
				SudokuItem sudokuItem =
					new SudokuItem(viewModel.getSudokuId(), -1,
									this.status, current.copy());
				viewModel.insert(sudokuItem);
			}
		}
	}

	// reset sudoku to initialSudoku and start playing
	public void restart(){
		viewModel.setCurrentNote(new Note(3));
		viewModel.resetSudoku();
		this.status = Constants.STATUS_PLAYING;
		// reset and start timer
		playingTime = 0L;
		mBinding.timeLabel.stop();
		mBinding.timeLabel.setBase(SystemClock.elapsedRealtime());
		mBinding.timeLabel.start();
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
