package com.norana.numberplace.sudoku;

import java.util.*;
import java.util.stream.Collectors;

public class SudokuManipulator{ 
	/* Following functions are fundamental trasformation of sudoku.
	 * We regard two sudoku as "essentially same" if one can be transformed
	 * into another by some sequence of these transformation.
	 */

	// Permute numbers (except 0) in sudoku
	public static void permuteNumbers(Sudoku s, int[] permutation){
		int size = s.getSize();
		if (permutation.length != size*size){
			System.out.println("invalid array parameter");
			return;
		}
		List<Integer> permutationList = Arrays.stream(permutation)
			.mapToObj(i -> (Integer) i)
			.collect(Collectors.toList());
		for (int n = 1; n <= size*size; n++){
			if (!permutationList.contains(n)){
				System.out.println("invalid array paremeter");
				return;
			}
		}
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				int n = s.getElement(i,j);
				if (n != 0){
					s.setElement(i,j,permutation[n-1]);
				}
			}
		}
	}

	// Reflect sudoku
	private final static int VERTICAL = 0;
	private final static int HORIZONTAL = 1;
	private final static int DIAGONAL = 2;
	public static void reflectSudoku(Sudoku s, int axis){
		int size = s.getSize();
		int[] p = new int[size];
		Arrays.setAll(p, i -> p.length-i-1);
		switch (axis){
			case VERTICAL:
				permuteBlockCols(s, p);
				for (int bCol = 0; bCol < size; bCol++)
					permuteOneBlockCol(s, bCol, p);
				break;
			case HORIZONTAL:
				permuteBlockRows(s, p);
				for (int bRow = 0; bRow < size; bRow++)
					permuteOneBlockRow(s, bRow, p);
				break;
			case DIAGONAL:
				Sudoku temp = new Sudoku(size);
				for (int i = 0; i < size*size; i++)
					for (int j = 0; j < size*size; j++)
						temp.setElement(i,j,
							s.getElement(j,i));
				for (int i = 0; i < size*size; i++)
					for (int j = 0; j < size*size; j++)
						s.setElement(i,j,
							temp.getElement(i,j));
				break;
		}
	}

	// Rotate sudoku (left rotation)
	public static void rotateSudoku(Sudoku s, int numRotation){
		int sq_size = s.getSize()*s.getSize();
		for (int c = 0; c < numRotation; c++){
			// rotate outside squares and then inside squares
			for (int i = 0; i < sq_size/2; i++){
				for (int j = i; j < sq_size-1-i; j++){
					int tempNumber = s.getElement(i,j);
					s.setElement(i,j,
						s.getElement(j,sq_size-1-i));
					s.setElement(j,sq_size-1-i, 
						s.getElement(sq_size-1-i,
							     sq_size-1-j));
					s.setElement(sq_size-1-i,sq_size-1-j,
						s.getElement(sq_size-1-j,i));
					s.setElement(sq_size-1-j,i,tempNumber);
				}
			}
		}
	}

	// Permute block-rows in sudoku
	public static void permuteBlockRows(Sudoku s, int[] permutation){
		int size = s.getSize();
		if (permutation.length != size){
			System.out.println("invalid array parameter");
			return;
		}
		List<Integer> permutationList = Arrays.stream(permutation)
			.mapToObj(i -> (Integer) i)
			.collect(Collectors.toList());
		for (int bRow = 0; bRow < size; bRow++){
			if (!permutationList.contains((bRow))){
				System.out.println("invalid array parameter");
				return;
			}
		}
		Sudoku temp = new Sudoku(size);
		for (int bRow = 0; bRow < size; bRow++){
			int bRow2 = permutation[bRow];
			for (int bCol = 0; bCol < size; bCol++){
				temp.setBlock(bRow*size+bCol,
						s.getBlock(bRow2*size+bCol));
			}
		}
		for (int b = 0; b < size*size; b++){
			s.setBlock(b, temp.getBlock(b));
		}
	}

	// Permute block-columns in sudoku
	public static void permuteBlockCols(Sudoku s, int[] permutation){
		int size = s.getSize();
		if (permutation.length != size){
			System.out.println("invalid array parameter");
			return;
		}
		List<Integer> permutationList = Arrays.stream(permutation)
			.mapToObj(i -> (Integer) i)
			.collect(Collectors.toList());
		for (int bRow = 0; bRow < size; bRow++){
			if (!permutationList.contains((bRow))){
				System.out.println("invalid array parameter");
				return;
			}
		}
		Sudoku temp = new Sudoku(size);
		for (int bCol = 0; bCol < size; bCol++){
			int bCol2 = permutation[bCol];
			for (int bRow = 0; bRow < size; bRow++){
				temp.setBlock(bRow*size+bCol,
						s.getBlock(bRow*size+bCol2));
			}
		}
		for (int b = 0; b < size*size; b++){
			s.setBlock(b, temp.getBlock(b));
		}
	}

	// Permute rows in one block-row (bRow) in sudoku
	public static void permuteOneBlockRow(Sudoku s, 
			int bRow, int[] permutation){
		int size = s.getSize();
		if (permutation.length != size){
			System.out.println("invalid array parameter");
			return;
		}
		List<Integer> permutationList = Arrays.stream(permutation)
			.mapToObj(i -> (Integer) i)
			.collect(Collectors.toList());
		for (int i = 0; i < size; i++){
			if (!permutationList.contains((i))){
				System.out.println("invalid array parameter");
				return;
			}
		}
		Sudoku temp = new Sudoku(size);
		int startRow = bRow*size;
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size*size; j++){
				int n = s.getElement(startRow+permutation[i],j);
				temp.setElement(startRow+i, j, n);
			}
		}
		for (int i = startRow; i < startRow+size; i++){
			for (int j = 0; j < size*size; j++){
				s.setElement(i, j, temp.getElement(i,j));
			}
		}
	}

	// Permute columns in one block-column (bCol) in sudoku
	public static void permuteOneBlockCol(Sudoku s, 
			int bCol, int[] permutation){
		int size = s.getSize();
		if (permutation.length != size){
			System.out.println("invalid array parameter");
			return;
		}
		List<Integer> permutationList = Arrays.stream(permutation)
			.mapToObj(i -> (Integer) i)
			.collect(Collectors.toList());
		for (int i = 0; i < size; i++){
			if (!permutationList.contains((i))){
				System.out.println("invalid array parameter");
				return;
			}
		}
		Sudoku temp = new Sudoku(size);
		int startCol = bCol*size;
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size; j++){
				int n = s.getElement(i,startCol+permutation[j]);
				temp.setElement(i, startCol+j, n);
			}
		}
		for (int i = 0; i < size*size; i++){
			for (int j = startCol; j < startCol+size; j++){
				s.setElement(i, j, temp.getElement(i,j));
			}
		}
	}
}
