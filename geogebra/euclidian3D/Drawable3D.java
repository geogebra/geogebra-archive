package geogebra.euclidian3D;

import geogebra.euclidian.*;

public abstract class Drawable3D 
	extends Drawable{
	
	EuclidianView3D view3D;
	
	public Drawable3D(){
		
		view3D = (EuclidianView3D) view;
	}
	

}
