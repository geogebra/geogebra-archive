package tutor.business.services;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;

import java.util.logging.Logger;

import tutor.business.TutorConstants;
import tutor.logs.TutorLog;
import tutor.model.TutorElement;

/**
 * add or remove elements from a TutorConstruction, depending on the kind of element it is
 * here we implement the real business logic of tutor system
 * @author AlbertV
 *
 */
public class TutorElementService implements TutorConstants {
	private static final Logger logger = TutorLog.getLogger();
	
	/********** Constructors */
	
	/********** Public Methods */
	
	/**
	 * add or remove elements from a TutorConstruction, depending on the kind of element it is
	 * here we implement the real business logic of tutor system
	 * @param element a ConstructionElement
	 * @param tutorElement
	 * @param operation to add or to remove
	 */
	public static void operate(final ConstructionElement element, TutorElement tutorElement, final int operation) {
	
//		//just looking more properties
//		int type = element.getGeoClassType();
//		String object_type = element.getObjectType();
//		String label = element.getLabel();
		
//		logger.fine("type = " + type);
//		logger.fine("objectType = " + object_type);		
//		logger.fine("label = " + label);

		// operating a point
		if (element instanceof GeoPoint) {
			logger.fine("it's a " + element.getClass().getSimpleName() +
					" with label " + ((GeoElement)element).getLabel());
			operateGeoPoint(tutorElement, operation);
		}
		// operating a line
		else if (element instanceof GeoLine) {
			logger.fine("it's a " + element.getClass().getSimpleName() + 
					" with label " + ((GeoElement)element).getLabel());
			operateGeoLine(tutorElement, operation);
		}
		// operating a GeoAngle
		else if (element instanceof GeoAngle) {
			logger.fine("It's a " + element.getClass().getSimpleName() + 
					" with label " + ((GeoElement)element).getLabel());
			operateGeoAngle((GeoAngle) element, tutorElement, operation);
		}
		// operating a GeoPolygon
		else if (element instanceof GeoPolygon) {
			logger.fine("It's a " + element.getClass().getSimpleName() +
					" with " + ((GeoPolygon)element).getPoints().length + " ponits" +
					" with label " + ((GeoElement)element).getLabel());
			operateGeoPolygon((GeoPolygon) element, tutorElement, operation);
		}
		// operating an AlgoElement
		else if (element instanceof AlgoElement) {
			logger.fine("It's an " + element.getClass().getSimpleName() + 
					" with " + element.getGeoElements().length + " elements");
			operateAlgoElement((AlgoElement) element, tutorElement, operation);
		}
		else {
			logger.fine("it's Nothing Known! ... it's a " + element.getClass().getSimpleName());
		}
	}

	/********** Private Methods */
	
	/**
	 * @param element
	 * @param tutorElement
	 * @param operation
	 */
	private static void operateAlgoElement(final AlgoElement element, TutorElement tutorElement, final int operation) {
		GeoElement[] geoElements = element.getGeoElements();
		for(int i=0; i<geoElements.length; i++) {
			GeoElement geoElement = geoElements[i];
			// operate every GeoElement inside this AlgoElement
			operate(geoElement, tutorElement, operation);
		}
	}

	/**
	 * @param element
	 * @param tutorElement
	 * @param operation
	 */
	private static void operateGeoPolygon(final GeoPolygon element, TutorElement tutorElement, final int operation) {
		// points of this polygon
		GeoPoint[] geoPoints = element.getPoints();

		for(int i=0; i<geoPoints.length; i++) {
			GeoPoint geoPoint = geoPoints[i];
			//TODO: quan un GeoPoint no es independent??
			if(geoPoint.isIndependent()) {
				// DON'T operate GeoPoint inside this GeoPolygon, because they are already included
				// as single GeoPoints in the Construction, so it would duplicate the number of total points 
				logger.fine("GeoPoint " + geoPoint.getLabel() + " is independent!!");
				continue;
			}
			// operate every dependent GeoPoint inside this GeoPolygon
			operate(geoPoint, tutorElement, operation);
		}
	}

	/**
	 * @param element
	 * @param tutorElement
	 * @param operation
	 */
	private static void operateGeoAngle(final GeoAngle element, TutorElement tutorElement, final int operation) {
		double rawAngle = element.getRawAngle();
		switch(operation) {
		case ADD:
			if(rawAngle > Math.PI) {
				tutorElement.addAngleGt90();
			}
			else if (rawAngle < Math.PI) {
				tutorElement.addAngleLt90();
			}
			else {
				tutorElement.addAngleEq90();
			}
			break;
		case REMOVE:
			if(rawAngle > Math.PI) {
				tutorElement.removeAngleGt90();
			}
			else if (rawAngle < Math.PI) {
				tutorElement.removeAngleLt90();
			}
			else {
				tutorElement.removeAngleEq90();
			}
			break;
		}
	}

	/**
	 * @param tutorElement
	 * @param operation
	 */
	private static void operateGeoLine(TutorElement tutorElement, final int operation) {
		switch(operation) {
			case ADD: 
				tutorElement.addLine();
				break;
			case REMOVE:
				tutorElement.removeLine();
				break;
		}
		
//			// just looking more properties
//			String start_point = ((GeoLine) element).getStartPoint().getLabel();
//			String end_point = ((GeoLine) element).getEndPoint().getLabel();
//			
//			logger.fine("start_point = " + start_point);
//			logger.fine("end_point = " + end_point);
	}

	/**
	 * @param tutorElement
	 * @param operation
	 */
	private static void operateGeoPoint(TutorElement tutorElement, final int operation) {
		switch(operation) {
			case ADD: 
				tutorElement.addPoint();
				break;
			case REMOVE:
				tutorElement.removePoint();
				break;
		}
	}

}
