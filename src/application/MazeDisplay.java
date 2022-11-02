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

/*
 * @author Shannon Duvall
 * 
 * This class is the "Main" class to run - holds the Java FX application
 */

public class MazeDisplay extends Application {

	/*
	 * GUI settings
	 */
	private final int MILLISECOND_DELAY = 15;	// speed of animation
	private final int EXTRA_VERTICAL = 100; 	// GUI area allowance when making the scene width
	private final int EXTRA_HORIZONTAL = 150; 	// GUI area allowance when making the scene width
	private final int BLOCK_SIZE = 12;     		// size of each cell in pixels
	private final int NUM_ROWS = 31; 
	private final int NUM_COLUMNS = 41;

	private Scene myScene;						// the container for the GUI
	private boolean paused = false;		
	private Button pauseButton;

	private Rectangle[][] mirrorMaze;	// the Rectangle objects that will get updated and drawn.  It is 
	// called "mirror" maze because there is one entry per square in 
	// the maze.

	/*
	 * Maze color settings
	 */
	private Color[] color  = new Color[] {
			Color.rgb(200,0,0),		// wall color
			Color.rgb(128,128,255),	// path color
			Color.WHITE,			// empty cell color
			Color.rgb(200,200,200)	// visited cell color
	};  		// the color of each of the states  

	/* 
	 * Logic of the program
	 */
	// The search algorithms
	private Greedy greedy;				
	private BFS bfs;
	private DFS dfs;
	private RandomWalk rand;
	private Magic magic;
	private String search = "";		// This string tells which algorithm is currently chosen.  Anything other than 
	// the implemented search class names will result in no search happening.

	// Where to start and stop the search
	private Point start;
	private Point goal;

	// The maze to search
	private Maze maze;


	// Start of JavaFX Application
	public void start(Stage stage) {
		// Initializing logic state
		int numRows = NUM_ROWS;
		int numColumns = NUM_COLUMNS;
		start = new Point(1,1);
		goal = new Point(numRows-2, numColumns-2);
		maze = new Maze(numRows, numColumns);

		
		// Initializing the gui
		myScene = setupScene();
		stage.setScene(myScene);
		stage.setTitle("aMAZEing");
		stage.show();

		// Makes the animation happen.  Will call "step" method repeatedly.
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> step(MILLISECOND_DELAY));
		Timeline animation = new Timeline();
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.getKeyFrames().add(frame);
		animation.play();
	}

	// Create the scene - Controls and Maze areas
	private Scene setupScene () {
		// Make three container 
		Group mazeDrawing = setupMaze();
		HBox searches = setupSearchButtons();
		HBox controls = setupControlButtons();

		VBox root = new VBox();
		root.setAlignment(Pos.TOP_CENTER);
		root.setSpacing(10);
		root.setPadding(new Insets(10, 10, 10, 10));
		root.getChildren().addAll(searches,mazeDrawing,controls);

		Scene scene = new Scene(root, NUM_COLUMNS*BLOCK_SIZE+ EXTRA_HORIZONTAL, 
				NUM_ROWS*BLOCK_SIZE + EXTRA_VERTICAL, Color.ANTIQUEWHITE);

		return scene;
	}

	private HBox setupControlButtons(){
		// Make the controls part
		HBox controls = new HBox();
		controls.setAlignment(Pos.BASELINE_CENTER);
		controls.setSpacing(10);

		Button newMazeButton = new Button("New Maze");
		newMazeButton.setOnAction(value ->  {
			newMaze();
		});
		controls.getChildren().add(newMazeButton);

		pauseButton = new Button("Pause");
		pauseButton.setOnAction(value ->  {
			pressPause();
		});
		controls.getChildren().add(pauseButton);

		Button stepButton = new Button("Step");
		stepButton.setOnAction(value ->  {
			this.doOneStep(MILLISECOND_DELAY);
		});
		controls.getChildren().add(stepButton);
		return controls;
	}

	private HBox setupSearchButtons(){
		HBox searches = new HBox();
		searches.setAlignment(Pos.BASELINE_CENTER);
		searches.setSpacing(5);

		Button dfsButton = new Button("Depth-First Search");
		dfsButton.setOnAction(value ->  {
			startSearch("DFS");
		});
		searches.getChildren().add(dfsButton);

		Button bfsButton = new Button("Breadth-First Search");
		bfsButton.setOnAction(value ->  {
			startSearch("BFS");
		});
		searches.getChildren().add(bfsButton);

		Button greedyButton = new Button("Greedy");
		greedyButton.setOnAction(value ->  {
			startSearch("Greedy");
		});
		searches.getChildren().add(greedyButton);

		Button randButton = new Button("Random Walk");
		randButton.setOnAction(value ->  {
			startSearch("RandomWalk");
		});
		searches.getChildren().add(randButton);

		Button magicButton = new Button("Magic!");
		magicButton.setOnAction(value ->  {
			startSearch("Magic");
		});
		searches.getChildren().add(magicButton);
		return searches;
	}

	public Point getMazeDimensions() {
		return new Point(NUM_ROWS, NUM_COLUMNS);
	}

	/*
	 * Setup the maze part for drawing. In particular,
	 * make the mirrorMaze.
	 */
	private Group setupMaze(){
		Group drawing = new Group();
		mirrorMaze = new Rectangle[NUM_ROWS][NUM_COLUMNS];
		for(int i = 0; i< NUM_ROWS; i++){
			for(int j =0; j < NUM_COLUMNS; j++){
				Rectangle rect = new Rectangle(j*BLOCK_SIZE, i*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
				rect.setFill(color[getCellState(new Point(i,j))]);
				mirrorMaze[i][j] = rect;
				drawing.getChildren().add(rect);
			}	
		}
		return drawing;
	}
	
	/*
	 * Re-create the maze from scratch.
	 * When this happens, we should also stop the search.
	 */
	public void newMaze() {
		maze.createMaze(maze.getNumRows(),maze.getNumCols());
		search = "";
		redraw();
	}

	/*
	 * Toggle the pause button
	 */
	public void pressPause(){
		this.paused = !this.paused;
		if(this.paused){
			pauseButton.setText("Resume");
		}
		else{
			pauseButton.setText("Pause");
		}
	}

	/*
	 * Pause the animation (regardless of current state of pause button)
	 */
	public void pauseIt(){
		this.paused = true;
		pauseButton.setText("Resume");
	}

	/*
	 * resets all the rectangle colors according to the 
	 * current state of that rectangle in the maze.  This 
	 * method assumes the display maze matches the model maze
	 */
	public void redraw(){
		for(int i = 0; i< mirrorMaze.length; i++){
			for(int j =0; j < mirrorMaze[i].length; j++){
				mirrorMaze[i][j].setFill(color[getCellState(new Point(i,j))]);
			}
		}
	}

	/*
	 * Does a step in the search only if not paused.
	 */
	public void step(double elapsedTime){
		if(!paused) {
			doOneStep(elapsedTime);
		}
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
		redraw();
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

	public static void main(String[] args) {
		launch(args);
	}
}