package com.namely.seebee.application.internal.util;

import com.namely.seebee.condiguration.Configuration;
import com.namely.seebee.repository.Repository;
import com.namely.seebee.version.Version;

/**
 *
 * @author Per Minborg
 */
public  final class RepositoryUtil {
    
    private RepositoryUtil() { throw new UnsupportedOperationException(); }
    
    public static Repository.Builder standardRepositoryBuilder() {
        return Repository.builder()
            .provide(Configuration.class).getting(Configuration::defaultConfiguration)
            .provide(Version.class).getting(Version::defaultVersion);
    }
    
}
