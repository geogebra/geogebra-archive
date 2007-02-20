package sharptools;
/*
 * @(#)TabPanel.java
 * 
 * $Id: TabPanel.java,v 1.1 2007-02-20 13:58:20 hohenwarter Exp $
 * 
 * Created Novenmber 27, 2000, 11:27 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A class for one panel, which includes the image panel and
 * all the control components.  In short, this is everything
 * for one chart.
 *
 * @author Hua Zhong
 * @version $Revision: 1.1 $
 */
public class TabPanel extends JPanel {

    private Histogram owner;
    private SharpTableModel model;
    private JTabbedPane tab;
    private HistoPanel histo;
    
    // values
    private CellRange range;
    private Float startvalue, endvalue, bucketvalue,
	xmin, xmax, ymin, ymax, xunit, yunit;
    private boolean bypercentage = true;    

    /**
     * Constructor
     *
     * @param tableModel the SharpTableModel as backend data
     * @param range the selected cell range
     * @param frame the parent frame
     * @param pane the JTabbedPane object that this panel belongs to
     */
    TabPanel(SharpTableModel tableModel, CellRange range,
	     Histogram frame, JTabbedPane pane) {
	super();
	this.owner = frame;
	this.model = tableModel;
	this.tab = pane;
	this.range = range;
	
	// set layout - tedious..
	setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	setLayout(new BorderLayout());

	// add the HistoPanel - our canvas
	histo = new HistoPanel();
	histo.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	add(histo, BorderLayout.CENTER);	

	// buttons
	JPanel south = new JPanel();
	south.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
	south.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

	// Remove button
	JButton closeButton = new JButton("Remove", SharpTools.getImageIcon("no.gif"));
	closeButton.setMnemonic(KeyEvent.VK_R);
	closeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    // remove the current tab
		    tab.remove(tab.getSelectedComponent());
		    
		    // if there is no tab left, close the window
		    if (tab.getTabCount() == 0) {
			owner.hide();
		    }
		}
	    });

	// Options button
	JButton optionButton = new JButton("Options...", SharpTools.getImageIcon("options.gif"));	
	optionButton.setMnemonic(KeyEvent.VK_O);
	optionButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    getOptions(false);		    
		}
	    });

	// Update button
	JButton updateButton = new JButton
	    ("Update", SharpTools.getImageIcon("refresh.gif"));

	updateButton.setMnemonic(KeyEvent.VK_U);
	updateButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    updateData();
		    histo.paintImmediately(0, 0, histo.getWidth(),
					   histo.getHeight());
		}
	    });
	
	south.add(updateButton);
	south.add(optionButton);
	south.add(closeButton);
	
	add(south, BorderLayout.SOUTH);
    }    

    /**
     * Parameter configuration for this histogram
     *
     * @param firstTime whether this is the first time to call the dialog
     */
    void getOptions(boolean firstTime) {

	HistoDialog option = new HistoDialog(owner);
	option.pack();
	option.setLocationRelativeTo(owner);

	// title textfield
	option.setTitleField(tab.getTitleAt(tab.getSelectedIndex()));
	
	if (firstTime) {
	    // set initial values
	    option.setCellRange(range);

	    float[] minmax = TabPanel.getValueMinMax(model, range);
	    int min = (int)minmax[0];
	    int max = (int)minmax[1];

	    startvalue = new Float(min);	    
	    endvalue = new Float(max);	    

	    // estimate a reasonable bucket size
	    int power = 0;
	    if (max>min)
		power = (int)((Math.log(max-min)/Math.log(10)));
	    if (power<0)
		power--;
	    
	    bucketvalue = new Float(Math.pow(10, power));
	    xunit = bucketvalue;

	    xmin = new Float(min);
	    xmax = new Float(max);
	    
	    ymin = new Float(0);
	    ymax = new Float(100);
	    yunit = new Float(10);

	}

	// initialize all the fields
	option.setCellRange(range);
	option.setStartValue(startvalue);
	option.setEndValue(endvalue);
	option.setBucket(bucketvalue);
	option.setXMin(xmin);
	option.setXMax(xmax);
	option.setXUnit(xunit);
	option.setYMin(ymin);
	option.setYMax(ymax);
	option.setYUnit(yunit);
	option.setByPercentage(bypercentage);

	// show the dialog
	option.setVisible(true);

	if (!option.isCancelled()) {

	    // save data
	    tab.setTitleAt(tab.getSelectedIndex(),
			   option.getTitleField());
	    range = option.getCellRange();
	    startvalue = option.getStartValue();
	    endvalue = option.getEndValue();
	    bucketvalue = option.getBucket();
	    xmin = option.getXMin();
	    xmax = option.getXMax();
	    ymin = option.getYMin();
	    ymax = option.getYMax();
	    xunit = option.getXUnit();
	    yunit = option.getYUnit();
	    bypercentage = option.getByPercentage();

	    // do an update to repaint the chart
	    update();
	}
    }
    
    /**
     * get the min and max values from the cell range
     *
     * @param model the backend table data source
     * @param range the range of cells
     * @return two float numbers, first is min and second is max
     */
    static float[] getValueMinMax(SharpTableModel model, CellRange range) {
	// value[0] is min, value[1] is max
	float[] values = { Float.MAX_VALUE, Float.MIN_VALUE };
	float v;
	for (int i = range.getStartRow(); i <= range.getEndRow(); i++)
	    for (int j = range.getStartCol(); j <= range.getEndCol(); j++) {
		try {
		    v = model.getNumericValueAt(i, j).floatValue();
		}
		catch (ParserException e) {
		    v= 0;
		}

		values[0] = Math.min(values[0], v);
		values[1] = Math.max(values[1], v);
	    }

	return values;
    }

    /**
     * Update everything of the histogram.
     *
     * This is called after we change options by the Option dialog.
     */
    public void update() {

	float start = startvalue.floatValue();
	float end = endvalue.floatValue();
	float bucket = bucketvalue.floatValue();
	float nb = (end-start)/bucket;
	int nbars = (int)nb;
	if (nbars > (int)nbars)
	    nbars++;

	// values below and above
	nbars += 2;

	float[] data = new float[nbars];
	histo.setData(data);

	updateData();
	
	histo.setStartEndPoints(start, end, bucket);
	histo.setXYAxis(xmin.floatValue(),
			xmax.floatValue(),
			xunit.floatValue(),
			ymin.floatValue(),
			ymax.floatValue(),
			yunit.floatValue());

	histo.setByPercentage(bypercentage);
	histo.paintImmediately(0, 0, histo.getWidth(), histo.getHeight());
	
    }

    /**
     * Read values from textfields and update the histogram's data;
     * Nothing else.
     *
     * This is called from the "Update" button.
     */
    private void updateData() {

	float start = startvalue.floatValue();
	float end = endvalue.floatValue();
	float bucket = bucketvalue.floatValue();
	float[] data = histo.getData();
	
	for (int k = 0; k < data.length; k++)
	    data[k] = 0;
	
	// two more buckets below start or above end
	//	float scale = end-start+2;

	for (int i = range.getStartRow(); i <= range.getEndRow(); i++) {
            for (int j = range.getStartCol(); j <= range.getEndCol(); j++) {
		
		float value = 0;

		try {
		    value = model.getNumericValueAt(i, j).floatValue();
		}
		catch (Exception e) {
		}
		
		if (value < start)
		    data[0]++;
		else if (value > end)
		    data[data.length-1]++;
		else {
		    int index = (int)((value-start)/bucket)+1;
		    data[index]++;
		}
	    }
	}

	if (bypercentage) {
	    int nums = range.getWidth()*range.getHeight();
	    for (int k = 0; k < data.length; k++)
		data[k] = data[k]*100/nums;
	}
	// histo.setData(data); - dont need it since we've modified in-place
    }
}

