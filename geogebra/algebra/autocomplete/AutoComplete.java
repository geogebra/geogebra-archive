//------------------------------------------------------------------------------
// Copyright (c) 1999-2001 Matt Welsh.  All Rights Reserved.
//------------------------------------------------------------------------------
package geogebra.algebra.autocomplete;

import geogebra.util.AutoCompleteDictionary;

/**
 * Defines the API that autocomplete components must implement
 * @author Matt Welsh (matt@matt-welsh.com)
 */
public interface AutoComplete {
  /**
   * Set the dictionary that autocomplete lookup should be performed by.
   *
   * @param dict The dictionary that will be used for the autocomplete lookups.
   *             The dictionary implemetation should be very fast at lookups to
   *             avoid delays as the user types.
   */
  public void setDictionary(AutoCompleteDictionary dict);

  /**
   * Gets the dictionary currently used for lookups.
   *
   * @return dict The dictionary that will be used for the autocomplete lookups.
   *             The dictionary implemetation should be very fast at lookups to
   *             avoid delays as the user types.
   */
  public AutoCompleteDictionary getDictionary();

  /**
   * Sets whether the component is currently performing autocomplete lookups as
   * keystrokes are performed.
   *
   * @param val True or false.
   */
  public void setAutoComplete(boolean val);

  /**
   * Gets whether the component is currently performing autocomplete lookups as
   * keystrokes are performed.
   *
   * @return True or false.
   */
  public boolean getAutoComplete();
} 
