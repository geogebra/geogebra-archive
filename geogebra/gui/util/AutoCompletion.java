package geogebra.gui.util;

import static org.apache.commons.collections15.IteratorUtils.arrayIterator;
import static org.apache.commons.collections15.IteratorUtils.filteredIterator;
import static org.apache.commons.collections15.IteratorUtils.toList;
import edu.jas.util.ArrayUtil;
import geogebra.main.Application;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.apache.commons.collections15.Predicate;

/**
 * This class provides static methods for conveniently installing auto completion
 * for {@link JTextField} and {@link JFileChooser} components. 
 * 
 * @author Julian Lettner
 */
public class AutoCompletion {
	
	// Public interface - extensible API
	
	/**
	 * Interface for specifying a custom completion provider.
	 */
	public static interface CompletionProvider {
		/**
		 * This method is called repeatedly while the user is typing.
		 * It should return a list of suitable completion options for the prefix.
		 * 
		 * @param prefix The user input with a minimal length of 1
		 * @return A List of matching strings (inclusive prefix)
		 */
		List<String> getCompletionOptions(String prefix);
	}
	
	// Private implementation - helper classes
	
	private static class SortedArrayCompletionProvider implements CompletionProvider {
		private final String[] sortedCompletionOptions;
		private final boolean caseInsensitiveCompletion;
		
		SortedArrayCompletionProvider(String[] unsortedCompletionOptions, boolean caseInsensitiveCompletion) {
			this.sortedCompletionOptions = unsortedCompletionOptions;
			this.caseInsensitiveCompletion = caseInsensitiveCompletion;
			// Sort for an intuitive user experience
			Arrays.sort(sortedCompletionOptions);
		}
		
		public List<String> getCompletionOptions(String prefix) {
			// Proper case for prefix
			final String prefixWithProperCase = caseInsensitiveCompletion ? prefix.toLowerCase() : prefix;
			
			// Predicate for filtering 
			Predicate<String> isValidCompletionOption = new Predicate<String>() {
				public boolean evaluate(String option) {
					return (caseInsensitiveCompletion ? option.toLowerCase() : option).startsWith(prefixWithProperCase);
				}
			};
			// The static import of IteratorUtils allows us to do the following in a single line
			// wrap array in iterator -> filter that iterator with a predicate -> make a list
			List<String> options = toList(filteredIterator(arrayIterator(sortedCompletionOptions), isValidCompletionOption));
			
			// If there is only a single matching option which has the same length as the prefix, remove it
			if(1 == options.size() && prefix.length() == options.get(0).length()) {
				options.clear();
			}
			
			return options;
		}
	}
	
	// --- Static section ---
	
	private final static int POPUP_ROW_COUNT_FOR_FILE_CHOOSER = 8;
	
	private final static boolean caseInsensitivePaths = initCaseInsenitvePaths();
	
	private static boolean initCaseInsenitvePaths() {
		try {
			return System.getProperty("os.name").toLowerCase().contains("windows");
		} catch(SecurityException ex) {
			Application.debug("Could not determine underlying os: " + ex);
			return false;
		}
	}
	
	/**
	 * Convenience method for adding auto completion to a {@link JFileChooser}.
	 * Path name completion will be case(in)sensitive depending on the operating system.
	 * 
	 * @param fileChooser
	 */
	public static void install(JFileChooser fileChooser) {
		install(fileChooser, caseInsensitivePaths);
	}
	
	/**
	 * Convenience method for adding auto completion to a {@link JFileChooser}.
	 * 
	 * @param fileChooser The file chooser
	 * @param caseInsensitiveCompletion <code>true</code> if the casing of path names should be ignored for completion
	 */
	public static void install(final JFileChooser fileChooser, final boolean caseInsensitiveCompletion) {
		CompletionProvider fileChooserCompletionProvider = new CompletionProvider() {
			public List<String> getCompletionOptions(String prefix) {
				// Create adapter: javax.swing.filechooser filter --> java.io filter
				final javax.swing.filechooser.FileFilter fileChooserFileFilter = fileChooser.getFileFilter();
				java.io.FilenameFilter filenameFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return fileChooserFileFilter.accept(new File(dir, name));
					}
				};
				// All visible items in the file chooser are possible options
				String[] options =  fileChooser.getCurrentDirectory().list(filenameFilter);
				// We cannot cache the above steps because the user could change the directory or file filter
				CompletionProvider completionProvider = new SortedArrayCompletionProvider(options, caseInsensitiveCompletion);
				
				return completionProvider.getCompletionOptions(prefix);
			}
		}; 
		
		// Extract internal text field and install auto completion
		JTextField textField = getInternalTextField(fileChooser);
		if (null != textField) {
			install(textField, fileChooserCompletionProvider, POPUP_ROW_COUNT_FOR_FILE_CHOOSER);
		} else {
			Application.debug("Could not find an instance of JTextField inside the file chooser: " + fileChooser);
		}
	}
	
	// TODO-investigate: There should be a better way to get hold of the text field inside a JFileChooser
	// This method assumes that there is exactly one internal JTextField 
	private static JTextField getInternalTextField(Container parent) {
		if (parent instanceof JTextField) {
			return (JTextField) parent;
		}
		
		// Decompose component tree
		for(Component child : parent.getComponents()) {
			if (child instanceof Container) {
				JTextField textField = getInternalTextField((Container) child);
				if (null != textField) {
					return textField;		// Return first JTextField found
				}
			}
		}
		
		// JTextField not found in this subtree
		return null;
	}
	
	/**
	 * Adds auto completion support to a {@link JTextField}.
	 * If dynamic or user defined completion behavior is needed use {@link #install(JTextField, CompletionProvider, int)}
	 * and specify a custom {@link CompletionProvider}.
	 * 
	 * @param textField The text field
	 * @param completionOptions The completion options, will be searched linearly for completion matches
	 * @param caseInsensitiveCompletion <code>true</code> for case insensitive completion
	 * @param maxPopupRowCount The maximum number of rows (height) of the completion popup, 
	 *                         that is the number of options the user can see without scrolling
	 */
	public static void install(JTextField textField, String[] completionOptions, boolean caseInsensitiveCompletion, int maxPopupRowCount) {
		// Array will be changed (sorted) - create defensive copy
		String[] optionsCopy = ArrayUtil.copyOf(completionOptions);
		// Wrap array in provider and install
		install(textField, new SortedArrayCompletionProvider(optionsCopy, caseInsensitiveCompletion), maxPopupRowCount);
	}
	
	/**
	 * Adds auto completion support to a {@link JTextField}.
	 * If all you need is completion for a fixed set of options you may use 
	 * {@link #install(JTextField, String[], boolean, int)} instead.
	 * 
	 * @param textField The text field
	 * @param completionProvider A custom completion provider
	 * @param maxPopupRowCount The maximum number of rows (height) of the completion popup, 
	 *                         that is the number of options the user can see without scrolling
	 */
	public static void install(JTextField textField, CompletionProvider completionProvider, int maxPopupRowCount) {
		new OptionsPopup(textField, completionProvider, maxPopupRowCount);
	}
	
}
