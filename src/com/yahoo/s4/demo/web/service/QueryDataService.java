package com.yahoo.s4.demo.web.service;

import java.util.List;

import com.yahoo.s4.demo.web.util.*;

public interface QueryDataService extends BaseService {


	public List<QueryCount> getTopQueries();
	
	public List<Long[]> getHistoryTrend(String query);
	
	public List<Long[]> getDeltaTrend(String query, int range);
}
