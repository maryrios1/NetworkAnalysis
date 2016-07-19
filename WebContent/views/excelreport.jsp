<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.List" %>
<%@ page import="com.NetworkAnalysis.rsc.Tweet" %>
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Sample Hello World Example</title>
 </head>
<body>
 <h1>Export to Excel Example</h1>
 <table cellpadding="1"  cellspacing="1" border="1" bordercolor="gray">
  <tr>
   <td>Id</td>
   <td>Id user</td>
   <td>Text</td>
   <td>Hashtags</td>
   <td>Screen name</td>
   <td>Retweet count</td>
   <td>Favorite count</td>
   <td>Created at</td>
   <td>Location</td>
   <td>User Mentions</td>
  </tr>
  <%
   List<Tweet> tweets  = (List<Tweet>)request.getAttribute("tweets");
         if (tweets != null) {
             response.setContentType("application/vnd.ms-excel");
             response.setHeader("Content-Disposition", "attachment; filename="+ "tweetreport.xlsx");
         }
   for(Tweet e: tweets){
  %>
   <tr>
   <td><%=e.getId()%></td>   
   <td><%=e.getUsr_id()%></td>
   <td><%=e.getText()%></td>
   <td><%=e.getHashtags()%></td>
   <td><%=e.getScreen_name()%></td>
   <td><%=e.getRetweet_count()%></td>
   <td><%=e.getFavorite_count()%></td>
   <td><%=e.getCreated_at()%></td>
   <td><%=e.getLocation()%></td>
   <td><%=e.getUser_mentions()%></td>
  </tr>
  <% 
   }
  %>
 </table>
</body>
</html>