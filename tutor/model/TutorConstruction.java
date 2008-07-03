package tutor.model;


/**
 * different descriptors for Tutor comparison purposes
 * @author AlbertV
 *
 */
public class TutorConstruction {

	private TutorElement element;
	private TutorGraph graph;
	
	//TODO: think smthg better to deal with that
	/** path to the strategy file */
	private String ggbPath;
	
	public TutorConstruction() {
		element = new TutorElement();
		graph = new TutorGraph();
	}
	
	
	// TutorElement
	public TutorElement getElement() {
		return element;
	}
	public void setElement(TutorElement element) {
		this.element = element;
	}
	
	// TutorGraph
	public TutorGraph getGraph() {
		return graph;
	}
	public void setGraph(TutorGraph graph) {
		this.graph = graph;
	}
	
	// GGB File Path
	public String getGgbPath() {
		return ggbPath;
	}
	public void setGgbPath(String ggbPath) {
		this.ggbPath = ggbPath;
	}
	
}
