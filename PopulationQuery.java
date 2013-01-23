import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class queries census data to find population densities in different
 * areas of the US.
 */
public class PopulationQuery {
	//version number 
	public static int versionNum = 1; 
	//total population
	public int total = 0;
	public int rows = 0;
	public int cols = 0;
	public Corners corn; 
	public PreProPara para; 

	/**
	 * For parsing - the number of comma-separated fields on a given line.
	 */
	public static final int TOKENS_PER_LINE = 7;

	/**
	 * For parsing - zero-based index of the field containing the population of
	 * the current census group.
	 */
	public static final int POPULATION_INDEX = 4;

	/**
	 * For parsing - zero-based index of the field containing the latitude of
	 * the current census group.
	 */
	public static final int LATITUDE_INDEX = 5;

	/**
	 * For parsing - zero-based index of the field containing the longitude of
	 * the current census group.
	 */
	public static final int LONGITUDE_INDEX = 6;

	/**
	 * There should be only one fork/join pool per program, so this needs to be
	 * a static variable.
	 */
	public static ForkJoinPool fjPool = new ForkJoinPool(1);

	/**
	 * Array of census data parsed from the input file.
	 */
	private CensusData data;
	private CumulativeGrid cuGrid;
	private RecursiveCumulativeGrid rcuGrid;
	private LockedBasedGrid lbGrid;

	/**
	 * Initialize the query object by parsing the census data in the given file.
	 * 
	 * @param filename
	 *            name of the census data file
	 */
	//The index array to store the grid index of the each group in the census data
	public Pair<Integer, Integer>[] gridIndex;
	public PopulationQuery(String filename) {
		// Parse the data and store it in an array.
		this.data = parse(filename);
	}

	/**
	 * Parse the input file into a large array held in a CensusData object.
	 * 
	 * @param filename
	 *            name of the file to be used as input.
	 * @return CensusData object containing the parsed data.
	 */
	private static CensusData parse(String filename) {
		CensusData result = new CensusData();

		try {
			BufferedReader fileIn = new BufferedReader(new FileReader(filename));

			/*
			 * Skip the first line of the file. After that, each line has 7
			 * comma-separated numbers (see constants above). We want to skip
			 * the first 4, the 5th is the population (an int) and the 6th and
			 * 7th are latitude and longitude (floats).
			 */

			try {
				/* Skip the first line. */
				String oneLine = fileIn.readLine();

				/*
				 * Read each subsequent line and add relevant data to a big
				 * array.
				 */
				while ((oneLine = fileIn.readLine()) != null) {
					String[] tokens = oneLine.split(",");
					if (tokens.length != TOKENS_PER_LINE)
						throw new NumberFormatException();
					int population = Integer.parseInt(tokens[POPULATION_INDEX]);
					result.add(population,
							Float.parseFloat(tokens[LATITUDE_INDEX]),
							Float.parseFloat(tokens[LONGITUDE_INDEX]));
				}
			} finally {
				fileIn.close();
			}
		} catch (IOException ioe) {
			System.err
					.println("Error opening/reading/writing input or output file.");
			System.exit(1);
		} catch (NumberFormatException nfe) {
			System.err.println(nfe.toString());
			System.err.println("Error in file format");
			System.exit(1);
		}
		return result;
	}

