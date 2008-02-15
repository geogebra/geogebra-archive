package tutor.model;

import java.io.Serializable;

public class MessageType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5659160507074046435L;

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
