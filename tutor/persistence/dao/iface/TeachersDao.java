package tutor.persistence.dao.iface;

import java.util.List;

import tutor.model.Teacher;

public interface TeachersDao {

	public Teacher getTeacherById(Long id);
	public List getTeachers();
	public void saveTeacher(Teacher teacher);
	public void updateTeacher(Teacher teacher);
	public void removeTeacher(Long id);
	public void removeTeacher(Teacher teacher);
}
