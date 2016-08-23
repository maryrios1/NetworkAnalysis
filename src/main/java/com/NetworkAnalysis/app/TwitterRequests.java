package com.NetworkAnalysis.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import com.NetworkAnalysis.rsc.*;
/*import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.concurrent.Executor;
*/

@Path("/")
public class TwitterRequests {
	static String consumerKeyStr = "Y8NvcZqWjUvNR0wfict2rSKmx";
	static String consumerSecretStr = "A3K8YqVjLpTN5sSbk9MJ8DmiwIxRapLmyhmZcCau55sqzPjA1y";
	static String accessTokenStr = "566064066-BMF8JBt2JI7c4KBWEDtxRqPN2rLNxwKcUoykzoTR";
	static String accessTokenSecretStr = "wo4LnwlsYYfbYkGixN0CS3NxlYfXxbxwl0gWfpQTIKas4";
	static String URL = "https://3821df9d-7bae-4fbe-82d1-4f2858f1af57-bluemix:4848e6b90b7dc6138e84f1c237e968ef73f2ad1c84e01582dce037334c575df3@3821df9d-7bae-4fbe-82d1-4f2858f1af57-bluemix.cloudant.com/test/";
	static String STORAGE_DIR = "/tmp";
	static long BYTES_PER_FILE = 64 * 1024 * 1024;
	PreparedStatement preparedStatement = null;
	ConnectionRDBMS dbConnection = null;
	HttpResponse response = null;
	HttpGet httpGet = null;

	@GET
	@Produces("text/html")
	public Response getStartingPage() {
		String output = "<h1>Hello World!<h1>" + "<p>RESTful Service is running ... <br>Ping @ " + new Date().toString()
				+ "</p<br>";
		return Response.status(200).entity(output).build();
	}

