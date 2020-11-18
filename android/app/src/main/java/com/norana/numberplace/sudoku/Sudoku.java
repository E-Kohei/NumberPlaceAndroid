package com.norana.numberplace.sudoku;


import java.util.*;

public class Sudoku implements Cloneable{
	private int[][] numbers;    // numbers of sudoku
	                            // 0 means blank
	private boolean[][] fixedNumbers;  // boolean matrix that denies change of numbers
	private int size;           // size of one block. So there are 
	                            // size*size rows and size*size cols

	/**
	 * Constructor to make initialized sudoku (all numbers are 0)
	 */
	public Sudoku(int size){
		this.size = size;
		numbers = new int[size*size][size*size];
		fixedNumbers = new boolean[size*size][size*size];
		// create sudoku which is yet to be initialized and set them unfixed
		// (all numbers are zero)
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				numbers[i][j] = 0;
			}
		}
	}
	/**
	 * Constructor to make sudoku from 2d-array
	 */
	public Sudoku(int nums[][], int size){
		this.size = size;
		numbers = new int[size*size][size*size];
		fixedNumbers = new boolean[size*size][size*size];
		if (nums.length == size*size && nums[0].length == size*size){
			for (int i = 0; i < size*size; i++){
				for (int j = 0; j < size*size; j++){
					numbers[i][j] = nums[i][j];
					fixedNumbers[i][j] = true;
				}
			}
		}
		else{
			System.out.println("The size mismatch");
			for (int i = 0; i < size*size; i++){
				for (int j = 0; j < size*size; j++){
					numbers[i][j] = 0;
				}
			}
		}
	}

	/**
	 * Constructor to make sudoku from 2 kind of 2d-array
	 */
	public Sudoku(int nums[][], boolean fnums[][], int size){
		this.size = size;
		numbers = new int[size*size][size*size];
		fixedNumbers = new boolean[size*size][size*size];
		if (nums.length == size*size && nums[0].length == size*size){
			for (int i = 0; i < size*size; i++){
				for (int j = 0; j < size*size; j++){
					numbers[i][j] = nums[i][j];
					fixedNumbers[i][j] = fnums[i][j];
				}
			}
		}
		else{
			System.out.println("The size mismatch");
		}
	}

	@Override
	public String toString(){
		int numDigits = size / 10 + 1;
		String s = "";
		// delimiter between size x size squares
		String delimiter = "";
		// formatted String to add to s
		String format = "%" + numDigits + "d";
		for (int i = 0; i < (numDigits+1)*size*size+2*(size-1); i++){
			delimiter += "-";
		}
		for (int i = 0; i < size*size; i++){
			if (i != 0 && i % size == 0){
				s += delimiter;
				s += "\n";
			}
			for (int j = 0; j < size*size; j++){
				if (j != 0 && j % size == 0){
					s += "| ";
				}
				s += String.format(format, numbers[i][j])
					+ " ";
			}
			s += "\n";
		}
		return s;
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Sudoku){
			Sudoku s = (Sudoku)obj;
			for (int i = 0; i < size*size; i++){
				if (!(this.numbers[i].equals(s.numbers[i]))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	@Override
	public int hashCode(){
		return size;
	}
	@Override
	public Object clone(){
		Sudoku copy = new Sudoku(this.size);
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				copy.numbers[i][j] = this.numbers[i][j];
				copy.fixedNumbers[i][j] = this.fixedNumbers[i][j];
			}
		}
		return copy;
	}

	/**
	 * returns copy of sudoku
	 *
	 * @return copy of sudoku
	 */
	public Sudoku copy(){
		return (Sudoku) this.clone();
	}

	/**
	 * returns string of fixedNumbers
	 */
	public String fixedNumbersToString(){
		int numDigits = size / 10 + 1;
		StringBuilder builder = new StringBuilder();
		// delimiter between size x size squares
		String delimiter = "";
		for (int i = 0; i < (numDigits+1)*size*size+2*(size-1); i++){
			delimiter += "-";
		}
		for (int i = 0; i < size*size; i++){
			if (i != 0 && i % size == 0){
				builder.append(delimiter);
				builder.append("\n");
			}
			for (int j = 0; j < size*size; j++){
				if (j != 0 && j % size == 0){
					builder.append("| ");
				}
				if (fixedNumbers[i][j])
					builder.append("x ");
				else
					builder.append("o ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	/**
	 * returns size of sudoku (for example, if 9x9 sudoku size is 3)
	 *
	 * @return size of sudoku
	 */
	public int getSize(){
		return size;
	}

	/**
	 * returns ith row
	 *
	 * @return int-array of ith row
	 */
	public int[] getRow(int i){
		return Arrays.copyOf(numbers[i], size*size);
	}

	/**
	 * returns ith row as List
	 *
	 * @return Integer-List of ith row
	 */
	public List<Integer> getRowAsList(int i){
		List<Integer> row = new ArrayList<Integer>(size*size);
		for (int j = 0; j < size*size; j++){
			row.add(numbers[i][j]);
		}
		return row;
	}

	/**
	 * returns jth column
	 *
	 * @return int-array of jth column
	 */
	public int[] getCol(int j){
		int[] col = new int[size*size];
		for (int i = 0; i < size*size; i++){
			col[i] = numbers[i][j];
		}
		return col;
	}

	/**
	 * returns jth column as List
	 *
	 * @return Integer-List of jth column
	 */
	public List<Integer> getColAsList(int j){
		List<Integer> col = new ArrayList<Integer>(size*size);
		for (int i = 0; i < size*size; i++){
			col.add(numbers[i][j]);
		}
		return col;
	}

	/**
	 * returns int at numbers[i][j]
	 *
	 * @return int at (i,j) of sudoku
	 */
	public int getElement(int i, int j){
		return numbers[i][j];
	}

	/**
	 * returns int at the cell
	 */
	public int getElement(Pair<Integer, Integer> cell){
		return numbers[cell.getFirst()][cell.getSecond()];
	}

	/**
	 * sets n at numbers[i][j] regardless of fixedNumbers
	 */
	public void setElement(int i, int j, int n){
		numbers[i][j] = n;
	}

	/**
	 * sets n at the cell regardless of fixedNumbers
	 */
	public void setElement(Pair<Integer, Integer> cell, int n){
		numbers[cell.getFirst()][cell.getSecond()] = n;
	}

	/**
	 * sets n at numbers[i][j] if the cell is not fixed and return whether succeeded
	 */
	public boolean setNumber(int i, int j, int n){
		if (!fixedNumbers[i][j]) {
			numbers[i][j] = n;
			return true;
		}
		else
			return false;
	}

	/**
	 * sets n at the cell if the cell is not fixed cell and return whether succeeded
	 */
	public boolean setNumber(Pair<Integer, Integer> cell, int n){
		return setNumber(cell.getFirst(), cell.getSecond(), n);
	}

	/**
	 * returns matrix of numbers in bth block
	 *
	 * @return 2d-array of int in bth block
	 */
	public int[][] getBlock(int b){
		int[][] block = new int[size][size];
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				block[i][j] = numbers[i+size*(b/size)]
						     [j+size*(b%size)];
			}
		}
		return block;
	}

	/**
	 * returns array of numbers in bth block
	 *
	 * @return 1d-array of int in bth block
	 */
	public int[] getBlockAsArray(int b){
		int n = 0;
		int[] block = new int[size*size];
		for (int i = size*(b/size); i < size*(b/size+1); i++){
			for (int j = size*(b%size); j < size*(b%size+1); j++){
				block[n] = numbers[i][j];
				n++;
			}
		}
		return block;
	}

	/**
	 * returns List of numbers in bth block
	 *
	 * @return List of Integers in bth block
	 */
	public List<Integer> getBlockAsList(int b){
		List<Integer> block = new ArrayList<Integer>(size*size);
		for (int i = size*(b/size); i < size*(b/size+1); i++){
			for (int j = size*(b%size); j < size*(b%size+1); j++){
				block.add(numbers[i][j]);
			}
		}
		return block;
	}

	/**
	 * sets matrix of numbers in bth block
	 */
	public void setBlock(int b, int block[][]){
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				numbers[i+size*(b/size)][j+size*(b%size)] =
					block[i][j];
			}
		}
	}

	/**
	 * fix the cell (i, j)
	 */
	public void fixCell(int i, int j, boolean b){
		fixedNumbers[i][j] = b;
	}

	/**
	 * fix the cell
	 */
	public void fixCell(Pair<Integer, Integer> cell, boolean b){
		fixedNumbers[cell.getFirst()][cell.getSecond()] = b;
	}

	/**
	 * return if the cell (i, j) is fixed
	 */
	public boolean isFixedCell(int i, int j){
		return fixedNumbers[i][j];
	}

	/**
	 * return if the cell is fixed
	 */
	public boolean isFixedCell(Pair<Integer, Integer> cell){
		return fixedNumbers[cell.getFirst()][cell.getSecond()];
	}

	/**
	 * fix cells where some number is already set
	 */
	public void fixNumbers(){
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				if (numbers[i][j] != 0)
					fixedNumbers[i][j] = true;
			}
		}
	}

	/**
	 * reset fixedNumbers to allow any change
	 */
	public void resetFixedNumbers(){
		for (int i = 0; i < size*size; i++)
			for (int j = 0; j < size*size; j++)
				fixedNumbers[i][j] = false;
	}

	/**
	 * reset sudoku, that is set all unfixed numbers 0
	 */
	public void resetSudoku(){
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				if (!fixedNumbers[i][j])
					numbers[i][j] = 0;
			}
		}
	}

	/**
	 * checks if the sudoku is already solved
	 *
	 * @return true if the sudoku is already solved
	 */
	public boolean isSolved(){
		// if any of row, col, and block contains 1,2,...,size*size,
		// then the sudoku is solved
		List<Integer> correctArray = new ArrayList<Integer>(size*size);
		for (int i = 0; i < size*size; i++){
			correctArray.add(i+1);
		}
		for (int n = 0; n < size*size; n++){
			if (!(this.getRowAsList(n).containsAll(correctArray)))
				return false;
			if (!(this.getColAsList(n).containsAll(correctArray)))
				return false;
			if (!(this.getBlockAsList(n).containsAll(correctArray)))
				return false;
		}
		return true;
	}
}
