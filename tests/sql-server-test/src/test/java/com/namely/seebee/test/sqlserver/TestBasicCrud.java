package com.namely.seebee.test.sqlserver;

import com.namely.seebee.application.internal.util.RepositoryUtil;
import com.namely.seebee.configuration.Configuration;
import com.namely.seebee.crudreactor.*;
import com.namely.seebee.crudreactor.sqlserver.SqlServerCrudReactor;
import com.namely.seebee.dockerdb.DockerException;
import com.namely.seebee.dockerdb.TestDatabase;
import com.namely.seebee.repository.Repository;
import com.namely.seebee.typemapper.TypeMapper;
import com.namely.seebee.typemapper.standard.StandardTypeMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class TestBasicCrud {

    private static final String DB_NAME = "speedment";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "Password1";
    private static final String SQL_SERVER_URL_SCHEME = "jdbc:sqlserver";

    @Test
    void testConnect() throws DockerException, IOException, InterruptedException {
        TestDatabase sqlserver = TestDatabase.createDockerDatabase("sqlserver");
        //Thread.sleep(10_000_000);
        sqlserver.close();
    }

    private Repository buildRepository(TestDatabase db, EventSink sink) {
        return RepositoryUtil.standardRepositoryBuilder()
                .provide(TypeMapper.class).with(new StandardTypeMapper())
                .provide(Configuration.class).with(new TestConfig(db))
                .provide(CrudEventListener.class).with(sink)
                .provide(SqlServerCrudReactor.class).applying(SqlServerCrudReactor::create)
                .build();
    }

    private class EventSink implements CrudEventListener, AutoCloseable {
        private final List<RowEvent> allEvents = new ArrayList<>();
        private volatile SQLException exceptionBeforeClose;

        @Override
        public Optional<String> startVersion() {
            return Optional.empty();
        }

        @Override
        public void newEvents(CrudEvents events) {
            synchronized (allEvents) {
                try {
                    events.stream().forEach(allEvents::add);
                } catch (SQLException e) {
                    exceptionBeforeClose = e;
                }
            }
        }

        List<RowEvent> events() {
            return allEvents;
        }

        @Override
        public void close() throws SQLException {
            if (exceptionBeforeClose != null) {
                throw exceptionBeforeClose;
            }
        }
    }

    @Test
    void testTrackInserts() throws Exception {

        try (TestDatabase db = TestDatabase.createDockerDatabase("sqlserver");
             EventSink sink = new EventSink();
             Repository repository = buildRepository(db, sink);
             SqlServerCrudReactor reactor = repository.getOrThrow(SqlServerCrudReactor.class)) {

            final String url = createUrl(
                    db.getHostName(),
                    db.getPort(),
                    DB_NAME
            );

            long deadLine = System.currentTimeMillis() + 60_000;
            while(reactor.state() != CrudReactorState.RUNNING && System.currentTimeMillis() < deadLine) {
                Thread.sleep(100);
            }


            int n = 1000;
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

            while (sink.events().size() < n) {
                Thread.sleep(100);
            }

            List<RowEvent> events = sink.events();
            for (int i = 0; i < events.size(); i++) {
                final int idx = i;
                RowEvent event = events.get(idx);
                assertEquals(CrudEventType.ADD, event.type());
                event.values().columns().forEach(column -> {
                    switch(column.name()) {
                        case "id":
                            assertEquals(idx, column.get());
                            break;
                        case "name":
                            assertEquals("name " + idx, column.get());
                            break;
                        case "state":
                            assertEquals(idx*idx+idx, column.get());
                            break;
                        default:
                            fail("Bad column name: " + column.name());
                    }
                });
            }
        }
    }

    @Test
    void testTrackUpdates() throws Exception {
        try (TestDatabase db = TestDatabase.createDockerDatabase("sqlserver");
             EventSink sink = new EventSink();
             Repository repository = buildRepository(db, sink);
             SqlServerCrudReactor reactor = repository.getOrThrow(SqlServerCrudReactor.class)) {

            final String url = createUrl(
                    db.getHostName(),
                    db.getPort(),
                    DB_NAME
            );

            long deadLine = System.currentTimeMillis() + 60_000;
            while(reactor.state() != CrudReactorState.RUNNING && System.currentTimeMillis() < deadLine) {
                Thread.sleep(100);
            }


            int n = 1_000;
            try (Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO SWITCHES(id, name, state) VALUES (?, ?, ?)")
            ) {
                for (int i=0; i<n; i++) {
                    stmt.setInt(1, i);
                    stmt.setString(2, "name " + i);
                    stmt.setInt(3, 100);
                    stmt.executeUpdate();
                }
            }

            Integer endValue = 0;
            int u = n * 10;
            Random random = new Random(42);
            try (Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement("UPDATE SWITCHES SET state = ? WHERE id = ?")
            ) {
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
            }


            deadLine = System.currentTimeMillis() + 120_000;

            while (true) {
                Thread.sleep(100);

                try {
                    Map<Integer, Integer> states = new HashMap<>();
                    sink.events().forEach(event -> {
                        Map<String, Integer> intValues = event.values().columns()
                                .collect(HashMap::new, (m, c) -> {
                                    if (int.class.equals(c.javaType())) {
                                        m.put(c.name(), (Integer) c.get());
                                    }
                                }, HashMap::putAll);
                        states.put(intValues.get("id"), intValues.get("state"));
                    });

                    if (states.values().stream().allMatch(endValue::equals)) {
                        break;
                    }
                } catch (ConcurrentModificationException e) {
                    // expected to happen while results are coming in
                }
                assertTrue(System.currentTimeMillis() < deadLine, "Timed out before receiving all updates");
            }
        }
    }

    @Test
    void testTrackDeletes() throws Exception {
        try (TestDatabase db = TestDatabase.createDockerDatabase("sqlserver");
             EventSink sink = new EventSink();
             Repository repository = buildRepository(db, sink);
             SqlServerCrudReactor reactor = repository.getOrThrow(SqlServerCrudReactor.class)) {

            final String url = createUrl(
                    db.getHostName(),
                    db.getPort(),
                    DB_NAME
            );

            long deadLine = System.currentTimeMillis() + 60_000;
            while(reactor.state() != CrudReactorState.RUNNING && System.currentTimeMillis() < deadLine) {
                Thread.sleep(100);
            }


            int n = 1_000;
            try (Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO SWITCHES(id, name, state) VALUES (?, ?, ?)")
            ) {
                for (int i=0; i<n; i++) {
                    stmt.setInt(1, i);
                    stmt.setString(2, "name " + i);
                    stmt.setInt(3, i);
                    stmt.executeUpdate();
                }
            }

            int u = n * 10;
            Random random = new Random(42);
            try (Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement("UPDATE SWITCHES SET state = ? WHERE id = ?")
            ) {
                for (int i=0; i<u; i++) {
                    stmt.setInt(1, random.nextInt(9999));
                    stmt.setInt(2, random.nextInt(n));
                    stmt.executeUpdate();
                }
            }


            try (Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement("DELETE FROM SWITCHES WHERE id = ?")
            ) {
                for (int i=0; i<n; i++) {
                    stmt.setInt(1, i);
                    stmt.executeUpdate();
                }
            }


            deadLine = System.currentTimeMillis() + 120_000;

            while (true) {
                Thread.sleep(100);

                try {
                    boolean[] deleted = new boolean[n];

                    sink.events().forEach(event -> {
                        if (CrudEventType.REMOVE.equals(event.type())) {
                            event.values().columns()
                                    .filter(c -> "id".equals(c.name()))
                                    .limit(1)
                                    .forEach(columnValue -> deleted[(int) columnValue.get()] = true);
                        }
                    });

                    boolean allDone = true;
                    for (boolean d : deleted) {
                        if (!d) {
                            allDone = false;
                            break;
                        }
                    }
                    if (allDone) {
                        break;
                    }
                } catch (ConcurrentModificationException e) {
                    // expected to happen while results are coming in
                }
                assertTrue(System.currentTimeMillis() < deadLine, "Timed out before receiving all deletes");
            }
        }
    }


    private static String createUrl(String ipAddress, Integer port, String name) {
        return SQL_SERVER_URL_SCHEME +
                "://" + ipAddress +
                ":" + port +
                ";databaseName=" + name;
    }
}
