package com.NetworkAnalysis.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	) {
		Gson gson = new Gson();
		Message msg = startSearchTweets(words, relation, searchName, IDUser, Status.START, 0);
		return gson.toJson(msg);
	}

	@GET
	@Path("/SearchTweetsLocation/{words}/{relation}/{searchName}/{iduser}/{latitud}/{longitud}")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetSearchTweets(@PathParam("words") String words, @PathParam("relation") String relation,
			@PathParam("searchName") String searchName, @PathParam("iduser") int IDUser,
			@PathParam("latitud") String latitud, @PathParam("longitud") String longitud
	// ,@Suspended final AsyncResponse asyncResponse
	) throws IOException {
		String json = startSearchTweets(words, relation, searchName, IDUser, Status.START, 0, latitud, longitud);
		return json;

	}
	
	@GET
	@Path("/StreamTweets/{word}/{relation}/{searchName}/{iduser}")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetStreamTweets(@PathParam("word") String word, @PathParam("relation") String relation,
			@PathParam("searchName") String searchName, @PathParam("iduser") int IDUser) throws IOException {
		Gson gson = new Gson();
		Message msg = startStreamTweets(word, relation, searchName, IDUser, Status.START, 0);
		return gson.toJson(msg);
	}
	
	@GET
	@Path("/FixTweets/{idSearch}/{delete}/{start}/{total}")
	@Produces(MediaType.TEXT_PLAIN)
	public void FixTweets( @PathParam("idSearch") int IdSearch,@PathParam("delete") int delete,@PathParam("start") int start,@PathParam("total") int total
	) throws IOException {
		
		dbConnection= new ConnectionRDBMS();
		if(total==-1 || total>10000){
			int pos=10000;
			while(pos<=total){
				dbConnection.fixTweets(IdSearch,delete,start,10000);
				pos+=10000;
			}
			dbConnection.fixTweets(IdSearch,delete,start,total-(pos-10000));
		}
		else
			dbConnection.fixTweets(IdSearch,delete,start,total);

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
	public Message startSearchTweets(String words, String relation, String searchName, int IDUSer, Status status,
			int idSearch) {
		Boolean KeepSearch = true;
		String line = "";
		Search search = new Search();
		//search.setIDSearch(idSearch);
		Message msg = null;		
		try {
			
			if (status == Status.START) {
				search = insertRecordDB(searchName, IDUSer, SearchType.SEARCH, words);
			} else
				search = updateRecordDB(search.getIDSearch(), SearchType.SEARCH, Status.RESTART);
			
			Credential credential = search.getCredential();
			msg = search.getMessage();
			
			if(msg.getStatus().equals("ERROR")){
				search = updateRecordDB(search.getIDSearch(), SearchType.SEARCH, Status.STOP);
				return msg;
			}
				
			// Connect to twiter API
			OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(credential.getConsumerKeyStr(),credential.getConsumerSecretStr());
			oAuthConsumer.setTokenWithSecret(credential.getAccessTokenStr(),credential.getAccessTokenSecretStr());

			httpGet = new HttpGet("https://api.twitter.com/1.1/search/tweets.json?q=" + words + "&count=500");

			oAuthConsumer.sign(httpGet);

			DefaultHttpClient client = new DefaultHttpClient();
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseAPI = response.getStatusLine().getReasonPhrase();
			System.out.println(statusCode + " : " + responseAPI);
			
			//ERROR connecting to API Twitter
			if(statusCode!=200){
				msg.setStatus("ERROR");
				msg.setMessage("Error con la credencial de twitter por favor intenta de nuevo.");
				msg.setError(responseAPI);
				msg.setCode(statusCode);
				msg.setObject("RESTART");
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String templine = "";
			Boolean tweetsFound = false;			

			// Read the tweets according the search
			while (KeepSearch && msg.getStatus().equals("OK")) {
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

			if (search.getMessage().getStatus().equals("ERROR")) {
				return search.getMessage();
			}

			if (tweetsFound) {
				System.out.println("Search tweets finished");
				msg.setMessage("Search tweets finished");
				msg.setCode(100);
				msg.setStatus("OK");
				msg.setObject("RESTART");
			} else {
				System.out.println("Tweets not found");
				msg.setMessage("Tweets not found request finished");
				msg.setCode(101);
				msg.setStatus("OK");
				msg.setObject("RESTART");
			}

		} catch (Exception ex) {
			System.out.println("ERROR: " + ex.getMessage());
			msg = new Message();
			msg.setMessage("ERROR: " + ex.getMessage());
			msg.setCode(502);
			msg.setStatus("ERROR");
			return msg;
		} finally {
			return msg;
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
			
			if (status == Status.START) {
				search = insertRecordDB(searchName, IDUSer, SearchType.SEARCH, words);
			} else
				search = updateRecordDB(search.getIDSearch(), SearchType.SEARCH, Status.RESTART);
			
			Credential credential = search.getCredential();
			msg = search.getMessage();
			
			if(msg.getStatus().equals("ERROR")){
				search = updateRecordDB(search.getIDSearch(), SearchType.SEARCH, Status.STOP);
				return gson.toJson(msg);
			}
			
			// Connect to twiter API
			OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(credential.getConsumerKeyStr(), credential.getConsumerSecretStr());
			oAuthConsumer.setTokenWithSecret(credential.getAccessTokenStr(), credential.getAccessTokenSecretStr());
			String url = "https://api.twitter.com/1.1/search/tweets.json?q=&geocode="
					+ latitud + "," + longitud + ",10km&count=500";
			System.out.println(url);
			httpGet = new HttpGet(url);

			oAuthConsumer.sign(httpGet);

			DefaultHttpClient client = new DefaultHttpClient();
			response = client.execute(httpGet);
			
			int statusCode = response.getStatusLine().getStatusCode();
			String responseAPI = response.getStatusLine().getReasonPhrase();
			System.out.println(statusCode + " : " + responseAPI);
			
			//ERROR connecting to API Twitter
			if(statusCode!=200){
				msg.setStatus("ERROR");
				msg.setMessage("Error con la credencial de twitter por favor intenta de nuevo.");
				msg.setError(responseAPI);
				msg.setCode(statusCode);
				msg.setObject("RESTART");
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String templine = "";
			Boolean tweetsFound = false;			

			// Read the tweets according the search
			while (KeepSearch && msg.getStatus().equals("OK")) {
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

			if (search.getMessage().getStatus().equals("ERROR")) {
				return gson.toJson(search.getMessage());
			}

			if (tweetsFound) {
				System.out.println("Search tweets finished");
				msg.setMessage("Search tweets finished.");
				msg.setCode(100);
				msg.setStatus("OK");
			} else {
				System.out.println("Tweets not found");
				msg.setMessage("Tweets not found request finished.");
				msg.setCode(101);
				msg.setStatus("OK");
			}

		} catch (Exception ex) {
			msg.setMessage(ex.getMessage());
			msg.setCode(502);
			msg.setStatus("ERROR");
			return gson.toJson(msg);
		} finally {
			return gson.toJson(msg);
		}
	}

	public Message startStreamTweets(String word, String relation, String searchName, int idUSer, Status status,
			int idSearch) {
		String line = "";
		Search search ;//= new Search();
		/*search.setIDSearch(idSearch);
		search.setSearchwords(word);
		search.setIduser(idUSer);
		search.setSearchname(searchName);*/
		Message msg = null;// = new Message();
		Boolean KeepSearch = true;
		//Gson gson = new Gson();
		Boolean tweetsFound = false;
		System.out.println("Entrando a la busqueda por stream con status: " + status);
		
		try {
			// Insert or update the search
			if (status.equals(Status.START)) {
				search = insertRecordDB(searchName, idUSer, SearchType.STREAM, word);				
			} else
				search = updateRecordDB(idSearch, SearchType.STREAM, Status.RESTART);
			
			System.out.println("Search: " + search.toString());
			
			Credential credential = search.getCredential();
			msg = search.getMessage();
			System.out.println("Message: " + msg.toString());
			
			if(msg.getStatus().equals("ERROR"))
			{
				System.out.println("Error creando busqueda en la bd.");
				idSearch = search.getIDSearch();
				if (idSearch >0)
					updateRecordDB(idSearch, SearchType.STREAM, Status.STOP);
				return msg;
			}
			
			OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(credential.getConsumerKeyStr(), credential.getConsumerSecretStr());
			oAuthConsumer.setTokenWithSecret(credential.getAccessTokenStr(), credential.getAccessTokenSecretStr());

			httpGet = new HttpGet("https://stream.twitter.com/1.1/statuses/filter.json?delimited=length&track=" + word);

			System.out.println("GetStreamTweets ");
			oAuthConsumer.sign(httpGet);

			DefaultHttpClient client = new DefaultHttpClient();
			response = client.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			String responseMessage = response.getStatusLine().getReasonPhrase();
			System.out.println(statusCode + " : " + responseMessage);

			if(statusCode!=200){
				// Stop the logical search in the database
				System.out.println("Hubo un error al ejecutar la búsqueda.");
				search = updateRecordDB(idSearch, SearchType.STREAM, Status.STOP);
				msg.setCode(statusCode);
				msg.setMessage("Error en la credencial por favor intente de nuevo.");
				msg.setObject(status.RESTART.toString());
				msg.setError(responseMessage);
				msg.setStatus("ERROR");
				return msg;
			}
								
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			System.out.println("GetStreamTweets 2");
			String templine = "";
			int i = 0;
			

			while (KeepSearch && msg.getStatus().equals("OK")) {
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
						i++;
						obj = parser.parse(line);
						jsonObject = (JSONObject) obj;
						insertTweetMySQLDB(line, RelationshipSearch.valueOf(relation), search.getIDSearch());
						if (!dbConnection.selectRecordSearch(search.getIDSearch())) {
							System.out.println("Stop!!");
							KeepSearch = false;
							break;
						}

					} catch (Exception exjson) {
						System.out.println("#" + i + ": " + line);
						continue;
					}
				}

			}
			search = updateRecordDB(search.getIDSearch(), SearchType.STREAM, Status.STOP);

			if (search.getMessage().getStatus().equals("ERROR")) 
				return search.getMessage();
			

			if (tweetsFound) {
				System.out.println("Search tweets finished");
				msg.setMessage("Búsqueda de tweets terminada.");
				msg.setCode(101);
				msg.setStatus("OK");
				msg.setObject(Status.RESTART.toString());
			} else {
				System.out.println("Tweets not found");
				msg.setMessage("Tweets no encontrados, solicitud terminada.");
				msg.setCode(101);
				msg.setStatus("OK");
				msg.setObject(Status.RESTART.toString());
			}

		} catch (Exception ex) {
			msg = new Message();
			msg.setMessage(ex.getMessage());
			msg.setCode(502);
			msg.setStatus("ERROR");
			System.out.println("ERROR TwitterRequests catch:" + ex.getMessage());
			return msg;
		} finally {
			return msg;
		}

	}

	public int insertTweetMySQLDB(String text, RelationshipSearch relation, int idSearch) {
		int status = -1;
		try {
			dbConnection = new ConnectionRDBMS();
			status = dbConnection.insert(text, relation, idSearch);
			/*status = dbConnection.insert(text, TableType.NODE, relation, idSearch);
			status = dbConnection.insert(text, TableType.EDGE, relation, idSearch);*/			
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
