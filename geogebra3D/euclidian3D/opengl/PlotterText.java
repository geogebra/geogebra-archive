package geogebra3D.euclidian3D.opengl;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
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
 * Class that manages all text rendering
 * 
 * @author matthieu
 *
 */
public class PlotterText {
	
	
	private Manager manager;
	

	
	//texture writting
    protected final static Component COMPONENT = (Component) new Canvas();
	
    private static final int ALPHA_SHIFT = 24;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int BLUE_SHIFT = 0;
    private static final int COMPONENT_MASK = 0xFF;
    private static final int NB_COMPONENTS = 4;

    protected Buffer buffer;
    protected float height;
    protected float width;
    protected Texture texture;
    
    private String text;
    
    protected int textureIndex;
    
    private static final int NB_COMP = 4;

    
    //latex stuff
	
	protected TeXFormula formula;
	//protected TeXIcon texi;
	protected Icon texi;

	
	
	
	public PlotterText(Manager manager){
		
		this.manager = manager;
		
	}
	
	
	public void create(String text, int fontsize){
		
		this.text = text;
		
		/*
		try {
			formula = new TeXFormula(text);
		} catch (ParseException e) {
			Application.debug(e.toString());
		}
		
		texi = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontsize + 6);
		*/
		
		
		//Application.debug("texi: "+texi.getIconWidth()+","+texi.getIconHeight());
		
			
		

		
	}
	
	
	
	
	
	
	
	
	   public void makeImage() {
			//texi.setInsets(new Insets(1, 1, 1, 1));
			//width = texi.getIconWidth();
			//height = texi.getIconHeight();

		   width=50;height=50;
			/*
			if (width <= 0 || height <= 0) {
				formula = new TeXFormula("An\\ error\\ occured,\\ please\\ contact\\ the\\ author\\ of\\ \\JLaTeXMath");
				this.texi = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 10);
				//texi.setInsets(new Insets(1, 1, 1, 1));
				width = texi.getIconWidth();
				height = texi.getIconHeight();
			}   
			*/

			BufferedImage bimg = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
		
			Graphics2D g2d = bimg.createGraphics();
		
			AffineTransform gt = new AffineTransform();
			
			gt.translate(0, height);
			gt.scale(1, -1d);
			g2d.transform(gt);
			
		
			COMPONENT.setForeground(Color.BLACK);
			//texi.paintIcon(COMPONENT, (Graphics) g2d, 0, 0);
			g2d.setColor(Color.BLACK);
			g2d.drawString(text, 0, height);
		
			int[] intData = ((DataBufferInt) bimg.getRaster().getDataBuffer()).getData();
			buffer = ByteBuffer.wrap(ARGBtoRGBA(intData));
			g2d.dispose();
			
			//Application.debug("dim="+width+"x"+height);
			
			
			/*
			File file = new File("Example1.png");
			try {
			    ImageIO.write(bimg, "png", file.getAbsoluteFile());
			} catch (IOException ex) { }
		    */
	    }
	   
	   
	   
	   
	   private int wait = 10;
	   
	    /**
	     * Draw special content to the screen.
	     * @param content the special code
	     * @param x the x position
	     * @param y the y position
	     * @param z the z position
	     * @param scaleFactor the scale factor used in the TextRenderer
	     */
	    public void draw3D(GL gl, float x, float y, float z) {
			
	    	
	    	
	    	//float scaleFactor = 1f;

	    	
	    	if (wait>0){
	    		wait--;
	    		return;
	    	}
	    	
	    	if (texture==null && wait==0){
	    		create(text, 14);
	    		makeImage();
	    		wait--;
	    		createTexture(this);
	    		//return;
	    		//textureIndex = createTexture(this);
	    	}
	    	
	    	
	    	//if (wait<0)	    		return;
	    	
	    	
	    	//Application.debug("(x,y,z)=("+x+","+y+","+z+")");
	    	

			
			float width = getWidth();
			float height = getHeight();
			
			//gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
			//gl.glPushMatrix();

			/* The following code handles the case where the label is colored */
			
			Texture t = getTexture();
			TextureCoords tc = t.getImageTexCoords();
			
			
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
			
			t.enable();
			t.bind();
			
			
			//Application.debug("("+tc.left()+","+tc.top()+")-("+tc.right()+","+tc.bottom()+")");

			
			gl.glEnable(GL.GL_TEXTURE_2D);
			manager.getView3D().getRenderer().getTextures().loadTexture(textureIndex);
			
			
			gl.glBegin(gl.GL_QUADS);
			
			gl.glTexCoord2f(tc.left(), tc.bottom()); //gl.glVertex2d(0, 0);
			gl.glVertex3f(x,y,z); 
			gl.glTexCoord2f(tc.right(), tc.bottom()); //gl.glVertex2d(width, 0);
			gl.glVertex3f(x+width,y,z); 
			gl.glTexCoord2f(tc.right(), tc.top()); //gl.glVertex2d(width, height);
			gl.glVertex3f(x+width,y+height,z); 
			gl.glTexCoord2f(tc.left(), tc.top()); //gl.glVertex2d(0, height);
			gl.glVertex3f(x,y+height,z); 
			
			/*
			gl.glTexCoord2f(0,0); gl.glVertex2d(0, 0);
			gl.glTexCoord2f(1,0); gl.glVertex2d(width, 0);
			gl.glTexCoord2f(0,1); gl.glVertex2d(width, height);
			gl.glTexCoord2f(1,1); gl.glVertex2d(0, height);
			*/
			
			gl.glEnd();
			t.disable();

			//gl.glPopMatrix();
			//gl.glPopAttrib();
			
			gl.glDisable(GL.GL_TEXTURE_2D);
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

	    
	    /**
	     * Create a new texture with the buffer got from the image of a label
	     * @param pt the label to render
	     */
	    private void createTexture(PlotterText pt) {
		        /* If the buffer is null, it must be regenerated before getting width and height */
			Buffer buf = pt.getBuffer();
			
			
			Texture t = TextureIO.newTexture(new TextureData(GL.GL_RGBA, (int) pt.getWidth(), (int) pt.getHeight(),
									 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, false, false, false,
									 buf, null));
			
			pt.setTexture(t);
			
			
			//return manager.getView3D().getRenderer().getTextures().createTexture((int) pt.getWidth(), (int) pt.getHeight(), (ByteBuffer) pt.getBuffer());
			
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
	    public float getHeight() {
			return height;
	    }
	    
	    /**
	     * Return the width of the content
	     * @return width
	     */
	    public float getWidth() {
			return width;
	    }

}
