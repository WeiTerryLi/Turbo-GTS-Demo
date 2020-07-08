package algorithm_related.minor_structures;

public class WorkerStatus {
    private int idx;
    public double distance = 0.0;
    
    public WorkerStatus(int i) {
        idx = i;
    }
    
    public int getIdx() {
    	return idx;
    }
}

