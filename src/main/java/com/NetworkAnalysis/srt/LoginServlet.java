/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.NetworkAnalysis.srt;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.NetworkAnalysis.rsc.ConnectionRDBMS;
import com.NetworkAnalysis.rsc.Message;
import com.NetworkAnalysis.rsc.User;
import com.google.gson.Gson;

/**
 *
 * @author Maestria
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	Gson gson =  new Gson();
    	try{
	    	String userString = request.getParameter("user");
	    	System.out.println(userString);
	    	String pwd = request.getParameter("password");
	    	System.out.println(pwd);
	        ConnectionRDBMS connect = new ConnectionRDBMS();
	        User user = connect.checkLogin(userString, pwd);
	        System.out.println(user.getMessage().toString());
	        if(user.getMessage().getStatus().equals("OK")){
	        	 System.out.println("entro");
	        	HttpSession session = request.getSession(true);
	        	session.setAttribute("user", user);
	        	//response.sendRedirect("index.jsp");
	        }
	        
	        
	        response.setContentType("application/json");
	        response.getWriter().print(gson.toJson(user.getMessage()));
    	}
    	catch(Exception ex){
    		System.out.println("ERROR:" + ex.getMessage());
    		Message msg = new Message();
    		msg.setStatus("ERROR");
			msg.setMessage(ex.getMessage());
			response.getWriter().print(gson.toJson(msg));
    	}
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
