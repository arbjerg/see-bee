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
