package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.Application3D;

public abstract class GeoCoordSys1D extends GeoElement3D implements Path,
GeoLineND, GeoCoordSys{
	
	protected GgbCoordSys coordsys;
	
	protected GeoPointND startPoint;

	protected GeoPointND endPoint;

	public GeoCoordSys1D(Construction c){
		super(c);
		coordsys = new GgbCoordSys(1);
	}
	
	public GeoCoordSys1D(Construction c, GgbVector O, GgbVector V){
		this(c);
		setCoord(O,V);
	}
	
	
	public GeoCoordSys1D(Construction c, GeoPointND O, GeoPointND I){
		this(c);
		setCoord(O,I);
	}	
	
	
	
	public boolean isDefined() {
		return coordsys.isDefined();
	}
	
	

	public void setUndefined() {
		coordsys.setUndefined();
	}

	
	
	
	/** set the matrix to [V O] */
	public void setCoordFromPoints(GgbVector a_O, GgbVector a_I){
		 setCoord(a_O,a_I.sub(a_O));
	}
	
	/** set the matrix to [V O] */
	public void setCoord(GgbVector o, GgbVector v){
		coordsys.resetCoordSys();
		coordsys.addPoint(o);
		coordsys.addVector(v);
		coordsys.makeOrthoMatrix(false);
	}
	
	
	/** set coords to origin O and vector (I-O).
	 * If I (or O) is infinite, I is used as direction vector.
	 * @param O origin point
	 * @param I unit point*/
	public void setCoord(GeoPointND O, GeoPointND I){
		
		startPoint = O;
		endPoint = I;
		
		if (I.isInfinite())
			if (O.isInfinite())
				setUndefined(); //TODO infinite line
			else
				setCoord(O.getCoordsInD(3),I.getCoordsInD(3));
		else
			if (O.isInfinite())
				setCoord(I.getCoordsInD(3),O.getCoordsInD(3));
			else
				setCoord(O.getCoordsInD(3),I.getCoordsInD(3).sub(O.getCoordsInD(3)));
		
	}
	
	
	public void setCoord(GeoCoordSys1D geo){
		setCoord(geo.getCoordSys().getOrigin(),geo.getCoordSys().getVx());
	}
	
	
	public void set(GeoElement geo) {
		if (geo instanceof GeoCoordSys1D){
			if (!geo.isDefined())
				setUndefined();
			else
				setCoord((GeoCoordSys1D) geo);
		}

	}
	
	
	/**
	 * @param cons 
	 * @return a new instance of the proper GeoCoordSys1D (GeoLine3D, GeoSegment3D, ...)
	 */
	abstract protected GeoCoordSys1D create(Construction cons);
	
	

	final public GeoElement copy() {
		GeoCoordSys1D geo = create(cons);
		geo.setCoord(this);
		return geo;
	}
	

	
	/** returns matrix corresponding to segment joining l1 to l2, using getLineThickness() */
	/*
	public GgbMatrix getSegmentMatrix(double l1, double l2){
		
	
		
		return GgbMatrix4x4.subSegmentX(getMatrix4x4(), l1, l2);
	}	
*/
	
	
	/** returns the point at position lambda on the coord sys 
	 * @param lambda 
	 * @return the point at position lambda on the coord sys  */
	public GgbVector getPoint(double lambda){
		return coordsys.getPoint(lambda);
		
	}


	

	/** returns the point at position lambda on the coord sys in the dimension given
	 * @param dimension 
	 * @param lambda 
	 * @return the point at position lambda on the coord sys  
	 * */
	public GgbVector getPointInD(int dimension, double lambda){

		GgbVector v = getPoint(lambda);
		switch(dimension){
		case 3:
			return v;
		case 2:
			return new GgbVector(v.getX(), v.getY(), v.getW());
		default:
			return null;
		}
	}


	/** returns cs unit */
	public double getUnit(){
		
		/*
		GgbVector v = getCoordSys().getVx();
		
		if (v==null)
			return 0;
		else
		*/
			return getCoordSys().getVx().norm();
	}

	
	public GgbVector getMainDirection(){ 
		return getCoordSys().getMatrixOrthonormal().getVx();
	};

	
	
	
	// Path3D interface
	public boolean isPath(){
		return true;
	}
	
	public void pointChanged(GeoPointND P){
		
		
		boolean done = false;
		
		//project P on line
		double t = 0;
		if (((GeoElement) P).isGeoElement3D()){
			if (((GeoPoint3D) P).getWillingCoords()!=null){
				if(((GeoPoint3D) P).getWillingDirection()!=null){
					//project willing location using willing direction
					//GgbVector[] project = coordsys.getProjection(P.getWillingCoords(), P.getWillingDirection());

					GgbVector[] project = ((GeoPoint3D) P).getWillingCoords().projectOnLineWithDirection(
							coordsys.getOrigin(),
							coordsys.getVx(),
							((GeoPoint3D) P).getWillingDirection());

					t = project[1].get(1);
					done = true;
				}else{
					//project current point coordinates
					//Application.debug("ici\n getWillingCoords=\n"+P.getWillingCoords()+"\n matrix=\n"+getMatrix().toString());
					GgbVector preDirection = ((GeoPoint3D) P).getWillingCoords().sub(coordsys.getOrigin()).crossProduct(coordsys.getVx());
					if(preDirection.equalsForKernel(0, Kernel.STANDARD_PRECISION))
						preDirection = coordsys.getVy();

					GgbVector[] project = ((GeoPoint3D) P).getWillingCoords().projectOnLineWithDirection(
							coordsys.getOrigin(),
							coordsys.getVx(),
							preDirection.crossProduct(coordsys.getVx()));

					t = project[1].get(1);	
					done = true;
				}
			}
		}
		
		if(!done){
			//project current point coordinates
			//Application.debug("project current point coordinates");
			GgbVector preDirection = P.getCoordsInD(3).sub(coordsys.getOrigin()).crossProduct(coordsys.getVx());
			if(preDirection.equalsForKernel(0, Kernel.STANDARD_PRECISION))
				preDirection = coordsys.getVy();
			
			GgbVector[] project = P.getCoordsInD(3).projectOnLineWithDirection(
					coordsys.getOrigin(),
					coordsys.getVx(),
					preDirection.crossProduct(coordsys.getVx()));
	
			t = project[1].get(1);	
		}
		
		
		if (t<getMinParameter())
			t=getMinParameter();
		else if (t>getMaxParameter())
			t=getMaxParameter();

		
		
		
		
		// set path parameter		
		PathParameter pp = P.getPathParameter();
		
		
		pp.setT(t);
		
		//udpate point using pathChanged
		pathChanged(P);
		
		

	}
	
	
	public void pathChanged(GeoPointND P){
		
		PathParameter pp = P.getPathParameter();
		P.setCoords(getPoint(pp.getT()),false);

	}
	
	
	public boolean isOnPath(GeoPointND PI, double eps){
		return false; //TODO
	}

	
	

	////////////////////////////////////
	//
	
	/** return true if x is a valid coordinate (eg 0<=x<=1 for a segment)
	 * @param x coordinate
	 * @return true if x is a valid coordinate (eg 0<=x<=1 for a segment)
	 */
	abstract public boolean isValidCoord(double x);
	
	
	
	////////////////////////////////////
	// XML
	////////////////////////////////////
	
	
    /**
     * returns all class-specific xml tags for saveXML
     */
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb);
		//	line thickness and type  
		getLineStyleXML(sb);
		
	}
	
	
	

	
	public GgbCoordSys getCoordSys() {
		return coordsys;
	}
	
	
	public GgbMatrix4x4 getDrawingMatrix(){
		return getCoordSys().getMatrixOrthonormal();
	}

	

	public GgbVector getLabelPosition(){
		return coordsys.getPoint(0.5);
	}

	
	
	public boolean getTrace() {
		return false;//TODO
	}
	

	public GgbVector getCartesianEquationVector(GgbMatrix m){
		GgbVector origin = getCoordSys().getOrigin();
		GgbVector direction = getCoordSys().getVx();
		
		//TODO generalize it to other planes than xOy
		
		//if lines is not in the plane, return null
		if (!Kernel.isZero(origin.getZ()) || !Kernel.isZero(direction.getZ()))
			return null;
		
		double x = -direction.getY();
		double y = direction.getX();
		double z = -x*origin.getX()-y*origin.getY();
		
		return new GgbVector(x, y, z);
	}
	
	
	public GgbVector getStartInhomCoords(){
		return getCoordSys().getOrigin().getInhomCoords();
	}
	
	/**
	 * @return inhom coords of the end point
	 */
	public GgbVector getEndInhomCoords(){
		return getCoordSys().getPoint(1).getInhomCoords();
	}
	
	
	

	
}
