package test.java.com.zuora.sdk.samples;

import java.text.SimpleDateFormat;

import org.junit.Test;

import main.java.com.zuora.sdk.lib.ConnectionManager;
import main.java.com.zuora.sdk.lib.ZClient;
import main.java.com.zuora.sdk.samples.SubscriptionManager;

public class SubscriptionManagerTest {
   
   @Test
   public void test_subscription(){
   // create a Z_Client
      ZClient zClient = new ZClient();

      // Create a z_client object and pass it to APIRepo
      SubscriptionManager subscriptionManager = new SubscriptionManager(zClient);

      // Connect to the endPoint using default tenant's credentials
      // and practice APIs


      if (new ConnectionManager().isConnected(zClient)) {
    	  subscriptionManager.cancel("A-S00009896");
      }
   }
}
