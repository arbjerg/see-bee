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
import com.namely.seebee.condiguration.Configuration;
import com.namely.seebee.repository.Repository;
import com.namely.seebee.version.Version;
import java.lang.System.Logger;

/**
 *
 * @author Per Minborg
 */
public class Main {

    private static final Logger LOGGER = System.getLogger(Main.class.getName());

    public static void main(String[] args) {

        try (Repository repo = RepositoryUtil.standardRepositoryBuilder().build()) {

            System.out.println("TypeMapper components");
            repo.stream(Integer.class)
                .forEach(System.out::println);

            final Configuration configuration = repo.getOrThrow(Configuration.class);
            final Version version = repo.getOrThrow(Version.class);

            System.out.println(GreetingUtil.seeBeeGreetingMessage(version));
            System.out.println();
            System.out.println(GreetingUtil.jvmGreetingMessage(version));

            LOGGER.log(Logger.Level.INFO, "Started");
        }

    }

}
