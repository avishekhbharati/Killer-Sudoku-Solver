/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import java.util.Arrays;

import grid.StdSudokuGrid;

/**
 * Abstract class for common attributes or methods for solvers of standard
 * Sudoku. Note it is not necessary to use this, but provided in case you wanted
 * to do so and then no need to change the hierarchy of solver types.
 */
public abstract class StdSudokuSolver extends SudokuSolver {

	// Grid size
	protected int SIZE;
	// Box size
	protected int BOX_SIZE;
	protected int EMPTY_CELL = 0;
	// 4 constraints : cell, line, column, boxes
	protected static final int CONSTRAINTS = 4;
	// Values for each cells
	protected int MIN_VALUE;
	protected int MAX_VALUE;
	// Starting index for cover matrix
	protected static final int COVER_START_INDEX = 1;

	private void initConstants(StdSudokuGrid grid) {
		SIZE = grid.size;
		MIN_VALUE = minValue(grid.validNumbers);
		MAX_VALUE = maxValue(grid.validNumbers);
		BOX_SIZE = (int) Math.sqrt(SIZE);
	}

	// Find minimum (lowest) value in array using array sort
	protected static int minValue(int[] numbers) {
		Arrays.sort(numbers);
		return numbers[0];
	}

	// Find maximum (largest) value in array using array sort
	protected static int maxValue(int[] numbers) {
		Arrays.sort(numbers);
		return numbers[numbers.length - 1];
	}

	// Index in the cover matrix
	protected int indexInCoverMatrix(int row, int column, int num) {
		return (row - 1) * SIZE * SIZE + (column - 1) * SIZE + (num - 1);
	}

	// Building of an empty cover matrix
	private int[][] createCoverMatrix() {
		int[][] coverMatrix = new int[SIZE * SIZE * MAX_VALUE][SIZE * SIZE * CONSTRAINTS];

		int header = 0;
		header = createCellConstraints(coverMatrix, header);
		header = createRowConstraints(coverMatrix, header);
		header = createColumnConstraints(coverMatrix, header);
		createBoxConstraints(coverMatrix, header);

		return coverMatrix;
	}

	private int createBoxConstraints(int[][] matrix, int header) {
		for (int row = COVER_START_INDEX; row <= SIZE; row += BOX_SIZE) {
			for (int column = COVER_START_INDEX; column <= SIZE; column += BOX_SIZE) {
				for (int n = COVER_START_INDEX; n <= SIZE; n++, header++) {
					for (int rowDelta = 0; rowDelta < BOX_SIZE; rowDelta++) {
						for (int columnDelta = 0; columnDelta < BOX_SIZE; columnDelta++) {
							int index = indexInCoverMatrix(row + rowDelta, column + columnDelta, n);
							matrix[index][header] = 1;
						}
					}
				}
			}
		}

		return header;
	}

	private int createColumnConstraints(int[][] matrix, int header) {
		for (int column = COVER_START_INDEX; column <= SIZE; column++) {
			for (int n = COVER_START_INDEX; n <= SIZE; n++, header++) {
				for (int row = COVER_START_INDEX; row <= SIZE; row++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int createRowConstraints(int[][] matrix, int header) {
		for (int row = COVER_START_INDEX; row <= SIZE; row++) {
			for (int n = COVER_START_INDEX; n <= SIZE; n++, header++) {
				for (int column = COVER_START_INDEX; column <= SIZE; column++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int createCellConstraints(int[][] matrix, int header) {
		for (int row = COVER_START_INDEX; row <= SIZE; row++) {
			for (int column = COVER_START_INDEX; column <= SIZE; column++, header++) {
				for (int n = COVER_START_INDEX; n <= SIZE; n++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	// Converting Sudoku grid as a cover matrix
	private int[][] convertInCoverMatrix(int[][] grid) {
		int[][] coverMatrix = createCoverMatrix();

		// Taking into account the values already entered in Sudoku's grid instance
		for (int row = COVER_START_INDEX; row <= SIZE; row++) {
			for (int column = COVER_START_INDEX; column <= SIZE; column++) {
				int n = grid[row - 1][column - 1];

				if (n != EMPTY_CELL) {
					for (int num = MIN_VALUE; num <= MAX_VALUE; num++) {
						if (num != n) {
							Arrays.fill(coverMatrix[indexInCoverMatrix(row, column, num)], 0);
						}
					}
				}
			}
		}

		return coverMatrix;
	}

	protected int[][] getCoverMatrix(StdSudokuGrid stdgrid){

		initConstants(stdgrid);
		return convertInCoverMatrix(stdgrid.grid);
	}
} // end of class StdSudokuSolver
