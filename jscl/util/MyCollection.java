package jscl.util;

import java.util.Collection;
import java.util.Iterator;

public interface MyCollection extends Collection {
	Iterator iterator(boolean direction);
}
