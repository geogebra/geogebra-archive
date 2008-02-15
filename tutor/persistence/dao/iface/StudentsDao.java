package tutor.persistence.dao.iface;

import java.util.List;

import tutor.model.Student;

public interface StudentsDao {

	public Student getStudentById(Long id);
	public List getStudentsList();
	
	public void saveStudent(Student student);
	public void removeStudent(Student student);
	public void removeStudent(Long id);
	public void updateStudent(Student student);
}
