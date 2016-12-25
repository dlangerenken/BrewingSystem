package general;

/**
 * A class that holds to objects of types A and B
 * 
 * @author Patrick
 *
 * @param <A>
 * @param <B>
 */
public class Pair<A, B> {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((o1 == null) ? 0 : o1.hashCode());
		result = prime * result + ((o2 == null) ? 0 : o2.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (o1 == null) {
			if (other.o1 != null) {
				return false;
			}
		} else if (!o1.equals(other.o1)) {
			return false;
		}
		if (o2 == null) {
			if (other.o2 != null) {
				return false;
			}
		} else if (!o2.equals(other.o2)) {
			return false;
		}
		return true;
	}

	private A o1;
	private B o2;

	/**
	 * A simple class to store two Objects of arbitrary types into one wrapper
	 * object
	 * 
	 * @param first
	 *            object
	 * @param second
	 *            object
	 */
	public Pair(final A first, final B second) {
		this.o1 = first;
		this.o2 = second;
	}

	/**
	 * Returns object A
	 * 
	 * @return
	 */
	public A getFirst() {
		return o1;
	}

	/**
	 * return object B
	 * 
	 * @return
	 */
	public B getSecond() {
		return o2;
	}

	/** sets first object in pair */
	public void setFirst(A o1) {
		this.o1 = o1;
	}

	/** sets second object in pair */
	public void setSecond(B o2) {
		this.o2 = o2;
	}
}
