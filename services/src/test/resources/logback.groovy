import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.WARN

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%X{user} %thread] %-5level %logger{36} - %msg%n"
    }
    withJansi = true
}

logger "com.kalixia.ha", INFO
logger "com.kalixia.ha.api.rest", DEBUG

root(WARN, ["STDOUT"])