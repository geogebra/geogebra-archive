package sharptools;
/*
 * @(#)FileOp.java
 *
 * $Id: FileOp.java,v 1.2 2007-03-07 06:24:32 hohenwarter Exp $
 *
 * Created on November 16, 2000, 12:00 AM
 *
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Handles the saving and opening of files, and the updating of the table model
 * appropriately.
 *
 * @author  Daniel Goldberg, Hua Zhong, Shiraz Kanga (openDb)
 * @version $Revision: 1.2 $
 */

public class FileOp implements ActionListener, Printable {

    final static private int UNNAMED = 0;
    final static private int FILE = 1;
    final static private int DATABASE = 2;

    private int openType = UNNAMED;
    
    private int maxNumPage = 1;
    private boolean columnOverflow;

    private StringTokenizer tokenizer;
    private PrintStream out;
    private BufferedReader in;

    private SharpTools sharp;
    private JTable table;
    private SharpTableModel tableModel;
    
    private File file;
    private String password;
    //Create a file chooser
    private JFileChooser fileChooser;
    private static SharpFileFilter filter;
    final private static ImageIcon newIcon = SharpTools.getImageIcon("new32.gif");
    final private static ImageIcon openIcon = SharpTools.getImageIcon("open.gif");
    final private static ImageIcon saveIcon = SharpTools.getImageIcon("save32.gif");
    final private static String extname = ".cvs";

    class SharpFileFilter extends javax.swing.filechooser.FileFilter {
    
	public boolean accept(File file) {
	    if (file.isDirectory())
		return true;

	    return file.getName().endsWith(extname);
	}

	public String getDescription() {
	    return "Sharp Tools Spreadsheet (*.cvs)";
	}
    }
    /** 
     * This contructor creates a FileOperations object with reference
     * SharpTableModel; the file of the TableModel is extracted.
     *
     * @param tableModel calling table model is referenced in
     */
    public FileOp(SharpTools gui) {
	sharp = gui;
	table = gui.getTable();
	tableModel = gui.getTableModel();
	fileChooser = new JFileChooser(new File("."));
	filter = new SharpFileFilter();
	fileChooser.addChoosableFileFilter(filter);
	sharp.setTitle("Untitled - Sharp Tools Spreadsheet");

        columnOverflow = false;

	// below define key shortcuts

	table.registerKeyboardAction
	    (this,"New",
	     KeyStroke.getKeyStroke
	     (KeyEvent.VK_N,ActionEvent.CTRL_MASK,false),
	     JComponent.WHEN_FOCUSED);
	
	table.registerKeyboardAction
	    (this,"OpenFile",
	     KeyStroke.getKeyStroke
	     (KeyEvent.VK_O,ActionEvent.CTRL_MASK,false),
	     JComponent.WHEN_FOCUSED);

	table.registerKeyboardAction
	    (this,"OpenDb",
	     KeyStroke.getKeyStroke
	     (KeyEvent.VK_D,ActionEvent.CTRL_MASK,false),
	     JComponent.WHEN_FOCUSED);
	
	table.registerKeyboardAction
	    (this,"Save",
	     KeyStroke.getKeyStroke
	     (KeyEvent.VK_S,ActionEvent.CTRL_MASK,false),
	     JComponent.WHEN_FOCUSED);

	table.registerKeyboardAction
	    (this,"Print",
	     KeyStroke.getKeyStroke
	     (KeyEvent.VK_P,ActionEvent.CTRL_MASK,false),
	     JComponent.WHEN_FOCUSED);
	
	table.registerKeyboardAction
	    (this,"Exit",
	     KeyStroke.getKeyStroke
	     (KeyEvent.VK_E,ActionEvent.CTRL_MASK,false),
	     JComponent.WHEN_FOCUSED);	     
    }

    /**
     * Confirm dialog box that queries whether user wishes to save
     * current document or not.
     *
     * @return true to save, false otherwise
     */
    public int queryForSave() {
	// show confirm dialog box from static JOptionPane method
	String filename = "Untitled";
	if (file != null)
	    filename = file.getName();
	
	int choice =
	    SharpOptionPane.showOptionDialog
	    (sharp,
	     "Do you want to save the changes you made to \""+filename+"\"?",
	     "Save",
	     JOptionPane.YES_NO_CANCEL_OPTION,
	     JOptionPane.QUESTION_MESSAGE,
	     saveIcon);
	// return user's choice
	return choice;
    }

