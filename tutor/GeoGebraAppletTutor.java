/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package tutor;

/*
 * GeoGebraApplet.java
 *
 * Created on 23. J�nner 2003, 22:37
 */

import geogebra.Application;
import geogebra.GeoGebraApplet;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import tutor.persistence.dao.http.factory.HttpDaoFactory;
import tutor.persistence.dao.iface.JustificationDao;
import tutor.persistence.dao.iface.StrategyDao;

/**
 *
 * @author  Markus Hohenwarter
 */
public class GeoGebraAppletTutor extends GeoGebraApplet {


	protected Application buildApplication(String[] args, boolean undoActive) {
		
		Application app = new CustomApplication(args, this, undoActive);
		app.setMenubar(new MyCustomMenubar(app));
		app.initMenubar();
		
		return app; 
	}

	private String problem = null;
	private String student = null;
	private TutorView tutorView;
	private TutorController tutorController = null;
	
	private HttpDaoFactory httpDaoFactory;

	String protocol = null;
	String ip = null;
	String port = null;
	String context = null;
	String strategyFilesContext = null;

	public GeoGebraAppletTutor() {}
	
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
		
		if (problem == null) problem = "7";
		if (student == null)  student = "1";
		
		if (protocol == null) protocol = "http";
		if (ip == null) ip = "192.168.1.4";
		if (port == null) port = "80";
		if (context == null) context = "agentgeom/ws/wstestlist.php";
		if (strategyFilesContext == null) strategyFilesContext = "agentgeom/problemes";
		
		if (problem!=null && student != null) {	
			initGUI(problem, student);
		}
		
		httpDaoFactory = new HttpDaoFactory(protocol, ip, port, context);
		
		tutorView.setStrategyFilesURL(protocol+"://"+ip+":"+port+"/"+strategyFilesContext);
		
		JustificationDao justificationDao =
			(JustificationDao) httpDaoFactory.getDao(JustificationDao.class);
		StrategyDao strategyDao =
			(StrategyDao) httpDaoFactory.getDao(StrategyDao.class);
		
		tutorView.setJustificationDao(justificationDao);
		tutorView.setStrategyDao(strategyDao);
		
		tutorView.initDataModel();
		tutorView.createGUI();
		
		
	}
	
	protected void initGUI(String p, String s) {
		// TODO: build user interface of applet in here	
	
		//this.setSize(1000,500);

		tutorController = new TutorController(app.getKernel());
		app.getEuclidianView().addMouseListener(tutorController);

		tutorView = new TutorView(p,s, tutorController);

		kernel.attach(tutorView); // register view  
		
		JPanel geogebraPanel = createGeoGebraAppletPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				geogebraPanel, tutorView);

		getContentPane().add(splitPane);
		
		splitPane.setDividerLocation(800);
	}

	protected void initDataModel() {
		
		System.out.println("**** MAIN construction BEGIN");
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
		System.out.println("**** MAIN construction END");
	}
	
	public void start() {
		//	for some strange reason this is needed to get the right font size		
		//showApplet();
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

	public TutorView getTutorView() {
		return tutorView;
	}

	public void setTutorView(TutorView tutorView) {
		this.tutorView = tutorView;
	}
	
	public static void main(String[] args) throws Throwable {
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.getTime();
		
	}
			
}
