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
package com.namely.seebee.application.support.util;

import com.namely.seebee.softwareinfo.SoftwareInfo;

import java.text.MessageFormat;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public final class GreetingUtil {

    private static final Logger LOGGER = Logger.getLogger(GreetingUtil.class.getName());

    private GreetingUtil() {
        throw new UnsupportedOperationException();
    }

    public static void printGreeting(Supplier<Stream<SoftwareInfo>> infoSupplier) {
        infoSupplier.get()
            .forEachOrdered(GreetingUtil::handleInfo);
    }

    private static void handleInfo(SoftwareInfo info) {
        if ("See Bee".equals(info.name())) {
            System.out.println(GreetingUtil.seeBeeGreetingMessage(info));
            System.out.println();
        } else {
            System.out.println(GreetingUtil.generalGreetingMessage(info));
        }
        if (!info.isProductionMode()) {
            LOGGER.warning(() -> MessageFormat.format("This version of {0} is NOT INTENDED FOR PRODUCTION USE!", info.name()));
        }
    }

    private static String seeBeeGreetingMessage(SoftwareInfo info) {
        String greeting
            = "   _____             ____            \n"
            + "  / ____|           |  _ \\           \n"
            + " | (___   ___  ___  | |_) | ___  ___ \n"
            + "  \\___ \\ / _ \\/ _ \\ |  _ < / _ \\/ _ \\\n"
            + "  ____) |  __/  __/ | |_) |  __/  __/\n"
            + " |_____/ \\___|\\___| |____/ \\___|\\___|\n"
            + " :: " + info.name() + " by " + info.vendor()
            + " :: " + info.version();
        return greeting;
    }

    private static String generalGreetingMessage(SoftwareInfo info) {
        return "Using " + info.name() + " by " + info.vendor() + ", version " + info.version();
    }

}
