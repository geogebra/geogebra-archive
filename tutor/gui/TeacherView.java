package tutor.gui;

import geogebra.Application;
import geogebra.MyError;
import geogebra.MyResourceBundle;
import geogebra.View;
import geogebra.io.MyXMLio;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import tutor.model.Annotation;
import tutor.model.Justification;
import tutor.model.Strategy;
import tutor.persistence.dao.iface.JustificationDao;
import tutor.persistence.dao.iface.StrategyDao;

public class TeacherView extends JPanel implements View  {
	
	
	private Application app;
	private Kernel kernel;
	private MyXMLio xmlio;
	private TeacherController teacherController;
	
	//private DataBaseInterface dbi;
	
	private JustificationDao justificationDao;
	private StrategyDao strategyDao;
	
	private String alumne;
	private String problema;

	public static final int STUDENT = 0;
	public static final int TUTOR = 1;
	public static final int GRAPHICAL_INFO = 2;
	public static final int SENTENCE = 3;
	public static final int ARGUMENT = 4;
	public static final String studentName = "Eloi";
	public static final String tutorName = "Fortuny";
	public static final String LN = System.getProperty("line.separator");
	public static final String WELCOME = "Welcome!!!"+LN;
	
	private JComboBox justificationCombo;
	private JTextField commentField = new JTextField(70);
	private StyleContext styleContext = new StyleContext();
	private StyledDocument doc = new DefaultStyledDocument(styleContext);
	private JTextPane resultArea = new JTextPane(doc);
	
	private JTable table;
    private JTextField missatge;
    private JComboBox justificacions;
    ButtonColumn buttonColumn;
    
    DefaultTableModel model;

    private List strategies;

	private long lineCounter = 0;
	private ResourceBundle teacherResources;
	
	private List annotations = new ArrayList();
	
	String strategyFilesURL;
	
	public String getStrategyFilesURL() {
		return strategyFilesURL;
	}

	public void setStrategyFilesURL(String strategyFilesURL) {
		this.strategyFilesURL = strategyFilesURL;
	}

	public String getPlain(String key) {
	
		if (teacherResources == null) {
			teacherResources = MyResourceBundle.createBundle("/tutor/properties/plain", app.getLocale());
		}
		
		try {
			return teacherResources.getString(key);
		}
		catch (Exception e) {
			e.printStackTrace();
			return key;
		}
	}
	
	public TeacherView(String problema, String alumne, TeacherController tc) 
	{
		this.app = tc.getApp();
		this.kernel = tc.getKernel();
		this.teacherController = tc;
		this.problema = problema;
		this.alumne = alumne;
		tc.setView(this);
	}
	
	public void initDataModel() {
		
		//this.dbi = new DataBaseInterface();
		//strategies = this.dbi.retrieveStrategies(problema);
		//StrategyDao strategiesDao = new HttpStrategyDao();
		
		strategies = strategyDao.findStrategiesByProblemId(new Long(problema));

		//String context = "http://antalya.uab.es/edumat/agentgeom/problemes";
		String context = strategyFilesURL;
		
		for (Iterator it = strategies.iterator(); it.hasNext();) {
			Strategy strategy = (Strategy) it.next();
			try {
				
				String file = strategy.getFile();
				String strUrl = context + "/" + file;
				strategy.setUrl(strUrl);
				URL strategyUrl = new URL(strategy.getUrl());
				System.out.println(strategyUrl);
				Construction construction = getConstruction(strategyUrl);
				strategy.setConstruction(construction);
			}
			catch (Exception e) {
				System.out.println(e);
				app.showError(app.getError("Strategies Loading Process Failed. ") 
						+ "\n" + e.getMessage());
			}
		}
		
		Vector justs = getJustifications();
		for (Iterator it = justs.iterator(); it.hasNext();) {
			String j = (String) it.next();
			//justificationCombo.addItem(j);
			justificacions.addItem(j);
		}
		
		/*
		 if (strategies != null) 
		 //PROCEED STRATEGIES FILES
		//nom fitxer
			for (int i =0; i<strategies.length; i++){
				// Try to load Strategies files;
				System.out.println(strategies[0].getUrl());
				try {
					URL url = strategies[i].getUrl(); 
					Construction c = getConstruction(url);
					strategies[i].setConstruction(c);	
			    	System.out.println("Construction"+i+c.getXML());
				
				} catch (Exception e) {					
					app.showError(app.getError("Strategies Loading Process Failed. ") 
										+ "\n" + e.getMessage());
				}
			}
		 */
	}
	
