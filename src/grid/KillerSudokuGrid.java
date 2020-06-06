/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class implementing the grid for Killer Sudoku. Extends SudokuGrid (hence
 * implements all abstract methods in that abstract class). You will need to
 * complete the implementation for this for task E and subsequently use it to
 * complete the other classes. See the comments in SudokuGrid to understand what
 * each overriden method is aiming to do (and hence what you should aim for in
 * your implementation).
 */
public class KillerSudokuGrid extends SudokuGrid {
	private int[][] grid;
	private int size;
	private int noOfCages;
	private int[] validNumbers;
	private ArrayList<CagePair> cagePairList = new ArrayList<CagePair>();

	public KillerSudokuGrid() {
		super();

		// TODO: any necessary initialisation at the constructor
	} // end of KillerSudokuGrid()

	/* ********************************************************* */

	@Override
	public void initGrid(String filename) throws FileNotFoundException, IOException {
		File myObj = new File("easy-killer-44-01.in");
		Scanner myReader = new Scanner(myObj);
		int lineCount = 0;

		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();

			if (lineCount == 0) {
				size = Integer.parseInt(data);
				grid = new int[size][size];
			} else if (lineCount == 1) {
				String validNumsStr[] = data.split(" ");
				int arrLength = validNumsStr.length;
				validNumbers = new int[arrLength];

				for (int i = 0; i < arrLength; i++) {
					validNumbers[i] = Integer.parseInt(validNumsStr[i]);
				}
			} else if (lineCount == 2) {
				noOfCages = Integer.parseInt(data);
			} else {
				String[] arr = data.split(" ");
				int arrlen = arr.length;

				int total = Integer.parseInt(arr[0].split(",")[0]);

				// initalize CagePair
				CagePair cp = new CagePair(total);

				// skip the 0th index as it has already been processed
				for (int i = 1; i < arrlen; i++) {
					int row = Integer.parseInt(arr[i].split(",")[0]);
					int col = Integer.parseInt(arr[i].split(",")[1]);

					cp.addCell(new Cell(row, col));
				}
				cagePairList.add(cp);
			}
			lineCount++;
		}
		myReader.close();

	} // end of initBoard()

	@Override
	public void outputGrid(String filename) throws FileNotFoundException, IOException {
		try {
			File myObj = new File(filename);

			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			}

			FileWriter myWriter = new FileWriter(filename);

			myWriter.write(this.toString());
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	} // end of outputBoard()

	@Override
	public String toString() {
		int gridLen = grid.length;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < gridLen; i++) {
			for (int j = 0; j < gridLen; j++) {
				if (j == gridLen - 1)
					sb.append(grid[i][j]);
				else
					sb.append(grid[i][j] + ",");
			}
			sb.append('\n');
		}
		return sb.toString();
	} // end of toString()

	// might have to check if it fails for 0
	@Override
	public boolean validate() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (!isOk(i, j, grid[i][j]))
					return false;
			}
		}
		return true;
	} // end of validate()

	// combined methods to check the constraints
	private boolean isOk(int row, int col, int number) {
		if (checkNumberIsValid(number) && isInRow(row, number) == 1 && isInCol(col, number) == 1
				&& isInBox(row, col, number) == 1 && isCagesTotalOk(row, col, number)) {
			return true;
		}
		return false;
	}

	// checks if the given number to insert is in the list of valid numbers.
	private boolean checkNumberIsValid(int number) {

		for (int n : validNumbers) {
			if (n == number)
				return true;
		}
		return false;
	}

	// check if the number is in that row
	private int isInRow(int row, int number) {
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (grid[row][i] == number)
				count++;
		}
		return count;
	}

	// check if the number is in that column.
	private int isInCol(int col, int number) {
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (grid[i][col] == number)
				count++;
		}
		return count;
	}

	// check if the possible number is in the box.
	private int isInBox(int row, int col, int number) {
		int boxSize = (int) Math.sqrt(size);
		int r = row - row % boxSize;
		int c = col - col % boxSize;

		int count = 0;

		for (int i = r; i < r + boxSize; i++) {

			for (int j = c; j < c + boxSize; j++) {
				if (grid[i][j] == number)
					count++;
			}

		}
		return count;
	}

//    //checks if the given number to insert is in the list of valid numbers.
//    private boolean checkNumberIsValid(int number) {
//    	
//    	for (int n : validNumbers) {
//			if (n == number)
//				return true;				
//		}
//    	return false;
//    }
//    
//    
//    //check if the number is in that row
//    private boolean isInRow(int row, int number) {
//    	for (int i = 0; i < size; i++) {
//			if (grid[row][i] == number)
//				return true;
//		}
//    	return false;
//    }
//    
//    
//    //check if the number is in that column.
//    private boolean isInCol(int col, int number) {
//    	for (int i = 0; i < size; i++) {
//			if (grid[i][col] == number)
//				return true;
//		}
//    	return false;
//    }
//    
//    
//    //check if the possible number is in the box.
//    private boolean isInBox(int row, int col, int number) {
//    	int boxSize = (int) Math.sqrt(size);
//    	int r = row - row % boxSize;
//    	int c = row - col  % boxSize;
//    	
//    	for (int i = r; i < r + boxSize; i++) {
//    		for (int j = c; j < c + boxSize; j++) {
//				if (grid[i][j] == number)
//					return true;
//			}				
//		}        	
//    	return false;  			
//    }  

	// Pair class to store cages location and total
	protected static class CagePair {
		public int total;
		public ArrayList<Cell> cells;

		public CagePair(int _total) {
			this.total = _total;
			this.cells = new ArrayList<Cell>();
		}

		public void addCell(Cell cell) {
			cells.add(cell);
		}
	}

	// Class to store cell location. row and column
	protected static class Cell {
		public int row;
		public int col;

		public Cell(int _row, int _col) {
			this.row = _row;
			this.col = _col;
		}
	}

	private boolean isCagesTotalOk(int row, int col, int number) {
		boolean isCellFound = false;

		for (CagePair cagePair : cagePairList) {
			ArrayList<Cell> cells = cagePair.cells;

			// the expected total of the cage pairs.
			int cellsExpectedTotal = cagePair.total;

			// stores the total values of cage pairs.
			int cellsCurrentTotal = 0;
			boolean isCagesPartiallyFilled = false;

			for (Cell cell : cells) {
				if (cell.row == row && cell.col == col) {
					cellsCurrentTotal += number;
					isCellFound = true;
				} else {
					// check if any cell's value is 0.
					int cellValue = grid[cell.row][cell.col];
					if (cellValue == 0)
						isCagesPartiallyFilled = true;

					cellsCurrentTotal += cellValue;
				}
			}

			// check if the existing cell satisfies the constraint.
			// less is when all the cages are partially filled
			if (isCellFound) {
				if (isCagesPartiallyFilled)
					return cellsCurrentTotal < cellsExpectedTotal;

				// else should always satisfy the constraint.
				return cellsCurrentTotal == cellsExpectedTotal;
			}
		}

		return false;
	}
} // end of class KillerSudokuGrid
