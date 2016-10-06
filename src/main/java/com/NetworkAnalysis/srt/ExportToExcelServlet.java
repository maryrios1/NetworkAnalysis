package com.NetworkAnalysis.srt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.json.simple.parser.ParseException;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvResultSetWriter;
import org.supercsv.prefs.CsvPreference;

import com.NetworkAnalysis.rsc.ConnectionRDBMS;
import com.NetworkAnalysis.rsc.Edge;
import com.NetworkAnalysis.rsc.ExcelUtils;
import com.NetworkAnalysis.rsc.GlobalVariablesInterface;
import com.NetworkAnalysis.rsc.Node;
import com.NetworkAnalysis.rsc.Tweet;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


/**
 * Servlet implementation class ExportToExcelServlet
 */
@WebServlet(description = "Export to excel the information requested", urlPatterns = { "/ExportToExcelServlet" })
public class ExportToExcelServlet extends HttpServlet implements GlobalVariablesInterface {
	static final String REST_URI = global.getConnection();
	static final String STREAM_TWEETS= "/StreamTweets/";
	static final String SEARCH_TWEETS = "/SearchTweets/";
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportToExcelServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*
		 * response.getWriter().append("Served at: "
		 * ).append(request.getContextPath());
		 */
		String searchId = request.getParameter("SearchId");
		String tweet = request.getParameter("Tweets");
		String normalNodes = request.getParameter("NormalNodes");
		String hashtagNodes = request.getParameter("HashtagNodes");
		String favorite = request.getParameter("Favorite");
		String retweeted = request.getParameter("Retweeted");
		String limit = request.getParameter("Limit");
		
		if(limit ==null || limit.isEmpty())
			limit="-1";
		
