package geogebra.gui.menubar;

import geogebra.GeoGebra;
import geogebra.gui.layout.Layout;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class GeoGebraMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1736020764918189176L;

	private BaseMenu fileMenu, editMenu, viewMenu, optionsMenu, toolsMenu, windowMenu, helpMenu;

	private Application app;
	private Layout layout;

	public GeoGebraMenuBar(Application app, Layout layout) {
		this.layout = layout;

		/**
		 * A nasty workaround to prevent any borders from being drawn. All other
		 * elements will have a border at the top to prevent visual conflicts
		 * while moving the toolbar / algebra input to the top / bottom. The
		 * JMenuBar *always* draws a border at the bottom however, even if the
		 * border set via setBorder() is empty. By drawing an one pixel border
		 * with the color of the background we can prevent this.
		 */
		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(0, 0, 1, 0, SystemColor.control),
				BorderFactory.createEmptyBorder(1, 1, 0, 1)));

		this.app = app;
	}

	/**
	 * Initialize the menubar. No update is required after initialization.
	 */
	public void initMenubar() {
		removeAll();

		// "File"
		fileMenu = new FileMenu(app);
		add(fileMenu);
		
		// "Edit"
		editMenu = new EditMenu(app);
		add(editMenu);
		
		// "View"
		viewMenu = new ViewMenu(app, layout);
		add(viewMenu);
		
		// "Options"
		optionsMenu = new OptionsMenu(app, layout);
		add(optionsMenu);
		
		// "Tools"
		toolsMenu = new ToolsMenu(app);
		add(toolsMenu);
		
		// "Window"
		windowMenu = new WindowMenu(app);
		
		if(!app.isApplet()) // just add the menu if this is not an applet we're 
		{
			add(windowMenu);
			
			if (app.getPluginManager() != null) {
				javax.swing.JMenu pim = app.getPluginManager().getPluginMenu();
				if (pim != null) {
					add(pim);
				} // H-P Ulven 2008-04-17
			}
		}
		
		// "Help"
		helpMenu = new HelpMenu(app);
		add(helpMenu);
	}

	/**
	 * Update the menubar.
	 */
	public void updateMenubar() {
		fileMenu.update();
		editMenu.update();
		viewMenu.update();
		optionsMenu.update();
		toolsMenu.update();
		
		if(!app.isApplet())
			windowMenu.update();
		
		helpMenu.update();
		
		updateSelection();
	}

	/**
	 * Update the selection.
	 */
	public void updateSelection() {
		((EditMenu)editMenu).updateSelection();
	}
	
	/**
	 * Update the file menu without being forced to updated the other menus as well.
	 */
	public void updateMenuFile() {
		fileMenu.update();
	}
	
	/**
	 * Update the window menu without having to update the other menus as well.
	 */
	public void updateMenuWindow() {
		windowMenu.update();
	}
	
	/**
	 * Show the print preview dialog.
	 * 
	 * @param app
	 */
	public static void showPrintPreview(final Application app) {
		try {
			Thread runner = new Thread() {
				public void run() {
					app.setWaitCursor();

					try {
						// use reflection for
						// new geogebra.export.PrintPreview(app,
						// app.getEuclidianView(), PageFormat.LANDSCAPE);
						// Class classObject =
						// Class.forName("geogebra.export.PrintPreview");
						// Object[] args = new Object[] { app ,
						// app.getEuclidianView(), new
						// Integer(PageFormat.LANDSCAPE)};
						// Class [] types = new Class[] {Application.class,
						// Printable.class, int.class};
						// Constructor constructor =
						// classObject.getDeclaredConstructor(types);
						// constructor.newInstance(args);
						new geogebra.export.PrintPreview(app, app
								.getEuclidianView(), PageFormat.LANDSCAPE);
					} catch (Exception e) {
						Application.debug("Print preview not available");
					}

					app.setDefaultCursor();
				}
			};
			runner.start();
		} catch (java.lang.NoClassDefFoundError ee) {
			app.showErrorDialog(app.getError("ExportJarMissing"));
			ee.printStackTrace();
		}
	}

	/**
	 * Show the "About" dialog.
	 * 
	 * @param app
	 */
	public static void showAboutDialog(final Application app) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><b>");
		sb.append(app.getPlain("ApplicationName"));
		sb.append(" ");
		sb.append(GeoGebra.VERSION_STRING);
		sb.append("</b>  (");
		sb.append("Java "+ System.getProperty("java.version") + ", " +(app.getHeapSize()/1024/1024)+"MB"); 
		sb.append(")<br>");	
		sb.append(GeoGebra.BUILD_DATE);

		// license
		String text = app.loadTextFile(Application.LICENSE_FILE);
		JTextArea textArea = new JTextArea(21, 45);
		JScrollPane scrollPane = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textArea.setEditable(false);
		textArea.setText(text);
		textArea.setCaretPosition(0);

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(new JLabel(sb.toString()), BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.SOUTH);

		JOptionPane infoPane = new JOptionPane(panel,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);

		final JDialog dialog = infoPane.createDialog(app.getMainComponent(),
				app.getMenu("About") + " / " + app.getMenu("License"));

		final ActionListener listener = new ActionListener() {
			public final void actionPerformed(final ActionEvent e) {
				JOptionPane.showMessageDialog(dialog, null, "GeoZebra forever",
						JOptionPane.DEFAULT_OPTION, app
								.getImageIcon("zebra.gif"));
			}
		};

		final KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0,
				true);
		dialog.getRootPane().registerKeyboardAction(listener, keyStroke,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		dialog.setVisible(true);
	}
}