    /**
     * Menu and button wrapper that handles interactive process of creating
     * new document.  queryForSave returns choice, which determines action
     * action performed according to entry point in switch block.
     */
    public void newFile() {
	int choice;

        // pop up querySave box (yes, no, cancel) and save choice
	// if table modified state is modified, else set choice
	// to JOptionPane.NO_OPTION which clears cells without
	if(tableModel.isModified())
	    choice = queryForSave();
	else
	    choice = JOptionPane.NO_OPTION;
	
	switch(choice) {
	case JOptionPane.YES_OPTION:
	    // if user chooses yes in dialog box, save document first
	    // before executing clear range
	    saveFile();
	case JOptionPane.NO_OPTION:

	    Config config = sharp.getConfig();

	    NewFileDialog dialog = new NewFileDialog(sharp,
						     config.getInt("ROWS"),
						     config.getInt("COLUMNS"));
	    dialog.setLocationRelativeTo(sharp);
	    dialog.setVisible(true);

	    if (dialog.isCancelled())
		return;

	    int row = dialog.getRows();
	    int col = dialog.getColumns();

	    // range of cells is cleared, the filename is set to untitled
	    // and the modified state is set to unmodified.
	    //	    file = new File("Untitled");
	    file = null;
	    sharp.setTitle("Untitled - Sharp Tools Spreadsheet");
	    sharp.newTableModel(row, col);
	    tableModel = sharp.getTableModel();

	    // reset password
	    setPasswordValue(password);

	default:
	    // break out of loop for all choices.  action depends
	    // on entry point.
	    break;
	}
    }

    /**
     * Menu and button wrapper that handles interactive process of saving
     * current file as itself.
     *
     */
    public void saveFile() {
	if(tableModel.isModified()) {
	    //	    if(file.getName().equals("Untitled")) {
	    if (file == null) {
		saveAsFile();
	    } else {
		saveTableModel();
	    }
        }
    }
    
    /**
     * Menu and button wrapper that handles interactive process of saving
     * current file.
     *
     */
    public void saveAsFile() {
	// open save dialog and save user input
	int choice = fileChooser.showSaveDialog(sharp);
	
	// if user clicks ok, then procede with save, otherwise do nothing
	if(choice == JFileChooser.APPROVE_OPTION) {
	    // get selected file to save to
	    File selectedfile = fileChooser.getSelectedFile();

	    fileChooser = new JFileChooser(selectedfile.getParentFile());
	    fileChooser.addChoosableFileFilter(filter);

	    // enforce the extension name
	    String path;

	    try {
		path = selectedfile.getCanonicalPath();
	    }
	    catch (IOException e) {
		fileOpError("Save", "Unexpected error!");
		return;
	    }

	    if (!path.endsWith(extname))
		selectedfile = new File(path+extname);

	    // check the existence of the file
	    if (selectedfile.exists()) {
		choice =
		    SharpOptionPane.showOptionDialog
		    (sharp,
		     "File "+selectedfile.getName()+" already exists.\n\n"+
		     "Do you want to overwrite it?\n\n",
		     "Save",
		     JOptionPane.YES_NO_OPTION,
		     JOptionPane.WARNING_MESSAGE,
		     saveIcon, 1);
		if (choice != JOptionPane.YES_OPTION)
		    return;
	    }
	    
	    saveTableModel(selectedfile);
	}
    }

