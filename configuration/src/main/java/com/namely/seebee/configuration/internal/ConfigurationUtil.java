package com.namely.seebee.configuration.internal;

import com.namely.seebee.configuration.Configuration;

/**
 *
 * @author Per Minborg
 */
public final class ConfigurationUtil {
    
    private ConfigurationUtil() {
        throw new UnsupportedOperationException();
    }
    
    public static String toString(Configuration c) {
        return new StringBuilder()
            .append(c.getClass().getSimpleName())
            .append(" {")
            .append("schemaReloadIntervalSeconds").append("=").append(c.schemaReloadIntervalSeconds()).append(", ")
            .append("jdbcUsername").append("=").append(c.jdbcUsername()).append(", ")
            .append("jdbcPassword").append("=").append(c.jdbcPassword().map(pw -> "********"))
            .append("}")
            .toString();
        
    }
    
}
