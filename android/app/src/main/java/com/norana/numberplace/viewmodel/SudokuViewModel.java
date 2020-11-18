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

package com.norana.numberplace.viewmodel;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.content.res.AssetManager;

import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.Random;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import com.norana.numberplace.NumberPlaceApp;
import com.norana.numberplace.AppExecutors;
import com.norana.numberplace.R;
import com.norana.numberplace.SudokuRepository;
import com.norana.numberplace.sudoku.*;
import com.norana.numberplace.database.SudokuItem;
import com.norana.numberplace.util.InputLog;

public class SudokuViewModel extends AndroidViewModel{

	private SudokuRepository mRepository;

	private final long mSudokuId;
	@Nullable
	private String mLevel;
	@Nullable
	private MutableLiveData<String> mBackgroundText;
	@Nullable
	private MutableLiveData<Sudoku> mInitialSudoku;
	@Nullable
	private MutableLiveData<Sudoku> mCurrentSudoku;
	@Nullable
	private MutableLiveData<Note> mCurrentNote;
	private Deque<InputLog> mLogStack;
	@Nullable
	private final LiveData<SudokuItem> mObservableSudoku;

	public SudokuViewModel(@NonNull Application application,
			SudokuRepository repository, long id, String level){
		super(application);
		mRepository = repository;

		mLevel = level;

		mSudokuId = id;
		if (mSudokuId != -1){
			// -1 implies the initial sudoku is not a saved sudoku but new one
			mObservableSudoku = mRepository.getSudokuById(mSudokuId);
		}
		else{
			mObservableSudoku = null;
		}
	}

	public long getSudokuId(){ return mSudokuId; }

	@Nullable
	public String getLevel(){ return mLevel; }

	public MutableLiveData<String> getBackgroundText(){
		if (mBackgroundText == null)
			mBackgroundText = new MutableLiveData<String>();
		return mBackgroundText;
	}

	public MutableLiveData<Sudoku> getInitialSudoku(){
		if (mInitialSudoku == null)
			mInitialSudoku = new MutableLiveData<Sudoku>();
		return mInitialSudoku;
	}

	public MutableLiveData<Sudoku> getCurrentSudoku(){
		if (mCurrentSudoku == null)
			mCurrentSudoku = new MutableLiveData<Sudoku>();
		return mCurrentSudoku;
	}

	public int getNumberInInitialSudoku(Pair<Integer,Integer> cell){
		Sudoku initial = mInitialSudoku.getValue();
		if (initial != null)
			return initial.getElement(cell);
		else
			return 0;
	}

	public int getNumberInCurrentSudoku(Pair<Integer,Integer> cell){
		Sudoku current = mCurrentSudoku.getValue();
		if (current != null)
			return current.getElement(cell);
		else
			return 0;
	}

	public MutableLiveData<Note> getCurrentNote(){
		if (mCurrentNote == null)
			mCurrentNote = new MutableLiveData<Note>();
		return mCurrentNote;
	}

	public int getNumberInCurrentNote(Pair<Integer,Integer> cell){
		Note current = mCurrentNote.getValue();
		if (current != null)
			return current.getNoteNumber(cell);
		else
			return 0;
	}

	public Deque<InputLog> getLogStack(){ 
		if (mLogStack == null)
			mLogStack = new ArrayDeque<InputLog>();
		return mLogStack;
	}

	@Nullable
	public LiveData<SudokuItem> getObservableSudoku(){ 
		return mObservableSudoku;
	}

	public void setBackgroundText(@Nullable String text){
		if (mBackgroundText == null)
			mBackgroundText = new MutableLiveData<String>();
		mBackgroundText.setValue(text);
	}

	public void setInitialSudoku(@Nullable Sudoku initial){
		if (mInitialSudoku == null)
			mInitialSudoku = new MutableLiveData<Sudoku>();
		mInitialSudoku.setValue(initial);
	}

