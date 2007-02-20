package sharptools;
/** 
 * This is a customized dialog which provides standard buttons.
 * 
 * @author Hua Zhong
 * @version $Revision: 1.1 $
 *
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @see PasswordDialog
 * @see NewFileDialog
 * @see FindDialog
 * @see SortDialog
 * @see HistoDialog
 * @see ConnectDialog
 */
class SharpDialog extends JDialog {
    
    static final public int CLOSED_OPTION = JOptionPane.CLOSED_OPTION;
    static final public int YES_OPTION = JOptionPane.YES_OPTION;
    static final public int NO_OPTION = JOptionPane.NO_OPTION;
    static final public int OK_OPTION = JOptionPane.OK_OPTION;
    static final public int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
    
    static final private ImageIcon okIcon = SharpTools.getImageIcon("ok.gif");
    static final private ImageIcon cancelIcon = SharpTools.getImageIcon("cancel.gif");
    static final private ImageIcon noIcon = SharpTools.getImageIcon("no.gif");

    // get string from the UIManager - support locale
    final private JButton okButton = new JButton(UIManager.get("OptionPane.okButtonText").toString(), okIcon);
    final private JButton cancelButton = new JButton(UIManager.get("OptionPane.cancelButtonText").toString(), cancelIcon);
    final private JButton yesButton = new JButton(UIManager.get("OptionPane.yesButtonText").toString(), okIcon);
    final private JButton noButton = new JButton(UIManager.get("OptionPane.noButtonText").toString(), noIcon);

    final private Object[] ok = { okButton };
    final private Object[] yesno = { yesButton, noButton };
    final private Object[] yesnocancel = { yesButton, noButton, cancelButton };
    final private Object[] okcancel = { okButton, cancelButton };

    private int choice = CLOSED_OPTION;


    // override all constructors
    /**
     * create a non-modal dialog
     *
     * @param c parent dialog
     */
    SharpDialog(Dialog c) {
	super(c);
    }

    /**
     * @param c parent dialog
     * @param model whether to create a modal dialog 
     */
    SharpDialog(Dialog c, boolean modal) {
	super(c, modal);
    }

    /**
     * create a non-modal dialog
     *
     * @param c parent dialog
     * @param title title
     */
    SharpDialog(Dialog c, String title) {
	super(c, title);
    }

    /**
     * @param c parent dialog
     * @param title title
     * @param model whether to create a modal dialog 
     */
    SharpDialog(Dialog c, String title, boolean modal) {
	super(c, title, modal);
    }

    /**
     * create a non-modal dialog
     *
     * @param c parent frame
     */
    SharpDialog(Frame c) {
	super(c);
    }

    /**
     * @param c parent frame
     * @param model whether to create a modal dialog 
     */
    SharpDialog(Frame c, boolean modal) {
	super(c, modal);
    }

    /**
     * create a non-modal dialog
     *
     * @param c parent frame
     * @param title title
     */
    SharpDialog(Frame c, String title) {
	super(c, title);
    }

    /**
     * @param c parent frame
     * @param title title
     * @param model whether to create a modal dialog 
     */
    SharpDialog(Frame c, String title, boolean modal) {
	super(c, title, modal);
    }

    /**
     * override this protected funciton to register ESCAPE with the root pane
     *
     * @return a customized JRootPane object
     */
    protected JRootPane createRootPane() {
	KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	JRootPane rootPane = new JRootPane();
	rootPane.registerKeyboardAction(
					new ActionListener() {
						public void actionPerformed
						    (ActionEvent ev) {    
						    if (onClose()) {
							choice = CLOSED_OPTION;
							setVisible(false);
						    }
						}
					    },
					stroke,
					JComponent.WHEN_IN_FOCUSED_WINDOW);
	return rootPane;
    }

    /**
     * I provide several hooks for the sublasses to override..
     */

    /**
     *
     * This function is called when the dialog window is opened.
     *
     */
    protected void onOpen() {
	
    }
    
