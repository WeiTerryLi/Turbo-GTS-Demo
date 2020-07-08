/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainframe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import algorithm_related.solutions.BLALS_T;
import algorithm_related.solutions.QT_NNH;
import algorithm_related.solutions.QT_NUD;
import algorithm_related.solutions.WBT_NNH;
import algorithm_related.solutions.WBT_NUD;
import common_structures.ExecutionStatistics;
import common_structures.PointOfInterest;
import common_structures.RouteStop;
import common_structures.Task;
import common_structures.Worker;
import common_structures.WorkerRoute;


public class TurboGTS {
    private static int num_of_repeat;
    private static int num_of_tasks;
    private static int num_of_workers;
    private static long departTimeBase;
    private static int departTimeNum;
    private static long departTimeInterval;
    public static long expirBase;
    public static int expirNum;
    public static long expirInterval;
    public static long durBase;
    public static int durNum;
    public static long durInterval;
    private static long speedInMile;
    private static ArrayList<PointOfInterest> pois;
    private static int[] indices;
    public static Random randomGenerator;
    public static ArrayList<ArrayList<Long>> costBtwPois;
    public static final double earth_radius = 6371000.0;
    public static Worker[] workers;
    public static Task[] tasks;
    
    public static long getDurBase() {
    	return durBase;
    }
    
    public static int getDurNum() {
    	return durNum;
    }
    
    public static long getDurInterval() {
    	return durInterval;
    }
    
    private static void shuffle() {
		for (int i = indices.length - 1; i > 0; --i) {
            int randomInt = randomGenerator.nextInt(i + 1);

            int temp = indices[randomInt];
            indices[randomInt] = indices[i];
            indices[i] = temp;
		}
    }
    
    public static long earthSufaceDistCal(double lat1, double lon1, double lat2, double lon2) {
        double ang = Math.pow(Math.sin((lat1 - lat2) * Math.PI / 360), 2) + 
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
                Math.pow(Math.sin((lon1 - lon2) * Math.PI / 360), 2);
        
        return Math.round(earth_radius * 2.0 * Math.atan2(Math.sqrt(ang), 
                Math.sqrt(1.0 - ang)) * 3600.0 / (speedInMile * 1600.0));
    }
    
    public static long getCostBtwPois(int a, int b) {
        return a < b ? costBtwPois.get(b - 1).get(a) : costBtwPois.get(a - 1).get(b);
    }
    
    public static boolean taskCanBeDoneByWorker(Worker wk, Task tsk) {
        return wk.getDepartTime() + getCostBtwPois(wk.getIndexInIndices(), 
                tsk.getIndexInIndices()) + tsk.getDur() <= tsk.getExpir();
    }
    
    public static boolean taskCanBeAccomplished(ArrayList<ArrayList<Long>> costBtwPois, 
            Worker[] workers, int taskId, int dur, int expir) {
        boolean accomplishable = false;
        
        for (int w = 0; w < workers.length; ++w) {
            if (workers[w].getDepartTime() + 
            		getCostBtwPois(workers[w].getIndexInIndices(), taskId) + dur <= expir) {
                accomplishable = true;
                break;
            }
        }
        
        return accomplishable;
    }
    
    public static int binarySearch(double v, int[] array, int begin, int end) {
        if (end <= begin + 1) {
            return begin;
        }
        
        int mid = (begin + end) / 2;
        if (v <= array[mid]) {
            return binarySearch(v, array, begin, mid);
        } else {
            return binarySearch(v, array, mid, end);
        }
    }
    
