/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;

class ZAPIObject implements JSONString {

  private JSONObject jsonObject = null;

  public ZAPIObject(JSONTokener tokener) {
    try {
      jsonObject = new JSONObject(tokener);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public ZAPIObject() {
    jsonObject = new JSONObject();
  }

  public void initPropertyAsArray(String propertyName) {
    if (propertyName == null) {
      throw new RuntimeException("Property name can't be null.");
    }
    try {
      jsonObject.put(propertyName, new JSONArray());
    } catch (JSONException e) {
      throw new RuntimeException();
    }
  }

  protected void setValue(String propertyName, Object value)
          throws JSONException {
    int arrayIndex = getArrayIndex(propertyName);
    if (arrayIndex < 0) {
      jsonObject.put(propertyName, value);
    } else {
      String arrayPropertyName = getArrayPropertyName(propertyName);
      JSONArray jsonArray = (JSONArray) jsonObject.get(arrayPropertyName);
      if (jsonArray == null) {
        throw new RuntimeException(
          "the array doesn't exist, please call the method to init a array.");
      }
      jsonArray.put(arrayIndex, value);
    }
  }

  protected Object getValue(String propertyName) throws JSONException {
    if (propertyName == null) {
      throw new RuntimeException("Property name can't be null.");
    }
    int arrayIndex = getArrayIndex(propertyName);
    if (arrayIndex < 0) {
      return jsonObject.get(propertyName);
    } else {
      String arrayPropertyName = getArrayPropertyName(propertyName);
      JSONArray jsonArray = (JSONArray) jsonObject.get(arrayPropertyName);
      if (jsonArray == null) {
        throw new RuntimeException(
                "Invalid call, the array don't exist.");
      }
      return jsonArray.get(arrayIndex);
    }
  }

  protected boolean isValidValue(Object value) {
    if (value == null) {
      return true;
    }
    if (isJsonSupportType(value.getClass())) {
      return true;
    }
    return false;
  }

  protected boolean isJsonSupportType(Class<?> clazz) {// exclude boolean and
    // Boolean
    return (clazz.isPrimitive() || String.class.isAssignableFrom(clazz)
            || Boolean.class.isAssignableFrom(clazz)
            || Integer.class.isAssignableFrom(clazz)
            || Long.class.isAssignableFrom(clazz)
            || Float.class.isAssignableFrom(clazz)
            || Character.class.isAssignableFrom(clazz)
            || Short.class.isAssignableFrom(clazz)
            || Double.class.isAssignableFrom(clazz)
            || BigDecimal.class.isAssignableFrom(clazz) || BigInteger.class.isAssignableFrom(clazz));
  }

  protected String getArrayPropertyName(String propertyName) {
    int startBracketIndex = propertyName.lastIndexOf('[');
    return propertyName.substring(0, startBracketIndex);
  }

  protected int getArrayIndex(String propertyName) {
    int endBracketIndex = propertyName.lastIndexOf(']');
    if (endBracketIndex < 0) {
      return -1;
    }
    int startBracketIndex = propertyName.lastIndexOf('[');
    if (startBracketIndex < 0) {
      return -1;
    }
    if (startBracketIndex >= endBracketIndex) {
      return -1;
    }
    String number = propertyName.substring(startBracketIndex + 1,
            endBracketIndex);
    try {
      return Integer.parseInt(number);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  @Override
  public String toJSONString() {
    try {
      return jsonObject.toString(2);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

	@SuppressWarnings("unchecked")
	Iterator<String> keys(){
		return jsonObject.keys();
	}

	@SuppressWarnings("rawtypes")
	Iterator iterator(String propertyName) throws JSONException{
		Object obj = jsonObject.get(propertyName);
		if(!(obj instanceof JSONArray))
			throw new RuntimeException("it is not a Array");
		return new JSONIterator((JSONArray)obj);

	}

	@SuppressWarnings("rawtypes")
	class JSONIterator implements Iterator{

		JSONArray array;
		int pos = 0;
		JSONIterator(JSONArray jsonArray){
			array = jsonArray;
		}

		@Override
		public boolean hasNext() {
			return pos<array.length();
		}

		@Override
		public Object next() {
			try {
				return array.get(pos++);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException("Don't support this method");
		}
	}
}
