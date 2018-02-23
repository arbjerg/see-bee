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
package com.namely.seebee.test.tester;

import com.namely.seebee.test.tester.parquet.ParquetFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests in this class are run when building the tester container. Therefore,
 * these tests will have to work without any external dependencies working.
 *
 * @author Dan Lawesson
 */
public class TestIntegrity {

    @Test
    public void testThatJDBCDriverIsOnClasspath() throws ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Test
    public void testParquetFileReaderSanity() throws URISyntaxException, IOException {
        URL url = TestIntegrity.class.getResource("/switches.pqt");
        File file = new File(url.toURI());
        ParquetFile parquetFile = new ParquetFile(file);
        assertEquals("v0", parquetFile.metadata().get("DATA_START"));
        assertEquals("v3", parquetFile.metadata().get("DATA_END"));
        // parquetFile.stream().flatMap(ParquetGroup::stream).forEach(System.out::println);
    }
}
