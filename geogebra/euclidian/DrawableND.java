package geogebra.euclidian;

import geogebra.kernel.GeoElement;

/**
 * Class for drawables in any dimension
 * 
 * @author matthieu
 *
 */
public abstract class DrawableND {
	
	
	private boolean createdByDrawList = false;	
	/** drawList that has created this */
	private DrawableND drawList;
	

	
	/**
	 * sets that this has been created by the DrawList
	 * @param drawList 
	 */
	public void setCreatedByDrawList(DrawableND drawList) {
		createdByDrawList = true;
		this.drawList = drawList;
		
	}
	
	/**
	 * @return if this has been created by a DrawList
	 */
	public boolean createdByDrawList() {
		return createdByDrawList;
		
	}
	
	/**
	 * @return the drawList that has created this (if one)
	 */
	public DrawableND getDrawListCreator(){
		return drawList;
	}
	
	
	
	/**
	 * @return the geo linked to this
	 */
	public abstract GeoElement getGeoElement();
	
	
	/**
	 * says that the drawable has to be updated
	 * in 2D : update it immediately
	 * in 3D : update at next frame
	 */
	public void setWaitForUpdate(){
		
		update();
	}
	
	/**
	 * update it immediately
	 */
	public abstract void update();
	
}
