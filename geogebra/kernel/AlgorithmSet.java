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

import geogebra.util.FastHashMapKeyless;

import java.util.Iterator;


/**
 * Set to store AlgoElement objects for updating. 
 */
public class AlgorithmSet {

	private FastHashMapKeyless hashMap;
	
    private Link head, tail;
    private int size;   
    
    public AlgorithmSet() {
        size = 0;
    }
    
    final public int getSize() {
        return size;
    }
    
    final public boolean isEmpty() {
        return size == 0;
    }
       
    /**
     * Inserts algo at the end of the set. Note: this leads to a topological sorting
     * of the algorithms which is important for updating.
     * @return: true = the algo was added, false = the algo was already in the set
     */
    final public boolean add(AlgoElement algo) {            	    	
        if (head == null) {
        	if (hashMap == null) {
        		hashMap = new FastHashMapKeyless();
        	}        	
        	hashMap.put(algo, algo);
          			
            head = new Link(algo, null);
            tail = head;
            size++;
            return true;
        }        
        
        // add only new algo to tail
        // the test for the tail is only for efficiency (many hits here)
        if (tail.algo != algo && hashMap.get(algo) == null) {
        	hashMap.put(algo, algo);
            tail.next = new Link(algo, null);
            tail = tail.next;
            size++;
            return true;
        }       
        return false;
    }
    
    /**
     * Inserts all algos of set at the end of this set.
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
     */
    final public boolean contains(AlgoElement algo) {
        if (size == 0 || algo == null) return false;
        
        return hashMap.get(algo) != null;        
    }
    
    /**
     * Removes algo from set.    
     */
    final public boolean remove(AlgoElement algo) {
    	if (!contains(algo)) return false;
    	
    	hashMap.remove(algo);
    	
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
    
    final public void updateAll() {
        Link cur = head;
        while (cur != null) {
            cur.algo.update();
            cur = cur.next;
        }
    }        
    
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AlgorithmSet[");
        
        Link cur = head;
        while (cur != null) {
            sb.append("\t");
            sb.append(cur.algo.getCommandDescription());        
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
    
    public AlgorithmSetIterator getIterator() {
    	return new AlgorithmSetIterator();
    }       
    
    private class AlgorithmSetIterator implements Iterator {
    	private Link cur = head;
    	
    	public void remove() {
    		AlgorithmSet.this.remove(cur.algo);
    		cur = cur.next;
    	}
    			
    	public boolean hasNext() {
    		return cur != null;
    	}
    	
    	public Object next() {
    		Object ret = cur.algo;
    		cur = cur.next; 
    		return ret;
    	}

    }
}


