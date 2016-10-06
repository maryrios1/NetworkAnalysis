package com.NetworkAnalysis.rsc;

import java.util.Date;

public class Node {
	
	String id;
	String label;
	String url;
	int count;
	Date timeinterval;

	public Node() {
		// TODO Auto-generated constructor stub
	}

	public Node(String id, String label, String url, int count,Date timeinterval) {
		this.id = id;
		this.label = label;
		this.url = url;
		this.count = count;
		this.timeinterval = timeinterval;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Date getTimeinterval() {
		return timeinterval;
	}

	public void setTimeinterval(Date timeinterval) {
		this.timeinterval = timeinterval;
	}

}
