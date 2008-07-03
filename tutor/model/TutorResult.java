package tutor.model;

import java.util.Comparator;

public class TutorResult {
	private Float linesScore;
	private Float pointsScore;
	private Float anglesScore;
	/** graph adjacencies */
	private TutorGraphResult graphResult;
	/** total totalorum */
	private Float score;
	
	//TODO: think smthg better to deal with that
	/** path to the strategy file */
	private String ggbPath;
	
	// Lines
	public Float getLinesScore() {
		return linesScore;
	}
	public void setLinesScore(Float linesScore) {
		this.linesScore = linesScore;
	}
	
	// Points
	public Float getPointsScore() {
		return pointsScore;
	}
	public void setPointsScore(Float pointsScore) {
		this.pointsScore = pointsScore;
	}
	
	// Angles
	public Float getAnglesScore() {
		return anglesScore;
	}
	public void setAnglesScore(Float anglesScore) {
		this.anglesScore = anglesScore;
	}
	
	// Adjacencies
	public TutorGraphResult getGraphResult() {
		return graphResult;
	}
	public void setGraphResult(TutorGraphResult graphResult) {
		this.graphResult = graphResult;
	}
	
	// The Result
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}
	
	// GGB File Path
	public String getGgbPath() {
		return ggbPath;
	}
	public void setGgbPath(String ggbPath) {
		this.ggbPath = ggbPath;
	}

	/**
	 * the numeric result closer to "number 1" is the best one
	 * i.e. the strategy that resembles more the actual construction
	 */
	public static class TutorResultComparator implements Comparator {

		float weight;
		public TutorResultComparator(float weight) {
			this.weight = weight;
		}
		
		public int compare(Object arg0, Object arg1) {
			TutorResult tutorResult0 = (TutorResult) arg0;
			TutorResult tutorResult1 = (TutorResult) arg1;
			
			float score0 = weight - tutorResult0.score.floatValue();
			float score1 = weight - tutorResult1.score.floatValue();
			
			if(score0 == 0 || Math.abs(score0)<Math.abs(score1)) {
				return -1;
			}
			else if(score1 == 0 || Math.abs(score0)>Math.abs(score1)) {
				return 1;
			}
			return 0;
		}
	    
	}

}
