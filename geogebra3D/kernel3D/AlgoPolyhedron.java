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
 * Creates a new GeoPolyhedron
 *
 */
public class AlgoPolyhedron extends AlgoElement3D
implements AlgoElementWithResizeableOutput{

	/** the polyhedron created */
	//protected GeoPolyhedron polyhedron;
	
	/** all points of the polyhedron (needed for the faces description) */
	private GeoPointND[] inputPoints;
	
	private GeoPolygon inputPolygon;
	
	private NumberValue inputHeight;
	
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
	
	private AlgoPolyhedron(Construction c, String[] labels){
		super(c);

		/*
		String s="";
		for(int i=0;i<labels.length;i++)
			s+=labels[i]+", ";
		Application.printStacktrace(s);
		 */


        //set labels dependencies: will be used with Construction.resolveLabelDependency()
        if (labels!=null && labels.length>1){
        	for (int i=0; i<labels.length; i++){       		
        		cons.setLabelDependsOn(labels[i], this);
        	}
        }

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
	public AlgoPolyhedron(Construction c, String[] labels, GeoPointND[] a_points, int type) {
		this(c,labels);
		
		
		
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

		case GeoPolyhedron.TYPE_PRISM://construct a prism
			inputPoints = a_points;
			numPoints = a_points.length - 1;
			GeoPointND[] points = new GeoPointND[numPoints*2];
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
		
		update();
		
		setInput();
		polyhedron.updateFaces();
		setOutput();
		
		
        
        
        polyhedron.defaultLabels(labels);
	}
	

	
	
	/** creates a polyhedron regarding basis and first vertex of second parallel face
	 * @param c construction 
	 * @param labels 
	 * @param polygon 
	 * @param point 
	 * @param type type of polyhedron
	 */
	public AlgoPolyhedron(Construction c, String[] labels, GeoPolygon polygon, GeoPointND point, int type) {
		this(c,labels);
		
		this.type = type;
		
		outputPolyhedron.adjustOutputSize(1);
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		inputPolygon = polygon;
		
		
		int numPoints;
		
		switch(type){
		/*
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
*/
		case GeoPolyhedron.TYPE_PRISM://construct a prism
			numPoints = polygon.getPointsLength();
			inputPoints = new GeoPointND[numPoints+1];
			for(int i=0;i<numPoints;i++)
				inputPoints[i] = polygon.getPointND(i);
			inputPoints[numPoints] = point;
			GeoPointND[] points = new GeoPointND[numPoints*2];
			outputPoints.adjustOutputSize(numPoints-1);
			outputPoints.setLabels(null);
			for(int i=0;i<numPoints+1;i++)
				points[i] = inputPoints[i];
			for(int i=0;i<numPoints-1;i++){
				points[numPoints+1+i] = outputPoints.getElement(i);
			}
			
			//bottom of the prism
			/*
			polyhedron.startNewFace();
			//for (int i=numPoints-1; i>=0; i--)
			for (int i=0; i<numPoints; i++)
				polyhedron.addPointToCurrentFace(points[i]);
			polyhedron.endCurrentFace();
			*/
			polyhedron.addPolygonLinked(polygon);
			
			
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
		
		update();
		
		setInput();
		polyhedron.updateFaces();
		setOutput();
		
		
        //set labels dependencies: will be used with Construction.resolveLabelDependency()
        if (labels!=null && labels.length>1)
        	for (int i=0; i<labels.length; i++){
        		cons.setLabelDependsOn(labels[i], this);
        	}
		
        
        
        polyhedron.defaultLabels(labels);
	}
	
	
	
	
	/** creates a right prism or pyramid regarding basis and height
	 * @param c construction 
	 * @param labels 
	 * @param polygon 
	 * @param height 
	 * @param type type of polyhedron
	 */
	public AlgoPolyhedron(Construction c, String[] labels, GeoPolygon polygon, NumberValue height, int type) {
		this(c,labels);
		
		this.type = type;
		
		outputPolyhedron.adjustOutputSize(1);
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		inputPolygon = polygon;
		inputHeight = height;
		
		int numPoints;
		
		switch(type){
		case GeoPolyhedron.TYPE_PRISM://construct a prism
			numPoints = polygon.getPointsLength();
			inputPoints = new GeoPointND[numPoints];
			for(int i=0;i<numPoints;i++)
				inputPoints[i] = polygon.getPointND(i);
			GeoPointND[] points = new GeoPointND[numPoints*2];
			outputPoints.adjustOutputSize(numPoints);
			outputPoints.setLabels(null);
			for(int i=0;i<numPoints;i++)
				points[i] = inputPoints[i];
			for(int i=0;i<numPoints;i++){
				points[numPoints+i] = outputPoints.getElement(i);
			}
			
			//bottom of the prism
			polyhedron.addPolygonLinked(polygon);
			
			
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
		
		
		update();
		
		setInput();
		polyhedron.updateFaces();
		setOutput();
		
		//sets the height as coord parent of the top face
		if (type == GeoPolyhedron.TYPE_PRISM && height instanceof GeoNumeric)
			getTopFace().setCoordParentNumber((GeoNumeric) height);
		
		
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
			return (GeoElement) outputPoints.addLabel(label);
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
		this(c,labels);
		this.type = GeoPolyhedron.TYPE_NONE;
		this.faces = faces;
		setFaces();

		//outputPoints = new GeoPoint3D[0];
		
		setInput();
		setOutput();
		
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		polyhedron.initLabels(labels);
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
			if (inputPolygon==null){
				input = new GeoElement[inputPoints.length];
				for (int i=0; i<inputPoints.length; i++)
					input[i] = (GeoElement) inputPoints[i];
			}else{
				input = new GeoElement[2];
				input[0] = inputPolygon;
				if (inputHeight==null)
					input[1] = (GeoElement) inputPoints[inputPoints.length-1];
				else
					input[1] = (GeoElement) inputHeight;
			}
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

	
	
	
	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedron getPolyhedron(){
		return outputPolyhedron.getElement(0);
	}
	
	
	
	
	
	
	
	
	
	

    

	
	
	protected void compute() {
		

		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		switch(type){
		case GeoPolyhedron.TYPE_PYRAMID:
			//TODO remove this and replace with tesselation
			Coords interiorPoint = new Coords(4);
			for (int i=0;i<inputPoints.length;i++){
				interiorPoint = (Coords) interiorPoint.add(inputPoints[i].getCoordsInD(3));
			}
			interiorPoint = (Coords) interiorPoint.mul((double) 1/(inputPoints.length));
			polyhedron.setInteriorPoint(interiorPoint);
			//Application.debug("interior\n"+interiorPoint);
			break;
		case GeoPolyhedron.TYPE_PRISM:
			//translation from bottom to top
			Coords v;
			int shift;
			if (inputHeight==null){
				v = inputPoints[inputPoints.length-1].getCoordsInD(3).sub(inputPoints[0].getCoordsInD(3));
				shift=1;
			}else{
				//TODO use Oz for default orientation
				v=inputPolygon.getMainDirection().normalized().mul(inputHeight.getDouble());
				shift=0;
			}
			
			//translate all output points
			for (int i=0;i<outputPoints.size();i++){
				outputPoints.getElement(i).setCoords(inputPoints[i+shift].getCoordsInD(3).add(v),true);
				//outputPoints.getElement(i).updateCoords();
				//outputPoints.getElement(i).update();
			}
			
			
			//polyhedron.updatePolygonsAndSegmentsFromParentAlgorithms();
			//polyhedron.update();			
			
			//TODO remove this and replace with tesselation
			interiorPoint = new Coords(4);
			for (int i=0;i<inputPoints.length-shift;i++){
				interiorPoint = (Coords) interiorPoint.add(inputPoints[i].getCoordsInD(3));
			}
			interiorPoint = (Coords) interiorPoint.mul((double) 1/(inputPoints.length-shift));
			polyhedron.setInteriorPoint((Coords) interiorPoint.add(v.mul(0.5)));
			
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
	
	
    public void update() {
    	
        // compute output from input
        compute();
        
        //polyhedron
        getPolyhedron().update();
        

        //output points
        for (int i=0; i<outputPoints.size(); i++)
			outputPoints.getElement(i).update();
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
	
	
	/**
	 * set visibility of output segments and polygons
	 * @param visible
	 */
	public void setOutputSegmentsAndPolygonsEuclidianVisible(boolean visible){
		for (int i=0; i<outputSegments.size(); i++)
			outputSegments.getElement(i).setEuclidianVisible(visible);
		for (int i=0; i<outputPolygons.size(); i++)
			outputPolygons.getElement(i).setEuclidianVisible(visible, false);
	}
	
	/**
	 * notify kernel update of output segments and polygons
	 */
	public void notifyUpdateOutputSegmentsAndPolygons(){
		for (int i=0; i<outputSegments.size(); i++)
			getKernel().notifyUpdate(outputSegments.getElement(i));
		for (int i=0; i<outputPolygons.size(); i++)
			getKernel().notifyUpdate(outputPolygons.getElement(i));
	}


	/**
	 * set output points invisible (use for previewable)
	 * @param visible 
	 */
	public void setOutputPointsEuclidianVisible(boolean visible){
		for (int i=0; i<outputPoints.size(); i++)
			outputPoints.getElement(i).setEuclidianVisible(visible);
	}
	
	
	/**
	 * notify kernel update of output points
	 */
	public void notifyUpdateOutputPoints(){
		for (int i=0; i<outputPoints.size(); i++)
			getKernel().notifyUpdate(outputPoints.getElement(i));
	}
	

	
	/**
	 * used for previewable of prism
	 * @return the middle point of the bottom face (for prism)
	 */
	public Coords getBottomMiddlePoint(){
		Coords ret = new Coords(4);
		
		int size;
		if (inputHeight==null)
			size=inputPoints.length-1;//use only input points of bottom face
		else
			size=inputPoints.length;		
		
		for (int i=0; i<size; i++)
			ret = ret.add(inputPoints[i].getCoordsInD(3));
		
		return ret.mul((double) 1/size);
	}

	/**
	 * used for previewable of prism
	 * @return the middle point of the top face (for prism)
	 */
	public Coords getTopMiddlePoint(){
		Coords ret = new Coords(4);
		for (int i=0; i<outputPoints.size(); i++)
			ret = ret.add(outputPoints.getElement(i).getCoordsInD(3));
		
		return ret.mul((double) 1/outputPoints.size());
	}
	
	
	public GeoPolygon getTopFace(){
		return outputPolygons.getElement(outputPolygons.size()-1);
		
	}
	
	public NumberValue getHeight(){
		return inputHeight;
	}
}