/**
 *
 * The is the panel to draw histogram on.
 *
 * One thing to pay attention to is the virtual coordinate we are using.
 * Virtual coordinate is the coordinate used as in the histogram.
 * The differences between a virtual coordinate and the Java coordinate system
 * are:
 * 1) Y axis direction is different
 * 2) We take border into account for virtual coordinate (so it has a smaller
 *    scale).
 * 
 * @author Hua Zhong
 * @version $Revision: 1.1 $
 */

class HistoPanel extends JPanel /*implements ComponentListener*/ {    

    private float[] bars; // distribution
    private float start, end, bucket;
    private float xmin, xmax, xunit;
    private float ymin, ymax, yunit;
    private float xscale, yscale; // xmax-xmin and ymax-ymin
    
    private int xsize, ysize;
    private int xborder = 50;
    private int yborder = 40;
    private boolean bypercentage;
    //    private int maxdigits;

    public void setStartEndPoints(float start, float end, float bucket) {
	this.start = start;
	this.end = end;
	this.bucket = bucket;
    }

    public void setXYAxis(float xmin, float xmax, float xunit,
			  float ymin, float ymax, float yunit) {
	this.xmin = xmin;
	this.xmax = xmax;
	this.xunit = xunit;
	this.ymin = ymin;
	this.ymax = ymax;
	this.yunit = yunit;

	xscale = xmax-xmin;
	yscale = ymax-ymin;

	// calculate optimized xborder
	int len1 = String.valueOf(ymin).length();
	int len2 = String.valueOf(ymax).length();
	int len3 = String.valueOf(yunit).length();

	int maxdigits = Math.max(Math.max(len1, len2), len3);
	xborder = maxdigits*getGraphics().getFontMetrics().charWidth('0')+10;
	
    }

