package com.namely.seebee.condiguration.internal;

import com.namely.seebee.condiguration.Configuration;

/**
 *
 * @author Per Minborg
 */
public class DefaultConfiguration implements Configuration {

    @Override
    public String greetingLogo() {
        return "See Bee";
    }

}
