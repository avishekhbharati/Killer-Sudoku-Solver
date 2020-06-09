/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Backtracking solver for standard Sudoku.
 */
public class BackTrackingSolver extends StdSudokuSolver {

	private final int EMPTY = 0;

	public BackTrackingSolver() {

	} // end of BackTrackingSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		StdSudokuGrid stdgrid = (StdSudokuGrid) grid;

		int size = stdgrid.size;
		int[] validNumbers = stdgrid.validNumbers;
		int[][] board = stdgrid.grid;

		//iterate over each cell.
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				
				//check if cell is empty.
				if (board[row][col] == EMPTY) {
					
					//try inserting the values from the list of valid values.
					for (int number : validNumbers) {
						
						// check if the value satisfies all the constraints.
						if (isOkToInsert(row, col, number, stdgrid)) {
							
							//insert value to the cell
							board[row][col] = number;
							
							//recursive call to solve the grid
							if (solve(grid)) {
								return true;
							} else {
								board[row][col] = EMPTY;
							}
						}
					}
					return false;
				}
			}
		}
		return true;

	} // end of solve()

	
	// combined methods to check the constarints before inserting a value
	public boolean isOkToInsert(int row, int col, int number, StdSudokuGrid grid) {
		if (grid.checkNumberIsValid(number) 
				&& grid.isInRow(row, number) == 0 
				&& grid.isInCol(col, number) == 0
				&& grid.isInBox(row, col, number) == 0) {
			
			return true;
		}
		
		return false;
	}

} // end of class BackTrackingSolver()
