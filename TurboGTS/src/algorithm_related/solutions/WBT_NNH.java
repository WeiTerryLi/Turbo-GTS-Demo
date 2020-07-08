/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm_related.solutions;

import common_structures.ExecutionStatistics;


public class WBT_NNH extends WBT_Solution{
    public static ExecutionStatistics es;

    public WBT_NNH() {
    	super();
    	
        topDownDivide_WBT_NN(wbt_root);
    } 
}
