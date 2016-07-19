package com.NetworkAnalysis.srt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class SearchTweetsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String SEARCH_NAME;
	static final String REST_URI = "http://localhost:8080/NetworkAnalysis/";
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
        System.out.println("keywords:" + keywords);
        System.out.println("SearchName: " + SEARCH_NAME);
        /*
        Client client = ClientBuilder.newClient();
        WebTarget addService = client.target("REST_URI").path("rest").path(SEARCH_TWEETS + keywords);
        String jsonString = getResponse(addService);
        System.out.println("GetTweets Response: " + jsonString);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(jsonString);
        JSONObject jsonObject = (JSONObject) obj;
        response.getWriter( ).write(jsonObject.toJSONString());*/
        
        ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(REST_URI);
		String asd = REST_URI + "rest" + SEARCH_TWEETS + keywords+"/REPLIED/" + SEARCH_NAME+ "/1";
		WebResource addService = service.path("rest").path(SEARCH_TWEETS + keywords+"/REPLIED/" + SEARCH_NAME+ "/1");
		
		System.out.println("GetTweets Response: " + getResponse(addService));
		/*JSONParser parser = new JSONParser();
		Object obj = parser.parse(getOutputAsXML(addService));
		JSONObject jsonObject = (JSONObject) obj;
		*/
		//response.getWriter( ).write(jsonObject.toJSONString());
		//response.getWriter().print(jsonObject.toJSONString());
        
    }
	
	private static String getResponse(WebResource service) {
		/*Response response = service.request().buildGet().invoke();
		String y = service.request().accept(MediaType.APPLICATION_JSON).get(String.class).toString();
		String x = response.readEntity(String.class).toString();
		return x;*/
		return service.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString();
	}
	
	private static String getOutputAsXML(WebResource service) {
		return service.accept(MediaType.TEXT_PLAIN).get(String.class);
	}

}
