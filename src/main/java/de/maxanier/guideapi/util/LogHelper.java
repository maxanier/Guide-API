package de.maxanier.guideapi.util;

import de.maxanier.guideapi.GuideConfig;
import de.maxanier.guideapi.GuideMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {

    private static final Logger logger = LogManager.getLogger(GuideMod.NAME);

    /**
     * @param info - String to log to the info level
     */

    public static void info(String info) {
        if (GuideConfig.COMMON.enableLogging.get())
            logger.info(info);
    }

    /**
     * @param error - String to log to the error level
     */

    public static void error(String error, Object... args) {
        if (GuideConfig.COMMON.enableLogging.get())
            logger.error(error, args);
    }

    /**
     * @param debug - String to log to the debug level
     */

    public static void debug(String debug) {
        if (GuideConfig.COMMON.enableLogging.get())
            logger.debug(debug);
    }
}
