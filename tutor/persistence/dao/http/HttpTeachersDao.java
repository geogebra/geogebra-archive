package tutor.persistence.dao.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tutor.model.Teacher;
import tutor.persistence.dao.http.impl.HttpDaoImpl;
import tutor.persistence.dao.http.mapper.TeacherMapper;
import tutor.persistence.dao.iface.TeachersDao;

public class HttpTeachersDao extends HttpDaoImpl implements TeachersDao {

	private static String SERVICE_NAME = "professors";
	
	private static String IP = "158.109.2.26";
	
	public List getTeachers() {
		
		String serviceName = "http://"+IP+"/edumat/agentgeom/ws/wstestlist.php";
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "getTeachersList");
		
		List l = queryForList(serviceName, (Map)params, TeacherMapper.class);
		
		return l;
	}
	
	public Teacher getTeacherById(Long id) {
		
		String serviceName = "http://"+IP+"/edumat/agentgeom/ws/wstestlist.php";
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "getTeacherById");
		params.put("id", id);
		
		Teacher teacher = (Teacher) queryForObject(serviceName, (Map)params, TeacherMapper.class);
		
		return teacher;
	}
	
	public void removeTeacher(Long id) {
	}

	public void removeTeacher(Teacher teacher) {
	}

	public void saveTeacher(Teacher teacher) {
	}

	public void updateTeacher(Teacher teacher) {
	}
	
	public static void main(String[] args) throws Throwable {
		
		TeachersDao dao = new HttpTeachersDao();
		List l = dao.getTeachers();
		for (Iterator it = l.iterator(); it.hasNext();) {
			Teacher t = (Teacher) it.next();
			System.out.println(t.getName());
		}
		
	}
}
