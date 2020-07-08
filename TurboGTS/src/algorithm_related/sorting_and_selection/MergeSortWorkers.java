package algorithm_related.sorting_and_selection;

import java.util.ArrayList;

import algorithm_related.minor_structures.WorkerAssumed;

public class MergeSortWorkers {	
	private static void mergeLat(ArrayList<WorkerAssumed> workerList, int st, int md, int ed) {
		ArrayList<WorkerAssumed> firstHalf = new ArrayList<WorkerAssumed>();
		for (int i = st; i <= md; ++i) {
			firstHalf.add(workerList.get(i));
		}
		
		ArrayList<WorkerAssumed> secondHalf = new ArrayList<WorkerAssumed>();
		for (int i = md + 1; i <= ed; ++i) {
			secondHalf.add(workerList.get(i));
		}
		
		int idx1 = 0;
		int idx2 = 0;
		int i = st;
		while (idx1 < firstHalf.size() && idx2 < secondHalf.size()) {
			if (firstHalf.get(idx1).Y <= secondHalf.get(idx2).Y) {
				workerList.set(i++, firstHalf.get(idx1++));
			} else {
				workerList.set(i++, secondHalf.get(idx2++));
			}
		}
		
		if (idx1 >= firstHalf.size()) {
			while (idx2 < secondHalf.size()) {
				workerList.set(i++, secondHalf.get(idx2++));
			}
		}
		
		if (idx2 >= secondHalf.size()) {
			while (idx1 < firstHalf.size()) {
				workerList.set(i++, firstHalf.get(idx1++));
			}
		}
	}
	
	public static void mergeSortLat(ArrayList<WorkerAssumed> workerList, int st, int ed) {
		if (ed > st) {
			int md = (ed + st) / 2;
			mergeSortLat(workerList, st, md);
			mergeSortLat(workerList, md + 1, ed);
			mergeLat(workerList, st, md, ed);
		}
	}
	

	private static void mergeLon(ArrayList<WorkerAssumed> workerList, int st, int md, int ed) {
		ArrayList<WorkerAssumed> firstHalf = new ArrayList<WorkerAssumed>();
		for (int i = st; i <= md; ++i) {
			firstHalf.add(workerList.get(i));
		}
		
		ArrayList<WorkerAssumed> secondHalf = new ArrayList<WorkerAssumed>();
		for (int i = md + 1; i <= ed; ++i) {
			secondHalf.add(workerList.get(i));
		}
		
		int idx1 = 0;
		int idx2 = 0;
		int i = st;
		while (idx1 < firstHalf.size() && idx2 < secondHalf.size()) {
			if (firstHalf.get(idx1).X <= secondHalf.get(idx2).X) {
				workerList.set(i++, firstHalf.get(idx1++));
			} else {
				workerList.set(i++, secondHalf.get(idx2++));
			}
		}
		
		if (idx1 >= firstHalf.size()) {
			while (idx2 < secondHalf.size()) {
				workerList.set(i++, secondHalf.get(idx2++));
			}
		}
		
		if (idx2 >= secondHalf.size()) {
			while (idx1 < firstHalf.size()) {
				workerList.set(i++, firstHalf.get(idx1++));
			}
		}
	}
	
	public static void mergeSortLon(ArrayList<WorkerAssumed> workerList, int st, int ed) {
		if (ed > st) {
			int md = (ed + st) / 2;
			mergeSortLon(workerList, st, md);
			mergeSortLon(workerList, md + 1, ed);
			mergeLon(workerList, st, md, ed);
		}
	}
	
}