    /**
     * Opens document.  Queries user to save if modified state is modified.
     * Uses switch statement on user's choice to choose path of execution.
     */
    public void openFile() {
	int choice;

        // pop up querySave box (yes, no, cancel) and save choice
	// if table modified state is modified, else set choice
	// to JOptionPane.NO_OPTION which clears cells without
	if(tableModel.isModified())
	    choice = queryForSave();
	else
	    choice = JOptionPane.NO_OPTION;
	
	switch(choice) {
	  case JOptionPane.YES_OPTION:
	      // if user chooses yes in dialog box, save document first
	      // before executing clear range
	      saveFile();
  	  case JOptionPane.NO_OPTION:
	      // open dialog from filechooser and save user choice
	      int userChoice = fileChooser.showOpenDialog(sharp);
	      // if user chooses file, call openTableModel on new
	      // FileOperations object, else go to break in default
	      if(userChoice == JFileChooser.APPROVE_OPTION) {
		  File selectedfile = fileChooser.getSelectedFile();
		  fileChooser = new JFileChooser(selectedfile.getParentFile());
		  fileChooser.addChoosableFileFilter(filter);

		  openTableModel(selectedfile);

	      }
	  default:
	      // break out of loop for all choices.  action depends
	      // on entry point.
	      break;
	}
    }

    /**
     * Closes program but checks to see if document is modified and prompts
     * for save.  Saves or not depending on user's choice.  Then exits.
     * Queries for save if modified, and uses switch to parse choice.
     *
     * @return whether the file is closed; user can cancel and return false
     */
    public boolean closeFile() {
	int choice;
	
	if(tableModel.isModified())
	    choice = queryForSave();
	else
	    choice = JOptionPane.NO_OPTION;
	
	switch(choice) {
	case JOptionPane.CANCEL_OPTION:
	    return false;
	case JOptionPane.YES_OPTION:
	    // if user chooses yes in dialog box, save document first
	    // before exiting.
	    saveFile();
	default:
	    //	    System.exit( 0 );
	    return true;
	}
    }
    
    
    /** 
     * Saves table model to specified file cell by cell in tab-delimited
     * format.
     * 
     * @param aFile the file name to save to
     */
    public void saveTableModel(File aFile) {

	try {
	    
	    // initialize output

	    out = new PrintStream(new FileOutputStream(aFile));
	    
	    int rowCount = tableModel.getRowCount();
	    int colCount = tableModel.getColumnCount();
	    // print out number of columns and rows
	    //	    out.println(rowCount+"\t"+colCount);
	    if (password != null && password.length() > 0)
		out.println("Password: "+password);
	    
	    out.print(tableModel.toString());
	    out.flush();
	    
	    file = aFile;
	    // set modified to false
	    tableModel.setPasswordModified(false);
	    tableModel.setModified(false);

	    // update recent files
	    addRecentFile(file);
	    
	    String title = file.getName();
	    if (title.endsWith(extname))
		title = title.substring(0, title.length()-4);
	    sharp.setTitle(title+" - Sharp Tools Spreadsheet");
	} catch (FileNotFoundException e) {
	    fileOpError("Save", "File \""+aFile.getName()+"\" cannot be created!");
	} catch (IOException e) {
	    fileOpError("Save", "I/O error in saving \""+aFile.getName()+"\"!");
	}
    }
    
    /** 
     * Overloaded save function that takes the current filename  as default.
     */
    public void saveTableModel() {
	saveTableModel(file);
    }

    /**
     * Opens table model data from file.
     * 
     * @param aFile file name from which to open
     */
    public void openTableModel(File aFile) {

	StringBuffer textBuf = new StringBuffer();
	String line;
	String password = null;
	try {
	    in =  new BufferedReader(new FileReader(aFile));
	    line = in.readLine();
	    if (line.indexOf('\t') == -1 &&
		line.startsWith("Password: ")) {
		// password defined
		password = line.substring(10);
		if (!verifyPassword(password)) {
		    return;
		}
	    }
	    else {
		textBuf.append(line);
		textBuf.append("\n");
	    }
		
	    while ((line = in.readLine()) != null) {
		textBuf.append(line);
		textBuf.append("\n");
	    }

	    // reset password
	    setPasswordValue(password);

	    String text = textBuf.toString();
	    // create new table model
	    CellPoint size = SharpTableModel.getSize(text);
	    //	    System.out.println(size);
	    sharp.newTableModel(size.getRow(), size.getCol());
	    tableModel = sharp.getTableModel();
	    tableModel.fromString(text, 0, 0,
				  new CellRange(SharpTools.baseRow,
						size.getRow(),
						SharpTools.baseCol,
						size.getCol()));
	    file = aFile;
	    //	    tableModel.setModified(false);
	    // set new title for spreadsheet
	    String title = file.getName();
	    if (title.endsWith(extname))
		title = title.substring(0, title.length()-4);

	    sharp.setTitle(title+" - Sharp Tools Spreadsheet");

	    // update recent files
	    addRecentFile(file);
	    
	} catch (FileNotFoundException e) {
	    fileOpError("Open", "File \""+aFile.getName()+"\" not found!");
	} catch (IOException e) {
	    fileOpError("Open", "I/O error in opening \""+aFile.getName()+"\"!");
	} catch (Exception e) {

	}
    }

