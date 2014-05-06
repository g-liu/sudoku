Sudoku solver
=============

A Sudoku solver, written in Java. My first major Java program.

## Usage

Run from command line:

	java Sudoku

This opens a GUI with which you can input your partial Sudoku solution (see next section).

### Input

You can input a number in two ways:

* Using the mouse scroll wheel, to move between 1-9
* Clicking a cell and inputting a number 1-9

### Solving

Hit `Calculate!` to solve the puzzle. This implementation is a brute-force solving algorithm, so it checks every possiblity until it finds one. The progress bar at bottom indicates the current cell that the solver is trying.

## Saving / loading puzzles

This program has basic load/save functionality. You can save your files into a `.sudoku` file, which is a text file that stores all the numbers in the Sudoku grid. A sample file might look like this:

	100007090030020008009600500005300900010080002600004000300000010040000007007000300

Numbers are mapped to the grid starting with the top-left most cell, working towards the bottom-right most cell. Zeroes represent unknown/blank values, all other digits represent the value in the cell.
