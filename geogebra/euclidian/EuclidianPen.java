package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoPoint;
import geogebra.main.Application;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class EuclidianPen {

	private Application app;
	private EuclidianView view;
	
	private int penOffsetX = 0;
	private int penOffsetY = 0;
	private boolean penUsingOffsets = false;
	private BufferedImage penImage = null;
	private GeoImage penGeo = null; // used if drawing to existing GeoImage	
	private GeoImage lastPenImage = null;
	private boolean penWritingToExistingImage = false;
	private ArrayList penPoints = new ArrayList();


	private boolean erasing = false;
	
	private MiniStyle style;
	private int penSize, eraserSize, penLineStyle;
	private Color penColor;
	
	
	/************************************************
	 * Construct EuclidianPen
	 */
	public EuclidianPen(Application app, EuclidianView view){
		this.view = view;
		this.app = app;
		
		this.style = new MiniStyle(app, MiniStyle.MODE_PEN);
		penSize = style.pointSize;
		eraserSize = style.pointSize;
		penColor = style.color;
		penLineStyle = style.lineStyle;
	}

	

	//===========================================
	//       Getters/Setters
	//===========================================

	public MiniStyle getStyle() {
		return style;
	}

	public void setStyle(MiniStyle style) {
		this.style = style;
		penSize = style.pointSize;
		eraserSize = style.pointSize;
		penColor = style.color;
		penLineStyle = style.lineStyle;
		
	}
	
	public boolean isErasing() {
		return erasing;
	}
	
	public void setErasing(boolean erasing) {
		this.erasing = erasing;
	}
	
	
	public boolean isPenWritingToExistingImage() {
		return penWritingToExistingImage;
	}


	public void setPenWritingToExistingImage(boolean penWritingToExistingImage) {
		this.penWritingToExistingImage = penWritingToExistingImage;
	}
	
	public GeoImage getPenGeo() {
		return penGeo;
	}


	public void setPenGeo(GeoImage penGeo) {
		this.penGeo = penGeo;
	}

	
	public void resetPenOffsets(){
		penOffsetX = 0;
		penOffsetY = 0;
		penUsingOffsets = false;
	}
	
	
	
	
	//===========================================
	//       Mouse Event Handlers
	//===========================================

	
	public void handleMousePressedForPenMode(MouseEvent e) {

		Rectangle rect = view.getSelectionRectangle();


		if (Application.isRightClick(e)) {
			view.setCursor(app.getEraserCursor());
			erasing = true;
		} else {	
			view.setCursor(app.getTransparentCursor());
			erasing = false;
		}
		
		//Graphics2D g2D = null;
		
		
		
		if (penGeo != null) {
			// image was selected before Pen Tool selected
			
			penUsingOffsets = true;
			penImage = penGeo.getFillImage();
			//lastPenImage = penGeo;
			
			penWritingToExistingImage = true;
			
			if (penGeo.isAbsoluteScreenLocActive()) {
				penOffsetX = penGeo.getAbsoluteScreenLocX();
				penOffsetY = penGeo.getAbsoluteScreenLocY();
			} else {
				GeoPoint startPoint = penGeo.getStartPoint();
				penOffsetX = view.toScreenCoordX(startPoint.inhomX);
				penOffsetY = view.toScreenCoordY(startPoint.inhomY) - penImage.getHeight();
				
			}
			
			app.addSelectedGeo(penGeo);

			
			
			penGeo = null;
		} else
		if (rect != null && (!penUsingOffsets || penOffsetX != rect.x || 
				penOffsetY != rect.y) ) {
			// just draw on a subset of the Graphics View
			GraphicsEnvironment ge =
				GraphicsEnvironment.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc =
				gs.getDefaultConfiguration();
			penImage = gc.createCompatibleImage((int)rect.getWidth(),
					(int)rect.getHeight(), Transparency.BITMASK);
			
			lastPenImage = null;
			
			penOffsetX = rect.x;
			penOffsetY = rect.y;
			penUsingOffsets = true;
			
			penWritingToExistingImage = false;

			
			//view.setSelectionRectangle(null);
		}
		else if (lastPenImage != null && !penWritingToExistingImage) {

			penImage = lastPenImage.getFillImage();

			GeoPoint corner = lastPenImage.getCorner(0);
			int x = view.toScreenCoordX(corner.getInhomX());
			int y = view.toScreenCoordY(corner.getInhomY());
			int width = penImage.getWidth();
			int height = penImage.getHeight();

			
			// check if image is still the same size as the current euclidian view window
			if ((penOffsetX >0 && penOffsetY > 0) || 
					(x == 0 && y == height && height == view.getHeight() && width == view.getWidth()))
				penImage = lastPenImage.getFillImage();
			else {
				penImage = null;
				lastPenImage = null;
			}
			
			penWritingToExistingImage = false;

		}

		if (penImage == null) {

			GraphicsEnvironment ge =
				GraphicsEnvironment.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc =
				gs.getDefaultConfiguration();
			penImage = gc.createCompatibleImage(view.getWidth(),
					view.getHeight(), Transparency.BITMASK);

		}

		//if (g2D == null) g2D = penImage.createGraphics();


		Point newPoint = new Point(e.getX() - penOffsetX, e.getY() - penOffsetY);
		Graphics2D g2D = (Graphics2D) view.getGraphics();
		Shape circle;
		if (Application.isRightClick(e)) {
			g2D.setColor(Color.white);
			circle = new Ellipse2D.Float(e.getX() - eraserSize, e.getY() - eraserSize, eraserSize*2, eraserSize*2);		
		} else {
			g2D.setColor(penColor);
			circle = new Ellipse2D.Float(e.getX() - penSize, e.getY() - penSize, penSize*2, penSize*2);
		}
		//g2D.drawOval(e.getX(), e.getY(), penSize, penSize);
		g2D.fill(circle);

		if (penPoints.size() == 0)
			penPoints.add(newPoint);
		else {
			Point lastPoint = (Point)penPoints.get(penPoints.size() - 1);
			if (lastPoint.distance(newPoint) > 3)
				penPoints.add(newPoint);
		}
	}

	
	
	public void handleMouseReleasedForPenMode(MouseEvent e) {

		
		if (penImage == null) return; // right click
		
		app.setDefaultCursor();
		
		Point newPoint = new Point(e.getX() - penOffsetX, e.getY() - penOffsetY);
		penPoints.add(newPoint);


		//if (lastPenImage != null) penImage = lastPenImage.getImage(); //app.getExternalImage(lastPenImage);



		//Application.debug(penPoints.size()+"");

		PolyBezier pb = new PolyBezier(penPoints);

		Graphics2D g2d = (Graphics2D)penImage.getGraphics();

		if (erasing) {
			g2d.setStroke(EuclidianView.getStroke(2 * eraserSize, (penPoints.size() <= 2) ? EuclidianView.LINE_TYPE_FULL : penLineStyle));
			g2d.setColor(new Color(0, 0, 0, 0)); // transparent	
			g2d.setComposite(AlphaComposite.Src);
		} else {
			g2d.setStroke(EuclidianView.getStroke(2 * penSize, (penPoints.size() <= 2) ? EuclidianView.LINE_TYPE_FULL : penLineStyle));
			g2d.setColor(penColor);
		}
		g2d.draw(pb.gp);

		penPoints.clear();

		EuclidianView ev = app.getEuclidianView();

		app.refreshViews(); // clear trace
		ev.getGraphics().drawImage(penImage, penOffsetX, penOffsetY, null);


		if (lastPenImage == null && !penWritingToExistingImage) {
			String fileName = app.createImage(penImage, "penimage.png");
			//Application.debug(fileName);

			GeoImage geoImage = new GeoImage(app.getKernel().getConstruction());
			geoImage.setImageFileName(fileName);
			geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);
			GeoPoint corner = (new GeoPoint(app.getKernel().getConstruction(), null, ev.toRealWorldCoordX(penOffsetX),ev.toRealWorldCoordY( penOffsetY + penImage.getHeight()),1.0));
			corner.update();
			geoImage.setCorner(corner, 0);
			geoImage.setLabel(null);
			if (penUsingOffsets) { // want to allow easy resizing
				GeoPoint corner2 = (new GeoPoint(app.getKernel().getConstruction(), null, ev.toRealWorldCoordX(penOffsetX + penImage.getWidth()),ev.toRealWorldCoordY( penOffsetY + penImage.getHeight()),1.0));
				corner.setLabelVisible(false);
				corner2.setLabelVisible(false);
				corner2.update();
				geoImage.setCorner(corner2, 1);
			}
			corner.setEuclidianVisible(penUsingOffsets);
			corner.setAuxiliaryObject(!penUsingOffsets);
			corner.update();
			geoImage.setFixed(!penUsingOffsets);

			GeoImage.updateInstances();


			lastPenImage = geoImage;
		}

		// doesn't work as all changes are in the image not the XML
		//app.storeUndoInfo();
		app.setUnsaved();

		if (!penWritingToExistingImage) penImage = null;
		//penWritingToExistingImage = false;

	}
	
	
	
	
	
	
	
	
	
}
