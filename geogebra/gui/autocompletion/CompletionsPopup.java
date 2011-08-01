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
import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
	private KeyListener keyListener;
	private KeyListener[] textFieldKeyListeners;
	
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
	
	private class PopupListener implements PopupMenuListener {

		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			textField.removeKeyListener(keyListener);
			for (KeyListener listener: textFieldKeyListeners) {
				textField.addKeyListener(listener);
			}			
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			textFieldKeyListeners = textField.getKeyListeners();
			for (KeyListener listener: textFieldKeyListeners) {
				textField.removeKeyListener(listener);
			}
			textField.addKeyListener(keyListener);
		}
		
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
		keyListener = new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) { handleSpecialKeys(e); }
		};
		// Hide popup when text field loses focus
		textField.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) { hidePopup(); }
		});
		// Allow the user click on an option for completion
		list.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) { handleMouseClick(e); }
		});
		popup.addPopupMenuListener(new PopupListener());
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
		// Remove key listeners and replace with own;
	}
	
	private boolean isPopupVisible() {
		return popup.isVisible();
	}

	private void hidePopup() {
		if (!isPopupVisible()) {
			return;
		}
		popup.setVisible(false);
		list.clearSelection();
		// Reinstate textField's key listeners
	}
	
	public void handleSpecialKeys(KeyEvent keyEvent) {
		if (!isPopupVisible()) {
			return;
		}

		switch(keyEvent.getKeyCode()) {
		case VK_ESCAPE:			// [ESC] cancels the popup
			textField.cancelAutoCompletion();
			hidePopup();
			keyEvent.consume();
			break;
		case VK_ENTER:			// [ENTER] validates the completions
			textField.validateAutoCompletion(list.getSelectedIndex());
			hidePopup();
			keyEvent.consume();
			break;
		case VK_DOWN:			// [DOWN] next completion
		case VK_TAB:			// [TAB]
			navigateRelative(+1);
			keyEvent.consume();
			break;
		case VK_UP:				// [UP] prev. completion
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
			textField.processKeyEvent(keyEvent);
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
	}
	
	private void handleMouseClick(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			textField.validateAutoCompletion(list.getSelectedIndex());
			hidePopup();
		} 
	}

}