    /**
     * Get the file name from the recent list by index
     *
     * @param index the index in the list (also the menu)
     * @return file name
     */
    private String getRecentFile(int index) {
	Config config = SharpTools.getConfig();
	return config.get("RECENTFILE"+String.valueOf(index));
    }

    /**
     * Set the file name in the recent list by index
     *
     * @param index the index in the list (also the menu)
     * @param s file name
     */
    private void setRecentFile(int index, String s) {
	Config config = SharpTools.getConfig();	
	config.set("RECENTFILE"+String.valueOf(index), s);
    }

    /**
     * Move an item from the specified index to the first one
     *
     * @param index the index in the list (also the menu)
     */
    private void moveToFront(int index) {
	if (index <= 0)
	    return;
	
	String frontName = getRecentFile(index);
	for (int i = index; i >0; i--)
	    setRecentFile(i, getRecentFile(i-1));
	
	setRecentFile(0, frontName);
    }

    private void addRecentFile(File file) {
	try {
	    String filename = file.getCanonicalPath();
	    int total = SharpTools.getConfig().getInt("RECENTFILELIST");
	    for (int i = 0; i < total; i++) {
		if (filename.equals(getRecentFile(i))) {
		    moveToFront(i);
		    return;
		}
	    }
	    
	    // now we push it
	    setRecentFile(total-1, filename);
	    moveToFront(total-1);
	}
	catch (Exception e) {
	}
    }

    
    /**
     * construct the recent file list from Config
     *
     * @param menu the menu item to insert file list in
     */
    public void createRecentFilesMenu(JMenu menu) {
        Config config = SharpTools.getConfig();
	menu.removeAll();
	
	try {
	    for (int i = 0; i < config.getInt("RECENTFILELIST"); i++) {
		String filename = getRecentFile(i);
		if (filename.length() > 0) {
		    File file = new File(filename);
		    JMenuItem item = new JMenuItem(file.getName(), openIcon);
		    item.addActionListener(new RecentFileListener(file, this));
		    menu.add(item);
		}
	    }
	    
	}
	catch (Exception e) {}

    }    
    
    /*
     * Set password and do some extra stuff - always use this function
     * to change password value!
     */
    private void setPasswordValue(String newPassword) {
	password = newPassword;
	sharp.checkPasswordState();
    }
    
    /*
     * Initiates print job by creating a <code>PrintJob</code> object,
     * and assigning the current <code>FileOp</code> object to it.
     * It then calls up the Print Dialog, and then if user clicks print
     * on the dialog, print() is called on the PrintJob object.  This call
     * also calls the print method for the FileOp object, which takes
     * care of the creation of a printable table
     */
    public void printData() {
	
	// create temporary object that contains this FileOp object
	// and reset columnOverflow to false
	FileOp fileOp = this;
	columnOverflow = false;

	try {
	    PrinterJob prnJob = PrinterJob.getPrinterJob();

	    prnJob.setPrintable(fileOp);
	    
	    // opens print dialog, and if user doesn't cancel the dialog
	    // it calls print()
	    if (!prnJob.printDialog())
		return;
	    maxNumPage = 1;

	    prnJob.print();
	}
	catch (PrinterException e) {
	    e.printStackTrace();
	    fileOpError("Print", "Printing error: "+e.toString());
	}
    }
    
