package tutor.persistence.dao.iface;

import java.util.List;

public interface StrategyDao {

	public List findStrategiesByProblemId(Long id);
}