	/**
	 * Preprocess the census data for a run using the given parameters.
	 * @param <T>
	 * 
	 * @param cols
	 *            Number of columns in the map grid.
	 * @param rows
	 *            Number of rows in the map grid.
	 * @param versionNum
	 *            implementation to use
	 */
	@SuppressWarnings("unchecked")
	public <T> void preprocess(int cols, int rows, int versionNum) {
			this.rows = rows;
			this.cols = cols;
			switch (versionNum){
			case 1:{
				versionNum = 1;
				System.out.println("We are in version 1");
				//determine the coordinates of the rectangle. 
				this.total = 0;
				float b = data.data[0].latitude;
				float l = data.data[0].longitude; 
				float t = data.data[0].latitude;
				float r = data.data[0].longitude;
				//Determine the rectangle that contains all the points. 
				for (int i= 0 ; i < data.dataSize;  i++){
					this.total = this.total + data.data[i].population; 
					if (data.data[i].latitude < b){
						b = data.data[i].latitude;
					}
					if (data.data[i].latitude >t){
						t = data.data[i].latitude;
					}
					if (data.data[i].longitude < l){
						l = data.data[i].longitude;
					}
					if (data.data[i].longitude > r){
						r = data.data[i].longitude;
					}
					
				}
				//Now we found the four corners, we determine which rectangle it is in 
				gridIndex =  new Pair[data.dataSize];
				for (int i= 0 ; i < data.dataSize;  i++){
					
					int xIndex = (int) Math.ceil((data.data[i].latitude - b)/(t-b)*rows) ;
					int yIndex = (int) Math.ceil((data.data[i].longitude - l)/(r-l)*cols);
					if (data.data[i].latitude == b){ 
						xIndex = 1;
						}
					if ((data.data[i]).longitude == l){
						yIndex = 1;
						}
					this.gridIndex[i] = new Pair<Integer, Integer> (xIndex, yIndex);
				}
				//.out.println("We built the grid");
				break;}
		
			case 2:{
				versionNum =2;
				CornerFindRecursive t = new CornerFindRecursive(data.data, 0 , data.dataSize);
				Corners corn = fjPool.invoke(t);
				this.corn = corn; 
				this.total = corn.total;
				//System.out.println("We built the grid in version two");
				break;}
		
			case 3: {
				versionNum = 3;
				System.out.println("We are in version 3");
				//determine the coordinates of the rectangle. 
				this.total = 0;
				float b = data.data[0].latitude;
				float l = data.data[0].longitude; 
				float t = data.data[0].latitude;
				float r = data.data[0].longitude;
				//Determine the rectangle that contains all the points. 
				for (int i= 0 ; i < data.dataSize;  i++){
					this.total = this.total + data.data[i].population; 
					if (data.data[i].latitude < b){
						b = data.data[i].latitude;
					}
					if (data.data[i].latitude >t){
						t = data.data[i].latitude;
					}
					if (data.data[i].longitude < l){
						l = data.data[i].longitude;
					}
					if (data.data[i].longitude > r){
						r = data.data[i].longitude;
					}
					
				}
				//Now we found the four corners, we determine which rectangle it is in 
				this.gridIndex = new Pair[data.dataSize];
				for (int i= 0 ; i < data.dataSize;  i++){
					
					int xIndex = (int) Math.ceil((data.data[i].latitude - b)/(t-b)*rows) ;
					int yIndex = (int) Math.ceil((data.data[i].longitude - l)/(r-l)*cols);
					if (data.data[i].latitude == b){ 
						xIndex = 1;
						}
					if ((data.data[i]).longitude == l){
						yIndex = 1;
						}
					this.gridIndex[i] = new Pair<Integer, Integer> (xIndex, yIndex);
				}
				cuGrid = new CumulativeGrid(data, rows, cols, gridIndex);
				break;}
				
			case 4: {
				versionNum =4;
				CornerFindRecursive t = new CornerFindRecursive(data.data, 0 , data.dataSize);
				Corners corn = fjPool.invoke(t);
				total = corn.total;
				gridIndex = new Pair[data.dataSize];
				
				RecursiveIndexing index = new RecursiveIndexing(0, data.dataSize, data, cols, rows, gridIndex, corn);
				
				System.out.println("we are in version 4");
				fjPool.invoke(index);
				PreProPara para = new PreProPara(cols, rows, data, gridIndex);
				rcuGrid = new RecursiveCumulativeGrid(0,data.dataSize, para);
				fjPool.invoke(rcuGrid);
				rcuGrid.CumulativeParse();
				break;
			}
			case 5:{
				versionNum = 5;
				System.out.println("We are in version 5");
				//determine the coordinates of the rectangle. 
				CornerFindRecursive t = new CornerFindRecursive(data.data, 0 , data.dataSize);
				Corners corn = fjPool.invoke(t);
				total = corn.total;
				gridIndex = new Pair[data.dataSize];
				RecursiveIndexing index = new RecursiveIndexing(0, data.dataSize, data, cols, rows, gridIndex, corn);
				fjPool.invoke(index);
				PreProPara para = new PreProPara(cols, rows, data, gridIndex);
				LockedBasedGrid lbGrid = new LockedBasedGrid(0, data.dataSize, para);
				fjPool.invoke(lbGrid);
				lbGrid.CumulativeParse();
				break;
				}

			}
			
	}

