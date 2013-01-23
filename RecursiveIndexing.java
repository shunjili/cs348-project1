import java.util.concurrent.RecursiveAction;


public class RecursiveIndexing extends RecursiveAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CensusData data;
	int lo;
	int hi;
	int cols;
	int rows;
	Corners corn; 
	Pair<Integer, Integer>[] gridIndex; 
	public static int SEQUENTIAL_THRESHHOLD = 100; 
	float t; 
	float b;
	float l;
	float r; 
	
	public RecursiveIndexing(int lo, int hi, CensusData data, int cols, int rows, Pair<Integer, Integer>[] gridIndex, Corners corn){
		this.data = data;
		this.rows = rows;
		this.cols = cols;
		this.gridIndex = gridIndex;
		this.lo = lo;
		this.hi = hi;
		this.corn = corn; 
		this.t = corn.t;
		this.b = corn.b;
		this.l = corn.l;
		this.r = corn.r;
	}
	public void compute(){
		if ((hi-lo) < SEQUENTIAL_THRESHHOLD){
			for (int i= lo ; i < hi;  i++){
				int xIndex = (int) Math.ceil((data.data[i].latitude - b)/(t-b)*rows) ;
				int yIndex = (int) Math.ceil((data.data[i].longitude - l)/(r-l)*cols);
				if (data.data[i].latitude == b){ 
					xIndex = 1;
					}
				if ((data.data[i]).longitude == l){
					yIndex = 1;
					}
				//System.out.println("group" + i + "is in grid" + xIndex + " " +yIndex);
				gridIndex[i] = new Pair<Integer, Integer> (xIndex, yIndex);
			}
		}else{
			RecursiveIndexing left = new RecursiveIndexing(lo, (lo+hi)/2, data, cols, rows, gridIndex, corn);
			RecursiveIndexing right = new RecursiveIndexing((lo+hi)/2, hi, data, cols, rows, gridIndex, corn);
			left.fork();
			right.compute();
			left.join();
		}
	}
	

}
