package com.namely.seebee.crudreactor.sqlserver.internal.engine;

import java.text.MessageFormat;

public class SqlServerNumberedVersion {
    public static final SqlServerNumberedVersion ZERO = new SqlServerNumberedVersion(0);
    private static final String PREFIX = "SqlServer Data v";

    private final long versionNumber;

    public SqlServerNumberedVersion(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    static SqlServerNumberedVersion fromString(String s) {
        if (s.startsWith(PREFIX)) {
            return new SqlServerNumberedVersion(Long.parseLong(s.substring(PREFIX.length())));
        }
        throw new IllegalArgumentException("Unknown version: " + s);
    }

    public String dumpToString() {
        return PREFIX + Long.toString(versionNumber);
    }

    public long getVersionNumber() {
        return versionNumber;
    }

    @Override
    public String toString() {
        return MessageFormat.format("SqlServerDataVersion({0})", versionNumber);
    }
}
