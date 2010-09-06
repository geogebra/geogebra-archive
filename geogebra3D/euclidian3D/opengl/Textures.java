package geogebra3D.euclidian3D.opengl;

import geogebra.euclidian.EuclidianView;

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
		gl.glGenTextures(TEXTURES_NUMBER, texturesIndex, 0);

		// dash textures
		for(int i=0; i<DASH_NUMBER; i++)
			initDashTexture(texturesIndex[i],DASH_DESCRIPTION[i]);

		// fading textures
		initFadingTexture(texturesIndex[FADING]);



		gl.glDisable(GL.GL_TEXTURE_2D);

	}
	

	
	/** load a template texture (nearest type)
	 * @param index
	 */
	public void loadTextureNearest(int index){

		setTextureNearest(texturesIndex[index]);
		
	}
	
	/** load a template texture (linear type)
	 * @param index
	 */
	public void loadTextureLinear(int index){

		setTextureLinear(texturesIndex[index]);
		
	}
	
	/** sets a computed texture (nearest type)
	 * @param index
	 */
	public void setTextureNearest(int index){

		gl.glBindTexture(GL.GL_TEXTURE_2D, index);
		setTextureNearest();
		
	}
	
	/** sets a computed texture (linear type)
	 * @param index
	 */
	public void setTextureLinear(int index){

		gl.glBindTexture(GL.GL_TEXTURE_2D, index);
		setTextureLinear();
		
	}

	/////////////////////////////////////////
	// DASH TEXTURES
	/////////////////////////////////////////

	private void initDashTexture(int n, boolean[] description){

		int sizeX = description.length; 
		//int sizeY = 1;

		//byte[] bytes = new byte[4*sizeX*sizeY];
		byte[] bytes = new byte[sizeX];

		for (int i=0; i<sizeX; i++)
			if (description[i])   
				bytes[i]= (byte) 255;
				/*
				bytes[4*i+0]=
					bytes[4*i+1]= 
						bytes[4*i+2]= 
							bytes[4*i+3]= (byte) 255;
							*/

		ByteBuffer buf = ByteBuffer.wrap(bytes);

		gl.glBindTexture(GL.GL_TEXTURE_2D, n);

		


		//TODO use gl.glTexImage1D ?
		//gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  4, sizeX, sizeY, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  GL.GL_ALPHA, sizeX, 1, 0, GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, buf);

	}
	
	

	
	
	
	/**
	 * call the correct texture for the line type specified
	 * @param lineType
	 */
	public void setDashFromLineType(int lineType){

    	switch (lineType) {
		case EuclidianView.LINE_TYPE_FULL:
			loadTextureNearest(DASH_NONE);
			break;
			
		case EuclidianView.LINE_TYPE_DOTTED:
			loadTextureNearest(DASH_DOTTED);
			break;

		case EuclidianView.LINE_TYPE_DASHED_SHORT:
			loadTextureNearest(DASH_SHORT);
			break;

		case EuclidianView.LINE_TYPE_DASHED_LONG:
			loadTextureNearest(DASH_LONG);
			break;

		case EuclidianView.LINE_TYPE_DASHED_DOTTED:
			loadTextureNearest(DASH_DOTTED_DASHED);
			break;

		default: 
			break;
    	}
	}
	


	/////////////////////////////////////////
	// DASH TEXTURES
	/////////////////////////////////////////

	private void initFadingTexture(int index){
		
		/*
		int n=256;
		int sizeX = n,  sizeY = n;
		
		boolean[] description = new boolean[sizeX*sizeY];
		
		for (int i=0;i<sizeX;i++)
				for (int j=0; j<sizeY;j++)
					if (Math.random()*n*n>i*j)
						description[i+sizeX*j]=true;
		*/

		
		int n=2;
		int sizeX = n,  sizeY = n;
		boolean[] description = {
				true, false,
				false,false
		};
		
		
		
		
		byte[] bytes = new byte[sizeX*sizeY];

		for (int i=0; i<sizeX*sizeY; i++)
			if (description[i])      		
				bytes[i]= (byte) 255;

		ByteBuffer buf = ByteBuffer.wrap(bytes);

		gl.glBindTexture(GL.GL_TEXTURE_2D, index);
		
		/*
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_NEAREST);
*/

		//TODO use gl.glTexImage1D ?
		//gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  4, sizeX, sizeY, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf);

		
		//setTextureLinear();


		//TODO use gl.glTexImage1D ?
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  GL.GL_ALPHA, sizeX, sizeY, 0, GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, buf);

	}
	
	
	private void setTextureLinear(){
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE); //prevent repeating the texture
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE); //prevent repeating the texture

	}

	private void setTextureNearest(){
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_NEAREST);
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
		
		
		
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  GL.GL_ALPHA, sizeX, sizeY, 0, GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, buf);
      
        
        gl.glDisable(GL.GL_TEXTURE_2D);
        
        return index[0];
	}
	

}
