
import java.util.concurrent.RecursiveTask;


public class RecursiveGridAddition extends RecursiveTask<int[][]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int SEQUENTIAL_CUTOFF = 20;
	public int[][] result; 
	public int[][] grid1;
	public int[][] grid2;
	int t;
	int b;
	int l;
	int r;
	public RecursiveGridAddition(int[][] result, int[][] grid1, int[][] grid2, int t, int b, int l, int r){
		this.t = t;
		this.b = b;
		this.l = l;
		this.r = r;
		this.grid1 = grid1;
		this.grid2 = grid2;
		this.result = result; 
		
	}
	public int[][] compute(){
		if (t-b < SEQUENTIAL_CUTOFF || r-l < SEQUENTIAL_CUTOFF){
			for (int i= b; i < t; i++){
				for (int j = l; j < r; j++){
					result[i][j]  = grid1[i][j]+ grid2[i][j];
				}
			}
		}else{
			//System.out.println("The 4 numbers are " + t + " " + b + " " + l + " " + r);
			RecursiveGridAddition tl = new RecursiveGridAddition(result,  grid1, grid2, t, (t+b)/2, l, (l+r)/2);
			RecursiveGridAddition tr = new RecursiveGridAddition(result,  grid1, grid2, t, (t+b)/2,(l+r)/2, r);
			RecursiveGridAddition bl = new RecursiveGridAddition(result, grid1, grid2, (t+b)/2, b, l, (l+r)/2);
			RecursiveGridAddition br = new RecursiveGridAddition(result, grid1, grid2, (t+b)/2, b,(l+r)/2, r);
			//System.out.println("here");
			tl.fork();
			tr.fork();
			bl.fork();
			br.compute();
			tl.join();
			tr.join();
			bl.join();
			
		}
		return result;

	}
//	public static void main(String[] args){
//		int rows = 10;
//		int cols = 10;
//		int[][] grid1 = new int[rows][cols];
//		int[][] grid2 = new int[rows][cols];
//		int[][] result = new int[rows][cols];
//		for (int i=0;i<rows;i++){
//			for (int j=0; j<cols;j++){			
//				grid1[i][j] = i*j;
//				grid2[i][j] = i+j;
//			}	
//		}
//		
//		RecursiveGridAddition gridsum = new RecursiveGridAddition(result, grid1, grid2, rows, 0, 0, cols);
//		final ForkJoinPool fjPool = new ForkJoinPool();
//		fjPool.invoke(gridsum);
//		for (int j=0;j<rows;j++){
//			for (int i=0; i<cols;i++){			
//				System.out.print(result[j][i]+ "     ");
//			}	
//			System.out.println();
//		}
//		
//	}
}
