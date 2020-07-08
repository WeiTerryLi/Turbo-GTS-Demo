package algorithm_related.sorting_and_selection;

public class MOMSelection {
    static void swap(double array[], int a, int b) {
    	double temp = array[a];
    	array[a] = array[b];
    	array[b] = temp;
    }
    
    static void findMedian(double array[], int st, int ed, int md) {
    	for (int i = st; i <= md; ++i) {
    		int key = i;
    		for (int j = i + 1; j < ed; ++j) {
    			if (array[j] < array[key]) {
    				key = j;
    			}
    		}
    		swap(array, i, key);
    	}
    }
    
    public static void medianOfMedians(double array[], int st, int ed, int md) {
    	int i = st;
    	for (; i + 5 <= ed; i += 5) {
    		findMedian(array, i, i + 5, i + 2);
    	}
    	
    	if (i < ed - 1) {
    		findMedian(array, i, ed, (i + ed - 1) / 2);
    	}
    	
    	if (ed - st > 5) {
    		int em = st;
    		int j = st;
    		for (; j + 5 <= ed; j += 5) {
    			swap(array, em, j + 2);
    			++em;
    		}
    		
    		if (j < ed) {
    			swap(array, em, (j + ed - 1) / 2);
    			++em;
    		}
    		
    		int mm = (st + em - 1) / 2;
    		medianOfMedians(array, st, em, mm);
    		
    		int p = ed - 1;
    		for (int k = em - 1; k > mm; --k) {
    			swap(array, k, p);
    			--p;
    		}
    		        	
    		for (int k = mm + 1; k <= p; ++k) {
    			if (array[k] < array[mm]) {
    				swap(array, k, mm);
    				int x = mm;
    				mm = k;
    				
    				if (k > x + 1) {
    					swap(array, mm, x + 1);
    					mm = x + 1;
    				}
    			}
    		}
    		
    		if (mm < md) {
    			medianOfMedians(array, mm + 1, ed, md);
    		} 
    		
    		if (mm > md) {
    			medianOfMedians(array, st, mm, md);
    		}
    	}
    }
}
