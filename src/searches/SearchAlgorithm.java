package searches;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import application.Maze;

public abstract class SearchAlgorithm {
	
	protected Maze maze;					// The maze being solved
	protected Point goal;					// The goal Point - will let us know when search is successful
	protected Collection<Point> data;		// Data structure used to keep "fringe" points
	protected boolean searchOver = false;	// Is search done?
	protected boolean searchResult = false;	// Was it successful?
	protected Point current;
	
	
	public SearchAlgorithm(Maze mazeBlocks, Point startPoint, Point goalPoint) {
		maze = mazeBlocks;
		current = startPoint;
		goal = goalPoint;
		maze.markPath(current);
	}
	
	/*
	 * Algorithm for Breadth-First Search
	 */
	public boolean step(){
		// Don't keep computing after goal is reached or determined impossible.
		if(searchOver){
			return endSearch();
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
		// if no next step is found, mark current 
		else{	
			ifNoStepFound();
		}
		resetCurrent();
		checkSearchOver();
		return searchResult;	
	}
	
	protected abstract void recordLink(Point next);
	
	protected abstract void resetCurrent();
	
	protected abstract void ifNoStepFound();
	
	protected boolean endSearch() {
		return searchResult;
	}
	
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
	
	protected Point chooseNeighbor(Collection<Point> neighbors){
		for(Point p: neighbors){
			if(maze.get(p)==Maze.EMPTY){
				return p;
			}
		}
		return null;
	}
	
	/*
	 * Tells me when the search is over.
	 */
	private boolean isGoal(Point square){
		return square!= null && square.equals(goal);
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
}
