package sharptools;
/*
 * @(#)Cell.java
 *
 * $Id: Cell.java,v 1.1 2007-02-20 13:58:21 hohenwarter Exp $
 *
 * Created on October 14, 2000, 6:23 PM
 */

import java.util.*;

/** 
 * This is a wrapper class for all objects in the table.
 * <P>
 * A cell has a value which is either text or a number.
 * If it is a formula cell it also has an expression associated with it.
 * In addition, a cell has a list of all cells that reference it.
 *
 * @author Ricky Chin
 * @version $Revision: 1.1 $
 */

public class Cell {

    /** The integer code that denotes the cell holds text. */
    public static final int TEXT = 0;

    /** The integer code that denotes the cell holds numbers. */
    public static final int NUMBER = 1;

    /** The integer code that denotes the cell holds a formula. */
    public static final int FORMULA = 2;

    /** Value of the cell. In a formula, value holds the evaluated value. */
    private Object value;
    
    /**
     * This expression contains the string and internal representation
     * of the formula if it exists.
     */
    private Formula expression;

    /**
     *  The list of cells that reference this cell. When a cell is changes,
     * all cells on this list should notified.
     */
    private TreeSet refs;

    /** Creates an empty cell */
    public Cell() {
        value = null;
        expression = null;
        refs = null;
    }

    /** 
     * This constructor constructs a cell that will not have cells
     * referencing it and are not formulas. Basically only used for creating
     * the row and column labels.
     * <P>
     * <b>Warning:</b> Do not use this with Formulas
     * @param datum the value of the "label" cell (not a Formula)
     */
    public Cell(Object datum) {
        value = datum;
        expression = null;
        refs = null;
    }
        
    /** 
     * The constructor creates a new data cell which is not a formula.
     * <P>
     * <b>Note:</b> A normal cell's data is stored in value field
     * but expression (i.e. formula) field is null.
     * @param datum the value of the cell (text or number)
     * @param reference the list of cells that reference this one (can be null)
     */
    public Cell(Object datum, TreeSet reference) {
        value = datum;
        expression = null;
        refs = reference;
    }
    
    /** 
     * This version of the constructor constructs a formula cell.
     *
     * @param thing internal represenation of the formula
     * @param eVal the evaluated value of the formula (thing)
     * @param reference the list of cells that reference this one (can be null)
     */
    public Cell(Formula thing, Object eVal, TreeSet reference) {
        expression = thing;
        value = eVal;
        refs = reference;
    }

    /**
     * This method returns the formula associated with the cell or null if it
     * does not exist.
     * 
     * @return the formula (string and internal object) or null if does not
     * exist
     */
    public Formula getFormula() {
        return expression;
    }

    

    /** 
     * If it is a data cell, it returns the data of the cell. If it is a
     * formula, it returns the previously evaluated value of the formula.
     * 
     * @return the value (data or evaluated) of the cell
     */
    public Object getValue() {
        return value;
    }

    
    /** 
     * Sets the value field of the cell.
     * 
     * @param datum the object to set the value of cell to
     */
    public void setValue(Object datum) {
        value = datum;
    }

    /**
     * This method changes the cell to a data cell with value datum. This
     * method useful because changing the value does not affect the value of
     * the reference list.
     *
     * <b>Warning</b>: If the cell is going to be set to a formula, you must
     * use setFormula() because this present method sets the associated formula
     * field, expression, to null.
     * 
     * @param datum the new value of cell
     */
    public void setData(Object datum) {
        value = datum;
        expression = null;
    }  

    /**
     * This method sets the cell to be a formula cell. It puts the formula
     * object into the expression field. The Table of Cells is responsible
     * for recalculating and setting the appropriate value in the value
     * field of this cell.
     * 
     * @param form the internal representation of formula to set this cell to
     */
    public void setFormula(Formula form) {
        expression = form;
    }

    /** Returns true if cell at specified position is empty.
     * @return true if empty, false is not
     */
    public boolean isEmpty() {
	if(value.equals("") && expression == null)
	    return true;
	else
	    return false;
    }
        
    /**
     * This method returns true if there are cells that reference this one.
     * 
     * @return true only if there are cells that reference this one
     */
    public boolean hasRefs() {
        return (refs != null && !refs.isEmpty());
    }

    /**
     * This method gets the list of cells that reference this one.
     * This method should be used after the cell's value has been changed
     * to find out which cells need to be updated.
     * 
     * @return all cells that reference this cell
     */
    public TreeSet getRefs() {
	return refs;
    }

    /**
     *  This method removes a cell from reference list.
     * 
     * @param reference the cell to be removed from the reference list
     */ 
    public void removeRef(CellPoint reference) {
	//	System.out.println("remove ref "+reference+" from "+this);
	if (refs != null) {
	    refs.remove(reference);
	    if (refs.isEmpty()) refs = null;
	}
    }

    /**
     *  This method adds a cell to the dependency list.
     * 
     * @param reference a new cell that references this one that needs to be
     * added to the reference list
     */
    public void addRef(CellPoint reference) {
	//	System.out.println("add ref "+reference+" to ("+this);
        if (refs == null) {// check if refs is initiated
            refs = new TreeSet();
        }
        refs.add(reference);   
    }

    /** 
     * This method is useful for determining what information a cell holds. To
     * check if a cell holds a certain type just see if
     * getType() == Cell.CODE where CODE is any of the cell constants.
     *
     * @return the integer code of the type of data this cell holds
     */
    public int getType() {
        if (expression != null) return Cell.FORMULA;
        if (value instanceof Number) return Cell.NUMBER;
        return Cell.TEXT;
    }
   
    /** 
     * This method returns true IFF it is a formula cell
     *
     * @return true iff a formula cell
     */
    public boolean isFormula() {
        return (expression != null);
    }
    
    /**
     * This method determines a cell is a formula cell that has a error.
     * 
     * @return true if cell is an error cell
     */
    public boolean isErrorCell() {
        return ((isFormula()) && (value instanceof ParserException));
    }
    
    /**
     * This is a method similar to compareTo except that it is not
     * consistent with the equals method. It is used for sorting.
     * Cells are rated in increasing order: blank, error, string,
     * number (including formulas). If two cells are blank or errors
     * then they are "equal" by this compare method. Numbers and
     * strings are compared the usual way.
     * 
     * @param x cell to compare this to
     * @return -1 if this < x, 0 if this = x, 1 if this > x
     */
    public int compare(Cell x, boolean ascending) {
        //if this is blank which is lowest value
        if (this.value.equals("")) {
            //x is not blank so this is less than
            if (!x.value.equals("")) {
                if (ascending) {
                    return 1;
                } else {
                    return -1;
                }
            } else {               //otherwise equal
                return 0;
            }
        } else {
            if (x.value.equals("")) {
                if (ascending) {
                    return -1;
                }else {
                    return 1;
                }
            } else {               //both have values
                if (this.isErrorCell()) {
                    if (x.isErrorCell()) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    if (x.isErrorCell()) {
                        return 1;
                    } else {
                        if (this.getType() == Cell.TEXT) {
                            if (x.getType() == Cell.TEXT) {
                                String temp = (String)this.value;
                                return temp.compareToIgnoreCase((String)x.value);
                            } else {
                                return -1;
                            }
                        } else {
                            if (x.getType() == Cell.TEXT) {
                                return 1;
                            } else {
                                Float first = (Float)this.value;
                                Float second = (Float)x.value;
                                return first.compareTo(second);
                            }
                        }
                    }
                }
            }
        }
    } 
    
    public String toString() {
        
        if (expression != null)
            return "="+expression.toString();
	else
	    return value.toString();
    }
}

