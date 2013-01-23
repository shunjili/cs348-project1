
public class PreProPara {
	int cols;
	int rows;
	CensusData data;
	Pair<Integer, Integer>[] gridIndex;
	public PreProPara (int cols, int rows, CensusData data, Pair<Integer, Integer>[] gridIndex){
		this.cols =  cols;
		this.rows = rows;
		this.data = data;
		this.gridIndex = gridIndex;
	}
}
