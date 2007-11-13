package tutor.model;

/**
 * 
 * @author albert
 *
 */
public class Strategy {

	private Long id;
	private Long problemId;
	private String titol;
	private String url;
	
	/**
	 * 
	 * @return
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @return
	 */
	public Long getProblemaId() {
		return problemId;
	}
	
	/**
	 * 
	 * @param problemId
	 */
	public void setProblemId(Long problemId) {
		this.problemId = problemId;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTitol() {
		return titol;
	}
	
	/**
	 * 
	 * @param titol
	 */
	public void setTitol(String titol) {
		this.titol = titol;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
