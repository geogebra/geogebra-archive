package tutor.model;


/**
 * different descriptors for Tutor comparison purposes
 * @author AlbertV
 *
 */
public class TutorElement {

	private int numPoints;
	private int numLines;
	private int numAnglesEq90;
	private int numAnglesGt90;
	private int numAnglesLt90;
	
	// Points
	public int getNumPoints() {
		return numPoints;
	}
	public void setNumPoints(int numPoints) {
		this.numPoints = numPoints;
	}
	public void addPoint() {
		this.numPoints++;
	}
	public void removePoint() {
		this.numPoints--;
	}
	
	// Lines
	public int getNumLines() {
		return numLines;
	}
	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}
	public void addLine() {
		this.numLines++;
	}
	public void removeLine() {
		this.numLines--;
	}
	
	// Angles
	public int getNumAnglesEq90() {
		return numAnglesEq90;
	}
	public void setNumAnglesEq90(int numAnglesEq90) {
		this.numAnglesEq90 = numAnglesEq90;
	}
	public void addAngleEq90() {
		this.numAnglesEq90++;
	}
	public void removeAngleEq90() {
		this.numAnglesEq90--;
	}
	public int getNumAnglesGt90() {
		return numAnglesGt90;
	}
	public void setNumAnglesGt90(int numAnglesGt90) {
		this.numAnglesGt90 = numAnglesGt90;
	}
	public void addAngleGt90() {
		this.numAnglesGt90++;
	}
	public void removeAngleGt90() {
		this.numAnglesGt90--;
	}
	public int getNumAnglesLt90() {
		return numAnglesLt90;
	}
	public void setNumAnglesLt90(int numAnglesLt90) {
		this.numAnglesLt90 = numAnglesLt90;
	}
	public void addAngleLt90() {
		this.numAnglesLt90++;
	}
	public void removeAngleLt90() {
		this.numAnglesLt90--;
	}
	
}
