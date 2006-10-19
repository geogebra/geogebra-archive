/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.AbsoluteScreenLocateable;
import geogebra.kernel.AlgoSlope;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.LimitedPath;
import geogebra.kernel.Locateable;
import geogebra.kernel.Traceable;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Markus Hohenwarter
 */
public class PropertiesDialog
	extends JDialog
	implements
		WindowListener,
		WindowFocusListener,
		ListSelectionListener,
		KeyListener,
		GeoElementSelectionListener {
		
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Application app;
	private Kernel kernel;
	private JListGeoElements geoList;
	private JButton cancelButton, applyButton;
	private PropertiesPanel propPanel;
	
	private boolean firstTimeVisible = true;

	final static int TEXT_FIELD_FRACTION_DIGITS = 3;
	final static int SLIDER_MAX_WIDTH = 170;
	
	final private static int MIN_WIDTH = 200;
	final private static int MIN_HEIGHT = 300;

	/**
	 * Creates new PropertiesDialog.
	 * @param app: parent frame
	 */
	public PropertiesDialog(Application app) {
		super(app.getFrame(), false);
		this.app = app;
		kernel = app.getKernel();		

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		addWindowListener(this);		
		geoList = new JListGeoElements();
		geoList.addListSelectionListener(this);
				
		// build GUI
		initGUI();
	}

	/**
	 * inits GUI with labels of current language	 
	 */
	public void initGUI() {
		setTitle(app.getPlain("Properties"));

		//	LIST PANEL
		JPanel listPanel = new JPanel();
		//listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setLayout(new BorderLayout(5, 5));
		// JList with GeoElements		
		geoList.updateUI();
		JScrollPane listScroller = new JScrollPane(geoList);
		listScroller.setPreferredSize(new Dimension(100, 200));
		listPanel.add(listScroller, BorderLayout.CENTER);

		// rename, redefine and delete button
		int pixelX = 20;
		int pixelY = 10;
		MySmallJButton renameButton = new MySmallJButton(app.getImageIcon("rename.gif"), pixelX, pixelY);
		renameButton.setToolTipText(app.getPlain("Rename"));
		renameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rename();
			}
		});
		MySmallJButton  redefineButton = new MySmallJButton (app.getImageIcon("redefine.gif"), pixelX, pixelY);
		redefineButton.setToolTipText(app.getPlain("Redefine"));
		redefineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redefine();
			}
		});
		MySmallJButton delButton = new MySmallJButton(app.getImageIcon("delete_small.gif"), pixelX, pixelY);
		delButton.setToolTipText(app.getPlain("Delete"));
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelectedGeos();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));			
		if (app.letRedefine())
			buttonPanel.add(redefineButton);
		if (app.letDelete())
			buttonPanel.add(delButton);
		if (app.letRename())
			buttonPanel.add(renameButton);

		listPanel.add(buttonPanel, BorderLayout.SOUTH);
		Border compound =
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(app.getPlain("Objects")),
				BorderFactory.createEmptyBorder(0, 2, 0, 2));
		listPanel.setBorder(compound);		

		// PROPERTIES PANEL
		propPanel = new PropertiesPanel();
		selectionChanged(); // init propPanel		

		// Cancel and Apply Button
		cancelButton = new JButton(app.getPlain("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();				
			}
		});
		applyButton = new JButton(app.getPlain("Apply"));
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apply();			
			}
		});

		// put it all together				 		 		 
		Container contentPane = getContentPane();
		contentPane.removeAll();
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(applyButton);
		buttonPanel.add(cancelButton);
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(propPanel, BorderLayout.CENTER);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);
		JPanel dialogPanel = new JPanel(new BorderLayout());
		dialogPanel.add(listPanel, BorderLayout.WEST);
		dialogPanel.add(rightPanel, BorderLayout.CENTER);

		contentPane.add(dialogPanel);
		dialogPanel.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		
		
		
		Util.addKeyListenerToAll(this, this);	
		packDialog();
	}
	
	public void cancel() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		kernel.detach(geoList);
				
		// remember current construction step
		int consStep = kernel.getConstructionStep();
		
		// restore old construction state
		app.restoreCurrentUndoInfo();
		
		// go to current construction step
		ConstructionProtocol cp = app.getConstructionProtocol();
		if (cp != null) {
			cp.setConstructionStep(consStep);     			 
		}
		
		setCursor(Cursor.getDefaultCursor());
		setVisible(false);
	}
	
	public void apply() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));				
		app.storeUndoInfo();
		setCursor(Cursor.getDefaultCursor());
		setVisible(false);	
	}
	
	private void packDialog() {
		pack();
				
		Dimension d1 = getSize();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		if (firstTimeVisible) {
			// center dialog
			firstTimeVisible = false;						
			int w = Math.min(d1.width, dim.width);
			int h = Math.min(d1.height, (int) (dim.height * 0.8));
			setLocation((dim.width - w) / 2, (dim.height - h) / 2);
			setSize(w, h);
		} else {
			Point loc = getLocation(); 
			int x = Math.min(loc.x, dim.width - d1.width);
			int y = Math.min(loc.y, dim.height - d1.height - 25);
			setLocation(x, y);
		}
		
		SwingUtilities.updateComponentTreeUI(this);	
	}

	/**
	 * shows this dialog and select GeoElement geo at screen position location
	 */
	public void setVisible(ArrayList geos) {		
		setViewActive(true);		
		geoList.setSelected(geos, false);		
		if (!isShowing()) 					
			super.setVisible(true);			
	}

	public void setVisible(boolean visible) {
		if (visible) {			
			setVisible(null);			
		} else {
			super.setVisible(false);
			setViewActive(false);
		}
	}
	
	private void setViewActive(boolean flag) {
		if (flag == viewActive) return; 
		viewActive = flag;
		
		if (flag) {			
			geoList.clear();	
			kernel.attach(geoList);
			kernel.notifyAddAll(geoList);					
			
			app.setSelectionListenerMode(this);
			addWindowFocusListener(this);			
		} else {
			kernel.detach(geoList);					
			
			removeWindowFocusListener(this);						
			app.setSelectionListenerMode(null);
		}		
	}
	private boolean viewActive = false;

	/**
	 * handles selection change	 
	 */
	private void selectionChanged() {
		Object[] geos = geoList.getSelectedValues();
		propPanel.updateSelection(geos);
		Util.addKeyListenerToAll(propPanel, this);
		
		// update selection of application too
		if (app.getMode() == EuclidianView.MODE_ALGEBRA_INPUT)
			app.setSelectedGeos(geos);		
	}
	
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (geo == null) return;
		tempArrayList.clear();
		tempArrayList.add(geo);
		geoList.setSelected(tempArrayList, addToSelection);
		//requestFocus();
	}	
	private ArrayList tempArrayList = new ArrayList();

	/**
	 * deletes all selected GeoElements from Kernel	 
	 */
	private void deleteSelectedGeos() {
		int firstIndex = geoList.getSelectedIndex();
		Object[] geos = geoList.getSelectedValues();
		if (geos == null)
			return;
		for (int i = 0; i < geos.length; i++) {
			((GeoElement) geos[i]).remove();
		}

		int size = geoList.getModel().getSize();
		if (size > 0) {
			if (firstIndex < size)
				geoList.setSelectedIndex(firstIndex);
			else
				geoList.setSelectedIndex(size - 1);
		}
	}

	/**
	 * renames first selected GeoElement
	 */
	private void rename() {
		Object[] geos = geoList.getSelectedValues();
		if (geos == null)
			return;
		app.showRenameDialog((GeoElement) geos[0], false);
		//geoList.setSelected((GeoElement) geos[0]);
		//selectionChanged();
	}
	
	/**
	 * redefines first selected GeoElement
	 */
	private void redefine() {
		Object[] geos = geoList.getSelectedValues();
		if (geos == null)
			return;
		app.showRedefineDialog((GeoElement) geos[0]);
		//geoList.setSelected((GeoElement) geos[0]);
		//selectionChanged();
	}

	/**
	 * implements ListSelectionListener
	 * @param e
	 */
	public void valueChanged(ListSelectionEvent e) {
		// selection should be finished
		if (e.getValueIsAdjusting())
			return;
		selectionChanged();
	}

	/*
	 * Window Listener
	 */
	public void windowActivated(WindowEvent e) {
		if (!isModal()) {
			geoList.setSelected(null, false);
			selectionChanged();						
		}
		repaint();
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		cancel();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}
	public void windowIconified(WindowEvent e) {
	}
	public void windowOpened(WindowEvent e) {
	}
	


	/**
	 * INNER CLASS
	 * PropertiesPanel for displaying all gui elements for changing properties
	 * of currently selected GeoElements. 
	 * @see update() in PropertiesPanel
	 * @author Markus Hohenwarter
	 */
	class PropertiesPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ShowObjectPanel showObjectPanel;
		private ColorPanel colorPanel;
		private LabelPanel labelPanel;
		private CoordPanel coordPanel;
		private LineEqnPanel lineEqnPanel;
		private ConicEqnPanel conicEqnPanel;
		private PointSizePanel pointSizePanel;
		private TextOptionsPanel textOptionsPanel;
		private ArcSizePanel arcSizePanel;
		private LineStylePanel lineStylePanel;
		private FillingPanel fillingPanel;
		private TracePanel tracePanel;
		private FixPanel fixPanel;
 		private AllowReflexAnglePanel allowReflexAnglePanel;
 		private AllowOutlyingIntersectionsPanel allowOutlyingIntersectionsPanel;
		private AuxiliaryObjectPanel auxPanel;
		private AnimationStepPanel animStepPanel;
		private SliderPanel sliderPanel;
		private SlopeTriangleSizePanel slopeTriangleSizePanel;
		private StartPointPanel startPointPanel;
		private CornerPointsPanel cornerPointsPanel;
		private TextEditPanel textEditPanel;
		private BackgroundImagePanel bgImagePanel;
		private AbsoluteScreenLocationPanel absScreenLocPanel;		

		public PropertiesPanel() {			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	
			//setLayout(new FlowLayout());
			
			setBorder(
				BorderFactory.createTitledBorder(app.getPlain("Properties")));

			showObjectPanel = new ShowObjectPanel();
			colorPanel = new ColorPanel();
			labelPanel = new LabelPanel();
			coordPanel = new CoordPanel();
			lineEqnPanel = new LineEqnPanel();
			conicEqnPanel = new ConicEqnPanel();
			pointSizePanel = new PointSizePanel();
			textOptionsPanel = new TextOptionsPanel();
			arcSizePanel = new ArcSizePanel();
			slopeTriangleSizePanel = new SlopeTriangleSizePanel();
			lineStylePanel = new LineStylePanel();
			fillingPanel = new FillingPanel();
			tracePanel = new TracePanel();
			fixPanel = new FixPanel();
			absScreenLocPanel = new AbsoluteScreenLocationPanel();
			auxPanel = new AuxiliaryObjectPanel();
			animStepPanel = new AnimationStepPanel(app);
			sliderPanel = new SliderPanel(app, this);
			startPointPanel = new StartPointPanel();
			cornerPointsPanel = new CornerPointsPanel();
			textEditPanel = new TextEditPanel();
			bgImagePanel = new BackgroundImagePanel();
 			allowReflexAnglePanel = new AllowReflexAnglePanel();
 			allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
		}			

		public void updateSelection(Object[] geos) {
			removeAll();
			repaint();
			if (geos != null && geos.length != 0) {
				Vector pVec = new Vector();							

				// visual stuff
				// object panel: show object & color
				JPanel objectPanel =
					new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));		
				
				JPanel p = showObjectPanel.update(geos);
				if (p != null)
					objectPanel.add(p);
				p = colorPanel.update(geos);
				if (p != null)
					objectPanel.add(p);
				if (objectPanel.getComponentCount() > 0) {					
					pVec.add(objectPanel);
				}					

				// label
				pVec.add(labelPanel.update(geos));									

				// trace, fix
				JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));	
				p = tracePanel.update(geos);
					if (p != null) linePanel.add(p);								
				p = fixPanel.update(geos);
					if (p != null) linePanel.add(p);															
				if (linePanel.getComponentCount() > 0)
					pVec.add(linePanel);
				
				// algebra stuff
				pVec.add(coordPanel.update(geos));
				pVec.add(lineEqnPanel.update(geos));
				pVec.add(conicEqnPanel.update(geos));				
				pVec.add(textEditPanel.update(geos));
				
				// visual stuff
				pVec.add(textOptionsPanel.update(geos));
				pVec.add(bgImagePanel.update(geos));
 				pVec.add(allowReflexAnglePanel.update(geos));
 				pVec.add(absScreenLocPanel.update(geos));				
				pVec.add(pointSizePanel.update(geos));				
				pVec.add(arcSizePanel.update(geos));
				pVec.add(slopeTriangleSizePanel.update(geos));							
				pVec.add(startPointPanel.update(geos));
				pVec.add(cornerPointsPanel.update(geos));
				pVec.add(lineStylePanel.update(geos));
				pVec.add(fillingPanel.update(geos));
				pVec.add(sliderPanel.update(geos));
				
				pVec.add(allowOutlyingIntersectionsPanel.update(geos));
				pVec.add(animStepPanel.update(geos));					
				pVec.add(auxPanel.update(geos));					

				// build new panel					
				for (int i = 0; i < pVec.size(); i++) {
					p = (JPanel) pVec.get(i);
					if (p != null) {						
						add(p);
						p.setAlignmentX(LEFT_ALIGNMENT);
						//p.setAlignmentY(Component.TOP_ALIGNMENT);
					}
				}
							
				// update size	
				packDialog();				
			}
		}

	} // PropertiesPanel

	/**
	 * panel with show/hide object checkbox
	 */
	private class ShowObjectPanel extends JPanel implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showObjectCB;

		public ShowObjectPanel() {
			// check box for show object
			showObjectCB = new JCheckBox(app.getPlain("ShowObject"));
			showObjectCB.addItemListener(this);			
			add(showObjectCB);
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showObjectCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalObjectVal = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.isSetEuclidianVisible()
					!= temp.isSetEuclidianVisible()) {
					equalObjectVal = false;
					break;
				}
			}

			// set object visible checkbox
			if (equalObjectVal)
				showObjectCB.setSelected(geo0.isSetEuclidianVisible());
			else
				showObjectCB.setSelected(false);

			showObjectCB.addItemListener(this);
			return this;
		}

		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof GeoNumeric) {
					GeoNumeric num = (GeoNumeric) geos[i];
					if (!num.isDrawable()) {
						geosOK = false;
						break;
					}
 				} 
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show object value changed
			if (source == showObjectCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setEuclidianVisible(showObjectCB.isSelected());
					geo.updateRepaint();
				}
			}
			propPanel.updateSelection(geos);
		}

	} // ShowObjectPanel

	/**
	 * panel with button for color choosing
	 */
	private class ColorPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private Color currentColor;
		private JButton colorButton;

		public ColorPanel() {
			// color		
			//		full block: \u2588, smiley: \u263b
			colorButton = new JButton("\u2588");
			colorButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setObjColor(app.showColorChooser(currentColor));
				}
			});

			//	objectPanel with show checkbox and color panel
			JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
			colorPanel.add(new JLabel(app.getPlain("Color") + ":"));
			colorPanel.add(colorButton);

			add(colorPanel);
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalObjColor = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object color
				if (!geo0.getColor().equals(temp.getColor())) {
					equalObjColor = false;
					break;
				}
			}

			// set colorButton's color to object color
			if (equalObjColor) {
				currentColor = geo0.getColor();
				colorButton.setForeground(currentColor);
			} else {
				currentColor = Color.lightGray;
				colorButton.setForeground(currentColor);
			}
			return this;
		}

		/**
		 * sets color of selected GeoElements
		 *
		 */
		private void setObjColor(Color col) {
			if (col == null)
				return;
			currentColor = col;
			colorButton.setForeground(currentColor);

			GeoElement geo;
			for (int i = 0; i < geos.length; i++) {
				geo = (GeoElement) geos[i];
				geo.setObjColor(col);
				geo.updateRepaint();
			}
		}

	
		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof GeoNumeric) {
					GeoNumeric num = (GeoNumeric) geos[i];
					if (!num.isDrawable())
						return false;
				} else if (geos[i] instanceof GeoImage)
					return false;
			}
			return true;
		}

	} // ColorPanel

	/**
	 * panel with label properties
	 */
	private class LabelPanel
		extends JPanel
		implements ItemListener, ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showLabelCB;
		private JComboBox labelModeCB;
		private boolean locusSelected;

		public LabelPanel() {
			// check boxes for show object, show label
			showLabelCB = new JCheckBox(app.getPlain("ShowLabel") + ":");
			showLabelCB.addItemListener(this);

			// combo box for label mode: name or algebra
			labelModeCB = new JComboBox();
			labelModeCB.addItem(app.getPlain("Name")); // index 0
			labelModeCB.addItem(
				app.getPlain("Name") + " & " + app.getPlain("Value"));
			// index 1
			labelModeCB.addItem(app.getPlain("Value")); // index 2
			labelModeCB.addActionListener(this);

			// labelPanel with show checkbox
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(showLabelCB);
			add(labelModeCB);			
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showLabelCB.removeItemListener(this);
			labelModeCB.removeActionListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalLabelVal = true;
			boolean equalLabelMode = true;
			locusSelected =  geo0 instanceof GeoLocus;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				//	same label visible value
				if (geo0.isLabelVisible() != temp.isLabelVisible())
					equalLabelVal = false;
				//	same label mode
				if (geo0.getLabelMode() != temp.getLabelMode())
					equalLabelMode = false;
				
				if (!locusSelected)
					locusSelected = temp instanceof GeoLocus;
			}

			//	set label visible checkbox
			if (equalLabelVal) {
				showLabelCB.setSelected(geo0.isLabelVisible());
				labelModeCB.setEnabled(geo0.isLabelVisible());
			} else {
				showLabelCB.setSelected(false);
				labelModeCB.setEnabled(false);
			}

			//	set label visible checkbox
			if (equalLabelMode)
				labelModeCB.setSelectedIndex(geo0.getLabelMode());
			else
				labelModeCB.setSelectedItem(null);

			// locus in selection
			labelModeCB.setEnabled(!locusSelected);
			
			showLabelCB.addItemListener(this);
			labelModeCB.addActionListener(this);
			return this;
		}

		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof GeoNumeric) {
					GeoNumeric num = (GeoNumeric) geos[i];
					if (!num.isDrawable()) {
						geosOK = false;
						break;
					}
				} else if (geos[i] instanceof GeoText || 
						   geos[i] instanceof GeoImage) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show label value changed
			if (source == showLabelCB) {
				boolean flag = showLabelCB.isSelected();
				labelModeCB.setEnabled(!locusSelected && flag);
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLabelVisible(flag);
					geo.updateRepaint();
				}
			}
		}

		/**
		* action listener implementation for label mode combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == labelModeCB) {
				GeoElement geo;
				int mode = labelModeCB.getSelectedIndex();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLabelMode(mode);
					geo.updateRepaint();
				}
			}
		}

	} // LabelPanel

	/**
	 * panel for trace
	 * @author Markus Hohenwarter
	 */
	private class TracePanel extends JPanel implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showTraceCB;

		public TracePanel() {
			// check boxes for show trace
			showTraceCB = new JCheckBox(app.getPlain("ShowTrace"));
			showTraceCB.addItemListener(this);
			add(showTraceCB);
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showTraceCB.removeItemListener(this);

			// check if properties have same values
			Traceable temp, geo0 = (Traceable) geos[0];
			boolean equalTrace = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (Traceable) geos[i];
				// same object visible value
				if (geo0.getTrace() != temp.getTrace())
					equalTrace = false;
			}

			// set trace visible checkbox
			if (equalTrace)
				showTraceCB.setSelected(geo0.getTrace());
			else
				showTraceCB.setSelected(false);

			showTraceCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof Traceable)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			Traceable geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == showTraceCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (Traceable) geos[i];
					geo.setTrace(showTraceCB.isSelected());
					geo.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel for fixing an object
	 * @author Markus Hohenwarter
	 */
	private class FixPanel extends JPanel implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showFixCB;

		public FixPanel() {
			// check boxes for show trace
			showFixCB = new JCheckBox(app.getPlain("FixObject"));
			showFixCB.addItemListener(this);
			add(showFixCB);
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showFixCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalFix = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.isFixed() != temp.isFixed())
					equalFix = false;
			}

			// set trace visible checkbox
			if (equalFix)
				showFixCB.setSelected(geo0.isFixed());
			else
				showFixCB.setSelected(false);

			showFixCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (!((GeoElement) geos[i]).isFixable())
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == showFixCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setFixed(showFixCB.isSelected());
					geo.updateRepaint();
				}
			}		
			if (propPanel != null)		
				propPanel.updateSelection(geos);
			else
				update(geos);
		}
	}

	/**
	 * panel to set object's absoluteScreenLocation flag
	 * @author Markus Hohenwarter
	 */
	private class AbsoluteScreenLocationPanel extends JPanel implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox cbAbsScreenLoc;

		public AbsoluteScreenLocationPanel() {
			// check boxes for show trace
			setLayout(new FlowLayout(FlowLayout.LEFT));
			cbAbsScreenLoc = new JCheckBox(app.getPlain("AbsoluteScreenLocation"));
			cbAbsScreenLoc.addItemListener(this);

			// put it all together		
			add(cbAbsScreenLoc);						
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			cbAbsScreenLoc.removeItemListener(this);

			// check if properties have same values
			AbsoluteScreenLocateable temp, geo0 = (AbsoluteScreenLocateable) geos[0];
			boolean equalVal = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (AbsoluteScreenLocateable) geos[i];
				// same object visible value
				if (geo0.isAbsoluteScreenLocActive() != temp.isAbsoluteScreenLocActive())
					equalVal = false;
			}

			// set checkbox
			if (equalVal)
				cbAbsScreenLoc.setSelected(geo0.isAbsoluteScreenLocActive());
			else
				cbAbsScreenLoc.setSelected(false);

			cbAbsScreenLoc.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof AbsoluteScreenLocateable) {
					AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geos[i];
					if (!absLoc.isAbsoluteScreenLocSetable())
						return false;
				}					
				else
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			AbsoluteScreenLocateable geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == cbAbsScreenLoc) {
				boolean flag = cbAbsScreenLoc.isSelected();
				EuclidianView ev = app.getEuclidianView();
				for (int i = 0; i < geos.length; i++) {
					geo = (AbsoluteScreenLocateable) geos[i];
					if (flag) {
						// convert real world to screen coords
						int x = ev.toScreenCoordX(geo.getRealWorldLocX());
						int y = ev.toScreenCoordY(geo.getRealWorldLocY());
						geo.setAbsoluteScreenLoc(x, y);							
					} else {
						// convert screen coords to real world 
						double x = ev.toRealWorldCoordX(geo.getAbsoluteScreenLocX());
						double y = ev.toRealWorldCoordY(geo.getAbsoluteScreenLocY());
						geo.setRealWorldLoc(x, y);
					}
					geo.setAbsoluteScreenLocActive(flag);															
					geo.toGeoElement().updateRepaint();
				}
				
				if (propPanel != null)		
					propPanel.updateSelection(geos);
				else
					update(geos);
			}
		}
	}	
	
	/**
	 * panel for angles to set whether reflex angles are allowed 
	 * @author Markus Hohenwarter
	 */
	private class AllowReflexAnglePanel extends JPanel implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox reflexAngleCB;

		public AllowReflexAnglePanel() {
			// check boxes for show trace			
			reflexAngleCB = new JCheckBox(app.getPlain("allowReflexAngle"));
			reflexAngleCB.addItemListener(this);
			
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(reflexAngleCB);			
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			reflexAngleCB.removeItemListener(this);

			// check if properties have same values
			GeoAngle temp, geo0 = (GeoAngle) geos[0];
			boolean equalReflexAngle = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (GeoAngle) geos[i];
				// same object visible value
				if (geo0.allowReflexAngle() != temp.allowReflexAngle())
					equalReflexAngle = false;
			}

			// set trace visible checkbox
			if (equalReflexAngle)
				reflexAngleCB.setSelected(geo0.allowReflexAngle());
			else
				reflexAngleCB.setSelected(false);

			reflexAngleCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo.isIndependent() || !(geo instanceof GeoAngle))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoAngle geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == reflexAngleCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoAngle) geos[i];
					geo.setAllowReflexAngle(reflexAngleCB.isSelected());
					geo.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel for limted paths to set whether outlying intersection points are allowed 
	 * @author Markus Hohenwarter
	 */
	private class AllowOutlyingIntersectionsPanel extends JPanel implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox outlyingIntersectionsCB;

		public AllowOutlyingIntersectionsPanel() {
			// check boxes for show trace			
			outlyingIntersectionsCB = new JCheckBox(app.getPlain("allowOutlyingIntersections"));
			outlyingIntersectionsCB.addItemListener(this);
			
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(outlyingIntersectionsCB);			
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			outlyingIntersectionsCB.removeItemListener(this);

			// check if properties have same values
			LimitedPath temp, geo0 = (LimitedPath) geos[0];
			boolean equalVal = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (LimitedPath) geos[i];
				// same value?
				if (geo0.allowOutlyingIntersections() != temp.allowOutlyingIntersections())
					equalVal = false;
			}

			// set trace visible checkbox
			if (equalVal)
				outlyingIntersectionsCB.setSelected(geo0.allowOutlyingIntersections());
			else
				outlyingIntersectionsCB.setSelected(false);

			outlyingIntersectionsCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof LimitedPath))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			LimitedPath geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == outlyingIntersectionsCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (LimitedPath) geos[i];
					geo.setAllowOutlyingIntersections(outlyingIntersectionsCB.isSelected());
					geo.toGeoElement().updateRepaint();
				}
			}
		}
	}

	
 	/**
	 * panel to set a background image (only one checkbox)
	 * @author Markus Hohenwarter
	 */
	private class BackgroundImagePanel extends JPanel implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox isBGimage;

		public BackgroundImagePanel() {
			// check boxes for show trace
			isBGimage = new JCheckBox(app.getPlain("BackgroundImage"));
			isBGimage.addItemListener(this);
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(isBGimage);
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			isBGimage.removeItemListener(this);

			// check if properties have same values
			GeoImage temp, geo0 = (GeoImage) geos[0];
			boolean equalIsBGimage = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (GeoImage) geos[i];
				// same object visible value
				if (geo0.isInBackground() != temp.isInBackground())
					equalIsBGimage = false;
			}

			// set trace visible checkbox
			if (equalIsBGimage)
				isBGimage.setSelected(geo0.isInBackground());
			else
				isBGimage.setSelected(false);

			isBGimage.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoImage))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoImage geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == isBGimage) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoImage) geos[i];
					geo.setInBackground(isBGimage.isSelected());
					geo.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel for making an object auxiliary 
	 * @author Markus Hohenwarter
	 */
	private class AuxiliaryObjectPanel extends JPanel implements ItemListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox auxCB;

		public AuxiliaryObjectPanel() {
			// check boxes for show trace
			setLayout(new FlowLayout(FlowLayout.LEFT));
			auxCB = new JCheckBox(app.getPlain("AuxiliaryObject"));
			auxCB.addItemListener(this);
			add(auxCB);			
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			auxCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalAux = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.isAuxiliaryObject() != temp.isAuxiliaryObject())
					equalAux = false;
			}

			// set trace visible checkbox
			if (equalAux)
				auxCB.setSelected(geo0.isAuxiliaryObject());
			else
				auxCB.setSelected(false);

			auxCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			// geo should be visible in algebra view
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!geo.isAlgebraVisible()) return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == auxCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setAuxiliaryObject(auxCB.isSelected());
				}
			}
		}
	}


	/**
	 * panel for location of vectors and text 
	 */
	private class StartPointPanel
		extends JPanel
		implements ActionListener, FocusListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JComboBox cbLocation;
		private DefaultComboBoxModel cbModel;

		public StartPointPanel() {
			// textfield for animation step
			JLabel label = new JLabel(app.getPlain("StartingPoint") + ": ");
			cbLocation = new JComboBox();
			cbLocation.setEditable(true);
			cbModel = new DefaultComboBoxModel();
			cbLocation.setModel(cbModel);
			label.setLabelFor(cbLocation);
			cbLocation.addActionListener(this);
			cbLocation.addFocusListener(this);

			// put it all together
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(label);
			add(cbLocation);			
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			cbLocation.removeActionListener(this);

			// repopulate model with names of points from the geoList's model
			cbModel.removeAllElements();
			cbModel.addElement(null);
			ListModel lm = geoList.getModel();
			int size = lm.getSize();
			for (int i = 0; i < size; i++) {
				Object o = lm.getElementAt(i);
				if (o instanceof GeoPoint) {
					GeoPoint p = (GeoPoint) o;
					boolean cycleDefinition = false;
					for (int j = 0; j < geos.length; j++) {
						GeoElement loc = ((Locateable) geos[j]).toGeoElement();
						if (p.isChildOf(loc)) {
							cycleDefinition = true;
							break;
						}
					}
					if (!cycleDefinition)
						cbModel.addElement(p.getLabel());
				}
			}

			// check if properties have same values
			Locateable temp, geo0 = (Locateable) geos[0];
			boolean equalLocation = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (Locateable) geos[i];
				// same object visible value
				if (geo0.getStartPoint() != temp.getStartPoint()) {
					equalLocation = false;
					break;
				}

			}

			// set location textfield
			GeoPoint p = geo0.getStartPoint();
			if (equalLocation && p != null) {
				cbLocation.setSelectedItem(p.getLabel());
			} else
				cbLocation.setSelectedItem(null);

			cbLocation.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof Locateable) || geos[i] instanceof GeoImage) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cbLocation)
				doActionPerformed();
		}

		private void doActionPerformed() {
			String strLoc = (String) cbLocation.getSelectedItem();
			GeoPoint newLoc = null;

			if (strLoc == null || strLoc.trim().length() == 0) {
				newLoc = null;
			} else {
				newLoc = app.getAlgebraController().evaluateToPoint(strLoc);
			}

			for (int i = 0; i < geos.length; i++) {
				Locateable l = (Locateable) geos[i];
				try {
					l.setStartPoint(newLoc);
					l.toGeoElement().updateRepaint();
				} catch (CircularDefinitionException e) {
					app.showError("CircularDefinition");
				}
			}

			propPanel.updateSelection(geos);
		}

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent e) {
			doActionPerformed();
		}
	}

	/**
	 * panel for three corner points of an image (A, B and D)
	 */
	private class CornerPointsPanel
		extends JPanel
		implements ActionListener, FocusListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JComboBox [] cbLocation;
		private DefaultComboBoxModel [] cbModel;

		public CornerPointsPanel() {
			cbLocation = new JComboBox[3];
			cbModel = new DefaultComboBoxModel[3];
			
			// put it all together
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			// textfield for animation step
			String strLabelEnd = ". " + app.getPlain("CornerPoint") + ": ";
			String strLabel;
			for (int i = 0; i < 3; i++) {
				strLabel = i < 2 ? (i+1) + strLabelEnd : (i+2) + strLabelEnd;
				JLabel label = new JLabel(strLabel);
				cbLocation[i] = new JComboBox();
				cbLocation[i].setEditable(true);
				cbModel[i] = new DefaultComboBoxModel();
				cbLocation[i].setModel(cbModel[i]);
				label.setLabelFor(cbLocation[i]);
				cbLocation[i].addActionListener(this);
				cbLocation[i].addFocusListener(this);
				
				JPanel locPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				locPanel.add(label);
				locPanel.add(cbLocation[i]);
				add(locPanel);
			}
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			for (int k=0; k<3; k++) {
				cbLocation[k].removeActionListener(this);
	
				// repopulate model with names of points from the geoList's model
				cbModel[k].removeAllElements();
				cbModel[k].addElement(null);
				ListModel lm = geoList.getModel();
				int size = lm.getSize();
				for (int i = 0; i < size; i++) {
					Object o = lm.getElementAt(i);
					if (o instanceof GeoPoint) {
						GeoPoint p = (GeoPoint) o;
						boolean cycleDefinition = false;
						for (int j = 0; j < geos.length; j++) {
							GeoElement loc = ((Locateable) geos[j]).toGeoElement();
							if (p.isChildOf(loc)) {
								cycleDefinition = true;
								break;
							}
						}
						if (!cycleDefinition)
							cbModel[k].addElement(p.getLabel());
					}
				}
	
				// check if properties have same values
				GeoImage temp, geo0 = (GeoImage) geos[0];
				boolean equalLocation = true;
	
				for (int i = 0; i < geos.length; i++) {
					temp = (GeoImage) geos[i];
					// same object visible value
					if (geo0.getCorner(k) != temp.getCorner(k)) {
						equalLocation = false;
						break;
					}	
				}
	
				// set location textfield
				GeoPoint p = geo0.getCorner(k);
				if (equalLocation && p != null) {
					cbLocation[k].setSelectedItem(p.getLabel());
				} else
					cbLocation[k].setSelectedItem(null);
	
				cbLocation[k].addActionListener(this);
			}
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo instanceof GeoImage) {
					GeoImage img = (GeoImage) geo;
					if (img.isAbsoluteScreenLocActive() ||
							!img.isIndependent())
						return false;					
				} else
					return false;
			}
			return true;
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {	
			doActionPerformed(e.getSource());
		}

		private void doActionPerformed(Object source) {
			int number = 0;
			if (source == cbLocation[1])
				number = 1;
			else if (source == cbLocation[2])
				number = 2;
			
			String strLoc = (String) cbLocation[number].getSelectedItem();
			GeoPoint newLoc = null;

			if (strLoc == null || strLoc.trim().length() == 0) {
				newLoc = null;
			} else {
				newLoc = app.getAlgebraController().evaluateToPoint(strLoc);
			}

			for (int i = 0; i < geos.length; i++) {
				GeoImage im = (GeoImage) geos[i];		
				im.setCorner(newLoc, number);
				im.updateRepaint();				
			}

			propPanel.updateSelection(geos);
		}

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent e) {
			doActionPerformed(e.getSource());
		}
	}

	/**
	 * panel for text to open edit window
	 */
	private class TextEditPanel
		extends JPanel
		implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JButton btEdit;
		private JTextField tf;
		
		private static final int PREVIEW_TEXT_LENGTH = 28;

		public TextEditPanel() {			
			tf = new JTextField(PREVIEW_TEXT_LENGTH / 2);
//			int fontSize = app.getFontSize();					
			
			
			tf.setEditable(false);
			//tf.setEnabled(false);			
			btEdit = new JButton(app.getImageIcon("redefine.gif"));
			btEdit.setToolTipText(app.getPlain("Edit"));
			btEdit.addActionListener(this);
						
			/*
			Border compound =
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(app.getPlain("Text")),
					BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setBorder(compound);*/
								
			//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));			
			
			//add(Box.createHorizontalGlue());
			//add(Box.createRigidArea(new Dimension(0, 5)));
			add(btEdit);
			add(tf);	
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (geos.length != 1 || !checkGeos(geos))
				return null;			
			
			GeoElement geo = (GeoElement) geos[0];
			String text; 
			if (geo.isIndependent())
				text = geo.toValueString();
			else
				text = geo.getDefinitionDescription();
		
			// shorten text to max PREVIEW_TEXT_LENGTH characters
			if (text != null) {
				text.replaceAll("\n", " ");
				if (text.length() > PREVIEW_TEXT_LENGTH)
					text = text.substring(0, PREVIEW_TEXT_LENGTH) + "...";				
			}
			
			tf.setText(text);			
			btEdit.setEnabled(!geo.isFixed());
			
			//Dimension dim = getPreferredSize();
			//dim.width = Math.min(300, dim.width);
			//setPreferredSize(dim);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			return geos.length == 1 && geos[0] instanceof GeoText;			
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btEdit)
				app.showTextDialog((GeoText) geos[0]);
		}
	}

	/**
	 * panel to select the kind of coordinates (cartesian or polar)
	 *  for GeoPoint and GeoVector
	 * @author Markus Hohenwarter
	 */
	private class CoordPanel extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JComboBox coordCB;		

		public CoordPanel() {
			JLabel coordLabel = new JLabel(app.getPlain("Coordinates")+":");
			coordCB = new JComboBox();
			coordCB.addItem(app.getPlain("CartesianCoords")); // index 0
			coordCB.addItem(app.getPlain("PolarCoords")); // index 1
			coordCB.addActionListener(this);

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(coordLabel);
			add(coordCB);
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			coordCB.removeActionListener(this);

			//	check if properties have same values
			GeoVec3D temp, geo0 = (GeoVec3D) geos[0];
			boolean equalMode = true;
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoVec3D) geos[i];
				// same mode?
				if (geo0.getMode() != temp.getMode())
					equalMode = false;
			}

			int mode;
			if (equalMode)
				mode = geo0.getMode();
			else
				mode = -1;
			switch (mode) {
				case Kernel.COORD_CARTESIAN :
					coordCB.setSelectedIndex(0);
					break;
				case Kernel.COORD_POLAR :
					coordCB.setSelectedIndex(1);
					break;
				default :
					coordCB.setSelectedItem(null);
			}

			coordCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoPoint
					|| geos[i] instanceof GeoVector)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* action listener implementation for coord combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == coordCB) {
				GeoVec3D geo;
				switch (coordCB.getSelectedIndex()) {
					case 0 : // Kernel.CARTESIAN					
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoVec3D) geos[i];
							geo.setMode(Kernel.COORD_CARTESIAN);
							geo.updateRepaint();
						}
						break;

					case 1 : // Kernel.POLAR					
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoVec3D) geos[i];
							geo.setMode(Kernel.COORD_POLAR);
							geo.updateRepaint();
						}
						break;
				}
			}
		}
	}

	/**
	 * panel to select the kind of line equation 
	 *  for GeoLine 
	 * @author Markus Hohenwarter
	 */
	private class LineEqnPanel extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private DefaultComboBoxModel eqnCBmodel;
		private JComboBox eqnCB;
		private JLabel eqnLabel;

		public LineEqnPanel() {
			eqnLabel = new JLabel(app.getPlain("Equation") + ":");
			eqnCB = new JComboBox();
			eqnCBmodel = new DefaultComboBoxModel();
			eqnCB.setModel(eqnCBmodel);
			eqnCBmodel.addElement(app.getPlain("ImplicitLineEquation"));
			// index 0
			eqnCBmodel.addElement(app.getPlain("ExplicitLineEquation"));
			// index 1		
			eqnCBmodel.addElement(app.getPlain("ParametricForm")); // index 2
			eqnCB.addActionListener(this);

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(eqnLabel);
			add(eqnCB);
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			eqnCB.removeActionListener(this);

			//	check if properties have same values
			GeoLine temp, geo0 = (GeoLine) geos[0];
			boolean equalMode = true;
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoLine) geos[i];
				// same mode?
				if (geo0.getMode() != temp.getMode())
					equalMode = false;
			}

			int mode;
			if (equalMode)
				mode = geo0.getMode();
			else
				mode = -1;
			switch (mode) {
				case GeoLine.EQUATION_IMPLICIT :
					eqnCB.setSelectedIndex(0);
					break;
				case GeoLine.EQUATION_EXPLICIT :
					eqnCB.setSelectedIndex(1);
					break;
				case GeoLine.PARAMETRIC :
					eqnCB.setSelectedIndex(2);
					break;
				default :
					eqnCB.setSelectedItem(null);
			}

			eqnCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoLine)
					|| geos[i] instanceof GeoSegment) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* action listener implementation for coord combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == eqnCB) {
				GeoLine geo;
				switch (eqnCB.getSelectedIndex()) {
					case 0 : // GeoLine.EQUATION_IMPLICIT				
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoLine) geos[i];
							geo.setMode(GeoLine.EQUATION_IMPLICIT);
							geo.updateRepaint();
						}
						break;

					case 1 : // GeoLine.EQUATION_EXPLICIT				
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoLine) geos[i];
							geo.setMode(GeoLine.EQUATION_EXPLICIT);
							geo.updateRepaint();
						}
						break;

					case 2 : // GeoLine.PARAMETRIC	
						for (int i = 0; i < geos.length; i++) {
							geo = (GeoLine) geos[i];
							geo.setMode(GeoLine.PARAMETRIC);
							geo.updateRepaint();
						}
						break;
				}
			}
		}
	}

	/**
	 * panel to select the kind of conic equation 
	 *  for GeoConic 
	 * @author Markus Hohenwarter
	 */
	private class ConicEqnPanel extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private DefaultComboBoxModel eqnCBmodel;
		private JComboBox eqnCB;
		private JLabel eqnLabel;
		int implicitIndex, explicitIndex, specificIndex;

		public ConicEqnPanel() {
			eqnLabel = new JLabel(app.getPlain("Equation") + ":");
			eqnCB = new JComboBox();
			eqnCBmodel = new DefaultComboBoxModel();
			eqnCB.setModel(eqnCBmodel);
			eqnCB.addActionListener(this);

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(eqnLabel);
			add(eqnCB);
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			eqnCB.removeActionListener(this);

			// check if all conics have same type and mode
			// and if specific, explicit is possible		 
			GeoConic temp, geo0 = (GeoConic) geos[0];
			boolean equalType = true;
			boolean equalMode = true;
			boolean specificPossible = geo0.isSpecificPossible();
			boolean explicitPossible = geo0.isExplicitPossible();
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoConic) geos[i];
				// same type?
				if (geo0.getType() != temp.getType())
					equalType = false;
				// same mode?
				if (geo0.getMode() != temp.getMode())
					equalMode = false;
				// specific equation possible?
				if (!temp.isSpecificPossible())
					specificPossible = false;
				// explicit equation possible?
				if (!temp.isExplicitPossible())
					explicitPossible = false;
			}

			// specific can't be shown because there are different types
			if (!equalType)
				specificPossible = false;

			specificIndex = -1;
			explicitIndex = -1;
			implicitIndex = -1;
			int counter = -1;
			eqnCBmodel.removeAllElements();
			if (specificPossible) {
				eqnCBmodel.addElement(geo0.getSpecificEquation());
				specificIndex = ++counter;
			}
			if (explicitPossible) {
				eqnCBmodel.addElement(app.getPlain("ExplicitConicEquation"));
				explicitIndex = ++counter;
			}
			implicitIndex = ++counter;
			eqnCBmodel.addElement(app.getPlain("ImplicitConicEquation"));

			int mode;
			if (equalMode)
				mode = geo0.getMode();
			else
				mode = -1;
			switch (mode) {
				case GeoConic.EQUATION_SPECIFIC :
					if (specificIndex > -1)
						eqnCB.setSelectedIndex(specificIndex);
					break;

				case GeoConic.EQUATION_EXPLICIT :
					if (explicitIndex > -1)
						eqnCB.setSelectedIndex(explicitIndex);
					break;

				case GeoConic.EQUATION_IMPLICIT :
					eqnCB.setSelectedIndex(implicitIndex);
					break;

				default :
					eqnCB.setSelectedItem(null);
			}

			eqnCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (geos[i].getClass() != GeoConic.class) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* action listener implementation for coord combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == eqnCB) {
				GeoConic geo;
				int selIndex = eqnCB.getSelectedIndex();
				if (selIndex == specificIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToSpecific();
						geo.updateRepaint();
					}
				} else if (selIndex == explicitIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToExplicit();
						geo.updateRepaint();
					}
				} else if (selIndex == implicitIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToImplicit();
						geo.updateRepaint();
					}
				}
			}
		}
	}

	/**
	 * panel to select the size of a GeoPoint
	 * @author Markus Hohenwarter
	 */
	private class PointSizePanel extends JPanel implements ChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public PointSizePanel() {			
			//setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
			//JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");		
		
			slider = new JSlider(1, 9);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);	
			
			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;
			slider.setMaximumSize(dim);		
			slider.setPreferredSize(dim);	
			*/

			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);			
			
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size") ));		
			add(slider);			
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first point's size 
			GeoPoint geo0 = (GeoPoint) geos[0];
			slider.setValue(geo0.getPointSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoPoint)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* change listener implementation for slider
		*/
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				GeoPoint point;
				for (int i = 0; i < geos.length; i++) {
					point = (GeoPoint) geos[i];
					point.setPointSize(size);
					point.updateRepaint();
				}
			}
		}
	}
	
	/**
	 * panel to select the size of a GeoText
	 * @author Markus Hohenwarter
	 */
	private class TextOptionsPanel extends JPanel implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JComboBox cbFont, cbSize, cbDecimalPlaces;		
		private JToggleButton btBold, btItalic;
		
		private JPanel secondLine;
		private boolean secondLineVisible = false;

		public TextOptionsPanel() {	
			// font: serif, sans serif
			String [] fonts = { "Sans Serif", "Serif" };
			cbFont = new JComboBox(fonts);
			cbFont.addActionListener(this);	
			
			// font size					
			cbSize = new JComboBox();
			int fontSize = app.getFontSize();			
			for (int i=fontSize - 2; i <= fontSize + 6; i = i + 2) {
				cbSize.addItem(Integer.toString(i));
			}
			cbSize.addActionListener(this);						
			
			// toggle buttons for bold and italic
			btBold = new JToggleButton(app.getPlain("Bold").substring(0,1));
			btBold.setFont(app.getBoldFont());
			btBold.addActionListener(this);			
			btItalic = new JToggleButton(app.getPlain("Italic").substring(0,1));
			btItalic.setFont(app.getPlainFont().deriveFont(Font.ITALIC));
			btItalic.addActionListener(this);		
			
			// decimal places			
		    String[] strDecimalSpaces = { null,  "0", "1", "2", "3", "4", "5" };   
		    cbDecimalPlaces = new JComboBox(strDecimalSpaces);
		    cbDecimalPlaces.addActionListener(this);
		    
			// font, size
			JPanel firstLine = new JPanel();
			firstLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));			
			firstLine.add(cbFont);			
			firstLine.add(cbSize);	
			firstLine.add(btBold);
			firstLine.add(btItalic);
			
			// bold, italic
			secondLine = new JPanel();
			secondLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));		
			JLabel decimalLabel  = new JLabel(app.getMenu("DecimalPlaces") + ":");					
			secondLine.add(decimalLabel);
			secondLine.add(cbDecimalPlaces);									
			
			setLayout(new BorderLayout(5,5));
			add(firstLine, BorderLayout.NORTH);
			add(secondLine, BorderLayout.SOUTH);	
			secondLineVisible = true;
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;			
			
			cbSize.removeActionListener(this);
			cbFont.removeActionListener(this);
			cbDecimalPlaces.removeActionListener(this);

			//	set value to first text's size and style
			GeoText geo0 = (GeoText) geos[0];		
			
			cbSize.setSelectedItem(Integer.toString(geo0.getFontSize()+app.getFontSize()));
			cbFont.setSelectedIndex(geo0.isSerifFont() ? 1 : 0);
			
			int decimals = geo0.getPrintDecimals();
			Object selItem = decimals < 0 ? null :  Integer.toString(decimals);
			cbDecimalPlaces.setSelectedItem(selItem);
			
			if (geo0.isIndependent()) {
				if (secondLineVisible) {
					remove(secondLine);	
					secondLineVisible = false;
				}				
			} else {
				if (!secondLineVisible) {
					add(secondLine, BorderLayout.SOUTH);
					secondLineVisible = true;
				}	
			}
		
			int style = geo0.getFontStyle();
			btBold.setSelected(style == Font.BOLD || style == (Font.BOLD + Font.ITALIC));
			btItalic.setSelected(style == Font.ITALIC || style == (Font.BOLD + Font.ITALIC));				
			
			
			
			cbSize.addActionListener(this);
			cbFont.addActionListener(this);			
			cbDecimalPlaces.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoText)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* change listener implementation for slider
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			
			if (source == cbSize) {
				int size = Integer.parseInt(cbSize.getSelectedItem().toString()) - app.getFontSize();			
				GeoText text;
				for (int i = 0; i < geos.length; i++) {
					text = (GeoText) geos[i];
					text.setFontSize(size);
					text.updateRepaint();
				}
			} 
			else if (source == cbFont) {
				boolean serif = cbFont.getSelectedIndex() == 1;		
				GeoText text;
				for (int i = 0; i < geos.length; i++) {
					text = (GeoText) geos[i];
					text.setSerifFont(serif);
					text.updateRepaint();
				}
			}
			else if (source == cbDecimalPlaces) {
				Object selItem = cbDecimalPlaces.getSelectedItem();
				int decimals = selItem == null ? -1 : Integer.parseInt((String) selItem);
				GeoText text;
				for (int i = 0; i < geos.length; i++) {
					text = (GeoText) geos[i];
					text.setPrintDecimals(decimals);					 
					text.updateRepaint();
				}
			}
			else if (source == btBold || source == btItalic) {
				int style = 0;
				if (btBold.isSelected()) style += 1;
				if (btItalic.isSelected()) style += 2;
					
				GeoText text;
				for (int i = 0; i < geos.length; i++) {
					text = (GeoText) geos[i];
					text.setFontStyle(style);
					text.updateRepaint();
				}
			}								
		}
	}
	
	/**
	 * panel to select the size of a GeoPoint
	 * @author Markus Hohenwarter
	 */
	private class SlopeTriangleSizePanel
		extends JPanel
		implements ChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public SlopeTriangleSizePanel() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
			//JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");		
			slider = new JSlider(1, 10);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;			
			slider.setMaximumSize(dim);
			slider.setPreferredSize(dim);
