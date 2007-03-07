package sharptools;
/*
 * @(#)NumberField.java
 * 
 * $Id: NumberField.java,v 1.2 2007-03-07 06:24:32 hohenwarter Exp $
 * 
 * Created Novenmber 25, 2000, 5:13 AM
 */
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This class is used to only allow number input (int/float).
 * It extends JTextField and provides its own Document Filter.
 *
 * For simplicity, now it uses InputVerifier - huaz
 *
 * @see HistoDialog
 * @see NewFileDialog
 * @see AddressField
 *
 * @author Hua Zhong
 * @version $Revision: 1.2 $
 */
public class NumberField extends JTextField {

    // private Float number;
    //    private String oldValue;
    //    private Float number;
    private boolean positiveOnly;
    private boolean integer;

    /**
     * Construct a float-number only text field (including integer)
     *
     * @param columns the length of the text field
     */
    public NumberField(int columns) {
	super(columns);
	setDocument(new NumberFilterDocument());
	//	setInputVerifier(new NumberVerifier());
    }
    
    /**
     * Construct a float-number only text field.  If positive is true
     * only positive float number is allowed.
     *
     * @param columns the length of the text field
     * @param positive the value needs to be positive
     */
    public NumberField(int columns, boolean positive) {
	this(columns);
	this.positiveOnly = positive;
	setDocument(new NumberFilterDocument());
	//	setInputVerifier(new NumberVerifier());
    }

    /**
     * Construct a number only text field and can add more restriction
     * as positive number only or integer only
     *
     * @param columns the length of the text field
     * @param positive the value needs to be positive
     * @param integer the value needs to be an integer
     */
    public NumberField(int columns, boolean positive, boolean integer) {
	this(columns);
	this.positiveOnly = positive;
	this.integer = integer;
	setDocument(new NumberFilterDocument());
	//	setInputVerifier(new NumberVerifier());
    }	

    /**
     * Construct a number only text field and can add more restriction
     * as positive number only or integer only
     *
     * @param positive the value needs to be positive
     * @param integer the value needs to be an integer
     */
    public NumberField(boolean positive, boolean integer) {
	super();
	this.positiveOnly = positive;
	this.integer = integer;
	setDocument(new NumberFilterDocument());
	//setInputVerifier(new NumberVerifier());
    }
    
    /**
     * get the input float number
     *
     * @return a Float object that converted from the user's input
     */
    public Float getNumber() {
	try {
	    return new Float(Float.parseFloat(getText()));
	}
	catch (Exception e) {
	    return null;
	}
    }

    /**
     * set the textfield to the specified float number
     *
     * @param f a Float object
     */
    public void setNumber(Float f) {

	if (f == null)
	    setText(null);
	else
	    setText(f.toString());
    }

    /**
     * When it's used as integer field, get the input integer number.
     *
     * @return an Integer object that converted from the user's input
     */
    public Integer getInteger() {
	try {
	    return new Integer(Integer.parseInt(getText()));
	}
	catch (Exception e) {
	    return null;
	}
    }

   /**
    * When it's used as integer field, set the textfield to
    * the specified integer number
    *
    * @param f a Float object
    */
    public void setInteger(Integer i) {

	if (i == null)
	    setText(null);
	else
	    setText(i.toString());
    }

    // a class used to filter the user's input
    class NumberFilterDocument extends PlainDocument {
	private StringBuffer __scratchBuffer;

	public NumberFilterDocument() {
	    super();
	    __scratchBuffer = new StringBuffer();
	}

	// this is called when user inserts a string into the text field
	public void insertString(int offset, String text, AttributeSet aset)
	    throws BadLocationException {
	    if (text == null)
		return;
	    
	    __scratchBuffer.setLength(0);

	    // Reject all strings that cause the contents of the field not
	    // to be a valid number (i.e., string representation of a double)
	    try {
		__scratchBuffer.append(getText(0, getLength()));
		__scratchBuffer.insert(offset, text);
		// Kludge: Append a 0 so that leading decimal points
		// and signs will be accepted
		__scratchBuffer.append('0');
	    } catch(BadLocationException ble) {
		ble.printStackTrace();
		return;
	    } catch(StringIndexOutOfBoundsException sioobe) {
		sioobe.printStackTrace();
		return;
	    }

	    float value;
	    try {
		if (integer)
		    value = Integer.parseInt(__scratchBuffer.toString());
		else
		    value = Float.parseFloat(__scratchBuffer.toString());
	    } catch(NumberFormatException nfe) {
		// Resulting string will not be number, so reject it
		return;
	    }

	    if (positiveOnly && value<0)
		return;
	    
	    super.insertString(offset, text, aset);
	}
    }

    /*
class NumberVerifier extends InputVerifier {

    NumberVerifier() {
    super();
    }
    
    public boolean verify(JComponent input) {
	JTextField tf = (JTextField)input;
	String number = tf.getText();
	float value = 0;
	try {
	    if (integer)
		value = Integer.parseInt(number);
	    else
		value = Float.parseFloat(number);
	} catch(NumberFormatException nfe) {
	    // Resulting string will not be number, so reject it
	    return false;
	}

	if (positiveOnly && value < 0)
	    return false;
	
	return true;
    }
}
*/


}







