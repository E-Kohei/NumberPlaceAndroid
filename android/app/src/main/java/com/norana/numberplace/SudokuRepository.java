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

package com.norana.numberplace;

import androidx.lifecycle.LiveData;
import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import com.norana.numberplace.database.SudokuDatabase;
import com.norana.numberplace.database.SudokuDao;
import com.norana.numberplace.database.SudokuItem;

public class SudokuRepository{

	private static SudokuRepository sInstance;

	private final SudokuDatabase mDatabase;

	private final SudokuDao mSudokuDao;

	private LiveData<List<SudokuItem>> mAllSudokus;

	private SudokuRepository(final SudokuDatabase database){
		mDatabase = database;
		mSudokuDao = mDatabase.sudokuDao();
		mAllSudokus = mSudokuDao.getAllSudokus();
	}

	public static SudokuRepository getRepository(final SudokuDatabase database){
		if (sInstance == null){
			synchronized (SudokuRepository.class){
				if (sInstance == null){
					sInstance = new SudokuRepository(database);
				}
			}
		}
		return sInstance;
	}

	public LiveData<List<SudokuItem>> getAllSudokus(){
		return mAllSudokus;
	}

	public LiveData<SudokuItem> getSudokuById(long id){
		return mSudokuDao.getSudokuById(id);
	}

	public void insert(SudokuItem sudoku){
		new insertAsyncTask(mSudokuDao).execute(sudoku);
	}

	public void delete(long sudokuId){
		new deleteAsyncTask(mSudokuDao).execute(sudokuId);
	}

	private static class insertAsyncTask
			extends AsyncTask<SudokuItem, Void, Void>{
		private SudokuDao mAsyncTaskDao;
		
		insertAsyncTask(SudokuDao dao){
			mAsyncTaskDao = dao;
		}

		protected Void doInBackground(final SudokuItem... params){
			mAsyncTaskDao.insert(params[0]);
			return null;
		}
	}

	private static class deleteAsyncTask
			extends AsyncTask<Long, Void, Void>{
		private SudokuDao mAsyncTaskDao;

		deleteAsyncTask(SudokuDao dao){
			mAsyncTaskDao = dao;
		}

		protected Void doInBackground(final Long... params){
			mAsyncTaskDao.delete(params[0]);
			return null;
		}
	}
}
