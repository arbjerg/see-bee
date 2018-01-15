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
package com.namely.seebee.application.internal.util;

import com.namely.seebee.version.Version;

/**
 *
 * @author Per Minborg
 */
public final class GreetingUtil {

    private static final System.Logger LOGGER = System.getLogger(GreetingUtil.class.getName());

    private GreetingUtil() {
        throw new UnsupportedOperationException();
    }

    public static void printGreeting(Version version) {
        System.out.println(GreetingUtil.seeBeeGreetingMessage(version));
        System.out.println();
        System.out.println(GreetingUtil.jvmGreetingMessage(version));
        if (!version.isProductionMode()) {
            LOGGER.log(System.Logger.Level.WARNING, "This version is NOT INTENDED FOR PRODUCTION USE!");
        }
    }

    private static String seeBeeGreetingMessage(Version version) {
        String greeting
            = "   _____             ____            \n"
            + "  / ____|           |  _ \\           \n"
            + " | (___   ___  ___  | |_) | ___  ___ \n"
            + "  \\___ \\ / _ \\/ _ \\ |  _ < / _ \\/ _ \\\n"
            + "  ____) |  __/  __/ | |_) |  __/  __/\n"
            + " |_____/ \\___|\\___| |____/ \\___|\\___|\n"
            + " :: " + version.name() + " by " + version.vendor()
            + " :: " + version.version();
        return greeting;
    }

    private static String jvmGreetingMessage(Version version) {
        return "Running under " + version.jvmImplementationName() + " by " + version.jvmImplementationVendor() + ", version " + version.jvmImplementationVersion();
    }

}
