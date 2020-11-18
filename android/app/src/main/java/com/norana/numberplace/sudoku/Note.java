package com.norana.numberplace.sudoku;

import java.util.*;

public class Note implements Cloneable{
	private int[][] notes;    // notes of note (represented as 9 bis)
							  // 0 means blank
	private int size;           // size of one block. So there are 
	                            // size*size rows and size*size cols

	/**
	 * Constructor to make initialized note (all notes are 0)
	 */
	public Note(int size){
		this.size = size;
		notes = new int[size*size][size*size];
		// create note which is yet to be initialized
		// (all notes are zero)
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				notes[i][j] = 0;
			}
		}
	}
	/**
	 * Constructor to make note from 2d-array
	 */
	public Note(int nums[][], int size){
		this.size = size;
		notes = new int[size*size][size*size];
		if (nums.length == size*size && nums[0].length == size*size){
			for (int i = 0; i < size*size; i++){
				for (int j = 0; j < size*size; j++){
					notes[i][j] = nums[i][j];
				}
			}
		}
		else{
			System.out.println("The size mismatch");
			for (int i = 0; i < size*size; i++){
				for (int j = 0; j < size*size; j++){
					notes[i][j] = 0;
				}
			}
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
				s += String.format(format, notes[i][j])
					+ " ";
			}
			s += "\n";
		}
		return s;
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Note){
			Note s = (Note) obj;
			for (int i = 0; i < size*size; i++){
				if (!(this.notes[i].equals(s.notes[i]))){
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
		Note copy = new Note(this.size);
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				copy.notes[i][j] = this.notes[i][j];
			}
		}
		return copy;
	}

	/**
	 * returns copy of note
	 *
	 * @return copy of note
	 */
	public Note copy(){
		return (Note) this.clone();
	}

	/**
	 * returns size of note (for example, if 9x9 note size is 3)
	 *
	 * @return size of note
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
		return Arrays.copyOf(notes[i], size*size);
	}

	/**
	 * returns ith row as List
	 *
	 * @return Integer-List of ith row
	 */
	public List<Integer> getRowAsList(int i){
		List<Integer> row = new ArrayList<Integer>(size*size);
		for (int j = 0; j < size*size; j++){
			row.add(notes[i][j]);
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
			col[i] = notes[i][j];
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
			col.add(notes[i][j]);
		}
		return col;
	}

	/**
	 * returns int at notes[i][j]
	 *
	 * @return int at (i,j) of note
	 */
	public int getNoteNumber(int i, int j){
		return notes[i][j];
	}

	/**
	 * returns int at the cell
	 *
	 * @return int at cell of note
	 */
	public int getNoteNumber(Pair<Integer, Integer> cell){
		return notes[cell.getFirst()][cell.getSecond()];
	}

	/**
	 * sets n at notes[i][j]
	 */
	public void setNoteNumber(int i, int j, int n){
		notes[i][j] = n;
	}

	/**
	 * sets n at the cell
	 */
	public void setNoteNumber(Pair<Integer, Integer> cell, int n){
		notes[cell.getFirst()][cell.getSecond()] = n;
	}

	/**
	 * returns note array at notes[i][j]
	 *
	 * @return int array that represents note at (i,j)
	 */
	public int[] getNoteArray(int i, int j){
		int[] noteIndicators = new int[size*size];
		int noteNumber = getNoteNumber(i, j);
		for (int k = 0; k < size*size; k++)
			noteIndicators[k] = (noteNumber >> k & 1);
		return noteIndicators;
	}

	/**
	 * returns note array at cell
	 *
	 * @return int array that represents note at the cell
	 */
	public int[] getNoteArray(Pair<Integer, Integer> cell){
		int[] noteIndicators = new int[size*size];
		int noteNumber = getNoteNumber(cell);
		for (int k = 0; k < size*size; k++)
			noteIndicators[k] = (noteNumber >> k & 1);
		return noteIndicators;
	}

	/**
	 * sets note array at notes[i][j]
	 */
	public void setNoteArray(int i, int j, int[] noteIndicators){
		int noteNumber = 0;
		for (int k = 0; k < size*size; k++)
			noteNumber += (noteIndicators[k] << k);
		setNoteNumber(i, j, noteNumber);
	}

	/**
	 * sets note array at cell
	 */
	public void setNoteArray(Pair<Integer, Integer> cell, int[] noteIndicators){
		int noteNumber = 0;
		for (int k = 0; k < size*size; k++)
			noteNumber += (noteIndicators[k] << k);
		setNoteNumber(cell, noteNumber);
	}

	/**
	 * toggle note number m at (i,j)
	 */
	public void toggleNoteNumber(int i, int j, int m){
		if (m >= 1 && m <= 9){
			// add or remove note by toggling indicator
			int toggled = getNoteNumber(i, j) ^ (1<<(m-1));
			setNoteNumber(i, j, toggled);
		}
		else if (m == 0){
			// reset the note
			setNoteNumber(i, j, 0);
		}
	}

	/**
	 * toggle note number m at the cell
	 */
	public void toggleNoteNumber(Pair<Integer, Integer> cell, int m){
		toggleNoteNumber(cell.getFirst(), cell.getSecond(), m);
	}

	/**
	 * returns matrix of notes in bth block
	 *
	 * @return 2d-array of int in bth block
	 */
	public int[][] getBlock(int b){
		int[][] block = new int[size][size];
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				block[i][j] = notes[i+size*(b/size)]
						     [j+size*(b%size)];
			}
		}
		return block;
	}

	/**
	 * returns array of notes in bth block
	 *
	 * @return 1d-array of int in bth block
	 */
	public int[] getBlockAsArray(int b){
		int n = 0;
		int[] block = new int[size*size];
		for (int i = size*(b/size); i < size*(b/size+1); i++){
			for (int j = size*(b%size); j < size*(b%size+1); j++){
				block[n] = notes[i][j];
				n++;
			}
		}
		return block;
	}

	/**
	 * returns List of notes in bth block
	 *
	 * @return List of Integers in bth block
	 */
	public List<Integer> getBlockAsList(int b){
		List<Integer> block = new ArrayList<Integer>(size*size);
		for (int i = size*(b/size); i < size*(b/size+1); i++){
			for (int j = size*(b%size); j < size*(b%size+1); j++){
				block.add(notes[i][j]);
			}
		}
		return block;
	}

	/**
	 * sets matrix of notes in bth block
	 */
	public void setBlock(int b, int block[][]){
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				notes[i+size*(b/size)][j+size*(b%size)] =
					block[i][j];
			}
		}
	}
}
