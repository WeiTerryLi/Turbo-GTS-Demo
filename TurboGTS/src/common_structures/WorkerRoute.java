/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common_structures;

import java.util.ArrayList;
import java.util.Arrays;

public class WorkerRoute {
    public ArrayList<RouteStop> sequence;
    public boolean[] ableToBeDone;
    public boolean isFull = false;
    public double nud = -1.0;

    public WorkerRoute(int len) {
        sequence = new ArrayList<RouteStop>();
        ableToBeDone = new boolean[len];
        Arrays.fill(ableToBeDone, true);
    }

    public ArrayList<RouteStop> getSequence() {
        return sequence;
    }
}
