package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.Inequality;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;


/**
 * Graphical representation of linear inequality
 * 
 * @author Michael Borcherds
 * 
 */
public class DrawInequality extends Drawable {

	

	private boolean isVisible;
	private boolean labelVisible;
			
	private Drawable drawable;
	private int operation = 0;
	private DrawInequality left,right;
	private Inequality ineq;
	/**
	 * Creates new drawable linear inequality
	 * 
	 * @param view
	 * @param function boolean 2-var function
	 */
	public DrawInequality(EuclidianView view, FunctionalNVar function) {
		this.view = view;
    	hitThreshold = view.getCapturingThreshold();		
		geo = (GeoElement)function;		
		operation = function.getIneqs().getOperation(); 
		if(function.getIneqs().getLeft()!=null)
			left=new DrawInequality(function.getIneqs().getLeft(),view,geo);
		if(function.getIneqs().getRight()!=null)
			right=new DrawInequality(function.getIneqs().getRight(),view,geo);
		if(function.getIneqs().getIneq()!=null)
			ineq=function.getIneqs().getIneq();			
		update();

	}
	private DrawInequality(FunctionNVar.IneqTree tree,EuclidianView view,GeoElement geo){
		this.view = view;
		this.geo = geo;		
		setForceNoFill(true);
		operation = tree.getOperation();
		if(tree.getLeft()!=null)
			left=new DrawInequality(tree.getLeft(),view,geo);
		if(tree.getRight()!=null)
			right=new DrawInequality(tree.getRight(),view,geo);
		if(tree.getIneq()!=null)
			ineq=tree.getIneq();
	}

	final public void update() {
		// take line g here, not geo this object may be used for conics too
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		// updateStrokes(n);

		// init gp
		updateRecursive();
		
		
		// plot like definite integral
		
			
		
					
			// gp on screen?
			/*if (!gp[i].intersects(0, 0, view.width, view.height)) {
				isVisible = false;
				// don't return here to make sure that getBounds() works for
				// offscreen points too
			}*/

			
		
	}
	

	private void updateRecursive() {
		if(left!=null)
			left.updateRecursive();
		if(right!=null)
			right.updateRecursive();
		if(operation==ExpressionNode.AND){
			setShape(left.getShape());
			getShape().intersect(right.getShape());
		}else if(operation==ExpressionNode.OR){
			setShape(left.getShape());
			getShape().add(right.getShape());
		}
		if(ineq==null)
			return;		
		if(drawable == null || !matchBorder(ineq.getBorder(),drawable)){
			switch (ineq.getType()){
				case Inequality.INEQUALITY_PARAMETRIC_Y: 
					drawable = new DrawParametricInequality(ineq, view, geo);
					break;
				case Inequality.INEQUALITY_PARAMETRIC_X: 
					drawable = new DrawParametricInequality(ineq, view, geo);
					break;
				case Inequality.INEQUALITY_1VAR_X: 
					drawable = new DrawInequality1Var(ineq, view, geo, false);
					break;
				case Inequality.INEQUALITY_1VAR_Y: 
					drawable = new DrawInequality1Var(ineq, view, geo, true);
					break;	
				case Inequality.INEQUALITY_CONIC: 
					drawable = new DrawConic(view, ineq.getConicBorder());					
					ineq.getConicBorder().setInverseFill(geo.isInverseFill() ^ ineq.isAboveBorder());	
					break;	
				case Inequality.INEQUALITY_IMPLICIT: 
					drawable = new DrawImplicitPoly(view, ineq.getImpBorder());
					break;
				default: drawable = null;
			}
		
			drawable.setGeoElement(geo);
			drawable.setForceNoFill(true);
			drawable.update();
			
		}
		else {
			if(ineq.getType() == Inequality.INEQUALITY_CONIC) {					
				ineq.getConicBorder().setInverseFill(geo.isInverseFill() ^ ineq.isAboveBorder());
			}
			drawable.update();
		}
		setShape(drawable.getShape());
		
		
	}
	private boolean matchBorder(GeoElement border, Drawable d) {
		if(d instanceof DrawConic && ((DrawConic)d).getConic().equals(border))
			return true;
		if(d instanceof DrawImplicitPoly && ((DrawImplicitPoly)d).getPoly().equals(border))
			return true;
		if(d instanceof DrawParametricInequality && ((DrawParametricInequality)d).getBorder().equals(border))
			return ((DrawParametricInequality)d).isXparametric();
		
		return false;
	}
	
	public void draw(Graphics2D g2) {
		if(operation == ExpressionNode.NO_OPERATION){						
			if ( drawable!=null) {
				drawable.draw(g2);			
		}
		}else{
		if(left!=null)
			left.draw(g2);
		if(right!=null)
			right.draw(g2);				
		}
		if(!isForceNoFill()){
			g2.setPaint(geo.getObjectColor());
			fill(g2, getShape(), true);
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		if(left!= null && operation==ExpressionNode.AND)
			 return left.hit(x, y) && right.hit(x, y);
		 if(left!= null && operation==ExpressionNode.OR)
			 return left.hit(x, y) || right.hit(x, y);
		 if(left!= null && operation==ExpressionNode.NOT)
			 return !left.hit(x, y);
		 if(drawable==null)
			 return false;
		 return drawable.hit(x, y);
		 
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
		
		protected DrawParametricInequality(Inequality ineq,EuclidianView view,GeoElement geo){
			this.view = view;
			this.ineq = ineq;
			this.geo = geo;								
		}
		
		public Area getShape(){
			return new Area(gp);
		}
		
		private Object getBorder() {
			return ineq.getBorder();			
		}
		@Override
		public void draw(Graphics2D g2) {			
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				Drawable.drawWithValueStrokePure(gp, g2);
			}
			
			if(!isForceNoFill())
				fill(g2, gp, true); // fill using default/hatching/image as
			// appropriate

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
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
			return gp.contains(x, y) || gp.intersects(x - hitThreshold, y - hitThreshold, 2*hitThreshold, 2*hitThreshold);			
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
				if (geo.isInverseFill() ^ ineq.isAboveBorder()) {
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
				if (labelVisible) {
					xLabel = (int) Math.round((ax + bx) / 2) - 6;
					yLabel = (int) view.yZero - view.fontSize;
					labelDesc = geo.getLabelDescription();
					addLabelOffset();
				}
			}
			else{
				double ax = view.toRealWorldCoordX(0);
				double bx = view.toRealWorldCoordX(view.width);
				if (geo.isInverseFill() ^ ineq.isAboveBorder()) {
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
				border.evaluateCurve(ax);
				if (labelVisible) {
					yLabel = (int) Math.round((ax + bx) / 2) - 6;
					xLabel = (int) view.xZero;
					labelDesc = geo.getLabelDescription();
					addLabelOffset();
				}
			}
			
		}
		
		private boolean  isXparametric(){
			return ineq.getType() == Inequality.INEQUALITY_PARAMETRIC_X;
		}
		
	}
}


