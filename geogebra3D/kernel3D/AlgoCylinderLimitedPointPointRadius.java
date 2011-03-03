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
public class AlgoCylinderLimitedPointPointRadius extends AlgoElement3D {

	//input
	private GeoPointND origin, secondPoint;
	private NumberValue radius;
	
	//output
	private GeoQuadric3DPart side;
	private GeoConic3D bottom, top;
	private GeoQuadric3DLimited quadric;
	
	//coordsys
	private CoordSys bottomCoordsys, topCoordsys; 

	/**
	 * 
	 * @param c
	 * @param label
	 * @param origin
	 * @param secondPoint
	 * @param r
	 */
	public AlgoCylinderLimitedPointPointRadius(Construction c, String[] labels, GeoPointND origin, GeoPointND secondPoint, NumberValue r) {
		super(c);
		
		this.origin=origin;
		this.secondPoint=secondPoint;
		this.radius=r;
		
		
		//bottomCoordsys = new CoordSys(2);
		//topCoordsys = new CoordSys(2);
		
		quadric=new GeoQuadric3DLimited(c,origin,secondPoint);


		//bottom.setCoordSys(bottomCoordsys);
		//top.setCoordSys(topCoordsys);
		
		//setInputOutput(new GeoElement[] {(GeoElement) origin,(GeoElement) secondPoint,(GeoElement) r}, new GeoElement[] {quadric,bottom,top,side},false);

		input = new GeoElement[] {(GeoElement) origin,(GeoElement) secondPoint,(GeoElement) r};

		
		((GeoElement) origin).addAlgorithm(this);
		((GeoElement) secondPoint).addAlgorithm(this);
		
		
		
    	// parent of output
        quadric.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 
        
		//compute();
        
        quadric.setParts();
		
		side=quadric.getSide();
		bottom=quadric.getBottom();
		top=quadric.getTop();

		output = new GeoElement[] {quadric,bottom,top,side};
	


		compute();
		
		
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
		
		quadric.setCylinder(o,d.mul(1/altitude),r,0, altitude);

		quadric.calcVolume();

	}

	

	
	//compute and update quadric (for helper algos)
	public void update() {
        compute();
        quadric.update();
    }
    

	public String getClassName() {
		return "AlgoCylinder";
	}
	
    final public String toString() {
    	return app.getPlain("CylinderBetweenABRadiusC",origin.getLabel(),secondPoint.getLabel(),((GeoElement) radius).getLabel());

    }
    

}
