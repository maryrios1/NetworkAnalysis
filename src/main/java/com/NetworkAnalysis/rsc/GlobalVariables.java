package com.NetworkAnalysis.rsc;

public class GlobalVariables {

	String connection;
	
	public GlobalVariables() {
		connection = "http://localhost/NetworkAnalysis/";
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}
}
