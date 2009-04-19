package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.euclidian3D.Drawable3D;

/**
 * @author ggb3D
 *
 */
public class GeoConic3D 
extends GeoConic implements GeoElement3DInterface{

	
	/** 2D coord sys where the polygon exists */
	private GeoCoordSys2D coordSys; 

	/** link with drawable3D */
	private Drawable3D drawable3D = null;
	
	
	
	
	
	
	
	/** Creates new GeoConic with Coordinate System for 3D */
	public GeoConic3D(Construction c, String label, double[] coeffs, GeoElement cs) {
		super(c,label,coeffs,cs);
	}	
	
	
	
	/////////////////////////////////////////
	// link with the 2D coord sys
	
	
	/** set the 2D coordinate system
	 * @param cs the 2D coordinate system
	 */
	 public void setCoordSys(GeoElement cs){
		 		 
		 this.coordSys = (GeoCoordSys2D) cs;

	 }

	 

	 /////////////////////////////////////////
	 // link with Drawable3D

	 /**
	  * set the 3D drawable linked to
	  * @param d the 3D drawable 
	  */
	 public void setDrawable3D(Drawable3D d){
		 drawable3D = d;
	 }

	 /** return the 3D drawable linked to
	  * @return the 3D drawable linked to
	  */
	 public Drawable3D getDrawable3D(){
		 return drawable3D;
	 }

		

	 public Ggb3DMatrix4x4 getDrawingMatrix() {
		 if (coordSys!=null)
			 return coordSys.getDrawingMatrix();
		 else
			 return null;
	 }


	 public void setDrawingMatrix(Ggb3DMatrix4x4 matrix) {
		 coordSys.setDrawingMatrix(matrix);

	 }



		
		
		
		


	 /////////////////////////////////////////
	 // link with GeoElement2D


	 public GeoElement getGeoElement2D() {
		 return null;
	 }

	 public boolean hasGeoElement2D() {
		 return false;
	 }


	 public void setGeoElement2D(GeoElement geo) {

	 }



	 /////////////////////////////////////////
	 // GeoConic3D
	 public int getGeoClassType() {
		 return GeoElement3D.GEO_CLASS_CONIC3D;
	 }
	 
	 

	 /**
	  * it's a 3D GeoElement.
	  * @return true
	  */
	 public boolean isGeoElement3D(){
		 return true;
	 } 
	 
	 
		public String toString() {	
			StringBuffer sbToString = getSbToString();
			sbToString.setLength(0);
			sbToString.append(label);
			

			//says in which 2D coord sys the equation is calculated
			sbToString.append(" (");
			sbToString.append(coordSys.getLabel());
			sbToString.append(")");

			
			sbToString.append(": ");
			sbToString.append(buildValueString()); 
			return sbToString.toString();
		}

	 
	 
}
