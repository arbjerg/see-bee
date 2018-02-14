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

import com.namely.seebee.application.sqlserver.parquet.internal.util.GreetingUtil;
import com.namely.seebee.application.sqlserver.parquet.internal.util.RepositoryUtil;
import com.namely.seebee.application.support.config.ApplicationConfiguration;
import com.namely.seebee.application.support.config.ConfigUtil;
import com.namely.seebee.application.support.logging.SeeBeeLogging;
import com.namely.seebee.configuration.ConfigurationException;
import com.namely.seebee.configuration.ConfigurationResolver;
import com.namely.seebee.configuration.yaml.YamlConfigurationResolvers;
import com.namely.seebee.configuration.yaml.YamlConfigurationSettings;
import com.namely.seebee.crudeventlistener.parquet.ParquetCrudEventListener;
import com.namely.seebee.crudreactor.CrudEventListener;
import com.namely.seebee.crudreactor.sqlserver.SqlServerCrudReactor;
import com.namely.seebee.repository.Repository;
import com.namely.seebee.softwareinfo.SoftwareInfo;
import com.namely.seebee.typemapper.TypeMapper;
import com.namely.seebee.typemapper.standard.StandardTypeMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import static java.util.stream.Collectors.joining;

/**
 *
 * @author Per Minborg
 * @author Dan Lawesson
 */
public class Main {
    private final Semaphore done = new Semaphore(0);

    public static void main(String[] args) {
        new Main().mainHelper(args);
    }

    private void mainHelper(String[] args) {
        Map<String, String> paramSettings = ConfigUtil.getSettingsFromArgs(args);
        ApplicationConfiguration commandLineConfig = getApplicationConfiguration(paramSettings);
        setupLogging(commandLineConfig);
        Logger logger = Logger.getLogger(Main.class.getName());

        Runtime.getRuntime().addShutdownHook(new Thread(done::release));

        try (Repository repo = buildRepository(commandLineConfig, paramSettings)) {
            SeeBeeLogging.setSeeBeeLoggingLevel(repo.getConfiguration(ApplicationConfiguration.class).getLoggingLevel());
            logger.fine(() -> "Started with parameters " + Arrays.stream(args).collect(joining(" ")));
            GreetingUtil.printGreeting(() -> repo.stream(SoftwareInfo.class));
            System.out.println("Ctrl-C to quit");
            done.acquireUninterruptibly();
            logger.info("Exit requested. Shutting down.");
        }
    }

    private ApplicationConfiguration getApplicationConfiguration(Map<String, String> paramSettings) {
        ConfigurationResolver config = YamlConfigurationResolvers.create(paramSettings);
        ApplicationConfiguration commandLineConfig;
        try {
            commandLineConfig = config.createAndUpdate(ApplicationConfiguration.class);
        } catch (ConfigurationException e) {
            System.out.println("Illegal command line parameters");
            e.getCause().printStackTrace();
            System.exit(1);
            throw e;  // to make it clear also to the compiler that we will not get any further
        }
        return commandLineConfig;
    }

    private void setupLogging(ApplicationConfiguration commandLineConfig) {
        if (commandLineConfig.getLoggingConfigFile() != null) {
            SeeBeeLogging.setLoggingConfigFile(commandLineConfig.getLoggingConfigFile());
        }
        SeeBeeLogging.setSeeBeeLoggingLevel(commandLineConfig.getLoggingLevel());
    }

    private Repository buildRepository(ApplicationConfiguration commandLineConfig, Map<String, String> overrides) {
        Repository.Builder builder = RepositoryUtil.standardRepositoryBuilder()
                .provide(YamlConfigurationSettings.class)
                .with(new YamlConfigurationSettings(commandLineConfig.getConfigFile(), overrides));

        return builder
                .provide(TypeMapper.class).with(new StandardTypeMapper())
                .provide(ConfigurationResolver.class).with(YamlConfigurationResolvers.create())
                .provide(CrudEventListener.class).with(ParquetCrudEventListener.create())
                .provide(SqlServerCrudReactor.class).with(SqlServerCrudReactor.create())
                .build();
    }

}
