package com.NetworkAnalysis.rsc;

import java.util.Date;

public class Search {
	int IDSearch;
	String searchname;
	int iduser;
	Date startsearch;
	Date endsearch;
	Date lastupdate;
	String type;
	Boolean keepsearching;
	String searchwords;
	Boolean enable;
	public Message message ;
	float tweetsNumber;
	public Credential credential;

	public Search(int iDSearch, String searchname, int iduser, Date startsearch, Date endsearch, Date lastupdate,
			String type, Boolean keepsearching, String searchwords, Boolean enable,float tweetsNumber) {
		super();
		IDSearch = iDSearch;
		this.searchname = searchname;
		this.iduser = iduser;
		this.startsearch = startsearch;
		this.endsearch = endsearch;
		this.lastupdate = lastupdate;
		this.type = type;
		this.keepsearching = keepsearching;
		this.searchwords = searchwords;
		this.enable = enable;
		this.tweetsNumber = tweetsNumber;
		message = new Message();
		credential= new Credential();
	}
	
	public Search()
	{
		IDSearch = -1;
		enable = false;
		keepsearching = false;
		message = new Message();
		credential= new Credential();
	}

	public int getIDSearch() {
		return IDSearch;
	}

	public void setIDSearch(int iDSearch) {
		IDSearch = iDSearch;
	}

	public String getSearchname() {
		return searchname;
	}

	public void setSearchname(String searchname) {
		this.searchname = searchname;
	}

	public int getIduser() {
		return iduser;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public Date getStartsearch() {
		return startsearch;
	}

	public void setStartsearch(Date startsearch) {
		this.startsearch = startsearch;
	}

	public Date getEndsearch() {
		return endsearch;
	}

	public void setEndsearch(Date endsearch) {
		this.endsearch = endsearch;
	}

	public Date getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getKeepsearching() {
		return keepsearching;
	}

	public void setKeepsearching(Boolean keepsearching) {
		this.keepsearching = keepsearching;
	}

	public String getSearchwords() {
		return searchwords;
	}

	public void setSearchwords(String searchwords) {
		this.searchwords = searchwords;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}

	public float getTweetsNumber() {
		return tweetsNumber;
	}

	public void setTweetsNumber(float tweetsNumber) {
		this.tweetsNumber = tweetsNumber;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

}
