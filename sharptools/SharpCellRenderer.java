package sharptools;
/*
 * SharpCellRenderer.java
 *
 * $Id: SharpCellRenderer.java,v 1.1 2007-02-20 13:58:21 hohenwarter Exp $
 *
 * Created on October 26, 2000, 9:23 PM
 */

import javax.swing.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.*;
import java.awt.Component;
import java.awt.Color;
import java.awt.Rectangle;

/** The class for rendering (displaying) individual Cell objects in a
 * <code>JTable</code>.
 * <p>
 * Because the DefaultTableCellRender is optimized for rendering objects
 * in JTable, this renderer extends that class.  Since the zeroth row and
 * column of the table are used as labels, SharpCellRender renders them
 * differently by have a gray background etc. SharpCellRenderer can
 * be changed to toggle between displaying the text and value of a formula
 * cell. This was done in a previous version but is not removed.
 * @author Ricky Chin
 * @version $Revision: 1.1 $
 */
public class SharpCellRenderer extends DefaultTableCellRenderer {

    /** 
     * Creates a SharpCellRenderer.
     */
    public SharpCellRenderer() {
	super();
    }

    // implements javax.swing.table.TableCellRenderer
    /**
     * Returns the default table cell renderer.
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at 
     *                                   <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus, int row, int column) {

        /* the following is the similar to DefaultTableCellRenderer */
	if (isSelected && !(isHeaderCell(row, column))) {
	   super.setForeground(table.getSelectionForeground());
	   super.setBackground(table.getSelectionBackground());
	}else {
	    /* set header cells background to grey and rest to default */
	    if (isHeaderCell(row, column)) {
		super.setBackground(Color.lightGray);
		super.setForeground(table.getForeground());
	    }else {
		super.setForeground(table.getForeground());
		super.setBackground(table.getBackground());
	    }
	}
	
	setFont(table.getFont());

	if (hasFocus) {
	    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
	    if (table.isCellEditable(row, column)) {
	        super.setForeground( UIManager.getColor(
					    "Table.focusCellForeground") );
	        super.setBackground( UIManager.getColor(
					     "Table.focusCellBackground") );
	    }
	} else {
	    setBorder(noFocusBorder);
	}

        /* this method has been changed for formula feature */
        setValue(value, isSelected, hasFocus, row, column); 

        //DefaulTableCellRenderer code
	// begin optimization to avoid painting background
	Color back = getBackground();
	boolean colorMatch = (back != null) && 
                    ( back.equals(table.getBackground()) ) && table.isOpaque();
        setOpaque(!colorMatch);
	// end optimization to aviod painting background

	return this;
    }
    
    /**
     * Sets the string for the cell being rendered to <code>value</code>.
     * @param value  the string value for this cell; if value is
     * 		<code>null</code> it sets the text value to an empty string
     * @param hasFocus whether cell has focus or not
     * @param isSelected whether cell is selected
     * @param row cell row
     * @param column cell column
     */
    protected void setValue(Object value, boolean hasFocus,
                                     boolean isSelected, int row, int column) {
        if (value instanceof Cell) {
            //we only care about the value, not the formula string
            Cell temp = (Cell)value;
	    Object data = temp.getValue();
            
	    if (isHeaderCell(row, column)) {
                //label cells are center aligned
		setHorizontalAlignment(JTextField.CENTER);
	    }else {
                if (data instanceof Number) {
                    //numbers are right justified
		    setHorizontalAlignment(JTextField.RIGHT);
                }else {
                    //everything else is left justified
		    setHorizontalAlignment(JTextField.LEFT);
	        }
            }
            
            //value to display in table
	    setText((data == null) ? "" : data.toString());
        }
        else {
            //not cell object so render with toString of that object
            setText((value == null) ? "" : value.toString());
        }
    }

    /**
     * Header cells are special.
     *
     * @param row the int value of the row
     * @param column the int value of the column
     * @return true if row = column = 0.
     */    
    protected boolean isHeaderCell(int row, int column) {
	return (/*row == 0 || */column == SharpTools.baseCol-1);
    }
}
