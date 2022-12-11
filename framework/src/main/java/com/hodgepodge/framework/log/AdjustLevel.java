package main.java.com.hodgepodge.framework.log;

import ch.qos.logback.classic.LoggerContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fengdui1
 * @since 2022/3/4 1:50 下午
 */
public class AdjustLevel {

    public void log4j(String logger, String level) {

        Logger log = null;
        if (StringUtils.isNotEmpty(logger)) {
            log = LogManager.getLogger(logger);
            if (log == null) {
                throw new RuntimeException("logger not exist");
            }
        } else {
            log = LogManager.getRootLogger();
        }
        log.setLevel(Level.toLevel(level));
    }

    public void logback(String logger, String level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger log = context.getLogger(logger);
        if (log != null) {
            log.setLevel(ch.qos.logback.classic.Level.toLevel(level));
        } else {
            throw new RuntimeException("logger not exist");
        }
    }
}