	@GET
	@Path("/SearchTweets/{words}/{relation}/{searchName}/{iduser}")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetSearchTweets(@PathParam("words") String words, @PathParam("relation") String relation,
			@PathParam("searchName") String searchName, @PathParam("iduser") int IDUser
	// ,@Suspended final AsyncResponse asyncResponse
	) throws IOException {
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { String result =
		 * veryExpensiveOperation(); asyncResponse.resume(result); }
		 * 
		 * private String veryExpensiveOperation() {
		 */
		String json = startSearchTweets(words, relation, searchName, IDUser, Status.START, 0);
		return json;

		/*
		 * } }).start();
		 */

	}

	@GET
	@Path("/SearchTweetsLocation/{words}/{relation}/{searchName}/{iduser}/{latitud}/{longitud}")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetSearchTweets(@PathParam("words") String words, @PathParam("relation") String relation,
			@PathParam("searchName") String searchName, @PathParam("iduser") int IDUser,
			@PathParam("latitud") String latitud, @PathParam("longitud") String longitud
	// ,@Suspended final AsyncResponse asyncResponse
	) throws IOException {
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { String result =
		 * veryExpensiveOperation(); asyncResponse.resume(result); }
		 * 
		 * private String veryExpensiveOperation() {
		 */
		String json = startSearchTweets(words, relation, searchName, IDUser, Status.START, 0, latitud, longitud);
		return json;

		/*
		 * } }).start();
		 */

	}

	/***
	 * Start the search API of Twitter
	 * 
	 * @param words
	 *            Words to track
	 * @param relation
	 *            Relation of the network
	 * @param searchName
	 *            Name of the search
	 * @param IDUSer
	 *            User who requested the search
	 * @return Result of the search
	 */
	public String startSearchTweets(String words, String relation, String searchName, int IDUSer, Status status,
			int idSearch) {
		Boolean KeepSearch = true;
		String line = "";
		Search search = new Search();
		search.setIDSearch(idSearch);
		Message msg = new Message();
		Gson gson = new Gson();
		try {
			// Connect to twiter API
			OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(consumerKeyStr, consumerSecretStr);
			oAuthConsumer.setTokenWithSecret(accessTokenStr, accessTokenSecretStr);

			httpGet = new HttpGet("https://api.twitter.com/1.1/search/tweets.json?q=" + words + "&count=500");

			oAuthConsumer.sign(httpGet);

			DefaultHttpClient client = new DefaultHttpClient();
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println(statusCode + " : " + response.getStatusLine().getReasonPhrase());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String templine = "";
			Boolean tweetsFound = false;

			if (status == Status.START) {
				search = insertRecordDB(searchName, IDUSer, SearchType.SEARCH, words);
			} else
				search = updateRecordDB(search.getIDSearch(), SearchType.SEARCH, Status.RESTART);

			// Read the tweets according the search
			while (KeepSearch && search.getMessage().contains("OK")) {
				line = reader.readLine();
				// if the line is null finish
				if (line == null) {
					// System.out.println("BREAK");
					line = templine;
					break;
				}
				// Insert the tweets in the DB
				if (line.length() > 0) {
					tweetsFound = true;
					templine = line;
					line = "";
					System.out.println("templine " + templine);

					try {
						JSONParser parser = new JSONParser();
						Object objLine = parser.parse(templine);
						JSONObject obj = (JSONObject) objLine;

						JSONArray jArray = (JSONArray) obj.get("statuses");

						for (int i = 0; i < jArray.size(); i++) {
							System.out.println("insert into db tweets # " + i);
							insertTweetMySQLDB(jArray.get(i).toString(), RelationshipSearch.valueOf(relation),
									search.getIDSearch());

							// Stop the search if the flag has been changed to
							if (!dbConnection.selectRecordSearch(search.getIDSearch())) {
								System.out.println("Stop!!");
								KeepSearch = false;
								break;

							}
						}
					} catch (Exception ex) {
						continue;
					}
				}
			}

			search = updateRecordDB(search.getIDSearch(), SearchType.valueOf(search.getType()), Status.STOP);

			if (search.getMessage().contains("ERROR")) {
				return gson.toJson(msg);
			}

			if (tweetsFound) {
				System.out.println("Search tweets finished");
				msg.setMessage("Search tweets finished");
				msg.setCode(1);
				msg.setStatus("OK");
			} else {
				System.out.println("Tweets not found");
				msg.setMessage("Tweets not found request finished");
				msg.setCode(2);
				msg.setStatus("OK");
			}

		} catch (Exception ex) {
			msg.setMessage(ex.getMessage());
			msg.setCode(0);
			msg.setStatus("ERROR");
			return gson.toJson(msg);
		} finally {
			return gson.toJson(msg);
		}
	}

	public String startSearchTweets(String words, String relation, String searchName, int IDUSer, Status status,
			int idSearch, String latitud, String longitud) {
		Boolean KeepSearch = true;
		String line = "";
		Search search = new Search();
		search.setIDSearch(idSearch);
		Message msg = new Message();
		Gson gson = new Gson();
		try {
			// Connect to twiter API
			OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(consumerKeyStr, consumerSecretStr);
			oAuthConsumer.setTokenWithSecret(accessTokenStr, accessTokenSecretStr);
			String url = "https://api.twitter.com/1.1/search/tweets.json?q=&geocode="
					+ latitud + "," + longitud + ",10km&count=500";
			System.out.println(url);
			httpGet = new HttpGet(url);

			oAuthConsumer.sign(httpGet);

			DefaultHttpClient client = new DefaultHttpClient();
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println(statusCode + " : " + response.getStatusLine().getReasonPhrase());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String templine = "";
			Boolean tweetsFound = false;

			if (status == Status.START) {
				search = insertRecordDB(searchName, IDUSer, SearchType.SEARCH, words);
			} else
				search = updateRecordDB(search.getIDSearch(), SearchType.SEARCH, Status.RESTART);

			// Read the tweets according the search
			while (KeepSearch && search.getMessage().contains("OK")) {
				line = reader.readLine();
				// if the line is null finish
				if (line == null) {
					// System.out.println("BREAK");
					line = templine;
					break;
				}
				// Insert the tweets in the DB
				if (line.length() > 0) {
					tweetsFound = true;
					templine = line;
					line = "";
					System.out.println("templine " + templine);

					try {
						JSONParser parser = new JSONParser();
						Object objLine = parser.parse(templine);
						JSONObject obj = (JSONObject) objLine;

						JSONArray jArray = (JSONArray) obj.get("statuses");

						for (int i = 0; i < jArray.size(); i++) {
							System.out.println("insert into db tweets # " + i);
							insertTweetMySQLDB(jArray.get(i).toString(), RelationshipSearch.valueOf(relation),
									search.getIDSearch());

							// Stop the search if the flag has been changed to
							if (!dbConnection.selectRecordSearch(search.getIDSearch())) {
								System.out.println("Stop!!");
								KeepSearch = false;
								break;

							}
						}
					} catch (Exception ex) {
						continue;
					}
				}
			}

			search = updateRecordDB(search.getIDSearch(), SearchType.valueOf(search.getType()), Status.STOP);

			if (search.getMessage().contains("ERROR")) {
				return gson.toJson(msg);
			}

			if (tweetsFound) {
				System.out.println("Search tweets finished");
				msg.setMessage("Search tweets finished");
				msg.setCode(1);
				msg.setStatus("OK");
			} else {
				System.out.println("Tweets not found");
				msg.setMessage("Tweets not found request finished");
				msg.setCode(2);
				msg.setStatus("OK");
			}

		} catch (Exception ex) {
			msg.setMessage(ex.getMessage());
			msg.setCode(0);
			msg.setStatus("ERROR");
			return gson.toJson(msg);
		} finally {
			return gson.toJson(msg);
		}
	}

	@GET
	@Path("/StreamTweets/{word}/{relation}/{searchName}/{iduser}")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetStreamTweets(@PathParam("word") String word, @PathParam("relation") String relation,
			@PathParam("searchName") String searchName, @PathParam("iduser") int IDUser) throws IOException {
		String jsonResult = startStreamTweets(word, relation, searchName, IDUser, Status.START, 0);
/*
 * tweet.startStreamTweets(search.getSearchwords(), relation, search.getSearchname(), search.getIduser(), 
						Status.RESTART, idSearch);
 * 
 * */
		return jsonResult;
	}

	public String startStreamTweets(String word, String relation, String searchName, int idUSer, Status status,
			int idSearch) {
		String line = "";
		Search search = new Search();
		search.setIDSearch(idSearch);
		search.setSearchwords(word);
		search.setIduser(idUSer);
		search.setSearchname(searchName);
		Message msg = new Message();
		Boolean KeepSearch = true;
		Gson gson = new Gson();
		Boolean tweetsFound = false;
		
		try {
			OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(consumerKeyStr, consumerSecretStr);
			oAuthConsumer.setTokenWithSecret(accessTokenStr, accessTokenSecretStr);

			httpGet = new HttpGet("https://stream.twitter.com/1.1/statuses/filter.json?delimited=length&track=" + word);

			System.out.println("GetStreamTweets ");
			oAuthConsumer.sign(httpGet);

			DefaultHttpClient client = new DefaultHttpClient();
			response = client.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println(statusCode + " : " + response.getStatusLine().getReasonPhrase());

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			System.out.println("GetStreamTweets 2");
			String templine = "";
			int i = 0;

			if (status.equals(Status.START)) {
				search = insertRecordDB(searchName, idUSer, SearchType.STREAM, word);
				msg.setStatus("OK");
			} else
				search = updateRecordDB(idSearch, SearchType.STREAM, Status.RESTART);

			while (KeepSearch && search.getMessage().contains("OK")) {
				line = reader.readLine();

				if (line == null) {
					// System.out.println("BREAK");
					line = templine;
					break;
				}
				if (line.length() > 0) {
					templine = line;
					tweetsFound = true;
					JSONParser parser = new JSONParser();
					Object obj = null;
					JSONObject jsonObject;

					try {
						// check if the line is a json value
						System.out.println("#" + i++ + ": " + line);
						obj = parser.parse(line);
						jsonObject = (JSONObject) obj;
						insertTweetMySQLDB(line, RelationshipSearch.valueOf(relation), search.getIDSearch());
						if (!dbConnection.selectRecordSearch(search.getIDSearch())) {
							System.out.println("Stop!!");
							KeepSearch = false;
							break;
						}

					} catch (Exception exjson) {
						continue;
					}
				}

			}
			search = updateRecordDB(search.getIDSearch(), SearchType.STREAM, Status.STOP);

			if (search.getMessage().contains("ERROR")) {
				return gson.toJson(msg);
			}

			if (tweetsFound) {
				System.out.println("Search tweets finished");
				msg.setMessage("Search tweets finished");
				msg.setCode(1);
				msg.setStatus("OK");
			} else {
				System.out.println("Tweets not found");
				msg.setMessage("Tweets not found request finished");
				msg.setCode(2);
				msg.setStatus("OK");
			}

		} catch (Exception ex) {
			msg.setMessage(ex.getMessage());
			msg.setCode(0);
			msg.setStatus("ERROR");
			return gson.toJson(msg);
		} finally {
			return gson.toJson(msg);
		}

	}

	public int insertTweetMySQLDB(String text, RelationshipSearch relation, int idSearch) {
		int status = -1;
		try {
			dbConnection = new ConnectionRDBMS();
			status = dbConnection.insert(text, TableType.NODE, relation, idSearch);
			status = dbConnection.insert(text, TableType.EDGE, relation, idSearch);
			status = dbConnection.insert(text, TableType.TWEET, relation, idSearch);
		} catch (Exception ex) {
			System.out.println("ERROR insert: " + ex.getMessage());
		}
		return status;
	}

	/**
	 * Insert new search
	 * 
	 * @param searchname
	 *            Name of the search
	 * @param iduser
	 *            User ID
	 * @param type
	 *            Search type STREAM/SEARCH
	 * @param words
	 *            Words to search
	 * @return Search
	 * @throws Exception
	 */
	public Search insertRecordDB(String searchname, int iduser, SearchType type, String words) throws Exception {
		dbConnection = new ConnectionRDBMS();
		return dbConnection.insert(searchname, iduser, type, words);
	}

	/***
	 * Update the search to new status
	 * 
	 * @param idSearch
	 *            Search ID
	 * @param action
	 *            Action STOP/RESTART
	 * @return
	 * @throws Exception
	 */
	public Search updateRecordDB(int idSearch, SearchType search, Status action) throws Exception {
		dbConnection = new ConnectionRDBMS();
		return dbConnection.update(idSearch, search, action);
	}

	public boolean keepSearchingTweets(int idSearch) throws Exception {
		boolean status = false;
		dbConnection = new ConnectionRDBMS();
		status = dbConnection.selectRecordSearch(idSearch);
		return status;
	}

}
