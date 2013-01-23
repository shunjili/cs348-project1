
import java.util.concurrent.RecursiveTask;
public class QueryV2 extends RecursiveTask<Integer>{
	private static final long serialVersionUID = 1L;
	static int SEQUENTIAL_THRESHOLD = 100;
	int lo;
	int hi;
	CensusGroup[] data;
	Pair<Integer, Integer>[] indexArray; 
	int rows;
	int cols;
	int s;
	int n;
	int w;
	int e;
	float t;
	float b;
	float l;
	float r;
	
	public QueryV2(int lo, int hi,int rows, int cols, float t, float b, float l, float r, CensusGroup[] data, int s, int n, int w, int e) {
		this.lo = lo;
		this.hi = hi;
		this.data = data;
		this.s = s;
		this.n = n;
		this.w = w;
		this.e = e;
		this.t = t;
		this.b = b;
		this.l = l;
		this.r = r;
		this.rows = rows;
		this.cols = cols;
	}
	public Integer compute(){
		int localSum = 0;
		if (hi-lo <= SEQUENTIAL_THRESHOLD){
			for (int i = lo; i <hi;i++ ){
				int xIndex = (int) Math.ceil((data[i].latitude - b)/(t-b)*rows) ;
				int yIndex = (int) Math.ceil((data[i].longitude - l)/(r-l)*cols);
				if(xIndex >= s && 
						xIndex<= n && 
						yIndex >= w && 
						yIndex <= e){
					localSum = localSum + data[i].population;
				}
			}
			return localSum;
		}else{
			QueryV2 left = new QueryV2(lo, (lo+hi)/2 , rows,  cols,  t,  b,  l,  r,  data,  s,  n,  w,  e) ;
			QueryV2 right = new QueryV2(  (lo+hi)/2,  hi, rows,  cols,  t,  b,  l,  r,  data,  s,  n,  w,  e) ;
			left.fork();
			int rightAns = right.compute();
			int leftAns = left.join();
			return leftAns + rightAns;
		}
	}

}
