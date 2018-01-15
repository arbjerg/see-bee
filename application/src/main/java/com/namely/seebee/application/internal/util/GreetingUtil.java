package com.namely.seebee.application.internal.util;

import com.namely.seebee.version.Version;

/**
 *
 * @author Per Minborg
 */
public final class GreetingUtil {

    private GreetingUtil() { throw new UnsupportedOperationException(); }

    public static String seeBeeGreetingMessage(Version version) {
        String greeting
            = "   _____             ____            \n"
            + "  / ____|           |  _ \\           \n"
            + " | (___   ___  ___  | |_) | ___  ___ \n"
            + "  \\___ \\ / _ \\/ _ \\ |  _ < / _ \\/ _ \\\n"
            + "  ____) |  __/  __/ | |_) |  __/  __/\n"
            + " |_____/ \\___|\\___| |____/ \\___|\\___|\n"
            + " :: " + version.name() + " by " + version.vendor()
            + " :: " + version.version();
        return greeting;
    }

    public static String jvmGreetingMessage(Version version) {
        return "Running under " + version.jvmImplementationName() + " by " + version.jvmImplementationVendor() + ", version " + version.jvmImplementationVersion();
    }

}
