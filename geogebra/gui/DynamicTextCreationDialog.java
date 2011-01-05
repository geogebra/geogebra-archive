/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.JTextComponent;

/**
 * Dialog to create a GeoBoolean object (checkbox) that determines the
 * visibility of a list of objects.
 */
public class DynamicTextCreationDialog extends JDialog 
implements WindowFocusListener, ActionListener, GeoElementSelectionListener, KeyListener, ListDataListener {
	
	private static final long serialVersionUID = 1L;

	private JTextComponent tfFreeText;
	private JButton btApply, btCancel, btAdd;
	private JPanel optionPane, btPanel;
	private JLabel previewLabel;
	private DefaultListModel listModel;
	private DefaultComboBoxModel comboModel;
	
	private GeoPoint location;
	private Application app;
	private GeoText geoText;
	
	/**
	 * Dialog to create a Dynamic Text
	 */
	public DynamicTextCreationDialog(Application app, GeoPoint location) {	
		super(app.getFrame(), false);
		this.app = app;
		this.location = location;
		//this.geoText = geoBoolean;
		
		initLists();
		createGUI(app.getMenu("CreateDynamicText"));
		pack();
		setLocationRelativeTo(app.getMainComponent());		
	}
	
	private void initLists() {
		// fill combo box with all geos
		comboModel = new DefaultComboBoxModel();
		TreeSet sortedSet = app.getKernel().getConstruction().
									getGeoSetNameDescriptionOrder();			
		
		// lists for combo boxes to select input and output objects
		// fill combobox models
		Iterator it = sortedSet.iterator();
		comboModel.addElement(null);
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();				
			if (geo.isEuclidianShowable()) {				
				comboModel.addElement(geo);
			}
		}	
		
		// fill list with all selected geos
		listModel = new DefaultListModel() {
			public void addElement(Object ob) {
				//if (contains(ob)) return;
				
				if (ob instanceof GeoElement) {
					GeoElement geo = (GeoElement) ob;
					super.addElement(geo);
					//comboModel.removeElement(geo);
				} else if (ob instanceof String) {
					super.addElement(ob);
				}
			}
		};
		
		// add all selected geos to list
	    for (int i=0; i < app.getSelectedGeos().size(); i++) {
		  GeoElement geo = (GeoElement) app.getSelectedGeos().get(i);
		  listModel.addElement(geo);
	    }	
	}
	
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {				
		listModel.addElement(geo);				
	}
			
	protected void createGUI(String title) {
		setTitle(title);
		setResizable(false);		
		
		// create caption panel
		JLabel freeTextLabel = new JLabel(app.getMenu("FreeText")+":");
		String initString = geoText == null ? "" : geoText.getCaption();
		InputPanel ip = new InputPanel(initString, app, 1, 15, true, false, this);				
		tfFreeText = ip.getTextComponent();
		if (tfFreeText instanceof AutoCompleteTextField) {
			AutoCompleteTextField atf = (AutoCompleteTextField) tfFreeText;
			atf.setAutoComplete(false);
		}
		
		freeTextLabel.setLabelFor(tfFreeText);
		JPanel captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		captionPanel.add(freeTextLabel);
		captionPanel.add(ip);
		
		btAdd = new JButton(app.getMenu("Add"));
		btAdd.addActionListener(this);		
		captionPanel.add(btAdd);
		
		// list panel
		JPanel listPanel = ToolCreationDialog.
			createInputOutputPanel(app, listModel, comboModel, true, true, this);
		
		JPanel previewPanel = new JPanel();
			
		previewPanel.setBorder(
			BorderFactory.createTitledBorder(
					app.getMenu("Preview")
				)
			);
		
		previewLabel = new JLabel(" ");
		previewPanel.add(previewLabel);

		JPanel centerPanel = new JPanel(new BorderLayout(5,10));
		centerPanel.add(listPanel, BorderLayout.NORTH);
		centerPanel.add(previewPanel, BorderLayout.SOUTH);
		
		// buttons
		btApply = new JButton(app.getMenu("CreateText"));
		btApply.setActionCommand("CreateText");
		btApply.addActionListener(this);
		btCancel = new JButton(app.getPlain("Cancel"));
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btPanel.add(btApply);
		btPanel.add(btCancel);
		
			
		//Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5,5));
		
		// create object list
		optionPane.add(captionPanel, BorderLayout.NORTH);
		optionPane.add(centerPanel, BorderLayout.CENTER);	
		optionPane.add(btPanel, BorderLayout.SOUTH);	
		optionPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Make this dialog display it.
		setContentPane(optionPane);				
	}
	
	
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		
		if (src == btCancel) {
			setVisible(false);
		}
		else if (src == btApply) {
			apply();
			setVisible(false);
		}
		else if (src == btAdd) {
			addFreeText();
		}
		else updatePreview(); // object added
	}
	
	private void apply() {
		
			
			geoText = app.getKernel().getAlgebraProcessor().evaluateToText(getText(true), true);
		
		
		geoText.setEuclidianVisible(true);
		try {
			geoText.setStartPoint(location);
		} catch (CircularDefinitionException e) {
			e.printStackTrace();
		}
		geoText.setLabelVisible(true);
		geoText.updateRepaint();
		
		app.getKernel().storeUndoInfo();
	}
	
	public void windowGainedFocus(WindowEvent arg0) {		
		// make sure this dialog is the current selection listener
		if (app.getMode() != EuclidianView.MODE_SELECTION_LISTENER ||
			app.getCurrentSelectionListener() != this) 
		{
			app.setSelectionListenerMode(this);
		}	
	}

	public void windowLostFocus(WindowEvent arg0) {
	}

	public void setVisible(boolean flag) {	
		if (!isModal()) {
			if (flag) { // set old mode again			
				addWindowFocusListener(this);			
			} else {		
				removeWindowFocusListener(this);
				app.setSelectionListenerMode(null);
			}
		}
		super.setVisible(flag);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
				
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			addFreeText();
		}
		
	}

	public void keyReleased(KeyEvent e) {
	}
	
	private void addFreeText() {
		String str = tfFreeText.getText();
		
		if (str.length() == 0) return;

		// replace all " by '
		while (str.indexOf('\"') > -1)
			str = str.replace('\"', '\'');

		listModel.addElement(str);
		tfFreeText.setText("");
		
		updatePreview();
		
	}
	
	private void updatePreview() {
        SwingUtilities.invokeLater( new Runnable(){ public void
        	run() { previewLabel.setText(getText(false));
        	} });
	}
	
	private String getText(boolean forDefinition) {
		StringBuilder text = new StringBuilder();
		if (forDefinition) text.append("\"\""); // means we can just start all with +
		
		// set visibility condition for all GeoElements in list
			for (int i=0; i < listModel.size(); i++) {
				Object obj = listModel.get(i);
				
				if (obj instanceof String) {
					boolean addSpaceAtStart = !((String)obj).startsWith(" ");
					boolean addSpaceAtEnd = !((String)obj).endsWith(" ");
					
					
					String str = text.toString();
					if (str.endsWith(" ") || str.endsWith(" \"")) addSpaceAtStart = false;
					
					
					if (forDefinition) text.append("+\"");
					
					// add space if there's not one already
					if (addSpaceAtStart) text.append(' ');
					
					text.append((String)obj);

					// add space if there's not one already
					if (addSpaceAtEnd) text.append(' ');
					
					if (forDefinition) text.append('\"');
				} else if (obj instanceof GeoElement) {
					if (forDefinition) {
						text.append('+');
						text.append(((GeoElement)obj).getLabel());
						text.append("+\" \""); // add +" " to end
					} else {
						text.append(((GeoElement)obj).toValueString());
						text.append(' '); // add space to end
					}
				}
				
			}
			
			return text.toString();

	}

	public void intervalAdded(ListDataEvent e) {
		updatePreview();
	}

	public void intervalRemoved(ListDataEvent e) {
		updatePreview();
	}

	public void contentsChanged(ListDataEvent e) {
		updatePreview();
	}

}
