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

import com.namely.seebee.test.tester.fakefields.FakedBigInt;
import com.namely.seebee.test.tester.fakefields.FakedField;
import com.namely.seebee.test.tester.fakefields.FakedInt;
import com.namely.seebee.test.tester.fakefields.FakedString;
import com.namely.seebee.test.tester.parquet.ParquetFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

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
        Deadline deadline = new Deadline();

        List<FakedField> fakedFields = new ArrayList<>();

        fakedFields.add(new FakedBigInt(14,"decimalNullable",9, offset -> BigDecimal.valueOf((double)(offset*1000 + offset + 9)/1000)));
        fakedFields.add(new FakedBigInt(15,"decimalnn",9, offset -> BigDecimal.valueOf((double)(offset*1000 + offset + 7)/1000)));
        fakedFields.add(new FakedBigInt(22,"moneyNullable",4, offset -> BigDecimal.valueOf((double)(offset*1000 + 5)/100)));
        fakedFields.add(new FakedBigInt(23,"moneynn",4, offset -> BigDecimal.valueOf((double)(offset*1000 + 3)/100)));
        fakedFields.add(new FakedBigInt(35,"sMoneyNullable",4, offset -> BigDecimal.valueOf((double)(offset*1000 + 42)/100)));
        fakedFields.add(new FakedBigInt(36,"sMoneynn",4, offset -> BigDecimal.valueOf((double)(offset*1000 + 17)/100)));
        fakedFields.add(new FakedBigInt(46,"smallDecimal",2, offset -> BigDecimal.valueOf((double)(offset*100 + 3)/10)));
        fakedFields.add(new FakedBigInt(47,"mediumDecimal",3, offset -> BigDecimal.valueOf((double)(offset*1000 + offset + 7)/1000)));
        fakedFields.add(new FakedBigInt(48,"hugeDecimal",20, offset -> BigDecimal.valueOf((double)(offset*1000_000 + offset + 42)/100000)));

        fakedFields.add(new FakedString(5,"charNullable", offset -> "x" + offset).withFixedLength(10));
        fakedFields.add(new FakedString(6,"charnn", offset -> "x" + offset).withFixedLength(10));
        fakedFields.add(new FakedString(7,"charSingle", offset -> Integer.toString(offset).substring(0, 1)));
        fakedFields.add(new FakedString(26,"nvarShort", offset -> Integer.toString(offset).substring(0, 1)));
        fakedFields.add(new FakedString(27,"nvarMedium", offset -> "ax" + offset));
        fakedFields.add(new FakedString(28,"nvarLong", offset -> "bx" + offset));
        fakedFields.add(new FakedString(41,"varShort", offset -> Integer.toString(offset).substring(0, 1)));
        fakedFields.add(new FakedString(42,"varMedium", offset -> "xy" + offset));
        fakedFields.add(new FakedString(43,"varLong", offset -> "xz" + offset));
        fakedFields.add(new FakedString(44,"xmlNullable", offset -> "<foo>" + (offset + 44) + "</foo>"));
        fakedFields.add(new FakedString(45,"xmlnn", offset -> "<bar>" + (offset + 45) + "</bar>"));

        fakedFields.add(new FakedInt(20,"intNullable", offset -> offset + 17));
        fakedFields.add(new FakedInt(21,"intnn", offset -> offset + 42));
        fakedFields.add(new FakedInt(33,"sintNullable", offset -> offset + 3));
        fakedFields.add(new FakedInt(34,"sintnn", offset -> offset + 7));


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
                stmt.setLong(1, offset + 1);
                stmt.setLong(2, offset + 2);
                stmt.setBoolean(3, true);
                stmt.setBoolean(4, false);

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

                stmt.setDouble(24, offset + 24);
                stmt.setDouble(25, offset + 25);

                stmt.setFloat(29, offset + 29);
                stmt.setFloat(30, offset + 30);

                stmt.setTimestamp(31, new Timestamp((offset + 31) * 100_000));
                stmt.setTimestamp(32, new Timestamp((offset + 32) * 100_000));

                stmt.setShort(33, (short) (offset + 33));
                stmt.setShort(34, (short) (offset + 34));

                stmt.setByte(37, (byte) (0xff & (offset + 37)));
                stmt.setByte(38, (byte) (0xff & (offset + 38)));

                stmt.setString(39, UUID.nameUUIDFromBytes(("" + (offset + 39)).getBytes()).toString());
                stmt.setString(40, UUID.nameUUIDFromBytes(("" + (offset + 40)).getBytes()).toString());

                for (FakedField field : fakedFields) {
                    field.set(stmt, offset);
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
        fakedFields.forEach(fi -> fi.check(spooled, n));
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
