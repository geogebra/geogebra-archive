package geogebra3D.euclidian3D;



import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.TreeSet;

import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;



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
	private EuclidianView3D m_view3D; 
	
	//matrix for openGL display
	private GgbMatrix m_matrix = new GgbMatrix(4,4);
	
	//links to the GeoElement
	private GeoElement m_geo; 	
	private boolean m_isVisible;
	private boolean m_labelVisible;

	//picking
	private boolean m_isPicked = false;	
	public float zPickMax, zPickMin; //for odering elements with openGL picking
	private static final float EPSILON_Z = 0.0001f;//0.0001f;//10000000; //limit to consider two objects to be at the same place
	
	//constants for picking : have to be from 0 to DRAW_PICK_ORDER_MAX-1, regarding to picking order
	static final public int DRAW_PICK_ORDER_MAX = 3;
	static final public int DRAW_PICK_ORDER_0D = 0; //for 0-Dimensional objects : points
	static final public int DRAW_PICK_ORDER_1D = 1; //for 1-Dimensional objects : segments, line
	static final public int DRAW_PICK_ORDER_2D = 2; //for 2-Dimensional objects : polygons, planes
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	// constructors
		
	/** default constructor : setting view3D and geo */
	public Drawable3D(EuclidianView3D a_view3D, GeoElement3D a_geo){
		setView3D(a_view3D);
		setGeoElement(a_geo);
		update();
	}
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	// update
	
	/** update the 3D object */
	public void update(){
		//verify if object is visible for drawing
		setVisible(getGeoElement().isEuclidianVisible());       				 
		if (!isVisible()) return;
		setLabelVisible(getGeoElement().isLabelVisible());  //TODO label  	

		//update the matrix of the drawable for the renderer to draw it
		updateDrawingMatrix();
		getView3D().toScreenCoords3D(getMatrix());
	}
	
	
	/** update the matrix of the drawable for the renderer to draw it */
	abstract public void updateDrawingMatrix();
	
	
	
	
	
	
	
	
	
	

	
	public void setMatrix(GgbMatrix a_matrix){
		m_matrix=a_matrix;
	}
	public GgbMatrix getMatrix(){
		return m_matrix;
	}

	protected EuclidianView3D getView3D(){
		return m_view3D; 
	}
	protected void setView3D(EuclidianView3D a_view3D){
		m_view3D=a_view3D; 
	}
	
	protected boolean isVisible(){
		return m_isVisible; 
	}
	protected void setVisible(boolean a_isVisible){
		m_isVisible=a_isVisible; 
	}
	

	protected boolean getLabelVisible(){
		return m_labelVisible; 
	}
	protected void setLabelVisible(boolean a_labelVisible){
		m_labelVisible=a_labelVisible; 
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////
	// drawing
	
	
	/** draw the 3D object */
	abstract public void drawPrimitive(EuclidianRenderer3D renderer); 
	abstract public void drawPrimitivePicked(EuclidianRenderer3D renderer); 
	abstract public void draw(EuclidianRenderer3D renderer); 
	abstract public void drawHidden(EuclidianRenderer3D renderer); 
	abstract public void drawTransp(EuclidianRenderer3D renderer); 
	abstract public void drawHiding(EuclidianRenderer3D renderer); 
	abstract public void drawPicked(EuclidianRenderer3D renderer); 

	
	public void drawForPicking(EuclidianRenderer3D renderer) {
		if(!getGeoElement().isEuclidianVisible())
			return;	
		
		renderer.setMatrix(getMatrix());
		drawPrimitive(renderer);

	}

	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////
	// picking
	
	/** returns picking order */
	abstract public int getPickOrder();
	
	/** for picking an object through another */
	abstract public boolean isTransparent();
	
	/** compare this to another Drawable3D with picking */
	public int comparePickingTo(Drawable3D d, boolean checkPickOrder){
		
		//check if one is transparent and the other not
		if ( (!this.isTransparent()) && (d.isTransparent()) )
			return -1;
		if ( (this.isTransparent()) && (!d.isTransparent()) )
			return 1;
		
		
		
		//check if the two objects are "mixed"			
		if ((this.zPickMin-d.zPickMin)*(this.zPickMax-d.zPickMax)<EPSILON_Z){
			
			if (DEBUG){
				DecimalFormat df = new DecimalFormat("0.000000000");
				Application.debug("mixed :\n"
						+"zMin= "+df.format(this.zPickMin)+" | zMax= "+df.format(this.zPickMax)+" ("+this.getGeoElement().getLabel()+")\n"
						+"zMin= "+df.format(d.zPickMin)+" | zMax= "+df.format(d.zPickMax)+" ("+d.getGeoElement().getLabel()+")\n");
			}
			
			if (checkPickOrder){
				if (this.getPickOrder()<d.getPickOrder())
					return -1;
				if (this.getPickOrder()>d.getPickOrder())
					return 1;
			}
			
			// check if one is on a path and the other not
			//TODO do this only for points
			if ((this.getGeoElement3D().hasPathOn())&&(!d.getGeoElement3D().hasPathOn()))
				return -1;
			if ((!this.getGeoElement3D().hasPathOn())&&(d.getGeoElement3D().hasPathOn()))
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
	

	
	/////////////////////////////////////////////////////////////////////////////
	// links to the GeoElement
	
    public GeoElement getGeoElement() {
        return m_geo;
    } 
    
    
    public GeoElement3D getGeoElement3D() {
        return (GeoElement3D) m_geo;
    }   
    
    
    public void setGeoElement(GeoElement geo) {
        this.m_geo = geo;
    } 
    
 	

}




