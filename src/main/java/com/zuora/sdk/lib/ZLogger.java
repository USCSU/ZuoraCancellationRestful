/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ZLogger {

  private static final int FILE_SIZE = 4096000;
  private static final int FILE_AGE = 3;
  private static ZLogger instance_;
  private Logger sdkLogger = Logger.getLogger("sdkLogger");
  private Logger apiLogger = Logger.getLogger("apiLogger");

  public ZLogger() {
    // The logging directory can be passed directly in the environment (must already exist)
    String sdkLogDir = System.getProperty("zuora.log.dir");
    if (sdkLogDir == null || !(new File(sdkLogDir).isDirectory())) {
      // Otherwise, use the hard-coded value
      sdkLogDir = new File(System.getProperty("user.dir"), ZConstants.LOG_DIR).toString();
    }

    // Make the logging directory if necessary
    if (!(new File(sdkLogDir).isDirectory())) {
      new File(sdkLogDir).mkdir();
    }

    // Configure the Java Logger
    configLogger(sdkLogger, sdkLogDir, ZConstants.LOG_FILENAME);
    configLogger(apiLogger, sdkLogDir, ZConstants.API_TRACE_FILENAME);
  }

  class MyFormatter extends Formatter {

    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public String format(LogRecord record) {
      StringBuilder builder = new StringBuilder(1000);
      builder.append(df.format(new Date(record.getMillis()))).append(" ");
      builder.append(Thread.currentThread().getId()).append(" ");
      builder.append(formatMessage(record));
      builder.append("\n");
      return builder.toString();
    }
  }

  public static ZLogger getInstance() {
    if (instance_ == null) {
      instance_ = new ZLogger();
    }
    return instance_;
  }

  private void configLogger(Logger logger, String logDir, String logName) {
    try {
      FileHandler handler = new FileHandler(new File(logDir, logName).toString(),
        FILE_SIZE, FILE_AGE, true);
      handler.setFormatter(new MyFormatter());
      logger.addHandler(handler);
      logger.setUseParentHandlers(false);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public void log(String msg, int logDest) {
    switch (logDest) {
      case ZConstants.LOG_SDK:
        sdkLogger.info(msg);
        break;
      case ZConstants.LOG_API:
        apiLogger.info(msg);
        break;
      default:
        sdkLogger.info(msg);
        apiLogger.info(msg);
    }
  }
}