/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common_structures;

public class PointOfInterest {
    protected int idxOfPois;
    protected double lat;
    protected double lon;
    
    public PointOfInterest(int id, double lat, double lon) {
        this.idxOfPois = id;
        this.lat = lat;
        this.lon = lon;
    }
    
    public int getIdxOfPois() {
        return idxOfPois;
    }
    
    public double getLat() {
        return lat;
    }
    
    public double getLon() {
        return lon;
    }
}
