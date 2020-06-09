/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import grid.StdSudokuGrid;
import grid.SudokuGrid;
import solver.DancingLinksSolver.DancingLinks.DancingNode;

/**
 * Dancing links solver for standard Sudoku.
 */
public class DancingLinksSolver extends StdSudokuSolver {

	public DancingLinksSolver() {
	} // end of DancingLinksSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		StdSudokuGrid stdgrid = (StdSudokuGrid) grid;

		//build cover matrix out of sudoku board
		int[][] cover = getCoverMatrix(stdgrid);

		DancingLinks dlx = new DancingLinks(cover, new SudokuHandler(stdgrid.size));

		//run a method to solve the problem
		dlx.runSolver();

		//get the solved board.
		int[][] answer = dlx.resolvedGrid;

		//if the value in board is 0 that means it might be empty.
		if ( answer == null || answer[0][0] == 0)
			return false;
		
		stdgrid.grid = answer;
		return true;
	} // end of solve()


	//Internal class
	public class DancingLinks {

		static final boolean verbose = true;

		class DancingNode {
			DancingNode L, R, U, D;
			ColumnNode C;

			// hooks node n1 `below` current node
			DancingNode hookDown(DancingNode n1) {
				assert (this.C == n1.C);
				n1.D = this.D;
				n1.D.U = n1;
				n1.U = this;
				this.D = n1;
				return n1;
			}

			// hooke a node n1 to the right of `this` node
			DancingNode hookRight(DancingNode n1) {
				n1.R = this.R;
				n1.R.L = n1;
				n1.L = this;
				this.R = n1;
				return n1;
			}

			void unlinkLR() {
				this.L.R = this.R;
				this.R.L = this.L;
				updates++;
			}

			void relinkLR() {
				this.L.R = this.R.L = this;
				updates++;
			}

			void unlinkUD() {
				this.U.D = this.D;
				this.D.U = this.U;
				updates++;
			}

			void relinkUD() {
				this.U.D = this.D.U = this;
				updates++;
			}

			public DancingNode() {
				L = R = U = D = this;
			}

			public DancingNode(ColumnNode c) {
				this();
				C = c;
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

			void cover() {
				unlinkLR();
				for (DancingNode i = this.D; i != this; i = i.D) {
					for (DancingNode j = i.R; j != i; j = j.R) {
						j.unlinkUD();
						j.C.size--;
					}
				}
				header.size--; // not part of original
			}

			void uncover() {
				for (DancingNode i = this.U; i != this; i = i.U) {
					for (DancingNode j = i.L; j != i; j = j.L) {
						j.C.size++;
						j.relinkUD();
					}
				}
				relinkLR();
				header.size++; // not part of original
			}
		}

		private ColumnNode header;
		private int solutions = 0;
		private int updates = 0;
		private SudokuHandler handler;
		private List<DancingNode> answer;
		public int[][] resolvedGrid;

		// Heart of the algorithm
		private void search(int k) {
			if (header.R == header) { // all the columns removed
				
				resolvedGrid =  handler.handleSolution(answer);				
				
			} else {
			
				ColumnNode c = selectColumnNodeHeuristic();
				c.cover();

				for (DancingNode r = c.D; r != c; r = r.D) {
					answer.add(r);

					for (DancingNode j = r.R; j != r; j = j.R) {
						j.C.cover();
					}

					search(k + 1);

					r = answer.remove(answer.size() - 1);
					c = r.C;

					for (DancingNode j = r.L; j != r; j = j.L) {
						j.C.uncover();
					}
				}
				c.uncover();
			}
		}

		private ColumnNode selectColumnNodeHeuristic() {
			int min = Integer.MAX_VALUE;
			ColumnNode ret = null;
			for (ColumnNode c = (ColumnNode) header.R; c != header; c = (ColumnNode) c.R) {
				if (c.size < min) {
					min = c.size;
					ret = c;
				}
			}
			return ret;
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
			headerNode = headerNode.R.C;

			for (int i = 0; i < ROWS; i++) {
				DancingNode prev = null;
				for (int j = 0; j < COLS; j++) {
					if (grid[i][j] == 1) {
						ColumnNode col = columnNodes.get(j);
						DancingNode newNode = new DancingNode(col);
						if (prev == null)
							prev = newNode;
						col.U.hookDown(newNode);
						prev = prev.hookRight(newNode);
						col.size++;
					}
				}
			}

			headerNode.size = COLS;

			return headerNode;
		}

		public DancingLinks(int[][] grid, SudokuHandler h) {
			header = makeDLXBoard(grid);
			handler = h;
		}

		public void runSolver() {
			solutions = 0;
			updates = 0;
			answer = new LinkedList<DancingNode>();
			search(0);
		}

	}


	//class to handle the solved matrix.
	class SudokuHandler{
		int size = 9;

		public int[][] handleSolution(List<DancingNode> answer) {
			int[][] result = parseBoard(answer);
			return result;
		}

		private int[][] parseBoard(List<DancingNode> answer) {
			int[][] result = new int[size][size];
			for (DancingNode n : answer) {
				DancingNode rcNode = n;
				int min = Integer.parseInt(rcNode.C.name);
				for (DancingNode tmp = n.R; tmp != n; tmp = tmp.R) {
					int val = Integer.parseInt(tmp.C.name);
					if (val < min) {
						min = val;
						rcNode = tmp;
					}
				}
				int ans1 = Integer.parseInt(rcNode.C.name);
				int ans2 = Integer.parseInt(rcNode.R.C.name);
				int r = ans1 / size;
				int c = ans1 % size;
				int num = (ans2 % size) + 1;
				result[r][c] = num;
			}
			return result;
		}

		public SudokuHandler(int boardSize) {
			size = boardSize;
		}
	}
} // end of class DancingLinksSolver
