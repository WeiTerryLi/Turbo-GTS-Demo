/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common_structures;

public class Task extends PointOfInterest{
    protected int idx_in_indices;
    protected long dur;
    protected long expir;
    protected int value = 1;

    public Task(int id, double lat, double lon, int i, long d, long e) {
        super(id, lat, lon);

        idx_in_indices = i;
        dur = d;
        expir = e;
    }

    public Task(int id, double lat, double lon, int i, long d, long e, int v) {
        super(id, lat, lon);

        idx_in_indices = i;
        dur = d;
        expir = e;
        value = v;
    }
            
    public int getIndexInIndices() {
        return idx_in_indices;
    }
        
    public long getDur() {
        return dur;
    }
    
    public long getExpir() {
        return expir;
    }
    
    public int getValue() {
        return value;
    }
}
