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
package com.namely.seebee.repositoryclient;

public interface HasInitialize {
    /**
     * Called by the repository builder when the repository is built,
     * this method allows a component to initialize by finding uninitialized components,
     * including ones added later to the builder.
     *
     * The resolve method of HasResolve is called after initialization.
     *
     * Contrary to the builder provided in the component constructor, the repository
     * provided in this method will remain valid during the application life time.
     *
     * @see HasResolve#resolve(HasConfiguration)
     * @see HasStart#start(HasConfiguration)
     * @param repository the created repository
     */
    void initialize(HasComponents repository);
}
