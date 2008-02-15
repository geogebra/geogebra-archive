package tutor.persistence.dao.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tutor.model.Student;
import tutor.persistence.dao.http.impl.HttpDaoImpl;
import tutor.persistence.dao.http.mapper.StudentMapper;
import tutor.persistence.dao.iface.StudentsDao;

public class HttpStudentsDao extends HttpDaoImpl implements StudentsDao {

	private static String SERVICE_NAME = "alumnes";
	
	public Student getStudentById(Long id) {
		
		String serviceName = getDataSource();
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "findStudentById");
		params.put("id", id);
		
		Student student = (Student) queryForObject(serviceName, (Map)params, StudentMapper.class);
		
		return student;
	}

	public List getStudentsList() {
		
		String serviceName = getDataSource();
		
		Map params = new HashMap();
		params.put("service", SERVICE_NAME);
		params.put("call", "getStudentsList");
		
		List students = queryForList(serviceName, (Map)params, StudentMapper.class);
		
		return students;
	}

	public void removeStudent(Long id) {
		// TODO Auto-generated method stub
		
	}

	public void removeStudent(Student student) {
		// TODO Auto-generated method stub
		
	}

	public void saveStudent(Student student) {
		// TODO Auto-generated method stub
		
	}

	public void updateStudent(Student student) {
		// TODO Auto-generated method stub
		
	}

}
