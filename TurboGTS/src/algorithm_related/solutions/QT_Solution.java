package algorithm_related.solutions;

import static mainframe.TurboGTS.tasks;
import static mainframe.TurboGTS.workers;

import java.util.ArrayList;
import java.util.LinkedList;

import algorithm_related.core_structures.QuadTree;
import algorithm_related.minor_structures.FittestWorker;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.WorkerRoute;

public abstract class QT_Solution extends GeneralSolution {
	protected final QuadTree qt_root;
    
    private class TreeAndWorkers {
    	final QuadTree tree;
    	LinkedList<WorkerStatus> workerSet;
    	
    	TreeAndWorkers(QuadTree qt) {
    		tree = qt;
    	}
    }
    
    private void treeAndWorkersSorting(ArrayList<TreeAndWorkers> array) {
    	for (int i = 1; i < array.size(); ++i) {
    		TreeAndWorkers key = array.get(i);
    		int j = i - 1;
    		for (; j >= 0 && array.get(j).tree.getNumOfUndoneTasks() < key.tree.getNumOfUndoneTasks(); --j) {
    			array.set(j + 1, array.get(j));
    		}
    		array.set(j + 1, key);
    	}
    }
    
	protected void doRemainingTasksNN(QuadTree destQuad, LinkedList<WorkerStatus> workerSet) {
        while (destQuad.getNumOfUndoneTasks() > 0) {
            FittestWorker fw = destQuad.findFittestWorker(worker_routes, workerSet);
            
            if (fw.getWorker() == null) {
                break;
            }
            
            workerSet.remove(fw.getWorker());
            taskSequencingNN(destQuad.getTasksInside(), fw.getWorker().getIdx());      
        }
    }

	protected void doRemainingTasksNUD(QuadTree qt) {
    	ArrayList<TreeAndWorkers> treeWorkers = new ArrayList<TreeAndWorkers>();
        
        treeWorkers.add(new TreeAndWorkers(qt.getUpperLeftChild()));
        treeWorkers.add(new TreeAndWorkers(qt.getUpperRightChild()));
        treeWorkers.add(new TreeAndWorkers(qt.getLowerLeftChild()));
        treeWorkers.add(new TreeAndWorkers(qt.getLowerRightChild()));
        
        for (int i = 0; i < 4; ++i) {
        	treeWorkers.get(i).tree.updateUndoneWithDurAndEmax(taskUndone);        	
			treeWorkers.get(i).workerSet = new LinkedList<WorkerStatus>();
			for (int j = 0; j < 4; ++j) {
				if (j != i) {
					treeWorkers.get(i).workerSet.addAll(treeWorkers.get(j).tree.getWorkersAssigned());
				}
			} 
		}
        
        while (treeWorkers.size() > 0) {
            treeAndWorkersSorting(treeWorkers);
            
        	FittestWorker fw = qt.findFittestWorker(worker_routes, treeWorkers.get(0).workerSet); 
        	
        	if (fw.getWorker() != null) {
        		taskSequencingNUD(treeWorkers.get(0).tree.getTasksInside(), fw.getWorker().getIdx());  
        		treeWorkers.get(0).tree.updateUndoneWithDurAndEmax(taskUndone);
        		treeWorkers.get(0).workerSet.remove(fw.getWorker());
        	} else {
        		treeWorkers.remove(0);
        	}
        }
    }

    protected QT_Solution() {
    	super();

        qt_root = new QuadTree(null, lowerLeft, upperRight);
        
        for (int i = 0; i < tasks.length; ++i) {
            qt_root.getTasksInside().add(i);
        }
        
        for (int i = 0; i < workers.length; ++i) {
            qt_root.getWorkersAssigned().add(new WorkerStatus(i));
        }          
    }

    protected QT_Solution(ArrayList<WorkerRoute> wrs, ArrayList<Integer> taskList) {
    	super(wrs, taskList);
        
    	qt_root = new QuadTree(null, lowerLeft, upperRight);
        
        for (int i : taskList) {
        	qt_root.getTasksInside().add(i);
        }
        
        for (int i = 0; i < workers.length; ++i) {
        	qt_root.getWorkersAssigned().add(new WorkerStatus(i));
        }  
    }
}
