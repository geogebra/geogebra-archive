package geogebra3D.euclidian3D.opengl;

import geogebra.main.Application;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

/**
 * openGL capabilities for JPanel3D
 * @author matthieu
 *
 */
public class JPanel3DCapabilities extends GLCapabilities{

	public JPanel3DCapabilities() {
		super(GLProfile.getDefault());
		
		//anti-aliasing
    	setSampleBuffers(true);setNumSamples(4);    	
        //avoid flickering
    	setDoubleBuffered(true);	      	
        //stereo
    	Application.debug("stereo: "+getStereo()); 
	}
	
	

}
