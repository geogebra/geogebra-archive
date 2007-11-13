package tutor.model;

import java.util.Date;

/**
 * 
 * @author albert
 *
 */
public class Problem {

	private Long id;
	private Long teacherId;
	private String title;
	private String description;
	private String initialFileName;
	private String strategiesFileName;
	private Boolean active;
	private Date datetime;
	private String tags;
	private String difficulty;
	private Integer publicScope;
	
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
	public Long getTeacherId() {
		return teacherId;
	}
	
	/**
	 * 
	 * @param teacherId
	 */
	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getInitialFileName() {
		return initialFileName;
	}
	
	/**
	 * 
	 * @param initialFileName
	 */
	public void setInitialFileName(String initialFileName) {
		this.initialFileName = initialFileName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getStrategiesFileName() {
		return strategiesFileName;
	}
	
	/**
	 * 
	 * @param strategiesFileName
	 */
	public void setStrategiesFileName(String strategiesFileName) {
		this.strategiesFileName = strategiesFileName;
	}
	
	/**
	 * 
	 * @return
	 */
	public Boolean getActive() {
		return active;
	}
	
	/**
	 * 
	 * @param active
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	/**
	 * 
	 * @return
	 */
	public Date getDatetime() {
		return datetime;
	}
	
	/**
	 * 
	 * @param datetime
	 */
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTags() {
		return tags;
	}
	
	/**
	 * 
	 * @param tags
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDifficulty() {
		return difficulty;
	}
	
	/**
	 * 
	 * @param difficulty
	 */
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getPublicScope() {
		return publicScope;
	}
	
	/**
	 * 
	 * @param publicScope
	 */
	public void setPublicScope(Integer publicScope) {
		this.publicScope = publicScope;
	}
	
}
