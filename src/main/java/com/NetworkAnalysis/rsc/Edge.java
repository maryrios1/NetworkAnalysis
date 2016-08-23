package com.NetworkAnalysis.rsc;

public class Edge {

	public Node nodeSource;
	public Node nodeTarget;
	int weight;
	String relation;
	
	public Edge(Node nodeSource, Node nodeTarget, int weight, String relation) {
		super();
		this.nodeSource = nodeSource;
		this.nodeTarget = nodeTarget;
		this.weight = weight;
		this.relation = relation;
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

}
