package geogebra.gui.inputbar;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.main.Application;
import geogebra.util.LowerCaseDictionary;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class InputBarHelpPanel extends JPanel implements TreeSelectionListener, ActionListener, FocusListener {

	private Application app;
	private JTree tree;
	private String selectedCommand, rollOverCommand;
	private int rollOverRow = -1;
	private JTextArea helpTextArea;
	private JToggleButton btnShowTree;
	private JButton btnCollapseTree;
	private JButton btnOnlineHelp;
	private JPanel helpPanel;
	
	
	
	private DefaultMutableTreeNode rootSubCommands, rootAllCommands;
	boolean showAllCommands = false;
	private DefaultTreeModel treeModel;

	
	public String getSelectedCommand() {
		return selectedCommand;
	}

	InputBarHelpPanel thisButton;
	private SelectionTable charTable;
	private JScrollPane charScroller;
	private SelectionTable greekTable;
	private JScrollPane greekScroller;
	private JScrollPane commandScroller;
	private JList charList;
	private JToggleButton btnShowTables;
	private JToggleButton btnShowGreekList;
	private JToggleButton btnShowCommandTree;
	private JPanel cards;
	

	private Color bgColor = Color.WHITE;
	private SelectionTable functionTable;
	private SelectionTable symbolTable;
	private JLabel greekLabel;
	private JLabel symbolLabel;
	private JLabel functionLabel;
	private JButton btnInsert;
	private JLabel commandLabel;
	private JLabel tableLabel;
	private JTabbedPane tabbedPane;
	
	private JPopupMenu popup;
	private JScrollPane helpScrollPane;
	
	
	
	
	public InputBarHelpPanel(Application app) {
		
		this.app = app;		
		
		//===========================================
		// help panel
		helpTextArea = new JTextArea();
		helpTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//helpTextArea.setLineWrap(true);
		//helpTextArea.setWrapStyleWord(true);
		helpTextArea.setText("");
		helpTextArea.setMinimumSize(new Dimension(200,300));
		helpScrollPane = new JScrollPane(helpTextArea);

		btnOnlineHelp = new JButton(app.getPlain("ShowOnlineHelp"));
		btnOnlineHelp.setEnabled(rollOverCommand != null);
		btnOnlineHelp.addActionListener(this);
		
		JPanel helpPanel = new JPanel(new BorderLayout());
		helpPanel.add(helpScrollPane, BorderLayout.CENTER);
		//helpPanel.add(btnOnlineHelp, BorderLayout.SOUTH);
		
		//===========================================
		
		
		
		/*
		
		btnShowTables = new JToggleButton("Table");
		btnShowCommandTree = new JToggleButton("Command");
		btnInsert = new JButton(app.getImageIcon("list-add-gray.png"));
		
		btnShowTables.addActionListener(this);
		btnShowCommandTree.addActionListener(this);
		btnInsert.addActionListener(this);
		
		

		JPanel centerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerButtonPanel.add(btnShowTables);
		centerButtonPanel.add(btnShowCommandTree);
		
		JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		commandLabel = new JLabel();
		rightButtonPanel.add(btnCollapseTree);
		//rightButtonPanel.add(Box.createRigidArea(new Dimension(5,1)));
		//rightButtonPanel.add(commandLabel);
			
		
		//centerButtonPanel.setBackground(bgColor);
			
		rightButtonPanel.setBackground(bgColor);
			
		JPanel buttonPanel = new JPanel(new BorderLayout());
		//buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
		//buttonPanel.add(centerButtonPanel, BorderLayout.CENTER);
		//buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
		buttonPanel.setBackground(bgColor);
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		
		*/
		
		//==========================================
		// create panel for command tree
		
		createCommandTree();
		
		btnCollapseTree = new JButton(GeoGebraIcon.createTreeIcon());
		btnCollapseTree.setSelectedIcon(GeoGebraIcon.createTreeIcon());
		btnCollapseTree.addActionListener(this);
		
		JPanel treeButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		treeButtonPanel.add(btnCollapseTree);
		treeButtonPanel.setBackground(bgColor);
		
		commandScroller = new JScrollPane(tree);
		commandScroller.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));	
		commandScroller.setColumnHeaderView(treeButtonPanel);
		
		JPanel commandPanel = new JPanel(new BorderLayout());
		commandPanel.add(commandScroller, BorderLayout.CENTER);
		//commandPanel.add(treeButtonPanel, BorderLayout.NORTH);
		commandPanel.add(helpPanel, BorderLayout.EAST);
		
		JSplitPane cmdSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, commandPanel,helpPanel);
		
		
		//==========================================
		// create panel for symbol tables
		
		createTables();
		
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new GridBagLayout());
		GridBagConstraints cTable = new GridBagConstraints();
		cTable.insets = new Insets(4,0,10,0);
		cTable.gridx = 1;
		cTable.anchor = GridBagConstraints.FIRST_LINE_START;
		GridBagConstraints cLabel = new GridBagConstraints();
		cLabel.gridx = 1; 
		cLabel.anchor = GridBagConstraints.FIRST_LINE_START;
		
		tablePanel.add(greekLabel, cLabel);
		tablePanel.add(greekTable, cTable);
		tablePanel.add(symbolLabel, cLabel);
		tablePanel.add(symbolTable, cTable);
		tablePanel.add(functionLabel, cLabel);
		tablePanel.add(functionTable, cTable);
		tablePanel.setBackground(bgColor);
		
		
		JScrollPane tableScroller = new JScrollPane(tablePanel);
		tableScroller.setBorder(commandScroller.getBorder());
		
		JPanel symbolPanel = new JPanel(new BorderLayout());
		symbolPanel.add(tableScroller, BorderLayout.CENTER);


		//===============================================
		// create tabbed pane for the command and table panels

		
		tabbedPane = new JTabbedPane();  
		tabbedPane.addTab(app.getPlain("Tables"), symbolPanel);
		tabbedPane.addTab(app.getPlain("Commands"), cmdSplitPane);
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

		tabbedPane.setBackground(bgColor);   
		//tabbedPane.setMinimumSize(new Dimension(tree.getPreferredSize().width, 20));
		
		
		
		//cards = new JPanel(new CardLayout());
		//cards.add(commandPanel, "commands");
		//cards.add(basicPanel, "tables");
		
		
		
		//===============================================
		// put all the sub panels together
		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);
		//this.add(buttonPanel, BorderLayout.SOUTH);
		
		this.setBorder(BorderFactory.createLoweredBevelBorder());
		tabbedPane.setMinimumSize(new Dimension(300, 20));	

		setLabels();
		
	}


	private void createTables(){
/*
		String[] greek = new String[InputPanel.greekLowerCase.length +  InputPanel.greekUpperCase.length];

		for(int i = 0; i < InputPanel.greekLowerCase.length; i++){
			greek[i] = InputPanel.greekLowerCase[i];
		}
		for(int i = InputPanel.greekLowerCase.length; i < InputPanel.greekUpperCase.length; i++){
			greek[i] = InputPanel.greekUpperCase[i];
		}
	*/	
		String[] greek = InputPanel.greek;

		greekTable = new SelectionTable(app, greek, -1,6, new Dimension(20,16), SelectionTable.MODE_TEXT);
		greekTable.setShowGrid(true);
		greekTable.setHorizontalAlignment(SwingConstants.CENTER);
		greekTable.setBorder(BorderFactory.createEtchedBorder());
		greekTable.addMouseListener(new TableSelectionListener());
		greekLabel = new JLabel();
		
		
		symbolTable = new SelectionTable(app, InputPanel.symbols, -1,6, new Dimension(20,16), SelectionTable.MODE_TEXT);
		symbolTable.setShowGrid(true);
		symbolTable.setHorizontalAlignment(SwingConstants.CENTER);
		symbolTable.setBorder(BorderFactory.createEtchedBorder());
		symbolTable.addMouseListener(new TableSelectionListener());
		symbolLabel = new JLabel();
		
		functionTable = new SelectionTable(app, InputPanel.functions, -1,2, new Dimension(20,16), SelectionTable.MODE_TEXT);
		functionTable.setShowGrid(true);
		functionTable.setHorizontalAlignment(SwingConstants.CENTER);
		functionTable.setBorder(BorderFactory.createEtchedBorder());
		functionTable.addMouseListener(new TableSelectionListener());
		functionLabel = new JLabel();
		
	}
	
	
	/** mouse listener to handle table selection */
	public class TableSelectionListener extends MouseAdapter  {

		public void mouseClicked(MouseEvent evt) {

			if(evt.getSource() == greekTable){
				insertInputBarString((String) greekTable.getSelectedValue());
				symbolTable.setSelectedIndex(-1);
				functionTable.setSelectedIndex(-1);
			}

			else if(evt.getSource() == symbolTable){
				insertInputBarString((String) symbolTable.getSelectedValue());
				greekTable.setSelectedIndex(-1);
				functionTable.setSelectedIndex(-1);
			}

			else if(evt.getSource() == functionTable){
				insertInputBarString((String) functionTable.getSelectedValue());
				symbolTable.setSelectedIndex(-1);
				greekTable.setSelectedIndex(-1);
			}
		}

	}

	
	public void setLabels(){
		
		greekLabel.setText(app.getPlain("Greek Letters"));			
		symbolLabel.setText(app.getPlain("Symbols"));	
		functionLabel.setText(app.getPlain("Functions"));
	
		
	}
	
	
