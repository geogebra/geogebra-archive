package geogebra.gui.util;


import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianView;
import geogebra.main.Application;
import geogebra.util.ImageManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * Creates various ImageIcons for use in lists and tables.
 *   
 * @author G. Sturr
 *
 */
public class GeoGebraIcon extends ImageIcon {


	public static final int MODE_IMAGE = 0;
	public static final int MODE_IMAGE_FILE = 1;
	public static final int MODE_LATEX = 2;
	public static final int MODE_TEXT = 3;
	public static final int MODE_COLOR_SWATCH = 4;
	public static final int MODE_POINTSTYLE = 5;
	public static final int MODE_LINESTYLE = 6;
	public static final int MODE_SLIDER_LINE = 7;
	public static final int MODE_SLIDER_POINT = 8;
	
	private Application app;
	
	public void setImage(Application app, Object[] args, Dimension iconSize, int mode){

		this.app = app;
		
		Color lineColor, fillColor;	
		float alpha; 
		String fileName;
		float thickness;
		
		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		
		switch (mode){
		
		case MODE_IMAGE:

			fileName = (String) args[0];
			alpha = ((Integer) args[1])/1.0f;	 
			image = ImageManager.toBufferedImage( app.getImageManager().getImageResource(fileName));
			break;


		case MODE_COLOR_SWATCH:

			lineColor = (Color)args[0];
			alpha = (Float) args[1];

			// fill background layer with opaque white
			fillColor = new Color(1.0f,1.0f,1.0f,1.0f);	
			g2.setPaint(fillColor);
			g2.fillRect(0, 0, w, h);

			float[] rgb = new float[3];
			lineColor.getRGBColorComponents(rgb);
			fillColor = new Color(rgb[0], rgb[1], rgb[2], alpha);			

			g2.setPaint(fillColor);
			g2.fillRect(0, 0, w, h);

			g2.setPaint(lineColor);
			g2.setStroke(new BasicStroke(4)); 
			g2.drawRect(0, 0, w, h);

			break;


		case MODE_LINESTYLE:

			int dashStyle = (Integer)args[0];
			//thickness = (Integer)args[1];
			
			thickness = 1;
			
			// fill background layer with opaque white
			fillColor = new Color(1.0f,1.0f,1.0f,1.0f);	
			g2.setPaint(fillColor);
			//g2.fillRect(0, 0, w, h);
			//if (getBackground()==Color.LIGHT_GRAY) g2.setColor(Color.LIGHT_GRAY); else g2.setColor(Color.WHITE); 

			// draw dashed line
			g2.setPaint(Color.BLACK);

			g2.setStroke(EuclidianView.getStroke(thickness, dashStyle));
			int mid = h / 2;
			g2.drawLine(4, mid, w - 4, mid);

			break;
			
		case MODE_SLIDER_LINE:

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			// draw line
			g2.setPaint(Color.DARK_GRAY);
			// args[0] = line thickness
			g2.setStroke(EuclidianView.getStroke( (Integer)args[0], EuclidianView.LINE_TYPE_FULL));
			g2.drawLine(1, h/2, w-1, h/2);

			break;
			
						
		case MODE_SLIDER_POINT:

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			
			// draw circle
			
			int r =  (Integer)args[0];
			g2.setPaint(Color.LIGHT_GRAY);
			g2.fillOval(w/2 - r, h/2-r, 2*r, 2*r);
			g2.setPaint(Color.BLACK);
			g2.drawOval(w/2 - r, h/2-r, 2*r, 2*r);
			
			break;

		

		}
		super.setImage(image);

	}
	
	
	
	

	/**
	 * Draw a LaTeX image in the icon. Drawing is done twice. First draw gives 
	 * the needed size of the image. Second draw renders the image with the correct
	 * dimensions.
	 */
	public void createLatexIcon(String latex, Font font, boolean serif, Color fgColor, Color bgColor) {
		
		// Create image with dummy size, then draw into it to get the correct size
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		Dimension d = new Dimension();
		d = Drawable.drawEquation(app, g2image, 0, 0, latex, font, serif, fgColor,
				bgColor);

		// Now use this size and draw again to get the final image
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		d = Drawable.drawEquation(app, g2image, 0, 0, latex, font, serif, fgColor,
				bgColor);

		setImage(image);
		
	}
	
	
	
	
	
	
	
}
