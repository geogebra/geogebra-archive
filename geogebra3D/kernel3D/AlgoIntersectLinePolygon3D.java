package geogebra3D.kernel3D;


import geogebra.Matrix.CoordMatrixUtil;
import geogebra.Matrix.Coords;
import geogebra.kernel.AlgoIntersectLinePolygon;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.Kernel;
import geogebra.kernel.PathParameter;
import geogebra.kernel.AlgoElement.OutputHandler;
import geogebra.kernel.AlgoElement.elementFactory;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoSegmentND;

import java.util.TreeMap;


public class AlgoIntersectLinePolygon3D extends AlgoIntersectLinePolygon {

	AlgoIntersectLinePolygon3D(Construction c, String[] labels, GeoLineND g,
			GeoPolygon p) {
		super(c, labels, g, p);
	}
	
	
    public AlgoIntersectLinePolygon3D(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p, boolean asBoundary) {
    	super(c, labels, g, p, asBoundary);
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
	
	   protected OutputHandler<GeoElement> createOutputSegments(){
	    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
				public GeoSegment3D newElement() {
				
					GeoPoint3D aS = new GeoPoint3D(cons);
					aS.setCoords(0, 0, 0, 1);
					GeoPoint3D aE = new GeoPoint3D(cons);
					aE.setCoords(0, 0, 0, 1);
					GeoSegment3D a=new GeoSegment3D(cons, aS, aE);
					a.setParentAlgorithm(AlgoIntersectLinePolygon3D.this);
					return a;
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
    protected void intersectionsSegments(GeoLineND g, GeoPolygon p,
			TreeMap<Double, Coords> newCoords,
			TreeMap<Double, Coords[]> newSegmentCoords) {
		
    	
    	if (newCoords==null || newCoords.size()==0) {
    		newSegmentCoords.clear();
    		return;
    	}
    	/*
      	//coords of some point outside of p, e.g. (x=1+Max X coord of P, 0) 
    	double pMaxX = 0;
    	for (int i = 0; i< p.getPointsLength(); i++) {
    		pMaxX = Math.max(pMaxX, p.getPointX(i));
    		//pMaxY = Math.max(pMaxY, p.getPointY(i));
    	}
    	double pOutsideX = pMaxX + 1;
    	double pOutsideY = 0;
    	*/
    	
    	//naive traversing algorithm which assumes no special case
    	double s1 = g.getMinParameter();
    	double s2 = g.getMaxParameter(); 
       	double minKey = newCoords.firstKey();
    	double maxKey = newCoords.lastKey();
    	
    	double tLast;
   
    	if (s1>=maxKey)
    		tLast = s1;
    	else
    		tLast = s2;
    	
    	//PathParameter tempParam = new PathParameter(t);
    	int nextChangeOfRegion = 1;
    	
    	Coords tempCoords = new Coords(0, 0, 0, 1);
    	tempCoords.set(((GeoLineND)g).getPointInD(3, tLast));
    	if (p.isInRegion(tempCoords.get(1),tempCoords.get(2)) && !Kernel.isEqual(tLast, maxKey)) {
    		newSegmentCoords.put(tLast, new Coords[] {tempCoords, newCoords.get(maxKey)});
    		nextChangeOfRegion = -1;
    	}

    	tLast = maxKey;
    	
    	while (tLast > minKey){
    		double t = newCoords.subMap(minKey, tLast).lastKey();
    		if (nextChangeOfRegion == 1) {
  
    			newSegmentCoords.put(tLast,  new Coords[] {
    					newCoords.get(tLast), 
    					newCoords.get(t)
    							});
    			nextChangeOfRegion = -1;
    		} else if (nextChangeOfRegion == -1) {
    			nextChangeOfRegion = 1;
    		}
    		tLast = t;
    	}
    	
    	//traversing algorithm for dealing with degenerated case
   /* 	int regionChange = 1;  // 1: enter once; -1: quit once; 0: neither
    	int regionWaitingToChange = 0; //0: no need to wait;
    	//1: wait for other right hand segment
    	//-1: wait for another left hand segment 
    	
    	double minKey = newCoords.firstKey();
    	double maxKey = newCoords.lastKey();
    	
    	for (double t = maxKey;
    			t > minKey;
    			t = newCoords.subMap(minKey, t).lastKey()) {
    		//if guaranteed no singularity, just do    		
    		//check which segments contains P(t) 
    	}	 
    */		
		
	}

    

}
