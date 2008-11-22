package geogebra.gui.layout;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import geogebra.main.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.InputDialog;
import geogebra.gui.InputHandler;
import geogebra.io.layout.DockPanelXml;
import geogebra.io.layout.DockSplitPaneXml;
import geogebra.io.layout.Perspective;

/**
 * Manage layout related stuff.
 * 
 * TODO Save settings permanent (title bar visible, load document perspective)
 * 
 * @author Florian Sonner
 * @version 2008-07-18
 */
public class Layout {
	private static Layout instance;
	
	private boolean isInitialized = false;
	
	private Application app;
	private DockManager dockManager;
	private ArrayList perspectives;
	
	/**
	 * If the title bar should be displayed.
	 */
	private boolean titleBarVisible = true;
	
	/**
	 * An array with the default perspectives.
	 */
	public static Perspective[] defaultPerspectives;
	
	/**
	 * Hidden constructor of the layout class. Please use {@link getInstance()}
	 * to get an instance of the layout.
	 * {@link initialize()} has to be called once in order to use this class.
	 */
	private Layout() {
		initializeDefaults();
		
		this.perspectives = new ArrayList(1);
	}
	
	/**
	 * Singleton implementation for the layout class.
	 * You have to call initialize() in order to be able to use the
	 * layout class.
	 * 
	 * @return
	 */
	public static Layout getInstance() {
		if(Layout.instance == null) {
			Layout.instance = new Layout();
		}
		
		return Layout.instance;
	}
	
	/**
	 * Initialize the layout component.
	 * 
	 * @param app
	 */
	public void initialize(Application app) {
		if(isInitialized)
			return;
		
		isInitialized = true;
		
		this.app = app;
		
		// don't display the titlebar in an applet
		titleBarVisible = !app.isApplet();
		
		dockManager = new DockManager(this);
	}
	
	/**
	 * Initialize the default perspectives
	 */
	private void initializeDefaults() {
		// TODO modify toolbar
		defaultPerspectives = new Perspective[4];
		
		// algebra & graphics - default of GeoGebra < 3.2
		DockPanelXml[] dpInfo = new DockPanelXml[3];
		dpInfo[0] = new DockPanelXml(Application.VIEW_EUCLIDIAN, true, false, new Rectangle(100, 100, 600, 400), "1", 500);
		dpInfo[1] = new DockPanelXml(Application.VIEW_ALGEBRA, true, false, new Rectangle(100, 100, 250, 400), "3", 200);
		dpInfo[2] = new DockPanelXml(Application.VIEW_SPREADSHEET, false, false, new Rectangle(100, 100, 600, 400), "1,1", 300);
		
		DockSplitPaneXml[] spInfo = new DockSplitPaneXml[1];
		spInfo[0] = new DockSplitPaneXml("", 0.25, DockSplitPane.HORIZONTAL_SPLIT);
		
		defaultPerspectives[0] = new Perspective("AlgebraAndGraphics", spInfo, dpInfo, "0 | 40 | 1", false, true, true, true, false);
		
		// basic geometry - just the euclidian view
		dpInfo = new DockPanelXml[3];
		dpInfo[0] = new DockPanelXml(Application.VIEW_EUCLIDIAN, true, false, new Rectangle(100, 100, 600, 400), "1", 500);
		dpInfo[1] = new DockPanelXml(Application.VIEW_ALGEBRA, false, false, new Rectangle(100, 100, 250, 400), "3", 200);
		dpInfo[2] = new DockPanelXml(Application.VIEW_SPREADSHEET, false, false, new Rectangle(100, 100, 600, 400), "1,1", 300);
		
		defaultPerspectives[1] = new Perspective("BasicGeometry", spInfo, dpInfo, "2", false, false, false, true, false);
		
		// geometry - like basic geometry but with more toolbar entries
		defaultPerspectives[2] = new Perspective("Geometry", spInfo, dpInfo, "0 | 40 | 1", true, true, true, false, true);
		
		// table & graphics - just the euclidian view
		spInfo = new DockSplitPaneXml[1];
		spInfo[0] = new DockSplitPaneXml("", 0.45, DockSplitPane.HORIZONTAL_SPLIT);
		
		dpInfo = new DockPanelXml[3];
		dpInfo[0] = new DockPanelXml(Application.VIEW_EUCLIDIAN, true, false, new Rectangle(100, 100, 600, 400), "1", 500);
		dpInfo[1] = new DockPanelXml(Application.VIEW_ALGEBRA, false, false, new Rectangle(100, 100, 250, 400), "3,3", 200);
		dpInfo[2] = new DockPanelXml(Application.VIEW_SPREADSHEET, true, false, new Rectangle(100, 100, 600, 400), "3", 300);
		
		defaultPerspectives[3] = new Perspective("TableAndGraphics", spInfo, dpInfo, "0 | 40 | 1", false, false, false, true, false);
	}
	
