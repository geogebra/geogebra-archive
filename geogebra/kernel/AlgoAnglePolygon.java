/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Creates all angles of a polygon.
 */

public class AlgoAnglePolygon extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPolygon poly; // input
    private GeoAngle[] angles; // output      

    private AlgoAnglePoints[] algos;

    AlgoAnglePolygon(Construction cons, String[] labels, GeoPolygon poly) {
        super(cons);
        this.poly = poly;

        createAngles();
        setInputOutput(); // for AlgoElement

        GeoElement.setLabels(labels, angles);
    }

    public String getClassName() {
        return "AlgoAnglePolygon";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = poly;

        output = angles;
        //setDependencies();
        poly.addAlgorithm(this);
    }

    GeoAngle[] getAngles() {
        return angles;
    }
    GeoPolygon getPolygon() {
        return poly;
    }

    private void createAngles() {
        GeoPoint[] points = poly.getPoints();
        angles = new GeoAngle[points.length];
        algos = new AlgoAnglePoints[points.length];

        for (int i = 0; i < points.length; i++) {
            algos[i] =
                new AlgoAnglePoints(
                    cons,
                    this,
                    points[(i + 1) % points.length],
                    points[i % points.length],
                    points[(i + points.length - 1) % points.length]);
            //  this is only an internal Algorithm that shouldn't be in the construction list
            cons.removeFromConstructionList(algos[i]);
            angles[i] = algos[i].getAngle();
        }
        
        // the angles of a triangle should not become reflex
        if (angles.length == 3) {
        	for (int i=0; i < 3; i++) {
        		angles[i].setAllowReflexAngle(false);
        	}
        }
    }

    // calc all angles of the polygon
    // this is done by the algorithms created in createAngles()
    // so nothing has to be done here
    public void update() {}

    protected final void compute() {}

    public void remove() {
        // clear algoAnglePoly in all algos to avoid null pointer exception
        for (int i = 0; i < algos.length; i++) {
            algos[i].setAlgoAnglePolygon(null);
        }
        super.remove();
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("AngleOfA",poly.getLabel());
    }
}
