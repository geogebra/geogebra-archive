package tutor.business.services;

import tutor.model.TutorConstruction;
import tutor.model.TutorGraphResult;

public class TutorComparatorService {
	TutorElementComparatorService elementComparator;
	TutorGraphComparatorService graphComparator;
	
	public TutorComparatorService(TutorConstruction strategy_construction, TutorConstruction actual_construction) {
		elementComparator = new TutorElementComparatorService(strategy_construction.getElement(), actual_construction.getElement());
		graphComparator = new TutorGraphComparatorService(strategy_construction.getGraph(), actual_construction.getGraph());
	}

	public Float compareLines() {
		return elementComparator.compareLines();
	}

	public Float comparePoints() {
		return elementComparator.comparePoints();
	}
	
	public Float compareAngles() {
		return elementComparator.compareAngles();
	}

	public TutorGraphResult compareGraphs() {
		return graphComparator.compareGraphs();
	}
}
