/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.KillerSudokuGrid;
import grid.StdSudokuGrid;

/**
 * Abstract class for common attributes or methods for solvers of Killer
 * Sudoku.
 * Note it is not necessary to use this, but provided in case you wanted to do
 * so and then no need to change the hierarchy of solver types.
 */
public abstract class KillerSudokuSolver extends SudokuSolver
{
	
	private void initConstants(KillerSudokuGrid grid) {
		SIZE = grid.size;
		MIN_VALUE = minValue(grid.validNumbers);
		MAX_VALUE = maxValue(grid.validNumbers);
		BOX_SIZE = (int) Math.sqrt(SIZE);
	}
	
	protected int[][] getCoverMatrix(KillerSudokuGrid killergrid){
		initConstants(killergrid);
		return convertInCoverMatrix(killergrid.grid);
	}
} // end of class KillerSudokuSolver
