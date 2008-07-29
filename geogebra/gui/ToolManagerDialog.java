/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;
import geogebra.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog to manage existing user defined tools (macros).
 * 
 * @author Markus Hohenwarter
 */
public class ToolManagerDialog extends javax.swing.JDialog {	
			
	private Application app;	
	private DefaultListModel toolsModel;
	
	public ToolManagerDialog(Application app) {
		super(app.getFrame());
		setModal(true);
		
		this.app = app;
		initGUI();
	}
	
	public void setVisible(boolean flag) {
		if (flag) {
			app.setMoveMode();
		} else {
			// recreate tool bar of application window
			updateToolBar(toolsModel);	
		}
		
		super.setVisible(flag);					
	}
			
	/**
	 * Updates the order of macros using the listModel.
	 */
	private void updateToolBar(DefaultListModel listModel) {		
		// update order of macros:
		// remove all macros from kernel and add them again in new order
		Kernel kernel = app.getKernel();				
		kernel.removeAllMacros();		
		int size = listModel.getSize();		
		for (int i=0; i < size; i++) {
			kernel.addMacro((Macro) listModel.getElementAt(i));
		}
	
		app.updateToolBar();
	}
	
	/**
	 * Deletes all selected tools that are not used
	 * in the construction.	 
	 */
	private void deleteTools(JList toolList, DefaultListModel listModel) {		
		Object [] sel = toolList.getSelectedValues();				
		if (sel == null || sel.length == 0) return;	
					
		// ARE YOU SURE ?
    	// Michael Borcherds 2008-05-04
    	Object[] options = { app.getMenu("DeleteTool"), app.getMenu("DontDeleteTool") };
    	int	returnVal=    
		JOptionPane.showOptionDialog(this, app.getMenu("Tool.DeleteQuestion"), app.getPlain("Question"),
         JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
         null, options, options[1]);     
    	if (returnVal == 1 ) return;
    	
/*		int returnVal =
            JOptionPane.showConfirmDialog(
                this,
                app.getMenu("Tool.DeleteQuestion"),
                app.getPlain("Question"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (returnVal == JOptionPane.NO_OPTION ) 
        	return;	       	    */    				
		
        boolean didDeletion = false;
		boolean changeToolBar = false;
		boolean foundUsedMacro = false;
		String macroNames = "";
		Kernel kernel = app.getKernel();
		for (int i=0; i < sel.length; i++) {
			Macro macro = (Macro) sel[i];				
			if (!macro.isUsed()) {
				// delete macro
				changeToolBar = changeToolBar || macro.isShowInToolBar();
				app.removeFromToolbarDefinition(kernel.getMacroID(macro) + EuclidianView.MACRO_MODE_ID_OFFSET);				
				kernel.removeMacro(macro);
				listModel.removeElement(macro);
				didDeletion = true;
			} else {
				// don't delete, remember name
				foundUsedMacro = true;
				macroNames += "\n" + macro.getToolOrCommandName() + ": " + macro.getNeededTypesString();
			}					
		}			
		
		if (didDeletion){
			// we reinit the undo info to make sure an undo does not use
			// any deleted tool
			kernel.initUndoInfo();
		}
		
		if (changeToolBar) {
			updateToolBar(listModel);			
		}
		
		if (foundUsedMacro)
			app.showError(app.getError("Tool.DeleteUsed") + ": " + macroNames);										
	}
	
	/**
	 * Saves all selected tools in a new file.	 
	 */
	private void saveTools(JList toolList) {
		Object [] sel = toolList.getSelectedValues();				
		if (sel == null || sel.length == 0) return;		
		
		File file =
			app.showSaveDialog(Application.FILE_EXT_GEOGEBRA_TOOL, null,
	                	app.getPlain("ApplicationName") + " " + app.getMenu("Tools"));
        if (file == null)
            return;
        
        // we need to save all selected tools and all tools
        // that are used by the selected tools
        LinkedHashSet tools = new LinkedHashSet();
        for (int i=0; i < sel.length; i++) {
        	Macro macro = (Macro) sel[i];
        	ArrayList macros = macro.getUsedMacros();
        	if (macros != null)
        		tools.addAll(macros);
        	tools.add(macro);        	
        }
                
        // create Macro array list from tools set
        ArrayList macros = new ArrayList(tools.size());
        Iterator it = tools.iterator();
        while (it.hasNext()) {
        	 macros.add(it.next());
        }
               
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
				
			toolsModel = new DefaultListModel();			
			insertTools(toolsModel);					
			final JList toolList = new JList(toolsModel);					
			toolList.setCellRenderer(new MacroCellRenderer());
			toolList.setVisibleRowCount(6);
			
			JPanel centerPanel = ToolCreationDialog.createListUpDownRemovePanel(app, toolList, null, false, true);
													
			//JScrollPane jScrollPane1 = new JScrollPane(toolList);
			//jScrollPane1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED ));
			//toolListPanel.add(jScrollPane1, BorderLayout.CENTER);					
			toolListPanel.add(centerPanel, BorderLayout.CENTER);	
			
			JPanel toolButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
			final JButton btClose = new JButton(app.getMenu("Close"));
			closePanel.add(btClose);
			panel.add(closePanel, BorderLayout.SOUTH);
			
			// action listener for buttone
			ActionListener ac = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object src = e.getSource();										
					if (src == btClose) {	
						// ensure to set macro properties from namePanel
						namePanel.init(null, null); 
										
						// make sure new macro command gets into dictionary
						app.updateCommandDictionary();														
						
						// destroy dialog
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
						namePanel.init(null, null);																	
					} else {									
						Macro macro = (Macro) toolsModel.getElementAt(selIndices[0]);			
						namePanel.init(ToolManagerDialog.this, macro);	
					}																		
				}				
			};
			selModel.addListSelectionListener(selListener);		
						
			// select first tool in list
			if (toolsModel.size() > 0)
				toolList.setSelectedIndex(0);
			else
				namePanel.init(null, null);
			
			setResizable(false);
			namePanel.setPreferredSize(new Dimension(400, 200));
			pack();				
			setLocationRelativeTo(app.getFrame()); //	center	
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
	        	sb.append(macro.getNeededTypesString());
	        	sb.append("</html>");
	        	setText(sb.toString());
	        	
	        	BufferedImage img = app.getExternalImage(macro.getIconFileName());
	        	if (img != null) {
	        		setIcon(new ImageIcon(img));
	        		Dimension dim = getPreferredSize();
	        		dim.height = img.getHeight();
	        		setPreferredSize(dim);
	        		setMinimumSize(dim);
	        	}
	        }
	        return this;
	    }
	}

}
