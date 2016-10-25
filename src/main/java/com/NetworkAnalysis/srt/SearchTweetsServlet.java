package com.NetworkAnalysis.srt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
/*
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;*/
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.NetworkAnalysis.rsc.GlobalVariablesInterface;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Servlet implementation class SearchTweets
 *//*
@WebServlet(
		description = "Call the search tweets API", 
		urlPatterns = { "/SearchTweets" }, 
		initParams = { 
				@WebInitParam(name = "keywords", value = "", description = "Words to look for")
		})*/
@WebServlet("/SearchTweetsServlet")
public class SearchTweetsServlet extends HttpServlet implements GlobalVariablesInterface {
	private static final long serialVersionUID = 1L;
	String SEARCH_NAME;
	String SEARCH_TYPE;
	static final String REST_URI = global.getConnection();
	static final String STREAM_TWEETS= "/StreamTweets/";
	static final String SEARCH_TWEETS = "/SearchTweets/";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchTweetsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		try {
			processRequest(request, response);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParseException {
        
		String keywords = request.getParameter("keywords");
        SEARCH_NAME = request.getParameter("SearchName");
        SEARCH_TYPE = request.getParameter("SearchType");
        System.out.println("keywords:" + keywords);
        System.out.println("SearchName: " + SEARCH_NAME);
        System.out.println("SearchType: " + SEARCH_TYPE);              
        
        ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		
		WebResource service = client.resource(REST_URI);
		String asd = REST_URI + "rest" + SEARCH_TWEETS + keywords+"/REPLIED/" + SEARCH_NAME+ "/1";
		WebResource addService;
		try{
			if (SEARCH_TYPE.equals("Search"))
				addService = service.path("rest").path(SEARCH_TWEETS + keywords+"/REPLIED/" + SEARCH_NAME+ "/1");
				//response.getWriter().print((service.path("rest").path(SEARCH_TWEETS + keywords+"/REPLIED/" + SEARCH_NAME+ "/1"))
					//.accept(MediaType.TEXT_PLAIN).get(String.class));
			else
				addService = service.path("rest").path(STREAM_TWEETS + keywords + "/REPLIED/" + SEARCH_NAME+ "/1");
				//response.getWriter().print((service.path("rest").path(STREAM_TWEETS + keywords + "/REPLIED/" + SEARCH_NAME+ "/1"))
					//	.accept(MediaType.TEXT_PLAIN).get(String.class));		
			
			System.out.println("GetTweets Response: " + getResponse(addService));		
			
			response.getWriter().print(getOutputAsJSON(addService));
		}
		catch(Exception ex){
			System.out.println("ERROR" + ex.getMessage());
		}
        
    }
	
	private static String getResponse(WebResource service) {
		return service.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString();
	}
	
	private static String getOutputAsJSON(WebResource service) {
		return service.accept(MediaType.TEXT_PLAIN).get(String.class);
	}

}
