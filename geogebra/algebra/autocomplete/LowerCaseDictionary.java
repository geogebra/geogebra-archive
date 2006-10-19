package geogebra.algebra.autocomplete;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A default implementation of the autocomplete dictionary. This implementation
 * is based upon the TreeSet collection class to provide quick lookups and
 * default sorting.
 * All lookups are case insensitive!
 * 
 * @author Matt Welsh (matt@matt-welsh.com), Markus Hohenwarter
 */
public class LowerCaseDictionary extends Hashtable implements AutoCompleteDictionary {
	
  private static final long serialVersionUID = 1L;

  private TreeSet treeSet = new TreeSet();
  
  /**
   * Adds an entry to the dictionary.
   *
   * @param s The string to add to the dictionary.
   */
  public void addEntry(String s) {
  	String lowerCase = s.toLowerCase();
  	put(lowerCase, s);
  	
  	// store lowerCase in tree
  	treeSet.add(lowerCase);
  }

  /**
   * Removes an entry from the dictionary.
   *
   * @param s The string to remove to the dictionary.
   * @return True if successful, false if the string is not contained or cannot
   *         be removed.
   */
  public boolean removeEntry(String s) {
  	String lowerCase = s.toLowerCase();
  	remove(lowerCase);
    return treeSet.remove(lowerCase);
  }

  /**
   * Perform a lookup. This routine returns the closest matching string that
   * completely starts with the given string, or null if none is found.
   * Note: the lookup is NOT case sensitive.
   *
   * @param curr The string to use as the base for the lookup.
   * @return curr The closest matching string that completely contains the
   *              given string.
   */
  public String lookup(String curr) {  	
    if(curr == null || "".equals(curr))
		return null;
    
    String currLowerCase = curr.toLowerCase();
    try {
      SortedSet tailSet = treeSet.tailSet(currLowerCase);
      if(tailSet != null) {
        Object firstObj = tailSet.first();
        if(firstObj != null) {
          String first = (String)firstObj;
          if(first.startsWith(currLowerCase)) {          	
          	String ret = (String) get(first);          	
            return ret;
          }
        }
      }
    }
    catch (Exception e) {
      return null;
    }
    return null;
  }
    
} 