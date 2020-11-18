package com.norana.numberplace.sudoku;

import java.util.*;
import java.util.Random;

public class SudokuMaker{

	/**
	 * Make sudoku from valid sudoku as question by manipulating it 
	 * randomly.
	 *
	 * @param sudoku valid sudoku as question
	 * @return new sudoku for question
	 */
	public static Sudoku makeSudokuFromQuestion(Sudoku sudoku){
		Random rnd = new Random();
		int size = sudoku.getSize();
		Sudoku sudokuQ = sudoku.copy();
		// permute numbers randomly
		int[] numPermutation = makeRandomAry(1, size*size+1);
		SudokuManipulator.permuteNumbers(sudokuQ, numPermutation);
		// rotate or permute rows and columns randomly
		for (int i = 0; i < 36; i++){
			int operation = rnd.nextInt(7);
			int[] permutation = makeRandomAry(0, size);
			switch (operation){
				case 0:
					// do nothing
					break;
				case 1:
					// reflect
					SudokuManipulator.
					reflectSudoku(sudokuQ, rnd.nextInt(3));
					break;
				case 2:
					// rotate
					SudokuManipulator.
					rotateSudoku(sudokuQ, rnd.nextInt(4));
					break;
				case 3:
					// permute block-rows
					SudokuManipulator.
					permuteBlockRows(sudokuQ, permutation);
					break;
				case 4:
					// permute block-columns
					SudokuManipulator.
					permuteBlockCols(sudokuQ, permutation);
					break;
				case 5:
					// permute rows in a block-rows
					SudokuManipulator.
					permuteOneBlockRow(sudokuQ, 
						rnd.nextInt(size), permutation);
					break;
				case 6:
					// permute columns in a block-columns
					SudokuManipulator.
					permuteOneBlockCol(sudokuQ,
						rnd.nextInt(size), permutation);
					break;
			}
		}
		return sudokuQ;
	}

	/**
	 * Make sudoku from solved sudoku by scraping number randomly.
	 *
	 * @param sudoku solved sudoku
	 * @return new sudoku for question
	 */
	public static Sudoku makeSudokuFromSolution(Sudoku sudoku){
		Random rnd = new Random();
		int size = sudoku.getSize();
		Sudoku sudokuQ = sudoku.copy();
		while (true){
			int i = rnd.nextInt(size*size);
			int j = rnd.nextInt(size*size);
			int n1 = sudokuQ.getElement(i,j);
			int n2 = sudokuQ.getElement(size*size-1-i,
						    size*size-1-j);
			sudokuQ.setElement(i, j, 0);
			sudokuQ.setElement(size*size-1-i, size*size-1-j, 0);
			if (SudokuSolver.isSolvable(sudokuQ) == 2){
				sudokuQ.setElement(i, j, n1);
				sudokuQ.setElement(size*size-1-i,
						   size*size-1-j, n2);
				return sudokuQ;
			}
		}
	}

	/**
	 * Transform sudoku randomly
	 *
	 * @param sudoku sudoku to be transformed
	 */
	public static void transformSudokuRandomly(Sudoku sudoku){
		Random rnd = new Random();
		int size = sudoku.getSize();
		// permute numbers randomly
		int[] numPermutation = makeRandomAry(1, size*size+1);
		SudokuManipulator.permuteNumbers(sudoku, numPermutation);
		// rotate or permute rows and columns randomly keeping symmetry
		for (int i = 0; i < 36; i++){
			int operation = rnd.nextInt(7);
			int[] permutation = makeRandomAry(0, size);
			switch (operation){
				case 0:
					// do nothing
					break;
				case 1:
					// reflect
					SudokuManipulator.
					reflectSudoku(sudoku, rnd.nextInt(3));
					break;
				case 2:
					// rotate
					SudokuManipulator.
					rotateSudoku(sudoku, rnd.nextInt(4));
					break;
				case 3:
					// permute block-rows
					SudokuManipulator.
					permuteBlockRows(sudoku, permutation);
					break;
				case 4:
					// permute block-columns
					SudokuManipulator.
					permuteBlockCols(sudoku, permutation);
					break;
				case 5:
					// permute rows in a block-row
					int bRow = rnd.nextInt(size);
					SudokuManipulator.
					permuteOneBlockRow(sudoku, bRow, permutation);
					break;
				case 6:
					// permute columns in a block-column
					int bCol = rnd.nextInt(size);
					SudokuManipulator.
					permuteOneBlockCol(sudoku, bCol, permutation);
					break;
			}
		}
	}


