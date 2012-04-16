package com.yahoo.s4.demo.web.util;


public class QueryCount implements Comparable<QueryCount>{
	public String	query;
	public int		count;
	
	public QueryCount(String query, int count) {
		this.query = query;
		this.count = count;
	}
	
	/**
	 * We need to show queries in descending order, so we reverse the natural order here.
	 */
	@Override
	public int compareTo(QueryCount o) {
		if (count > o.count) {
			return -1;
		} 
		if (count < o.count) {
			return 1;
		}
		return 0;
	}	
}
