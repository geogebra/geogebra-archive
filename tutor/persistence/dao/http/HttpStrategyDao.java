package tutor.persistence.dao.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tutor.model.Strategy;
import tutor.persistence.dao.http.impl.HttpDaoImpl;
import tutor.persistence.dao.http.mapper.StrategyMapper;
import tutor.persistence.dao.iface.StrategyDao;

public class HttpStrategyDao extends HttpDaoImpl implements StrategyDao {

	private static String SERVICE_NAME = "estrategies";
	
	public List findStrategiesByProblemId(Long id) {

		String serviceName = getDataSource();
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "findEstrategiesByProblemId");
		params.put("id", id);
		
		List strategies = queryForList(serviceName, (Map)params, StrategyMapper.class);
		
		return strategies;
	}

	public List getStrategiesList() {
		
		String serviceName = getDataSource();
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "findEstrategiesList");
		
		List strategies = queryForList(serviceName, (Map)params, StrategyMapper.class);
		
		return strategies;
	}

	public Strategy getStrategyById(Long id) {
		
		String serviceName = getDataSource();
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "findEstrategiyById");
		params.put("id", id);
		
		Strategy strategy = (Strategy) queryForObject(serviceName, (Map)params, StrategyMapper.class);
		
		return strategy;
	}

	public void removeStrategy(Long id) {
		// TODO Auto-generated method stub
		
	}

	public void removeStrategy(Strategy strategy) {
		// TODO Auto-generated method stub
		
	}

	public void saveStrategy(Strategy strategy) {
		// TODO Auto-generated method stub
		
	}

	public void updateStrategy(Strategy strategy) {
		// TODO Auto-generated method stub
	
	}
	
	public static void main(String[] args) throws Throwable {
		
		HttpStrategyDao dao = new HttpStrategyDao();
		
		List l = dao.findStrategiesByProblemId(new Long(1));
		System.out.println("");
	}
}
