package geogebra.gui;

import geogebra.kernel.ConstructionDefaults;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

/**
 * Options with the default settings of objects.
 * 
 * @author Florian Sonner
 */
public class OptionsDefaults extends JPanel implements TreeSelectionListener {
	/** */
	private static final long serialVersionUID = 1L;
	
	/**
	 * An instance of the GeoGebra application.
	 */
	private Application app;
	
	/**
	 * The panel with the tabs for the different properties.
	 */
	private PropertiesPanel propPanel;
	
	/**
	 * A tree with the different available object types to which new default
	 * values can be assigned.
	 */
	private JTree tree;
	
	/**
	 * The tree model.
	 */
	private DefaultTreeModel treeModel;
	
	/**
	 * The root node of the tree.
	 */
	private DefaultMutableTreeNode rootNode;
	
	/**
	 * Nodes for points.
	 */
	private DefaultMutableTreeNode pointsNode, pointsFreeNode, pointsDepNode, pointsPathNode;
	
	private DefaultMutableTreeNode lineNode, vectorNode, conicNode, conicSectorNode;
	
	private DefaultMutableTreeNode numberNode, angleNode;
	
	private DefaultMutableTreeNode functionNode, polygonNode, locusNode;
	
	private DefaultMutableTreeNode textNode, imageNode, booleanNode;
	
	private DefaultMutableTreeNode listNode;
	
	/**
	 * The class which contains all default objects.
	 */
	private ConstructionDefaults defaults;
	
	/**
	 * A dictionary which assigns a constant of the ConstructionsDefaults class to the
	 * tree nodes.
	 */
	private static Hashtable<DefaultMutableTreeNode, Integer> typeToNode;
	
	/**
	 * Construct an panel where the user can assign new values to the
	 * default objects.
	 * 
	 * @param app
	 */
	public OptionsDefaults(Application app) {
		this.app = app;
		
		defaults = app.getKernel().getConstruction().getConstructionDefaults();
		
		initGUI();
		updateGUI();
	}
	
