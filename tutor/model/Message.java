package tutor.model;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6468823231895585044L;
	
	private Long id;
	private String message;
	private Long teacherId;
	private Long messageTypeId;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public Long getMessageTypeId() {
		return messageTypeId;
	}

	public void setMessageTypeId(Long messageTypeId) {
		this.messageTypeId = messageTypeId;
	}
}
