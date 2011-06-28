package geogebra3D.euclidian3D.opengl;

import geogebra.main.Application;

import javax.media.opengl.awt.GLJPanel;

/**
 * Simple class extending GLJPanel
 * @author matthieu
 *
 */
public class JPanel3D extends GLJPanel{

	public JPanel3D(){

		super(new JPanel3DCapabilities());
		Application.debug("create gl renderer");
    	
	}
}
