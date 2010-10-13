package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.arithmetic.Inequality;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Graphical representation of linear inequality
 * 
 * @author Michael Borcherds
 * 
 */
public class DrawInequality extends Drawable {

	private boolean offScreen; // to avoid OpenJDK glitch we don't draw
	// offscreen line

	private boolean isVisible;
	private boolean labelVisible;
	private Drawable rd;
	private GeoFunctionNVar function;
	private int ineqCount;
	private ArrayList<Drawable> drawables;

	/**
	 * Creates new drawable linear inequality
	 * 
	 * @param euclidianView
	 * @param geo
	 */
	public DrawInequality(EuclidianView view, GeoFunctionNVar function) {
		this.view = view;
		this.function = function;
		geo = function;
		update();

	}

	final public void update() {
		// take line g here, not geo this object may be used for conics too
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		// updateStrokes(n);

		// init gp

		double ax = view.toRealWorldCoordX(0);
		double bx = view.toRealWorldCoordX(view.width);

		ineqCount = function.getIneqs().size();
		// plot like definite integral
		if (drawables == null) 
			drawables = new ArrayList<Drawable>(ineqCount);
				
		for (int i = 0; i < ineqCount; i++) {
			Inequality ineq = function.getIneqs().get(i);
			if(drawables.size() <= i){
			Drawable draw;
			switch (ineq.getType()){
				case Inequality.INEQUALITY_PARAMETRIC_Y: 
					draw = new DrawParametricInequality(ineq,view);
					break;
				case Inequality.INEQUALITY_PARAMETRIC_X: 
					draw = new DrawParametricInequality(ineq,view);
					break;
				case Inequality.INEQUALITY_CONIC: 
					draw = new DrawConic(view, ineq.getConicBorder());
										
					break;	
				case Inequality.INEQUALITY_IMPLICIT: 
					draw = new DrawImplicitPoly(view, ineq.getImpBorder());
					
					break;
				default: draw = null;
					
			}
			if(ineq.isAboveBorder())
				draw.setFillInverted(true);
			draw.update();
			draw.setGeoElement(function);
			drawables.add(draw);
			}
			else drawables.get(i).update();
			// gp on screen?
			/*if (!gp[i].intersects(0, 0, view.width, view.height)) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}*/

			if (labelVisible) {
				xLabel = (int) Math.round((ax + bx) / 2) - 6;
				yLabel = (int) view.yZero - view.fontSize;
				labelDesc = geo.getLabelDescription();
				addLabelOffset();
			}
		}
	}

	

	public void draw(Graphics2D g2) {
		if (isVisible) {
			for(int i=0;i<ineqCount;i++)
				drawables.get(i).draw(g2);
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		 if(drawables==null)
			 return false;
		 boolean ret = false; 
		 for (int i = 0; i < ineqCount; i++)
				ret |= drawables.get(i).hit(x,y);
		 return ret;
	}

	@Override
	public boolean isInside(Rectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	private class DrawParametricInequality extends Drawable{

		private Inequality ineq;
		private GeneralPathClipped gp;
		
		protected DrawParametricInequality(Inequality ineq,EuclidianView view){
			this.view = view;
			this.ineq = ineq;
		}
		@Override
		public void draw(Graphics2D g2) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				for (int i = 0; i < ineqCount; i++)
					Drawable.drawWithValueStrokePure(gp, g2);
			}
			fill(g2, gp, true); // fill using default/hatching/image as
			// appropriate

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				for (int i = 0; i < ineqCount; i++)
					Drawable.drawWithValueStrokePure(gp, g2);
			}

			if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
			
		}

		@Override
		public GeoElement getGeoElement() {
			return geo;
		}

		@Override
		public boolean hit(int x, int y) {
			return gp.contains(x, y) || gp.intersects(x - 3, y - 3, 6, 6);			
		}

		@Override
		public boolean isInside(Rectangle rect) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setGeoElement(GeoElement geo) {
			this.geo = geo;
			
		}

		@Override
		public void update() {
			if(gp==null)
				gp=new GeneralPathClipped(view);
			else
				gp.reset();
			GeoFunction border = ineq.getFunBorder();
			updateStrokes(border);
			if(ineq.getType() == Inequality.INEQUALITY_PARAMETRIC_X){
				double ax = view.toRealWorldCoordY(0);
				double bx = view.toRealWorldCoordY(view.height);
				if (isFillInverted()) {
					gp.moveTo(view.width+10, -10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(view.width+10, view.height+10);
					gp.lineTo(view.width+10, -10);
					gp.closePath();
				} else {
					gp.moveTo(-10, -10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(-10, view.height+10);
					gp.lineTo(-10, -10);
					gp.closePath();
				}
			}
			else{
				double ax = view.toRealWorldCoordX(0);
				double bx = view.toRealWorldCoordX(view.width);
				if (isFillInverted()) {
					gp.moveTo(-10, -10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(view.width+10, -10);
					gp.lineTo(-10, -10);
					gp.closePath();
				} else {
					gp.moveTo(-10, view.height+10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(view.width+10, view.height+10);
					gp.lineTo(-10, view.height+10);
					gp.closePath();
				}
			}
		}
		
	}
}


