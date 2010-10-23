// TODO: Not yet converted - deprecated (by me).
/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections15.set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.collection.TransformedCollection;

import java.util.Set;

/**
 * Decorates another <code>Set</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 * <p>
 * Note: This class cannot support generics without breaking the Collection contract.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:39 $
 * @since Commons Collections 3.0
 */
public class TransformedSet extends TransformedCollection implements Set {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 306127383500410386L;

    /**
     * Factory method to create a transforming set.
     * <p/>
     * If there are any elements already in the set being decorated, they
     * are NOT transformed.
     *
     * @param set         the set to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if set or transformer is null
     */
    public static <I,O> Set<O> decorate(Set<I> set, Transformer<? super I, ? extends O> transformer) {
        return new TransformedSet(set, transformer);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p/>
     * If there are any elements already in the set being decorated, they
     * are NOT transformed.
     *
     * @param set         the set to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if set or transformer is null
     */
    protected TransformedSet(Set set, Transformer transformer) {
        super(set, transformer);
    }

}
