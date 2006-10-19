package jscl.util;

import java.util.Comparator;

public class ArrayComparator implements Comparator {
	public static final Comparator comparator=new ArrayComparator();

	private ArrayComparator() {}

	public int compare(Object o1, Object o2) {
		Comparable co1[]=(Comparable[])o1;
		Comparable co2[]=(Comparable[])o2;
		if(co1.length<co2.length) return -1;
		else if(co1.length>co2.length) return 1;
		for(int i=co1.length-1;i>=0;i--) {
			if(co1[i].compareTo(co2[i])<0) return -1;
			else if(co1[i].compareTo(co2[i])>0) return 1;
		}
		return 0;
	}
}
