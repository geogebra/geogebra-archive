/* 
 GeoGebra - Dynamic Geometry and Algebra
 Copyright Markus Hohenwarter, http://www.geogebra.at

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation; either version 2 of the License, or 
 (at your option) any later version.
 */

package geogebra.euclidian;

import geogebra.kernel.AlgoAngleLines;
import geogebra.kernel.AlgoAnglePoints;
import geogebra.kernel.AlgoAngleVector;
import geogebra.kernel.AlgoAngleVectors;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
/**
 * 
 * @author Markus Hohenwarter
 * @version
 */
public class DrawAngle extends Drawable {

	private GeoAngle angle;

	private GeoPoint vertex, point;

	private GeoLine line, line2;

	private GeoVector vector;

	boolean isVisible, labelVisible, is90degrees;

	final private static int DRAW_MODE_POINTS = 0;

	final private static int DRAW_MODE_VECTORS = 1;

	final private static int DRAW_MODE_LINES = 2;

	final private static int DRAW_MODE_SINGLE_VECTOR = 3;

	final private static int DRAW_MODE_SINGLE_POINT = 4;

	private int angleDrawMode;

	//private Arc2D.Double fillArc = new Arc2D.Double();
	private Arc2D.Double drawArc = new Arc2D.Double();

	private Ellipse2D.Double dot90degree = new Ellipse2D.Double();

	private Shape shape;

	private double m[] = new double[2];

	private double coords[] = new double[2];

	private double[] firstVec = new double[2];

	private GeoPoint tempPoint;

	private Kernel kernel;

	public DrawAngle(EuclidianView view, GeoAngle angle) {
		this.view = view;
		kernel = view.getKernel();
		this.angle = angle;
		geo = angle;

		angleDrawMode = -1;

		AlgoElement algo = geo.getParentAlgorithm();
		Construction cons = geo.getConstruction();
		tempPoint = new GeoPoint(cons);
		tempPoint.setCoords(0.0, 0.0, 1.0);

		// angle defined by three points
		if (algo instanceof AlgoAnglePoints) {
			angleDrawMode = DRAW_MODE_POINTS;
			AlgoAnglePoints pa = (AlgoAnglePoints) algo;
			vertex = pa.getB();
			point = pa.getA();
		}
		// angle between two vectors
		else if (algo instanceof AlgoAngleVectors) {
			angleDrawMode = DRAW_MODE_VECTORS;
			AlgoAngleVectors va = (AlgoAngleVectors) algo;
			GeoVector v = va.getv();
			vector = v;
		}
		// angle between two lines
		else if (algo instanceof AlgoAngleLines) {
			angleDrawMode = DRAW_MODE_LINES;
			AlgoAngleLines la = (AlgoAngleLines) algo;
			line = la.getg();
			line2 = la.geth();
			vertex = tempPoint;
		}
		// angle of a single vector or a single point
		else if (algo instanceof AlgoAngleVector) {
			AlgoAngleVector va = (AlgoAngleVector) algo;
			GeoVec3D vec = va.getVec3D();
			if (vec instanceof GeoVector) {
				angleDrawMode = DRAW_MODE_SINGLE_VECTOR;
				vector = (GeoVector) vec;
			} else if (vec instanceof GeoPoint) {
				angleDrawMode = DRAW_MODE_SINGLE_POINT;
				point = (GeoPoint) vec;
				vertex = tempPoint;
			}
			firstVec[0] = 1;
			firstVec[1] = 0;
		}

		if (angleDrawMode > -1) {
			angle.setDrawable(true);
			update();
		}
	}

	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		updateStrokes(angle);

		// set vertex and first vector to determine start angle
		switch (angleDrawMode) {
		case DRAW_MODE_POINTS: // three points
			// vertex
			vertex.getInhomCoords(m);

			// first vec
			firstVec[0] = point.inhomX - m[0];
			firstVec[1] = point.inhomY - m[1];
			break;

		case DRAW_MODE_VECTORS: // two vectors
			// vertex
			vertex = vector.getStartPoint();
			if (vertex == null)
				vertex = tempPoint;
			vertex.getInhomCoords(m);

			// first vec
			vector.getInhomCoords(firstVec);
			break;

		case DRAW_MODE_LINES: // two lines
			// intersect lines to get vertex
			GeoVec3D.cross(line, line2, vertex);
			vertex.getInhomCoords(m);

			// first vec
			line.getDirection(firstVec);
			break;

		case DRAW_MODE_SINGLE_VECTOR: // single GeoVector
			// vertex
			vertex = vector.getStartPoint();
			if (vertex == null)
				vertex = tempPoint;
			vertex.getInhomCoords(m);

			// first vec is constant (1,0)
			break;

		case DRAW_MODE_SINGLE_POINT: // single GeoPoint
			// vertex
			vertex.getInhomCoords(m);

			// first vec is constant (1,0)
			break;

		default:
			/*
			 * if (vertex == null) {
			 * System.err.println(Util.toHTMLString("vertex null for: " + geo + ",
			 * parent: " + geo.getParentAlgorithm().getCommandDescription())); }
			 */
			return;
		}

		// check vertex
		if (!vertex.isDefined() || vertex.isInfinite()) {
			isVisible = false;
			return;
		}

		// calc start angle
		double angSt = Math.atan2(firstVec[1], firstVec[0]);
		if (Double.isNaN(angSt) || Double.isInfinite(angSt)) {
			isVisible = false;
			return;
		}
		double angExt = angle.getValue();


		// if this angle was not allowed to become a reflex angle
		// (i.e. greater than pi) we got (2pi - angleValue) for angExt
		if (angle.changedReflexAngle()) {
			angSt = angSt - angExt;
		}

