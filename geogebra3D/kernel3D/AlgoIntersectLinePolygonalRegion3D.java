package geogebra3D.kernel3D;


import geogebra.Matrix.CoordMatrixUtil;
import geogebra.Matrix.Coords;
import geogebra.kernel.AlgoIntersectLinePolygonalRegion;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;

import java.util.TreeMap;

public class AlgoIntersectLinePolygonalRegion3D extends AlgoIntersectLinePolygonalRegion {

	protected int spaceDim = 3;
	
	protected boolean lineInPlaneOfPolygon = false;
	
	/**
	 * This assumes that the line is in the plane of polygon 
	 * and the polygon acts as a region
	 * @param c 
	 * @param labels 
	 * @param g 
	 * @param p 
	 */
	AlgoIntersectLinePolygonalRegion3D(Construction c, String[] labels, 
			GeoPolygon p, GeoLineND g) {
		this(c, labels, g, p);
	}
	
	
    public AlgoIntersectLinePolygonalRegion3D(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p) {
    	super(c, labels, g, p);
    

	}

//TODO: merge 2D and 3D
	protected OutputHandler<GeoElement> createOutputPoints(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLinePolygonalRegion3D.this);
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
					a.setParentAlgorithm(AlgoIntersectLinePolygonalRegion3D.this);
					return a;
				}
			});
	    }
    
    protected void intersectionsCoords(GeoLineND g, GeoPolygon p, TreeMap<Double, Coords> newCoords){

    	if (!lineInPlaneOfPolygon){
    		//p.getConstruction().getKernel().setSilentMode(true);
    		
    		//AlgoIntersectCS1D2D algo = new AlgoIntersectCS1D2D(cons, null, (GeoElement) g,  p);
    		//GeoPoint3D point = (GeoPoint3D) algo.getIntersection();
    		
    		Coords singlePoint = AlgoIntersectCS1D2D.getIntersectLinePlane(g,p);
    		
    		if (singlePoint!=null)
    			newCoords.put(0.0, singlePoint);
    		
    		//p.getConstruction().getKernel().setSilentMode(false);
    		return;
    	}
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
		
    	if (!lineInPlaneOfPolygon)
    		return;
    	
    	//the following are pretty much the same as 2D
    	//most differences are about the incompatibility of 2D and 3D types
    	//TODO merge with 2D version
    	if (newCoords==null ) 
    		return;
    	if (newCoords.isEmpty()) {
    		if (g instanceof GeoSegmentND &&
    				((GeoPolygon3D) p).isInRegion(g.getStartPoint(),false))
    			newSegmentCoords.put(0.0,  new Coords[] {
    					g.getStartPoint().getCoordsInD(spaceDim),
    					g.getEndPoint().getCoordsInD(spaceDim)});
    		
    		return;
    	}

     	double minKey = newCoords.firstKey();
    	double maxKey = newCoords.lastKey();
    	double tOld, tLast;
    	Double tFirst;
    	GeoPointND pointFirst;
    	Coords coordsOld;
	   		
    	boolean isEnteringRegion = false;
    	boolean segmentAlongLine = false;
    	
    	
    	if (g.getMinParameter()>=maxKey){
    		tFirst = g.getMinParameter();
    		if (!tFirst.isInfinite()) {
    			pointFirst = g.getStartPoint();
    			isEnteringRegion = ((GeoPolygon3D)p).isInRegion(pointFirst);
    		}
    		tLast = g.getMaxParameter();
    		
    	} else {
    		tFirst = g.getMaxParameter();
       		if (!tFirst.isInfinite()) {
    			pointFirst = g.getEndPoint();
    			isEnteringRegion = ((GeoPolygon3D)p).isInRegion(pointFirst);
       		}
    		tLast = g.getMinParameter();
    		//pointLast = ((GeoLine3D)g).startPoint;
    	}
    
    	tOld = tFirst;

    	if ( isEnteringRegion &&
    		!Kernel.isEqual(tOld, maxKey))
    			newSegmentCoords.put(tOld,
    				new Coords[] {
    					g.getPointInD(spaceDim, tFirst),
    					newCoords.get(maxKey)
    				}
    			);
    	isEnteringRegion = !isEnteringRegion;
    	
    	tOld = maxKey;
    	coordsOld = newCoords.get(maxKey);
    	
 
    	//loop for all possible change of region
    	while (tOld > minKey) {
    		double tNew = newCoords.subMap(minKey, tOld).lastKey();
    		
    		//Check multiplicity at tLast
    		//i.e. how many times g crosses the border at tLast
    		int tOld_m = 0;
    		int tOld_mRight = 0;
    		int tOld_mLeft = 0;
    		
    		Coords gRight = g.getDirectionInD3().crossProduct(p.getCoordSys().getNormal());
    		
    		for (int i = 0; i<p.getPointsLength(); i++) {
    			GeoSegmentND currSeg = p.getSegments()[i];
    			
    			if (currSeg.isOnPath(coordsOld, Kernel.EPSILON)) {
    				tOld_m++;
    			} else {
    				continue;
    			}
    				
    			if (coordsOld.getInhomCoords().isEqual(currSeg.getStartInhomCoords())
    					|| coordsOld.getInhomCoords().isEqual(currSeg.getEndInhomCoords()) ) {
    				tOld_m--;
    				
    				double currSegIncline = currSeg.getDirectionInD3().dotproduct(gRight);
    				if (Kernel.isGreater(currSegIncline, 0))
    					tOld_mRight++;
    				else if (Kernel.isGreater(0, currSegIncline))
    					tOld_mLeft++;
    				else {//logically saying currSeg is along the line; can have potential computational problem unknown
    					segmentAlongLine = true;
    					tOld_m--;
    				}
    			}
    		}
    		
    		if (tOld_mRight != 0 || tOld_mLeft != 0)
    			if (tOld_mRight >= tOld_mLeft) {
    				tOld_mRight -= tOld_mLeft;
    				tOld_m += tOld_mLeft;
    				tOld_mLeft=0;
    			} else if (tOld_mRight < tOld_mLeft) {
    				tOld_mLeft -= tOld_mRight;
    				tOld_m += tOld_mRight;
    				tOld_mRight=0;
    			}
    				
    		isEnteringRegion ^= (tOld_m % 2 == 0);
    				
    		if (segmentAlongLine) { 
    			
    			newSegmentCoords.put(tOld,  new Coords[] {
		    			newCoords.get(tOld), 
		    			newCoords.get(tNew)
		    			});
    		} else {
    			if (isEnteringRegion)
    				newSegmentCoords.put(tOld,  new Coords[] {
    						newCoords.get(tOld), 
    						newCoords.get(tNew)
    						});
    			isEnteringRegion = !isEnteringRegion;
    		}
    		tOld = tNew;
    		coordsOld = newCoords.get(tOld);
    	}
 
       	if(!Kernel.isEqual(tOld, tLast)) {
    		int tOld_m = 0;
    		for (int i = 0; i<p.getPointsLength(); i++) {
    			GeoSegmentND currSeg = p.getSegments()[i];
    			if (currSeg.isOnPath(coordsOld, Kernel.EPSILON)) {
    				tOld_m++;
    			} else {
    				continue;
    			}
    		}
    		isEnteringRegion ^= (tOld_m % 2 == 0);
    			if (isEnteringRegion) //add the segment only if it is entering the region
    				newSegmentCoords.put(tOld,  new Coords[] {
    						newCoords.get(tOld), 
    						g.getPointInD(spaceDim, tLast)
    						});
    			isEnteringRegion = !isEnteringRegion;
    	}
    	
		
    
    }

    protected void compute() {
    	//TODO: optimize for plane/poly
    	calcLineInPlaneOfPolygon();
    	super.compute();
    }
    
	protected void calcLineInPlaneOfPolygon() {
		
    	lineInPlaneOfPolygon = (AlgoIntersectCS1D2D.getConfigLinePlane(g, p) == AlgoIntersectCS1D2D.RESULTCATEGORY_CONTAINED);
		
	}


	protected void setLabels(String[] labels) {

		if (!lineInPlaneOfPolygon) {
				outputPoints.setLabels(labels);			
		} else {/*
        //if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
			if (labels!=null &&
					labels.length==1 &&
					outputSegments.size() > 1 &&
					labels[0]!=null &&
					!labels[0].equals("")) {
        //	outputPoints.setIndexLabels(labels[0]);
          	outputSegments.setIndexLabels(labels[0]);
          	outputPoints.setLabels(new String[] {null});
        //if there are k>=2 labels, now for simplicity only the first two will be used.
          	//first for indexing points, second for indexing segments.
          	//  } else if (labels!=null &&
          	//		labels.length>=2 &&
          	//outputPoints.size() > 1 && outputSegments.size() > 1 &&
        	//	labels[0]!=null && labels[1]!=null &&
        		//!labels[0].equals("") && !labels[1].equals("")) {
        	//outputPoints.setIndexLabels(labels[0]);
        	//outputSegments.setIndexLabels(labels[1]);
        	} else {
        	
        	
        //	outputPoints.setIndexLabels(outputPoints.getElement(0).getLabel());
        	//if ( outputPoints.size()==0 || outputSegments.size()!=0) //when there is a point, an 'empty segment' is not needed.
        		outputSegments.setLabels(labels);
        		outputSegments.setIndexLabels(outputSegments.getElement(0).getLabel());
        		outputPoints.setLabels(new String[] {null});
        	}*/
			String labelPrefix = null;
			if (outputPoints.size()>0){
				outputPoints.setLabels(null);
				labelPrefix = outputPoints.getElement(0).getLabel().toLowerCase();
			}	
			
			if (labels!=null &&
					labels.length==1 &&
					outputSegments.size() > 1 &&
					labels[0]!=null &&
					!labels[0].equals("")) {
				labelPrefix = labels[0];
				outputSegments.setIndexLabels(labels[0]);
			}
	    	else
	    		outputSegments.setIndexLabels(labelPrefix);
		}
	}

    
	@Override
	public String getClassName() {
		return "AlgoIntersectLinePolygonalRegion3D";
	}
}
