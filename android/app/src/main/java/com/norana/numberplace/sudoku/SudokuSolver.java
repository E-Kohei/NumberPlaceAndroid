package com.norana.numberplace.sudoku;

import java.util.*;
import java.util.stream.Collectors;

public class SudokuSolver{

	/*               Overview of solveSudoku
	 *  solveSudoku solves sudoku literally, by updating "NumCandidates"
	 * which has information about which number should be located on 
	 * which square. The algorithm is as follows:
	 *
	 * Step 1. --firstUpdataNumCandidates--
	 *         Initialize NumCandidates from the unchanged sudoku.
	 *
	 * Step 2. --updateNumCandidates--
	 *         For location (i,j), if a number n is in the row i, in the
	 *         column j, or in the block b, n can't be located at (i,j).
	 *         Considering this condition, update NumCandidates from the 
	 *         current sudoku.
	 *         (In the first loop, this is verbose step)
	 *         
	 *
	 * Step 3. --checkCandidateNums--
	 *         For each location, check if the number of candidate-
	 *         numbers is one. If so, n is determined.
	 *
	 * Step 4. --findSameCandidateNumsInBlock etc.--
	 *         Find locations which have same candidate-numbers in
	 *         common in a block, row and column. If the number of
	 *         such locations is equal to the number of candidate-
	 *         numbers, the number n-s can't be located in the other
	 *         locations in the block, row or column.
	 *
	 * Step 5-1. --checkCandidateLocs--
	 *           Make Map of candidate-locations from NumCandidates,
	 *           and check its uniqueness for each number. 
	 *
	 * Step 5-3. --findHiddenBlock etc.--
	 *           Using the Map mentioned above, find a block, row,
	 *           or column only in which candidate-locations for n
	 *           are. If found, n can't be located in the block, row
	 *           or column except the candidate-locations.
	 *
	 * Step 5-2. --findSameCandidateLocs--
	 *           Using the Map mentioned above, find numbers which
	 *           have same candidate-locations in common in a block,
	 *           row and column. If the number of such numbers is 
	 *           equal to the number of candidate-locations, the
	 *           number n-s can't be located in the other locations
	 *           in the block, row or column.
	 *           (This is reverse approach of Step 4)
	 * 
	 * Step 6. --findSameRowsInBlockRow etc.--
	 *         In a block-row, find rows over blocks which have
	 *         candidate-locations of n. If the number of rows is equal
	 *         to the number of blocks where the rows are candidate of 
	 *         n, the number cannot be located in the rows in the other
	 *         blocks.
	 *
	 * Step 7. --XWing--
	 *         Check if some rows have same candidate-locations for n.
	 *         If the number of such rows are equal to the number of
	 *         candidate-locations for n, n can't be located in the 
	 *         other rows.
	 *
	 * Step 8. If sudoku is solved, return the solved sudoku.
	 *         If sudoku is unsolved and NumCandidates is updating,
	 *         repeat the loop of Step 2 ~ Step 7.
	 *         If sudoku is unsolved and NumCandidates is not updated
	 *         anymore, return original sudoku and exit, since this
	 *         means this program cannot solve the sudoku anymore.
	 */

