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
package com.namely.seebee.application.sqlserver.parquet;

import com.namely.seebee.application.support.config.ApplicationConfiguration;
import com.namely.seebee.application.support.util.AbstractApplicationMain;
import com.namely.seebee.application.support.util.RepositoryUtil;
import com.namely.seebee.configuration.ConfigurationResolver;
import com.namely.seebee.configuration.yaml.YamlConfigurationResolvers;
import com.namely.seebee.configuration.yaml.YamlConfigurationSettings;
import com.namely.seebee.crudeventlistener.parquet.ParquetCrudEventListener;
import com.namely.seebee.crudreactor.CrudEventListener;
import com.namely.seebee.crudreactor.CrudReactor;
import com.namely.seebee.crudreactor.common.CrudReactors;
import com.namely.seebee.crudreactor.common.PollingCrudReactorSpecifics;
import com.namely.seebee.crudreactor.sqlserver.SqlServerCrudReactor;
import com.namely.seebee.repository.Repository;
import com.namely.seebee.typemapper.TypeMapper;
import com.namely.seebee.typemapper.standard.StandardTypeMapper;

import java.util.Map;

/**
 *
 * @author Per Minborg
 * @author Dan Lawesson
 */
public class Main extends AbstractApplicationMain {
    public static void main(String[] args) {
        new Main().mainHelper(args);
    }

    @Override
    protected Repository buildRepository(ApplicationConfiguration commandLineConfig, Map<String, String> overrides) {
        Repository.Builder builder = RepositoryUtil.standardRepositoryBuilder()
                .provide(YamlConfigurationSettings.class)
                .with(new YamlConfigurationSettings(commandLineConfig.getConfigFile(), overrides));

        return builder
                .provide(TypeMapper.class).with(new StandardTypeMapper())
                .provide(ConfigurationResolver.class).with(YamlConfigurationResolvers.create())
                .provide(CrudEventListener.class).with(ParquetCrudEventListener.create())
                .provide(CrudReactor.class).with(CrudReactors.createPolling())
                .provide(PollingCrudReactorSpecifics.class).with(SqlServerCrudReactor.create())
                .build();
    }
}
