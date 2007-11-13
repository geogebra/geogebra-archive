package tutor.persistence.dao.http.mapper;

import org.dom4j.Element;

import tutor.model.Teacher;
import tutor.persistence.dao.http.mapper.iface.XmlRowMapper;

/**
 * 
 * @author albert
 *
 */
public class TeacherMapper implements XmlRowMapper {

	public Object mapXmlRow(Element xmlrow) throws Throwable {
		
		Element row = xmlrow;
		
		String id = row.elementText("id");
		String nom = row.elementText("nom");
		String cognoms = row.elementText("cognoms");
		String usuari = row.elementText("row");
		String password = row.elementText("password");
		String actiu = row.elementText("actiu");
		
		Teacher teacher = new Teacher();
		teacher.setId(new Long(id));
		teacher.setName(nom);
		teacher.setSurnames(cognoms);
		teacher.setUsername(usuari);
		teacher.setPassword(password);
		teacher.setActive("1".equals(actiu)? Boolean.valueOf(true):Boolean.valueOf(false));
		
		return teacher;
	}

}
