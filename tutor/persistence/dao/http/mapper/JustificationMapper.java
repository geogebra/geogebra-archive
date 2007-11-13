package tutor.persistence.dao.http.mapper;

import org.dom4j.Element;

import tutor.model.Justification;
import tutor.persistence.dao.http.mapper.iface.XmlRowMapper;

public class JustificationMapper implements XmlRowMapper {

	public Object mapXmlRow(Element xmlrow) throws Throwable {

		Element row = xmlrow;
		
		String id = row.elementText("id");
		String description = row.elementText("descripcio");
		
		Justification justification = new Justification();
		justification.setId(new Long(id));
		justification.setDescription(description);
		
		return justification;
	}

}
