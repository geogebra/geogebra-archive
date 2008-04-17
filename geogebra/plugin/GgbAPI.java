package geogebra.plugin;
/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */
import geogebra.Application;
import geogebra.kernel.Kernel;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.Construction;


/** 
<h3>GgbAPI - API for PlugLets </h3>
<pre>
   The Api the plugin program can use.
</pre>
<ul><h4>Interface:</h4>
<li>GgbAPI(Allication)      //Application owns it
<li>getApplication()
<li>getKernel()
<li>getConstruction()
<li>getAlgebraProcessor()
<li>evalCommand(String)
<li>and the rest of the methods from the Applet JavaScript/Java interface
<li>...
</ul>
@author      H-P Ulven
@version     11.04.08
*/


public class GgbAPI {

    ///// ----- Properties ----- /////
    private Application         app=                null;   //References ...
    private Kernel              kernel=             null;
    private Construction        construction=       null;
    private AlgebraProcessor    algebraprocessor=   null;
    ///// ----- Interface ----- /////
   
   /** Constructor:
    *  Makes the api with a reference to the GeoGebra program.
    *  Called from GeoGebra.
    */
    public GgbAPI(Application app) {
        this.app=app;
        kernel=app.getKernel();
        algebraprocessor=kernel.getAlgebraProcessor();
        construction=kernel.getConstruction();
    }//Constructor
    
    /** Returns reference to Application */
    public Application getApplication(){return this.app;}
    
    /** Returns reference to Construction */
    public Construction getConstruction(){return this.construction;}
    
    /** Returns reference to Kernel */
    public Kernel getKernel(){return this.kernel;}
    
    /** Returns reference to AlgebraProcessor */
    public AlgebraProcessor getAlgebraProcessor(){return this.algebraprocessor;}

    /** Executes a GeoGebra command */
    public void evalCommand(String cmd) {
        if(algebraprocessor!=null) {
            //ggb.evalCommand(cmd);
            algebraprocessor.processAlgebraCommand(cmd, true);
        }else{
            System.out.println("Cannot find the GeoGebra AlgebraProcessor!");
        }//if ggb not null
    }//evalCommand(String)
    
    /// --- and some others ... --- ///
          
}// class GgbAPI

