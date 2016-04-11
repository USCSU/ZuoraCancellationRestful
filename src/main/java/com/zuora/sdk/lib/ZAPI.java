/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ZAPI {

  private String defaultTenantUserId;
  private String defaultTenantPassword;
  private String connectTenantUserId;
  private String connectTenantPassword;
  private DefaultHttpClient zHttpClient;

  public ZAPI(String defaultTenantUserId, String defaultTenantPassword) {
    this.defaultTenantUserId = defaultTenantUserId;
    this.defaultTenantPassword = defaultTenantPassword;
  }

  public void setConnectCredentials(String connectTenantUserId, String connectTenantPassword) {
    this.connectTenantUserId = connectTenantUserId;
    this.connectTenantPassword = connectTenantPassword;
  }

  public ZAPIResp execGetAPI(String uri, HashMap queryString) {
    String url;

   // For a nextPage call the uri is the URL
    if (uri.toLowerCase().startsWith("http")) {
      url = uri;
    } else {
      // turn uri to URL
      url = ZConfig.getInstance().getVal("rest.api.endpoint") +
        "/" + ZConfig.getInstance().getVal("rest.api.version") + uri;
    }

    // Get a httpget request ready
    HttpGet httpGet = new HttpGet(url);

    // indicate accept response body in JSON
    httpGet.setHeader("Accept", "application/json");

    // for a GET call, chase redirects
    httpGet.setHeader("follow_redirect", "true");

    // build query string into url
    URIBuilder uriBuilder = new URIBuilder(httpGet.getURI());
    Iterator it = queryString.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      uriBuilder.addParameter((String)pairs.getKey(), (String)pairs.getValue());
    }

    // perform pre API arguments tracing if required
    if (Boolean.valueOf(((String)ZConfig.getInstance().getVal("api.trace")).toLowerCase())) {
      ZLogger.getInstance().log("***** PRE-API TRACE *****", ZConstants.LOG_API);
      ZLogger.getInstance().log("HTTP method = GET", ZConstants.LOG_API);
      ZLogger.getInstance().log("URL = " + url, ZConstants.LOG_API);
      ZLogger.getInstance().log("Query String = " + queryString.toString(), ZConstants.LOG_API);
      Header headers[] = httpGet.getAllHeaders();
      for(Header h:headers){
        ZLogger.getInstance().log("Header = " + h.getName() + ": " + h.getValue(), ZConstants.LOG_API);
      }
    }

    // get a ssl pipe (httpclient), execute, trace response
    try {
      httpGet.setURI(uriBuilder.build());
      ZAPIResp resp = tracePostAPIResponse(httpGet, sslPipe().execute(httpGet));
      httpGet.releaseConnection();
      return resp;
    } catch (Exception e) {
      ZLogger.getInstance().log(e.getMessage(), ZConstants.LOG_BOTH);
      ZLogger.getInstance().log(ZUtils.stackTraceToString(e), ZConstants.LOG_BOTH);
      httpGet.abort();
      throw new RuntimeException("Fatal Error in executing HTTP GET " + url);
    }
  }

  public ZAPIResp execPutAPI(String uri, String reqBody) {
    String url;

    // turn uri to URL
    url = ZConfig.getInstance().getVal("rest.api.endpoint") +
      "/" + ZConfig.getInstance().getVal("rest.api.version") + uri;

    // Get a httpput request ready
    HttpPut httpPut = new HttpPut(url);

    // indicate accept response body in JSON
    httpPut.setHeader("Accept", "application/json");

    // For a PUT call, request body content is in JSON
    httpPut.setHeader("Content-Type", "application/json");

    if (Boolean.valueOf(((String)ZConfig.getInstance().getVal("api.trace")).toLowerCase())) {
      // perform pre API tracing
      ZLogger.getInstance().log("***** PRE-API TRACE *****", ZConstants.LOG_API);
      ZLogger.getInstance().log("HTTP method = PUT", ZConstants.LOG_API);
      ZLogger.getInstance().log("URL = " + url, ZConstants.LOG_API);
      ZLogger.getInstance().log("Request Body = " + reqBody.toString(), ZConstants.LOG_API);
      Header headers[] = httpPut.getAllHeaders();
      for(Header h:headers){
        ZLogger.getInstance().log("Header = " + h.getName() + ": " + h.getValue(), ZConstants.LOG_API);
      }
    }

    // get a ssl pipe (httpclient), execute, trace response
    try {
      StringEntity entity = new StringEntity(reqBody);
      httpPut.setEntity(entity);
      ZAPIResp resp = tracePostAPIResponse(httpPut, sslPipe().execute(httpPut));
      httpPut.releaseConnection();
      return resp;
    } catch (Exception e) {
      ZLogger.getInstance().log(e.getMessage(), ZConstants.LOG_BOTH);
      ZLogger.getInstance().log(ZUtils.stackTraceToString(e), ZConstants.LOG_BOTH);
      httpPut.abort();
      throw new RuntimeException("Fatal Error in executing HTTP PUT " + url);
    }
  }
  // Do POST
  public ZAPIResp execPostAPI(String uri, String reqBody) {
     return execPostAPI(uri, reqBody, null);
  }
  // Do POST
  public ZAPIResp execPostAPI(String uri, String reqBody, String reqParams) {
    String url;
    // For POST CONNECT call the version number is not in the url
    if (uri.toLowerCase().contains(ZConstants.CONNECTION_URI)) {
      url = ZConfig.getInstance().getVal("rest.api.endpoint") + uri;
    } else {
      // turn the resource uri to a full URL
      url = ZConfig.getInstance().getVal("rest.api.endpoint") +
        "/" + ZConfig.getInstance().getVal("rest.api.version") + uri;
    }

    // Get a httpput request ready
    HttpPost httpPost = new HttpPost(url);

    // indicate accept response body in JSON
    httpPost.setHeader("Accept", "application/json");

    // For file upload dont need to set content type
    if (!(uri.toLowerCase().contains(ZConstants.UPLOAD_USAGE_URL) || uri.toLowerCase().contains(ZConstants.MASS_UPDATER_URL))) {
      // For non-POST USAGE call, request body content is in JSON
      httpPost.setHeader("Content-Type", "application/json");
    }

    // put tenant's credentials in request header for a POST CONNECTION
    if (uri.toLowerCase().contains(ZConstants.CONNECTION_URI)) {
      // put tenant's credentials in request header
      httpPost.setHeader("apiAccessKeyId", tenantUserIdToUse());
      httpPost.setHeader("apiSecretAccessKey", tenantPasswordToUse());
    }

    if ((Boolean)Boolean.valueOf(((String)ZConfig.getInstance().getVal("api.trace")).toLowerCase())) {
      // perform pre API tracing
      ZLogger.getInstance().log("***** PRE-API TRACE *****",ZConstants.LOG_API);
      ZLogger.getInstance().log("HTTP method = POST",ZConstants.LOG_API);
      ZLogger.getInstance().log("URL = " + url, ZConstants.LOG_API);
      ZLogger.getInstance().log("Request Body = " + reqBody.toString(), ZConstants.LOG_API);
      Header headers[] = httpPost.getAllHeaders();
      for(Header h:headers){
        ZLogger.getInstance().log("Header = " + h.getName() + ": " + h.getValue(), ZConstants.LOG_API);
      }
    }
    // get a ssl pipe (httpclient), execute, trace response
    try {
      if (uri.contains(ZConstants.UPLOAD_USAGE_URL) || uri.contains(ZConstants.MASS_UPDATER_URL)) {
        MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
        // For File parameters
        entity.addPart( "file", new FileBody(new File(reqBody)));
        if(uri.contains(ZConstants.MASS_UPDATER_URL)) 
           entity.addPart("params", new StringBody(reqParams));
        httpPost.setEntity( entity );
      } else {
        StringEntity entity = new StringEntity(reqBody);
        httpPost.setEntity(entity);
      }
      ZAPIResp resp =tracePostAPIResponse(httpPost, sslPipe().execute(httpPost));
      httpPost.releaseConnection();
      return resp;
    } catch (Exception e) {
      ZLogger.getInstance().log(e.getMessage(), ZConstants.LOG_BOTH);
      ZLogger.getInstance().log(ZUtils.stackTraceToString(e), ZConstants.LOG_BOTH);
      httpPost.abort();
      throw new RuntimeException("Fatal Error in executing HTTP POST " + url);
    }
  }

  // Do DELETE
  public ZAPIResp execDeleteAPI(String uri, HashMap queryString) {
    String url;

    // turn uri to URL
    url = ZConfig.getInstance().getVal("rest.api.endpoint") +
        "/" + ZConfig.getInstance().getVal("rest.api.version") + uri;

    // Get a httpdelete request ready
    HttpDelete httpDelete = new HttpDelete(url);

    // indicate accept response body in JSON
    httpDelete.setHeader("Accept", "application/json");

    // build query string into url
    URIBuilder uriBuilder = new URIBuilder(httpDelete.getURI());
    Iterator it = queryString.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      uriBuilder.addParameter((String)pairs.getKey(), (String)pairs.getValue());
    }

    // perform pre API arguments tracing if required
    if (Boolean.valueOf(((String)ZConfig.getInstance().getVal("api.trace")).toLowerCase())) {
      ZLogger.getInstance().log("***** PRE-API TRACE *****", ZConstants.LOG_API);
      ZLogger.getInstance().log("HTTP method = DELETE", ZConstants.LOG_API);
      ZLogger.getInstance().log("URL = " + url, ZConstants.LOG_API);
      ZLogger.getInstance().log("Query String = " + queryString.toString(), ZConstants.LOG_API);
      Header headers[] = httpDelete.getAllHeaders();
      for(Header h:headers){
        ZLogger.getInstance().log("Header = " + h.getName() + ": " + h.getValue(), ZConstants.LOG_API);
      }
    }

    // get a ssl pipe (httpclient), execute, trace response
    try {
      httpDelete.setURI(uriBuilder.build());
      ZAPIResp resp = tracePostAPIResponse(httpDelete, sslPipe().execute(httpDelete));
      httpDelete.releaseConnection();
      return resp;
    } catch (Exception e) {
      ZLogger.getInstance().log(e.getMessage(), ZConstants.LOG_BOTH);
      ZLogger.getInstance().log(ZUtils.stackTraceToString(e), ZConstants.LOG_BOTH);
      httpDelete.abort();
      throw new RuntimeException("Fatal Error in executing HTTP DELETE " + url);
    }
  }

  // Get a SSL pipe for all http traffic
  private DefaultHttpClient sslPipe() {
    if (zHttpClient == null) {
      zHttpClient = ZHttpClient.getInstance();
    }
    return zHttpClient;
  }

  // resolve final tenant user Id to use
  private String tenantUserIdToUse() {
    if (connectTenantUserId == null) {
      return defaultTenantUserId;
    } else {
      return connectTenantUserId;
    }
  }

  // resolve final tenant password to use
  private String tenantPasswordToUse() {
    if (connectTenantPassword == null) {
      return defaultTenantPassword;
    } else {
      return connectTenantPassword;
    }
  }


  // Print some HTTP artifacts and response
  private ZAPIResp tracePostAPIResponse(HttpUriRequest httpRequest, HttpResponse httpResp) throws JSONException {
    JSONObject jsonObjResp = null;
    String jsonResp = null;

    try {
      jsonObjResp = new JSONObject(EntityUtils.toString(httpResp.getEntity()));
    // If there is no JSON response create an empty JSON object
    } catch (Exception e) {
      jsonObjResp = new JSONObject();
    }

    // then add HTTP status and reason inside
    jsonObjResp.put("httpStatusCode", httpResp.getStatusLine().getStatusCode());
    jsonObjResp.put("httpReasonbPhrase", httpResp.getStatusLine().getReasonPhrase());
    jsonResp = jsonObjResp.toString(2);

    if (Boolean.valueOf(((String)ZConfig.getInstance().getVal("api.trace")).toString())) {
      ZLogger.getInstance().log("***** POST-API RESPONSE TRACE *****", ZConstants.LOG_API);
      ZLogger.getInstance().log("HTTP method = " + httpRequest.getMethod(), ZConstants.LOG_API);
      ZLogger.getInstance().log("Proxy = " + sslPipe().getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY), ZConstants.LOG_API);
      ZLogger.getInstance().log("URL = " + httpRequest.getURI().toString(), ZConstants.LOG_API);
      Header headers[] = httpResp.getAllHeaders();
      for(Header h:headers){
        ZLogger.getInstance().log("Header = " + h.getName() + ": " + h.getValue(), ZConstants.LOG_API);
      }
      ZLogger.getInstance().log("HTTP status = " + httpResp.getStatusLine().getStatusCode(), ZConstants.LOG_API);
      ZLogger.getInstance().log("HTTP reason = " + httpResp.getStatusLine().getReasonPhrase(), ZConstants.LOG_API);
      ZLogger.getInstance().log("HTTP version = " + httpResp.getProtocolVersion(), ZConstants.LOG_API);
      ZLogger.getInstance().log("API Response = " + jsonResp, ZConstants.LOG_API);
    }
    // convert json response string to ZAPIResp and return result
    return new ZAPIResp(jsonResp);
  }

}