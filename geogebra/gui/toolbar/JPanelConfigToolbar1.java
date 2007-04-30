/*
    GEONExT

    Copyright (C) 2002  GEONExT-Group, Lehrstuhl für Mathematik und ihre Didaktik, Universität Bayreuth

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA. 
*/
package geogebra.gui.toolbar;
import geogebra.Application;
import geogebra.MyToolbar;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.*;
import javax.swing.tree.TreeSelectionModel;
import java.net.URL;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
/**
 * 
 */
class JPanelConfigToolbar1 extends javax.swing.JPanel implements java.awt.event.ActionListener, javax.swing.event.TreeExpansionListener {	
	public javax.swing.JButton okButton;
	public javax.swing.JButton cancelButton;
	public javax.swing.JButton editButton;
	public javax.swing.JButton insertButton;
	public javax.swing.JButton moveUpButton;
	public javax.swing.JButton moveDownButton;
	public javax.swing.JButton deleteButton;
	public JTree tree;
	JScrollPane configScrollPane;
	JScrollPane modeScrollPane;
	JScrollPane iconScrollPane;
	JPanel selectionPanel;
	JList modeList;
	String toolbar;
	int selectedRow;	
	
	boolean change = false;
	/**
	 * 
	 */
	public JPanelConfigToolbar1(String titel, Application app) {
		super();	
		
		selectionPanel = new JPanel();
		selectionPanel.setLayout(new BorderLayout(10, 10));
		selectionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout(5, 5));
				
		tree = generateTree(MyToolbar.createToolBarVec(app.getToolBarDefinition()));		
		expandAllRows();
		
