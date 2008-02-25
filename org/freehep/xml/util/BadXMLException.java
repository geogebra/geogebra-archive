package org.freehep.xml.util;

import org.xml.sax.SAXException;

/**
 * A SAXException with an optional nested exception
 * @author tonyj
 * @version $Id: BadXMLException.java,v 1.1 2008-02-25 21:18:14 murkle Exp $
 */

public class BadXMLException extends SAXException
{
   /**
	 * 
	 */
   private static final long serialVersionUID = 1493961676061071756L;
   
   public BadXMLException(String message)
   {
      super(message);
   }
   public BadXMLException(String message, Throwable detail)
   {
      super(message);
      initCause(detail);
   }
}