	public Vector getJustifications() {
		
			Vector a = new Vector();
			a.add("");
		
			//JustificationDao dao = new HttpJustificationDao();
			List justs = justificationDao.findProblemJustifications(new Long(7));
			for (Iterator it = justs.iterator(); it.hasNext();) {
				Justification j = (Justification) it.next();
				a.add(j.getDescription());
			}
			
			return a;
	}
	
	public void createGUI() {
		/*
		resultArea.setBackground(Color.WHITE);
        resultArea.setText(WELCOME);
        resultArea.setEditable(false);

		//justificationCombo = new JComboBox(getJustifications());
        justificationCombo = new JComboBox();
		justificationCombo.addActionListener(teacherController);
	
        commentField.setEditable(true);
        commentField.setEnabled(true);
        commentField.addActionListener(teacherController);
*/
        createTecharPanel();
        
        try {
        	for (int i=0; i<20; i++)
        		doc.insertString(doc.getLength(), ""+LN, null);
        }catch (Throwable t) {}
	}
	
	private void createTecharPanel() {
		
		JPanel techerPanel = new JPanel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		model = new DefaultTableModel(0,3);
        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(350, 200));

        table.getSelectionModel().addListSelectionListener(new RowListener());
        table.getColumnModel().getSelectionModel().
            addListSelectionListener(new ColumnListener());
        
        techerPanel.add(new JScrollPane(table));

        techerPanel.add(new JLabel("Missatge"));
        missatge = new JTextField(30);
        missatge.setSize(20,5);
        techerPanel.add(missatge);
        
        techerPanel.add(new JLabel("Justificacio"));
        justificacions = new JComboBox();
        techerPanel.add(justificacions);

        add(techerPanel);
        
