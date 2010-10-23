// GenericsNote: Converted.
/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections15.map;

import java.util.*;

/**
 * <p>A customized implementation of <code>java.util.HashMap</code> designed
 * to operate in a multithreaded environment where the large majority of
 * method calls are read-only, instead of structural changes.  When operating
 * in "fast" mode, read calls are non-synchronized and write calls perform the
 * following steps:</p>
 * <ul>
 * <li>Clone the existing collection
 * <li>Perform the modification on the clone
 * <li>Replace the existing collection with the (modified) clone
 * </ul>
 * <p>When first created, objects of this class default to "slow" mode, where
 * all accesses of any type are synchronized but no cloning takes place.  This
 * is appropriate for initially populating the collection, followed by a switch
 * to "fast" mode (by calling <code>setFast(true)</code>) after initialization
 * is complete.</p>
 * <p/>
 * <p><strong>NOTE</strong>: If you are creating and accessing a
 * <code>HashMap</code> only within a single thread, you should use
 * <code>java.util.HashMap</code> directly (with no synchronization), for
 * maximum performance.</p>
 * <p/>
 * <p><strong>NOTE</strong>: <i>This class is not cross-platform.
 * Using it may cause unexpected failures on some architectures.</i>
 * It suffers from the same problems as the double-checked locking idiom.
 * In particular, the instruction that clones the internal collection and the
 * instruction that sets the internal reference to the clone can be executed
 * or perceived out-of-order.  This means that any read operation might fail
 * unexpectedly, as it may be reading the state of the internal collection
 * before the internal collection is fully formed.
 * For more information on the double-checked locking idiom, see the
 * <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">
 * Double-Checked Locking Idiom Is Broken Declaration</a>.</p>
 *
 * @author Craig R. McClanahan
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 1.0
 */
public class FastHashMap <K,V> extends HashMap<K, V> {

    /**
     * The underlying map we are managing.
     */
    protected HashMap<K, V> map = null;

    /**
     * Are we currently operating in "fast" mode?
     */
    protected boolean fast = false;

    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct an empty map.
     */
    public FastHashMap() {
        super();
        this.map = new HashMap<K, V>();
    }

    /**
     * Construct an empty map with the specified capacity.
     *
     * @param capacity the initial capacity of the empty map
     */
    public FastHashMap(int capacity) {
        super();
        this.map = new HashMap<K, V>(capacity);
    }

    /**
     * Construct an empty map with the specified capacity and load factor.
     *
     * @param capacity the initial capacity of the empty map
     * @param factor   the load factor of the new map
     */
    public FastHashMap(int capacity, float factor) {
        super();
        this.map = new HashMap<K, V>(capacity, factor);
    }

    /**
     * Construct a new map with the same mappings as the specified map.
     *
     * @param map the map whose mappings are to be copied
     */
    public FastHashMap(Map map) {
        super();
        this.map = new HashMap<K, V>(map);
    }


    // Property access
    // ----------------------------------------------------------------------

    /**
     * Returns true if this map is operating in fast mode.
     *
     * @return true if this map is operating in fast mode
     */
    public boolean getFast() {
        return (this.fast);
    }

    /**
     * Sets whether this map is operating in fast mode.
     *
     * @param fast true if this map should operate in fast mode
     */
    public void setFast(boolean fast) {
        this.fast = fast;
    }


    // Map access
    // ----------------------------------------------------------------------
    // These methods can forward straight to the wrapped Map in 'fast' mode.
    // (because they are query methods)

    /**
     * Return the value to which this map maps the specified key.  Returns
     * <code>null</code> if the map contains no mapping for this key, or if
     * there is a mapping with a value of <code>null</code>.  Use the
     * <code>containsKey()</code> method to disambiguate these cases.
     *
     * @param key the key whose value is to be returned
     * @return the value mapped to that key, or null
     */
    public V get(Object key) {
        if (fast) {
            return (map.get(key));
        } else {
            synchronized (map) {
                return (map.get(key));
            }
        }
    }

    /**
     * Return the number of key-value mappings in this map.
     *
     * @return the current size of the map
     */
    public int size() {
        if (fast) {
            return (map.size());
        } else {
            synchronized (map) {
                return (map.size());
            }
        }
    }

    /**
     * Return <code>true</code> if this map contains no mappings.
     *
     * @return is the map currently empty
     */
    public boolean isEmpty() {
        if (fast) {
            return (map.isEmpty());
        } else {
            synchronized (map) {
                return (map.isEmpty());
            }
        }
    }

    /**
     * Return <code>true</code> if this map contains a mapping for the
     * specified key.
     *
     * @param key the key to be searched for
     * @return true if the map contains the key
     */
    public boolean containsKey(Object key) {
        if (fast) {
            return (map.containsKey(key));
        } else {
            synchronized (map) {
                return (map.containsKey(key));
            }
        }
    }

    /**
     * Return <code>true</code> if this map contains one or more keys mapping
     * to the specified value.
     *
     * @param value the value to be searched for
     * @return true if the map contains the value
     */
    public boolean containsValue(Object value) {
        if (fast) {
            return (map.containsValue(value));
        } else {
            synchronized (map) {
                return (map.containsValue(value));
            }
        }
    }