    /*
     * Main print method of FileOp that performs calculations for
     * dimensions of printed table, and writes the strings out to
     * the <code>Graphics</code> object that is responsible for 
     * holding the printed data.  All parameters are called by Java
     * classes, specifically <code>PrintJob</code>, not explicitly in
     * our code
     *
     * @param pg Graphics object that holds the printed information
     * @param pageFormat PageFormat object that holds print dimensions
     * @param pageIndex number of current Page
     *
     * @return status of current call of print method
     */
    public int print(Graphics pg, PageFormat pageFormat, 
		     int pageIndex) throws PrinterException {
	// if pageIndex is beyond maximum page number, then
	// don't return
	if (pageIndex >= maxNumPage)
	    return NO_SUCH_PAGE;

	// set Graphics object to printed cartesian origin
	pg.translate((int)pageFormat.getImageableX(), 
		     (int)pageFormat.getImageableY());

	// get printable width and height of page
	int wPage = 0;
	int hPage = 0;
	if (pageFormat.getOrientation() == pageFormat.PORTRAIT) {
	    wPage = (int)pageFormat.getImageableWidth();
	    hPage = (int)pageFormat.getImageableHeight();

	    wPage = 550;
	    hPage = 950;
	}
	else {
	    wPage = (int)pageFormat.getImageableWidth();
	    wPage += wPage/2;
	    hPage = (int)pageFormat.getImageableHeight();
	    
	    wPage = 950;
	    hPage = 550;

	    pg.setClip(0,0,wPage,hPage);
	}

	// testcode
	//	Debug.println(wPage);
	//	Debug.println(hPage);

	// create int to keep track of vertical distance
	int y = 0;

	// set particular font and color for printing title of file
	pg.setColor(Color.black);
	Font fn = pg.getFont().deriveFont(Font.BOLD);
	FontMetrics fm = pg.getFontMetrics();

	// increment height for title
	y += fm.getAscent();

	// if file has no name, then print hard coded string
	if(getFile() == null)
	    pg.drawString("Untitled File", 0, y);
	else
	    pg.drawString(getFile().getName(), 0, y);

	y += 20; // space between title and table headers

//	Font headerFont = table.getFont().deriveFont(Font.BOLD);
//	pg.setFont(headerFont);
//	fm = pg.getFontMetrics();

	TableColumnModel colModel = table.getColumnModel();
	int nColumns = colModel.getColumnCount();
	int x[] = new int[nColumns];
	x[0] = 0;
        
	int h = fm.getAscent();
	y += h; // add ascent of header font because of baseline
	// positioning (see figure 2.10)
 
	int nRow, nCol;

	// define widths of each column
	for (nCol=1; nCol<nColumns; nCol++) {
	    TableColumn tk = colModel.getColumn(nCol);
	    int width = tk.getWidth();
	    // check if table width is within print width
	    if (x[nCol] + width > wPage) {
		nColumns = nCol;
		
		// show error one time if table width exceeds print width
		if(!columnOverflow) {
		    fileOpError("Print","Table width exceeds printed width,\nsome data will not be printed.");
		    columnOverflow = true;
		}

		break;
	    }
	    if (nCol+1<nColumns)
		x[nCol+1] = x[nCol] + width;
	    String title = (String)tk.getIdentifier();
	    //pg.drawString(title, x[nCol], y);
	}
	pg.setFont(table.getFont());
	fm = pg.getFontMetrics();
        
	// find out number of rows that can be printed on current page
	int header = y;
	h = fm.getHeight();
	int rowH = Math.max((int)(h*1.5), 10);
	int rowPerPage = (hPage-header)/rowH;
	maxNumPage = Math.max((int)Math.ceil(table.getRowCount()/
					     (double)rowPerPage), 1);

	// calculate initial and final rows for current page
	int iniRow = pageIndex*rowPerPage;
	int endRow = Math.min(table.getRowCount(), 
			      iniRow+rowPerPage);
        
	// iterate through each rows for this page
	for (nRow=iniRow+1; nRow<endRow; nRow++) {

	    y += h; // space between each row

	    // iterate through each column on the row, getting data of each
	    // cell and calling drawString with the data
	    for (nCol=1; nCol<nColumns; nCol++) {
		int col = table.getColumnModel().getColumn(nCol).getModelIndex();
		String str = tableModel.getCellAt(nRow, nCol).getValue().toString();
		// testcode
//		System.out.println(str);
		
		pg.setColor(Color.black);
		pg.drawString(str, x[nCol], y);
	    }
	}
	
	// explicit call on garbage collector to flush all temporary data
	// used, such as graphics objects
	System.gc();
	return PAGE_EXISTS;
    }


