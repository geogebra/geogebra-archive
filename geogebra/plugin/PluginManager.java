package geogebra.plugin;
/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.reflect.*;
import geogebra.Application;   
import geogebra.plugin.ClassPathManipulator;


/** 
<pre>
<h3>PluginManager for GeoGebra</h3>
<ul><b>Interface:</b>
<li>PluginManager(Application);                         //Owned by Application
<li>getPluginMenu():JMenu                               //Menubar <- Application <- PluginManager
<li>getClassPathManipulator():ClassPathManipulator()    //For interactive adding of plugins
<li>addPath(String)                                     //For interactive adding of plugins
<li>addPlugin(classname,args)                           //For interactive adding of plugins
</ul>
</pre>
@author      H-P Ulven
@version     25.05.08
*/
public class PluginManager implements ActionListener{       //Listens on PluginMenu
 
    private final static boolean    DEBUG=          true;  
    private final static String     PLUGINFILE=     "plugin.properties";
    private final static String		nl=				System.getProperty("line.separator");
    
    
    ///// ----- Properties ----- /////
   
    private Hashtable               plugintable=    new Hashtable();        //1.4.2: Not generics :-\ String classname,pluginclass
    private geogebra.GeoGebra       ggb=            null;
    private geogebra.Application    app=            null;
    private JMenu                   pluginmenu=     null;   //Make it here, let Application and Menubar get it
    private String                  startdir=       null;
    private ArrayList               lines=          new ArrayList();
    private ClassPathManipulator    cpm=            new ClassPathManipulator();
   
    ///// ----- Interface ----- /////
   
   /** Constructor */
    public PluginManager(Application app) {
        this.app=app;               //ref to Ggb application
        try{
            startdir=new File("").getCanonicalPath();                   debug("startdir: "+startdir);
//            startdir_nosp=startdir.replace(" ", "%20");      
//            System.out.println("startdir_nosp="+startdir_nosp);
        }catch(IOException ioe){
            System.out.println("PluginManager could not find start directory!");
        }catch(Throwable t){	//27.05.08: To keep ggb from crashing from errors in plugin.properties or applet security problems
        	System.out.println("Pluginmanager:"+t.toString());
        }//try-catch
        addPath(".");
        loadProperties();   
    }//PluginManager()
    
    /** Returns pluginmenu. Called from Application */
    public JMenu getPluginMenu(){
        if(pluginmenu==null){
            this.installPlugins();         //
        }//if null
        return pluginmenu;
    }//getPluginMenu()
    
    /** Returns reference to ClassPathManipulator */
    public ClassPathManipulator getClassPathManipulator(){
        return this.cpm;
    }//getClassPathManipulator()
    
    /** Add path for a plugin on the net to classpath */
    public void addURL(String path){
    	URL url=null;
    	try{
    		url=new URL(path);
    		cpm.addURL(url);
        }catch(MalformedURLException e) {
            System.out.println("PluginManager.addPath: MalformedURLExcepton for "+path);
        }catch(Throwable e){
            System.out.println("PluginManager.addPath: "+e.getMessage()+" for "+path);
        }//try-catch   
    }//addURL(String)
    
    /** Add path for a plugin to classpath*/
    public void addPath(String path){
        File file=null;
        URL  url=null;        
        try{
        	if(path.startsWith("http://")){	//url!
        		url=new URL(path);
        	}else{							//local file!
        		file=new File(path);
        		url=file.toURL();
        	}
            cpm.addURL(url);
        }catch(MalformedURLException e) {
            System.out.println("PluginManager.addPath: MalformedURLExcepton for "+path);
        }catch(Throwable e){
            System.out.println("PluginManager.addPath: "+e.getMessage()+" for "+path);
        }//try-catch        
    }//addPath(String)
        
    /** Installs a plugin given classname and args
     *  (Public: Can be used in scripting and interactively.)
     */
    public void addPlugin(String cname,String args){

        PlugLetIF plugin=null;
        JMenuItem   menuitem=null;
        //String      menutext=null;        
                
        // Reflect out class and install:    
                                                                                    //cpm.listClassPath();
                                                                                    
                                                                                    debug("addPlugin: "+cname+","+args);
        plugin=getPluginInstance(cname);   //reflect out an instance of plugin
        if(plugin!=null) {                                                          debug("plugin.getMenuText(): "+plugin.getMenuText());
            plugin.init(app.getGgbApi(),args);  //new syntax
            plugintable.put(cname,plugin);      //put in hashtable
            menuitem=new JMenuItem(plugin.getMenuText()); //make menuitem
            menuitem.setName(cname);
            menuitem.addActionListener(this);
            if(pluginmenu==null){pluginmenu=new JMenu("Plugins");}
            pluginmenu.add(menuitem);           //add to menu   
        }else{
           System.out.println("PluginManager could not reflect out plugin "+cname);
        }//if plugin null
    }//addPlugin(cname,patharray[],args)
    
    ///// ----- Private ----- /////   
    
