package tutor.persistence.dao.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tutor.persistence.dao.http.impl.HttpDaoImpl;
import tutor.persistence.dao.http.mapper.StrategyMapper;
import tutor.persistence.dao.iface.StrategyDao;

public class HttpStrategyDao extends HttpDaoImpl implements StrategyDao {

	private static String SERVICE_NAME = "estrategies";
	
	private static String IP = "158.109.2.26";
	
	public List findStrategiesByProblemId(Long id) {

		String serviceName = "http://"+IP+"/edumat/agentgeom/ws/wstestlist.php";
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "findEstrategiesByProblemId");
		params.put("id", id);
		
		List strategies = queryForList(serviceName, (Map)params, StrategyMapper.class);
		
		return strategies;
	}

	public static void main(String[] args) throws Throwable {
		
		HttpStrategyDao dao = new HttpStrategyDao();
		
		List l = dao.findStrategiesByProblemId(new Long(1));
		System.out.println("");
	}
}
