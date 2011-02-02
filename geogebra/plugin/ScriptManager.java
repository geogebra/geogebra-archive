package geogebra.plugin;

import geogebra.kernel.GeoElement;
import geogebra.kernel.View;
import geogebra.main.Application;
import geogebra.usb.USBLogger;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.SensorDataProducer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


public class ScriptManager {
	
	private Application app;
	boolean listenersEnabled = true;
	
	// library of functions that is available to all JavaScript calls
	// init() is called when GeoGebra starts up (eg to start listeners)
	/*
	private String libraryScriptxxx ="function ggbOnInit() {}";
	private String libraryScriptxx ="function ggbOnInit() {"+
		"ggbApplet.evalCommand('A=(1,2)');" +
	//"ggbApplet.registerAddListener('listener');" +
	"ggbApplet.registerObjectUpdateListener('A','listener');" +
			"}" +
			"function listener() {//java.lang.System.out.println('add listener called');\n" +
			"var x = ggbApplet.getXcoord('A');" +
			"var y = ggbApplet.getYcoord('A');" +
			"var len = Math.sqrt(x*x + y*y);" +
			"if (len > 5) { x=x*5/len; y=y*5/len; }" +
			"" +
			"ggbApplet.unregisterObjectUpdateListener('A');" +
			"ggbApplet.setCoords('A',x,y);" +
			"ggbApplet.registerObjectUpdateListener('A','listener');" +
			"}";*/
	
	public ScriptManager(Application app) {
		this.app = app;
		
		//evalScript("ggbOnInit();");
	}
	
	public void ggbOnInit() {
		
		// call only if libraryJavaScript is not the default (ie do nothing)
		if (!app.getKernel().getLibraryJavaScript().equals(app.getKernel().defaultLibraryJavaScript))
				evalScript("ggbOnInit();", null);
	}
	

	public void evalScript(String script, String arg) {
			//Application.debug(app.getKernel().getLibraryJavaScript() + script);
	        Context cx = Context.enter();
	            // Initialize the standard objects (Object, Function, etc.)
	            // This must be done before scripts can be executed. Returns
	            // a scope object that we use in later calls.
	            Scriptable scope = cx.initStandardObjects();

	            // initialise the JavaScript variable applet so that we can call
	            // GgbApi functions, eg ggbApplet.evalCommand()
	            GeoGebraGlobal.initStandardObjects(app, scope, arg, false);

	            // JavaScript to execute
	            //String s = "ggbApplet.evalCommand('F=(2,3)')";
	            
	            // No class loader for unsigned applets so don't try and optimize.
	            // http://www.mail-archive.com/batik-dev@xmlgraphics.apache.org/msg00108.html
	            if (!app.hasFullPermissions()) {
	            	cx.setOptimizationLevel(-1);
	            	Context.setCachingEnabled(false);
	            }
	            // Now evaluate the string we've collected.
	            Object result = cx.evaluateString(scope, app.getKernel().getLibraryJavaScript() + script , app.getPlain("ErrorAtLine"), 1, null);

	            // Convert the result to a string and print it.
	            //Application.debug("script result: "+(Context.toString(result)));
	        
			
	}



	/*
	 * Change listener implementation
	 * Java to JavaScript
	 *
	 */
	
	// maps between GeoElement and JavaScript function names
	private HashMap updateListenerMap;
	private ArrayList<String> addListeners, removeListeners, renameListeners, updateListeners, clearListeners, loggerListenerMap;
	private JavaToJavaScriptView javaToJavaScriptView;
	
