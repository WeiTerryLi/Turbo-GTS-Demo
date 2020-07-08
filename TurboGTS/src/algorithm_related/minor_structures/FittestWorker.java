package algorithm_related.minor_structures;

public class FittestWorker {
    private WorkerStatus worker;
    public double displace;
    
    public FittestWorker(WorkerStatus wk, double dist) {
        worker = wk;
        displace = dist;
    }
    
    public WorkerStatus getWorker() {
    	return worker;
    }
}

