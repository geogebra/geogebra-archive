package tutor;

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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
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



public class TutorView extends JPanel implements View  {
	
	
	private Application app;
	private Kernel kernel;
	private MyXMLio xmlio;
	private TutorController tutorController;
	
	private DataBaseInterface dbi;
	
	private JustificationDao justificationDao;
	private StrategyDao strategyDao;
	
	private String alumne;
	private String problema;

	private static final int STUDENT = 0;
	private static final int TUTOR = 1;
	private static final int GRAPHICAL_INFO = 2;
	private static final int SENTENCE = 3;
	private static final int ARGUMENT = 4;
	private static final String studentName = "Eloi";
	private static final String tutorName = "Fortuny";
	private static final String LN = System.getProperty("line.separator");
	private static final String WELCOME = "Welcome!!!"+LN;
	
	private JComboBox justificationCombo;
	private JTextField commentField = new JTextField(70);
	private StyleContext styleContext = new StyleContext();
	private StyledDocument doc = new DefaultStyledDocument(styleContext);
	private JTextPane resultArea = new JTextPane(doc);
	private DefaultListModel listModel = new DefaultListModel(); 
	private JList resArea = new JList();
	
	private Strategy[] strategies;

	private long lineCounter = 0;
	private ResourceBundle tutorResources;
	
	private List annotations = new ArrayList();
	
	public String getPlain(String key) {
	
		if (tutorResources == null) {
			tutorResources = MyResourceBundle.createBundle("/tutor/properties/plain", app.getLocale());
		}
		
		try {
			return tutorResources.getString(key);
		}
		catch (Exception e) {
			return key;
		}
	}
	
	public TutorView(String problema, String alumne, TutorController tc) 
	{
		this.app = tc.getApp();
		this.kernel = tc.getKernel();
		this.tutorController = tc;
		this.problema = problema;
		this.alumne = alumne;
		tc.setView(this);
	}
	
	public void initDataModel() {
		
		//this.dbi = new DataBaseInterface();
		//strategies = this.dbi.retrieveStrategies(problema);
		//StrategyDao strategiesDao = new HttpStrategyDao();
		
		List strats = strategyDao.findStrategiesByProblemId(new Long(problema));

		for (Iterator it = strats.iterator(); it.hasNext();) {
			Strategy strategy = (Strategy) it.next();
			try {
				URL strategyUrl = new URL(strategy.getUrl());
				Construction c = getConstruction(strategyUrl);
			}
			catch (MalformedURLException mue) {
				
			}
			catch (Exception e) {
				
			}
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
		
		resultArea.setBackground(Color.WHITE);

		justificationCombo = new JComboBox(getJustifications());
		justificationCombo.addActionListener(new ActionListener() {
    		public void actionPerformed( ActionEvent evt ) {
    			JComboBox cb = (JComboBox) evt.getSource();
    	        String just = (String)cb.getSelectedItem();    			
    	        printTextArea(just, ARGUMENT);
    		}});
	
		
	        resultArea.setText(WELCOME);
	        resultArea.setEditable(false);
	        commentField.setEditable(true);
	        commentField.setEnabled(true);
	        
	
	        JScrollPane scrollingArea = new JScrollPane(resultArea);
	        
	        //resultArea.setSize(300,600);
	        //scrollingArea.setSize(300,600);
	        
	        //... Get the content pane, set layout, add to center
	        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	        
	        //setLayout(new FlowLayout());
	        add(scrollingArea);
	        //scrollingArea.setSize(200,400);
	        //add(Box.createVerticalGlue());  

	        add(new JLabel("Comments:",SwingConstants.LEFT));
	       // commentField.setMaximumSize(commentField.getPreferredSize());
	        
	        commentField.addActionListener(new ActionListener() {
	        		public void actionPerformed( ActionEvent evt ) {
	        			processCommentField();
	        		}
	        }
	        );
	        
	        add(commentField);
	        //add( new JTextArea(3, 70));
	        //add( new JTextArea(3, 70));
	        add(justificationCombo);
	        //add( new JTextArea(3, 70));
	        
	        
	        try {
	        	for (int i=0; i<20; i++)
	        		doc.insertString(doc.getLength(), ""+LN, null);
	        }catch (Throwable t) {}
	        
	        
	}
	
	private void processCommentField(){
		
		//String strSel = (String) justificationCombo.getSelectedItem();
		//int selectedUser = Integer.parseInt(strSel);
		
		printTextArea(commentField.getText(), SENTENCE);
		
		//System.out.println(strSel);
		commentField.setText("");
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
		commentField.requestFocus();
		
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
	private void printTextArea(String text, int user)
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

	 private Construction getConstruction(URL url) throws Exception
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
}

