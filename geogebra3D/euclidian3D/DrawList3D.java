package geogebra3D.euclidian3D;

import geogebra.Application;
import geogebra.kernel.linalg.GgbVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.j3d.Appearance;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;



public class DrawList3D extends LinkedList{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6015533177388934376L;
	
	
	static final boolean DEBUG = false; //conditionnal compilation
	
	
	Appearance hidingApp, pickedApp;
	
	
	public DrawList3D(){
		
		//appearance for hiding objects
		hidingApp = new Appearance();		
	    RenderingAttributes ra = new RenderingAttributes();
	    ra.setRasterOpEnable(true);
	    ra.setRasterOp(RenderingAttributes.ROP_NOOP); //don't draw it
	    hidingApp.setRenderingAttributes(ra);			    	    
	    PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE ); //no culling for back faces
		//pa.setBackFaceNormalFlip(true);		
		hidingApp.setPolygonAttributes(pa);		
		
		
		
		
		//appearance for picked objects
		pickedApp = new Appearance();
		pickedApp.setMaterial(new Material(new Color3f(0,0,0), 
				new Color3f(0,0,0), 
				new Color3f(0.25f,0.25f,0.25f), //gray
				new Color3f(1, 1, 1), 15));
		pickedApp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST,0.25f));
		/*
		ra = new RenderingAttributes();	    	   
	    ra.setDepthBufferWriteEnable(false); //don't write zbuffer	    
	    pickedApp.setRenderingAttributes(ra);
		*/
	}
	
	
	/** update all 3D objects */
	public void updateAll(){
		
		if(DEBUG){Application.debug("updateAll");}
		
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.update();	
		}
	}
	
	
	/** draw all 3D objects */
	public void drawAll(GraphicsContext3D gc){
		
		if(DEBUG){Application.debug("updateAll");}
		

		//drawing hidden parts
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawHidden(gc);	
		}
		
		//drawing picked objects
		gc.setAppearance(pickedApp);
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawPicked(gc);	
		}			
		
		//drawing transparents parts
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawTransp(gc);	
		}

		//drawing hiding parts
		gc.setAppearance(hidingApp);
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
	
	
	
	
	
	
	
	////////////////////////////////////////
	// picking
	
	public ArrayList doPick(GgbVector pickPoint, boolean list){
		
		ArrayList hits = new ArrayList();
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			if (d.isPicked(pickPoint))
				if (list)
					hits.add(d.getGeoElement());
		}
		
		return hits;
		
	}
	

}
