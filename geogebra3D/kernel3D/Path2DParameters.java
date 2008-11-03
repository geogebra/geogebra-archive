package geogebra3D.kernel3D;




public class Path2DParameters extends Path1DParameter {

	double t2; //second parameter
	
	public Path2DParameters() {
		super();
		t2 = Double.NaN;
	}
	
	public Path2DParameters(double t1, double t2) {
		super(t1);
		this.t2=t2;
	}
	
	final public void set(Path2DParameters pp) {
		super.set(pp);
		this.t2=pp.t2;
	}
	





	public final double getT1() {
		return getT();
	}

	
	public final double getT2() {
		return t2;
	}

	public final void setT(double t1, double t2) {
		super.setT(t1);
		this.t2 = t2;
	}

	

}
