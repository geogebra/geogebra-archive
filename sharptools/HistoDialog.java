package sharptools;
/*
 * @(#)HistoDialog.java
 * 
 * $Id: HistoDialog.java,v 1.1 2007-02-20 13:58:21 hohenwarter Exp $
 * 
 * Created Novenmber 23, 2000, 2:55 PM
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This is used to provide options configuration for a histogram
 *
 * @author Hua Zhong
 * @version $Revision: 1.1 $
 */

public final class HistoDialog extends SharpDialog {

    private JFrame owner;
    private JTextField title;
    private AddressField startCell;
    private AddressField endCell;
    private NumberField startValue;
    private NumberField endValue;
    private NumberField bucket;
    private NumberField xMin;
    private NumberField xMax;
    private NumberField xUnit;
    private NumberField yMin;
    private NumberField yMax;
    private NumberField yUnit;
    private JRadioButton percentageButton; // radio button for "Percentage"
    private JRadioButton countButton;
    final private ImageIcon histogramIcon32 = SharpTools.getImageIcon ("chart32.gif");

    /**
     * construct a modal dialog
     *
     * @param aFrame the parent frame
     */
    HistoDialog(JFrame aFrame) {
	super(aFrame, "Histogram Options", true);

	owner = aFrame;

	// initialize all the components
	title = new JTextField(10);
	startCell = new AddressField(5);
	endCell = new AddressField(5);
	startValue = new NumberField(5);
	endValue = new NumberField(5);
	bucket = new NumberField(5, true);
	xMin = new NumberField(5);
	xMax = new NumberField(5);
	xUnit = new NumberField(5, true);
	yMin = new NumberField(5);
	yMax = new NumberField(5);
	yUnit = new NumberField(5, true);

	// set tooltip text
	startCell.setToolTipText("Start cell");
	endCell.setToolTipText("End cell");
	startValue.setToolTipText("Start value");
	endValue.setToolTipText("End value");
	xMin.setToolTipText("Minumum value on X axis");
	xMax.setToolTipText("Maximum value on X axis");
	xUnit.setToolTipText("Unit on X axis");
	yMin.setToolTipText("Minumum value on Y axis");
	yMax.setToolTipText("Maximum value on Y axis");
	yUnit.setToolTipText("Unit on Y axis");
	
	/*
	 * All the following crap is laying out the component.
	 * Nothing interesting.
	 */
	
	JPanel east = new JPanel();///new GridLayout(0, 1, 5, 5));
	east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
	east.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));

	// title
	JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel label = new JLabel("Chart Title: ");
	label.setLabelFor(title);
	label.setDisplayedMnemonic(KeyEvent.VK_T);
	textPanel.add(label);
	textPanel.add(title);
	
	east.add(textPanel);

	// separator
	textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	textPanel.add(new JSeparator());
	//	textPanel.setBorder(BorderFactory.createEtchedBorder());
	east.add(textPanel);
	
	// Cell Range
	textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
	label = new JLabel("Cell Range: ");
	label.setLabelFor(startCell);
	label.setDisplayedMnemonic(KeyEvent.VK_C);

	textPanel.add(label);
	textPanel.add(startCell);

	textPanel.add(new JLabel(":"));
	textPanel.add(endCell);

	east.add(textPanel);

	// Value Range
	textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	label = new JLabel("Value Range: ");
	label.setLabelFor(startValue);
	label.setDisplayedMnemonic(KeyEvent.VK_V);

	textPanel.add(label);
	textPanel.add(startValue);

	label = new JLabel(" to ");
	label.setLabelFor(endValue);
	textPanel.add(label);
	textPanel.add(endValue);

	east.add(textPanel);

	// Bucket Size
	label = new JLabel("   Bucket ");
	label.setLabelFor(bucket);
	label.setDisplayedMnemonic(KeyEvent.VK_B);
	textPanel.add(label);
	textPanel.add(bucket);
	
	east.add(textPanel);

	// separator
	textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	textPanel.add(new JSeparator());
	//	textPanel.setBorder(BorderFactory.createEtchedBorder());
	east.add(textPanel);
	
	// X Scale
	textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	

	label = new JLabel("X Scale: ");
	label.setLabelFor(xMin);
	label.setDisplayedMnemonic(KeyEvent.VK_X);

	textPanel.add(label);
	textPanel.add(xMin);

	label = new JLabel(" to ");
	label.setLabelFor(xMax);
	textPanel.add(label);
	textPanel.add(xMax);

	label = new JLabel("   Unit ");
	label.setLabelFor(xUnit);
	label.setDisplayedMnemonic(KeyEvent.VK_U);
	textPanel.add(label);
	textPanel.add(xUnit);

	east.add(textPanel);

	// Y Scale
	textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	label = new JLabel("Y Scale: ");
	label.setLabelFor(yMin);
	label.setDisplayedMnemonic(KeyEvent.VK_Y);

	textPanel.add(label);
	textPanel.add(yMin);

	label = new JLabel(" to ");
	label.setLabelFor(yMax);
	textPanel.add(label);
	textPanel.add(yMax);

	label = new JLabel("   Unit ");
	label.setLabelFor(yUnit);
	label.setDisplayedMnemonic(KeyEvent.VK_N);
	textPanel.add(label);
	textPanel.add(yUnit);
	
	east.add(textPanel);

	// separator
	textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	textPanel.add(new JSeparator());
	east.add(textPanel);

	// Radio buttons	
	textPanel = new JPanel();
	textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

	// add radio buttons
	ButtonGroup metric = new ButtonGroup();
	percentageButton = new JRadioButton("Percentage");
	percentageButton.setMnemonic(KeyEvent.VK_P);
	//	radio.setActionCommand("Count");
	
	metric.add(percentageButton);

	label = new JLabel("Display Y by ");
	textPanel.add(label);
	
	textPanel.add(percentageButton);
	//	percentageButton.setSelected(true);

	countButton = new JRadioButton("Count");
	countButton.setMnemonic(KeyEvent.VK_O);
	
	metric.add(countButton);
	textPanel.add(countButton);
	
	east.add(textPanel);

	
	setOptionPane(east,
		      JOptionPane.PLAIN_MESSAGE,
		      JOptionPane.OK_CANCEL_OPTION,
		      histogramIcon32);

	// this is OK; onClose() will not be called
	// but choice's default value is CLOSED_OPTION
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	startCell.requestFocus();	
	
    }

    // strings defined here
    final static private String bucketstring = "Bucket Size";
    final static private String startvaluestring = "Start Value";
    final static private String endvaluestring = "End Value";
    final static private String xunitstring = "X Unit";
    final static private String yunitstring = "Y Unit";
    final static private String xminstring = "X Start";
    final static private String xmaxstring = "X End";
    final static private String yminstring = "Y Start";
    final static private String ymaxstring = "Y End";

    /**
     * Validates the input - called when the OK button is pressed
     *
     * @return whether the input is valid
     */
    private boolean validateInput() {
	/*
	 * get values from text fields and check the validity,
	 * give friendly error report
	 */
	if (!addressOK(startCell, "Start Cell"))
	    return false;

	if (!addressOK(endCell, "End Cell"))
	    return false;
	
	if (!orderOK(startValue, endValue, startvaluestring, endvaluestring))
	    return false;
	
	if (!orderOK(xMin, xMax, xminstring, xmaxstring))
	    return false;
	
	if (!orderOK(yMin, yMax, yminstring, ymaxstring))
	    return false;
	
	if (isNegative(bucket, bucketstring))
	    return false;

	if (isNegative(xUnit, xunitstring))
	    return false;

	if (isNegative(yUnit, yunitstring))
	    return false;

	return true;
    }

    /**
     * Check the NumberField can't contain a negative number
     *
     * @param field the NumberField component
     * @param name used for error reporting
     * @return whether the input is a negative number
     */
    private boolean isNegative(NumberField field, String name) {
	Float f = field.getNumber();
	if (f == null || f.floatValue() <= 0) {
	    invalidInput(name);
	    field.selectAll();
	    field.requestFocus();
	    return true;
	}
	return false;
    }
    
    /**
     * Make sure first value is lower than the second's and both have valid input
     *
     * @param start the first number field
     * @param end the second number field
     * @param startname the name of the first field
     * @param endname the name of the second field
     * @return whether the order is correct
     */
    private boolean orderOK(NumberField start, NumberField end,
			    String startname, String endname) {
	// calculate
	Float f1 = start.getNumber();

	if (f1 == null) {
	    invalidInput(startname);
	    start.selectAll();
	    start.requestFocus();
	    return false;
	}
	
	Float f2 = end.getNumber();

	if (f2 == null) {
	    invalidInput(endname);
	    end.selectAll();
	    end.requestFocus();
	    return false;
	}
	
	if (f1.floatValue() > f2.floatValue()) {
	    // exchange	    
	    start.setNumber(f2);
	    end.setNumber(f1);
	}

	return true;
    }

    /**
     * Make sure AddressField valid input
     *
     * @param field the address text field
     * @param name the name of this field
     * @return whether the input is a valid address
     */
    private boolean addressOK(AddressField field, String name) {
	// calculate
	CellPoint addr = field.getAddress();

	if (addr == null) {
	    invalidInput(name);
	    field.selectAll();
	    field.requestFocus();
	    return false;
	}
	
	return true;
    }

    /*
     * Set the Title textfield
     * @param s the title
     */
    public void setTitleField(String s) {
	title.setText(s);
	setTitle("Options - "+s);
    }

    /**
     * Get the Title textfield
     *
     * @return the new title
     */
    public String getTitleField() {
	return title.getText();
    }

    /**
     * Set the cell range
     *
     * @param range the new cell range
     */
    public void setCellRange(CellRange range) {
	if (range != null) {
	    startCell.setAddress(range.getminCorner());
	    endCell.setAddress(range.getmaxCorner());
	}
    }

    /**
     * Get the cell range
     *
     * @return the range that user input
     */
    public CellRange getCellRange() {
	CellPoint point1 = startCell.getAddress();
	CellPoint point2 = endCell.getAddress();
	if (point1 == null || point2 == null)
	    return null;
	else {
	    // correct problems like "A2:B1".  Always convert to "A1:B2"
	    int minrow = Math.min(point1.getRow(), point2.getRow());
	    int maxrow = Math.max(point1.getRow(), point2.getRow());
	    int mincol = Math.min(point1.getCol(), point2.getCol());
	    int maxcol = Math.max(point1.getCol(), point2.getCol());
	    
	    return new CellRange(minrow, maxrow, mincol, maxcol);
	}
    }

    // many get/set functions
    public CellPoint getStartCell() { return startCell.getAddress(); }
    public void setStartCell(CellPoint addr) { startCell.setAddress(addr); }
    
    public CellPoint getEndCell() { return endCell.getAddress(); }    
    public void setEndCell(CellPoint addr) { endCell.setAddress(addr); }

    public Float getStartValue() { return startValue.getNumber(); }
    public void setStartValue(Float f) { startValue.setNumber(f); }
    
    public Float getEndValue() { return endValue.getNumber(); }
    public void setEndValue(Float f) { endValue.setNumber(f); }

    public Float getBucket() { return bucket.getNumber(); }
    public void setBucket(Float f) { bucket.setNumber(f); }

    public Float getXMin() { return xMin.getNumber(); }
    public void setXMin(Float f) { xMin.setNumber(f); }

    public Float getXMax() { return xMax.getNumber(); }
    public void setXMax(Float f) { xMax.setNumber(f); }

    public Float getXUnit() { return xUnit.getNumber(); }
    public void setXUnit(Float f) { xUnit.setNumber(f); }

    public Float getYMin() { return yMin.getNumber(); }
    public void setYMin(Float f) { yMin.setNumber(f); }

    public Float getYMax() { return yMax.getNumber(); }
    public void setYMax(Float f) { yMax.setNumber(f); }

    public Float getYUnit() { return yUnit.getNumber(); }
    public void setYUnit(Float f) { yUnit.setNumber(f); }

    public boolean getByPercentage() { return percentageButton.isSelected(); }
    public void setByPercentage(boolean set) {
	percentageButton.setSelected(set);
	countButton.setSelected(!set);
    }

    /**
     * Pop up an error message
     *
     * @param s the name of the field with invalod input
     */
    private void invalidInput(String s){
	SharpOptionPane.showMessageDialog(owner,
					  "Sorry, "+s+" does not have a valid value.  Please go back to check it.",
					  "Histogram",
					  JOptionPane.ERROR_MESSAGE, null);
    }

    protected boolean onOK() {
	// validate the input here
	return validateInput();
    }
}

