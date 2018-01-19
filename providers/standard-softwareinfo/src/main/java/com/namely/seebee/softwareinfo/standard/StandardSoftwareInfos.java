package com.namely.seebee.softwareinfo.standard;

import com.namely.seebee.softwareinfo.SoftwareInfo;
import com.namely.seebee.softwareinfo.standard.internal.JavaSoftwareInfo;
import com.namely.seebee.softwareinfo.standard.internal.SeeBeeSoftwareInfo;

/**
 *
 * @author Per Minborg
 */
public final class StandardSoftwareInfos {

    private StandardSoftwareInfos() {
        throw new UnsupportedOperationException();
    }

    private static final SoftwareInfo SEE_BEE_VERSION = new SeeBeeSoftwareInfo();
    private static final SoftwareInfo JAVA_VERSION = new JavaSoftwareInfo();

    /**
     * Returns a version applicable for See Bee
     *
     * @return a version applicable for See Bee
     */
    public static SoftwareInfo seeBee() {
        return SEE_BEE_VERSION;
    }

    /**
     * Returns a version applicable for Java
     *
     * @return a version applicable for Java
     */
    public static SoftwareInfo java() {
        return JAVA_VERSION;
    }

}
