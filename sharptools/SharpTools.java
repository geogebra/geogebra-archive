package sharptools;
/*
 * @(#)SharpTools.java
 * 
 * $Id: SharpTools.java,v 1.1 2007-02-20 13:58:20 hohenwarter Exp $
 * 
 * Created on October 10, 2000, 1:15 AM
 */

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * This is the graphical user interface class for the spreadsheet.
 * <p>
 * The initial size of the window can be controlled by editing the
 * sharptools.ini file.  This class also contains button and menu
 * initialization and editing methods.
 *
 * @author  Daniel Goldberg (initial UI)
 * @author  Andrei Scudder, Daniel Medina (more work)
 * @author  Hua Zhong (complete for v1.0)
 * @version $Revision: 1.1 $
 */
final public class SharpTools extends JFrame implements ListSelectionListener {

    protected int maxNumPage = 1;

    // the rest of the components
    private Container container;
    private JTable table;
    private JScrollPane scrollPane;
    private JToolBar toolBar;
    private JMenuBar menuBar;
    private JPanel barPanel;
    private History history;
    private Histogram histogram;

    static private Config config;

    private SharpTableModel tableModel;
    private ListSelectionModel rowSelectionModel;
    private ListSelectionModel columnSelectionModel;

    private URL url;

    // these are used to access our global objects
    static public Config getConfig() { return config; }    
    
    public History getHistory() { return history; }
    public JTable getTable() { return table; }
    public SharpTableModel getTableModel() { return tableModel; }
    public JToolBar getToolBar() { return toolBar; }
    //public JMenuBar getMenuBar() { return menuBar; }
    private FileOp fileOp;
    private EditOp editOp;
    private TableOp tableOp;
    //    private HelpOp helpOp;

    // initialize JMenu fields
    // menu headings
    private JMenu fileMenu;
    private JMenu openMenu;
    private JMenu recentMenu;
    private JMenu editMenu;
    private JMenu tableMenu;
    private JMenu chartMenu;
    private JMenu helpMenu;
	// submenu headings
    private JMenu insertMenu;
    private JMenu deleteMenu;
    private JMenu sortMenu;
    //    private JMenuItem menuItem;
    private JMenuItem saveMenuItem;
    private JCheckBoxMenuItem saveOnExitMenuItem;
    private JMenuItem undoMenuItem;
    private JMenuItem redoMenuItem;
    private JMenuItem findNextMenuItem;
    private JCheckBoxMenuItem showHistogramMenuItem;
    private JCheckBoxMenuItem showFunctionsMenuItem;
    
    private JButton saveButton;
    private JButton passwordButton;
    private JButton undoButton;
    private JButton redoButton;
    private JComponent funcList;
    //    private JCheckBox showButton;

    // recources
    final private ImageIcon newIcon = getImageIcon("new.gif");
    final private ImageIcon openIcon = getImageIcon("open.gif");
    final private ImageIcon saveIcon = getImageIcon("save.gif");
    final private ImageIcon unlockedIcon = getImageIcon("unlocked.gif");
    final private ImageIcon lockedIcon = getImageIcon("locked.gif");
    final private ImageIcon printIcon = getImageIcon("print.gif");
    final private ImageIcon undoIcon = getImageIcon("undo.gif");
    final private ImageIcon redoIcon = getImageIcon("redo.gif");
    final private ImageIcon cutIcon = getImageIcon("cut.gif");
    final private ImageIcon copyIcon = getImageIcon("copy.gif");
    final private ImageIcon pasteIcon = getImageIcon("paste.gif");
    final private ImageIcon findIcon = getImageIcon("find.gif");
    final private ImageIcon insertRowIcon = getImageIcon("insertrow.gif");
    final private ImageIcon insertColumnIcon = getImageIcon("insertcolumn.gif");
    final private ImageIcon deleteRowIcon = getImageIcon("deleterow.gif");
    final private ImageIcon deleteColumnIcon = getImageIcon("deletecolumn.gif");
    
