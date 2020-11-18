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

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.norana.numberplace.Constants;
import com.norana.numberplace.sudoku.Sudoku;

@Entity(tableName = "sudoku_table")
public class SudokuItem{

	@PrimaryKey(autoGenerate = true)
	@NonNull
	@ColumnInfo(name = "id")
	private long id;
	@NonNull
	@ColumnInfo(name = "time")
	private long time;
	@NonNull
	@ColumnInfo(name = "status")
	private int status;
	private Sudoku sudoku;

	public SudokuItem(){}

	@Ignore
	public SudokuItem(long id, long time, int status, Sudoku sudoku){
		this.id = id;
		this.time = time;
		this.status = status;
		this.sudoku = sudoku;
	}

	@Ignore
	public SudokuItem(long time, int status, Sudoku sudoku){
		this.time = time;
		this.status = status;
		this.sudoku = sudoku;
	}

	@Ignore
	public SudokuItem(int status, Sudoku sudoku){
		this.status = status;
		if (status == Constants.STATUS_MAKING)
			this.time = -1;
		else  // playing or solved
			this.time = 0;
		this.sudoku = sudoku;
	}

	public SudokuItem copy(){
		int copyStatus = this.status;
		long copyTime = this.time;
		Sudoku copySudoku = this.sudoku.copy();
		return new SudokuItem(copyTime, copyStatus, copySudoku);
	}

	public long getId(){
		return this.id;
	}

	public void setId(long id){
		this.id = id;
	}

	public long getTime(){
		return this.time;
	}

	public void setTime(long time){
		this.time = time;
	}

	public int getStatus(){
		return this.status;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public Sudoku getSudoku(){
		return this.sudoku;
	}

	public void setSudoku(Sudoku sudoku){
		this.sudoku = sudoku;
	}
}