public void updateFonts(){
			
		greekLabel.setFont(app.getBoldFont());
		greekTable.updateFonts();
	
		symbolLabel.setFont(app.getBoldFont());
		symbolTable.updateFonts();
		
		functionLabel.setFont(app.getBoldFont());
		functionTable.updateFonts();
		tabbedPane.setFont(app.getPlainFont());
		
		
	}
	
	
	
	private void createCommandTree(){
		
		setCommands();

		treeModel = new DefaultTreeModel(rootSubCommands);
		tree = new JTree(treeModel);
		
		ToolTipManager.sharedInstance().registerComponent(tree);
		
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
		tree.setRootVisible(false);
		tree.setShowsRootHandles(false);
		//tree.setScrollsOnExpand(true);	
		tree.setToggleClickCount(1);
		tree.setBackground(bgColor);
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
	}
	
	
	
	
	public void setCommands(){

		if(rootSubCommands == null)
			rootSubCommands = new DefaultMutableTreeNode();
		if(rootAllCommands == null)
			rootAllCommands = new DefaultMutableTreeNode(app.getCommand("All Commands") );
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
		
		rootSubCommands.add(rootAllCommands);
		
		
		
	}

	
	
	

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

		if (node == null) return; // Nothing is selected.	

		if(node.isLeaf()){
			Object nodeInfo = node.getUserObject();
			selectedCommand = (String)nodeInfo;
			((AlgebraInput)app.getGuiManager().getAlgebraInput()).insertCommand(selectedCommand);
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
				
						//((DefaultTreeCellRenderer)tree.getCellRenderer()).setToolTipText(sb.toString());
						
					}else{
						rollOverCommand = null;
						helpTextArea.setText("");			
					}
			
					//helpScrollPane.scrollRectToVisible(new Rectangle(0,0,1,1));
					helpTextArea.setCaretPosition(0);
					helpTextArea.repaint();		
					
				}
				/*
				popup.setLocation(-popup.getWidth(), e.getY());
				if(!helpTextArea.equals("") && !popup.isShowing())
					popup.show(tree, -popup.getWidth(), e.getY());
				//else
					//popup.setVisible(false);
				*/
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
			
			selectionColor = MyTable.SELECTED_BACKGROUND_COLOR ;   //this.getBackgroundSelectionColor(); 
			rollOverColor =  Color.LIGHT_GRAY;
			
			this.setTextSelectionColor(Color.black);
			this.setTextNonSelectionColor(Color.black);
			this.setBorderSelectionColor(null);
			this.setBackground(bgColor);

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
					this.setBackgroundSelectionColor(bgColor);
					this.setBackgroundNonSelectionColor(bgColor);
				}
				
				
			}else{
				setFont(app.getBoldFont());
				this.setBackgroundSelectionColor(bgColor);
				this.setBackgroundNonSelectionColor(bgColor);
				if(row == rollOverRow) 
					this.setBackgroundNonSelectionColor(rollOverColor);
			}
			
			return this;
		}
	}



	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == btnCollapseTree) {
				treeModel.setRoot(rootSubCommands);
				treeModel.reload();
				tree.setRootVisible(false);
			}

		if(e.getSource() == btnShowTree) {
			treeModel.setRoot(this.rootSubCommands);
			treeModel.reload();
			btnCollapseTree.setSelected(false);
			btnShowTree.setSelected(true);
			tree.setRootVisible(false);
		}

		else if(e.getSource() == btnOnlineHelp){
			app.getGuiManager().openHelp(rollOverCommand);
		}

		else if(e.getSource() == btnShowTables){
			CardLayout cl = (CardLayout)(cards.getLayout());
		    cl.show(cards, "tablePanel");
		}
		
		else if(e.getSource() == this.btnShowCommandTree){
			CardLayout cl = (CardLayout)(cards.getLayout());
		    cl.show(cards, "commandPanel");
		}
		
		
	}

	public void setDisplay(String displayType){
		CardLayout cl = (CardLayout)(cards.getLayout());
	    cl.show(cards, displayType);
	}
	
	

	private void insertInputBarString(String cmd){
		//((AlgebraInput)app.getGuiManager().getAlgebraInput()).getInputPanel().insertString(cmd);
		
		((AlgebraInput)app.getGuiManager().getAlgebraInput()).insertString(cmd);
	}
	
	
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}



}
