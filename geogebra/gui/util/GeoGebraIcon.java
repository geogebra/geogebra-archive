package geogebra.gui.util;


import geogebra.euclidian.EuclidianView;
import geogebra.main.Application;
import geogebra.util.ImageManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
	public static final int MODE_POINT = 5;
	public static final int MODE_LINE = 6;
	public static final int MODE_SLIDER = 7;
	
	
	public void setImage(Application app, Object[] args, Dimension iconSize, int mode){

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


		case MODE_LINE:

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
			
		case MODE_SLIDER:

			int th = (Integer)args[0];
			int center = (int) (w/2);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			// fill background layer with opaque white
			fillColor = new Color(1.0f,1.0f,1.0f,1.0f);	
			g2.setPaint(fillColor);
			//g2.fillRect(0, 0, iconSize.width, iconSize.height);
			//if (getBackground()==Color.LIGHT_GRAY) g2.setColor(Color.LIGHT_GRAY); else g2.setColor(Color.WHITE); 

			// draw line
			g2.setPaint(Color.DARK_GRAY);
			//g2.setStroke(EuclidianView.getStroke(th, EuclidianView.LINE_TYPE_FULL));
			
			g2.fillRect(4, 4, 4 + th, h-8);

			break;

		}

		super.setImage(image);

	}
}
