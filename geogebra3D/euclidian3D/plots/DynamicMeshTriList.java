package geogebra3D.euclidian3D.plots;

import geogebra3D.euclidian3D.TriList;
import geogebra3D.euclidian3D.TriListElem;

/**
 * A triangle list for dynamic meshes
 * @author André Eriksson
 */
abstract class DynamicMeshTriList extends TriList {
	
	/**
	 * @param capacity the maximum number of triangles
	 * @param margin free triangle amount before considered full
	 * @param trisInChunk amount of triangles in each chunk
	 */
	DynamicMeshTriList(int capacity, int margin, int trisInChunk) {
		super(capacity, margin, trisInChunk);
	}

	/**
	 * @param e
	 * @return
	 */
	abstract public TriListElem add(AbstractDynamicMeshElement e);
	
	abstract public double getError();
	
	/**
	 * @param e
	 * @return
	 */
	abstract public boolean remove(AbstractDynamicMeshElement e);
	
	/**
	 * @param t
	 * @return
	 */
	abstract public boolean hide(AbstractDynamicMeshElement t);
	
	/**
	 * @param t
	 * @return
	 */
	abstract public boolean show(AbstractDynamicMeshElement t);
}

