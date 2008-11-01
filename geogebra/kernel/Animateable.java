package geogebra.kernel;

public interface Animateable {
	
	/**
	 * Performs the next animation step for this GeoElement. This may
	 * change the value of this GeoElement but will NOT call update() or updateCascade().
	 * 
	 * @return whether the value of this GeoElement was changed
	 */
	public boolean doAnimationStep();

}
