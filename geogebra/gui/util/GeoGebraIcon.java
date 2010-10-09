package geogebra.gui.util;


import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianView;
import geogebra.main.Application;
import geogebra.util.ImageManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * Creates various ImageIcons for use in lists and tables.
 *   
 * @author G. Sturr
 *
 */
public class GeoGebraIcon extends ImageIcon {

	public GeoGebraIcon(){
		super();
	}

	
	public GeoGebraIcon(Application app, String fileName, Dimension iconSize){
		super();
		setImage(app.getImageIcon(fileName).getImage());
		ensureIconSize(iconSize);
	}

	public void createFileImageIcon(Application app, String fileName, float alpha, Dimension iconSize){

		int h = iconSize.height;
		int w = iconSize.width;
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);	 
		image = ImageManager.toBufferedImage( app.getImageManager().getImageResource(fileName));
		setImage(image);
	}



	public void createColorSwatchIcon(float alpha, Dimension iconSize, Color fgColor, Color bgColor){

		int h = iconSize.height;
		int w = iconSize.width;
		int offset = 2;
		float thickness = 3;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		--h;
		--w;

		if(bgColor != null){
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// interior fill color using alpha level

		float[] rgb = new float[3];
		fgColor.getRGBColorComponents(rgb);
		g2.setPaint(new Color( rgb[0], rgb[1], rgb[2], alpha));
		g2.fillRect(offset, offset, w-2*offset, h-2*offset);

		// border color with alpha = 1
		g2.setPaint(fgColor);
		g2.setStroke(new BasicStroke(thickness)); 
		g2.drawRect(offset, offset, w-2*offset, h-2*offset);

		setImage(image);

	}



	public void createLineStyleIcon(int dashStyle, int thickness, Dimension iconSize, Color fgColor, Color bgColor){

		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null){
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// draw dashed line
		g2.setPaint(fgColor);
		g2.setStroke(EuclidianView.getStroke(thickness, dashStyle));
		int mid = h / 2;
		g2.drawLine(4, mid, w - 4, mid);

		setImage(image);

	}


	public void createTextSymbolIcon(String symbol,Font font, Dimension iconSize, Color fgColor, Color bgColor){
		
		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null)
			g2.setBackground(bgColor);
		
	    g2.setColor (fgColor);
	    g2.setFont (new Font (font.getFamily(),Font.PLAIN,h-9));
	    
	    FontMetrics fm = g2.getFontMetrics ();
	    int symbolWidth = fm.stringWidth (symbol);
	    int ascent = fm.getMaxAscent ();
	    int descent= fm.getMaxDescent ();
	    int msg_x = w/2 - symbolWidth/2;
	    int msg_y = h/2 - descent/2 + ascent/2;

	    g2.drawString (symbol, msg_x, msg_y-2);
	    g2.fillRect(1, h-5, w-1, 3);
		setImage(image);

	}
	
	
	public void createCharIcon(String c,Font font, boolean isBold, boolean isItalic, Dimension iconSize, Color fgColor, Color bgColor){
		
		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null)
			g2.setBackground(bgColor);
		
	    g2.setColor (fgColor);
	    font = font.deriveFont((h-4)*1.0f);
	    if(isBold)
	    	font = font.deriveFont(Font.BOLD);
	    if(isItalic)
	    	font = font.deriveFont(Font.ITALIC);
	    g2.setFont (font);
	    
	    
	    FontMetrics fm = g2.getFontMetrics ();
	    int symbolWidth = fm.stringWidth (c);
	    int ascent = fm.getMaxAscent ();
	    int descent= fm.getMaxDescent ();
	    int msg_x = w/2 - symbolWidth/2;
	    int msg_y = h/2 - descent/2 + ascent/2;

	    g2.drawString (c, msg_x, msg_y);
	   
		setImage(image);

	}
	
	
	
	
	
	
	public void createPointStyleIcon(int pointStyle, int pointSize, Dimension iconSize, Color fgColor, Color bgColor){

		PointStyleImage image = new PointStyleImage(iconSize, pointStyle, pointSize,  fgColor,  bgColor);
		setImage(image);

	}

	


