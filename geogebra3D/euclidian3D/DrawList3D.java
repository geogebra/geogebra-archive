package geogebra3D.euclidian3D;

import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;




public class DrawList3D extends LinkedList{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6015533177388934376L;
	
	
	
	private ArrayList hits = null; //drawable picked
	
	
	public DrawList3D(){
		
	}
	
	
	/** update all 3D objects */
	public void updateAll(){
		
		//Application.printStacktrace("updateAll");
		
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.update();	
		}
	}
	
	
	
	
	public void drawHidden(Renderer renderer){

        for (Iterator iter = iterator(); iter.hasNext();) {
        	Drawable3D d = (Drawable3D) iter.next();
        	d.drawHidden(renderer);	
        }
	}
	
	
	public void drawPicked(Renderer renderer){

        for (Iterator iter = iterator(); iter.hasNext();) {
        	Drawable3D d = (Drawable3D) iter.next();
        	d.drawPicked(renderer);	
        }
	}
	
	
	public void drawTransp(Renderer renderer){

        for (Iterator iter = iterator(); iter.hasNext();) {
        	Drawable3D d = (Drawable3D) iter.next();
        	d.drawTransp(renderer);	
        }
	}
	
	
	public void drawLabel(Renderer renderer){

        for (Iterator iter = iterator(); iter.hasNext();) {
        	Drawable3D d = (Drawable3D) iter.next();
        	d.drawLabel(renderer,true,false);	
        }
	}
	
	
	public void drawHiding(Renderer renderer){

        for (Iterator iter = iterator(); iter.hasNext();) {
        	Drawable3D d = (Drawable3D) iter.next();
        	d.drawHiding(renderer);	
        }
	}
	

}
