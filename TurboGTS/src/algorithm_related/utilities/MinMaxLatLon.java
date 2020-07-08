/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm_related.utilities;

import static mainframe.TurboGTS.tasks;
import static mainframe.TurboGTS.workers;

import java.util.ArrayList;

import algorithm_related.minor_structures.TaskStatus;

public class MinMaxLatLon {
	public double minLat;
    public double maxLat;
    public double minLon;
    public double maxLon;

    public MinMaxLatLon(double min1, double max1, double min2, double max2) {
        minLat = min1;
        maxLat = max1;
        minLon = min2;
        maxLon = max2;
    }
        
    public static MinMaxLatLon findBorderLatLon() {
        double lat_max = -100;
        double lat_min = 100;
        double lon_max = -200;
        double lon_min = 200;
        
        for (int i = 0; i < workers.length; ++i) {
            if (workers[i].getLat() > lat_max) {
                lat_max = workers[i].getLat();
            }
            
            if (workers[i].getLat() < lat_min) {
                lat_min = workers[i].getLat();
            }
            
            if (workers[i].getLon() > lon_max) {
                lon_max = workers[i].getLon();
            }
            
            if (workers[i].getLon() < lon_min) {
                lon_min = workers[i].getLon();
            }
        }
                
        for (int i = 0; i < tasks.length; ++i) {
            if (tasks[i].getLat() > lat_max) {
                lat_max = tasks[i].getLat();
            }
            
            if (tasks[i].getLat() < lat_min) {
                lat_min = tasks[i].getLat();
            }
            
            if (tasks[i].getLon() > lon_max) {
                lon_max = tasks[i].getLon();
            }
            
            if (tasks[i].getLon() < lon_min) {
                lon_min = tasks[i].getLon();
            }
        }
        
        return new MinMaxLatLon(lat_min, lat_max, lon_min, lon_max);
    }
    
    public static MinMaxLatLon findBorderLatLonInSet(ArrayList<Integer> taskList) {
        double lat_max = -100;
        double lat_min = 100;
        double lon_max = -200;
        double lon_min = 200;
        
        for (int i = 0; i < workers.length; ++i) {
            if (workers[i].getLat() > lat_max) {
                lat_max = workers[i].getLat();
            }
            
            if (workers[i].getLat() < lat_min) {
                lat_min = workers[i].getLat();
            }
            
            if (workers[i].getLon() > lon_max) {
                lon_max = workers[i].getLon();
            }
            
            if (workers[i].getLon() < lon_min) {
                lon_min = workers[i].getLon();
            }
        }
                
        for (int i = 0; i < taskList.size(); ++i) {
            if (tasks[taskList.get(i)].getLat() > lat_max) {
                lat_max = tasks[taskList.get(i)].getLat();
            }
            
            if (tasks[taskList.get(i)].getLat() < lat_min) {
                lat_min = tasks[taskList.get(i)].getLat();
            }
            
            if (tasks[taskList.get(i)].getLon() > lon_max) {
                lon_max = tasks[taskList.get(i)].getLon();
            }
            
            if (tasks[taskList.get(i)].getLon() < lon_min) {
                lon_min = tasks[taskList.get(i)].getLon();
            }
        }
        
        return new MinMaxLatLon(lat_min, lat_max, lon_min, lon_max);
    }

    public static MinMaxLatLon findBordersInTaskStatus(ArrayList<TaskStatus> taskList) {
        double lat_max = -100;
        double lat_min = 100;
        double lon_max = -200;
        double lon_min = 200;
        
        for (int i = 0; i < workers.length; ++i) {
            if (workers[i].getLat() > lat_max) {
                lat_max = workers[i].getLat();
            }
            
            if (workers[i].getLat() < lat_min) {
                lat_min = workers[i].getLat();
            }
            
            if (workers[i].getLon() > lon_max) {
                lon_max = workers[i].getLon();
            }
            
            if (workers[i].getLon() < lon_min) {
                lon_min = workers[i].getLon();
            }
        }
                
        for (int i = 0; i < taskList.size(); ++i) {
            if (tasks[taskList.get(i).getIdxOfTasks()].getLat() > lat_max) {
                lat_max = tasks[taskList.get(i).getIdxOfTasks()].getLat();
            }
            
            if (tasks[taskList.get(i).getIdxOfTasks()].getLat() < lat_min) {
                lat_min = tasks[taskList.get(i).getIdxOfTasks()].getLat();
            }
            
            if (tasks[taskList.get(i).getIdxOfTasks()].getLon() > lon_max) {
                lon_max = tasks[taskList.get(i).getIdxOfTasks()].getLon();
            }
            
            if (tasks[taskList.get(i).getIdxOfTasks()].getLon() < lon_min) {
                lon_min = tasks[taskList.get(i).getIdxOfTasks()].getLon();
            }
        }
        
        return new MinMaxLatLon(lat_min, lat_max, lon_min, lon_max);
    }

}
