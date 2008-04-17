package geogebra.plugin;
/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
import java.util.*;
import java.io.*;
//unused: import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.reflect.*;
import geogebra.Application;   
import java.net.URL;


/** 
<h3>PluginManager for GeoGebra</h3>
<ul><b>Interface:</b>
<li>PluginManager(Application);                         //Owned by Application
<li>getPluginMenu():JMenu                               //Menubar<-Application<-PluginManager
</ul>
@author      H-P Ulven
@version     17.04.08
*/
public class PluginManager implements ActionListener{       //Listens on PluginMenu
 
    private final static boolean    DEBUG=          true;  
    private final static String     PLUGINFILE=     "plugin.properties";
    
    ///// ----- Properties ----- /////
    private Properties              properties=     new Properties();
    private Hashtable               plugintable=    new Hashtable();        //1.4.2: Not generics :-\ String,pluginclass
    private geogebra.GeoGebra       ggb=            null;
    private geogebra.Application    app=            null;
    private JMenu                   pluginmenu=     new JMenu("Plugins");   //Make it here, let Application and Menubar get it
    private String                  startdir=       null;
//    private String                  startdir_nosp=  null;
   
    ///// ----- Interface ----- /////
   
   /** Constructor */
    public PluginManager(Application app) {
        this.app=app;               //ref to Ggb application
        try{
            startdir=new File("").getCanonicalPath();    debug("startdir: "+startdir);
//            startdir_nosp=startdir.replace(" ", "%20");
            
//            System.out.println("startdir_nosp="+startdir_nosp);
        }catch(IOException ioe){
            System.out.println("Could not find homedir!");
        }//try-catch
        loadProperties();   
        this.init();
    }//PluginManager()
    
    /** Returns pluginmenu. Called from Application */
    public JMenu getPluginMenu(){
        return pluginmenu;
    }//getPluginMenu()
    
    ///// ----- Private ----- /////   
    /* Init - called from constructor */
    private void init() {
        String      name,path;
        Class       pluginclass;    //class
        PlugLetIF   plugin;         //object
        JMenuItem   menuitem=null;
        String      menutext=null;
        Enumeration names=properties.propertyNames();  
        while(names.hasMoreElements()){
            name=(String)names.nextElement();
            path=properties.getProperty(name);      
            
            System.out.println("startdir="+startdir);
            
            try{                                    
                debug("Before...");
                /*** change: ClassPathManipulator.listClassPath(); */
                debug(ClassPathManipulator.getClassPath()) ;
                debug("property read: "+name+", "+path);
                File f=new File(path);
                URL  u=f.toURL(); 
                /*** change: ClassPathManipulator.addFile(u.toString());  
 */
                ClassPathManipulator.addURL(u);
                debug("After");
                /*** change: ClassPathManipulator.listClassPath(); */
                debug(ClassPathManipulator.getClassPath());

                plugin=getPluginInstance(name);     //reflect out an instance of plugin from class
                plugintable.put(name,plugin);
                menuitem=new JMenuItem(plugin.getMenuText());
                menuitem.setName(name);
                menuitem.addActionListener(this);
                pluginmenu.add(menuitem);
            }catch(Exception e){
                System.out.println("Could not handle class: "+name);
                System.out.println(e.toString());
                pluginclass=null;
                e.printStackTrace();
                }//try-catch
        }//while more properties        
    }//init()
    
    public void actionPerformed(ActionEvent ae){
        JMenuItem   mi=(JMenuItem)ae.getSource();
        String name=mi.getName();
        Object o=   plugintable.get(name);
        if(o instanceof PlugLetIF){
            PlugLetIF   plugin=(PlugLetIF)plugintable.get(name);
            plugin.execute(app.getGgbApi());
        }else{
            System.out.println("Not PlugLetIF in plugintable!");
        }//if-else
    }//actionPerformed(ActionEvent)
    
    /// --- Private: --- ///

    /** Get instance from class in plugintable */
    private PlugLetIF getPluginInstance(String name) {           //debug: public during testing
        try{
            Class   c   =   Class.forName(name);
            Class[] empty=new Class[]{};
            Method  get =   c.getMethod("getInstance",empty);   //Use Singleton DP!
            // Not: 
            // PlugLetIF   o   =   (PlugLetIF)c.newInstance(); //c.newInstance();
            // which is not able to enforce Singleton DP...
            // Later: Might use XML install file, and not instantiate before called
            Object[] emptyobj=new Object[] {};  /*** change */
            PlugLetIF   o   =   (PlugLetIF)get.invoke(c,emptyobj);
            return (PlugLetIF)o;
        }catch(Throwable e){
            System.out.println(e.toString());
            return null;
        }//try-catch
    }//getPluginInstance()
    
    /** Loads properties from plugin.properties */
    private void loadProperties() {
        BufferedInputStream is=null;
        try{
            is=new BufferedInputStream(new FileInputStream(new File(startdir+PLUGINFILE)));
            properties.load(is);
            is.close();
        }catch(Exception e) {
            try{
                is=new BufferedInputStream(new FileInputStream(new File(startdir+java.io.File.separator+PLUGINFILE)));
                properties.load(is);
                is.close();
            }catch(Exception e2) {
            	System.out.println(PLUGINFILE+" not found=>No pluginmenu.");
            }//try-catch
        }//try-catch
    }//loadProperties();    

    ///// ----- Debug ----- /////
    private final static void debug(String s) {
        if(DEBUG) {
            System.out.print("\nPluginManager:   ");
            System.out.println(s);
        }//if()
    }//debug()
            
}// class PluginManager


