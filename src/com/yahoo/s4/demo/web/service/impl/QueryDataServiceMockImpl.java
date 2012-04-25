package com.yahoo.s4.demo.web.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.yahoo.s4.demo.web.service.QueryDataService;
import com.yahoo.s4.demo.web.util.QueryCount;


//@Service("dataService")
public class QueryDataServiceMockImpl extends BaseServiceImpl implements QueryDataService {

	private static final String[] queryKeys = new String[] {
		"Yahoo", "Microsoft", "Facebook", "Lady Gaga", "Google", "Walmart", "Dell", "Jobs", "IPhone", "Obama"
	};
	
	private static final int[] queryCounts = new int[] {
		220000, 340000, 180000, 420000, 390000, 160000, 120000, 480000, 120000, 80000
	};
	
	private static final long	BASE_COUNT = 100;
	private static final int	SLOT_COUNT = 300;
	
	private static final Random random = new Random();
	
	@Override
	public List<QueryCount> getTopQueries() {
		List<QueryCount> topQueries = new ArrayList<QueryCount>();
		
		Collections.shuffle(Arrays.asList(queryKeys));
		for (int i = 0; i < queryKeys.length; i++) {
			topQueries.add(new QueryCount(queryKeys[i], queryCounts[i] + random.nextInt(100000)));
		}
		Collections.sort(topQueries);
		
		return topQueries;
	}

	@Override
	public List<Long[]> getHistoryTrend(String query) {		
		List<Long[]> trend = new ArrayList<Long[]>();
		long currentSeconds = System.currentTimeMillis() / 1000;		
		for (int i = SLOT_COUNT; i >= 0; i--) {
			Long[] point = new Long[] {(currentSeconds - 3 * i) * 1000, BASE_COUNT + random.nextInt(10)};
			trend.add(point);
		}
		return trend; 
	}

	@Override
	public List<Long[]> getDeltaTrend(String query, int range) {
		List<Long[]> delta = new ArrayList<Long[]>();
		long currentSeconds = System.currentTimeMillis() / 1000;
		Long[] point = new Long[] {currentSeconds * 1000, BASE_COUNT + random.nextInt(10)};
		delta.add(point);
		return delta;
	}

	@Override
	public void setTopQueries(String[] queries, Integer[] counts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTrends(String[] queries, Integer[][] detailCounts) {
		// TODO Auto-generated method stub
		
	}
	
}
