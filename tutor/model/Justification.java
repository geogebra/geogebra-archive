package tutor.model;

import java.io.Serializable;

public class Justification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3322747914923941338L;
	
	private Long id;
	private String description;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
