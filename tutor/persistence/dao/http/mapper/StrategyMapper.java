package tutor.persistence.dao.http.mapper;

import org.dom4j.Element;

import tutor.model.Strategy;
import tutor.persistence.dao.http.mapper.iface.XmlRowMapper;

/**
 * 
 * @author albert
 *
 */
public class StrategyMapper implements XmlRowMapper {

	public Object mapXmlRow(Element xmlrow) throws Throwable {

		Element row = xmlrow;
		
		String id = row.elementText("id");
		String problemaId = row.elementText("id_problema");
		String titol = row.elementText("titol");
		String fEstrategia = row.elementText("fitxer_estrategia");
		
		Strategy strategy = new Strategy();
		strategy.setId(new Long(id));
		strategy.setProblemId(new Long(problemaId));
		strategy.setTitol(titol);
		strategy.setUrl(fEstrategia);
		
		return strategy;
	}

}
