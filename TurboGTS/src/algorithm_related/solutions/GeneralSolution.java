package algorithm_related.solutions;

import static algorithm_related.utilities.MinMaxLatLon.findBorderLatLon;
import static algorithm_related.utilities.MinMaxLatLon.findBorderLatLonInSet;
import static algorithm_related.utilities.Clustering.iterativeClustering_local;
import static mainframe.TurboGTS.earthSufaceDistCal;
import static mainframe.TurboGTS.getCostBtwPois;
import static mainframe.TurboGTS.tasks;
import static mainframe.TurboGTS.workers;

import java.util.ArrayList;
import java.util.Arrays;

import static mainframe.TurboGTS.durBase;
import static mainframe.TurboGTS.durInterval;
import static mainframe.TurboGTS.durNum;
import static mainframe.TurboGTS.expirBase;
import static mainframe.TurboGTS.expirInterval;
import static mainframe.TurboGTS.expirNum;

import algorithm_related.minor_structures.DoublePair;
import algorithm_related.utilities.MinMaxLatLon;
import common_structures.RouteStop;
import common_structures.WorkerRoute;

abstract public class GeneralSolution {
	protected final ArrayList<WorkerRoute> worker_routes;
	protected final boolean[] taskUndone;
    protected final long appxmt;
    protected final DoublePair lowerLeft;
    protected final DoublePair upperRight;

    private boolean findNearestNeighbor(ArrayList<Integer> taskList, int wk) {
	    int choice = -1;
	    long min_cost = Long.MAX_VALUE;
	    int array_end = worker_routes.get(wk).sequence.size() - 1;
	    RouteStop cur_stop = worker_routes.get(wk).sequence.get(array_end);
	    
	    for (int tsk : taskList) {
	        if (taskUndone[tsk] && worker_routes.get(wk).ableToBeDone[tsk]) {
	            long cost = getCostBtwPois(cur_stop.getPosInIndices(), 
	                    tasks[tsk].getIndexInIndices()) + tasks[tsk].getDur();
	
	            if (cur_stop.curTime + cost <= tasks[tsk].getExpir()) {
	                if (cost < min_cost) {
	                    min_cost = cost;
	                    choice = tsk;
	                }
	            } else {
	                worker_routes.get(wk).ableToBeDone[tsk] = false;
	            }
	        }
	    }
	    
	    if (choice >= 0) {
	        worker_routes.get(wk).sequence.add(new RouteStop(tasks[choice].getIndexInIndices(), 
	            choice, cur_stop.curTime + min_cost));
	        taskUndone[choice] = false;
	        
	        return true;
	    } else {
	        return false;
	    }
	}

	protected boolean findLargestNUD(ArrayList<Integer> taskList, int wk) {
		int choice = -1;
		double max_nud = -1.0;
	    int array_end = worker_routes.get(wk).sequence.size() - 1;
	    RouteStop cur_stop = worker_routes.get(wk).sequence.get(array_end);
	    
	    for (int tsk : taskList) {
	    	if (taskUndone[tsk] && worker_routes.get(wk).ableToBeDone[tsk]) {
	    		long cur_time = cur_stop.curTime + getCostBtwPois(cur_stop.getPosInIndices(), 
	                    tasks[tsk].getIndexInIndices()) + tasks[tsk].getDur();
	
	            if (cur_time <= tasks[tsk].getExpir()) {
	            	double nud = 0.0;
	            	
	            	for (int otherTsk : taskList) {
	            		if (taskUndone[otherTsk] && 
	            				worker_routes.get(wk).ableToBeDone[otherTsk] && otherTsk != tsk) {
	            			long dist = getCostBtwPois(tasks[tsk].getIndexInIndices(), 
	                                tasks[otherTsk].getIndexInIndices());
	            			
	            			double surplus = tasks[otherTsk].getExpir() - tasks[otherTsk].getDur() - 
	            					dist - cur_time;
	            			
	            			if (surplus >= 0.0) {
	                            double dur = tasks[otherTsk].getDur();
	                            double weight = dur > 0.0 ? 1.0 / dur : 1;
	            				nud += surplus * weight + 1;
	            			}
	            		}
	            	}
	            	
	            	if (nud > max_nud) {
	            		max_nud = nud;
	            		choice = tsk;
	            	}
	            } else {
	                worker_routes.get(wk).ableToBeDone[tsk] = false;
	            }
	    	}
	    }
	    
	    if (choice >= 0) {
	    	long cost = getCostBtwPois(cur_stop.getPosInIndices(), 
	                tasks[choice].getIndexInIndices()) + tasks[choice].getDur();
	    	
	        worker_routes.get(wk).sequence.add(new RouteStop(tasks[choice].getIndexInIndices(), 
	            choice, cur_stop.curTime + cost));
	        taskUndone[choice] = false;
	        
	        return true;
	    } else {
	        return false;
	    }
	}  
	  
