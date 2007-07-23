/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * AlgebraView.java
 *
 * Created on 27. September 2001, 11:30
 */

package geogebra.algebra;

import geogebra.Application;
import geogebra.View;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.util.FastHashMapKeyless;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import atp.sHotEqn;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgebraView extends JTree implements View {	
		
	private static final long serialVersionUID = 1L;
	
	private ImageIcon iconShown, iconHidden;

	private Application app; // parent appame
	private Kernel kernel;
	private DefaultTreeModel model;
	
	private MyRenderer renderer;
	private MyDefaultTreeCellEditor editor;
	private JTextField editTF;
	
	// store all pairs of GeoElement -> node in the Tree
	private FastHashMapKeyless nodeTable = new FastHashMapKeyless(500);

	// nodes
	private DefaultMutableTreeNode root, depNode, indNode, auxiliaryNode;	
	private TreePath tpInd, tpDep, tpAux; // tree paths for main nodes

	private GeoElement selectedGeoElement;
	private DefaultMutableTreeNode selectedNode;
	
	//	closing cross
	private static BasicStroke crossStroke = new BasicStroke(1.5f); 
	private static int crossBorder = 4;
	private static int crossOffset = crossBorder + 6;
	private boolean highlightCross;

	/** Creates new AlgebraView */
	public AlgebraView(AlgebraController algCtrl) {		
		app = algCtrl.getApplication();
		kernel = algCtrl.getKernel();
		algCtrl.setView(this);			
		
		iconShown = app.getImageIcon("shown.gif");
		iconHidden = app.getImageIcon("hidden.gif");		

		// tree's selection model	
		/*
		TreeSelectionModel tsm = new DefaultTreeSelectionModel();
		tsm.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		setSelectionModel(tsm);		
		tsm.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {				
				selectionChanged();	
			}				 
		});*/

		// cell renderer (tooltips) and editor
		ToolTipManager.sharedInstance().registerComponent(this);

		// EDITOR	   
		setEditable(true);
		initTreeCellRendererEditor();

		// add listener
		addKeyListener(algCtrl);
		addMouseListener(algCtrl);
		addMouseMotionListener(algCtrl);		

		// build default tree structure
		root = new DefaultMutableTreeNode();
		depNode = new DefaultMutableTreeNode(); // dependent objects
		indNode = new DefaultMutableTreeNode();
		auxiliaryNode = new DefaultMutableTreeNode();
				
		
		// independent objects                  
		root.add(indNode);
		root.add(depNode);
		root.add(auxiliaryNode);

		// create model from root node
		model = new DefaultTreeModel(root);
		// this.treeModel = model;        
		setModel(model);
		setLargeModel(true);
		setLabels();

		// tree's options             
		setRootVisible(false);
		// show lines from parent to children
		putClientProperty("JTree.lineStyle", "Angled");
		setInvokesStopCellEditing(true);
		setScrollsOnExpand(true);	
		setRowHeight(-1); // to enable flexible height of cells
		
		tpInd = new TreePath(indNode.getPath());
		tpDep = new TreePath(depNode.getPath());
		tpAux =new TreePath(auxiliaryNode.getPath()); 		
		
		attachView();						
	}

	public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);		
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
		//kernel.notifyRemoveAll(this);		
	}
	
	public void updateFonts() {
		setFont(app.plainFont);
		editor.setFont(app.plainFont);
		renderer.setFont(app.plainFont);
		editTF.setFont(app.plainFont);
	}    

	private void initTreeCellRendererEditor() {
		renderer = new MyRenderer();		
		editTF = new JTextField();
		editor = new MyDefaultTreeCellEditor(this, renderer, 
									new MyCellEditor(editTF));
		
		editor.addCellEditorListener(editor); // self-listening
		setCellRenderer(renderer);
		setCellEditor(editor);
	}

	public void clearSelection() {
		super.clearSelection();
		selectedGeoElement = null;
	}

	public GeoElement getSelectedGeoElement() {
		return selectedGeoElement;
	}
	
	public boolean showAuxiliaryObjects() {
		return auxiliaryNode.getParent() != null;
	}
	
	public void setShowAuxiliaryObjects(boolean flag) {
		if (flag == showAuxiliaryObjects()) return;
		cancelEditing();
		
		if (flag) {
			clearView();
			//	add to root
			model.insertNodeInto(auxiliaryNode, root, root.getChildCount());		
			kernel.notifyAddAll(this);
		} else {
			model.removeNodeFromParent(auxiliaryNode);			
		}					
	}
	
	final public void paint(Graphics g) { 	 
		// draw a cross in the upper right corner 
		// to close the algebra view
		super.paint(g);
				
		if (!app.isApplet())
			drawClosingCross((Graphics2D) g);
	}		
	
	private void drawClosingCross(Graphics2D g2) {
		int width = getWidth();			
		g2.setStroke(crossStroke);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (highlightCross) {
			g2.setColor(Color.red);
		} else {
			g2.setColor(Color.gray);
		}
		g2.drawLine(width-crossOffset, crossBorder, width-crossBorder, crossOffset);
		g2.drawLine(width-crossOffset, crossOffset, width-crossBorder, crossBorder);
		
		if (highlightCross) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
								RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			// "close" 
			String strClose = app.getMenu("Close");
			TextLayout layout = new TextLayout(strClose, app.smallFont, g2.getFontRenderContext());
			g2.setColor(Color.gray);
			
			int stringX = (int) (width - crossOffset - crossOffset - layout.getAdvance());
			
			g2.drawString(strClose, stringX, layout.getAscent()+2);
		}
	}
	
	boolean hitClosingCross(int x, int y) {
		return !app.isApplet() && 
				(y <= crossOffset) && 
		  		(x >= getWidth() - crossOffset);		
	}
	
	void setClosingCrossHighlighted(boolean flag) {
		if (flag == highlightCross) return;		
		highlightCross = flag;		
		repaint();
	}
	
	/* *
	 * selection mangament
	 *
	private GeoElement [] selectedGeos; 
	 
	
	public GeoElement [] getAllSelectedGeoElements() {
		return selectedGeos;
	}						
	
	private void selectionChanged() {
		TreePath[] paths = getSelectionPaths();			
		if (paths == null) { // no paths selected			
			app.clearSelectedGeos();
			return;
		} 
				
		// get all GeoElements out of selection		
		for (int i=0; i < paths.length; i++) {
			Object ob = paths[i].getLastPathComponent();
			Object userOb  = ((DefaultMutableTreeNode) ob).getUserObject();
			if (userOb instanceof GeoElement) {			
				app.addSelectedGeo((GeoElement) userOb, false);
			}
		}		
		kernel.notifyRepaint();
	}*/
	
	/*
	public void select(GeoElement geo, boolean flag) {
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) nodeTable.get(geo);
		if (node != null) {
			TreePath tp = new TreePath(node.getPath());			
			if (flag) 
				addSelectionPath(tp);
			else 
				removeSelectionPath(tp);
		}
	}	*/

	public static GeoElement getGeoElementForLocation(JTree tree, int x, int y) {
		TreePath tp = tree.getPathForLocation(x, y);
		if (tp == null)
			return null;

		Object ob;
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) tp.getLastPathComponent();
		if (node != null
			&& (ob = node.getUserObject()) instanceof GeoElement)
			return (GeoElement) ob;
		else
			return null;
	}
	
	public void setToolTipText(String text) {
		renderer.setToolTipText(text);
	}

	/**
	 * Open Editor textfield for geo.
	 */
	public void startEditing(GeoElement geo) {
		if (geo == null) return;
		
		if (!geo.isChangeable()) {
			if (geo.isFixed()) {
				app.showMessage(app.getError("AssignmentToFixed"));
			} 
			else if (geo.isRedefineable()) { 
				app.showRedefineDialog(geo);
			}
			return;
		}
		
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) nodeTable.get(geo);

		if (node != null) {
			cancelEditing();
			// select and show node
			TreePath tp = new TreePath(node.getPath());
			setSelectionPath(tp); // select
			expandPath(tp);
			makeVisible(tp);
			scrollPathToVisible(tp);						
			startEditingAtPath(tp); // opend editing text field
		}
	}

	/**
	 * resets all fix labels of the View. This method is called
	 * by the application if the language setting is changed.
	 */
	public void setLabels() {
		// tree node labels        		
		setNodeLabel(indNode, app.getPlain("FreeObjects"));
		setNodeLabel(depNode, app.getPlain("DependentObjects"));
		setNodeLabel(auxiliaryNode, app.getPlain("AuxiliaryObjects"));		
	}

	/** update everything up the tree */
	private void setNodeLabel(DefaultMutableTreeNode node, String label) {
		node.setUserObject(label);
		if (model != null) model.nodeChanged(node);
	}

	/*
	public String getToolTipText(MouseEvent evt) {				
		GeoElement geo = getGeoElementForLocation(evt.getX(), evt.getY());
	   	if (geo == null) return null;
	   	return geo.getLongDescriptionHTML(true, true);    	   	
	}*/

	/**
	 * adds a new node to the tree
	 */
	public void add(GeoElement geo) {	
		cancelEditing();			

		if (geo.isLabelSet() && geo.isSetAlgebraVisible()) {
			DefaultMutableTreeNode parent, node;
			node = new DefaultMutableTreeNode(geo);
			if (geo.isAuxiliaryObject()) {
				parent = auxiliaryNode;
			}				
			else if (geo.isIndependent() || geo.isPointOnPath()) {			
				parent = indNode;				
			} 
			else {				
				parent = depNode;
			}

			// add node to model (alphabetically ordered)                                    
			model.insertNodeInto(node, parent, getInsertPosition(parent, geo));			
			nodeTable.put(geo, node);
			
			// show new node			
			if (parent == indNode)
				expandPath(tpInd);
			else if (parent == depNode)
				expandPath(tpDep);
			else
				expandPath(tpAux);
						
			//TreePath tp = new TreePath(node.getPath());			
			//this.scrollPathToVisible(tp);											
		}
	}

	
	/**
	 * Gets the insert position for newGeo to insert it in alphabetical
	 * order in parent node. Note: all children of parent must have instances of GeoElement 
	 * as user objects.
	 */
	final public static int getInsertPosition(DefaultMutableTreeNode parent, GeoElement newGeo) { 							
		// label of inserted geo
		String newLabel = newGeo.getLabel();
		
		// standard case: binary search		
		int left = 0;
		int right = parent.getChildCount();	
		if (right == 0) return right;
		
		// bigger then last?
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastChild();	
		String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();	
		if (newLabel.compareTo(nodeLabel) > 0) 
			return right;				
		
		// binary search
		while (right > left) {							
			int middle = (left + right) / 2;
			node = (DefaultMutableTreeNode) parent.getChildAt(middle);
			nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
			
			if (newLabel.compareTo(nodeLabel) < 0) {
				right = middle;
			} else {
				left = middle + 1;
			}
		}													
		
		// insert at correct position
		return right;				
	}
	
	/**
	 * Performs a binary search for geo among the children of parent. All children of parent
	 * have to be instances of GeoElement sorted alphabetically by their names.
	 * @return -1 when not found
	 */
	final public static int binarySearchGeo(DefaultMutableTreeNode parent, String geoLabel) { 				
		int left = 0;
		int right = parent.getChildCount()-1;
		if (right == -1) return -1;
	
		// binary search for geo's label
		while (left <= right) {							
			int middle = (left + right) / 2;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(middle);
			String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
			
			int compare = geoLabel.compareTo(nodeLabel);
			if (compare < 0)
				right = middle -1;
		    else if (compare > 0)
		    	left = middle + 1;	
		    else
		    	return middle;
		}												  
		
		return -1;				
	}		
	
	/**
	 * Performs a linear search for geo among the children of parent.
	 * @return -1 when not found
	 */
	final public static int linearSearchGeo(DefaultMutableTreeNode parent, String geoLabel) { 												
		int childCount = parent.getChildCount();	
		for (int i = 0; i < childCount; i++) {			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel()))
				return i;
		}
		return -1;
	}

	/**
	 * removes a node from the tree
	 */
	public void remove(GeoElement geo) {		
		cancelEditing();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeTable.get(geo);
		if (node != null) removeFromModel(node);		
	}
	
	public void clearView() {
		nodeTable.clear();		
		indNode.removeAllChildren();
		depNode.removeAllChildren();
		auxiliaryNode.removeAllChildren();
		model.reload();
	}
	
	final public void repaintView() {
		repaint();
	}

	/**
	 * renames an element and sorts list 
	 */
	public void rename(GeoElement geo) {
		remove(geo);
		add(geo);
	}
	
	public void reset() {
		cancelEditing();
	  repaint();
	}

	private void removeFromModel(DefaultMutableTreeNode node) {
		// remove node from model
		model.removeNodeFromParent(node);
		nodeTable.remove(node.getUserObject());
		//updateNodeLabel(parent);                   
	}

	/**
	   * updates node of GeoElement geo (needed for highlighting)
	   * @see EuclidianView.setHighlighted()
	   */
	final public void update(GeoElement geo) {	
		if (isEditing())
			cancelEditing();
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) nodeTable.get(geo);						
		if (node != null) {
			model.nodeChanged(node);		
		} 	
	}
	
	final public void updateAuxiliaryObject(GeoElement geo) {
		remove(geo);
		add(geo);
	}		

	/**
	 * inner class MyRenderer for GeoElements 
	 */
	private class MyRenderer extends DefaultTreeCellRenderer {
		
		private static final long serialVersionUID = 1L;				
			
		public MyRenderer() {
			setOpaque(true);
		}
		
		public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {	
						
			//System.out.println("getTreeCellRendererComponent: " + value);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;			
			Object ob = node.getUserObject();
						
			if (ob instanceof GeoElement) {	
				GeoElement geo = (GeoElement) ob;										
				
				setFont(app.boldFont);
				setForeground(geo.labelColor);
				String str = geo.getAlgebraDescriptionTextOrHTML();
				//String str = geo.getAlgebraDescription();
				setText(str);								
				
				if (geo.doHighlighting())				   
					setBackground(Application.COLOR_SELECTION);
				else 
					setBackground(getBackgroundNonSelectionColor());
								
				// ICONS               
				if (geo.isEuclidianVisible()) {
					setIcon(iconShown);
				} else {
					setIcon(iconHidden);
				}
				
//				 TODO: LaTeX in AlgebraView
				//if (geo.isGeoFunction()) {
					
			//	}
				
				
				/*// HIGHLIGHTING
				if (geo.highlight) {
					//setBorder(BorderFactory.createLineBorder(geo.selColor));
					setBackground(geo.selColor);
				} else {
					//setBorder(null);
				}*/
			}								
			//	no leaf (no GeoElement)
			else { 
				if (expanded) {
					setIcon(getOpenIcon());
				} else {
					setIcon(getClosedIcon());
				}
				setForeground(Color.black);
				setBackground(getBackgroundNonSelectionColor());
				setFont(app.plainFont);
				selected = false;				
				setBorder(null);
				setText(value.toString());
			}		
			
			return this;
		}							
		
		/*
		final public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);                                        
			 g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);    
			 super.paint(g);
		}
		*/
	} // MyRenderer

	/**
	 * inner class MyEditor handles editing of tree nodes
	 *
	 * Created on 28. September 2001, 12:36
	 */
	private class MyDefaultTreeCellEditor
		extends DefaultTreeCellEditor
		implements CellEditorListener {

		public MyDefaultTreeCellEditor(AlgebraView tree, DefaultTreeCellRenderer renderer,
								DefaultCellEditor editor) {
			super(tree, renderer, editor);						
		}

		/*
		 * CellEditorListener implementation 
		*/
		public void editingCanceled(ChangeEvent event) {
		}

		public void editingStopped(ChangeEvent event) {
			// get the entered String
			String newValue = getCellEditorValue().toString();
			
			// the userObject was changed to this String
			// reset it to the old userObject, which we stored
			// in selectedGeoElement (see valueChanged())        
			// only nodes with a GeoElement as userObject can be edited!		
			selectedNode.setUserObject(selectedGeoElement);
			
			// change this GeoElement in the Kernel                  
			GeoElement geo = kernel.getAlgebraProcessor().changeGeoElement(selectedGeoElement, newValue, false);			
			if (geo != null) {				
				selectedGeoElement = geo;
				selectedNode.setUserObject(selectedGeoElement);
			}			
			model.nodeChanged(selectedNode); // refresh display        
		}

		/*
		 * OVERWRITE SOME METHODS TO ONLY ALLOW EDITING OF GeoElements
		 */

		public boolean isCellEditable(EventObject event) {
			boolean retValue = false;
			boolean editable = false;

			if (event != null) {
				if (event.getSource() instanceof JTree) {
					setTree((JTree) event.getSource());
					if (event instanceof MouseEvent) {
						TreePath path =
							tree.getPathForLocation(
								((MouseEvent) event).getX(),
								((MouseEvent) event).getY());
						editable =
							(lastPath != null
								&& path != null
								&& lastPath.equals(path));
					}
				}
			}
			if (!realEditor.isCellEditable(event))
				return false;
			if (canEditImmediately(event))
				retValue = true;
			else if (editable && shouldStartEditingTimer(event)) {
				startEditingTimer();
			} else if (timer != null && timer.isRunning())
				timer.stop();

			/***********************************************************/
			// ADDED by Markus Hohenwarter			
			if (retValue) {
				Object ob = lastPath == null ? null :
					((DefaultMutableTreeNode) lastPath.getLastPathComponent())
						.getUserObject();
				if (ob instanceof GeoElement) {
					GeoElement geo = (GeoElement) ob;					
					retValue = geo.isChangeable() || geo.isRedefineable();					
				} else
					retValue = false;
			}
			/***********************************************************/

			if (retValue)
				prepareForEditing();							
				
			return retValue;
		}

		//
		// TreeSelectionListener
		//

		/**
		 * Resets lastPath.
		 */
		public void valueChanged(TreeSelectionEvent e) {
			if (tree != null) {
				if (tree.getSelectionCount() == 1)
					lastPath = tree.getSelectionPath();
				else
					lastPath = null;
				/***** ADDED by Markus Hohenwarter ***********/
				storeSelection(lastPath);
				/********************************************/
			}
			if (timer != null) {
				timer.stop();
			}
		}

		/** stores currently selected GeoElement and node.
		 *  selectedNode, selectedGeoElement are private members of AlgebraView
		 */
		private void storeSelection(TreePath tp) {
			if (tp == null)
				return;

			Object ob;
			selectedNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
			if (selectedNode != null
				&& (ob = selectedNode.getUserObject()) instanceof GeoElement) {
				selectedGeoElement = (GeoElement) ob;
			} else {
				selectedGeoElement = null;
			}
		}

	}  // MyEditor
	
	
	// this is needed to distinguish between the editing
	// of independent and dependent objects
	private class MyCellEditor extends DefaultCellEditor {	
		
		private static final long serialVersionUID = 1L;
		
		public MyCellEditor(final JTextField textField) {
			super(textField);			
		}
		
		/** Implements the <code>TreeCellEditor</code> interface. */
		public Component getTreeCellEditorComponent(JTree tree, Object value,
							boolean isSelected,
							boolean expanded,
							boolean leaf, int row) {
				
			String str = null;		
			if (value instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				Object ob = node.getUserObject();
				if (ob instanceof GeoElement) {
					GeoElement geo = (GeoElement) ob;
					if (geo.isChangeable()) {
						str = geo.toString();
					} else {
						str = geo.getCommandDescription();
					}
				}
			}
		
			String stringValue;
			if (str == null) {				
				stringValue = (value == null) ? "" : value.toString();
			} else {
				stringValue = str;
			}			
			delegate.setValue(stringValue);
			return editorComponent;
		}
	}
	
	


} // AlgebraView
