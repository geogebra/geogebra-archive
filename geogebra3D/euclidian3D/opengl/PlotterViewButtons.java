package geogebra3D.euclidian3D.opengl;

import java.awt.Color;

import geogebra.Matrix.GgbVector;

import javax.media.opengl.GL;





/**
 * Class that describes the geometry of buttons for 3D view
 * 
 * @author ggb3D
 *
 */
public class PlotterViewButtons {
	
	
	final static public int TYPE_OK = 0;
	final static public int TYPE_CANCEL = 1;
	
	
	final static public int TYPE_LENGTH = 2;
	
	
	static private float size = 25f;
	static private float shift = size+10f;
	static private float transparency = 0.25f;
	
	private int[] index;
	
	private Manager manager;
	

	/** common constructor
	 * @param geometryRenderer
	 */
	public PlotterViewButtons(Manager manager) {
		
		this.manager = manager;
		
		index = new int[TYPE_LENGTH];
		
		
		//buttons
		for (int i=0; i<TYPE_LENGTH; i++){
			index[i] = manager.startNewList();
			manager.startGeometry(Manager.QUADS);
			button(i);
			manager.endGeometry();
			manager.endList();
		}
		

	}

	
	
	//////////////////////////////////
	// INDEX
	//////////////////////////////////	
	
	/** return geometry index for each type of button
	 * @param i
	 * @return geometry index for each type of button
	 */
	public int getIndex(int i){
		return index[i];
	}
	
	//////////////////////////////////
	// GEOMETRIES
	//////////////////////////////////
	
	private void button(int i){

		switch(i){
		case TYPE_OK:
			//green
			manager.color(0,1,0,transparency);

			manager.vertex(size, size, 0);
			manager.vertex(0, size, 0);
			manager.vertex(0, 0, 0);
			manager.vertex(size, 0, 0);
			
			break;
		case TYPE_CANCEL:
			
			//red
			manager.color(1,0,0,transparency);

			manager.vertex(shift+size, size, 0);
			manager.vertex(shift, size, 0);
			manager.vertex(shift, 0, 0);
			manager.vertex(shift+size, 0, 0);
			

			break;
		}
	}
	
	
	
	
	
	


	
}
