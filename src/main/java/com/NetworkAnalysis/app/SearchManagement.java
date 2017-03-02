package com.NetworkAnalysis.app;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.NetworkAnalysis.rsc.ConnectionRDBMS;
import com.NetworkAnalysis.rsc.Message;
import com.NetworkAnalysis.rsc.Search;
import com.NetworkAnalysis.rsc.SearchType;
import com.NetworkAnalysis.rsc.Status;
import com.NetworkAnalysis.rsc.Tweet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Path("/Search")
public class SearchManagement {
	PreparedStatement preparedStatement = null;
	ConnectionRDBMS dbConnection = null;

	@GET
	@Path("/StopRequest/{idSearch}")
	//@Produces(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String StopRequest(@PathParam("idSearch") int idSearch) {
		Search search =  new Search();
		Gson gson = new Gson();
		Message msg = new Message();
		try {
			search = updateRecordDB(idSearch, SearchType.SEARCH, Status.STOP);
			msg = search.getMessage();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.setMessage("Error stopping the search ID: " + idSearch + ", ERROR: " + e.getMessage());
			msg.setCode(502);
			msg.setStatus("ERROR");
			msg.setObject("STOP");
		}

		return gson.toJson(msg);
		//return msg;
	}

	@GET
	@Path("/RestartRequest/{idSearch}/{type}/{relation}")
	//@Produces(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String RestartRequest(@PathParam("idSearch") int idSearch, @PathParam("type") String type,
			@PathParam("relation") String relation) {

		Message temMsg =  new Message();
		Gson gson = new Gson();
		//String msg = "";
		try {

			TwitterRequests tweet = new TwitterRequests();
			SearchType searchType = SearchType.valueOf(type);
			dbConnection = new ConnectionRDBMS();
			System.out.println("RestartRequest type: " + type);
			
			Search search = dbConnection.getRecordSearch(idSearch);
			if(search.message.getStatus().equals("ERROR")){
				//throw new Exception ("Error getting the search ID: " + idSearch);
				temMsg = search.getMessage();
				return gson.toJson(temMsg);
				//return temMsg;
			}
			
			switch (searchType.toString()) {
			case "SEARCH":
				temMsg = tweet.startSearchTweets(search.getSearchwords(), relation, search.getSearchname(), search.getIduser(),
						Status.RESTART, idSearch);
				break;
			case "STREAM":
				temMsg = tweet.startStreamTweets(search.getSearchwords(), relation, search.getSearchname(), search.getIduser(), 
						Status.RESTART, idSearch);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			temMsg.setMessage("Error restarting the search ID: " + idSearch + ", ERROR: " + e.getMessage());
			temMsg.setCode(502);
			temMsg.setStatus("ERROR");
			temMsg.setObject("RESTART");
			//msg = gson.toJson(temMsg);
			System.out.println("ERROR (RestartRequest): " +e.getMessage());
		}

		return gson.toJson(temMsg);
	}

	@GET
	@Path("/GetSearchesByUser/{iduser}/{total}")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetSearchesByUser(@PathParam("iduser") int idUser,@PathParam("total") int total){
		ArrayList searchesList = new ArrayList();
		try {
			ConnectionRDBMS connect = new ConnectionRDBMS();
			searchesList = connect.getAllSearches(idUser,total);
		} catch (Exception ex) {
			System.out.println("ERROR" + ex.getMessage());
		}

		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(searchesList, new TypeToken<List<Search>>() {	}.getType());
		
		JsonArray jsonArray = element.getAsJsonArray();
		return jsonArray.toString();
	}
	
	@GET
	@Path("/DisableSearch/{idSearch}")
	@Produces(MediaType.TEXT_PLAIN)
	public String DisableSearch(@PathParam("idSearch") int idSearch) {
		Message msg = new Message();
		Gson gson = new Gson();
		try {
			dbConnection = new ConnectionRDBMS();
			dbConnection.disableRecordSearch(idSearch, Status.DISABLE);
			msg.setMessage("Search disabled correctly ID:" + idSearch + ".");
			msg.setCode(100);
			msg.setStatus("OK");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.setMessage("Error disabling the search ID: " + idSearch + ", ERROR: " + e.getMessage());
			msg.setCode(501);
			msg.setStatus("ERROR");
		}

		return gson.toJson(msg);
	}

	@GET
	@Path("/EnableSearch/{idSearch}")
	@Produces(MediaType.TEXT_PLAIN)
	public String EnableSearch(@PathParam("idSearch") int idSearch) {

		Message msg = new Message();
		Gson gson = new Gson();

		try {
			dbConnection = new ConnectionRDBMS();
			dbConnection.disableRecordSearch(idSearch, Status.ENABLE);
			msg.setMessage("Search enable correctly ID:" + idSearch + ".");
			msg.setCode(100);
			msg.setStatus("OK");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.setMessage("Error enabling the search ID: " + idSearch + ", ERROR: " + e.getMessage());
			msg.setCode(501);
			msg.setStatus("ERROR");
		}

		return gson.toJson(msg);
	}

	public Search updateRecordDB(int idSearch, SearchType type, Status action) throws Exception {
		dbConnection = new ConnectionRDBMS();
		return dbConnection.update(idSearch, type, action);
	}
	
	
	@GET
	@Path("/GetTweetBySearch/{idsearch}/{total}")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetTweetBySearch(@PathParam("idsearch") int idSearch,@PathParam("total") int total){
		ArrayList tweetsList = new ArrayList();
		try {
			ConnectionRDBMS connect = new ConnectionRDBMS();
			tweetsList = connect.getTweetBySearchJson(idSearch, total);
		} catch (Exception ex) {
			System.out.println("ERROR" + ex.getMessage());
		}

		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(tweetsList, new TypeToken<List<Tweet>>() {	}.getType());
		
		JsonArray jsonArray = element.getAsJsonArray();
		return jsonArray.toString();
	}
	
}
