/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONString;

public final class ZAPIArgs implements JSONString {

  private ZAPIObject object = null;

  public ZAPIArgs() {
    object = new ZAPIObject();
  }

  public void set(String propertyName, Object value) {
    try {
      if (propertyName == null) {
        throw new RuntimeException("Property name can't be null.");
      }
      if (!isValidValue(value)) {
        throw new RuntimeException("Don't support this kind of value:"
          + value);
      }
      object.setValue(propertyName, value);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }
  
  public void setArray(String propertyName) {
    object.initPropertyAsArray(propertyName);
  }

  public ZAPIArgs getArg(String propertyName) {
    return (ZAPIArgs) get(propertyName);
  }

  public Object get(String propertyName) {
    try {
      return object.getValue(propertyName);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      // throw new RuntimeException(e);
      return null;
    }
  }

  private boolean isValidValue(Object value) {
    if (value instanceof ZAPIArgs) {
      return true;
    }
    return object.isValidValue(value);
  }

  public String toJSONString() {
    return object.toJSONString();
  }

  public String toString() {
    return object.toString();
  }

  public Iterator keys() {
    return object.keys();
  }
}