*/
			
			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			//slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);

			/*
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));	
			sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			slider.setAlignmentY(Component.TOP_ALIGNMENT);			
			//setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
			//		BorderFactory.createEmptyBorder(3,5,0,5)));	
			add(Box.createRigidArea(new Dimension(5,0)));
			add(sizeLabel);
			*/		
			add(slider);			
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first point's size 
			GeoNumeric geo0 = (GeoNumeric) geos[0];
			slider.setValue(geo0.getSlopeTriangleSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof GeoNumeric
					&& geo.getParentAlgorithm() instanceof AlgoSlope)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* change listener implementation for slider
		*/
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				GeoNumeric num;
				for (int i = 0; i < geos.length; i++) {
					num = (GeoNumeric) geos[i];
					num.setSlopeTriangleSize(size);
					num.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel to select the size of a GeoAngle's arc
	 * @author Markus Hohenwarter
	 */
	private class ArcSizePanel extends JPanel implements ChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public ArcSizePanel() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
			//JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");		
			slider = new JSlider(10, 100);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;
			slider.setMaximumSize(dim);
			slider.setPreferredSize(dim);
*/
			
			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			/*
			//slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));	
			sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			slider.setAlignmentY(Component.TOP_ALIGNMENT);			
			//setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
			//		BorderFactory.createEmptyBorder(3,5,0,5)));
			add(Box.createRigidArea(new Dimension(5,0)));
			add(sizeLabel);
			*/		
			add(slider);			
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first point's size 
			GeoAngle geo0 = (GeoAngle) geos[0];
			slider.setValue(geo0.getArcSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof GeoAngle) {
					GeoAngle angle = (GeoAngle) geos[i];
					if (angle.isIndependent() || !angle.isDrawable()) {
						geosOK = false;
						break;
					}
				} else {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* change listener implementation for slider
		*/
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				GeoAngle angle;
				for (int i = 0; i < geos.length; i++) {
					angle = (GeoAngle) geos[i];
					angle.setArcSize(size);
					angle.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel to select the filling of a polygon or conic section
	 * @author Markus Hohenwarter
	 */
	private class FillingPanel extends JPanel implements ChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public FillingPanel() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Filling")));
			//JLabel sizeLabel = new JLabel(app.getPlain("Filling") + ":");		
			slider = new JSlider(0, 100);
			slider.setMajorTickSpacing(25);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;
			slider.setMaximumSize(dim);
			slider.setPreferredSize(dim);
*/
			
			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			/*
			//slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));	
			sizeLabel.setAlignmentY(TOP_ALIGNMENT);
			slider.setAlignmentY(TOP_ALIGNMENT);			
			//setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
			//		BorderFactory.createEmptyBorder(3,5,0,5)));
			add(Box.createRigidArea(new Dimension(5,0)));			
			add(sizeLabel);			
			*/
			add(slider);			
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			//	set value to first geo's alpha value
			double alpha = ((GeoElement) geos[0]).getAlphaValue();
			slider.setValue((int) Math.round(alpha * 100));

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!((GeoElement) geos[i]).isFillable()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* change listener implementation for slider
		*/
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				float alpha = slider.getValue() / 100.0f;
				GeoElement geo;
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setAlphaValue(alpha);
					geo.updateRepaint();
				}
			}
		}
	}

	/**
	 * panel to select thickness and style (dashing) of a GeoLine
	 * @author Markus Hohenwarter
	 */
	private class LineStylePanel
		extends JPanel
		implements ChangeListener, ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;
		private JComboBox dashCB;

		public LineStylePanel() {
			// thickness slider		
			slider = new JSlider(1, 13);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			Dimension dim = slider.getPreferredSize();
			dim.width = SLIDER_MAX_WIDTH;
			slider.setMaximumSize(dim);
			slider.setPreferredSize(dim);
*/
			
			// set label font
			Dictionary labelTable = slider.getLabelTable();
			Enumeration en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}
			//slider.setFont(app.getSmallFont());	
			slider.addChangeListener(this);

			// line style combobox (dashing)		
			DashListRenderer renderer = new DashListRenderer();
			renderer.setPreferredSize(
				new Dimension(130, app.getFontSize() + 6));
			dashCB = new JComboBox(EuclidianView.getLineTypes());
			dashCB.setRenderer(renderer);
			dashCB.addActionListener(this);

			// line style panel
			JPanel dashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel dashLabel = new JLabel(app.getPlain("LineStyle") + ":");
			dashPanel.add(dashLabel);
			dashPanel.add(dashCB);

			// thickness panel
			JPanel thicknessPanel = new JPanel();
			thicknessPanel.setBorder(
				BorderFactory.createTitledBorder(app.getPlain("Thickness")));
			/*
			JLabel thicknessLabel = new JLabel(app.getPlain("Thickness") + ":");
			thicknessPanel.setLayout(new BoxLayout(thicknessPanel, BoxLayout.X_AXIS));	
			thicknessLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			slider.setAlignmentY(Component.TOP_ALIGNMENT);			
			//thicknessPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
			//		BorderFactory.createEmptyBorder(3,5,0,5)));	
			thicknessPanel.add(Box.createRigidArea(new Dimension(5,0)));
			thicknessPanel.add(thicknessLabel);
			*/				
			thicknessPanel.add(slider);			

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			thicknessPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			dashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);	
			add(dashPanel);
			add(thicknessPanel);
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);
			dashCB.removeActionListener(this);

			//	set slider value to first geo's thickness 
			GeoElement temp, geo0 = (GeoElement) geos[0];
			slider.setValue(geo0.getLineThickness());

			//	check if geos have same line style
			boolean equalStyle = true;
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same style?
				if (geo0.getLineType() != temp.getLineType())
					equalStyle = false;
			}

			// select common line style
			if (equalStyle) {
				int type = geo0.getLineType();				
				for (int i = 0; i < dashCB.getItemCount(); i++) {
					if (type == ((Integer) dashCB.getItemAt(i)).intValue()) {
						dashCB.setSelectedIndex(i);
						break;
					}
				}
			} else
				dashCB.setSelectedItem(null);

			slider.addChangeListener(this);
			dashCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoLine
					|| geos[i] instanceof GeoVector
					|| geos[i] instanceof GeoConic
					|| geos[i] instanceof GeoFunction
					|| geos[i] instanceof GeoPolygon
					|| geos[i] instanceof GeoLocus
					|| (geos[i] instanceof GeoNumeric
						&& ((GeoNumeric) geos[i]).isDrawable()))) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		* change listener implementation for slider
		*/
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				GeoElement geo;
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLineThickness(size);
					geo.updateRepaint();
				}
			}
		}

		/**
		* action listener implementation for coord combobox
		*/
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == dashCB) {
				GeoElement geo;
				int type = ((Integer) dashCB.getSelectedItem()).intValue();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLineType(type);
					geo.updateRepaint();
				}
			}
		}
	} // LineStylePanel

	/**
	 * INNER CLASS
	 * JList for displaying GeoElements
	 * @see GeoListCellRenderer
	 * @author Markus Hohenwarter
	 */
	private class JListGeoElements extends JList implements View {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private DefaultListModel listModel = new DefaultListModel();

		/*
		 * has to be registered as view for GeoElement 
		 */
		public JListGeoElements() {
			setModel(listModel);
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setLayoutOrientation(JList.VERTICAL);
			setVisibleRowCount(8);
			GeoListCellRenderer renderer = new GeoListCellRenderer();
			setCellRenderer(renderer);
			setSelectionBackground(Application.COLOR_SELECTION);
		}

		/**
		 * selects object geo in the list of GeoElements	 
		 * @param addToSelection: false => clear old selection 
		 */
		public void setSelected(ArrayList geos, boolean addToSelection) {
			ListSelectionModel lsm = getSelectionModel();
			if (geos == null) {
				if (lsm.isSelectionEmpty()) {
					// select first if list is not empty
					if (listModel.size() > 0) {
						lsm.setSelectionInterval(0, 0);
						fireSelectionValueChanged(0, listModel.size(), false);
					}
				}			
			}			
			else {
				if (!addToSelection) 
					lsm.clearSelection();				
				int minPos = listModel.getSize()-1;
				for (int i=0; i<geos.size(); i++) {
					int pos = listModel.indexOf(geos.get(i));	
					if (pos < minPos) minPos = pos;				
					lsm.addSelectionInterval(pos, pos);													
				}
				fireSelectionValueChanged(0, listModel.getSize(), false);	
				ensureIndexIsVisible(minPos);				
			}						
		}	

		public void clearSelection() {
			getSelectionModel().clearSelection();
		}

		/**
		 * Clears the list.
		 */
		public void clear() {
			listModel.clear();
		}

		/* **********************/
		/* VIEW IMPLEMENTATION */
		/* **********************/						
		
		/**
		   * adds a new element to the list
		   */
		public void add(GeoElement geo) {	
			/*
			if (listModel.contains(geo)) {
				update(geo);
				return;
			}*/
			
			if (geo.isLabelSet() && geo.hasProperties()) {	
				// add node to model (alphabetically ordered)			
				try {
					int pos = getInsertPosition(geo);							
					listModel.add(pos, geo);
				} catch (Exception e) { 
					System.err.println(e.getMessage());
				}
			}
		}				

		// geo should be inserted in alphabetical order
		private int getInsertPosition(GeoElement geo) {
			int size = listModel.size();
			int insertPos = size;
			String label = geo.getLabel();
			for (int i = 0; i < size; i++) {				 
				GeoElement g = (GeoElement) listModel.get(i);
				if (label.compareTo(g.getLabel()) < 0) {
					insertPos = i;
					break;
				}
			}
			return insertPos;
		}

		/**
		 * removes an element from the list
		 */
		public void remove(GeoElement geo) {
			listModel.removeElement(geo);
		}

		/**
		 * renames an element and sorts list 
		 */
		public void rename(GeoElement geo) {
			remove(geo);			
			add(geo);
			geoElementSelected(geo, false);
		}

		/**
		 * updates a list of elements
		 */
		public void update(GeoElement geo) {
			repaint();
		}

		public void updateAuxiliaryObject(GeoElement geo) {
			repaint();
		}

		public void reset() {
			repaint();
		}

		public void clearView() {
			listModel.clear();
		}

	} // JListGeoElements


	/*
	 * Keylistener implementation of PropertiesDialog
	 */

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
			case KeyEvent.VK_ESCAPE :
				cancel();
				break;

			case KeyEvent.VK_ENTER :
				// needed for input fields
				//applyButton.doClick();
				break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void windowGainedFocus(WindowEvent arg0) {
		app.setSelectionListenerMode(this);
		selectionChanged();
	}
		

	public void windowLostFocus(WindowEvent arg0) {
	}

} // PropertiesDialog

