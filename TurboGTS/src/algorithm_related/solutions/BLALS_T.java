/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm_related.solutions;

import static mainframe.TurboGTS.getCostBtwPois;
import static mainframe.TurboGTS.taskCanBeDoneByWorker;
import static mainframe.TurboGTS.tasks;
import static mainframe.TurboGTS.workers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import algorithm_related.minor_structures.IntLongPair;
import algorithm_related.minor_structures.IntegerPair;
import common_structures.ExecutionStatistics;
import common_structures.RouteStop;

public class BLALS_T {
    private long threshold;
    private final ArrayList<ArrayList<Integer>> tasksOfWorkers;
    private final ArrayList<ArrayList<Integer>> forbiddenWorkers;
    private final LinkedList<Partition> partitions;
    private final boolean[] taskUndone;
    private final ArrayList<LinkedList<RouteStop>> worker_routes;
    public static ExecutionStatistics es;
    
    private static class Partition {
        LinkedList<Integer> partitionWorkerSet;
        LinkedList<Integer> partitionTaskSet;
        long workload;
        Partition parent = null;
        Partition leftChild = null;
        Partition rightChild = null;
        
        Partition(LinkedList<Integer> pws, LinkedList<Integer> pts, long wkld) {
            partitionWorkerSet = pws;
            partitionTaskSet = pts;
            workload = wkld;
        }
    }    
  
    private long workloadCal(LinkedList<Integer> curW, LinkedList<Integer> curS) {
        long wkld = 0L;
        
        for (int wk : curW) {
            for (int tsk : curS) {
                if (taskCanBeDoneByWorker(workers[wk], tasks[tsk])) {
                    ++wkld;
                }
            }
        }
        
        return wkld;
    }
    
    private IntegerPair findNextClosestTask(LinkedList<Integer> taskList, int idxInTasks, 
            boolean[] toBePartitioned) {
        long minDist = Long.MAX_VALUE;
        int clsTskIdx = -1;
        int closestTask = -1;
    //    int t = taskList.get(idxInTaskList);
        
        ListIterator<Integer> iTaskList = taskList.listIterator();
        while (iTaskList.hasNext()) {
    //    for (int tsk : taskList) {
            int i = iTaskList.nextIndex();
            int tsk = iTaskList.next();
            if ((tsk != idxInTasks) && (toBePartitioned[i])) {
                int a = tasks[idxInTasks].getIndexInIndices();
                int b = tasks[tsk].getIndexInIndices();                 
                long dist = getCostBtwPois(a, b);
                
                if (dist < minDist) {
                    minDist = dist;
                    clsTskIdx = i;
                    closestTask = tsk;
                }
            }
        }
        
        return new IntegerPair(clsTskIdx, closestTask);
    }
    
