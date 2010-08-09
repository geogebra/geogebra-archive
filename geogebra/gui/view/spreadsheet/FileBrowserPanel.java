package geogebra.gui.view.spreadsheet;

import geogebra.gui.InputDialog;
import geogebra.io.DocHandler;
import geogebra.io.QDParser;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
import javax.swing.ImageIcon;
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
 * Creates a sidebar panel for the spreadsheet that displays a directory tree of
 * data files. Clicking on a data file clears the spreadsheet and loads in the
 * new data.
 * 
 * Data can be loaded from the local file system or a remote URL. Directory
 * trees from remote sources use a custom XML catalog file created by the file
 * browser.
 * 
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

	private static final String URL_INIT_STRING = "http://www.geogebra.org/static/data/data.xml";
	final static Color bgColor = Color.white;
	final static Color fgColor = Color.black;


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


		//======================================		
		// Create file tree

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
		tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));			

		//TODO is this needed?
		tree.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				requestFocusInWindow();
			}
		});

		// enclose tree in a scroll pane 
		JScrollPane treePane = new JScrollPane(tree);


		// mouse listener to trigger context menu
		tree.addMouseListener ( new MouseAdapter ()
		{
			public void mousePressed (MouseEvent e) {
				if(Application.isRightClick(e) && !isXMLTree){		
					//ContextMenu contextMenu = new ContextMenu();
					FileBrowserMenu contextMenu = new FileBrowserMenu();
					contextMenu.show(e.getComponent(), e.getX(),e.getY());
				}
			}
		}
		);

		//======================================	
		// Create header

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		//menuButton = new JButton(app.getMenu("Load")+ "...");  
		menuButton = new JButton(dropDownIcon(app.getImageIcon("aux_folder.gif"), this.getBackground()));
		//menuButton = new JButton(app.getImageIcon("aux_folder.gif"));
		menuButton.setBorderPainted(false);
		menuButton.setFont(app.getPlainFont());
		menuButton.addActionListener(this);
		toolbar.add(menuButton);


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


		//===========================================	
		// Add all components to the browser panel

		this.add(header, BorderLayout.NORTH);
		this.add(treePane, BorderLayout.CENTER);

		// Load the local file system root as directory default
		// TODO: add a default directory option to preferences/XML
		String curDir = System.getProperty("user.dir"); 
		String homeDir = System.getProperty("user.home"); 
		this.setDirectory(new File(homeDir));
		updateFonts();





	}



	//=============================================
	//      Context Menu
	//=============================================

	private class ContextMenu extends JPopupMenu{

		JMenuItem menuItem;

		public  ContextMenu(){
			this.setOpaque(true);
			setBackground(bgColor);	
			setFont(app.getPlainFont());

			menuItem = new JMenuItem(app.getMenu("Save to XML") + "...", app.getEmptyIcon());
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					try {
						saveXMLTree(rootFile);

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			add(menuItem);
			menuItem.setBackground(bgColor);
		}
	}




	//=============================================
	//      File Browser Menu
	//=============================================



	private class FileBrowserMenu extends JPopupMenu{

		JMenuItem menuItem;

		public  FileBrowserMenu(){
			this.setOpaque(true);
			setBackground(bgColor);	
			setFont(app.getPlainFont());

			menuItem = new JMenuItem(app.getMenu("Open File Folder") + "...", app.getImageIcon("document-open.png"));
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
			add(menuItem);
			menuItem.setBackground(bgColor);

			menuItem = new JMenuItem(app.getMenu("Open Web Page") + "..." , app.getImageIcon("wiki.png"));
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					try {

						//TODO: 1) drop down history of URLs
						//      2) store URL in XML 

						//	URL url = new URL("http://www.santarosa.edu/~gsturr/data/Text.xml");
						//	URL url = new URL("http://www.santarosa.edu/~gsturr/data/BPS5/BPS5.xml");

						String initString = "http://";				
						initString  = URL_INIT_STRING;

						InputDialog id = new InputDialogOpenDataFolderURL(app,view, initString);
						id.setVisible(true);

						// setDirectory(url);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			add(menuItem);
			menuItem.setBackground(bgColor);
			
			addSeparator();

			menuItem = new JMenuItem(app.getMenu("Save to XML") + "...", app.getEmptyIcon());
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					try {
						saveXMLTree(rootFile);

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			add(menuItem);
			menuItem.setBackground(bgColor);
			menuItem.setEnabled(!isXMLTree);
					

		}
	}



	//=============================================
	//   Set Directory Tree
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
	 * Handle button clicks. 
	 */
	public void actionPerformed(ActionEvent e) {

		// open the drop down menu
		if (e.getSource() == menuButton) {
			FileBrowserMenu menu = new FileBrowserMenu();
			Point locButton = menuButton.getLocationOnScreen();
			Point locApp = app.getMainComponent().getLocationOnScreen();
			menu.show(app.getMainComponent(), locButton.x - locApp.x,
					locButton.y - locApp.y + menuButton.getHeight());

			// minimize the browser	
		} else if (e.getSource() == minimizeButton) {
			view.minimizeBrowserPanel();
			minimizeButton.getModel().setRollover(false);
		}
	}



	/**
	 * Listener for data file (tree leaf) selection. Creates a path
	 * string for the file and then loads the file into the spreadsheet.
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
	 * Listener for expanded node. Adds sub-directory contents to the tree when
	 * a node is expanded
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

	/**
	 * Update fonts
	 */
	public void updateFonts(){
		Font font = app.getPlainFont();
		setFont(font);
		menuButton.setFont(app.getPlainFont());
		repaint();
	}




	// =============================================
	//         Tree Cell Renderer
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
			setFont(app.getPlainFont());
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
	//        File system methods
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
	//        XML 
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

	/** 
	 * Add a downward triangle to an icon to indicate a drop down menu. */
	private ImageIcon dropDownIcon( ImageIcon icon, Color bgColor) {

		// Create image 
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		
		BufferedImage image = new BufferedImage(w + 14, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();

	
		g2.drawImage(icon.getImage(), 0, 0, w, h, 0, 0, w, h, null);

		// right hand side: a downward triangle
		g2.setColor(Color.BLACK);
		int x = w + 5;
		int y = h/2-1;
		g2.drawLine(x, y, x+6, y);
		g2.drawLine(x+1, y+1, x+5, y+1);
		g2.drawLine(x+2, y+2, x+4, y+2);
		g2.drawLine(x+3, y+3, x+3, y+3);

		return new ImageIcon(image);	

	}

	
	
	
	
	
	

}


