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

public interface HasStart {

    /**
     * Called by the repository builder when the repository is built, and all components are resolved,
     * this method allows a component to start executing, safely assuming all other components
     * are resolved.
     *
     * Contrary to the builder provided in the component constructor, the repository
     * provided in this method will remain valid during the application life time.
     *
     * @param repository the created repository
     */
    void start(HasConfiguration repository);
}