	/**
	 * solve sudoku which is solvable without any trials
	 *
	 * @param sudoku unsolved sudoku
	 * @return solved sudoku
	 */
	public static Sudoku solveSudoku(Sudoku sudoku){
		int size = sudoku.getSize();
		Sudoku solved_s = sudoku.copy();
		
		// An Array which stores indicators of candidate-numbers of 
		// each location. For example, if n is candidate-number at 
		// location (i,j), NumCandidates[i][j][n] = n  else -1
		int[][][] NumCandidates = 
			new int[size*size][size*size][size*size+1];

		firstUpdateNumCandidates(solved_s, NumCandidates);

		while (true){
			int[][][] prevNumCandidates = copyNumCandidates(NumCandidates);

			updateNumCandidates(solved_s, NumCandidates);

			checkCandidateNums(solved_s, NumCandidates);

			findSameCandidateNums(NumCandidates);

			for (int b = 0; b < size*size; b++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInB = makeCandidateLocsInB(NumCandidates, b);
				checkCandidateLocs(solved_s, CandidateLocsInB, NumCandidates);
				findHiddenRowAndCol(CandidateLocsInB, NumCandidates);
				findSameCandidateLocs(CandidateLocsInB, NumCandidates);
			}
			for (int row = 0; row < size*size; row++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInRow = makeCandidateLocsInRow(NumCandidates, row);
				checkCandidateLocs(solved_s, CandidateLocsInRow, NumCandidates);
				findHiddenBlock(CandidateLocsInRow, NumCandidates);
				findSameCandidateLocs(CandidateLocsInRow, NumCandidates);
			}
			for (int col = 0; col < size*size; col++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInCol = makeCandidateLocsInCol(NumCandidates, col);
				checkCandidateLocs(solved_s, CandidateLocsInCol, NumCandidates);
				findHiddenBlock(CandidateLocsInCol, NumCandidates);
				findSameCandidateLocs(CandidateLocsInCol, NumCandidates);
			}

			findSameRowsInBlockRow(NumCandidates);
			findSameColsInBlockCol(NumCandidates);

			XWing_Row(NumCandidates);
			XWing_Col(NumCandidates);

			if (solved_s.isSolved()){
				return solved_s;
			}
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates)){
				System.out.println("I can't solve");
				return solved_s;
			}
		}
	}

	/**
	 * solve any sudoku with trials if needed
	 *
	 * @param sudoku unsolved sudoku
	 * @return solved sudoku
	 */
	public static Sudoku solveSudokuWithTrials(Sudoku sudoku){
		Pair<Integer, Sudoku> solvedPair = solveSudokuWithTrials_helper(sudoku);
		if (solvedPair.getFirst() == 0)
			return solvedPair.getSecond();
		else if (solvedPair.getFirst() == 1){
			System.out.println("this sudoku is unsolvable");
			return sudoku;
		}
		else if (solvedPair.getFirst() == 2){
			System.out.println("this sudoku has several solutions");
			return sudoku;
		}
		else{
			System.out.println("Error occured");
			return sudoku;
		}
	}

	// This algorithm continues solving sudoku even if above algorithm
	// can't solve sudoku anymore by assuming a number locates at some 
	// location.
	public static Pair<Integer, Sudoku> solveSudokuWithTrials_helper(Sudoku s){

		// firstly check if this sudoku has more numbers than "gold number" 17
		if (!hasNumbersMoreThan17(s))
			return new Pair<Integer, Sudoku>(1, null);

		int size = s.getSize();
		Sudoku solved_s = s.copy();
		
		// An Array which stores indicators of candidate-numbers of 
		// each location. For example, if n is candidate-number at 
		// location (i,j), NumCandidates[i][j][n] = n  else -1
		int[][][] NumCandidates = 
			new int[size*size][size*size][size*size+1];


		firstUpdateNumCandidates(solved_s, NumCandidates);

		while (true){
			int[][][] prevNumCandidates = copyNumCandidates(NumCandidates);

			updateNumCandidates(solved_s, NumCandidates);

			
			checkCandidateNums(solved_s, NumCandidates);

			findSameCandidateNums(NumCandidates);

			for (int b = 0; b < size*size; b++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInB = makeCandidateLocsInB(NumCandidates, b);
				checkCandidateLocs(solved_s, CandidateLocsInB, NumCandidates);
				findHiddenRowAndCol(CandidateLocsInB, NumCandidates);
				findSameCandidateLocs(CandidateLocsInB, NumCandidates);
			}
			for (int row = 0; row < size*size; row++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInRow = makeCandidateLocsInRow(NumCandidates, row);
				checkCandidateLocs(solved_s, CandidateLocsInRow, NumCandidates);
				findHiddenBlock(CandidateLocsInRow, NumCandidates);
				findSameCandidateLocs(CandidateLocsInRow, NumCandidates);
			}
			for (int col = 0; col < size*size; col++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInCol = makeCandidateLocsInCol(NumCandidates, col);
				checkCandidateLocs(solved_s, CandidateLocsInCol, NumCandidates);
				findHiddenBlock(CandidateLocsInCol, NumCandidates);
				findSameCandidateLocs(CandidateLocsInCol, NumCandidates);
			}

			findSameRowsInBlockRow(NumCandidates);
			findSameColsInBlockCol(NumCandidates);

			XWing_Row(NumCandidates);
			XWing_Col(NumCandidates);

			// the sudoku is solvable
			if (solved_s.isSolved()){
				return new Pair<Integer, Sudoku>(0, solved_s);
			}
			// the sudoku has contradiction (there is a cell where
			// any number cannot be locate) , so unsolvable
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates) && hasContradiction(solved_s, NumCandidates)){
				// the sudoku has contradiction (there is a cell where
				// any number cannot be locate)
				return new Pair<Integer, Sudoku>(1, null);
			}
			// the sudoku cannot be solved anymore by current hints:
			// need some assumption
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates)){
				List<Pair<Integer, Sudoku>> solvedSudokus = new ArrayList<Pair<Integer, Sudoku>>();
out:				for (int k = 2; k < size*size; k++){
					for (int i = 0; i < size*size; i++){
						for (int j = 0; j < size*size; j++){
							int[] CandidatesAtij = Arrays.stream(NumCandidates[i][j])
								.filter(n -> n>0)
								.toArray();
							if (CandidatesAtij.length == k){
								for (int l = 0; l < k; l++){
									Sudoku assumedSudoku = solved_s.copy();
									assumedSudoku.setElement(i, j, CandidatesAtij[l]);
									Pair<Integer, Sudoku> solvedSudoku = solveSudokuWithTrials_helper(assumedSudoku);
									if (solvedSudoku.getFirst() == 2)
										return new Pair<Integer, Sudoku>(2, null);
									solvedSudokus.add(solvedSudoku);
								}
								break out;
							}
						}
					}
				}
				int numSolvable = (int) solvedSudokus.stream()
					.filter(p -> p.getFirst() == 0).count();
				int numUnsolvable = (int) solvedSudokus.stream()
					.filter(p -> p.getFirst() == 1).count();
				int numHasSeveralSolution = (int) solvedSudokus.stream()
					.filter(p -> p.getFirst() == 2).count();
				int numException = (int) solvedSudokus.stream()
					.filter(p -> p.getFirst() == 100).count();
				if (numUnsolvable > 0 && numUnsolvable == solvedSudokus.size()){
					// not solvable
					return new Pair<Integer, Sudoku>(1, null);
				}
				else if (numSolvable >= 2 || numHasSeveralSolution >= 1){
					// has several solutions
					return new Pair<Integer, Sudoku>(2, null);
				}
				else if (numSolvable == 1){
					// solvable
					Sudoku newSolvedSudoku = solvedSudokus.stream()
						.filter(p -> p.getFirst() == 0)
						.collect(Collectors.toList())
						.get(0).getSecond();
					return new Pair<Integer, Sudoku>(0, newSolvedSudoku);
				}
				else // something wrong
					return new Pair<Integer, Sudoku>(100, null);
			}
			// if NumCandidates is updating, continue the loop
		}
	}

	/* Check if the sudoku is solvable by actually solving the sudoku
	 * with solveSudoku. If so, return 0. And if not, it means the 
	 * sudoku contains contradiction or the sudoku have several answers.
	 * In former case, return 1 and in latter case, return 2.
	 */
	/**
	 * Check if the sudoku is solvable
	 * @param sudoku sudoku to be checked
	 * @return 0 if solvable
	 *         1 if unsolvable
	 *         2 if it has several solutions
	 */
	public static int isSolvable(Sudoku sudoku){

		// firstly check if this sudoku has more numbers than "gold number" 17
		if (!hasNumbersMoreThan17(sudoku))
			return 1;

		int size = sudoku.getSize();
		Sudoku solved_s = sudoku.copy();
		
		// An Array which stores indicators of candidate-numbers of 
		// each location. For example, if n is candidate-number at 
		// location (i,j), NumCandidates[i][j][n] = n  else -1
		int[][][] NumCandidates = 
			new int[size*size][size*size][size*size+1];

		firstUpdateNumCandidates(solved_s, NumCandidates);

		while (true){
			int[][][] prevNumCandidates = copyNumCandidates(NumCandidates);

			updateNumCandidates(solved_s, NumCandidates);

			
			checkCandidateNums(solved_s, NumCandidates);

			findSameCandidateNums(NumCandidates);

			for (int b = 0; b < size*size; b++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInB = makeCandidateLocsInB(NumCandidates, b);
				checkCandidateLocs(solved_s, CandidateLocsInB, NumCandidates);
				findHiddenRowAndCol(CandidateLocsInB, NumCandidates);
				findSameCandidateLocs(CandidateLocsInB, NumCandidates);
			}
			for (int row = 0; row < size*size; row++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInRow = makeCandidateLocsInRow(NumCandidates, row);
				checkCandidateLocs(solved_s, CandidateLocsInRow, NumCandidates);
				findHiddenBlock(CandidateLocsInRow, NumCandidates);
				findSameCandidateLocs(CandidateLocsInRow, NumCandidates);
			}
			for (int col = 0; col < size*size; col++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInCol = makeCandidateLocsInCol(NumCandidates, col);
				checkCandidateLocs(solved_s, CandidateLocsInCol, NumCandidates);
				findHiddenBlock(CandidateLocsInCol, NumCandidates);
				findSameCandidateLocs(CandidateLocsInCol, NumCandidates);
			}

			findSameRowsInBlockRow(NumCandidates);
			findSameColsInBlockCol(NumCandidates);

			XWing_Row(NumCandidates);
			XWing_Col(NumCandidates);

			// the sudoku is solvable
			if (solved_s.isSolved()){
				return 0;
			}
			// the sudoku has contradiction (there is a cell where
			// any number cannot be locate) , so unsolvable
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates) && hasContradiction(solved_s, NumCandidates)){
				// the sudoku has contradiction (there is a cell where
				// any number cannot be locate)
				return 1;
			}
			// the sudoku cannot be solved anymore by current hints:
			// need some assumption
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates)){
				List<Integer> isSolvableIndicators = new ArrayList<Integer>();
out:				for (int k = 2; k < size*size; k++){
					for (int i = 0; i < size*size; i++){
						for (int j = 0; j < size*size; j++){
							int[] CandidatesAtij = Arrays.stream(NumCandidates[i][j])
								.filter(n -> n>0)
								.toArray();
							if (CandidatesAtij.length == k){
								for (int l = 0; l < k; l++){
									Sudoku assumedSudoku = solved_s.copy();
									assumedSudoku.setElement(i, j, CandidatesAtij[l]);
									int is_solvable = isSolvable(assumedSudoku);
									if (is_solvable == 2)
										return 2;
									isSolvableIndicators.add(is_solvable);
								}
								break out;
							}
						}
					}
				}
				int numSolvable = (int) isSolvableIndicators.stream()
					.filter(n -> n==0).count();
				int numUnsolvable = (int) isSolvableIndicators.stream()
					.filter(n -> n==1).count();
				int numHasSeveralSolution = (int) isSolvableIndicators.stream()
					.filter(n -> n==2).count();
				int numException = (int) isSolvableIndicators.stream()
					.filter(n -> n==100).count();
				if (numUnsolvable > 0 && numUnsolvable == isSolvableIndicators.size())
					return 1;   // not solvable
				else if (numSolvable >= 2 || numHasSeveralSolution >= 1)
					return 2;   // has several solutions
				else if (numSolvable == 1)
					return 0;   // solvable
				else
					return 100; // somethins wrong
			}
			// if NumCandidates is updating, continue the loop
		}
	}

	// Check if the sudoku easier than level 1
	public static boolean isSolvableLevel1(Sudoku sudoku){
		int size = sudoku.getSize();
		Sudoku solved_s = sudoku.copy();
		
		int[][][] NumCandidates = 
			new int[size*size][size*size][size*size+1];

		firstUpdateNumCandidates(solved_s, NumCandidates);

		while (true){
			int[][][] prevNumCandidates = copyNumCandidates(NumCandidates);

			updateNumCandidates(solved_s, NumCandidates);

			checkCandidateNums(solved_s, NumCandidates);

			findSameCandidateNums(NumCandidates);

			for (int b = 0; b < size*size; b++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInB = makeCandidateLocsInB(NumCandidates, b);
				checkCandidateLocs(solved_s, CandidateLocsInB, NumCandidates);
			}
			for (int row = 0; row < size*size; row++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInRow = makeCandidateLocsInRow(NumCandidates, row);
				checkCandidateLocs(solved_s, CandidateLocsInRow, NumCandidates);
			}
			for (int col = 0; col < size*size; col++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInCol = makeCandidateLocsInCol(NumCandidates, col);
				checkCandidateLocs(solved_s, CandidateLocsInCol, NumCandidates);
			}

			if (solved_s.isSolved()){
				return true;
			}
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates)){
				return false;
			}
		}
	}

	// Check if the sudoku easier than level 2
	public static boolean isSolvableLevel2(Sudoku sudoku){
		int size = sudoku.getSize();
		Sudoku solved_s = sudoku.copy();
		
		int[][][] NumCandidates = 
			new int[size*size][size*size][size*size+1];

		firstUpdateNumCandidates(solved_s, NumCandidates);

		while (true){
			int[][][] prevNumCandidates = copyNumCandidates(NumCandidates);

			updateNumCandidates(solved_s, NumCandidates);

			checkCandidateNums(solved_s, NumCandidates);

			findSameCandidateNums(NumCandidates);

			for (int b = 0; b < size*size; b++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInB = makeCandidateLocsInB(NumCandidates, b);
				checkCandidateLocs(solved_s, CandidateLocsInB, NumCandidates);
				findHiddenRowAndCol(CandidateLocsInB, NumCandidates);
			}
			for (int row = 0; row < size*size; row++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInRow = makeCandidateLocsInRow(NumCandidates, row);
				checkCandidateLocs(solved_s, CandidateLocsInRow, NumCandidates);
				findHiddenBlock(CandidateLocsInRow, NumCandidates);
			}
			for (int col = 0; col < size*size; col++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInCol = makeCandidateLocsInCol(NumCandidates, col);
				checkCandidateLocs(solved_s, CandidateLocsInCol, NumCandidates);
				findHiddenBlock(CandidateLocsInCol, NumCandidates);
			}

			if (solved_s.isSolved()){
				return true;
			}
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates)){
				return false;
			}
		}
	}

	// Check if the sudoku easier than level 3
	public static boolean isSolvableLevel3(Sudoku sudoku){
		int size = sudoku.getSize();
		Sudoku solved_s = sudoku.copy();
		
		int[][][] NumCandidates = 
			new int[size*size][size*size][size*size+1];

		firstUpdateNumCandidates(solved_s, NumCandidates);

		while (true){
			int[][][] prevNumCandidates = copyNumCandidates(NumCandidates);

			updateNumCandidates(solved_s, NumCandidates);

			checkCandidateNums(solved_s, NumCandidates);

			findSameCandidateNums(NumCandidates);

			for (int b = 0; b < size*size; b++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInB = makeCandidateLocsInB(NumCandidates, b);
				checkCandidateLocs(solved_s, CandidateLocsInB, NumCandidates);
				findHiddenRowAndCol(CandidateLocsInB, NumCandidates);
				findSameCandidateLocs(CandidateLocsInB, NumCandidates);
			}
			for (int row = 0; row < size*size; row++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInRow = makeCandidateLocsInRow(NumCandidates, row);
				checkCandidateLocs(solved_s, CandidateLocsInRow, NumCandidates);
				findHiddenBlock(CandidateLocsInRow, NumCandidates);
				findSameCandidateLocs(CandidateLocsInRow, NumCandidates);
			}
			for (int col = 0; col < size*size; col++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInCol = makeCandidateLocsInCol(NumCandidates, col);
				checkCandidateLocs(solved_s, CandidateLocsInCol, NumCandidates);
				findHiddenBlock(CandidateLocsInCol, NumCandidates);
				findSameCandidateLocs(CandidateLocsInCol, NumCandidates);
			}

			if (solved_s.isSolved()){
				return true;
			}
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates)){
				return false;
			}
		}
	}

	// Check if the sudoku easier than level 4
	public static boolean isSolvableLevel4(Sudoku sudoku){
		int size = sudoku.getSize();
		Sudoku solved_s = sudoku.copy();
		
		// An Array which stores indicators of candidate-numbers of 
		// each location. For example, if n is candidate-number at 
		// location (i,j), NumCandidates[i][j][n] = n  else -1
		int[][][] NumCandidates = 
			new int[size*size][size*size][size*size+1];

		firstUpdateNumCandidates(solved_s, NumCandidates);

		while (true){
			int[][][] prevNumCandidates = copyNumCandidates(NumCandidates);

			updateNumCandidates(solved_s, NumCandidates);

			checkCandidateNums(solved_s, NumCandidates);

			findSameCandidateNums(NumCandidates);

			for (int b = 0; b < size*size; b++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInB = makeCandidateLocsInB(NumCandidates, b);
				checkCandidateLocs(solved_s, CandidateLocsInB, NumCandidates);
				findHiddenRowAndCol(CandidateLocsInB, NumCandidates);
				findSameCandidateLocs(CandidateLocsInB, NumCandidates);
			}
			for (int row = 0; row < size*size; row++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInRow = makeCandidateLocsInRow(NumCandidates, row);
				checkCandidateLocs(solved_s, CandidateLocsInRow, NumCandidates);
				findHiddenBlock(CandidateLocsInRow, NumCandidates);
				findSameCandidateLocs(CandidateLocsInRow, NumCandidates);
			}
			for (int col = 0; col < size*size; col++){
				Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInCol = makeCandidateLocsInCol(NumCandidates, col);
				checkCandidateLocs(solved_s, CandidateLocsInCol, NumCandidates);
				findHiddenBlock(CandidateLocsInCol, NumCandidates);
				findSameCandidateLocs(CandidateLocsInCol, NumCandidates);
			}

			findSameRowsInBlockRow(NumCandidates);
			findSameColsInBlockCol(NumCandidates);
			
			if (solved_s.isSolved()){
				return true;
			}
			else if (isSameNumCandidates(prevNumCandidates, NumCandidates)){
				return false;
			}
		}
	}


	/*------- following methods are helper methods for solveSudoku --------*/

	// Module method composed of findSameCandidateNumsIn Block, Row, and Col
	static void findSameCandidateNums(int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
		for (int b = 0; b < size*size; b++)
			findSameCandidateNumsInBlock(NumCandidates, b);
		for (int row = 0; row < size*size; row++)
			findSameCandidateNumsInRow(NumCandidates, row);
		for (int col = 0; col < size*size; col++)
			findSameCandidateNumsInCol(NumCandidates, col);
	}

	// Module method 

	// Initialize and update NumCandidates from the unchanged sudoku
	static void firstUpdateNumCandidates(Sudoku s, int[][][] NumCandidates){
		int size = s.getSize();
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				NumCandidates[i][j][0] = -1;
				// if (i,j) is already fixed, skip following step
				if (s.getElement(i,j) != 0){
					Arrays.fill(NumCandidates[i][j],-1);
					continue;
				}
				for (int n = 1; n <= size*size; n++){
					int b = getBlockByLoc(i,j,size);
					// check list to check if a number n can be
					// located in the location (i,j)
					Set<Integer> checkList = new HashSet<Integer>();
					checkList.addAll(s.getBlockAsList(b));
					checkList.addAll(s.getRowAsList(i));
					checkList.addAll(s.getColAsList(j));
					// if checkList doesn't contains n,
					// n is candidate-number at (i,j) 
					if (!checkList.contains(n)){
						NumCandidates[i][j][n] = n;
					}

					// if checkList contains n, the number n
					// cannot be candidate-number anymore
					if (checkList.contains(n) || s.getElement(i,j) != 0){
						NumCandidates[i][j][n] = -1;
					}
				}
			}
		}
	}

	// Copy NumCandidates
	static int[][][] copyNumCandidates(int[][][] NumCandidates){
		int sq_size = NumCandidates.length;
		int[][][] copy = new int[sq_size][sq_size][sq_size+1];
		for (int i = 0; i < sq_size; i++){
			for (int j = 0; j < sq_size; j++){
				for (int n = 0; n <= sq_size; n++){
					copy[i][j][n] = NumCandidates[i][j][n];
				}
			}
		}
		return copy;
	}

	// Check if two NumCandidates are same
	static boolean isSameNumCandidates(int[][][] NumCandidates1, int[][][] NumCandidates2){
		int sq_size = NumCandidates1.length;
		if (NumCandidates1.length != NumCandidates2.length) { return false; }
		for (int i = 0; i < sq_size; i++){
			for (int j = 0; j < sq_size; j++){
				if (!Arrays.equals(NumCandidates1[i][j], NumCandidates2[i][j]))
					return false;
			}
		}
		return true;
	}

	// Update data for NumCandidates
	// For each location (i,j), check whether each number can be a candidate
	// in the location. To check this, firstly check if (i,j) is empty,
	// and then, check if the number is not involved in "checkList", which
	// contains all numbers in the ith row, the jth column, the block which 
	// has (i,j), and hiddenNumbers of (i,j).If so, it means the location is
	// a candidate-location of the number n. (In other words, the number n 
	// is a candidate-number of the location (i,j).)
	static void updateNumCandidates(Sudoku s, int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				// if (i,j) is already fixed, skip following step
				if (s.getElement(i,j) != 0)
					continue;
				for (int n = 1; n <= size*size; n++){
					if (NumCandidates[i][j][n] != -1){
						int b = getBlockByLoc(i,j,size);
						// check list to check if a number n can be
						// located in the location (i,j)
						Set<Integer> checkList = new HashSet<Integer>();
						checkList.addAll(s.getBlockAsList(b));
						checkList.addAll(s.getRowAsList(i));
						checkList.addAll(s.getColAsList(j));
						/*if (isCandidate(s, reservedLocs, n,i,j) &&
								(checkList.contains(n))){
							NumCandidates[i][j][n] = n;
						}
						*/
						// if checkList contains n, the number n 
						// cannot be candidate-number anymore
						// (This will not be changed forever)
						if (checkList.contains(n)){
							NumCandidates[i][j][n] = -1;
						}
					}
				}
			}
		}
	}

	// make HashMap which maps number n to Set of its candidate-
	// locations in a block b
	static Map<Integer, Set<Pair<Integer,Integer>>> makeCandidateLocsInB(int[][][] NumCandidates, int b){
		int size = (int) Math.sqrt(NumCandidates.length);
		Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInB = new HashMap<Integer, Set<Pair<Integer,Integer>>>();
		int startRow = getStartRowByBlock(b, size);
		int startCol = getStartColByBlock(b, size);
		for (int n = 1; n <= size*size; n++){
			CandidateLocsInB.put(n, new HashSet<Pair<Integer,Integer>>());
			for (int i = startRow; i < startRow+size; i++){
				for (int j = startCol; j < startCol+size; j++){
					if (NumCandidates[i][j][n] == n)
						CandidateLocsInB.get(n).add(new Pair<Integer,Integer>(i,j));
				}
			}
		}
		return CandidateLocsInB;
	}

	// make HashMap which maps number n to Set of its candidate-
	// locations in a row
	static Map<Integer, Set<Pair<Integer,Integer>>> makeCandidateLocsInRow(int[][][] NumCandidates, int row){
		int size = (int) Math.sqrt(NumCandidates.length);
		Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInRow = new HashMap<Integer, Set<Pair<Integer,Integer>>>();
		for (int n = 1; n <= size*size; n++){
			CandidateLocsInRow.put(n, new HashSet<Pair<Integer,Integer>>());
			for (int j = 0; j < size*size; j++){
				if (NumCandidates[row][j][n] == n)
					CandidateLocsInRow.get(n).add(new Pair<Integer,Integer>(row,j));
			}
		}
		return CandidateLocsInRow;
	}

	// make HashMap which maps number n to Set of its candidate-
	// locations in a column
	static Map<Integer, Set<Pair<Integer,Integer>>> makeCandidateLocsInCol(int[][][] NumCandidates, int col){
		int size = (int) Math.sqrt(NumCandidates.length);
		Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocsInCol = new HashMap<Integer, Set<Pair<Integer,Integer>>>();
		for (int n = 1; n <= size*size; n++){
			CandidateLocsInCol.put(n, new HashSet<Pair<Integer,Integer>>());
			for (int i = 0; i < size*size; i++){
				if (NumCandidates[i][col][n] == n)
					CandidateLocsInCol.get(n).add(new Pair<Integer,Integer>(i, col));
			}
		}
		return CandidateLocsInCol;
	}
	
	// Check if the number of candidate-locations of a number n is one
	// If so, only the candidate n can be located at (i,j)
	static void checkCandidateLocs(Sudoku solved_s, Map<Integer, Set<Pair<Integer,Integer>>>  CandidateLocs,
			int[][][] NumCandidates){
		int size = solved_s.getSize();
		for (int n = 1; n <= size*size; n++){
			if (CandidateLocs.get(n).size() == 1){
				// Actually, this loop consists of one loop
				for (Pair<Integer,Integer> loc : CandidateLocs.get(n)){
					int i = loc.getFirst();
					int j = loc.getSecond();
					int b = getBlockByLoc(i, j, size);
					solved_s.setElement(i, j, n);
					// indicates that a number is fixed
					Arrays.fill(NumCandidates[i][j], -1);
					// the number n cannot be located in the same row and col
					for (int k = 0; k < size*size; k++){
						NumCandidates[i][k][n] = -1;
						NumCandidates[k][j][n] = -1;
					}
					// the number cannot be located in the same block
					int startRow = getStartRowByBlock(b, size);
					int startCol = getStartColByBlock(b, size);
					for (int k = startRow; k < startRow+size; k++){
						for (int l = startCol; l < startCol+size; l++){
							NumCandidates[k][l][n] = -1;
						}
					}
				}
			}
		}
	}

	// Check if the number of candidate-numbers at a location is one
	// If so, only the candidate n can be located at (i,j)
	static void checkCandidateNums(Sudoku solved_s, int[][][] NumCandidates){
		int size = solved_s.getSize();
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				List<Integer> CandidatesAtij = Arrays.stream(NumCandidates[i][j])
					.filter(n -> n>0)
					.mapToObj(n -> (Integer) n)
					.collect(Collectors.toList());
				if (CandidatesAtij.size() == 1){
					int n = CandidatesAtij.get(0);
					solved_s.setElement(i, j, n);
					// indicates that a number is fixed
					Arrays.fill(NumCandidates[i][j], -1);
					// the number cannot be located in the same row and col
					for (int k = 0; k < size*size; k++){
						NumCandidates[i][k][n] = -1;
						NumCandidates[k][j][n] = -1;
					}
					// the number cannot be located in the same block
					int b = getBlockByLoc(i, j, size);
					int startRow = getStartRowByBlock(b, size);
					int startCol = getStartColByBlock(b, size);
					for (int k = startRow; k < startRow+size; k++){
						for (int l = startCol; l < startCol+size; l++){
							NumCandidates[k][l][n] = -1;
						}
					}
				}
			}
		}
	}

	// Get block number by location (i,j)
	// For example, in 9x9 sudoku, the locations (3,0) ~ (5,2) are in block 3
	// (notate that block number starts at 0)
	static int getBlockByLoc(int i, int j, int size){
		return (i / size) * size + (j / size);
	}

	// Get start row of the block
	// For example, in 9x9 sudoku, the block 3 has row 3 ~ 5,
	// so its start row is 3
	static int getStartRowByBlock(int block, int size){
		return size * (block/size);
	}

	// Get start column of the block
	// For example, in 9x9 sudoku, the block 3 has col 0 ~ 2,
	// so its start column is 0
	static int getStartColByBlock(int block, int size){
		return size * (block%size);
	}

	// From candidate-locations, find a row and a column
	// that all these locations have in common.
	static void findHiddenRowAndCol(Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocs, int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
loop1:		for (int n = 1; n <= size*size; n++){
			// candidate-locations of n
			List<Pair<Integer,Integer>> locations = new ArrayList<Pair<Integer,Integer>>(CandidateLocs.get(n));
			// if no candidate-locations, skip to next n
			if (locations.size() == 0)
				continue loop1;

			// find hidden row
			// get the row of the first location as sample
			int rowChecker = locations.get(0).getFirst();
			for (Pair<Integer,Integer> loc : locations){
				// if the row of loc is not same as rowChecker,
				// loc-s are not in same row
				if (loc.getFirst() != rowChecker)
					continue loop1;
			}
			// set indicators of all the location in the row -1 once
			// (which means n is not candidate-number)
			for (int j = 0; j < size*size; j++){
				NumCandidates[rowChecker][j][n] = -1;
			}
			// And then, set indicators of candidate-locations n
			for (Pair<Integer,Integer> loc : locations){
				NumCandidates[loc.getFirst()][loc.getSecond()][n] = n;
			}
		}

loop2:		for (int n = 1; n <= size*size; n++){
			// candidate-locations of n
			List<Pair<Integer,Integer>> locations = new ArrayList<Pair<Integer,Integer>>(CandidateLocs.get(n));
			// if no candidate-locations, skip to next n
			if (locations.size() == 0)
				continue loop2;

			// find hidden column
			// get the column of the first location as sample
			int colChecker = locations.get(0).getSecond();
			for (Pair<Integer,Integer> loc : locations){
				// if the column of loc is not same as colChecker,
				// loc-s are not in same column
				if (loc.getSecond() != colChecker)
					continue loop2;
			}
			// set indicators of all the location in the column -1 once
			// (which means n is not candidate-number)
			for (int i = 0; i < size*size; i++){
				NumCandidates[i][colChecker][n] = -1;
			}
			// And then, set indicators of candidate-locations n
			for (Pair<Integer,Integer> loc : locations){
				NumCandidates[loc.getFirst()][loc.getSecond()][n] = n;
			}
		}
	}

	// From candidate-locations, find a block that contains all these
	// locations
	static void findHiddenBlock(Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocs, int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
loop:		for (int n = 1; n <= size*size; n++){
			// candidate-locations of n
			List<Pair<Integer,Integer>> locations = new ArrayList<Pair<Integer,Integer>>(CandidateLocs.get(n));
			// if no candidate-locations, skip to next n
			if (locations.size() == 0)
				continue loop;

			// find locations which are in a same block.
			// get the block of the first location as sample
			int blockChecker = getBlockByLoc(locations.get(0).getFirst(), locations.get(0).getSecond(), size);
			for (Pair<Integer,Integer> loc : locations){
				// if the block of loc is not same as blockChecker
				// loc-s are not in same block
				int i = loc.getFirst(); int j = loc.getSecond();
				if (getBlockByLoc(i,j,size) != blockChecker)
					continue loop;
			}
			// set indicators of all the locations in the block -1 once
			// (which means n is not candidate-number)
			int startRow = getStartRowByBlock(blockChecker, size);
			int startCol = getStartColByBlock(blockChecker, size);
			for (int i = startRow; i < startRow+size; i++){
				for (int j = startCol; j < startCol+size; j++){
					NumCandidates[i][j][n] = -1;
				}
			}
			// And then, set indicators of candidate-locations n
			for (Pair<Integer,Integer> loc : locations){
				NumCandidates[loc.getFirst()][loc.getSecond()][n] = n;
			}
		}
	}

	// In a block, find the locations that have same candidate-numbers.
	static void findSameCandidateNumsInBlock(int[][][] NumCandidates, int block){
		int size = (int) Math.sqrt(NumCandidates.length);
		// a HashMap which maps Set of numbers to List of locations which are candidates of the very numbers
		Map<Set<Integer>, List<Pair<Integer,Integer>>> nsToLocs = new HashMap<Set<Integer>, List<Pair<Integer,Integer>>>();
		int startRow = getStartRowByBlock(block, size);
		int startCol = getStartColByBlock(block, size);
		// make nsToLocs
		for (int i = startRow; i < startRow+size; i++){
			for (int j = startCol; j < startCol+size; j++){
				Set<Integer> ns = Arrays.stream(NumCandidates[i][j])
					.filter(n -> n>0)
					.mapToObj(n -> (Integer) n)
					.collect(Collectors.toSet());
				if (nsToLocs.get(ns) != null){
					nsToLocs.get(ns).add(new Pair<Integer,Integer>(i,j));
				}
				else if (ns.size() != 0){
					nsToLocs.put(ns, new ArrayList<Pair<Integer,Integer>>());
					nsToLocs.get(ns).add(new Pair<Integer,Integer>(i,j));
				}
			}
		}
		for (Set<Integer> ns : nsToLocs.keySet()){
			// if the number of numbers is equal to the number of locations
			// the other locations cannot be candidates of these numbers
			if (ns.size() == nsToLocs.get(ns).size()){
				for (Integer n : ns){
					// Once set all the n's indicator in the block -1
					for (int i = startRow; i < startRow+size; i++){
						for (int j = startCol; j < startCol+size; j++){
							NumCandidates[i][j][n] = -1;
						}
					}
					// And then, set n's indicator of locs n
					for (Pair<Integer,Integer> loc : nsToLocs.get(ns)){
						NumCandidates[loc.getFirst()][loc.getSecond()][n] = n;
					}
				}
			}
		}
	}

	// In a row, find the locations that have same candidate-numbers.
	static void findSameCandidateNumsInRow(int[][][] NumCandidates, int row){
		int size = (int) Math.sqrt(NumCandidates.length);
		// a HashMap which maps Set of numbers to List of locations which are candidates of the very numbers
		Map<Set<Integer>, List<Pair<Integer,Integer>>> nsToLocs = new HashMap<Set<Integer>, List<Pair<Integer,Integer>>>();
		for (int j = 0; j < size*size; j++){
			Set<Integer> ns = Arrays.stream(NumCandidates[row][j])
				.filter(n -> n>0)
				.mapToObj(n -> (Integer) n)
				.collect(Collectors.toSet());
			if (nsToLocs.get(ns) != null){
				nsToLocs.get(ns).add(new Pair<Integer,Integer>(row,j));
			}
			else{
				nsToLocs.put(ns, new ArrayList<Pair<Integer,Integer>>());
				nsToLocs.get(ns).add(new Pair<Integer,Integer>(row,j));
			}
		}
		for (Set<Integer> ns : nsToLocs.keySet()){
			// if the number of numbers is equal to the number of locations
			// the other locations cannot be candidates of these numbers
			if (ns.size() == nsToLocs.get(ns).size()){
				for (Integer n : ns){
					// Once set all the n's indicator in the row -1
					for (int j = 0; j < size*size; j++){
						NumCandidates[row][j][n] = -1;
					}
					// And then, set n's indicator of locs n
					for (Pair<Integer,Integer> loc : nsToLocs.get(ns)){
						NumCandidates[loc.getFirst()][loc.getSecond()][n] = n;
					}
				}
			}
		}
	}

	// In a column, find the locations that have same candidate-numbers.
	static void findSameCandidateNumsInCol(int[][][] NumCandidates, int col){
		int size = (int) Math.sqrt(NumCandidates.length);
		// a HashMap which maps Set of numbers to List of locations which are candidates of the very numbers
		Map<Set<Integer>, List<Pair<Integer,Integer>>> nsToLocs = new HashMap<Set<Integer>, List<Pair<Integer,Integer>>>();
		for (int i = 0; i < size*size; i++){
			Set<Integer> ns = Arrays.stream(NumCandidates[i][col])
				.filter(n -> n>0)
				.mapToObj(n -> (Integer) n)
				.collect(Collectors.toSet());
			if (nsToLocs.get(ns) != null){
				nsToLocs.get(ns).add(new Pair<Integer,Integer>(i,col));
			}
			else{
				nsToLocs.put(ns, new ArrayList<Pair<Integer,Integer>>());
				nsToLocs.get(ns).add(new Pair<Integer,Integer>(i,col));
			}
		}
		for (Set<Integer> ns : nsToLocs.keySet()){
			// if the number of numbers is equal to the number of locations
			// the other locations cannot be candidates of these numbers
			if (ns.size() == nsToLocs.get(ns).size()){
				for (Integer n : ns){
					// Once set all the n's indicator in the column -1
					for (int i = 0; i < size*size; i++){
						NumCandidates[i][col][n] = -1;
					}
					// And then, set n's indicator of locs n
					for (Pair<Integer,Integer> loc : nsToLocs.get(ns)){
						NumCandidates[loc.getFirst()][loc.getSecond()][n] = n;
					}
				}
			}
		}
	}

	// From CandidateLocs, find the numbers that have same candidate-
	// locations. 
	// The CandidateLocs is a HashMap which maps a number n to its 
	// candidate-locations in a block, row or column
	static void findSameCandidateLocs(Map<Integer, Set<Pair<Integer,Integer>>> CandidateLocs, int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
		// a HashMap which maps Set of locations to List of numbers whose candidates are the very locations
		Map<Set<Pair<Integer,Integer>>, List<Integer>> LocsTons = new HashMap<Set<Pair<Integer,Integer>>, List<Integer>>();
		for (int n = 1; n <= size*size; n++){
			// copy the locations to locs
			Set<Pair<Integer,Integer>> locs = new HashSet<Pair<Integer,Integer>>(CandidateLocs.get(n));
			if (LocsTons.get(locs) != null){
				LocsTons.get(locs).add(n);
			}
			else if (locs.size() != 0){
				LocsTons.put(locs, new ArrayList<Integer>());
				LocsTons.get(locs).add(n);
			}
		}
		for (Set<Pair<Integer,Integer>> locs : LocsTons.keySet()){
			// if the number of locations is equal to the number of numbers
			// the other numbers cannot be candidates of these locations
			if (locs.size() == LocsTons.get(locs).size()){
				for (Pair<Integer,Integer> loc : locs){
					// Once set all the indicator in the location -1
					for (int n = 1; n <= size*size; n++)
						NumCandidates[loc.getFirst()][loc.getSecond()][n] = -1;
					// And then, set n's indicator n (n is candidate-number)
					for (Integer n : LocsTons.get(locs))
						NumCandidates[loc.getFirst()][loc.getSecond()][n] = n;
				}
			}
		}
	}

	// Find rows over blocks (which are in same block-row) in which a number
	// is to be located. In this case, if the number of rows is equal
	// to the number of blocks where the rows are candidate of n,
	// in the other blocks, the number cannot be located in the rows.
	// In this code, firstly in each number in each block-row make HashMap 
	// of a block to rows which represents which block has which rows as 
	// candidates of n. And then classify blocks by same list of rows, and 
	// remap list of blocks to list of rows. Finally, check if the number 
	// of rows is equal to the number of candidate numbers, and if so, add
	// the data to hiddenNumbers
	static void findSameRowsInBlockRow(int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
                for (int bRow = 0; bRow < size; bRow++){
			int startRow = size*bRow;
			for (int n = 1; n <= size*size; n++){
				// HashMap which maps a block to rows where n will be located
				Map<Integer, Set<Integer>> rowCandidatesOfn = new HashMap<Integer, Set<Integer>>();
				// make rowCandidatesOfn
				for (int i = startRow; i < startRow+size; i++){
					for (int j = 0; j < size*size; j++){
						if (NumCandidates[i][j][n] == n){
							int b = getBlockByLoc(i,j,size);
							// if the key exist, add the row to the list
							if (rowCandidatesOfn.get(b) != null){
								rowCandidatesOfn.get(b).add(i);
							}
							// if the key doesn't exist, map the key to new ArrayList
							// and add the row to the list
							else{
								rowCandidatesOfn.put(b, new HashSet<Integer>());
								rowCandidatesOfn.get(b).add(i);
							}
						}
					}
				}
				// HashMap which maps a list of rows to a list of blocks where the rows are 
				// candidates of n
				Map<Set<Integer>, List<Integer>> RowsToBlocks = new HashMap<Set<Integer>, List<Integer>>();
				// make RowsToBlocks
				for (Integer b : rowCandidatesOfn.keySet()){
					Set<Integer> rows = rowCandidatesOfn.get(b);
					// if the key exist, add the block to the list
					if (RowsToBlocks.get(rows) != null){
						RowsToBlocks.get(rows).add(b);
					}
					// if the key doesn't exist, map the key to new ArrayList
					// and add the block to the list
					else{
						RowsToBlocks.put(rows, new ArrayList<Integer>());
						RowsToBlocks.get(rows).add(b);	
					}
				}
				// if the number of rows and the number of blocks are same,
				// n cannot be located in the rows of the other blocks 
				for (Set<Integer> rows : RowsToBlocks.keySet()){
					if (rows.size() == RowsToBlocks.get(rows).size()){
						List<Integer> blocks = RowsToBlocks.get(rows);
						for (Integer row : rows)
							for (int j = 0; j < size*size; j++)
								if (!blocks.contains(getBlockByLoc(row,j,size)))
									NumCandidates[row][j][n] = -1;
					}
				}
			}
		}
	}

	// Find columns over blocks (which are in same block-col) in which 
	// a number is to be located. In this case, if the number of rows 
	// is equal to the number of blocks where the columns are candidate of n,
	// in the other blocks, the number cannot be located in the columns.
	// This algorithm is same as findSameRowsInBlockRow
	static void findSameColsInBlockCol(int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
                for (int bCol = 0; bCol < size; bCol++){
			int startCol = size*bCol;
			for (int n = 1; n <= size*size; n++){
				// HashMap which maps a block to columns where n will be located
				Map<Integer, Set<Integer>> colCandidatesOfn = new HashMap<Integer, Set<Integer>>();
				// make colCandidatesOfn
				for (int j = startCol; j < startCol+size; j++){
					for (int i = 0; i < size*size; i++){
						if (NumCandidates[i][j][n] == n){
							int b = getBlockByLoc(i,j,size);
							// if the key exist, add the column to the list
							if (colCandidatesOfn.get(b) != null){
								colCandidatesOfn.get(b).add(j);
							}
							// if the key doesn't exist, map the key to new ArrayList
							// and add the column to the list
							else{
								colCandidatesOfn.put(b, new HashSet<Integer>());
								colCandidatesOfn.get(b).add(j);
							}
						}
					}
				}
				// HashMap which maps a list of columns to a list of blocks where the columns are 
				// candidates of n
				Map<Set<Integer>, List<Integer>> ColsToBlocks = new HashMap<Set<Integer>, List<Integer>>();
				// make ColsToBlocks
				for (Integer b : colCandidatesOfn.keySet()){
					Set<Integer> cols = colCandidatesOfn.get(b);
					// if the key exist, add the block to the list
					if (ColsToBlocks.get(cols) != null){
						ColsToBlocks.get(cols).add(b);
					}
					// if the key doesn't exist, map the key to new ArrayList
					// and add the block to the list
					else{
						ColsToBlocks.put(cols, new ArrayList<Integer>());
						ColsToBlocks.get(cols).add(b);	
					}
				}
				// if the number of columns and the number of blocks are same,
				// n cannot be located in the columns of the other blocks 
				for (Set<Integer> cols : ColsToBlocks.keySet()){
					if (cols.size() == ColsToBlocks.get(cols).size()){
						List<Integer> blocks = ColsToBlocks.get(cols);
						for (Integer col : cols)
							for (int i = 0; i < size*size; i++)
								if (!blocks.contains(getBlockByLoc(i,col,size)))
									NumCandidates[i][col][n] = -1;
					}
				}
			}
		}
	}

	// Check if some rows have same candidate-locations for n.
	// If the number of such rows are equal to the number of
	// candidate-locations for n, n can't be located in the 
	// other rows.
	static void XWing_Row(int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
		// Now we call column number of a location, col-coord.
		// For example, col-coord of location (i,j) is j.
		for (int n = 1; n <= size*size; n++){
			// A HashMap which maps Set of col-coord of n's candidate-locations to List of rows
			// each of in which n is candidate at the col-coord
			// In other words, this Map classifies rows by col-coords of n's candidate-location
			Map<Set<Integer>, List<Integer>> ColsToRows = new HashMap<Set<Integer>, List<Integer>>();
			// make ColsToRows
			for (int row = 0; row < size*size; row++){
				// Set of column coordinates in a row where n is candidate
				Set<Integer> colCoordinatesOfRow = new HashSet<Integer>();
				for (int col = 0; col < size*size; col++){
					// if n is candidate in the col-coord, add col
					if (NumCandidates[row][col][n] == n)
						colCoordinatesOfRow.add(col);
				}
				// if same Set(key) exist, add the row to the List(value)
				if (ColsToRows.get(colCoordinatesOfRow) != null){
					ColsToRows.get(colCoordinatesOfRow).add(row);
				}
				// if same Set doesn't exist and colCoordinatesOfRow is not empty,
				// map it to a new List and add the row to the List
				else if (colCoordinatesOfRow.size() != 0){
					ColsToRows.put(colCoordinatesOfRow, new ArrayList<Integer>());
					ColsToRows.get(colCoordinatesOfRow).add(row);
				}
			}
			for (Set<Integer> cols : ColsToRows.keySet()){
				// if the number of col-coords is equal to  the number of its rows,
				// n cannot be located in the other locations in the columns.
				// ("other" means "except the rows (value of ColsToRows)")
				if (cols.size() == ColsToRows.get(cols).size()){
					for (Integer col : cols){
						// set all the indicators in the column -1 once
						for (int i = 0; i < size*size; i++)
							NumCandidates[i][col][n] = -1;
						// And then, set indicators in the row n
						for (Integer row : ColsToRows.get(cols))
							NumCandidates[row][col][n] = n;
					}
				}
			}
		}
	}

	// Check if some columns have same candidate-locations for n.
	// If the number of such columns are equal to the number of
	// candidate-locations for n, n can't be located in the 
	// other columns.
	static void XWing_Col(int[][][] NumCandidates){
		int size = (int) Math.sqrt(NumCandidates.length);
		// Now we say row number of a location, row-coord.
		// For example, row-coord of location (i,j) is i.
		for (int n = 1; n <= size*size; n++){
			// A HashMap which maps Set of row-coords of n's candidate-locations to List of columns
			// each of in which n is candidate at the row-coords
			// In other words, this Map classifies columns by row-coords of n's candidate-location
			Map<Set<Integer>, List<Integer>> RowsToCols = new HashMap<Set<Integer>, List<Integer>>();
			// make RowsToCols
			for (int col = 0; col < size*size; col++){
				// Set of row-coords in a column where n is candidate
				Set<Integer> rowCoordinatesOfCol = new HashSet<Integer>();
				for (int row = 0; row < size*size; row++){
					// if n is candidate in the row-coord, add row
					if (NumCandidates[row][col][n] == n)
						rowCoordinatesOfCol.add(row);
				}
				// if same Set(key) exist, add the column to the List(value)
				if (RowsToCols.get(rowCoordinatesOfCol) != null){
					RowsToCols.get(rowCoordinatesOfCol).add(col);
				}
				// if key doesn't exist and rowCoordinatesOfCol is not empty,
				// map it to a new List and add the column to the List
				else if (rowCoordinatesOfCol.size() != 0){
					RowsToCols.put(rowCoordinatesOfCol, new ArrayList<Integer>());
					RowsToCols.get(rowCoordinatesOfCol).add(col);
				}
			}
			for (Set<Integer> rows : RowsToCols.keySet()){
				// if the number of row coordinates is equal to  the number of its cols,
				// n cannot be located in the other locations in the rows.
				// ("other" means "except the columns (value of RowsToCols)")
				if (rows.size() == RowsToCols.get(rows).size()){
					for (Integer row : rows){
						// set all the indicators in the row -1 once
						for (int j = 0; j < size*size; j++)
							NumCandidates[row][j][n] = -1;
						// And then, set indicators in the column n
						for (Integer col : RowsToCols.get(rows))
							NumCandidates[row][col][n] = n;
					}
				}
			}
		}
	}

	// Check if the sudoku has more numbers than 17.
	// If not the sudoku cannot be solved
	static boolean hasNumbersMoreThan17(Sudoku s){
		int count = 0;
		int size = s.getSize();
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				if (s.getElement(i, j) != 0)
					count++;
				if (count >= 17)
					return true;
			}
		}
		return false;
	}

	// Check if there is contradiction in the sudoku
	// In other words, check if there is a cell where
	// any number cannot locate.
	static boolean hasContradiction(Sudoku s, int[][][] NumCandidates){
		int size = s.getSize();
		// check if there is a cell that has no candidate-numbers
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				int[] CandidatesAtij = Arrays.stream(NumCandidates[i][j])
					.filter(n -> n>0)
					.toArray();
				if (s.getElement(i,j) == 0 &&
					CandidatesAtij.length == 0){
					return true;
				}
			}
		}
		// check if there is a number in a block, row or column
		// that has no candidate-locations
		for (int i = 0; i < size*size; i++){
			Map<Integer, Set<Pair<Integer,Integer>>> candidateLocsInB = 
				makeCandidateLocsInB(NumCandidates, i);
			Map<Integer, Set<Pair<Integer,Integer>>> candidateLocsInRow = 
				makeCandidateLocsInRow(NumCandidates, i);
			Map<Integer, Set<Pair<Integer,Integer>>> candidateLocsInCol = 
				makeCandidateLocsInCol(NumCandidates, i);
			List<Integer> NumbersInB = s.getBlockAsList(i);
			List<Integer> NumbersInRow = s.getRowAsList(i);
			List<Integer> NumbersInCol = s.getColAsList(i);
			for (int n = 1; n <= size*size; n++){
				if (!NumbersInB.contains(n) &&
						candidateLocsInB.get(n).size() == 0)
					return true;
				if (!NumbersInRow.contains(n) &&
						candidateLocsInRow.get(n).size() == 0)
					return true;
				if (!NumbersInCol.contains(n) &&
						candidateLocsInCol.get(n).size() == 0)
					return true;
			}
		}
		return false;
	}

	/*public static void main(String[] args){
		System.out.println("SudokuManipulator test");
		Sudoku s = new Sudoku(new int[][] 
				{{1,2,3,4,5,6,7,8,9},
				{4,5,6,7,8,9,1,2,3},
				{7,8,9,1,2,3,4,5,6},
				{2,3,4,5,6,7,8,9,1},
				{5,6,7,8,9,1,2,3,4},
				{8,9,1,2,3,4,5,6,7},
				{3,4,5,6,7,8,9,1,2},
				{6,7,8,9,1,2,3,4,5},
				{9,1,2,3,4,5,6,7,8}}, 3);
		System.out.println(s.toString());
		System.out.println(s.isSolved());
		System.out.println(s.toString());
		System.out.println(s.toString());
	}*/
}
