package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.arithmetic.Inequality;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Graphical representation of linear inequality
 * 
 * @author Michael Borcherds
 * 
 */
public class DrawInequality extends Drawable {

	private GeneralPathClipped[] gp;
	private boolean offScreen; // to avoid OpenJDK glitch we don't draw
	// offscreen line

	private boolean isVisible;
	private boolean labelVisible;
	private Drawable rd;
	private GeoFunctionNVar function;
	private int ineqCount;

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
		if (gp == null) {
			gp = new GeneralPathClipped[ineqCount];// (view);
			for (int i = 0; i < ineqCount; i++)
				gp[i] = new GeneralPathClipped(view);
		} else
			for (int i = 0; i < ineqCount; i++) {
				gp[i].reset();
			}
		for (int i = 0; i < ineqCount; i++) {
			Inequality ineq = function.getIneqs().get(i);
			
			
			switch (ineq.getType()){
				case Inequality.INEQUALITY_PARAMETRIC_Y: 
					updateParametricY(ineq,gp[i]);
					break;
				case Inequality.INEQUALITY_PARAMETRIC_X: 
					updateParametricX(ineq,gp[i]);
					break;
				case Inequality.INEQUALITY_IMPLICIT: 
					DrawImplicitPoly dip = new DrawImplicitPoly(view, ineq.getImpBorder());
					dip.update();
					dip.draw(view.getBackgroundGraphics());
					break;
			}
				
			// gp on screen?
			if (!gp[i].intersects(0, 0, view.width, view.height)) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}

			if (labelVisible) {
				xLabel = (int) Math.round((ax + bx) / 2) - 6;
				yLabel = (int) view.yZero - view.fontSize;
				labelDesc = geo.getLabelDescription();
				addLabelOffset();
			}
		}
	}

	private void updateParametricY(Inequality ineq,GeneralPathClipped gp) {
		GeoFunction border = ineq.getBorder();
		updateStrokes(border);
		double ax = view.toRealWorldCoordX(0);
		double bx = view.toRealWorldCoordX(view.width);
		if (ineq.isAboveBorder()) {
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
	
	private void updateParametricX(Inequality ineq,GeneralPathClipped gp) {
		GeoFunction border = ineq.getBorder();
		updateStrokes(border);
		double ax = view.toRealWorldCoordY(0);
		double bx = view.toRealWorldCoordY(view.height);
		if (ineq.isAboveBorder()) {
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

	public void draw(Graphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				for (int i = 0; i < ineqCount; i++)
					Drawable.drawWithValueStrokePure(gp[i], g2);
			}
			for (int i = 0; i < ineqCount; i++)
				fill(g2, gp[i], true); // fill using default/hatching/image as
			// appropriate

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				for (int i = 0; i < ineqCount; i++)
					Drawable.drawWithValueStrokePure(gp[i], g2);
			}

			if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		 boolean ret =gp != null;
		 for (int i = 0; i < ineqCount; i++)
				ret &= (gp[i].contains(x, y) || gp[i].intersects(x - 3, y - 3, 6, 6));
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

}
