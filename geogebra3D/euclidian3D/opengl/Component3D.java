package geogebra3D.euclidian3D.opengl;

import java.awt.BorderLayout;

import geogebra.main.Application;

import javax.media.opengl.GLCanvas;
import javax.swing.JPanel;

/**
 * Simple class extending GL JPanel/Canvas
 * @author matthieu
 *
 */
public class Component3D extends GLCanvas{

	
	public Component3D(){

		
		super(new Component3DCapabilities());
		Application.debug("create gl renderer");
		
    	
	}
	
}
