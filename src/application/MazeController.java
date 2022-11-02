package application;
import javafx.util.Duration;
import searches.BFS;
import searches.DFS;
import searches.Greedy;
import searches.Magic;
import searches.RandomWalk;

import java.awt.Point;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MazeController {

	// The maze to search
	private Maze maze;
	
	// Where to start and stop the search
	private Point start;
	private Point goal;
	
	// The search algorithms
	private Greedy greedy;				
	private BFS bfs;
	private DFS dfs;
	private RandomWalk rand;
	private Magic magic;
	private String search = "";		// This string tells which algorithm is currently chosen.  Anything other than 
									// the implemented search class names will result in no search happening.
	
	public MazeDisplay display;
	
	public MazeController(int rows, int cols, MazeDisplay mazedisplay) {
		// Initializing logic state
		int numRows = rows;
		int numColumns = cols;
		start = new Point(1,1);
		goal = new Point(numRows-2, numColumns-2);
		maze = new Maze(numRows, numColumns);
		display = mazedisplay;
	}
	
	/*
	 * Does a step in the search regardless of pause status
	 */
	public void doOneStep(double elapsedTime){
		if(search.equals("DFS")) dfs.step();
		else if (search.equals("BFS")) bfs.step();
		else if (search.equals("Greedy")) greedy.step();
		else if (search.equals("RandomWalk")) rand.step();
		else if (search.equals("Magic")) magic.step();
		display.redraw();
	}
	
	public void startSearch(String searchType) {
		maze.reColorMaze();
		search = searchType;
		
		// Restart the search.  Since I don't know 
		// which one, I'll restart all of them.
		
		bfs = new BFS(maze, start, goal);	// start in upper left and end in lower right corner
		dfs = new DFS(maze, start, goal);
		greedy = new Greedy(maze, start, goal);
		rand = new RandomWalk(maze, start, goal);
		magic = new Magic(maze, start, goal);
	}

	public int getCellState(Point position) {
		return maze.get(position);
	}
	
	/*
	 * Re-create the maze from scratch.
	 * When this happens, we should also stop the search.
	 */
	public void newMaze() {
		maze.createMaze(maze.getNumRows(),maze.getNumCols());
		search = "";
		display.redraw();
	}
}