	/**
	 * Set a list of perspectives as the perspectives of this user and
	 * apply the "tmp" perspective if one was found.
	 * 
	 * @param perspectives
	 */
	public void setPerspectives(ArrayList perspectives) {
		this.perspectives = perspectives;
		
		boolean foundTmp = false;
		
		for(Iterator iter = perspectives.iterator(); iter.hasNext();) {
			Perspective next = (Perspective)iter.next();
				
			if(next.getId().equals("tmp")) {
				applyPerspective(next);
				perspectives.remove(next);
				foundTmp = true;
				break;
			}
		}
		
		if(!foundTmp) {
			applyPerspective(defaultPerspectives[0]);
		}
	}
	
	/**
	 * Use a new perspective.
	 * 
	 * TODO consider applet parameters
	 * 
	 * @param perspective
	 */
	public void applyPerspective(Perspective perspective) {		
		EuclidianView ev = app.getEuclidianView();
		ev.showAxes(perspective.getShowAxes(), perspective.getShowAxes());
		ev.showGrid(perspective.getShowGrid());
		ev.repaint();
		
		app.getGuiManager().setToolBarDefinition(perspective.getToolbarDefinition());

		app.setShowAlgebraInput(perspective.getShowInputPanel());
		app.setShowInputTop(perspective.getShowInputPanelOnTop());
		
		dockManager.applyPerspective(perspective.getSplitPaneInfo(), perspective.getDockPanelInfo());
		// TODO: Apply other settings
		
		if(!app.isIniting()) {
			app.updateToolBar();
			app.updateMenubar();
			app.updateCenterPanel(true);
		}
	}
	
	/**
	 * Create a perspective for the current layout.
	 * 
	 * @param id
	 * @return
	 */
	public Perspective createPerspective(String id) {
		if(app == null || dockManager.getRoot() == null)
			return null;
		
		// return the default perspective in case we're creating new preferences of
		// a virgin application.		
		EuclidianView ev = app.getEuclidianView();
		Perspective perspective = new Perspective(id);

		// get the information about the split panes
		DockSplitPane.TreeReader spTreeReader = new DockSplitPane.TreeReader(app);
		perspective.setSplitPaneInfo(spTreeReader.getInfo(dockManager.getRoot()));

		// get the information about the dock panels
		DockPanel[] panels = dockManager.getPanels();
		DockPanelXml[] dockPanelInfo = new DockPanelXml[panels.length];

		for (int i = 0; i < panels.length; ++i) {
			// just the width of the panels isn't updated every time the panel
			// is updated, so we have to take care of this by ourself
			if (!panels[i].getInfo().getOpenInFrame() && panels[i].getInfo().isVisible()) {
				DockSplitPane parent = panels[i].getParentSplitPane();
				if (parent.getOrientation() == DockSplitPane.HORIZONTAL_SPLIT) {
					panels[i].getInfo().setEmbeddedSize(panels[i].getWidth());
				} else {
					panels[i].getInfo().setEmbeddedSize(panels[i].getHeight());
				}
			}

			panels[i].getInfo().setEmbeddedDef(panels[i].getEmbeddedDef());

			dockPanelInfo[i] = (DockPanelXml)panels[i].getInfo().clone();
		}

		// Sort the dock panels as the entries with the smallest amount of
		// definition should
		// be read first by the loading algorithm.
		Arrays.sort(dockPanelInfo, new Comparator() {
			public int compare(Object o1, Object o2) {
				int diff = ((DockPanelXml) o2).getEmbeddedDef().length()
						- ((DockPanelXml) o1).getEmbeddedDef().length();
				return diff;
			}
		});
		
		perspective.setDockPanelInfo(dockPanelInfo);

		perspective.setToolbarDefinition(app.getGuiManager().getToolBarDefinition());
		perspective.setShowAxes(ev.getShowXaxis() && ev.getShowYaxis());
		perspective.setShowGrid(ev.getShowGrid());
		perspective.setShowInputPanel(app.showAlgebraInput());
		perspective.setShowInputPanelCommands(app.showCmdList());
		perspective.setShowInputPanelOnTop(app.showInputTop());

		return perspective;
	}
	
