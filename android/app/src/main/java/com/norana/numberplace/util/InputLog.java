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

package com.norana.numberplace.util;

import com.norana.numberplace.sudoku.Pair;

/**
 * store input logs to allow a user to undo
 */
public class InputLog{
	// input modes
	// MODE_NUMBER = 200
	// MODE_MEMO = 201

	// the input mode at the time the user input
	private int mode;

	// which cell the user changed
	private Pair<Integer, Integer> selectedCell;

	// store log by remembering the former state and the latter state
	// In memo mode, the integer is stored as 9 bits of indicators
	private int before;
	private int after;

	public InputLog(int before, int after, Pair<Integer,Integer> cell, int mode){
		this.before = before;
		this.after = after;
		this.selectedCell = cell;
		this.mode = mode;
	}

	public InputLog(int before, int after, int cell_x, int cell_y, int mode){
		this.before = before;
		this.after = after;
		this.selectedCell = new Pair<Integer, Integer>(cell_x, cell_y);
		this.mode = mode;
	}

	public int getBefore(){ return before; }

	public int getAfter(){ return after; }

	public Pair<Integer, Integer> getCell(){ return selectedCell; }

	public int getInputMode(){ return mode; }

}
