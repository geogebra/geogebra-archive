package tutor.model;

/**
 * 
 * @author albert
 *
 */
public class Student {

	private Long id;
	private String name;
	private String surnames;
	private String username;
	private String password;
	private Boolean active;
	
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
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSurnames() {
		return surnames;
	}
	
	/**
	 * 
	 * @param surnames
	 */
	public void setSurnames(String surnames) {
		this.surnames = surnames;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
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
	
}