    /**
     *
     * This function is called when
     * (1) the close button is clocked
     * (2) Escape is pressed
     *
     * By default, we think it's the same as clicking Cancel button
     *
     * @return whether the dialog should be closed
     */
    protected boolean onClose() {
	return onCancel();
    }

    /**
     *
     * This function is called when the OK button is pressed
     *
     * @return whether the dialog should be closed
     */
    protected boolean onOK() {	
	return true;
    }
    
    /**
     *
     * This function is called when the Cancel button is pressed
     *
     * @return whether the dialog should be closed
     */
    protected boolean onCancel() {
	return true;
    }

    /**
     *
     * This function is called when the Yes button is pressed
     *
     * @return whether the dialog should be closed
     */
    protected boolean onYes() {
	return true;
    }

    /**
     *
     * This function is called when the No button is pressed
     *
     * @return whether the dialog should be closed
     */
    protected boolean onNo() {
	return true;
    }

    /**
     * This is used by subclasses to set the JOptionPane component
     * in the dialog.
     *
     * @param message the message box
     * @param messageType the message type
     * @param optionType the option type
     * @param icon the icon to display
     */
    protected void setOptionPane(Object message,
				 int messageType,
				 int optionType,
				 Icon icon) {
	
	setOptionPane(message, messageType, optionType, icon, 0);
    }
    
    /**
     * This is used by subclasses to set the JOptionPane component
     * in the dialog.
     *
     * @param message the message box
     * @param messageType the message type
     * @param optionType the option type
     * @param icon the icon to display
     * @param defaultIndex the default index
     */
    protected void setOptionPane(Object message,
				 int messageType,
				 int optionType,
				 Icon icon,			       
				 int defaultIndex) {
	
	addWindowListener(new WindowAdapter() {

		public void windowOpened(WindowEvent we) {
		    // set the initial focus to rowField
		    onOpen();
		}
		
                public void windowClosing(WindowEvent we) {
		    if (onClose()) {
			choice = CLOSED_OPTION;
			setVisible(false);
		    }
		}
	    });
	
	// handle this by onClose()
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

	okButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    if (onOK()) {
			choice = OK_OPTION;
			setVisible(false);
		    }
		}
	    });

	cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    if (onCancel()) {
			choice = CANCEL_OPTION;
			setVisible(false);
		    }
		}
	    });
	
	yesButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    if (onYes()) {
			choice = YES_OPTION;
			setVisible(false);
		    }
		}
	    });
	
	noButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    if (onNo()) {
			choice = NO_OPTION;
			setVisible(false);
		    }
		}
	    });

	// setup Mnemonic
	okButton.setMnemonic (KeyEvent.VK_O);
	cancelButton.setMnemonic (KeyEvent.VK_C);
	yesButton.setMnemonic (KeyEvent.VK_Y);
	noButton.setMnemonic (KeyEvent.VK_N);
	
	Object[] buttons = setButtons(optionType);
	JOptionPane optionPane = new JOptionPane(message, messageType,
						 optionType, icon,
						 buttons,
						 buttons[defaultIndex]);
	
	setContentPane(optionPane);

	pack();
	setLocationRelativeTo(getParent());
    }

    /**
     * set button array
     */
    private Object[] setButtons(int type) {

	Object[] buttons;
	
	switch (type) {
	case JOptionPane.YES_NO_CANCEL_OPTION:
	    buttons = yesnocancel;
	    break;
	case JOptionPane.YES_NO_OPTION:
	    buttons = yesno;
	    break;
	case JOptionPane.OK_CANCEL_OPTION:	    
	    buttons = okcancel;
	    break;
	default:
	    buttons = ok;
	    break;
	}

	return buttons;
    }

    /**
     * @return user choice
     */
    public int getChoice() {
	return choice;
    }

    /**
     * @return whether this operation was cancelled
     */    
    public boolean isCancelled() {
	return choice == CANCEL_OPTION || choice == CLOSED_OPTION;
    }
}




