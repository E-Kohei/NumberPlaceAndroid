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

public class Constants{
	// flags which indicate the status of the sudoku
	public final static int STATUS_PLAYING = 0;
	public final static int STATUS_MAKING = 1;
	public final static int STATUS_SOLVED = 2;

	// flags which indicate the input mode of sudoku
	public final static int MODE_NUMBER = 200;
	public final static int MODE_NOTE = 201;
}
