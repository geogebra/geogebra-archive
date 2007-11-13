package tutor.persistence.dao.http.mapper;

import org.dom4j.Element;

import tutor.model.Student;
import tutor.persistence.dao.http.mapper.iface.XmlRowMapper;

/**
 * 
 * @author albert
 *
 */
public class StudentMapper implements XmlRowMapper {

	public Object mapXmlRow(Element xmlrow) throws Throwable {

		Element row = xmlrow;

		String id = row.elementText("id");
		String nom = row.elementText("nom");
		String cognoms = row.elementText("cognoms");
		String usuari = row.elementText("row");
		String password = row.elementText("password");
		String actiu = row.elementText("actiu");
		
		Student student = new Student();
		student.setId(new Long(id));
		student.setName(nom);
		student.setSurnames(cognoms);
		student.setUsername(usuari);
		student.setPassword(password);
		student.setActive("1".equals(actiu)? Boolean.valueOf(true):Boolean.valueOf(false));
		
		return student;
	}

}
