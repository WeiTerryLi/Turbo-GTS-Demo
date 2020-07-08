/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common_structures;

public class Worker extends PointOfInterest{
    protected int idx_in_indices;
    protected long departTime;
    
    public Worker(int id, double lat, double lon, int i, long d) {
        super(id, lat, lon);

        idx_in_indices = i;
        departTime = d;
    }
    
    public int getIndexInIndices() {
        return idx_in_indices;
    }
    
    public long getDepartTime() {
        return departTime;
    }
}