	/**
	 * Get all current perspectives as array.
	 * 
	 * @return
	 */
	public Perspective[] getPerspectives() {
		Perspective[] array = new Perspective[perspectives.size()];
		return (Perspective[])perspectives.toArray(array);
	}

	/**
	 * @param index
	 * @return
	 */
	public Perspective getPerspective(int index) {
		if(index >= perspectives.size())
			throw new IndexOutOfBoundsException();
		
		return (Perspective)perspectives.get(index);
	}
	
	/**
	 * Add a new perspective to the list of available perspectives.
	 * 
	 * @param perspective
	 */
	public void addPerspective(Perspective perspective) {
		perspectives.add(perspective);
	}
	
	/**
	 * Remove a perspective identified by the object.
	 * 
	 * @param perspective
	 */
	public void removePerspective(Perspective perspective) {
		if(perspectives.contains(perspective)) {
			perspectives.remove(perspective);
		}
	}
	
	/**
	 * Remove a perspective identified by the index.
	 * 
	 * @param index
	 */
	public void removePerspective(int index) {
		if(index >= 0 && index < perspectives.size()) {
			perspectives.remove(index);
		} else {
			Application.debug("Invalid perspective index: " + index);
		}
	}
	
	/**
	 * Return the layout as XML.
	 * 
	 * @param asPreference If the collected data is used for the preferences
	 * @return
	 */
	public String getXml(boolean asPreference) {
		/**
		 * Create a temporary perspective which is used to store the layout
		 * of the document at the moment. This perspective isn't accessible
		 * through the menu and will be removed as soon as the document was 
		 * saved with another perspective. 
		 */ 
		Perspective tmpPerspective = createPerspective("tmp");

		StringBuffer sb = new StringBuffer();
		sb.append("\t<perspectives>\n");
		
		// save the current perspective
		if(tmpPerspective != null)
			sb.append(tmpPerspective.getXml());
		
		// save all custom perspectives as well
		for(Iterator iter = perspectives.iterator(); iter.hasNext();) {
			sb.append(((Perspective)iter.next()).getXml());
		}
		
		sb.append("\t</perspectives>\n");

		/**
		 * Certain user elements should be just saved as preferences and not
		 * if a document is saved normally as they just depend on the
		 * preferences of the user.
		 */
		if(asPreference && app != null) {
			sb.append("\t<settings ignoreDocument=\"");
			sb.append(app.isIgnoringDocumentPerspective());
			sb.append("\" showTitleBar=\"");
			sb.append(isTitleBarVisible());
			sb.append("\" />\n");
		}
		
		return sb.toString();
	}

	/**
	 * @param titleBarVisible
	 */
	public void setTitlebarVisible(boolean titleBarVisible) {
		this.titleBarVisible = titleBarVisible;
		
		dockManager.updatePanels();
	}

