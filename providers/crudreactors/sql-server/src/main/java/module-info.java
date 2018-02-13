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
module com.namely.seebee.crudreactor.sqlserver {
    requires transitive com.namely.seebee.crudreactor;
    requires transitive com.namely.seebee.repositoryclient;
    requires com.namely.seebee.configuration;
    requires com.namely.seebee.typemapper;
    requires java.sql;
    requires mssql.jdbc;

    exports com.namely.seebee.crudreactor.sqlserver;
}
