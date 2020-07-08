package algorithm_related.minor_structures;

public class WorkerAssumed {
	private int idx;
    public double X;
    public double Y;
    
    public WorkerAssumed(int i, double x, double y) {
        idx = i;
        X = x;
        Y = y;
    }
    
    public int getIdx() {
    	return idx;
    }
}
