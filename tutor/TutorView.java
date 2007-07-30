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
import java.util.LinkedList;

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
	private LinkedList strategies = new LinkedList();
	private DataBaseInterface dbi;
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://158.109.2.26:33/intermates";
	private static final String USER ="jmfortuny";
	private static final String PWD = "jmfortuny";
	private static final int STUDENT = 0;
	private static final int TUTOR = 1;
	private static final String studentName = "Eloi";
	private static final String tutorName = "Fortuny";
	private static final String WELCOME = "Welcome!!!\n";
	private JTextArea resultArea = new JTextArea(40, 20);
	private JComboBox justificationCombo = new JComboBox();
	private JTextField commentField = new JTextField(70);
	
	public TutorView (String[] strategiesXML,Application app) 
	{
		this.app = app;
		this.dbi = new DataBaseInterface(DRIVER, URL, USER, PWD);
		
		if (strategiesXML != null) 
		// PROCEED STRATEGIES FILES
			//nom fitxer
			for (int i =0; i<strategiesXML.length; i++){
				// Try to load Strategies files;
				try {
					Strategy str = new Strategy();
					// What is passed through applet params?
					str.fillData(dbi,strategiesXML[i]);
	       			URL url = str.getURL(); 
	       			
	       			handleFileArg(strategiesXML[i]); 
			    
	       			Construction c = getConstruction(url);
			    	str.setConstruction(c);
			    	//System.out.println("Construction"+i+c.getXML());
				 strategies.add(str);
				} catch (Exception e) {					
					app.showError(app.getError("Strategies Loading Process Failed. ") 
										+ "\n" + e.getMessage());
				}
			}
	
		createGUI();
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
		System.out.println(commentField.getText());
	}
	 private URL handleFileArg(String fileArgument) {	     	      
	        try {             		        	
	        	String lowerCase = fileArgument.toLowerCase();
	            URL url=null;
	        	if (lowerCase.startsWith("http") || 
	            	lowerCase.startsWith("file")) {         
	                 url = new URL(fileArgument);                                        
	            } else {                       	
	                File f = new File(fileArgument);
	                f = f.getCanonicalFile();
	                if (f.exists())
	                	url = f.toURL();
	                else new Exception("File doesn't exists");
	            }
	            return url;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
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
		return geo.getObjectType()+":"+geo.getLabel()+"\n";
	}
		
		//ConstructionProtocol cp = c.getApplication().getConstructionProtocol();
		
	/*
	 * Print text into Tutor Dialogue area.
	 */
	private void printTextArea(String txt, int user)
	{
		
		if (user==STUDENT) {
		
		//	resultArea.set
			resultArea.append(studentName+":"+txt);
		
		}
		else if (user == TUTOR){
			
			resultArea.append(tutorName+":"+txt);
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
}

