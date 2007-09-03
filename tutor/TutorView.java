package tutor;

import geogebra.Application;
import geogebra.MyError;
import geogebra.View;
import geogebra.io.MyXMLio;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



public class TutorView extends JPanel implements View {
	
	
	private Application app;
	private MyXMLio xmlio;
	
	private DataBaseInterface dbi;
	
	private static final int STUDENT = 0;
	private static final int TUTOR = 1;
	private static final String studentName = "Eloi";
	private static final String tutorName = "Fortuny";
	private static final String WELCOME = "Welcome!!!\n";
	private JTextArea resultArea = new JTextArea(40, 20);
	private JComboBox justificationCombo;
	private JTextField commentField = new JTextField(70);
	private static final String LN = System.getProperty("line.separator");
	private Strategy[] strategies;

	
	public TutorView (String problema, String alumne,Application app) 
	{
		this.app = app;
		this.dbi = new DataBaseInterface();
		strategies = this.dbi.retrieveStrategies(problema);
		
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
		justificationCombo = new JComboBox(getJustifications());
	
		createGUI();
	}
	public Vector getJustifications() {
		
			Vector a = new Vector();
			a.add("");
			a.add("1");
			a.add("2");
			
			return a;
	}
	public void createGUI() {
		
		// TODO: implement user interface of tutor view
		//add(new JLabel("Hello Eloi!"));
	
		
	        resultArea.setText(WELCOME);
	        resultArea.setEditable(false);
	        commentField.setEditable(true);
	        commentField.setEnabled(true);
	        
	        JScrollPane scrollingArea = new JScrollPane(resultArea);
	        //... Get the content pane, set layout, add to center
	        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	        add(scrollingArea);
	        
	        add(Box.createVerticalGlue());  

	        add(new JLabel("Comments:",SwingConstants.LEFT));
	       // commentField.setMaximumSize(commentField.getPreferredSize());
	        commentField.addActionListener(new ActionListener() {
	        		public void actionPerformed( ActionEvent evt ) {
	        			processCommentField();
	        		}
	        }
	        );
	        add(commentField);
	        
	        add(justificationCombo);
	        
	}
	
	private void processCommentField(){
		
		printTextArea(commentField.getText(),STUDENT);
		commentField.setText("");
		//System.out.println(commentField.getText());
	}
	
	
	
	public void add(GeoElement geo) {		
		//Every GeoElement gets here.
		//Construction c = geo.getConstruction();
		
		//TreeSet t = geo.getAllPredecessors();
		//if (strategies.size() == 0){
		//System.out.println("STR1"+ ((Construction)strategies.getFirst()).getXML());
		//System.out.println("STR2"+ ((Construction)strategies.getLast()).getXML());
		//System.out.println("CMD"+geo.getCommandDescription());
		//System.out.println(geo+"/"+geo.getObjectType());
		//System.out.println(t);
	//	System.out.println(c.getXML());
		//if (geo.getObjectType()  )
		
		printTextArea(objectToDialogue(geo),TUTOR);
		commentField.requestFocus();
		
	}
	/*
	 * Method to pass GeoElement to Diaologue text
	 */
	private String objectToDialogue(GeoElement geo)
	{
		return geo.getObjectType()+":"+geo.getLabel();
	}
		
		//ConstructionProtocol cp = c.getApplication().getConstructionProtocol();
		
	/*
	 * Print text into Tutor Dialogue area.
	 */
	private void printTextArea(String txt, int user)
	{
		
		if (user==STUDENT) {
//			resultArea.setForeground(Color.GREEN);
			resultArea.append(studentName);
		}
		else if (user == TUTOR){
//			resultArea.setForeground(Color.BLUE);
			resultArea.append(tutorName);
		}
		//resultArea.setForeground(Color.BLACK);
		resultArea.append(": "+txt+LN);
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
}

