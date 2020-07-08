package algorithm_related.solutions;

import java.util.ArrayList;
import java.util.LinkedList;

import algorithm_related.core_structures.Workload_Tree;
import algorithm_related.minor_structures.FittestWorker;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.WorkerRoute;

abstract public class WorkloadTree_Solution extends GeneralSolution {
    protected void doRemainingTasksNN(Workload_Tree destBisec, LinkedList<WorkerStatus> workerSet) {
        while (true) {
            FittestWorker fw = destBisec.findFittestWorker(worker_routes, workerSet);
            
            if (fw.getWorker() == null) {
                break;
            }
            
            workerSet.remove(fw.getWorker());
            taskSequencingNN(destBisec.getTasksInside(), fw.getWorker().getIdx());      
        }
    }
  
    protected WorkloadTree_Solution() {
    	super();
    }

    protected WorkloadTree_Solution(ArrayList<WorkerRoute> wrs, ArrayList<Integer> taskList) {
    	super(wrs, taskList);
    }
}
