/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoConicFivePoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoConicFivePoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint[] P; // input  five points      
    private GeoConic conic; // output             

    private double[][] A, B, C;
    private double l, m;
    private GeoVec3D[] line;
    private int i, j;

    AlgoConicFivePoints(Construction cons, String label, GeoPoint[] P) {
        super(cons);
        this.P = P;
        conic = new GeoConic(cons);
        for (int i=0; i < P.length; i++) {
        	conic.addPointOnConic(P[i]);
        }
        
        setInputOutput(); // for AlgoElement

        line = new GeoVec3D[4];
        for (i = 0; i < 4; i++) {
            line[i] = new GeoLine(cons);
        }
        A = new double[3][3];
        B = new double[3][3];
        C = new double[3][3];

        compute();
        conic.setLabel(label);
    }

    String getClassName() {
        return "AlgoConicFivePoints";
    }

    // for AlgoElement
    void setInputOutput() {
        input = P;

        output = new GeoElement[1];
        output[0] = conic;
        setDependencies(); // done by AlgoElement
    }

    GeoConic getConic() {
        return conic;
    }
    GeoPoint[] getPoints() {
        return P;
    }

    // compute conic through five points P[0] ... P[4]
    // with Pl�cker � method
    final void compute() {
        // compute lines P0 P1, P2 P3, 
        //               P0 P2, P1 P3
        GeoVec3D.cross(P[0], P[1], line[0]);
        GeoVec3D.cross(P[2], P[3], line[1]);
        GeoVec3D.cross(P[0], P[2], line[2]);
        GeoVec3D.cross(P[1], P[3], line[3]);

        // compute degenerate conics A = line[0] u line[1],
        //                           B = line[2] u line[3]
        degCone(line[0], line[1], A);
        degCone(line[2], line[3], B);
        l = evalMatrix(B, P[4]);
        m = -evalMatrix(A, P[4]);
        linComb(A, B, l, m, C);
        conic.setMatrix(C);
    }

    // compute degenerate conic from lines a, b
    // the result is written into A as a NON-SYMMETRIC Matrix
    final private void degCone(GeoVec3D a, GeoVec3D b, double[][] A) {
        // A = a . b^t
        A[0][0] = a.x * b.x;
        A[0][1] = a.x * b.y;
        A[0][2] = a.x * b.z;
        A[1][0] = a.y * b.x;
        A[1][1] = a.y * b.y;
        A[1][2] = a.y * b.z;
        A[2][0] = a.z * b.x;
        A[2][1] = a.z * b.y;
        A[2][2] = a.z * b.z;
    }

    // computes P.A.P, where A is a (possibly not symmetric) 3x3 matrix
    final private double evalMatrix(double[][] A, GeoPoint P) {
        return A[0][0] * P.x * P.x
            + A[1][1] * P.y * P.y
            + A[2][2] * P.z * P.z
            + (A[0][1] + A[1][0]) * P.x * P.y
            + (A[0][2] + A[2][0]) * P.x * P.z
            + (A[1][2] + A[2][1]) * P.y * P.z;
    }

    // computes the linear combination C = l * A + m * B    
    final private void linComb(
        double[][] A,
        double[][] B,
        double l,
        double m,
        double[][] C) {
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                C[i][j] = l * A[i][j] + m * B[i][j];
            }
        }
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("Conic"));
            sb.append(' ');
        }
        sb.append(app.getPlain("through"));
        sb.append(' ');
        sb.append(P[0].getLabel());
        for (i = 1; i < 5; i++) {
            sb.append(", ");
            sb.append(P[i].getLabel());
        }
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(app.getPlain("Conic"));
        }

        return sb.toString();
    }
}