		//  check for 90 degrees
		is90degrees = kernel.isEqual(angExt, Kernel.PI_HALF);
		// set arc in real world coords
		double as = Math.toDegrees(angSt);
		double ae = Math.toDegrees(angExt);
		double r = angle.arcSize * view.invXscale;
		drawArc.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.PIE);

		// transform arc to screen coords
		shape = view.coordTransform.createTransformedShape(drawArc);

		// shape on screen?
		if (!shape.intersects(0, 0, view.width, view.height)) {
			isVisible = false;
			return;
		}

		if (labelVisible || is90degrees) {
			// calculate label and 90° dot position
			double radius = r / 1.7;
			double labelAngle = angSt + angExt / 2.0;
			coords[0] = m[0] + radius * Math.cos(labelAngle);
			coords[1] = m[1] + radius * Math.sin(labelAngle);
			view.toScreenCoords(coords);
		}

		if (is90degrees) {
			// set 90 degrees dot
			int diameter = 2 * geo.lineThickness;
			dot90degree.setFrame(coords[0] - geo.lineThickness, coords[1]
					- geo.lineThickness, diameter, diameter);
		}

		if (labelVisible) {
			labelDesc = angle.getLabelDescription();
			xLabel = (int) (coords[0] - 3);
			yLabel = (int) (coords[1] + 5);
			if (!addLabelOffset() && is90degrees) {
				xLabel = (int) (coords[0] + 2 * geo.lineThickness);
			}
		}
	}

	final public void draw(Graphics2D g2) {
		if (isVisible) {

			if (angle.alphaValue > 0.0f) {
				g2.setPaint(angle.fillColor);
				g2.fill(shape);
			}

			if (geo.doHighlighting()) {
				g2.setPaint(angle.selColor);
				g2.setStroke(selStroke);
				g2.draw(shape);
			}

			g2.setPaint(angle.objColor);
			g2.setStroke(objStroke);
			g2.draw(shape);
			mark(g2);

			if (is90degrees) {
				g2.fill(dot90degree);
			}

			if (labelVisible) {
				g2.setPaint(angle.labelColor);
				g2.setFont(view.fontAngle);
				drawLabel(g2);
			}
		}
	}

	//added by loîc for decoration on GeoAngle Objects
private void mark(Graphics2D g2){
		int deco=angle.getDecorationType();
		int radius=angle.getArcSize();
		// calc start angle
		double angSt = Math.atan2(firstVec[1], firstVec[0]);
		if (Double.isNaN(angSt) || Double.isInfinite(angSt)) {
			isVisible = false;
			return;
		}		
        double angExt = angle.getValue();
         // if this angle was not allowed to become a reflex angle
        // (i.e. greater than pi) we got (2pi - angleValue) for angExt
        if (angle.changedReflexAngle()) {        	
        	angSt = angSt - angExt;
        }
    	switch(deco){
			case GeoElement.DECORATION_ANGLE_TWO_ARCS:
				Shape shape2;
				Arc2D.Double drawArc2 = new Arc2D.Double();
				// set arc in real world coords
				double as = Math.toDegrees(angSt);
				double ae = Math.toDegrees(angExt);
				double r = (angle.arcSize+5) * view.invXscale;
				drawArc2.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.PIE);
				// transform arc to screen coords
				shape2 = view.coordTransform.createTransformedShape(drawArc2);
				g2.draw(shape2);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_ARCS:
				drawArc2 = new Arc2D.Double();
				// set arc in real world coords
				as = Math.toDegrees(angSt);
				ae = Math.toDegrees(angExt);
				r = (angle.arcSize+5) * view.invXscale;
				drawArc2.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.PIE);
				// transform arc to screen coords
				shape2 = view.coordTransform.createTransformedShape(drawArc2);
				g2.draw(shape2);
				if (angle.arcSize>5)r = (angle.arcSize-5) * view.invXscale;
				else r = (angle.arcSize+10) * view.invXscale;
				drawArc2.setArcByCenter(m[0], m[1], r, -as, -ae, Arc2D.PIE);
				// transform arc to screen coords
				shape2 = view.coordTransform.createTransformedShape(drawArc2);
				g2.draw(shape2);
			break;
			case GeoElement.DECORATION_ANGLE_ONE_TICK:
				double mid=-angSt-angExt/2;
				drawTick(g2,mid,radius);
			break;
			case GeoElement.DECORATION_ANGLE_TWO_TICKS:
				mid=-angSt-2*angExt/5;
				drawTick(g2,mid,radius);
				mid=-angSt-3*angExt/5;
				drawTick(g2,mid,radius);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_TICKS:
				mid=(-angSt-3*angExt/8);
				drawTick(g2,mid,radius);
				mid=-angSt-angExt/2;
				drawTick(g2,mid,radius);
				mid=(-angSt-5*angExt/8);
				drawTick(g2,mid,radius);
			break;
		}
	}
	// draw a tick for the angle at a relative angle position 
	private void drawTick(Graphics2D g2,double angle,int radius){
		Line2D.Double tick=new Line2D.Double();
		double[] A=new double[2];
		A[0]=m[0];
		A[1]=m[1];
		view.toScreenCoords(A);
		double x1=A[0]+(radius-3)*Math.cos(angle);
		double y1=A[1]+(radius-3)*Math.sin(angle)*view.getScaleRatio();
		double x2=A[0]+(radius+3)*Math.cos(angle);
		double y2=A[1]+(radius+3)*Math.sin(angle)*view.getScaleRatio();
		tick.setLine(x1,y1,x2,y2);
		g2.draw(tick);
	}

	final public boolean hit(int x, int y) {
		return shape != null && shape.contains(x, y);
	}

	public GeoElement getGeoElement() {
		return geo;
	}

	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}