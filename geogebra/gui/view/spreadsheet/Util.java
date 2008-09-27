
package geogebra.gui.view.spreadsheet;

import java.awt.Component;
import javax.swing.JOptionPane;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Util {
	
	public static void handleException(Component component, String message) {
		try {
			throw new RuntimeException(message);
		} catch (RuntimeException ex) {
			handleException(component, ex);
		}
	}

	public static void handleException(Component component, Exception ex) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream printOut = new PrintStream(output);
		ex.printStackTrace(printOut);
		JOptionPane.showMessageDialog(component, output.toString(), "Exception", JOptionPane.ERROR_MESSAGE);
	}

}
