package geogebra3D.kernel3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.AlgoElementWithResizeableOutput;
import geogebra.kernel.AlgoSimpleRootsPolynomial;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.AlgoElement.OutputHandler;
import geogebra.kernel.AlgoElement.elementFactory;
import geogebra.main.Application;

import java.util.ArrayList;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoPolyhedron extends AlgoElement3D
implements AlgoElementWithResizeableOutput{

	/** the polyhedron created */
	//protected GeoPolyhedron polyhedron;
	
	/** all points of the polyhedron (needed for the faces description) */
	private GeoPoint3D[] inputPoints;
	
	/** lists describing the faces (for polyhedron of TYPE_NONE) */
	private GeoList faces = null;
	
	
	/** points generated as output (e.g. for prisms) */
	private OutputHandler<GeoPoint3D> outputPoints;
	
	private OutputHandler<GeoSegment3D> outputSegments;
	private OutputHandler<GeoPolygon3D> outputPolygons;
	
	private int type;

	private OutputHandler<GeoPolyhedron> outputPolyhedron;
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	////////////////////////////////////////////
	
	private AlgoPolyhedron(Construction c){
		super(c);
		//polyhedron = new GeoPolyhedron(c);

		outputPolyhedron=new OutputHandler<GeoPolyhedron>(new elementFactory<GeoPolyhedron>() {
			public GeoPolyhedron newElement() {
				GeoPolyhedron p=new GeoPolyhedron(cons);
				p.setParentAlgorithm(AlgoPolyhedron.this);
				return p;
			}
		});
		
		outputPoints=new OutputHandler<GeoPoint3D>(new elementFactory<GeoPoint3D>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				//GeoPoint3D p=(GeoPoint3D) kernel.getManager3D().Point3D(null, 0, 0, 0);//new GeoPoint3D(cons);
				p.setParentAlgorithm(AlgoPolyhedron.this);
				return p;
			}
		});
	
		

		outputPolygons=new OutputHandler<GeoPolygon3D>(new elementFactory<GeoPolygon3D>() {
			public GeoPolygon3D newElement() {
				GeoPolygon3D p=new GeoPolygon3D(cons);
				//p.setParentAlgorithm(AlgoPolyhedron.this);
				return p;
			}
		});
		
		
		
		outputSegments=new OutputHandler<GeoSegment3D>(new elementFactory<GeoSegment3D>() {
			public GeoSegment3D newElement() {
				GeoSegment3D s=new GeoSegment3D(cons);
				//s.setParentAlgorithm(AlgoPolyhedron.this);
				return s;
			}
		});

		
		
	}
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param label name
	 * @param points vertices
	 * @param type type of polyhedron
	 */
	/*
	public AlgoPolyhedron(Construction c, String label, GeoPoint3D[] points, int type) {
		this(c,points,type);
		polyhedron.setLabel(label);
	}
	*/
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param a_points vertices
	 * @param type type of polyhedron
	 */
	public AlgoPolyhedron(Construction c, String[] labels, GeoPoint3D[] a_points, int type) {
		this(c);
		
		this.type = type;
		
		outputPolyhedron.adjustOutputSize(1);
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		
		int numPoints;
		
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID://construct a pyramid with last point as apex
			this.inputPoints = a_points;
			numPoints = inputPoints.length;
			
			
			//base of the pyramid
			polyhedron.startNewFace();
			//for (int i=numPoints-2; i>=0; i--)
			for (int i=0; i<numPoints-1; i++)
				polyhedron.addPointToCurrentFace(this.inputPoints[i]);
			polyhedron.endCurrentFace();
			
			//sides of the pyramid
			for (int i=0; i<numPoints-1; i++){
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(this.inputPoints[i]);
				polyhedron.addPointToCurrentFace(this.inputPoints[(i+1)%(numPoints-1)]);
				polyhedron.addPointToCurrentFace(this.inputPoints[numPoints-1]);//apex
				polyhedron.endCurrentFace();
			}
			

			
			break;
			
			/*
		case GeoPolyhedron.TYPE_PSEUDO_PRISM://construct a "pseudo-prismatic" polyhedron
			this.points = a_points;
			outputPointsIndex = a_points.length;
			numPoints = points.length /2;
			faces = new int[numPoints+2][];
			for (int i=0; i<numPoints; i++){
				faces[i]=new int[4];
				faces[i][0]=i;
				faces[i][1]=(i+1)%(numPoints);
				faces[i][2]=numPoints + ((i+1)%(numPoints));
				faces[i][3]=numPoints + i;
			}
			faces[numPoints]=new int[numPoints];
			for (int i=0; i<numPoints; i++)
				faces[numPoints][i]=numPoints-i-1;
			faces[numPoints+1]=new int[numPoints];
			for (int i=0; i<numPoints; i++)
				faces[numPoints+1][i]=numPoints+i;
			break;
			*/

		case GeoPolyhedron.TYPE_PRISM://construct a prism
			inputPoints = a_points;
			numPoints = a_points.length - 1;
			GeoPoint3D[] points = new GeoPoint3D[numPoints*2];
			outputPoints.adjustOutputSize(numPoints-1);
			outputPoints.setLabels(null);
			for(int i=0;i<numPoints+1;i++)
				points[i] = a_points[i];
			for(int i=0;i<numPoints-1;i++){
				//outputPoints[i] = (GeoPoint3D) kernel.getManager3D().Point3D(null, 0, 0, 0);
				points[numPoints+1+i] = outputPoints.getElement(i);
			}
			
			//bottom of the prism
			polyhedron.startNewFace();
			//for (int i=numPoints-1; i>=0; i--)
			for (int i=0; i<numPoints; i++)
				polyhedron.addPointToCurrentFace(points[i]);
			polyhedron.endCurrentFace();
			
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
			
			break;
		}
		
		setInput();
		polyhedron.updateFaces();
		setOutput();
		
		//compute();

		update();
		
        //set labels dependencies: will be used with Construction.resolveLabelDependency()
        if (labels!=null && labels.length>1)
        	for (int i=0; i<labels.length; i++){
        		cons.setLabelDependsOn(labels[i], this);
        	}
		
        
        
        polyhedron.defaultLabels(labels);
	}
	


	public GeoElement addLabelToOutput(String label, int type){

		switch(type){
		case GeoElement.GEO_CLASS_POLYHEDRON:
			return outputPolyhedron.addLabel(label);
		case GeoElement.GEO_CLASS_POINT3D:
			return outputPoints.addLabel(label);
		case GeoElement.GEO_CLASS_POLYGON3D:
			return outputPolygons.addLabel(label);
		case GeoElement.GEO_CLASS_SEGMENT3D:
			return outputSegments.addLabel(label);
		default:
			return null;
		}

	}

	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF TYPE NONE
	////////////////////////////////////////////

	
	/** creates a polyhedron regarding faces description
	 * @param c construction 
	 * @param label name
	 * @param faces faces description
	 */
	public AlgoPolyhedron(Construction c, String[] labels, GeoList faces) {
		this(c,faces);

		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		polyhedron.initLabels(labels);
	}
	
	/** creates a polyhedron regarding vertices and faces description
	 * @param c construction 
	 * @param faces faces description
	 */
	public AlgoPolyhedron(Construction c, GeoList faces) {
		this(c);
		this.type = GeoPolyhedron.TYPE_NONE;
		this.faces = faces;
		setFaces();

		//outputPoints = new GeoPoint3D[0];
		
		setInput();
		setOutput();
		
	}
	
	
	/** send GeoList description of faces to polyhedron
	 * and update polyhedron polygons and segments
	 * 
	 */
	private void setFaces(){
		

		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		for(int i=0;i<faces.size();i++){ 
			polyhedron.startNewFace();
			GeoList list = (GeoList) faces.get(i);
			for (int j=0;j<list.size();j++){
				GeoPoint3D point = (GeoPoint3D) list.get(j);
				polyhedron.addPointToCurrentFace(point);
	
			}
			polyhedron.endCurrentFace();
		}
		
		polyhedron.updateFaces();

		
	}

	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	protected void setInput(){
		
		// input : inputPoints or list of faces
		if(this.faces == null){
			input = inputPoints;
		}else{
			input = new GeoElement[1];
			input[0] = this.faces;
		}
		
		for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }
	}

		
	protected void setOutput(){
		
		updateOutput();
        cons.addToAlgorithmList(this);  
		
	}
	
	
	
	
	
	private void updateOutput(){
		
		//add polyhedron's segments and polygons, without setting this algo as algoparent
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		outputPolygons.addOutput(polyhedron.getFaces(),false);
		outputSegments.addOutput(polyhedron.getSegments(),false);
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

    

	
	
	protected void compute() {
		

		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID:
			//TODO remove this and replace with tesselation
			GgbVector interiorPoint = new GgbVector(4);
			for (int i=0;i<inputPoints.length;i++){
				interiorPoint = (GgbVector) interiorPoint.add(inputPoints[i].getCoords());
			}
			interiorPoint = (GgbVector) interiorPoint.mul((double) 1/(inputPoints.length));
			polyhedron.setInteriorPoint(interiorPoint);
			//Application.debug("interior\n"+interiorPoint);
			break;
		case GeoPolyhedron.TYPE_PRISM:
			//translation from bottom to top
			GgbVector v = inputPoints[inputPoints.length-1].getCoords().sub(inputPoints[0].getCoords());
			//Application.debug("v=\n"+v);
			//translate all output points
			for (int i=0;i<outputPoints.size();i++){
				outputPoints.getElement(i).setCoords(inputPoints[i+1].getCoords().add(v));
				outputPoints.getElement(i).updateCoords();
			}
			
			
			polyhedron.updatePolygonsAndSegmentsFromParentAlgorithms();
			//polyhedron.update();			
			
			//TODO remove this and replace with tesselation
			interiorPoint = new GgbVector(4);
			for (int i=0;i<inputPoints.length-1;i++){
				interiorPoint = (GgbVector) interiorPoint.add(inputPoints[i].getCoords());
			}
			interiorPoint = (GgbVector) interiorPoint.mul((double) 1/(inputPoints.length-1));
			polyhedron.setInteriorPoint((GgbVector) interiorPoint.add(v.mul(0.5)));
			
			break;
		case GeoPolyhedron.TYPE_NONE:
			//Application.printStacktrace("compute");
			polyhedron.restartFaces();
			setFaces();	
			updateOutput();
			break;
		default:
		}

	}



	public String getClassName() {
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID:
			return "AlgoPyramid";
		case GeoPolyhedron.TYPE_PRISM:
			return "AlgoPrism";
		default:
			return "AlgoPolyhedron";
		}
	}
}
