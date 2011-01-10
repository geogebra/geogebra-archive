package geogebra3D.euclidian3D;

import geogebra.euclidian.DrawableND;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;

/**
 * Class for drawing GeoList (3D part)
 * 
 * @author matthieu
 *
 */
public class DrawList3D extends Drawable3D {
	
	private GeoList geoList;	
	private DrawList3DArray drawables;
	private boolean isVisible;

	/**
	 * common constructor
	 * @param view3D
	 * @param geo
	 */
	public DrawList3D(EuclidianView3D view3D, GeoList geo) {
		super(view3D, geo);
		this.geoList = geo;
		
		drawables = new DrawList3DArray(view3D);
		
	}
	

    /**
     * @return all 3D drawables contained in this list
     */
    public DrawList3DArray getDrawables3D(){
    	return drawables;
    }

	@Override
	public void draw(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGeometryPicked(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawHidden(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawHiding(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawTransp(Renderer renderer) {
		// TODO Auto-generated method stub
		
	}
	
	public Drawable3D drawForPicking(Renderer renderer) {
		
		return this;
		/*
		Application.debug("ici");
		
		for (DrawableND d : drawables)
			((Drawable3D) d).drawForPicking(renderer);
		
		return this;
		
		*/
		
	}

	public void drawLabel(Renderer renderer){
		
	}
	
	
	public void drawLabelForPicking(Renderer renderer){
			
	}
	
	@Override
	public int getPickOrder() {
		// TODO Auto-generated method stub
		return 0;
	}



	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_DEFAULT);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_DEFAULT);
    }
    
    
	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}


	protected boolean updateForItSelf() {
		
		
		
		isVisible = geoList.isEuclidianVisible();
    	if (!isVisible) return true;    	
    	
    	// go through list elements and create and/or update drawables
    	int size = geoList.size();
    	drawables.ensureCapacity(size);
    	int oldDrawableSize = drawables.size();
    	
    	int drawablePos = 0;
    	for (int i=0; i < size; i++) {    		
    		GeoElement listElement = geoList.get(i);
    		
    		//Application.debug(listElement.toString()+", "+listElement.hasDrawable3D());
    		
    		//if (!listElement.isDrawable())  continue;
    		
    		// only new 3D elements are drawn 
    		if (!listElement.hasDrawable3D())
    			continue;
    		
    		// add drawable for listElement
    		if (drawables.addToDrawableList(listElement, drawablePos, oldDrawableSize, this))
    			drawablePos++;
    		
    	}    
    	
    	// remove end of list
    	for (int i=drawables.size()-1; i >= drawablePos; i--) {      		 
    		DrawableND d = drawables.remove(i);
    		if (d.createdByDrawList()) //sets the drawable to not visible
    			d.setCreatedByDrawListVisible(false);
    	}
    	
    	return true;
	}

	@Override
	protected void updateForView() {
		// TODO Auto-generated method stub
		
	}
	
	//no label for 3D lists
	protected void updateLabel(){
		
	}

	

	protected double getColorShift(){
		return 0;
	}
}
