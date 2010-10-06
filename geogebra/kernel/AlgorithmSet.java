/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Set to store AlgoElement objects for updating. 
 */
public class AlgorithmSet {

	private HashMap<AlgoElement,AlgoElement> hashMap;
	
    private Link head, tail;
    private int size;   
    
    /**
     * Creates new algorithm set
     */
    public AlgorithmSet() {
        size = 0;
    }
    
    /**
     * Returns number of algos
     * @return number of algos
     */
    final public int getSize() {
        return size;
    }
    
    /**
     * Returns true iff empty
     * @return true iff empty
     */
    final public boolean isEmpty() {
        return size == 0;
    }
       
    /**
     * Inserts algo into set sorted by constructionIndex. Note: this leads to a topological sorting
     * of the algorithms which is important for updating.
     * @return true = the algo was added, false = the algo was already in the set
     * @param algo algo to be added
     */
    final public boolean add(AlgoElement algo) {   
    	if (contains(algo))
    		return false;
    	    	
    	// empty list?
        if (head == null) {
        	if (hashMap == null) {
        		hashMap = new HashMap<AlgoElement,AlgoElement>();
        	}        	
        	hashMap.put(algo, algo);
          			
            head = new Link(algo, null);
            tail = head;
            size++;
            return true;
        }        
        
        // check if algo is already at end of list
        if (tail.algo == algo) 
        	return false;
        
        /*
         * Usually we can just add an algorithm at the end of the list
         * to have it at the right place for updating.
         * However, in certain cases an algorithm needs to be inserted at
         * an earlier place right after a certain parentAlgo. For example,
         * in a regular polygon, new segments can be created later that
         * need to be inserted directly after the parent polygon. 
         */
                       
        // check if algo needs to be inserted right after a certain parentAlgo
        AlgoElement parentAlgo = algo.getUpdateAfterAlgo();
        
        // Standard case: insert at end of list
        if (parentAlgo == null || parentAlgo == tail.algo || !contains(parentAlgo)) {  
            tail.next = new Link(algo, null);
            tail = tail.next;                        
        }   
        
        // Special case: insert in the middle, right after parentAlgo   
        else {        	     
        	// search for parentAlgo
	        Link cur = head;
	        while (cur.algo != parentAlgo) {
	        	cur = cur.next; 	        	
	        }         
	        
	        // now cur.algo == parentAlgo, insert right afterwards
	        cur.next = new Link(algo, cur.next);
        }
        
        hashMap.put(algo, algo);
        size++;
        
        return true;        
    }
    
    /**
     * Inserts all algos of set at the end of this set.
     * @param algoSet set of algos to be added
     */
    public void addAll(AlgorithmSet algoSet) {
        Link cur = algoSet.head;
        while (cur != null) {
            add(cur.algo);
            cur = cur.next;
        }
    }
    
    /**
     * Returns true if this set contains algo.
     * @return true iff this set contains algo.
     * @param algo
     */
    final public boolean contains(AlgoElement algo) {
        if (size == 0 || algo == null) return false;
        
        return hashMap.get(algo) != null;        
    }
    
    /**
     * Removes algo from set.    
     * @return true if found and removed, false if not found
     * @param algo algo to be removed
     */
    final public boolean remove(AlgoElement algo) {
    	Object remObj = hashMap.remove(algo);
    	if (remObj == null) {
    		return false;
    	}
    	
        Link prev = null;
        Link cur = head;
        while (cur != null) {           
            // found algo to remove
            if (cur.algo == algo) {             
                if (prev == null) { // remove from head
                    head = cur.next;
                    if (head == null) tail = null;
                } else { // standard case
                    prev.next = cur.next;
                    if (prev.next == null) tail = prev;
                }       
                size--;
                return true;     
            }           
            else { // not yet found
                prev = cur;
                cur = cur.next;
            }
        }         
        
        return false;
    }
    
    /**
     * Updates all algorithms of this set.
     */
    final public void updateAll() {
        Link cur = head;
        while (cur != null) {
            cur.algo.update();
            cur = cur.next;
        }
    }    
    
    /**
     * Updates all algorithms of this set until the given algorithm
     * is reached.
     * @param lastAlgoToUpdate last algorithm to update
     */
    final public void updateAllUntil(AlgoElement lastAlgoToUpdate) {
        Link cur = head;
        while (cur != null) {        	
        	cur.algo.update();        	
        	
        	if (cur.algo == lastAlgoToUpdate) 
        		return;
            cur = cur.next;
        }
    } 
    
    /**
     * Adds all algorithms in this set to the given collection
     * @param collection
     */
    final public void addAllToCollection(Collection<AlgoElement> collection) {
    	Link cur = head;
        while (cur != null) {
        	collection.add(cur.algo);
            cur = cur.next;
        }    	
    }
    
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AlgorithmSet[");
        
        Link cur = head;
        while (cur != null) {
        	sb.append("\n\t");
            sb.append(cur.algo + ", constIndex: " + cur.algo.getConstructionIndex());            
            cur = cur.next;
        }
        sb.append("]");
        return sb.toString();
    }
        
    
    
    private class Link {
        AlgoElement algo;
        Link next;      
        
        Link(AlgoElement a, Link n) {
            algo = a; next = n;
        }
    }
    
    /**
     * Returns iterator for this set
     * @return iterator for this set
     */
    public AlgorithmSetIterator getIterator() {
    	return new AlgorithmSetIterator();
    }       
    
    private class AlgorithmSetIterator implements Iterator<AlgoElement> {
    	private Link cur = head;
    	
    	public void remove() {
    		AlgorithmSet.this.remove(cur.algo);
    		cur = cur.next;
    	}
    			
    	public boolean hasNext() {
    		return cur != null;
    	}
    	
    	public AlgoElement next() {
    		AlgoElement ret = cur.algo;
    		cur = cur.next; 
    		return ret;
    	}    	
    }
}


