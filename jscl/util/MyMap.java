/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package jscl.util;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * An object that maps keys to values.  A map cannot contain duplicate keys;
 * each key can map to at most one value.<p>
 *
 * This interface takes the place of the <tt>Dictionary</tt> class, which was
 * a totally abstract class rather than an interface.<p>
 *
 * The <tt>Map</tt> interface provides three <i>collection views</i>, which
 * allow a map's contents to be viewed as a set of keys, collection of values,
 * or set of key-value mappings.  The <i>order</i> of a map is defined as
 * the order in which the iterators on the map's collection views return their
 * elements.  Some map implementations, like the <tt>TreeMap</tt> class, make
 * specific guarantees as to their order; others, like the <tt>HashMap</tt>
 * class, do not.<p>
 *
 * Note: great care must be exercised if mutable objects are used as map keys.
 * The behavior of a map is not specified if the value of an object is changed
 * in a manner that affects equals comparisons while the object is a
 * key in the map.  A special case of this prohibition is that it is not
 * permissible for a map to contain itself as a key.  While it is permissible
 * for a map to contain itself as a value, extreme caution is advised: the
 * equals and hashCode methods are no longer well defined on a such a map.<p>
 *
 * All general-purpose map implementation classes should provide two
 * "standard" constructors: a void (no arguments) constructor which creates an
 * empty map, and a constructor with a single argument of type <tt>Map</tt>,
 * which creates a new map with the same key-value mappings as its argument.
 * In effect, the latter constructor allows the user to copy any map,
 * producing an equivalent map of the desired class.  There is no way to
 * enforce this recommendation (as interfaces cannot contain constructors) but
 * all of the general-purpose map implementations in the SDK comply.
 *
 * @author  Josh Bloch
 * @version 1.33, 02/06/02
 * @see SortedMap
 * @see Collection
 * @see Set
 * @since 1.2
 */

public interface MyMap {
    // Query Operations

    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this map.
     */
    int size();

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    boolean isEmpty();

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     *
     * @param key key whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     * 
     * @throws ClassCastException if the key is of an inappropriate type for
     * 		  this map.
     * @throws NullPointerException if the key is <tt>null</tt> and this map
     *            does not not permit <tt>null</tt> keys.
     */
    boolean containsKey(Object key);

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.  More formally, returns <tt>true</tt> if and only if
     * this map contains at least one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
     * will probably require time linear in the map size for most
     * implementations of the <tt>Map</tt> interface.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     */
    boolean containsValue(Object value);

    /**
     * Returns the value to which this map maps the specified key.  Returns
     * <tt>null</tt> if the map contains no mapping for this key.  A return
     * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
     * map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     *
     * @param key key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     *	       <tt>null</tt> if the map contains no mapping for this key.
     * 
     * @throws ClassCastException if the key is of an inappropriate type for
     * 		  this map.
     * @throws NullPointerException key is <tt>null</tt> and this map does not
     *		  not permit <tt>null</tt> keys.
     * 
     * @see #containsKey(Object)
     */
    Object get(Object key);

    Map.Entry myGetEntry(Object key);

    // Modification Operations

    /**
     * Associates the specified value with the specified key in this map
     * (optional operation).  If the map previously contained a mapping for
     * this key, the old value is replaced.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the map previously associated <tt>null</tt>
     *	       with the specified key, if the implementation supports
     *	       <tt>null</tt> values.
     * 
     * @throws UnsupportedOperationException if the <tt>put</tt> operation is
     *	          not supported by this map.
     * @throws ClassCastException if the class of the specified key or value
     * 	          prevents it from being stored in this map.
     * @throws IllegalArgumentException if some aspect of this key or value
     *	          prevents it from being stored in this map.
     * @throws NullPointerException this map does not permit <tt>null</tt>
     *            keys or values, and the specified key or value is
     *            <tt>null</tt>.
     */
    Object put(Object key, Object value);

    /**
     * Removes the mapping for this key from this map if present (optional
     * operation).
     *
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the map previously associated <tt>null</tt>
     *	       with the specified key, if the implementation supports
     *	       <tt>null</tt> values.
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is
     *         not supported by this map.
     */
    Object remove(Object key);


    // Bulk Operations

    /**
     * Copies all of the mappings from the specified map to this map
     * (optional operation).  These mappings will replace any mappings that
     * this map had for any of the keys currently in the specified map.
     *
     * @param t Mappings to be stored in this map.
     * 
     * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
     * 		  not supported by this map.
     * 
     * @throws ClassCastException if the class of a key or value in the
     * 	          specified map prevents it from being stored in this map.
     * 
     * @throws IllegalArgumentException some aspect of a key or value in the
     *	          specified map prevents it from being stored in this map.
     * 
     * @throws NullPointerException this map does not permit <tt>null</tt>
     *            keys or values, and the specified key or value is
     *            <tt>null</tt>.
     */
    void putAll(Map t);

    /**
     * Removes all mappings from this map (optional operation).
     *
     * @throws UnsupportedOperationException clear is not supported by this
     * 		  map.
     */
    void clear();


    // Views

    /**
     * Returns a set view of the keys contained in this map.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  If the map is modified while an iteration over the set is
     * in progress, the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding mapping from
     * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the add or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map.
     */
    public MySet keySet();

    /**
     * Returns a collection view of the values contained in this map.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  If the map is modified while an
     * iteration over the collection is in progress, the results of the
     * iteration are undefined.  The collection supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations.
     * It does not support the add or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this map.
     */
    public MyCollection values();

    /**
     * Returns a set view of the mappings contained in this map.  Each element
     * in the returned set is a <tt>Map.Entry</tt>.  The set is backed by the
     * map, so changes to the map are reflected in the set, and vice-versa.
     * If the map is modified while an iteration over the set is in progress,
     * the results of the iteration are undefined.  The set supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
     * the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map.
     */
    public MySet entrySet();


    // Comparison and hashing

    /**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two Maps
     * represent the same mappings.  More formally, two maps <tt>t1</tt> and
     * <tt>t2</tt> represent the same mappings if
     * <tt>t1.entrySet().equals(t2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * @param o object to be compared for equality with this map.
     * @return <tt>true</tt> if the specified object is equal to this map.
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this map.  The hash code of a map
     * is defined to be the sum of the hashCodes of each entry in the map's
     * entrySet view.  This ensures that <tt>t1.equals(t2)</tt> implies
     * that <tt>t1.hashCode()==t2.hashCode()</tt> for any two maps
     * <tt>t1</tt> and <tt>t2</tt>, as required by the general
     * contract of Object.hashCode.
     *
     * @return the hash code value for this map.
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    int hashCode();
}
