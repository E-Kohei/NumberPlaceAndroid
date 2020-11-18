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

import android.app.Application;

import com.norana.numberplace.database.SudokuDatabase;

/**
 * Android Application class
 */
public class NumberPlaceApp extends Application{

	private AppExecutors mAppExecutors;

	@Override
	public void onCreate(){
		super.onCreate();

		mAppExecutors = new AppExecutors();
	}

	public SudokuDatabase getDatabase(){
		return SudokuDatabase.getDatabase(this);
	}

	public SudokuRepository getRepository(){
		return SudokuRepository.getRepository(getDatabase());
	}

	public AppExecutors getExecutors(){
		return mAppExecutors;
	}
}
