/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class ZConfig {

  private static ZConfig instance_;
  private Properties zProperties;
  private String configFilePath;
  private InputStream is;

  public ZConfig() {

    // The config file can be passed directly in the environment
    configFilePath = System.getProperty("zuora.config.file");
    if (configFilePath == null || !(new File(configFilePath).isFile())) {
      // Otherwise, local config file supercedes the one in SDK
      configFilePath = new File(System.getProperty("user.dir"), ZConstants.CONFIG_FILE).toString();
    }

    // Attempt to use the local one, if not, use the one in the SDK
    try {
      if (!(new File(configFilePath).isFile())) {
        configFilePath = ZConstants.SDK_CONFIG_FILE_PATH;
        is = this.getClass().getClassLoader().getResourceAsStream(configFilePath);
        if (is == null) throw new FileNotFoundException("Failed to find config file " + configFilePath);
      } else {
        is = new FileInputStream(configFilePath);
      }
    } catch (FileNotFoundException e) {
      ZLogger.getInstance().log("message " + e.getMessage(), ZConstants.LOG_SDK);
      ZLogger.getInstance().log("trace " + ZUtils.stackTraceToString(e), ZConstants.LOG_SDK);
      String error_msg = "Failed to load configuration_file " + configFilePath;
      ZLogger.getInstance().log(error_msg, ZConstants.LOG_SDK);
      throw new RuntimeException(error_msg);
    }
    ZLogger.getInstance().log("configFilePath="+configFilePath + " opened successfully", ZConstants.LOG_SDK);
  }

  public void loaded() {
    // load and log all properties
    ZLogger.getInstance().log("Loading configuration file " + configFilePath + " ...", ZConstants.LOG_SDK);

    zProperties = new Properties();
    try {
      zProperties.load(is);
      TreeMap tm = new TreeMap(zProperties);
      Iterator i = tm.entrySet().iterator();
      while (i.hasNext()) {
        Map.Entry me = (Map.Entry)i.next();
        if (((String)me.getKey()).contains("password")) {
          ZLogger.getInstance().log(me.getKey() + " set to ********", ZConstants.LOG_SDK);
        } else {
          ZLogger.getInstance().log(me.getKey() + " set to " + me.getValue(), ZConstants.LOG_SDK);
        }
      }
    }catch(Exception e){
      ZLogger.getInstance().log("message " + e.getMessage(), ZConstants.LOG_SDK);
      ZLogger.getInstance().log("trace " + ZUtils.stackTraceToString(e), ZConstants.LOG_SDK);
      String error_msg = "Failed to load configuration_file " + configFilePath;
      ZLogger.getInstance().log(error_msg, ZConstants.LOG_SDK);
      throw new RuntimeException(error_msg);
    }
    ZLogger.getInstance().log("Configuration file " + configFilePath + " successfully loaded.", ZConstants.LOG_SDK);
  }


  public Object getVal(String key) {
	return zProperties.getProperty(key);
  }

  public static ZConfig getInstance() {
    if (instance_ == null) {
      instance_ = new ZConfig();
    }
    return instance_;
  }
}