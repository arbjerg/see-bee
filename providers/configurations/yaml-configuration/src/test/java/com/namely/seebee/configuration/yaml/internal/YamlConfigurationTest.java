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
package com.namely.seebee.configuration.yaml.internal;

import com.namely.seebee.configuration.ConfigurationResolver;
import com.namely.seebee.configuration.yaml.YamlConfigurationResolvers;
import com.namely.seebee.configuration.yaml.YamlConfigurationSettings;
import com.namely.seebee.repository.standard.StandardRepositories;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Dan Lawesson
 */
final class YamlConfigurationTest {
    @ConfigurationResolver.ConfigurationBean(key = "mytype")
    public static class MyConfig {
        public String a;
        public int b;

        public void setA(String a) {
            this.a = a;
        }

        public void setB(int b) {
            this.b = b;
        }
    }

    @ConfigurationResolver.ConfigurationBean(key = "newType")
    public static class NewConfig {
        public String a;
        public int b;

        public void setA(String a) {
            this.a = a;
        }

        public void setB(int b) {
            this.b = b;
        }
    }


    @Test
    void testAnnotation() throws IOException {
        ConfigurationResolver resolver = createResolver();
        MyConfig c = resolver.createAndUpdate(MyConfig.class);
        assertEquals(null, c.a);
        assertEquals(15, c.b);

        NewConfig nc = resolver.createAndUpdate(NewConfig.class);
        assertEquals("Foo", nc.a);
        assertEquals(99, nc.b);
    }

    private ConfigurationResolver createResolver() throws IOException {
        File file = File.createTempFile("tmp", ".yaml");
        file.deleteOnExit();
        FileWriter writer = new FileWriter(file);
        String yaml
                = "noannotation:\n"
                + "  a: first\n"
                + "  b: 42\n"
                + "mytype:\n"
                + "  b: 17\n"
                ;
        writer.write(yaml);
        writer.close();
        Map<String, String> defaults = new HashMap<>();
        defaults.put("mytype.b", "15");
        defaults.put("newType.a", "Foo");
        defaults.put("newType.b", "99");
        ConfigurationResolver configurationResolver = YamlConfigurationResolvers.create();
        StandardRepositories.builder()
                .provide(YamlConfigurationSettings.class)
                .with(new YamlConfigurationSettings(file.getAbsolutePath(), defaults))
                .provide(ConfigurationResolver.class)
                .with(configurationResolver)
                .build();
        return configurationResolver;
    }
}
