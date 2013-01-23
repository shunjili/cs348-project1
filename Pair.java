/**
 * This class represents an immutable pair of types T and S.
 * 
 * @param <T> type of first element
 * @param <S> type of second element
 */
public class Pair<T, S> {
	/**
	 * First element of the pair.
	 */
	private final T elt1;
	
	/**
	 * Second element of the pair.
	 */
	private final S elt2;

	/**
	 * Create a pair object.
	 * 
	 * @param elt1 first element of the pair
	 * @param elt2 second element of the pair
	 */
	public Pair(T elt1, S elt2) {
		this.elt1 = elt1;
		this.elt2 = elt2;
	}

	/**
	 * Get the first element of the pair.
	 * 
	 * @return first element of the pair
	 */
	public T getElementA() {
		return elt1;
	}

	/**
	 * Get the second element of the pair.
	 * 
	 * @return second element of the pair
	 */
	public S getElementB() {
		return elt2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + elt1.toString() + ", " + elt2.toString() + ")";
	}
}
