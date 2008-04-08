package tutor.gui.applet;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import geogebra.Application;
import geogebra.GeoGebraAppletBase;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;
import tutor.TeacherApplication;
import tutor.gui.TeacherController;
import tutor.gui.TeacherMenubar;
import tutor.gui.TeacherView;
import tutor.gui.TutorController;
import tutor.gui.TutorView;
import tutor.persistence.dao.http.factory.HttpDaoFactory;
import tutor.persistence.dao.iface.JustificationDao;
import tutor.persistence.dao.iface.StrategyDao;

public class GeoGebraAppletTeacher extends GeoGebraAppletBase {

	private String problem = null;
	private String student = null;
	private TeacherView teacherView;
	private TeacherController teacherController = null;
	
	private HttpDaoFactory httpDaoFactory;

	String protocol = null;
	String ip = null;
	String port = null;
	String context = null;
	String strategyFilesContext = null;
	
	protected Application buildApplication(String[] args, boolean undoActive) {
		
		Application app = new TeacherApplication(args, this, undoActive);
		app.setMenubar(new TeacherMenubar(app));
		app.initMenubar();
		
		return app; 
	}
	
	public GeoGebraAppletTeacher() {}

	public void init() 
	{
		super.init();
		
		// Collect parameters
		problem = getParameter("problem");
		student = getParameter("student");
		protocol = getParameter("protocol");
		ip = getParameter("ip");
		port = getParameter("port");
		context = getParameter("context");
		strategyFilesContext = getParameter("strategyFilesContext");
		
		initGUI(problem, student);
		
		httpDaoFactory = new HttpDaoFactory(protocol, ip, port, context);
		
		teacherView.setStrategyFilesURL(protocol+"://"+ip+":"+port+"/"+strategyFilesContext);
		
		JustificationDao justificationDao =
			(JustificationDao) httpDaoFactory.getDao(JustificationDao.class);
		StrategyDao strategyDao =
			(StrategyDao) httpDaoFactory.getDao(StrategyDao.class);
		
		teacherView.setJustificationDao(justificationDao);
		teacherView.setStrategyDao(strategyDao);
		
		teacherView.initDataModel();
	}
	
	protected void initGUI() {
		initGUI(problem, student);
	}
	
	protected void initGUI(String p, String s) {

		teacherController = new TeacherController(app.getKernel());
		app.getEuclidianView().addMouseListener(teacherController);

		teacherView = new TeacherView(p,s, teacherController);

		kernel.attach(teacherView); // register view  
		
		JPanel geogebraPanel = createGeoGebraAppletPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				geogebraPanel, teacherView);

		getContentPane().add(splitPane);
		splitPane.setDividerLocation(800);
		
		if (teacherView != null) teacherView.createGUI();
	}

	protected void initDataModel() {
		
		Construction c = kernel.getConstruction();
		int i=0;
		while (c.getConstructionElement(i)!= null)
    	{
			ConstructionElement ce = c.getConstructionElement(i);
			if (ce.isAlgoElement()) {
				System.out.println("algo: " + ce);	
			} else {
				System.out.println("geo: " + ce);	
			}
    		
    		i++;
    	}
	}
	
	public void start() {
		repaint();
	}

	public void stop() {
		repaint();
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public String getStudent() {
		return student;
	}

	public void setStudent(String student) {
		this.student = student;
	}

	public TeacherView getTutorView() {
		return teacherView;
	}

	public void setTutorView(TeacherView teacherView) {
		this.teacherView = teacherView;
	}
	
	public static void main(String[] args) throws Throwable {
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.getTime();
	}
			

}
