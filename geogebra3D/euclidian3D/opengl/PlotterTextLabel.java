package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.ParseException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

/**
 * Class for "one" text label
 * 
 * @author matthieu
 *
 */
public class PlotterTextLabel {
	
	
	//private Manager manager;
	private Renderer renderer;
	private EuclidianView3D view;
	

	
	//texture writting
    protected final static Component COMPONENT = (Component) new Canvas();
	
    private static final int ALPHA_SHIFT = 24;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int BLUE_SHIFT = 0;
    private static final int COMPONENT_MASK = 0xFF;
    private static final int NB_COMPONENTS = 4;

    protected Buffer buffer;
    protected int height, width;
    protected Texture texture = null;
    
    private String text;
    private Color color;
    private GgbVector origin; 
    private float xOffset, yOffset;
    private boolean isVisible;
    
    protected int textureIndex;
    
    private static final int NB_COMP = 4;

    
    //latex stuff
	
	protected TeXFormula formula;
	//protected TeXIcon texi;
	protected Icon texi;

	
	
	
	public PlotterTextLabel(EuclidianView3D view){
		
		this.view = view;
		this.renderer = view.getRenderer();
		
	}
	
	
	
	public void update(String text, int fontsize, Color color,
			GgbVector v, float xOffset, float yOffset){
		
		this.origin = v;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.color = color;
		
		setIsVisible(true);
		
		
		// TODO check if the text is already set
		if (text.equals(this.text)){	
			updateTexture();
			return;
		}
		

		this.text = text;

		Font font = view.getFont();
		TextLayout textLayout = new TextLayout(text, font, new FontRenderContext(null, false, false));
		Rectangle2D rectangle = textLayout.getBounds();
		
		int xMin = (int) rectangle.getMinX()-1;
		int xMax = (int) rectangle.getMaxX()+1;
		int yMin = (int) rectangle.getMinY()-1;
		int yMax = (int) rectangle.getMaxY()+1;
		
		/*
		Application.debug("bounds:("
				+rectangle.getMinX()+","+rectangle.getMinY()
				+")--("+
				+rectangle.getMaxX()+","+rectangle.getMaxY()
				+")"
		);

*/

		//int n = 32;//+2;
		width=xMax-xMin;height=yMax-yMin;
		//int n = 50;//+2;
		//width=n;height=n;

		BufferedImage bimg = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = bimg.createGraphics();

		AffineTransform gt = new AffineTransform();

		//gt.translate(0, -yMax);
		gt.scale(1, -1d);
		gt.translate(0, -height-yMax);
		g2d.transform(gt);


		//COMPONENT.setForeground(Color.BLACK);
		//texi.paintIcon(COMPONENT, (Graphics) g2d, 0, 0);
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, height);

		int[] intData = ((DataBufferInt) bimg.getRaster().getDataBuffer()).getData();
		//buffer = ByteBuffer.wrap(ARGBtoRGBA(intData));
		buffer = ByteBuffer.wrap(ARGBtoAlpha(intData));
		g2d.dispose();
		
