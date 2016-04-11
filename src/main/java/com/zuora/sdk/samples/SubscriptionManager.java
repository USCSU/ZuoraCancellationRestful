/**
 * Copyright (c) 2013 Zuora Inc.
 *
 * Sample code to demonstrate how to use the Subscriptions resources
 */

package main.java.com.zuora.sdk.samples;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import main.java.com.zuora.sdk.lib.ResourceEndpoints;
import main.java.com.zuora.sdk.lib.ZAPIArgs;
import main.java.com.zuora.sdk.lib.ZAPIResp;
import main.java.com.zuora.sdk.lib.ZClient;

public class SubscriptionManager {
  static final String SAMPLE_ACCOUNT_KEY = "A00000222";
  private ZClient zClient;
  private String accountKey;
  private String subscriptionKey;

  public SubscriptionManager(ZClient zClient) {
    this.zClient = zClient;
  }

  // Get subscriptions by account
  public ZAPIResp getAll(String accountKey, String nextPage) {
  	try {
	  this.accountKey = URLEncoder.encode(accountKey, "UTF-8");
	} catch (Exception e) {
	  System.out.println(e.getMessage());
	  e.printStackTrace();
	  return null;
	}
  	
    ZAPIArgs args = new ZAPIArgs();

    if (nextPage != null) {
      args.set("uri", nextPage);
    } else {
      args.set("uri", ResourceEndpoints.GET_SUBSCRIPTIONS.replace("{account-key}", this.accountKey));
      args.set("queryString", new ZAPIArgs());
      args.getArg("queryString").set("pageSize", 10);
    }

    System.out.println("========== GET SUBSCRIPTIONS ============");

    ZAPIResp response = null;
    try {
      ZAPIResp resp = response = zClient.get(args);
      System.out.println(response.toJSONString());
      if ((Integer)resp.get("httpStatusCode") == 200 && (Boolean)resp.get("success")) {
        response = resp;
      }
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }

    return response;
  }
  
  public ZAPIResp getAll(String accountKey) {
    return getAll(accountKey, null);
  }

