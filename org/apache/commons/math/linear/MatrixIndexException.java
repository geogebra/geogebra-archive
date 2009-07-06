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

package org.apache.commons.math.linear;

/**
 * Thrown when an operation addresses a matrix coordinate (row,col)
 * which is outside of the dimensions of a matrix.
 * @version $Revision: 1.1 $ $Date: 2009-07-06 21:31:46 $
 */
public class MatrixIndexException extends RuntimeException {

    /** Serializable version identifier */
    private static final long serialVersionUID = -1341109412864309526L;

    /**
     * Default constructor.
     * @deprecated as of 1.2 replaced by #MatrixIndexException(String)
     */
    public MatrixIndexException() {
        this(null);
    }

    /**
     * Construct an exception with the given message and root cause.
     * @param message descriptive error message.
     */
    public MatrixIndexException(String message) {
        super(message);
    }

}
