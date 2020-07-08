package algorithm_related.core_structures;

import static mainframe.TurboGTS.tasks;
import static mainframe.TurboGTS.workers;

import java.util.ArrayList;

import algorithm_related.minor_structures.DoublePair;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.RouteStop;
import common_structures.WorkerRoute;

public class QuadTree extends RectTree {
    QuadTree upperLeftChild;
    QuadTree upperRightChild;
    QuadTree lowerLeftChild;
    QuadTree lowerRightChild;

    void findQuadForTasks() {
        for (int tsk : tasksInside) {
            if ((tasks[tsk].getLon() >= upperLeftChild.lowerLeftVertex.X) && (tasks[tsk].getLat() 
                    >= upperLeftChild.lowerLeftVertex.Y) && (tasks[tsk].getLon() <= upperLeftChild.upperRightVertex.X) 
                    && (tasks[tsk].getLat() <= upperLeftChild.upperRightVertex.Y)) {
                upperLeftChild.tasksInside.add(tsk);
            } else if ((tasks[tsk].getLon() >= upperRightChild.lowerLeftVertex.X) && (tasks[tsk].getLat() 
                    >= upperRightChild.lowerLeftVertex.Y) && (tasks[tsk].getLon() <= upperRightChild.upperRightVertex.X) 
                    && (tasks[tsk].getLat() <= upperRightChild.upperRightVertex.Y)) {
                upperRightChild.tasksInside.add(tsk);
            } else if ((tasks[tsk].getLon() >= lowerLeftChild.lowerLeftVertex.X) && (tasks[tsk].getLat() 
                    >= lowerLeftChild.lowerLeftVertex.Y) && (tasks[tsk].getLon() <= lowerLeftChild.upperRightVertex.X) 
                    && (tasks[tsk].getLat() <= lowerLeftChild.upperRightVertex.Y)) {
                lowerLeftChild.tasksInside.add(tsk);
            } else {
                lowerRightChild.tasksInside.add(tsk);
            }            
        }
    } 

