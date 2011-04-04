package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.AlgoElementWithResizeableOutput;
import geogebra.kernel.AlgoSimpleRootsPolynomial;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.AlgoElement.OutputHandler;
import geogebra.kernel.AlgoElement.elementFactory;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;

import java.util.ArrayList;

/**
 * @author ggb3D
 * 
 * Creates a new Pyramid
 *
 */
public class AlgoPolyhedronPointsPyramid extends AlgoPolyhedronPoints{

	
	/** creates a pyramid regarding vertices and faces description
	 * @param c construction 
	 * @param a_points vertices
	 */
	public AlgoPolyhedronPointsPyramid(Construction c, String[] labels, GeoPointND[] points) {
		super(c,labels,points);

	}
	
	protected void createPolyhedron(GeoPolyhedron polyhedron){

		GeoPointND[] bottomPoints = getBottomPoints();
		GeoPointND topPoint = getTopPoint();
		
		int numPoints = bottomPoints.length;
		
		


		//base of the pyramid
		setBottom(polyhedron);

		//sides of the pyramid
		for (int i=0; i<numPoints; i++){
			polyhedron.startNewFace();
			polyhedron.addPointToCurrentFace(bottomPoints[i]);
			polyhedron.addPointToCurrentFace(bottomPoints[(i+1)%(numPoints)]);
			polyhedron.addPointToCurrentFace(topPoint);//apex
			polyhedron.endCurrentFace();
		}


		polyhedron.setType(GeoPolyhedron.TYPE_PYRAMID);
		
	}
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	
	
	protected void compute() {


		GeoPolyhedron polyhedron = getPolyhedron();
		GeoPointND[] bottomPoints = getBottomPoints();

		//TODO remove this and replace with tesselation
		Coords interiorPoint = new Coords(4);
		for (int i=0;i<bottomPoints.length;i++){
			interiorPoint = interiorPoint.add(bottomPoints[i].getCoordsInD(3));
		}
		interiorPoint = interiorPoint.add(getTopPoint().getCoordsInD(3));
		
		interiorPoint = (Coords) interiorPoint.mul((double) 1/(bottomPoints.length+1));
		polyhedron.setInteriorPoint(interiorPoint);
		//Application.debug("interior\n"+interiorPoint);

	}
	
	

    public String getClassName() {

    	return "AlgoPyramid";

    }
	
}
