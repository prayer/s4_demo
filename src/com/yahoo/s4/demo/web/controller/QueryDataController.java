package com.yahoo.s4.demo.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.yahoo.s4.demo.web.service.QueryDataService;
import com.yahoo.s4.demo.web.util.QueryCount;



@Controller
@RequestMapping("/query")
public class QueryDataController extends BaseController {
	
	private static final Logger logger = Logger.getLogger(QueryDataController.class);

	private final Gson gson = new Gson();
	
	private QueryDataService dataService;
	
	

	public QueryDataService getQueryDataService() {
		return dataService;
	}

	@Autowired
	public void setQueryDataService(QueryDataService dataService) {
		this.dataService = dataService;
	}
	

	/**
	 * Get top queries' keywords and their counts
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/list")
	public void getTopQueries(HttpServletResponse response) throws IOException {
		logger.info("Get top queries");
		
		List<String> queries = new ArrayList<String>();
		List<Integer> counts = new ArrayList<Integer>();
		
		List<QueryCount> queryCounts = dataService.getTopQueries();
		for (QueryCount c : queryCounts) {
			queries.add(c.query);
			counts.add(c.count);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("keys", queries);
		result.put("counts", counts);
		
		String jsonStr = gson.toJson(result);
		response.setContentType("text/json");
		response.getWriter().println(jsonStr);
	}
	
	/**
	 * Get the trend for one specific keyword
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/trend/history")
	public void getTrend(@RequestParam("query") String query, HttpServletResponse response) throws IOException {
		logger.info("Get trends for: " + query);
		
		List<Long[]> trend = dataService.getHistoryTrend(query);
		
		String jsonStr = gson.toJson(trend);
		response.setContentType("text/json");
		response.getWriter().println(jsonStr);
	}
	
	/**
	 * Get the trend for one specific keyword
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/trend/delta")
	public void getDeltaTrend(@RequestParam("query") String query, HttpServletResponse response) throws IOException {
		logger.info("Get trends for: " + query);
		
		List<Long[]> delta = dataService.getDeltaTrend(query, 1);
		
		String jsonStr = gson.toJson(delta);
		response.setContentType("text/json");
		response.getWriter().println(jsonStr);
	}
	
}
