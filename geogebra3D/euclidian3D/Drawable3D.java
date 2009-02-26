package geogebra3D.euclidian3D;



import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.kernel3D.GeoElement3D;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.TreeSet;



/**
 * 3D representation of a {@link GeoElement3D}
 * 
 * @author ggb3D
 * @version 3.2
 *
 * 
 *
 */
public abstract class Drawable3D {

	
	private static final boolean DEBUG = false;

	
	//constants for rendering
	/** objects that are picked are drawn with a thickness * PICKED_DILATATION*/
	protected static final float PICKED_DILATATION = 1.3f;	
	/** default radius for drawing 3D points*/
	protected static final float POINT3D_RADIUS = 0.017f;
	/** points on a path are a little bit more bigger than others */
	protected static final float POINT_ON_PATH_DILATATION = 1.01f;
	/** default thickness of 3D lines, segments, ... */
	protected static final float LINE3D_THICKNESS = 0.01f;
	/** default thickness of lines of a 3D grid ... */	
	protected static final float GRID3D_THICKNESS = 0.005f;
		
	
	
	//view3D
	private EuclidianView3D m_view3D; 
	
	//matrix for openGL display
	private Ggb3DMatrix4x4 m_matrix = new Ggb3DMatrix4x4();
	
	//links to the GeoElement
	private GeoElement m_geo; 	
	private boolean m_isVisible;
	private boolean m_labelVisible;

	//picking
	//private boolean m_isPicked = false;	
	/** max picking value, used for odering elements with openGL picking */
	public float zPickMax; 
	/** min picking value, used for odering elements with openGL picking */	
	public float zPickMin; 
	private static final float EPSILON_Z = 0.0001f;//0.0001f;//10000000; //limit to consider two objects to be at the same place
	
	//constants for picking : have to be from 0 to DRAW_PICK_ORDER_MAX-1, regarding to picking order
	/** default value for picking order */
	static final public int DRAW_PICK_ORDER_MAX = 3;
	/** picking order value for 0-Dimensional objects (points) */
	static final public int DRAW_PICK_ORDER_0D = 0; 
	/** picking order value for 1-Dimensional objects (lines, segments, ...) */
	static final public int DRAW_PICK_ORDER_1D = 1; 
	/** picking order value for 2-Dimensional objects (polygons, planes, ...) */
	static final public int DRAW_PICK_ORDER_2D = 2; 
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	// constructors
		
	/** default constructor.
	 * Call the {@link #update()} method.
	 * @param a_view3D the {@link EuclidianView3D} using this Drawable3D
	 * @param a_geo the {@link GeoElement3D} linked to this GeoElement3D
	 */
	public Drawable3D(EuclidianView3D a_view3D, GeoElement3D a_geo){
		setView3D(a_view3D);
		setGeoElement(a_geo);
		update();
	}
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	// update
	
