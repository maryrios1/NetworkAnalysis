package com.NetworkAnalysis.rsc;

public class Message {
	String message;
	int code;
	String error;
	String source;
	String object;
	String status;
	
	public String getMessage() {
		return message;
	}	

	public Message(String message, int code, String error, String source, String object,String status) {
		super();
		this.message = message;
		this.code = code;
		this.error = error;
		this.source = source;
		this.object = object;
		this.status = status;
	}

	public Message(String message, int code,String status) {
		this.message = message;
		this.code = code;
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public Message() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{Message:" + this.message + ",Status:" + this.status.toString() + ",Error:" + this.error + 
				",Object:" + this.object +",Code:" + this.code +",Source:" + this.source + "}";
	}
	
	

}
