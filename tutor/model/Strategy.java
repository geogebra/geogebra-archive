package tutor.model;

import geogebra.kernel.Construction;

import java.util.LinkedList;

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
	private String file;
	
	private LinkedList messages = new LinkedList();
	private Construction construction;
	
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

	public LinkedList getMessages() {
		return messages;
	}

	public void setMessages(LinkedList messages) {
		this.messages = messages;
	}

	public Construction getConstruction() {
		return construction;
	}

	public void setConstruction(Construction construction) {
		this.construction = construction;
	}

	public Long getProblemId() {
		return problemId;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}
