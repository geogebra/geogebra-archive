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
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog to manage existing user defined tools (macros).
 * 
 * @author Markus Hohenwarter
 */
public class ToolManagerDialog extends javax.swing.JDialog {	
			
	private Application app;	
	
	public ToolManagerDialog(Application app) {
		super(app.getFrame());
		setModal(true);
		
		this.app = app;
		initGUI();
	}
	
	/**
	 * Deletes all selected tools that are not used
	 * in the construction.	 
	 */
	private void deleteTools(JList toolList, DefaultListModel listModel) {		
		Object [] sel = toolList.getSelectedValues();				
		if (sel == null) return;	
					
		// ARE YOU SURE ?
		int returnVal =
            JOptionPane.showConfirmDialog(
                this,
                app.getMenu("Tool.DeleteQuestion"),
                app.getPlain("Question"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (returnVal == JOptionPane.NO_OPTION ) 
        	return;	       	        				
		
        boolean didDeletion = false;
		boolean changeToolBar = false;
		boolean foundUsedMacro = false;
		String macroNames = "";
		for (int i=0; i < sel.length; i++) {
			Macro macro = (Macro) sel[i];				
			if (!macro.isUsed()) {
				// delete macro
				changeToolBar = changeToolBar || macro.isShowInToolBar();
				app.getKernel().removeMacro(macro);
				listModel.removeElement(macro);
				didDeletion = true;
			} else {
				// don't delete, remember name
				foundUsedMacro = true;
				macroNames += "\n" + macro.getToolOrCommandName() + ": " + macro.getToolHelpOrNeededTypes();
			}					
		}			
		
		if (didDeletion){
			// we reinit the undo info to make sure an undo does not use
			// any deleted tool
			app.getKernel().initUndoInfo();
		}
		
		if (changeToolBar)
			app.updateToolBar();			
		
		if (foundUsedMacro)
			app.showError(app.getError("Tool.DeleteUsed") + ": " + macroNames);										
	}
	
	/**
	 * Saves all selected tools in a new file.	 
	 */
	private void saveTools(JList toolList) {
		Object [] sel = toolList.getSelectedValues();				
		if (sel == null) return;	
		
		File file =
			app.showSaveDialog(Application.FILE_EXT_GEOGEBRA_TOOL, null,
	                			app.getPlain("ApplicationName") + " " + app.getMenu("Tools"));
        if (file == null)
            return;
        
        // create Macro array from selection
        Macro [] macros = new Macro[sel.length];
        for (int i=0; i < sel.length; i++)
        	macros[i] = (Macro) sel[i];
        
        // save selected macros
        app.saveMacroFile(file, macros);							
	}
	
	private void initGUI() {
		try {
			setTitle(app.getMenu("Tool.Manage"));			
			
			JPanel panel = new JPanel(new BorderLayout(5,5));
			setContentPane(panel);
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			JPanel toolListPanel = new JPanel(new BorderLayout(5, 5));
			toolListPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Tools")));
			getContentPane().add(toolListPanel, BorderLayout.NORTH);				
				
			final DefaultListModel toolsModel = new DefaultListModel();			
			insertTools(toolsModel);					
			final JList toolList = new JList(toolsModel);					
			toolList.setCellRenderer(new MacroCellRenderer());
			toolList.setVisibleRowCount(6);
													
			JScrollPane jScrollPane1 = new JScrollPane(toolList);
			jScrollPane1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED ));
			toolListPanel.add(jScrollPane1, BorderLayout.CENTER);					
				
			JPanel toolButtonPanel = new JPanel();
			toolListPanel.add(toolButtonPanel, BorderLayout.SOUTH);
			
			final JButton btDelete = new JButton();
			toolButtonPanel.add(btDelete);
			btDelete.setText(app.getPlain("Delete"));
						
			final JButton btSave = new JButton();
			toolButtonPanel.add(btSave);
			btSave.setText(app.getMenu("SaveAs") + " ...");
								
			// name & icon			
			final ToolNameIconPanel namePanel = new ToolNameIconPanel(app);		
			namePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("NameIcon")));			
			panel.add(namePanel, BorderLayout.CENTER);
			
			JPanel closePanel = new JPanel();
			FlowLayout closePanelLayout = new FlowLayout();
			closePanelLayout.setAlignment(FlowLayout.RIGHT);
			closePanel.setLayout(closePanelLayout);
			final JButton btClose = new JButton(app.getPlain("Close"));
			closePanel.add(btClose);
			panel.add(closePanel, BorderLayout.SOUTH);
			
			// action listener for buttone
			ActionListener ac = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object src = e.getSource();										
					if (src == btClose) {
						setVisible(false);
						dispose();
					}	
					else if (src == btDelete) {
						deleteTools(toolList, toolsModel);
					}
					else if (src == btSave) {
						saveTools(toolList);
					}
				}				
			};
			btSave.addActionListener(ac);
			btDelete.addActionListener(ac);
			btClose.addActionListener(ac);
							
			
			// add selection listener for list
			final ListSelectionModel selModel = toolList.getSelectionModel();
			ListSelectionListener selListener = new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (selModel.getValueIsAdjusting()) return;
					
					int [] selIndices = toolList.getSelectedIndices();
					if (selIndices == null || selIndices.length != 1) {
						// no or several tools selected
						namePanel.setEnabled(false);
						namePanel.init(null);													
					} else {															
						Macro macro = (Macro) toolsModel.getElementAt(selIndices[0]);
						namePanel.init(macro);						
					}																				
				}				
			};
			selModel.addListSelectionListener(selListener);		
						
			// select first tool in list
			if (toolsModel.size() > 0)
				toolList.setSelectedIndex(0);
			
			setResizable(false);
			namePanel.setPreferredSize(new Dimension(400, 200));
			pack();				
			setLocationRelativeTo(null); //	center	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insertTools(DefaultListModel listModel) {
		Kernel kernel = app.getKernel();
		int size = kernel.getMacroNumber();
		for (int i=0; i < size; i++) {
			Macro macro = kernel.getMacro(i);
			listModel.addElement(macro);
		}				
	}
	
	private class MacroCellRenderer extends DefaultListCellRenderer {			  
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
	        	Macro macro = (Macro) value;
	        	StringBuffer sb = new StringBuffer();
	        	sb.append("<html><b>");
	        	sb.append(macro.getToolName());
	        	sb.append("</b>: ");
	        	sb.append(macro.getToolHelpOrNeededTypes());
	        	sb.append("</html>");
	        	setText(sb.toString());
	        }
	        return this;
	    }
	}

}
