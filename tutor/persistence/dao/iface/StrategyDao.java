package tutor.persistence.dao.iface;

import java.util.List;

import tutor.model.Strategy;

public interface StrategyDao {

	public Strategy getStrategyById(Long id);
	public List getStrategiesList();
	public List findStrategiesByProblemId(Long id);
	
	public void saveStrategy(Strategy strategy);
	public void removeStrategy(Strategy strategy);
	public void removeStrategy(Long id);
	public void updateStrategy(Strategy strategy);
}
