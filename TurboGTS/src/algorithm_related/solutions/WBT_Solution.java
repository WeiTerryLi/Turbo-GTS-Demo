package algorithm_related.solutions;

import static mainframe.TurboGTS.tasks;
import static mainframe.TurboGTS.workers;

import java.util.ArrayList;
import java.util.LinkedList;

import algorithm_related.core_structures.WBT_Tree;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.WorkerRoute;

abstract public class WBT_Solution extends WorkloadTree_Solution {
    protected final WBT_Tree wbt_root;
    
    protected void topDownDivide_WBT_NN(WBT_Tree wbt) {
        if (wbt.getWorkersAssigned().size() == 1) {
            taskSequencingNN(wbt.getTasksInside(), wbt.getWorkersAssigned().getFirst().getIdx());
        } else if (wbt.getWorkersAssigned().size() > 1) {
        	if (wbt.getUpperRightVertex().X - wbt.getLowerLeftVertex().X >= wbt.getUpperRightVertex().Y - wbt.getLowerLeftVertex().Y) {
        		wbt.isHorizontalSplit = true;
        	} else {
        		wbt.isHorizontalSplit = false;
        	}

    		wbt.split(worker_routes);
        	wbt.keepBalancingWorkers(worker_routes, taskUndone, appxmt);
            
        	topDownDivide_WBT_NN((WBT_Tree) wbt.getLeftOrLowerChild());
        	topDownDivide_WBT_NN((WBT_Tree) wbt.getRightOrUpperChild());
            
            wbt.getLeftOrLowerChild().updateUndoneWithDurAndEmax(taskUndone);
            wbt.getRightOrUpperChild().updateUndoneWithDurAndEmax(taskUndone);
            
            WBT_Tree moreTasksSec;
            WBT_Tree lessTasksSec;
            
            if (wbt.getLeftOrLowerChild().getNumOfUndoneTasks() >= wbt.getRightOrUpperChild().getNumOfUndoneTasks()) {
            	moreTasksSec = (WBT_Tree) wbt.getLeftOrLowerChild();
            	lessTasksSec = (WBT_Tree) wbt.getRightOrUpperChild();
            } else {
            	moreTasksSec = (WBT_Tree) wbt.getRightOrUpperChild();
            	lessTasksSec = (WBT_Tree) wbt.getLeftOrLowerChild();
            }
            
            LinkedList<WorkerStatus> workerSet = new LinkedList<WorkerStatus>(lessTasksSec.getWorkersAssigned());
            doRemainingTasksNN(moreTasksSec, workerSet); 
            
            workerSet.clear();
            workerSet.addAll(moreTasksSec.getWorkersAssigned());
            doRemainingTasksNN(lessTasksSec, workerSet);     
        }
    }       

	protected WBT_Solution() {
    	super();
        
        wbt_root = new WBT_Tree(null, lowerLeft, upperRight);
        
        for (int i = 0; i < tasks.length; ++i) {
        	wbt_root.getTasksInside().add(i);
        }
        
        for (int i = 0; i < workers.length; ++i) {
        	wbt_root.getWorkersAssigned().add(new WorkerStatus(i));
        }          
    }
	
	protected WBT_Solution(ArrayList<WorkerRoute> wrs, ArrayList<Integer> taskList) {
    	super(wrs, taskList);
        
        wbt_root = new WBT_Tree(null, lowerLeft, upperRight);
        
        for (int i : taskList) {
        	wbt_root.getTasksInside().add(i);
        }
        
        for (int i = 0; i < workers.length; ++i) {
        	wbt_root.getWorkersAssigned().add(new WorkerStatus(i));
        }          
    }
}
