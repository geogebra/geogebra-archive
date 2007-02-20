package sharptools;
/*
 * @(#)History.java
 *
 * $Id: History.java,v 1.1 2007-02-20 13:58:21 hohenwarter Exp $
 *
 * Created on November 15, 2000, 10:38 PM
 */
import java.util.*;
import javax.swing.*;

/** This is the class to support nearly-arbitrary undo/redo operations.
 * <p>
 * It's based on clipboard.  Each time a range of cells being changed,
 * they are saved in a clipboard and the clibboard is added to a linked
 * list.  A <b>current</b> pointer is maintained.
 * <ol>
 * <li> When a new clipboard is added, the objects after <b>current</b> are deleted
 *    and the new clipboard is added to the list and <b>current</b> is updated to
 *    point to the this new clipboard.</li>
 * <li> To undo, take out the object that <b>current</b> points to, and paste to the
 *    table.  But before the paste the table data is saved to replace the
 *    <b>current</b> clipboard.  Then move <b>current</b> one step backward</li>
 * <li> To redo, move current a step forward, take out the object <b>current</b>
 *    points to, and paste to the table.  But before the paste the table data
 *    is saved to replace the <b>current</b> clipboard.</li>
 * <li> Row/Column insertion/deletion are specially treated.<li>
 *</ol>
 *
 * @author Hua Zhong <huaz@cs.columbia.edu>
 * @version $Revision: 1.1 $
 */
public class History {

/** 
 * This is the doubly linked list class that undo/redo is implemented with.
 * It has a type, an object and two pointers (prev, next).
 */
    class ListNode {

	private ListNode prev; // points to previous node
	private ListNode next; // points to next node
	private int type; // one of the 5 History static values defined below
	private Object obj; // could be a SharpClipboard or a CellRange
	
	ListNode(Object obj) {
	    setObject(obj);
	}

	// simple get/set functions
	Object getObject() { return obj; }
	ListNode getPrev() { return prev; }    
	ListNode getNext() { return next; }
	
	void setObject(Object obj) { this.obj = obj; }
	void setPrev(ListNode prev) { this.prev = prev; }
	void setNext(ListNode next) { this.next = next; }
	
	int getType() { return type; }    
	void setType(int type) { this.type = type; }    
    }

    private SharpTools sharp;
    final public static int UNCHANGED = 0;
    final public static int INSERTROW = 1;
    final public static int INSERTCOLUMN = 2;
    final public static int REMOVEROW = 3;
    final public static int REMOVECOLUMN = 4;

    /** Holds the objects that are cut or copied */
    private ListNode current;

    /**
     * Constructor:
     *
     * @param sharp the SharpTools object
     */
    History(SharpTools sharp) {
	this.sharp = sharp;
	current = new ListNode(null);	
    }

    /** 
     * This adds the range of cells to the history.
     *
     * @param model the SharpTableModel we operate on
     * @param range the cell range the operation will affect
     */
    public void add(SharpTableModel model, CellRange range) {
	// construct the clipboard to be saved
	SharpClipboard clip = new SharpClipboard(model, range, false);
        add(model, clip);
    }

    /** 
     * This adds a clipboard object to the history list.
     *
     * @param model the SharpTableModel we operate on
     * @param range the cell range the operation will affect
     */
    public void add(SharpTableModel model, SharpClipboard clip) {
	ListNode node = new ListNode(clip);
	if (Debug.isDebug())
	    Debug.println("Add history for range "+clip.getSource());

	// add to the linked list
	current.setNext(node);
	node.setPrev(current);

	// move current forward
	current = node;

	// modified!
	// all operations should call add instead of setModified themselves
	model.setModified(true);
	sharp.checkUndoRedoState();
    }

