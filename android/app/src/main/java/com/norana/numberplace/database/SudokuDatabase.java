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

package com.norana.numberplace.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import android.content.Context;
import android.os.AsyncTask;

import com.norana.numberplace.Constants;
import com.norana.numberplace.sudoku.Sudoku;

@Database(entities = {SudokuItem.class}, version=2)
@TypeConverters(SudokuConverter.class)
public abstract class SudokuDatabase extends RoomDatabase{

	private static volatile SudokuDatabase INSTANCE;

	public static final String DATABASE_NAME = "sudoku_table";

	public abstract SudokuDao sudokuDao();

	public static SudokuDatabase getDatabase(final Context context){
		if (INSTANCE == null){
			synchronized (SudokuDatabase.class){
				if (INSTANCE == null){
					INSTANCE = Room.databaseBuilder(
							context.getApplicationContext(),
							SudokuDatabase.class, DATABASE_NAME)
						.build();
				}
			}
		}
		return INSTANCE;
	}

	private static RoomDatabase.Callback sRoomDatabaseCallback =
		new RoomDatabase.Callback(){
			@Override
			public void onCreate(@NonNull SupportSQLiteDatabase db){
				super.onCreate(db);
				new PopulateDbAsync(INSTANCE).execute();
			}
		};

	private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>{
		private final SudokuDao mDao;

		PopulateDbAsync(SudokuDatabase db){
			mDao = db.sudokuDao();
		}

		@Override
		protected Void doInBackground(final Void... params){
			mDao.deleteAll();
			return null;
		}
	}
}
