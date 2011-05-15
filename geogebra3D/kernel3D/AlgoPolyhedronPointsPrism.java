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
public class AlgoPolyhedronPointsPrism extends AlgoPolyhedronPoints{
	


	

	/**
	 * @param c
	 * @param labels
	 * @param polygon
	 * @param point
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels, GeoPolygon polygon, GeoPointND point) {
		super(c, labels, polygon, point);

	}
	
	/**
	 * @param c
	 * @param labels
	 * @param points
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels, GeoPointND[] points) {
		super(c,labels,points);

	}
	
	
	/**
	 * @param c
	 * @param labels
	 * @param polygon
	 * @param height
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels, GeoPolygon polygon, NumberValue height) {
		super(c, labels, polygon, height);

	}
	
	
	
	
	
	
	protected void createPolyhedron(GeoPolyhedron polyhedron){

		GeoPointND[] bottomPoints = getBottomPoints();
		GeoPointND topPoint = getTopPoint();
		
		int numPoints = bottomPoints.length;


		GeoPointND[] points = new GeoPointND[numPoints*2];
		outputPoints.augmentOutputSize(numPoints-1);
		outputPoints.setLabels(null);
		for(int i=0;i<numPoints;i++)
			points[i] = bottomPoints[i];
		points[numPoints] = topPoint;		
		for(int i=0;i<numPoints-1;i++){
			GeoPoint3D point = outputPoints.getElement(i+1-getShift());
			points[numPoints+1+i] = point;
			polyhedron.addPointCreated(point);
		}
		
		//bottom of the prism
		setBottom(polyhedron);
		
		//sides of the prism
		for (int i=0; i<numPoints; i++){
			polyhedron.startNewFace();
			polyhedron.addPointToCurrentFace(points[i]);
			polyhedron.addPointToCurrentFace(points[(i+1)%(numPoints)]);
			polyhedron.addPointToCurrentFace(points[numPoints + ((i+1)%(numPoints))]);
			polyhedron.addPointToCurrentFace(points[numPoints + i]);
			polyhedron.endCurrentFace();
		}
		

		
		//top of the prism
		polyhedron.startNewFace();
		for (int i=0; i<numPoints; i++)
			polyhedron.addPointToCurrentFace(points[numPoints+i]);
		polyhedron.endCurrentFace();


		polyhedron.setType(GeoPolyhedron.TYPE_PRISM);
		
	}
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	
	
	protected void compute() {

		super.compute();

		GeoPolyhedron polyhedron = getPolyhedron();
		GeoPointND[] bottomPoints = getBottomPoints();

		//translation from bottom to top
		Coords v = getUpTranslation();
		
		//translate all output points
		for (int i=0;i<outputPoints.size();i++)
			outputPoints.getElement(i).setCoords(bottomPoints[i+getShift()].getCoordsInD(3).add(v),true);

		//TODO remove this and replace with tesselation
		Coords interiorPoint = new Coords(4);
		for (int i=0;i<bottomPoints.length;i++){
			interiorPoint = (Coords) interiorPoint.add(bottomPoints[i].getCoordsInD(3));
		}
		interiorPoint = (Coords) interiorPoint.mul((double) 1/(bottomPoints.length));
		polyhedron.setInteriorPoint((Coords) interiorPoint.add(v.mul(0.5)));


	}

	public void update() {

		// compute and polyhedron
		super.update();
		
		//output points
		for (int i=0;i<outputPoints.size();i++)
			outputPoints.getElement(i).update();

	}

	

    public String getClassName() {

    	return "AlgoPrism";

    }
    
    

}
