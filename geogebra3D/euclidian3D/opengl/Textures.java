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
    private int[] texturesIndex;    
	/** no dash. */
	static public int DASH_NONE = 0;	
	/** simple dash: 1-(1), ... */
	static public int DASH_SHORT = DASH_NONE+1;	
	/** long dash: 2-(2), ... */
	static public int DASH_LONG = DASH_SHORT+1;	
	/** dotted dash: 1-(3), ... */
	static public int DASH_DOTTED = DASH_LONG+1;		
	/** dotted/dashed dash: 7-(4)-1-(4), ... */
	static public int DASH_DOTTED_DASHED = DASH_DOTTED+1;	
    /** number of dash styles */
    static private int DASH_NUMBER = DASH_DOTTED_DASHED+1;  
	/** description of the dash styles */
	static private boolean[][] DASH_DESCRIPTION = {
		{true}, // DASH_NONE
		{true, false, true, false}, // DASH_SHORT
		{true, true, false, false}, // DASH_LONG
		{true, false, true, false, true, false, true, false}, //, false, false}, // DASH_DOTTED
		{true,true,true,true, true,true,true,false, false,false,false,true, false,false,false,false} // DASH_DOTTED_DASHED
	};

	
	
	
	///////////////////
	//fading
	/** fading texture for surfaces */
	static public int FADING = DASH_NUMBER;
	
	
	static private int TEXTURES_NUMBER = FADING+1;

	
	
	
	
	

	/** default constructor
	 * @param gl
	 */
	public Textures(GL gl){
		this.gl = gl;
		


		gl.glEnable(GL.GL_TEXTURE_2D);
    	
		texturesIndex = new int[TEXTURES_NUMBER];
    	
    	// dash textures
    	
    	gl.glGenTextures(DASH_NUMBER, texturesIndex, 0);
        for(int i=0; i<DASH_NUMBER; i++)
        	initDashTexture(texturesIndex[i],DASH_DESCRIPTION[i]);
        
        // fading textures
        initFadingTexture(texturesIndex[DASH_NUMBER]);
         
        
        
        gl.glDisable(GL.GL_TEXTURE_2D);
		
	}
	

	
	/** load a template texture
	 * @param index
	 */
	public void loadTexture(int index){

		//gl.glBindTexture(GL.GL_TEXTURE_2D, texturesIndex[index]);
		setTexture(texturesIndex[index]);
	}
	
	
	/** sets a computed texture
	 * @param index
	 */
	public void setTexture(int index){

		gl.glBindTexture(GL.GL_TEXTURE_2D, index);
		
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
	
	

	
	
	
	public void setDashFromLineType(int lineType){

    	switch (lineType) {
		case EuclidianView.LINE_TYPE_FULL:
			loadTexture(DASH_NONE);
			break;
			
		case EuclidianView.LINE_TYPE_DOTTED:
			loadTexture(DASH_DOTTED);
			break;

		case EuclidianView.LINE_TYPE_DASHED_SHORT:
			loadTexture(DASH_SHORT);
			break;

		case EuclidianView.LINE_TYPE_DASHED_LONG:
			loadTexture(DASH_LONG);
			break;

		case EuclidianView.LINE_TYPE_DASHED_DOTTED:
			loadTexture(DASH_DOTTED_DASHED);
			break;

		default: 
			break;
    	}
	}
	


	/////////////////////////////////////////
	// DASH TEXTURES
	/////////////////////////////////////////

	private void initFadingTexture(int index){
		
		
		
		
		boolean[] description = {
				true, false,
				false,false
		};
		
		
		
		int sizeX = 2,  sizeY = 2;
		
		
		/*
		int n = 3;
		int sizeX = (int) Math.pow(2, n); int sizeY = sizeX;
		boolean[] description = new boolean[sizeX*sizeY];
		for (int i=0; i<sizeX-1; i++)
			for (int j=0; i<sizeY-1; i++){
				description[i+j*sizeX] = true;
			}
		*/
		
		/*
		boolean[] description = {
				true, true, true, false,
				true, true, true, false,
				true, true, true, false,
				false,false,false,false
		};
		
		int sizeX = 4,  sizeY = 4;		
		*/


		
		byte[] bytes = new byte[sizeX*sizeY];

		for (int i=0; i<sizeX*sizeY; i++)
			if (description[i])      		
				bytes[i]= (byte) 255;

		ByteBuffer buf = ByteBuffer.wrap(bytes);

		gl.glBindTexture(GL.GL_TEXTURE_2D, index);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE); //prevent repeating the texture
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE); //prevent repeating the texture


		//TODO use gl.glTexImage1D ?
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  GL.GL_ALPHA, sizeX, sizeY, 0, GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, buf);

	}
	
	

	/////////////////////////////////////////
	// IMAGE TEXTURES
	/////////////////////////////////////////
	
	
	
	
	/**
	 * removes the texture
	 * @param index
	 */
	public void removeTexture(int index){
		//size, array, offset
		gl.glDeleteTextures(1, new int[] {index}, 0);
	}

	/** 
	 * @param sizeX
	 * @param sizeY
	 * @param buf
	 * @return a texture for alpha channel
	 */
	public int createAlphaTexture(int sizeX, int sizeY, ByteBuffer buf){
		
		gl.glEnable(GL.GL_TEXTURE_2D);  
		
		int[] index = new int[1];
     	gl.glGenTextures(1, index, 0);


		
		gl.glBindTexture(GL.GL_TEXTURE_2D, index[0]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE); //prevent repeating the texture
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE); //prevent repeating the texture

		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  GL.GL_ALPHA, sizeX, sizeY, 0, GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, buf);
      
        
        gl.glDisable(GL.GL_TEXTURE_2D);
        
        return index[0];
	}
	

}
