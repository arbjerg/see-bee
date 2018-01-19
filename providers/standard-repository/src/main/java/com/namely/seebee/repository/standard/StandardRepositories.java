package com.namely.seebee.repository.standard;

import com.namely.seebee.repository.Repository;
import com.namely.seebee.repository.standard.internal.StandardRepositoryBuilder;

/**
 *
 * @author Per Minborg
 */
public final class StandardRepositories {

    private StandardRepositories() {
        throw new UnsupportedOperationException();
    }

    public static Repository.Builder builder() {
        return new StandardRepositoryBuilder();
    }
    
}
