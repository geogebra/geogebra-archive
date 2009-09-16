package geogebra.gui.virtualkeyboard;

import geogebra.main.Application;
import geogebra.main.MyResourceBundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class start_vk {

   //public static Hashtable<String, String> myConf = new Hashtable<String, String>();
   public static Hashtable<String, keys>   myKeys = new Hashtable<String, keys>();

   /**
    * @param args
    *
   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
        	   myConf = defaultConf.setDefault(myConf);
            myKeys = defaultConf.setKeyboard(myKeys);
            readConf();
            vk_gui thisClass = new vk_gui();
            thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            thisClass.setVisible(true);
         }
      });
   } //*/

   /**
    * This method initializes the Virtual Keyboard
    *
    * @return void
    */
   public static void readConf() {
	   
		ResourceBundle rbKeyboard = MyResourceBundle.loadSingleBundleFile("/geogebra/gui/virtualkeyboard/keyboard");
		Enumeration keys = rbKeyboard.getKeys();
		while (keys.hasMoreElements()) {
			String keyU = (String) keys.nextElement();
			
			if (keyU.endsWith("U")) {
				keys keyItem = new keys();
				String key = keyU.substring(0,keyU.length() - 1);

				String valueU = rbKeyboard.getString(keyU);
				String valueL = rbKeyboard.getString(key+"L");
				
				
				keyItem.setLowerCase(valueL);
				keyItem.setUpperCase(valueU);
				
				//Application.debug(key+"char "+valueL+" "+valueU);
				
				myKeys.put(key+"char",keyItem);
			}
		}	

		/*
		ResourceBundle rbKeyboardPrefs = MyResourceBundle.loadSingleBundleFile("/geogebra/gui/virtualkeyboard/window");
		keys = rbKeyboardPrefs.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			
			myConf.put(key.trim(), rbKeyboardPrefs.getString(key).trim());
			} */
		}	

                        
   
}
