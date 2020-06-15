///*
// * @author Jeffrey Chan & Minyi Li, RMIT 2020
// */
package solver;

import java.util.ArrayList;
import java.util.List;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Algorithm X solver for standard Sudoku.
 */

public class AlgorXSolver extends StdSudokuSolver {
	final int MATRIX_INDEX_CELL = 0;
	final int MATRIX_INDEX_ROW = 1;
	final int MATRIX_INDEX_COLUMNS = 2;
	final int MATRIX_INDEX_BOX = 3;

	public AlgorXSolver() {
	} // end of AlgorXSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		StdSudokuGrid stdgrid = (StdSudokuGrid) grid;
		// Generate tables for the size of the grid with options for each
		BinaryMatrix binaryMatrix = new BinaryMatrix(stdgrid.grid.length);
		binaryMatrix.generateMatrixConstraints(stdgrid.size);
		cutbackMatrixConstraints(binaryMatrix, stdgrid.size, stdgrid);
		BinaryMatrix foundSolution = algorithmx(binaryMatrix, 0);

		if (foundSolution != null) {
			matrixToGrid(foundSolution, stdgrid);
			return true;
		}
		return false;
	} // end of solve()

	private BinaryMatrix algorithmx(BinaryMatrix bm, int columnIndex) {

		// store valid rows
		ArrayList<ArrayList<Boolean>> validRows = bm.getColumnsWhereSet(columnIndex);
		columnIndex += 1;
		BinaryMatrix found = null;

		for (ArrayList<Boolean> row : validRows) {
			BinaryMatrix newbm = new BinaryMatrix(bm, bm.size);
			newbm.removeSimilar(row);

			if (bm.isComplete()) {

				if (bm.checkValidity()) {
					return newbm;
				}
			}

			found = algorithmx(newbm, columnIndex);

			if (found != null) {
				break;
			}
		}

		return found;
	}

	// minimizes the matrix to have valid constraints and removes the duplicates.
	private void cutbackMatrixConstraints(BinaryMatrix constraints, int size, StdSudokuGrid grid) {
		int box_width = (int) Math.sqrt(size);

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				if (grid.grid[y][x] != 0) {
					ArrayList<Boolean> toKeep = new ArrayList<>(size * size * 4);
					int value = grid.grid[y][x];

					for (int i = 0; i < size * size * 4; i++) {
						toKeep.add(Boolean.FALSE);
					}

					// Row Column
					int temp = (x % size) + (y * size);
					toKeep.set(temp, Boolean.TRUE);
					// Row Val
					temp = ((value - 1) % size) + (y * size);
					temp += (size * size);
					toKeep.set(temp, Boolean.TRUE);
					// Col Val

					temp = (value - 1) + (x * size);
					temp += (2 * size * size);
					toKeep.set(temp, Boolean.TRUE);
					// Block Val

					temp = (value - 1) % size;
					int multiplier = x / box_width;
					temp += multiplier * size;

					int temp_y = y;

					while (temp_y - box_width >= 0) {
						temp_y -= box_width;
						temp += (size * box_width);
					}

					temp += (3 * size * size);
					toKeep.set(temp, Boolean.TRUE);
					constraints.removeSimilar(toKeep);
				}
			}
		}
	}

	// returns the string representatuon of the matrix.
	private String printLine(ArrayList<Boolean> binaryMatrix, int size) {
		String temp = "";

		for (int x = 0; x < binaryMatrix.size(); x++) {
			if (x != 0) {
				temp += ",";
			}

			if ((x % (size * size)) == 0 && x != 0) {
				temp += " ";
			}

			temp += (binaryMatrix.get(x) ? "1" : "0");
		}

		return temp;
	}

	// converts binary matrix ot sudoku grid
	private void matrixToGrid(BinaryMatrix binaryMatrix, StdSudokuGrid grid) {
		int size = binaryMatrix.size;
		int column = 0;

		for (int i = 0; i < binaryMatrix.binaryMatrix.size(); i++) {
			for (int j = (size * size); j < (2 * size * size); j++) {
				if (binaryMatrix.binaryMatrix.get(i).get(j) == Boolean.TRUE) {
					int number = (j % size) + 1;
					int row = i % size;

					if ((i % size == 0) && i != 0) {
						column += 1;
					}

					grid.grid[row][column] = number;
				}
			}
		}
	}

	private class BinaryMatrix {
		private ArrayList<ArrayList<Boolean>> binaryMatrix;
		private int size;

		public BinaryMatrix(int size) {
			this.size = size;

			this.binaryMatrix = new ArrayList<>(size * size * size);

			for (int i = 0; i < (size * size * size); i++) {
				// 4 - Number Constraints
				ArrayList<Boolean> row = new ArrayList<Boolean>(size * size * 4);

				for (int j = 0; j < (size * size * 4); j++) {
					row.add(Boolean.FALSE);
				}

				binaryMatrix.add(row);
			}
		}

		public BinaryMatrix(BinaryMatrix bm, int size) {
			this.size = size;
			this.binaryMatrix = new ArrayList<>(bm.binaryMatrix.size());

			for (int i = 0; i < (bm.binaryMatrix.size()); i++) {
				// 4 - Number Constraints
				ArrayList<Boolean> row = new ArrayList<Boolean>(size * size * 4);
				ArrayList<Boolean> oldRow = bm.binaryMatrix.get(i);

				for (int j = 0; j < (size * size * 4); j++) {
					row.add(oldRow.get(j));
				}

				binaryMatrix.add(row);
			}
		}

		public void add(int x, int y, boolean value, int matrix_id) {
			int matrix_offset = matrix_id * (this.size * this.size);
			List row = binaryMatrix.get(y);
			row.set(x + matrix_offset, value ? Boolean.TRUE : Boolean.FALSE);
		}

		public void generateMatrixConstraints(int size) {
			int row_val_offset = 0;
			int box_offset = 0;
			int new_column_box_offset = 0;
			int box_pos = 0;
			int box_width = (int) Math.sqrt(size);

			// Finish expanding this for all possible values
			for (int y = 0; y < size * size * size; y++) {
				if (y != 0) {
					// Go the box ->
					if (y % (box_width * size) == 0) {
						box_offset += size;
					}

					// Reset box but don't go to new column box
					if (box_offset % (box_width * size) == 0) {
						box_offset = 0;
					}

					// Go to new column box
					if (y % (size * size * box_width) == 0) {
						new_column_box_offset += (size * box_width);
					}
				}

				if (y % (size * size) == 0 && y != 0) {
					row_val_offset += size;
				}

				int rc_x_pos = y / size;
				int row_val_x_pos = (y % size) + row_val_offset;
				int col_val_x_pos = y % (size * size);
				int box_val_x_pos = (y % size) + box_offset + new_column_box_offset;

				// cell constraints
				this.add(rc_x_pos, y, true, MATRIX_INDEX_CELL);
				// Row Value
				this.add(row_val_x_pos, y, true, MATRIX_INDEX_ROW);
				// Column Value
				this.add(col_val_x_pos, y, true, MATRIX_INDEX_COLUMNS);
				// Box Value
				this.add(box_val_x_pos, y, true, MATRIX_INDEX_BOX);
			}
		}

		// converts binary matrix to string representation.
		private String matrixToString(ArrayList<Boolean> binaryMatrix) {
			String temp = "";

			for (int x = 0; x < binaryMatrix.size(); x++) {
				if (x != 0) {
					temp += ",";
				}

				if (x % (size * size) == 0 && x != 0) {
					temp += " ";
				}

				temp += (binaryMatrix.get(x) ? "1" : "0");
			}

			return temp;
		}

		// Check if the matrix has been build to its required size.
		public boolean isComplete() {
			if (binaryMatrix.size() <= (this.size * this.size)) {
				return true;
			}

			return false;
		}

		public ArrayList<ArrayList<Boolean>> getColumnsWhereSet(int columnIndex) {
			ArrayList<ArrayList<Boolean>> rows = new ArrayList<>();

			for (ArrayList<Boolean> row : this.binaryMatrix) {
				if (row.get(columnIndex) == Boolean.TRUE) {
					rows.add(row);
				}
			}

			return rows;
		}

		// removes the duplicate row.
		public void removeSimilar(ArrayList<Boolean> rows) {
			ArrayList<ArrayList<Boolean>> toRemove = new ArrayList<>();
			for (ArrayList<Boolean> comparisonRow : this.binaryMatrix) {
				for (int i = 0; i < comparisonRow.size(); i++) {
					if (comparisonRow.get(i) == Boolean.TRUE && rows.get(i) == Boolean.TRUE) {
						if (!comparisonRow.equals(rows)) {
							toRemove.add(comparisonRow);
							break;
						}
					}
				}
			}
			this.binaryMatrix.removeAll(toRemove);
		}

		// check the validity of the row.
		public boolean checkValidity() {
			ArrayList<Boolean> hasVal = new ArrayList<>(size * size * 4);

			for (int i = 0; i < this.size * this.size * 4; i++) {
				hasVal.add(Boolean.FALSE);
			}

			for (int index = 0; index < this.binaryMatrix.size(); index++) {
				ArrayList<Boolean> row = this.binaryMatrix.get(index);

				for (int i = 0; i < row.size(); i++) {
					if (row.get(i) == Boolean.TRUE) {
						if (hasVal.get(i) == Boolean.TRUE) {
							return false;
						} else {
							hasVal.set(i, Boolean.TRUE);
						}
					}
				}
			}

			for (int i = 0; i < hasVal.size(); i++) {
				if (hasVal.get(i) == Boolean.FALSE) {
					return false;
				}
			}
			return true;
		}
	}

}
