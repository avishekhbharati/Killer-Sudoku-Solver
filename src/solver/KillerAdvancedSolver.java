/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import grid.KillerSudokuGrid;
import grid.SudokuGrid;
import solver.KillerAdvancedSolver.DancingLinkHelper.DancingNode;

/**
 * Your advanced solver for Killer Sudoku.
 */
public class KillerAdvancedSolver extends KillerSudokuSolver {
	// TODO: Add attributes as needed.

	public KillerAdvancedSolver() {
	} // end of KillerAdvancedSolver()

	@Override
	public boolean solve(SudokuGrid grid) {

		KillerSudokuGrid killergrid = (KillerSudokuGrid) grid;

		// build cover matrix out of sudoku board
		int[][] cover = getCoverMatrix(killergrid);

		DancingLinkHelper dlx = new DancingLinkHelper(cover, new KillerSudokuHandler(killergrid.size), killergrid);

		// run a method to solve the problem
		dlx.gridSolver();

		// get the solved board.
		int[][] answer = dlx.resolvedGrid;

		// if the value in board is 0 that means it might be empty.
		if (answer == null || answer[0][0] == 0)
			return false;

		killergrid.grid = answer;
		return true;
	} // end of solve()

	// internal helper class to solve the problem using dancing link
	class DancingLinkHelper {
		KillerSudokuGrid killerGrid;

		class DancingNode {
			DancingNode left, right, up, down;
			ColumnNode C;
			int rowNumber;

			// Hooks node below the existing node
			DancingNode hookDown(DancingNode dn) {
				assert (this.C == dn.C);
				dn.down = this.down;
				dn.down.up = dn;
				dn.up = this;
				this.down = dn;
				return dn;
			}

			// Hooks node to the right
			DancingNode hookRight(DancingNode dn) {
				dn.right = this.right;
				dn.right.left = dn;
				dn.left = this;
				this.right = dn;
				return dn;
			}

			void unlinkUD() {
				this.up.down = this.down;
				this.down.up = this.up;
				updates++;
			}

			void relinkUD() {
				this.up.down = this.down.up = this;
				updates++;
			}

			public DancingNode() {
				left = right = up = down = this;
			}

			public DancingNode(ColumnNode c, int rownum) {
				this();
				C = c;
				this.rowNumber = rownum;
			}

			void unlinkLR() {
				this.left.right = this.right;
				this.right.left = this.left;
				updates++;
			}

			void relinkLR() {
				this.left.right = this.right.left = this;
				updates++;
			}
		}

		class ColumnNode extends DancingNode {
			int size; // number of ones in current column
			String name;

			public ColumnNode(String n) {
				super();
				size = 0;
				name = n;
				C = this;
			}

			// reattaches the deleted row.
			void uncover() {
				for (DancingNode i = this.up; i != this; i = i.up) {
					for (DancingNode j = i.left; j != i; j = j.left) {
						j.C.size++;
						j.relinkUD();
					}
				}
				relinkLR();
				header.size++; // not part of original
			}

			// covers the rows.i.e. delets the covered rows.
			void cover() {
				unlinkLR();
				for (DancingNode i = this.down; i != this; i = i.down) {
					for (DancingNode j = i.right; j != i; j = j.right) {
						j.unlinkUD();
						j.C.size--;
					}
				}
				header.size--; // not part of original
			}
		}

		// local variables
		private ColumnNode header;
		private int solutions = 0;
		private KillerSudokuHandler handler;
		private List<DancingNode> answer;
		public int[][] resolvedGrid;
		private int updates = 0;

		// gets the row location and value
		private int[] getRowAttribute(DancingNode dn) {
			DancingNode rcNode = dn;
			int x = dn.rowNumber;
			// get row index
			int rowIndex = x / (this.killerGrid.size * this.killerGrid.size);
			// col index
			int colIndex = (x / this.killerGrid.size) % this.killerGrid.size;
			int value = this.killerGrid.validNumbers[x % this.killerGrid.size];
			int[] returnArr = { rowIndex, colIndex, value };

			return returnArr;
		}

