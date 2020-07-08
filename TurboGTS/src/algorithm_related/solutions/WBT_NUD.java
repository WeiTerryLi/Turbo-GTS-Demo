/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm_related.solutions;

import algorithm_related.core_structures.Workload_Tree;
import algorithm_related.core_structures.WBT_Tree;
import algorithm_related.minor_structures.FittestWorker;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.ExecutionStatistics;
import common_structures.RouteStop;

import static mainframe.TurboGTS.getCostBtwPois;
import static mainframe.TurboGTS.tasks;

import java.util.ArrayList;
import java.util.LinkedList;


public class WBT_NUD extends WBT_Solution {
    public static ExecutionStatistics es;

    private void topDownDivide_WBT_NUD(WBT_Tree sbt) {
        if (sbt.getWorkersAssigned().size() == 1) {
        	taskSequencingNUD_Adapt_IC(sbt.getTasksInside(), sbt.getWorkersAssigned().getFirst().getIdx());
        } else if (sbt.getWorkersAssigned().size() > 1) {
        	if (sbt.getUpperRightVertex().X - sbt.getLowerLeftVertex().X >= sbt.getUpperRightVertex().Y - sbt.getLowerLeftVertex().Y) {
        		sbt.isHorizontalSplit = true;
        	} else {
        		sbt.isHorizontalSplit = false;
        	}

    		sbt.split(worker_routes);
        	sbt.keepBalancingWorkers(worker_routes, taskUndone, appxmt);
            
        	topDownDivide_WBT_NUD((WBT_Tree) sbt.getLeftOrLowerChild());
        	topDownDivide_WBT_NUD((WBT_Tree) sbt.getRightOrUpperChild());
            
            sbt.getLeftOrLowerChild().updateUndoneWithDurAndEmax(taskUndone);
            sbt.getRightOrUpperChild().updateUndoneWithDurAndEmax(taskUndone);
            
            WBT_Tree moreTasksSec;
            WBT_Tree lessTasksSec;
            
            if (sbt.getLeftOrLowerChild().getNumOfUndoneTasks() >= sbt.getRightOrUpperChild().getNumOfUndoneTasks()) {
            	moreTasksSec = (WBT_Tree) sbt.getLeftOrLowerChild();
            	lessTasksSec = (WBT_Tree) sbt.getRightOrUpperChild();
            } else {
            	moreTasksSec = (WBT_Tree) sbt.getRightOrUpperChild();
            	lessTasksSec = (WBT_Tree) sbt.getLeftOrLowerChild();
            }
            
            LinkedList<WorkerStatus> workerSet = new LinkedList<WorkerStatus>(lessTasksSec.getWorkersAssigned());
            doRemainingTasksNUD_Adapt_IC(moreTasksSec, workerSet); 
            
            workerSet.clear();
            workerSet.addAll(moreTasksSec.getWorkersAssigned());
            doRemainingTasksNUD_Adapt_IC(lessTasksSec, workerSet);     
        }
    }       
    
    private void doRemainingTasksNUD_Adapt_IC(Workload_Tree destBisec, LinkedList<WorkerStatus> workerSet) {
        while (true) {
            FittestWorker fw = destBisec.findFittestWorker(worker_routes, workerSet);
            
            if (fw.getWorker() == null) {
                break;
            }
            
            int wk = fw.getWorker().getIdx();
            workerSet.remove(fw.getWorker());
            
            ArrayList<Integer> taskList = new ArrayList<Integer>();
            for (int tsk : destBisec.getTasksInside()) {
            	if (taskUndone[tsk] && worker_routes.get(wk).ableToBeDone[tsk]) {
            		int array_end = worker_routes.get(wk).sequence.size() - 1;
            	    RouteStop cur_stop = worker_routes.get(wk).sequence.get(array_end);
        	    	long cur_time = cur_stop.curTime + getCostBtwPois(cur_stop.getPosInIndices(), 
        	                    tasks[tsk].getIndexInIndices()) + tasks[tsk].getDur();
        	
    	            if (cur_time <= tasks[tsk].getExpir()) {
    	            	taskList.add(tsk);
    	            } else {
    	                worker_routes.get(wk).ableToBeDone[tsk] = false;
    	            }            		
            	}
            }
            
            taskSequencingNUD_Adapt_IC(taskList, wk);  
        }
    }
   
    public WBT_NUD() {
    	super();
    	    	
    	topDownDivide_WBT_NUD(wbt_root);
    }
}
