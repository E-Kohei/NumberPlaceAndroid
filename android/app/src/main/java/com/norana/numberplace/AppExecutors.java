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

import androidx.annotation.NonNull;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 */
public class AppExecutors{

	private final Executor mMainThread;

	private final Executor mCalcThread;

	private AppExecutors(Executor mainThread, Executor calcThread){
		this.mMainThread = mainThread;
		this.mCalcThread = calcThread;
	}

	public AppExecutors(){
		this(new MainThreadExecutor(), Executors.newSingleThreadExecutor());
	}

	public Executor mainThread(){
		return mMainThread;
	}

	public Executor calcThread(){
		return mCalcThread;
	}

	private static class MainThreadExecutor implements Executor{
		private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

		@Override
		public void execute(@NonNull Runnable command){
			mainThreadHandler.post(command);
		}

	}
}

