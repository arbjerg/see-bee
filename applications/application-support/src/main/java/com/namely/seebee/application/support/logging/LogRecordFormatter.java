package com.namely.seebee.application.support.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import static java.util.stream.Collectors.joining;

public class LogRecordFormatter extends Formatter {
    private static String FORMAT = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %3$-7s %2$-70.70s - %4$s %5$s%n";

    @Override
    public String format(LogRecord record) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(
                record.getInstant(), ZoneId.systemDefault());
        String source;
        if (record.getSourceClassName() != null) {
            source = shortenQualifiedClassName(record.getSourceClassName());
            if (record.getSourceMethodName() != null) {
                source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }

        return String.format(FORMAT,
                zdt,
                source,
                record.getLevel().getName(),
                message,
                throwable);
    }

    private String shortenQualifiedClassName(String name) {
        String[] splitted = name.split("\\.");
        String dotted = Arrays.stream(splitted).map(s -> s.substring(0, 1)).collect(joining("."));
        return dotted.substring(0, dotted.length() - 1) + splitted[splitted.length - 1];
    }

}