/**
 * panel for numeric slider
 * @author Markus Hohenwarter
 */
class SliderPanel
	extends JPanel
	implements ActionListener, FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] geos; // currently selected geos
	private JTextField tfMin, tfMax, tfWidth;
	private JTextField [] tfields;
	private JCheckBox cbSliderFixed;
	private JComboBox coSliderHorizontal;
	
	private Application app;
	private PropertiesDialog.PropertiesPanel propPanel;
	private Kernel kernel;

	public SliderPanel(Application app, PropertiesDialog.PropertiesPanel propPanel) {
		this.app = app;
		kernel = app.getKernel();
		this.propPanel = propPanel;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
		JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,5));
		//Border compound =
		//	BorderFactory.createCompoundBorder(
		//		BorderFactory.createTitledBorder(app.getPlain("Interval")),
		//		BorderFactory.createEmptyBorder(5, 5, 5, 5));
		Border compound =
			BorderFactory.createTitledBorder(app.getPlain("Interval"));
		intervalPanel.setBorder(compound);			
		
		JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5, 5));
		compound =
			BorderFactory.createTitledBorder(app.getPlain(app.getPlain("Slider")));
		sliderPanel.setBorder(compound);
		
		String [] comboStr = {app.getPlain("horizontal"), app.getPlain("vertical")};
		coSliderHorizontal = new JComboBox(comboStr);
		coSliderHorizontal.addActionListener(this);
		sliderPanel.add(coSliderHorizontal);
					
		String[] labels = { app.getPlain("min")+":",
							app.getPlain("max")+":", app.getPlain("Width")+":"};
		tfMin = new JTextField(8);
		tfMax = new JTextField(8);
		tfWidth = new JTextField(4);
		tfields = new JTextField[3];
		tfields[0] = tfMin;
		tfields[1] = tfMax;
		tfields[2] = tfWidth;
		int numPairs = labels.length;

		//	add textfields
		for (int i = 0; i < numPairs; i++) {
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		    JLabel l = new JLabel(labels[i], SwingConstants.LEADING);
		    p.add(l);
		    JTextField textField = tfields[i];
		    l.setLabelFor(textField);
		    textField.addActionListener(this);
		    textField.addFocusListener(this);
		    p.add(textField);
		    p.setAlignmentX(Component.LEFT_ALIGNMENT);
		    
		    if (i < 2)
		    	intervalPanel.add(p);
		    else 
		    	sliderPanel.add(p);
		}														
		
		cbSliderFixed = new JCheckBox(app.getPlain("fixed"));
		cbSliderFixed.addActionListener(this);
		sliderPanel.add(cbSliderFixed);
		
		add(intervalPanel);	
		add(sliderPanel);					
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;
		
		for (int i=0; i<tfields.length; i++) 
			tfields[i].removeActionListener(this);

		// check if properties have same values
		GeoNumeric temp, num0 = (GeoNumeric) geos[0];
		boolean equalMax = true;
		boolean equalMin = true;
		boolean equalWidth = true;
		boolean equalSliderFixed = true;
		boolean equalSliderHorizontal = true;
		boolean onlyAngles = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoNumeric) geos[i];

			if (!num0.isIntervalMinActive() || !temp.isIntervalMinActive() || !kernel.isEqual(num0.getIntervalMin(), temp.getIntervalMin()))
				equalMin = false;
			if (!num0.isIntervalMaxActive() || !temp.isIntervalMaxActive() || !kernel.isEqual(num0.getIntervalMax(), temp.getIntervalMax()))
				equalMax = false;
			if (!kernel.isEqual(num0.getSliderWidth(), temp.getSliderWidth()))
				equalWidth = false;
			if (num0.isSliderFixed() != temp.isSliderFixed())
				equalSliderFixed = false;
			if (num0.isSliderHorizontal() != temp.isSliderHorizontal())
				equalSliderHorizontal = false;
			
			if (!(temp instanceof GeoAngle))
				onlyAngles = false;
		}

		// set values
		int oldDigits = kernel.getMaximumFractionDigits();
		kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		if (equalMin){
			if (onlyAngles)
				tfMin.setText(kernel.formatAngle(num0.getIntervalMin()).toString());
			else
				tfMin.setText(kernel.format(num0.getIntervalMin()));
		} else {
			tfMin.setText("");
		}
		
		if (equalMax){
			if (onlyAngles)
				tfMax.setText(kernel.formatAngle(num0.getIntervalMax()).toString());
			else
				tfMax.setText(kernel.format(num0.getIntervalMax()));
		} else {
			tfMax.setText("");
		}
		
		if (equalWidth){
			tfWidth.setText(kernel.format(num0.getSliderWidth()));
		} else {
			tfMax.setText("");
		}
		kernel.setMaximumFractionDigits(oldDigits);
		
		if (equalSliderFixed)
			cbSliderFixed.setSelected(num0.isSliderFixed());
		
		if (equalSliderHorizontal) {
			coSliderHorizontal.setSelectedIndex(num0.isSliderHorizontal() ? 0 : 1);
		}
			

		for (int i=0; i<tfields.length; i++) 
			tfields[i].addActionListener(this);
	
		return this;
	}

	private boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
 			if (!(geo.isIndependent() && geo instanceof GeoNumeric)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == cbSliderFixed) 
			doCheckBoxActionPerformed((JCheckBox) source);
		else if (source == coSliderHorizontal)
			doComboBoxActionPerformed((JComboBox) source);
		else
			doTextFieldActionPerformed((JTextField) e.getSource());
	}
	
	private void doCheckBoxActionPerformed(JCheckBox source) {	
		boolean fixed = source.isSelected();			
		for (int i = 0; i < geos.length; i++) {
			GeoNumeric num = (GeoNumeric) geos[i];
			num.setSliderFixed(fixed);
			num.updateRepaint();
		}
		update(geos);
	}
	
	private void doComboBoxActionPerformed(JComboBox source) {	
		boolean horizontal = source.getSelectedIndex() == 0;			
		for (int i = 0; i < geos.length; i++) {
			GeoNumeric num = (GeoNumeric) geos[i];
			num.setSliderHorizontal(horizontal);
			num.updateRepaint();
		}
		update(geos);
	}

	private void doTextFieldActionPerformed(JTextField source) {			
		String inputText = source.getText().trim();
		boolean emptyString = inputText.equals("");
		double value = Double.NaN;
		if (!emptyString) {
			value = app.getAlgebraController().evaluateToDouble(inputText);					
		}			
		
		if (source == tfMin) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				if (emptyString) {
					num.setIntervalMinInactive();
				} else {
					num.setIntervalMin(value);
				}
				num.updateRepaint();				
			}
		}
		else if (source == tfMax) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				if (emptyString) {
					num.setIntervalMaxInactive();
				} else {
					num.setIntervalMax(value);
				}
				num.updateRepaint();
			}
		}
		else if (source == tfWidth) {
			for (int i = 0; i < geos.length; i++) {
				GeoNumeric num = (GeoNumeric) geos[i];
				num.setSliderWidth(value);
				num.updateRepaint();
			}
		} 
		
		if (propPanel != null)		
			propPanel.updateSelection(geos);
		else
			update(geos);
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField) e.getSource());
	}
}	

