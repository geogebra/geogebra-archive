package geogebra3D.kernel3D;


import geogebra.Matrix.CoordMatrixUtil;
import geogebra.Matrix.Coords;
import geogebra.kernel.AlgoIntersectLinePolygon;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoSegmentND;

import java.util.TreeMap;


public class AlgoIntersectLinePolygon3D extends AlgoIntersectLinePolygon {

	AlgoIntersectLinePolygon3D(Construction c, String[] labels, GeoLineND g,
			GeoPolygon p) {
		super(c, labels, g, p);
	}
	
	
    protected OutputHandler<GeoElement> createOutputPoints(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLinePolygon3D.this);
				return p;
			}
		});
    }
    
    protected void intersectionsCoords(GeoLineND g, GeoPolygon p, TreeMap<Double, Coords> newCoords){

    	//line origin, direction, min and max parameter values
    	Coords o1 = g.getPointInD(3, 0);
    	Coords d1 = g.getPointInD(3, 1).sub(o1);
    	double min = g.getMinParameter();
    	double max = g.getMaxParameter();
 
    	
    	
    	for(int i=0; i<p.getSegments().length; i++){
    		GeoSegmentND seg = (GeoSegmentND) p.getSegments()[i];
    		
    		Coords o2 = seg.getPointInD(3, 0);
           	Coords d2 = seg.getPointInD(3, 1).sub(o2);

           	Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(
           			o1,d1,o2,d2
           	);

           	//check if projection is intersection point
           	if (project!=null && project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){
           	
           		double t1 = project[2].get(1); //parameter on line
           		double t2 = project[2].get(2); //parameter on segment


           		if (t1>=min && t1<=max //TODO optimize that
           				&& t2>=0 && t2<=1)
           			newCoords.put(t1, project[0]);

           	}
        }
        
    }
    

}
