package geogebra3D.euclidian3D;

import geogebra.Application;
import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;




public class DrawList3D extends LinkedList{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6015533177388934376L;
	
	
	static final boolean DEBUG = false; //conditionnal compilation
	
	private ArrayList hits = null; //drawable picked
	
	
	public DrawList3D(){
		
	}
	
	
	/** update all 3D objects */
	public void updateAll(){
		
		if(DEBUG){Application.debug("updateAll");}
		
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.update();	
		}
	}
	
	
	
	
	
	
	
	
	////////////////////////////////////////
	// picking
	
	public ArrayList doPick(GgbVector pickPoint, boolean list, boolean repaint){
			
		hits = new ArrayList();
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			if (d.isPicked(pickPoint,repaint)){				
				if (list)
					hits.add(d.getGeoElement());
			}
			//d.getGeoElement().updateRepaint();//for highlighting in algebraView
		}
		
		return hits;
		
	}
	

}