/**
 * panel for animation step
 * @author Markus Hohenwarter
 */
class AnimationStepPanel
	extends JPanel
	implements ActionListener, FocusListener {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	private JTextField tfAnimStep;
	
	private Application app;
	private Kernel kernel;

	public AnimationStepPanel(Application app) {
		this.app = app;
		kernel = app.getKernel();
		
		// textfield for animation step
		JLabel label = new JLabel(app.getPlain("AnimationStep") + ": ");
		tfAnimStep = new JTextField(5);
		label.setLabelFor(tfAnimStep);
		tfAnimStep.addActionListener(this);
		tfAnimStep.addFocusListener(this);

		// put it all together
		JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		animPanel.add(label);
		animPanel.add(tfAnimStep);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(animPanel);
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		tfAnimStep.removeActionListener(this);

		// check if properties have same values
		GeoElement temp, geo0 = (GeoElement) geos[0];
		boolean equalStep = true;
		boolean onlyAngles = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoElement) geos[i];
			// same object visible value
			if (geo0.getAnimationStep() != temp.getAnimationStep())
				equalStep = false;
			if (!(temp instanceof GeoAngle))
				onlyAngles = false;
		}

		// set trace visible checkbox
		int oldDigits = kernel.getMaximumFractionDigits();
		kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		if (equalStep)
			if (onlyAngles)
				tfAnimStep.setText(
					kernel.formatAngle(geo0.getAnimationStep()).toString());
			else
				tfAnimStep.setText(kernel.format(geo0.getAnimationStep()));
		else
			tfAnimStep.setText("");
		kernel.setMaximumFractionDigits(oldDigits);

		tfAnimStep.addActionListener(this);
		return this;
	}

	private boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!geo.isChangeable() || geo instanceof GeoText || geo instanceof GeoImage) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfAnimStep)
			doActionPerformed();
	}

	private void doActionPerformed() {
		double newVal =
			app.getAlgebraController().evaluateToDouble(
				tfAnimStep.getText());
		if (!Double.isNaN(newVal)) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.setAnimationStep(newVal);
				geo.updateRepaint();
			}
		}
		update(geos);
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}
}

