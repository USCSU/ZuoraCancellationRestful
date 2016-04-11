package main.java.com.zuora.sdk.apiMain;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import main.java.com.zuora.sdk.lib.ConnectionManager;
import main.java.com.zuora.sdk.lib.ZClient;
import main.java.com.zuora.sdk.samples.SubscriptionManager;

public class CancellationMain {
	final static String filename = "input/input.csv";
	public static void main(String[] args){
		 ZClient zClient = new ZClient();
	     SubscriptionManager subscriptionManager = new SubscriptionManager(zClient);
	     CancellationMain app = new CancellationMain();
	     app.cancelSub(subscriptionManager,app.readSubsFromFile(filename),zClient);
	     
	}
	public   void cancelSub(SubscriptionManager manager, String subNum, ZClient zclient){
		 if (new ConnectionManager().isConnected(zclient)) {
	    	  manager.cancel(subNum);
	      }
	}
	public void cancelSub(SubscriptionManager manager, Set<String> subs, ZClient zclient){
		for(String sub:subs)
			cancelSub(manager,sub,zclient);
	}
	public   Set<String> readSubsFromFile(String path){
		Set<String> listOfSubs = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			boolean firstRow = true;
			while((line = br.readLine())!=null){
				//format strings 
				String[] cols = line.split(",");
				listOfSubs.add(cols[0]);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		} 
		return listOfSubs;
	}
}