	protected void taskSequencingNN(ArrayList<Integer> taskList, int wk) {
		while (findNearestNeighbor(taskList, wk));
	}

	protected void taskSequencingNUD(ArrayList<Integer> taskList, int wk) {
		while (findLargestNUD(taskList, wk));
	}

	protected void taskSequencingNUD_Adapt_IC(ArrayList<Integer> taskList, int wk) {
		if (taskList.size() > 200) {
	    	long tmp_eps = 0L;	
			long max_tmp_tc = 0L;
			
			for (int tsk = 0; tsk < taskList.size(); ++tsk){
	    		for (int tsk2 = tsk + 1; tsk2 < taskList.size(); ++tsk2) {
    				long tmp = getCostBtwPois(tasks[tsk].getIndexInIndices(), tasks[tsk2].getIndexInIndices());
    				tmp_eps += tmp;
			
					if (max_tmp_tc < tmp) {
						max_tmp_tc = tmp;
					}
	    		}
			}
			
			tmp_eps /= taskList.size() * (taskList.size() + 1L) / 2L;
			
	    	double nb_cnt_avg = 0.0;
	    	
	    	for (int tsk : taskList) {
	    		for (int tsk2 : taskList) {
	    			if (tsk != tsk2) {
	    				if (getCostBtwPois(tasks[tsk].getIndexInIndices(), 
	    						tasks[tsk2].getIndexInIndices()) <= tmp_eps) {
	    					++nb_cnt_avg;
	    				}
	    			}
	    		}
	    	}
	    	
	    	nb_cnt_avg /= taskList.size();
			
	    	double tmp_ddr = Math.pow(max_tmp_tc, 2) * nb_cnt_avg * 
	    			(Math.sqrt(Math.pow((2 * durBase - durInterval), 2) + 
	    			8 * ((expirBase + (expirNum - 1) * expirInterval / 2) - tmp_eps) * 
	    			durNum / nb_cnt_avg) - (2 * durBase - durInterval)) / 
	    			(Math.pow(2 * tmp_eps, 2) * 2 * durInterval * durNum * taskList.size());
	    	
	    	int tmp_minPts = (int) (nb_cnt_avg * tmp_ddr);
	    		
			ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> outliers = new ArrayList<Integer>();
	    	
	        iterativeClustering_local(taskList, clusters, outliers, tmp_eps, tmp_minPts, tmp_ddr);  
	        
	        for (ArrayList<Integer> clst : clusters) {
	        	while (findLargestNUD(clst, wk));
	        }
			
	        while (findLargestNUD(outliers, wk));
		} else {
			while (findLargestNUD(taskList, wk));
		}
	}

	protected GeneralSolution() {
    	taskUndone = new boolean[tasks.length];
        Arrays.fill(taskUndone, true);
                        
        worker_routes = new ArrayList<WorkerRoute>(workers.length);

        for (int i = 0; i < workers.length; ++i) {
            worker_routes.add(new WorkerRoute(tasks.length));
            worker_routes.get(i).sequence.add(new RouteStop(workers[i].getIndexInIndices(), 
                    -1, workers[i].getDepartTime()));
        }        

        MinMaxLatLon borderLatLon = findBorderLatLon();
        
        long trueDist = earthSufaceDistCal(borderLatLon.minLat, borderLatLon.minLon, 
                borderLatLon.maxLat, borderLatLon.maxLon);
        appxmt = Math.round(trueDist / Math.sqrt(Math.pow(borderLatLon.maxLat - borderLatLon.minLat, 2) + 
                Math.pow(borderLatLon.maxLon - borderLatLon.minLon, 2)));
        
        lowerLeft = new DoublePair(borderLatLon.minLon, borderLatLon.minLat);
        upperRight = new DoublePair(borderLatLon.maxLon, borderLatLon.maxLat);
    }

	protected GeneralSolution(ArrayList<WorkerRoute> wrs, ArrayList<Integer> taskList) {
    	taskUndone = new boolean[tasks.length];
        Arrays.fill(taskUndone, false);
        
        for (int i : taskList) {
        	taskUndone[i] = true;
        }
                        
        worker_routes = wrs;

        MinMaxLatLon borderLatLon = findBorderLatLonInSet(taskList);
        
        long trueDist = earthSufaceDistCal(borderLatLon.minLat, borderLatLon.minLon, 
                borderLatLon.maxLat, borderLatLon.maxLon);
        appxmt = Math.round(trueDist / Math.sqrt(Math.pow(borderLatLon.maxLat - borderLatLon.minLat, 2) + 
                Math.pow(borderLatLon.maxLon - borderLatLon.minLon, 2)));
        
        lowerLeft = new DoublePair(borderLatLon.minLon, borderLatLon.minLat);
        upperRight = new DoublePair(borderLatLon.maxLon, borderLatLon.maxLat);
    }

    public ArrayList<WorkerRoute> getWorkerRoutes() {
        return worker_routes;
    }
}
