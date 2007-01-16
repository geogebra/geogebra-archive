/*
 * ArrayMap.java
 *
 * Created on December 22, 2006, 6:21 PM
 */

package jscl.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public class ArrayMap extends AbstractMap implements SortedMap {
    static final int CAPACITY=8;
    Object keys[]=new Object[CAPACITY];
    Object values[]=new Object[CAPACITY];
    int size;

    public Object getKey(int n) {
        return n<size?keys[n]:null;
    }

    public Object getValue(int n) {
        return values[n];
    }

    public Object setValue(int n, Object value) {
        Object oldvalue=values[n];
        values[n]=value;
        return oldvalue;
    }

    public void add(Object key, Object value) {
        ensureCapacity(size+1);
        keys[size]=key;
        values[size]=value;
        size++;
    }

    public int indexOf(Object key) {
        return binarySearch(key);
    }

    public int size() {
	return size;
    }

    public boolean isEmpty() {
	return size == 0;
    }

    public boolean containsValue(Object value) {
        for(int i=0;i<size;i++) if(value.equals(values[i])) return true;
        return false;
    }

    public boolean containsKey(Object key) {
        int n=binarySearch(key);
        if(n<size && compare(key,keys[n])==0) return true;
        else return false;
    }

    int binarySearch(Object key) {
	int low = 0;
	int high = size;
	while (low < high) {
	    int mid = (low + high) >> 1;
            int cmp = ((Comparable)keys[mid]).compareTo(key);
	    if (cmp < 0) low = mid + 1;
            else if(cmp > 0) high = mid;
	    else return mid;
	}
	return low;
    }

    private int compare(Object k1, Object k2) {
        return ((Comparable)k1).compareTo(k2);
    }

    public Object get(Object key) {
        int n=binarySearch(key);
        if(n<size && compare(key,keys[n])==0) return values[n];
        else return null;
    }

    int getCeilIndex(Object key) {
        return binarySearch(key);
    }

    int getPrecedingIndex(Object key) {
        int n=binarySearch(key);
        return n>0?n-1:size;
    }

    public Object put(Object key, Object value) {
        int n=binarySearch(key);
        if(n<size && compare(key,keys[n])==0) {
            return setValue(n,value);
        } else {
            ensureCapacity(size+1);
            System.arraycopy(keys,n,keys,n+1,size-n);
            System.arraycopy(values,n,values,n+1,size-n);
            size++;
            keys[n]=key;
            values[n]=value;
            return null;
        }
    }

    void ensureCapacity(int size) {
        if(size>keys.length) {
            int capacity=keys.length<<1;
            Object k[]=new Object[capacity];
            Object v[]=new Object[capacity];
            System.arraycopy(keys,0,k,0,keys.length);
            System.arraycopy(values,0,v,0,keys.length);
            keys=k;
            values=v;
        }
    }

    public void trimToSize() {
        if(size<keys.length) {
            Object k[]=new Object[size];
            Object v[]=new Object[size];
            System.arraycopy(keys,0,k,0,size);
            System.arraycopy(values,0,v,0,size);
            keys=k;
            values=v;
        }
    }

    public Object remove(Object key) {
        int n=binarySearch(key);
        if(n<size && compare(key,keys[n])==0) {
            Object oldvalue=values[n];
            System.arraycopy(keys,n+1,keys,n,size-n-1);
            System.arraycopy(values,n+1,values,n,size-n-1);
            size--;
            keys[size]=null;
            values[size]=null;
            return oldvalue;
        } else {
            return null;
        }
    }

    transient volatile Set keySet = null;
    transient volatile Collection values_ = null;
    transient volatile Set entrySet = null;

    public Set keySet() {
        if (keySet == null) {
            keySet = new AbstractSet() {
                public Iterator iterator() {
                    return new KeyIterator();
                }

                public int size() {
                    throw new UnsupportedOperationException();
                }

                public boolean contains(Object o) {
                    throw new UnsupportedOperationException();
                }

                public boolean remove(Object o) {
                    throw new UnsupportedOperationException();
                }

                public void clear() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return keySet;
    }

    public Collection values() {
        if (values_ == null) {
            values_ = new AbstractCollection() {
                public Iterator iterator() {
                    return new ValueIterator();
                }

                public int size() {
                    throw new UnsupportedOperationException();
                }

                public boolean contains(Object o) {
                    throw new UnsupportedOperationException();
                }

                public boolean remove(Object o) {
                    throw new UnsupportedOperationException();
                }

                public void clear() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return values_;
    }

    public Set entrySet() {
        if (entrySet == null) {
            entrySet = new AbstractSet() {
                public Iterator iterator() {
                    return new EntryIterator();
                }

                public boolean contains(Object o) {
                    throw new UnsupportedOperationException();
                }

                public boolean remove(Object o) {
                    throw new UnsupportedOperationException();
                }

                public int size() {
                    throw new UnsupportedOperationException();
                }

                public void clear() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return entrySet;
    }

    public Comparator comparator() {
        throw new UnsupportedOperationException();
    }

    public Object firstKey() {
        return 0<size?keys[0]:null;
    }

    public Object lastKey() {
        return size>0?keys[size-1]:null;
    }

    public SortedMap subMap(Object fromKey, Object toKey) {
        return new SubMap(fromKey, toKey);
    }

    public SortedMap headMap(Object toKey) {
        return new SubMap(toKey, true);
    }

    public SortedMap tailMap(Object fromKey) {
        return new SubMap(fromKey, false);
    }

    private class SubMap extends AbstractMap implements SortedMap {
        private boolean fromStart = false, toEnd = false;
        private Object  fromKey, toKey;

        SubMap(Object fromKey, Object toKey) {
            if (compare(fromKey, toKey) > 0) throw new IllegalArgumentException("fromKey > toKey");
            this.fromKey = fromKey;
            this.toKey = toKey;
        }

        SubMap(Object key, boolean headMap) {
            if (headMap) {
                fromStart = true;
                toKey = key;
            } else {
                toEnd = true;
                fromKey = key;
            }
        }

        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException();
        }

        public Object get(Object key) {
            throw new UnsupportedOperationException();
        }

        public Object put(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        public Comparator comparator() {
            throw new UnsupportedOperationException();
        }

        public Object firstKey() {
            throw new UnsupportedOperationException();
        }

        public Object lastKey() {
            throw new UnsupportedOperationException();
        }

        private transient Set entrySet = new EntrySetView();

        public Set entrySet() {
            return entrySet;
        }

        private class EntrySetView extends AbstractSet {
            public int size() {
                throw new UnsupportedOperationException();
            }

            public boolean isEmpty() {
                throw new UnsupportedOperationException();
            }

            public boolean contains(Object o) {
                throw new UnsupportedOperationException();
            }

            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            public Iterator iterator() {
                return new SubMapEntryIterator(
                    (fromStart ? 0 : getCeilIndex(fromKey)),
                    (toEnd ? size : getCeilIndex(toKey)));
            }
        }

        public SortedMap subMap(Object fromKey, Object toKey) {
            throw new UnsupportedOperationException();
        }

        public SortedMap headMap(Object toKey) {
            throw new UnsupportedOperationException();
        }

        public SortedMap tailMap(Object fromKey) {
            throw new UnsupportedOperationException();
        }
    }

    private class EntryIterator implements Iterator {
        int n;

        EntryIterator() {}

        // Used by SubMapEntryIterator
        EntryIterator(int first) {
            n=first;
        }

        public boolean hasNext() {
            return n<size;
        }

        final Entry nextEntry() {
            if (n==size) throw new NoSuchElementException();
            return new Entry(n++);
        }

        public Object next() {
            return nextEntry();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class KeyIterator extends EntryIterator {
        KeyIterator() {}

        public Object next() {
            return nextEntry().getKey();
        }
    }

    private class ValueIterator extends EntryIterator {
        ValueIterator() {}

        public Object next() {
            return nextEntry().getValue();
        }
    }

    private class SubMapEntryIterator extends EntryIterator {
        private final int firstExcluded;

        SubMapEntryIterator(int first, int firstExcluded) {
            super(first);
            this.firstExcluded = firstExcluded;
        }

        public boolean array_hasNext() {
            return n != firstExcluded;
        }

        public Object next() {
            if (n == firstExcluded) throw new NoSuchElementException();
            return nextEntry();
        }
    }

    class Entry implements Map.Entry {
        int n;

	public Entry(int n) {
            this.n=n;
	}

	public Entry(Map.Entry e) {
            throw new UnsupportedOperationException();
	}

	public Object getKey() {
	    return keys[n];
	}

	public Object getValue() {
	    return values[n];
	}

	public Object setValue(Object value) {
	    Object oldValue = values[n];
	    values[n] = value;
	    return oldValue;
	}

	public boolean equals(Object o) {
	    if (!(o instanceof Map.Entry))
		return false;
	    Map.Entry e = (Map.Entry)o;
	    return eq(keys[n], e.getKey()) &&  eq(values[n], e.getValue());
	}

	public int hashCode() {
	    Object v;
	    return ((keys[n]   == null)   ? 0 :   keys[n].hashCode()) ^
		   ((values[n] == null)   ? 0 : values[n].hashCode());
	}

	public String toString() {
	    return keys[n] + "=" + values[n];
	}

        private boolean eq(Object o1, Object o2) {
            return (o1 == null ? o2 == null : o1.equals(o2));
        }
    }
}
