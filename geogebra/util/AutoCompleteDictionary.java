package geogebra.util;

import java.util.Iterator;

/**
 * This interface defines the API that dictionaries for autocomplete components
 * must implement. Note that implementations of this interface should perform
 * look ups as quickly as possible to avoid delays as the user types.
 */
public interface AutoCompleteDictionary {
  /**
   * Adds an entry to the dictionary.
   *
   * @param s The string to add to the dictionary.
   */
  public void addEntry(String s);
  
  public Iterator getIterator();

  /**
   * Removes an entry from the dictionary.
   *
   * @param s The string to remove to the dictionary.
   * @return True if successful, false if the string is not contained or cannot
   *         be removed.
   */
  public boolean removeEntry(String s);

  /**
   * Perform a lookup and returns the closest matching string to the passed
   * string.
   *
   * @param s The string to use as the base for the lookup. How this routine
   *          is implemented determines the behaviour of the component.
   *          Typically, the closest matching string that completely contains
   *          the given string is returned.
   */
  public String lookup(String s);
}
