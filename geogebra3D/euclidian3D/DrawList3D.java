package geogebra3D.euclidian3D;

import java.util.Iterator;
import java.util.LinkedList;

import javax.media.j3d.GraphicsContext3D;



public class DrawList3D extends LinkedList{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6015533177388934376L;
	
	
	static final boolean DEBUG = false; //conditionnal compilation
	
	
	
	/** update all 3D objects */
	public void updateAll(){
		
		if(DEBUG){System.out.println("updateAll");}
		
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.update();	
		}
	}
	
	
	/** draw all 3D objects */
	public void drawAll(GraphicsContext3D gc){
		
		if(DEBUG){System.out.println("updateAll");}
		

		//drawing hidden parts
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawHidden(gc);	
		}
		
		//drawing transparents parts
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawTransp(gc);	
		}

		//drawing hiding parts
		gc.setAppearance(Drawable3D.hidingApp());
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawHiding(gc);	
		}
		
		//drawing not hidden parts
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.draw(gc);	
		}
	}
	

}
