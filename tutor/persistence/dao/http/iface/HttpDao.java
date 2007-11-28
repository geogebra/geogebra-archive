package tutor.persistence.dao.http.iface;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author albert
 *
 */
public interface HttpDao {

	/**
	 * 
	 * @param serviceName
	 * @param params
	 * @param mapper
	 * @return
	 */
	public Object queryForObject(String serviceName, Map params, Class mapper);
	
	/**
	 * 
	 * @param serviceName
	 * @param params
	 * @param mapper
	 * @return
	 */
	public List queryForList(String serviceName, Map params, Class mapper);
	
	public void setDataSource(String ds);
	public String getDataSource();
}
