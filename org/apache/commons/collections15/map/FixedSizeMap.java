// GenericsNote: Converted.
/*
 *  Copyright 2003-2004 The Apache Software Foundation
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

import org.apache.commons.collections15.BoundedMap;
import org.apache.commons.collections15.collection.UnmodifiableCollection;
import org.apache.commons.collections15.set.UnmodifiableSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Decorates another <code>Map</code> to fix the size, preventing add/remove.
 * <p/>
 * Any action that would change the size of the map is disallowed.
 * The put method is allowed to change the value associated with an existing
 * key however.
 * <p/>
 * If trying to remove or clear the map, an UnsupportedOperationException is
 * thrown. If trying to put a new mapping into the map, an
 * IllegalArgumentException is thrown. This is because the put method can
 * succeed if the mapping's key already exists in the map, so the put method
 * is not always unsupported.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Paul Jack
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class FixedSizeMap <K,V> extends AbstractMapDecorator<K, V> implements Map<K, V>, BoundedMap<K, V>, Serializable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 7450927208116179316L;

    /**
     * Factory method to create a fixed size map.
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static <K,V> Map<K, V> decorate(Map<K, V> map) {
        return new FixedSizeMap<K, V>(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected FixedSizeMap(Map<K, V> map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Write the map out using a custom routine.
     *
     * @param out the output stream
     * @throws IOException
     * @since Commons Collections 3.1
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

    /**
     * Read the map in using a custom routine.
     *
     * @param in the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     * @since Commons Collections 3.1
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map<K, V>) in.readObject();
    }

    //-----------------------------------------------------------------------
    public V put(K key, V value) {
        if (map.containsKey(key) == false) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return map.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        for (Iterator it = mapToCopy.keySet().iterator(); it.hasNext();) {
            if (mapToCopy.containsKey(it.next()) == false) {
                throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
            }
        }
        map.putAll(mapToCopy);
    }

    public void clear() {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    public V remove(Object key) {
        throw new UnsupportedOperationException("Map is fixed size");
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = map.entrySet();
        // unmodifiable set will still allow modification via Map.Entry objects
        return UnmodifiableSet.decorate(set);
    }

    public Set<K> keySet() {
        Set<K> set = map.keySet();
        return UnmodifiableSet.decorate(set);
    }

    public Collection<V> values() {
        Collection<V> coll = map.values();
        return UnmodifiableCollection.decorate(coll);
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return size();
    }

}