        buttonColumn = new ButtonColumn(table, 2);
	}
	
	public void processCommentField(){
		
		//String strSel = (String) justificationCombo.getSelectedItem();
		//int selectedUser = Integer.parseInt(strSel);
		
		//printTextArea(commentField.getText(), SENTENCE);
		
		//System.out.println(strSel);
		//commentField.setText("");
		//System.out.println(commentField.getText());
	}
	
	
	
	public void add(GeoElement geo) {		
		//Every GeoElement gets here.
		Construction c = geo.getConstruction();
		GeoElement[] goes = geo.getGeoElements();

		//TreeSet t = geo.getAllPredecessors();
		//if (strategies.size() == 0) {
		//System.out.println("STR1"+ ((Construction)strategies.getFirst()).getXML());
		//System.out.println("STR2"+ ((Construction)strategies.getLast()).getXML());
		//System.out.println("CMD"+geo.getCommandDescription());
		//System.out.println(geo+"/"+geo.getObjectType());
		//System.out.println(t);
		//System.out.println(c.getXML());
		//if (geo.getObjectType()  )
		
		printTextArea(objectToDialogue(geo), GRAPHICAL_INFO);
		//commentField.requestFocus();
		model.addRow(new Object[]{objectToDialogue(geo), "", ""});
		//model.setValueAt(aValue, row, column)
		
		
		
		
	}
	/*
	 * Method to pass GeoElement to Diaologue text
	 */
	private String objectToDialogue(GeoElement geo)
	{
		String objectType = geo.getObjectType();
		String objectLabel = geo.getLabel();
		String message = "";
		
		if (geo instanceof GeoBoolean) {
			GeoBoolean blnGeo = (GeoBoolean) geo;
			if (blnGeo.getBoolean()) {
				message = getPlain("valid.deduction");
			}
			else {
				message = getPlain("non-valid.deduction");
			}
		}
		else if (geo instanceof GeoFunction) {
			GeoFunction fnGeo = (GeoFunction) geo;
			message = ""+fnGeo.getNameDescription();
		}
		else {
			String objectTypeDesc = getPlain("geoelement.type."+objectType);
			String objectTypeArt = getPlain("geoelement.type."+objectType+".article").split(",")[1];
			
			message = "ha creat " + objectTypeArt + " " + objectTypeDesc + ": " + objectLabel;
		}
		
		return message;
	}
		
		//ConstructionProtocol cp = c.getApplication().getConstructionProtocol();
		

    
	/*
	 * Print text into Tutor Dialogue area.
	 */
	public void printTextArea(String text, int user)
	{
		switch(user) {
		
			case STUDENT:
				printStudentMessage(text);
				break;
				
			case TUTOR:
				printTutorMessage(text);
				break;
				
			case GRAPHICAL_INFO:
				printGraphicalInfo(text);
				break;
				
			case SENTENCE:
				printSentence(text);
				break;
				
			case ARGUMENT:
				printArgument(text);
				break;
				
			default:
				break;
		}
		
		String anos = showAnnotation();
		System.out.println(anos);
		
		// Scrolls text area
		resultArea.scrollRectToVisible(
				  new Rectangle(0,resultArea.getHeight()+10,1,1));
	}
	
	private void insertAnnotatedString(String text, String annotation, SimpleAttributeSet attributes) {
		
		int pos = doc.getLength();
		int length = text.length();
		
		try {
			doc.insertString(pos, text, attributes);
			
			Annotation anno = new Annotation();
			anno.setOffset(pos);
			anno.setLength(length);
			anno.setAnnotation(annotation);
			annotations.add(anno);
		}
		catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}
	
	private String showAnnotation() {
		
		String annotatedDoc = "";
		
		for (Iterator it = annotations.iterator(); it.hasNext();) {
			Annotation annotation = (Annotation) it.next();
			int pos = annotation.getOffset();
			int len = annotation.getLength();
			try {
				String text = doc.getText(pos, len);
				
				annotatedDoc += "<" + annotation.getAnnotation() + ">" + text +"<" + annotation.getAnnotation() + ">\n";
			}
			catch (BadLocationException ble) {
				ble.printStackTrace();
			}
		}
		
		return "<annotated-document>\n" + annotatedDoc +"</annotated-document>";
	}
	
	private void printStudentMessage(String text) {
	}
	
	private void printTutorMessage(String text) {
		
		lineCounter++;
		
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		
		try {
			StyleConstants.setForeground(attributes, Color.BLUE);
			StyleConstants.setItalic(attributes, true);
			
			doc.insertString(doc.getLength(), "Tutor: ", attributes);
			//doc.insertString(doc.getLength(), text+LN, attributes);
			insertAnnotatedString(text+LN, "tutorMessage", attributes);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	static long lastCount = -1;
	
	private void printGraphicalInfo(String text) {
		
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		
		try {
			StyleConstants.setForeground(attributes, Color.BLACK);
			String strLineCount = "";
			if (lastCount != lineCounter) {
				strLineCount = ""+lineCounter;
				lastCount = lineCounter;
			}
			doc.insertString(doc.getLength(), strLineCount+" - "+studentName+": ", attributes);
			//doc.insertString(doc.getLength(), text+LN, attributes);
			insertAnnotatedString(text+LN, "graphicalInfo", attributes);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void printSentence(String text) {
		
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		
		try {
			StyleConstants.setForeground(attributes, Color.BLACK);
			StyleConstants.setBold(attributes, true);
			
			//doc.insertString(doc.getLength(), studentName+": ", attributes);
			doc.insertString(doc.getLength(), "     ", attributes);
			//doc.insertString(doc.getLength(), text+LN, attributes);
			insertAnnotatedString(text+LN, "sentence", attributes);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void printArgument(String text) {
		
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		
		try {
			StyleConstants.setFontSize(attributes, 12);
			
			//doc.insertString(doc.getLength(), studentName+":   ", attributes);
			doc.insertString(doc.getLength(), "     ", attributes);
			//doc.insertString(doc.getLength(), text.toUpperCase()+LN, attributes);
			insertAnnotatedString(text.toUpperCase()+LN, "argumentation", attributes);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void clearView() {
		// TODO Auto-generated method stub

	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void repaintView() {
		// TODO Auto-generated method stub

	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	private void compareConstruction(GeoElement geo)
	{
		
		
	}

	 public Construction getConstruction(URL url) throws Exception
	 {		 
	    	// build macro construction
		 
		    Kernel k = new Kernel(app);
		    k.setUndoActive(false);
	    	xmlio = new MyXMLio(k,k.getConstruction());	    		    	
	    	try {        	
	    		xmlio.readZipFromInputStream(url.openStream(), false);
	    	} 
	    	catch (MyError e) {  
	    		String msg = e.getLocalizedMessage();
	    		System.err.println(msg);
	    		e.printStackTrace(); 
	    		throw new Exception(msg);
	    	}    	
	    	catch (Exception e) {
	    		e.printStackTrace();       		   
	        	throw new Exception(e.getMessage());
	    	}   	    	
	    	Construction c = k.getConstruction();
	    	
	    	
	    	// TODO: remove test block
	    	System.out.println("**** STRATEGY construction BEGIN");
	    	int i=0;
	    	while (c.getConstructionElement(i)!= null)
	    	{
	    		ConstructionElement ce = c.getConstructionElement(i);
				if (ce.isAlgoElement()) {
					System.out.println("algo: " + ce);	
				} else {
					System.out.println("geo: " + ce + ", free: " + ce.isIndependent());	
				}
				i++;
	    	}
	    	System.out.println("**** STRATEGY construction END");
	    	
	    	//System.out.println(k.getConstruction().getXML());
	    	return k.getConstruction();
	 }
	 
	 public void setCommentFieldFocus() {
		 commentField.requestFocusInWindow();
	 }
	 
	 public void incrementLineCounter() {
		 this.lineCounter++;
	 }

	public JustificationDao getJustificationDao() {
		return justificationDao;
	}

	public void setJustificationDao(JustificationDao justificationDao) {
		this.justificationDao = justificationDao;
	}

	public StrategyDao getStrategyDao() {
		return strategyDao;
	}

	public void setStrategyDao(StrategyDao strategyDao) {
		this.strategyDao = strategyDao;
	}

	public String getAlumne() {
		return alumne;
	}

	public void setAlumne(String alumne) {
		this.alumne = alumne;
	}

	public String getProblema() {
		return problema;
	}

	public void setProblema(String problema) {
		this.problema = problema;
	}

	public Application getApp() {
		return app;
	}

	public void setApp(Application app) {
		this.app = app;
	}

	public Kernel getKernel() {
		return kernel;
	}

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	public JComboBox getJustificationCombo() {
		return justificationCombo;
	}

	public void setJustificationCombo(JComboBox justificationCombo) {
		this.justificationCombo = justificationCombo;
	}

	public JTextField getCommentField() {
		return commentField;
	}

	public void setCommentField(JTextField commentField) {
		this.commentField = commentField;
	}

	public StyleContext getStyleContext() {
		return styleContext;
	}

	public void setStyleContext(StyleContext styleContext) {
		this.styleContext = styleContext;
	}

	public StyledDocument getDoc() {
		return doc;
	}

	public void setDoc(StyledDocument doc) {
		this.doc = doc;
	}

	public JTextPane getResultArea() {
		return resultArea;
	}

	public void setResultArea(JTextPane resultArea) {
		this.resultArea = resultArea;
	}

	/*
	public JButton getBotoNou() {
		return botoNou;
	}

	public void setBotoNou(JButton botoNou) {
		this.botoNou = botoNou;
	}

	public JButton getBotoGuardar() {
		return botoGuardar;
	}

	public void setBotoGuardar(JButton botoGuardar) {
		this.botoGuardar = botoGuardar;
	}
*/
	public long getLineCounter() {
		return lineCounter;
	}

	public void setLineCounter(long lineCounter) {
		this.lineCounter = lineCounter;
	}

	public ResourceBundle getTutorResources() {
		return teacherResources;
	}

	public void setTutorResources(ResourceBundle tutorResources) {
		this.teacherResources = tutorResources;
	}

	public List getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List annotations) {
		this.annotations = annotations;
	}

    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            //output.append("ROW SELECTION EVENT. ");
            //outputSelection();
        	int i = table.getSelectionModel().getLeadSelectionIndex();
        	String str = (String) model.getValueAt(i, 0);
        	System.out.println(str);
        	missatge.setText(str);
        }
    }

    private class ColumnListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            //output.append("COLUMN SELECTION EVENT. ");
            //outputSelection();
        }
    }

    class MyTableModel extends DefaultTableModel {
        private String[] columnNames = {"accio", "", ""};
        private Object[][] data = {
            {"","",""}, {"","",""}
        };

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 2) {
                return false;
            } else {
                return true;
            }
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

    }

    class ButtonColumn extends AbstractCellEditor
    implements TableCellRenderer, TableCellEditor, ActionListener {

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

    		renderButton.setSize(10, 10);
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
