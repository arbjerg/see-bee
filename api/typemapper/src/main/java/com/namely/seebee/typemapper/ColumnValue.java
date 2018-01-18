/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.namely.seebee.typemapper;

import java.util.function.Supplier;

/**
 * Generic immutable column value container that may hold any type of column
 * value.
 *
 * @author Per Minborg
 * @param <E> the Java type obtained via JDBC
 */
public interface ColumnValue<E> extends Supplier<E>, HasJavaType<E> {

    /**
     * Returns the container value.
     *
     * @return the container value
     */
    @Override
    E get();

    /**
     * Returns if the container value is null.
     *
     * @return if the container value is null
     */
    boolean isNull();

    /**
     * Serializes the container value to an external target using the provided
     * args.
     *
     * @param arg to be specified
     */
    //void serialize(Consumer<? super ColumnValue<? extends E>> serializer);
    void serialize(Object arg);

}
