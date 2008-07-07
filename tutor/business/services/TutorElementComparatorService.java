package tutor.business.services;

import java.math.BigDecimal;

import tutor.model.TutorElement;


class TutorElementComparatorService {

	private TutorElement strategyElement;
	private TutorElement actualElement;

	protected TutorElementComparatorService(TutorElement strategyElement, TutorElement actualElement) {
		this.strategyElement = strategyElement;
		this.actualElement = actualElement;
	}

	public Float compareLines() {
		return compareLines(strategyElement.getNumLines(), actualElement.getNumLines());
	}

	public Float comparePoints() {
		return comparePoints(strategyElement.getNumPoints(), actualElement.getNumPoints());
	}
	
	public Float compareAngles() {
		return compareAngles(strategyElement, actualElement);
	}
	/**
	 * @param actual_lines
	 * @param strategy_lines
	 * @return a quantification of its similarity
	 */
	private static Float compareLines(final int strategy_lines, final int actual_lines) {
		return divide(strategy_lines, actual_lines);
	}
	
	/**
	 * @param actual_points
	 * @param strategy_points
	 * @return a quantification of it similarity
	 */
	private static Float comparePoints(final int strategy_points, final int actual_points) {
		return divide(strategy_points, actual_points);
	}
	
	/**
	 * @param actual
	 * @param strategy
	 * @return a quantification of it similarity
	 */
	private static Float compareAngles(final TutorElement tutor_element, final TutorElement actual_element) {
		// compare number of anglesEq90
		Float resAnglesEq90 = divide(tutor_element.getNumAnglesEq90(), actual_element.getNumAnglesEq90());
		// compare number of anglesEq90
		Float resAnglesGt90 = divide(tutor_element.getNumAnglesGt90(), actual_element.getNumAnglesGt90());
		// compare number of anglesEq90
		Float resAnglesLt90 = divide(tutor_element.getNumAnglesGt90(), actual_element.getNumAnglesLt90());

		//TODO: posar pesos i fer formula
		// en aquest cas es fa la mitja entre 3? cada angle val igual, no?
		float res = ( resAnglesEq90.floatValue() + resAnglesGt90.floatValue() + resAnglesLt90.floatValue()) / 3 ;
		
		return new Float(res); 
	}
	
	/**
	 * @param actual_descriptor
	 * @param strategy_descriptor
	 * @return the division of these values
	 */
	private static Float divide(final int strategy_descriptor, final int actual_descriptor) {
		//TODO: investigar pq el primer dia ho vaig solucionar amb el DECIMAL32, i al dia seguent vaig haver de fer aixo
		if(strategy_descriptor == 0) {
			return new Float(0); 
		}
		
		BigDecimal strategy = new BigDecimal(strategy_descriptor);
		BigDecimal actual = new BigDecimal(actual_descriptor);
		return new Float(actual.divide(strategy, BigDecimal.ROUND_CEILING).floatValue());
	}

}