	/**
	 * Initialize the GUI.
	 */
	private void initGUI() {
		// init the model of the tree
		initNodes();
		treeModel = new DefaultTreeModel(rootNode);
		
		// init the real JTree component
		tree = new JTree(treeModel);
		
		tree.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, SystemColor.controlDkShadow),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)
		));

		tree.setRootVisible(false);
		tree.setScrollsOnExpand(true);
		tree.setPreferredSize(new Dimension(110, 0));
		
		// expand the point node and select the first point
		tree.expandRow(0);
		tree.setSelectionRow(1);
		
		tree.addTreeSelectionListener(this);
		
		// disable leaf icons
		DefaultTreeCellRenderer treeCellRenderer = new DefaultTreeCellRenderer();
		treeCellRenderer.setLeafIcon(null);
		tree.setCellRenderer(treeCellRenderer);
		
		// create the properties panel
		JColorChooser colorChooser = new JColorChooser(new Color(1, 1,1, 100));
		propPanel = new PropertiesPanel(app, colorChooser, true);
		propPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		propPanel.updateSelection(new Object[] { defaults.getDefaultGeo(ConstructionDefaults.DEFAULT_POINT_FREE) });
		
		// set the labels of the components
		setLabels();
		
		// define panel properties
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout());
		
		// add components
		add(tree, BorderLayout.WEST);
		add(propPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Initialize the nodes of the tree.
	 */
	private void initNodes() {
		// create the nodes for the tree
		rootNode = new DefaultMutableTreeNode();
		pointsNode = new DefaultMutableTreeNode();
		pointsFreeNode = new DefaultMutableTreeNode();
		pointsDepNode = new DefaultMutableTreeNode();
		pointsPathNode = new DefaultMutableTreeNode();
		lineNode = new DefaultMutableTreeNode();
		vectorNode = new DefaultMutableTreeNode();
		conicNode = new DefaultMutableTreeNode();
		conicSectorNode = new DefaultMutableTreeNode();
		functionNode = new DefaultMutableTreeNode();
		polygonNode = new DefaultMutableTreeNode();
		locusNode = new DefaultMutableTreeNode();
		textNode = new DefaultMutableTreeNode();
		imageNode = new DefaultMutableTreeNode();
		numberNode = new DefaultMutableTreeNode();
		angleNode = new DefaultMutableTreeNode();
		booleanNode = new DefaultMutableTreeNode();
		listNode = new DefaultMutableTreeNode();
		
		rootNode.add(pointsNode);
		pointsNode.add(pointsFreeNode);
		pointsNode.add(pointsDepNode);
		pointsNode.add(pointsPathNode);
		
		rootNode.add(lineNode);
		rootNode.add(vectorNode);
		rootNode.add(conicNode);
		rootNode.add(conicSectorNode);
		rootNode.add(functionNode);
		rootNode.add(polygonNode);
		rootNode.add(locusNode);
		rootNode.add(textNode);
		rootNode.add(imageNode);
		rootNode.add(numberNode);
		rootNode.add(angleNode);
		rootNode.add(booleanNode);
		rootNode.add(listNode);
		
		// create the dictionary which is used to assign a type constant (int) to
		// every tree leaf
		typeToNode = new Hashtable<DefaultMutableTreeNode, Integer>(15);
		typeToNode.put(pointsFreeNode, ConstructionDefaults.DEFAULT_POINT_FREE);
		typeToNode.put(pointsDepNode, ConstructionDefaults.DEFAULT_POINT_DEPENDENT);
		typeToNode.put(pointsPathNode, ConstructionDefaults.DEFAULT_POINT_ON_PATH);
		typeToNode.put(lineNode, ConstructionDefaults.DEFAULT_LINE);
		typeToNode.put(vectorNode, ConstructionDefaults.DEFAULT_VECTOR);
		typeToNode.put(conicNode, ConstructionDefaults.DEFAULT_CONIC);
		typeToNode.put(conicSectorNode, ConstructionDefaults.DEFAULT_CONIC_SECTOR);
		typeToNode.put(numberNode, ConstructionDefaults.DEFAULT_NUMBER);
		typeToNode.put(functionNode, ConstructionDefaults.DEFAULT_FUNCTION);
		typeToNode.put(polygonNode, ConstructionDefaults.DEFAULT_POLYGON);
		typeToNode.put(locusNode, ConstructionDefaults.DEFAULT_LOCUS);
		typeToNode.put(textNode, ConstructionDefaults.DEFAULT_TEXT);
		typeToNode.put(imageNode, ConstructionDefaults.DEFAULT_IMAGE);
		typeToNode.put(numberNode, ConstructionDefaults.DEFAULT_NUMBER);
		typeToNode.put(angleNode, ConstructionDefaults.DEFAULT_ANGLE);
		typeToNode.put(booleanNode, ConstructionDefaults.DEFAULT_BOOLEAN);
		typeToNode.put(listNode, ConstructionDefaults.DEFAULT_LIST);
	}
	
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	public void updateGUI() {
		
	}
	
	/**
	 * Update the labels of the current panel. Should be applied if the
	 * language was changed.
	 * 
	 * TODO use phrase for conic sector (F.S.)
	 */
	public void setLabels() {
		// update tree labels
		pointsNode.setUserObject(app.getPlain("Point"));
		pointsFreeNode.setUserObject(app.getPlain("PointFree"));
		pointsDepNode.setUserObject(app.getPlain("PointDep"));
		pointsPathNode.setUserObject(app.getPlain("PointOnPath"));
		lineNode.setUserObject(app.getPlain("Line"));
		vectorNode.setUserObject(app.getPlain("Vector"));
		conicNode.setUserObject(app.getPlain("Conic"));
		conicSectorNode.setUserObject("Conic Sector");
		functionNode.setUserObject(app.getPlain("Function"));
		polygonNode.setUserObject(app.getPlain("Polygon"));
		locusNode.setUserObject(app.getPlain("Locus"));
		textNode.setUserObject(app.getPlain("Text"));
		imageNode.setUserObject(app.getPlain("Image"));
		numberNode.setUserObject(app.getPlain("Numeric"));
		angleNode.setUserObject(app.getPlain("Angle"));
		booleanNode.setUserObject(app.getPlain("Boolean"));
		listNode.setUserObject(app.getPlain("List"));
		
		propPanel.setLabels();
	}

	/**
	 * The selection has changed.
	 */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		
		if(node == null || node == rootNode) {
			propPanel.setVisible(false);
		} else {
			if(!propPanel.isVisible()) {
				propPanel.setVisible(true);
			}
			
			if(node == pointsNode) {
				Object[] selection = new Object[pointsNode.getChildCount()];
				
				for(int i = 0; i < pointsNode.getChildCount(); ++i) {
					selection[i] = defaults.getDefaultGeo(typeToNode.get(pointsNode.getChildAt(i)));
				}
				
				propPanel.updateSelection(selection);
			} else {
				if(typeToNode.containsKey(node)) {
					propPanel.updateSelection(new Object[] { defaults.getDefaultGeo(typeToNode.get(node)) });
				}
			}
		}
	}
	
	/**
	 * Save the settings of this panel.
	 */
	public void apply() {
		
	}
}
