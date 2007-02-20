package sharptools;
/*
 * @(#)Histogram.java
 * 
 * $Id: Histogram.java,v 1.1 2007-02-20 13:58:21 hohenwarter Exp $
 * 
 * Created Novenmber 21, 2000, 11:27 PM
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This is a fully functional histogram class (including UI).
 *
 * A tabbed panel is used.  Multiple histograms can be managed simultaneously.
 * 
 * @author Hua Zhong
 * @version $Revision: 1.1 $
 */

public class Histogram extends JFrame implements ActionListener {

    private SharpTools sharp;
    private JTable table;
    private SharpTableModel model;
    private JTabbedPane tab;
    private int width;
    private int height;
    private String title;
    private Point location;
    private Dimension dim;
    final private ImageIcon histogramIcon = SharpTools.getImageIcon("chart.gif");
    
    //    private int selected = -1;    
    
    /**
     * Constructor:
     *
     * @param model the table model to read data from
     * @param title the title for the histogram window
     * @param width the initial widh
     * @param height the initial height
     */
    Histogram(SharpTools sharp, String title, int width, int height) {
	super(title);
	setSize(width, height);
	//	setIconImage(histogramIcon.getImage());

	this.sharp = sharp;
	this.table = sharp.getTable();
	this.model = sharp.getTableModel();
	this.title = title;
	this.width = width;
	this.height = height;

	tab = new JTabbedPane();

	// register toggle key
	table.registerKeyboardAction
	    (this, "Show",
	     KeyStroke.getKeyStroke(KeyEvent.VK_F8,0, false),
	     JComponent.WHEN_FOCUSED);

	// register add histogram key
	table.registerKeyboardAction
	    (this, "Add",
	     KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK, false),
	     JComponent.WHEN_FOCUSED);
	
	
	Container container = getContentPane();
	container.setLayout(new BorderLayout());

	container.add(tab, BorderLayout.CENTER);
	/*
	if (selected >= 0) {
	    selected = Math.min(selected, tab.getTabCount()-1);
	    tab.setSelectedIndex(selected);
	}
	*/
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // bring the dialog to the front; restore if minimized
    private void bringToFront() {
	if (getState() == Frame.ICONIFIED)
	    setState(Frame.NORMAL);
	
	toFront();
    }
    
    /**
     * Show histograms
     */
    public void show() {
	super.show();
	sharp.checkShowHistogramState();
    }

    /**
     * Hide histograms.
     */
    public void hide() {
	super.hide();
	sharp.checkShowHistogramState();
    }

    /**
     * Add a histogram to the tab panel
     *
     * @param title the title for the histogram
     * @param range the selected cell range
     */
    public void add(String title, CellRange range) {

	bringToFront();

	if (!isVisible())
	    show();

	TabPanel newTab = new TabPanel(model, range, this, tab);
	
	tab.addTab(title, null, newTab, range.toString());
	
	tab.setSelectedIndex(tab.getTabCount()-1);
	newTab.getOptions(true); // first time
    }

    /**
     * toggle the show/hide state
     */
    public void toggle() {
	if (isVisible())
	    hide();
	else if (hasChart()) {
	    //	    setSize(width, height);
	    bringToFront();
	    show();
	}
    }

    /**
     * Add Histogram
     */
    public void addHistogram() {
	//checks if anything is selected	
	if (table.getSelectedRowCount() != 0) { 
	    CellRange range = new CellRange
		(table.getSelectedRows(), table.getSelectedColumns());

	    String title = "Chart "+(tab.getTabCount()+1);
	    add(title, range);
	    
	    sharp.checkShowHistogramState();
	    
	} else {
            sharp.noCellsSelected("Histogram");
        }
    }        
        
    /**
     * Whether the histogram is currently having any defined charts
     *
     * @return true or false
     */
    public boolean hasChart() {
	return tab.getTabCount()>0;
    }

    /**
     * This method is activated on the Keystrokes we are listening to
     * in this implementation. Here it listens for "Show" ActionCommands.
     * Without this listener, when we press certain keys the individual
     * cell will be activated into editing mode in addition to the
     * effect of the key accelerators we defined with menu items.
     * With this key listener, we avoid this side effect.
     */
    public void actionPerformed(ActionEvent e) {

	if (e.getActionCommand().compareTo("Show")==0) {
	    toggle();
	}
	else if (e.getActionCommand().compareTo("Add")==0) {
	    addHistogram();
	}
    }

}

