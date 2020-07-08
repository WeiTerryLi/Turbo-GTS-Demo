/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm_related.solutions;

import java.util.LinkedList;

import algorithm_related.core_structures.QuadTree;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.ExecutionStatistics;


public class QT_NNH extends QT_Solution {
    public static ExecutionStatistics es;
   
    private void quadSorting(QuadTree[] array) {
    	for (int i = 1; i < array.length; ++i) {
    		QuadTree key = array[i];
    		int j = i - 1;
    		for (; j >= 0 && array[j].getNumOfUndoneTasks() < key.getNumOfUndoneTasks(); --j) {
    			array[j + 1] = array[j];
    		}
    		array[j + 1] = key;
    	}
    }
       
    private void topDownDivide(QuadTree qt) {
        if (qt.getWorkersAssigned().size() == 1) {
        	taskSequencingNN(qt.getTasksInside(), qt.getWorkersAssigned().getFirst().getIdx());
            qt.updateUndoneWithDurAndEmax(taskUndone);
        } else if (qt.getWorkersAssigned().size() > 1) {
            qt.split(worker_routes, taskUndone);
            
            topDownDivide(qt.getUpperLeftChild());
            topDownDivide(qt.getUpperRightChild());
            topDownDivide(qt.getLowerLeftChild());
            topDownDivide(qt.getLowerRightChild());
            
            qt.getUpperLeftChild().updateUndoneWithDurAndEmax(taskUndone);
            qt.getUpperRightChild().updateUndoneWithDurAndEmax(taskUndone);
            qt.getLowerLeftChild().updateUndoneWithDurAndEmax(taskUndone);
            qt.getLowerRightChild().updateUndoneWithDurAndEmax(taskUndone);
            
            QuadTree[] quadArray = new QuadTree[4];
            
            quadArray[0] = qt.getUpperLeftChild();
            quadArray[1] = qt.getUpperRightChild();
            quadArray[2] = qt.getLowerLeftChild(); 
            quadArray[3] = qt.getLowerRightChild();
            
            quadSorting(quadArray);
            
            for (int i = 0; i < 4; ++i) {
            	 LinkedList<WorkerStatus> workerSet = new LinkedList<WorkerStatus>();
            	 for (int j = 0; j < 4; ++j) {
            		 if (j != i) {
            			 workerSet.addAll(quadArray[j].getWorkersAssigned());
            		 }
            	 }
            	 doRemainingTasksNN(quadArray[i], workerSet);    
            }            
        }
    }       

    public QT_NNH() {
    	super();

        qt_root.updateUndoneWithDurAndEmax(taskUndone);
        
        if (qt_root.getNumOfUndoneTasks() > 0) {
            topDownDivide(qt_root);
        }
    }
}
