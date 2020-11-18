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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;

import com.norana.numberplace.Constants;
import com.norana.numberplace.R;
import com.norana.numberplace.sudoku.Sudoku;
import com.norana.numberplace.sudoku.Note;
import com.norana.numberplace.sudoku.Pair;
import com.norana.numberplace.ui.dialog.ConfirmSolveDialogFragment;
import com.norana.numberplace.ui.dialog.NextPuzzleDialogFragment;
import com.norana.numberplace.ui.dialog.NotSavedDialogFragment;
import com.norana.numberplace.ui.dialog.RestartDialogFragment;
import com.norana.numberplace.ui.dialog.SolvedDialogFragment;
import com.norana.numberplace.viewmodel.SudokuViewModel;
import com.norana.numberplace.database.SudokuItem;
import com.norana.numberplace.util.InputLog;
import com.norana.numberplace.databinding.ActivitySudokuBinding;

public class PlayActivity extends AppCompatActivity{

	// status of the sudoku: playing or solved here
	private int status = Constants.STATUS_PLAYING;
	// what the user input: number, note
	private int inputMode = Constants.MODE_NUMBER;
	// playing time
	// (this property is not necessarily updated every time; this will be
	// updated at onSavedInstanceState, numberClickCallback(if solved) and 
	// saveSudoku, and be initialized at playNewSudoku and restart)
	private long playingTime = 0L;

	// ViewModel to store UI data
	private SudokuViewModel viewModel;
	// ViewDataBinding
	private ActivitySudokuBinding mBinding;


	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		// get the level of sudoku the user solve
		String level = null;
		Intent data = getIntent();
		if (data != null)
			level = data.getStringExtra("level");

		SudokuViewModel.Factory factory = new SudokuViewModel.Factory(
				getApplication(), -1, level);

		viewModel =
			(new ViewModelProvider(this, factory)).get(SudokuViewModel.class);

		// initialize view
		mBinding 
			= DataBindingUtil.setContentView(this, R.layout.activity_sudoku);
		setSupportActionBar(mBinding.toolbar);
		mBinding.toolbarBackground.setImageResource(R.drawable.dripdrop_red);
		mBinding.setShowTimer(true);
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

		// if this is first creation, initialize sudoku the user solve
		if (savedInstanceState == null){
			//viewModel.setInitialSudoku(new Sudoku(3));
			viewModel.setBackgroundText("");
			viewModel.setCurrentSudoku(new Sudoku(3));
			viewModel.setCurrentNote(new Note(3));
			viewModel.setLogStack(new ArrayDeque<InputLog>());
			viewModel.makeSudokuInBackground();
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
		// stop timer when paused
		mBinding.timeLabel.stop();
		super.onPause();
	}

	@Override
	protected void onResume(){
		super.onResume();
		// set timer when resumed
		mBinding.timeLabel.setBase(SystemClock.elapsedRealtime() - playingTime);
		if (this.status == Constants.STATUS_PLAYING)
			// and if playing, restart timer
			mBinding.timeLabel.start();
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
		// get saved properties (After this, onResume is called)
		status = savedInstanceState.getInt("status");
		inputMode = savedInstanceState.getInt("inputMode");
		playingTime = savedInstanceState.getLong("playingTime");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_play_only, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();

		switch (id){
			case R.id.action_new:
				// remake the sudoku the user plays and restart
				NextPuzzleDialogFragment fragment1 = new NextPuzzleDialogFragment();
				fragment1.show(getSupportFragmentManager(), "nextPuzzleDialog");
				return true;
			case R.id.action_save:
				// save the current sudoku
				saveSudoku();
				return true;
			case R.id.action_undo:
				// undo
				undoInput();
				return true;
			case R.id.action_restart:
				// restart from the initial sudoku
				RestartDialogFragment fragment2 = new RestartDialogFragment();
				fragment2.show(getSupportFragmentManager(), "restartDialog");
				return true;
			case R.id.action_clear_note:
				// clear all notes
				clearAllNotes();
				return true;
			case R.id.action_solve:
				// solve the current sudoku
				ConfirmSolveDialogFragment fragment3 = new ConfirmSolveDialogFragment();
				fragment3.show(getSupportFragmentManager(), "confirmSolveDialog");
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

	/** callback function for change number button */
	public void numberClickCallback(View v){
		Pair<Integer, Integer> cell = mBinding.sudokuview.getSelectedCell();
		if (cell != null && this.status == Constants.STATUS_PLAYING){
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
			if (this.status == Constants.STATUS_PLAYING &&
					viewModel.isCurrentSudokuSolved()){
				// if sudoku is solved,
				// get playing time and stop the timer
				playingTime = SystemClock.elapsedRealtime() -
					mBinding.timeLabel.getBase();
				mBinding.timeLabel.stop();
				// update the status and show solved dialog
				this.status = Constants.STATUS_SOLVED;
				SolvedDialogFragment newFragment
					= SolvedDialogFragment.createInstance(
							mBinding.timeLabel.getText().toString());
				newFragment.show(getSupportFragmentManager(), "solvedDialog");
			}
		}
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

	// make new sudoku and restart
	public void playNewSudoku(){
		// make new sudoku
		viewModel.makeSudokuInBackground();
		// reset and start timer
		playingTime = 0L;
		mBinding.timeLabel.stop();
		mBinding.timeLabel.setBase(SystemClock.elapsedRealtime());
		mBinding.timeLabel.start();
	}

	/** save the current sudoku **/
	public void saveSudoku(){
		Sudoku currentS = viewModel.getCurrentSudoku().getValue();
		if (currentS != null){
			// get current playing time
			playingTime = SystemClock.elapsedRealtime() - 
				mBinding.timeLabel.getBase();
			SudokuItem sudokuItem = 
				new SudokuItem(playingTime, this.status, currentS.copy());
			viewModel.insert(sudokuItem);;
		}
	}

	// reset sudoku to initialSudoku
	public void restart(){
		viewModel.setCurrentNote(new Note(3));
		viewModel.resetSudoku();
		this.status = Constants.STATUS_PLAYING;
		// start timer
		playingTime = 0L;
		mBinding.timeLabel.stop();
		mBinding.timeLabel.setBase(SystemClock.elapsedRealtime());
		mBinding.timeLabel.start();
	}

	// clear all notes
	private void clearAllNotes(){
		viewModel.setCurrentNote(new Note(3));
	}

	// solve sudoku currently displayed
	public void solveSudoku(){
		Sudoku current = viewModel.getCurrentSudoku().getValue();
		viewModel.solveSudokuInBackground(current);
	}
}
