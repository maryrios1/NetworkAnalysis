package com.NetworkAnalysis.rsc;

public class Credential {
	int id;
	String consumerKeyStr;
	String consumerSecretStr;
	String accessTokenStr;
	String accessTokenSecretStr;
	boolean enable;
	
	public Credential(int id, String consumerKeyStr, String consumerSecretStr, String accessTokenStr,
			String accessTokenSecretStr, boolean enable) {
		super();
		this.id = id;
		this.consumerKeyStr = consumerKeyStr;
		this.consumerSecretStr = consumerSecretStr;
		this.accessTokenStr = accessTokenStr;
		this.accessTokenSecretStr = accessTokenSecretStr;
		this.enable = enable;
	}

	public Credential() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConsumerKeyStr() {
		return consumerKeyStr;
	}

	public void setConsumerKeyStr(String consumerKeyStr) {
		this.consumerKeyStr = consumerKeyStr;
	}

	public String getConsumerSecretStr() {
		return consumerSecretStr;
	}

	public void setConsumerSecretStr(String consumerSecretStr) {
		this.consumerSecretStr = consumerSecretStr;
	}

	public String getAccessTokenStr() {
		return accessTokenStr;
	}

	public void setAccessTokenStr(String accessTokenStr) {
		this.accessTokenStr = accessTokenStr;
	}

	public String getAccessTokenSecretStr() {
		return accessTokenSecretStr;
	}

	public void setAccessTokenSecretStr(String accessTokenSecretStr) {
		this.accessTokenSecretStr = accessTokenSecretStr;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	
	
}
