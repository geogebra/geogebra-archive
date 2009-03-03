package geogebra3D.kernel3D;

import geogebra.kernel.PathParameter;


/**
 * @author ggb3D
 * 
 * Extends PathParameter to allow more than one parameter
 */
public class PathParameters extends PathParameter {

	double[] ts; //all parameters
	int n; //number of parameters
	
	public PathParameters() {
		super();
		
	}
	
	public PathParameters(int n) {
		super();
		this.n = n;
		ts = new double[n];
	}
	
	public PathParameters(double[] ts) {
		super(ts[0]);
		setTs(ts);
	}
	
	final public void set(PathParameters pp) {
		super.set(pp);
		setTs(pp.getTs());
	}
	

	public final double getT(int i) {
		return ts[i];
	}
	
	public final double[] getTs() {
		return ts;
	}

	public final void setTs(double[] ts) {
		setT(ts[0]);
		n=ts.length;
		for (int i=0;i<n;i++)
			this.ts[i]=ts[i];
	}

	

}
