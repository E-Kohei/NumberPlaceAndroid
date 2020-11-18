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

import androidx.room.TypeConverter;

import java.util.Scanner;
import java.util.regex.Pattern;

import com.norana.numberplace.sudoku.Sudoku;

public class SudokuConverter{

	private static Pattern pattern = Pattern.compile("f");

	@TypeConverter
	public static Sudoku toSudoku(String value){
		if (value == null)
			return null;
		int[][] numbers = new int[9][9];
		boolean[][] fixed = new boolean[9][9];
		Scanner sc = new Scanner(value);
		sc.useDelimiter(",");
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sc.hasNextInt()) {
					numbers[i][j] = sc.nextInt();
				}
				if (sc.hasNext(pattern)) {
					fixed[i][j] = true;
					sc.next();
				}
			}
		}
		sc.close();
		return new Sudoku(numbers, fixed, 3);
	}

	@TypeConverter
	public static String toSudokuString(Sudoku sudoku){
		if (sudoku == null)
			return null;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				builder.append(sudoku.getElement(i, j));
				builder.append(",");
				if (sudoku.isFixedCell(i, j))
					builder.append("f,");
			}
		}
		return builder.toString();
	}
}
