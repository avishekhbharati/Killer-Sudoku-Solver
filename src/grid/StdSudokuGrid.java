/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class implementing the grid for standard Sudoku. Extends SudokuGrid (hence
 * implements all abstract methods in that abstract class). You will need to
 * complete the implementation for this for task A and subsequently use it to
 * complete the other classes. See the comments in SudokuGrid to understand what
 * each overriden method is aiming to do (and hence what you should aim for in
 * your implementation).
 */
public class StdSudokuGrid extends SudokuGrid {
	// TODO: Add your own attributes
	public int[][] grid;
	public int size = 0;
	public int[] validNumbers;

	public StdSudokuGrid() {
		super();
	} // end of StdSudokuGrid()

	/* ********************************************************* */

	@Override
	public void initGrid(String filename) throws FileNotFoundException, IOException {
		File myObj = new File(filename);
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

				// get the list of valid inputs
				for (int i = 0; i < arrLength; i++) {
					validNumbers[i] = Integer.parseInt(validNumsStr[i]);
				}
			} else {
				String[] arr = data.split(" ");
				int row = Integer.parseInt(arr[0].split(",")[0]);
				int col = Integer.parseInt(arr[0].split(",")[1]);

				// add the number to the respective location.
				grid[row][col] = Integer.parseInt(arr[1]);
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
				if (isOkAfterInsert(i, j, grid[i][j]))
					return true;
			}
		}
		return false;
	} // end of validate()

	// combined methods to check the constraints after inserting a value.
	private boolean isOkAfterInsert(int row, int col, int number) {
		if (checkNumberIsValid(number) && isInRow(row, number) == 1 && isInCol(col, number) == 1
				&& isInBox(row, col, number) == 1) {
			return true;
		}
		return false;
	}

	// combined methods to check the constarints before inserting a value
	public boolean isOkToInsert(int row, int col, int number) {
		if (checkNumberIsValid(number) && isInRow(row, number) == 0 && isInCol(col, number) == 0
				&& isInBox(row, col, number) == 0) {
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

} // end of class StdSudokuGrid
