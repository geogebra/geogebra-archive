package geogebra.gui.view.spreadsheet;

import geogebra.gui.InputDialog;
import geogebra.io.DocHandler;
import geogebra.io.QDParser;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * 
 * @author G.Sturr 2010-2-6
 *
 */
public class FileBrowserPanel extends JPanel implements ActionListener, TreeSelectionListener, TreeExpansionListener {

	private SpreadsheetView view;
	private Application app;
	
	private FileBrowserPanel browserPanel;
	private JTree tree;
	private DefaultTreeModel treeModel;
	
	private File rootFile;
	private URL rootURL;
	
	private static  QDParser xmlParser;
	private static  myFileTreeHandler handler;
	
	private boolean isXMLTree = false;
	
	public JButton minimizeButton;
	private JButton menuButton;
	private JPopupMenu contextMenu;

	
	
	/**
	 * Construct a browser panel
	 */
	public FileBrowserPanel(SpreadsheetView view) {
	
		this.view = view;
		browserPanel = this;	
		app = view.getApplication();
		
		xmlParser = new QDParser();
		handler = new myFileTreeHandler();
		
		setBackground(view.table.getBackground());
		setLayout(new BorderLayout());	
		
			
		
		// Create tree
	
		tree = new JTree(new DefaultMutableTreeNode());
		treeModel = (DefaultTreeModel) tree.getModel();
		// add listeners
	    tree.addTreeSelectionListener(this); 
	    tree.addTreeExpansionListener(this);
	    
	    // set visual properties
		//tree.setRootVisible(false);
		MyRenderer renderer = new MyRenderer();
		tree.setCellRenderer(renderer);	
		tree.setFont(app.getPlainFont());	
		tree.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5,
						5)));			
		
		//TODO is this needed?
		tree.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				requestFocusInWindow();
			}
		});
			
		// enclose tree in a scroll pane 
		JScrollPane treePane = new JScrollPane(tree);
		
			
		
		
		// Create header

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		menuButton = new JButton(app.getPlain("New Folder") + "...", app.getImageIcon("aux_folder.gif"));
		menuButton.addActionListener(this);
		toolbar.add(menuButton);
		makeContextMenu();

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		minimizeButton = new JButton(app.getImageIcon("view-close.png"));
		minimizeButton.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		minimizeButton.addActionListener(this);
		minimizeButton.setFocusPainted(false);
		minimizeButton.setPreferredSize(new Dimension(16, 16));
		buttonPanel.add(minimizeButton);

		JPanel header = new JPanel(new BorderLayout());
		header.add(toolbar, BorderLayout.WEST);
		header.add(buttonPanel, BorderLayout.EAST);
	//	header.setBorder(BorderFactory.createCompoundBorder(BorderFactory
		//		.createEtchedBorder(), BorderFactory.createEmptyBorder(2, 5, 2,5)));

		
		
		// Add all components to the browser panel

		this.add(header, BorderLayout.NORTH);
		this.add(treePane, BorderLayout.CENTER);
		
		// Load the local file system root as directory default
		// TODO: add a default directory option to preferences
		String curDir = System.getProperty("user.dir"); 
		String homeDir = System.getProperty("user.home"); 
		this.setDirectory(new File(homeDir));
		
	}
 
	
	
	
	private void makeContextMenu(){

		contextMenu = new JPopupMenu();
		JMenuItem menuItem;

		menuItem = new JMenuItem("New File Folder", app.getImageIcon("document-open.png"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(browserPanel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setDirectory(fc.getSelectedFile());
					// ((JButton)e.getSource()).setText(fc.getSelectedFile().getName());
				}
			}
		});
		contextMenu.add(menuItem);

		menuItem = new JMenuItem("New URL Folder", app.getImageIcon("wiki.png"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
				//	URL url = new URL("http://www.santarosa.edu/~gsturr/data/Text.xml");
				//	URL url = new URL("http://www.santarosa.edu/~gsturr/data/BPS5/BPS5.xml");
					
					String initString = "http://";				
					initString  = "http://www.santarosa.edu/~gsturr/data/BPS5/BPS5.xml";
					
					InputDialog id = new InputDialogOpenDataFolderURL(app,view, initString);
					id.setVisible(true);
					
					// setDirectory(url);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		contextMenu.add(menuItem);
		
		contextMenu.addSeparator();

		menuItem = new JMenuItem("Save XML Tree");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					saveXMLTree(rootFile);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		contextMenu.add(menuItem);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuButton) {

			Point locButton = menuButton.getLocationOnScreen();
			Point locApp = app.getMainComponent().getLocationOnScreen();
			contextMenu.show(app.getMainComponent(), locButton.x - locApp.x,
					locButton.y - locApp.y + menuButton.getHeight());

		} else if (e.getSource() == minimizeButton) {
		
			view.minimizeBrowserPanel();
			minimizeButton.getModel().setRollover(false);
		}
	}
	
	
	
	
	//=============================================
	// Set Tree Directory
	//=============================================
		
	public boolean setDirectory(URL rootURL){
		return setDirectory(rootURL, null, true);
	}
	
	public boolean setDirectory(File rootFile){
		return setDirectory(null, rootFile, false);
	}
	
	private boolean setDirectory(URL rootURL, File rootFile, boolean isXMLTree){

		boolean succ = true;
		
		this.isXMLTree = isXMLTree;

		if (isXMLTree) {
			this.rootURL = rootURL;
			InputStream is;
			try {
				
				is = rootURL.openStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				xmlParser.parse(handler,reader);
				
				treeModel.setRoot(handler.getFileTree());
				treeModel.reload();
		
				is.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				succ = false;
			}

			
		} else {

			try {
				this.rootFile = rootFile;
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
						rootFile.getName());
				addFileTree(newNode, rootFile);
				treeModel.setRoot(newNode);
				treeModel.reload();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				succ = false;
			}

		}	
		
		
		return succ;
		
	}
	
	
	//=============================================
	// Event Handlers
	//=============================================
	
	
	/**
	 * If a leaf (data file) is selected, create its path
	 * string and then load the file into the spreadsheet.
	 */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		
		if (node == null)
			return;
		
		Object nodeInfo = node.getUserObject();
		
		if (node.isLeaf()) {
			TreePath p = e.getPath();
			if(isXMLTree){
				try {
					view.loadSpreadsheetFromURL(getURLFromPath(p));
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else{
				view.loadSpreadsheetFromURL(getFileFromPath(p));
			}
		}
	
	};
	
		

	/**
	 * When a directory node is expanded get the directory contents
	 * and add them to the node
	 */
	public void treeExpanded(TreeExpansionEvent e) {

		if(!isXMLTree){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath()
				.getLastPathComponent();

		addFileTree(node, getFileFromPath(e.getPath()));
		treeModel.reload(node);
		}
	}
	  
	public void treeCollapsed(TreeExpansionEvent e) {

	}
	
	
	// =============================================
	// Renderer
	// =============================================

	private class MyRenderer extends DefaultTreeCellRenderer {

		public MyRenderer() { }

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			
			if (value == null) {		
				setText("");
				return this;
			}
			
			String text = value.toString();			
			if (leaf) {
				setIcon(null);
				if (text != null && text.contains(".")) {
					text = text.substring(0, text.lastIndexOf("."));
				}
			}		
			setText(text);		
				
			return this;
		}
	}
	

	
	
	// =============================================
	// File system 
	// =============================================

	/**
	 * Adds the entries in a file directory to a tree node. 
	 * (Does not traverse sub-directories.)
	 */
	private void addFileTree(DefaultMutableTreeNode node, File dir ) {
		
		// Get list of entries in rootDir.
		// Exit if no entries, otherwise sort the list
		File [] dirList = dir.listFiles(new DataFileFilter());
		
		if(dirList == null ) {
			node.add(new DefaultMutableTreeNode());
			return;			
		}
		sortFileList(dirList);

		
		// Add nodes for each directory and text file.
		node.removeAllChildren();	
		for (File file : dirList) {
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					file.getName());
			if (file.isDirectory()) {
				newNode.add(new DefaultMutableTreeNode());
				node.add(newNode);}
			if (file.isFile()) {
				node.add(newNode);
			}
		}
		
		// If our node is still empty, add a child so the tree will display a
		// a directory folder.
		if(node.getChildCount()==0)
			node.add(new DefaultMutableTreeNode());
		
		return;
		
	}

	@SuppressWarnings("unchecked")
	private void sortFileList(File [] fileList){
		Arrays.sort(fileList, new Comparator()
		{
		    public int compare(Object o1, Object o2)
		    {
			return ((File) o1).getName().compareTo(((File) o2).getName());
		    }
		});
	}
	
	/**
	 * Filter that returns directories and text files
	 */
	public class DataFileFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {

			File f = new File(dir.getPath() + dir.separatorChar + name);
			if (f.isDirectory())
				return true;

			String[] extensions = { "txt", "csv","dat" };
			name = name.toLowerCase();
			for (int i = extensions.length - 1; i >= 0; i--) {
				if (name.endsWith(extensions[i])) {
					return true;
				}
			}

			return false;
		}
	}
	
	
	private File getFileFromPath(TreePath p){
		
		String filePath = rootFile.getPath()+ File.separator;
		for(int i=1;i< p.getPathCount()-1;i++){
			filePath += p.getPathComponent(i).toString()+ File.separator;
		}
		filePath += p.getLastPathComponent().toString();
		
		return new File(filePath);
		
	}
	
	private URL getURLFromPath(TreePath p) throws MalformedURLException{
		
		String dirPath = rootURL.getFile();
		
		//Extract the URL directory path from the name of the XML file.
		//We assume that the XML file is located within the data file
		//at the topmost level.
		//TODO -- is there a better way?
		int lastIndex = dirPath.lastIndexOf("/");
		dirPath = dirPath.substring(0, lastIndex);
		lastIndex = dirPath.lastIndexOf("/");
		dirPath = dirPath.substring(0, lastIndex);
				
		for(int i=0;i< p.getPathCount();i++){
			dirPath += "/" + p.getPathComponent(i).toString();
		}	
		//System.out.println("dirpath: " + dirPath);
		
		
		return new URL(rootURL.getProtocol(),rootURL.getHost(),dirPath);
		
	}
	
	
	
	
	//==============================
	// XML 
	//==============================
	
	private void saveXMLTree(File rootDir) {
		
		StringBuilder sb = buildXMLFileTree(rootDir);
		//System.out.println(sb);
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(rootDir.getName()+ ".xml"));
		fc.setCurrentDirectory(rootFile);
		
		
		int returnVal = fc.showSaveDialog(browserPanel);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				OutputStream os = new FileOutputStream(fc.getSelectedFile());
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF8"));
				for (int i = 0; i < sb.length(); i++) {
					writer.write(sb.charAt(i));
				}
				writer.close();
			}

			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private StringBuilder buildXMLFileTree(File rootDir) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<fileTree>\n");
		sb.append("\t<root name = \"" + rootDir.getName() + "\"></root>\n");
		traverseDirectory(rootDir, sb);
		sb.append("</fileTree>");
		
		return sb;
	}

	private void traverseDirectory(File dir, StringBuilder sb) {
		
		File [] fileList = dir.listFiles(new DataFileFilter());
		
		sortFileList(fileList);
		for (File file : fileList) {
			if (file.isDirectory()) {
				sb.append("<directory name = \"" + file.getName() + "\">\n");
				traverseDirectory(file, sb);
				sb.append("</directory>\n");
			}
			if (file.isFile()) {
				sb.append("\t<file name = \"" + file.getName() + "\"></file>\n");
			}
		}
	
	}

	
	/**
	 * Handler used by QDParser to parse XML file trees. 
	 */
	public class myFileTreeHandler implements DocHandler {
		
		private DefaultMutableTreeNode previousNode = new DefaultMutableTreeNode();
		private DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode();
			
		public void startElement(String tag, LinkedHashMap<String, String> h)
				throws Exception {
			
			if (tag.equals("root")) {
				currentNode = new DefaultMutableTreeNode((String) h.get("name"));
			}
			
			if (tag.equals("directory")) {
				previousNode = currentNode;
				currentNode = new DefaultMutableTreeNode((String) h.get("name"));
				previousNode.add(currentNode);
			}

			if (tag.equals("file")) {
				if (((String) h.get("name")).equals("null")) {
					DefaultMutableTreeNode newFileNode = new DefaultMutableTreeNode();
					currentNode.add(newFileNode);
				} else {
					DefaultMutableTreeNode newFileNode = new DefaultMutableTreeNode((String) h.get("name"));
					currentNode.add(newFileNode);
				}
			}
		}

		public void endElement(String elem) {
			if (elem.equals("directory")) {
				currentNode = (DefaultMutableTreeNode) currentNode.getParent();
			}
		}

		public DefaultMutableTreeNode getFileTree() {
			return currentNode;
		}
	
		public void startDocument() {
		}

		public void endDocument() {
		}

		public void text(String text) {
		}

		public int getConsStep() {
			return 0;
		}		
	}

	
}
		
		
	