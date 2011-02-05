package geogebra3D.euclidian3D.plots;

import geogebra3D.euclidian3D.TriList;

/**
 * A triangle list for dynamic meshes
 * 
 * @author André Eriksson
 */
abstract class DynamicMeshTriList extends TriList {

	/**
	 * @param capacity
	 *            the maximum number of triangles
	 * @param margin
	 *            free triangle amount before considered full
	 * @param trisInChunk
	 *            amount of triangles in each chunk
	 */
	DynamicMeshTriList(int capacity, int margin, int trisInChunk) {
		super(capacity, margin, trisInChunk, true);
	}

	/**
	 * @param e
	 *            the element to add
	 */
	abstract public void add(AbstractDynamicMeshElement e);

	/**
	 * @param e
	 *            the element to remove
	 * @param i
	 */
	abstract public void add(AbstractDynamicMeshElement e, int i);

	/**
	 * @return the total visible error
	 */
	abstract public double getError();

	/**
	 * @param e
	 *            the element to remove
	 * @return true if the element was removed, otherwise false
	 */
	abstract public boolean remove(AbstractDynamicMeshElement e);

	/**
	 * @param e
	 *            the element to remove
	 * @param i
	 * @return true if the element was removed, otherwise false
	 */
	abstract public boolean remove(AbstractDynamicMeshElement e, int i);

	/**
	 * @param t
	 *            the element to attempt to hide
	 * @return true if the element was hidden, otherwise false
	 */
	abstract public boolean hide(AbstractDynamicMeshElement t);

	/**
	 * @param t
	 *            the elemet to attempt to show
	 * @return true if the element was shown, otherwise false
	 */
	abstract public boolean show(AbstractDynamicMeshElement t);

}
