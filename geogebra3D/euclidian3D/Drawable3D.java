package geogebra3D.euclidian3D;



import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.TreeSet;

import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoElement3D;



/**
 * 
 * @author ggb3D
 *
 * 3D representation of a GeoElement3D
 *
 */
public abstract class Drawable3D {

	
	private static final boolean DEBUG = false;

	
	//constants for rendering
	protected static final float PICKED_DILATATION = 1.3f;
	protected static final float POINT3D_RADIUS = 50f;
	protected static final float POINT_ON_PATH_DILATATION = 1.01f;
	protected static final float LINE3D_THICKNESS = 0.01f;
	
	
	
	//view3D
	EuclidianView3D view3D; 
	
	//matrix for openGL display
	GgbMatrix matrix = new GgbMatrix(4,4);
	
	
	//picking
	boolean isPicked = false;	
	public float zPickMax, zPickMin; //for odering elements with openGL picking
	private static final float EPSILON_Z = 0.0001f;//0.0001f;//10000000; //limit to consider two objects to be at the same place
	
	
	
	//links to the GeoElement
	GeoElement geo; 	
	boolean isVisible;
	boolean labelVisible;
	
	
	
	
	/** update the 3D object */
	abstract public void update(); 
	
	
	
	
	/** return matrix for openGL */
	public double[] getMatrixGL(){
		return matrix.getGL();
	}
	
	
	
	/** draw the 3D object */
	abstract public void draw(EuclidianRenderer3D renderer); 
	abstract public void drawHidden(EuclidianRenderer3D renderer); 
	abstract public void drawTransp(EuclidianRenderer3D renderer); 
	abstract public void drawHiding(EuclidianRenderer3D renderer); 
	abstract public void drawPicked(EuclidianRenderer3D renderer); 
	abstract public void drawForPicking(EuclidianRenderer3D renderer); 

	
	////////////////////////////////
	// picking
	
	/** compare this to another Drawable3D with picking */
	public int comparePickingTo(Drawable3D d, boolean checkGeoClassType){
		
		//check if the two objects are "mixed"			
		if ((this.zPickMin-d.zPickMin)*(this.zPickMax-d.zPickMax)<EPSILON_Z){
			
			if (DEBUG){
				DecimalFormat df = new DecimalFormat("0.000000000");
				Application.debug("mixed :\n"
						+"zMin= "+df.format(this.zPickMin)+" | zMax= "+df.format(this.zPickMax)+" ("+this.getGeoElement().getLabel()+")\n"
						+"zMin= "+df.format(d.zPickMin)+" | zMax= "+df.format(d.zPickMax)+" ("+d.getGeoElement().getLabel()+")\n");
			}
			
			if (checkGeoClassType){
				if (this.getGeoElement().getGeoClassType()<d.getGeoElement().getGeoClassType())
					return -1;
				if (this.getGeoElement().getGeoClassType()>d.getGeoElement().getGeoClassType())
					return 1;
			}
			
			// check if one is on a path and the other not
			//TODO do this only for points
			if ((this.getGeoElement3D().hasPath1D())&&(!d.getGeoElement3D().hasPath1D()))
				return -1;
			if ((!this.getGeoElement3D().hasPath1D())&&(d.getGeoElement3D().hasPath1D()))
				return 1;


			//check if one is the child of the other
			if (this.getGeoElement().isChildOf(d.getGeoElement()))
				return -1;
			if (d.getGeoElement().isChildOf(d.getGeoElement()))
				return 1;
		
		}

		//finally check if one is before the other
		if (this.zPickMax<d.zPickMax)
			return -1;
		if (this.zPickMax>d.zPickMax)
			return 1;

		//says that the two objects are equal for the comparator
		if (DEBUG){
			DecimalFormat df = new DecimalFormat("0.000000000");
			Application.debug("equality :\n"
					+"zMin= "+df.format(this.zPickMin)+" | zMax= "+df.format(this.zPickMax)+" ("+this.getGeoElement().getLabel()+")\n"
					+"zMin= "+df.format(d.zPickMin)+" | zMax= "+df.format(d.zPickMax)+" ("+d.getGeoElement().getLabel()+")\n");
		}
		return 0;

		
	}
	
	
	/** Comparator for Drawable3Ds */
	static final public class drawableComparator implements Comparator{
		public int compare(Object arg1, Object arg2) {
			Drawable3D d1 = (Drawable3D) arg1;
			Drawable3D d2 = (Drawable3D) arg2;
			
						
			return d1.comparePickingTo(d2,false);


		}
	}
	
	/** Comparator for sets of Drawable3Ds */
	static final public class setComparator implements Comparator{
		public int compare(Object arg1, Object arg2) {

			TreeSet set1 = (TreeSet) arg1;
			TreeSet set2 = (TreeSet) arg2;
			
			//check if one set is empty
			if (set1.isEmpty())
				return 1;
			if (set2.isEmpty())
				return -1;
			
			Drawable3D d1 = (Drawable3D) set1.first();
			Drawable3D d2 = (Drawable3D) set2.first();
			
						
			return d1.comparePickingTo(d2,true);


		}
	}		
	

	/////////////////////////////////
	// links to the GeoElement
    public GeoElement getGeoElement() {
        return geo;
    } 
    
    
    public GeoElement3D getGeoElement3D() {
        return (GeoElement3D) geo;
    }   
    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 
    
 	

}