		configScrollPane = new JScrollPane(tree);
		configScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		configScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		configScrollPane.setSize(150, 150);
		configScrollPane.setPreferredSize(new Dimension(150, 150));
		JPanel scrollSpacePanel = new JPanel();
		scrollSpacePanel.setLayout(new BorderLayout(0, 0));
		scrollSpacePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		scrollSpacePanel.add("Center", configScrollPane); //
		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BorderLayout(0, 0));
		scrollPanel.setBorder(new TitledBorder(new EtchedBorder(1), " " + "Aktuelle Konstruktionsleiste" + " "));
		//scrollPanel.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), " " + "Aktuelle Konstruktionsleiste" + " "));
		scrollPanel.add("Center", scrollSpacePanel);
		//
		selectionPanel.add("West", scrollPanel);
		//
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
		//
		insertButton = new javax.swing.JButton("Einfügen", new ImageIcon(getClass().getResource("16/function_left.gif")));
		insertButton.setName("insert");
		insertButton.addActionListener(this);
		buttonPanel.add(insertButton);
		//		
		moveUpButton = new javax.swing.JButton("Nach oben", new ImageIcon(getClass().getResource("16/function_up.gif")));
		moveUpButton.setName("moveUp");
		moveUpButton.addActionListener(this);
		buttonPanel.add(moveUpButton);
		//
		moveDownButton = new javax.swing.JButton("Nach unten", new ImageIcon(getClass().getResource("16/function_down.gif")));
		moveDownButton.setName("moveDown");
		moveDownButton.addActionListener(this);
		buttonPanel.add(moveDownButton);
		//
		//
		insertButton = new javax.swing.JButton("Löschen", new ImageIcon(getClass().getResource("16/mode_delete.gif")));
		insertButton.setName("delete");
		insertButton.addActionListener(this);
		buttonPanel.add(insertButton);
		//
		JPanel buttonAllPanel = new JPanel();
		buttonAllPanel.setLayout(new BorderLayout(0, 0));
		buttonAllPanel.setBorder(new TitledBorder(new EmptyBorder(10, 10, 10, 10), " " + "Eintrag" + " "));
		buttonAllPanel.add("North", buttonPanel);
		buttonAllPanel.add("Center", new JPanel());
		//
		selectionPanel.add("Center", buttonAllPanel);
		//
		JPanel modePanel = new JPanel();
		modePanel.setLayout(new BorderLayout(0, 0));
		modePanel.setBorder(new TitledBorder(new EtchedBorder(1), " " + "Wählbare Einträge" + " "));
		//modePanel.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), " " + "Wählbare Einträge" + " "));
		//
		Vector modeVector = generateListVector();
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
		modeList.setCellRenderer(new ImageCellRenderer(tree.getBackground(), modeList.getForeground(), modeList.getSelectionBackground(), modeList.getSelectionForeground()));
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
		expandRows();
		try {
			tree.setSelectionRow(1);
		} catch (Exception exc) {
			tree.setSelectionRow(0);
		}
	}
	/**
	 * 
	 */
	public void actionPerformed(java.awt.event.ActionEvent event) {
		try {
			selectedRow = tree.getSelectionRows()[0];
		} catch (Exception exc) {
			tree.setSelectionRow(0);
		}
		if (((AbstractButton) (event.getSource())).getName() == "insert") {
			change = true;
			/*Vector feVector = new Vector();
			feVector.addElement(new FrontendElement(null, null, "Separator", null, null));
			popups.insertElementAt(feVector,modeList.getSelectedIndex());
			modeList.setSelectedIndex(0);*/
			int[] pos;
			try {
				pos = ((ModeLabel) (((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject())).pos;
			} catch (Exception exc) {
				pos = new int[] { -1, -1 };
			}
			if (pos[1] == -1) {
				Vector feVector = new Vector();
				//feVector.addElement(new FrontendElement(null, null, "Separator", null, null));
				feVector.addElement(modeList.getSelectedValue());
				increase = true;
				decrease = false;
				changeIndex = pos[0];
				saveExpandedRows();
				popups.insertElementAt(feVector, pos[0] + 1);
				updateTree();
				tree.setSelectionRow(selectedRow + 1);
			} else {
				if (!((FrontendElement) modeList.getSelectedValue()).type.equals("Separator")) {
					increase = true;
					decrease = false;
					changeIndex = pos[0];
					saveExpandedRows();
					if (((FrontendElement) (((Vector) popups.get(pos[0])).get(0))).type.equals("Separator")) {
						((Vector) popups.get(pos[0])).insertElementAt(modeList.getSelectedValue(), 0);
						((Vector) popups.get(pos[0])).removeElementAt(1);
					} else
						 ((Vector) popups.get(pos[0])).insertElementAt(modeList.getSelectedValue(), pos[1] + 1);
					updateTree();
					tree.setSelectionRow(selectedRow + 1);
				}
			}
		}
		if (((AbstractButton) (event.getSource())).getName() == "moveUp") {
			change = true;
			int[] pos = ((ModeLabel) (((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject())).pos;
			if ((pos[1] == -1) && pos[0] > 0) {
				increase = false;
				decrease = false;
				saveExpandedRows(true, false);
				popups.insertElementAt(popups.get(pos[0]), pos[0] - 1);
				popups.removeElementAt(pos[0] + 1);
				updateTree();
				tree.setSelectionRow(selectedRow - 1);
			}
			if (pos[1] > 0) {
				//popups.insertElementAt(((Vector)popups.get(pos[0])).get(pos[1]), pos[1] +2);
				increase = false;
				decrease = false;
				saveExpandedRows();
				((Vector) popups.get(pos[0])).insertElementAt(((Vector) popups.get(pos[0])).get(pos[1]), pos[1] - 1);
				((Vector) popups.get(pos[0])).removeElementAt(pos[1] + 1);
				updateTree();
				tree.setSelectionRow(selectedRow - 1);
			}
		}
		if (((AbstractButton) (event.getSource())).getName() == "moveDown") {
			change = true;
			int[] pos = ((ModeLabel) (((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject())).pos;
			if (pos[1] == -1) {
				if (pos[0] < popups.size() - 1) {
					increase = false;
					decrease = false;
					saveExpandedRows(false, true);
					popups.insertElementAt(popups.get(pos[0]), pos[0] + 2);
					popups.removeElementAt(pos[0]);
					updateTree();
					tree.setSelectionRow(selectedRow + 1);
				}
			} else {
				if (pos[1] < ((Vector) popups.get(pos[0])).size() - 1) {
					//popups.insertElementAt(((Vector)popups.get(pos[0])).get(pos[1]), pos[1] +2);
					increase = false;
					decrease = false;
					saveExpandedRows();
					((Vector) popups.get(pos[0])).insertElementAt(((Vector) popups.get(pos[0])).get(pos[1]), pos[1] + 2);
					((Vector) popups.get(pos[0])).removeElementAt(pos[1]);
					updateTree();
					tree.setSelectionRow(selectedRow + 1);
				}
			}
		}
		if (((AbstractButton) (event.getSource())).getName() == "delete") {
			change = true;
			int[] pos = ((ModeLabel) (((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject())).pos;
			if ((pos[1] == -1)) {
				changeIndex = pos[0];
				increase = false;
				decrease = true;
				saveExpandedRows(selectedRow);
				popups.removeElementAt(pos[0]);
				updateTree();
				if (selectedRow > 0)
					tree.setSelectionRow(selectedRow - 1);
				else
					tree.setSelectionRow(0);
			} else {
				if (((Vector) popups.get(pos[0])).size() > 1) {
					changeIndex = pos[0];
					increase = false;
					decrease = false;
					saveExpandedRows();
					((Vector) popups.get(pos[0])).removeElementAt(pos[1]);
					updateTree();
					if (selectedRow > 0)
						tree.setSelectionRow(selectedRow - 1);
					else
						tree.setSelectionRow(0);
				}
			}
		}
		String source = event.getActionCommand();
		if (source.equals("apply")) {
			tabbed.exit(true, popups);
		} else
			if (source.equals("cancel")) {
				tabbed.exit(false, popups);
			}
		/*
				if (((AbstractButton) (event.getSource())).getName() == "cancel") {
					tabbed.exit(false, popups);
				}
				if (((AbstractButton) (event.getSource())).getName() == "ok") {
					tabbed.exit(true, popups);
				}*/
	}
	/**
	 * 
	 */
	public void collapseAllRows() {
		int z = tree.getRowCount();
		for (int i = z; i >= 0; i--) {
			tree.collapseRow(i);
		}
	}
	/**
	 * 
	 */
	public void doLayout() {
		super.doLayout();
	}
	/**
	 *
	 */
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
	public void expandRows() {
		collapseAllRows();
		tree.expandRow(0);
		int expandIndex = 1;
		int lastExpand = 0;
		for (int i = 0; i < expandedRows.size(); i++) {
			expandIndex = expandIndex + ((Integer) expandedRows.elementAt(i)).intValue() - lastExpand;
			tree.expandRow(expandIndex);
			expandIndex = expandIndex + ((Vector) popups.get(((Integer) expandedRows.elementAt(i)).intValue())).size();
			lastExpand = ((Integer) expandedRows.elementAt(i)).intValue();
		}
	}
	/**
	 * 
	 */
	public Vector generateListVector() {
		Vector vector = new Vector();
		vector.addElement(Geonext.frontendElements.get("SEPARATOR"));
		vector.addElement(Geonext.frontendElements.get("MODE_MOVE"));
		//
		vector.addElement(Geonext.frontendElements.get("MODE_POINT"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_MIDPOINT"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_PERPENDICULAR_POINT"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_CIRCUMCIRCLE_CENTER"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_PARALLELOGRAM_POINT"));
		vector.addElement(Geonext.frontendElements.get("MODE_CASPOINT"));
		vector.addElement(Geonext.frontendElements.get("MODE_INTERSECTION"));
		vector.addElement(Geonext.frontendElements.get("MODE_SLIDER"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_MIRROR_LINE"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_MIRROR_POINT"));
		//vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_ROTATION"));
		/*
		vector = new Vector();
		vector.addElement(Geonext.frontendElements.get("MODE_SLIDER"));
		vector.addElement(Geonext.frontendElements.get("FUNCTION_PLAY"));
		vector.addElement(Geonext.frontendElements.get("FUNCTION_STOP"));
		tbv.addElement(vector);
		*/
		vector.addElement(Geonext.frontendElements.get("MODE_LINE_STRAIGHT"));
		vector.addElement(Geonext.frontendElements.get("MODE_LINE_SEGMENT"));
		vector.addElement(Geonext.frontendElements.get("MODE_LINE_RAY"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_BISECTOR"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_PERPENDICULAR"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_NORMAL"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_PARALLEL"));
		//
		vector.addElement(Geonext.frontendElements.get("MODE_CIRCLE"));
		vector.addElement(Geonext.frontendElements.get("MODE_CIRCLE_RADIUS"));
		vector.addElement(Geonext.frontendElements.get("MODE_CIRCLE_CALC"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_CIRCUMCIRCLE"));
		vector.addElement(Geonext.frontendElements.get("MODE_ARC"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_SECTOR"));
		//
		vector.addElement(Geonext.frontendElements.get("MODE_ARROW"));
		vector.addElement(Geonext.frontendElements.get("MODE_COMPOSITION_ARROW_PARALLEL"));
		/*
		vector = new Vector();
		vector.addElement(Geonext.frontendElements.get("MODE_INTERSECTION"));
		tbv.addElement(vector);
		*/
		vector.addElement(Geonext.frontendElements.get("MODE_POLYGON"));
		//
		vector.addElement(Geonext.frontendElements.get("MODE_GRAPH"));
		vector.addElement(Geonext.frontendElements.get("MODE_PARAMETERCURVE"));
		vector.addElement(Geonext.frontendElements.get("MODE_TRACECURVE"));
		//
		vector.addElement(Geonext.frontendElements.get("MODE_TEXT_DISTANCE"));
		vector.addElement(Geonext.frontendElements.get("MODE_TEXT_ANGLE"));
		vector.addElement(Geonext.frontendElements.get("MODE_TEXT"));
		//
		vector.addElement(Geonext.frontendElements.get("MODE_GROUP_ON"));
		vector.addElement(Geonext.frontendElements.get("MODE_GROUP_OFF"));
		//vector.addElement(Geonext.frontendElements.get("MODE_GROUP_ROTATE"));
		//
		vector.addElement(Geonext.frontendElements.get("MODE_ANGLE"));
		vector.addElement(Geonext.frontendElements.get("MODE_ARC_CALC"));
		//
		vector.addElement(Geonext.frontendElements.get("MODE_RENAME"));
		vector.addElement(Geonext.frontendElements.get("MODE_SHOWNAME"));
		vector.addElement(Geonext.frontendElements.get("MODE_VISIBLE"));
		vector.addElement(Geonext.frontendElements.get("MODE_DRAFT"));
		vector.addElement(Geonext.frontendElements.get("MODE_SETTRACE"));
		return vector;
	}
	/**
	 *
	 */
	public JTree generateTree(Vector toolbarModes) {
		JTree jTree = new JTree(generateRootNode(toolbarModes));
		jTree.setCellRenderer(new ImageTreeCellRenderer());
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
					sub.add(new DefaultMutableTreeNode(menu.get(i)));
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
	public void updateTree() {
		tree.removeTreeExpansionListener(this);
		configScrollPane.getViewport().remove(tree);
		tree = generateTree();
		expandRows();
		configScrollPane.setViewportView(tree);
		tabbed.validateTree();
	}
	/**
	 * 
	 */
	public void valueChanged(javax.swing.event.ListSelectionEvent e) {}
}