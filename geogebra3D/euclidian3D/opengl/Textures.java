package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.euclidian.EuclidianView;
import geogebra.main.Application;

import java.nio.ByteBuffer;

import javax.media.opengl.GL;


/**
 * Class managing textures (dash, images, etc.)
 * @author mathieu
 *
 */
public class Textures {
	
	private GL gl;
	
	///////////////////
	//dash	
    /** opengl organization of the dash textures */
    private int[] texturesDash;    
    /** number of dash styles */
    static private int DASH_NUMBER = 4;  
	/** no dash. */
	static public int DASH_NONE = -1;	
	/** simple dash: 1-(1), ... */
	static public int DASH_SHORT = 0;	
	/** long dash: 2-(2), ... */
	static public int DASH_LONG = 1;	
	/** dotted dash: 1-(3), ... */
	static public int DASH_DOTTED = 2;		
	/** dotted/dashed dash: 7-(4)-1-(4), ... */
	static public int DASH_DOTTED_DASHED = 3;	
	/** description of the dash styles */
	static private boolean[][] DASH_DESCRIPTION = {
		{true, false, true, false}, // DASH_SHORT
		{true, true, false, false}, // DASH_LONG
		{true, false, false, false}, // DASH_DOTTED
		{true,true,true,true, true,true,true,false, false,false,false,true, false,false,false,false} // DASH_DOTTED_DASHED
	};

	
	
	
	
	
	
	
	
	
	

	/** default constructor
	 * @param gl
	 */
	public Textures(GL gl){
		this.gl = gl;
		


		gl.glEnable(GL.GL_TEXTURE_2D);
    	
    	
    	
    	// dash textures
    	texturesDash = new int[DASH_NUMBER];
    	gl.glGenTextures(DASH_NUMBER, texturesDash, 0);
        for(int i=0; i<DASH_NUMBER; i++)
        	initDashTexture(texturesDash[i],DASH_DESCRIPTION[i]);
         
        
        
        gl.glDisable(GL.GL_TEXTURE_2D);
		
	}
	

	

	/////////////////////////////////////////
	// DASH TEXTURES
	/////////////////////////////////////////

	private void initDashTexture(int n, boolean[] description){

		int sizeX = description.length; 
		int sizeY = 1;

		byte[] bytes = new byte[4*sizeX*sizeY];

		for (int i=0; i<sizeX; i++)
			if (description[i])      		
				bytes[4*i+0]=
					bytes[4*i+1]= 
						bytes[4*i+2]= 
							bytes[4*i+3]= (byte) 255;

		ByteBuffer buf = ByteBuffer.wrap(bytes);

		gl.glBindTexture(GL.GL_TEXTURE_2D, n);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_NEAREST);


		//TODO use gl.glTexImage1D ?
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  4, sizeX, sizeY, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf);

	}
	
	
	/** sets the dash texture
	 * @param dash
	 */
	public void setDash(int dash){

		gl.glBindTexture(GL.GL_TEXTURE_2D, texturesDash[dash]);
		
	}
	
	public void setDashFromLineType(int lineType){

    	switch (lineType) {
		case EuclidianView.LINE_TYPE_DOTTED:
			setDash(DASH_DOTTED);
			break;

		case EuclidianView.LINE_TYPE_DASHED_SHORT:
			setDash(DASH_SHORT);
			break;

		case EuclidianView.LINE_TYPE_DASHED_LONG:
			setDash(DASH_LONG);
			break;

		case EuclidianView.LINE_TYPE_DASHED_DOTTED:
			setDash(DASH_DOTTED_DASHED);
			break;

		default: 
			break;
    	}
	}
	

	/////////////////////////////////////////
	// LINEAR DILATATION
	/////////////////////////////////////////
	
	
	
	/** calculate texture x coords for 0 and 1 positions
	 * @param n number of repetitions per unit
	 * @param unit
	 * @param length of the cylinder
	 * @param posZero position of the "center" of the cylinder
	 * @param valZero texture coord for the "center"
	 * @return texture x coords for 0 and 1 positions
	 */	
	static final public float[] linear(int n, float unit, float length, float posZero, float valZero){

		//maxima : f(x):=a*x+b;solve([f(posZero/length)=0.25,f(unit/length)-f(0)=n],[a,b]);
		float a, b;
		a=(length*n)/unit;
		b=(unit*valZero-n*posZero)/unit;
		float start = b;
		float end = a+b;
		return new float[] {start,end};
	}
	
	

}
