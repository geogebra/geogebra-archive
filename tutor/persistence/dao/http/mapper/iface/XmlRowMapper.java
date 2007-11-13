package tutor.persistence.dao.http.mapper.iface;

import org.dom4j.Element;

/**
 * 
 * @author albert
 *
 */
public interface XmlRowMapper {

	/**
	 * 
	 * @param xmlrow
	 * @return
	 * @throws Throwable
	 */
	public Object mapXmlRow(Element xmlrow) throws Throwable;
}