	/**
	 * Transform sudoku randomly keeping its symmetry
	 *
	 * @param sudoku sudoku to be transformed
	 */
	public static void transformSudokuKeepingSymmetry(Sudoku sudoku){
		Random rnd = new Random();
		int size = sudoku.getSize();
		// permute numbers randomly
		int[] numPermutation = makeRandomAry(1, size*size+1);
		SudokuManipulator.permuteNumbers(sudoku, numPermutation);
		// rotate or permute rows and columns randomly keeping symmetry
		for (int i = 0; i < 36; i++){
			int operation = rnd.nextInt(7);
			switch (operation){
				case 0:
					// do nothing
					break;
				case 1:
					// reflect
					SudokuManipulator.
					reflectSudoku(sudoku, rnd.nextInt(3));
					break;
				case 2:
					// rotate
					SudokuManipulator.
					rotateSudoku(sudoku, rnd.nextInt(4));
					break;
				case 3:
					// permute block-rows symmetrically
					int[] symmetricalP1 = makeSymmetricalRandomAry(0, size);
					SudokuManipulator.
					permuteBlockRows(sudoku, symmetricalP1);
					break;
				case 4:
					// permute block-columns symmetrically
					int[] symmetricalP2 = makeSymmetricalRandomAry(0, size);
					SudokuManipulator.
					permuteBlockCols(sudoku, symmetricalP2);
					break;
				case 5:
					// permute rows in one block-row
					int bRow = rnd.nextInt(size);
					if (size % 2 == 1 && bRow == size/2){
						// if bRow is the center of sudoku, permute only bRow
						int[] symmetricalP3 = makeSymmetricalRandomAry(0, size);
						SudokuManipulator.
						permuteOneBlockRow(sudoku, bRow, symmetricalP3);
					}
					else{
						// else permute both bRow and size-bRow-1
						int[] permutation1 = makeRandomAry(0, size);
						SudokuManipulator.
						permuteOneBlockRow(sudoku, bRow, permutation1);
						reverseNumAndOrder(permutation1);
						SudokuManipulator.
						permuteOneBlockRow(sudoku, size-bRow-1, permutation1);
					}
					break;
				case 6:
					// permute columns in one block-column
					int bCol = rnd.nextInt(size);
					if (size % 2 == 1 && bCol == size/2){
						// if bRow is the center of sudoku, permute only bRow
						int[] symmetricalP4 = makeSymmetricalRandomAry(0, size);
						SudokuManipulator.
						permuteOneBlockCol(sudoku, bCol, symmetricalP4);
					}
					else{
						// else permute both bRow and size-bRow-1
						int[] permutation2 = makeRandomAry(0, size);
						SudokuManipulator.
						permuteOneBlockCol(sudoku, bCol, permutation2);
						reverseNumAndOrder(permutation2);
						SudokuManipulator.
						permuteOneBlockCol(sudoku, size-bCol-1, permutation2);
					}
					break;
			}
		}
	}



	public static Sudoku makeSudokuFromSolutionLevel2(Sudoku sudoku){
		Random rnd = new Random();
		int size = sudoku.getSize();
		int trial = 1;
loop:		while (true){
			Sudoku sudokuQ = sudoku.copy();
			System.out.println("trial: "+trial);
			while (true){
				int i = rnd.nextInt(size*size);
				int j = rnd.nextInt(size*size);
				int n1 = sudokuQ.getElement(i,j);
				sudokuQ.setElement(i, j, 0);
				if (!SudokuSolver.isSolvableLevel2(sudokuQ)){
					sudokuQ.setElement(i, j, n1);
					if (!SudokuSolver.isSolvableLevel1(sudokuQ) || trial > 100)
						return sudokuQ;
					else{
						trial++;
						continue loop;
					}
				}
			}
		}
	}
				

	// swap two elements in the int-array
	static void swap(int[] intAry, int index1, int index2){
		int temp = intAry[index1];
		intAry[index1] = intAry[index2];
		intAry[index2] = temp;
	}

	// make int-array which starts with startNum and ends with 
	// endNum-1
	static int[] makeIntAry(int startNum, int endNum){
		int size = endNum - startNum;
		int[] ary = new int[size];
		for (int i = 0; i < size; i++)
			ary[i] = startNum + i;
		return ary;
	}

	// shuffle int-array randomly
	static void shuffleAry(int[] intAry){
		Random rnd = new Random();
		int size = intAry.length;
		for (int i = 0; i < size*size; i++){
			int index1 = rnd.nextInt(size);
			int index2 = rnd.nextInt(size);
			swap(intAry, index1, index2);
		}
	}

	// make permutation array randomly
	static int[] makeRandomAry(int startNum, int endNum){
		int[] randomAry = makeIntAry(startNum, endNum);
		shuffleAry(randomAry);
		return randomAry;
	}

	// make symmetrical permutation array randomly 
	static int[] makeSymmetricalRandomAry(int startNum, int endNum){
		Random rnd = new Random();
		int size = endNum - startNum;
		int[] randomAry = makeIntAry(startNum, endNum);
		int[] temp = makeIntAry(startNum, endNum);
		int[] onesideP = makeRandomAry(0, size/2);
		// permute both sides of numbers in the same order,
		// which keeps its symmetry
		for (int i = 0; i < size/2; i++){
			randomAry[i] = temp[onesideP[i]];
			randomAry[size-i-1] = temp[size-onesideP[i]-1];
		}
		// swap one element and its 'opposite' element randomly
		for (int i = 0; i < size; i++){
			int index = rnd.nextInt(size);
			swap(randomAry, index, size-index-1);
		}
		return randomAry;
	}

	// reverse the order of an array
	static void reverseAry(int[] ary){
		int size = ary.length;
		int half_size = ary.length / 2;
		for (int i = 0; i < half_size; i++)
			swap(ary, i, size-i-1);
	}

	// reverse numbers in a permutation array which starts from 0
	static void reverseNums(int[] permutation){
		int size = permutation.length;
		for (int i = 0; i < size; i++)
			permutation[i] = size - permutation[i] - 1;
	}

	// reverse numbers and order of a permutation array which starts from 0
	static void reverseNumAndOrder(int[] permutation){
		reverseAry(permutation);
		reverseNums(permutation);
	}

}

