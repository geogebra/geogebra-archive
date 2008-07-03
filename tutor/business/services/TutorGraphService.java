package tutor.business.services;

import geogebra.kernel.AlgoAngleLines;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoIntersectLines;
import geogebra.kernel.AlgoJoinPoints;
import geogebra.kernel.AlgoJoinPointsSegment;
import geogebra.kernel.AlgoMidpoint;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;

import java.util.logging.Logger;

import tutor.logs.TutorLog;
import tutor.model.TutorGraph;
import tutor.model.TutorVertex;

/**
 * add or remove elements from a TutorGraph, depending on the kind of element it is
 * here we implement the real business logic of tutor system
 * @author AlbertV
 *
 */
public class TutorGraphService {
	private static final Logger logger = TutorLog.getLogger("graph");
	
	/********** Constructors */
	
	/********** Public Methods */
	
	/**
	 * update the graph adjacencies of a construction
	 * 
	 * realment en l'event add jo no se lalgorimte que s'ha creat, nomes se l'element que estic afegint, per tant: 
	 * opcio a) o miro l'element i busco en quins algoritmes esta, 
	 * opcio b) o simplement em recorro tots els algoritmes de la construccio, i anar sumant adjacencies element per element si trobo el seu algoritme pare
	 * son compatibles les dues opcions?
	 */
	public static void operate(final ConstructionElement element, TutorGraph graph) {
		logger.fine("************ Graph :: it's a " + element.getClass().getSimpleName() + " ************");
		
		// opcio b) 
		// 19/5/8 --> R&D which ParentAlgorithm this element has? ... mostly none!!?? only at the end o a command?
		if(element instanceof GeoElement) {
			if(((GeoElement)element).getParentAlgorithm() != null) {
				logger.fine("Graph :: has ParentAlgorithm --> " + ((GeoElement)element).getParentAlgorithm().getClass().getSimpleName());				
				operateParentAlgorithm(((GeoElement)element).getParentAlgorithm(), graph);
			}
			else {
				logger.fine("Graph :: has NOT ParentAlgorithm, it's independent!!");
			}
		} else if (element instanceof AlgoElement) {
			operateParentAlgorithm((AlgoElement) element, graph);
			
		} else {

		}
	}

	/**
	 * @param element
	 */
	private static void operateParentAlgorithm(final AlgoElement element, TutorGraph graph) {
		// operating a AlgoIntersectLines
		if (element instanceof AlgoIntersectLines) {
			// 26/5/8 --> DE MOMENT NOMES MIREM EL NUMERO D'ADJACENCIES
			operateAlgoIntersectLines((AlgoIntersectLines) element, graph);
		}
		// operating a AlgoJoinPoints
		else if (element instanceof AlgoJoinPoints) {
			// 26/5/8 --> DE MOMENT NOMES MIREM EL NUMERO D'ADJACENCIES			
			operateAlgoJoinPoints((AlgoJoinPoints) element, graph);
		}
		// operating a AlgoAngleLines
		else if (element instanceof AlgoAngleLines) {
			// 26/5/8 --> DE MOMENT NOMES ENS CENTREM EN INTERSECTIONS I JOINPOINTS
			operateAlgoAngleLines((AlgoAngleLines) element, graph);
		}
		// operating a AlgoMidpoint
		else if (element instanceof AlgoMidpoint) {
			// 26/5/8 --> DE MOMENT NOMES ENS CENTREM EN INTERSECTIONS I JOINPOINTS
			operateAlgoMidPoint((AlgoMidpoint) element, graph);
		}
		// operating a AlgoJoinPointsSegment --> polygon
		else if (element instanceof AlgoJoinPointsSegment) {
			// 26/5/8 --> DE MOMENT NOMES ENS CENTREM EN INTERSECTIONS I JOINPOINTS
			operateAlgoJoinPointsSegment((AlgoJoinPointsSegment) element, graph);
		}
		
		if (isGenericAlgoPoints(element)) {
			// FASE EXPERIMENTAL A TOPE
			operateGenericAlgoPoints((AlgoElement) element, graph);
		}
	}
	
	/********** Private Methods */

	/**
	 * TODO
	 * @param element
	 * @param graph 
	 */
	private static void operateGenericAlgoPoints(final AlgoElement element, TutorGraph graph) {
		GeoElement geoElement1 = element.getInput()[0];
		if (geoElement1 instanceof GeoPoint) {
			GeoPoint point1 = (GeoPoint) geoElement1;
		}
		logger.fine("Graph :: isAlgoPoints geoElement1 it's a " + geoElement1.getClass().getSimpleName());
		
		GeoElement geoElement2 = element.getInput()[1];
		if (geoElement2 instanceof GeoPoint) {
			GeoPoint point2 = (GeoPoint) geoElement2;
		}
		logger.fine("Graph :: isAlgoPoints geoElement2 it's a " + geoElement2.getClass().getSimpleName());
		
		// el punt resultany
		GeoElement geoElement3 = element.getOutput()[0];
		logger.fine("Graph :: isAlgoPoints geoElement3 it's a " + geoElement3.getClass().getSimpleName());
	}
	
