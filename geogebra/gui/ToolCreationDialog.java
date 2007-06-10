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
import geogebra.GeoElementSelectionListener;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;


/**
 * Dialog to create a new user defined tool
 * 
 * @author Markus Hohenwarter
 */
public class ToolCreationDialog extends javax.swing.JDialog
implements GeoElementSelectionListener {
			
	private Application app;
	private JTabbedPane tabbedPane;		
	private ToolNameIconPanel namePanel;	
	
	private DefaultListModel outputList, inputList;
	private DefaultComboBoxModel cbOutputAddList, cbInputAddList;
	
	private Macro newTool;
	
	public ToolCreationDialog(Application app) {
		super(app.getFrame());
		this.app = app;
			
		initLists();						
		initGUI();
	}
	
	public void setVisible(boolean flag) {		
		super.setVisible(flag);
		
		if (flag) {
			// add all currently selected geos to output list
			ArrayList selGeos = app.getSelectedGeos();
			for (int i=0; i < selGeos.size(); i++) {
				GeoElement geo = (GeoElement) selGeos.get(i);
				addToOutputList(geo);
			}
			
			app.setMoveMode();			
			app.setSelectionListenerMode(this);			
		} else {
			app.setSelectionListenerMode(null);		
		}					
	}
	
	private void addToOutputList(GeoElement geo) {
		if (geo.isIndependent() || outputList.contains(geo))
			return;
		
		// add geo to list
		outputList.addElement(geo);
		
		// remove listener from output add combobox before removing geo
		JComboBox cbListener = removeListeningJComboBox(cbOutputAddList);	
		cbOutputAddList.removeElement(geo);
		cbOutputAddList.addListDataListener(cbListener);
		
		// special case for polygon: add all points and segments too
		if (geo.isGeoPolygon()) {
			GeoPolygon poly = (GeoPolygon) geo;
			GeoPoint [] points = poly.getPoints();
			for (int i=0; i < points.length; i++) {
				addToOutputList(points[i]);				
			}
			GeoSegment [] segments = poly.getSegments();
			for (int i=0; i < segments.length; i++) {
				addToOutputList(segments[i]);
			}
		}
	}
	
	private void addToInputList(GeoElement geo) {
		if (!possibleInput(geo) || inputList.contains(geo))
			return;
		
		// add geo to list
		inputList.addElement(geo);	
		
		// remove listener from input add combobox before removing geo
		JComboBox cbListener = removeListeningJComboBox(cbInputAddList);	
		cbInputAddList.removeElement(geo);
		cbInputAddList.addListDataListener(cbListener);
	}
	
	
	private boolean createTool() {		
		// get input and output objects		
		GeoElement [] input = toGeoElements(inputList);
		GeoElement [] output = toGeoElements(outputList);
		
		// try to create macro	
		Kernel kernel = app.getKernel();
		try {
			newTool = new Macro(kernel, "newTool", input, output);	
			return true;
		} catch (Exception e) {				
			// go back to output tab
			tabbedPane.setSelectedIndex(1);		
			
			// show error message
			app.showError(app.getError("Tool.CreationFailed") 
								+ "\n" + e.getMessage());			
			newTool = null;			
			return false;
		}							
	}
	
	private void finish() {
		// check if command name is not used already by another macro
		String cmdName = namePanel.getCommandName();
		Kernel kernel = app.getKernel();
		if (kernel.getMacro(cmdName) != null) {
			app.showError("Tool.CommandNameTaken");
			return;
		}
		
		newTool.setCommandName(namePanel.getCommandName());
		newTool.setToolName(namePanel.getToolName());
		newTool.setToolHelp(namePanel.getToolHelp());			
		newTool.setShowInToolBar(namePanel.showInToolBar());
		newTool.setIconFileName(namePanel.getIconFileName());	
		kernel.addMacro(newTool);
		
		// hide and dispose dialog
		setVisible(false);	
		dispose();
		
		// make sure new macro command gets into dictionary
		app.updateCommandDictionary();
		
		// set macro mode
		if (newTool.isShowInToolBar()) {
			int mode = kernel.getMacroID(newTool) + EuclidianView.MACRO_MODE_ID_OFFSET;
			app.addToToolbarDefinition(mode);			
			app.updateToolBar();			
			app.setMode(mode);			
		}		
			
		app.showMessage(app.getMenu("Tool.CreationSuccess"));				
	}
	
	/**
	 * Updates the list of input objects by using the specified
	 * output objects.
	 */
	private void updateInputList() {
		// only change empty input list
		if (inputList.size() > 0) return;
		
		// get output objects				
		GeoElement [] output = toGeoElements(outputList);
		
		// determine all free parents of output
		TreeSet freeParents = new TreeSet();
		for (int i=0; i < output.length; i++) {
			output[i].addPredecessorsToSet(freeParents, true);
		}
		
		// remove listener from input add combobox
		JComboBox cbListener = removeListeningJComboBox(cbInputAddList);
		
		// fill input list with labeled free parents
		Iterator it = freeParents.iterator();		
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			if (geo.isLabelSet()) {
				inputList.addElement(geo);												
				cbInputAddList.removeElement(geo);						
			}
		}
		
		// add JComboBox listener to cbInputAddList again
		cbInputAddList.addListDataListener(cbListener);
	}
	
	private JComboBox removeListeningJComboBox(DefaultComboBoxModel cbModel) {
		// we need to remove the JComboBox as listener from the cbInputAddList
		// temporarily to avoid multiple additions to inputList
		ListDataListener [] listeners = cbModel.getListDataListeners();
		JComboBox cbListener = null;
		for (int i=0; i < listeners.length; i++) {
			if (listeners[i] instanceof JComboBox) {
				cbListener = (JComboBox) listeners[i];
				break;
			}
		}					
		cbModel.removeListDataListener(cbListener);
		return cbListener;
	}
	
	private GeoElement [] toGeoElements(DefaultListModel listModel) {
		// get output objects
		int size = listModel.size();		
		GeoElement [] geos = new GeoElement[size];
		for (int i=0; i < size; i++) {
			geos[i] = (GeoElement) listModel.get(i);
		}	
		return geos;
	}
	
	private void initLists() {
		// lists for input and output objects
		inputList = new DefaultListModel();
		outputList = new DefaultListModel();
				
		// lists for combo boxes to select input and output objects
				
		// sorted set of geos
		TreeSet sortedSet = new TreeSet(new NameDescriptionComparator());
		
		// get all GeoElements from construction and sort their names
		Construction cons = app.getKernel().getConstruction();
		Iterator it = cons.getGeoSetConstructionOrder().iterator();	
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			// sorted inserting using name description of geo
			sortedSet.add(geo);			
		}		
		
		// input and output objects list
		cbInputAddList = new DefaultComboBoxModel();							
		cbOutputAddList = new DefaultComboBoxModel();
		it = sortedSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();				
			if (possibleInput(geo)) {
				cbInputAddList.addElement(geo);	
			}			
			if (!geo.isIndependent()) {
				cbOutputAddList.addElement(geo);	
			}
		}								
	}		
	
	/**
	 * Returns whether geo can be used as an input object.
	 * @param geo
	 * @return
	 */
	private boolean possibleInput(GeoElement geo) {
		return geo.hasChildren();
	}
	
	private void initGUI() {
		try {
			setTitle(app.getMenu("Tool.CreateNew"));
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			
			// Tabbed Pane
			tabbedPane = new JTabbedPane();
			getContentPane().add(tabbedPane, BorderLayout.CENTER);
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			// Button panel
			JPanel navPanel = createNavigationPanel();
			getContentPane().add(navPanel, BorderLayout.SOUTH);									
							
			// output and input panel
			JPanel outputPanel = createInputOutputPanel(outputList, cbOutputAddList);
			JPanel inputPanel = createInputOutputPanel(inputList, cbInputAddList);
			
			tabbedPane.addTab(app.getMenu("OutputObjects"), null, outputPanel, null);
			tabbedPane.addTab(app.getMenu("InputObjects"), null, inputPanel, null);															
			
			// name & icon
			namePanel = new ToolNameIconPanel(app);
			tabbedPane.addTab(app.getMenu("NameIcon"), null, namePanel, null);									
			
			setResizable(false);			
			pack();
			// center
			setLocationRelativeTo(app.getFrame());								
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JPanel createNavigationPanel() {		
		JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));		
		btPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 5));
							
		final JButton btBack = new JButton();
		btPanel.add(btBack);		
		btBack.setText("< " + app.getPlain("Back") );	
		
		final JButton btNext = new JButton();
		btPanel.add(btNext);		
		btNext.setText(app.getPlain("Next") + " >");			
		
		final JButton btCancel = new JButton();
		btPanel.add(Box.createRigidArea(new Dimension(10,0)));
		btPanel.add(btCancel);		
		btCancel.setText(app.getPlain("Cancel"));	
		
		ActionListener ac = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				if (src == btNext) {
					int index = tabbedPane.getSelectedIndex() + 1;				
					if (index == tabbedPane.getTabCount()) {
						finish();
					} else {						
						tabbedPane.setSelectedIndex(index);
					}
				}
				else if (src == btBack) {
					int index = tabbedPane.getSelectedIndex() - 1;
					tabbedPane.setSelectedIndex(index);
				}
				else if (src == btCancel) {
					setVisible(false);
				}
			}			
		};
		btCancel.addActionListener(ac);
		btNext.addActionListener(ac);
		btBack.addActionListener(ac);
		
		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int tab = tabbedPane.getSelectedIndex();
				btBack.setEnabled(tab > 0);
				
				switch (tab) {								
					case 1: // input objects
						updateInputList();
						
					case 0: // output objects
						btNext.setText(app.getPlain("Next") + " >");	
						btNext.setEnabled(true);
						break;	
						
					case 2: // name panel (finish)
						if (createTool()) {
							btNext.setText(app.getPlain("Finish"));	
							btNext.setEnabled(inputList.size() > 0 && outputList.size() > 0);
							namePanel.requestFocus();
						} 
						break;				
				}				
			}			
		};		
		tabbedPane.addChangeListener(cl);
		return btPanel;
	}			
	
	/** 
	 * Creates a panel with a list to choose input/output objects of the new tool.
	 */
	private JPanel createInputOutputPanel(final DefaultListModel listModel, final DefaultComboBoxModel cbModel) {		
		JPanel panel = new JPanel(new BorderLayout(5, 5));		
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// NORTH: text
		JLabel labelAddOutput = new JLabel(app.getMenu("Tool.SelectObjects"));
		panel.add(labelAddOutput, BorderLayout.NORTH);																		
					
		// CENTER: combobox, list and some buttons on the right						
		// combobox to add geos				
		final JComboBox cbAdd = new JComboBox(cbModel);
		// listener for the combobox
		MyComboBoxListener ac = new MyComboBoxListener() {
			public void doActionPerformed(Object source) {				
				GeoElement geo = (GeoElement) cbAdd.getSelectedItem();		
				if (geo == null) return;
				
				if (listModel == outputList)
					addToOutputList(geo);
				else if (listModel == inputList)
					addToInputList(geo);											
			}
		};
		cbAdd.addActionListener(ac);
		cbAdd.addMouseListener(ac);
		
		// list to show selected geos
		JList list = new JList(listModel);												
		panel.add(createListUpDownRemovePanel(app, list, cbAdd, true), BorderLayout.CENTER);			
		
		// renderer to show long description of geos in list and combobox
		MyCellRenderer rend = new MyCellRenderer();
		list.setCellRenderer(rend);
		cbAdd.setRenderer(rend);
						
		return panel;
	}	
	
	/**
	 * Creates a panel with a list on the left and buttons (up, down, remove) on the right.
	 * If the combobox is not null it is added on top of the list.
	 * @param listPanel
	 * @param list
	 */
	public static JPanel createListUpDownRemovePanel(Application app, final JList list, final JComboBox cbAdd, boolean showRemoveButton) {
		JPanel centerPanel = new JPanel(new BorderLayout(5,5));
		
		JPanel listPanel = new JPanel(new BorderLayout(5,3));
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		if (cbAdd != null)
			listPanel.add(cbAdd, BorderLayout.NORTH);
		listPanel.add(scrollPane, BorderLayout.CENTER);
		centerPanel.add(listPanel, BorderLayout.CENTER);											
		
		// buttons on the right
		JPanel outputButtonPanel = new JPanel();
		BoxLayout outputButtonPanelLayout = new BoxLayout(
			outputButtonPanel,
			javax.swing.BoxLayout.Y_AXIS);
		outputButtonPanel.setLayout(outputButtonPanelLayout);
		
		final JButton btUp = new JButton("\u25b2");
		btUp.setToolTipText(app.getPlain("Up"));
		final JButton btDown = new JButton("\u25bc");
		btDown.setToolTipText(app.getPlain("Down"));
		if (cbAdd != null)
			outputButtonPanel.add(Box.createRigidArea(new Dimension(0,30)));
		outputButtonPanel.add(btUp);
		outputButtonPanel.add(Box.createRigidArea(new Dimension(0,5)));
		outputButtonPanel.add(btDown);
				
		final JButton btRemove = new JButton("\u2718");
		btRemove.setVisible(showRemoveButton);
		btRemove.setToolTipText(app.getPlain("Remove"));
		outputButtonPanel.add(Box.createRigidArea(new Dimension(0,15)));
		outputButtonPanel.add(btRemove);				
		centerPanel.add(outputButtonPanel, BorderLayout.EAST);								
						
		// listener for buttons
		ActionListener ac = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				DefaultListModel listModel = (DefaultListModel) list.getModel();
				int [] selIndices = list.getSelectedIndices();
				if (src == btUp && selIndices != null) {
					for (int i=0; i < selIndices.length; i++) {
						int index = selIndices[i];
						if (index > 0) { 
							Object ob = listModel.get(index);
							listModel.remove(index);
							listModel.add(index-1, ob);
							selIndices[i] = index-1;
						}									
					}
					list.setSelectedIndices(selIndices);
				}
				else if (src == btDown && selIndices != null) {
					for (int i=selIndices.length-1; i >= 0; i--) {
						int index = selIndices[i];
						if (index < listModel.size()-1) { 
							Object ob = listModel.get(index);
							listModel.remove(index);
							listModel.add(index+1, ob);
							selIndices[i] = index+1;
						}
					}
					list.setSelectedIndices(selIndices);
				}
				else if (src == btRemove && selIndices != null) {
					NameDescriptionComparator comparator = new NameDescriptionComparator();
					for (int i=selIndices.length-1; i >= 0; i--) {
						if (cbAdd != null) {
							DefaultComboBoxModel cbModel = (DefaultComboBoxModel) cbAdd.getModel();
							
							//	take from list and insert sorted into add-combobox
							GeoElement geo = (GeoElement) listModel.elementAt(selIndices[i]);
							int k=0;
							for (; k < cbModel.getSize(); k++) {
								GeoElement cbGeo = (GeoElement) cbModel.getElementAt(k);
								if (comparator.compare(geo, cbGeo) <= 0) {									
									break;
								}									
							}
							cbModel.insertElementAt(geo, k);
						}
						
						// remove from list
						listModel.remove(selIndices[i]);							
					}
				}				
			}			
		};		
		btUp.addActionListener(ac);
		btDown.addActionListener(ac);
		btRemove.addActionListener(ac);
		
		return centerPanel;
	}
	
	private class MyCellRenderer extends DefaultListCellRenderer {			  
	    /* This is the only method defined by ListCellRenderer.  We just
	     * reconfigure the Jlabel each time we're called.
	     */
	    public Component getListCellRendererComponent(
	        JList list,
		Object value,   // value to display
		int index,      // cell index
		boolean iss,    // is the cell selected
		boolean chf)    // the list and the cell have the focus
	    {
	        /* The DefaultListCellRenderer class will take care of
	         * the JLabels text property, it's foreground and background
	         * colors, and so on.
	         */
	        super.getListCellRendererComponent(list, value, index, iss, chf);
	       
	        if (value != null) {
	        	GeoElement geo = (GeoElement) value;	     
	        	setText(geo.getLongDescriptionHTML(true, true));
	        }
	        return this;
	    }
	}

	/**
	 * Adds selected geo to input/output list of dialog.
	 */
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		int tab = tabbedPane.getSelectedIndex();							
		switch (tab) {
			case 0: // output objects								
				addToOutputList(geo);
				break;
				
			case 1: // input objects								
				addToInputList(geo);
				break;			
		}
		
		
	}	

}

class NameDescriptionComparator implements Comparator {				
	
	public int compare(Object ob1, Object ob2) {
		GeoElement geo1 = (GeoElement) ob1;
		GeoElement geo2 = (GeoElement) ob2;
		return geo1.getNameDescription().compareTo(geo2.getNameDescription());			
	}				
}


