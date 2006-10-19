/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.gui;

import geogebra.Application;
import geogebra.MyError;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/** 
 * Simple browser with home, back, next button for online help
 * 
 * usage:
 * HelpBrowser browser = new HelpBrowser();
 * URL home = new URL("http://www.google.at");
 * browser.setHomePage(home);
 * 
 * to reinit history and set new home page:
 * URL newHome = new URL("http://www.yahoo.de");
 * browser.setHomePage(newHome);
 * 
 * @author Markus Hohenwarter
 */
public class HelpBrowser extends JFrame implements HyperlinkListener, 
                                               ActionListener, PropertyChangeListener {

  	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton homeButton, backButton, forwardButton;
  	private MyJEditorPane htmlPane;
  	private URL homeURL;
  	private Application app;
  
  	private JScrollBar scrollBar;
  	private int scrollBarPos;
  	
 	private int historyIndex; 	
  	private ArrayList history = new ArrayList(50);	
  	private class HistoryEntry {
  		URL url; 
  		int scrollBarPos;
  		
  		HistoryEntry(URL url, int scrollBarPos) {
  			this.url = url;
  			this.scrollBarPos = scrollBarPos;
  		}  		
  		
  		public boolean equals(Object ob) {
  			if (ob instanceof HistoryEntry) {
  				HistoryEntry he = (HistoryEntry) ob;
  				return url.equals(he.url) && 
						scrollBarPos == he.scrollBarPos;
  			} else
  				return false;
  		}
  	}

  	public HelpBrowser(Application app) {
  		this.app = app;
	    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));	    
	    homeButton 		= new JButton( app.getImageIcon("Home24.gif"));	   
	    backButton 		= new JButton(app.getImageIcon("Back24.gif"));		
		forwardButton 	= new JButton(app.getImageIcon("Forward24.gif"));		
		homeButton.addActionListener(this);
		backButton.addActionListener(this);
		forwardButton.addActionListener(this);
	    
	    topPanel.add(homeButton);
	    topPanel.add(backButton);
	    topPanel.add(forwardButton);
	    getContentPane().add(topPanel, BorderLayout.NORTH);
		
		htmlPane = new MyJEditorPane();
		htmlPane.setEditable(false);
		htmlPane.addHyperlinkListener(this);
		htmlPane.addPropertyChangeListener(this);
		JScrollPane scrollPane = new JScrollPane(htmlPane);
		getContentPane().add(scrollPane, BorderLayout.CENTER);	    		
		scrollBar = scrollPane.getVerticalScrollBar(); 
		
		updateFonts();
		setLabels();			
		pack();
	}
	
	public void updateFonts() {
		setFont(app.getPlainFont());					
	}
	
	public void setLabels() {
		setTitle(app.getPlain("ApplicationName") + " " + app.getMenu("Help"));
		homeButton.setToolTipText(app.getPlain("Home"));
		backButton.setToolTipText(app.getPlain("back"));
		forwardButton.setToolTipText(app.getPlain("forward"));		
	}
	
	/**
	 * Processes button click.
	 */
	public void actionPerformed(ActionEvent event) {
	    HistoryEntry histEntry = null;
	    Object src = event.getSource();	
	    if (src == homeButton) 			
	    	histEntry = getHomeURL();
	    else if (src == backButton) 		
	    	histEntry = getPreviousURL();
	    else if (src == forwardButton)  
	    	histEntry = getNextURL();
 	    	    	
	    if (histEntry != null) {	    	
	    	setPage(histEntry.url, histEntry.scrollBarPos);
	    }	  
	    	  
		setButtonsEnabled();			
	}
	
	/**
	 * Processes hyperlink click.
	 */
	public void hyperlinkUpdate(HyperlinkEvent event) {			    
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			URL newUrl = event.getURL();
															
			//	remember current url 
			addToHistory(new HistoryEntry(newUrl, 0));
			setButtonsEnabled();
			
			// load new url			
			setPage(newUrl, 0);
		}
	}
		
	/**
	 * Sets the page displayed in the browser.	 
	 * @return true if successful	
	 */	
	private void setPage(URL url, int scrollBarPos) {	
		storeCurrentScrollBarPos();		
		
		try {	 		
			this.scrollBarPos = scrollBarPos; // see propertyChange()
			htmlPane.setPage(url);			
		} catch(Exception e) {						
			throw new MyError(app, app.getError("URLnotFound") + ":\n" 
												+ url.toExternalForm() );			
		}
		
		/*
		System.out.println("HISTORY index: " + historyIndex);
		for (int i=0; i < history.size(); i++) {
			HistoryEntry h = (HistoryEntry) history.get(i);
			System.out.println("  " + i + ": " + 
					h.scrollBarPos + ", " +  h.url );			
		} */
	}	

	// store scrollbar pos of current page in history
	private void storeCurrentScrollBarPos() {
		URL url = htmlPane.getPage();
		if (url == null) return;
		
		// search url in history and update scrollbar pos
		for (int i = history.size() - 1; i >= 0; i--) {
			HistoryEntry he = (HistoryEntry) history.get(i);
			if (url.equals(he.url)) {
				he.scrollBarPos = scrollBar.getValue();
			}				
		}			
	}

	/* 
	 * updates scrollbarPosition as soon as page was loaded
	 */
	public void propertyChange(PropertyChangeEvent ev) {
		if ("page".equals(ev.getPropertyName())) {			
			// System.out.println("set scrollbar pos:" + scrollBarPos);
			if (scrollBarPos > 0)
				scrollBar.setValue(scrollBarPos);
		}
					
	}
  	
  /**
   * Adds url to browser's history   
   */		   
	private void addToHistory(HistoryEntry he) {			
		if (he == null || he.url ==  null) return;				
		int size = history.size();
	
		// avoid duplicate entry
		if (historyIndex >= 0 && historyIndex < size) {
			if (he.equals(history.get(historyIndex))) return;
		}
								
		// delete end of history								 	
		for (int i=size - 1; i > historyIndex; i--) {
			history.remove(i);						
		}											
		history.add(he);			
		historyIndex++;							
	}
	
	/**
	 * Inits browser history and sets home page.	 
	 */   
    public void setHomePage(URL url) {
    	initHistory();
    	homeURL = url;    	    	    	    	
    	
		// remember the new home url in history
		addToHistory(new HistoryEntry(homeURL, 0));
		setButtonsEnabled();	
		
		setPage(homeURL, 0); 
    }
   
	
	private void initHistory() {
		historyIndex = -1;
		history.clear();
		backButton.setEnabled(false);
		forwardButton.setEnabled(false);
	}
	
	private HistoryEntry getHomeURL() {
		historyIndex = 0;
		return (HistoryEntry) history.get(0);
	}
	
   
	/**
	  * @return previous url from browser's history
	  */	   
	 private HistoryEntry getPreviousURL() {	  
	 	return (HistoryEntry) history.get(--historyIndex);
	 }
	
	/**
	 * @return next url from browser's history
	 */
	 private HistoryEntry getNextURL() {		
		return (HistoryEntry) history.get(++historyIndex);			
	 }
	 
	 private void setButtonsEnabled() {  		  
		 boolean back 	= (historyIndex > 0);
		 boolean forward = (historyIndex < history.size()-1);        
		 backButton.setEnabled(back);        
		 forwardButton.setEnabled(forward);		               
	 }
	 
	 private class MyJEditorPane extends JEditorPane {
		 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void paint(Graphics g) {
			 Graphics2D g2 = (Graphics2D) g;
			   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);                                        
			   g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                       RenderingHints.VALUE_TEXT_ANTIALIAS_ON);                   
			   super.paint(g2);
		 }
	 }

}