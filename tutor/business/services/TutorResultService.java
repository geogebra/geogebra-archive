package tutor.business.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import tutor.business.TutorConstants;
import tutor.logs.TutorLog;
import tutor.model.TutorConstruction;
import tutor.model.TutorGraphResult;
import tutor.model.TutorResult;
import tutor.model.TutorResult.TutorResultComparator;

/**
 * <p>comparing the Construction being constructed (actual), with Construction strategies (different solutions)
 * and getting a formal result of this comparison</p>
 * @author AlbertV
 *
 */
public class TutorResultService implements TutorConstants {
	private final Logger logger = TutorLog.getLogger();
	private final Logger logger_graph = TutorLog.getLogger("graph");
	
	/********** Constructors */
	
	/********** Public Methods */
	
	/**
	 * @param tutor_construction_strategies
	 * @return
	 */
	public TutorResult getBestResult(final List tutor_construction_strategies, final TutorConstruction actual_construction) {
		TutorResult bestResult = new TutorResult();
		final List tutorResults = getResults(tutor_construction_strategies, actual_construction);
		
		// sorting results, the best one is the one with a numeric result closer to 1
		final float weight = 1 / NUM_DESCRIPTORS;
		Collections.sort(tutorResults, new TutorResultComparator(weight));
//		Collections.sort(tutorResults, new BeanComparator("score"));
//		Collections.sort(tutorResults, new BeanComparator("score", new TutorResultComparator(weight)));
		
		if(!tutorResults.isEmpty()) {
			bestResult = (TutorResult) tutorResults.get(0);
		}
		return bestResult;
	}
	
	/********** Private Methods */

	/**
	 * @param tutor_construction_strategies
	 * @param actual_construction 
	 * @return
	 */
	private List getResults(final List tutor_construction_strategies, TutorConstruction actual_construction) {
		// list of results for each comparison
		List results = new ArrayList();
		
		Iterator strategies = tutor_construction_strategies.iterator();
		while (strategies.hasNext()) {
			// strategy to compare
			TutorConstruction strategy = (TutorConstruction) strategies.next();
			// get comparison result
			TutorResult result = getResult(strategy, actual_construction);
			results.add(result);
			
			// just looking into the graph world
			Iterator iterator = result.getGraphResult().getCandidates().entrySet().iterator();
			while(iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				String label = (String) entry.getKey();
				List candidates = (List) entry.getValue();
				StringBuffer traca = new StringBuffer("actual construction vertex " + label + " has strategy candidates: ");
				for (int i = 0; i < candidates.size(); i++) {
					String candidate_label = (String) candidates.get(i);
					traca.append(candidate_label + ", ");
				}
				traca.append(" from " + result.getGgbPath());
				logger_graph.fine(traca.toString());
			}
			
			logger.fine(result.getGgbPath() + " :: result=" + result.getScore());
		}
		return results;
	}
	
	/**
	 * compare two TutorConstructions looking at their descriptors
	 * @param actual_construction
	 * @param strategy_construction
	 * @return the result of comparing the descriptors of both constructions
	 */
	private TutorResult getResult(final TutorConstruction strategy_construction, final TutorConstruction actual_construction) {
		TutorResult result = new TutorResult();
		
		final TutorComparatorService comparator = new TutorComparatorService(strategy_construction, actual_construction);
		 
		// compare number of lines
		Float linesScore = comparator.compareLines();
		result.setLinesScore(linesScore);
		
		// compare number of points
		Float resPoints = comparator.comparePoints();
		result.setPointsScore(resPoints);
		
		// compare number of angles
		Float resAngles = comparator.compareAngles();
		result.setAnglesScore(resAngles);
		
		// compare graphs
		TutorGraphResult graphResult= comparator.compareGraphs();
		result.setGraphResult(graphResult);
		
		//TODO: posar pesos i fer formula
		float score = ( linesScore.floatValue() + resPoints.floatValue() + resAngles.floatValue()) / NUM_DESCRIPTORS ;
		result.setScore(Float.valueOf(score));
		
		// path to this strategy file
		result.setGgbPath(strategy_construction.getGgbPath());
		
		return result;
	}
	
}
