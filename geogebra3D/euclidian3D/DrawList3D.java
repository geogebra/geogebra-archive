package geogebra3D.euclidian3D;

import geogebra3D.euclidian3D.opengl.Renderer;

import java.util.Iterator;
import java.util.LinkedList;




/**
 * Class to list the 3D drawables for EuclidianView3D
 * 
 * @author ggb3D
 * 
 *
 */
public class DrawList3D {
	
	
	@SuppressWarnings("serial")
	private class Drawable3DList extends LinkedList<Drawable3D>{}
	
	/** lists of Drawable3D */
	private Drawable3DList[] lists;
	
	
	
	/**
	 * default constructor
	 */
	public DrawList3D(){
		lists = new Drawable3DList[Drawable3D.DRAW_TYPE_MAX];
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			lists[i] = new Drawable3DList();
	}
	
	
	
	/** add the drawable to the correct list
	 * @param drawable drawable to add
	 */
	public void add(Drawable3D drawable){
		
		lists[drawable.getType()].add(drawable);
		
	}
	
	/** remove the drawable from the correct list
	 * @param drawable drawable to remove
	 */
	public void remove(Drawable3D drawable){
		
		lists[drawable.getType()].remove(drawable);
		
	}
	
	/**
	 *  return the size of the cummulated lists
	 * @return the size of the cummulated lists
	 */
	public int size(){
		int size = 0;
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			size += lists[i].size();
		return size;
	}
	
	
	/**
	 * clear all the lists
	 */
	public void clear(){
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			lists[i].clear();
	}
	
	
	/** update all 3D objects */
	public void updateAll(){
		
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().update();		
		
	}
	
	
	

	/** draw the hidden (dashed) parts of curves and points
	 * @param renderer opengl context
	 */
	public void drawHidden(Renderer renderer){

		// points TODO hidden aspect ?
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_POINTS].iterator(); d.hasNext();) 
			d.next().drawHidden(renderer);
		
		// curves
		// if there's no surfaces, no hidden part has to be drawn
		if(!lists[Drawable3D.DRAW_TYPE_SURFACES].isEmpty()){
			for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CURVES].iterator(); d.hasNext();) 
				d.next().drawHidden(renderer);
		}


	}

	/** draw the highlighting of objects
	 * @param renderer opengl context
	 */
	public void drawHighlighting(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().drawHighlighting(renderer);	

	}


	/** draw surfaces as transparent parts
	 * @param renderer opengl context
	 */
	public void drawTransp(Renderer renderer){

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_SURFACES].iterator(); d.hasNext();) 
			d.next().drawTransp(renderer);	

	}

	/** draw the not hidden (solid) parts of curves and points
	 * @param renderer opengl context
	 */
	public void draw(Renderer renderer){	

		// points TODO hidden aspect ?
		/*
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_POINTS].iterator(); d.hasNext();) 
			d.next().draw(renderer);
			*/
		
		// curves
		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_CURVES].iterator(); d.hasNext();) 
			d.next().draw(renderer);
		
	}
	
	/** draw the labels of objects
	 * @param renderer opengl context
	 */
	public void drawLabel(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().drawLabel(renderer,true,false);	

	}


	/** draw the hiding (surfaces) parts
	 * @param renderer opengl context
	 */
	public void drawHiding(Renderer renderer){

		for (Iterator<Drawable3D> d = lists[Drawable3D.DRAW_TYPE_SURFACES].iterator(); d.hasNext();) 
			d.next().drawHiding(renderer);	

	}
	
	
	/** draw objects to pick them
	 * @param renderer opengl context
	 * @param drawHits recording the its
	 * @param loop counter
	 * @return return the final counter
	 */
	public int drawForPicking(Renderer renderer, Drawable3D[] drawHits, int loop){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> iter = lists[i].iterator(); iter.hasNext();) {
	        	Drawable3D d = iter.next();
	        	loop++;
	        	renderer.glLoadName(loop);
	        	d.drawForPicking(renderer);	
	        	drawHits[loop] = d;
			}
		
		return loop;

	}
	
	/** draw objects labels to pick them
	 * @param renderer opengl context
	 * @param drawHits recording the its
	 * @param loop counter
	 * @return return the final counter
	 */
	public int drawLabelForPicking(Renderer renderer, Drawable3D[] drawHits, int loop){

		
		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> iter = lists[i].iterator(); iter.hasNext();) {
	        	Drawable3D d = iter.next();
	        	loop++;
	        	renderer.glLoadName(loop);
	        	d.drawLabel(renderer,false,true);
	        	drawHits[loop] = d;
			}
		
		return loop;

	}
	

}