	/**
	 * Query the population of a given rectangle.
	 * 
	 * @param w
	 *            western edge of the rectangle
	 * @param s
	 *            southern edge of the rectangle
	 * @param e
	 *            eastern edge of the rectangle
	 * @param n
	 *            northern edge of the rectangle
	 * @return pair containing the population of the rectangle and the
	 *         population as a percentage of the total US population.
	 */
	public Pair<Integer, Float> singleInteraction(int w, int s, int e, int n) {
		// YOUR CODE GOES HERE
		int localSum = 0;
		switch(versionNum){
		case 1: 
			
			for (int i = 0; i < this.gridIndex.length;i++ ){
				if(this.gridIndex[i].getElementA() >= s && 
						this.gridIndex[i].getElementA() <= n && 
						this.gridIndex[i].getElementB() >= w && 
						this.gridIndex[i].getElementB() <= e){
					localSum = localSum + data.data[i].population;
				}
			}
			break;
		case 2: 
			QueryV2 t = new QueryV2(0, data.dataSize, rows, cols, corn.t, corn.b, corn.l, corn.r, data.data, s, n, w, e);
			localSum = fjPool.invoke(t);
			break;
		case 3:
			System.out.println("we are querying version 3");
			localSum = this.cuGrid.Query(s, n, w, e);
			break;
		case 4:
			localSum = this.rcuGrid.Query(s, n, w, e);
			break;
		case 5:
			localSum = this.lbGrid.Query(s, n, w, e);
		}
		
	
		
		float percentage = ((float)  localSum) /this.total*100; 
		System.out.println("total percentage is "+ percentage);
		return new Pair<Integer, Float>(localSum, percentage);
	
			
	}

	// argument 1: file name for input data: pass this to parse
	// argument 2: number of x-dimension buckets
	// argument 3: number of y-dimension buckets
	// argument 4: -v1, -v2, -v3, -v4, or -v5
	public static void main(String[] args) {
		System.gc();
		int testrows = 108;
		int testcols = 149;
		PopulationQuery ppq = new PopulationQuery("CenPop2010.txt");
		for (int  i=1; i < 6; i ++ ){
			final long starttime = System.currentTimeMillis();
			ppq.preprocess(testrows, testcols, i);
			final long finishtime = System.currentTimeMillis();
			long duration = finishtime-starttime;
			System.out.println("The duration is "+ duration);
		}
		
		// Parse the command-line arguments.
		String filename;
		int cols, rows, versionNum;
		try {
			filename = args[0];
			cols = Integer.parseInt(args[1]);
			rows = Integer.parseInt(args[2]);
			String versionStr = args[3];
			Pattern p = Pattern.compile("-v([12345])");
			Matcher m = p.matcher(versionStr);
			m.matches();
			versionNum = Integer.parseInt(m.group(1));
		} catch (Exception e) {
			System.out
					.println("Usage: java PopulationQuery <filename> <rows> <cols> -v<num>");
			System.exit(1);
			return;
		}
		
		// Parse the input data.
		PopulationQuery pq = new PopulationQuery(filename);
		
		// Preprocess the input data.
		pq.preprocess(cols, rows, versionNum);
	
		// Read queries from stdin.
		Scanner scanner = new Scanner(new BufferedInputStream(System.in));
		while (true) {
			int w, s, e, n;
			try {
				System.out.print("Query? (west south east north | quit) ");
				String west = scanner.next();
				if (west.equals("quit")) {
					break;
				}
				w = Integer.parseInt(west);
				s = scanner.nextInt();
				e = scanner.nextInt();
				n = scanner.nextInt();
	
				if (w < 1 || w > cols)
					throw new IllegalArgumentException();
				if (e < w || e > cols)
					throw new IllegalArgumentException();
				if (s < 1 || s > rows)
					throw new IllegalArgumentException();
				if (n < s || n > rows)
					throw new IllegalArgumentException();
			} catch (Exception ex) {
				System.out
						.println("Bad input. Please enter four integers separated by spaces.");
				System.out.println("1 <= west <= east <= " + cols);
				System.out.println("1 <= south <= north <= " + rows);
				continue;
			}
	
			// Query the population for this rectangle.
			Pair<Integer, Float> result = pq.singleInteraction(w, s, e, n);
			System.out.printf("Query population: %10d\n", result.getElementA());
			System.out.printf("Percent of total: %10.2f%%\n",
					result.getElementB());
		}
		scanner.close();
	}
}
