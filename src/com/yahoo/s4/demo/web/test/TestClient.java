package com.yahoo.s4.demo.web.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.yahoo.s4.demo.web.util.QueryCount;

public class TestClient {
	
	private static final Logger logger = Logger.getLogger(TestClient.class);

	private static final String[] queryKeys = new String[] {
		"Yahoo", "Microsoft", "Facebook", "Lady Gaga", "Google", "Walmart", "Dell", "Jobs", "IPhone", "Obama"
	};	
	private static final int[] queryCounts = new int[] {
		220000, 340000, 180000, 420000, 390000, 160000, 120000, 480000, 120000, 80000
	};
	
	private static final int	BASE_COUNT = 100;
	private static final int	SLOT_COUNT = 300;
	
	//private static final String	TOP_QUERIES_URL = "http://localhost:8080/s4_demo/query/list.do?";
	//private static final String	QUERY_TREND_URL = "http://localhost:8080/s4_demo/query/trends.do?";
	
	private static final String	TOP_QUERIES_URL = "http://fed2008.yss.ne1.yahoo.com:8080/s4_demo/query/list.do?";
	private static final String	QUERY_TREND_URL = "http://fed2008.yss.ne1.yahoo.com:8080/s4_demo/query/trends.do?";
	
	//private static final String	TOP_QUERIES_URL = "http://10.82.133.173:8080/s4_demo/query/list.do?";
	
	private static final Random random 	= new Random();	
	private static final Gson 	gson 	= new Gson();
	
	private HttpClient	httpClient = new DefaultHttpClient();
	private HttpPost	topQueriesPost = new HttpPost(TOP_QUERIES_URL);
	private HttpPost	queryTrendPost = new HttpPost(QUERY_TREND_URL);
	
	
	public List<QueryCount> generateTopQueries() {
		List<QueryCount> topQueries = new ArrayList<QueryCount>();
		
		//Collections.shuffle(Arrays.asList(queryKeys));
		for (int i = 0; i < queryKeys.length; i++) {
			topQueries.add(new QueryCount(queryKeys[i], queryCounts[i] + random.nextInt(100000)));
		}
		//Collections.sort(topQueries);
		
		return topQueries;
	}

		
	public Integer[][] generateHistoryTrend(int slotCount) {
		Integer[][] trends = new Integer[10][slotCount];
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < slotCount; j++) {
				trends[i][j] = BASE_COUNT + random.nextInt(10);
			}
		}
		
		return trends; 
	}
	
	
	public void sendTopQueries(String[] queries, Integer[] counts) {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		String jsonQueries = gson.toJson(queries);
		String jsonCounts = gson.toJson(counts);
		
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("queries", jsonQueries));
		postParams.add(new BasicNameValuePair("counts", jsonCounts));
		
		System.out.println(jsonQueries);
		
		try {
			long startTime = System.currentTimeMillis();
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);
			//StringEntity entity = new StringEntity("queries=" + "xxx");
			//BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			//System.out.println(reader.readLine());
			
			topQueriesPost.setEntity(entity);
			//topQueriesPost.setHeader("Content-Type", "text/json");
			//topQueriesPost.getParams().setParameter("queries", jsonQueries);
			
			String response = httpClient.execute(topQueriesPost, responseHandler);
			long timeElapsed = System.currentTimeMillis() - startTime;
			logger.info("Response: " + response + " with latency: " + timeElapsed + "ms");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendTrends(String[] queries, int slotCount) {
		slotCount = (slotCount > 300) ? 300 : slotCount;
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		Integer[][] detailCounts = generateHistoryTrend(slotCount);
		
		String jsonQueries = gson.toJson(queries);
		String jsonDetailCounts = gson.toJson(detailCounts);
		

		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("queries", jsonQueries));		
		postParams.add(new BasicNameValuePair("detailCounts", jsonDetailCounts));
		
		
		try {
			long startTime = System.currentTimeMillis();
			queryTrendPost.setEntity(new UrlEncodedFormEntity(postParams));			
			String response = httpClient.execute(queryTrendPost, responseHandler);
			long timeElapsed = System.currentTimeMillis() - startTime;
			logger.info("Response: " + response + " with latency: " + timeElapsed + "ms");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runTest(int slotCount) {
		List<QueryCount> topQueries = generateTopQueries();
		String[] queries = new String[topQueries.size()];
		Integer[] counts = new Integer[topQueries.size()];
		
		for (int i = 0; i < topQueries.size(); i++) {
			queries[i] = topQueries.get(i).query;
			counts[i] = topQueries.get(i).count;
		}
		
		sendTopQueries(queries, counts);
		//sendTrends(queries, slotCount);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestClient tester = new TestClient();
		
		
		System.setProperty("socksProxySet", "true");
		System.setProperty("socksProxyHost", "socks.yahoo.com");
		System.setProperty("socksProxyPort", "1080");
		tester.runTest(300);
		/*
		for (int i = 280; i < 320; i++) {
			try {
				tester.runTest(i);
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/

	}

}
