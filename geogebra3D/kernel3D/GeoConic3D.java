package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
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
	
	
	/**
	 * Creates an empty 3D conic with 2D coord sys
	 * @param c construction
	 * @param cs 2D coord sys
	 */
	public GeoConic3D(Construction c, GgbCoordSys cs) {
		this(c);
		setCoordSys(cs);
	}	
	
	
	/**
	 * Creates an empty 3D conic with 2D coord sys
	 * @param c construction
	 */
	public GeoConic3D(Construction c) {
		super(c,2);
	}	
	
	
	
	
	/////////////////////////////////////////
	// link with the 2D coord sys
	
	
	/** set the 2D coordinate system
	 * @param cs the 2D coordinate system
	 */
	 public void setCoordSys(GgbCoordSys cs){
		 		 
		 this.coordSys = cs;

	 }

	 public GgbCoordSys getCoordSys(){
		 return coordSys;
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
			 return coordSys.getMatrixOrthonormal();
		 else
			 return null;
	 }


	 public void setDrawingMatrix(GgbMatrix4x4 matrix) {
		 //coordSys.setDrawingMatrix(matrix);

	 }
	 


	 public GgbVector getLabelPosition(){
		 return new GgbVector(4); //TODO
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
	 

	 public GgbVector getMainDirection(){ 
		 return coordSys.getNormal();
	 };

	 /////////////////////////////////////////
	 // GeoConicND
	 
	 public GgbVector getMidpoint(){
		 return coordSys.getPoint(super.getMidpoint());
		 
	 }


	 public GgbVector getEigenvec3D(int i){
		 return coordSys.getVector(super.getEigenvec(i));
	 }

	 public GgbVector getMidpoint3D(){
		 return getMidpoint();
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

	 
		


		protected StringBuilder buildValueString() {
			return new StringBuilder("todo-GeoConic3D");
		}



		@Override
		public void setSphereND(GeoPointND M, GeoSegmentND segment) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void setSphereND(GeoPointND M, GeoPointND P) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public GeoElement copy() {
			// TODO Auto-generated method stub
			return null;
		}



		//TODO move to GeoConicND
		   protected String getTypeString() {
				switch (type) {
				case GeoConic.CONIC_CIRCLE: 
					return "Circle";
		 		default:
					return "Conic3D";
				}                       


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





		public boolean showInAlgebraView() {
			return true;
		}



		protected boolean showInEuclidianView() {
			return true;
		}



		public String getClassName() {
			 return "GeoConic3D";
		}



		public boolean isVector3DValue() {
			// TODO Auto-generated method stub
			return false;
		}







		@Override
		public void addPointOnConic(GeoPointND p) {
			// TODO Auto-generated method stub
			
		}







		@Override
		public void setCircle(GeoPoint M, GeoPoint P) {
			// TODO Auto-generated method stub
			
		}

		
		
		////////////////////////////////////
		// XML
		////////////////////////////////////
		
		
	    /**
	     * returns all class-specific xml tags for saveXML
	     */
		protected void getXMLtags(StringBuilder sb) {
	        super.getXMLtags(sb);
			//	curve thickness and type  
			getLineStyleXML(sb);
			
		}
		
	 
}
