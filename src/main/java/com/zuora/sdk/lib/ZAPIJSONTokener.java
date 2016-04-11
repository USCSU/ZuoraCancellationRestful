/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

class ZAPIJSONTokener extends JSONTokener {

  public ZAPIJSONTokener(String s) {
    super(s);
  }

  public Object nextValue() throws JSONException {
    char c = this.nextClean();
    String string;

    switch (c) {
      case '"':
      case '\'':
        return this.nextString(c);
      case '{':
        this.back();
        return new ZAPIResp(this);
      case '[':
        this.back();
        return new JSONArray(this);
    }

    /*
     * Handle unquoted text. This could be the values true, false, or
     * null, or it can be a number. An implementation (such as this one)
     * is allowed to also accept non-standard forms.
     *
     * Accumulate characters until we reach the end of the text or a
     * formatting character.
     */

    StringBuffer sb = new StringBuffer();
    while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
      sb.append(c);
      c = this.next();
    }
    this.back();

    string = sb.toString().trim();
    if ("".equals(string)) {
      throw this.syntaxError("Missing value");
    }
    return JSONObject.stringToValue(string);
  }
}
