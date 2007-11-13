package tutor.persistence.dao.http.mapper;

import org.dom4j.Element;

import tutor.model.Problem;
import tutor.persistence.dao.http.mapper.iface.XmlRowMapper;

/**
 * 
 * @author albert
 *
 */
public class ProblemMapper implements XmlRowMapper {
	
	public Object mapXmlRow(Element xmlrow) throws Throwable {

		Element row = xmlrow;
		
		String id = row.elementText("id");
		String professorId = row.elementText("id_professor");
		String titol = row.elementText("titol");
		String enunciat = row.elementText("enunciat");
		//String data = row.elementText("data");
		String fitxerInicial = row.elementText("fitxer_inicial");
		String dificultat = row.elementText("dificultat");
		String tags = row.elementText("tags");
		
		Problem problem = new Problem();
		problem.setId(new Long(id));
		problem.setTeacherId(new Long(professorId));
		problem.setTitle(titol);
		problem.setDescription(enunciat);
		problem.setDatetime(null);
		problem.setInitialFileName(fitxerInicial);
		problem.setDifficulty(dificultat);
		problem.setTags(tags);
		
		return problem;
	}

}
