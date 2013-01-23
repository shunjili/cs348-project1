/**
 * Just a class representing a single census group (one line in the file).
 * Fields are public for simplicity.
 */
public class CensusGroup {
	/**
	 * Population of the census group.
	 */
	public final int population;

	/**
	 * Latitude of the census group as listed in the file.
	 */
	public final float realLatitude;

	/**
	 * Latitude of the census group, projected onto our map using the Mercator
	 * Projection.
	 */
	public final float latitude;

	/**
	 * Longitude of the census group.
	 */
	public final float longitude;

	/**
	 * Create a new CensusGroup.
	 * 
	 * @param pop
	 *            population of the group
	 * @param lat
	 *            (real) latitude of the group
	 * @param lon
	 *            longitude of the group
	 */
	public CensusGroup(int pop, float lat, float lon) {
		population = pop;
		realLatitude = lat;
		latitude = mercatorConversion(lat);
		// latitude = lat;
		longitude = lon;
	}

	/**
	 * Project the latitude onto a rectangular map using the Mercator
	 * Projection.
	 * 
	 * @param lat
	 *            real latitude
	 * @return projected latitude
	 */
	private static float mercatorConversion(float lat) {
		float latpi = (float) (lat * Math.PI / 180);
		float x = (float) Math.log(Math.tan(latpi) + 1 / Math.cos(latpi));
		// System.out.println(lat + " -> " + x);
		return x;
	}
}