		// Heart of the algorithm
		private boolean solveGrid(int k) {
			if (header.right == header) { // all the columns removed
				resolvedGrid = handler.handleSudokuSolution(answer);
				return true;

			} else {

				ColumnNode c = (ColumnNode) header.right;
				c.cover();

				// loop through each row node in a column
				for (DancingNode r = c.down; r != c; r = r.down) {
					int[] v = getRowAttribute(r);
					if (killerGrid.isCagesTotalOk(v[0], v[1], v[2])) {
						killerGrid.grid[v[0]][v[1]] = v[2];
						answer.add(r);

						for (DancingNode j = r.right; j != r; j = j.right) {
							j.C.cover();
						}
						// recursive call
						solveGrid(k + 1);

						r = answer.remove(answer.size() - 1);
						killerGrid.grid[v[0]][v[1]] = 0;

						c = r.C;

						for (DancingNode j = r.left; j != r; j = j.left) {
							j.C.uncover();
						}
					}
				}
				c.uncover();
			}
			return false;
		}

		// grid is a grid of 0s and 1s to solve the exact cover for
		// returns the root column header node
		private ColumnNode makeDLXBoard(int[][] grid) {
			final int COLS = grid[0].length;
			final int ROWS = grid.length;

			ColumnNode headerNode = new ColumnNode("header");
			ArrayList<ColumnNode> columnNodes = new ArrayList<ColumnNode>();

			for (int i = 0; i < COLS; i++) {
				ColumnNode n = new ColumnNode(Integer.toString(i));
				columnNodes.add(n);
				headerNode = (ColumnNode) headerNode.hookRight(n);
			}
			headerNode = headerNode.right.C;

			// loop through all the dlx rows and parse it to the cell.
			for (int i = 0; i < ROWS; i++) {
				DancingNode prev = null;
				for (int j = 0; j < COLS; j++) {
					if (grid[i][j] == 1) {
						ColumnNode col = columnNodes.get(j);
						DancingNode newNode = new DancingNode(col, i);
						if (prev == null)
							prev = newNode;
						col.up.hookDown(newNode);
						prev = prev.hookRight(newNode);
						col.size++;
					}
				}
			}
			headerNode.size = COLS;
			return headerNode;
		}

		// cnstr
		public DancingLinkHelper(int[][] board, KillerSudokuHandler h, KillerSudokuGrid _killerGrid) {
			killerGrid = _killerGrid;
			header = makeDLXBoard(board);
			handler = h;
		}

		// calls the solution to solve grid
		public void gridSolver() {
			solutions = 0;
			updates = 0;
			answer = new LinkedList<DancingNode>();
			solveGrid(0);
		}
	}

	// class to handle the solved matrix.
	class KillerSudokuHandler {
		// default init
		int size = 9;

		public int[][] handleSudokuSolution(List<DancingNode> answer) {
			int[][] result = convertDlToBoard(answer);
			return result;
		}

		// converts dancing link to the 2D sudoku board.
		private int[][] convertDlToBoard(List<DancingNode> answer) {
			int[][] result = new int[size][size];
			for (DancingNode n : answer) {
				DancingNode rcNode = n;

				int min = Integer.parseInt(rcNode.C.name);
				for (DancingNode tmp = n.right; tmp != n; tmp = tmp.right) {
					int val = Integer.parseInt(tmp.C.name);
					if (val < min) {
						min = val;
						rcNode = tmp;
					}
				}
				int ans1 = Integer.parseInt(rcNode.C.name);
				int ans2 = Integer.parseInt(rcNode.right.C.name);
				int r = ans1 / size;
				int c = ans1 % size;
				int num = (ans2 % size) + 1;
				result[r][c] = num;
			}
			return result;
		}

		public KillerSudokuHandler(int boardSize) {
			size = boardSize;
		}
	}
} // end of class KillerAdvancedSolver
