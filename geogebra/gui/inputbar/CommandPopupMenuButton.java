package geogebra.gui.inputbar;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.main.Application;
import geogebra.util.LowerCaseDictionary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class CommandPopupMenuButton extends PopupMenuButton implements TreeSelectionListener, ActionListener, FocusListener {

	private Application app;
	private JTree tree;
	private String selectedCommand, rollOverCommand;
	private int rollOverRow;
	private JTextArea helpTextArea;
	private JToggleButton btnShowAll, btnShowTree;
	private JButton btnOnlineHelp;
	private JPanel helpPanel;
	
	
	
	private DefaultMutableTreeNode rootSubCommands, rootAllCommands;
	boolean showAllCommands = false;
	private DefaultTreeModel treeModel;

	
	public String getSelectedCommand() {
		return selectedCommand;
	}

	CommandPopupMenuButton thisButton;

	public CommandPopupMenuButton(Application app) {
		super();
		this.app = app;
		thisButton = this;
		addPopupComponent(createCmdPanel(app));

		setText("\u0192" + "[ ] ");
		setStandardButton(true);
		setIcon(GeoGebraIcon.createEmptyIcon(1, 1));
		setKeepVisible(false);
		setMinimumSize(new Dimension (16, 16));
	}

	
	private JPanel createCmdPanel(Application app){

		// create components
		initTree();
		btnShowAll = new JToggleButton(GeoGebraIcon.createListIcon());
		btnShowAll.setPreferredSize(new Dimension(20, 20));
		
		btnShowAll.addActionListener(this);
		btnShowTree = new JToggleButton(GeoGebraIcon.createTreeIcon());
		btnShowTree.setPreferredSize(new Dimension(20, 20));
		btnShowTree.addActionListener(this);
		btnShowTree.setSelected(true);
		
		helpTextArea = new JTextArea();
		helpTextArea.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));
		helpTextArea.setLineWrap(true);
		helpTextArea.setWrapStyleWord(true);

		JScrollPane helpScrollPane = new JScrollPane(helpTextArea);
	//	helpScrollPane.setVerticalScrollBarPolicy(
			//	JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//helpScrollPane.setPreferredSize(new Dimension(300, 300));

		
		
		
		btnOnlineHelp = new JButton(app.getPlain("ShowOnlineHelp"));
		btnOnlineHelp.setEnabled(rollOverCommand != null);
		btnOnlineHelp.addActionListener(this);
		
		JPanel tb2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tb2.add(btnOnlineHelp);
		
		JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tb.add(btnShowTree);
		tb.add(btnShowAll);
		
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(tb2, BorderLayout.EAST);
		buttonPanel.add(tb, BorderLayout.WEST);
		//buttonPanel.setPreferredSize(new Dimension(16,20));
		
		
		
		JPanel helpPanel = new JPanel(new BorderLayout());
		helpPanel.add(new JScrollPane(helpScrollPane), BorderLayout.CENTER);
			
		
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.add(new JScrollPane(tree),BorderLayout.CENTER);

		// put all the sub panels together
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(treePanel, BorderLayout.WEST);
		panel.add(helpPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		//panel.add(buttonPanel, BorderLayout.SOUTH);
	//	helpTextArea.setPreferredSize(new Dimension(300,300));
		
		panel.setPreferredSize(new Dimension(500, 500));
		
		
		return panel;

	}

	
	private void initTree(){
		
		setCommands();

		treeModel = new DefaultTreeModel(rootSubCommands);
		tree = new JTree(treeModel);
		
		// add listener for selection changes.
		tree.addTreeSelectionListener(this);

		// add listener for mouse roll over
		RollOverListener rollOverListener = new RollOverListener();
		tree.addMouseMotionListener(rollOverListener);
		tree.addMouseListener(rollOverListener);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new MyRenderer());
		//tree.setLargeModel(true);
		//tree.putClientProperty("JTree.lineStyle", "none");
		//tree.setInvokesStopCellEditing(true);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(false);
		//tree.setScrollsOnExpand(true);	
		tree.setToggleClickCount(1);
		
		
	}
	
	
	
	
	public void setCommands(){

		if(rootSubCommands == null)
			rootSubCommands = new DefaultMutableTreeNode(app.getCommand("Command") );
		if(rootAllCommands == null)
			rootAllCommands = new DefaultMutableTreeNode(app.getCommand("Command") );
		rootSubCommands.removeAllChildren();
		rootAllCommands.removeAllChildren();

		DefaultMutableTreeNode child;

		LowerCaseDictionary[] subDict = app.getSubCommandDictionary();

		for(int i=0; i<subDict.length; i++){
			String name = app.getKernel().getAlgebraProcessor().getSubCommandSetName(i);
			child = new DefaultMutableTreeNode(name);
			rootSubCommands.add(child);
			Iterator<?> it = subDict[i].getLowerCaseIterator();
			while (it.hasNext()) {
				String cmdName = (String) subDict[i].get(it.next());
				if (cmdName != null && cmdName.length() > 0){
					child.add(new DefaultMutableTreeNode(cmdName));
				}
			}	 
		}

		LowerCaseDictionary dict = app.getCommandDictionary(); 
		Iterator<?> it = dict.getLowerCaseIterator();
		while (it.hasNext()) {
			String cmdName = (String) dict.get(it.next());
			if (cmdName != null && cmdName.length() > 0){
				rootAllCommands.add(new DefaultMutableTreeNode(cmdName));
			}
		}	
		
	}

	
	
	

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

		if (node == null) return; // Nothing is selected.	

		if(node.isLeaf()){
			Object nodeInfo = node.getUserObject();

			selectedCommand = (String)nodeInfo;
				
		//	this.fireActionPerformed(new ActionEvent(this,
		//			ActionEvent.ACTION_PERFORMED,getActionCommand()));
			
			thisButton.handlePopupActionEvent();
		}

	}

	private class RollOverListener extends MouseInputAdapter {

		public void mouseExited(MouseEvent e) {
			rollOverRow = -1;
			tree.repaint();
		}

		public void mouseMoved(MouseEvent e) {
			int row = tree.getRowForLocation(e.getX(), e.getY()); 
			if( row != rollOverRow) {
				rollOverRow = row;
				if(row != -1){
					TreePath tp =  tree.getPathForRow(row);				
					DefaultMutableTreeNode node = ((DefaultMutableTreeNode)tp.getLastPathComponent());

					if(node.isLeaf()){
						Object nodeInfo = node.getUserObject();
						String cmd = (String)nodeInfo;
						rollOverCommand = cmd;
						StringBuilder sb = new StringBuilder();
						cmd = app.translateCommand(cmd); // internal name
						CommandProcessor.getCommandSyntax(sb, app, cmd, -1);
						helpTextArea.setText(sb.toString());
					}else{
						rollOverCommand = null;
						helpTextArea.setText("");
					}
					helpTextArea.repaint();
				}
				btnOnlineHelp.setEnabled(rollOverCommand != null);
				tree.repaint();
			}
		}
	}
	
	
	
	// =============================================
	//         Tree Cell Renderer
	// =============================================

	private class MyRenderer extends DefaultTreeCellRenderer {

		private Color selectionColor, rollOverColor;
		
		public MyRenderer() {
			setOpenIcon(app.getImageIcon("tree-open.png"));
			setClosedIcon(app.getImageIcon("tree-close.png"));
			setLeafIcon(GeoGebraIcon.createEmptyIcon(5, 1));
			
			selectionColor = this.getBackgroundSelectionColor(); // MyTable.SELECTED_BACKGROUND_COLOR ;
			rollOverColor =  Color.LIGHT_GRAY;
			
			this.setTextSelectionColor(Color.black);
			this.setTextNonSelectionColor(Color.black);
			this.setBorderSelectionColor(null);

		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, isSelected, expanded,
					leaf, row, hasFocus);

			if (value == null) {		
				setText("");
				return this;
			}

			setText(value.toString());			
			if (leaf) {
				
				  
				setFont(app.getPlainFont());
				
				if (isSelected) {
					this.setBackgroundSelectionColor(selectionColor);
				} 
				else if(row == rollOverRow) {
					this.setBackgroundNonSelectionColor(rollOverColor);
					
				}
				else{
					this.setBackgroundSelectionColor(Color.white);
					this.setBackgroundNonSelectionColor(Color.white);
				}
				
				
			}else{
				setFont(app.getBoldFont());
				this.setBackgroundSelectionColor(Color.white);
				this.setBackgroundNonSelectionColor(Color.white);
				if(row == rollOverRow) 
					this.setBackgroundNonSelectionColor(rollOverColor);
			}
			
			return this;
		}
	}



	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == btnShowAll) {	
			treeModel.setRoot(rootAllCommands);
			treeModel.reload();
			btnShowAll.setSelected(true);
			btnShowTree.setSelected(false);
			tree.setRootVisible(true);
		}

		if(e.getSource() == btnShowTree) {
			treeModel.setRoot(this.rootSubCommands);
			treeModel.reload();
			btnShowAll.setSelected(false);
			btnShowTree.setSelected(true);
			tree.setRootVisible(false);
		}

		else if(e.getSource() == btnOnlineHelp){
			app.getGuiManager().openHelp(rollOverCommand);
		}

	}


	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}











}