    public void setByPercentage(boolean bypercentage) {
	this.bypercentage = bypercentage;
    }
    
    public void setData(float[] data) {
	bars = data;
    }

    public float[] getData() {
	return bars;
    }
    
    /**
     * This is the overriden method of customized drawing
     *
     * @param g the Graphics object
     */
    public void paintComponent(Graphics g) {
	super.paintComponent(g);	

	if (bars == null)
	    return;
	
	xsize = getXSize();
	ysize = getYSize();

	// draw x axise
	if (xsize < 0 || ysize < 0)
	    return;

	float xratio = xscale/xsize;
	float yratio = yscale/ysize; // xscale/xsize; yscale/ysize
	
	// x axis
	drawLine(g, 0, 0, xsize+20, 0);
	// y xais
	drawLine(g, 0, 0, 0, ysize+20);

	if (bypercentage)
	    cString(g, "(%)", 0, ysize+30);

	// draw x ticks
	for (float x = xmin; x <= xmax; x+=xunit) {
	    int xcoor = (int)((x-xmin)/xratio);
	    drawLine(g, xcoor, +3, xcoor, -3);
	    cString(g, String.valueOf(x), xcoor, -20);
	}

	// draw y ticks
	for (float y = ymin; y <= ymax; y+=yunit) {
	    int ycoor = (int)((y-ymin)/yratio);
	    drawLine(g, -3, ycoor, +3, ycoor);
	    rString(g, String.valueOf(y), -5, ycoor);
	}
	
	// draw bars!
	// int barwidth = (int)(bucket/xratio);
	for (int i = 0; i < bars.length; i++) {
	    //	for (float x = start-bucket; x < end+bucket; x+=bucket) {
	    float x = start+(i-1)*bucket;
	    int ycoor = (int)((bars[i]-ymin)/yratio);
	    
	    int xcoor1 = (int)((x-xmin)/xratio);
	    int xcoor2 = (int)((x+bucket-xmin)/xratio);
	    

	    // must be in range
	    xcoor1 = Math.max(xcoor1, 0);
	    xcoor2 = Math.min(xcoor2, xsize);

	    if (i == 0)
		xcoor1 = 0;

	    if (i == bars.length-1)
		xcoor2 = xsize;
	    
	    if (xcoor1 >= xcoor2)
		continue;

	    // draw the bar
	    drawLine(g, xcoor1, 0, xcoor1, ycoor);
	    
	    drawLine(g, xcoor2, 0, xcoor2, ycoor);
	    
	    drawLine(g, xcoor1, ycoor, xcoor2, ycoor);

	}
	
    }

    private int getXSize() {
	return getWidth()-2*xborder;
    }
    
    private int getYSize() {
	return getHeight()-2*yborder;
    }

    // draw a line between virtual coordinates (x1, y1) and (x2, y2)
    // borders are taken into account and Y axis is upwards.
    private void drawLine(Graphics g, int x1, int y1, int x2, int y2) {

	// draw axis
	g.drawLine(x1+xborder, ysize-y1+yborder,
		   x2+xborder, ysize-y2+yborder);
    }

    // draw a string at virtual coordinate (x, y)
    // borders are taken into account and Y axis are up.
    private void drawString(Graphics g, String s, int x, int y) {

	// draw axis
	g.drawString(s, x+xborder, ysize-y+yborder);
    }
    
    
    // do some internal coordinate translation
    
    // coordinate translation for X (to virtual coordinate)
    private int getx(int x) {
	return x+xborder;
    }
    
    // coordinate translation for Y (to virtual coordinate)
    private int gety(int y) {
	return ysize-y+yborder;
    }

    // display a string by center alignment
    private void cString(Graphics g1, String s, int i, int j) {
        int k = g1.getFontMetrics().stringWidth(s);
        drawString(g1, s, i - k / 2, j);
    }

    // display a string by right alignment
    private void rString(Graphics g1, String s, int i, int j) {
        int k = g1.getFontMetrics().stringWidth(s);
        drawString(g1, s, i - k, j);
    }

}