//TODO: draw LaTeX centered within a given icon size

	/**
	 * Draw a LaTeX image in the icon. Drawing is done twice. First draw gives 
	 * the needed size of the image. Second draw renders the image with the correct
	 * dimensions.
	 */
	public void createLatexIcon(Application app, String latex, Font font, boolean serif, Color fgColor, Color bgColor) {

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

	


	public class PointStyleImage extends BufferedImage {

		private int pointStyle = -1;

		// for drawing
		private int pointSize = 4;
		private Ellipse2D.Double circle = new Ellipse2D.Double();
		private Line2D.Double line1, line2, line3, line4;
		private GeneralPath gp = null;
		private BasicStroke borderStroke = EuclidianView.getDefaultStroke();
		private BasicStroke[] crossStrokes = new BasicStroke[10];
		private int h,w;


		public PointStyleImage(Dimension d, int pointStyle, int pointSize, Color fgColor, Color bgColor) {
			super(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			this.h = d.height;
			this.w = d.width;
			this.pointStyle = pointStyle;
			this.pointSize = pointSize;

			drawPointStyle(fgColor, bgColor);

		}


		public void drawPointStyle(Color fgColor, Color bgColor) {

			Graphics2D g2 = createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			// set background
			if (bgColor != null) 
				g2.setBackground(bgColor);

			// draw point using routine from euclidian.DrawPoint
			g2.setPaint(fgColor);
			getPath();

			switch (pointStyle) {
			case EuclidianView.POINT_STYLE_PLUS:
			case EuclidianView.POINT_STYLE_CROSS:
				// draw cross like: X or +
				g2.setStroke(crossStrokes[pointSize]);
				g2.draw(line1);
				g2.draw(line2);
				break;

			case EuclidianView.POINT_STYLE_EMPTY_DIAMOND:
				// draw diamond
				g2.setStroke(crossStrokes[pointSize]);
				g2.draw(line1);
				g2.draw(line2);
				g2.draw(line3);
				g2.draw(line4);
				break;

			case EuclidianView.POINT_STYLE_FILLED_DIAMOND:
			case EuclidianView.POINT_STYLE_TRIANGLE_NORTH:
			case EuclidianView.POINT_STYLE_TRIANGLE_SOUTH:
			case EuclidianView.POINT_STYLE_TRIANGLE_EAST:
			case EuclidianView.POINT_STYLE_TRIANGLE_WEST:
				// draw diamond
				g2.setStroke(crossStrokes[pointSize]);
				// drawWithValueStrokePure(gp, g2);
				g2.draw(gp);
				g2.fill(gp);
				break;

			case EuclidianView.POINT_STYLE_CIRCLE:
				// draw a circle
				g2.setStroke(crossStrokes[pointSize]);
				g2.draw(circle);
				break;

				// case EuclidianView.POINT_STYLE_CIRCLE:
			default:
				// draw a dot
				g2.fill(circle);
				g2.setStroke(borderStroke);
				g2.draw(circle);
			}
		}

		public void getPath() {
			// clear old path
			if (gp != null)
				gp.reset();

			// set point size
			//pointSize = 4;
			int diameter = 2 * pointSize;

			// set coords = center of cell
			double[] coords = new double[2];
			coords[0] = w / 2.0;
			coords[1] = h / 2.0;

			// get draw path using routine from euclidian.DrawPoint
			double xUL = coords[0] - pointSize;
			double yUL = coords[1] - pointSize;
			double root3over2 = Math.sqrt(3.0) / 2.0;

			switch (pointStyle) {
			case EuclidianView.POINT_STYLE_FILLED_DIAMOND:

				double xR = coords[0] + pointSize;
				double yB = coords[1] + pointSize;

				if (gp == null) {
					gp = new GeneralPath();
				}
				gp.moveTo((float) (xUL + xR) / 2, (float) yUL);
				gp.lineTo((float) xUL, (float) (yB + yUL) / 2);
				gp.lineTo((float) (xUL + xR) / 2, (float) yB);
				gp.lineTo((float) xR, (float) (yB + yUL) / 2);
				gp.closePath();

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianView.POINT_STYLE_TRIANGLE_SOUTH:
			case EuclidianView.POINT_STYLE_TRIANGLE_NORTH:

				double direction = 1.0;
				if (pointStyle == EuclidianView.POINT_STYLE_TRIANGLE_NORTH)
					direction = -1.0;

				if (gp == null) {
					gp = new GeneralPath();
				}
				gp.moveTo((float) coords[0], (float) (coords[1] + direction
						* pointSize));
				gp.lineTo((float) (coords[0] + pointSize * root3over2),
						(float) (coords[1] - direction * pointSize / 2));
				gp.lineTo((float) (coords[0] - pointSize * root3over2),
						(float) (coords[1] - direction * pointSize / 2));
				gp.lineTo((float) coords[0], (float) (coords[1] + direction
						* pointSize));
				gp.closePath();

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianView.POINT_STYLE_TRIANGLE_EAST:
			case EuclidianView.POINT_STYLE_TRIANGLE_WEST:

				direction = 1.0;
				if (pointStyle == EuclidianView.POINT_STYLE_TRIANGLE_WEST)
					direction = -1.0;

				if (gp == null) {
					gp = new GeneralPath();
				}
				gp.moveTo((float) (coords[0] + direction * pointSize),
						(float) coords[1]);
				gp.lineTo((float) (coords[0] - direction * pointSize / 2),
						(float) (coords[1] + pointSize * root3over2));
				gp.lineTo((float) (coords[0] - direction * pointSize / 2),
						(float) (coords[1] - pointSize * root3over2));
				gp.lineTo((float) (coords[0] + direction * pointSize),
						(float) coords[1]);
				gp.closePath();

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianView.POINT_STYLE_EMPTY_DIAMOND:
				xR = coords[0] + pointSize;
				yB = coords[1] + pointSize;

				if (line1 == null) {
					line1 = new Line2D.Double();
					line2 = new Line2D.Double();
				}
				if (line3 == null) {
					line3 = new Line2D.Double();
					line4 = new Line2D.Double();
				}
				line1.setLine((xUL + xR) / 2, yUL, xUL, (yB + yUL) / 2);
				line2.setLine(xUL, (yB + yUL) / 2, (xUL + xR) / 2, yB);
				line3.setLine((xUL + xR) / 2, yB, xR, (yB + yUL) / 2);
				line4.setLine(xR, (yB + yUL) / 2, (xUL + xR) / 2, yUL);

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianView.POINT_STYLE_PLUS:
				xR = coords[0] + pointSize;
				yB = coords[1] + pointSize;

				if (line1 == null) {
					line1 = new Line2D.Double();
					line2 = new Line2D.Double();
				}
				line1.setLine((xUL + xR) / 2, yUL, (xUL + xR) / 2, yB);
				line2.setLine(xUL, (yB + yUL) / 2, xR, (yB + yUL) / 2);

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianView.POINT_STYLE_CROSS:
				xR = coords[0] + pointSize;
				yB = coords[1] + pointSize;

				if (line1 == null) {
					line1 = new Line2D.Double();
					line2 = new Line2D.Double();
				}
				line1.setLine(xUL, yUL, xR, yB);
				line2.setLine(xUL, yB, xR, yUL);

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianView.POINT_STYLE_CIRCLE:
				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;
			}
			// for circle points
			circle.setFrame(xUL, yUL, diameter, diameter);
		}


	}


	
public GeoGebraIcon ensureIconSize(Dimension iconSize){
		
		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		try {	
			Image currentImage =  this.getImage();
			if(currentImage !=null){
				int h2 = currentImage.getHeight(null);
				int w2 = currentImage.getWidth(null);

				if(h2 == h && w2 == w) 
					return this;

				int wInset = (w - w2) > 0 ? (w-w2)/2 : w;
				int hInset = (h - h2) > 0 ? (h-h2)/2 : h;

				g2.drawImage(currentImage, hInset, wInset, null);
			}
			setImage(newImage);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return this;
		
	}
	
	
	

}