  // Create a subscription without an account and for preview only
  public void createPreview() {
    ZAPIArgs args = new ZAPIArgs();
    args.set("uri", ResourceEndpoints.POST_SUBSCRIPTION_PREVIEW);

    args.set("reqBody", new ZAPIArgs());

    args.getArg("reqBody").set("termType", "TERMED");
    args.getArg("reqBody").set("initialTerm", 12);
    args.getArg("reqBody").set("contractEffectiveDate", "2013-1-15");
    args.getArg("reqBody").set("invoiceTargetDate", "2013-12-31");

    args.getArg("reqBody").set("previewAccountInfo", new ZAPIArgs());
    args.getArg("reqBody").getArg("previewAccountInfo").set("currency", "USD");
    args.getArg("reqBody").getArg("previewAccountInfo").set("billCycleDay", 31);
    args.getArg("reqBody").getArg("previewAccountInfo").set("billToContact", new ZAPIArgs());
    args.getArg("reqBody").getArg("previewAccountInfo").getArg("billToContact").set("city", "Walnut Creek");
    args.getArg("reqBody").getArg("previewAccountInfo").getArg("billToContact").set("county", "Contra Consta");
    args.getArg("reqBody").getArg("previewAccountInfo").getArg("billToContact").set("state", "California");
    args.getArg("reqBody").getArg("previewAccountInfo").getArg("billToContact").set("zipCode", "94549");
    args.getArg("reqBody").getArg("previewAccountInfo").getArg("billToContact").set("country", "United States");

    args.getArg("reqBody").setArray("subscribeToRatePlans");
    args.getArg("reqBody").set("subscribeToRatePlans[0]", new ZAPIArgs());
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").set("productRatePlanId", "8a8582933b24b3cd013b250572f50faf");
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").setArray("chargeOverrides");
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").set("chargeOverrides[0]", new ZAPIArgs());
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").getArg("chargeOverrides[0]").set("productRatePlanChargeId", "8a8582933b24b3cd013b25067e660fb1");
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").getArg("chargeOverrides[0]").set("quantity", 100);

    System.out.println("========== CREATE A SUBSCRIPTION FOR PREVIEW ============");

    try {
      ZAPIResp response = zClient.post(args);
      System.out.println(response.toJSONString());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  // create a subscription with an existing account
  public String create() {
    ZAPIArgs args = new ZAPIArgs();
    args.set("uri", ResourceEndpoints.POST_SUBSCRIPTION);

    args.set("reqBody", new ZAPIArgs());
    args.getArg("reqBody").set("accountKey", SAMPLE_ACCOUNT_KEY);
    args.getArg("reqBody").set("contractEffectiveDate", "2016-04-11");
    args.getArg("reqBody").set("termType", "TERMED");
    args.getArg("reqBody").set("initialTerm", "12");
    args.getArg("reqBody").set("autoRenew", true);
    args.getArg("reqBody").set("renewalTerm", "3");
    args.getArg("reqBody").set("notes", "Test POST subscription from z-ruby-sdk");
    args.getArg("reqBody").setArray("subscribeToRatePlans");
    args.getArg("reqBody").set("subscribeToRatePlans[0]", new ZAPIArgs());
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").set("productRatePlanId", "8a8582933b24b3cd013b250572f50faf");
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").setArray("chargeOverrides");
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").set("chargeOverrides[0]", new ZAPIArgs());
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").getArg("chargeOverrides[0]").set("productRatePlanChargeId", "8a8582933b24b3cd013b25067e660fb1");
    args.getArg("reqBody").getArg("subscribeToRatePlans[0]").getArg("chargeOverrides[0]").set("quantity", 10);
    args.getArg("reqBody").set("invoiceTargetDate", "2016-04-11");
    args.getArg("reqBody").set("invoiceCollect", false);

    System.out.println("========== CREATE A SUBSCRIPTION ============");

    try {
      ZAPIResp response = zClient.post(args);
      System.out.println(response.toJSONString());
      if ((Integer)response.get("httpStatusCode") == 200 && (Boolean)response.get("success")) {
        return (String)response.get("subscriptionNumber");
      }
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }

    return null;
  }

  // Get subscription by Id
  public ZAPIResp get(String subscriptionKey) {
	try {
	  this.subscriptionKey = URLEncoder.encode(subscriptionKey, "UTF-8");
	} catch (Exception e) {
	  System.out.println(e.getMessage());
	  e.printStackTrace();
	  return null;
	}
	
    ZAPIArgs args = new ZAPIArgs();
    args.set("uri", ResourceEndpoints.GET_SUBSCRIPTION.replace("{subscription-key}", this.subscriptionKey));

    System.out.println("========== GET A SUBSCRIPTION ============");

    try {
      ZAPIResp response = zClient.get(args);
      System.out.println(response.toJSONString());
      if ((Integer)response.get("httpStatusCode") == 200 && (Boolean)response.get("success")) {
        return response;
      }
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  // Update a subscription
  public void update(String subscriptionKey, String ratePlanId, String ratePlanChargeId) {
	try {
	  this.subscriptionKey = URLEncoder.encode(subscriptionKey, "UTF-8");
	} catch (Exception e) {
	  System.out.println(e.getMessage());
	  e.printStackTrace();
	  return;
	}
		
    ZAPIArgs args = new ZAPIArgs();
    args.set("uri", ResourceEndpoints.PUT_SUBSCRIPTION.replace("{subscription-key}", this.subscriptionKey));

    args.set("reqBody", new ZAPIArgs());
    args.getArg("reqBody").set("termType", "TERMED");
    args.getArg("reqBody").set("currentTerm", "10");
    args.getArg("reqBody").set("autoRenew", false);
    args.getArg("reqBody").set("renewalTerm", "4");
    args.getArg("reqBody").set("notes", "Test UPDATE subscription from z-ruby-sdk");
    args.getArg("reqBody").setArray("update");
    args.getArg("reqBody").set("update[0]", new ZAPIArgs());
    args.getArg("reqBody").getArg("update[0]").set("ratePlanId", ratePlanId);
    args.getArg("reqBody").getArg("update[0]").setArray("chargeUpdateDetails");
    args.getArg("reqBody").getArg("update[0]").set("chargeUpdateDetails[0]", new ZAPIArgs());
    args.getArg("reqBody").getArg("update[0]").getArg("chargeUpdateDetails[0]").set("ratePlanChargeId", ratePlanChargeId);
    args.getArg("reqBody").getArg("update[0]").getArg("chargeUpdateDetails[0]").set("quantity", 12);
    args.getArg("reqBody").getArg("update[0]").set("contractEffectiveDate", "2013-04-28");

    System.out.println("========== UPDATE A SUBSCRIPTION ============");

    try {
      ZAPIResp response = zClient.put(args);
      System.out.println(response.toJSONString());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  // Renew a subscription
  public void renew(String subscriptionKey) {
	try {
	  this.subscriptionKey = URLEncoder.encode(subscriptionKey, "UTF-8");
	} catch (Exception e) {
	  System.out.println(e.getMessage());
	  e.printStackTrace();
	  return;
	}
	
    ZAPIArgs args = new ZAPIArgs();
    args.set("uri", ResourceEndpoints.PUT_SUBSCRIPTION_RENEW.replace("{subscription-key}", this.subscriptionKey));

    args.set("reqBody", new ZAPIArgs());
    args.getArg("reqBody").set("invoiceCollect", false);

    System.out.println("========== RENEW A SUBSCRIPTION ============");

    try {
      ZAPIResp response = zClient.put(args);
      System.out.println(response.toJSONString());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  // Cancel a subscription
  public void cancel(String subscriptionKey) {
	try {
	  this.subscriptionKey = URLEncoder.encode(subscriptionKey, "UTF-8");
	} catch (Exception e) {
	  System.out.println(e.getMessage());
	  e.printStackTrace();
	  return;
	}
	
    ZAPIArgs args = new ZAPIArgs();
    args.set("uri", ResourceEndpoints.PUT_SUBSCRIPTION_CANCEL.replace("{subscription-key}", this.subscriptionKey));

    args.set("reqBody", new ZAPIArgs());
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    args.getArg("reqBody").set("cancellationPolicy", "SpecificDate");
    args.getArg("reqBody").set("cancellationEffectiveDate", format.format(new Date()));
    args.getArg("reqBody").set("invoiceCollect", false);

    System.out.println("========== CANCEL A SUBSCRIPTION ============");

    try {
      ZAPIResp response = zClient.put(args);
      System.out.println(response.toJSONString());
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }
}
