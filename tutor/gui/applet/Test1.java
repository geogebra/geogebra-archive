package tutor.gui.applet;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class Test1 extends JPanel {

	static String[] columnNames = {"First Name",
			"Last Name",
			"Sport",
			"# of Years",
	"Vegetarian"};

	static Object[][] data = {
			{"Mary", "Campione",
				"Snowboarding", new Integer(5), new Boolean(false)},
				{"Alison", "Huml",
					"Rowing", new Integer(3), new Boolean(true)},
					{"Kathy", "Walrath",
						"Knitting", new Integer(2), new Boolean(false)},
						{"Sharon", "Zakhour",
							"Speed reading", new Integer(20), new Boolean(true)},
							{"Philip", "Milne",
								"Pool", new Integer(10), new Boolean(false)}
	};

	private static void createAndShowGUI() {

		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));

        JScrollPane scrollPane = new JScrollPane(table);
        topPanel.add(scrollPane);
        
        JTextField textField = new JTextField(10);
        JLabel textFieldLabel = new JLabel();
        
        
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        bottomPanel.setLayout(gridbag);
        bottomPanel.add(textField);

        
        Test1 newContentPane = new Test1();
        newContentPane.setOpaque(true);
        newContentPane.setLayout(new BoxLayout(newContentPane,BoxLayout.Y_AXIS));
        newContentPane.add(topPanel);
        newContentPane.add(bottomPanel);
        
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
	}
	
	public static void main(String[] args) throws Throwable {
		
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}