	/**
	 * Registers a JavaScript function as an add listener for the applet's construction.
	 *  Whenever a new object is created in the GeoGebraApplet's construction, the JavaScript 
	 *  function JSFunctionName is called using the name of the newly created object as a single argument. 
	 */
	public synchronized void registerAddListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (addListeners == null) {
			addListeners = new ArrayList();			
		}		
		addListeners.add(JSFunctionName);				
		Application.debug("registerAddListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered add listener 
	 * @see registerAddListener() 
	 */
	public synchronized void unregisterAddListener(String JSFunctionName) {
		if (addListeners != null) {
			addListeners.remove(JSFunctionName);
			Application.debug("unregisterAddListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a remove listener for the applet's construction.
	 * Whenever an object is deleted in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public synchronized void registerRemoveListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (removeListeners == null) {
			removeListeners = new ArrayList();			
		}		
		removeListeners.add(JSFunctionName);				
		Application.debug("registerRemoveListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered remove listener 
	 * @see registerRemoveListener() 
	 */
	public synchronized void unregisterRemoveListener(String JSFunctionName) {
		if (removeListeners != null) {
			removeListeners.remove(JSFunctionName);
			Application.debug("unregisterRemoveListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a clear listener for the applet's construction.
	 * Whenever the construction in the GeoGebraApplet's is cleared (i.e. all objects are removed), the JavaScript 
	 * function JSFunctionName is called using no arguments. 	
	 */
	public synchronized void registerClearListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (clearListeners == null) {
			clearListeners = new ArrayList();			
		}		
		clearListeners.add(JSFunctionName);				
		Application.debug("registerClearListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered clear listener 
	 * @see registerClearListener() 
	 */
	public synchronized void unregisterClearListener(String JSFunctionName) {
		if (clearListeners != null) {
			clearListeners.remove(JSFunctionName);
			Application.debug("unregisterClearListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as a rename listener for the applet's construction.
	 * Whenever an object is renamed in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the deleted object as a single argument. 	
	 */
	public synchronized void registerRenameListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (renameListeners == null) {
			renameListeners = new ArrayList();			
		}		
		renameListeners.add(JSFunctionName);				
		Application.debug("registerRenameListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered rename listener.
	 * @see registerRenameListener() 
	 */
	public synchronized void unregisterRenameListener(String JSFunctionName) {
		if (renameListeners != null) {
			renameListeners.remove(JSFunctionName);
			Application.debug("unregisterRenameListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript function as an update listener for the applet's construction.
	 * Whenever any object is updated in the GeoGebraApplet's construction, the JavaScript 
	 * function JSFunctionName is called using the name of the updated object as a single argument. 	
	 */
	public synchronized void registerUpdateListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;				
						
		// init view
		initJavaScriptView();
		
		// init list
		if (updateListeners == null) {
			updateListeners = new ArrayList();			
		}		
		updateListeners.add(JSFunctionName);				
		Application.debug("registerUpdateListener: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously registered update listener.
	 * @see registerRemoveListener() 
	 */
	public synchronized void unregisterUpdateListener(String JSFunctionName) {
		if (updateListeners != null) {
			updateListeners.remove(JSFunctionName);
			Application.debug("unregisterUpdateListener: " + JSFunctionName);
		}	
	}	
	
	/**
	 * Registers a JavaScript update listener for an object. Whenever the object with 
	 * the given name changes, a JavaScript function named JSFunctionName 
	 * is called using the name of the changed object as the single argument. 
	 * If objName previously had a mapping JavaScript function, the old value 
	 * is replaced.
	 * 
	 * Example: First, set a change listening JavaScript function:
	 * ggbApplet.setChangeListener("A", "myJavaScriptFunction");
	 * Then the GeoGebra Applet will call the Javascript function
	 * myJavaScriptFunction("A");
	 * whenever object A changes.	
	 */
	public synchronized void registerObjectUpdateListener(String objName, String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;		
		GeoElement geo = app.getKernel().lookupLabel(objName);
		if (geo == null) return;
				
		// init view
		initJavaScriptView();
		
		// init map and view
		if (updateListenerMap == null) {
			updateListenerMap = new HashMap();			
		}
		
		// add map entry
		updateListenerMap.put(geo, JSFunctionName);		
		Application.debug("registerUpdateListener: object: " + objName + ", function: " + JSFunctionName);
	}
	
	/**
	 * Removes a previously set change listener for the given object.
	 * @see setChangeListener
	 */
	public synchronized void unregisterObjectUpdateListener(String objName) {
		if (updateListenerMap != null) {
			GeoElement geo = app.getKernel().lookupLabel(objName);
			if (geo != null) {
				updateListenerMap.remove(geo);
				Application.debug("unregisterUpdateListener for object: " + objName);
			}
		}
	}			
	
	USBLogger logger = null;
	
	/**
	 * Registers a listener to listen for events from a data logger
	 * eg Go!Motion	
	 */
	public synchronized void registerLoggerListener(String JSFunctionName) {
		if (JSFunctionName == null || JSFunctionName.length() == 0)
			return;		
				
		// init view
		initJavaScriptView();
		
		// init map and view
		if (loggerListenerMap == null) {
			loggerListenerMap = new ArrayList<String>();			
		}
		
		if (logger == null) logger = new USBLogger(LoggerListener);
		
		// add map entry
		loggerListenerMap.add(JSFunctionName);		
		Application.debug("registerLoggerListener: function: " + JSFunctionName);
		
		SensorDataProducer sDataProducer = logger.sDataProducer;
		
		if (sDataProducer != null)
			sDataProducer.start();
	}
	
	DataListener LoggerListener = new DataListener(){
		public void dataReceived(DataStreamEvent dataEvent)
		{
			int numSamples = dataEvent.getNumSamples();
			float [] data = dataEvent.getData();
			if(numSamples > 0) {
				//System.out.println("" + numSamples + " " + data[0]);
				//System.out.flush();

				Object [] args = { data[0] };

				if (listenersEnabled) 
				{
					int size = loggerListenerMap.size();
					for (int i=0; i < size; i++) {
						String jsFunction = (String) loggerListenerMap.get(i);
						Application.debug(jsFunction);
						callJavaScript(jsFunction, args);					
					}			
				}
			} 
			else {
				Application.debug("no sample");
			}
		}

		public void dataStreamEvent(DataStreamEvent dataEvent)
		{				
			String eventString;
			int eventType = dataEvent.getType();
			
			if(eventType == 1001) return;
			
			switch(eventType) {
				case DataStreamEvent.DATA_DESC_CHANGED:
					eventString = "Description changed";
				break;
				default:
					eventString = "Unknown event type";					
			}
			
			System.out.println("Data Event: " + eventString); 
			
		
			
		}
	};
	
	/**
	 * Removes a previously set change listener for the given object.
	 * @see setChangeListener
	 */
	public synchronized void unregisterLoggerListener(String JSFunctionName) {
		if (loggerListenerMap != null) {
			
			loggerListenerMap.remove(JSFunctionName);		
			Application.debug("unregisterLoggerListener for object: " + JSFunctionName);
			
			//Application.debug(loggerListenerMap.size()+"",1);
			
			// stop events from logging device
			if (loggerListenerMap.size() == 0 && logger != null) {
				final SensorDataProducer sDataProducer = logger.sDataProducer;
				
				if (sDataProducer != null) {
		            SwingUtilities.invokeLater( new Runnable(){ public void
		            	run() { 					
			            	Application.debug("stopping logging",1);
							sDataProducer.stop();
		            	} });
				}
				
			}
			
		}
	}					
	/**
	 * Implements the View interface for
	 * Java to JavaScript communication, see
	 * addChangeListener() and removeChangeListener()
	 */	
	private class JavaToJavaScriptView implements View {
		
		/**
		 * Calls all registered add listeners.
		 * @see registerAddListener()
		 */
		public void add(GeoElement geo) {
			if (addListeners != null && geo.isLabelSet()) { 	
				Object [] args = { geo.getLabel() };
				notifyListeners(addListeners, args);
			}
		}
		
		/**
		 * Calls all registered remove listeners.
		 * @see registerRemoveListener()
		 */
		public void remove(GeoElement geo) {
			if (removeListeners != null && geo.isLabelSet()) {  
				Object [] args = { geo.getLabel() };
				notifyListeners(removeListeners, args);						
			}			
		}
		
		/**
		 * Calls all registered clear listeners.
		 * @see registerClearListener()
		 */
		public void clearView() {
			/* 
			 * This code would make sense for a "reload" 
			 * 
			// try to keep all update listeners
			if (updateListenerMap != null) {			
				HashMap newGeoJSfunMap = new HashMap(); 
				
				// go through all geos and update their maps
				Iterator it = updateListenerMap.keySet().iterator();
				while (it.hasNext()) {
					// try to find new geo with same label
					GeoElement oldGeo = (GeoElement) it.next();				
					GeoElement newGeo = kernel.lookupLabel(oldGeo.getLabel());
					
					if (newGeo != null)
						// add mapping to new map
						newGeoJSfunMap.put(newGeo,(String) updateListenerMap.get(oldGeo));				
				}
				
				// use new map
				updateListenerMap.clear();
				updateListenerMap = newGeoJSfunMap;			
			}
			*/
			
			app.getGgbApi().lastGeoElementsIteratorSize = 0;	//ulven 29.08.05: should have been a method...
			updateListenerMap = null;			
			if (clearListeners != null) {  				
				notifyListeners(clearListeners, null);						
			}
		}
		
		/**
		 * Calls all registered rename listeners.
		 * @see registerRenameListener()
		 */
		public void rename(GeoElement geo) {						
			if (renameListeners != null && geo.isLabelSet()) {
				Object [] args = { geo.getOldLabel(), geo.getLabel() };
				notifyListeners(renameListeners, args);				
			}			
		}
		
		/**
		 * Calls all JavaScript functions (listeners) using 
		 * the specified arguments.
		 */
		private synchronized void notifyListeners(ArrayList listeners, Object [] args) {	
			if (!listenersEnabled) return;
			int size = listeners.size();
			for (int i=0; i < size; i++) {
				String jsFunction = (String) listeners.get(i);										
				callJavaScript(jsFunction, args);					
			}			
		}	
																	
		/**
		 * Calls all registered update and updateObject listeners.
		 * @see registerUpdateListener()
		 */
		public synchronized void update(GeoElement geo) {			
			geo.runUpdateScripts();
			if (!listenersEnabled) return;
			// update listeners
			if (updateListeners != null && geo.isLabelSet()) {
				Object [] args = { geo.getLabel() };
				notifyListeners(updateListeners, args);	
			}
			
			// updateObject listeners
			if (updateListenerMap != null) {			
				String jsFunction = (String) updateListenerMap.get(geo);		
				if (jsFunction != null) {	
					Object [] args = { geo.getLabel() };
					callJavaScript(jsFunction, args);
				}
			}
			
		}
				
		public void updateAuxiliaryObject(GeoElement geo) {
			update(geo);
		}				
					
		public void reset() {							
		}
		
		public void setMode(int mode) {
			// don't react..
		}
				
    	public void repaintView() {
    		// no repaint should occur here: views that are
    		// part of the applet do this on their own    		
    	}    	    	
	}
		
	public synchronized void initJavaScriptView() {
		if (javaToJavaScriptView == null) {
			javaToJavaScriptView = new JavaToJavaScriptView();
			app.getKernel().attach(javaToJavaScriptView); // register view
			initJavaScript();
		}
	}
	
	public synchronized void initJavaScriptViewWithoutJavascript() {
		if (javaToJavaScriptView == null) {
			javaToJavaScriptView = new JavaToJavaScriptView();
			app.getKernel().attach(javaToJavaScriptView); // register view			
		}
	}
	
	public synchronized void initJavaScript() {
		
		if (app.isApplet()) {
			app.getApplet().initJavaScript();
		}
	}
	
	public void callJavaScript(String jsFunction, Object [] args) {		
		if (app.isApplet() && app.useBrowserForJavaScript()) {
			app.getApplet().callJavaScript(jsFunction, args);
		} else {

			
			StringBuilder sb = new StringBuilder();
			sb.append(jsFunction);
			sb.append("(");
			for (int i = 0 ; i < args.length ; i++) {
				sb.append('"');
				sb.append(args[i].toString());
				sb.append('"');
				if (i < args.length - 1) sb.append(",");
			}
			sb.append(");");
			
			//Application.debug(sb.toString());
			
			evalScript(sb.toString(), null);

		}
	}
	
	public void disableListeners() {
		listenersEnabled = false;
	}

	public void enableListeners() {
		listenersEnabled = true;
	}


}
