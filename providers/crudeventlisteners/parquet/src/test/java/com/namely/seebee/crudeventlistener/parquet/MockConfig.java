package com.namely.seebee.crudeventlistener.parquet;

import com.namely.seebee.crudeventlistener.parquet.internal.parquet.ParquetWriterConfiguration;
import com.namely.seebee.repositoryclient.HasConfiguration;
import com.namely.seebee.repositoryclient.Parameter;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

class MockConfig extends ParquetWriterConfiguration {
    public static HasConfiguration buildRepo(MockConfig config) {
        return new HasConfiguration() {
            @Override
            public <T> Stream<T> stream(Class<T> type) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> Optional<T> get(Class<T> type) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T getOrThrow(Class<T> type) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> Stream<T> streamOfTrait(Class<T> trait) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T extends Parameter<?>> Optional<T> getParameter(Class<T> parameterType, String name) {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T getConfiguration(Class<T> configurationBeanClass) {
                if (configurationBeanClass.isAssignableFrom(MockConfig.class)) {
                    return (T) config;
                }
                throw new UnsupportedOperationException();
            }
        };
    }

    MockConfig(File workDir) {
        setWorkDirectory(workDir.getAbsolutePath());
        File spool = new File(workDir, "spool");
        if (!spool.mkdirs()) {
            throw new RuntimeException("Unable to create spool directory " + getSpoolDirectory());
        }
        setSpoolDirectory(spool.getAbsolutePath());
        resolve();
    }
}
