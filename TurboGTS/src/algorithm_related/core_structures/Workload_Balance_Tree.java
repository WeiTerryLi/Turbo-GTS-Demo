package algorithm_related.core_structures;

import static mainframe.TurboGTS.tasks;
import static mainframe.TurboGTS.workers;

import java.util.ArrayList;
import algorithm_related.minor_structures.DoublePair;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.RouteStop;
import common_structures.WorkerRoute;

abstract public class Workload_Balance_Tree extends Workload_Tree {
	public boolean isHorizontalSplit;
	protected Workload_Balance_Tree leftOrLowerChild;
	protected Workload_Balance_Tree rightOrUpperChild;

    private void compareTWD(ArrayList<WorkerRoute> worker_routes, boolean[] taskUndone, long appxmt) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        
        child_with_larger_twd = null;
        child_with_lesser_twd = null;
        
    	leftOrLowerChild.calculateTWD(worker_routes, taskUndone, appxmt);            
    	rightOrUpperChild.calculateTWD(worker_routes, taskUndone, appxmt);        
        
        if (leftOrLowerChild.num_of_undone_tasks > 0) { 
            if (leftOrLowerChild.task_worker_density > max) {
                max = leftOrLowerChild.task_worker_density;
                child_with_larger_twd = leftOrLowerChild;
            }
        }
          
        if (leftOrLowerChild.workersAssigned.size() > 0) {     
            if (leftOrLowerChild.task_worker_density < min) {
                min = leftOrLowerChild.task_worker_density;
                child_with_lesser_twd = leftOrLowerChild;
            }
        }
         
        if (rightOrUpperChild.num_of_undone_tasks > 0) { 
            if (rightOrUpperChild.task_worker_density > max) {
                max = rightOrUpperChild.task_worker_density;
                child_with_larger_twd = rightOrUpperChild;
            }
        }
        
        if (rightOrUpperChild.workersAssigned.size() > 0) { 
            if (rightOrUpperChild.task_worker_density < min) {
                min = rightOrUpperChild.task_worker_density;
                child_with_lesser_twd = rightOrUpperChild;
            }
        }               
    }

    public void keepBalancingWorkers(ArrayList<WorkerRoute> worker_routes, 
    		boolean[] taskUndone, long appxmt) {
    	compareTWD(worker_routes, taskUndone, appxmt);            
    	keepExchangingWorkers(worker_routes, appxmt);
    }

    protected void findChildForTasks() {
    	if (isHorizontalSplit) {
    		for (int tsk : tasksInside) {
    			if (tasks[tsk].getLon() <= leftOrLowerChild.upperRightVertex.X) {
    				leftOrLowerChild.tasksInside.add(tsk);
    			} else {
    				rightOrUpperChild.tasksInside.add(tsk);
    			}
    		}
    	} else {
    		for (int tsk : tasksInside) {
    			if (tasks[tsk].getLat() <= leftOrLowerChild.upperRightVertex.Y) {
    				leftOrLowerChild.tasksInside.add(tsk);
    			} else {
    				rightOrUpperChild.tasksInside.add(tsk);
    			}
    		}
    	}
    } 
    
    protected void findChildForWorkers(ArrayList<WorkerRoute> worker_routes) {
        for (WorkerStatus wk : workersAssigned) {
            int array_end = worker_routes.get(wk.getIdx()).sequence.size() - 1;
            double wk_lat;
            double wk_lon;

            if (array_end > 0) {
                RouteStop cur_stop = worker_routes.get(wk.getIdx()).sequence.get(array_end);
                wk_lat = tasks[cur_stop.getIdxInTasks()].getLat();
                wk_lon = tasks[cur_stop.getIdxInTasks()].getLon();
            } else {
                wk_lat = workers[wk.getIdx()].getLat();
                wk_lon = workers[wk.getIdx()].getLon();
            }

            if (isHorizontalSplit) {
            	if (wk_lon <= leftOrLowerChild.upperRightVertex.X) {
            		leftOrLowerChild.workersAssigned.add(wk);
            		wk.distance = leftOrLowerChild.updateWorkerDistance(wk_lon, wk_lat);
            	} else {
            		rightOrUpperChild.workersAssigned.add(wk);
            		wk.distance = rightOrUpperChild.updateWorkerDistance(wk_lon, wk_lat);
            	}
            } else {
            	if (wk_lat <= leftOrLowerChild.upperRightVertex.Y) {
            		leftOrLowerChild.workersAssigned.add(wk);
            		wk.distance = leftOrLowerChild.updateWorkerDistance(wk_lon, wk_lat);
            	} else {
            		rightOrUpperChild.workersAssigned.add(wk);
            		wk.distance = rightOrUpperChild.updateWorkerDistance(wk_lon, wk_lat);
            	}
            }                
        }
    }

    public Workload_Balance_Tree getLeftOrLowerChild() {
    	return leftOrLowerChild;
    }
    
	public Workload_Balance_Tree getRightOrUpperChild() {
		return rightOrUpperChild;
	}

    public Workload_Balance_Tree(Workload_Balance_Tree p, DoublePair ll, DoublePair ur) {
        super(p, ll, ur);
    	
        leftOrLowerChild = null;
        rightOrUpperChild = null;
    } 
}
