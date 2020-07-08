package algorithm_related.core_structures;

import java.util.ArrayList;

import algorithm_related.minor_structures.DoublePair;
import algorithm_related.minor_structures.FittestWorker;
import algorithm_related.minor_structures.WorkerStatus;
import common_structures.RouteStop;
import common_structures.WorkerRoute;

abstract public class Workload_Tree extends RectTree {
	protected double task_worker_density;
	protected Workload_Tree child_with_larger_twd;
	protected Workload_Tree child_with_lesser_twd;
	   
	 public double getRangeX(long appxmt) {
	     return appxmt * (upperRightVertex.X - lowerLeftVertex.X);
	 }
	 
	 public double getRangeY(long appxmt) {
	     return appxmt * (upperRightVertex.Y - lowerLeftVertex.Y);
	 }

    protected void calculateTWD(ArrayList<WorkerRoute> worker_routes, 
    		boolean[] taskUndone, long appxmt) {
    	updateUndoneWithDurAndEmax(taskUndone);
    	task_worker_density = num_of_undone_tasks * calculateRPD(getRangeX(appxmt), getRangeY(appxmt));
        
        double avail_time = 0.0;
        for (WorkerStatus wk : workersAssigned) {
            ArrayList<RouteStop> seq = worker_routes.get(wk.getIdx()).getSequence();
            double diff_time = expir_max - (seq.get(seq.size() - 1).curTime + wk.distance);
            if (diff_time > 0) {
            	avail_time += diff_time;
            }
        }           
        
        task_worker_density /= avail_time;           
    }  

    public static double calculateRPD(double a, double b) {
		double d = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		double result = (Math.pow(a, 3) / Math.pow(b, 2) + 
				Math.pow(b, 3) / Math.pow(a, 2) + 
				d * (3 - Math.pow(a, 2) / Math.pow(b, 2) - Math.pow(b, 2) / Math.pow(a, 2)) + 
				5 * (Math.pow(b, 2) * Math.log((a + d) / b) / a + 
						Math.pow(a, 2) * Math.log((b + d) / a) / b) / 2) / 15;
		
		return result;
	}

	protected void keepExchangingWorkers(ArrayList<WorkerRoute> worker_routes, long appxmt) {
        if (child_with_larger_twd != null && child_with_lesser_twd != null && 
        		child_with_larger_twd.workersAssigned.size() < child_with_larger_twd.tasksInside.size()) { 
        	boolean exchanged = true;
        	
        	while (exchanged && child_with_larger_twd.workersAssigned.size() < child_with_larger_twd.tasksInside.size()) {
                FittestWorker fw = child_with_larger_twd.findFittestWorker(worker_routes, child_with_lesser_twd.workersAssigned);

                if (fw.getWorker() != null) {
                    int array_end = worker_routes.get(fw.getWorker().getIdx()).sequence.size() - 1;
                    long time = worker_routes.get(fw.getWorker().getIdx()).sequence.get(array_end).curTime;
                    
                    double srcAvgDist = calculateRPD(child_with_lesser_twd.getRangeX(appxmt), 
                    		child_with_lesser_twd.getRangeY(appxmt));                    
                    double srcNmntr = child_with_lesser_twd.num_of_undone_tasks * srcAvgDist;
                    double srcPotential = srcNmntr / 
                    		(srcNmntr / child_with_lesser_twd.task_worker_density - 
                    		(child_with_lesser_twd.expir_max - (time + fw.getWorker().distance)));
                    
                    double destAvgDist = calculateRPD(child_with_larger_twd.getRangeX(appxmt), 
                    		child_with_larger_twd.getRangeY(appxmt));                    
                    double destNmntr = child_with_larger_twd.num_of_undone_tasks * destAvgDist;
                    double destPotential = destNmntr / 
                    		(destNmntr / child_with_larger_twd.task_worker_density + 
                    		(child_with_larger_twd.expir_max - (time + fw.displace)));

                    if (destPotential >= srcPotential) {
                    	child_with_lesser_twd.workersAssigned.remove(fw.getWorker());
                    	child_with_lesser_twd.task_worker_density = srcPotential;

                        child_with_larger_twd.workersAssigned.add(fw.getWorker());
                        fw.getWorker().distance = fw.displace;
                        child_with_larger_twd.task_worker_density = destPotential;

                        exchanged = true;
                    } else {
                    	exchanged = false;
                    }
                } else {
                	exchanged = false;
                }
            }
        }        		
    }

	public Workload_Tree(Workload_Tree p, DoublePair ll, DoublePair ur) {
        super(p, ll, ur);

        child_with_larger_twd = null;
        child_with_lesser_twd = null;
	} 
}
