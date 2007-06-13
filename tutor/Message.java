package tutor;

public class Message {
private long id;
private long id_professor;
private String message;
private String tipus;


/**
 * @return the id_professor
 */
public long getId_professor() {
	return id_professor;
}

/**
 * @param id_professor the id_professor to set
 */
public void setId_professor(long id_professor) {
	this.id_professor = id_professor;
}

/**
 * @return the message
 */
public String getMessage() {
	return message;
}

/**
 * @param message the message to set
 */
public void setMessage(String message) {
	this.message = message;
}

/**
 * @return the tipus
 */
public String getTipus() {
	return tipus;
}

/**
 * @param tipus the tipus to set
 */
public void setTipus(String tipus) {
	this.tipus = tipus;
}

public long getId() {
	return id;
}

public void setId(long id) {
	this.id = id;
}

public Message(long id, long id_professor, String message, String tipus) {
	super();
	this.id = id;
	this.id_professor = id_professor;
	this.message = message;
	this.tipus = tipus;
}
public Message(String message){

	this(-1,-1,message,"");
}
	
}
