package com.NetworkAnalysis.srt;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.NetworkAnalysis.rsc.ExcelUtils;
import com.NetworkAnalysis.rsc.GlobalVariablesInterface;
import com.NetworkAnalysis.rsc.Tweet;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        
        Workbook wb;
		try {
			wb = getTweets(searchId,"Tweets");
			
			if (wb != null) {
	            OutputStream os = response.getOutputStream();
	            wb.write(os);
	        }			
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Workbook getTweets(String searchId, String string) throws java.text.ParseException {
		// TODO Auto-generated method stub
		Workbook wb = null;
		List<Tweet> tweets = new ArrayList<Tweet>();
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(REST_URI);
		service.accept(MediaType.APPLICATION_JSON_TYPE);
		String asd = REST_URI + "rest/EnableSearch/" + searchId;
		WebResource addService = service.path("rest").path("/Search/GetTweetBySearch/" + searchId + "/-1");
		String jsonTweets = getOutputAsXML(addService);
		
		JSONParser parser = new JSONParser();
		System.out.println("SSS " + jsonTweets);
		
		try {
			//JSONObject json = (JSONObject) parser.parse(jsonTweets);
			JSONArray jArray = (JSONArray)parser.parse(jsonTweets);
			wb = new XSSFWorkbook();
            XSSFSheet sheetPhases = (XSSFSheet) wb.createSheet("Tweets");
            
            ExcelUtils excelUtils = new ExcelUtils(wb);            
            XSSFRow rowPhs, rowPrd;
               
            //Phases Headers
            int r = 0;
            rowPhs = sheetPhases.createRow(r++);
            //Project Headers
            createHeaderCell(rowPhs, 0, "Id", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 1, "Id Str", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 2, "Screen name", 10000, excelUtils.getStyleHeader1());            
            createHeaderCell(rowPhs, 3, "in_reply_to_user_id", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 4, "in_reply_to_screen_name", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 5, "Text", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 6, "Lang", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 7, "possibly_sensitive", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 8, "truncated", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 9, "hashtags", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 10, "user_mentions", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 11, "usr_id_str", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 12, "usr_id", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 13, "location", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 14, "created_at", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 15, "source", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 16, "retweet_count", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 17, "retweeted", 10000, excelUtils.getStyleHeader1());
            createHeaderCell(rowPhs, 18, "favorite_count", 10000, excelUtils.getStyleHeader1());
            
			for(int i = 0; i < jArray.size(); i++)
			{
			    Object jTempObject = jArray.get(i);	
			    System.out.print("Tweet " + i + ":" + jArray.get(i));
			    JSONObject jsonObject = (JSONObject) jTempObject;
			    Tweet t1 = new Tweet();
			    t1.setId((long)jsonObject.get("id"));
				t1.setId_str(jsonObject.get("id_str").toString());
				t1.setScreen_name(jsonObject.get("screen_name").toString());
				t1.setIn_reply_to_user_id(  jsonObject.get("in_reply_to_user_id")!=null?(long)jsonObject.get("in_reply_to_user_id"):0);
				t1.setIn_reply_to_screen_name(jsonObject.get("in_reply_to_screen_name") != null?jsonObject.get("in_reply_to_screen_name").toString():"");
				t1.setText(jsonObject.get("text").toString());
				t1.setLang(jsonObject.get("lang")!=null?jsonObject.get("lang").toString():"");				
				t1.setPossibly_sensitive(jsonObject.get("possibly_sensitive") != null? ((boolean)jsonObject.get("possibly_sensitive")?true:false):false);
				t1.setTruncated(jsonObject.get("truncated")!=null?((boolean)jsonObject.get("truncated")?true:false):false);
				t1.setHashtags(jsonObject.get("hashtags") != null?jsonObject.get("hashtags").toString():null);
				t1.setUser_mentions(jsonObject.get("user_mentions") != null?jsonObject.get("user_mentions").toString():null);
				t1.setUsr_id_str(jsonObject.get("usr_id_str").toString());
				t1.setUsr_id((long) jsonObject.get("usr_id"));
				t1.setLocation(jsonObject.get("location") != null?jsonObject.get("location").toString():null);
				
				//Checar como convertir la fecha correctamente
				String dateString = jsonObject.get("created_at").toString();
				java.util.Date dateUtil = getTwitterDate(dateString);
								
				t1.setCreated_at(dateUtil);
				
				t1.setSource(jsonObject.get("source").toString());
				t1.setRetweet_count((long) jsonObject.get("retweet_count"));
				t1.setRetweeted((boolean) jsonObject.get("retweeted"));
				t1.setFavorite_count( (long) jsonObject.get("favorite_count"));
				t1.setTweet(jsonObject.toString());
			    //tweets.add(t1);
			    rowPhs = sheetPhases.createRow((i)+1);
                createCell(rowPhs, 0, t1.getId(), null);
                createCell(rowPhs, 1, t1.getId_str(), null);
                createCell(rowPhs, 2, t1.getScreen_name(), null);
                createCell(rowPhs, 3, t1.getIn_reply_to_user_id(), null);
                createCell(rowPhs, 4, t1.getIn_reply_to_screen_name(), null);
                createCell(rowPhs, 5, t1.getText(), null);
                createCell(rowPhs, 6, t1.getLang(), null);
                createCell(rowPhs, 7, t1.getPossibly_sensitive(), null);
                createCell(rowPhs, 8, t1.getTruncated(), null);
                createCell(rowPhs, 9, t1.getHashtags(), null);
                createCell(rowPhs, 10, t1.getUser_mentions(), null);
                createCell(rowPhs, 11, t1.getUsr_id_str(), null);
                createCell(rowPhs, 12, t1.getUsr_id(), null);
                createCell(rowPhs, 13, t1.getLocation(), null);
                createCell(rowPhs, 14, t1.getCreated_at(), excelUtils.getStyleDate());
                createCell(rowPhs, 15, t1.getSource(), null);
                createCell(rowPhs, 16, t1.getRetweet_count(), null);
                createCell(rowPhs, 17, t1.getRetweeted(), null);
                createCell(rowPhs, 18, t1.getFavorite_count(), null);
			    
			}
		} catch (ParseException e) {
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
	
	static void createHeaderCell(XSSFRow row, int index, String header, int width, CellStyle style) {
        XSSFSheet sheet = row.getSheet();
        sheet.setColumnWidth(index, width);
        
        XSSFCell cell = row.createCell(index);
        cell.setCellValue(header);
        cell.setCellStyle(style);
    }
            
    static void createCell(XSSFRow row, int index, Object value, CellStyle style) {
        if (value != null)
        {
            XSSFCell cell = row.createCell(index);

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



