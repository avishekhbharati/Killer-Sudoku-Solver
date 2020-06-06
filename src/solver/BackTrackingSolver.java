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
		// TODO: your implementation of the backtracking solver for standard Sudoku.
		StdSudokuGrid stdgrid = (StdSudokuGrid) grid;

		int size = stdgrid.size;
		int[] validNumbers = stdgrid.validNumbers;
		int[][] board = stdgrid.grid;

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (board[row][col] == EMPTY) {

					for (int number : validNumbers) {
						if (stdgrid.isOkToInsert(row, col, number)) {
							board[row][col] = number;

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

} // end of class BackTrackingSolver()
