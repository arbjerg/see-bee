package com.namely.seebee.version.internal;

import com.namely.seebee.version.Version;

/**
 *
 * @author Per Minborg
 */
public class DefaultVersion implements Version {

    @Override
    public String version() {
        return "0.0.1-SNAPSHOT";
    }

    @Override
    public String name() {
        return "See Bee";
    }

    @Override
    public String vendor() {
        return "Namely, Inc.";
    }

    @Override
    public String jvmImplementationVersion() {
        return System.getProperty("java.vm.version");
    }

    @Override
    public String jvmImplementationVendor() {
        return System.getProperty("java.vm.vendor");
    }

    @Override
    public String jvmImplementationName() {
        return System.getProperty("java.vm.name");
    }

}
