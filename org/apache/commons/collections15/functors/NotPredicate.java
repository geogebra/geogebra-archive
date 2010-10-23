// GenericsNote: Converted.
/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
package org.apache.commons.collections15.functors;

import org.apache.commons.collections15.Predicate;

import java.io.Serializable;

/**
 * Predicate implementation that returns the opposite of the decorated predicate.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class NotPredicate <T> implements Predicate<T>, PredicateDecorator<T>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = -2654603322338049674L;

    /**
     * The predicate to decorate
     */
    private final Predicate<T> iPredicate;

    /**
     * Factory to create the not predicate.
     *
     * @param predicate the predicate to decorate, not null
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null
     */
    public static <T> Predicate<T> getInstance(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new NotPredicate<T>(predicate);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicate the predicate to call after the null check
     */
    public NotPredicate(Predicate<T> predicate) {
        super();
        iPredicate = predicate;
    }

    /**
     * Evaluates the predicate returning the opposite to the stored predicate.
     *
     * @param object the input object
     * @return true if predicate returns false
     */
    public boolean evaluate(T object) {
        return !(iPredicate.evaluate(object));
    }

    /**
     * Gets the predicate being decorated.
     *
     * @return the predicate as the only element in an array
     * @since Commons Collections 3.1
     */
    public Predicate<? super T>[] getPredicates() {
        return new Predicate[]{iPredicate};
    }

}
