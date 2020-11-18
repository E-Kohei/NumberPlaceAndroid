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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;

import java.util.List;

import com.norana.numberplace.sudoku.Sudoku;

@Dao
public interface SudokuDao{

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(SudokuItem sudoku);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertAll(List<SudokuItem> sudokus);

	@Query("DELETE FROM sudoku_table")
	void deleteAll();

	@Query("DELETE FROM sudoku_table WHERE id = :id")
	void delete(long id);

	@Query("SELECT * FROM sudoku_table ORDER BY ID")
	LiveData<List<SudokuItem>> getAllSudokus();

	@Query("SELECT * FROM sudoku_table WHERE status = :status")
	LiveData<List<SudokuItem>> getSudokusByStatus(int status);

	@Query("SELECT * FROM sudoku_table WHERE id = :id")
	LiveData<SudokuItem> getSudokuById(long id);

	@Query("UPDATE sudoku_table SET sudoku = :new_sudoku WHERE id = :id")
	int updateSudoku(long id, Sudoku new_sudoku);

}
