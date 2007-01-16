package jscl.util;

import java.util.Comparator;

public class NullComparator implements Comparator {
    public static final Comparator direct=new NullComparator(false);
    public static final Comparator reverse=new NullComparator(true);
    int one;

    private NullComparator(boolean reverse) {
        one=reverse?-1:1;
    }

    public int compare(Object o1, Object o2) {
        if(o1==null) return -one;
        else if(o2==null) return one;
        else return ((Comparable)o1).compareTo((Comparable)o2);
    }
}
