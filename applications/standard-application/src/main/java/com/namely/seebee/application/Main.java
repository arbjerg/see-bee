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
package com.namely.seebee.application;

import com.namely.seebee.application.internal.util.GreetingUtil;
import com.namely.seebee.application.internal.util.RepositoryUtil;
import com.namely.seebee.configuration.Configuration;
import com.namely.seebee.configuration.yaml.YamlConfigurations;
import com.namely.seebee.repository.standard.Repository;
import com.namely.seebee.softwareinfo.SoftwareInfo;

import java.lang.System.Logger;

/**
 *
 * @author Per Minborg
 */
public class Main {

    private static final Logger LOGGER = System.getLogger(Main.class.getName());

    public static void main(String[] args) {
        new Main().mainHelper(args);
    }

    protected void mainHelper(String[] args) {
        try (Repository repo = buildRepository()) {

            final Configuration configuration = repo.getOrThrow(Configuration.class);

            GreetingUtil.printGreeting(() -> repo.stream(SoftwareInfo.class));
            LOGGER.log(Logger.Level.INFO, "Started");

        }
    }

    protected Repository.Builder addCustomComponents(Repository.Builder builder) {
        return builder
//            .provide(String.class).with(Configuration.YAML_FILE_NAME_CONFIGURATION + "=custom_config.yml")
//            .provide(Configuration.class).applying(YamlConfigurations::create)
            ;
    }

    private Repository buildRepository() {
        return addCustomComponents(RepositoryUtil.standardRepositoryBuilder())
            .build();
    }

}
