package tutor.persistence.dao.http.factory;

import java.util.HashMap;
import java.util.Map;

import tutor.persistence.dao.http.HttpJustificationDao;
import tutor.persistence.dao.http.HttpProblemsDao;
import tutor.persistence.dao.http.HttpStrategyDao;
import tutor.persistence.dao.http.HttpTeachersDao;
import tutor.persistence.dao.http.iface.HttpDao;
import tutor.persistence.dao.iface.JustificationDao;
import tutor.persistence.dao.iface.ProblemsDao;
import tutor.persistence.dao.iface.StrategyDao;
import tutor.persistence.dao.iface.TeachersDao;

public class HttpDaoFactory {

	private static Map daos = new HashMap();
	
	private String PROTOCOL = "http";
	private String IP = "158.109.2.26";
	private String PORT = "";
	private String CONTEXT = "edumat/agentgeom/ws/wstestlist.php";
	
	public HttpDaoFactory(String protocol, String ip, String port, String context) {
		PROTOCOL = protocol;
		IP = ip;
		PORT = port;
		CONTEXT = context;
	}
	
	public static String buildDataSource(String protocol,
			String ip, String port, String context) {
		
		String ds = "";
		
		ds += protocol;
		ds += "://";
		ds += ip;
		
		if (port != null && !"".equals(port)) {
			ds += ":"+port;
		}
		
		ds += "/";
		ds += context;
		
		return ds;
	}
	
	public HttpDao getDao(Class c) {
		
		HttpDao dao = null;
		
		String ds = buildDataSource(PROTOCOL, IP,
					PORT, CONTEXT);
		
		if (c.equals(TeachersDao.class)) {
			dao = new HttpTeachersDao();
		}
		else if (c.equals(StrategyDao.class)) {
			dao = new HttpStrategyDao();
		}
		else if (c.equals(ProblemsDao.class)) {
			dao = new HttpProblemsDao();
		}
		else if (c.equals(JustificationDao.class)) {
			dao = new HttpJustificationDao();
		}
		
		if (dao != null) dao.setDataSource(ds);
		
		return dao;
	}
}
