/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.gui.toolbar;
import geogebra.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class ToolbarConfigPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, javax.swing.event.TreeExpansionListener {	
	
	public JButton insertButton;
	public JButton moveUpButton;
	public JButton moveDownButton;
	public JButton deleteButton;
	public JTree tree;
	JScrollPane configScrollPane;
	JScrollPane modeScrollPane;
	JScrollPane iconScrollPane;
	JPanel selectionPanel;
	JList modeList;
	String toolbar;
	int selectedRow;	
	Application app;
	
	/**
	 * 
	 */
	public ToolbarConfigPanel(Application app) {
		super();	
		this.app = app;
		
		selectionPanel = new JPanel();
		selectionPanel.setLayout(new BorderLayout(10, 10));
		selectionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout(5, 5));
				
		tree = generateTree(MyToolbar.createToolBarVec(app.getToolBarDefinition()));		
		expandAllRows();
		
		configScrollPane = new JScrollPane(tree);
		configScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		configScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//configScrollPane.setSize(150, 150);
		//configScrollPane.setPreferredSize(new Dimension(150, 150));
		JPanel scrollSpacePanel = new JPanel();
		scrollSpacePanel.setLayout(new BorderLayout(0, 0));
		scrollSpacePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		scrollSpacePanel.add(configScrollPane, BorderLayout.CENTER); //
		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BorderLayout(0, 0));
		scrollPanel.setBorder(new TitledBorder(app.getMenu("Toolbar")));
		scrollPanel.add(scrollSpacePanel, BorderLayout.CENTER);
		//
		selectionPanel.add(scrollPanel, BorderLayout.WEST);
		//
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
		
		final JButton btUp = new JButton("\u25b2");
		btUp.setToolTipText(app.getPlain("Up"));
		final JButton btDown = new JButton("\u25bc");
		btDown.setToolTipText(app.getPlain("Down"));
		
		//
		insertButton = new JButton("\u25c0" + app.getPlain("Insert"));
		insertButton.setName("insert");
		insertButton.addActionListener(this);
		buttonPanel.add(insertButton);
		//		
		insertButton = new javax.swing.JButton("\u25b6" + app.getPlain("Remove"));
		insertButton.setName("delete");
		insertButton.addActionListener(this);
		buttonPanel.add(insertButton);
		//		
		moveUpButton = new javax.swing.JButton("\u25b2" + app.getPlain("Up"));
		moveUpButton.setName("moveUp");
		moveUpButton.addActionListener(this);
		buttonPanel.add(moveUpButton);
		//
		moveDownButton = new javax.swing.JButton("\u25bc" + app.getPlain("Down"));
		moveDownButton.setName("moveDown");
		moveDownButton.addActionListener(this);
		buttonPanel.add(moveDownButton);

		//
		JPanel buttonAllPanel = new JPanel();
		buttonAllPanel.setLayout(new BorderLayout(0, 0));		
		buttonAllPanel.add("North", buttonPanel);
		buttonAllPanel.add("Center", new JPanel());
		//
		selectionPanel.add("Center", buttonAllPanel);
		//
		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BorderLayout(0, 0));
		modePanel.setBorder(new TitledBorder(app.getPlain("Tools")));
		//modePanel.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), " " + "Wählbare Einträge" + " "));
		//
		Vector modeVector = generateToolsVector();
		modeList = new JList(modeVector);
		//modeList.setPreferredSize(new Dimension(150, 150));
		//
		//modeList.setSize(new Dimension(150, 150));
		//modeList.addListSelectionListener(this);
		//
		ListSelectionModel lsm = modeList.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		modeList.setBackground(configScrollPane.getBackground());
		modeScrollPane = new JScrollPane(modeList);
		modeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		modeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//modeList.setCellRenderer(new ImageCellRenderer(tree.getBackground(), modeList.getForeground(), modeList.getSelectionBackground(), modeList.getSelectionForeground()));
		modeList.setSelectedIndex(0);
		//
		//
		JPanel modeSpacePanel = new JPanel();
		modeSpacePanel.setLayout(new BorderLayout(0, 0));
		modeSpacePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		modeSpacePanel.add("Center", modeScrollPane);
		//
		modeSpacePanel.setPreferredSize(new Dimension(175, 175));
		//
		modeSpacePanel.setSize(new Dimension(175, 175));
		//
		modePanel.add("Center", modeSpacePanel);
		selectionPanel.add("East", modePanel);
		selectionPanel.doLayout();
		add("Center", selectionPanel);
		//
		/*JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		okButton = new javax.swing.JButton("OK");
		okButton.setName("ok");
		okButton.addActionListener(this);
		controlPanel.add(okButton);
		cancelButton = new javax.swing.JButton("Abbrechen");
		cancelButton.setName("cancel");
		cancelButton.addActionListener(this);
		controlPanel.add(cancelButton);
		controlPanel.doLayout();*/
		//add(controlPanel, BorderLayout.SOUTH);
		//
		//doLayout();
		//this.validateTree();
		//modeScrollPane.setPreferredSize(new Dimension(Math.max(modeScrollPane.getWidth(), configScrollPane.getWidth()), (int) configScrollPane.getSize().getHeight()));
		//modeScrollPane.setSize(modeList.getPreferredSize());
		//modeScrollPane.setSize(modeScrollPane.getPreferredSize());
		//configScrollPane.setPreferredSize(new Dimension(Math.max(modeScrollPane.getWidth(), configScrollPane.getWidth()), (int) configScrollPane.getSize().getHeight()));
		
		expandAllRows();
		try {
			tree.setSelectionRow(1);
		} catch (Exception exc) {
			tree.setSelectionRow(0);
		}				
	}
		
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO: implement
		System.out.println("actionPerformed: " + e);
	}
	
	public void collapseAllRows() {
		int z = tree.getRowCount();
		for (int i = z; i >= 0; i--) {
			tree.collapseRow(i);
		}
	}
	
	public void doLayout() {
		super.doLayout();
	}
	
	public void expandAllRows() {
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}
	/**
	 * 
	 */
	public void expandAllRows(JTree tree) {}
	
	/**
	 * 
	 */
	public Vector generateToolsVector() {				
		Vector vector = new Vector();		
		// separator
		vector.add(new Integer(-1));
				
		// get default toolbar as nested vectors
		Vector defTools = MyToolbar.createToolBarVec(app.getToolbar().getDefaultToolbarString());				
		for (int i=0; i < defTools.size(); i++) {
			Object element = defTools.get(i);
			
			if (element instanceof Vector) {
				Vector menu = (Vector) element;
				for (int j=0; j < menu.size(); j++) {
					Integer modeInt = (Integer) menu.get(j);
					int mode = modeInt.intValue();
					if (mode != -1)
						vector.add(modeInt);
				}
			} else {
				Integer modeInt = (Integer) element;
				int mode = modeInt.intValue();
				if (mode != -1)
					vector.add(modeInt);
			}			
		}				
		return vector;
	}
	/**
	 *
	 */
	public JTree generateTree(Vector toolbarModes) {
		JTree jTree = new JTree(generateRootNode(toolbarModes));
		jTree.setCellRenderer(new ModeCellRenderer(app));
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree.putClientProperty("JTree.lineStyle", "Angled");
		jTree.addTreeExpansionListener(this);
		return jTree;
	}
	/**
	 * 
	 */
	public DefaultMutableTreeNode generateRootNode(Vector toolbarModes) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode();
		
		for (int i = 0; i < toolbarModes.size(); i++) {
			Object ob = toolbarModes.get(i);
			if (ob instanceof Vector) {
				Vector menu = (Vector) ob;  
				DefaultMutableTreeNode sub = new DefaultMutableTreeNode();
				for (int j = 0; j < menu.size(); j++) {
					sub.add(new DefaultMutableTreeNode(menu.get(j)));
				}
				node.add(sub);
			}
			else
				node.add(new DefaultMutableTreeNode(ob));
		}
		return node;
	}
	
	/**
	 * 
	 */
	public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {}
	/**
	 * 
	 */
	public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
		/*tabbed.invalidate();
		tabbed.validateTree();*/
	}
	
	/**
	 * 
	 */
	public void valueChanged(javax.swing.event.ListSelectionEvent e) {}
}