    /*  installPlugins - called from constructor 
     *  Makes pluginmenu from lines read from plugin.properties
     */
    private void installPlugins() {
        String      cname,args,line,rest,token="";
        String[]    tokens;
        File        file;
        URL         url;
        ArrayList   paths=new ArrayList();
        Class       pluginclass;    //class
        PlugLetIF   plugin;         //object
        JMenuItem   menuitem=null;
        String      menutext=null;
        // not here, only if not empty in addPlugin: pluginmenu=new JMenu("Plugins");
        plugintable.clear();
        for(int i=0;i<lines.size();i++){    //for all lines in plugin.properties
            paths.clear();
            line=(String)lines.get(i);      
            line=line.trim();
            if( line.startsWith("#") ||     //comment or
                (line.indexOf("=")==-1)       ){  //wrong syntax or blank
                //ignore, nothing to do...
                //debug("PluginManager ignored: "+line);
            }else{
                tokens=line.split("=");
                cname=tokens[0].trim();
                if(!cname.equals("")){      //cname exists
                    args="";                                    debug("Class "+cname+":");
                    if(tokens.length>1){    //if more...
                        rest=tokens[1].trim();
                        tokens=rest.split(",");
                        for(int j=0;j<tokens.length;j++){
                            token=tokens[j].trim();
                            if(token.matches(".*\\.jar")){                  debug("\tPath "+token);
                                paths.add(token);
                            }else if (token.matches("\\{.*\\}")){  //args
                                token=token.substring(1,token.length()-1);    debug("\tArgs: "+args);
                                args=token;     //remember args, only one...(if more the last one counts)
                            }else{      //Must be just a path or an error...
                                paths.add(token);                           debug("\tPath "+token);
                            }//if-else
                        }//for all params
                    }//if more tokens: If not: just a class,
                    //install cname:
                    addPaths(paths);
                    addPlugin(cname,args);                    //debug(cpm.getClassPath());
                }//if cname not blank
            }//if not plugin statement
        }//for all lines in plugin.properties
    }//installPlugins()

    
    /** Implementing ActionListener for MenuItems */
    public void actionPerformed(ActionEvent ae){
        JMenuItem   mi=(JMenuItem)ae.getSource();
        String name=mi.getName();
        Object o=   plugintable.get(name);
        if(o instanceof PlugLetIF){
            PlugLetIF   plugin=(PlugLetIF)plugintable.get(name);
            //plugin.execute(app.getGgbApi());
            plugin.execute();
        }else{
            System.out.println("No PlugLetIF called "+name+"in plugintable!");
        }//if-else
    }//actionPerformed(ActionEvent)
    
    /// --- Private: --- ///
    private void addPaths(ArrayList paths){
        String path=null;
        int n=paths.size();
        for(int i=0;i<n;i++) {
            path=(String)paths.get(i);
            this.addPath(path);
        }//for all paths         
    }//addPaths(ArrayList)    

    /** Get instance from class in plugintable */
    private PlugLetIF getPluginInstance(String name) {
        PlugLetIF   pluglet=null;
        String method=name+".getInstance()";                //For exception messages
        try{
            Class   c   =   Class.forName(name);            debug(c.getName());
            Class[] empty=new Class[]{};
            Method  get =   c.getMethod("getInstance",empty);   //Use Singleton DP!
            // Not: 
            // PlugLetIF   o   =   (PlugLetIF)c.newInstance(); //c.newInstance();
            // which is not able to enforce Singleton DP...
            // Later: Preferable not to instantiate plugin before it is used/called...
            Object[] emptyobj=new Object[] {};  /*** change */
            Object   o   =   get.invoke(c,emptyobj);
            pluglet= (PlugLetIF)o;
        } catch (NoSuchMethodException t){
            System.out.println("PluginManager: "+method+" gives NoSuchMethodExcepton.");
        }catch(IllegalAccessException e){
            System.out.println("PluginManager: "+method+" gives IllegalAccesException.");
        }catch(InvocationTargetException e){
            System.out.println("PluginManager: "+method+" gives InvocationTargetException");
        }catch(Throwable t){
            System.out.println("PluginManager: "+method+" gives "+t.toString());
        }//end try catch            
        return pluglet;
    }//getPluginInstance()
    
    /** Loads properties from plugin.properties */
    private void loadProperties() {
        ClassLoader loader=this.getClass().getClassLoader();
        InputStream is=loader.getResourceAsStream(PLUGINFILE);
        if(is==null){
            System.out.println("PluginManager cannot find "+PLUGINFILE);
        }else{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            try{
                while((line=br.readLine())!=null) {            
                lines.add(line);                //debug(line);
            }//while lines
        }catch(IOException ioe){
            System.out.println("PluginManager: IOException reading "+PLUGINFILE);
        }//try-catch
        }//if is
    }//loadProperties();    

    ///// ----- Debug ----- /////
    private final static void debug(String s) {
        if(DEBUG) {
            System.out.print("\nPluginManager:   ");
            System.out.println(s);
        }//if()
    }//debug()
    
}// class PluginManager


