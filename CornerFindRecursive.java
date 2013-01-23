import java.util.concurrent.RecursiveTask;

public class CornerFindRecursive extends RecursiveTask<Corners> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int SEQUENTIAL_THRESHOLD = 10;
	int lo;
	int hi;
	public float t; 
	public float b;
	public float l;
	public float r;
	public int total = 0; 
	
	public CensusGroup[] groupArray;
	
	
	public CornerFindRecursive(CensusGroup[] groupArray, int lo, int hi){
		t = groupArray[0].latitude; 
		b = groupArray[0].latitude;
		l = groupArray[0].longitude;
		r = groupArray[0].longitude;
		this.groupArray = groupArray; 
		this.lo = lo;
		this.hi = hi;
	}
	public Corners compute(){
		if (hi - lo <= SEQUENTIAL_THRESHOLD){
			for (int i = lo ; i < hi; i++){
				total = total + groupArray[i].population;
				if (groupArray[i].latitude < b){
					b = groupArray[i].latitude;
				}
				if (groupArray[i].latitude >t){
					t =groupArray[i].latitude;
				}
				if (groupArray[i].longitude < l){
					l = groupArray[i].longitude;
				}
				if (groupArray[i].longitude > r){
					r = groupArray[i].longitude;
				}
				
			}
			return new Corners(t, b, l, r, total);
		}else{
			CornerFindRecursive left = new CornerFindRecursive( groupArray, lo, (hi+lo)/2);
			CornerFindRecursive right = new CornerFindRecursive( groupArray, (hi+lo)/2, hi);
			left.fork();
			Corners rightAns = right.compute();
			Corners leftAns = left.join();
			return Corners.cornerMerge(leftAns, rightAns);
		}
	}

}
