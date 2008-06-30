package geogebra3D.kernel3D;

public interface Path3D {
	
	
	
	/**
	 * Sets coords of P and its path parameter when
	 * the coords of P have changed.
	 * Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 */
	public void pointChanged(GeoPoint3D P);	

}
