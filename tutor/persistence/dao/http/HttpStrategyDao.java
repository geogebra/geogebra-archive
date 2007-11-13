package tutor.persistence.dao.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tutor.persistence.dao.http.impl.HttpDaoImpl;
import tutor.persistence.dao.http.mapper.StrategyMapper;
import tutor.persistence.dao.iface.StrategyDao;

public class HttpStrategyDao extends HttpDaoImpl implements StrategyDao {

	private static String SERVICE_NAME = "estrategies";
	
	public List findStrategiesByProblemId(Long id) {

		String serviceName = "http://127.0.0.1/agentgeom/ws/wstestlist.php";
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "findEstrategiesByProblemId");
		params.put("id", id);
		
		List strategies = queryForList(serviceName, (Map)params, StrategyMapper.class);
		
		return strategies;
	}

}
