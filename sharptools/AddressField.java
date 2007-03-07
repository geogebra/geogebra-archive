package sharptools;
/*
 * @(#)AddressField.java
 * 
 * $Id: AddressField.java,v 1.2 2007-03-07 06:24:32 hohenwarter Exp $
 * 
 * Created Novenmber 25, 2000, 5:13 AM
 */

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This class is used to only allow address input (letters followed
 * by numbers).
 * It extends JTextField and provides its own Document Filter.
 *
 * @see NumberField
 *
 * @author Hua Zhong
 * @version $Revision: 1.2 $
 */
public class AddressField extends JTextField {

    /**
     * get the input address
     *
     * @return a CellPoint object that converted from the user's input
     */
    public CellPoint getAddress() {
	return Formula.parseAddress(getText());
    }

    /**
     * set the textfield to the specified address
     *
     * @param f a CellPoint object specifying the address
     */
    public void setAddress(CellPoint point) {
	if (point == null)
	    setText(null);
	else
	    setText(point.toString());
    }

    
    //    public Object getValue() { return getAddress(); }

    // a class used to filter the user's input
    class NumberFilterDocument extends PlainDocument {
	private StringBuffer __scratchBuffer;

	public NumberFilterDocument() {
	    __scratchBuffer = new StringBuffer();
	}

	// this is called when user inserts a string into the text field
	public void insertString(int offset, String text, AttributeSet aset)
	    throws BadLocationException {
	    if (text == null || text.length() == 0)
		return;	    

	    __scratchBuffer.setLength(0);

	    // Reject all strings that cause the contents of the field not
	    // to be a valid number (i.e., string representation of a double)
	    try {
		__scratchBuffer.append(getText(0, getLength()));
		__scratchBuffer.insert(offset, text);
		// Kludge: Append a 0 so that leading decimal points
		// and signs will be accepted
	    } catch(BadLocationException ble) {
		ble.printStackTrace();
		return;
	    } catch(StringIndexOutOfBoundsException sioobe) {
		sioobe.printStackTrace();
		return;
	    }
	    
	    String buf = __scratchBuffer.toString();
	    boolean hasNumber = false;
	    
	    // only allow {letters}{numbers}
	    for (int i=0; i<buf.length(); i++) {
		char c = buf.charAt(i);

		// is a letter?
		if (!Character.isLetter(c)) {
		    // no, is a digit?
		    if (Character.isDigit(c) && i>0)
			// the first c has to be letter
			// yes, following should be all digits
			hasNumber = true;
		    else
			// no, invalid
			return;
		}
		else if (hasNumber) // should not be letter anymore
		    return;
	    }		    
	    
	    super.insertString(offset, text.toUpperCase(), aset);
	}
    }

    
    /**
     * Construct an address only text field.  
     *
     * @param columns the length of the text field
     */
    public AddressField(int columns) {
	super(columns);
	setDocument(new NumberFilterDocument());
    }
}
