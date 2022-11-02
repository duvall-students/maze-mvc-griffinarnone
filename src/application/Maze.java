package application;

import java.awt.Point;

/*
 * @author Shannon Duvall
 * 
 * 
 * This class represents the Maze.  A maze is a grid of integers, where each
 * integer represents a "state" of a square in the maze.  Each square is either a 
 * wall or a hall.  If it is a hall, it may be empty (undiscovered), visited (discovered and abandoned)
 * or path (discovered and actively being explored.)
 * 
 * Maze squares are accessed by their point in (row, column) order
 */
public class Maze {
	// Possible states of squares that make up a maze
	public static final int WALL = 0;	
	public static final int PATH = 1;		// A hall space that is either the current spot being 
	// explored or a spot that has been determined to be in 
	// the computed path
	public static final int EMPTY = 2;		// Basic hall space
	public static final int VISITED = 3;	// A hall space that was visited and discarded

	private int[][] maze;	// The squares making up the maze

	public Maze(int rows, int columns){
		assert(rows > 0 && columns > 0);
		createMaze(rows, columns);
	}

	public int getNumRows(){
		assert(maze!=null);
		return maze.length;
	}

	public int getNumCols(){
		assert(maze!=null);
		return maze[0].length;
	}

	/*
	 * Check to see if the square is inside the outer walls of the maze
	 */
	public boolean inBounds(Point p){
		assert(maze!=null);
		return (p!= null && p.x < maze.length-1 && p.x > 0 && p.y < maze[0].length-1 && p.y >0);
	}

	/*
	 * Check to see if the point is in bounds (won't cause out-of-bounds or null errors)
	 */
	public boolean validPoint(Point p){
		assert(maze!=null);
		return (p!=null && p.x < maze.length && p.x >= 0 && p.y < maze[0].length && p.y >= 0);
	}

	/*
	 * get - returns a square state at the given position.
	 */
	public int get(Point square){
		assert(validPoint(square));
		return maze[square.x][square.y];
	}

	/*
	 * markPath - turns a square into a "path".  
	 */
	public void markPath(Point square){
		assert(validPoint(square));
		maze[square.x][square.y] = PATH;
	}

	/*
	 * markVisited - makes a square in the maze "visited" 
	 * Note -will not work if the square is a wall.
	 */
	public void markVisited(Point square){
		assert(inBounds(square));
		if(maze[square.x][square.y]!= WALL){
			maze[square.x][square.y] = VISITED;
		}
	}

	/*
	 * This method "resets" the maze by erasing the "Path" and "visited"
	 * squares, leaving only walls and empty halls.
	 */
	public void reColorMaze(){
		assert(maze != null);
		for(int i = 0; i< maze.length; i++){
			for(int j =0; j < maze[i].length; j++){
				if(maze[i][j] == PATH || maze[i][j] == VISITED){
					maze[i][j] = EMPTY;
				}

			}
		}
	}
	/* 
	 * 
	 * 
	 * 
	 * Remaining code is from "Introduction to Programming Using Java" by Eck.
	 *
	 *
	 *
	 */
	/*
	 * Create a new random maze of the given dimensions and store the result.
	 * Maze has no cycles.
	 */

	public void createMaze(int rows, int cols) {
		assert(rows > 0 && cols > 0);
		maze = new int[rows][cols];
		// Create a random maze.  The strategy is to start with
		// a grid of disconnected "rooms" separated by walls,
		// then look at each of the separating walls, in a random
		// order.  If tearing down a wall would not create a loop
		// in the maze, then tear it down.  Otherwise, leave it in place.
		int i,j;
		int emptyCt = 0; // number of rooms
		int wallCt = 0;  // number of walls
		int[] wallrow = new int[(rows*cols)/2];  // position of walls between rooms
		int[] wallcol = new int[(rows*cols)/2];
		for (i = 0; i<rows; i++)  // start with everything being a wall
			for (j = 0; j < cols; j++)
				maze[i][j] = WALL;
		for (i = 1; i<rows-1; i += 2)  { // make a grid of empty rooms
			for (j = 1; j<cols-1; j += 2) {
				emptyCt++;
				maze[i][j] = -emptyCt;  // each room is represented by a different negative number
				if (i < rows-2) {  // record info about wall below this room
					wallrow[wallCt] = i+1;
					wallcol[wallCt] = j;
					wallCt++;
				}
				if (j < cols-2) {  // record info about wall to right of this room
					wallrow[wallCt] = i;
					wallcol[wallCt] = j+1;
					wallCt++;
				}
			}
		}
		int r;
		for (i=wallCt-1; i>0; i--) {
			r = (int)(Math.random() * i);  // choose a wall randomly and maybe tear it down
			tearDown(wallrow[r],wallcol[r]);
			wallrow[r] = wallrow[i];
			wallcol[r] = wallcol[i];
		}
		for (i=1; i<rows-1; i++)  // replace negative values in maze[][] with emptyCode
			for (j=1; j<cols-1; j++)
				if (maze[i][j] < 0)
					maze[i][j] = EMPTY;
	}

	/*
	 * Subroutine of creating a new random maze.
	 * This method removes a wall, unless doing so makes a cycle in the maze.
	 */
	private void tearDown(int row, int col) {
		// Tear down a wall, unless doing so will form a loop.  Tearing down a wall
		// joins two "rooms" into one "room".  (Rooms begin to look like corridors
		// as they grow.)  When a wall is torn down, the room codes on one side are
		// converted to match those on the other side, so all the cells in a room
		// have the same code.  Note that if the room codes on both sides of a
		// wall already have the same code, then tearing down that wall would 
		// create a loop, so the wall is left in place.
		if (row % 2 == 1 && maze[row][col-1] != maze[row][col+1]) {
			// row is odd; wall separates rooms horizontally
			fill(row, col-1, maze[row][col-1], maze[row][col+1]);
			maze[row][col] = maze[row][col+1];
		}
		else if (row % 2 == 0 && maze[row-1][col] != maze[row+1][col]) {
			// row is even; wall separates rooms vertically
			fill(row-1, col, maze[row-1][col], maze[row+1][col]);
			maze[row][col] = maze[row+1][col];
		}
	}

	/*
	 * Subroutine of creating a new random maze.
	 * This method joins "rooms" that were previously separate, once a wall is torn down.
	 */
	private void fill(int row, int col, int replace, int replaceWith) {
		// called by tearDown() to change "room codes".
		if (maze[row][col] == replace) {
			maze[row][col] = replaceWith;
			fill(row+1,col,replace,replaceWith);
			fill(row-1,col,replace,replaceWith);
			fill(row,col+1,replace,replaceWith);
			fill(row,col-1,replace,replaceWith);
		}
	}
}


