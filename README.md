# Zuora RESTful API Sample Code
## Pre-requisites

The following libraries are required to be downloaded:

* java 6
* apache httpclient 4.2.3
* apache httpcore 4.2.2
* apache httpmime 4.2.3
* java-json.jar

You also need a trial account in any Zuora application, go get a free trial at http://info.zuora.com/zuora-free-trial.html and then update `src/main/resources/com/zuora/sdk/resource/ZConfig.properties` with your credential.

## Version

1.0

## Description

`zuora-rest-java` facilitates developers to build Java based Zuora applications without being burdened by the underlying HTTP mechanics, multi-threading, proxy, SSL security, error handling, and logging support required for debugging and troubleshooting. Complex JSON responses and call arguments can also be manipulated using simple Java setter and getter methods.

The sample code demonstrates how all Zuora REST resources can be modeled as "resource manager". There are 8 resource manager classes: Connection, Catalog, Payment_Method, Account, Subscription, Transaction, Operation and Usage. Three sample usage files are also provided.

The sample code including any code libraries are provided to you "AS IS" for demonstration purposes only. Zuora does not guarantee or make any representations regarding the use, results of use, accuracy, security, timeliness, or completeness of any data or information relating to the sample code. 

You, the customer, are responsible for making sure that your implementation is secure and functional.

## Features

* Environment neutral. Support JSE or JEE
* Support configuration for local installation needs
* Provide full API pre-call and post-call trace for easy debugging
* Provide log files as support documentation
* Support multi-threading environment with http connection pooling
* Navigate call arguments and JSON response string using setter and getter
* Support VERIFY NONE and VERIFY PEER for SSL invocations
* Support proxy, including basic authentication

## Configuration

Support global configuration via a configuration properties file in the "resource" subdirectory. This can be customized and built into the target jar file and used by the developers as the default configuration and further simply code.

Different applications can also use different configuration. This is achieved by including a local copy of a ZConfig.properties in the working directory (i.e. pwd) where the application runs.

Change Installation specific properties as needed. Most default settings work in most installations, but some are installation specific properties:

* Setting default tenant's credentials enables a connect call to be made without arguments - remove security ensitive information from developers.
* if using forwarding proxy server set proxy.used to true
* If the proxy uses basic authentication set proxy.auth to true, and its credentials in proxy.user and proxy.password.
* When developing switch on api trace for debugging. Set api.trace to true.
* If you are worried about the authenticity of the api end point set ssl.verify to true. This may slightly degrade performance on CONNECT calls.

## Logging

SDK puts all log files in the subdirectory "sdk_logs" under the current working directory where the application runs.

`sdk.log` is for the sdk itself.

`api_trace.log` is for API debugging.

When reporting an incident report to Zuora, both logs should be sent as documentation.

Each log files has a 4M size limit and up to 3 generations before it is wrapped around.

## Examples

Refer to the samples for the following discussion. You can write your own resource managers or any other abstraction; they are only there to illustrate one way of designing application objects around the APIs.

**1. import the necessary jars**
**2. All calls must be routed through ZClient. To create a ZClient:**
```
ZClient zClient = new ZClient();
```

Use the get, put, post, and delete methods to send a REST API.

**3. Pass the zClient object to the resource manager object.**

**4. Prepare arguments**

All arguments are ultimately put inside a JSON structure. Some arguments, mandatory or optional, are required to be created in some nested structure.

Use the ZAPIArgs class which allows argument object to be manipulated via its setter and getter methods. Whenever there is a need for a nested argument structure, use new ZAPIArgs. Refer to sample code and the corresponding published call arguments in the API documentation for more sample usage.

There are 4 key attributes in ZAPIArgs that are needed to make a REST API call:

* uri
Refers to the resource uri (excluding the API endpoint). For instance
```java
ZAPIArgs args =  new ZAPIArgs();
args.set("uri", "/catalog/products");
```
Predefined resource endpoints are provided as samples and can be found in ResourceEndpoints.java

* headers
Used as fields in request header. Only relevant to the CONNECT API
```java
ZAPIArgs args =  new ZAPIArgs();
args.set("headers", new ZAPIArgs());
args.getArg("headers").set("apiAccessKeyId", "your_username");
args.getArg("headers").set("apiSecretAccessKey", "password");
```
If indded th ZConfig.properties file has the default tenant's credentials configured, these headers will not be needed when connecting to the default tenant.

* queryString
Also known as HTTP parameters. For instance:
```java
ZAPIArgs args =  new ZAPIArgs();
args.set("query_string", new ZAPIArgs());
args.getArg("query_string").set("pageSize", 20);
```
* reqBody
Most POST and PUT APIs require substantial amount of arguments to be set up in the request body of the HTTP request. This needs to be set up before the request can be sent to the service provider.
```java
ZAPIArgs args =  new ZAPIArgs();
args.set("req_body", new ZAPIArgs());
args.getArg("req_body").set("name", "api_tester");
args.getArg("req_body").set("billToContact", new ZAPIArgs());
args.getArg("req_body").getArg("billToContact").set("firstName", "api");
args.getArg("req_body").getArg("billToContact").set("lastName", "Tester");
```
Consult Zuora REST API documentation for details of the call arguments.

**5. Authenticate to the endpoint using tenant's user id and password:**
```java
connect();           // connect with pre-configured tenant's credentials
```
To provide tenant's credentials on the fly:
```java
connect("box.net", "secretPassword");
```
**6. Check response**

Both the HTTP Status Code and Response Phrase are always included in the response object ZAPIResp. If the invocation had been successful, ZAPIResp also contains the body of the HTTP response.
The response attributes can be parsed using getter method. For instance:
```java
    response.get("nextPage");
```
To reach the response in a nested structure, use the getResp method.
Refer to Zuora REST API documentation for details of the call arguments and the sample resource managers for more examples.

**7. Catch exceptions**

If the syntax of the arguments are wrong, for example, missing body or presence of extraneous arguments, the SDK will raise an IllegalArgumentException with the corresponding diagnostic message.

If there is an exception while the call is in progress, a RuntimeException will be raised, and the corresponding stack trace will also be logged in both the sdk and api_trace log files.

**8. Follow the "nextPage"**

When issuing a GET call, sometimes there are multiple pages of response returned.

This is indicated by the presence of the "nextPage" key in the response body.

For example, in the get_all method of the PaymentMethod resource manager, if the nextPage key is present in the response, pass nextPage as the url and make a recursive call.

## How To 

**1. To run the samples:**
```bash
mvn test
```
**2. To import this maven project into Eclipse:**
```bash
mvn eclipse:eclipse
```
  
## Author

Zuora Inc.

## Copyright

Copyright (c) 2013 (Zuora Inc.)[http://www.zuora.com].

See License for details