	/**
	 * @return The visibility of the title bar.
	 */
	public boolean isTitleBarVisible() {
		return titleBarVisible;
	}
	
	/**
	 * @return The application object.
	 */
	public Application getApp() {
		return app;
	}
	
	/**
	 * @return The management class for the docking behavior.
	 */
	public DockManager getDockManager() {
		return dockManager;
	}
	
	/**
	 * Show the prompt which is used to save the current perspective.
	 */
	public void showSaveDialog() {
		InputDialog inputDialog = new InputDialog(app, app.getPlain("PerspectiveName"), app.getMenu("SaveCurrentPerspective"), "", false, new SaveInputHandler(this));
		inputDialog.showSpecialCharacters(false);
		inputDialog.showGreekLetters(false);
        inputDialog.setVisible(true);    
	}
	
	private class SaveInputHandler implements InputHandler {
		private Layout layout;
		
		public SaveInputHandler(Layout layout) {
			this.layout = layout;
		}
		
		public boolean processInput(String inputString) {
			if(inputString.equals("tmp")) {
				return false;
			}
			
			layout.addPerspective(layout.createPerspective(inputString));
			layout.getApp().updateMenubar();
			
			return true;
		}
	}
	
	/**
	 * Show the dialog which is used to manage the custom defined perspectives.
	 */
	public void showManageDialog() {
		ManagePerspectivesDialog dialog = new ManagePerspectivesDialog(app);
		dialog.setVisible(true);
	}
	
	/**
	 * Dialog which is used to manage (delete) the custom perspectives.
	 * 
	 * @author Florian Sonner
	 * @version 2008-09-14
	 * 
	 * TODO More advanced functions (rename, etc.)
	 */
	private class ManagePerspectivesDialog extends JDialog implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		private Application app;
		private Layout layout;
		private JList list;
		private DefaultListModel listModel;
		private JButton cancelButton, removeButton;
		
		public ManagePerspectivesDialog(Application app) {
			super(app.getFrame());
			
			this.app = app;
			this.layout = Layout.getInstance();
			
			setModal(true);
			setTitle(app.getMenu("ManagePerspectives"));
			buildGUI();
			pack();
			setLocationRelativeTo(app.getMainComponent());
		}
		
		/**
		 * Build the GUI which includes a list with perspectives and some buttons.
		 */
		private void buildGUI() {
			// build list with perspectives
			listModel = new DefaultListModel();
			Perspective[] perspectives = layout.getPerspectives();
			for(int i = 0; i < perspectives.length; ++i) {
				listModel.addElement(perspectives[i].getId());
			}
			
			list = new JList(listModel);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setLayoutOrientation(JList.VERTICAL);
			list.setVisibleRowCount(6);
			
			JScrollPane listSP = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			listSP.setPreferredSize(new Dimension(150, 200));
			
			// build button panel to remove perspectives and to close this dialog
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			removeButton = new JButton(app.getPlain("Remove"));
			removeButton.addActionListener(this);
			updateRemoveButton();
			buttonPanel.add(removeButton);
			
			cancelButton = new JButton(app.getPlain("Cancel"));
			cancelButton.addActionListener(this);
			buttonPanel.add(cancelButton);
			
			Container cp = getContentPane();
			cp.setLayout(new BorderLayout());
			cp.add(listSP, BorderLayout.CENTER);
			cp.add(buttonPanel, BorderLayout.SOUTH);
		}

		/**
		 * One of the buttons was pressed.
		 */
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == cancelButton) {
				setVisible(false);
				dispose();
			} else if(e.getSource() == removeButton) {
				int index = list.getSelectedIndex();
				
				if(index != -1) { // -1 = no entry selected
					listModel.remove(index);
					layout.removePerspective(index);
					app.updateMenubar();
					updateRemoveButton();
				}
			}
		}
		
		/**
		 * Don't enable the remove button if there are no items.
		 */
		private void updateRemoveButton() {
			removeButton.setEnabled(listModel.size() != 0);
		}
	}
}
