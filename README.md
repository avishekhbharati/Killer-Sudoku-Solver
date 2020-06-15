
# Advanced Killer Sudoku Solver Algorithm

## Introduction
Sudoku was a game first popularised in Japan in the 80s but dates back to the 18th
century and the \Latin Square" game. The aim of Sudoku is to place numbers from 1
to 9 in cells of a 9 by 9 grid, such that in each row, column and 3 by 3 block/box all
9 digits are present. Typical Sudoku puzzle will have some of the cells initially filled in
with digits and a well designed game will have one unique solution. In this assignment
you will implement algorithms that can solve puzzles of Sudoku and its variants.

  ### Sudoku
  Sudoku puzzles are typically played on a 9 by 9 grid, where each cell can be filled in
  with discrete values from 1-9. Sudoku puzzles have some of these cells pre-filled with
  values, and the aim is to fill in all the remaining cells with values that would form a valid
  solution. A valid solution (for a 9 by 9 grid with values 1-9) needs to satisfy a number of
  constraints:
    1. Every cell is assigned a value between 1 to 9.
    2. Every row contains 9 unique values from 1 to 9.
    3. Every column contains 9 unique values from 1 to 9.
    4. Every 3 by 3 block (called a box) contains 9 unique values from 1 to 9.

  ### Killer Sudoku
  Killer Sudoku puzzles are typically played on 9 by 9 grids also and have many elements
  of Sudoku puzzles, including all of its constraints. It additionally has cages, which are
  subset of cells that have a total assigned to them. A valid Killer Sudoku must also satisfy
  the constraint that the values assigned to a cage are unique and add up to the total.
  Formally, a valid solution for a Killer Sudoku of 9 by 9 grid and 1-9 as values needs
  to satisfy all of the following constraints (the first 4 are the same as standard Sudoku):
    1. Every cell is assigned a value between 1 to 9.
    2. Every row contains 9 unique values from 1 to 9.
    3. Every column contains 9 unique values from 1 to 9.
    4. Every 3 by 3 block/box contains 9 unique values from 1 to 9.
    5. The sum of values in the cells of each cage must be equal to the cage target total
    and all the values in a cage must be unique.    

## Tasks
The task is broken up into a number of tasks. Apart from Task A that should be
completed initially, all other tasks can be completed in an order you are more comfortable
with, but we have ordered them according to what we perceive to be their dificulty. Task
E is considered a high distinction task and hence we suggest to tackle this after you have
completed the other tasks.

### Task A: Implement Sudoku Grid
Implement the grid representation, including reading in from file and outputting a solved
grid to an output file. Note we will use the output file to evaluate the correctness of your
implementations and algorithms.
A typically Sudoku puzzle is played on a 9 by 9 grid, but there are 4 by 4, 16 by 16,
25 by 25 and larger. In this task and subsequent tasks, your implementation should be
able to represent and solve Sudoku and variants of any valid sizes, e.g., 4 by 4 and above.
You won't get a grid size that isn't a perfect square, e.g., 7 by 7 is not a valid grid size,
and all puzzles will be square in shape.
In addition, the values/symbols of the puzzles may not be sequential digits, e.g., 1-9
for a 9 by 9 grid, but could be any set of 9 unique non-negative integer digits. The
same Sudoku rules and constraints still hold for non-standard set of values/symbols.
Your implementation should be able to read this in and handle any set of valid integer
values/symbols.

### Task B: Implement Backtracking Solver for Sudoku
To help to understand the problem and the challenges involved, the first task is to develop
a backtracking approach to solve Sudoku puzzles.

### Task C: Exact Cover Solver - Algorithm X
In this task, you will implement the first approaches to solve Sudoku as an exact cover
problem - Algorithm X.

### Task D: Exact Cover Solver - Dancing Links
In this task, you will implement the second of two approaches to solve Sudoku as an exact
cover problem - the Dancing Links algorithm. We suggest to attempt to understand and
implement Algorithm X first, then the Dancing Links approach.

### Task E: Killer Sudoku Solver
In this task, you will take what you have learnt from the first two tasks and devise
and implement 2 solvers for Killer Sudoku puzzles. One will be based on backtracking
and the other should be more efficient (in running time) than the backtracking one.
Your implementation will be assessed for its ability to solve Killer Sudoku puzzles of
various dificulties within reasonable time, as well as your proposed approach, which will
be detailed in a short (1-2 pages) report. We are as interested in


