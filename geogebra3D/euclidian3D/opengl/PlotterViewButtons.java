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
	final static public int TYPE_HANDLE = 2;
	
	
	final static public int TYPE_LENGTH = 3;
	
	
	static private float size = 25f;
	static private float shift = 10f;
	static private float shift2 = size+shift;
	static private float transparency = 0.25f;
	

	/** shift (separation) value */
	static public float SHIFT = shift;
	/** height of screen buttons value */
	static public float HEIGHT = size;
	
	static private float handleSize = 60f;
	static private float thickness = 3f;
	
	private int[] index;
	
	private Manager manager;
	

	/** common constructor
	 * @param geometryRenderer
	 */
	public PlotterViewButtons(Manager manager) {
		
		this.manager = manager;
		
		index = new int[TYPE_LENGTH];
		
		
		//buttons
		for (int i=0; i<2; i++){
			index[i] = manager.startNewList();
			manager.startGeometry(Manager.QUADS);
			button(i);
			manager.endGeometry();
			manager.endList();
		}

		//handle
		PlotterBrush brush = manager.getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);

		//sets the thickness for arrows
		brush.setThickness(1,0.7f);

		brush.setAffineTexture(0.5f, 0.125f);

		brush.start(16);
		brush.setColor(Color.GRAY);
		brush.setThickness(thickness);//re sets the thickness
		brush.segment(new GgbVector(0, 0, 0, 1),new GgbVector(handleSize, 0, 0, 1));
		brush.setThickness(thickness);//re sets the thickness
		brush.segment(new GgbVector(0, 0, 0, 1),new GgbVector(-handleSize, 0, 0, 1));
		index[2] =brush.end();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
		

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

			manager.vertex(shift2+size, size, 0);
			manager.vertex(shift2, size, 0);
			manager.vertex(shift2, 0, 0);
			manager.vertex(shift2+size, 0, 0);
			

			break;
		}
	}
	
	
	
	
	
	


	
}
