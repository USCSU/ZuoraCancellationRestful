package main.java.com.zuora.sdk.lib;

public final class ResourceEndpoints {
	  // RESOURCE GROUP CONNECTION API
	  public static final String CONNECT = "/connections";

	  // RESOURCE CATALOG API
	  public static final String GET_PRODUCT_CATALOG = "/catalog/products";

	  // RESOURCE ACCOUNT API
	  public static final String GET_ACCOUNT_DETAIL = "/accounts/{account-key}";
	  public static final String GET_ACCOUNT_SUMMARY = "/accounts/{account-key}/summary";
	  public static final String POST_ACCOUNT = "/accounts";
	  public static final String PUT_ACCOUNT = "/accounts/{account-key}";

	  // RESOURCE PAYMENT METHOD API
	  public static final String GET_CREDIT_CARDS = "/payment-methods/credit-cards/accounts/{account-key}";
	  public static final String POST_CREDIT_CARD = "/payment-methods/credit-cards";
	  public static final String PUT_CREDIT_CARD = "/payment-methods/credit-cards/{payment-method-id}";
	  public static final String DELETE_PAYMENT_METHOD = "/payment-methods/{payment-method-id}";

	  // RESOURCE TRANSACTION API
	  public static final String GET_INVOICES = "/transactions/invoices/accounts/{account-key}";
	  public static final String GET_PAYMENTS = "/transactions/payments/accounts/{account-key}";
	  public static final String GET_USAGE = "/usage/accounts/{account-key}";

	  // RESOURCE SUBSCRIPTION API
	  public static final String GET_SUBSCRIPTION = "/subscriptions/{subscription-key}";
	  public static final String GET_SUBSCRIPTIONS = "/subscriptions/accounts/{account-key}";
	  public static final String POST_SUBSCRIPTION = "/subscriptions";
	  public static final String POST_SUBSCRIPTION_PREVIEW = "/subscriptions/preview";
	  public static final String PUT_SUBSCRIPTION = "/subscriptions/{subscription-key}";
	  public static final String PUT_SUBSCRIPTION_RENEW = "/subscriptions/{subscription-key}/renew";
	  public static final String PUT_SUBSCRIPTION_CANCEL = "/subscriptions/{subscription-key}/cancel";

	  // RESOURCE OPERATIONS
	  public static final String POST_INVOICE_COLLECT = "/operations/invoice-collect";

	  // RESOURCE USAGE API
	  public static final String POST_USAGE = "/usage";
	  
	  //RESOURCE TRANSACTION REVENUE SCHEDULE
	  public static final String GET_REVENUE_SCHEDULE_DETAIL= "/revenue-schedules/{rs-number}";
	  public static final String GET_REVENUE_SCHEDULE_DETAILS_BY_CHARGE= "/revenue-schedules/subscription-charges/{charge-key}";
	  public static final String GET_REVENUE_SCHEDULE_DETAIL_BY_INVOICE_ITEM= "/revenue-schedules/invoice-items/{invoice-item-key}";
	  public static final String GET_REVENUE_SCHEDULE_DETAIL_BY_INVOICE_ITEM_ADJUSTMENT= "/revenue-schedules/invoice-item-adjustments/{invoice-item-adj-key}";
	  
	  public static final String PUT_REVENUE_SCHEDULE_DISTRIBUTE_RATABLY= "/revenue-schedules/{rs-number}/distribute-revenue-with-date-range";
	  public static final String PUT_REVENUE_SCHEDULE_DISTRIBUTE_MANUALLY= "/revenue-schedules/{rs-number}/distribute-revenue-across-accounting-periods";
	  public static final String PUT_REVENUE_SCHEDULE_BASIC_INFORMATION= "/revenue-schedules/{rs-number}/basic-information";
	  public static final String PUT_REVENUE_SCHEDULE_CHANGE_RS_AMOUNT="/revenue-schedules/{rs-number}/override-revenue-schedule-amount";
	  
	  public static final String POST_REVENUE_SCHEDULE_BY_CHARGE_MANUALLY_DISTRIBUTION="/revenue-schedules/subscription-charges/{charge-key}";
	  
	  //RESOURCE REVENUE EVENT
	  public static final String GET_REVENUE_EVENTS_DETAIL= "/revenue-events/{event-number}";
	  public static final String GET_REVENUE_EVENTS_BY_REVENUE_SCHEDULE= "/revenue-events/revenue-schedules/{rs-number}";
	  
	  //RESOURCE REVENUE ITEM
	  public static final String GET_REVENUE_ITEMS_BY_CHARGE_REVENUE_SUMMARY= "/revenue-items/charge-revenue-summaries/{crs-number}";
	  public static final String GET_REVENUE_ITEMS_BY_REVENUE_SCHEDULE= "/revenue-items/revenue-schedules/{rs-number}";
	  public static final String GET_REVENUE_ITEMS_BY_REVENUE_EVENT= "/revenue-items/revenue-events/{event-number}";
	  
	  //RESOURCE REVENUE RECOGNITION RULE
	  public static final String GET_REVENUE_RECOGNITION_RULE_BY_SUBSCRIPTION_CHARGE= "/revenue-recognition-rules/subscription-charges/{charge-key}";
	  
	  //RESOURCE REVNEUE SETTING
	  public static final String GET_REVENUE_AUTOMATION_START_DATE= "/settings/finance/revenue-automation-start-date";
	  
	  //RESOURCE JOURNAL ENTRY
	  public static final String PUT_JOURNAL_ENTRY_BASIC_INFORMATION= "/journal-entries/{je-number}/basic-information";
	  public static final String POST_JOURNAL_ENTRY="/journal-entries";
	  public static final String GET_JOURNAL_ENTRIES_BY_JOURNAL_RUN_NUMBER="/journal-entries/journal-runs/{jr-number}";
	  public static final String GET_JOURNAL_ENTRY_BY_JOURNAL_ENTRY_NUMBER="/journal-entries/{je-number}";
	  
	  //RESOURCE JOURNAL RUN
	  public static final String POST_JOURNAL_RUN= "/journal-runs";
	  public static final String GET_JOURNAL_RUN= "/journal-runs/{jr-number}";
	  
	  //RESOURCE MASS UPDATE
	  public static final String PATH_MASS_UPDATE = "/bulk";
	  public static final String PATH_MASS_UPDATE_KEY = "/bulk/{bulk-key}";
	  public static final String PATH_MASS_UPDATE_STOP = "/bulk/{bulk-key}/stop";
	}