#### ------------------------------------REPORT------------------------------------------------------

## 1.	Introduction
  This report explains an algorithm to solve killer sudoku in much efficient way. The name Sudoku comes from Japan and consists of the Japanese character “Su”(meaning number) and “Doku”(meaning single).  It is typically played in 9x9 grid where each row and column should be filled with the provided inputs (usually 1-9) and these inputs should be non-repeating. The grid can also be of size 4 by 4, 16 by 16 or even 25 by 25. A typical sudoku board has 4 constraints - cell constraint, row constraint, column constraint and box constraint. Cell constraint verifies that validity of the input value for a cell. Row constraint ensures there is only one occurrence for each value in a row. Similarly, column constraint ensures there is only one occurrence for each value in a column. Box constraint verifies if there is only one occurrence of valid input in a box. 
  Killer sudoku is an advance version of the standard sudoku. In addition to the above specified constraints, killer sudoku has one more constraint – cage constraint. Cage in a sudoku is a group of cells whose value adds up to produce the specified cage total.

## 2.	Killer Sudoku Grid 
  The initial Killer sudoku is stored in 2D array. In an array empty values are represented by 0 and the inputs values are assumed to be positive integers. The cage values are stored in an ArrayList of CagePair object. CagePair object holds properties like cage total as int and list of cells as ArrayList<Cell>. Cell class has properties – row and column, both as integer type.

## 3.	Solution - Algorithm 
Here the killer sudoku grid has been transferred to an exact cover problem and it has been tried to solve using dancing link and algorithm x. Below is the steps used in solving the killer sudoku grid using proposed algorithm. 
1.	Construct a 2D numeric array representation of initial grid. 
2.	Convert the 2D array into exact cover problem matrix.
3.	Convert the matrix into dancing links which represents the quad directional linked list. Unlike list link which is bidirectional – left and right. Dancing Nodes have Up and down direction as well.
4.	The algorithm x is used on dancing links to solve the problem. 
  
### Pseudocode
```
    if headerNode.Right = headerNode:
        return true
    else select column c from headerNode
        loop rows R in c:
            if row r satisfies the cage constraint:
                add row r to solution set
                cover all rows and columns related to row r
                if solveGrid(k+1):
                    return true
                uncover all row and columns related to row r
    return false
```

## 4.	Empirical Analysis
It is noticed that the performance of backtrack approach of solving sudoku is faster than the proposed algorithm. Although theoretical analysis landed me to the conclusion of using dancing links to solve the cover problem matrix in faster approach than backtrack, the practical implementation produced contradicting results.  

Files tested | Backtracking | Advanced
---------------------|-------------------|-----------------------
easy-killer-99-01.in	| 3.343984247 sec.	| 12.69350722 sec.
easy-killer-99-02.in	| 0.091158878 sec.	| 0.202541358 sec.
easy-killer-99-03.in	| 0.30823471 sec.	  | 0.960756156 sec.
Hard-killer-99-01.in	| 100.506359188 sec.|	372.834988605 sec

From the data above it can be concluded that the proposed solution using the dancing link and recursive algorithm lags behind the backtracking approach of solving killer sudoku by more than 3 folds.

## 5.	Further Enhancement
Despite of using dancing link approach to solve the sudoku with the reference to how quickly it was able to solve standard sudoku board, a degrading performance is noticed. Although, the initiatives have been made to improve the performance by code optimisation, I believe the performance could be increased by finding a way to add a cage constraint in the cover matrix itself.

## 6.	References
www.sudokudragon.com. (n.d.). The origin of the Sudoku Puzzle. [online] Available at: https://www.sudokudragon.com/sudokuhistory.htm#:~:text=The%20name%20Sudoku%20or%20more.

Saurel, S. (2019). Building A Sudoku Solver In Java With Dancing Links. [online] Medium. Available at: https://medium.com/javarevisited/building-a-sudoku-solver-in-java-with-dancing-links-180274b0b6c1 [Accessed 7 Jun. 2020].


#### ----------------------------------------END OF REPORT-------------------------------------------------


# Compiling and Executing
To compile the files, run the following command from the root directory (the directory that RmitSudoku.java is in):
  ```
  javac *.java grid/*.java solver/*.java
  ```
  
Note that for Windows machine, remember to replace `/' with `n'.
To run the framework:
  ```
  java RmitSudokuTester [puzzle fileName] [game type] [solver type] [visualisation] <output fileName>
  ```
