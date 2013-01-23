import java.util.concurrent.RecursiveTask;


public class RecursiveCumulativeGrid extends RecursiveTask<int[][]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	int lo;
	int hi;
	int[][] population; 
	int cols;
	int rows;
	CensusData data;
	int SEQUENTIAL_CUTOFF = 10000;
	int GRID_CUTOFF = 3;
	PreProPara param;
	
	public Pair<Integer, Integer>[] gridIndex;
	public RecursiveCumulativeGrid(int lo, int hi, PreProPara para){
		this.lo = lo;
		this.hi = hi;
		this.cols = para.cols;
		this.rows = para.rows;
		this.data = para.data;
		this.gridIndex = para.gridIndex;
		this.param = para;
	}
	public int[][] compute(){
		
		if(hi-lo < SEQUENTIAL_CUTOFF){
			int[][] baseResult = new int[rows+1][cols+1];
			for (int i = lo; i < hi; i++){
				baseResult[gridIndex[i].getElementA()][gridIndex[i].getElementB()] += data.data[i].population;
			}
			return baseResult;
		}else{
			int[][] result = new int[rows+1][cols+1];
			RecursiveCumulativeGrid left = new RecursiveCumulativeGrid(lo, (lo+hi)/2, param);
			RecursiveCumulativeGrid right = new RecursiveCumulativeGrid((lo+hi)/2,hi, param);
			left.fork();
			int[][] rightAns = right.compute();
			int[][] leftAns = left.join();		
			RecursiveGridAddition t = new RecursiveGridAddition(result, leftAns, rightAns, rows+1, 0, 0, cols+1);
			this.population = PopulationQuery.fjPool.invoke(t);
			return result;
		}
	}
	public void CumulativeParse(){
		//System.out.println("population dimension" + rows +" "+  cols + "");
		for (int i = 1; i < this.rows +1; i ++){
			for (int j= 1; j < this.cols+1 ; j++){
				this.population[i][j] = this.population[i][j]+ this.population[i-1][j]+this.population[i][j-1]-this.population[i-1][j-1];
			}
		}
	}
	public int Query(int s, int n, int w, int e){
		int localSum = 0;
		localSum =population[n][e] -population[n][w-1] - population[s-1][e]+population[s-1][w-1];
		return localSum; 
	}
}
