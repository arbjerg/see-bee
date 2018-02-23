package com.namely.seebee.test.sqlserver;

import com.namely.seebee.application.support.logging.SeeBeeLogging;
import com.namely.seebee.crudeventlistener.parquet.ParquetCrudEventListener;
import com.namely.seebee.crudeventlistener.parquet.internal.parquet.ParquetWriterConfiguration;
import com.namely.seebee.crudreactor.CrudEventListener;
import com.namely.seebee.crudreactor.CrudReactorState;
import com.namely.seebee.crudreactor.sqlserver.SqlServerCrudReactor;
import com.namely.seebee.crudreactor.sqlserver.internal.Configuration;
import com.namely.seebee.dockerdb.TestDatabase;
import com.namely.seebee.repository.Repository;
import com.namely.seebee.repository.standard.internal.StandardRepositoryBuilder;
import com.namely.seebee.typemapper.TypeMapper;
import com.namely.seebee.typemapper.standard.StandardTypeMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSparkIngestion {
    static {
        SeeBeeLogging.setSeeBeeLoggingLevel(Level.WARNING);
    }

    private static final String DB_NAME = "seebee";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "Password1";
    private static final String SQL_SERVER_URL_SCHEME = "jdbc:sqlserver";
    private static final String SPOOL_DIRECTORY_NAME = "spool";

    private Repository buildRepository(TestDatabase db, File dir) throws IOException {
        return new StandardRepositoryBuilder()
                .provide(TypeMapper.class).with(new StandardTypeMapper())
                .provide(Configuration.class).with(new SaneTestConfig(db))
                .provide(ParquetWriterConfiguration.class).with(new ParquetConfig(dir, SPOOL_DIRECTORY_NAME))
                .provide(CrudEventListener.class).with(ParquetCrudEventListener.create())
                .provide(SqlServerCrudReactor.class).with(SqlServerCrudReactor.create())
                .build();
    }

    @Test
    void testTrackInserts() throws Exception {
        try (TestDatabase db = TestDatabase.create("sqlserver").start();
             AutoCloseTempDir root = new AutoCloseTempDir();
             Repository repository = buildRepository(db, root.dir());
             TestDatabase sparkHost = TestDatabase.create("spark")
                     .mount(new File(root.dir(), "spool").getAbsolutePath(), "/spool")
                     .start();
             SqlServerCrudReactor reactor = repository.getOrThrow(SqlServerCrudReactor.class);
        ) {

            final String url = createUrl(
                    db.getHostName(),
                    db.getPort(),
                    DB_NAME
            );

            long deadLine = System.currentTimeMillis() + 60_000;
            while(reactor.state() != CrudReactorState.RUNNING && System.currentTimeMillis() < deadLine) {
                Thread.sleep(100);
            }


            int n = 10;
            try (Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO SWITCHES(id, name, state) VALUES (?, ?, ?)")
            ) {
                for (int i=0; i<n; i++) {
                    stmt.setInt(1, i);
                    stmt.setString(2, "name " + i);
                    stmt.setInt(3, i*i+i);
                    stmt.executeUpdate();
                }
            }

            File spoolDir = new File(new File(root.dir(), SPOOL_DIRECTORY_NAME), "SWITCHES");
            while (true) {
                if (spoolDir.exists()) {
                    File[] spooledFiles = spoolDir.listFiles();
                    if (spooledFiles != null && spooledFiles.length > 0) {
                        break;
                    }
                }
                if (System.currentTimeMillis() > deadLine) {
                    throw new InterruptedException();
                }
                Thread.sleep(100);
            }

            String result = sparkHost.execToString("spark-submit", "--master", "local[1]", "/show_table.py", "/spool/SWITCHES");

            assertEquals("+---+-------------------+-----+--------------+\n" +
                    "| id|               name|state|CB_REMOVED|\n" +
                    "+---+-------------------+-----+----------+\n" +
                    "|  0|[6E 61 6D 65 20 30]|    0|     false|\n" +
                    "|  1|[6E 61 6D 65 20 31]|    2|     false|\n" +
                    "|  2|[6E 61 6D 65 20 32]|    6|     false|\n" +
                    "|  3|[6E 61 6D 65 20 33]|   12|     false|\n" +
                    "|  4|[6E 61 6D 65 20 34]|   20|     false|\n" +
                    "|  5|[6E 61 6D 65 20 35]|   30|     false|\n" +
                    "|  6|[6E 61 6D 65 20 36]|   42|     false|\n" +
                    "|  7|[6E 61 6D 65 20 37]|   56|     false|\n" +
                    "|  8|[6E 61 6D 65 20 38]|   72|     false|\n" +
                    "|  9|[6E 61 6D 65 20 39]|   90|     false|\n" +
                    "+---+-------------------+-----+----------+", result.trim());
        }
    }



    private static String createUrl(String ipAddress, Integer port, String name) {
        return SQL_SERVER_URL_SCHEME +
                "://" + ipAddress +
                ":" + port +
                ";databaseName=" + name;
    }


}
