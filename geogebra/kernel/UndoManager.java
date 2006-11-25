/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import geogebra.io.MyXMLio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * UndoManager handles undo information for a Construction. 
 * It uses an undo info list with construction snapshots in memory.
 * @author Markus Hohenwarter
 */
public class UndoManager {
	
	// maximum capacity of undo info list: you can undo MAX_CAPACITY - 1 steps
	private static final int MAX_CAPACITY = 101; 
	
	private Construction construction;
	private LinkedList undoInfoList;	      
	private ListIterator iterator;  // invariant: iterator.previous() is the current state
	private MyXMLio xmlio;

	/**
	 * Creates a new UndowManager for the given Construction.	 
	 */	
	public UndoManager(Construction c) {
		construction = c;
		xmlio = new MyXMLio(c.getKernel(), c);					
	}
	
	/**
	 * Clears undo info list and adds current state to the undo info list.	 
	 */
	void initUndoInfo() {
		undoInfoList = new LinkedList();	
		iterator = undoInfoList.listIterator();
		storeUndoInfo(); // store current state                                    
	}        

   /**
	 * Loads previous construction state from undo info list.
	 */
	public void undo() {
		if (undoPossible()) {
			iterator.previous();
			loadUndoInfo(iterator.previous());     
			iterator.next();   
		}				         			     
	}

	/**
	 * Loads next construction state from undo info list.
	 */
	public void redo() {           
		if (redoPossible()) {
			loadUndoInfo(iterator.next());	           
		}		   
	}           
        
	
	/**
	 * Reloads construction state at current position of undo list
	 * (this is needed for "cancel" actions).
	 */
	final public void restoreCurrentUndoInfo() {		
		loadUndoInfo(iterator.previous()); 
		iterator.next();   
	} 	
	
	/**
	 * Adds construction state to undo info list.
	 */
	public void storeUndoInfo() {           
		try {
			ByteArrayOutputStream bs = new ByteArrayOutputStream();            
			xmlio.writeUndoXML(bs, construction);             
			bs.close();
			
			//	insert undo info 
			 iterator.add(bs);			
			 // remove end of list
			 while (iterator.hasNext()) {
				 iterator.next();
				 iterator.remove();
			 }		       			 
			
			if (undoInfoList.size() > MAX_CAPACITY) {                						
				//	move to first element
				while (iterator.hasPrevious()) iterator.previous();				
				//	delete first element from list
				iterator.remove();
				//	move to end of list
				while (iterator.hasNext()) iterator.next();	
				System.gc();							
			}							      							
		} 
		catch (Exception e) {		
			System.err.println("storeUndoInfo: " + e.toString());
			e.printStackTrace();
		}                
	}

   /**
	* restore info at position pos of undo list
	*/
	final private void loadUndoInfo(Object info) {                                     			        			                          		
		try {                        
			// load data from byte array
			ByteArrayOutputStream undoInfo =  (ByteArrayOutputStream) info;
			ByteArrayInputStream bs = 
					new ByteArrayInputStream( undoInfo.toByteArray() );         
			xmlio.readZipFromMemory(bs);             
			bs.close();                        
		} 
		catch (Exception e) {
			System.err.println("setUndoInfo: " + e.toString());
			e.printStackTrace();      
		}   
	} 		       
	
	/**
	 * Returns whether undo operation is possible or not.	 
	 */
	public boolean undoPossible() {  
		return iterator.nextIndex() > 1;	
	}
	
	/**
	 * Returns whether redo operation is possible or not.	 
	 */
	public boolean redoPossible() {
		return iterator.hasNext();
	}
	
	/**
	 * Processes xml string. Note: this will change the construction.
	 */
	void processXML(String strXML) throws Exception {	
		xmlio.processXMLString(strXML, true);
	}
	
	/**
	 * Returns undo xml string of this UndoManager's construction.
	 */
	String getCurrentUndoXML() {
		return xmlio.getUndoXML(construction);
	}
		
}