    /*
     * This method should only be called with type != UNCHANGED,
     * i.e., for table insertion/deletion operations.
     *
     * @param model the SharpTableModel we operate on
     * @param range the cell range the operation will affect
     * @param type the operation type
     */
    public void add(SharpTableModel model, CellRange range, int type) {
	SharpClipboard clip;
	ListNode node;	

	if (type == UNCHANGED) {
	    add(model, range);
	    return;
	}
	    
	if (type == REMOVEROW || type == REMOVECOLUMN) {
	    // save the current range
	    //	    System.out.println("Add history of removing "+range);
	    clip = new SharpClipboard(model, range, false);
	    node = new ListNode(clip);
	}
	else {
	    //	    System.out.println("Add history of inserting "+range);
	    // for insertion, no data need to be saved
	    // just save the range value
	    node = new ListNode(range);
	}
	
	node.setType(type);

	// add to the end of history list
	current.setNext(node);
	node.setPrev(current);

	// move current forward
	current = node;
	model.setModified(true);
	sharp.checkUndoRedoState();
    }

    /**
     * This is the undo method.
     *
     * @param model the SharpTableModel we operate on
     */
    public void undo(SharpTableModel model) {
	if (!isUndoable())
	    return;

	int type = current.getType();
	CellRange range;
	
	if (type == UNCHANGED) {
	    // get the saved clipboard and its range
	    SharpClipboard oldClip = (SharpClipboard)current.getObject();
	    range = oldClip.getSource();
	
	    // replace the current object with the current table data
	    SharpClipboard newClip = new SharpClipboard(model, range, false);
	    current.setObject(newClip);
	
	    //	    Debug.println("Undo "+range);

	    // recover the data (undo)
	    oldClip.paste(model, range.getminCorner());
	}
	else if (type == REMOVEROW ||
		 type == REMOVECOLUMN) {
	    // undo a removal is just do an insertion and paste
	    // saved data to it
	    SharpClipboard clip = (SharpClipboard)current.getObject();
	    range = clip.getSource();
	    //	    Debug.println("Pos: "+clip.getSource());
	    //	    Debug.println(clip);
	    //	    Debug.println("Undo: insert "+range);
	    // insert lines first
	    if (type == REMOVEROW)
		model.insertRow(range);
	    else
		model.insertColumn(range);

	    // then paste stuff
	    clip.paste(model, range.getminCorner());
	}
	else {
	    // undo an insertion is just a removal
	    range = (CellRange)current.getObject();
	    //	    System.out.println("Undo: remove "+range);
	    if (type == INSERTROW)
		model.removeRow(range);
	    else
		model.removeColumn(range);
	}

	current = current.getPrev();

	// recover the selection
	model.setSelection(range);

	/*
	if (!isUndoable()) { // has come back to the beginning
	    model.setModified(false);
	}
	*/
	model.setModified(true);
	sharp.checkUndoRedoState();
    }

    /**
     * This is the redo method.
     *
     * @param model the SharpTableModel we operate on
     */
    public void redo(SharpTableModel model) {
	if (!isRedoable())
	    return;
	
	current = current.getNext();
	int type = current.getType();
	CellRange range;
	
	if (type == UNCHANGED) {

	    // get the saved clipboard
	    SharpClipboard oldClip = (SharpClipboard)current.getObject();
	    range = oldClip.getSource();
	
	    // replace the current object with the current model data
	    SharpClipboard newClip = new SharpClipboard(model, range, false);
	    current.setObject(newClip);

	    // restore data and selection
	    oldClip.paste(model, range.getminCorner());
	    model.setSelection(range);
	}
	else if (type == REMOVEROW ||
		 type == REMOVECOLUMN) {
	    // redo a removal
	    SharpClipboard clip = (SharpClipboard)current.getObject();
	    range = clip.getSource();
	    // insert lines first
	    if (type == REMOVEROW)
		model.removeRow(range);
	    else
		model.removeColumn(range);
	}
	else {
	    // redo an insertion
	    range = (CellRange)current.getObject();
	    
	    if (type == INSERTROW)
		model.insertRow(range);
	    else
		model.insertColumn(range);	    
	}

	model.setModified(true);
	sharp.checkUndoRedoState();
    }

    /**
     * This method check if undo can be done.
     *
     * @return true if undo is possible
     */
    public boolean isUndoable() {
	return current.getPrev() != null;
    }

    /**
     * This method check if redo can be done.
     *
     * @return true if redo is possible
     */
    public boolean isRedoable() {
	return current.getNext() != null;
    }
}

