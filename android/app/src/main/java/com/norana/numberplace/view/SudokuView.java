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

package com.norana.numberplace.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.norana.numberplace.sudoku.Pair;
import com.norana.numberplace.sudoku.Sudoku;
import com.norana.numberplace.sudoku.Note;

/**
 * Subclass of View to display sudoku
 */
public class SudokuView extends View{

	private final String[] digits = new String[] {"0", "1", "2", "3", "4", "5","6", "7", "8", "9"};                       // array of digits (used for onDraw)
	
	private float contentSize;            // width of the content
	private Paint thickLinePaint;         // Paint for thick line
	private Paint thinLinePaint;          // Paint for thin line
	private Paint numberPaint;            // Paint for text of number
	private Paint notePaint;              // Paint for text of note
	private Paint fixedCellPaint;         // Paint for the number-fixed cell color
	private Paint selectedCellPaint;      // Paint for the selected cell color
	private Rect textBounds;              // text bounds to adjust text size
	private float thickLineStrokeWidth;   // stroke width of thick line
	private float thinLineStrokeWidth;    // stroke width of thin line
	private float numberTextSize;         // text size of number
	private float noteTextSize;           // text size of note
	private Pair<Integer, Integer> selectedCell;  // coordinate of selected cell


	// sudoku to be displayed
	private Sudoku sudoku;
	// note numbers. Each number is represented by 9 bits
	private Note note;
	// matrix to hold data of contradictions
	private boolean[][] contradictionM;

	public SudokuView(Context context){
		super(context);
		initViewConstants();
	}

	public SudokuView(Context context, AttributeSet attrs){
		super(context, attrs);
		initViewConstants();
	}

	private void initViewConstants(){
		thickLinePaint = new Paint();
		thinLinePaint = new Paint();
		numberPaint = new Paint();
		notePaint = new Paint();
		fixedCellPaint = new Paint();
		selectedCellPaint = new Paint();
		textBounds = new Rect();
		setPadding(1,1,1,1);
		contentSize = 360;   // default content size
		thickLineStrokeWidth = contentSize / 100;
		thinLineStrokeWidth = contentSize / 200;
		thickLinePaint.setStrokeWidth(thickLineStrokeWidth);
		thickLinePaint.setColor(Color.BLACK);
		thinLinePaint.setStrokeWidth(thinLineStrokeWidth);
		thinLinePaint.setColor(Color.LTGRAY);
		numberPaint.setColor(Color.BLACK);
		notePaint.setColor(Color.BLACK);
		fixedCellPaint.setStyle(Paint.Style.FILL);
		fixedCellPaint.setARGB(128, 210, 210, 210);
		selectedCellPaint.setStyle(Paint.Style.FILL);
		selectedCellPaint.setARGB(128,63, 191, 180);

		sudoku = new Sudoku(3);
		note = new Note(3);
		contradictionM = new boolean[9][9];
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext()
			.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		// the default width (height) is min(screenW, screenH)
		int defaultSize = Math.min(displayMetrics.widthPixels,
								displayMetrics.heightPixels);

		int resultW = measureWidth(widthMeasureSpec, defaultSize);
		int resultH = measureHeight(heightMeasureSpec, defaultSize);
		int result = Math.min(resultW, resultH);
		contentSize = result - getPaddingLeft() - getPaddingRight();
		setMeasuredDimension(result, result);
	}

