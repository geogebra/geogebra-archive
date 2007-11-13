package tutor.persistence.dao.iface;

import java.util.List;

import tutor.model.Problem;

public interface ProblemsDao {

	public Problem getProblemById(Long id);
	public List getProblems();
	public void saveProblem(Problem problem);
	public void updateProblem(Problem problem);
	public void removeProblem(Long id);
	public void removeProblem(Problem problem);
}
