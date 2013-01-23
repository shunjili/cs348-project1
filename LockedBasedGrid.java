import java.util.concurrent.RecursiveAction;


public class LockedBasedGrid extends RecursiveAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int lo;
	int hi;
	Integer[][] population; 
	int cols;
	int rows;
	CensusData data;
	int SEQUENTIAL_CUTOFF = 100;
	PreProPara para;
	
	public Pair<Integer, Integer>[] gridIndex;
	public LockedBasedGrid(int lo, int hi, PreProPara para){
		this.lo = lo;
		this.hi = hi;
		this.cols = para.cols;
		this.rows = para.rows;
		this.data = para.data;
		this.gridIndex = para.gridIndex;
		this.para = para;
		population = new Integer[this.rows+1][this.cols+1];
	}
	public void compute(){
		if(hi-lo < SEQUENTIAL_CUTOFF){
				for (int i = lo; i < hi; i++) {
					int xIndex = gridIndex[i].getElementA();
					int yIndex = gridIndex[i].getElementB();
					//System.out.println("the number is " + population[xIndex][yIndex]);
					if( population[xIndex][yIndex]== null) {population[xIndex][yIndex] = 0;}
						synchronized (population[xIndex][yIndex]) {
							population[xIndex][yIndex] = population[xIndex][yIndex].intValue() + data.data[i].population;
						}
					} 
			}else{
			LockedBasedGrid left = new LockedBasedGrid(lo, (lo+hi)/2, para);
			LockedBasedGrid right = new LockedBasedGrid((lo+hi)/2,hi, para);
			left.fork();
			right.compute();
			left.join();		
		}
		
	}
	public void CumulativeParse(){
		System.out.println("population dimension" + rows +" "+  cols + "");
		for (int i = 1; i < this.rows +1; i ++){
			for (int j= 1; j < this.cols+1 ; j++){
				if (population[i][j] ==null) population[i][j] = 0;
				if (population[i-1][j] ==null) population[i-1][j] = 0;
				if (population[i][j-1] ==null) population[i][j-1] = 0;
				if (population[i-1][j-1] ==null) population[i-1][j-1] = 0;
	
				population[i][j] = population[i][j].intValue()+ population[i-1][j].intValue()+population[i][j-1].intValue()-population[i-1][j-1].intValue();
			}
		}
	}
	public int Query(int s, int n, int w, int e){
		int localSum = 0;
		localSum =population[n][e].intValue() -population[n][w-1].intValue() - population[s-1][e].intValue()+population[s-1][w-1].intValue();
		return localSum; 
	}
}
