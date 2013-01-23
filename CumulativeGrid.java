
public class CumulativeGrid {
	public int[][] population; 
	public int rows;
	public int cols; 
	public CensusData data;
	public Pair<Integer, Integer>[] gridIndex;
	
	public CumulativeGrid(CensusData data, int rows, int cols, Pair<Integer, Integer>[] gridIndex){
		this.rows= rows;
		this.cols = cols;
		this.data = data;
		this.gridIndex = gridIndex;
		this.population = new int[rows+1][cols+1];
		for ( int i = 0; i < data.dataSize; i++){
			int row = gridIndex[i].getElementA();
			int col = gridIndex[i].getElementB();
			//System.out.println(" " + row + " " + col);
			int groupPopulation = data.data[i].population;
			this.population[row][col] += groupPopulation;
		}
		for (int i = 1; i < this.rows +1; i ++){
			for (int j= 1; j < this.cols+1 ; j++){
				this.population[i][j] = this.population[i][j]+ this.population[i-1][j]+this.population[i][j-1]-this.population[i-1][j-1];
			}
		}
		
	}
	public int Query(int s, int n, int w, int e){
		int sum = 0;
		sum =population[n][e] -population[n][w-1] - population[s-1][e]+population[s-1][w-1];
		return sum;
	}
	
}
