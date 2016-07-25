package com.NetworkAnalysis.rsc;

import java.net.Proxy.Type;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.sql.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConnectionRDBMS {

	private static String USER = "cgc_user";
	private static String PASSWORD = "cgc_D3Access82)";
	private static String DB = "TwitterDB";
	private static String HOST = "cgcapp.coh6dv3qg5l4.us-west-2.rds.amazonaws.com";
	private static String PORT = "1531";
	private static Connection connect = null;
	static PreparedStatement preparedStatement = null;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ConnectionRDBMS() {

	}

	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DB + "?" + "user=" + USER
					+ "&password=" + PASSWORD + "&useUnicode=true");// &characterEncoding=UTF-8
		} catch (Exception ex) {
			System.out.println("error in connect: " + ex.getMessage());
		}
	}

	/***
	 * Insert new Search
	 * 
	 * @param searchname
	 *            Search Name
	 * @param iduser
	 *            User ID of the new search
	 * @param type
	 *            Type of search STREAM/SEARCH
	 * @param searchwords
	 *            Words to search
	 * @return Search
	 * @throws Exception
	 */
	public Search insert(String searchname, int iduser, SearchType type, String searchwords) throws Exception {
		return insertRecordSearch(searchname, iduser, type, searchwords);
	}

	/***
	 * Update the status of the search
	 * 
	 * @param idSearch
	 *            Search ID
	 * @param action
	 *            Action RESTART/STOP
	 * @return
	 * @throws Exception
	 */
	public Search update(int idSearch, SearchType search, Status action) throws Exception {
		return updateRecordSearch(idSearch, search, action);
	}

	/***
	 * Insert edge,nodes and tweet into the db according the relationship and
	 * type
	 * 
	 * @param text
	 *            Json tweet
	 * @param type
	 *            Node, edge or Tweet
	 * @param relation
	 *            Replied, Retweeted
	 * @param idsearch
	 *            id of the search
	 * @return
	 * @throws Exception
	 */
	public int insert(String text, TableType type, RelationshipSearch relation, int idsearch) throws Exception {
		// DB connection setup

		int status = -1;
		try {
			connect();
			switch (type) {
			case NODE:
				insertNodeDB(text, relation, idsearch);
				break;
			case EDGE:
				insertEdgeDB(text, relation, idsearch);
				break;
			case TWEET:
				insertTweetDB(text, relation, idsearch);
				break;
			default:
				break;

			}

		} catch (Exception e) {
			System.out.println("error1: " + e.getMessage() + " status: " + status);
			System.out.println(e.getStackTrace().toString());
			throw e;

		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				System.out.println("error2: " + e.getMessage());
			}
		}
		return status;
	}

	// region Tweet
	/***
	 * Insert tweet into the DB
	 * 
	 * @param text
	 *            json tweet
	 * @param relation
	 *            relation type
	 * @param search
	 *            id search
	 * @throws Exception
	 */
	private void insertTweetDB(String text, RelationshipSearch relation, int idsearch) throws Exception {
		int status = -1;
		JSONParser parser = new JSONParser();
		Object obj = null;
		JSONObject jsonObject;
		String userString = "";
		JSONObject jOUser = null;
		JSONObject jOTemp = null;
		String tempString = "";

		try {
			obj = parser.parse(text);
			jsonObject = (JSONObject) obj;

			preparedStatement = connect.prepareStatement(
					"INSERT INTO  tweet (id,id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
							+ "truncated,hashtags,user_mentions,usr_id_str,usr_id,location,created_at,source,retweet_count,retweeted,favorite_count,tweet,idsearch) "
							+ "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ) ;");

			userString = jsonObject.get("user").toString();
			jOUser = (JSONObject) parser.parse(userString);

			preparedStatement.setLong(1, (long) jsonObject.get("id"));
			preparedStatement.setString(2, jsonObject.get("id_str").toString());
			preparedStatement.setString(3, jOUser.get("screen_name").toString());

			if (jsonObject.get("in_reply_to_user_id") == null)
				preparedStatement.setNull(4, 0);
			else
				preparedStatement.setLong(4, (Long) jsonObject.get("in_reply_to_user_id"));

			if (jsonObject.get("in_reply_to_screen_name") == null)
				preparedStatement.setNull(5, 0);
			else
				preparedStatement.setString(5, jsonObject.get("in_reply_to_screen_name").toString());

			preparedStatement.setString(6, jsonObject.get("text").toString());
			preparedStatement.setString(7, jsonObject.get("lang").toString());

			if (jsonObject.get("possibly_sensitive") == null)
				preparedStatement.setBoolean(8, false);
			else
				preparedStatement.setBoolean(8, (boolean) jsonObject.get("possibly_sensitive"));

			preparedStatement.setBoolean(9, (boolean) jsonObject.get("truncated"));

			// Get the hashtags
			tempString = jsonObject.get("entities").toString();
			jOTemp = (JSONObject) parser.parse(tempString);

			if (jOTemp.get("hashtags") == null)
				preparedStatement.setNull(10, 0);
			else {
				JSONArray jArray = (JSONArray) jOTemp.get("hashtags");
				String tempHash = "";
				for (int i = 0; i < jArray.size(); i++)
					tempHash += ((JSONObject) jArray.get(i)).get("text").toString() + ",";
				if (jArray.size() > 0) {
					tempHash = tempHash.substring(0, tempHash.length() - 1);
					preparedStatement.setString(10, tempHash);
				} else
					preparedStatement.setNull(10, 0);
			}

			// entities->user_mentiones->id,id_str,screen_name
			if (jsonObject.get("user_mentions") == null)
				preparedStatement.setNull(11, 0);
			else
				preparedStatement.setString(11, jsonObject.get("user_mentions").toString());

			preparedStatement.setString(12, jOUser.get("id_str").toString());
			preparedStatement.setLong(13, (Long) jOUser.get("id"));

			if (jOUser.get("location") == null)
				preparedStatement.setNull(14, 0);
			else
				preparedStatement.setString(14, jOUser.get("location").toString());

			String dateString = jsonObject.get("created_at").toString();
			java.util.Date dateUtil = getTwitterDate(dateString);
			java.sql.Timestamp dateSql = new java.sql.Timestamp(dateUtil.getTime());

			preparedStatement.setTimestamp(15, dateSql);

			preparedStatement.setString(16, jsonObject.get("source").toString());
			preparedStatement.setLong(17, (Long) jsonObject.get("retweet_count"));
			preparedStatement.setBoolean(18, (boolean) jsonObject.get("retweeted"));
			preparedStatement.setLong(19, (Long) jsonObject.get("favorite_count"));
			preparedStatement.setString(20, jsonObject.toString());
			preparedStatement.setInt(21, idsearch);

			status = executeStatement(preparedStatement);

		} catch (Exception ex) {
			System.out.println("ERROR TWEET_DB: " + ex.getMessage());
			throw ex;
		}

	}

	/***
	 * Insert edge in the db according the relationship
	 * 
	 * @param text
	 *            json tweet to get the relationship
	 * @param relation
	 *            Relation type
	 * @throws Exception
	 */
	private void insertEdgeDB(String text, RelationshipSearch relation, int idsearch) throws Exception {
		int status = -1;
		JSONParser parser = new JSONParser();
		Object obj = null;
		JSONObject jsonObject;

		try {
			obj = parser.parse(text);
			jsonObject = (JSONObject) obj;
			String userString = "";
			JSONObject jOUser = null;
			JSONObject jOUser2 = null;

			preparedStatement = connect.prepareStatement(
					"INSERT INTO  edges (source,target,name,idsearch) VALUES (?, ?, ?,? ) ON DUPLICATE KEY UPDATE weight=weight+1;");

			userString = jsonObject.get("user").toString();
			jOUser = (JSONObject) parser.parse(userString);
			preparedStatement.setLong(1, (Long) jOUser.get("id"));
			preparedStatement.setInt(4, idsearch);

			// Create relationship
			/*switch (relation) {
			case RETWEETED:*/
				// retweeted_status
				if (jsonObject.get("retweeted_status") != null) {
					String retweetString = jsonObject.get("retweeted_status").toString();
					JSONObject jORetweet = (JSONObject) parser.parse(retweetString);

					String userRetweetString = jORetweet.get("user").toString();
					jOUser2 = (JSONObject) parser.parse(userRetweetString);

					// tweets
					System.out.println("RETWEETED in");
					preparedStatement.setLong(2, (Long) jOUser2.get("id"));
					preparedStatement.setString(3, "RETWEETED");
					status = executeStatement(preparedStatement);

				}
				/*break;
			case REPLIED:*/
				if (jsonObject.get("in_reply_to_user_id") != null) {
					System.out.println("replied in");
					preparedStatement.setLong(2, (Long) jsonObject.get("in_reply_to_user_id"));
					preparedStatement.setString(3, "REPLIED");
					status = executeStatement(preparedStatement);

				}
				/*break;
			case MENTIONED:*/
				/*
				 * "entities":{ "hashtags":[
				 * {"text":"TeamTrevi","indices":[83,93]}], "urls":[],
				 * "user_mentions":[ {"screen_name":"GloriaTrevi",
				 * "name":"GloriaTrevi", "id":86119466, "id_str":"86119466",
				 * "indices":[0,12]}, {"screen_name":"LaVozMexico", "name":
				 * "La Voz... M\u00e9xico", "id":310982691,
				 * "id_str":"310982691", "indices":[70,82]}],
				 */

				String entitiesString = jsonObject.get("entities").toString();
				JSONObject jOEntities = (JSONObject) parser.parse(entitiesString);

				if (jsonObject.get("user_mentions") != null) {
					String userMentionesString = jOEntities.get("user_mentions").toString();
					JSONArray jAUsers = (JSONArray) parser.parse(userMentionesString);

					String tempHash = "";
					System.out.println("Mentioned in");

					for (int i = 0; i < jAUsers.size(); i++) {
						String tempUserMentionId = ((JSONObject) jAUsers.get(i)).get("id").toString();

						preparedStatement.setLong(2, (Long) ((JSONObject) jAUsers.get(i)).get("id"));
						preparedStatement.setString(3, "MENTIONED");
						status = executeStatement(preparedStatement);
					}

				}

				/*break;
			case CONTRIBUTOR:*/
				/*System.out.println("Contribuitor in");
				preparedStatement.setString(3, "CONTRIBUTOR");
				status = executeStatement(preparedStatement);*/
				/*break;
			default:
				break;
			}*/

		} catch (Exception ex) {
			System.out.println("ERROR EDGE_DB: " + ex.getMessage());
			throw ex;
		}

	}

	/***
	 * Insert nodes of the current dataset
	 * 
	 * @param text
	 *            json tweet
	 * @param relation
	 *            relation of the tweet
	 * @param idsearch
	 *            id of the current search
	 * @throws Exception
	 */
	private void insertNodeDB(String text, RelationshipSearch relation, int idsearch) throws Exception {
		int status = -1;
		JSONParser parser = new JSONParser();
		Object obj = null;
		JSONObject jsonObject;

		try {
			obj = parser.parse(text);
			jsonObject = (JSONObject) obj;
			String userString = "";
			JSONObject jOUser = null;

			preparedStatement = connect.prepareStatement("INSERT INTO nodes (id,label,url,idsearch) "
					+ "VALUES (?, ?, ?,? ) ON DUPLICATE KEY UPDATE count=count+1;");

			userString = jsonObject.get("user").toString();
			jOUser = (JSONObject) parser.parse(userString);

			preparedStatement.setLong(1, (Long) jOUser.get("id"));
			preparedStatement.setString(2, jOUser.get("screen_name").toString());
			preparedStatement.setString(3, jOUser.get("profile_image_url_https").toString());
			preparedStatement.setInt(4, idsearch);
			status = executeStatement(preparedStatement);

			switch (relation) {
			case RETWEETED:

				break;
			case REPLIED:

				if (jsonObject.get("in_reply_to_user_id") != null
						&& jsonObject.get("in_reply_to_screen_name") != null) {
					System.out.println("REPLIED!: " + jsonObject.get("in_reply_to_screen_name").toString());
					preparedStatement = connect
							.prepareStatement("INSERT INTO nodes (id,label,url,idsearch) VALUES (?, ?, ?,? )");
					preparedStatement.setLong(1, (Long) jsonObject.get("in_reply_to_user_id"));
					preparedStatement.setString(2, jsonObject.get("in_reply_to_screen_name").toString());
					preparedStatement.setString(3, "");
					preparedStatement.setInt(4, idsearch);
					status = executeStatement(preparedStatement);
				}

				break;
			case MENTIONED:
				break;
			case CONTRIBUTOR:
				break;
			default:
				break;
			}

		} catch (Exception ex) {
			System.out.println("ERROR NODE_DB: " + ex.getMessage());
			throw ex;
		}
	}
	// endregion

	/***
	 * Insert the record of the new search in the DB
	 * 
	 * @param searchname
	 *            name of the search
	 * @param iduser
	 *            user which is creating the search
	 * @param type
	 *            stream,search
	 * @throws Exception
	 */
	private Search insertRecordSearch(String searchname, int iduser, SearchType type, String searchwords)
			throws Exception {
		Search search = new Search();
		try {
			connect();
			// InsertSearch ('anotherSearch1',3,'REPLIED','twitter');
			CallableStatement stmt = connect.prepareCall("{ call InsertSearch(?,?,?,?) }");
			stmt.setString(1, searchname);
			stmt.setInt(2, iduser);
			stmt.setString(3, type.toString());
			stmt.setString(4, searchwords);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int idsearch = (rs.getString("idsearch").contains("No hay credencial") ? -1 : rs.getInt("idsearch"));

				if (idsearch > 0) {
					search.setIDSearch(idsearch);
					search.setEnable(rs.getBoolean("enable"));
					search.setEndsearch(rs.getTimestamp("endsearch"));
					search.setIduser(iduser);
					search.setKeepsearching(rs.getBoolean("keepsearching"));
					search.setLastupdate(rs.getTime("lastupdate"));
					search.setMessage("OK");
					search.setSearchname(searchname);
					search.setSearchwords(searchwords);
					search.setStartsearch(rs.getTimestamp("startsearch"));
					search.setType(rs.getString("type"));
				} else {
					throw new Exception("ERROR not credential available.");
				}
			}

		} catch (Exception ex) {
			System.out.println("ERROR SearchRecordI_DB: " + ex.getMessage());
			search.setMessage("ERROR: " + ex.getMessage());
			throw ex;
		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				System.out.println("error2: " + e.getMessage());
				search.setMessage("ERROR: " + e.getMessage());
			}
		}
		return search;

	}

	/***
	 * Update search record to keepsearching true/false depending of the action
	 * RESTART, STOP
	 * 
	 * @param idSearch
	 *            Id of the search to update
	 * @param action
	 *            Action to perform RESTART/STOP
	 * @return
	 * @throws Exception
	 */
	private Search updateRecordSearch(int idSearch, SearchType search, Status action) throws Exception {
		Search src = new Search();
		try {
			connect();

			if (action.equals(Status.STOP)) {
				preparedStatement = connect.prepareStatement(
						"UPDATE search set keepsearching = ?, endsearch = now() WHERE idsearch = ? ;");
				preparedStatement.setBoolean(1, false);
			} else {
				preparedStatement = connect
						.prepareStatement("UPDATE search set keepsearching = true, type = ?  WHERE idsearch = ? ;");
				preparedStatement.setString(1, search.toString());
			}

			preparedStatement.setInt(2, idSearch);

			executeStatement(preparedStatement);

			src = getRecordSearch(idSearch);
			src.setMessage("OK: Search updated correctly");

		} catch (Exception ex) {
			System.out.println("ERROR SearchRecordU_DB: " + ex.getMessage());
			src.setMessage("ERROR SearchRecordU_DB: " + ex.getMessage());
			throw ex;
		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				System.out.println("error2: " + e.getMessage());
				src.setMessage("ERROR2 SearchRecordU_DB: " + e.getMessage());
				throw e;
			}
		}

		return src;
	}

	/***
	 * Disable/Enable search this allows to show the search in the interface
	 * 
	 * @param idSearch
	 *            ID of the search to change
	 * @param action
	 *            Action to perform Enable/Disable
	 * @return
	 * @throws Exception
	 */
	public int disableRecordSearch(int idSearch, Status action) throws Exception {
		int status = -1;
		try {
			connect();

			if (action.equals(Status.DISABLE)) {
				preparedStatement = connect.prepareStatement(
						"UPDATE search set keepsearching = ?,enable = ?, endsearch = now() WHERE idsearch = ? ;");
				preparedStatement.setBoolean(1, false);
				preparedStatement.setBoolean(2, false);
				preparedStatement.setInt(3, idSearch);
			} else {
				preparedStatement = connect.prepareStatement("UPDATE search set enable = ? WHERE idsearch = ? ;");
				preparedStatement.setBoolean(1, true);
				preparedStatement.setInt(2, idSearch);
			}

			status = executeStatement(preparedStatement);

		} catch (Exception ex) {
			System.out.println("ERROR SearchRecordU_DB: " + ex.getMessage());
			throw ex;
		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				System.out.println("error2: " + e.getMessage());
			}
		}

		return status;

	}

	/***
	 * Get all the search with the flag enable = true
	 * 
	 * @param idSearch
	 *            Search id to search
	 * @return Is searching? true/false
	 * @throws SQLException
	 */
	public boolean selectRecordSearch(int idSearch) throws SQLException {
		boolean keepSearching = false;
		try {
			connect();
			String sql = "SELECT keepsearching FROM search WHERE idsearch = ? AND enable = true;";
			preparedStatement = connect.prepareStatement(sql);
			preparedStatement.setInt(1, idSearch);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next())
				keepSearching = rs.getBoolean("keepsearching");

			rs.close();

		} catch (Exception ex) {
			System.out.println("ERROR selectRecordSearch: " + ex.getMessage());
			keepSearching = true;
		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				System.out.println("error2 selectRecordSearch: " + e.getMessage());
			}
		}

		return keepSearching;

	}

	public Search getRecordSearch(int idSearch) throws SQLException {
		Search search = new Search();
		try {

			connect();
			String sql = "SELECT idsearch,searchname,iduser,startsearch,endsearch,lastupdate,type,keepsearching,searchwords "
					+ "FROM search WHERE enable = true AND idsearch = ? ;";

			preparedStatement = connect.prepareStatement(sql);
			preparedStatement.setInt(1, idSearch);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				search.setIDSearch(rs.getInt("idsearch"));
				search.setSearchname(rs.getString("searchname"));
				search.setIduser(rs.getInt("iduser"));
				search.setStartsearch(rs.getTimestamp("startsearch"));
				search.setEndsearch(rs.getTimestamp("endsearch"));
				search.setLastupdate(rs.getTimestamp("lastupdate"));
				search.setType(rs.getString("type"));
				search.setKeepsearching(rs.getBoolean("keepsearching"));
				search.setSearchwords(rs.getString("searchwords"));
				search.setMessage("OK");
			}
			rs.close();

		} catch (Exception ex) {
			search.setMessage("ERROR:" + ex.getMessage());
			System.out.println("ERROR: " + ex.getMessage());
			throw ex;
		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				search.setMessage("ERROR2:" + e.getMessage());
				System.out.println("error2: " + e.getMessage());
				throw e;
			}
		}

		return search;

	}

	/***
	 * Get all the active searches
	 * 
	 * @param iduser
	 *            Owner of the searches
	 * @return All the searches in a ArrayList of Search
	 */
	public ArrayList<Search> getAllSearches(int iduser, int total) {
		ArrayList<Search> searchesList = new ArrayList<Search>();
		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT idsearch,searchname,iduser,startsearch,endsearch,lastupdate,type,keepsearching,searchwords "
						+ "FROM search WHERE enable = true AND iduser = " + iduser + " ORDER BY startsearch ;";
			else
				sql = "SELECT idsearch,searchname,iduser,startsearch,endsearch,lastupdate,type,keepsearching,searchwords "
						+ "FROM search WHERE enable = true AND iduser = " + iduser + " ORDER BY startsearch desc LIMIT "
						+ total + ";";

			System.out.println(sql);
			preparedStatement = connect.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Search search = new Search();
				search.setIDSearch(rs.getInt("idsearch"));// 0
				search.setSearchname(rs.getString("searchname"));// 1
				search.setIduser(rs.getInt("iduser"));// 2
				search.setStartsearch(rs.getTimestamp("startsearch"));// 3
				search.setEndsearch(rs.getTimestamp("endsearch"));// 4
				search.setLastupdate(rs.getTimestamp("lastupdate"));// 5
				search.setType(rs.getString("type"));// 6
				search.setKeepsearching(rs.getBoolean("keepsearching"));// 7
				search.setSearchwords(rs.getString("searchwords"));// 8
				searchesList.add(search);

			}
		} catch (Exception ex) {
			System.out.println("ERROR getSearches: " + ex.getMessage());
			ex.printStackTrace();
		}
		return searchesList;

	}

	public int executeStatement(PreparedStatement pStatement) throws Exception {
		int status = -1;
		try {
			// Insert into DB
			status = pStatement.executeUpdate();
		} catch (Exception ex1) {
			if (ex1.getMessage().startsWith("Duplicate entry '") && ex1.getMessage().endsWith("' for key 'PRIMARY'"))
				System.out.println("Valor repetido");
			else {
				System.out.println("ERROR: " + ex1.getMessage() + " Query: " + pStatement.toString());
				throw ex1;
			}

		}
		return status;
	}

	public User checkLogin(String user, String pwd) {
		User usr = new User();
		try {
			connect();
			String sql = "SELECT iduser,username,name,lastname,lastlogin,idaccess,enable,email "
					+ "FROM user where enable = true AND username = '" + user + "' AND password = '" + pwd + "'";

			preparedStatement = connect.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {

				usr.setIDUser(rs.getInt("iduser"));
				usr.setUserName(user);
				usr.setName(rs.getString("name"));
				usr.setLastname(rs.getString("lastname"));
				usr.setLastLogin(rs.getTimestamp("lastlogin"));
				usr.setIDAccess(rs.getInt("idaccess"));
				usr.setEnable(rs.getBoolean("enable"));
				usr.setEmail(rs.getString("email"));
			}
		} catch (Exception ex) {
			System.out.println("ERROR selectUsr: " + ex.getMessage());
		}
		return usr;
	}

	public static java.util.Date getTwitterDate(String date) throws ParseException {

		final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
		sf.setLenient(true);
		return sf.parse(date);
	}

	/***
	 * Register a new user to the application
	 * 
	 * @param user
	 *            user name
	 * @param pwd
	 *            pwd
	 * @param name
	 *            name of the user
	 * @param lastname
	 *            lastname of the user
	 * @param email
	 *            email of the user
	 * @return User enable false/true depending of if we got a failure
	 */
	public User registerUser(String user, String pwd, String name, String lastname, String email) {
		User usr = new User();
		try {
			connect();
			preparedStatement = connect.prepareStatement(
					"INSERT INTO  user (username,password,name,lastname,email,idaccess) VALUES (?,?, ?, ?,?,? );");
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pwd);
			preparedStatement.setString(3, name);
			preparedStatement.setString(4, lastname);
			preparedStatement.setString(5, email);
			preparedStatement.setInt(6, 1);

			preparedStatement.executeUpdate();
			preparedStatement = connect.prepareStatement("SELECT LAST_INSERT_ID() as iduser;");

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				usr.setIDUser(rs.getInt("iduser"));
				usr.setUserName(user);
				usr.setName(name);
				usr.setLastname(lastname);
				usr.setEmail(email);
				usr.setEnable(true);
				usr.setMessage("OK");
			}

		} catch (Exception ex) {
			System.out.println("ERROR UserI_DB: " + ex.getMessage());
			usr.setMessage("ERROR:" + ex.getMessage());
			return usr;
		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				System.out.println("error2: " + e.getMessage());
			}
			return usr;
		}

	}

	public ArrayList<Tweet> getTweetBySearch(int idSearch, int total) {
		ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();
		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT id,id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
						+ "truncated,hashtags,user_mentions,usr_id_str,usr_id,location,created_at," + // DATE_FORMAT(created_at,
																										// \"%Y-%l-%d
																										// %H:%m:%s\")
																										// AS
																										// created_at,"
																										// +
				"source,retweet_count,retweeted,favorite_count,tweet,idsearch " + "FROM tweet WHERE idsearch = "
						+ idSearch + " ORDER BY created_at ;";
			else
				sql = "SELECT id,id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
						+ "truncated,hashtags,user_mentions,usr_id_str,usr_id,location,created_at," + // DATE_FORMAT(created_at,
																										// \"%Y-%l-%d
																										// %H:%m:%s\")
																										// AS
																										// created_at,"
																										// +
				"source,retweet_count,retweeted,favorite_count,tweet,idsearch " + "FROM tweet WHERE idsearch = "
						+ idSearch + " ORDER BY created_at desc LIMIT " + total + ";";

			System.out.println(sql);
			preparedStatement = connect.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();

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
				System.out.println("date: " + rs.getTimestamp("created_at").toString());
				String dateString = rs.getTimestamp("created_at").toString();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date = dateFormat.parse(dateString);
				System.out.println(dateFormat.format(date));

				tweet.setCreated_at(date);
				tweet.setSource(rs.getString("Source"));
				tweet.setRetweet_count(rs.getLong("retweet_count"));
				tweet.setRetweeted(rs.getBoolean("retweeted"));
				tweet.setFavorite_count(rs.getLong("favorite_count"));
				tweet.setTweet(rs.getString("tweet"));
				tweet.setIdsearch(rs.getInt("idsearch"));

				tweetsList.add(tweet);

			}
		} catch (Exception ex) {
			System.out.println("ERROR getTweets: " + ex.getMessage());
			ex.printStackTrace();
		}
		return tweetsList;

	}

	public ArrayList<User> getUsersBySearch(int idSearch, int total) {
		ArrayList<User> usersList = new ArrayList<User>();

		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT n.label as sourcename,source ,n.count as sourceCount, n2.label as targetname, target,n2.count as targetCount,weight  "
						+ "FROM edges e " + "JOIN nodes n ON e.idsearch = n.idsearch and n.id = e.source "
						+ "LEFT outer JOIN nodes n2 ON e.idsearch = n2.idsearch and e.target = n2.id "
						+ "WHERE e.idsearch = " + idSearch + ";";
			else
				sql = "SELECT n.label as sourcename,source ,n.count as sourceCount, n2.label as targetname, target,n2.count as targetCount,weight  "
						+ "FROM edges e " + "JOIN nodes n ON e.idsearch = n.idsearch and n.id = e.source "
						+ "LEFT outer JOIN nodes n2 ON e.idsearch = n2.idsearch and e.target = n2.id "
						+ "WHERE e.idsearch = " + idSearch + " LIMIT " + total + ";";

			System.out.println(sql);
			preparedStatement = connect.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				/*
				 * User user = new User(); user.setId(rs.getLong("id"));
				 * user.setId_str(rs.getString("id_str"));
				 */
			}

		} catch (Exception ex) {
			System.out.println("ERROR getTweets: " + ex.getMessage());
			ex.printStackTrace();
		}

		return usersList;
	}
}
