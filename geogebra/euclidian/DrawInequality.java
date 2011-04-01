package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.Inequality;
import geogebra.kernel.arithmetic.FunctionNVar.IneqTree;
import geogebra.kernel.roots.RealRootUtil;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;

/**
 * Graphical representation of inequality
 * 
 * @author Zbynek Konecny
 * 
 */
public class DrawInequality extends Drawable {

	private boolean isVisible;
	private boolean labelVisible;

	private Drawable drawable;
	private int operation = 0;
	private DrawInequality left, right;
	private Inequality ineq;
	private FunctionalNVar function;

	/**
	 * Creates new drawable linear inequality
	 * 
	 * @param view
	 * @param function
	 *            boolean 2-var function
	 */
	public DrawInequality(EuclidianView view, FunctionalNVar function) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
		geo = (GeoElement) function;
		this.function = function;
		operation = function.getIneqs().getOperation();
		if (function.getIneqs().getLeft() != null)
			left = new DrawInequality(function.getIneqs().getLeft(), view, geo);
		if (function.getIneqs().getRight() != null)
			right = new DrawInequality(function.getIneqs().getRight(), view,
					geo);
		if (function.getIneqs().getIneq() != null)
			ineq = function.getIneqs().getIneq();
		update();

	}

	private DrawInequality(FunctionNVar.IneqTree tree, EuclidianView view,
			GeoElement geo) {
		this.view = view;
		this.geo = geo;
		setForceNoFill(true);
		updateRecursive(tree);
	}

	final public void update() {
		// take line g here, not geo this object may be used for conics too
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		// updateStrokes(n);

		// init gp
		updateRecursive(function.getIneqs());

	}

	private void updateRecursive(IneqTree it) {
		updateTrees(it);
		operation = it.getOperation();
		updateShape();

		if (ineq != it.getIneq())
			ineq = it.getIneq();
		if (ineq == null) {
			if (geo.isInverseFill() && !isForceNoFill()) {
				Area b = new Area(view.getBoundingPath());
				b.subtract(getShape());
				setShape(b);
			}
			return;
		}

		if (drawable == null || !matchBorder(ineq.getBorder(), drawable)) {
			createDrawable();
		} 
		else if (ineq.getType() == Inequality.INEQUALITY_CONIC) {
				ineq.getConicBorder().setInverseFill(ineq.isAboveBorder());
		}
		drawable.update();
		setShape(drawable.getShape());

	}

	private void createDrawable() {
		switch (ineq.getType()) {
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
			ineq.getConicBorder().setInverseFill(ineq.isAboveBorder());
			break;
		case Inequality.INEQUALITY_IMPLICIT:
			drawable = new DrawImplicitPoly(view, ineq.getImpBorder());
			break;
		default:
			drawable = null;
		}
		drawable.setGeoElement(geo);
		drawable.setForceNoFill(true);
	}

	private void updateShape() {
		if (operation == ExpressionNode.AND) {
			setShape(left.getShape());
			getShape().intersect(right.getShape());
		} else if (operation == ExpressionNode.OR) {
			setShape(left.getShape());
			getShape().add(right.getShape());
		} else if (operation == ExpressionNode.EQUAL_BOOLEAN) {
			setShape(new Area(view.getBoundingPath()));
			left.getShape().exclusiveOr(right.getShape());
			getShape().subtract(left.getShape());
		} else if (operation == ExpressionNode.NOT_EQUAL) {
			setShape(left.getShape());
			getShape().exclusiveOr(right.getShape());
		} else if (operation == ExpressionNode.NOT) {
			setShape(new Area(view.getBoundingPath()));
			getShape().subtract(left.getShape());
		}

	}

	private void updateTrees(IneqTree it) {
		if (it.getLeft() != null && left == null) {
			left = new DrawInequality(it.getLeft(), view, geo);
		}
		if (it.getLeft() != null) {
			left.updateRecursive(it.getLeft());
		} else
			left = null;
		if (it.getRight() != null && right == null) {
			right = new DrawInequality(it.getLeft(), view, geo);
		}
		if (it.getRight() != null)
			right.updateRecursive(it.getRight());
		else
			right = null;

	}

	private boolean matchBorder(GeoElement border, Drawable d) {
		if (d instanceof DrawConic && ((DrawConic) d).getConic().equals(border))
			return true;
		if (d instanceof DrawImplicitPoly
				&& ((DrawImplicitPoly) d).getPoly().equals(border))
			return true;
		if (d instanceof DrawParametricInequality
				&& ((DrawParametricInequality) d).getBorder().equals(border))
			return ((DrawParametricInequality) d).isXparametric();

		return false;
	}

	public void draw(Graphics2D g2) {
		if (!isForceNoFill() && !isVisible)
			return;
		if (operation == ExpressionNode.NO_OPERATION) {
			if (drawable != null) {
				drawable.draw(g2);
			}
		} else {
			if (left != null)
				left.draw(g2);
			if (right != null)
				right.draw(g2);
		}
		if (!isForceNoFill()) {
			g2.setPaint(geo.getObjectColor());
			fill(g2, getShape(), true);
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	private boolean hit2(int x, int y) {
		double[] coords = new double[] { view.toRealWorldCoordX(x),
				view.toRealWorldCoordY(y) };
		if (geo instanceof GeoFunction
				&& ((GeoFunction) geo).getVarString().equals("y"))
			return ((GeoFunction) geo).getFunction().evaluateBoolean(coords[1]);
		return ((FunctionalNVar) geo).getFunction().evaluateBoolean(coords);

	}

	@Override
	public boolean hit(int x, int y) {
		return hit2(x, y) || hit2(x - 4, y) || hit2(x + 4, y) || hit2(x, y - 4)
				|| hit2(x, y + 4);

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

	private class DrawParametricInequality extends Drawable {

		private Inequality ineq;
		private GeneralPathClipped gp;

		protected DrawParametricInequality(Inequality ineq, EuclidianView view,
				GeoElement geo) {
			this.view = view;
			this.ineq = ineq;
			this.geo = geo;
		}

		public Area getShape() {
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

			if (!isForceNoFill())
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
			return gp.contains(x, y)
					|| gp.intersects(x - hitThreshold, y - hitThreshold,
							2 * hitThreshold, 2 * hitThreshold);
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
			if (gp == null)
				gp = new GeneralPathClipped(view);
			else
				gp.reset();
			GeoFunction border = ineq.getFunBorder();
			updateStrokes(border);
			if (ineq.getType() == Inequality.INEQUALITY_PARAMETRIC_X) {
				double ax = view.toRealWorldCoordY(-10);
				double bx = view.toRealWorldCoordY(view.height + 10);
				double[] intervalX = RealRootUtil.getDefinedInterval(border
						.getFunction(), ax, bx);
				ax = intervalX[0];
				bx = intervalX[1];
				double axEv = view.toScreenCoordYd(ax);
				double bxEv = view.toScreenCoordYd(bx);
				if (ineq.isAboveBorder()) {
					gp.moveTo(view.width + 10, axEv);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(view.width + 10, bxEv);
					gp.lineTo(view.width + 10, axEv);
					gp.closePath();
				} else {
					gp.moveTo(-10, axEv);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(-10, bxEv);
					gp.lineTo(-10, axEv);
					gp.closePath();
				}
				if (labelVisible) {
					xLabel = (int) Math.round((ax + bx) / 2) - 6;
					yLabel = (int) view.yZero - view.fontSize;
					labelDesc = geo.getLabelDescription();
					addLabelOffset();
				}
			} else {
				double ax = view.toRealWorldCoordX(-10);
				double bx = view.toRealWorldCoordX(view.width + 10);
				double[] intervalX = RealRootUtil.getDefinedInterval(border
						.getFunction(), ax, bx);
				ax = intervalX[0];
				bx = intervalX[1];
				double axEv = view.toScreenCoordXd(ax);
				double bxEv = view.toScreenCoordXd(bx);
				if (ineq.isAboveBorder()) {
					gp.moveTo(axEv, -10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(bxEv, -10);
					gp.lineTo(axEv, -10);
					gp.closePath();
				} else {
					gp.moveTo(axEv, view.height + 10);
					DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							false, false);
					gp.lineTo(bxEv, view.height + 10);
					gp.lineTo(axEv, view.height + 10);
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

		private boolean isXparametric() {
			return ineq.getType() == Inequality.INEQUALITY_PARAMETRIC_X;
		}

	}
}
