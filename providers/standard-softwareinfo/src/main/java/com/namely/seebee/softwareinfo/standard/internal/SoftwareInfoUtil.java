package com.namely.seebee.softwareinfo.standard.internal;

import com.namely.seebee.softwareinfo.SoftwareInfo;
import java.util.stream.Stream;

/**
 *
 * @author Per Minborg
 */
public final class SoftwareInfoUtil {

    private SoftwareInfoUtil() {
        throw new UnsupportedOperationException();
    }

    public static boolean isProductionMode(SoftwareInfo version) {
        return Stream.of("EA", "SNAPSHOT")
            .noneMatch(version.version().toUpperCase()::contains);
    }

}
