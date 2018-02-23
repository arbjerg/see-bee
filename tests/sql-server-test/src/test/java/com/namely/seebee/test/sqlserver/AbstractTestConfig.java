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
package com.namely.seebee.test.sqlserver;

import com.namely.seebee.crudreactor.sqlserver.internal.Configuration;
import com.namely.seebee.dockerdb.TestDatabase;

import java.util.Optional;

public abstract class AbstractTestConfig extends Configuration {
    private final TestDatabase db;

    AbstractTestConfig(TestDatabase db) {
        this.db = db;
    }

    @Override
    public Optional<String> jdbcHostName() {
        return Optional.of(db.getHostName());
    }

    @Override
    public Optional<Integer> jdbcPort() {
        return Optional.of(db.getPort());
    }

    @Override
    public Optional<String> jdbcDatabasename() {
        return Optional.of("seebee");
    }

    @Override
    public Optional<String> jdbcUsername() {
        return Optional.of("sa");
    }

    @Override
    public Optional<String> jdbcPassword() {
        return Optional.of("Password1");
    }
}
