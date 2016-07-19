package com.NetworkAnalysis.app;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import com.NetworkAnalysis.rsc.ConnectionRDBMS;
import com.NetworkAnalysis.rsc.User;
import com.google.gson.Gson;

@Path("/User")
public class UserManagement {

	@GET
	@Produces("text/html")
	public Response getStartingPage() {
		String output = "<h1>Hello World!<h1>" + "<p>RESTful Service is running for user module ... <br>Ping @ "
				+ new Date().toString() + "</p<br>";
		return Response.status(200).entity(output).build();
	}

	@GET
	@Path("/Login/{user}/{pwd}")
	@Produces(MediaType.APPLICATION_JSON)
	public String Login(@PathParam("user") String user, @PathParam("pwd") String pwd) {

		Gson gson = new Gson();
		String jObjstring = "";

		try {
			ConnectionRDBMS conn = new ConnectionRDBMS();
			User userInfo = conn.checkLogin(user, pwd);	
			jObjstring = gson.toJson(userInfo);
			/*
			 * jObj.put("Message", "User logged in successfully " +
			 * userInfo.getUserName() + "."); jObj.put("Status", "Success");
			 * jObj.put("Code", 1);
			 */

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JSONObject jObj = new JSONObject();
			jObj.put("Message", "Error getting the access of the user " + user + ", ERROR: " + e.getMessage());
			jObj.put("Status", "Error");
			jObj.put("Code", 0);
			jObjstring = jObj.toJSONString();
		}

		return jObjstring;
	}
	
	@GET
	@Path("/Register/{user}/{pwd}/{name}/{lastname}/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public String Register(@PathParam("user") String user, @PathParam("pwd") String pwd,
			@PathParam("name") String name,@PathParam("lastname") String lastname,@PathParam("email") String email) {

		Gson gson = new Gson();
		String jObjstring = "";

		try {
			ConnectionRDBMS conn = new ConnectionRDBMS();
			User userInfo = conn.registerUser(user, pwd,name,lastname,email);	
			jObjstring = gson.toJson(userInfo);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JSONObject jObj = new JSONObject();
			jObj.put("Message", "Error getting the access of the user " + user + ", ERROR: " + e.getMessage());
			jObj.put("Status", "Error");
			jObj.put("Code", 0);
			jObjstring = jObj.toJSONString();
		}

		return jObjstring;
	}
}
