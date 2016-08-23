package com.NetworkAnalysis.rsc;

public class Node {
	
	long id;
	String label;
	String url;
	int count;

	public Node() {
		// TODO Auto-generated constructor stub
	}

	public Node(long id, String label, String url, int count) {
		this.id = id;
		this.label = label;
		this.url = url;
		this.count = count;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

}
