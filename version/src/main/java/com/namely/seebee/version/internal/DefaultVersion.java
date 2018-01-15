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
package com.namely.seebee.version.internal;

import com.namely.seebee.version.Version;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public class DefaultVersion implements Version {

    @Override
    public String version() {
        return "0.0.1-SNAPSHOT";
    }

    @Override
    public String name() {
        return "See Bee";
    }

    @Override
    public String vendor() {
        return "Namely, Inc.";
    }

        @Override
    public boolean isProductionMode() {
        return Stream.of("EA", "SNAPSHOT")
            .noneMatch(version().toUpperCase()::contains);
    }
    
    @Override
    public String jvmImplementationVersion() {
        return System.getProperty("java.vm.version");
    }

    @Override
    public String jvmImplementationVendor() {
        return System.getProperty("java.vm.vendor");
    }

    @Override
    public String jvmImplementationName() {
        return System.getProperty("java.vm.name");
    }

}
