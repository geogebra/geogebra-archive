package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.arithmetic.Inequality;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * @author kondr
 *
 */
public class DrawInequality1Var extends Drawable {

	private Inequality ineq;
	private GeneralPathClipped[] gp;
	
	
	
	/**
	 * Creates new drawable inequality
	 * @param view
	 * @param geo
	 * @param ineq
	 */
	public DrawInequality1Var(Inequality ineq,EuclidianView view,GeoElement geo) {
		super();
		this.ineq = ineq;
		this.geo = geo;
		this.view = view;
		
		
	}

	@Override
	public void draw(Graphics2D g2) {
		if(gp == null)
			return;
		int i= 0;
		while(i< gp.length && gp[i] != null){
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				Drawable.drawWithValueStrokePure(gp[i], g2);
			}
			fill(g2, gp[i], true); // fill using default/hatching/image as
			// appropriate

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				Drawable.drawWithValueStrokePure(gp[i], g2);
			}

			//TODO: draw label
			i++;
		}

	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		for(int i=0;i<gp.length;i++)
			if(gp[i]!= null && gp[i].contains(x,y))return true;
		return false;
	}

	@Override
	public boolean isInside(Rectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// get x-coords of the lines
		GeoPoint[] roots = ineq.getZeros();
		double[] x = new double[roots.length+2];
		x[0] = -10;
		int numOfX = 1;
		for(int i=0;i<roots.length;i++)
			x[numOfX++] = view.toScreenCoordX(roots[i].x);
		x[numOfX++] = view.width+10;
		
		if(gp == null)
			gp = new GeneralPathClipped[numOfX/2];
		int j = ineq.getFunBorder().evaluate(view.xmin)<0 ^ geo.isInverseFill() ? 1:0;
		for(int i=0;2*i+j+1<numOfX;i++){
			gp[i] = new GeneralPathClipped(view);
			gp[i].moveTo(x[2*i+j], -10);
			gp[i].lineTo(x[2*i+j], view.height+10);
			gp[i].lineTo(x[2*i+j+1], view.height+10);
			gp[i].lineTo(x[2*i+j+1], -10);
			gp[i].lineTo(x[2*i+j], -10);
			gp[i].closePath();
		}
	}

}
