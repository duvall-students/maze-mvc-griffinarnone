package searches;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import application.Maze;

public class RandomWalk{
	public final double EXPLORE_BIAS = .999;
	private Point next;
	private Random rand;
	
	private Maze maze;					// The maze being solved
	private Point goal;					// The goal Point - will let us know when search is successful
	private Collection<Point> data;		// Data structure used to keep "fringe" points
	private boolean searchOver = false;	// Is search done?
	private boolean searchResult = false;	// Was it successful?
	private Point current;				// Current point being explored
	
	public RandomWalk(Maze mazeBlocks, Point startPoint, Point goalPoint){
		maze = mazeBlocks;
		goal = goalPoint;
		current = startPoint;
		maze.markPath(current);
		next = startPoint;
		rand = new Random();
	}
	
	/*
	 * Algorithm for a Random Walk: Randomly choose a neighbor to go to
	 */
	public boolean step(){
		// Don't keep computing after goal is reached or determined impossible.
		if(searchOver){
			return searchResult;
		}
		// Find possible next steps
		Collection<Point> neighbors = getNeighbors();
		// Choose one to be a part of the path
		Point next = chooseNeighbor(neighbors);
		// mark the next step
		if(next!=null){
			maze.markPath(next);
			recordLink(next);
		}
		// if no next step is found
		else{	
			maze.markVisited(current);
		}
		resetCurrent();
		checkSearchOver();
		return searchResult;	
	}
	
	
	/*
	 * This method defines which "neighbors" or next points can be reached in the maze from
	 * the current one.  
	 * 
	 * In this method, the neighbors are defined as the four squares immediately to the north, south,
	 * east, and west of the current point, but only if they are in the bounds of the maze.  It does 
	 * NOT check to see if the point is a wall, or visited.  
	 * 
	 * Any other definition of "neighbor" indicates the search subclass should override this method.
	 */
	private Collection<Point> getNeighbors(){
		List<Point> maybeNeighbors = new ArrayList<>();
		maybeNeighbors.add(new Point(current.x-1,current.y));
		maybeNeighbors.add(new Point(current.x+1,current.y));
		maybeNeighbors.add(new Point(current.x,current.y+1));
		maybeNeighbors.add(new Point(current.x,current.y-1));
		List<Point> neighbors = new ArrayList<>();
		for(Point p: maybeNeighbors){
			if(maze.inBounds(p)){
				neighbors.add(p);
			}
		}
		return neighbors;
	}
	
	/*
	 * Choose a random neighbor out of all the non-wall neighbors. 
	 * To make the algorithm prefer going to unexplored areas, make the EXPLORE_BIAS higher.
	 */
	private Point chooseNeighbor(Collection<Point> neighbors){
		List<Point> empties = new ArrayList<>();
		List<Point> possibles = new ArrayList<>();
		for(Point p: neighbors){
			if(maze.get(p)==Maze.EMPTY){
				empties.add(p);
			}
			if(maze.get(p) != Maze.WALL){	// includes empties - all candidates
				possibles.add(p);
			}
		}
		if((rand.nextDouble()<EXPLORE_BIAS && !empties.isEmpty())) {
			int randIndex = rand.nextInt(empties.size());
			return empties.get(randIndex);
		}
		if(!possibles.isEmpty()){
			int randIndex = rand.nextInt(possibles.size());
			return possibles.get(randIndex);
		}
		return null;
	}
	
	private void recordLink(Point next){	
		this.next = next;
		maze.markVisited(current);
	}
	
	private void resetCurrent(){
		current = next;
	}
	
	/*
	 * Search is over and unsuccessful if there are no more fringe points to consider.
	 * Search is over and successful if the current point is the same as the goal.
	 */
	private void checkSearchOver(){
		if(data!= null && data.isEmpty()) {
			searchOver = true;
			searchResult = false;
		}
		if(isGoal(current)){
			searchOver = true;
			searchResult = true;
		}
	}
	private boolean isGoal(Point square){
		return square!= null && square.equals(goal);
	}
}
