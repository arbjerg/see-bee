package com.namely.seebee.configuration.internal.yaml;

import com.namely.seebee.configuration.Configuration;
import static com.namely.seebee.configuration.Configuration.JDBC_PASSWORD_KEY;
import static com.namely.seebee.configuration.Configuration.JDBC_USERNAME_KEY;
import static com.namely.seebee.configuration.Configuration.SCHEMA_RELOAD_INTERVAL_SECONDS_KEY;
import com.namely.seebee.repository.HasComponents;
import com.namely.seebee.repository.Parameter;
import com.namely.seebee.repository.StringParameter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Per Minborg
 */
final class YamlConfigurationTest {

    private static final int TEST_VALUE_FOR_SCHEMA_RELOADE_INTERVAL_SECONDS = 165;
    private static final String TEST_VALUE_FOR_JDBC_USERNAME = "someName123";
    private static final String TEST_VALUE_FOR_JDBC_PASSWORD = "somePassword456";

    // Not supported yet
    @Test
    void testConstructor() throws IOException {

        final Path tmpFile = Files.createTempFile(YamlConfiguration.class.getSimpleName() + "TestFile", "RemoveMe");
        tmpFile.toFile().deleteOnExit();

        System.out.println("File is " + tmpFile);

        final List<String> lines = List.of(
            "# This is a comment",
            SCHEMA_RELOAD_INTERVAL_SECONDS_KEY + ": " + TEST_VALUE_FOR_SCHEMA_RELOADE_INTERVAL_SECONDS,
            JDBC_USERNAME_KEY + ": " + TEST_VALUE_FOR_JDBC_USERNAME,
            JDBC_PASSWORD_KEY + ": " + TEST_VALUE_FOR_JDBC_PASSWORD
        );

//        final Repository repo = Repository.builder()
//            .provide(StringParameter.class).with(StringParameter.of(Configuration.YAML_FILE_NAME_CONFIGURATION, tmpFile.toString()))
//            .build();

        final HasComponents repo = new HasComponents() {
            @Override
            public <T> Stream<T> stream(Class<T> type) {
                return Stream.empty();
            }

            @Override
            public <T> Optional<T> get(Class<T> type) {
                return Optional.empty();
            }

            @Override
            public <T> T getOrThrow(Class<T> type) {
                throw new NoSuchElementException();
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T extends Parameter<?>> Optional<T> getParameter(Class<T> parameterType, String name) {
                if (StringParameter.class.equals(parameterType)) {
                    if (Configuration.YAML_FILE_NAME_CONFIGURATION.equals(name)) {
                        return (Optional<T>)Optional.of(new StringParameter() {
                            @Override
                            public String name() {
                                return name;
                            }

                            @Override
                            public String get() {
                                return tmpFile.toString();
                            }
                        });
                    }
                }
                return Optional.empty();
            }
        };
        

        Files.write(tmpFile, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        final Configuration configuration = new YamlConfiguration(repo);

        assertEquals(TEST_VALUE_FOR_SCHEMA_RELOADE_INTERVAL_SECONDS, configuration.schemaReloadIntervalSeconds());
        assertEquals(Optional.of(TEST_VALUE_FOR_JDBC_USERNAME), configuration.jdbcUsername());
        assertEquals(Optional.of(TEST_VALUE_FOR_JDBC_PASSWORD), configuration.jdbcPassword());

    }
}
