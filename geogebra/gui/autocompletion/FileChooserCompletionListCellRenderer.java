package geogebra.gui.autocompletion;

import geogebra.main.Application;

import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * A simple list cell renderer derived from {@link DefaultListCellRenderer}.
 * Sets icons on the returned labels depending on the file extension.
 * 
 * @author Julian Lettner
 */
public class FileChooserCompletionListCellRenderer extends DefaultListCellRenderer {
	
	private static final String ICON_LOCATION = "/geogebra/gui/images/";
	
	private static final Icon DIRECTORY_ICON = loadIcon("folder.png");
	private static final Icon UNKNOWN_FILE_ICON = loadIcon("unknown-file.png");
	
	private static final Map<String, Icon> FILE_EXT_ICONS = new HashMap<String, Icon>();
	static {
		Icon icon;
		// ggb, ggt 
		icon = loadIcon("geogebra-file.png");
		FILE_EXT_ICONS.put("ggb", icon);
		FILE_EXT_ICONS.put("ggt", icon);
		// html, htm
		icon = loadIcon("websites.png");
		FILE_EXT_ICONS.put("html", icon);
		FILE_EXT_ICONS.put("htm", icon);
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// Assumes that the values are of type 'File'
		File file = (File) value;
		// Cast is safe, DefaultListCellRenderer will always return a label
		JLabel label = (JLabel) super.getListCellRendererComponent(list, file.getName(), index, isSelected, cellHasFocus);
		label.setIcon(getIcon(file));
		
		return label;
	}

	private Icon getIcon(File file) {
		if (file.isDirectory()) {
			return DIRECTORY_ICON;
		}
		
		// inclusive toLower, default is ""
		String fileExt = Application.getExtension(file);
		Icon icon = FILE_EXT_ICONS.get(fileExt);
		if (icon == null) {
			icon = UNKNOWN_FILE_ICON;
		}
		
		return icon;
	}
	
	private static Icon loadIcon(String iconImage) {
		URL iconUrl = FileChooserCompletionListCellRenderer.class.getResource(ICON_LOCATION + iconImage);
		if (iconUrl == null) {
			Application.debug("Could not load icon: " + iconImage);
			return new ImageIcon();
		}
		return new ImageIcon(iconUrl);
	}
	
}
