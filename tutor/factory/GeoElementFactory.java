package tutor.factory;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;

/**
 * <p>creating different types of Constructions</p>
 * @author AlbertV
 */
public class GeoElementFactory {
	
	/**
	 * only for test purposes
	 * @return
	 */
	public static Construction createSquareForTest(Construction construction) {
		construction.clearConstruction();
		
		GeoPoint A = (GeoPoint) createPoint("A", construction);
		GeoPoint B = (GeoPoint) createPoint("B", construction);
		GeoPoint C = (GeoPoint) createPoint("C", construction);
		GeoPoint D = (GeoPoint) createPoint("D", construction);
		
		createLine("a", A, B, construction);
		createLine("b", B, C, construction);
		createLine("c", C, D, construction);
		createLine("d", D, A, construction);
		
		return construction;
	}
	
	/**
	 * only for test purposes
	 * @return
	 */
	public static Construction createTriangleForTest(Construction construction) {
		construction.clearConstruction();
		
		GeoPoint A = (GeoPoint) createPoint("A", construction);
		GeoPoint B = (GeoPoint) createPoint("B", construction);
		GeoPoint C = (GeoPoint) createPoint("C", construction);
		
		createLine("a", A, B, construction);
		createLine("b", B, C, construction);
		createLine("c", C, A, construction);
		
		return construction;
	}

	/**<
	 * 
	 * @param newLabel of new point
	 * @param construction 
	 * @return a geopoint
	 */
	private static GeoElement createPoint(final String newLabel, Construction construction) {
		// a point
		GeoElement point = new GeoPoint(construction);
		point.setLabel(newLabel);
		
		return point;
	}
	
	/**
	 * 
	 * @param label of line
	 * @param P start point?
	 * @param Q end point?
	 * @return a geoline
	 */
	private static GeoElement createLine(final String label, final GeoPoint P, final GeoPoint Q, Construction construction) {
		// a line
//		AlgoElement algoJoinPoints = new AlgoJoinPoints(construction, label, P, Q);

//		GeoElement line  = new GeoLine(construction, label, 1, 22, 3);
//		line.setLabel(label);
		
		GeoElement line  = new GeoLine(construction);
		line.setLabel(label);

		
		GeoElement line2  = construction.getKernel().Line(label, P, Q);
		
		line.set(line2);
		
//		construction.addToConstructionList(line, false);
		
		return line;
	}
	
}
