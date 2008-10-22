package geogebra3D.euclidian3D;



import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;



/**
 * 
 * @author ggb3D
 *
 * 3D representation of a GeoElement3D
 *
 */
public abstract class Drawable3D {
	
	//constants for rendering
	protected static final float PICKED_DILATATION = 1.3f;
	protected static final float POINT3D_RADIUS = 50f;
	protected static final float LINE3D_THICKNESS = 0.01f;
	
	
	
	//view3D
	EuclidianView3D view3D; 
	
	//matrix for openGL display
	GgbMatrix matrix = new GgbMatrix(4,4);
	
	
	//picking
	boolean isPicked = false;
	
	
	GeoElement geo; //GeoElement linked to this
	
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

	
	/** picking */
	abstract public boolean isPicked(GgbVector pickLine, boolean repaint);
	
	

	
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 
    
 	

}
