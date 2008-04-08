/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package tutor.gui.applet;

/*
 * GeoGebraApplet.java
 *
 * Created on 23. J�nner 2003, 22:37
 */

import geogebra.Application;
import geogebra.GeoGebraAppletBase;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import tutor.TutorApplication;
import tutor.gui.TutorController;
import tutor.gui.TutorMenubar;
import tutor.gui.TutorView;
import tutor.persistence.dao.http.factory.HttpDaoFactory;
import tutor.persistence.dao.iface.JustificationDao;
import tutor.persistence.dao.iface.StrategyDao;

/**
 *
 * @author  Markus Hohenwarter
 */
public class GeoGebraAppletTutor extends GeoGebraAppletBase {

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
	
	protected Application buildApplication(String[] args, boolean undoActive) {
		
		Application app = new TutorApplication(args, this, undoActive);
		app.setMenubar(new TutorMenubar(app));
		app.initMenubar();
		
		return app; 
	}

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
		
		initGUI(problem, student);
		
		httpDaoFactory = new HttpDaoFactory(protocol, ip, port, context);
		
		tutorView.setStrategyFilesURL(protocol+"://"+ip+":"+port+"/"+strategyFilesContext);
		
		JustificationDao justificationDao =
			(JustificationDao) httpDaoFactory.getDao(JustificationDao.class);
		StrategyDao strategyDao =
			(StrategyDao) httpDaoFactory.getDao(StrategyDao.class);
		
		tutorView.setJustificationDao(justificationDao);
		tutorView.setStrategyDao(strategyDao);
		
		tutorView.initDataModel();
	}
	
	protected void initGUI() {
		initGUI(problem, student);
	}
	
	protected void initGUI(String p, String s) {

		tutorController = new TutorController(app.getKernel());
		app.getEuclidianView().addMouseListener(tutorController);

		tutorView = new TutorView(p,s, tutorController);

		kernel.attach(tutorView); // register view  
		
		JPanel geogebraPanel = createGeoGebraAppletPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				geogebraPanel, tutorView);

		getContentPane().add(splitPane);
		splitPane.setDividerLocation(800);
		
		if (tutorView != null) tutorView.createGUI();
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
