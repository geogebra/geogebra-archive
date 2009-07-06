/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.discovery.log;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>Simple implementation of Log that sends all enabled log messages,
 * for all defined loggers, to System.err.
 * </p>
 * 
 * <p>Hacked from commons-logging SimpleLog for use in discovery.
 * This is intended to be enough of a Log implementation to bootstrap
 * Discovery.
 * </p>
 * 
 * <p>One property: <code>org.apache.commons.discovery.log.level</code>.
 * valid values: all, trace, debug, info, warn, error, fatal, off.
 * </p>
 * 
 * @author Richard A. Sitze
 * @author <a href="mailto:sanders@apache.org">Scott Sanders</a>
 * @author Rod Waldhoff
 * @author Robert Burrell Donkin
 *
 * @version $Id: DiscoveryLogFactory.java,v 1.1 2009-07-06 21:31:51 murkle Exp $
 */
public class DiscoveryLogFactory {
    private static LogFactory logFactory = null;
    private static final Hashtable  classRegistry = new Hashtable();
    private static final Class[] setLogParamClasses = new Class[] { Log.class };

    /**
     * Above fields must be initialied before this one..
     */
    private static Log log = DiscoveryLogFactory._newLog(DiscoveryLogFactory.class);

    /**
     */    
    public static Log newLog(Class clazz) {
        /**
         * Required to implement 'public static void setLog(Log)'
         */
        try {
            Method setLog = ClassUtils.findPublicStaticMethod(clazz,
                                                              void.class,
                                                              "setLog",
                                                              setLogParamClasses);
            
            if (setLog == null) {
                String msg = "Internal Error: " + clazz.getName() + " required to implement 'public static void setLog(Log)'";
                log.fatal(msg);
                throw new DiscoveryException(msg);
            }
        } catch (SecurityException se) {
            String msg = "Required Security Permissions not present";
            log.fatal(msg, se);
            throw new DiscoveryException(msg, se);
        }

        if (log.isDebugEnabled())
            log.debug("Class meets requirements: " + clazz.getName());

        return _newLog(clazz);
    }

    /**
     * This method MUST not invoke any logging..
     */
    public static Log _newLog(Class clazz) {
        classRegistry.put(clazz, clazz);

        return (logFactory == null)
               ? new SimpleLog(clazz.getName())
               : logFactory.getInstance(clazz.getName());
    }
    
    public static void setLog(Log _log) {
        log = _log;
    }

    /**
     * Set logFactory, works ONLY on first call.
     */
    public static void setFactory(LogFactory factory) {
        if (logFactory == null) {
            // for future generations.. if any
            logFactory = factory;
            
            // now, go back and reset loggers for all current classes..
            Enumeration elements = classRegistry.elements();
            while (elements.hasMoreElements()) {
                Class clazz = (Class)elements.nextElement();

                if (log.isDebugEnabled())
                    log.debug("Reset Log for: " + clazz.getName());
                
                Method setLog = null;
                
                // invoke 'setLog(Log)'.. we already know it's 'public static',
                // have verified parameters, and return type..
                try {
                    setLog = clazz.getMethod("setLog", setLogParamClasses);
                } catch(Exception e) {
                    String msg = "Internal Error: pre-check for " + clazz.getName() + " failed?!";
                    log.fatal(msg, e);
                    throw new DiscoveryException(msg, e);
                }
    
                Object[] setLogParam = new Object[] { factory.getInstance(clazz.getName()) };
                
                try {
                    setLog.invoke(null, setLogParam);
                } catch(Exception e) {
                    String msg = "Internal Error: setLog failed for " + clazz.getName();
                    log.fatal(msg, e);
                    throw new DiscoveryException(msg, e);
                }
            }
        }
    }
}
