package geogebra3D.euclidian3D;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoConic3D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.GeoQuadric;
import geogebra3D.kernel3D.GeoRay3D;
import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.GeoVector3D;

import java.util.ArrayList;
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
	
	
	

	public void drawHidden(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().drawHidden(renderer);	


	}


	public void drawPicked(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().drawPicked(renderer);	

	}


	public void drawTransp(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().drawTransp(renderer);	

	}

	public void draw(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().draw(renderer);	

	}
	
	public void drawLabel(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().drawLabel(renderer,true,false);	

	}


	public void drawHiding(Renderer renderer){

		for(int i=0; i<Drawable3D.DRAW_TYPE_MAX; i++)
			for (Iterator<Drawable3D> d = lists[i].iterator(); d.hasNext();) 
				d.next().drawHiding(renderer);	

	}
	
	
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
