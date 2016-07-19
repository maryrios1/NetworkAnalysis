package com.NetworkAnalysis.rsc;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tweet {
	long id;
	String id_str;
	String screen_name;
	long in_reply_to_user_id;
	String in_reply_to_screen_name;
	String text;
	String lang;
	Boolean possibly_sensitive;
	Boolean truncated;
	String hashtags;
	String user_mentions;
	String usr_id_str;
	long usr_id;
	String location;
	Date created_at;
	String source;
	long retweet_count;
	Boolean retweeted;
	long favorite_count;
	int idsearch;
	String tweet;
	

	public Tweet() {
		// TODO Auto-generated constructor stub
	}

	
	
	public Tweet(long id, String id_str, String screen_name, int in_reply_to_user_id, String in_reply_to_screen_name,
			String text, String lang, Boolean possibly_sensitive, Boolean truncated, String hashtags,
			String user_mentions, String usr_id_str, long usr_id, String location, Date created_at, String source,
			long retweet_count, Boolean retweeted, long favorite_count, int idsearch, String tweet) {
		super();
		this.id = id;
		this.id_str = id_str;
		this.screen_name = screen_name;
		this.in_reply_to_user_id = in_reply_to_user_id;
		this.in_reply_to_screen_name = in_reply_to_screen_name;
		this.text = text;
		this.lang = lang;
		this.possibly_sensitive = possibly_sensitive;
		this.truncated = truncated;
		this.hashtags = hashtags;
		this.user_mentions = user_mentions;
		this.usr_id_str = usr_id_str;
		this.usr_id = usr_id;
		this.location = location;
		this.created_at = created_at;
		this.source = source;
		this.retweet_count = retweet_count;
		this.retweeted = retweeted;
		this.favorite_count = favorite_count;
		this.idsearch = idsearch;
		this.tweet = tweet;
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getId_str() {
		return id_str;
	}

	public void setId_str(String id_str) {
		this.id_str = id_str;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public long getIn_reply_to_user_id() {
		return in_reply_to_user_id;
	}

	public void setIn_reply_to_user_id(long in_reply_to_user_id) {
		this.in_reply_to_user_id = in_reply_to_user_id;
	}

	public String getIn_reply_to_screen_name() {
		return in_reply_to_screen_name;
	}

	public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
		this.in_reply_to_screen_name = in_reply_to_screen_name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Boolean getPossibly_sensitive() {
		return possibly_sensitive;
	}

	public void setPossibly_sensitive(Boolean possibly_sensitive) {
		this.possibly_sensitive = possibly_sensitive;
	}

	public Boolean getTruncated() {
		return truncated;
	}

	public void setTruncated(Boolean truncated) {
		this.truncated = truncated;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}

	public String getUser_mentions() {
		return user_mentions;
	}

	public void setUser_mentions(String user_mentions) {
		this.user_mentions = user_mentions;
	}

	public String getUsr_id_str() {
		return usr_id_str;
	}

	public void setUsr_id_str(String usr_id_str) {
		this.usr_id_str = usr_id_str;
	}

	public long getUsr_id() {
		return usr_id;
	}

	public void setUsr_id(long usr_id) {
		this.usr_id = usr_id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getRetweet_count() {
		return retweet_count;
	}

	public void setRetweet_count(long retweet_count) {
		this.retweet_count = retweet_count;
	}

	public Boolean getRetweeted() {
		return retweeted;
	}

	public void setRetweeted(Boolean retweeted) {
		this.retweeted = retweeted;
	}

	public long getFavorite_count() {
		return favorite_count;
	}

	public void setFavorite_count(long favorite_count) {
		this.favorite_count = favorite_count;
	}

	public int getIdsearch() {
		return idsearch;
	}

	public void setIdsearch(int idsearch) {
		this.idsearch = idsearch;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

}
