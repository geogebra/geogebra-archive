package geogebra.cas.view.components;

import java.awt.*;
import javax.swing.*;

/**
 * TestFrame_TMP 
 * <pre>
 * For testing of menues, buttons and buttonmodel in GeoGebra CAS
 * Temporary; to be erased before final release
 * 
 * @author      Hans-Petter Ulven
 * @version     01.03.09
 */
public class TestFrame_TMP extends JFrame {

    private     final   static  boolean DEBUG   =   true;

    /// --- Properties --- ///
    
    Container cp = null;          //Panel inne i JFrame
        
    /// --- Interface --- ///

    /** Constructor */
    public TestFrame_TMP()  {
      cp=this.getContentPane();
      cp.setLayout(new BorderLayout());

      setTitle("TestFrame_TMP");
      setSize(500,300);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      gui();                     //Ordner GUI...
      setVisible(true);        
      toFront();
      requestFocus();       
    }//Constructor
    
         
    /// --- Private: --- ///
    private void gui() {
        BtnPanel    btnpanel=BtnPanel.getInstance(null);
        cp.add(btnpanel,BorderLayout.NORTH);
    }//gui()
    
    //DEBUG:
    private final static void debug(String s) {
        if(DEBUG) {
            System.out.print("\nTestFrame_TMP:   ");
            System.out.println(s);
        }//if()
    }//debug()

    //main til uttesting:
    public static void main(String[] args) {
        new TestFrame_TMP();
    }//main()

}//class TestFrame_TMP