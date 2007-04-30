/*
    GEONExT

    Copyright (C) 2002  GEONExT-Group, Lehrstuhl für Mathematik und ihre Didaktik, Universität Bayreuth

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA. 
*/
package geogebra.gui.toolbar;
import geogebra.Application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
class ToolbarConfigDialog extends JDialog implements ActionListener, ListSelectionListener, ChangeListener {
	public JTabbedPane tPane;
	private Application app;	
	public JPanelConfigToolbar1 configToolBar1;
	String actLeftToolBar;
		
	/**
	 * 
	 */
	public ToolbarConfigDialog(Application app) {
		super();
		this.app = app;
					
		actLeftToolBar = app.getToolBarDefinition();
		
		tPane = new JTabbedPane();
		tPane.setBorder(new EmptyBorder(0, 0, 0, 0));				
		jPanel.add(panel,BorderLayout.CENTER);
		
		tPane.addTab("Toolbar auswählen",jPanel);
		tPane.addTab("Konstruktionsleiste ändern", configToolBar2 = new JPanelConfigToolbar2("bla", geonext, this, 0));
		tPane.addTab("Konstruktionsleiste konfigurieren", configToolBar1 = new JPanelConfigToolbar1("bla", (ToolBarVector) geonext.allToolBarVectors.get(geonext.activeLeftToolBar), this));
		//
		//
		tPane.addChangeListener(this);
		setLayout(new BorderLayout(5, 5));
		add("Center", tPane);
		//tPane.setSize(tPane.getPreferredSize());
		//setSize(getPreferredSize());
		validateTree();
	}
	/**
	 * 
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		String source = e.getActionCommand();
		if (source.equals("reset")) {
			reset();
		} else
			if (source.equals("close")) {
				geonext.getGeonextConfigDialog().dispose();
			} else
				if (source.equals("apply")) {
					// Hier Übernehmen
					geonext.getGeonextConfigDialog().dispose();
				} else
					if (source.equals("cancel")) {
						geonext.getGeonextConfigDialog().dispose();
					} else {
						/*
						 try {
							java.lang.reflect.Method m = getClass().getMethod(source, null);
							m.invoke(this, null);
						} catch (Exception exc) {}
						*/
					}
		//setVisible(false);
		//	if (((AbstractButton) (event.getSource())).getName() == "ok") {}
		//	try {
		//		finalize();
		//	} catch (Throwable thr) {}
	}
	/**
	 * 
	 */
	public void exit(boolean changeToolbar, ToolBarVector popups) {
		if (changeToolbar) {
			this.popups = (ToolBarVector) popups.clone();
			if (configToolBar1.change) {
				if (((ToolBarVector) allToolBarVectors.get(actLeftToolBar)).getType().toLowerCase().equals("internal")) {
					allToolBarVectors.removeElementAt(geonext.allToolBarVectors.size() - 1);
					allToolBarVectors.addElement(configToolBar1.popups.clone());
					actLeftToolBar = geonext.allToolBarVectors.size() - 1;
				} else {
					allToolBarVectors.removeElementAt(actLeftToolBar);
					allToolBarVectors.add(actLeftToolBar, configToolBar1.popups.clone());
				}
			}
			geonext.allToolBarVectors = new Vector();
			for (int i = 0; i < allToolBarVectors.size(); i++)
				geonext.allToolBarVectors.add(((ToolBarVector) allToolBarVectors.get(i)).clone());
			((ToolBarVector) geonext.allToolBarVectors.get(allToolBarVectors.size() - 1)).setName("Benutzerdefinierte Konstruktionsleiste");
			((ToolBarVector) geonext.allToolBarVectors.get(allToolBarVectors.size() - 1)).setType("USER");
			((ToolBarVector) geonext.allToolBarVectors.get(allToolBarVectors.size() - 1)).setDescription("Aktuelle benutzerdefinierte Konstruktionsleiste");
			geonext.activeLeftToolBar = actLeftToolBar;
			geonext.pane.remove(geonext.leftToolBar.toolBar);
			geonext.leftToolBar = new LeftToolBar((ToolBarVector) geonext.allToolBarVectors.get(geonext.activeLeftToolBar), geonext);
			geonext.pane.add((JToolBar) (geonext.leftToolBar.toolBar), BorderLayout.WEST);
			geonext.pane.doLayout();
			geonext.leftToolBar.showAllComponents();
			geonext.activeBottomToolBar.showAllComponents();
			geonext.topToolBar.showAllComponents();
			geonext.makeLayout();
			try {
				geonext.actionPerformed(new ActionEvent(geonext.leftToolBar.modeButton.get(0), 0, ""));
			} catch (Exception exc) {}
		}
		try {
			setVisible(false);
			finalize();
		} catch (java.lang.Throwable thr) {}
	}
	/**
	 * 
	 */
	public void stateChanged(javax.swing.event.ChangeEvent e) {
		if (tPane.getSelectedIndex() == 1) {
			configToolBar1.popups = (ToolBarVector) ((ToolBarVector) allToolBarVectors.get(actLeftToolBar)).clone();
			configToolBar1.updateTree();
			try {
				configToolBar1.tree.setSelectionRow(1);
			} catch (Exception exc) {
				configToolBar1.tree.setSelectionRow(0);
			}
			//configToolBar1.change=false;
			//	actLeftToolBar=geonext.activeLeftToolBar;
		}
		if (configToolBar1.change) {
			if (((ToolBarVector) allToolBarVectors.get(actLeftToolBar)).getType().toLowerCase().equals("internal")) {
				allToolBarVectors.removeElementAt(geonext.allToolBarVectors.size() - 1);
				allToolBarVectors.addElement(configToolBar1.popups.clone());
				actLeftToolBar = geonext.allToolBarVectors.size() - 1;
				configToolBar2.updateTree();
				configToolBar2.userDefined.setSelected(true);
			} else {
				allToolBarVectors.removeElementAt(actLeftToolBar);
				allToolBarVectors.add(actLeftToolBar, configToolBar1.popups.clone());
				//				actLeftToolBar = geonext.allToolBarVectors.size() - 1;
				configToolBar2.updateTree();
				//				configToolBar2.userDefined.setSelected(true);
			}
		}
	}
	/**
	 * 
	 */
	public void validateTree() {
		super.validateTree();
		/*	setSize(getPreferredSize());
			tPane.setSize(tPane.getPreferredSize());
			super.validateTree();
			doLayout();*/
	}
	/**
	 * 
	 */
	public void valueChanged(javax.swing.event.ListSelectionEvent e) {}
}