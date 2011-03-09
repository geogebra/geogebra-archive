package geogebra3D.kernel3D;

import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;


/**
 * Algo for cylinder between two end points and given radius.
 * @author mathieu
 *
 */
public abstract class AlgoQuadricLimitedPointPointRadius extends AlgoElement3D {

	//input
	private GeoPointND origin, secondPoint;
	private NumberValue radius;
	
	//output
	private GeoQuadric3DPart side;
	private GeoConic3D bottom, top;
	private GeoQuadric3DLimited quadric;
	

	/**
	 * 
	 * @param c
	 * @param label
	 * @param origin
	 * @param secondPoint
	 * @param r
	 */
	public AlgoQuadricLimitedPointPointRadius(Construction c, String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r, int type) {
		super(c);
		
		this.origin=origin;
		this.secondPoint=secondPoint;
		this.radius=r;
		
		
		//bottomCoordsys = new CoordSys(2);
		//topCoordsys = new CoordSys(2);
		
		quadric=new GeoQuadric3DLimited(c,origin,secondPoint);
		quadric.setType(type);


		//bottom.setCoordSys(bottomCoordsys);
		//top.setCoordSys(topCoordsys);
		
		//setInputOutput(new GeoElement[] {(GeoElement) origin,(GeoElement) secondPoint,(GeoElement) r}, new GeoElement[] {quadric,bottom,top,side},false);

		input = new GeoElement[] {(GeoElement) origin,(GeoElement) secondPoint,(GeoElement) r};

		
		((GeoElement) origin).addAlgorithm(this);
		((GeoElement) secondPoint).addAlgorithm(this);
		((GeoElement) r).addAlgorithm(this);
		
		
		
    	// parent of output
        quadric.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 
        
		compute();
        
        //quadric.setParts();
        
		//AlgoQuadricSide algo = new AlgoQuadricSide(cons, quadric, origin, secondPoint);            
		AlgoQuadricSide algo = new AlgoQuadricSide(cons, quadric,true);            
		cons.removeFromConstructionList(algo);
		side = (GeoQuadric3DPart) algo.getQuadric();
		
		//AlgoQuadricEnds algo2 = new AlgoQuadricEnds(cons, quadric, origin, secondPoint);
		AlgoQuadricEnds algo2 = new AlgoQuadricEnds(cons, quadric);
		cons.removeFromConstructionList(algo2);
		bottom = algo2.getSection1();
		top = algo2.getSection2();
		

		quadric.setParts(side,bottom,top);

		output = new GeoElement[] {quadric,bottom,top,side};
	


		//compute();
		
		
		quadric.initLabels(labels);
		quadric.updatePartsVisualStyle();
		

		
	}
	
	
	protected void compute() {
		
		Coords o = origin.getInhomCoordsInD(3);
		Coords o2 = secondPoint.getInhomCoordsInD(3);
		Coords d = o2.sub(o);
		double r = radius.getDouble();
		
		d.calcNorm();
		double altitude = d.getNorm();
		
		setQuadric(o,o2,d.mul(1/altitude),r, 0, altitude);

		quadric.calcVolume();

	}

	abstract protected void setQuadric(Coords o1, Coords o2, Coords d, double r, double min, double max);

	
	public GeoQuadric3DLimited getQuadric(){
		return quadric;
	}
	
	//compute and update quadric (for helper algos)
	public void update() {
        compute();
        quadric.update();
    }
    

	

}
