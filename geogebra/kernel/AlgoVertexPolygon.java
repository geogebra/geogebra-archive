/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoVertex.java
 *
 * Created on 11. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

import java.util.ArrayList;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoVertexPolygon extends AlgoElement implements
		AlgoElementWithResizeableOutput {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPolygon p; // input
	private ArrayList<GeoPoint> pointsList; // output
	private String singleLabel = null;
	private int nbLabelSet = 0;
	private NumberValue index;
	private GeoPoint oneVertex;

	/**
	 * Creates new vertex algo
	 * 
	 * @param cons
	 * @param labels
	 * @param p
	 */

	AlgoVertexPolygon(Construction cons, String[] labels, GeoPolygon p) {

		this(cons, p);
		// if only one label (e.g. "A"), new labels will be A_1, A_2, ...
		if (labels != null)
			if (labels.length == 1)
				if (labels[0] != null)
					if (!labels[0].equals(""))
						singleLabel = labels[0];

		// set labels dependencies: will be used with
		// Construction.resolveLabelDependency()

	}

	/**
	 * @param cons
	 * @param label
	 * @param p
	 * @param v
	 */
	AlgoVertexPolygon(Construction cons, String label, GeoPolygon p,
			NumberValue v) {

		this(cons, p, v);
		oneVertex.setLabel(label);
	}

	/**
	 * Creates new unlabeled vertex algo
	 * 
	 * @param cons
	 * @param p
	 */

	AlgoVertexPolygon(Construction cons, GeoPolygon p) {
		super(cons);
		this.p = p;

		setInputOutput(); // for AlgoElement
		compute();
	}

	/**
	 * @param cons
	 * @param p
	 * @param v
	 */
	AlgoVertexPolygon(Construction cons, GeoPolygon p, NumberValue v) {
		super(cons);
		this.p = p;
		this.index = v;
		oneVertex = new GeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	public String getClassName() {
		return "AlgoVertex";
	}

	// for AlgoElement
	public void setInputOutput() {
		if(index!=null){
			input = new GeoElement[2];
			input[1] = index.toGeoElement();			
			setOutputLength(1);
			setOutput(0,oneVertex);
		}else{
			input = new GeoElement[1];
			if (pointsList == null)
				pointsList = new ArrayList<GeoPoint>();
		}
		input[0] = p;
		/*
		 * setOutputLength(vertex.length); for(int i=0;i<vertex.length;i++){
		 * setOutput(i,vertex[i]); }
		 */
		
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the polygon
	 * 
	 * @return input polygon
	 */
	public GeoPolygon getPolygon() {
		return p;
	}

	private void initPoints(int number) {
		// make sure that there are enough points

		if (pointsList.size() < number) {

			for (int i = pointsList.size(); i < number; i++) {
				GeoPoint tmp = new GeoPoint(cons);
				tmp.setCoords(0, 0, 1); // init as defined
				tmp.showUndefinedInAlgebraView(false);
				tmp.setParentAlgorithm(this);
				tmp.setLabel(singleLabel);
				cons.setLabelDependsOn(tmp.getLabel(), this);
				pointsList.add(tmp);

			}
		}
	}

	public int getOutputLength() {
		if(index!=null) return 1;
		return pointsList.size();
	}

	protected final void compute() {
		if(index != null){
			int  i = (int)Math.floor(index.getDouble())-1;
			if(i >= p.getPointsLength()||i < 0)
				oneVertex.setUndefined();
			else 
				oneVertex.set((GeoElement)p.getPoint(i));
			oneVertex.update();
			return;
		}
		int length = p.getPointsLength();
		for (int i = length; i < pointsList.size(); i++) {
			pointsList.get(i).setUndefined();
		}
		initPoints(length);
		for (int i = 0; i < length; i++) {
			GeoPoint point = p.getPoint(i);
			pointsList.get(i).set((GeoElement) point);
			pointsList.get(i).update();
		}
	}

	public final String toString() {
		return app.getPlain("VertexOfA", p.getLabel());

	}

	/**
	 * Returns list of the vertices
	 * 
	 * @return list of the vertices
	 */
	public GeoElement[] getVertex() {
		GeoElement[] output = new GeoElement[getOutputLength()];

		int i = 0;
		for (GeoElement geo : pointsList) {
			output[i] = geo;
			i++;
		}

		return output;
	}

	public GeoElement getOutput(int i) {
		if(index!=null)return oneVertex;
		return pointsList.get(i);
	}
	
	/**
	 * @return the vertex when called as Vertex[poly,number]
	 */
	public GeoPoint getOneVertex(){
		return oneVertex;
	}

	public GeoElement addLabelToOutput(String label, int type) {

		GeoElement ret;
		if (nbLabelSet < pointsList.size()) { // set geo equal to element of the
												// list
			ret = pointsList.get(nbLabelSet);
			nbLabelSet++;
		} else { // add this geo at the end of the list
			GeoPoint geo = new GeoPoint(getConstruction());
			pointsList.add((GeoPoint) geo);
			ret = geo;
			setOutputDependencies(geo);
			nbLabelSet++;
		}
		return ret;
	}

}