		System.out.println(searchId + " Tweets? " + tweet + " Normal? " + normalNodes + " Hashtag? " + hashtagNodes 
				+ " favorite?" + favorite + " retweeted?" + retweeted + " limit?" + limit);
		
/*
		RequestDispatcher rd = request.getRequestDispatcher("report.jsp");
		rd.forward(request, response);*/
		/*RequestDispatcher rd = request.getRequestDispatcher("views/excelreport.jsp");
		rd.forward(request, response);*/
		//response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		//response.setContentType("text/csv");
		//response.setHeader("Content-disposition","inline; filename="+ "Tweets_SearchId_" + searchId + ".csv");
		response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"allfiles_" + searchId + ".zip\"");

        Workbook wb;
       
        OutputStream os = response.getOutputStream();
        ZipOutputStream zos = new ZipOutputStream(os);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
		try {
			if(tweet.equals("true")){
				zos.putNextEntry(new ZipEntry("Tweets_" + searchId + ".xlsx"));
				//FileWriter sWriter = getTweets2(Integer.parseInt(searchId),"Tweets");
				//OutputStream f = new FileOutputStream(global.getFILE_PATH() + "Tweets_" + searchId + ".csv");
				//f.write(b, off, len);
				//byte[] bytes = sWriter.toString().getBytes("UTF-8");
				
				//bos.write(bytes);
				
				wb = getTweets(Integer.parseInt(searchId),"Tweets");
				wb.write(bos);
				bos.writeTo(zos);
	    		zos.closeEntry();
			}
			
			if(favorite.equals("true")){
				zos.putNextEntry(new ZipEntry("PlusFavorite_" + searchId + ".xlsx"));
				bos = new ByteArrayOutputStream();
				wb = getPopularTweets(Integer.parseInt(searchId),"PlusFavorite",Integer.parseInt(limit));
				wb.write(bos);
				bos.writeTo(zos);
	    		zos.closeEntry();
			}
			
			if(retweeted.equals("true")){
				zos.putNextEntry(new ZipEntry("PlusRetweeted_" + searchId + ".xlsx"));
				bos = new ByteArrayOutputStream();
				wb = getPopularTweets(Integer.parseInt(searchId),"PlusRetweeted",Integer.parseInt(limit));
				wb.write(bos);
				bos.writeTo(zos);
	    		zos.closeEntry();
			}
			
			if(normalNodes.equals("true")){
	    		//Export relations
				zos.putNextEntry(new ZipEntry("Edges_" + searchId + ".xlsx"));
	    		bos = new ByteArrayOutputStream();
				wb = getEdges(Integer.parseInt(searchId),"Tweet");
				wb.write(bos);
				bos.writeTo(zos);
	    		zos.closeEntry();
	    		
    			//Export nodes
	    		zos.putNextEntry(new ZipEntry("Nodes_" + searchId + ".xlsx"));//test
	    		bos = new ByteArrayOutputStream();
				wb = getNodes(Integer.parseInt(searchId),"Tweet");
				wb.write(bos);
				bos.writeTo(zos);
	    		zos.closeEntry();
			}
			
			if(hashtagNodes.equals("true")){
	    		//Export relations
				zos.putNextEntry(new ZipEntry("EdgesHashtag_" + searchId + ".xlsx"));
	    		bos = new ByteArrayOutputStream();
				wb = getEdges(Integer.parseInt(searchId),"Hashtag");
				wb.write(bos);
				bos.writeTo(zos);
	    		zos.closeEntry();
	    		
    			//Export nodes
	    		zos.putNextEntry(new ZipEntry("NodesHashtag_" + searchId + ".xlsx"));//test
	    		bos = new ByteArrayOutputStream();
				wb = getNodes(Integer.parseInt(searchId),"Hashtag");
				wb.write(bos);
				bos.writeTo(zos);
	    		zos.closeEntry();
			}
			
	        zos.close();
			
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private FileWriter getTweets2(int searchId, String type) throws java.text.ParseException {
		// TODO Auto-generated method stub		
		
		ConnectionRDBMS con = new ConnectionRDBMS(); 
//		ArrayList<Tweet> listTweets =  con.getTweetBySearch(searchId, -1);
		ResultSet rs =  con.getTweetBySearch(searchId, -1);
		try
	    {
			 //StringWriter outFile = new StringWriter();
			File f =  new File(global.getFILE_PATH() + "Tweet_" + searchId + ".csv");
			FileWriter outFile = new FileWriter(f);
		    // setup header for the file and processors. Notice the match between the header and the attributes of the
		    // objects to write. The rules are that
		    // - if optional "parent orders" are absent, write -1
		    // - and optional user comments absent are written as ""
		    String[] header = new String[] { "Id Str", "Screen name", "in_reply_to_user_id", "in_reply_to_screen_name","Text",
		    		"Lang","possibly_sensitive","truncated","hashtags","user_mentions","usr_id_str","usr_id","location",
		    		"created_at","source","retweet_count","retweeted","favorite_count","retweeted_user_id",
		    		"retweeted_user_screen_name","mentioned_users_ids","mentioned_users_screen_names"};
		    CellProcessor[] Processing = new CellProcessor[] { null, null, null, null,null,null, null, null, null,null,
		    		null, null, null, null,null,null, null, null, null,null,null,null,null};

		    // write the partial data
			CsvResultSetWriter writer = new 	CsvResultSetWriter(outFile, CsvPreference.EXCEL_PREFERENCE);
		    //writer.writeHeader(header);
		    writer.write(rs, Processing);
		    
		    writer.close();

		    // show output
		    //System.out.println(outFile.toString());
		    return outFile;
						
	    }
	    catch(Exception ex)
		{
	    	System.out.println("ERROR:" + ex.getMessage());
	    	return null;
	    }
		
	}
	
	private Workbook getPopularTweets(int searchId, String type, int limit) throws java.text.ParseException {
		// TODO Auto-generated method stub
				SXSSFWorkbook wb = null;
				ConnectionRDBMS con = new ConnectionRDBMS(); 
//				ArrayList<Tweet> listTweets =  con.getTweetBySearch(searchId, -1);
				String orderBy="";
				switch(type){
				case "PlusRetweeted":
					orderBy = "retweet_count";
					break;
				case "PlusFavorite":
					orderBy = "favorite_count";
					break;
				}
				
				ResultSet rs =  con.getPopularTweetBySearch(searchId,orderBy, limit);
				
				try {
					wb = new SXSSFWorkbook(1000);
					
		            SXSSFSheet sheetPhases = (SXSSFSheet) wb.createSheet(type);
		            
		            ExcelUtils excelUtils = new ExcelUtils(wb);            
		            SXSSFRow rowPhs, rowEdges,rowNodes;
		               
		            //Tweets Headers
		            int r = 0;
		            rowPhs = sheetPhases.createRow(r++);

		            createHeaderCell(rowPhs, 0, "Id Str", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 1, "Screen name", 10000, excelUtils.getStyleHeader1());            
		            createHeaderCell(rowPhs, 2, "in_reply_to_user_id", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 3, "in_reply_to_screen_name", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 4, "Text", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 5, "Lang", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 6, "possibly_sensitive", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 7, "truncated", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 8, "hashtags", 10000, excelUtils.getStyleHeader1());

		            createHeaderCell(rowPhs, 9, "usr_id_str", 10000, excelUtils.getStyleHeader1());

		            createHeaderCell(rowPhs, 10, "location", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 11, "created_at", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 12, "source", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 13, "retweet_count", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 14, "retweeted", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 15, "favorite_count", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 16, "retweeted_user_id", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 17, "retweeted_user_screen_name", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 18, "mentioned_users_ids", 10000, excelUtils.getStyleHeader1());
		            createHeaderCell(rowPhs, 19, "mentioned_users_screen_names", 10000, excelUtils.getStyleHeader1());
		            
		            int i=0;
		            while (rs.next()) {
						Tweet tweet = new Tweet();
						
						tweet.setId_str(rs.getString("id_str"));
						tweet.setScreen_name(rs.getString("screen_name"));
						tweet.setIn_reply_to_user_id(rs.getLong("in_reply_to_user_id"));
						tweet.setIn_reply_to_screen_name(rs.getString("in_reply_to_screen_name"));
						tweet.setText(rs.getString("text"));
						tweet.setLang(rs.getString("lang"));
						tweet.setPossibly_sensitive(rs.getBoolean("possibly_sensitive"));
						tweet.setTruncated(rs.getBoolean("truncated"));
						tweet.setHashtags(rs.getString("hashtags"));

						tweet.setUsr_id_str(rs.getString("usr_id_str"));
						
						tweet.setLocation(rs.getString("location"));
						
						String dateString = rs.getTimestamp("created_at").toString();
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						java.util.Date date = dateFormat.parse(dateString);
						//System.out.println(dateFormat.format(date));
						tweet.setCreated_at(date);
						tweet.setSource(rs.getString("Source"));
						tweet.setRetweet_count(rs.getLong("retweet_count"));
						tweet.setRetweeted(rs.getBoolean("retweeted"));
						tweet.setFavorite_count(rs.getLong("favorite_count"));

						tweet.setRetweeted_user_id(rs.getLong("retweeted_user_id"));
						tweet.setRetweeted_user_screen_name(rs.getString("retweeted_user_screen_name"));
						tweet.setMentioned_users_ids(rs.getString("mentioned_users_ids"));
						tweet.setMentioned_users_screen_names(rs.getString("mentioned_users_screen_names"));
						
						rowPhs = sheetPhases.createRow((i)+1);
		                //createCell(rowPhs, 0, tweet.getId(), null);
		                createCell(rowPhs, 0, tweet.getId_str(), null);
		                createCell(rowPhs, 1, tweet.getScreen_name(), null);
		                createCell(rowPhs, 2, tweet.getIn_reply_to_user_id(), null);
		                createCell(rowPhs, 3, tweet.getIn_reply_to_screen_name(), null);
		                createCell(rowPhs, 4, tweet.getText(), null);
		                createCell(rowPhs, 5, tweet.getLang(), null);
		                createCell(rowPhs, 6, tweet.getPossibly_sensitive(), null);
		                createCell(rowPhs, 7, tweet.getTruncated(), null);
		                createCell(rowPhs, 8, tweet.getHashtags(), null);
		               
		                createCell(rowPhs, 9, tweet.getUsr_id_str(), null);
		                
		                createCell(rowPhs, 10, tweet.getLocation(), null);
		                createCell(rowPhs, 11, tweet.getCreated_at(), excelUtils.getStyleDate());
		                createCell(rowPhs, 12, tweet.getSource(), null);
		                createCell(rowPhs, 13, tweet.getRetweet_count(), null);
		                createCell(rowPhs, 14, tweet.getRetweeted(), null);
		                createCell(rowPhs, 15, tweet.getFavorite_count(), null);
		                createCell(rowPhs, 16, tweet.getRetweeted_user_id(), null);
		                createCell(rowPhs, 17, tweet.getRetweeted_user_screen_name(), null);
		                createCell(rowPhs, 18, tweet.getMentioned_users_ids(), null);
		                createCell(rowPhs, 19, tweet.getMentioned_users_screen_names(), null);
		                i++;
					}                  
		            
				} catch (Exception e) {//ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				/*} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();*/
				}
				
				return wb;
	}
	
	private Workbook getTweets(int searchId, String type) throws java.text.ParseException {
		// TODO Auto-generated method stub
		SXSSFWorkbook wb = null;
		ConnectionRDBMS con = new ConnectionRDBMS(); 
//		ArrayList<Tweet> listTweets =  con.getTweetBySearch(searchId, -1);
		ResultSet rs =  con.getTweetBySearch(searchId, -1);
		/*ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(REST_URI);
		service.accept(MediaType.APPLICATION_OCTET_STREAM);
		String asd = REST_URI + "rest/EnableSearch/" + searchId;
		WebResource addService = service.path("rest").path("/Search/GetTweetBySearch/" + searchId + "/-1");
		String jsonTweets = getOutputAsXML(addService);
		
		JSONParser parser = new JSONParser();
		System.out.println("SSS " + jsonTweets);*/
		
		try {
			//JSONObject json = (JSONObject) parser.parse(jsonTweets);
			//JSONArray jArray = (JSONArray)parser.parse(jsonTweets);
			wb = new SXSSFWorkbook(1000);
			
            SXSSFSheet sheetPhases = (SXSSFSheet) wb.createSheet("Tweets");
            
            ExcelUtils excelUtils = new ExcelUtils(wb);            
            SXSSFRow rowPhs, rowEdges,rowNodes;
               
            //Tweets Headers
            int r = 0;
            rowPhs = sheetPhases.createRow(r++);

            //createHeaderCell(rowPhs, 0, "Id", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 0, "Id Str", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 1, "Screen name", 10000, excelUtils.getStyleHeader1());            
            createHeaderCell(rowPhs, 2, "in_reply_to_user_id", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 3, "in_reply_to_screen_name", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 4, "Text", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 5, "Lang", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 6, "possibly_sensitive", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 7, "truncated", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 8, "hashtags", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 9, "user_mentions", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 10, "usr_id_str", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 11, "usr_id", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 12, "location", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 13, "created_at", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 14, "source", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 15, "retweet_count", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 16, "retweeted", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 17, "favorite_count", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 18, "retweeted_user_id", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 19, "retweeted_user_screen_name", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 20, "mentioned_users_ids", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 21, "mentioned_users_screen_names", 10000, excelUtils.getStyleHeader1());
            
            int i=0;
            while (rs.next()) {
				Tweet tweet = new Tweet();
				tweet.setId(rs.getLong("id"));
				tweet.setId_str(rs.getString("id_str"));
				tweet.setScreen_name(rs.getString("screen_name"));
				tweet.setIn_reply_to_user_id(rs.getLong("in_reply_to_user_id"));
				tweet.setIn_reply_to_screen_name(rs.getString("in_reply_to_screen_name"));
				tweet.setText(rs.getString("text"));
				tweet.setLang(rs.getString("lang"));
				tweet.setPossibly_sensitive(rs.getBoolean("possibly_sensitive"));
				tweet.setTruncated(rs.getBoolean("truncated"));
				tweet.setHashtags(rs.getString("hashtags"));
				tweet.setUser_mentions(rs.getString("user_mentions"));
				tweet.setUsr_id_str(rs.getString("usr_id_str"));
				tweet.setUsr_id(rs.getLong("usr_id"));
				tweet.setLocation(rs.getString("location"));
				//System.out.println("date: " + rs.getTimestamp("created_at").toString());
				String dateString = rs.getTimestamp("created_at").toString();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date = dateFormat.parse(dateString);
				//System.out.println(dateFormat.format(date));
				tweet.setCreated_at(date);
				tweet.setSource(rs.getString("Source"));
				tweet.setRetweet_count(rs.getLong("retweet_count"));
				tweet.setRetweeted(rs.getBoolean("retweeted"));
				tweet.setFavorite_count(rs.getLong("favorite_count"));
				//tweet.setTweet(rs.getString("tweet"));
				//tweet.setIdsearch(rs.getInt("idsearch"));
				//retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names
				tweet.setRetweeted_user_id(rs.getLong("retweeted_user_id"));
				tweet.setRetweeted_user_screen_name(rs.getString("retweeted_user_screen_name"));
				tweet.setMentioned_users_ids(rs.getString("mentioned_users_ids"));
				tweet.setMentioned_users_screen_names(rs.getString("mentioned_users_screen_names"));
				
				rowPhs = sheetPhases.createRow((i)+1);
                //createCell(rowPhs, 0, tweet.getId(), null);
                createCell(rowPhs, 0, tweet.getId_str(), null);
                createCell(rowPhs, 1, tweet.getScreen_name(), null);
                createCell(rowPhs, 2, tweet.getIn_reply_to_user_id(), null);
                createCell(rowPhs, 3, tweet.getIn_reply_to_screen_name(), null);
                createCell(rowPhs, 4, tweet.getText(), null);
                createCell(rowPhs, 5, tweet.getLang(), null);
                createCell(rowPhs, 6, tweet.getPossibly_sensitive(), null);
                createCell(rowPhs, 7, tweet.getTruncated(), null);
                createCell(rowPhs, 8, tweet.getHashtags(), null);
                createCell(rowPhs, 9, tweet.getUser_mentions(), null);
                createCell(rowPhs, 10, tweet.getUsr_id_str(), null);
                createCell(rowPhs, 11, tweet.getUsr_id(), null);
                createCell(rowPhs, 12, tweet.getLocation(), null);
                createCell(rowPhs, 13, tweet.getCreated_at(), excelUtils.getStyleDate());
                createCell(rowPhs, 14, tweet.getSource(), null);
                createCell(rowPhs, 15, tweet.getRetweet_count(), null);
                createCell(rowPhs, 16, tweet.getRetweeted(), null);
                createCell(rowPhs, 17, tweet.getFavorite_count(), null);
                createCell(rowPhs, 18, tweet.getRetweeted_user_id(), null);
                createCell(rowPhs, 19, tweet.getRetweeted_user_screen_name(), null);
                createCell(rowPhs, 20, tweet.getMentioned_users_ids(), null);
                createCell(rowPhs, 21, tweet.getMentioned_users_screen_names(), null);
                i++;
			}                  
            
		} catch (Exception e) {//ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		/*} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();*/
		}
		
		return wb;
		
	}

	private File getDynamicGDF(int searchId,String type){
		return null;
	}
	
	private Workbook getEdges(int searchId, String type) throws java.text.ParseException {
		// TODO Auto-generated method stub
		SXSSFWorkbook wb = null;
		ConnectionRDBMS con = new ConnectionRDBMS(); 
		
		try {
			
			wb = new SXSSFWorkbook(1000);
            
            ExcelUtils excelUtils = new ExcelUtils(wb);            
            SXSSFRow rowPhs, rowEdges,rowNodes;
            int r,i = 0;
                                  
            System.out.println("Excel add edges");
            //Edges or relationships
            SXSSFSheet sheetEdges = (SXSSFSheet) wb.createSheet("Edges");
            ResultSet rs;
            rs =  con.getEdgesBySearch(searchId, -1,type);
            //Edges Headers
            r = 0;
            rowEdges = sheetEdges.createRow(r++);

            createHeaderCell(rowEdges, 0, "Source", 10000, excelUtils.getStyleHeader1());
            //createHeaderCell(rowEdges, 1, "Source Name", 10000, excelUtils.getStyleHeader1());            
            createHeaderCell(rowEdges, 1, "Target", 10000, excelUtils.getStyleHeader1());
            //createHeaderCell(rowEdges, 3, "Target Name", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowEdges, 2, "Weight", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowEdges, 3, "Label", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowEdges, 4, "Time Interval", 10000, excelUtils.getStyleHeader1());
            
            i=0;
            while (rs.next()) {
				Edge edge = new Edge();
				edge.nodeSource.setId(rs.getString("ID1"));
				//edge.nodeSource.setLabel(rs.getString("sourcename"));
				edge.nodeTarget.setId(rs.getString("ID2"));
				//edge.nodeTarget.setLabel(rs.getString("targetname"));
				edge.setWeight(rs.getInt("weight"));
				edge.setRelation(rs.getString("name"));
				
				String dateString = rs.getTimestamp("timeinterval").toString();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date = dateFormat.parse(dateString);
				edge.setTimeinterval(date);
				
				rowEdges = sheetEdges.createRow((i)+1);
                createCell(rowEdges, 0, edge.nodeSource.getId(), null);
                //createCell(rowEdges, 1, edge.nodeSource.getLabel(), null);
                createCell(rowEdges, 1, edge.nodeTarget.getId(), null);
                //createCell(rowEdges, 3, edge.nodeTarget.getLabel(), null);
                createCell(rowEdges, 2, edge.getWeight(), null);
                createCell(rowEdges, 3, edge.getRelation(), null);
                createCell(rowEdges, 4, edge.getTimeinterval(), excelUtils.getStyleDate());
                i++;
			}
            /*
            System.out.println("Excel add nodes");
            //Edges or relationships
            SXSSFSheet sheetNodes = (SXSSFSheet) wb.createSheet("Nodes");
            rs=null;
            rs =  con.getNodesBySearch(searchId, -1,type);
            //Edges Headers
            r = 0;
            rowNodes = sheetNodes.createRow(r++);

            createHeaderCell(rowNodes, 0, "ID", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowNodes, 1, "Label", 10000, excelUtils.getStyleHeader1());            
            createHeaderCell(rowNodes, 2, "Url", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowNodes, 3, "Count", 10000, excelUtils.getStyleHeader1());
            
            i=0;
            while (rs.next()) {
				Node node = new Node();
				node.setId(rs.getString("id"));
				node.setLabel(rs.getString("label"));
				node.setCount(rs.getInt("count"));
				node.setUrl(rs.getString("url"));
				
				rowNodes = sheetNodes.createRow((i)+1);
                createCell(rowNodes, 0, node.getId(), null);
                createCell(rowNodes, 1, node.getLabel(), null);
                createCell(rowNodes, 2, node.getUrl(), null);
                createCell(rowNodes, 3, node.getCount(), null);
                i++;
			}*/
            
		} catch (Exception e) {//ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		/*} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();*/
		}
		
		return wb;
		
	}
	
	private Workbook getNodes(int searchId, String type) throws java.text.ParseException {
		// TODO Auto-generated method stub
		SXSSFWorkbook wb = null;
		ConnectionRDBMS con = new ConnectionRDBMS(); 
		
		try {
			
			wb = new SXSSFWorkbook(1000);
            
            ExcelUtils excelUtils = new ExcelUtils(wb);            
            SXSSFRow rowNodes;
            int r,i = 0;
                                                        
            System.out.println("Excel add nodes");
            //Edges or relationships
            SXSSFSheet sheetNodes = (SXSSFSheet) wb.createSheet("Nodes");
            ResultSet rs=null;
            rs =  con.getNodesBySearch(searchId, -1,type);
            //Edges Headers
            r = 0;
            rowNodes = sheetNodes.createRow(r++);

            createHeaderCell(rowNodes, 0, "ID", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowNodes, 1, "Label", 10000, excelUtils.getStyleHeader1());            
            createHeaderCell(rowNodes, 2, "Url", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowNodes, 3, "Count", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowNodes, 4, "Time Interval", 10000, excelUtils.getStyleHeader1());

            i=0;
            while (rs.next()) {
				Node node = new Node();
				node.setId(rs.getString("id"));
				node.setLabel(rs.getString("label"));
				node.setCount(rs.getInt("count"));
				node.setUrl(rs.getString("url"));	
				
				String dateString = rs.getTimestamp("timeinterval").toString();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date = dateFormat.parse(dateString);
				node.setTimeinterval(date);
				
				rowNodes = sheetNodes.createRow((i)+1);
                createCell(rowNodes, 0, node.getId(), null);
                createCell(rowNodes, 1, node.getLabel(), null);
                createCell(rowNodes, 2, node.getUrl(), null);
                createCell(rowNodes, 3, node.getCount(), null);
                createCell(rowNodes, 4, node.getTimeinterval(), excelUtils.getStyleDate());
                i++;
			}
            
		} catch (Exception e) {//ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		/*} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();*/
		}
		
		return wb;
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		 doGet(request, response); 
		/*List<Tweet> tweets = new ArrayList<Tweet>();
		Tweet t1 = new Tweet();
		t1.setId(1);
		t1.setUsr_id(1);	
		t1.setText("Tweet2");
		tweets.add(t1);
		tweets.add(t1);
		tweets.add(t1);

		request.setAttribute("tweets", tweets);

		RequestDispatcher rd = request.getRequestDispatcher("views/excelreport.jsp");
		rd.forward(request, response);*/

	}

	private static String getResponse(WebResource service) {

		return service.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class).toString();
	}
	
	private static String getOutputAsXML(WebResource service) {
		return service.accept(MediaType.APPLICATION_JSON).get(String.class);
	}
	
	public static java.util.Date getTwitterDate(String date) throws ParseException, java.text.ParseException {

		//final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		System.out.println("Date: " + date);
		//yyyy-mm-dd hh:mm:ss
		//final String TWITTER = "yyyy-mm-dd HH:mm:ss";
		final String TWITTER = "MMM d, yyyy HH:mm:ss a";
		SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
		sf.setLenient(true);
		return sf.parse(date);
	}
	
	static void createHeaderCell(SXSSFRow row, int index, String header, int width, CellStyle style) {
        SXSSFSheet sheet = row.getSheet();
        sheet.setColumnWidth(index, width);
        
        SXSSFCell cell = row.createCell(index);
        cell.setCellValue(header);
        cell.setCellStyle(style);
    }
            
    static void createCell(SXSSFRow row, int index, Object value, CellStyle style) {
        if (value != null)
        {
            SXSSFCell cell = row.createCell(index);

            if (style != null) {
                cell.setCellStyle(style);
            }
            
            if (value instanceof String) 
            {
                cell.setCellValue((String)value);
            }
            else if (value instanceof Double)
            {
                cell.setCellValue((Double)value);
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            }
            else if (value instanceof Integer)
            {
                cell.setCellValue(((Integer)value).doubleValue());
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            }
            else if (value instanceof Date)
            {
                cell.setCellValue(((Date)value));
            }
            else if (value instanceof Long)
            {
                cell.setCellValue(((Long)value));
            }
            else if (value instanceof Boolean)
            {
                cell.setCellValue(((Boolean)value));
            }
        }
    }
    
    
}