    final private ImageIcon sortIcon = getImageIcon("sort.gif");
    //    final private ImageIcon showIcon = getImageIcon("show.gif");
    final private ImageIcon chartIcon = getImageIcon("chart.gif");
    final private ImageIcon helpIcon = getImageIcon("help.gif");

    public static int baseRow = 0;
    public static int baseCol = 1;
    
    /** Creates new SharpTools */
    public SharpTools() {
	
        super("Sharp Tools Spreadsheet");
	//	setIconImage(Toolkit.getDefaultToolkit().getImage("tools.jpg"));

	int x = config.getInt("X");
	int y = config.getInt("Y");
	Dimension scrdim = getToolkit().getScreenSize();

	// make sure the point is valid
	if (x>=0 && y>=0 &&
	    x<(int)scrdim.getWidth() && y <(int)scrdim.getHeight())
	    setLocation(x, y);
	
	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // get content pane
	container = this.getContentPane();
	container.setLayout(new BorderLayout());

	// create the table
	table = new JTable();

	// create the Functions combobox in the table
	funcList = HelpOp.createFunctionListComponent(this, table);
	
	// set up fileMenu
        fileMenu = new JMenu("File", true);
	fileMenu.setMnemonic(KeyEvent.VK_F); //used constructor instead
	// add fileMenu actions
	JMenuItem menuItem = new JMenuItem("New...");
	menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    fileOp.newFile();
		}
	    });
	fileMenu.add(menuItem);

	// SJK Added
	openMenu = new JMenu ("Open");
	openMenu.setMnemonic (KeyEvent.VK_O);

	menuItem = new JMenuItem ("File...");
	menuItem.setMnemonic (KeyEvent.VK_F);
	menuItem.setAccelerator (KeyStroke.getKeyStroke
				 (KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent evt) {
		    fileOp.openFile ();
		}
	    } );

	openMenu.add (menuItem);

	menuItem = new JMenuItem ("Database...");
	menuItem.setMnemonic (KeyEvent.VK_D);
	menuItem.setAccelerator (KeyStroke.getKeyStroke
				 (KeyEvent.VK_D, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent evt) {
		    fileOp.openDb ();
		}
	    } );

	openMenu.add (menuItem);

	fileMenu.add (openMenu);
	
	// use provate member for further reference
	saveMenuItem = new JMenuItem("Save");
	saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.CTRL_MASK));
	saveMenuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    fileOp.saveFile();
		}
	    });
        fileMenu.add(saveMenuItem);
	
	menuItem = new JMenuItem("Save As...");
	menuItem.setMnemonic(KeyEvent.VK_A);
	//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
	//				KeyEvent.VK_A, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    fileOp.saveAsFile();
		}
	    });
	fileMenu.add(menuItem);


	menuItem = new JMenuItem("Set Password...");
	menuItem.setMnemonic(KeyEvent.VK_E);
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    fileOp.setPassword();
		    checkPasswordState();
		}
	    });
	fileMenu.add(menuItem);

	fileMenu.addSeparator();
	menuItem = new JMenuItem("Print...");
	menuItem.setMnemonic(KeyEvent.VK_P); //used constructor instead
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_P, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    //notYetImplemented();		
		    Thread runner = new Thread() {
			    public void run() {
				fileOp.printData();
			    }
			};
		    runner.start(); 
		}
	    });
	fileMenu.add(menuItem);

	fileMenu.addSeparator();

	recentMenu = new JMenu("Recent Files");
	recentMenu.setMnemonic(KeyEvent.VK_R); //used constructor instead
	recentMenu.addMenuListener(new MenuListener() {
		public void menuCanceled(MenuEvent e) {}
		public void menuDeselected(MenuEvent e) {}
		public void menuSelected(MenuEvent e) {
		    fileOp.createRecentFilesMenu(recentMenu);
		}
	    });
	
	fileMenu.add(recentMenu);
	
	fileMenu.addSeparator();

	//set up Save Window on Exit
	saveOnExitMenuItem = new JCheckBoxMenuItem("Save Window on Exit");
	saveOnExitMenuItem.setMnemonic(KeyEvent.VK_W);
	saveOnExitMenuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    config.setBoolean("SAVEWINDOW", saveOnExitMenuItem.isSelected());
		}
	    });
	// initialize
	saveOnExitMenuItem.setSelected(config.get("SAVEWINDOW").equals("TRUE"));
	fileMenu.add(saveOnExitMenuItem);
	
	fileMenu.addSeparator();
	
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    //exitMenuActionPerformed(evt);
		    exit();
		}
	    });
        fileMenu.add(menuItem);

	//Set up Edit menu
	editMenu = new JMenu("Edit", true);
	editMenu.setMnemonic(KeyEvent.VK_E);

        // Undo
        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setMnemonic(KeyEvent.VK_U);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
	undoMenuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    // save current selection
		    history.undo(tableModel);
		}
	    });
        editMenu.add(undoMenuItem);

	// Redo
	redoMenuItem = new JMenuItem("Redo");
        redoMenuItem.setMnemonic(KeyEvent.VK_R);
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
	redoMenuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    history.redo(tableModel);
		}
	    });
        editMenu.add(redoMenuItem);
	editMenu.addSeparator();

	// cut
	menuItem = new JMenuItem("Cut");
	menuItem.setMnemonic(KeyEvent.VK_T); 
	menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.cut();
                }
            });
	editMenu.add(menuItem);

	// copy
	menuItem = new JMenuItem("Copy");
        menuItem.setMnemonic(KeyEvent.VK_C); 
	menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.copy();
		}
	    });
	editMenu.add(menuItem);

	// paste
	menuItem = new JMenuItem("Paste");
	menuItem.setMnemonic(KeyEvent.VK_P); 
	menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.paste();
		}
	    });
	editMenu.add(menuItem);
	editMenu.addSeparator();

	// Fill
	menuItem = new JMenuItem("Fill...");
	menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		  editOp.fill();
		}
	    }); 
	editMenu.add(menuItem);

	// Clear
	menuItem = new JMenuItem("Clear");
	menuItem.setMnemonic(KeyEvent.VK_L);
	menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_DELETE, 0, true));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.clear();
		}
	    });
	editMenu.add(menuItem);
	editMenu.addSeparator();

	// Find
	menuItem = new JMenuItem("Find...");
	menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.find(true);
		}
	    });
	editMenu.add(menuItem);
	
	findNextMenuItem = new JMenuItem("Find Next");
	findNextMenuItem.setMnemonic(KeyEvent.VK_N);
        findNextMenuItem.setAccelerator(KeyStroke.getKeyStroke
				    (KeyEvent.VK_F3, 0));
	findNextMenuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.find(false);
		}
	    });
	editMenu.add(findNextMenuItem);
	
	//set up tablemenu
	tableMenu = new JMenu("Table", true);
	tableMenu.setMnemonic(KeyEvent.VK_T);
	//set up tableMenu actions
	insertMenu = new JMenu("Insert");
	insertMenu.setMnemonic(KeyEvent.VK_I);
	
	menuItem = new JMenuItem("Row");
	menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_INSERT, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.insert(true);
		}
		});
	insertMenu.add(menuItem);
	
	menuItem = new JMenuItem("Column");
	menuItem.setMnemonic(KeyEvent.VK_C);
	menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_INSERT,
				 ActionEvent.CTRL_MASK|
				 ActionEvent.SHIFT_MASK));	
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.insert(false);
		}
	    });
	insertMenu.add(menuItem);
	tableMenu.add(insertMenu);

	//set up Delete actions
        deleteMenu = new JMenu("Delete");
	deleteMenu.setMnemonic(KeyEvent.VK_D);
	menuItem = new JMenuItem("Row");
	menuItem.setMnemonic(KeyEvent.VK_R);
	menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    //notYetImplemented();
                    tableOp.remove(true);
		}
	    });
	deleteMenu.add(menuItem);
	menuItem = new JMenuItem("Column");
	menuItem.setMnemonic(KeyEvent.VK_C);
	menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_DELETE,
				 ActionEvent.CTRL_MASK|
				 ActionEvent.SHIFT_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    //notYetImplemented();
                    tableOp.remove(false);
		}
	    });
	deleteMenu.add(menuItem);
	tableMenu.add(deleteMenu);
	
	tableMenu.addSeparator();
	//set up Sort actions
	sortMenu = new JMenu("Sort");
	sortMenu.setMnemonic(KeyEvent.VK_S);
	menuItem = new JMenuItem("Row...");
	menuItem.setMnemonic(KeyEvent.VK_R);
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
                    tableOp.sort(true);
		}
	    });
	sortMenu.add(menuItem);
	menuItem = new JMenuItem("Column...");
	menuItem.setMnemonic(KeyEvent.VK_C);
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.sort(false);
		}
	    });
	sortMenu.add(menuItem);
	tableMenu.add(sortMenu);
	tableMenu.addSeparator();
	
	menuItem = new JMenuItem("Set Column Width...");
	menuItem.setMnemonic(KeyEvent.VK_W);
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.setColumnWidth();
		}
	    });
	tableMenu.add(menuItem);
	
	//set up Histogram menu
	chartMenu = new JMenu("Chart", true);
	chartMenu.setMnemonic(KeyEvent.VK_C);

	chartMenu.addMenuListener (new MenuListener () {
                public void menuSelected (MenuEvent e) {
		    checkShowHistogramState();
		}
		public void menuDeselected (MenuEvent e) { }
		public void menuCanceled (MenuEvent e) { }		
	    });


	//set up Show Histogram actions
	showHistogramMenuItem = new JCheckBoxMenuItem("Show");
	showHistogramMenuItem.setMnemonic(KeyEvent.VK_S);
        showHistogramMenuItem.setAccelerator(KeyStroke.getKeyStroke
					 (KeyEvent.VK_F8, 0));
	showHistogramMenuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    histogram.toggle();
		}
	    }); 
	chartMenu.add(showHistogramMenuItem);	

	//set up Add Histogram actions
	menuItem = new JMenuItem("Histogram...");
	menuItem.setMnemonic(KeyEvent.VK_H);
        menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    histogram.addHistogram();
		}
	    }); 

	chartMenu.add(menuItem);
	tableMenu.add(chartMenu);


	//set up Help menu
	helpMenu = new JMenu("Help", true);
	helpMenu.setMnemonic(KeyEvent.VK_H);

	//set up Help actions        
	menuItem = new JMenuItem("Help Topics");
	menuItem.setMnemonic(KeyEvent.VK_H);
	menuItem.setAccelerator(KeyStroke.getKeyStroke
				(KeyEvent.VK_F1, 0));

	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    // testcode
		    try {
			URL url = new URL("http://www.cs.columbia.edu/sharptools/");
			new HelpOp(url);
		    } catch(MalformedURLException e) {
			e.printStackTrace();
			return;
		    }
		}
	    });
	helpMenu.add(menuItem);

	showFunctionsMenuItem = new JCheckBoxMenuItem("Show Functions");
	showFunctionsMenuItem.setMnemonic(KeyEvent.VK_S);
	showFunctionsMenuItem.setSelected(config.getBoolean("TOOLBAR_FUNCTIONS"));
	showFunctionsMenuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    boolean showFunc = showFunctionsMenuItem.isSelected();
		    config.setBoolean("TOOLBAR_FUNCTIONS", showFunc);
		    funcList.setVisible(showFunc);		    
		    // repaint
		}
            });
	
	helpMenu.add(showFunctionsMenuItem);

	helpMenu.addSeparator();
	menuItem = new JMenuItem("About SharpTools...");
	menuItem.setMnemonic(KeyEvent.VK_A); 
	menuItem.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    HelpOp.showAboutBox(SharpTools.this);
		    table.requestFocus();
		}
            });
	helpMenu.add(menuItem); 
	
        // set up menu bar and menus
        menuBar = new JMenuBar();

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(tableMenu);
	menuBar.add(chartMenu);
        menuBar.add(helpMenu);
        
        // button initializations
        JButton newButton = new JButton(newIcon);
	newButton.setToolTipText("New");
	newButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    fileOp.newFile();
		    table.requestFocus();
		}
	    });
        JButton openButton = new JButton(openIcon);
	openButton.setToolTipText("Open");
	openButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    fileOp.openFile();
		    table.requestFocus();
		}
	    });

	// we save this button as private member for further reference
        saveButton = new JButton(saveIcon);
	saveButton.setToolTipText("Save");
	saveButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    fileOp.saveFile();
		    table.requestFocus();
		}
	    });
	
	// we save this button as private member for further reference	
        passwordButton = new JButton(unlockedIcon);
	passwordButton.setToolTipText("Set Password");
	passwordButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    fileOp.setPassword();
		    table.requestFocus();
		}
	    });

	JButton printButton = new JButton(printIcon);
	printButton.setToolTipText("Print");
	printButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    Thread runner = new Thread() {
			    public void run() {
				fileOp.printData();
			    }
			};
		    runner.start();
		    table.requestFocus();
		}
	    });

	undoButton = new JButton(undoIcon);
	undoButton.setToolTipText("Undo");
	undoButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    history.undo(tableModel);
		    table.requestFocus();
		}
	    });

	redoButton = new JButton(redoIcon);
	redoButton.setToolTipText("Redo");
	redoButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    history.redo(tableModel);
		    table.requestFocus();
		}
	    });
	
        JButton cutButton = new JButton(cutIcon);
	cutButton.setToolTipText("Cut");
	cutButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.cut();
		    table.requestFocus();
		}
	    });
        JButton copyButton = new JButton(copyIcon);
	copyButton.setToolTipText("Copy");
	copyButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.copy();
		    table.requestFocus();
		}
	    });
	
        JButton pasteButton = new JButton(pasteIcon);
	pasteButton.setToolTipText("Paste");
        pasteButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.paste();
		    table.requestFocus();
		}
	    });

        JButton findButton = new JButton(findIcon);
	findButton.setToolTipText("Find");
        findButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    editOp.find(true);
		    table.requestFocus();
		}
	    });

	JButton sortButton = new JButton(sortIcon);
	sortButton.setToolTipText("Sort by Column");
        sortButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.sort(false);
		    table.requestFocus();
		}
	    });

	JButton insertRowButton = new JButton(insertRowIcon);
	insertRowButton.setToolTipText("Insert Row");
        insertRowButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.insert(true);
		    table.requestFocus();
		}
	    });

	JButton insertColumnButton = new JButton(insertColumnIcon);
	insertColumnButton.setToolTipText("Insert Column");
        insertColumnButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.insert(false);
		    table.requestFocus();
		}
	    });

	JButton deleteRowButton = new JButton(deleteRowIcon);
	deleteRowButton.setToolTipText("Delete Row");
        deleteRowButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.remove(true);
		    table.requestFocus();
		}
	    });

	JButton deleteColumnButton = new JButton(deleteColumnIcon);
	deleteColumnButton.setToolTipText("Delete Column");
        deleteColumnButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    tableOp.remove(false);
		    table.requestFocus();
		}
	    });

        JButton chartButton = new JButton(chartIcon);
	chartButton.setToolTipText("Histogram");
        chartButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    histogram.addHistogram();
		    table.requestFocus();
		}
	    });
	
	JButton helpButton = new JButton(helpIcon);
	helpButton.setToolTipText("Help");
	helpButton.addActionListener (new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
		    try {
			String basePath = System.getProperty("user.dir");
			url = new URL("http://www.cs.columbia.edu/sharptools/");
			new HelpOp(url);

		    } catch(MalformedURLException e) {
			e.printStackTrace();
			return;
		    }
		    table.requestFocus();
		}
	    });

        /*
	 * Set up toolbar
	 *
	 * Toolbar can be customized in the configuration file.
	 */
        toolBar = new JToolBar();
	toolBar.setFloatable(false);

	if (config.getBoolean("TOOLBAR_NEW"))
	    toolBar.add(newButton);

	if (config.getBoolean("TOOLBAR_OPEN"))
	    toolBar.add(openButton);

	if (config.getBoolean("TOOLBAR_SAVE"))
	    toolBar.add(saveButton);

	toolBar.addSeparator();

	if (config.getBoolean("TOOLBAR_PASSWORD"))
	    toolBar.add(passwordButton);
	
	if (config.getBoolean("TOOLBAR_PRINT"))
	    toolBar.add(printButton);

        toolBar.addSeparator();

	if (config.getBoolean("TOOLBAR_UNDO"))
	    toolBar.add(undoButton);

	if (config.getBoolean("TOOLBAR_REDO"))
	    toolBar.add(redoButton);

	if (config.getBoolean("TOOLBAR_CUT"))
	    toolBar.add(cutButton);

	if (config.getBoolean("TOOLBAR_COPY"))
	    toolBar.add(copyButton);

	if (config.getBoolean("TOOLBAR_PASTE"))
	    toolBar.add(pasteButton);

	if (config.getBoolean("TOOLBAR_FIND"))
	    toolBar.add(findButton);

	toolBar.addSeparator();

	if (config.getBoolean("TOOLBAR_INSERTROW"))
	    toolBar.add(insertRowButton);

	if (config.getBoolean("TOOLBAR_INSERTCOLUMN"))
	    toolBar.add(insertColumnButton);

	if (config.getBoolean("TOOLBAR_DELETEROW"))
	    toolBar.add(deleteRowButton);

	if (config.getBoolean("TOOLBAR_DELETECOLUMN"))
	    toolBar.add(deleteColumnButton);

	if (config.getBoolean("TOOLBAR_SORTCOLUMN"))
	    toolBar.add(sortButton);
	
        toolBar.addSeparator();

	if (config.getBoolean("TOOLBAR_HISTOGRAM")) {
	    toolBar.add(chartButton);
	    toolBar.addSeparator();
	}

	if (config.getBoolean("TOOLBAR_HELP"))
	    toolBar.add(helpButton);

        toolBar.addSeparator();
	toolBar.add(funcList);
	    
	funcList.setVisible(config.getBoolean("TOOLBAR_FUNCTIONS"));

	newTableModel(config.getInt("ROWS"),
		      config.getInt("COLUMNS"));
	
	// set window pos and size
	int w = config.getInt("WIDTH");
	int h = config.getInt("HEIGHT");	
	
	if (w >= 0 && h >= 0)
	    table.setPreferredScrollableViewportSize(new Dimension(w, h));

	// init fileOp objects
	fileOp = new FileOp(this);

	// clobber resizing of all columns
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // set table editor and renderer to custom ones
	table.setDefaultRenderer(Cell.class, new SharpCellRenderer());
        table.setDefaultEditor(Cell.class, new SharpCellEditor(
						  new JTextField()));

	// set selection mode for contiguous  intervals
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.setCellSelectionEnabled(true);

	// we don't allow reordering
        table.getTableHeader().setReorderingAllowed(false);
	table.getTableHeader().addMouseListener(new HeaderMouseAdapter());
	
	// create selection models
	rowSelectionModel = table.getSelectionModel();
        columnSelectionModel = table.getColumnModel().getSelectionModel();

	// add selection listeners to the selection models
	rowSelectionModel.addListSelectionListener(this);
	columnSelectionModel.addListSelectionListener(this);
	
	// set menu bar
	setJMenuBar(menuBar);
	container.add(toolBar, BorderLayout.NORTH);	
	
	scrollPane = new JScrollPane(table,
				     JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				     JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
        container.add(scrollPane, BorderLayout.CENTER);
        
        // add window exit listener
        addWindowListener (new WindowAdapter () {
		public void windowOpened (WindowEvent evt) {		    
		    table.requestFocus();
		}
		
		public void windowClosing (WindowEvent evt) {
		    exit();
		}
	    });

	// initial selection
	resetSelection();
	
	table.setRequestFocusEnabled(true);
	menuBar.setRequestFocusEnabled(false);
	toolBar.setRequestFocusEnabled(false);
	table.requestFocus();	
	
	pack();
	show();
    }

    public void setColumnWidth(int colWidth) {

	if (colWidth > 0)
	    for (int i = baseCol; i < tableModel.getColumnCount(); i ++) {
		TableColumn column = table.getColumnModel().getColumn(i);
		column.setMinWidth(colWidth);
		column.setPreferredWidth(colWidth);
	    }
    }
    
    /** 
     * Creates new blank SharpTableModel object with specified number of
     * rows and columns.  table is set to this table model to update screen.
     *
     * @param rows number of rows in new table model
     * @param cols number of columns in new table model
     */
    public void newTableModel(int rows, int cols) {
	tableModel = new SharpTableModel(this, rows, cols);
	table.setModel(tableModel);

	setBaseColumnWidth();
	
	setColumnWidth(config.getInt("COLUMNWIDTH"));

	// update history with new one
	history = new History(this);
	tableModel.setHistory(history);	

	// inform tableModel that it's unmodified now
	tableModel.setPasswordModified(false);
	tableModel.setModified(false);

	// init op objects
	// we shouldn't init fileOp!
	
	if (editOp == null)
	    editOp = new EditOp(this);
	else
	    /* if we already have an object, we don't construct
	       a new EditOp to keep the clipboard and findValue
	       still valid.  This makes us be able to exchange
	       data across files */
	    editOp.init(this);

	tableOp = new TableOp(this);
	histogram = new Histogram(this, "Histograms",
				  config.getInt("HISTOGRAMWIDTH"),
				  config.getInt("HISTOGRAMHEIGHT"));
	
	tableModel.setModified(false);
	
	resetSelection();
	
	// menubar/toolbar initial status
	checkUndoRedoState();
	
	table.requestFocus();
    }
    
    /**
     * a function to display warning messages
     *
     * @param s the operation that caused this error
     */
    public void noCellsSelected(String s) {
        SharpOptionPane.showMessageDialog(this, s + ": No cells selected","Error", JOptionPane.ERROR_MESSAGE, null);
    }

    /**
     * The error message for clicks on functions we haven't done yet.
     */
    private void notYetImplemented(){
	SharpOptionPane.showMessageDialog(this,
					  "Sorry, this function \n"+
					  "is not yet implemented!\n"+
					  "sharp@cs.columbia.edu",
					  "Sharp Tools Spreadsheet",
					  JOptionPane.WARNING_MESSAGE,
					  null);
    }

    /** Exit the Application */
    public void exit() {
	if (fileOp.closeFile()) {
	
	    // save window position and size
	    if (config.getBoolean("SAVEWINDOW")) {
		config.setInt("X", getX());
		config.setInt("Y", getY());
		Dimension dim = scrollPane.getViewport().getExtentSize();
		config.setInt("WIDTH", (int)dim.getWidth());
		config.setInt("HEIGHT", (int)dim.getHeight());
	    }
	    
	    config.save();	    
	    System.exit(0);
	}
    }
    
    /**
     * Directly open a file with specified name - used only in main
     *
     * @param filename the String of filename
     */
    private void openInitFile(String filename) {
	fileOp.openTableModel(new File(filename));
    }


    /**
     * Check menu items and toolbar buttons
     * Set to appropriate status (disable/enable)         
     */

    /**
     * Check the save menu/button state
     * Enable only when the file has been modified
     */
    public void checkSaveState() {
	boolean modified = tableModel.isModified();
	saveMenuItem.setEnabled(modified);
	saveButton.setEnabled(modified);
    }

    /**
     * Check the set password toolbar button icon
     * Change the icon based on whether password is set
     */
    public void checkPasswordState() {
	if (fileOp.hasPassword())
	    passwordButton.setIcon(lockedIcon);
	else
	    passwordButton.setIcon(unlockedIcon);
    }

    /**
     * Check the undo/redo menu/button state
     * Enable only when it's undoable/redoable
     */
    public void checkUndoRedoState() {
	boolean enable = history.isUndoable();
	undoMenuItem.setEnabled(enable);
	undoButton.setEnabled(enable);
	enable = history.isRedoable();
	redoMenuItem.setEnabled(enable);
	redoButton.setEnabled(enable);
    }

    /**
     * Check the find next menu/button state
     * Enable only when the user has searched once
     */
    public void checkFindNextState() {
	findNextMenuItem.setEnabled(editOp.hasFindValue());
    }

    /**
     * Check the show histogram menu
     * Enable only when there is at least one histogram defined
     */
    public void checkShowHistogramState() {	
	showHistogramMenuItem.setState(histogram.isVisible());
	showHistogramMenuItem.setEnabled(histogram.hasChart());	
    }

    public void setBaseColumnWidth() {
	// resize first column
	if (baseCol > 0) {
	    TableColumn firstColumn = table.getColumnModel().getColumn(baseCol-1);
	    int firstColWidth = config.getInt("FIRSTCOLUMNWIDTH");
	    if (firstColWidth>0) {
		firstColumn.setMinWidth(firstColWidth);
		firstColumn.setPreferredWidth(firstColWidth);
	    }
	}
    }
    
    /** This is the main method that gets the ball rolling */
    public static void main(String args[]){
	// show splash screen! - cancelled - we start up fast!
	//	new SplashWindow("logo.jpg", null, 2000);
	
	// read configuration file
	config = new Config("sharptools.ini");

	// set default value
	config.setInt("ROWS", 20);
	config.setInt("COLUMNS", 10);
	//	config.set("AUTORESIZE", "TRUE");
	config.setInt("HISTOGRAMWIDTH", 600);
	config.setInt("HISTOGRAMHEIGHT", 400);

	// read file
	config.load();

	// only change it when DEBUG is uncommented in the config file
	if (config.get("DEBUG") != null)
	    Debug.setDebug(config.getBoolean("DEBUG"));
	
	// initialize the function handler table object
	Formula.registerFunctions();
	
        SharpTools spreadsheet = new SharpTools();
        spreadsheet.show();
	if (args.length>0)
	    spreadsheet.openInitFile(args[0]);
    }

    // this is a static function to help loading images
    public static ImageIcon getImageIcon(String name) {
	URL url = ClassLoader.getSystemResource(name);
	if (url == null) {
	    System.out.println("image "+name+" not found");
	    return null;
	}
	return new ImageIcon(url);
    }

    class HeaderMouseAdapter extends MouseAdapter {

	public void mouseClicked(MouseEvent e) {
	    TableColumnModel colModel = 
		table.getColumnModel();
	    int col = 
		colModel.getColumn(colModel.getColumnIndexAtX(e.getX())).getModelIndex();
	    
	    int rowCount = table.getRowCount();
	    table.setRowSelectionInterval(baseRow, rowCount - 1);

	    if (col < baseCol)
		table.setColumnSelectionInterval(baseCol, table.getColumnCount()-1);
	    else
		table.setColumnSelectionInterval(col, col);
	}
    }

    // the ListSelectionListener interface
    public void valueChanged(ListSelectionEvent e) {
	table.requestFocus();
	    
	// Ignore extra messages
	if (e.getValueIsAdjusting()) return;
	
	// Get event source
	ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	
	if (lsm.isSelectionEmpty()) {
	    // always set selection
	    table.setRowSelectionInterval(baseRow, baseRow);
	    table.setColumnSelectionInterval(baseCol, baseCol);
	} else {
	    // Get selected column
	    if (table.getSelectedColumn() < baseCol) {
		int columnCount = table.getColumnCount();
		table.setColumnSelectionInterval(baseCol, columnCount - 1);
		table.removeColumnSelectionInterval(baseRow,baseRow);
	    }	    
	}
    }

    public void resetSelection() {
    	table.setRowSelectionInterval(baseRow, baseRow);
	table.setColumnSelectionInterval(baseCol, baseCol);
    }
}



