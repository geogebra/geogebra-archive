package geogebra3D.kernel3D;

import geogebra.kernel.Construction;

/**
 * @author ggb3D
 * 
 * Construction for 3D stuff
 *
 */
public class Construction3D extends Construction {

	/** default constructor
	 * @param k current kernel
	 */
	public Construction3D(Kernel3D k) {
		super(k);
	}

	/**
	 * creates the ConstructionDefaults consDefaults
	 */
	protected void newConstructionDefaults(){
		consDefaults = new ConstructionDefaults3D(this);
	}
}
