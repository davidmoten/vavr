/* ____  ______________  ________________________  __________
 * \   \/   /      \   \/   /   __/   /      \   \/   /      \
 *  \______/___/\___\______/___/_____/___/\___\______/___/\___\
 *
 * Copyright 2018 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.control;

import java.util.Objects;

/**
 * A {@linkplain java.util.function.Predicate} which may throw.
 *
 * @param <T> the type of the input to the predicate
 */
@FunctionalInterface
public interface CheckedPredicate<T> {

    /**
     * Creates a {@code CheckedPredicate}.
     *
     * <pre>{@code
     * final CheckedPredicate<Boolean> checkedPredicate = CheckedPredicate.of(Boolean::booleanValue);
     * final Predicate<Boolean> predicate = checkedPredicate.unchecked();
     *
     * // = true
     * predicate.test(Boolean.TRUE);
     *
     * // throws
     * predicate.test(null);
     * }</pre>
     *
     * @param methodReference (typically) a method reference, e.g. {@code Type::method}
     * @param <T> type of values that are tested by the predicate
     * @return a new {@code CheckedPredicate}
     */
    static <T> CheckedPredicate<T> of(CheckedPredicate<T> methodReference) {
        return methodReference;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     * @throws Exception if an error occurs
     */
    boolean test(T t) throws Exception;

    /**
     * Combines this predicate with {@code that} predicate using logical and (&amp;&amp;).
     * 
     * @param that a {@code CheckedPredicate}
     * @return a new {@code CheckedPredicate} with {@code p1.and(p2).test(t) == true :<=> p1.test(t) && p2.test(t) == true}
     */
    default CheckedPredicate<T> and(CheckedPredicate<? super T> that) {
        Objects.requireNonNull(that);
        return t -> test(t) && that.test(t);
    }

    /**
     * Negates this predicate.
     *
     * @return A new {@code CheckedPredicate} with {@code p.negate().test(t) == true :<=> p.test(t) == false}
     */
    default CheckedPredicate<T> negate() {
        return t -> !test(t);
    }

    /**
     * Combines this predicate with {@code that} predicate using logical or (||).
     *
     * @param that a {@code CheckedPredicate}
     * @return a new {@code CheckedPredicate} with {@code p1.or(p2).test(t) :<=> p1.test(t) || p2.test(t)}
     */
    default CheckedPredicate<T> or(CheckedPredicate<? super T> that) {
        Objects.requireNonNull(that);
        return t -> test(t) || that.test(t);
    }

}
