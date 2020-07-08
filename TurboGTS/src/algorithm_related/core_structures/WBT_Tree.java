package algorithm_related.core_structures;

import java.util.ArrayList;
import algorithm_related.minor_structures.DoublePair;
import common_structures.WorkerRoute;

public class WBT_Tree extends Workload_Balance_Tree {
    public WBT_Tree(WBT_Tree p, DoublePair ll, DoublePair ur) {
		super(p, ll, ur);
	}

	public void split(ArrayList<WorkerRoute> worker_routes) { 
    	if (isHorizontalSplit) {
        	leftOrLowerChild = new WBT_Tree(this, new DoublePair(lowerLeftVertex.X, lowerLeftVertex.Y), 
        			new DoublePair((lowerLeftVertex.X + upperRightVertex.X) / 2, upperRightVertex.Y));

        	rightOrUpperChild = new WBT_Tree(this, new DoublePair((lowerLeftVertex.X + upperRightVertex.X) / 2, lowerLeftVertex.Y), 
                new DoublePair(upperRightVertex.X, upperRightVertex.Y));
    	} else {
    		leftOrLowerChild = new WBT_Tree(this, new DoublePair(lowerLeftVertex.X, lowerLeftVertex.Y), 
    				new DoublePair(upperRightVertex.X, (lowerLeftVertex.Y + upperRightVertex.Y) / 2));
	
	        rightOrUpperChild = new WBT_Tree(this, new DoublePair(lowerLeftVertex.X, (lowerLeftVertex.Y + upperRightVertex.Y) / 2), 
	                new DoublePair(upperRightVertex.X, upperRightVertex.Y));
    	}
            
    	findChildForTasks();
    	findChildForWorkers(worker_routes);
    }  
 }
