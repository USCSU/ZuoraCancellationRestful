/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

public class ZUtils {

  public static String stackTraceToString(Throwable e) {
    StringBuilder sb = new StringBuilder();
    for (StackTraceElement element : e.getStackTrace()) {
        sb.append(element.toString());
        sb.append("\n");
    }
    return sb.toString();
  }
}