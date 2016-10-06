package com.NetworkAnalysis.srt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.NetworkAnalysis.rsc.GlobalVariablesInterface;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Servlet implementation class RestartStopSearchServlet
 */
@WebServlet("/RestartStopSearchServlet")
//@WebServlet(description = "Start and Stop search", urlPatterns = { "/RestartStopSearchServlet" })
public class RestartStopSearchServlet extends HttpServlet implements GlobalVariablesInterface{
	private static final long serialVersionUID = 1L;
	static final String REST_URI = global.getConnection();
	String idSearch = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RestartStopSearchServlet() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String typeAction = request.getParameter("typeAction");
		idSearch = request.getParameter("idSearch");
		String type = request.getParameter("type");
		String relation = request.getParameter("relation");
		System.out.println("keywords:" + typeAction);
		System.out.println("idSearch: " + idSearch);
		System.out.println("type: " + type);
		String url = "";
		System.out.print(REST_URI + "rest/" );
		if (typeAction.equals("RESTART"))
			url = "Search/RestartRequest/" + idSearch + "/" + type + "/" + relation;
		else
			url = "Search/StopRequest/" + idSearch;
		
		System.out.println(url);
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);		
		WebResource service = client.resource(REST_URI);
		//WebResource addService = service.path("rest").path(url);
		response.getWriter().print((service.path("rest").path(url)).accept(MediaType.TEXT_PLAIN).get(String.class));
		//System.out.println("GetTweets Response: " + getResponse(addService));
		//response.getWriter().print(getOutputAsJSON(addService));

	}

	private static String getResponse(WebResource service) {
		return service.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString();
	}

	private static String getOutputAsJSON(WebResource service) {
		return service.accept(MediaType.TEXT_PLAIN).get(String.class);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
