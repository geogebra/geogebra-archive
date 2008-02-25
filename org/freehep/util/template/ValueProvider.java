package org.freehep.util.template;

import java.util.List;

/** An interface to be implemented by anything that wishes
 * to provide values to the template engine.
 * @author tonyj
 * @version $Id: ValueProvider.java,v 1.1 2008-02-25 21:17:38 murkle Exp $
 */
public interface ValueProvider
{
   /** Get a single value
    * @param name The name whose value is to be returned
    * @return The value, or <CODE>null</CODE> if undefined.
    */   
   String getValue(String name);
   /** Get a list of value providers.
    * Each item in the returned list must itself be a ValueProvider.
    * @param name The template name of the list to be returned.
    * @return The list of value providers, or <CODE>null</CODE> if no template is defined.
    */   
   List getValues(String name);
}
