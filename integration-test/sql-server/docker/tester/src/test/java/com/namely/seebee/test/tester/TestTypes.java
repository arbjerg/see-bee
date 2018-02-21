/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Dan Lawesson
 */
public class TestTypes {

    private static final String DB_NAME = "seebee";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "Password1";
    private static final String SQL_SERVER_URL_SCHEME = "jdbc:sqlserver";

    private static final String SQL_SERVER_HOST_NAME = "sqlserver";
    private static final int SQL_SERVER_PORT = 1433;


    @Test
    public void testInsertsSingleTransaction() throws Exception {
        trackInserts("ALLTYPES1", false);
    }

    @Test
    public void testInsertsManyTransactions() throws Exception {
        trackInserts("ALLTYPES2", true);
    }

    private void trackInserts(String tableName, boolean autoCommit) throws Exception {
        Deadline deadline = new Deadline(10000);

        List<FakedBigInt> fakedInts = new ArrayList<>();

        fakedInts.add(new FakedBigInt(14,"decimalNullable",9, offset -> BigDecimal.valueOf((double)(offset*1000 + offset + 9)/1000)));
        fakedInts.add(new FakedBigInt(15,"decimalnn",9, offset -> BigDecimal.valueOf((double)(offset*1000 + offset + 7)/1000)));
        fakedInts.add(new FakedBigInt(22,"moneyNullable",4, offset -> BigDecimal.valueOf((double)(offset*1000 + 5)/100)));
        fakedInts.add(new FakedBigInt(23,"moneynn",4, offset -> BigDecimal.valueOf((double)(offset*1000 + 3)/100)));
        fakedInts.add(new FakedBigInt(35,"sMoneyNullable",4, offset -> BigDecimal.valueOf((double)(offset*1000 + 42)/100)));
        fakedInts.add(new FakedBigInt(36,"sMoneynn",4, offset -> BigDecimal.valueOf((double)(offset*1000 + 17)/100)));
        fakedInts.add(new FakedBigInt(46,"smallDecimal",2, offset -> BigDecimal.valueOf((double)(offset*100 + 3)/10)));
        fakedInts.add(new FakedBigInt(47,"mediumDecimal",3, offset -> BigDecimal.valueOf((double)(offset*1000 + offset + 7)/1000)));
        fakedInts.add(new FakedBigInt(48,"hugeDecimal",20, offset -> BigDecimal.valueOf((double)(offset*1000_000 + offset + 42)/100000)));

        int n = 10;
        String insertStatement = "INSERT INTO " + tableName + "(" +
                "bigintNullable," +
                "bigint," +
                "bitNullable," +
                "bitnn," +
                "charNullable," +
                "charnn," +
                "charSingle," +
                "dateNullable," +
                "datenn," +
                "datetimeNullable," +
                "datetimenn," +
                "datetime2Nullable," +
                "datetime2nn," +
                "decimalNullable," +
                "decimalnn," +
                "floatNullable," +
                "floatnn," +
                "imageNullable," +
                "imagenn," +
                "intNullable," +
                "intnn," +
                // "--mediumMoneyNullable," +
                // "--mediumMoneynn," +
                "moneyNullable," +
                "moneynn," +
                "numericNullable," +
                "numericnn," +
                "nvarShort," +
                "nvarMedium," +
                "nvarLong," +
                "realNullable," +
                "realnn," +
                "sdatetimeNullable," +
                "sdatetimenn," +
                "sintNullable," +
                "sintnn," +
                "sMoneyNullable," +
                "sMoneynn," +
                "tintNullable," +
                "tintnn," +
                "uuidNullable," +
                "uuidnn," +
                "varShort," +
                "varMedium," +
                "varLong," +
                "xmlNullable," +
                "xmlnn," +
                "smallDecimal," +
                "mediumDecimal," +
                "hugeDecimal" +
                ") VALUES (";
        int variableCount = insertStatement.split(",").length;
        insertStatement += IntStream.range(0, variableCount).mapToObj($ -> "?").collect(joining(", ")) + ")";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertStatement)
        ) {
            connection.setAutoCommit(autoCommit);
            for (int i = 0; i < n; i++) {
                int offset = 100*i;
                stmt.setInt(1, offset + 1);
                stmt.setInt(2, offset + 2);
                stmt.setBoolean(3, true);
                stmt.setBoolean(4, false);
                stmt.setString(5, "" + (offset + 5));
                stmt.setString(6, "" + (offset + 6));
                stmt.setString(7, ("" + (offset + 7)).substring(0, 1));

                stmt.setDate(8, new Date((offset + 8) * 100_000));
                stmt.setDate(9, new Date((offset + 9) * 100_000));
                stmt.setTimestamp(10, new Timestamp((offset + 10) * 100_000));
                stmt.setTimestamp(11, new Timestamp((offset + 11) * 100_000));
                stmt.setTimestamp(12, new Timestamp((offset + 12) * 100_000));
                stmt.setTimestamp(13, new Timestamp((offset + 13) * 100_000));

                stmt.setFloat(16, offset + 16);
                stmt.setFloat(17, offset + 17);

                stmt.setBytes(18, ("" + (offset + 18)).getBytes());
                stmt.setBytes(19, ("" + (offset + 19)).getBytes());

                stmt.setInt(20, offset + 20);
                stmt.setInt(21, offset + 21);

                stmt.setDouble(24, offset + 24);
                stmt.setDouble(25, offset + 25);

                stmt.setString(26, ("" + (offset + 26)).substring(0, 1));
                stmt.setString(27, "" + (offset + 27));
                stmt.setString(28, "" + (offset + 28));

                stmt.setFloat(29, offset + 29);
                stmt.setFloat(30, offset + 30);

                stmt.setTimestamp(31, new Timestamp((offset + 31) * 100_000));
                stmt.setTimestamp(32, new Timestamp((offset + 32) * 100_000));

                stmt.setInt(33, offset + 33);
                stmt.setInt(34, offset + 34);

                stmt.setInt(37, 0xff & (offset + 37));
                stmt.setInt(38, 0xff & (offset + 38));

                stmt.setString(39, UUID.nameUUIDFromBytes(("" + (offset + 39)).getBytes()).toString());
                stmt.setString(40, UUID.nameUUIDFromBytes(("" + (offset + 40)).getBytes()).toString());

                stmt.setString(41, ("" + (offset + 41)).substring(0, 1));
                stmt.setString(42, "" + (offset + 42));
                stmt.setString(43, "" + (offset + 43));

                stmt.setString(44, "<foo>" + (offset + 44) + "</foo>");
                stmt.setString(45, "<foo>" + (offset + 45) + "</foo>");

                for (FakedBigInt fi : fakedInts) {
                    fi.set(stmt, offset);
                }

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

        File[] spooled = parquetSpoolDir.listFiles();
        fakedInts.forEach(fi -> fi.check(spooled, n));
    }

    private static class FakedBigInt {
        private final int columnIndex;
        private final String columnName;
        private final int scale;
        private final Function<Integer, BigDecimal> generator;

        private FakedBigInt(int columnIndex, String columnName, int scale, Function<Integer, BigDecimal> generator) {
            this.columnIndex = columnIndex;
            this.columnName = columnName;
            this.scale = scale;
            this.generator = generator;
        }

        private int columnIndex() {
            return columnIndex;
        }

        private BigDecimal generate(int i) {
            return generator.apply(i);
        }

        private void check(File[] spooled, int n) {
            String expected = IntStream.range(0, n)
                    .map(i -> i*100)
                    .mapToObj(offset -> generate(offset).setScale(scale, RoundingMode.UNNECESSARY).toPlainString())
                    .collect(joining(", "));
            assertEquals(expected, getDecimals(spooled, columnName, scale));
        }

        private static String getDecimals(File[] spooled, String columnName, int scale) {
            return Arrays.stream(spooled)
                    .sorted(Comparator.comparing(File::getName)) // leveraging the undocumented feature that spooled files are created in lexical order unless version number wraps
                    .map(ParquetFile::new)
                    .flatMap(ParquetFile::stream)
                    .flatMap(ParquetGroup::stream)
                    .map(parquetRow -> parquetRow.getBigIntegerMap().get(columnName))
                    .map(bi -> new BigDecimal(bi, scale))
                    .map(BigDecimal::toPlainString)
                    .collect(joining(", "));
        }

        public void set(PreparedStatement stmt, int offset) throws SQLException {
            stmt.setBigDecimal(columnIndex(), generate(offset));
        }
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
