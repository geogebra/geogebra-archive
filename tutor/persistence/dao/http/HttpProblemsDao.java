package tutor.persistence.dao.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tutor.model.Problem;
import tutor.persistence.dao.http.impl.HttpDaoImpl;
import tutor.persistence.dao.http.mapper.ProblemMapper;
import tutor.persistence.dao.iface.ProblemsDao;

public class HttpProblemsDao extends HttpDaoImpl implements ProblemsDao {

	private static String SERVICE_NAME = "problemes";
	
	/**
	 * 
	 * @return
	 */
	public List getProblems() {
		
		String serviceName = getDataSource();
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "getProblemsList");
		
		List l = queryForList(serviceName, (Map)null, ProblemMapper.class);
		
		return l;
	}
	
	public Problem getProblemById(Long id) {

		String serviceName = getDataSource();
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "getProblemById");
		params.put("id", id);
		
		Problem problem = (Problem) queryForObject(serviceName, params, ProblemMapper.class);
		
		return problem;
	}

	public void removeProblem(Long id) {

		
	}

	public void removeProblem(Problem problem) {

		
	}

	public void saveProblem(Problem problem) {

		
	}

	public void updateProblem(Problem problem) {

		
	}

	public static void main(String[] args) {
		
		HttpProblemsDao dao = new HttpProblemsDao();
		
		List l = dao.getProblems();
		for(Iterator it = l.iterator(); it.hasNext();) {
			Problem p = (Problem) it.next();
			System.out.println(""+p.getDescription());
			System.out.println("");
			System.out.println("");
		}
		/*
		Problem p = dao.getProblemById(new Long(1));
		
		System.out.println(p.getDescription());
		System.out.println(p.getId());
		*/
		System.out.println("");
	}
}
