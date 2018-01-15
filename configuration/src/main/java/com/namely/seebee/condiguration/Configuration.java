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
package com.namely.seebee.condiguration;

import com.namely.seebee.condiguration.internal.DefaultConfiguration;

/**
 * Configuration component that is used to configure the See Bee application.
 * <p>
 * There can be many ways to obtain a custom configuration, for example from an
 * XML, JSON or YAML file.
 *
 * @author Per Minborg
 */
public interface Configuration {

    String greetingLogo();

    static Configuration defaultConfiguration() {
        return new DefaultConfiguration();
    }

}
