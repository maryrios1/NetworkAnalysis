package com.NetworkAnalysis.srt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.NetworkAnalysis.rsc.ConnectionRDBMS;
import com.NetworkAnalysis.rsc.Search;
import com.NetworkAnalysis.rsc.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class GetUserSearchesServlet
 */
@WebServlet("/GetUserSearchesServlet")
public class GetUserSearchesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetUserSearchesServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ArrayList searchesList = new ArrayList();
		try {
			HttpSession sessionUser = request.getSession(false);// don't create if it doesn't exist
			if(sessionUser != null && sessionUser.getAttribute("user")!=null) {
				User user = (User)sessionUser.getAttribute("user");
				if (user == null || user.getName()== null )
					response.sendRedirect("Login.jsp");
				
				ConnectionRDBMS connect = new ConnectionRDBMS();
				searchesList = connect.getAllSearches(user.getIDUser(),-1);
			}
			else
			{
				response.sendRedirect("Login.jsp");	
			}
			
		} catch (Exception ex) {
			System.out.println("ERROR" + ex.getMessage());
		}

		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(searchesList, new TypeToken<List<Search>>() {
		}.getType());
		JsonArray jsonArray = element.getAsJsonArray();
		//System.out.println(jsonArray.toString());
		response.setContentType("application/json");
		response.getWriter().print(jsonArray);

		// request.setAttribute("searchesList", searchesList);

		// RequestDispatcher view = request.getRequestDispatcher("index.jsp");
		// view.forward(request, response);

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
