/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONTokener;

public final class ZAPIResp {

  private ZAPIObject object = null;
  private String preTokenizedString = null;

  protected ZAPIResp(JSONTokener jsonTokener) {
    object = new ZAPIObject(jsonTokener);
  }

  public ZAPIResp(String jsonString) {
    this(new ZAPIJSONTokener(jsonString));
    preTokenizedString = jsonString;
  }

  public ZAPIResp getResp(String propertyName) {
    return (ZAPIResp) get(propertyName);
  }

  public Object get(String propertyName) {
    try {
      return object.getValue(propertyName);
    } catch (JSONException e) {
      return null;
    }
  }

  public String toJSONString() {
   return preTokenizedString;
  }

  @SuppressWarnings("rawtypes")
	public Iterator iterator(String propertyName){
		try {
			return object.iterator(propertyName);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
