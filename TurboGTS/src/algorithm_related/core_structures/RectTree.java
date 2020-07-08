package algorithm_related.core_structures;

import static mainframe.TurboGTS.earthSufaceDistCal;
import static mainframe.TurboGTS.tasks;
import static mainframe.TurboGTS.workers;

import java.util.ArrayList;
import java.util.LinkedList;

import algorithm_related.minor_structures.DoublePair;
import algorithm_related.minor_structures.FittestWorker;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.RouteStop;
import common_structures.WorkerRoute;

public abstract class RectTree {
	protected final ArrayList<Integer> tasksInside;
	protected final LinkedList<WorkerStatus> workersAssigned;
	protected DoublePair lowerLeftVertex;
	protected DoublePair upperRightVertex;
	protected double num_of_undone_tasks;
	protected long expir_max = 0;
	protected RectTree parent;

    public double updateWorkerDistance(double wk_lon, double wk_lat) {
        double diff;

        if (wk_lon >= lowerLeftVertex.X && wk_lon <= upperRightVertex.X) {
            if (wk_lat > upperRightVertex.Y) {
                diff = earthSufaceDistCal(wk_lat, wk_lon, upperRightVertex.Y, wk_lon);
            } else {
                diff = earthSufaceDistCal(wk_lat, wk_lon, lowerLeftVertex.Y, wk_lon);
            }
        } else if (wk_lat >= lowerLeftVertex.Y && wk_lat <= upperRightVertex.Y) {
            if (wk_lon < lowerLeftVertex.X) {
                diff = earthSufaceDistCal(wk_lat, wk_lon, wk_lat, lowerLeftVertex.X);
            } else {
                diff = earthSufaceDistCal(wk_lat, wk_lon, wk_lat, upperRightVertex.X);
            }
        } else {
            if (wk_lon < lowerLeftVertex.X) {
                if (wk_lat < lowerLeftVertex.Y) {
                	diff = earthSufaceDistCal(wk_lat, wk_lon, lowerLeftVertex.Y, lowerLeftVertex.X);
                } else {
                	diff = earthSufaceDistCal(wk_lat, wk_lon, upperRightVertex.Y, lowerLeftVertex.X);
                }
            } else {
                if (wk_lat < lowerLeftVertex.Y) {
                	diff = earthSufaceDistCal(wk_lat, wk_lon, lowerLeftVertex.Y, upperRightVertex.X);
                } else {
                	diff = earthSufaceDistCal(wk_lat, wk_lon, upperRightVertex.Y, upperRightVertex.X);
                }
            }
        }

        return diff;
    }
  
    public FittestWorker findFittestWorker(ArrayList<WorkerRoute> worker_routes, 
    		LinkedList<WorkerStatus> workerSet) {
        double min_cost = Double.MAX_VALUE;
        double displace = 0.0;
        WorkerStatus chosen = null;

        for (WorkerStatus wk : workerSet) {
            int array_end = worker_routes.get(wk.getIdx()).sequence.size() - 1;
            RouteStop cur_stop = worker_routes.get(wk.getIdx()).sequence.get(array_end);
            double wk_lat;
            double wk_lon;

            if (array_end > 0) {
                wk_lat = tasks[cur_stop.getIdxInTasks()].getLat();
                wk_lon = tasks[cur_stop.getIdxInTasks()].getLon();
            } else {
                wk_lat = workers[wk.getIdx()].getLat();
                wk_lon = workers[wk.getIdx()].getLon();
            }

            double diff = updateWorkerDistance(wk_lon, wk_lat);

            double cost = cur_stop.curTime + diff;
            if (cost < expir_max) {
                if (cost < min_cost) {
                    min_cost = cost;
                    chosen = wk;
                    displace = diff;
                }
            }
        }

        return new FittestWorker(chosen, displace);
    }
 
   public void updateUndoneAndEmax(boolean[] taskUndone) {
       num_of_undone_tasks = 0;
       expir_max = 0;
       
       for (int tsk : tasksInside) {
           if (taskUndone[tsk]) {
               ++num_of_undone_tasks;
               
               if (tasks[tsk].getExpir() > expir_max) {
                   expir_max = tasks[tsk].getExpir();                
               }
           }
       }
   }
   
   public void updateUndoneWithDurAndEmax(boolean[] taskUndone) {
      num_of_undone_tasks = 0.0;
      expir_max = 0;
      
      for (int tsk : tasksInside) {
          if (taskUndone[tsk]) {
              double dur = tasks[tsk].getDur();
              double weight = dur > 0.0 ? 1.0 / dur : 1;
              num_of_undone_tasks += weight;
              
              if (tasks[tsk].getExpir() > expir_max) {
                  expir_max = tasks[tsk].getExpir();                
              }
          }
      }
  }
	
	public DoublePair getLowerLeftVertex() {
   	return lowerLeftVertex;
   }
   
	public DoublePair getUpperRightVertex() {
		return upperRightVertex;
	}
   
   public double getNumOfUndoneTasks() {
   	return num_of_undone_tasks;
   }
   
   public ArrayList<Integer> getTasksInside() {
   	return tasksInside;
   }
   
   public LinkedList<WorkerStatus> getWorkersAssigned() {
   	return workersAssigned;
   }

	public RectTree(RectTree p, DoublePair ll, DoublePair ur) {
	    parent = p;
	    lowerLeftVertex = ll;
	    upperRightVertex = ur;
	    
	    workersAssigned = new LinkedList<WorkerStatus>();
	    tasksInside = new ArrayList<Integer>();	 
	} 
}
