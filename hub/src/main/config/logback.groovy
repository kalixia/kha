import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import static ch.qos.logback.classic.Level.*

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%X{CLIENT_ADDR}/%X{REQUEST_ID}] [%thread] %-5level %logger{36} - %msg%n"
    }
    withJansi = true
}

appender("FILE", RollingFileAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%X{CLIENT_ADDR}/%X{REQUEST_ID}] [%thread] %-5level %logger{36} - %msg%n"
//        immediateFlush = false
    }
    append = true
    file = '../logs/hub.log'
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = 'hub.%d{yyyy-MM-dd}.log'
        maxHistory = 30
    }
}

logger "com.kalixia.ha", INFO
logger "com.kalixia.grapi", DEBUG
logger "org.apache.shiro", INFO
logger "io.netty", DEBUG
logger "org.hibernate.validator", WARN

root(WARN, ["STDOUT", "FILE"])