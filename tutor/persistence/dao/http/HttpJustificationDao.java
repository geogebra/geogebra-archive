package tutor.persistence.dao.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tutor.model.Justification;
import tutor.persistence.dao.http.impl.HttpDaoImpl;
import tutor.persistence.dao.http.mapper.JustificationMapper;
import tutor.persistence.dao.iface.JustificationDao;

public class HttpJustificationDao extends HttpDaoImpl implements JustificationDao {

	private static String SERVICE_NAME = "justificacions";
	private static String IP = "158.109.2.26";
	
	public Justification getJustificationById(Long id) {
		
		String serviceName = "http://"+IP+"/edumat/agentgeom/ws/wstestlist.php";
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "getJustificationsList");
		params.put("id", id);
		
		Justification j = (Justification)
			queryForObject(serviceName, (Map)params, JustificationMapper.class);
		
		return j;
	}

	public List getJustificationsList() {
		
		String serviceName = "http://"+IP+"/edumat/agentgeom/ws/wstestlist.php";
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "getJustificationsList");
		
		List l = queryForList(serviceName, (Map)params, JustificationMapper.class);
		
		return l;
	}
	
	public List findProblemJustifications(Long problemId) {

		String serviceName = "http://"+IP+"/edumat/agentgeom/ws/wstestlist.php";
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "findProblemJustifications");
		params.put("id", problemId);
		
		List l = queryForList(serviceName, (Map)params, JustificationMapper.class);
		
		return l;
	}

	public void removeJustification(Long id) {
	}

	public void removeJustification(Justification justification) {
	}

	public void saveJustification(Justification justification) {
	}

	public void updateJustification(Justification justification) {
	}

}
