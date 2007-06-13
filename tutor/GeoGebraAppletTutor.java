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

import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 *
 * @author  Markus Hohenwarter
 */
public class GeoGebraAppletTutor extends geogebra.GeoGebraApplet {


	private String[] strategiesXML= null;
	private TutorView tutorView;
	

	/** Creates a new instance of GeoGebraApplet */
	public GeoGebraAppletTutor() {}

	public void init() {
		super.init();
		
		// STRATEGY FILES
		if (getParameter("strategies")!=null) {
		strategiesXML = getParameter("strategies").split(",");
		for (int i=0; i<strategiesXML.length; i++)
		{
			strategiesXML[i] = strategiesXML[i].trim();
			if (strategiesXML[i] != null && 
					!( strategiesXML[i].startsWith("http") || strategiesXML[i].startsWith("file") )) {
				strategiesXML[i] = getCodeBase() + strategiesXML[i];			
				}			
		}
		}
		
		
		
		//TODO: remove test block
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
		
		
		//Attach TutorView
		// Creates a new tutorView
		if (tutorView == null) {
			tutorView = new TutorView(strategiesXML,app);		
		}
		 kernel.attach(tutorView); // register view  	
		 initGUI();
	}
	
	protected void initGUI() {
		// TODO: build user interface of applet in here	
	
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				createGeoGebraAppletPanel(), tutorView);
		
		getContentPane().add(splitPane);
	}

	public void start() {
		//	for some strange reason this is needed to get the right font size		
		//showApplet();
		repaint();
	}

	public void stop() {
		repaint();
	}
	
			
}
