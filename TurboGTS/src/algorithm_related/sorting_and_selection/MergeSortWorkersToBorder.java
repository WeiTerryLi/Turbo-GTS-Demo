package algorithm_related.sorting_and_selection;

import java.util.ArrayList;

import algorithm_related.minor_structures.WorkerAssumed;

import static mainframe.TurboGTS.workers;

public class MergeSortWorkersToBorder {	
	private static void mergeLatToBorder(ArrayList<WorkerAssumed> workerList, int st, int md, int ed, double y) {
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
			if (Math.abs(workers[firstHalf.get(idx1).getIdx()].getLat() - y) 
					<= Math.abs(workers[secondHalf.get(idx2).getIdx()].getLat() - y)) {
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
	
	public static void mergeSortLatToBorder(ArrayList<WorkerAssumed> workerList, int st, int ed, double y) {
		if (ed > st) {
			int md = (ed + st) / 2;
			mergeSortLatToBorder(workerList, st, md, y);
			mergeSortLatToBorder(workerList, md + 1, ed, y);
			mergeLatToBorder(workerList, st, md, ed, y);
		}
	}
	

	private static void mergeLonToBorder(ArrayList<WorkerAssumed> workerList, int st, int md, int ed, double x) {
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
			if (Math.abs(workers[firstHalf.get(idx1).getIdx()].getLon() - x) 
					<= Math.abs(workers[secondHalf.get(idx2).getIdx()].getLon() - x)) {
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
	
	public static void mergeSortLonToBorder(ArrayList<WorkerAssumed> workerList, int st, int ed, double x) {
		if (ed > st) {
			int md = (ed + st) / 2;
			mergeSortLonToBorder(workerList, st, md, x);
			mergeSortLonToBorder(workerList, md + 1, ed, x);
			mergeLonToBorder(workerList, st, md, ed, x);
		}
	}
	
}
