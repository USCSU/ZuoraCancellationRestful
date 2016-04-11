/**
 * Copyright (c) 2013 Zuora Inc.
 */
package main.java.com.zuora.sdk.lib;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;


public class ZHttpClient {

  private static DefaultHttpClient instance_;
  private static String TLS_VERSION = "TLSv1.2";
  
  // Each thread has its own httpclient
  ZHttpClient() {
    SchemeRegistry registry = new SchemeRegistry();
    SSLSocketFactory socketFactory;
    try {
        if (Boolean.valueOf(((String) ZConfig.getInstance().getVal("ssl.verify.peer")).toLowerCase())) {
            socketFactory = new SSLSocketFactory(TLS_VERSION, null, null, null, null, null,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } else {
            socketFactory = new SSLSocketFactory(TLS_VERSION, null, null, null, null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
        // need both http and https
        registry.register(new Scheme("https", 443, socketFactory));
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        PoolingClientConnectionManager pccm = new PoolingClientConnectionManager(registry);

        // set max connections
        pccm.setMaxTotal(Integer.parseInt((String) ZConfig.getInstance().getVal("http.max.connection.pool.size")));
        pccm.setDefaultMaxPerRoute(
                Integer.parseInt((String) ZConfig.getInstance().getVal("http.max.connection.pool.size")));
        pccm.closeIdleConnections(15, TimeUnit.SECONDS);
        instance_ = new DefaultHttpClient(pccm);
    } catch (GeneralSecurityException e) {
        ZLogger.getInstance().log(e.getMessage(), ZConstants.LOG_BOTH);
        ZLogger.getInstance().log(ZUtils.stackTraceToString(e), ZConstants.LOG_BOTH);
        String errorMessage = "Fatal Error in creating HTTPClient";
        ZLogger.getInstance().log(errorMessage, ZConstants.LOG_BOTH);
        throw new RuntimeException(errorMessage);
    }
    configHttpClient();
  }

  // ZHttpClient is a singleton shared by all ZClients
  public synchronized static DefaultHttpClient getInstance() {
    if (instance_ == null) {
      new ZHttpClient();
    }
    return instance_;
  }

  // set all the nuts and bolts for httpclient
  private void configHttpClient() {
    instance_.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

    // set timeout parameters
    instance_.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Integer.parseInt((String)ZConfig.getInstance().getVal("http.connect.timeout")));
    instance_.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Integer.parseInt((String)ZConfig.getInstance().getVal("http.receive.timeout")));

    // get settings for proxy
    boolean proxyUsed = Boolean.valueOf(((String)ZConfig.getInstance().getVal("proxy.used")).toLowerCase());
    String urlString = (String)ZConfig.getInstance().getVal("proxy.url");

    // if proxy is used and proxy url is specified ...
    if (proxyUsed && urlString != null && !urlString.equals("")) {
      // decode URL
      try {
        URL url = new URL(urlString);
        String proxyProtocol = url.getProtocol();
        String proxyHost = url.getHost();
        int proxyPort = url.getPort();

        // add authenticating proxy support if in use
        if (Boolean.valueOf(((String)ZConfig.getInstance().getVal("proxy.auth")).toLowerCase())) {
          instance_.getCredentialsProvider().setCredentials(
            new AuthScope(proxyHost, proxyPort),
            new UsernamePasswordCredentials((String)ZConfig.getInstance().getVal("proxy.user"),
            (String)ZConfig.getInstance().getVal("proxy.password")));
        }

        // set proxy
        HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxyProtocol);
        instance_.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

      // proxy url is malformed ... giving up
      } catch (MalformedURLException e) {
        ZLogger.getInstance().log("Proxy URL string " + urlString + " is malformed.", ZConstants.LOG_BOTH);
        ZLogger.getInstance().log("Unable to use Proxy. Proxy config is not used.", ZConstants.LOG_BOTH);
      }
    }
    // add Useragent
    instance_.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
      (String)ZConfig.getInstance().getVal("http.user.agent"));
  }

  // Clean up all TCP connections before being collected
  public void finalize() {
    try {
      super.finalize();
    } catch (Throwable ex) {
    }
    instance_.getConnectionManager().shutdown();
  }
}
