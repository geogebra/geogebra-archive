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
package org.apache.commons.math;

import java.io.Serializable;

/**
 * Signals a configuration problem with any of the factory methods.
 * @version $Revision: 1.1 $ $Date: 2009-07-06 21:31:46 $
 */
public class MathConfigurationException extends MathException implements Serializable{

    /** Serializable version identifier */
    private static final long serialVersionUID = -4056541384141349722L;
    /**
     * Default constructor.
     */
    public MathConfigurationException() {
        super();
    }
    
    /**
     * Construct an exception with the given message.
     * @param message descriptive error message
     * @deprecated as of 1.2, replaced by {@link #MathConfigurationException(String, Object[])}
     */
    public MathConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 1.2
     */
    public MathConfigurationException(String pattern, Object[] arguments) {
        super(pattern, arguments);
    }

    /**
     * Construct an exception with the given message and root cause.
     * @param message descriptive error message
     * @param cause  the exception or error that caused this exception to be thrown
     * @deprecated as of 1.2, replaced by {@link #MathConfigurationException(String, Object[], Throwable)}
     */
    public MathConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create an exception with a given root cause.
     * @param cause  the exception or error that caused this exception to be thrown
     */
    public MathConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an exception with specified formatted detail message and root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @param cause  the exception or error that caused this exception to be thrown
     * @since 1.2
     */
    public MathConfigurationException(String pattern, Object[] arguments, Throwable cause) {
        super(pattern, arguments, cause);
    }

}