    private void findQuadForWorkers(ArrayList<WorkerRoute> worker_routes) {
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

            if ((wk_lon >= upperLeftChild.lowerLeftVertex.X) && (wk_lat >= upperLeftChild.lowerLeftVertex.Y) && 
                    (wk_lon <= upperLeftChild.upperRightVertex.X) && (wk_lat <= upperLeftChild.upperRightVertex.Y)) {
                upperLeftChild.workersAssigned.add(wk);
                wk.distance = 0.0;
            } else if ((wk_lon >= upperRightChild.lowerLeftVertex.X) && (wk_lat >= upperRightChild.lowerLeftVertex.Y) && 
                    (wk_lon <= upperRightChild.upperRightVertex.X) && (wk_lat <= upperRightChild.upperRightVertex.Y)) {
                upperRightChild.workersAssigned.add(wk);
                wk.distance = 0.0;
            } else if ((wk_lon >= lowerLeftChild.lowerLeftVertex.X) && (wk_lat >= lowerLeftChild.lowerLeftVertex.Y) && 
                    (wk_lon <= lowerLeftChild.upperRightVertex.X) && (wk_lat <= lowerLeftChild.upperRightVertex.Y)) {
                lowerLeftChild.workersAssigned.add(wk);
                wk.distance = 0.0;
            } else if ((wk_lon >= lowerRightChild.lowerLeftVertex.X) && (wk_lat >= lowerRightChild.lowerLeftVertex.Y) && 
                    (wk_lon <= lowerRightChild.upperRightVertex.X) && (wk_lat <= lowerRightChild.upperRightVertex.Y)) {
                lowerRightChild.workersAssigned.add(wk);
                wk.distance = 0.0;
            } else {
                double centerX = (lowerLeftVertex.X + upperRightVertex.X) / 2;
                double centerY = (lowerLeftVertex.Y + upperRightVertex.Y) / 2;
                
                if (wk_lat >= centerY) {
                    if (wk_lon <= centerX) {
                        upperLeftChild.workersAssigned.add(wk);
                        
                        if (wk_lon >= lowerLeftVertex.X) {
                            wk.distance = Math.abs(wk_lat - upperRightVertex.Y);
                        } else if (wk_lat <= upperRightVertex.Y) {
                            wk.distance = Math.abs(lowerLeftVertex.X - wk_lon);
                        } else {
                            wk.distance = Math.sqrt(Math.pow(lowerLeftVertex.X - wk_lon, 2) +
                                    Math.pow(wk_lat - upperRightVertex.Y, 2));
                        }
                    } else {
                        upperRightChild.workersAssigned.add(wk);
                        
                        if (wk_lon <= upperRightVertex.X) {
                            wk.distance = Math.abs(wk_lat - upperRightVertex.Y);
                        } else if (wk_lat <= upperRightVertex.Y) {
                            wk.distance = Math.abs(wk_lon - upperRightVertex.X);
                        } else {
                            wk.distance = Math.sqrt(Math.pow(wk_lon - upperRightVertex.X, 2) + 
                                    Math.pow(wk_lat - upperRightVertex.Y, 2));
                        }
                    }                        
                } else {
                    if (wk_lon <= centerX) {
                        lowerLeftChild.workersAssigned.add(wk);
                        
                        if (wk_lon >= lowerLeftVertex.X) {
                            wk.distance = Math.abs(lowerLeftVertex.Y - wk_lat);
                        } else if (wk_lat >= lowerLeftVertex.Y) {
                            wk.distance = Math.abs(lowerLeftVertex.X - wk_lon);
                        } else {
                            wk.distance = Math.sqrt(Math.pow(lowerLeftVertex.X - wk_lon, 2) + 
                                    Math.pow(lowerLeftVertex.Y - wk_lat, 2));
                        }
                    } else {
                        lowerRightChild.workersAssigned.add(wk);
                        
                        if (wk_lon <= upperRightVertex.X) {
                            wk.distance = Math.abs(lowerLeftVertex.Y - wk_lat);
                        } else if (wk_lat <= upperRightVertex.Y) {
                            wk.distance = Math.abs(wk_lon - upperRightVertex.X); 
                        } else {
                            wk.distance = Math.sqrt(Math.pow(wk_lon - upperRightVertex.X, 2) + 
                                    Math.pow(upperRightVertex.Y - wk_lat, 2));
                        }
                    }
                }
            }
        }
    }

    public void split(ArrayList<WorkerRoute> worker_routes, boolean[] taskUndone) { 
        upperLeftChild = new QuadTree(this, new DoublePair(lowerLeftVertex.X, (lowerLeftVertex.Y + upperRightVertex.Y) / 2), 
            new DoublePair((lowerLeftVertex.X + upperRightVertex.X) / 2, upperRightVertex.Y));

        upperRightChild = new QuadTree(this, new DoublePair((lowerLeftVertex.X + upperRightVertex.X) / 2, (lowerLeftVertex.Y + upperRightVertex.Y) / 2), 
            new DoublePair(upperRightVertex.X, upperRightVertex.Y));

        lowerLeftChild = new QuadTree(this, new DoublePair(lowerLeftVertex.X, lowerLeftVertex.Y),
            new DoublePair((lowerLeftVertex.X + upperRightVertex.X) / 2, (lowerLeftVertex.Y + upperRightVertex.Y) / 2));

        lowerRightChild = new QuadTree(this, new DoublePair((lowerLeftVertex.X + upperRightVertex.X) / 2, lowerLeftVertex.Y), 
            new DoublePair(upperRightVertex.X, (lowerLeftVertex.Y + upperRightVertex.Y) / 2));
        
        findQuadForTasks();
        findQuadForWorkers(worker_routes);
        
        upperLeftChild.updateUndoneWithDurAndEmax(taskUndone);
        
        upperRightChild.updateUndoneWithDurAndEmax(taskUndone);
        
        lowerLeftChild.updateUndoneWithDurAndEmax(taskUndone);
        
        lowerRightChild.updateUndoneWithDurAndEmax(taskUndone);      
    }  

    public QuadTree getUpperLeftChild() {
    	return upperLeftChild;
    }

    public QuadTree getUpperRightChild() {
    	return upperRightChild;
    }

    public QuadTree getLowerLeftChild() {
    	return lowerLeftChild;
    }

    public QuadTree getLowerRightChild() {
    	return lowerRightChild;
    }
    
    public QuadTree(QuadTree p, DoublePair ll, DoublePair ur) {
        super(p, ll, ur);
        
        upperLeftChild = null;
        upperRightChild = null;
        lowerLeftChild = null;
        lowerRightChild = null;
    } 
}
