package tutor.gui.applet;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import tutor.gui.applet.TableButton3.ButtonColumn;

public class Test1 extends JPanel {

	// http://www.javalobby.org/articles/jtable/
	// http://exampledepot.com/egs/javax.swing.table/AppendRow.html
	
	
	ButtonColumn buttonColumn;
	JTable table;
	
	static String[] columnNames = {"First Name",
			"Last Name",
			"Sport",
			"# of Years",
	"Vegetarian"};

	static Object[][] data = {
			{"Mary", "Campione", "Snowboarding", new Integer(5), new Boolean(false), "X"},
			{"Alison", "Huml", "Rowing", new Integer(3), new Boolean(true), "X"},
			{"Kathy", "Walrath", "Knitting", new Integer(2), new Boolean(false), "X"},
			{"Sharon", "Zakhour", "Speed reading", new Integer(20), new Boolean(true), "X"},
			{"Philip", "Milne", "Pool", new Integer(10), new Boolean(false), "X"}
	};

	private static void createAndShowGUI() {

		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
/*
        table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        
        JScrollPane scrollPane = new JScrollPane(table);
        topPanel.add(scrollPane);
        */
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
	
	public Test1() {
		buttonColumn = new ButtonColumn(table, 4);
	}
	public static void main(String[] args) throws Throwable {
		
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
    class ButtonColumn extends AbstractCellEditor
    implements TableCellRenderer, TableCellEditor, ActionListener
{
    JTable table;
    JButton renderButton;
    JButton editButton;
    String text;

    public ButtonColumn(JTable table, int column)
    {
        super();
        this.table = table;
        renderButton = new JButton();

        editButton = new JButton();
        editButton.setFocusPainted( false );
        editButton.addActionListener( this );

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer( this );
        columnModel.getColumn(column).setCellEditor( this );
    }
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (isSelected)
        {
            renderButton.setForeground(table.getSelectionForeground());
             renderButton.setBackground(table.getSelectionBackground());
        }
        else
        {
            renderButton.setForeground(table.getForeground());
            renderButton.setBackground(UIManager.getColor("Button.background"));
        }

        renderButton.setText( (value == null) ? "" : value.toString() );
        return renderButton;
    }

    public Component getTableCellEditorComponent(
        JTable table, Object value, boolean isSelected, int row, int column)
    {
        text = (value == null) ? "" : value.toString();
        editButton.setText( text );
        return editButton;
    }

    public Object getCellEditorValue()
    {
        return text;
    }

    public void actionPerformed(ActionEvent e)
    {
        fireEditingStopped();
        System.out.println( "Action: " + e.getActionCommand() );
    }
}
}
