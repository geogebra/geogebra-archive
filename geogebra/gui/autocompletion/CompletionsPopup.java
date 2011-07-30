package geogebra.gui.autocompletion;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_PAGE_DOWN;
import static java.awt.event.KeyEvent.VK_PAGE_UP;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_TAB;

import static java.lang.Math.max;
import static java.lang.Math.min;

import geogebra.gui.inputbar.AutoCompleteTextField;

import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Provides completion popup for {@link AutoCompleteTextField}.
 * Derived from OptionsPopup.
 * 
 * @author Arnaud Delobelle
 */
public class CompletionsPopup {
	private final AutoCompleteTextField textField;
	private final int maxPopupRowCount;
	
	private final JPopupMenu popup;
	private final DelegatingListModel listModel;
	private final JList list;
	
	private DocumentListener textFieldDocListener;
	
	/**
	 * Initializes components and registers event listeners.
	 * 
	 * @param textField The text field
	 * @param listCellRenderer A list cell renderer which visualizes the options 
	 *                         returned by the provided {@link CompletionProvider}
	 * @param maxPopupRowCount The maximal number of rows for the options popup
	 */
	public CompletionsPopup(AutoCompleteTextField textField, ListCellRenderer listCellRenderer, int maxPopupRowCount) {
		this.textField = textField;
		this.maxPopupRowCount = maxPopupRowCount;
		
		// Initialize components
		listModel = new DelegatingListModel();
		list = new JList(listModel);
		list.setCellRenderer(listCellRenderer);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFocusable(false);
		popup = new JPopupMenu();
		popup.add(new JScrollPane(list));
		popup.setBorder(BorderFactory.createEmptyBorder());
		popup.setFocusable(false);
		registerListeners();
	}

	private void registerListeners() {
		// Suggest completions on text changes, store reference to listener object
		textFieldDocListener = new DocumentListener() {
			public void removeUpdate(DocumentEvent e) { hidePopup(); }
			public void insertUpdate(DocumentEvent e) { /*showCompletions();*/ }
			public void changedUpdate(DocumentEvent e) { hidePopup(); }
		};
		textField.getDocument().addDocumentListener(textFieldDocListener);
		// Handle special keys (e.g. navigation)
		textField.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) { handleSpecialKeys(e); }
		});
		// Hide popup when text field loses focus
		textField.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) { hidePopup(); }
		});
		// Allow the user click on a option for completion
		list.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) { handleMouseClick(e); }
		});
	}
	
	public void showCompletions() {
		if (!textField.getAutoComplete()) {
			return;
		}
		List<String> completions = textField.getCompletions();
		if (completions == null) {
			hidePopup();
			return;
		}
		if (completions.size() > 0) {
			listModel.setDataList(completions);
			list.setSelectedIndex(0);
			list.ensureIndexIsVisible(0);
			showPopup();
		} else {
			hidePopup();
		}
	}
	
	private void showPopup() {		
		// Adjust size of the popup if necessary
		int newPopupRowCount = Math.min(listModel.getSize(), maxPopupRowCount);
		list.setVisibleRowCount(newPopupRowCount);
		// Let the UI calculate the preferred size
		popup.setPreferredSize(null);
		popup.pack();
		
		Rectangle startRect;
		try {
			startRect = textField.modelToView(textField.getCurrentWordStart());
		} catch (BadLocationException e) {
			startRect = new Rectangle(0, 0, 0, 0);
		}
		// Try to show popup just beneath the word to be completed
		popup.show(textField, startRect.x, startRect.y + startRect.height);
	}
	
	private boolean isPopupVisible() {
		return popup.isVisible();
	}

	private void hidePopup() {
		if (isPopupVisible()) {
			popup.setVisible(false);
			list.clearSelection();
		}
	}
	
	private void updateText() {
		int index = list.getSelectedIndex();
		Document d = textField.getDocument();
		d.removeDocumentListener(textFieldDocListener);
		textField.updateAutoCompletion(index);
		d.addDocumentListener(textFieldDocListener);
	}
	
	private void handleSpecialKeys(KeyEvent keyEvent) {
		if (!isPopupVisible()) {
			return;
		}

		switch(keyEvent.getKeyCode()) {
		case VK_ESCAPE:			// [ESC]
			hidePopup();
			textField.cancelAutoCompletion();
			keyEvent.consume();
			break;
		case VK_ENTER:			// [ENTER]
			hidePopup();
			//
			break;
		case VK_DOWN:			// [DOWN]
		case VK_TAB:			// [TAB]
			navigateRelative(+1);
			keyEvent.consume();
			break;
		case VK_UP:				// [UP]
			navigateRelative(-1);
			keyEvent.consume();
			break;	
		case VK_PAGE_DOWN:		// [PAGE_DOWN]
			navigateRelative(+maxPopupRowCount - 1);
			keyEvent.consume();
			break;
		case VK_PAGE_UP:		// [PAGE_UP]
			navigateRelative(-maxPopupRowCount + 1);
			keyEvent.consume();
			break;
		default:
			hidePopup();
		}
	}

	private void navigateRelative(int offset) {
		boolean up = offset < 0;
		int end = listModel.getSize() - 1;
		int index = list.getSelectedIndex();
		
		// Wrap around
		if (-1 == index) {
			index = up ? end : 0;
		} else if (0 == index && up || end == index && !up) {
			index = - 1;
		} else {
			index += offset;
			index = max(0, min(end, index));
		}

		if(-1 == index) {
			list.clearSelection();
		} else {
			list.setSelectedIndex(index);
			list.ensureIndexIsVisible(index);
		}
		updateText();
	}
	
	private void handleMouseClick(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			updateText();
			textField.validateAutoCompletion();
			hidePopup();
		} 
	}

}
