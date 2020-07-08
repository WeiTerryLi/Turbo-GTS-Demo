package algorithm_related.utilities;

import static mainframe.TurboGTS.getCostBtwPois;
import static mainframe.TurboGTS.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class Clustering {    
    private static ArrayList<ArrayList<Integer>> durCategorize(double ddr) {               
        int cat = (int) Math.ceil(1.0 / ddr);
        
        ArrayList<Long> thresDur = new ArrayList<Long>(cat);
        
        long maxDur = 0;
        for (int tsk = 0; tsk < tasks.length; ++tsk) {
        	if (tasks[tsk].getDur() > maxDur) {
        		maxDur = tasks[tsk].getDur();
        	}
        }
        
        long minDur = maxDur;
        for (int tsk = 0; tsk < tasks.length; ++tsk) {
        	if (tasks[tsk].getDur() < minDur) {
        		minDur = tasks[tsk].getDur();
        	}
        }
        
        if (maxDur > minDur) {
        	for (int i = 0; i < cat - 1; ++i) {
        		thresDur.add(minDur + Math.round((i + 1) * ddr * (maxDur - minDur)));
        	}
        }
    	
    	thresDur.add(maxDur);
        
    	ArrayList<ArrayList<Integer>> divisions = new ArrayList<ArrayList<Integer>>();     
        for (int i = 0; i < thresDur.size(); ++i) {
        	divisions.add(new ArrayList<Integer>());
        }
        
        for (int tsk = 0; tsk < tasks.length; ++tsk) {  
        	int j = 0; 
        	for (; j < thresDur.size() - 1 && tasks[tsk].getDur() > thresDur.get(j); ++j);
        	
        	divisions.get(j).add(tsk);
        }
        
        return divisions;
    }
    
    private static ArrayList<ArrayList<Integer>> durCategorize_local(ArrayList<Integer> taskList, 
    		double ddr) {               
        int cat = (int) Math.ceil(1.0 / ddr);
        
        ArrayList<Long> thresDur = new ArrayList<Long>(cat);
        
        long maxDur = 0;
        for (int tsk = 0; tsk < taskList.size(); ++tsk) {
        	if (tasks[taskList.get(tsk)].getDur() > maxDur) {
        		maxDur = tasks[taskList.get(tsk)].getDur();
        	}
        }
        
        long minDur = maxDur;
        for (int tsk = 0; tsk < taskList.size(); ++tsk) {
        	if (tasks[taskList.get(tsk)].getDur() < minDur) {
        		minDur = tasks[taskList.get(tsk)].getDur();
        	}
        }
        
        if (maxDur > minDur) {
        	for (int i = 0; i < cat - 1; ++i) {
        		thresDur.add(minDur + Math.round((i + 1) * ddr * (maxDur - minDur)));
        	}
        }
    	
    	thresDur.add(maxDur);
        
    	ArrayList<ArrayList<Integer>> divisions = new ArrayList<ArrayList<Integer>>();     
        for (int i = 0; i < thresDur.size(); ++i) {
        	divisions.add(new ArrayList<Integer>());
        }
        
        for (int tsk = 0; tsk < taskList.size(); ++tsk) {  
        	int j = 0; 
        	for (; j < thresDur.size() - 1 && tasks[taskList.get(tsk)].getDur() > thresDur.get(j); ++j);
        	
        	divisions.get(j).add(tsk);
        }
        
        return divisions;
    }
    
    private static int categoryDBSCAN(ArrayList<ArrayList<Integer>> divisions, int[] clstIds, 
    		long eps, int minPts) {
    	int clstCnt = 0;
		ArrayList<Integer> remains = new ArrayList<Integer>();
		
		int len = divisions.size();
		ListIterator<ArrayList<Integer>> liDvsns = divisions.listIterator();
    	while (liDvsns.nextIndex() < len - 1) {
    		ArrayList<Integer> dvsn = liDvsns.next();
    		dvsn.addAll(remains);
    		for (int tsk : dvsn) {
    			if (clstIds[tsk] < 0) {
    				clstIds[tsk] = 0;
    				
    				ArrayList<Integer> nghbs = new ArrayList<Integer>();
    				for (int tsk2 : dvsn) {
    					if (tsk2 != tsk) {
    						long dist = getCostBtwPois(tasks[tsk2].getIndexInIndices(), 
    		                        tasks[tsk].getIndexInIndices());
    						if (dist <= eps) {
    							nghbs.add(tsk2);
    						}
    					}
    				}
    				
    				if (nghbs.size() >= minPts) {
    					clstIds[tsk] = ++clstCnt;
    					
    					ListIterator<Integer> liNB = nghbs.listIterator();
    					while (liNB.hasNext()) {
    						int nb = liNB.next();
    						if (clstIds[nb] < 0) {
    							ArrayList<Integer> nghbs2 = new ArrayList<Integer>();
    							for (int tsk3 : dvsn) {
    								if (tsk3 != nb) {
    									long dist = getCostBtwPois(tasks[tsk3].getIndexInIndices(), 
    		    		                        tasks[nb].getIndexInIndices());
    									if (dist <= eps) {
    										nghbs2.add(tsk3);
    									}
    								}
    							}
    							
    							if (nghbs2.size() >= minPts) {
    								for (int nb2 : nghbs2) {
    									if (!nghbs.contains(nb2)) {
    										liNB.add(nb2);
    										liNB.previous();
    									}
    								}
    							}
    						}
    						
    						if (clstIds[nb] <= 0) {
    							clstIds[nb] = clstCnt;
    						}
    					}
    				}
    			}
    		}
    		
    		remains.clear();
    		for (int tsk : dvsn) {
    			if (clstIds[tsk] <= 0) {
    				remains.add(tsk);
    				clstIds[tsk] = -1;
    			}
    		}
    	}
    	
    	return clstCnt;
    }

    private static int categoryDBSCAN_local(ArrayList<Integer> taskList, 
    		ArrayList<ArrayList<Integer>> divisions, int[] clstIds, long eps, int minPts) {
    	int clstCnt = 0;
		ArrayList<Integer> remains = new ArrayList<Integer>();
		
		int len = divisions.size();
		ListIterator<ArrayList<Integer>> liDvsns = divisions.listIterator();
    	while (liDvsns.nextIndex() < len - 1) {
    		ArrayList<Integer> dvsn = liDvsns.next();
    		dvsn.addAll(remains);
    		for (int tsk : dvsn) {
    			if (clstIds[tsk] < 0) {
    				clstIds[tsk] = 0;
    				
    				ArrayList<Integer> nghbs = new ArrayList<Integer>();
    				for (int tsk2 : dvsn) {
    					if (tsk2 != tsk) {
    						long dist = getCostBtwPois(tasks[taskList.get(tsk2)].getIndexInIndices(), 
    		                        tasks[taskList.get(tsk)].getIndexInIndices());
    						if (dist <= eps) {
    							nghbs.add(tsk2);
    						}
    					}
    				}
    				
    				if (nghbs.size() >= minPts) {
    					clstIds[tsk] = ++clstCnt;
    					
    					ListIterator<Integer> liNB = nghbs.listIterator();
    					while (liNB.hasNext()) {
    						int nb = liNB.next();
    						if (clstIds[nb] < 0) {
    							ArrayList<Integer> nghbs2 = new ArrayList<Integer>();
    							for (int tsk3 : dvsn) {
    								if (tsk3 != nb) {
    									long dist = getCostBtwPois(tasks[taskList.get(tsk3)].getIndexInIndices(), 
    		    		                        tasks[taskList.get(nb)].getIndexInIndices());
    									if (dist <= eps) {
    										nghbs2.add(tsk3);
    									}
    								}
    							}
    							
    							if (nghbs2.size() >= minPts) {
    								for (int nb2 : nghbs2) {
    									if (!nghbs.contains(nb2)) {
    										liNB.add(nb2);
    										liNB.previous();
    									}
    								}
    							}
    						}
    						
    						if (clstIds[nb] <= 0) {
    							clstIds[nb] = clstCnt;
    						}
    					}
    				}
    			}
    		}
    		
    		remains.clear();
    		for (int tsk : dvsn) {
    			if (clstIds[tsk] <= 0) {
    				remains.add(tsk);
    				clstIds[tsk] = -1;
    			}
    		}
    	}
    	
    	return clstCnt;
    }
    
    private  static int categoryDBSCAN_new(ArrayList<ArrayList<Integer>> divisions, int[] clstIds, 
    		long eps, int minPts) {
    	int clstCnt = 0;
		ArrayList<Integer> remains = new ArrayList<Integer>();
		
		for (ArrayList<Integer> dvsn : divisions) {
    		dvsn.addAll(remains);
    		for (int tsk : dvsn) {
    			if (clstIds[tsk] < 0) {
    				clstIds[tsk] = 0;
    				
    				ArrayList<Integer> nghbs = new ArrayList<Integer>();
    				for (int tsk2 : dvsn) {
    					if (tsk2 != tsk) {
    						long dist = getCostBtwPois(tasks[tsk2].getIndexInIndices(), 
    		                        tasks[tsk].getIndexInIndices());
    						if (dist <= eps) {
    							nghbs.add(tsk2);
    						}
    					}
    				}
    				
    				if (nghbs.size() >= minPts) {
    					clstIds[tsk] = ++clstCnt;
    					
    					ListIterator<Integer> liNB = nghbs.listIterator();
    					while (liNB.hasNext()) {
    						int nb = liNB.next();
    						if (clstIds[nb] < 0) {
    							ArrayList<Integer> nghbs2 = new ArrayList<Integer>();
    							for (int tsk3 : dvsn) {
    								if (tsk3 != nb) {
    									long dist = getCostBtwPois(tasks[tsk3].getIndexInIndices(), 
    		    		                        tasks[nb].getIndexInIndices());
    									if (dist <= eps) {
    										nghbs2.add(tsk3);
    									}
    								}
    							}
    							
    							if (nghbs2.size() >= minPts) {
    								for (int nb2 : nghbs2) {
    									if (!nghbs.contains(nb2)) {
    										liNB.add(nb2);
    										liNB.previous();
    									}
    								}
    							}
    						}
    						
    						if (clstIds[nb] <= 0) {
    							clstIds[nb] = clstCnt;
    						}
    					}
    				}
    			}
    		}
    		
    		remains.clear();
    		for (int tsk : dvsn) {
    			if (clstIds[tsk] <= 0) {
    				remains.add(tsk);
    				clstIds[tsk] = -1;
    			}
    		}
    	}
    	
    	return clstCnt;
    }
    
    public static void iterativeClustering(ArrayList<ArrayList<Integer>> clusters, 
    		ArrayList<Integer> outliers, long eps, int minPts, double ddr) {
    	ArrayList<ArrayList<Integer>> divisions = durCategorize(ddr);
    	
    	int[] clstIds = new int[tasks.length];
    	Arrays.fill(clstIds, -1);
    	
    	int clstCnt = categoryDBSCAN(divisions, clstIds, eps, minPts);
    	
    	for (int i = 0; i < clstCnt; ++i) {
    		clusters.add(new ArrayList<Integer>());
    	}
    	
    	for (int tsk = 0; tsk < tasks.length; ++tsk) {
    		if (clstIds[tsk] > 0) {
    			clusters.get(clstIds[tsk] - 1).add(tsk);
    		} else {
    			outliers.add(tsk);
    		}
    	}
    }
    
    public static void iterativeClustering_local(ArrayList<Integer> taskList, 
    		ArrayList<ArrayList<Integer>> clusters, ArrayList<Integer> outliers, 
    		long eps, int minPts, double ddr) {
    	ArrayList<ArrayList<Integer>> divisions = durCategorize_local(taskList, ddr);
    	
    	int[] clstIds = new int[taskList.size()];
    	Arrays.fill(clstIds, -1);
    	
    	int clstCnt = categoryDBSCAN_local(taskList, divisions, clstIds, eps, minPts);
    	
    	for (int i = 0; i < clstCnt; ++i) {
    		clusters.add(new ArrayList<Integer>());
    	}
    	
    	for (int tsk = 0; tsk < taskList.size(); ++tsk) {
    		if (clstIds[tsk] > 0) {
    			clusters.get(clstIds[tsk] - 1).add(taskList.get(tsk));
    		} else {
    			outliers.add(taskList.get(tsk));
    		}
    	}
    }
       
    public static void iterativeClustering_new(ArrayList<ArrayList<Integer>> clusters, 
    		ArrayList<Integer> outliers, long eps, int minPts, double ddr) {
    	ArrayList<ArrayList<Integer>> divisions = durCategorize(ddr);
    	
    	int[] clstIds = new int[tasks.length];
    	Arrays.fill(clstIds, -1);
    	
    	int clstCnt = categoryDBSCAN_new(divisions, clstIds, eps, minPts);
    	
    	for (int i = 0; i < clstCnt; ++i) {
    		clusters.add(new ArrayList<Integer>());
    	}
    	
    	for (int tsk = 0; tsk < tasks.length; ++tsk) {
    		if (clstIds[tsk] > 0) {
    			clusters.get(clstIds[tsk] - 1).add(tsk);
    		} else {
    			outliers.add(tsk);
    		}
    	}
    }

    public static int dbscan(ArrayList<Integer> taskList, int[] clstIds, 
    		long eps, int minPts) {
    	int clstCnt = 0;
    	
		for (int tsk = 0; tsk < taskList.size(); ++tsk) {
			if (clstIds[tsk] < 0) {
				clstIds[tsk] = 0;
				
				ArrayList<Integer> nghbs = new ArrayList<Integer>();
				for (int tsk2 = 0; tsk2 < taskList.size(); ++tsk2) {
					if (tsk2 != tsk) {
						long dist = getCostBtwPois(tasks[taskList.get(tsk2)].getIndexInIndices(), 
		                        tasks[taskList.get(tsk)].getIndexInIndices());
						if (dist <= eps) {
							nghbs.add(tsk2);
						}
					}
				}
				
				if (nghbs.size() >= minPts) {
					clstIds[tsk] = ++clstCnt;
					
					ListIterator<Integer> liNB = nghbs.listIterator();
					while (liNB.hasNext()) {
						int nb = liNB.next();
						if (clstIds[nb] < 0) {
							ArrayList<Integer> nghbs2 = new ArrayList<Integer>();
							for (int tsk3 = 0; tsk3 < taskList.size(); ++tsk3) {
								if (tsk3 != nb) {
									long dist = getCostBtwPois(tasks[taskList.get(tsk3)].getIndexInIndices(), 
		    		                        tasks[taskList.get(nb)].getIndexInIndices());
									if (dist <= eps) {
										nghbs2.add(tsk3);
									}
								}
							}
							
							if (nghbs2.size() >= minPts) {
								for (int nb2 : nghbs2) {
									if (!nghbs.contains(nb2)) {
										liNB.add(nb2);
										liNB.previous();
									}
								}
							}
						}
						
						if (clstIds[nb] <= 0) {
							clstIds[nb] = clstCnt;
						}
					}
				}
			}
		}
		
		return clstCnt;
    }
}