		updateTexture();


	}
	
	
	public void setIsVisible(boolean flag){
		isVisible = flag;
		
		
	}
	   
	   
	   
	   
	   private int wait = 10;
	   
	    /**
	     * Draw special content to the screen.
	     */
	    public void draw3D(GL gl, GgbMatrix toScreenMatrix) {
			
	    	if (!isVisible)
	    		return;
	    	
	    	/*
	    	if (texture==null)
	    		return;
	    	*/
	    	
	    	if (textureIndex==0)
	    		return;
	    	

			
			
			//gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
			//gl.glPushMatrix();

			/* The following code handles the case where the label is colored */
			
			/*
			Texture t = getTexture();
			TextureCoords tc = t.getImageTexCoords();
			*/
			
			
			//gl.glEnable(GL.GL_BLEND);
			//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
					
			// required to correctly render pre-colored text 
			//if (spe.getIsColored()) {
				//gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			//}
			
			// the following lines fix a strange behaviour of GL_ADD on Windows 
			/*
			float[] f = new float[NB_COMP];
			gl.glGetFloatv(GL.GL_CURRENT_COLOR, f, 0);
			f[0] = 1 - f[0];
			f[1] = 1 - f[1];
			f[2] = 1 - f[2];
			gl.glTexEnvfv(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_COLOR, f, 0);
			
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_BLEND);
			*/
			
			//gl.glTranslatef(x, y, z);
			
			/*
			t.enable();
			t.bind();
			*/
			
			
			//Application.debug("("+tc.left()+","+tc.top()+")-("+tc.right()+","+tc.bottom()+")");

			GgbVector v = toScreenMatrix.mul(origin);
			int x = (int) (v.getX()+xOffset);
			int y = (int) (v.getY()+yOffset);
			int z = (int) v.getZ();
			
			
			renderer.setColor(color, 1f);
			
			renderer.getTextures().setTexture(textureIndex);
			//renderer.getTextures().loadTexture(Textures.FADING);
			
			
			
			gl.glBegin(GL.GL_QUADS);
			
			//gl.glTexCoord2f(tc.left(), tc.bottom()); //gl.glVertex2d(0, 0);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3i(x,y,z); 
			//gl.glTexCoord2f(tc.right(), tc.bottom()); //gl.glVertex2d(width, 0);
			gl.glTexCoord2f(1, 0);
			gl.glVertex3i(x+width,y,z); 
			//gl.glTexCoord2f(tc.right(), tc.top()); //gl.glVertex2d(width, height);
			gl.glTexCoord2f(1, 1);
			gl.glVertex3i(x+width,y+height,z); 
			//gl.glTexCoord2f(tc.left(), tc.top()); //gl.glVertex2d(0, height);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3i(x,y+height,z); 
			
			/*
			gl.glTexCoord2f(0,0); gl.glVertex2d(0, 0);
			gl.glTexCoord2f(1,0); gl.glVertex2d(width, 0);
			gl.glTexCoord2f(0,1); gl.glVertex2d(width, height);
			gl.glTexCoord2f(1,1); gl.glVertex2d(0, height);
			*/
			
			gl.glEnd();
			//t.disable();

			//gl.glPopMatrix();
			//gl.glPopAttrib();
		
	    }
	   
	   
	   
	    ///////////////////////////////////////
	    // CONVERSION
	    ///////////////////////////////////////

	   
	    /**
	     * Convert an ARGB pixmap into RGBA pixmap
	     * @param pix pixmap ARGB data
	     * @return pixmap RGBA data 
	     */
	    protected static byte[] ARGBtoRGBA(int[] pix) {
			byte[] bytes = new byte[pix.length * NB_COMPONENTS];
			int p;
			int [] tmpPix = new int[NB_COMPONENTS];
			int j = 0;
			for (int i = 0; i < pix.length; i++) {
			    p = pix[i];
			    tmpPix[0] = (p >> ALPHA_SHIFT) & COMPONENT_MASK;
			    tmpPix[1] = (p >> RED_SHIFT) & COMPONENT_MASK;
			    tmpPix[2] = (p >> GREEN_SHIFT) & COMPONENT_MASK;
			    tmpPix[NB_COMPONENTS - 1] = (p >> BLUE_SHIFT) & COMPONENT_MASK;
			    bytes[j] = (byte) tmpPix[1];
			    bytes[j + 1] = (byte) tmpPix[2];
			    bytes[j + 2] = (byte) tmpPix[NB_COMPONENTS - 1];
			    bytes[j + NB_COMPONENTS - 1] = (byte) tmpPix[0];
			    j += NB_COMPONENTS;
			}
			
			return bytes;
	    }

	    
	    /** get alpha channel of the array ARGB description
	     * @param pix
	     * @return the alpha channel of the array ARGB description
	     */
	    protected byte[] ARGBtoAlpha(int[] pix) {
	    	
	    	int w = firstPowerOfTwoGreaterThan(getWidth());
	    	int h = firstPowerOfTwoGreaterThan(getHeight());
	    	
	    	//Application.debug("w="+w+",h="+h);
	    	
			byte[] bytes = new byte[w*h];
			int p;
			//String s = "";
			int bytesIndex = 0;
			int pixIndex = 0;
			for (int y = 0; y < getHeight(); y++){
				for (int x = 0; x < getWidth(); x++){
					bytes[bytesIndex] = (byte) (pix[pixIndex] >> ALPHA_SHIFT);
					bytesIndex++;
					pixIndex++;
					/*
					for (int i = 0; i < pix.length; i++) {
						p = pix[i];
						bytes[i] = (byte) (p >> ALPHA_SHIFT);
						//s+="\ni="+i+",int="+pix[i]+",byte="+bytes[i];
					}
					*/
				}
				bytesIndex+=w-getWidth();
			}
			//Application.debug(s);
			
			width=w;
			height=h;
			
			return bytes;
	    }

	    
	    static final private int firstPowerOfTwoGreaterThan(int val){
	    	
	    	int ret = 1;
	    	while(ret<val)
	    		ret*=2;
	    	
	    	return ret;
	    	
	    }
	    
	    /*
	     * Create a new texture with the buffer got from the image of a label
	     /
	    private void createTexture() {
			Buffer buf = getBuffer();
			Texture t = TextureIO.newTexture(new TextureData(GL.GL_RGBA, 
					(int) getWidth(), (int) getHeight(),
					0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, false, false, false,
					buf, null));
			setTexture(t);
	    }
	    */
	    
	    public void updateTexture() {
	    	
	    	if (textureIndex!=0){
	    		renderer.getTextures().removeTexture(textureIndex);
	    		textureIndex = 0;
	    	}
	    	
	    	textureIndex = renderer.getTextures().createAlphaTexture(
	    			getWidth(), getHeight(), 
	    			(ByteBuffer) getBuffer());
	    	
	    	//Application.debug("textureIndex="+textureIndex);
	    	
	    }
	    
	    
	    
	    ///////////////////////////////////////
	    // SETTERS AND GETTERS
	    ///////////////////////////////////////
	    
	    /**
	     * Set the texture's name associated to this label
	     * @param t texture used by GL
	     */
	    public void setTexture(Texture t) {
			texture = t;
			/* The buffer is set to null since GL put it into the buffer of the video card */
			this.buffer = null;
	    }
	    

	    /**
	     * Return the texture's name associated to this label
	     * @return the texture object
	     */
	    public Texture getTexture() {
			return texture;
	    }
	    
	    /**
	     * Return a byte-buffer used to draw content
	     * @return byte-buffer 
	     */
	    public Buffer getBuffer() {
	    	return buffer;
	    	/*
			if (buffer != null) {
			    return buffer;
			}
			makeImage();
			return buffer;
			*/
	    }
	    
	    

	    /**
	     * Return the height of the content
	     * @return height
	     */
	    public int getHeight() {
			return height;
	    }
	    
	    /**
	     * Return the width of the content
	     * @return width
	     */
	    public int getWidth() {
			return width;
	    }

}
