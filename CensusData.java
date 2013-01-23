// just a resizing array for holding the input
// fields are public for simplicity
// note array may not be full; see data_size field

/**
 * Just a resizing array for holding the input. Fields are public for
 * simplicity. Note the array may not be full; see the dataSize field.
 */
public class CensusData {
	/**
	 * Initial size of the data array.
	 */
	public static final int INITIAL_SIZE = 100;
	/**
	 * Array storing the census data.
	 */
	public CensusGroup[] data;
	
	/**
	 * Number of elements stored in the data array.
	 */
	public int dataSize;

	/**
	 * Create a new CensusData object.
	 */
	public CensusData() {
		data = new CensusGroup[INITIAL_SIZE];
		dataSize = 0;
	}

	/**
	 * Add a new census group to the array.
	 * 
	 * @param population population of the census group
	 * @param latitude latitude of the census group
	 * @param longitude longitude of the census group
	 */
	public void add(int population, float latitude, float longitude) {
		if (dataSize == data.length) {
			// resize
			CensusGroup[] new_data = new CensusGroup[data.length * 2];
			for (int i = 0; i < data.length; ++i)
				new_data[i] = data[i];
			data = new_data;
		}
		CensusGroup g = new CensusGroup(population, latitude, longitude);
		data[dataSize++] = g;
	}
}
