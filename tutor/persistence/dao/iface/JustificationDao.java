package tutor.persistence.dao.iface;

import java.util.List;

import tutor.model.Justification;

public interface JustificationDao {

	public Justification getJustificationById(Long id);
	public List getJustificationsList();
	public List findProblemJustifications(Long problemId);
	public void saveJustification(Justification justification);
	public void updateJustification(Justification justification);
	public void removeJustification(Long id);
	public void removeJustification(Justification justification);
}
