package com.norana.numberplace.sudoku;

/**
 * Container to ease passing around a tuple of two objects. This object provides
 * a sensible implementation of equals(), returning true if equals() is true on 
 * each of the contained objects,
 */
public class Pair<K, V>{
	private final K first;
	private final V second;

	/**
	 * Constructor for a Pair.
	 *
	 * @param first The first object in the Pair
	 * @param second The second object in the Pair
	 */
	public Pair(K first, V second){
		this.first = first;
		this.second = second;
	}

	/**
	 * Checks the two objects for equality by delegating to their respective
	 * {@link  Object#equals(Object)} methods.
	 *
	 * @param obj the {@link Pair} to which this one is to be checked for 
	 * equality
	 * @return true if the underlying objects of the Pair are both 
	 * considered equal
	 */
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Pair){
			Pair<?, ?> p = (Pair<?, ?>) obj;
			return p.getFirst().equals(first) &&
				p.getSecond().equals(second);
		}
		else{
			return false;
		}
	}

	/**
	 * Computes a hash code using the hash codesof the underlying objects.
	 *
	 * @return a hash code of the Pair
	 */
	@Override
	public int hashCode(){
		return (first == null ? 0 : first.hashCode()) ^
			(second == null ? 0 : second.hashCode());
	}

	/**
	 * String representation of this Pair.
	 * The default first/second delimiter ", " is always used.
	 *
	 * @return String representation of this Pair
	 */
	@Override
	public String toString(){
		String s = "(";
		s += first.toString();
		s += ", ";
		s += second.toString();
		s += ")";
		return s;
	}

	/**
	 * Gets the first object for this pair
	 *
	 * @return first object for this pair
	 */
	public K getFirst(){
		return this.first;
	}

	/**
	 * Gets the second object for this pair
	 *
	 * @return second object for this pair
	 */
	public V getSecond(){
		return this.second;
	}
}

