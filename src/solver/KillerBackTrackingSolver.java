/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.KillerSudokuGrid;
import grid.SudokuGrid;

/**
 * Backtracking solver for Killer Sudoku.
 */
public class KillerBackTrackingSolver extends KillerSudokuSolver {
	private final int EMPTY = 0;

	public KillerBackTrackingSolver() {

	} // end of KillerBackTrackingSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		KillerSudokuGrid stdgrid = (KillerSudokuGrid) grid;
		int size = stdgrid.size;
		int[] validNumbers = stdgrid.validNumbers;
		int[][] board = stdgrid.grid;

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board[row][col] == EMPTY) {

					for (int number : validNumbers) {
						// check if the value satifies all the constraints.
						if (isOkToInsert(row, col, number, stdgrid)) {
							board[row][col] = number;
							// call recursive method
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
	public boolean isOkToInsert(int row, int col, int number, KillerSudokuGrid grid) {
		if (grid.checkNumberIsValid(number) && grid.isInRow(row, number) == 0 && grid.isInCol(col, number) == 0
				&& grid.isInBox(row, col, number) == 0 && grid.isCagesTotalOk(row, col, number)) {
			return true;
		}
		return false;
	}

} // end of class KillerBackTrackingSolver()