	private int measureWidth(int widthMeasureSpec, int defaultW){
		int result = 0;
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY){
			result = specSize;
		}
		else{
			// measure width for wrap_content
			result = defaultW;
			if (specMode == MeasureSpec.AT_MOST){
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private int measureHeight(int heightMeasureSpec, int defaultH){
		int result = 0;
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);

		if (specMode == MeasureSpec.EXACTLY){
			result = specSize;
		}
		else{
			// measure height for wrap_content
			result = defaultH;
			if (specMode == MeasureSpec.AT_MOST){
				result = Math.min(result, specSize);
			}
		}
		return result;
	}


	@Override
	protected void onDraw(Canvas canvas){
		contentSize = Math.min(getWidth()-getPaddingLeft()-getPaddingRight(),
				getHeight()-getPaddingTop()-getPaddingBottom());
		float startX = getPaddingLeft();
		float startY = getPaddingTop();
		// fill the entire canvas white
		canvas.drawColor(Color.WHITE);
		// draw the lines of sudoku UI
		for (int i = 0; i < 10; i++){
			canvas.drawLine(startX, i*contentSize/9 + startY,
					contentSize + startX, i*contentSize/9 + startY,
					thinLinePaint);
			canvas.drawLine(i*contentSize/9 + startX, startY,
					i*contentSize/9 + startX, contentSize + startY,
					thinLinePaint);
		}
		for (int i = 0; i < 4; i++){
			canvas.drawLine(startX, i*contentSize/3 + startY,
					contentSize + startX, i*contentSize/3 + startY,
					thickLinePaint);
			canvas.drawLine(i*contentSize/3 + startX, startY,
					i*contentSize/3 + startX, contentSize + startY,
					thickLinePaint);
		}

		// color the selected cell
		if (selectedCell != null){
			int i = selectedCell.getFirst();
			int j = selectedCell.getSecond();
			colorCell(canvas, i, j, selectedCellPaint);
		}

		// draw the numbers in the sudoku
		int size = sudoku.getSize();
		for (int i = 0; i < size*size; i++){
			for (int j = 0; j < size*size; j++){
				int n = sudoku.getElement(i,j);
				if (n != 0){
					if (sudoku.isFixedCell(i, j)){
						colorCell(canvas, i, j, fixedCellPaint);
					}
					if (contradictionM[i][j]){
						// if the number at (i,j) is contradicted
						drawNumber(canvas, i, j, n, Color.RED);
					}
					else{
						// if the number at (i,j) is not contradicted
						drawNumber(canvas, i, j, n, Color.BLACK);
					}
				}
				else   // if n==0, draw notes at (i,j)
					drawNote(canvas, i, j);
			}
		}

		invalidate();
	}

	// color a cell at (i,j) of sudoku
	private void colorCell(Canvas canvas, int i, int j, Paint color){
		/*float blankWidth = (contentSize
				- 4*thickLineStrokeWidth - 6*thinLineStrokeWidth) / 9;*/
		float blankWidth = contentSize / 9;
		/*float left = thickLineStrokeWidth*(j/3+1) +
						 thinLineStrokeWidth*(j-(j/3)) +
						 blankWidth * j +
						 getPaddingLeft();*/
		float left = j * contentSize / 9 + getPaddingLeft();
		float right = left + blankWidth;
		/*float top = thickLineStrokeWidth * (i/3+1) +
						 thinLineStrokeWidth * (i-(i/3)) +
						 blankWidth * i +
						 getPaddingTop();*/
		float top = i * contentSize / 9 + getPaddingTop();
		float bottom = top + blankWidth;
		// color a cell
		canvas.drawRect(left, top, right, bottom, color);
	}	

	// draw a number at (i,j) of sudoku
	private void drawNumber(Canvas canvas, int i, int j, int n, int color){
		float blankWidth = (contentSize
				- 4*thickLineStrokeWidth - 6*thinLineStrokeWidth) / 9;
		float center_x = thickLineStrokeWidth*(j/3+1) + 
						 thinLineStrokeWidth*(j-(j/3)) +
						 blankWidth * (2*j+1) / 2 +
						 getPaddingLeft();
		float center_y = thickLineStrokeWidth * (i/3+1) + 
						 thinLineStrokeWidth * (i-(i/3)) +
						 blankWidth * (2*i+1) / 2 +
						 getPaddingTop();
		float baseline = thickLineStrokeWidth * (i/3+1) + 
						 thinLineStrokeWidth * (i-(i/3)) +
						 blankWidth * (i+1) +
						 getPaddingTop();
		String s = digits[n];

		// set text color
		numberPaint.setColor(color);
		// set appropreate text size in a tricky way
		final float testTextSize = 40;
		numberPaint.setTextSize(testTextSize);
		numberPaint.getTextBounds(s, 0, s.length(), textBounds);
		// desired text measured width(height) is blankWidth / 2
		float desiredTextSize = blankWidth * testTextSize 
			/ Math.max(textBounds.width(), textBounds.height()) / 2;
		numberPaint.setTextSize(desiredTextSize);

		canvas.drawText(s, center_x - numberPaint.measureText(s)/2,
						   baseline - blankWidth/4, numberPaint);
	}

	// draw note at (i,j) of sudoku
	private void drawNote(Canvas canvas, int i, int j){
		float blankWidth = (contentSize
				- 4*thickLineStrokeWidth - 6*thinLineStrokeWidth) / 9;

		int noteNum = note.getNoteNumber(i, j);
		for (int n = 0; n < 9; n++){
			if ((noteNum >> n & 1) == 1){ // this means note n+1 is set at (i,j)
				String s = digits[n+1];
				int row = n / 3;
				int col = n % 3;
				float baseline = thickLineStrokeWidth * (i/3+1) + 
								 thinLineStrokeWidth * (i-(i/3)) +
								 blankWidth * (3*i+row+1) / 3 +
								 getPaddingTop();
				float center_x = thickLineStrokeWidth*(j/3+1) + 
								 thinLineStrokeWidth*(j-(j/3)) +
								 blankWidth * j +
								 blankWidth / 6 * (2*col+1) +
								 getPaddingLeft();
				// set appropreate text size in a ricky way
				final float testTextSize = 40;
				notePaint.setTextSize(testTextSize);
				notePaint.getTextBounds(s, 0, s.length(), textBounds);
				// desired text measured width(height) is blankWidth / 4
				float desiredTextSize = blankWidth * testTextSize 
					/ Math.max(textBounds.width(), textBounds.height()) / 4;
				notePaint.setTextSize(desiredTextSize);
				canvas.drawText(s,
						center_x - notePaint.measureText(s)/2,
						baseline, notePaint);
			}
		}
	}

	/**
	 * color a cell when the cell is touched if set true,or do not if false
	 */
	public void setColorOnTouch(boolean bool){
		if (bool){
			setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View v, MotionEvent event){
					if (event.getAction() == MotionEvent.ACTION_DOWN){
						float x = event.getX();
						float y = event.getY();
						selectedCell = getSelectedCellFromFloat(x,y);
					}
					return true;
				}
			});
		}
		else{
			setOnTouchListener(null);
		}
	}

	private Pair<Integer, Integer> getSelectedCellFromFloat(float x,float y){
		float blankWidth = contentSize / 9;
		int i = (int) Math.floor(y / blankWidth);
		int j = (int) Math.floor(x / blankWidth);
		if (0 <= i && i < 9 && 0 <= j && j < 9)
			return new Pair<Integer, Integer>(i, j);
		else
			return null;
	}

	/**
	 * get sudoku being displayed now
	 */
	public Sudoku getSudoku(){
		return this.sudoku;
	}

	/**
	 * set sudoku to display
	 */
	public void setSudoku(Sudoku s){
		if (s != null){
			this.sudoku = s;
			findContradictions();
		}
	}

	/**
	 * copy sudoku to display
	 */
	public void copySudoku(Sudoku s){
		if (s != null){
			int size = this.sudoku.getSize();
			for (int i = 0; i < size*size; i++){
				for (int j = 0; j < size*size; j++){
					this.sudoku.setElement(i, j, s.getElement(i, j));
					this.sudoku.fixCell(i, j, s.isFixedCell(i, j));
				}
			}
			findContradictions();
		}
	}

	// find contradictions and update contradictionM
	private void findContradictions(){
		// initialize contradictionM
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				contradictionM[i][j] = false;

		// loop through all rows, columns and blocks
		for (int k = 0; k < 9; k++){
			boolean[] indicators;

			// check same numbers in the row firstly
			indicators = findDuplicateNumbersInArray(sudoku.getRow(k));
			for (int l = 0; l < 9; l++)
				if (indicators[l])  // only if indicators[l]==true
					contradictionM[k][l] = indicators[l];

			// check same numbers in the column secondly
			indicators = findDuplicateNumbersInArray(sudoku.getCol(k));
			for (int l = 0; l < 9; l++)
				if (indicators[l])  // only if indicators[l]==true
					contradictionM[l][k] = indicators[l];

			// check same numbers in the block finally
			indicators = findDuplicateNumbersInArray(sudoku.getBlockAsArray(k));
			int startRow = getStartRowByBlock(k);
			int startCol = getStartColByBlock(k);
			for (int l1 = 0; l1 < 3; l1++){
				for (int l2 = 0; l2 < 3; l2++){
					if (indicators[3*l1 + l2]) // only if indicators[l]==true
						contradictionM[startRow+l1][startCol+l2] = 
							indicators[3*l1 + l2];
				}
			}
		}
	}

	// find duplicated numbers in the array and return its indicator
	// (true if duplicated, false otherwise)
	private boolean[] findDuplicateNumbersInArray(int[] array){
		int len = array.length;
		boolean[] indicators = new boolean[len];
		for (int i = 0; i < len; i++){
			int n = array[i];
			int count = 0;
			// find same number as array[i] in the rest of the array
			if (n != 0 && !indicators[i]){
				// if n==0 or indicators[i]==true, no check need
				for (int j = i+1; j < len; j++){
					if (array[j] == n){
						indicators[j] = true;
						count++;
					}
				}
				if (count >= 1)
					indicators[i] = true;
			}
		}
		return indicators;
	}

	private int getStartRowByBlock(int block){
		return 3 * (block/3);
	}

	private int getStartColByBlock(int block){
		return 3 * (block%3);
	}

	/**
	 * set note to display
	 */
	public void setNote(Note n){
		if (n != null)
			this.note = n;
	}

	/**
	 * copy note to display
	 */
	public void copyNote(Note n){
		if (n != null){
			int size = this.note.getSize();
			for (int i = 0; i < size*size; i++)
				for (int j = 0; j < size*size; j++)
					this.note.setNoteNumber(i, j, n.getNoteNumber(i, j));
		}
	}

	/**
	 * get the selected cell
	 */
	public Pair<Integer, Integer> getSelectedCell(){
		return this.selectedCell;
	}

	/**
	 * set the selected cell
	 */
	public void setSelectedCell(Pair<Integer, Integer> cell){
		this.selectedCell = cell;
	}

	/**
	 * get number at (i,j) of sudoku
	 */
	public int getNumber(int i, int j){
		return this.sudoku.getElement(i, j);
	}

	/**
	 * get number at Pair(i,j) of sudoku
	 */
	public int getNumber(Pair<Integer, Integer> cell){
		return this.sudoku.getElement(cell.getFirst(), cell.getSecond());
	}

	/**
	 * get number at the selected cell
	 */
	public int getNumber(){
		if (selectedCell != null){
			int i = selectedCell.getFirst();
			int j = selectedCell.getSecond();
			return this.sudoku.getElement(i, j);
		}
		else{
			return -1;
		}
	}

	/**
	 * set number n at (i,j) of sudoku
	 */
	public void setNumber(int i, int j, int n){
		this.sudoku.setNumber(i, j, n);
		findContradictions();;
	}

	/**
	 * set number n at Pair(i,j) of sudoku
	 */
	public void setNumber(Pair<Integer, Integer> cell, int n){
		this.sudoku.setNumber(cell.getFirst(), cell.getSecond(), n);
		findContradictions();
	}

	/**
	 * set number n at the selected cell
	 */
	public void setNumber(int n){
		if (selectedCell != null){
			int i = selectedCell.getFirst();
			int j = selectedCell.getSecond();
			this.sudoku.setNumber(i, j, n);
			findContradictions();
		}
	}

	/**
	 * reset sudoku, that is set all unfixed numbers 0
	 */
	public void resetSudoku(){
		this.sudoku.resetSudoku();
	}

	/**
	 * get note (as an indicator array) at (i,j) of sudoku
	 */
	public int[] getNoteArray(int i, int j){
		return this.note.getNoteArray(i, j);
	}

	/**
	 * get note (as an indicator array) at Pair(i,j) of sudoku
	 */
	public int[] getNoteArray(Pair<Integer ,Integer> cell){
		return this.note.getNoteArray(cell);
	}

	/**
	 * set note m at (i,j) of sudoku
	 */
	public void setNoteNumber(int i, int j, int m){
		if (m >= 1 && m <= 9){
			// add or remove note by toggling indicator
			int toggled = this.note.getNoteNumber(i, j) ^ (1<<(m-1));
			this.note.setNoteNumber(i, j, toggled);
		}
		else if (m == 0){
			// reset the note
			this.note.setNoteNumber(i, j, 0);
		}
	}

	/**
	 * set note m at Pair(i,j) of sudoku
	 */
	public void setNoteNumber(Pair<Integer, Integer> cell, int m){
		if (m >= 1 && m <= 9){
			// add or remove note by toggling indicator
			int toggled = this.note.getNoteNumber(cell) ^ (1<<(m-1));
			this.note.setNoteNumber(cell, toggled);
		}
		else if (m == 0){
			// reset the note
			this.note.setNoteNumber(cell, 0);
		}
	}

	/**
	 * set note m at the selected cell
	 */
	public void setNoteNumber(int m){
		if (selectedCell != null){
			setNoteNumber(selectedCell, m);
		}
	}

	/**
	 * set note at (i,j) from indicator array
	 */
	public void setNoteArray(int i, int j, int[] indicators){
		int noteNumber = 0;
		for (int k = 0; k < 9; k++)
			noteNumber += (indicators[k] << k);
		setNoteNumber(i, j, noteNumber);
	}

	/** 
	 * set note at Pair(i,j) from indicator array
	 */
	public void setNoteArray(Pair<Integer, Integer> cell, int[] indicators){
		int i = cell.getFirst();
		int j = cell.getSecond();
		setNoteArray(i, j, indicators);
	}

	/**
	 * toggle note number m at (i,j)
	 */
	public void toggleNoteNumber(int i, int j, int m){
		this.note.toggleNoteNumber(i, j, m);
	}

	/**
	 * toggle note number m at the cell
	 */
	public void toggleNoteNumber(Pair<Integer,Integer> cell, int m){
		this.note.toggleNoteNumber(cell, m);
	}
}
