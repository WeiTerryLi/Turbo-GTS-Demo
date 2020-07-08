/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common_structures;

public class RouteStop {
    private int posInIndices;
    private int idxInTasks;
    public long curTime;
    
    public RouteStop(int p, int i, long t) {
        posInIndices = p;
        idxInTasks = i;
        curTime = t;
    }
    
    public int getPosInIndices() {
    	return posInIndices;
    }
    
    public int getIdxInTasks() {
    	return idxInTasks;
    }
}