    private void leftRightPartition(Partition partition) {
        //long wkld = 0L;
        
        LinkedList<Integer> pWorkerSet = new LinkedList<Integer>();
        LinkedList<Integer> pTaskSet = new LinkedList<Integer>();
        LinkedList<Integer> workerSet = new LinkedList<Integer>();
        LinkedList<Integer> taskSet = new LinkedList<Integer>();
        
        boolean[] workersNotPartitioned = new boolean[partition.partitionWorkerSet.size()];
        Arrays.fill(workersNotPartitioned, true);
        
        boolean[] tasksNotPartitioned = new boolean[partition.partitionTaskSet.size()];
        Arrays.fill(tasksNotPartitioned, true);
        
        int s = partition.partitionTaskSet.getFirst();
        taskSet.add(s);
        tasksNotPartitioned[0] = false;
        
        while (true) {
        	boolean workersAreFull = false;
        	
            for (int tsk : taskSet) {
            	ListIterator<Integer> pwsLstItr = partition.partitionWorkerSet.listIterator();            	
                while (pwsLstItr.hasNext()) {
                	int wk_idx = pwsLstItr.nextIndex();
                	int wk = pwsLstItr.next();                	
                    if (workersNotPartitioned[wk_idx] && taskCanBeDoneByWorker(workers[wk], tasks[tsk])) {
                        workerSet.add(wk);
                        pWorkerSet.add(wk);
                        workersNotPartitioned[wk_idx] = false;
                        //++wkld;
                        
                        if (pWorkerSet.size() >= partition.partitionWorkerSet.size() / 2) {
                        	workersAreFull = true;
                        	break;
                        }
                    }
                }
                
                if (workersAreFull) {
                	break;
                }
            }
            
            taskSet.clear();
            

        	boolean tasksAreFull = false;        	
            
            for (int wk : workerSet) {
            	ListIterator<Integer> ptsLstItr = partition.partitionTaskSet.listIterator();
                while (ptsLstItr.hasNext()) {
                	int tsk_idx = ptsLstItr.nextIndex();
                	int tsk = ptsLstItr.next();                	
                	if (tasksNotPartitioned[tsk_idx] && taskCanBeDoneByWorker(workers[wk], tasks[tsk])) {
                        taskSet.add(tsk);
                        pTaskSet.add(tsk);
                        tasksNotPartitioned[tsk_idx] = false;
                        
                        if (pTaskSet.size() >= partition.partitionTaskSet.size() / 2) {
                        	tasksAreFull = true;
                        	break;
                        }
                    }
                }
                
                if (tasksAreFull) {
                	break;
                }
            }
            
            workerSet.clear();
            
            if (workersAreFull || tasksAreFull) {
                LinkedList<Integer> pTaskSet2 = new LinkedList<Integer>();
                LinkedList<Integer> pWorkerSet2 = new LinkedList<Integer>();
                //long wkld2 = 0L;
                
                ListIterator<Integer> ptsLstItr = partition.partitionTaskSet.listIterator();
                while (ptsLstItr.hasNext()) {
                	int tsk_idx = ptsLstItr.nextIndex(); 
                	int tsk = ptsLstItr.next();               	
                    if (tasksNotPartitioned[tsk_idx]) {
                        pTaskSet2.add(tsk);
                        tasksNotPartitioned[tsk_idx] = false;
                        
                        ListIterator<Integer> pwsLstItr = partition.partitionWorkerSet.listIterator();            	
                        while (pwsLstItr.hasNext()) {
                        	int wk_idx = pwsLstItr.nextIndex();
                        	int wk = pwsLstItr.next();
                            if (workersNotPartitioned[wk_idx] && 
                            		taskCanBeDoneByWorker(workers[wk], tasks[tsk])) {
                                pWorkerSet2.add(wk);
                                workersNotPartitioned[wk_idx] = false;
                                //++wkld2;
                            }
                        }
                    }
                }                
                
                partitions.add(new Partition(pWorkerSet, pTaskSet, workloadCal(pWorkerSet, pTaskSet)));
                partitions.getLast().parent = partition;
                partition.leftChild = partitions.getLast();

                partitions.add(new Partition(pWorkerSet2, pTaskSet2, workloadCal(pWorkerSet2, pTaskSet2)));
                partitions.getLast().parent = partition;
                partition.rightChild = partitions.getLast();            
                
                break;
            } 
            
            if (taskSet.isEmpty()) {
            	IntegerPair ip = findNextClosestTask(partition.partitionTaskSet, s, 
                		tasksNotPartitioned);
                
                if (ip.X >= 0 && ip.Y >= 0) {
                    taskSet.add(ip.Y);
                    tasksNotPartitioned[ip.X] = false;
                } else {
                    break;
                }                
            }
        }
    }
    
    private void RecursiveBisection(Partition partition) {
        leftRightPartition(partition);

        if (partition.leftChild != null) {
            if (partition.leftChild.workload > threshold) {
                RecursiveBisection(partition.leftChild);
            }            
        }

        if (partition.rightChild != null) {
            if (partition.rightChild.workload > threshold) {
                RecursiveBisection(partition.rightChild);
            }            
        }
    }
    