	public void setCurrentSudoku(@NonNull Sudoku current){
		if (mCurrentSudoku == null)
			mCurrentSudoku = new MutableLiveData<Sudoku>();
		mCurrentSudoku.setValue(current);
	}

	public void setNumberInCurrentSudoku(Pair<Integer,Integer> cell, int n){
		Sudoku current = getCurrentSudoku().getValue();
		if (current != null){
			// attention! number is not updated if cell is fixed
			current.setNumber(cell, n);

			setCurrentSudoku(current);
		}
	}

	public boolean isFixedCellInSudoku(Pair<Integer, Integer> cell){
		Sudoku current = getCurrentSudoku().getValue();
		if (current != null)
			return current.isFixedCell(cell);
		else
			return false;
	}

	public void resetSudoku(){
		Sudoku current = getCurrentSudoku().getValue();
		if (current != null){
			current.resetSudoku();
			setCurrentSudoku(current);
		}
	}

	public void fixNumbersInSudoku(){
		Sudoku current = getCurrentSudoku().getValue();
		if (current != null){
			current.fixNumbers();
			setCurrentSudoku(current);
		}
	}

	public void resetFixedNumbersInSudoku(){
		Sudoku current = getCurrentSudoku().getValue();
		if (current != null){
			current.resetFixedNumbers();
			setCurrentSudoku(current);
		}
	}

	public void setCurrentNote(@Nullable Note current){
		if (mCurrentNote == null)
			mCurrentNote = new MutableLiveData<Note>();
		mCurrentNote.setValue(current);
	}

	public void toggleNumberInCurrentNote(Pair<Integer,Integer> cell, int n){
		Note current = mCurrentNote.getValue();
		if (current != null){
			current.toggleNoteNumber(cell, n);
			setCurrentNote(current);
		}
	}

	public void setLogStack(Deque<InputLog> logStack){
		mLogStack = logStack;
	}

	public boolean isCurrentSudokuSolved(){
		Sudoku current = mCurrentSudoku.getValue();
		if (current != null)
			return current.isSolved();
		else
			return false;
	}

	public void insert(SudokuItem sudoku){
		mRepository.insert(sudoku);
	}

	/**
	 * make sudoku with certain level in back ground process
	 * and update mInitialSudoku when finished
	 */
	public void makeSudokuInBackground(){
		AppExecutors executors = ((NumberPlaceApp) getApplication())
			.getExecutors();
		executors.calcThread().execute(() -> {
			// initialize sudoku in the background process
			getBackgroundText().postValue(getApplication()
					.getString(R.string.making_text));

			Pair<Integer, Sudoku> pair = makeSudoku(mLevel);

			if (pair.getSecond() != null){
				//getInitialSudoku().postValue(pair.getSecond().copy());
				getCurrentSudoku().postValue(pair.getSecond());
			}
			if (pair.getFirst() == 1){
				getBackgroundText().postValue(getApplication()
						.getString(R.string.making_successed));
			}
			else{
				getBackgroundText().postValue(getApplication()
						.getString(R.string.making_failed));
			}
		});
	}

	/**
	 * solve sudoku in back ground process and update mCurrentSudoku
	 */
	public void solveSudokuInBackground(Sudoku sudoku){
		AppExecutors executors = ((NumberPlaceApp) getApplication())
			.getExecutors();
		executors.calcThread().execute(() -> {
			// solve sudoku in the background process
			getBackgroundText().postValue(getApplication()
					.getString(R.string.solving_text));
			Pair<Integer, Sudoku> solvedPair =
				SudokuSolver.solveSudokuWithTrials_helper(sudoku);
			if (solvedPair.getFirst() == 0){
				getBackgroundText().postValue(getApplication()
						.getString(R.string.solving_finished));
				getCurrentSudoku().postValue(solvedPair.getSecond());
			}
			else if (solvedPair.getFirst() == 1){
				getBackgroundText().postValue(getApplication()
						.getString(R.string.not_solvable));
			}
			else if (solvedPair.getFirst() == 2){
				getBackgroundText().postValue(getApplication()
						.getString(R.string.several_solutions));
			}
			else{
				getBackgroundText().postValue(getApplication()
						.getString(R.string.computation_error));
			}
		});
	}