    /** Get CPU time in nanoseconds. */
    public static long getCpuTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
        return bean.isCurrentThreadCpuTimeSupported( ) ?
            bean.getCurrentThreadCpuTime( ) : 0L;
    }
               
    /**
     * @param args the command line arguments
     * @throws java.io.UnsupportedEncodingException
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        if (args.length == 1) {        
            String inputPath = System.getProperty("user.dir") + File.separator + args[0];
            String[] inputNames = args[0].split("\\.");
            Date currDate = new Date();                
            String outPutFileName = inputNames[0] + "_" + currDate.getTime() + ".txt";
            FileWriter fw = null;
            BufferedWriter bw = null;        

            try (BufferedReader br = new BufferedReader(new FileReader(inputPath))) {
                fw = new FileWriter(outPutFileName);
                bw = new BufferedWriter(fw);

                String line;
                
                if ((line = br.readLine()) != null) {
                    String[] lineStrings = line.trim().split("\\s+");
                    
                    int cnt = 0;
                    num_of_repeat = Integer.parseInt(lineStrings[cnt++]);
                    num_of_tasks = Integer.parseInt(lineStrings[cnt++]);
                    num_of_workers = Integer.parseInt(lineStrings[cnt++]);
                    departTimeBase = Long.parseLong(lineStrings[cnt++]);
                    departTimeNum = Integer.parseInt(lineStrings[cnt++]);
                    departTimeInterval = Long.parseLong(lineStrings[cnt++]);
                    expirBase = Long.parseLong(lineStrings[cnt++]);
                    expirNum = Integer.parseInt(lineStrings[cnt++]);
                    expirInterval = Long.parseLong(lineStrings[cnt++]);
                    durBase = Long.parseLong(lineStrings[cnt++]);
                    durNum = Integer.parseInt(lineStrings[cnt++]);
                    durInterval = Long.parseLong(lineStrings[cnt++]);
                    speedInMile = Long.parseLong(lineStrings[cnt++]);
                    
                    bw.write("Total task No. " + num_of_tasks + " with expiration base " + 
                            expirBase + " and expiration choices " + expirNum + 
                            " and expiration interval " + expirInterval + 
                            " with durBase " + durBase + " and durNum " + durNum + " and durInterval " + 
                            durInterval + " with No. of workers " + num_of_workers + 
                            " with departure time base " + departTimeBase + 
                            " and with departure time choices " + departTimeNum + 
                            " and with departure time interval " + departTimeInterval + 
                            " with speed " + speedInMile + " with repetition times " + 
                            num_of_repeat);                            
                    bw.newLine();
                    
                    bw.flush();

                    pois = new ArrayList<PointOfInterest>(num_of_tasks + num_of_workers);
                    
                    while ((line = br.readLine()) != null) {
                        lineStrings = line.trim().split("\\s+");
                        
                        pois.add(new PointOfInterest(Integer.parseInt(lineStrings[0]), 
                                           Double.parseDouble(lineStrings[1]), 
                                           Double.parseDouble(lineStrings[2])));
                        
                        /* // For I/O test
                        for (int i = 0; i < lineStrings.length; ++i) {
                           bw.write(lineStrings[i] + "\t");                                 
                        }

                        bw.write("\n"); */
                    }
                    
                    /* // For arraylist test
                    bw.write(num_of_repeat + "\t");
                    bw.write(num_of_tasks + "\t");
                    bw.write(num_of_workers + "\t");
                    bw.write(departTimeBase + "\t");
                    bw.write(departTimeNum + "\t");
                    bw.write(departTimeInterval + "\t");
                    bw.write(expirBase + "\t");
                    bw.write(expirNum + "\t");
                    bw.write(expirInterval + "\t");
                    bw.write(durMin + "\t");
                    bw.write(durMax + "\t");
                    bw.write(num_of_cells_per_row + "\t");
                    bw.write(hilbert_cell_scale + "\n");
                    
                    for (int i = 0; i < pois.size(); ++i) {
                        bw.write(pois.get(i).getId() + "\t");
                        bw.write(pois.get(i).getLat() + "\t");
                        bw.write(pois.get(i).getLon() + "\n");
                    } */
                    
                    randomGenerator = new Random(System.currentTimeMillis());
                    
                    if (num_of_workers + num_of_tasks <= pois.size()) {
                        indices = new int[pois.size()];
                    
                        for (int i = 0; i < indices.length; ++i) {
                            indices[i] = i;
                        }

                        costBtwPois = new ArrayList<ArrayList<Long>>(num_of_workers + num_of_tasks - 1);
                        
                        BLALS_T.es = new ExecutionStatistics();
                        QT_NUD.es = new ExecutionStatistics();
                        QT_NNH.es = new ExecutionStatistics();
                        WBT_NUD.es = new ExecutionStatistics();
                        WBT_NNH.es = new ExecutionStatistics();
                        
                        for (int repeat = 0; repeat < num_of_repeat; ++repeat) {
                            shuffle();

                            costBtwPois.clear();

                            for (int i = 0; i < num_of_workers + num_of_tasks - 1; ++i) {
                                costBtwPois.add(new ArrayList<Long>(i + 1));

                                for (int j = 0; j <= i; ++j){
                                    costBtwPois.get(i).add(earthSufaceDistCal(pois.get(indices[i + 1]).getLat(), 
                                            pois.get(indices[i + 1]).getLon(), pois.get(indices[j]).getLat(), 
                                            pois.get(indices[j]).getLon()));
                                }                            
                            }

                            workers = new Worker[num_of_workers];
                            tasks = new Task[num_of_tasks];

                            for (int j = 0; j < num_of_workers; ++j) {
                                long departTime = departTimeBase + 
                                        randomGenerator.nextInt(departTimeNum) * 
                                        departTimeInterval;
                                workers[j] = new Worker(indices[j], 
                                        pois.get(indices[j]).getLat(), 
                                        pois.get(indices[j]).getLon(), j, departTime);
                            }
                            
                            for (int j = 0; j < num_of_tasks; ++j) {
                                long dur = durBase + randomGenerator.nextInt(durNum) * durInterval;
                                long expir = expirBase + randomGenerator.nextInt(expirNum) * expirInterval;

                                tasks[j] = new Task(indices[j + num_of_workers], 
                                        pois.get(indices[j + num_of_workers]).getLat(),
                                        pois.get(indices[j + num_of_workers]).getLon(),
                                        j + num_of_workers, dur, expir);                                        
                            }

                            /*
                            // For the test of workers and tasks arrays.
                            bw.write("Repeat ID " + repeat + "\n");
                            bw.write("Speed: " + speedInMile + "\n");
                            bw.write("--------------------------------------\n");
                            
                            bw.write("Workers: \n");
                            for (int j = 0; j < workers.length; ++j) {
                                bw.write(workers[j].getId() + "\t");                                
                                bw.write(workers[j].getDepartTime() + "\n");
                            }  
                            
                            bw.write("\n");
                            
                            bw.write("Tasks: \n");
                            for (int j = 0; j < tasks.length; ++j) {
                                bw.write(tasks[j].getId() + "\t");
                                bw.write(tasks[j].getDur() + "\t");
                                bw.write(tasks[j].getExpir() + "\n");
                            }
                            
                            bw.write("\n\n\n");
                            */
                                                    
                        /*   
          				// For the test of the shuffled pois and the traveling cost matrix.
                            bw.newLine();
                            bw.write("The shuffled sequence of pois is: ");
                            bw.newLine();
                            for (int indx : indices) {
                                bw.write(indx + "\t");
                            }
                            bw.newLine();
                            
                            bw.newLine();
                            bw.write("The matrix of traveling costs between each pair of pois are: ");
                            bw.newLine();
                            for (ArrayList<Long> arryLst : costBtwPois) {
                                for (long cost : arryLst) {
                                    bw.write(cost + "\t");
                                }
                                bw.newLine();
                            }
                        */    
                            
                            long startTime = getCpuTime();
                            long endTime = getCpuTime();
                            long time_diff = endTime - startTime;
                            int numOfTasksDone = 0;

                            
                            startTime = getCpuTime();
                            QT_NNH qts_nn = new QT_NNH();
                            endTime = getCpuTime();
                            time_diff = endTime - startTime;
                            QT_NNH.es.totalTime += time_diff;
                            
                            numOfTasksDone = 0;
                            for (WorkerRoute worker_route : qts_nn.getWorkerRoutes()) {
                                numOfTasksDone += worker_route.getSequence().size() - 1;
                            }       
                            QT_NNH.es.numOfCompleted += numOfTasksDone;
                            
                            /*
                            bw.newLine();
                            bw.write("QT_NNH completed " + numOfTasksDone + " tasks with CPU time " + 
                            		time_diff + " milliseconds with task sequence: ");  
                            bw.newLine();
                            
                            bw.newLine();
                            wk_count = 0;
                            for (WorkerRoute worker_route : qts_nn.getWorkerRoutes()) {
                            	bw.write("Worker ID: " + wk_count);
                                bw.newLine();
                                
                                for (int rs = 1; rs < worker_route.sequence.size(); ++rs) {
                                	bw.write(tasks[worker_route.sequence.get(rs).getIdxInTasks()].getId() + "\t");
                                }
                                
                                bw.newLine();
                                ++wk_count;
                            }
                            bw.newLine();
*/            
                       
                            
                            
                                                                                  
                            startTime = getCpuTime();
                            BLALS_T blals = new BLALS_T();
                            blals.generateTaskSequences();
                            endTime = getCpuTime();
                            time_diff = endTime - startTime;
                            BLALS_T.es.totalTime += time_diff;
                            
                            numOfTasksDone = 0;
                            for (LinkedList<RouteStop> worker_route : blals.getWorkerRoutes()) {
                                numOfTasksDone += worker_route.size() - 1;
                                for (int i = 1; i < worker_route.size(); ++i) {
                            		RouteStop cur_rs = worker_route.get(i);
                            		int tskId = cur_rs.getIdxInTasks();
                            		
                            		if (cur_rs.curTime > tasks[tskId].getExpir()) {
                            			System.out.println("BLALS_T does not return correct results!");
                                        System.exit(-1);
                            		}
                                }
                            }       
                            BLALS_T.es.numOfCompleted += numOfTasksDone;

/*                            
                            bw.newLine();
                            bw.write("BLALS_T completed " + numOfTasksDone + " tasks with CPU time " + 
                            		time_diff + " milliseconds with task sequence: ");  
                            bw.newLine();
                            
                            bw.newLine();
                            wk_count = 0;
                            for (LinkedList<RouteStop> worker_route : blals.getWorkerRoutes()) {
                            	bw.write("Worker ID: " + wk_count);
                                bw.newLine();
                                
                                ListIterator<RouteStop> wrIter = worker_route.listIterator(1);
                                while (wrIter.hasNext()) {
                                	bw.write(tasks[wrIter.next().getIdxInTasks()].getId() + "\t");
                                }
                                
                                bw.newLine();
                                ++wk_count;
                            }
                            bw.newLine();
*/                         
                         
                            
                            
                            
                                                         
                            startTime = getCpuTime();
                            QT_NUD qts_nud = new QT_NUD();
                            endTime = getCpuTime();
                            time_diff = endTime - startTime;
                            QT_NUD.es.totalTime += time_diff;
                            
                            numOfTasksDone = 0;
                            for (WorkerRoute worker_route : qts_nud.getWorkerRoutes()) {
                                numOfTasksDone += worker_route.getSequence().size() - 1;
                                for (int i = 1; i < worker_route.sequence.size(); ++i) {
                            		RouteStop cur_rs = worker_route.sequence.get(i);
                            		int tskId = cur_rs.getIdxInTasks();
                            		
                            		if (cur_rs.curTime > tasks[tskId].getExpir()) {
                            			System.out.println("QT_NUD does not return correct results!");
                                        System.exit(-1);
                            		}
                                }
                            }       
                            QT_NUD.es.numOfCompleted += numOfTasksDone;
                            
                            
                            /*
                            bw.newLine();
                            bw.write("QT_NUD completed " + numOfTasksDone + " tasks with CPU time " + 
                            		time_diff + " milliseconds with task sequence: ");  
                            bw.newLine();
                           
                            bw.newLine();
                            wk_count = 0;
                            for (WorkerRoute worker_route : qts_nud.getWorkerRoutes()) {
                            	bw.write("Worker ID: " + wk_count);
                                bw.newLine();
                                
                                for (int rs = 1; rs < worker_route.sequence.size(); ++rs) {
                                	bw.write(tasks[worker_route.sequence.get(rs).getIdxInTasks()].getId() + "\t");
                                }
                                
                                bw.newLine();
                                ++wk_count;
                            }
                            bw.newLine();
*/                            

                            
                                                        

                            
                            startTime = getCpuTime();
                            WBT_NUD sbt_d_naw_nud_aic = 
                            		new WBT_NUD();
                            endTime = getCpuTime();
                            time_diff = endTime - startTime;
                            WBT_NUD.es.totalTime += time_diff;
                            
                            numOfTasksDone = 0;
                            for (WorkerRoute worker_route : sbt_d_naw_nud_aic.getWorkerRoutes()) {
                                numOfTasksDone += worker_route.getSequence().size() - 1;
                                for (int i = 1; i < worker_route.sequence.size(); ++i) {
                            		RouteStop cur_rs = worker_route.sequence.get(i);
                            		int tskId = cur_rs.getIdxInTasks();
                            		
                            		if (cur_rs.curTime > tasks[tskId].getExpir()) {
                            			System.out.println("WBT_NUD does not return correct results!");
                                        System.exit(-1);
                            		}
                                }
                            }       
                            WBT_NUD.es.numOfCompleted += numOfTasksDone;
                            
                            
                            
                            
                           
                            startTime = getCpuTime();
                            WBT_NNH sbt_d_naw_nn = new WBT_NNH();
                            endTime = getCpuTime();
                            time_diff = endTime - startTime;
                            WBT_NNH.es.totalTime += time_diff;
                            
                            numOfTasksDone = 0;
                            for (WorkerRoute worker_route : sbt_d_naw_nn.getWorkerRoutes()) {
                                numOfTasksDone += worker_route.getSequence().size() - 1;
                                for (int i = 1; i < worker_route.sequence.size(); ++i) {
                            		RouteStop cur_rs = worker_route.sequence.get(i);
                            		int tskId = cur_rs.getIdxInTasks();
                            		
                            		if (cur_rs.curTime > tasks[tskId].getExpir()) {
                            			System.out.println("WBT_NNH does not return correct results!");
                                        System.exit(-1);
                            		}
                                }
                            }       
                            WBT_NNH.es.numOfCompleted += numOfTasksDone;

                            
                           
                        }
                    
                           
                        bw.newLine();
                        bw.write("The number of tasks accomplished by BLALS_T is " + 
                                BLALS_T.es.numOfCompleted + 
                                " with CPU time " + BLALS_T.es.totalTime / 1000000 + " milliseconds.");  
                        bw.newLine(); 

                        
                        
                        
                       
                        bw.newLine();
                        bw.write("The number of tasks accomplished by QT_NUD is " + 
                                QT_NUD.es.numOfCompleted + 
                                " with CPU time " + QT_NUD.es.totalTime / 1000000 + " milliseconds.");
                        bw.newLine();


                                               
                       
                        bw.newLine();
                        bw.write("The number of tasks accomplished by QT_NNH is " + 
                                QT_NNH.es.numOfCompleted + 
                                " with CPU time " + QT_NNH.es.totalTime / 1000000 + " milliseconds.");
                        bw.newLine();
                        
                        
                     

                        
                        bw.newLine();
                        bw.write("The number of tasks accomplished by WBT_NUD is " + 
                        		WBT_NUD.es.numOfCompleted + 
                                " with CPU time " + WBT_NUD.es.totalTime / 1000000 + " milliseconds.");
                        bw.newLine();
                        

                        
                        
                     
                        bw.newLine();
                        bw.write("The number of tasks accomplished by WBT_NNH is " + 
                                WBT_NNH.es.numOfCompleted + 
                                " with CPU time " + WBT_NNH.es.totalTime / 1000000 + " milliseconds.");
                        bw.newLine();


                        
                    } else {
                        System.out.println("The number of POIs is less than the number "
                                + "of workers plus the number of tasks!");
                    }
                }
                
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }

                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            System.out.println("Input file name not specified!");
        }
    }
    
}