    /**
     * returns the file
     *
     * @return the file
     */
    public File getFile() {
	return file;
    }

    /**
     * has password?
     *
     * 
     */
    public boolean hasPassword() {
	return password != null && password.length() > 0;
    }
    
    /**
     * Set Password Dialog
     */
    public void setPassword() {
	PasswordDialog passwordDialog = new PasswordDialog(sharp, null);
	passwordDialog.setVisible(true);
	String text = passwordDialog.getValidatedText();
	if (text != null) {
	    setPasswordValue(text);
	    tableModel.setPasswordModified(true);
	}
	table.requestFocus();
    }

    /**
     * Verify Password Dialog
     */
    private boolean verifyPassword(String password) {

	PasswordDialog passwordDialog = new PasswordDialog(sharp, password);
	passwordDialog.pack();
	passwordDialog.setLocationRelativeTo(sharp);
	passwordDialog.setVisible(true);
	String text = passwordDialog.getValidatedText();
	table.requestFocus();
	return text != null;
    }
    
    /**
     * a function to display error messages
     *
     * @param op the operation that caused this error
     * @param error the error message
     */
    private void fileOpError(String op, String error) {    
        SharpOptionPane.showMessageDialog(sharp, error, op,
					  JOptionPane.ERROR_MESSAGE, null);
	table.requestFocus();
    }

    /**
     * This method is activated on the Keystrokes we are listening to
     * in this implementation. Here it listens for keystroke
     * ActionCommands.
     *
     * Without this listener, when we press certain keys the individual
     * cell will be activated into editing mode in addition to the
     * effect of the key accelerators we defined with menu items.
     * With this key listener, we avoid this side effect.
     */
    public void actionPerformed(ActionEvent e) {

	if (e.getActionCommand().compareTo("New")==0) {
	    newFile();
	}
	else if (e.getActionCommand().compareTo("OpenFile")==0) {
	    openFile();
	}
	else if (e.getActionCommand().compareTo("OpenDb")==0) {
	    openDb();
	}	
	else if (e.getActionCommand().compareTo("Save")==0) {
	    saveFile();
	}	
	else if (e.getActionCommand().compareTo("Print")==0) {
	    printData();
	}
	else if (e.getActionCommand().compareTo("Exit")==0) {
	    sharp.exit();
	}
    }

    /**
     * Opens document.  Queries user to save if modified state is modified.
     * Uses switch statement on user's choice to choose path of execution.
     *
     * @author Shiraz Kanga
     */
    public void openDb ()
    {
	int choice;

	// pop up querySave box (yes, no, cancel) and save choice
	// if table modified state is modified, else set choice
	// to JOptionPane.NO_OPTION which clears cells without
	if (tableModel.isModified ())
	    choice = queryForSave ();
	else
	    choice = JOptionPane.NO_OPTION;
	
	switch (choice) {
	case JOptionPane.YES_OPTION:
	    // if user chooses yes in dialog box, save document first
	    // before executing clear range
	    saveFile ();
	case JOptionPane.NO_OPTION:
	    Database db = new Database(sharp);
	    db.connectDb();
				 
	default:
	    // break out of loop for all choices.  action depends
	    // on entry point.
	    break;
	}
    }        
}

/**
 * This is the listener for "Recent Files" menu
 *
 * @author Hua Zhong
 */
class RecentFileListener implements ActionListener {
    File file;
    FileOp fileOp;
    
    RecentFileListener(File file, FileOp fileOp) {
	this.file = file;
	this.fileOp = fileOp;
    }

    public void actionPerformed(ActionEvent evt) {
	fileOp.openTableModel(file);
    }
}






