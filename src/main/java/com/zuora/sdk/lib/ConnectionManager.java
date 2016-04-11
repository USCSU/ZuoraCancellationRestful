/**
 * Copyright (c) 2013 Zuora Inc.
 *
 * Sample code to demonstrate how to use the Connections resources
 */

package main.java.com.zuora.sdk.lib;

public class ConnectionManager {
  ZClient zClient;

  public boolean isConnected(ZClient zClient, String apiAccessKeyId, String apiSecretAccessKey) {
    this.zClient = zClient;

    ZAPIArgs args = new ZAPIArgs();
    args.set("uri", ResourceEndpoints.CONNECT);
    args.set("headers", new ZAPIArgs());
    args.getArg("headers").set("apiAccessKeyId", apiAccessKeyId);
    args.getArg("headers").set("apiSecretAccessKey", apiSecretAccessKey);

    System.out.println("========== CONNECT SERVICE ENDPOINT ============");

    ZAPIResp response = null;
    try {
      response = zClient.post(args);
      System.out.println(response.toJSONString());
      if ((Integer)response.get("httpStatusCode") == 200 && (Boolean)response.get("success")) {
        return true;
      }
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      ZLogger.getInstance().log(ZUtils.stackTraceToString(e), ZConstants.LOG_BOTH);
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
      ZLogger.getInstance().log(ZUtils.stackTraceToString(e), ZConstants.LOG_BOTH);
    }
    
    return false;
  }

  public boolean isConnected(ZClient zClient) {
    return isConnected(zClient, null, null);
  }
}