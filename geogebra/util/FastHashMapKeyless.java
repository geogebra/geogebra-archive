/*
 * Copyright 2001 by Olivier Refalo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package   geogebra.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * This complete HashMap implementation is typicaly:
 *  60% faster for get operations.
 *  100% faster for add operations.
 *
 * This implementation only keeps the Key's hashcode.
 * Feel free to change the source code to handle different Types.
 *
 * @version January 2001
 * @author Olivier Refalo
 * @see www.crionics.com
 */
public final class FastHashMapKeyless extends AbstractMap
	implements Map, Cloneable, Serializable {
    /**
     * Unique object ID. (Used by the Serialization mechanisum)
     */
    private final static long serialVersionUID = -7495231766421401792L;
    /**
     * Default fill fraction allowed before growing table.
     * When number of element reaches DEFAULT_FILL*Table size -> rehash()
     */
    private static final float DEFAULT_FILL = 0.70F;
    /**
     *  Minimum size used for hash table
     *  Must be a multiple of 2^N
     */
    //private static final int MINIMUM_SIZE = 16;
    /**
     * Binary mask used to optimize modulo operations
     */
    private int mask_;
    /**
     * Number of entries present in table
     */
    private int count_ = 0;
    /**
     * Fill factor for the collection
     */
    private float fillFactor_ = DEFAULT_FILL;
    /**
     * Entries allowed before growing table
     */
    private int limit_;
    /**
     * incremented when the collection is modified: used to detect concurrent access
     */
    transient private int modif_ = 0;
    /**
     * Contains a reference to an empty iterator.
     */
    private static final EmptyHashIterator EMPTY_ITERATOR = new EmptyHashIterator();
    /**
     * contains the Set associated with the values when first created.
     */
    private transient Set entrySet_ = null;
    /**
     * contains the Set associated with the keys when first created.
     */
    private transient Set keySet_ = null;
    /**
     * Array of keys
     */
    private int[] keyTable_;
    /**
     * Array of values
     */
    private Object[] valueTable_;
    /**
     * Used when an element is removed
     */
    private final static Long REMOVED = new Long(-1);

    /**
     * Constructor
     *
     * @param _initialCapacity number of elements at build time
     * @param _loadFactor pourcentage used before table is resized
     */
    public FastHashMapKeyless (int _initialCapacity, float _loadFactor) {
	// validate input params
	if (_initialCapacity < 0)
	    throw  new IllegalArgumentException("Illegal Initial Capacity: " + _initialCapacity);
	if (_loadFactor <= 0 || Float.isNaN(_loadFactor))
	    throw  new IllegalArgumentException("Illegal Load factor: " + _loadFactor);
	// Compute initial table size (ensure odd)
	int tempsize = (int)(_initialCapacity/_loadFactor);
	int size = 16;
	while (size < tempsize) {
	    size <<= 1;
	}
	int mask = size - 1;
	//	size = (tempsize > MINIMUM_SIZE) ? tempsize : MINIMUM_SIZE;
	//	Application.debug(size);
	mask_ = mask;
	limit_ = (int)(size*_loadFactor);
	fillFactor_ = _loadFactor;
	keyTable_ = new int[size];
	valueTable_ = new Object[size];
    }

    /**
     * Constructor
     *
     * @param initialCapacity number of elements at build time
     */
    public FastHashMapKeyless (int initialCapacity) {
	this(initialCapacity, DEFAULT_FILL);
    }

    /**
     * Default constructor
     *
     */
    public FastHashMapKeyless () {
	this(0, DEFAULT_FILL);
    }

    /**
     * @return The collection size
     */
    final public int size () {
	return  count_;
    }

    /**
     * @return true is the collection is empty
     */
    final public boolean isEmpty () {
	return  count_ == 0;
    }

    /**
     * @param _value a not null object
     * @return true if _value is in Map
     */
    public boolean containsValue (Object _value) {
	Object vtab[] = valueTable_;
	for (int i = vtab.length; --i >= 0;) {
	    Object o = vtab[i];
	    if (o != null && o != REMOVED && _value.equals(o))
		return  true;
	}
	return  false;
    }

    /**
     * @param _key the key to look for.
     * @return true if collection contains the key
     */
    final public boolean containsKey (Object _key) {
	return  containsKey(_key.hashCode());
    }

    /**
     * @param _key the key to look for.
     * @return true if collection contains the key
     */
    public boolean containsKey (int _key) {
	int mask = mask_;
	int offset = _key & mask;
	int ktab[] = keyTable_;
	Object vtab[] = valueTable_;
	int idx = ktab.length;
	while ((--idx >= 0 && ktab[offset] != _key) || vtab[offset] == REMOVED) {
	    offset = (offset - 1) & mask;
	}
	if (idx < 0)
	    return  false;
	else
	    return  vtab[offset] != null;
    }

    /**
     * @param _key the entry id
     * @return the object associated with the key
     */
    final public Object get (Object _key) {
	return  get(_key.hashCode());
    }

    /**
     * @param _key the entry id
     * @return the object associated with the key
     */
    public Object get (int _key) {
	int mask = mask_;
	int offset = _key & mask;
	int ktab[] = keyTable_;
	int idx = ktab.length;
	Object vtab[] = valueTable_;
	while ((--idx >= 0 && ktab[offset] != _key) || vtab[offset] == REMOVED) {
	    offset = (offset - 1) & mask;
	}
	if (idx < 0)
	    return  null;
	else
	    return  vtab[offset];
    }

    /**
     * Called when the unlying table needs to be expanded.
     *
     */
    private void rehash () {
	modif_++;
	int[] oldkeys = keyTable_;
	int len = oldkeys.length << 1;
	limit_ = (int)(len*fillFactor_);
	int[] ktab = keyTable_ = new int[len];
	Object[] oldvalues = valueTable_;
	Object[] vtab = valueTable_ = new Object[len];
	int mask = (mask_ << 1) | 1;
	for (int i = oldvalues.length; --i >= 0;) {
	    Object o = oldvalues[i];
	    if (o != null) {
		// Compute the new location
		int key = oldkeys[i];
		int offset = key%mask;
		while (ktab[offset] != key && vtab[offset] != null) {
		    offset = (offset - 1) & mask;
		}
		// Store the key
		vtab[offset] = o;
		ktab[offset] = key;
	    }
	}
	mask_ = mask;
    }

    /**
     * Stores a (key,value) in the Map.
     *
     * @param _key the entry id
     * @param _value the entry value
     * @return the old object or null.
     */
    final public Object put (Object _key, Object _value) {
	return  put(_key.hashCode(), _value);
    }

    /**
     * Removes the (key,value) pair from the Map
     *
     * @param _key entry id
     * @return the object just removed
     */
    final public Object remove (Object _key) {
	return  remove(_key.hashCode());
    }

    /**
     * Removes the (key,value) pair from the Map
     *
     * @param _key entry id
     * @return the object just removed
     */
    public Object remove (int _key) {
	modif_++;
	int mask = mask_;
	int offset = _key & mask;
	int ktab[] = keyTable_;
	int idx = ktab.length;
	Object vtab[] = valueTable_;
	while ((--idx >= 0 && ktab[offset] != _key) || vtab[offset] == REMOVED) {
	    offset = (offset - 1) & mask;
	}
	if (idx < 0)
	    return  null;
	Object o = vtab[offset];
	if (o != null) {
	    vtab[offset] = null;
	    ktab[offset] = 0;
	    count_--;
	    return  o;
	}
	return  null;
    }

    /**
     * Clear the collection.
     * Warning, this method doesn't deallocate the buffers
     *
     */
    public void clear () {
	modif_++;
	int ktab[] = keyTable_;
	Object vtab[] = valueTable_;
	for (int i = vtab.length; --i >= 0;) {
	    ktab[i] = 0;
	    vtab[i] = null;
	}
	count_ = 0;
    }

    /**
     * Clones the Map instance
     *
     * @return a cloned copy
     */
    public Object clone () {
	try {
	    FastHashMapKeyless t = (FastHashMapKeyless)super.clone();
	    // copy keys
	    int len = keyTable_.length;
	    t.keyTable_ = new int[len];
	    t.valueTable_ = new Object[len];
	    System.arraycopy(keyTable_, 0, t.keyTable_, 0, len);
	    // copy values, Clone objects if marked as Clonable
	    Object vtab[] = valueTable_;
	    Object dvtab[] = t.valueTable_;
	    for (; --len > 0;) {
		Object o = vtab[len];
		if (o != null && o instanceof Cloneable) {
		    // Object.clone() is protected -> reflection mandatory
		    Class c = o.getClass();
		    Method method = c.getMethod("clone", null);
		    o = method.invoke(o, null);
		}
		dvtab[len] = o;
	    }
	    t.count_ = count_;
	    t.limit_ = limit_;
	    t.mask_ = mask_;
	    t.fillFactor_ = fillFactor_;
	    t.keySet_ = null;
	    t.entrySet_ = null;
	    t.values = null;
	    t.modif_ = 0;
	    return  t;
	} catch (CloneNotSupportedException e) {
	    throw  new InternalError("One of the value object is not clonable");
	} catch (NoSuchMethodException e1) {
	    throw  new InternalError("Weird! clone() method not found!:" + e1.toString());
	} catch (InvocationTargetException e2) {
	    throw  new InternalError("Weird! problem during clone() invocation");
	} catch (IllegalAccessException e3) {
	    throw  new InternalError("Weird! illegal access on clone() invocation");
	}
    }
    private transient Collection values = null;

    /**
     * @return A browsable Set on the keys in this Map
     */
    public Set keySet () {
	if (keySet_ == null) {
	    keySet_ = new AbstractSet() {

		/**
		 *
		 * @return an Iterator
		 */
		public Iterator iterator () {
		    return  getHashIterator(KEYS);
		}

		/**
		 *
		 * @return the set size
		 */
		public int size () {
		    return  count_;
		}

		/**
		 *
		 * @param o
		 * @return true if set contains parameter
		 */
		public boolean contains (Object _o) {
		    return  containsKey(_o);
		}

		/**
		 * put your documentation comment here
		 * @param o
		 * @return
		 */
		public boolean remove (Object _o) {
		    int oldSize = count_;
		    FastHashMapKeyless.this.remove(_o);
		    return  count_ != oldSize;
		}

		/**
		 * put your documentation comment here
		 */
		public void clear () {
		    FastHashMapKeyless.this.clear();
		}
	    };
	}
	return  keySet_;
    }

    /**
     * @return A browsable Set on the values\s in this Map
     */
    public Collection values () {
	if (values == null) {
	    values = new AbstractCollection() {

		/**
		 * put your documentation comment here
		 * @return
		 */
		public Iterator iterator () {
		    return  getHashIterator(VALUES);
		}

		/**
		 * put your documentation comment here
		 * @return
		 */
		public int size () {
		    return  count_;
		}

		/**
		 * put your documentation comment here
		 * @param _o
		 * @return
		 */
		public boolean contains (Object _o) {
		    return  containsValue(_o);
		}

		/**
		 * put your documentation comment here
		 */
		public void clear () {
		    FastHashMapKeyless.this.clear();
		}
	    };
	}
	return  values;
    }

    /**
     * @return A browsable Set on the entries in this Map
     */
    public Set entrySet () {
	if (entrySet_ == null) {
	    entrySet_ = new AbstractSet() {

		/**
		 * put your documentation comment here
		 * @return
		 */
		public Iterator iterator () {
		    return  getHashIterator(ENTRIES);
		}

		/**
		 * put your documentation comment here
		 * @param o
		 * @return
		 */
		public boolean contains (Object o) {
		    if (!(o instanceof Map.Entry))
			return  false;
		    Map.Entry entry = (Map.Entry)o;
		    Object key = entry.getKey();
		    Object obj = FastHashMapKeyless.this.get(key);
		    return  (obj != null && obj.equals(entry.getValue()));
		}

		/**
		 * put your documentation comment here
		 * @param o
		 * @return
		 */
		public boolean remove (Object o) {
		    if (contains(o)) {
			Map.Entry entry = (Map.Entry)o;
			FastHashMapKeyless.this.remove(entry.getKey());
			return  true;
		    }
		    else
			return  false;
		}

		/**
		 *
		 * @return the size of teh collection
		 */
		public int size () {
		    return  count_;
		}

		/**
		 * Clear the collection entries
		 */
		public void clear () {
		    FastHashMapKeyless.this.clear();
		}
	    };
	}
	return  entrySet_;
    }

    /**
     * @param _type Type of Iterator: KEYS, VALUES, ENTRIES
     * @return an Iterator on the Map
     */
    private Iterator getHashIterator (int _type) {
	if (count_ == 0)
		return  EMPTY_ITERATOR;
	else
		return  new HashIterator(_type);
    }

    /**
     * HashMap collision list entry.
     */
    private final static class Entry
	    implements Map.Entry {
	int key_;
	Object value_[];
        int idx_;

	/**
	 *
	 * @param         int _key
	 * @param         Object _value
	 */
	Entry (int _key, Object _value[], int _idx) {
	    key_ = _key;
	    value_ = _value;
            idx_ = _idx;
	}

	/**
	 * Clones the current entry
	 * @return
	 */
	protected Object clone () {
	    return  new Entry(key_, value_, idx_);
	}

	/**
	 * Gets the entry's key
	 *
	 * @return an Integer
	 */
	public Object getKey () {
	    return  new Integer(key_);
	}

	/**
	 * Gets the entry's value
	 *
	 * @return an Object
	 */
	public Object getValue () {
	    return  value_[idx_];
	}

	/**
	 * Sets the entry's value
	 * @param _value the new value
	 * @return the old value
	 */
	public Object setValue (Object _value) {
	    Object oldValue = value_[idx_];
	    value_[idx_] = _value;
	    return  oldValue;
	}

	/**
	 * Compare two entry objects
	 *
	 * @param _o an Entry object
	 * @return true- if objects are equal
	 */
	public boolean equals (Object _o) {
	    if (!(_o instanceof Entry))
		return  false;
	    Entry e = (Entry)_o;
	    return  (key_ == e.key_ && value_[idx_].equals(e.getValue()));
	}

	/**
	 * get's the object hashcode
	 *
	 * @return the hashcode as a int value
	 */
	public int hashCode () {
	    return  key_;
	}

	/**
	 * @return a String representing the entry's value
	 */
	public String toString () {
	    return  key_ + "=" + value_;
	}
    }
    /**
     * // Types of Iterators
     */
    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;

    /**
     * Empty iterator
     */
    private final static class EmptyHashIterator
	    implements Iterator {

	/**
	 * put your documentation comment here
	 */
	EmptyHashIterator () {
	}

	/**
	 * put your documentation comment here
	 * @return
	 */
	public boolean hasNext () {
	    return  false;
	}

	/**
	 * put your documentation comment here
	 * @return
	 */
	public Object next () {
	    throw  new NoSuchElementException();
	}

	/**
	 * put your documentation comment here
	 */
	public void remove () {
	    throw  new IllegalStateException();
	}
    }

    private final class HashIterator
	    implements Iterator {
	// Index of last value found
	private int index_ = keyTable_.length;
	// Last value found
	private Object entry_ = null;
	private int lastReturned_ = -1;
	// KEYS, VALUES or ENTRIES
	private int type_;

	/** Used to detect multithread modifications */
	private int expectedModif_ = modif_;

	/**
	 * Contrustor for a given type
	 *
	 */
	HashIterator (int _type) {
	    type_ = _type;
	}

	/**
	 * @return true if entries are pending
	 */
	public boolean hasNext () {
	    int i = index_;
	    Object[] vtab = valueTable_;
	    Object e = entry_;
	    // Look for next value !=null
	    while ((e == null || e == REMOVED) && i > 0)
		e = vtab[--i];
	    entry_ = e;
	    index_ = i;
	    return  e != null && e != REMOVED;
	}

	/**
	 * Jumps to the next element.
	 *
	 * @return the next element (if any)
	 */
	public Object next () {
	    // check multi concurrency
	    if (modif_ != expectedModif_)
		throw  new ConcurrentModificationException();
	    Object v = entry_;
	    int i = index_;
	    Object[] vtab = valueTable_;
	    // look for value!=null
	    while ((v == null || v == REMOVED) && i > 0)
		v = vtab[--i];
	    index_ = i;
	    // if value found, return the appropriate value
	    if (v != null && v != REMOVED) {
		lastReturned_ = i;
		entry_ = null;
		if (type_ == KEYS)
		    return  new Integer(keyTable_[i]);
		if (type_ == VALUES)
		    return  v;
		else
		    return  new Entry(keyTable_[i], vtab, i);
	    }
	    throw  new NoSuchElementException();
	}

	/**
	 * Removes the currently pointed element.
	 *
	 */
	public void remove () {
	    // check if element already removed
	    if (lastReturned_ == -1)
		throw  new IllegalStateException();
	    // check for concurrency pb
	    if (modif_ != expectedModif_)
		throw  new ConcurrentModificationException();
	    Object o = FastHashMapKeyless.this.remove(keyTable_[lastReturned_]);
	    expectedModif_++;
	    lastReturned_ = -1;
	    if (o != null)
		return;
	    throw  new ConcurrentModificationException();
	}
    }

    /**
     * Stores a (key,value) in the Map.
     *
     * @param _key the entry id
     * @param _value the entry value
     * @return the old object or null.
     */
    public Object put (int _key, Object _value) {
	modif_++;
	if (count_++ > limit_) {
	    rehash();
	}
	Object vtab[] = valueTable_;
	int mask = mask_;
	int offset = _key & mask;
	int ktab[] = keyTable_;
	while ((vtab[offset] == REMOVED || vtab[offset]!=null ) && ktab[offset] != _key ) {
	    offset = (offset - 1) & mask;
	}
	Object o = vtab[offset];
	if (o != null && o != REMOVED) {
	    // It's an overlap
	    count_--;
	}
	else {
	    ktab[offset] = _key;
	}
	vtab[offset] = _value;
	if (o == REMOVED)
	    return  null;
	else
	    return  o;
    }
}