	/**
	 * check if the sudoku is solvable and update mBackgroundText
	 */
	public void checkSudokuInBackground(Sudoku sudoku){
		AppExecutors executors = ((NumberPlaceApp) getApplication())
			.getExecutors();
		executors.calcThread().execute(() -> {
			// check solvability in background process
			getBackgroundText().postValue(getApplication()
					.getString(R.string.checking_text));
			int indicatorInt = SudokuSolver.isSolvable(sudoku);
			if (indicatorInt == 0){
				getBackgroundText().postValue(getApplication()
						.getString(R.string.solvable));
			}
			else if (indicatorInt == 1){
				getBackgroundText().postValue(getApplication()
						.getString(R.string.not_solvable));
			}
			else if (indicatorInt == 2){
				getBackgroundText().postValue(getApplication()
						.getString(R.string.several_solutions));
			}
			else{
				getBackgroundText().postValue(getApplication()
						.getString(R.string.computation_error));
			}
		});
	}
				

	/**
	 * A creator is used to inject the statuses of sudoku into the ViewModel
	 */
	public static class Factory extends ViewModelProvider.NewInstanceFactory{

		@NonNull
		private final Application mApplication;

		private final SudokuRepository mRepository;

		private final long mSudokuId;
		@Nullable
		private String mLevel;

		public Factory(@NonNull Application application, long id, String level){
			mApplication = application;
			mRepository = ((NumberPlaceApp) application).getRepository();

			mSudokuId = id;
			mLevel = level;
		}

		@Override
		public <T extends ViewModel> T create(Class<T> modelClass){
			// noinspection unchecked
			return (T) new SudokuViewModel(mApplication, mRepository,
					mSudokuId, mLevel);
		}
	}

	// make new sudoku with certain level
	private Pair<Integer, Sudoku> makeSudoku(String level){
		Sudoku newS = new Sudoku(3);
		Random rnd = new Random();
		int successIndicator = 0;
		InputStream in = null;
		BufferedReader din = null;
		final AssetManager am = getApplication().getResources().getAssets();
		int number = rnd.nextInt(50);
		try{
			if (level.equals("Legend")){
				in = am.open("legend/legend" + (number/10) + ".txt");
			}
			else if (level.equals("Master")){
				in = am.open("master/master" + number + ".txt");
			}
			else if (level.equals("Medium")){
				in = am.open("medium/medium" + number + ".txt");
			}
			else if (level.equals("Beginner")){
				in = am.open("beginner/beginner" + number + ".txt");
			}
			else {
				in = am.open("newbie/newbie" + number + ".txt");
			}
			din = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String s = din.readLine();
			if (s != null){
				Scanner sc = new Scanner(s);
				sc.useDelimiter(",");
				int i = 0;
				while (sc.hasNextInt()){
					newS.setElement(i/9, i%9, sc.nextInt());
					i++;
				}
				sc.close();
			}
			if (level.equals("legend")){
				// transform sudoku completely randomly
				SudokuMaker.transformSudokuRandomly(newS);
			}
			else{
				// transform sudoku keeping symmetry
				SudokuMaker.transformSudokuKeepingSymmetry(newS);
			}
			successIndicator = 1;
		}
		catch (IOException e){
			successIndicator = 0;
		}
		finally{
			if (din != null)
				try{ din.close(); } catch (IOException e){}
			if (in != null)
				try{ in.close(); } catch (IOException e){}
		}
		newS.fixNumbers();
		return new Pair<Integer, Sudoku>(successIndicator, newS);
	}

}
