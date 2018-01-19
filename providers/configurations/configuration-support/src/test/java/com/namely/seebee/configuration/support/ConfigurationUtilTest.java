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
package com.namely.seebee.configuration.support;

import com.namely.seebee.configuration.Configuration;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Per Minborg
 */
final class ConfigurationUtilTest {

    @Test
    void testScalar() {
        Configuration c = new Configuration() {
            @Override
            public int schemaReloadIntervalSeconds() {
                return 40;
            }

            @Override
            public Optional<String> jdbcUsername() {
                return Optional.of("Olle");
            }

            @Override
            public Optional<String> jdbcPassword() {
                return Optional.of("Sven");
            }
        };

        final String actual = ConfigurationUtil.toString(c);

        assertTrue(actual.contains("Olle"));
        assertFalse(actual.contains("Sven"));

    }

}