	/** update this according to the {@link GeoElement3D} */
	public void update(){
		//verify if object is visible for drawing
		setVisible(getGeoElement().isEuclidianVisible());       				 
		if (!isVisible()) return;
		setLabelVisible(getGeoElement().isLabelVisible());  //TODO label  	

		//update the matrix of the drawable for the renderer to draw it
		setMatrix(getGeoElement3D().getDrawingMatrix());
		

	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	/**
	 * set the drawing matrix
	 * 
	 * @param a_matrix the drawing matrix
	 */
	public void setMatrix(Ggb3DMatrix4x4 a_matrix){
		m_matrix=a_matrix;
	}
	
	
	/**
	 * get the drawing matrix
	 * 
	 * @return the drawing matrix
	 */
	public Ggb3DMatrix4x4 getMatrix(){
		return m_matrix;
	}

	/**
	 * get the 3D view
	 * 
	 * @return the 3D view
	 */	
	protected EuclidianView3D getView3D(){
		return m_view3D; 
	}
	
	/**
	 * set the 3D view
	 * 
	 * @param a_view3D the 3D view
	 */		
	protected void setView3D(EuclidianView3D a_view3D){
		m_view3D=a_view3D; 
	}
	
	/**
	 * say if the Drawable3D is visible
	 * 
	 * @return the visibility
	 */
	protected boolean isVisible(){
		return m_isVisible; 
	}
	
	/**
	 * set the visibility of the Drawable3D
	 * @param a_isVisible the visibility
	 */
	protected void setVisible(boolean a_isVisible){
		m_isVisible=a_isVisible; 
	}
	

	/**
	 * get the visibility of the label
	 * 
	 * @return the visibility of the label
	 */	
	protected boolean getLabelVisible(){
		return m_labelVisible; 
	}
	
	/**
	 * set the visibility of the label
	 * @param a_labelVisible the visibility of the label
	 */
	protected void setLabelVisible(boolean a_labelVisible){
		m_labelVisible=a_labelVisible; 
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////
	// drawing
	

	/**
	 * draw the geometry for not hidden parts
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void drawGeometry(EuclidianRenderer3D renderer); 
	
	/**
	 * draw the geometry for picked visibility
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void drawGeometryPicked(EuclidianRenderer3D renderer); 
	
	/**
	 * draw the geometry to show the object is picked (highlighted)
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void drawGeometryHidden(EuclidianRenderer3D renderer); 
	
	/**
	 * draw the geometry for hidden parts
	 * @param renderer the 3D renderer where to draw
	 */
	abstract public void draw(EuclidianRenderer3D renderer); 
	
	/**
	 * sets the matrix, the pencil and draw the geometry for hidden parts
	 * @param renderer the 3D renderer where to draw
	 */	
	abstract public void drawHidden(EuclidianRenderer3D renderer); 
	
	
	/**
	 * sets the matrix, the pencil and draw the geometry for transparent parts
	 * @param renderer the 3D renderer where to draw
	 */	
	abstract public void drawTransp(EuclidianRenderer3D renderer); 
	
	
	/**
	 * sets the matrix, the pencil and draw the geometry for hiding parts
	 * @param renderer the 3D renderer where to draw
	 */	
	abstract public void drawHiding(EuclidianRenderer3D renderer); 
	
	/**
	 * sets the matrix, the pencil and draw the geometry to show the object is picked (highlighted)
	 * @param renderer the 3D renderer where to draw
	 */		
	abstract public void drawPicked(EuclidianRenderer3D renderer); 

	/**
	 * sets the matrix, the pencil and draw the geometry for the {@link EuclidianRenderer3D} to process picking
	 * @param renderer the 3D renderer where to draw
	 */			
	public void drawForPicking(EuclidianRenderer3D renderer) {
		if(!getGeoElement().isEuclidianVisible())
			return;	
		
		renderer.setMatrix(getMatrix());
		renderer.setDash(EuclidianRenderer3D.DASH_NONE);
		drawGeometry(renderer);
	}

	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////
	// picking
	
	/** get picking order 
	 * @return the picking order
	 */
	abstract public int getPickOrder();
	
	/** say if another object is pickable through this Drawable3D. 
	 * @return if the Drawable3D is transparent
	 */
	abstract public boolean isTransparent();
	
	/** compare this to another Drawable3D with picking 
	 * @param d the other Drawable3D
	 * @param checkPickOrder say if the comparison has to look to pick order
	 * @return 1 if this is in front, 0 if equality, -1 either*/
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
	
    /**
     * get the GeoElement linked to the Drawable3D
     * @return the GeoElement linked to
     */
    public GeoElement getGeoElement() {
        return m_geo;
    } 
    
    /**
     * get the GeoElement linked to the Drawable3D casted to a GeoElement3D
     * @return the GeoElement3D linked to
     */  
    public GeoElement3D getGeoElement3D() {
        return (GeoElement3D) m_geo;
    }   
    
    
    /**
     * set the GeoElement linked to the Drawable3D
     * @param geo the GeoElement
     */
    public void setGeoElement(GeoElement geo) {
        this.m_geo = geo;
    } 
    
 	

}