	/**
	 * TODO: AlgoJoinPointsSegment hauria de ser subclasse dde AlgoJoinPoints ??!!!
	 * polygons
	 * GeoPoint P, Q; // input
	 * GeoSegment s; // output: GeoSegment subclasses GeoLine 
	 * @param graph 
	 */
	private static void operateAlgoJoinPointsSegment(AlgoJoinPointsSegment element, TutorGraph graph) {
		// inputs
		GeoPoint point1 = (GeoPoint) element.getInput()[0];
		GeoPoint point2 = (GeoPoint) element.getInput()[1];

		// translate to graph world
		TutorVertex vertex1 = getVertexFromPoint(graph, point1);
		TutorVertex vertex2 = getVertexFromPoint(graph, point2);

		// add vertices with adjacencies
		addVertexWithAdjacencies(graph, vertex1, vertex2);
		addVertexWithAdjacencies(graph, vertex2, vertex1);
		
		// output
		GeoLine line = (GeoLine) element.getOutput()[0];
		
	}

	/**
	 * TODO
	 * @param element
	 * @param graph 
	 */
	private static void operateAlgoMidPoint(final AlgoMidpoint element, TutorGraph graph) {
		// els 2 punts d'origen
		GeoPoint point1 = (GeoPoint) element.getInput()[0];
		GeoPoint point2 = (GeoPoint) element.getInput()[1];
		
		// el punt resultany
		GeoPoint point3 = (GeoPoint) element.getOutput()[0];
	}

	/**
	 * TODO
	 * @param element
	 * @param graph 
	 */
	private static void operateAlgoAngleLines(final AlgoAngleLines element, TutorGraph graph) {
		// les dues linies d'origen
		GeoLine line1 = (GeoLine) element.getInput()[0];
		GeoLine line2 = (GeoLine) element.getInput()[1];
		
		// el punt resultany
		GeoAngle angle = (GeoAngle) element.getOutput()[0];
	}

	/**
	 * this algorithm has two input points and one output line
	 * @param element
	 * @param graph
	 */
	private static void operateAlgoJoinPoints(final AlgoJoinPoints element, TutorGraph graph) {
		
		// inputs
		GeoPoint point1 = (GeoPoint) element.getInput()[0];
		GeoPoint point2 = (GeoPoint) element.getInput()[1];

		// translate to graph world
		TutorVertex vertex1 = getVertexFromPoint(graph, point1);
		TutorVertex vertex2 = getVertexFromPoint(graph, point2);

		// add vertices with adjacencies
		addVertexWithAdjacencies(graph, vertex1, vertex2);
		addVertexWithAdjacencies(graph, vertex2, vertex1);
		
		// output
		GeoLine line = (GeoLine) element.getOutput()[0];
	}

	/**
	 * this algorithm has to input lines and one output point
	 * @param element
	 * @param graph
	 */
	private static void operateAlgoIntersectLines(final AlgoIntersectLines element, TutorGraph graph) {
		// first input line, and 2 resulting vertices
		GeoLine line1 = (GeoLine) element.getInput()[0];
		TutorVertex[] vertices1 = getVerticesFromLine(graph, line1);
		
		// second input line, and 2 resulting vertices
		GeoLine line2 = (GeoLine) element.getInput()[1];
		TutorVertex[] vertices2 = getVerticesFromLine(graph, line2);
		
		//TODO: el punt d'interseccio no es un punt de veritat, si h fos s'hauria de recalcular tota la construccio
		// sencera, i generar els algoritmes nous...un jaleeo!!
		// resulting point and resulting vertex
		GeoPoint point = (GeoPoint) element.getOutput()[0];
		TutorVertex intersection_vertex = getVertexFromPoint(graph, point);
		
		// all the adjacent vertices for this intersection point
		addVertexWithAdjacencies(graph, intersection_vertex, vertices1[0]);
		addVertexWithAdjacencies(graph, intersection_vertex, vertices1[1]);
		addVertexWithAdjacencies(graph, intersection_vertex, vertices2[0]);
		addVertexWithAdjacencies(graph, intersection_vertex, vertices2[1]);
	}
	
	/**
	 * one line has two points, and each point is a vertex
	 * @param graph
	 * @param line
	 * @return
	 */
	private static TutorVertex[] getVerticesFromLine(TutorGraph graph, GeoLine line) {
		
		GeoPoint startPoint1 = line.getStartPoint();
		TutorVertex vertex1 = getVertexFromPoint(graph, startPoint1);
		
		GeoPoint endPoint1 = line.getEndPoint();
		TutorVertex vertex2 = getVertexFromPoint(graph, endPoint1);
		
		final TutorVertex[] vertices = {vertex1, vertex2};
		return vertices;
	}
	
	/**
	 * 
	 * @param graph
	 * @param point
	 * @return
	 */
	private static TutorVertex getVertexFromPoint(TutorGraph graph, GeoPoint point) {
		return graph.getOrCreateVertexByLabel(point.getLabel());
	}
	
	/**
	 * add an adjacent vertex to the given vertex and 
	 * add a new vertex to the graph
	 * @param graph
	 * @param vertex_to_add
	 * @param adjacent_vertex
	 */
	private static void addVertexWithAdjacencies(TutorGraph graph, TutorVertex vertex_to_add, TutorVertex adjacent_vertex) {
		
		// add an adjacent vertex to the given point
		vertex_to_add.addAdjacentVertex(adjacent_vertex);
		
		// add a new vertex to the graph
		graph.addTutorVertex(vertex_to_add);
		
	}
	
	/**
	 * R&D
	 * like if AlgoPoints was an abstract class representing all classes with Points in it
	 */
	private static boolean isGenericAlgoPoints(ConstructionElement element) {
		boolean itIs = false;
		if (element.getClass().getName().indexOf("Points") != -1) {
			itIs = true;
		}
		return itIs;
	}
	
}
