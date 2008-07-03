package tutor.business.services;

import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;
import tutor.business.TutorConstants;
import tutor.model.TutorConstruction;
import tutor.model.TutorElement;
import tutor.model.TutorGraph;

/**
 * <p>transforming/translating Construction entities to Tutor purposes</p>
 * @author AlbertV
 *
 */
public class TutorConstructionService implements TutorConstants {
	
	/********** Constructors */
	
	/********** Public Methods */
	
	/**
	 * create a TutorConstruction from a given Construction
	 * @param geo
	 */
	public TutorConstruction getTutorConstruction(final Construction construction) {
		
		TutorConstruction tutorConstruction = new TutorConstruction();
		// we want to know how many lines and points this construction has
		TutorElement tutorElement = new TutorElement();
		// we want to know graph dependencies of this construction
		TutorGraph tutorGraph = new TutorGraph();
		
		// in order of construction
//		Iterator construction_it = construction.getGeoSetConstructionOrder().iterator();
//		while(construction_it.hasNext()) {
//			Object object = construction_it.next();
		// 22/4/8 --> en el getGeoSetConstructionOrder no hi ha inclosos els AlgoElements
		for(int i=0; i<construction.steps(); i++) {
			// look for every element in this construction which kind of element is
			ConstructionElement element = construction.getConstructionElement(i);
			// filling our TutorConstruction
			TutorElementService.operate(element, tutorElement, ADD);
			// filling our TutorGraph
			TutorGraphService.operate(element, tutorGraph);
		}
		
		tutorConstruction.setElement(tutorElement);
		tutorConstruction.setGraph(tutorGraph);
		
		return tutorConstruction;
	}
	
	/**
	 * add a new element to this TutorConstrucion
	 * and calculate graph dependencies
	 * @param element
	 * @param tutorConstruction
	 */
	public void addConstructionElement(final ConstructionElement element, TutorConstruction tutorConstruction) {
		
		TutorElement tutorElement = tutorConstruction.getElement();
		TutorElementService.operate(element, tutorElement, ADD);
		tutorConstruction.setElement(tutorElement);
		
		TutorGraph tutorGraph = tutorConstruction.getGraph();
		TutorGraphService.operate(element, tutorGraph);
		tutorConstruction.setGraph(tutorGraph);
	}

	/**
	 * remove an element from this TutorConstrucion
	 * and calculate graph dependencies
	 * @param element
	 * @param tutorConstruction
	 */
	public void removeConstructionElement(final ConstructionElement element, TutorConstruction tutorConstruction) {

		TutorElement tutorElement = tutorConstruction.getElement();
		TutorElementService.operate(element, tutorElement, REMOVE);
		tutorConstruction.setElement(tutorElement);
		
		TutorGraph tutorGraph = tutorConstruction.getGraph();
		TutorGraphService.operate(element, tutorGraph);
		tutorConstruction.setGraph(tutorGraph);
	}

	/********** Private Methods */
	
}
