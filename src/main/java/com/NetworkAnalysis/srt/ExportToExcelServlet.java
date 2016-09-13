package com.NetworkAnalysis.srt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
		
/*
		RequestDispatcher rd = request.getRequestDispatcher("report.jsp");
		rd.forward(request, response);*/
		/*RequestDispatcher rd = request.getRequestDispatcher("views/excelreport.jsp");
		rd.forward(request, response);*/
		//response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		//response.setContentType("text/csv");
		//response.setHeader("Content-disposition","inline; filename="+ "Tweets_SearchId_" + searchId + ".csv");
		response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"allfiles.zip\"");

        Workbook wb;
       
        OutputStream os = response.getOutputStream();
        ZipOutputStream zos = new ZipOutputStream(os);
		zos.putNextEntry(new ZipEntry("Tweets.csv"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        		/*workbook.write(bos);
        		
        		bos.writeTo(zos);
        		zos.closeEntry();*/
        
		try {
			wb = getTweets(Integer.parseInt(searchId),"Tweets");
			wb.write(bos);
			bos.writeTo(zos);
    		zos.closeEntry();
    		
			/*if (wb != null) {
	            OutputStream os = response.getOutputStream();
	            wb.write(os);
	        }*/
    		zos.putNextEntry(new ZipEntry("Edges.csv"));
    		bos = new ByteArrayOutputStream();
			wb = getEdges(Integer.parseInt(searchId),"Edges");
			wb.write(bos);
			bos.writeTo(zos);
    		zos.closeEntry();
			/*
			if (wb != null) {
	            OutputStream os = response.getOutputStream();
	            wb.write(os);
	            
	        }
			*/
    		zos.putNextEntry(new ZipEntry("Nodes.csv"));
    		bos = new ByteArrayOutputStream();
			wb = getNodes(Integer.parseInt(searchId),"Nodes");
			wb.write(bos);
			bos.writeTo(zos);
    		zos.closeEntry();
			/*
			if (wb != null) {
	            OutputStream os = response.getOutputStream();
	            wb.write(os);
	        }
	        */
	        
	        zos.close();
			
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				tweet.setTweet(rs.getString("tweet"));
				tweet.setIdsearch(rs.getInt("idsearch"));
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
            rs =  con.getEdgesBySearch(searchId, -1);
            //Edges Headers
            r = 0;
            rowEdges = sheetEdges.createRow(r++);

            createHeaderCell(rowEdges, 0, "Source", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowEdges, 1, "Source Name", 10000, excelUtils.getStyleHeader1());            
            createHeaderCell(rowEdges, 2, "Target", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowEdges, 3, "Target Name", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowEdges, 4, "Weight", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowEdges, 5, "Label", 10000, excelUtils.getStyleHeader1());
            
            i=0;
            while (rs.next()) {
				Edge edge = new Edge();
				edge.nodeSource.setId(rs.getString("ID1"));
				edge.nodeSource.setLabel(rs.getString("sourcename"));
				edge.nodeTarget.setId(rs.getString("ID2"));
				edge.nodeTarget.setLabel(rs.getString("targetname"));
				edge.setWeight(rs.getInt("weight"));
				edge.setRelation(rs.getString("name"));
				
				rowEdges = sheetEdges.createRow((i)+1);
                createCell(rowEdges, 0, edge.nodeSource.getId(), null);
                createCell(rowEdges, 1, edge.nodeSource.getLabel(), null);
                createCell(rowEdges, 2, edge.nodeTarget.getId(), null);
                createCell(rowEdges, 3, edge.nodeTarget.getLabel(), null);
                createCell(rowEdges, 4, edge.getWeight(), null);
                createCell(rowEdges, 5, edge.getRelation(), null);
                i++;
			}
            
            System.out.println("Excel add nodes");
            //Edges or relationships
            SXSSFSheet sheetNodes = (SXSSFSheet) wb.createSheet("Nodes");
            rs=null;
            rs =  con.getNodesBySearch(searchId, -1);
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
            rs =  con.getNodesBySearch(searchId, -1);
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