    private IntLongPair findBestInsertionPlace(int w, int t) {
        long delay;
        long minDelay = Long.MAX_VALUE; 
        int bestPos = -1;
        
        ListIterator<RouteStop> iWorkerRoute = worker_routes.get(w).listIterator();
        while (iWorkerRoute.hasNext()) {
            int pos = iWorkerRoute.nextIndex();
            RouteStop rs = iWorkerRoute.next();
            delay = getCostBtwPois(rs.getPosInIndices(), 
                        tasks[t].getIndexInIndices()) + tasks[t].getDur();
            
            if (rs.curTime + delay <= tasks[t].getExpir()) {
                boolean pass = true;
                
                if (iWorkerRoute.hasNext()) {
                    RouteStop rs_next = iWorkerRoute.next();
                    
                    delay += getCostBtwPois(tasks[t].getIndexInIndices(), rs_next.getPosInIndices()) - 
                    			getCostBtwPois(rs.getPosInIndices(), rs_next.getPosInIndices()); 

                    ListIterator<RouteStop> i2WorkerRoute = worker_routes.get(w).listIterator(pos + 1);
                    while (i2WorkerRoute.hasNext()) {
                        RouteStop rs2 = i2WorkerRoute.next();
                        
                        if (rs2.curTime + delay > tasks[rs2.getIdxInTasks()].getExpir()) {
                            pass = false;                    
                            break;
                        }
                    }
                }

                if (pass && (delay < minDelay)) {
                    minDelay = delay;
                    bestPos = pos;
                } 
            } else {
                break;
            }
        }
        
        return new IntLongPair(bestPos, minDelay);
    }
    
    private void insertionScheduling(int w, ArrayList<Integer> taskList) {
        while (true) {
            ArrayList<IntLongPair> bestPosArray = new ArrayList<IntLongPair>();
            
            for (int tsk : taskList) {
                if (taskUndone[tsk]) {
                    bestPosArray.add(findBestInsertionPlace(w, tsk));   
                    bestPosArray.get(bestPosArray.size() - 1).idxInTasks = tsk;
                }
            }

            long miniDelay = Long.MAX_VALUE;
            int taskChosen = -1;
            int position = -1;
            for (IntLongPair pair : bestPosArray) {
                if (pair.delay < miniDelay) {
                    miniDelay = pair.delay;
                    taskChosen = pair.idxInTasks;
                    position = pair.pos;
                }
            }
            
            if (taskChosen >= 0) {
                long cost = getCostBtwPois(worker_routes.get(w).get(position).getPosInIndices(), 
                        tasks[taskChosen].getIndexInIndices()) + tasks[taskChosen].getDur();
                long time = worker_routes.get(w).get(position).curTime;
                
                worker_routes.get(w).add(position + 1, new RouteStop(tasks[taskChosen].getIndexInIndices(), 
                        taskChosen, time + cost));
                
                ListIterator<RouteStop> iWorkerRoute = worker_routes.get(w).listIterator(position + 2);
                while (iWorkerRoute.hasNext()) {
                    iWorkerRoute.next().curTime += miniDelay;
                }
                
                taskUndone[taskChosen] = false;                
            } else {
                break;
            }
        }       
    }
    
    private void nearestNeighborAssign(LinkedList<Integer> workerSet, LinkedList<Integer> taskSet) {
        for (int tsk : taskSet) {
            long miniDist = Long.MAX_VALUE;
            int chosenWK = -1;
                
            for (int wk : workerSet) {
                if (!forbiddenWorkers.get(tsk).contains(wk)) {
                    if (taskCanBeDoneByWorker(workers[wk], tasks[tsk])) {
                        int a = workers[wk].getIndexInIndices();
                        int b = tasks[tsk].getIndexInIndices();                 
                        long dist = getCostBtwPois(a, b);

                        if (dist < miniDist) {
                            miniDist = dist;
                            chosenWK = wk;
                        }
                    } else {
                        forbiddenWorkers.get(tsk).add(wk);
                    }
                }
            }
            
            if (chosenWK >= 0) {
                tasksOfWorkers.get(chosenWK).add(tsk);
            }
        }
    }
    
    private void globalAssignLocalSchedule(Partition partition) {
        LinkedList<Integer> workerSet = partition.partitionWorkerSet;
        LinkedList<Integer> taskSet = partition.partitionTaskSet;
                
        while (!taskSet.isEmpty()) {
            nearestNeighborAssign(workerSet, taskSet);
            
            for (int wk : workerSet) {
                insertionScheduling(wk, tasksOfWorkers.get(wk));
                
                ListIterator<Integer> iTOW = tasksOfWorkers.get(wk).listIterator();
                while (iTOW.hasNext()) {
                    int tsk = iTOW.next();
                    
                    if (taskUndone[tsk]) {
                        forbiddenWorkers.get(tsk).add(wk);
                    }
                        
                    iTOW.remove();
                }
            }
            
            ListIterator<Integer> iTS = taskSet.listIterator();
            while (iTS.hasNext()) {
                int tsk = iTS.next();
                
                if (!taskUndone[tsk]) {
                    forbiddenWorkers.get(tsk).clear();
                    iTS.remove();
                }
                
                if (forbiddenWorkers.get(tsk).size() == workerSet.size()) {
                    forbiddenWorkers.get(tsk).clear();
                    iTS.remove();
                }
            }
        }
        
        for (int wk : workerSet) {
            tasksOfWorkers.get(wk).clear();
        }
        
        partition.workload = workloadCal(partition.partitionWorkerSet, partition.partitionTaskSet);
    }
    
