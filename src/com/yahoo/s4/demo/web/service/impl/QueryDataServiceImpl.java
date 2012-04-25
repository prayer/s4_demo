package com.yahoo.s4.demo.web.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;


import com.yahoo.s4.demo.web.service.QueryDataService;
import com.yahoo.s4.demo.web.util.QueryCount;


@Service("dataService")
public class QueryDataServiceImpl extends BaseServiceImpl implements QueryDataService {

	private static final Logger logger = Logger.getLogger(QueryDataServiceImpl.class);
	
	private static final int SLOT_UNIT = 3;	// 3 seconds	
	private static final int SLOT_COUNT = 300; // full time window slot count
	
	private List<QueryCount> topQueries = new ArrayList<QueryCount>(10);	
	private Map<String, List<Long[]>> queryTrends = new HashMap<String, List<Long[]>>(10);
			
	
	@Override
	public List<QueryCount> getTopQueries() {
		return topQueries;
	}

	@Override
	public List<Long[]> getHistoryTrend(String query) {
		return queryTrends.get(query);
	}

	@Override
	public List<Long[]> getDeltaTrend(String query, int range) {
		logger.info("Get trends for " + query);
		List<Long[]> currentTrend = queryTrends.get(query);
		int fromIndex = currentTrend.size() - range;		
		return currentTrend.subList(fromIndex, currentTrend.size());
	}

	
	public void setTopQueries(String[] queries, Integer[] counts) {
		synchronized (topQueries) {
			topQueries.clear();
			for (int i = 0; i < queries.length; i++) {
				topQueries.add(new QueryCount(queries[i], counts[i]));
			}
			Collections.sort(topQueries);
		}
	}
	
	public void setTrends(String[] queries, Integer[][] detailCounts) {
		queryTrends.clear();
		long currentSeconds = System.currentTimeMillis() / 1000;
		long startSeconds = currentSeconds - SLOT_UNIT * SLOT_COUNT;
		
		for (int i = 0; i < queries.length; i++) {
			List<Long[]> trend = new ArrayList<Long[]>(SLOT_COUNT);
			int actualSlotCount = detailCounts[i].length;
			int paddingZeroCount = SLOT_COUNT - actualSlotCount;
			
			long time = startSeconds;
			for (int j = 1; j <= paddingZeroCount; j++) {
				time += SLOT_UNIT;
				Long[] point = new Long[] {time * 1000, new Long(0)};
				trend.add(point);
			}
			for (int j = 0; j < (SLOT_COUNT - paddingZeroCount); j++) {
				time += SLOT_UNIT;
				Long[] point = new Long[] {time * 1000, detailCounts[i][j].longValue()};
				trend.add(point);
			}
			
			queryTrends.put(queries[i], trend);
		}
		
	}
}
