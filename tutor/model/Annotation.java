package tutor.model;

import java.io.Serializable;

public class Annotation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -336301359049095877L;
	
	private int offset;
	private int length;
	private String annotation;
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	
	
}