    private void bottomUpMerging() {
        while (partitions.size() > 1) {
            Partition partitionRight = partitions.getLast();
            Partition partitionParent = partitionRight.parent;
            Partition partitionLeft = partitionParent.leftChild;
            
            long wkld = partitionRight.workload + partitionLeft.workload + 
                    workloadCal(partitionRight.partitionWorkerSet, partitionLeft.partitionTaskSet) + 
                    workloadCal(partitionLeft.partitionWorkerSet, partitionRight.partitionTaskSet); 
                    
            if (wkld > threshold) {
                globalAssignLocalSchedule(partitionRight);
                globalAssignLocalSchedule(partitionLeft);
            }   
            
            ListIterator<Integer> iTaskSet = partitionParent.partitionTaskSet.listIterator();
            while (iTaskSet.hasNext()) {
                int tsk = iTaskSet.next();

                if (!partitionLeft.partitionTaskSet.contains(tsk) && !partitionRight.partitionTaskSet.contains(tsk)) {
                    iTaskSet.remove();
                }
            }

            partitionParent.workload = partitionRight.workload + partitionLeft.workload + 
                workloadCal(partitionRight.partitionWorkerSet, partitionLeft.partitionTaskSet) + 
                workloadCal(partitionLeft.partitionWorkerSet, partitionRight.partitionTaskSet); 
            
            partitions.removeLast();
            partitionParent.rightChild = null;
            
            partitions.removeLast();
            partitionParent.leftChild = null;
        }
    }
    
    public BLALS_T() {  
     //   threshold = thresholdCal();
        
        tasksOfWorkers = new ArrayList<ArrayList<Integer>>(workers.length);
        for (int i = 0; i < workers.length; ++i) {
            tasksOfWorkers.add(new ArrayList<Integer>());
        }
        
        forbiddenWorkers = new ArrayList<ArrayList<Integer>>(tasks.length);
        for (int i = 0; i < tasks.length; ++i) {
            forbiddenWorkers.add(new ArrayList<Integer>());
        }
        
        partitions = new LinkedList<Partition>();
       
        taskUndone = new boolean[tasks.length];
        Arrays.fill(taskUndone, true);
         
        worker_routes = new ArrayList<LinkedList<RouteStop>>(workers.length);
        for (int i = 0; i < workers.length; ++i) {
            worker_routes.add(new LinkedList<RouteStop>());
            worker_routes.get(i).add(new RouteStop(workers[i].getIndexInIndices(), -1, 
                    workers[i].getDepartTime()));
        }   
    }
    
    public ArrayList<LinkedList<RouteStop>> getWorkerRoutes() {
        return worker_routes;
    }
    
    public void generateTaskSequences() {
        LinkedList<Integer> curW = new LinkedList<Integer>();
        LinkedList<Integer> curS = new LinkedList<Integer>();
        
        threshold = 0;
        for (int i = 0; i < workers.length; ++i) {
            curW.add(i);
            
            for (int j = 0; j < tasks.length; ++j) {
                if (taskCanBeDoneByWorker(workers[i], tasks[j])) {
                	++threshold;
                	
	                if (!curS.contains(j)) {
	                    curS.add(j);                    
	                }
                }
            }
        }

  //      threshold = tasks.length / workers.length;
        threshold /= 100;
   
        threshold = threshold > 30000 ? threshold : 30000;
        
        partitions.add(new Partition(curW, curS, workloadCal(curW, curS)));
        while (partitions.getFirst().workload > threshold) {
            RecursiveBisection(partitions.getFirst());
            
            if (partitions.size() == 1) {
                break;
            }
            
            bottomUpMerging();
        }
        
        if (partitions.getFirst().workload > 0) {
            globalAssignLocalSchedule(partitions.getFirst());
        }
    }
}
