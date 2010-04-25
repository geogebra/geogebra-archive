package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicND;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoSegmentInterface;
import geogebra3D.euclidian3D.Drawable3D;

/**
 * @author ggb3D
 *
 */
public class GeoConic3D 
extends GeoConicND implements GeoElement3DInterface{

	
	/** 2D coord sys where the polygon exists */
	private GgbCoordSys coordSys; 

	/** link with drawable3D */
	private Drawable3D drawable3D = null;
	
	
	private boolean isPickable;

	
	
	
	/**
	 * Creates an empty 3D conic with 2D coord sys
	 * @param c construction
	 * @param cs 2D coord sys
	 */
	public GeoConic3D(Construction c, GgbCoordSys cs) {
		super(c,2);
		setCoordSys(cs);
	}	
	
	
	
	
	
	
	
	/////////////////////////////////////////
	// link with the 2D coord sys
	
	
	/** set the 2D coordinate system
	 * @param cs the 2D coordinate system
	 */
	 public void setCoordSys(GgbCoordSys cs){
		 		 
		 this.coordSys = cs;

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

		

	 public GgbMatrix4x4 getDrawingMatrix() {
		 if (coordSys!=null)
			 return coordSys.getMatrix4x4();
		 else
			 return null;
	 }


	 public void setDrawingMatrix(GgbMatrix4x4 matrix) {
		 //coordSys.setDrawingMatrix(matrix);

	 }
	 

	 public GgbMatrix4x4 getLabelMatrix(){
		 return null;//coordSys.getLabelMatrix();
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
	 

	 public GgbVector getNormal(){ 
		 return coordSys.getNormal();
	 };




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
			

			/*
			//TODO says in which 2D coord sys the equation is calculated
			if (coordSys.getLabel()!=null){
				sbToString.append("\\");
				sbToString.append(coordSys.getLabel());
			}
*/
			
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



		@Override
		protected StringBuilder buildValueString() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public void setSphereND(GeoPointInterface M, GeoSegmentInterface segment) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void setSphereND(GeoPointInterface M, GeoPointInterface P) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public GeoElement copy() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		protected String getTypeString() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public boolean isEqual(GeoElement Geo) {
			// TODO Auto-generated method stub
			return false;
		}



		@Override
		public void set(GeoElement geo) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void setUndefined() {
			// TODO Auto-generated method stub
			
		}



		@Override
		public boolean showInAlgebraView() {
			// TODO Auto-generated method stub
			return false;
		}



		@Override
		protected boolean showInEuclidianView() {
			// TODO Auto-generated method stub
			return false;
		}



		@Override
		protected String getClassName() {
			// TODO Auto-generated method stub
			return null;
		}



		public boolean isVector3DValue() {
			// TODO Auto-generated method stub
			return false;
		}







		@Override
		protected void addPointOnConic(GeoPointInterface p) {
			// TODO Auto-generated method stub
			
		}







		@Override
		public void setCircle(GeoPoint M, GeoPoint P) {
			// TODO Auto-generated method stub
			
		}

	 
}
