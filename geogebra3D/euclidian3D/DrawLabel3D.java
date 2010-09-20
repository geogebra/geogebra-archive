package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;


/**
 * Class for drawing labels of 3D elements
 * @author matthieu
 *
 */
public class DrawLabel3D {
	
	/** text of the label */
    private String text;    
    /** color of the label */
    private Color color;
    /** origin of the label (left-bottom corner) */
    private GgbVector origin; 
    /** x and y offset */
    private float xOffset, yOffset;   
    /** says if there's an anchor to do */
    private boolean anchor;
    /** says if the label is visible */
    private boolean isVisible;
    
    /** width and height of the text */
    private int height, width;
    /** width and height of the texture */
    private int height2, width2;
    
    /** buffer containing the texture */
    private ByteBuffer buffer;
    /** index of the texture used for this label */
    private int textureIndex;
    
    /** current view where this label is drawn */
	private EuclidianView3D view;
	
	/** says it wait for reset */
	private boolean waitForReset;
	
	

	/** shift for getting alpha value */
    private static final int ALPHA_SHIFT = 24;
	
	
	
	/**
	 * common constructor
	 * @param view
	 */
	public DrawLabel3D(EuclidianView3D view){
		this.view = view;
	}
	
	
	
	/**
	 * update the label
	 * @param text
	 * @param fontsize
	 * @param color
	 * @param v
	 * @param xOffset
	 * @param yOffset
	 */
	public void update(String text, int fontsize, Color color,
			GgbVector v, float xOffset, float yOffset){
		
		this.origin = v;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.color = color;
		
		setIsVisible(true);
		
		


		if (!waitForReset && text.equals(this.text))
			return;


		this.text = text;

		//select correct font, calculates bounds
		//Font font = view.getFont();
		Font font = view.getApplication().getPlainFont();//(false, Font.PLAIN, 60);
		TextLayout textLayout = new TextLayout(text, font, new FontRenderContext(null, false, false));
		Rectangle2D rectangle = textLayout.getBounds();		
		int xMin = (int) rectangle.getMinX()-1;
		int xMax = (int) rectangle.getMaxX()+1;
		int yMin = (int) rectangle.getMinY()-1;
		int yMax = (int) rectangle.getMaxY()+1;
		width=xMax-xMin;height=yMax-yMin;
		
		//creates a 2D image
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bimg.createGraphics();
		AffineTransform gt = new AffineTransform();
		gt.scale(1, -1d);
		gt.translate(0, -height-yMax);
		g2d.transform(gt);
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		g2d.drawString(text, 0, height);

		//creates the texture
		int[] intData = ((DataBufferInt) bimg.getRaster().getDataBuffer()).getData();
		buffer = ByteBuffer.wrap(ARGBtoAlpha(intData));
		g2d.dispose();
		
		// update the texture
		updateTexture();
		waitForReset = false;
		//Application.debug("textureIndex = "+textureIndex);


	}
	
	/**
	 * set the label to be reset
	 */
	public void setWaitForReset(){
		waitForReset = true;
	}
	
	/**
	 * sets the anchor
	 * @param flag
	 */
	public void setAnchor(boolean flag){
		anchor = flag;
	}
	
	/**
	 * draws the label
	 * @param renderer
	 */
	public void draw(Renderer renderer){
		
		if (!isVisible)
    		return;
		
		if (textureIndex==0)
    		return;
		
		GgbVector v = view.getToScreenMatrix().mul(origin);
		int x = (int) (v.getX()+xOffset);
		if (anchor && xOffset<0) x-=width;
			
		int y = (int) (v.getY()+yOffset);
		if (anchor && yOffset<0) y-=height;
		
		//x = (int) (v.getX());y = (int) (v.getY());
		
		int z = (int) v.getZ();
		
		
		renderer.setColor(color, 1f);
		
		//renderer.getTextures().setTextureNearest(textureIndex);
		renderer.getTextures().setTextureLinear(textureIndex);
		
		renderer.getGeometryManager().getText().rectangle(x, y, z, width2, height2);
		
	}

	/**
	 * update the texture
	 */
    public void updateTexture() {
    	
    	
    	if (textureIndex!=0 && !waitForReset){
    		view.getRenderer().getTextures().removeTexture(textureIndex);
    		textureIndex = 0;
    	}
    	
    	
    	textureIndex = view.getRenderer().getTextures().createAlphaTexture(
    			width2, height2, 
    			buffer);
    	
    }
	

	/** 
	 * sets the visibility of the label
	 * @param flag
	 */
	public void setIsVisible(boolean flag){
		isVisible = flag;
	}
	
	
	
	
    /** get alpha channel of the array ARGB description
     * @param pix
     * @return the alpha channel of the array ARGB description
     */
    protected byte[] ARGBtoAlpha(int[] pix) {
    	
    	//calculates 2^n dimensions
    	int w = firstPowerOfTwoGreaterThan(width);
    	int h = firstPowerOfTwoGreaterThan(height);
    	
    	//Application.debug("width="+width+",height="+height+"--w="+w+",h="+h);
    	
     	//get alpha channel and extends to 2^n dimensions
		byte[] bytes = new byte[w*h];
		int bytesIndex = 0;
		int pixIndex = 0;
		for (int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				bytes[bytesIndex] = 
					(byte) (pix[pixIndex] >> ALPHA_SHIFT);
				bytesIndex++;
				pixIndex++;
			}
			bytesIndex+=w-width;
		}
		
		//update width and height
		width2=w;
		height2=h;
		
		return bytes;
    }
    

    /**
     * 
     * @param val
     * @return first power of 2 greater than val
     */
    static final private int firstPowerOfTwoGreaterThan(int val){
    	
    	int ret = 1;
    	while(ret<val)
    		ret*=2;   	
    	return ret;
    	
    }

}
