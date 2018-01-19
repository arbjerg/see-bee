package com.namely.seebee.softwareinfo.standard.internal;

import com.namely.seebee.softwareinfo.SoftwareInfo;

/**
 *
 * @author Per Minborg
 */
public final class JavaSoftwareInfo implements SoftwareInfo {

    @Override
    public String version() {
        return System.getProperty("java.vm.version");
    }

    @Override
    public String vendor() {
        return System.getProperty("java.vm.vendor");
    }

    @Override
    public String name() {
        return System.getProperty("java.vm.name");
    }

    @Override
    public boolean isProductionMode() {
        return SoftwareInfoUtil.isProductionMode(this);
    }

}
