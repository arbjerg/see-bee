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
import com.namely.seebee.test.tester.parquet.ParquetGroup;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Dan Lawesson
 */
public class TestBasics {

    private static final String DB_NAME = "seebee";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "Password1";
    private static final String SQL_SERVER_URL_SCHEME = "jdbc:sqlserver";

    private static final String SQL_SERVER_HOST_NAME = "sqlserver";
    private static final int SQL_SERVER_PORT = 1433;

    @Test
    public void testConnectivity() throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT 1")) {
            stmt.executeQuery();
        }
    }

    @Test
    public void testInsertsSingleTransaction() throws Exception {
        trackInserts("SWITCH1", false);
    }

    @Test
    public void testInsertsManyTransactions() throws Exception {
        trackInserts("SWITCH2", true);
    }

    @Test
    public void testUpdatesSingleTransaction() throws Exception {
        trackUpdates("SWITCH3", false);
    }

    @Test
    public void testUpdateManyTransactions() throws Exception {
        trackUpdates("SWITCH4", true);
    }


    private void trackInserts(String tableName, boolean autoCommit) throws Exception {
        Deadline deadline = new Deadline(60000);

        int n = 1000;
        long sumOfStates = 0;
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO " + tableName + "(id, state) VALUES (?, ?)")
        ) {
            connection.setAutoCommit(autoCommit);
            for (int i = 0; i < n; i++) {
                stmt.setInt(1, i);
                int state = i * i + i;
                sumOfStates += state;
                stmt.setInt(2, state);
                stmt.executeUpdate();
            }
            if (!autoCommit) {
                connection.commit();
            }
        }

        File parquetSpoolDir = new File("/parquet/spool/dbo." + tableName);

        deadline.await(() -> {
            File[] files = parquetSpoolDir.listFiles();
            return files != null ? Arrays.stream(files)
                    .map(ParquetFile::new)
                    .map(s -> s.stream().count())
                    .reduce(0L, (c1, c2) -> c1 + c2) : 0L;
        }, i -> i == n);


        File[] spooled = deadline.await(parquetSpoolDir::listFiles, Objects::nonNull);
        Set<Integer> ids = Arrays.stream(spooled).map(ParquetFile::new)
                .flatMap(ParquetFile::stream)
                .flatMap(ParquetGroup::stream)
                .map(row -> row.getStringMap().get("id"))
                .map(Integer::parseInt)
                .collect(toSet());

        assertEquals(n, ids.size());

        long actualSumOfStates = Arrays.stream(spooled).map(ParquetFile::new)
                .flatMap(ParquetFile::stream)
                .flatMap(ParquetGroup::stream)
                .map(row -> row.getStringMap().get("state"))
                .map(Long::parseLong)
                .reduce(0L, (i1, i2) -> i1+i2);

        assertEquals(sumOfStates, actualSumOfStates);
    }

    private void trackUpdates(String tableName, boolean autoCommit) throws Exception {
        Deadline deadline = new Deadline(60000);

        int n = 1000;
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO " + tableName + "(id, state) VALUES (?, ?)")
        ) {
            connection.setAutoCommit(autoCommit);
            for (int i = 0; i < n; i++) {
                stmt.setInt(1, i);
                int state = i * i + i;
                stmt.setInt(2, state);
                stmt.executeUpdate();
            }
            if (!autoCommit) {
                connection.commit();
            }
        }

        Integer endValue = 0;
        int u = n * 10;
        Random random = new Random(42);
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE " + tableName + " SET state = ? WHERE id = ?")
        ) {
            connection.setAutoCommit(autoCommit);
            for (int i=0; i<u; i++) {
                stmt.setInt(1, random.nextInt(9999));
                stmt.setInt(2, random.nextInt(n));
                stmt.executeUpdate();
            }
            for (int i=0; i<u; i++) {
                stmt.setInt(1, 17);
                stmt.setInt(2, i % n );
                stmt.executeUpdate();
            }
            for (int i=0; i<n; i++) {
                stmt.setInt(1, endValue);
                stmt.setInt(2, i);
                stmt.executeUpdate();
            }
            if (!autoCommit) {
                connection.commit();
            }
        }

        File parquetSpoolDir = new File("/parquet/spool/dbo." + tableName);

        deadline.await(()-> {
            File[] files = parquetSpoolDir.listFiles();
            if (files != null) {
                Map<String, String> states = new HashMap<>();
                Arrays.stream(files)
                        .sorted(Comparator.comparing(File::getName)) // leveraging the undocumented feature that spooled files are created in lexical order unless version number wraps
                        .map(ParquetFile::new)
                        .flatMap(ParquetFile::stream)
                        .flatMap(ParquetGroup::stream)
                        .forEachOrdered(row -> states.put(row.getStringMap().get("id"), row.getStringMap().get("state")));
                Set<String> valueSet = new HashSet<>(states.values());
                return valueSet.size();
            } else {
                return 0;
            }
        }, i -> i == 1);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl(), DB_USER, DB_PASSWORD);
    }

    private static String connectionUrl() {
        return SQL_SERVER_URL_SCHEME +
                "://" + SQL_SERVER_HOST_NAME +
                ":" + SQL_SERVER_PORT +
                ";databaseName=" + DB_NAME;
    }
}
