package com.NetworkAnalysis.rsc;

import java.util.Date;

public class User {
	int IDUser;
	String UserName;
	String Name;
	String Lastname;
	Date LastLogin;
	int IDAccess;
	String Email;
	Boolean Enable;
	Message Message;
	
	public User(){
		this.Enable = false;
		this.Message = new Message();
	}

	public User(int iDUser, String userName, String name, String lastname, Date lastLogin, int iDAccess,
			Boolean enable) {
		super();
		IDUser = iDUser;
		UserName = userName;
		Name = name;
		Lastname = lastname;
		LastLogin = lastLogin;
		IDAccess = iDAccess;
		this.Enable = enable;
		this.Message = new Message();
	}

	public int getIDUser() {
		return IDUser;
	}

	public void setIDUser(int iDUser) {
		IDUser = iDUser;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getLastname() {
		return Lastname;
	}

	public void setLastname(String lastname) {
		Lastname = lastname;
	}

	public Date getLastLogin() {
		return LastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		LastLogin = lastLogin;
	}

	public int getIDAccess() {
		return IDAccess;
	}

	public void setIDAccess(int iDAccess) {
		IDAccess = iDAccess;
	}

	public Boolean getEnable() {
		return Enable;
	}

	public void setEnable(Boolean enable) {
		this.Enable = enable;
	}
	
	public String getEmail() {
		return Email;
	}
	
	public void setEmail(String email) {
		this.Email = email;
	}
	
	public Message getMessage() {
		return Message;
	}
	
	public void setMessage(Message message) {
		Message = message;
	}
	
}