    // Map modification
    // ----------------------------------------------------------------------
    // These methods perform special behaviour in 'fast' mode.
    // The map is cloned, updated and then assigned back.
    // See the comments at the top as to why this won't always work.

    /**
     * Associate the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced and returned.
     *
     * @param key   the key with which the value is to be associated
     * @param value the value to be associated with this key
     * @return the value previously mapped to the key, or null
     */
    public V put(K key, V value) {
        if (fast) {
            synchronized (this) {
                HashMap<K, V> temp = (HashMap<K, V>) map.clone();
                V result = temp.put(key, value);
                map = temp;
                return (result);
            }
        } else {
            synchronized (map) {
                return (map.put(key, value));
            }
        }
    }

    /**
     * Copy all of the mappings from the specified map to this one, replacing
     * any mappings with the same keys.
     *
     * @param in the map whose mappings are to be copied
     */
    public void putAll(Map<? extends K, ? extends V> in) {
        if (fast) {
            synchronized (this) {
                HashMap<K, V> temp = (HashMap<K, V>) map.clone();
                temp.putAll(in);
                map = temp;
            }
        } else {
            synchronized (map) {
                map.putAll(in);
            }
        }
    }

    /**
     * Remove any mapping for this key, and return any previously
     * mapped value.
     *
     * @param key the key whose mapping is to be removed
     * @return the value removed, or null
     */
    public V remove(Object key) {
        if (fast) {
            synchronized (this) {
                HashMap<K, V> temp = (HashMap<K, V>) map.clone();
                V result = temp.remove(key);
                map = temp;
                return (result);
            }
        } else {
            synchronized (map) {
                return (map.remove(key));
            }
        }
    }

    /**
     * Remove all mappings from this map.
     */
    public void clear() {
        if (fast) {
            synchronized (this) {
                map = new HashMap<K, V>();
            }
        } else {
            synchronized (map) {
                map.clear();
            }
        }
    }

    // Basic object methods
    // ----------------------------------------------------------------------
    
