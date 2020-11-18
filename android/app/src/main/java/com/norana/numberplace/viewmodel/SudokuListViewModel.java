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

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import com.norana.numberplace.NumberPlaceApp;
import com.norana.numberplace.SudokuRepository;
import com.norana.numberplace.database.SudokuItem;

public class SudokuListViewModel extends AndroidViewModel{
	
	private final SudokuRepository mRepository;

	private LiveData<List<SudokuItem>> mAllSudokus;

	public SudokuListViewModel(Application application){
		super(application);
		mRepository = ((NumberPlaceApp) application).getRepository();
		mAllSudokus = mRepository.getAllSudokus();
	}

	public LiveData<List<SudokuItem>> getAllSudokus(){
		return mAllSudokus;
	}

	public void insert(SudokuItem sudoku){
		mRepository.insert(sudoku);
	}

	public void delete(long sudokuId){
		mRepository.delete(sudokuId);
	}
}

