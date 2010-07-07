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

package org.apache.commons.math.geometry;

import org.apache.commons.math.MathException;

/**
 * This class represents exceptions thrown while building rotations
 * from matrices.
 *
 * @version $Revision: 811827 $ $Date: 2009-09-06 11:32:50 -0400 (Sun, 06 Sep 2009) $
 * @since 1.2
 */

public class NotARotationMatrixException
  extends MathException {

    /** Serializable version identifier */
    private static final long serialVersionUID = 5647178478658937642L;

    /**
     * Simple constructor.
     * Build an exception by translating and formating a message
     * @param specifier format specifier (to be translated)
     * @param parts to insert in the format (no translation)
     */
    public NotARotationMatrixException(String specifier, Object ... parts) {
        super(specifier, parts);
    }

}
