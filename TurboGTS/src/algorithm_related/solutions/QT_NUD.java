/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm_related.solutions;

import algorithm_related.core_structures.QuadTree;
import common_structures.ExecutionStatistics;


public class QT_NUD extends QT_Solution {
    public static ExecutionStatistics es;

    private void topDownDivide(QuadTree qt) {
        if (qt.getWorkersAssigned().size() == 1) {
            taskSequencingNUD(qt.getTasksInside(), qt.getWorkersAssigned().getFirst().getIdx());
        } else if (qt.getWorkersAssigned().size() > 1) {
            qt.split(worker_routes, taskUndone);
            
            topDownDivide(qt.getUpperLeftChild());
            qt.getUpperLeftChild().updateUndoneWithDurAndEmax(taskUndone);
            
            topDownDivide(qt.getUpperRightChild());
            qt.getUpperRightChild().updateUndoneWithDurAndEmax(taskUndone);
            
            topDownDivide(qt.getLowerLeftChild());
            qt.getLowerLeftChild().updateUndoneWithDurAndEmax(taskUndone);
            
            topDownDivide(qt.getLowerRightChild());
            qt.getLowerRightChild().updateUndoneWithDurAndEmax(taskUndone);
            
            doRemainingTasksNUD(qt);
        }
    }       

    public QT_NUD() {
    	super();

        topDownDivide(qt_root);
    }
}
