package com.namely.seebee.softwareinfo.standard.internal;

import com.namely.seebee.softwareinfo.SoftwareInfo;

/**
 *
 * @author Per Minborg
 */
public final class SeeBeeSoftwareInfo implements SoftwareInfo {

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
    public boolean isProductionMode() {
        return SoftwareInfoUtil.isProductionMode(this);
    }

}
