/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import java.util.Arrays;

import grid.SudokuGrid;

/**
 * Abstract class of a Sudoku solver. Defines the interface for all solvers.
 */
public abstract class SudokuSolver {

	/**
	 * Solves the input grid. Different solvers should override this to implement
	 * different solving strategies. The solver will fill the grid.
	 *
	 * @param grid Input grid to solve. The solver will write the solution to grid.
	 *
	 * @return True if successfully solved the grid; otherwise false if there are no
	 *         solutions found.
	 */
	public abstract boolean solve(SudokuGrid grid);

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
	protected static final int START_INDEX = 1;

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
	private int[][] buildCoverMatrix() {
		int[][] coverMatrix = new int[SIZE * SIZE * MAX_VALUE][SIZE * SIZE * CONSTRAINTS];

		int header = 0;
		header = buildCellConstraints(coverMatrix, header);
		header = buildRowConstraints(coverMatrix, header);
		header = buildColumnConstraints(coverMatrix, header);
		buildBoxConstraints(coverMatrix, header);
		return coverMatrix;
	}

	private int buildColumnConstraints(int[][] matrix, int header) {
		for (int column = START_INDEX; column <= SIZE; column++) {
			for (int n = START_INDEX; n <= SIZE; n++, header++) {
				for (int row = START_INDEX; row <= SIZE; row++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int buildRowConstraints(int[][] matrix, int header) {
		for (int row = START_INDEX; row <= SIZE; row++) {
			for (int n = START_INDEX; n <= SIZE; n++, header++) {
				for (int column = START_INDEX; column <= SIZE; column++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	private int buildBoxConstraints(int[][] matrix, int header) {
		for (int row = START_INDEX; row <= SIZE; row += BOX_SIZE) {
			for (int column = START_INDEX; column <= SIZE; column += BOX_SIZE) {
				for (int n = START_INDEX; n <= SIZE; n++, header++) {
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

	private int buildCellConstraints(int[][] matrix, int header) {
		for (int row = START_INDEX; row <= SIZE; row++) {
			for (int column = START_INDEX; column <= SIZE; column++, header++) {
				for (int n = START_INDEX; n <= SIZE; n++) {
					int index = indexInCoverMatrix(row, column, n);
					matrix[index][header] = 1;
				}
			}
		}

		return header;
	}

	// Converting Sudoku grid as a cover matrix
	protected int[][] convertInCoverMatrix(int[][] grid) {
		int[][] coverMatrix = buildCoverMatrix();

		// Taking into account the values already entered in Sudoku's grid instance
		for (int row = START_INDEX; row <= SIZE; row++) {
			for (int column = START_INDEX; column <= SIZE; column++) {
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
} // end of class SudokuSolver
