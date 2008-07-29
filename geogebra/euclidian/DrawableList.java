/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import java.awt.Graphics2D;

/**
 * List to store Drawable objects for fast drawing. 
 */
public class DrawableList {
	
	private Link head, tail;	
	private int size = 0;
	
	public DrawableList() {
	}
	
	final int size() {
		return size;
	}
	
	/**
	 * Inserts d at the end of the list.
	 */
	public final void add(Drawable d) {		
		if (d == null) return;
		
		if (head == null) {
			head = new Link(d, null);
			tail = head;
		}
		else {

			// Michael Borcherds 2008-02-29 BEGIN
			// add in the list according to when we want it drawn
			long priority = d.getGeoElement().getDrawingPriority();			
			Link cur = head;
			Link last = head;
		
			int count=0;
			
			while ((cur.d.getGeoElement().getDrawingPriority() < priority) && !cur.equals(tail)) {
					last = cur;
				cur = cur.next;
				count++;
			}
			
			if (cur.equals(head))
			{ 				
				if (cur.d.getGeoElement().getDrawingPriority() < priority)
				{// add at end (list size=1)						
					Link temp = new Link(d, null);
					tail.next = temp;
					tail = temp;				
				}
				else
				{ // add at start of list
					Link temp2=head;
					head= new Link(d, null);
					head.next=temp2;
				}
			}
			else if (cur.equals(tail))
			{ // add at end
				Link temp = new Link(d, null);
				tail.next = temp;
				tail = temp;				
			}
			else 
			{ // add in middle
				//System.out.println("middle");	
				//Link temp = new Link(d, null);
				//temp.next=cur.next;
				//cur.next = temp;
				
				Link temp = new Link(d, null);
				temp.next=last.next;
				last.next = temp;
			}
			// Michael Borcherds 2008-02-29 END
		
			
		}		
		size++;
	}
	
	/**
	 * Inserts d at the end of the list only if the list
	 * doesn't already contain d.
	 * @param d
	 */
	final void addUnique(Drawable d) {
		if (!contains(d)) add(d);
	}
	
	/**
	 * Returns true iff d is in this list.
	 */
	final boolean contains(Drawable d) {
		Link cur = head;
		while (cur != null) {
			if (cur.d == d) return true;
			cur = cur.next;
		}
		return false;
	}
	
	/**
	 * Removes d from list.	 
	 */
	final void remove(Drawable d) {
		Link prev = null;
		Link cur = head;
		while (cur != null) {			
			// found algo to remove
			if (cur.d == d) {				
				if (prev == null) { // remove from head
					head = cur.next;
					if (head == null) tail = null;
				} else { // standard case
					prev.next = cur.next;
					if (prev.next == null) tail = prev;
				}		
				size--;				
				return;		
			}			
			else { // not yet found
				prev = cur;
				cur = cur.next;
			}
		}
	}
	
	public final void drawAll(Graphics2D g2) {		
		Link cur = head;
		while (cur != null) {
				cur.d.draw(g2);
			cur = cur.next;
		}
	}	
	
	final void updateAll() {
		Link cur = head;
		while (cur != null) {
			cur.d.update();
			cur = cur.next;
		}
	}	
	
	final void updateFontSizeAll() {
		Link cur = head;
		while (cur != null) {
			cur.d.updateFontSize();
			cur = cur.next;
		}
	}	
	
	void clear() {
		head = null;
		tail = null;
		size = 0;
	}
	
	private class Link {
		Drawable d;
		Link next;		
		
		Link(Drawable a, Link n) {
			d = a; next = n;
		}
	}
	
	public DrawableIterator getIterator() {
		return new DrawableIterator(); 
	}
	
	class DrawableIterator {
		private Link it;
	
		private DrawableIterator() {
			reset();
		}
	
		final public Drawable next() {
			Drawable ret = it.d;
			it = it.next;
			return ret;
		}
	
		final public boolean hasNext() {
			return (it != null);
		}
		
		final public void reset() {
			it = head;
		}
	
	}


	
}
