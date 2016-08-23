package com.NetworkAnalysis.rsc;

public class GlobalVariables {

	String connection;
	
	public GlobalVariables() {
		//local
		//connection = "http://localhost:8080/NetworkAnalysis/";
		//amazon
		connection = "http://localhost/NetworkAnalysis/";
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}
}

