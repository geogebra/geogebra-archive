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

import geogebra.GeoGebraApplet;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author  Markus Hohenwarter
 */
public class GeoGebraAppletTutor extends GeoGebraApplet {


	private String problem = null;
	private String student = null;
	private TutorView tutorView;
	private TutorController tutorController = null;
	

	/** Creates a new instance of GeoGebraApplet */
	public GeoGebraAppletTutor() {}
	
	public void init() 
	{
		super.init();
		if (problem == null) problem = getParameter("problem");
		if (student == null) student = getParameter("student");
		
		// STRATEGY FILES
		if (problem!=null && student != null) {	
			initGUI(problem, student);
		}
		
		
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
	
	protected void initGUI(String p, String s) {
		// TODO: build user interface of applet in here	
	
		//this.setSize(1000,500);

		tutorController = new TutorController(app.getKernel());
		app.getEuclidianView().addMouseListener(tutorController);

		tutorView = new TutorView(p.trim(),s.trim(), tutorController);

		kernel.attach(tutorView); // register view  
		
		JPanel geogebraPanel = createGeoGebraAppletPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				geogebraPanel, tutorView);

		getContentPane().add(splitPane);
		
		splitPane.setDividerLocation(800);
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
