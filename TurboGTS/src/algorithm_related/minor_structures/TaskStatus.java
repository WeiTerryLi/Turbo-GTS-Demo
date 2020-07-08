package algorithm_related.minor_structures;

public class TaskStatus {
    private int idxOfTasks;
    public int clstId;
    
    public TaskStatus(int i, int c) {
    	idxOfTasks = i;
    	clstId = c;
    }
    
    public int getIdxOfTasks() {
    	return idxOfTasks;
    }
}

