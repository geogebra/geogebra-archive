package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
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
	
	
	private boolean isPickable;

	
	
	
	/**
	 * Creates an empty 3D conic with 2D coord sys
	 * @param c construction
	 * @param cs 2D coord sys
	 */
	public GeoConic3D(Construction c, GeoElement cs) {
		super(c,cs);
	}	
	
	
	
	/** Creates new GeoConic with Coordinate System for 3D 
	 * @param c construction
	 * @param label name
	 * @param coeffs coeffs of the implicit equation
	 * @param cs 2D coord sys
	 * */
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
	 

	 public Ggb3DMatrix4x4 getLabelMatrix(){
		 return coordSys.getLabelMatrix();
	 }



		

	 /** says if the object is pickable
	  * @return true if the object is pickable
	  */
	 public boolean isPickable(){
		 return true;
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
	 

	 //TODO implement this
	 public Ggb3DVector getNormal(){ return null;};




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
			StringBuilder sbToString = getSbToString();
			sbToString.setLength(0);
			sbToString.append(label);
			

			//TODO says in which 2D coord sys the equation is calculated
			if (coordSys.getLabel()!=null){
				sbToString.append("\\");
				sbToString.append(coordSys.getLabel());
			}

			
			sbToString.append(": ");
			sbToString.append(buildValueString()); 
			return sbToString.toString();
		}

	 
		
		/** sets the pickability of the object
		 * @param v pickability
		 */
		public void setIsPickable(boolean v){
			isPickable = v;
		}

	 
}