    /**
     * Compare the specified object with this list for equality.  This
     * implementation uses exactly the code that is used to define the
     * list equals function in the documentation for the
     * <code>Map.equals</code> method.
     *
     * @param o the object to be compared to this list
     * @return true if the two maps are equal
     */
    public boolean equals(Object o) {
        // Simple tests that require no synchronization
        if (o == this) {
            return (true);
        } else if (!(o instanceof Map)) {
            return (false);
        }
        Map mo = (Map) o;

        // Compare the two maps for equality
        if (fast) {
            if (mo.size() != map.size()) {
                return (false);
            }
            Iterator i = map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                Object key = e.getKey();
                Object value = e.getValue();
                if (value == null) {
                    if (!(mo.get(key) == null && mo.containsKey(key))) {
                        return (false);
                    }
                } else {
                    if (!value.equals(mo.get(key))) {
                        return (false);
                    }
                }
            }
            return (true);

        } else {
            synchronized (map) {
                if (mo.size() != map.size()) {
                    return (false);
                }
                Iterator i = map.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry e = (Map.Entry) i.next();
                    Object key = e.getKey();
                    Object value = e.getValue();
                    if (value == null) {
                        if (!(mo.get(key) == null && mo.containsKey(key))) {
                            return (false);
                        }
                    } else {
                        if (!value.equals(mo.get(key))) {
                            return (false);
                        }
                    }
                }
                return (true);
            }
        }
    }

    /**
     * Return the hash code value for this map.  This implementation uses
     * exactly the code that is used to define the list hash function in the
     * documentation for the <code>Map.hashCode</code> method.
     *
     * @return suitable integer hash code
     */
    public int hashCode() {
        if (fast) {
            int h = 0;
            Iterator<Map.Entry<K, V>> i = map.entrySet().iterator();
            while (i.hasNext()) {
                h += i.next().hashCode();
            }
            return (h);
        } else {
            synchronized (map) {
                int h = 0;
                Iterator<Map.Entry<K, V>> i = map.entrySet().iterator();
                while (i.hasNext()) {
                    h += i.next().hashCode();
                }
                return (h);
            }
        }
    }

    /**
     * Return a shallow copy of this <code>FastHashMap</code> instance.
     * The keys and values themselves are not copied.
     *
     * @return a clone of this map
     */
    public FastHashMap<K, V> clone() {
        FastHashMap<K, V> results = null;
        if (fast) {
            results = new FastHashMap<K, V>(map);
        } else {
            synchronized (map) {
                results = new FastHashMap<K, V>(map);
            }
        }
        results.setFast(getFast());
        return (results);
    }

    // Map views
    // ----------------------------------------------------------------------
    
    /**
     * Return a collection view of the mappings contained in this map.  Each
     * element in the returned collection is a <code>Map.Entry</code>.
     */
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    /**
     * Return a set view of the keys contained in this map.
     */
    public Set<K> keySet() {
        return new KeySet();
    }

    /**
     * Return a collection view of the values contained in this map.
     */
    public Collection<V> values() {
        return new Values();
    }

    // Map view inner classes
    // ----------------------------------------------------------------------

    /**
     * Abstract collection implementation shared by keySet(), values() and entrySet().
     */
    private abstract class CollectionView <E> implements Collection<E> {

        public CollectionView() {
        }

        protected abstract Collection<E> get(Map<K, V> map);

        protected abstract E iteratorNext(Map.Entry<K, V> entry);


        public void clear() {
            if (fast) {
                synchronized (FastHashMap.this) {
                    map = new HashMap<K, V>();
                }
            } else {
                synchronized (map) {
                    get(map).clear();
                }
            }
        }

        public boolean remove(Object o) {
            if (fast) {
                synchronized (FastHashMap.this) {
                    HashMap temp = (HashMap) map.clone();
                    boolean r = get(temp).remove(o);
                    map = temp;
                    return r;
                }
            } else {
                synchronized (map) {
                    return get(map).remove(o);
                }
            }
        }

        public boolean removeAll(Collection<?> o) {
            if (fast) {
                synchronized (FastHashMap.this) {
                    HashMap temp = (HashMap) map.clone();
                    boolean r = get(temp).removeAll(o);
                    map = temp;
                    return r;
                }
            } else {
                synchronized (map) {
                    return get(map).removeAll(o);
                }
            }
        }

        public boolean retainAll(Collection<?> o) {
            if (fast) {
                synchronized (FastHashMap.this) {
                    HashMap temp = (HashMap) map.clone();
                    boolean r = get(temp).retainAll(o);
                    map = temp;
                    return r;
                }
            } else {
                synchronized (map) {
                    return get(map).retainAll(o);
                }
            }
        }

        public int size() {
            if (fast) {
                return get(map).size();
            } else {
                synchronized (map) {
                    return get(map).size();
                }
            }
        }


        public boolean isEmpty() {
            if (fast) {
                return get(map).isEmpty();
            } else {
                synchronized (map) {
                    return get(map).isEmpty();
                }
            }
        }

        public boolean contains(Object o) {
            if (fast) {
                return get(map).contains(o);
            } else {
                synchronized (map) {
                    return get(map).contains(o);
                }
            }
        }

        public boolean containsAll(Collection<?> o) {
            if (fast) {
                return get(map).containsAll(o);
            } else {
                synchronized (map) {
                    return get(map).containsAll(o);
                }
            }
        }

        public <T> T[] toArray(T[] o) {
            if (fast) {
                return get(map).toArray(o);
            } else {
                synchronized (map) {
                    return get(map).toArray(o);
                }
            }
        }

        public Object[] toArray() {
            if (fast) {
                return get(map).toArray();
            } else {
                synchronized (map) {
                    return get(map).toArray();
                }
            }
        }


        public boolean equals(Object o) {
            if (o == this) return true;
            if (fast) {
                return get(map).equals(o);
            } else {
                synchronized (map) {
                    return get(map).equals(o);
                }
            }
        }

        public int hashCode() {
            if (fast) {
                return get(map).hashCode();
            } else {
                synchronized (map) {
                    return get(map).hashCode();
                }
            }
        }

        public boolean add(E o) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        public Iterator<E> iterator() {
            return new CollectionViewIterator();
        }

        private class CollectionViewIterator implements Iterator<E> {

            private Map expected;
            private Map.Entry lastReturned = null;
            private Iterator iterator;

            public CollectionViewIterator() {
                this.expected = map;
                this.iterator = expected.entrySet().iterator();
            }

            public boolean hasNext() {
                if (expected != map) {
                    throw new ConcurrentModificationException();
                }
                return iterator.hasNext();
            }

            public E next() {
                if (expected != map) {
                    throw new ConcurrentModificationException();
                }
                lastReturned = (Map.Entry) iterator.next();
                return iteratorNext(lastReturned);
            }

            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                if (fast) {
                    synchronized (FastHashMap.this) {
                        if (expected != map) {
                            throw new ConcurrentModificationException();
                        }
                        FastHashMap.this.remove(lastReturned.getKey());
                        lastReturned = null;
                        expected = map;
                    }
                } else {
                    iterator.remove();
                    lastReturned = null;
                }
            }
        }
    }

    /**
     * Set implementation over the keys of the FastHashMap
     */
    private class KeySet extends CollectionView<K> implements Set<K> {

        protected Collection get(Map<K, V> map) {
            return map.keySet();
        }

        protected K iteratorNext(Map.Entry<K, V> entry) {
            return entry.getKey();
        }


    }

    /**
     * Collection implementation over the values of the FastHashMap
     */
    private class Values extends CollectionView<V> {

        protected Collection get(Map<K, V> map) {
            return map.values();
        }

        protected V iteratorNext(Map.Entry<K, V> entry) {
            return entry.getValue();
        }
    }

    /**
     * Set implementation over the entries of the FastHashMap
     */
    private class EntrySet extends CollectionView<Map.Entry<K, V>> implements Set<Map.Entry<K, V>> {

        protected Collection get(Map<K, V> map) {
            return map.entrySet();
        }

        protected Map.Entry<K, V> iteratorNext(Map.Entry<K, V> entry) {
            return entry;
        }

    }

}
