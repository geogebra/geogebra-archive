package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;

import java.awt.Label;

public class AlgoImplicitPolyThroughPoints extends AlgoElement 
{
	private GeoPoint[] P; // input points      
    private GeoImplicitPoly implicitPoly; // output 
	
	AlgoImplicitPolyThroughPoints(Construction cons, String label, GeoPoint[] p)
	{
		super(cons);
		this.P = p;
		
		implicitPoly = new GeoImplicitPoly(cons);
		
		setInputOutput();
		compute();

		implicitPoly.setLabel(label);
	}
	
	public GeoImplicitPoly getImplicitPoly() {
		return implicitPoly;
	}
	
	public GeoPoint[] getP() {
		return P;
	}
	
	@Override
	protected void setInputOutput() {
		input = P;
		output = new GeoElement[1];
		output[0] = implicitPoly;
		setDependencies();
	}

	@Override
	protected void compute() {
		implicitPoly.throughPoints(P);
	}
	
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_IMPLICIT_POLY_THROUGH_POINTS;
    }

	@Override
	public String getClassName() {
		 return "AlgoImplicitPolyThroughPoints";
	}

	final public String toString() {
		 String [] str = new String[P.length];
		 for(int i=0; i<P.length; i++)
			 str[i] = P[i].getLabel();
		 return app.getPlain("ImplicitPolyThroughPoints",str);
	}
}
