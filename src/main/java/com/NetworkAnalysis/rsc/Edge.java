package com.NetworkAnalysis.rsc;

import java.util.Date;

public class Edge {

	public Node nodeSource;
	public Node nodeTarget;
	int weight;
	String relation;
	Date timeinterval;
	
	public Edge(Node nodeSource, Node nodeTarget, int weight, String relation, Date timeinterval) {
		super();
		this.nodeSource = nodeSource;
		this.nodeTarget = nodeTarget;
		this.weight = weight;
		this.relation = relation;
		this.timeinterval = timeinterval;
	}

	public Node getNodeSource() {
		return nodeSource;
	}

	public void setNodeSource(Node nodeSource) {
		this.nodeSource = nodeSource;
	}

	public Node getNodeTarget() {
		return nodeTarget;
	}

	public void setNodeTarget(Node nodeTarget) {
		this.nodeTarget = nodeTarget;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Edge() {
		nodeSource =  new Node();
		nodeTarget =  new Node();
		// TODO Auto-generated constructor stub
	}
	
	public Date getTimeinterval() {
		return timeinterval;
	}

	public void setTimeinterval(Date timeinterval) {
		this.timeinterval = timeinterval;
	}

}
