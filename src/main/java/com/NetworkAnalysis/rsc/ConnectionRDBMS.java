package com.NetworkAnalysis.rsc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ConnectionRDBMS implements GlobalVariablesInterface {

	private static String USER = global.getUSER();
	private static String PASSWORD = global.getPASSWORD();
	private static String DB = global.getDB();
	private static String HOST = global.getHOST();
	private static String PORT = global.getPORT();
	public static Connection connect = null;
	static PreparedStatement preparedStatement = null;
	static PreparedStatement preparedStatement2 = null;
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
	
	public Connection connect(Connection conn) {
		try {
			System.out.println("connect2");
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DB + "?" + "user=" + USER
					+ "&password=" + PASSWORD + "&useUnicode=true");// &characterEncoding=UTF-8
			return conn;
		} catch (Exception ex) {
			System.out.println("error in connect2: " + ex.getMessage());
		}
		return null;
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
	public int insert(String text, RelationshipSearch relation, int idsearch) throws Exception {
		// DB connection setup

		int status = -1;
		try {
			connect();
			/*switch (type) {
			case TWEET:*/
				insertTweetDB(text, relation, idsearch);				
				/*break;
			case EDGE:*/
				insertEdgeDB(text, relation, idsearch);
				/*break;
			case NODE:*/
				insertNodeDB(text, relation, idsearch);
				/*break;
			default:
				break;

			}*/

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
		JSONObject jOUser2 = null;
		JSONObject jOTemp = null;
		String tempString = "";

		try {
			obj = parser.parse(text);
			jsonObject = (JSONObject) obj;

			preparedStatement = connect.prepareStatement(
					"INSERT INTO  tweet (id,id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
							+ "truncated,hashtags,user_mentions,usr_id_str,usr_id,location,created_at,source,retweet_count,retweeted,favorite_count,tweet,idsearch,"
							+ "retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names) "
							+ "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ) ;");

			userString = jsonObject.get("user").toString();
			jOUser = (JSONObject) parser.parse(userString);

            //Get the date of the tweet
            String dateString = jsonObject.get("created_at").toString();
			java.util.Date dateUtil = getTwitterDate(dateString);
			java.sql.Timestamp dateSql = new java.sql.Timestamp(dateUtil.getTime());
			
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

			///insert hashtags relations mary!
			if (jOTemp.get("hashtags") == null)
				preparedStatement.setNull(10, 0);
			else {
				JSONArray jArray = (JSONArray) jOTemp.get("hashtags");
				String tempHash = "";
				for (int i = 0; i < jArray.size(); i++){
				//	tempHash += ((JSONObject) jArray.get(i)).get("text").toString() + ",";
					String hashtag= ((JSONObject) jArray.get(i)).get("text").toString();
                    tempHash += hashtag + ",";
                    
                    preparedStatement2 = connect.prepareStatement(
                            "INSERT INTO nodes (id,label,url,idsearch,type,timeinterval) "
                                + "VALUES (?, ?, ?,?,'Hashtag',? ) ON DUPLICATE KEY UPDATE count=count+1;");

                    userString = jsonObject.get("user").toString();
                    jOUser = (JSONObject) parser.parse(userString);
        			
                    //jOUser.get("screen_name").toString()
                    preparedStatement2.setString(1, hashtag);
                    preparedStatement2.setString(2, hashtag);
                    preparedStatement2.setString(3, null);
                    preparedStatement2.setInt(4, idsearch);
                    preparedStatement2.setTimestamp(5, dateSql);
                    status = executeStatement(preparedStatement2);
                    //Insert relationship
                    preparedStatement2 = connect.prepareStatement(
                                "INSERT INTO  edges (source,target,name,idsearch,type,timeinterval) "
                                        + "VALUES (?, ?, ?,? ,'Hashtag',?) ON DUPLICATE KEY UPDATE weight=weight+1;");

                    userString = jsonObject.get("user").toString();
                    jOUser = (JSONObject) parser.parse(userString);
                    preparedStatement2.setString(1,  jOUser.get("id").toString());
                    preparedStatement2.setString(2,hashtag );
                    preparedStatement2.setString(3, "HASHTAG");
                    preparedStatement2.setInt(4, idsearch);  
                    preparedStatement2.setTimestamp(5, dateSql);
                    status = executeStatement(preparedStatement2);
				}
				if (jArray.size() > 0) {
					tempHash = tempHash.substring(0, tempHash.length() - 1);
					preparedStatement.setString(10, tempHash);
				} else
					preparedStatement.setNull(10, 0);
			}

			// entities->user_mentiones->id,id_str,screen_name
			if (jOTemp.get("user_mentions") == null)
				preparedStatement.setNull(11, 0);
			else
				preparedStatement.setString(11, jOTemp.get("user_mentions").toString());

			preparedStatement.setString(12, jOUser.get("id_str").toString());
			preparedStatement.setLong(13, (Long) jOUser.get("id"));

			if (jOUser.get("location") == null)
				preparedStatement.setNull(14, 0);
			else
				preparedStatement.setString(14, jOUser.get("location").toString());			

			preparedStatement.setTimestamp(15, dateSql);

			preparedStatement.setString(16, jsonObject.get("source").toString());
			preparedStatement.setLong(17, (Long) jsonObject.get("retweet_count"));
			preparedStatement.setBoolean(18, (boolean) jsonObject.get("retweeted"));
			preparedStatement.setLong(19, (Long) jsonObject.get("favorite_count"));
			preparedStatement.setString(20, jsonObject.toString());
			preparedStatement.setInt(21, idsearch);
			// added columns

			if (jsonObject.get("retweeted_status") != null) {
				System.out.println("new retweeted_status");
				String retweetString = jsonObject.get("retweeted_status").toString();
				JSONObject jORetweet = (JSONObject) parser.parse(retweetString);

				String userRetweetString = jORetweet.get("user").toString();
				jOUser2 = (JSONObject) parser.parse(userRetweetString);
				preparedStatement.setLong(22, (Long) jOUser2.get("id"));
				preparedStatement.setString(23, jOUser2.get("screen_name").toString());

			} else {
				preparedStatement.setNull(22, 0);
				preparedStatement.setNull(23, 0);
			}

			String entitiesString = jsonObject.get("entities").toString();
			JSONObject jOEntities = (JSONObject) parser.parse(entitiesString);

			if (jOEntities.get("user_mentions") != null) {
				String idsTemp = "";
				String screennamesTemp = "";
				String userMentionesString = jOEntities.get("user_mentions").toString();
				JSONArray jAUsers = (JSONArray) parser.parse(userMentionesString);

				for (int i = 0; i < jAUsers.size(); i++) {
					String tempUserMentionId = ((JSONObject) jAUsers.get(i)).get("id").toString();

					idsTemp += ((JSONObject) jAUsers.get(i)).get("id").toString() + ",";
					screennamesTemp += ((JSONObject) jAUsers.get(i)).get("screen_name").toString() + ",";
				}

				if (idsTemp.length() > 0 && screennamesTemp.length() > 0) {
					idsTemp = idsTemp.substring(0, idsTemp.length() - 1);
					screennamesTemp = screennamesTemp.substring(0, screennamesTemp.length() - 1);
					preparedStatement.setString(24, idsTemp);
					preparedStatement.setString(25, screennamesTemp);
				} else {
					preparedStatement.setNull(24, 0);
					preparedStatement.setNull(25, 0);
				}
			} else {
				preparedStatement.setNull(24, 0);
				preparedStatement.setNull(25, 0);
			}

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

			String dateString = jsonObject.get("created_at").toString();
			java.util.Date dateUtil = getTwitterDate(dateString);
			java.sql.Timestamp dateSql = new java.sql.Timestamp(dateUtil.getTime());			
			
			preparedStatement = connect.prepareStatement(
					"INSERT INTO  edges (source,target,name,idsearch,timeinterval) VALUES (?, ?, ?,?,? ) ON DUPLICATE KEY UPDATE weight=weight+1;");

			userString = jsonObject.get("user").toString();
			jOUser = (JSONObject) parser.parse(userString);
			

			// Create relationship

			// This tweet was retweeted from
			if (jsonObject.get("retweeted_status") != null) {
				String retweetString = jsonObject.get("retweeted_status").toString();
				JSONObject jORetweet = (JSONObject) parser.parse(retweetString);

				String userRetweetString = jORetweet.get("user").toString();
				jOUser2 = (JSONObject) parser.parse(userRetweetString);

				// tweets
				System.out.println("RETWEETED in");
				preparedStatement.setString(1, jOUser.get("id").toString());
				preparedStatement.setInt(4, idsearch);
				preparedStatement.setTimestamp(5, dateSql);
				
				preparedStatement.setString(2, jOUser2.get("id").toString());
				preparedStatement.setString(3, "RETWEETED");
				status = executeStatement(preparedStatement);

			}

			// This tweet was in reply to the user

			if (jsonObject.get("in_reply_to_user_id") != null) {
				
				preparedStatement.setString(1, jOUser.get("id").toString());
				preparedStatement.setInt(4, idsearch);
				preparedStatement.setTimestamp(5, dateSql);
				
				preparedStatement.setString(2,  jsonObject.get("in_reply_to_user_id").toString());
				preparedStatement.setString(3, "REPLIED");
				status = executeStatement(preparedStatement);

			}

			// Users mentioned in the tweet

			String entitiesString = jsonObject.get("entities").toString();
			JSONObject jOEntities = (JSONObject) parser.parse(entitiesString);

			if (jOEntities.get("user_mentions") != null) {
				String userMentionesString = jOEntities.get("user_mentions").toString();
				JSONArray jAUsers = (JSONArray) parser.parse(userMentionesString);

				String tempHash = "";


				for (int i = 0; i < jAUsers.size(); i++) {
					String tempUserMentionId = ((JSONObject) jAUsers.get(i)).get("id").toString();
					preparedStatement.setString(1, jOUser.get("id").toString());
					preparedStatement.setInt(4, idsearch);
					preparedStatement.setTimestamp(5, dateSql);
					
					preparedStatement.setString(2,  ((JSONObject) jAUsers.get(i)).get("id").toString());
					preparedStatement.setString(3, "MENTIONED");
					status = executeStatement(preparedStatement);
					System.out.print(i + ",");
				}
				System.out.println("");

			}

			/*
			 * break; case CONTRIBUTOR:
			 */
			/*
			 * System.out.println("Contribuitor in");
			 * preparedStatement.setString(3, "CONTRIBUTOR"); status =
			 * executeStatement(preparedStatement);
			 */
			/*
			 * break; default: break; }
			 */

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
			JSONObject jOUser2 = null;

			preparedStatement = connect.prepareStatement("INSERT INTO nodes (id,label,url,idsearch,timeinterval) "
					+ "VALUES (?, ?, ?,?,? ) ON DUPLICATE KEY UPDATE count=count+1;");

			userString = jsonObject.get("user").toString();
			jOUser = (JSONObject) parser.parse(userString);
			String dateString = jsonObject.get("created_at").toString();
			java.util.Date dateUtil = getTwitterDate(dateString);
			java.sql.Timestamp dateSql = new java.sql.Timestamp(dateUtil.getTime());			

			preparedStatement.setString(1, jOUser.get("id").toString());
			preparedStatement.setString(2, jOUser.get("screen_name").toString());
			preparedStatement.setString(3, jOUser.get("profile_image_url_https").toString());
			preparedStatement.setInt(4, idsearch);
			preparedStatement.setTimestamp(5, dateSql);
			status = executeStatement(preparedStatement);

			// RETWEETED:
			if (jsonObject.get("retweeted_status") != null) {
				String retweetString = jsonObject.get("retweeted_status").toString();
				JSONObject jORetweet = (JSONObject) parser.parse(retweetString);

				String userRetweetString = jORetweet.get("user").toString();
				jOUser2 = (JSONObject) parser.parse(userRetweetString);

				//preparedStatement = connect.prepareStatement("INSERT INTO nodes (id,label,url,idsearch,timeinterval) "
					//	+ "VALUES (?, ?, ?,?,? ) ON DUPLICATE KEY UPDATE count=count+1;");
				
				preparedStatement.setString(1,  jOUser2.get("id").toString());
				preparedStatement.setString(2, jOUser2.get("screen_name").toString());
				preparedStatement.setString(3, jOUser2.get("profile_image_url_https").toString());
				preparedStatement.setInt(4, idsearch);
				preparedStatement.setTimestamp(5, dateSql);
				status = executeStatement(preparedStatement);
				//System.out.println("id:" + jOUser2.get("id").toString() + ",screen_name:" +jOUser2.get("screen_name").toString() 
					//	+ ",url:" +jOUser2.get("profile_image_url_https").toString());

			}

			// REPLIED
			if (jsonObject.get("in_reply_to_user_id") != null && jsonObject.get("in_reply_to_screen_name") != null) {
				//preparedStatement = connect.prepareStatement("INSERT INTO nodes (id,label,url,idsearch,timeinterval) "
					//	+ "VALUES (?, ?, ?,?,? ) ON DUPLICATE KEY UPDATE count=count+1;");
				
				preparedStatement.setString(1, jsonObject.get("in_reply_to_user_id").toString());
				preparedStatement.setString(2, jsonObject.get("in_reply_to_screen_name").toString());
				preparedStatement.setString(3, "");
				preparedStatement.setInt(4, idsearch);
				preparedStatement.setTimestamp(5, dateSql);
				status = executeStatement(preparedStatement);
			}

			// MENTIONED
			String entitiesString = jsonObject.get("entities").toString();
			JSONObject jOEntities = (JSONObject) parser.parse(entitiesString);

			if (jOEntities.get("user_mentions") != null) {
				String userMentionesString = jOEntities.get("user_mentions").toString();
				JSONArray jAUsers = (JSONArray) parser.parse(userMentionesString);

				for (int i = 0; i < jAUsers.size(); i++) {
					String tempUserMentionId = ((JSONObject) jAUsers.get(i)).get("id").toString();

					//preparedStatement = connect.prepareStatement("INSERT INTO nodes (id,label,url,idsearch,timeinterval) "
						//	+ "VALUES (?, ?, ?,?,? ) ON DUPLICATE KEY UPDATE count=count+1;");
					preparedStatement.setString(1,  ((JSONObject) jAUsers.get(i)).get("id").toString());
					preparedStatement.setString(2, ((JSONObject) jAUsers.get(i)).get("screen_name").toString());
					preparedStatement.setString(3, "");
					preparedStatement.setInt(4, idsearch);
					preparedStatement.setTimestamp(5, dateSql);
					status = executeStatement(preparedStatement);
				}
			}

			// CONTRIBUTOR

		} 
		catch(SQLException exSQL){
			System.out.println("ERROR NODE_DB_SQL: " + exSQL.getMessage());
			throw exSQL;
		}		
		catch (Exception ex) {
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

				int idsearch = (rs.getString("idsearch").contains("WARNING:") ? -1 : rs.getInt("idsearch"));

				if (idsearch > 0) {
					search.setIDSearch(idsearch);
					search.setEnable(rs.getBoolean("enable"));
					search.setEndsearch(rs.getTimestamp("endsearch"));
					search.setIduser(iduser);
					search.setKeepsearching(rs.getBoolean("keepsearching"));
					search.setLastupdate(rs.getTimestamp("lastupdate"));
					search.setMessage(new Message("Nueva búsqueda guardada.",100,"OK"));
					search.setSearchname(searchname);
					search.setSearchwords(searchwords);
					search.setStartsearch(rs.getTimestamp("startsearch"));
					search.setType(rs.getString("type"));
					search.credential.setConsumerKeyStr(rs.getString("consumer_key_str"));
					search.credential.setConsumerSecretStr(rs.getString("consumer_secret_str"));
					search.credential.setAccessTokenStr(rs.getString("access_token_str"));
					search.credential.setAccessTokenSecretStr(rs.getString("access_token_secret_str"));
					
				} else {
					search.setMessage(new Message(rs.getString("idsearch"),501,"ERROR"));
				}
			}

		} catch (Exception ex) {
			System.out.println("ERROR SearchRecordI_DB: " + ex.getMessage());
			search.message.setMessage("ERROR: " + ex.getMessage());
			search.message.setStatus("ERROR");
			search.message.setCode(502);
			search.message.setSource("ERROR SearchRecordI_DB");
			//throw ex;
		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				System.out.println("error2: " + e.getMessage());
				search.message.setMessage("ERROR: " + e.getMessage());
				search.message.setStatus("ERROR");
				search.message.setCode(502);
				search.message.setSource("ERROR SearchRecordI_DB");
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
		Message msg =  new Message();
		msg.setObject(action.toString());
		msg.setSource("updateRecordSearch function");
		ResultSet rs = null;
		CallableStatement stmt = null;
		Connection conn = null;
		try {
			conn = connect(conn);
			stmt = conn.prepareCall("{ call UpdateSearchCredential(?,?,?) }");
			stmt.setInt(1, idSearch);
			stmt.setString(2, search.toString());
			stmt.setString(3, action.toString());			

			rs = stmt.executeQuery();
			while (rs.next()) {

				int idsearch = (rs.getString("idsearch").contains("WARNING:") ? -1 : rs.getInt("idsearch"));

				if (idsearch > 0) {
					src.setIDSearch(idsearch);
					src.setEnable(rs.getBoolean("enable"));
					src.setEndsearch(rs.getTimestamp("endsearch"));
					//src.setIduser(iduser);
					src.setKeepsearching(rs.getBoolean("keepsearching"));
					src.setLastupdate(rs.getTimestamp("lastupdate"));
					msg.setCode(100);
					msg.setMessage("Actualización correcta: " + action.toString());
					msg.setStatus("OK");
					msg.setObject(rs.getBoolean("keepsearching")?"STOP":"RESTART");
					src.setMessage(msg);					
					src.setSearchname(rs.getString("searchname"));
					src.setSearchwords(rs.getString("searchwords"));
					src.setStartsearch(rs.getTimestamp("startsearch"));
					//src.setTweetsNumber(tweetsNumber);
					src.setType(rs.getString("type"));					
					src.setCredential(new Credential(rs.getInt("idcredential"),rs.getString("consumer_key_str"),
							rs.getString("consumer_secret_str"),rs.getString("access_token_str"),
							rs.getString("access_token_secret_str"),true));					
				}
				else
				{
					msg.setCode(501);
					msg.setStatus("ERROR");
					msg.setMessage(rs.getString("idsearch"));
					msg.setObject(action.toString());
					src.setMessage(msg);
				}
			}

		} catch (Exception ex) {
			System.out.println("ERROR SearchRecordU_DB: " + ex.getMessage());			
			msg.setCode(502);
			msg.setStatus("ERROR");
			msg.setMessage("ERROR SearchRecordU_DB: " + ex.getMessage());
			msg.setObject(action.toString());
			src.setMessage(msg);
			
		} finally {
			try {
				if (rs != null) {
	                rs.close();
	            }
	            if (stmt != null) {
	                stmt.close();
	            }
	            if (conn != null) {
	            	conn.close();
	            	conn = null;
	            }

			} catch (Exception e) {
				System.out.println("error2: " + e.getMessage());
				msg.setCode(502);
				msg.setStatus("ERROR");
				msg.setMessage("ERROR2 SearchRecordU_DB: " + e.getMessage());
				msg.setObject(action.toString());
				src.setMessage(msg);
				
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
		search.message.setSource("getRecordSearch");
		search.message.setMessage("Búsqueda no encontrada");
		search.message.setStatus("Not Found");
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
				search.message.setMessage("Búsqueda encontrada.");
				search.message.setStatus("OK");
			}
			rs.close();

		} catch (Exception ex) {
			search.message.setMessage("ERROR:" + ex.getMessage());
			search.message.setStatus("ERROR");
			search.message.setError("ERROR:" + ex.getMessage());
			search.message.setCode(502);
			System.out.println("ERROR: " + ex.getMessage());
			//throw ex;
		} finally {
			try {
				if (connect != null) {
					connect.close();
				}

			} catch (Exception e) {
				search.message.setMessage("ERROR2:" + e.getMessage());
				search.message.setStatus("ERROR");
				search.message.setCode(502);
				System.out.println("error2: " + e.getMessage());
				//throw e;
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
				sql = "SELECT s.idsearch,s.searchname,s.iduser,s.startsearch,s.endsearch,s.lastupdate,s.type,s.keepsearching,s.searchwords, "
						+ " (SELECT COUNT(*) FROM tweet t WHERE t.idsearch = s.idsearch) AS tweetsnumber "
						+ "FROM search s  WHERE s.enable = true AND s.iduser = " + iduser + " ORDER BY startsearch ;";

			else
				sql = "SELECT s.idsearch,s.searchname,s.iduser,s.startsearch,s.endsearch,s.lastupdate,s.type,s.keepsearching,s.searchwords, "
						+ " (SELECT COUNT(*) FROM tweet t WHERE t.idsearch = s.idsearch) AS tweetsnumber "
						+ "FROM search s  WHERE s.enable = true AND s.iduser = " + iduser + " ORDER BY startsearch desc LIMIT "
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
				search.setTweetsNumber(rs.getFloat("tweetsnumber"));//9
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
		usr.Message.setStatus("Denied");
		usr.Message.setMessage("Usuario o password incorrecto.");
		try {
			connect();
			String sql = "SELECT iduser,username,name,lastname,lastlogin,idaccess,enable,email "
					+ "FROM user where enable = true AND username = '" + user + "' AND password = '" + pwd + "'";

			preparedStatement = connect.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next()) {

				usr.setIDUser(rs.getInt("iduser"));
				usr.setUserName(user);
				usr.setName(rs.getString("name"));
				usr.setLastname(rs.getString("lastname"));
				usr.setLastLogin(rs.getTimestamp("lastlogin"));
				usr.setIDAccess(rs.getInt("idaccess"));
				usr.setEnable(rs.getBoolean("enable"));
				usr.setEmail(rs.getString("email"));
				usr.Message.status="OK";
				usr.Message.message="Usuario encontrado";
			}
		} catch (Exception ex) {
			System.out.println("ERROR selectUsr: " + ex.getMessage());
			usr.Message.status="ERROR";
			usr.Message.setMessage("ERROR selectUsr: " + ex.getMessage());
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
				usr.Message.setStatus("OK");
				usr.Message.setMessage("Usuario registrado");
			}

		} catch (Exception ex) {
			System.out.println("ERROR UserI_DB: " + ex.getMessage());
			usr.Message.setStatus("ERROR");
			usr.Message.setSource("registerUser method in ConnectionRDBMS");
			usr.Message.setMessage("ERROR:" + ex.getMessage());
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

	public ResultSet getTweetBySearch(int idSearch, int total) {
		int i = 0;
		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT id,id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
						+ "truncated,hashtags,user_mentions,usr_id_str,usr_id,location,created_at,"
						+ "source,retweet_count,retweeted,favorite_count,"//tweet,idsearch, "
						+ "retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names "
						+ "FROM tweet WHERE idsearch = " + idSearch + " ORDER BY created_at ;";
			else
				sql = "SELECT id,id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
						+ "truncated,hashtags,user_mentions,usr_id_str,usr_id,location,created_at,"
						+ "source,retweet_count,retweeted,favorite_count,"//tweet,idsearch, "
						+ "retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names "
						+ "FROM tweet WHERE idsearch = " + idSearch + " ORDER BY created_at desc LIMIT " + total + ";";

			System.out.println(sql);
			Statement stmt = connect.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			// preparedStatement.setFetchSize(Integer.MIN_VALUE);
			stmt.setFetchSize(Integer.MIN_VALUE);

			// preparedStatement = connect.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery(sql); // reparedStatement.executeQuery();
			System.out.println("Result set!!");
			return rs;
			
		} catch (Exception ex) {
			System.out.println("ERROR getTweets(" + i + "): " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}

	}

	public ArrayList<Tweet> getTweetBySearchJson(int idSearch, int total) {
		ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();
		int i = 0;
		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
						+ "truncated,hashtags,user_mentions,usr_id_str,usr_id,location,created_at,"
						+ "source,retweet_count,retweeted,favorite_count,tweet,idsearch, "
						+ "retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names "
						+ "FROM tweet WHERE idsearch = " + idSearch + " ORDER BY created_at ;";
			else
				sql = "SELECT id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
						+ "truncated,hashtags,user_mentions,usr_id_str,usr_id,location,created_at,"
						+ "source,retweet_count,retweeted,favorite_count,tweet,idsearch, "
						+ "retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names "
						+ "FROM tweet WHERE idsearch = " + idSearch + " ORDER BY created_at desc LIMIT " + total + ";";

			System.out.println(sql);
			Statement stmt = connect.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			// preparedStatement.setFetchSize(Integer.MIN_VALUE);
			stmt.setFetchSize(Integer.MIN_VALUE);

			// preparedStatement = connect.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery(sql); // reparedStatement.executeQuery();
			
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
				  tweet.setUser_mentions(rs.getString("user_mentions"));
				  tweet.setUsr_id_str(rs.getString("usr_id_str"));
				  tweet.setUsr_id(rs.getLong("usr_id"));
				  tweet.setLocation(rs.getString("location"));
				  //System.out.println("date: " + rs.getTimestamp("created_at").toString()); 
				  String dateString = rs.getTimestamp("created_at").toString(); 
				  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				  java.util.Date date = dateFormat.parse(dateString);
				  //System.out.println(dateFormat.format(date)); 
				  i++;
				  tweet.setCreated_at(date);
				  tweet.setSource(rs.getString("Source"));
				  tweet.setRetweet_count(rs.getLong("retweet_count"));
				  tweet.setRetweeted(rs.getBoolean("retweeted"));
				  tweet.setFavorite_count(rs.getLong("favorite_count"));
				  tweet.setTweet(rs.getString("tweet"));
				  tweet.setIdsearch(rs.getInt("idsearch"));
				  //retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names
				  tweet.setRetweeted_user_id(rs.getLong("retweeted_user_id"));
				  tweet.setRetweeted_user_screen_name(rs.getString( "retweeted_user_screen_name"));
				  tweet.setMentioned_users_ids(rs.getString("mentioned_users_ids")); 
				  tweet.setMentioned_users_screen_names(rs.getString("mentioned_users_screen_names")); 
				  tweetsList.add(tweet);			  
			  }
			 
		} catch (Exception ex) {
			System.out.println("ERROR getTweets(" + i + "): " + ex.getMessage());
			ex.printStackTrace();
		}

		 return tweetsList;

	}

	
	public ArrayList<Edge> getEdgesBySearchJson(int idSearch, int total) {
		ArrayList<Edge> edgesList = new ArrayList<Edge>();

		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT n.id as ID1, n.label as sourcename, target as ID2, "
						+ "n2.label as targetname, weight, name  " 
						+ "FROM edges e "
						+ "JOIN nodes n ON e.idsearch = n.idsearch and n.id = e.source "
						+ "LEFT outer JOIN nodes n2 ON e.idsearch = n2.idsearch and e.target = n2.id "
						+ "WHERE e.idsearch = " + idSearch + ";";
			else
				sql = "SELECT n.id as ID1, n.label as sourcename, target as ID2, "
						+ "n2.label as targetname, weight, name  "
						+ "FROM edges e " 
						+ "JOIN nodes n ON e.idsearch = n.idsearch and n.id = e.source "
						+ "LEFT outer JOIN nodes n2 ON e.idsearch = n2.idsearch and e.target = n2.id "
						+ "WHERE e.idsearch = " + idSearch + " LIMIT " + total + ";";

			System.out.println(sql);
			//preparedStatement = connect.prepareStatement(sql);
			//ResultSet rs = preparedStatement.executeQuery();
			Statement stmt = connect.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Edge edge = new Edge();
				edge.nodeSource.setId(rs.getString("ID1"));
				edge.nodeSource.setLabel(rs.getString("sourcename"));
				edge.nodeSource.setCount(rs.getInt("sourceCount"));
				edge.nodeTarget.setId(rs.getString("ID2"));
				edge.nodeTarget.setLabel(rs.getString("targetname"));
				edge.nodeTarget.setCount(rs.getInt("targetCount"));
				edge.setWeight(rs.getInt("weight"));
				edge.setRelation(rs.getString("name"));
				edgesList.add(edge);

			}
			

		} catch (Exception ex) {
			System.out.println("ERROR getTweets: " + ex.getMessage());
			ex.printStackTrace();
		}

		return edgesList;
	}
	
	public ResultSet getEdgesBySearch(int idSearch, int total,String type) {

		ResultSet rs=null;
		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT source as ID1, target as ID2, "
						+ "weight, name, timeinterval " 
						+ "FROM edges e "												
						+ "WHERE e.idsearch = " + idSearch + " AND e.type = '" + type + "';";
			else
				sql = "SELECT source as ID1, target as ID2, "
						+ "weight, name, timeinterval " 
						+ "FROM edges e " 						
						+ "WHERE e.idsearch = " + idSearch + " AND e.type = '" + type + "' LIMIT " + total + ";";

			System.out.println(sql);
			/*preparedStatement = connect.prepareStatement(sql);
			rs = preparedStatement.executeQuery();*/
			Statement stmt = connect.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			rs = stmt.executeQuery(sql);
			

		} catch (Exception ex) {
			System.out.println("ERROR getEdges: " + ex.getMessage());
			ex.printStackTrace();
		}

		return rs;
	}
	
	public ArrayList<Node> getNodesBySearchJson(int idSearch, int total) {
		ArrayList<Node> nodesList = new ArrayList<Node>();

		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT id, label,url,count " 
						+ "FROM nodes e "
						+ "WHERE e.idsearch = " + idSearch + ";";
			else
				sql = "SELECT id, label,url,count " 
						+ "FROM nodes e "
						+ "WHERE e.idsearch = " + idSearch + " LIMIT " + total + ";";

			System.out.println(sql);
			/*preparedStatement = connect.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();*/
			Statement stmt = connect.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Node node = new Node();
				node.setId(rs.getString("id"));
				node.setLabel(rs.getString("label"));
				node.setCount(rs.getInt("count"));
				node.setUrl(rs.getString("url"));
				nodesList.add(node);
			}
			

		} catch (Exception ex) {
			System.out.println("ERROR getTweets: " + ex.getMessage());
			ex.printStackTrace();
		}

		return nodesList;
	}
	
	public ResultSet getNodesBySearch(int idSearch, int total,String type) {
		ResultSet rs = null;

		try {
			connect();
			String sql = "";
			if (total == -1)
				sql = "SELECT id, label,url,count,timeinterval " 
						+ "FROM nodes n "
						+ "WHERE n.id IN (SELECT e.source FROM edges e WHERE e.idsearch=" + idSearch + " AND type ='" + type + "' ) OR " 
						+ "n.id IN (SELECT e.target FROM edges e WHERE e.idsearch=" + idSearch + " AND type ='" + type + "' ); " ;
			else
				sql = "SELECT id, label,url,count,timeinterval " 
						+ "FROM nodes n "
						+ "WHERE n.id IN (SELECT e.source FROM edges e WHERE e.idsearch=" + idSearch + " AND type ='" + type + "') OR " 
						+ "n.id IN (SELECT e.target FROM edges e WHERE e.idsearch=" + idSearch + " AND type ='" + type + "') LIMIT " 
						+ total + ";";

			System.out.println(sql);
			/*preparedStatement = connect.prepareStatement(sql);
			rs = preparedStatement.executeQuery();*/
			Statement stmt = connect.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			rs = stmt.executeQuery(sql);
			

		} catch (Exception ex) {
			System.out.println("ERROR getTweets: " + ex.getMessage());
			ex.printStackTrace();
		}

		return rs;
	}
	
	public void updateTweetDB(JSONObject jsonObject, RelationshipSearch relation, int idsearch,String IDTweet) throws Exception {
        int status = -1;
        JSONParser parser = new JSONParser();
        Object obj = null;
        //JSONObject jsonObject;
        String userString = "";
        JSONObject jOUser = null;
        JSONObject jOUser2 = null;
        JSONObject jOTemp = null;
        String tempString = "";

        try {
            //obj = parser.parse(text);
            //jsonObject = (JSONObject) obj;

            preparedStatement = connect.prepareStatement(
                "UPDATE tweet SET screen_name = ?,in_reply_to_user_id =?,"
                        + "in_reply_to_screen_name=?,text=?,lang=?,possibly_sensitive=?,"
                        + "truncated=?,hashtags=?,user_mentions=?,usr_id_str=?,usr_id=?,"
                        + "location=?,created_at=?,source=?,retweet_count=?,retweeted=?,"
                        + "favorite_count=?,"
                        + "retweeted_user_id=?,retweeted_user_screen_name=?,mentioned_users_ids=?,mentioned_users_screen_names=?,updated=1 "
                    + "WHERE id_str = ? ;");

            userString = jsonObject.get("user").toString();
            jOUser = (JSONObject) parser.parse(userString);

            //preparedStatement.setLong(1, (long) jsonObject.get("id"));
            //preparedStatement.setString(2, jsonObject.get("id_str").toString());
            preparedStatement.setString(1, jOUser.get("screen_name").toString());

            if (jsonObject.get("in_reply_to_user_id") == null)
                    preparedStatement.setNull(2, 0);
            else
                    preparedStatement.setLong(2, (Long) jsonObject.get("in_reply_to_user_id"));

            if (jsonObject.get("in_reply_to_screen_name") == null)
                    preparedStatement.setNull(3, 0);
            else
                    preparedStatement.setString(3, jsonObject.get("in_reply_to_screen_name").toString());

            preparedStatement.setString(4, jsonObject.get("text").toString());
            preparedStatement.setString(5, jsonObject.get("lang").toString());

            if (jsonObject.get("possibly_sensitive") == null)
                    preparedStatement.setBoolean(6, false);
            else
                    preparedStatement.setBoolean(6, (boolean) jsonObject.get("possibly_sensitive"));

            preparedStatement.setBoolean(7, (boolean) jsonObject.get("truncated"));

            // Get the hashtags
            tempString = jsonObject.get("entities").toString();
            jOTemp = (JSONObject) parser.parse(tempString);

            if (jOTemp.get("hashtags") == null)
                    preparedStatement.setNull(8, 0);
            else {
                JSONArray jArray = (JSONArray) jOTemp.get("hashtags");
                String tempHash = "";
                for (int i = 0; i < jArray.size(); i++){
                    String hashtag= ((JSONObject) jArray.get(i)).get("text").toString();
                    tempHash += hashtag + ",";
                    
                    preparedStatement2 = connect.prepareStatement(
                            "INSERT INTO nodes (id,label,url,idsearch,type) "
                                + "VALUES (?, ?, ?,?,'Hashtag' ) ON DUPLICATE KEY UPDATE count=count+1;");

                    userString = jsonObject.get("user").toString();
                    jOUser = (JSONObject) parser.parse(userString);
                    //jOUser.get("screen_name").toString()
                    preparedStatement2.setString(1, hashtag);
                    preparedStatement2.setString(2, hashtag);
                    preparedStatement2.setString(3, null);
                    preparedStatement2.setInt(4, idsearch);
                    status = executeStatement(preparedStatement2);
                    //Insert relationship
                    preparedStatement2 = connect.prepareStatement(
                                "INSERT INTO  edges (source,target,name,idsearch,type) "
                                        + "VALUES (?, ?, ?,? ,'Hashtag') ON DUPLICATE KEY UPDATE weight=weight+1;");

                    userString = jsonObject.get("user").toString();
                    jOUser = (JSONObject) parser.parse(userString);
                    preparedStatement2.setString(1,  jOUser.get("id").toString());
                    preparedStatement2.setString(2,hashtag );
                    preparedStatement2.setString(3, "HASHTAG MENTIONED");
                    preparedStatement2.setInt(4, idsearch);                    
                    status = executeStatement(preparedStatement2);
                    
                }
                if (jArray.size() > 0) {
                        tempHash = tempHash.substring(0, tempHash.length() - 1);
                        preparedStatement.setString(8, tempHash);
                } else
                        preparedStatement.setNull(8, 0);
            }

            // entities->user_mentiones->id,id_str,screen_name
            if (jOTemp.get("user_mentions") == null)
                    preparedStatement.setNull(9, 0);
            else
                    preparedStatement.setString(9, jOTemp.get("user_mentions").toString());

            preparedStatement.setString(10, jOUser.get("id_str").toString());
            preparedStatement.setLong(11, (Long) jOUser.get("id"));

            if (jOUser.get("location") == null)
                    preparedStatement.setNull(12, 0);
            else
                    preparedStatement.setString(12, jOUser.get("location").toString());

            String dateString = jsonObject.get("created_at").toString();
            java.util.Date dateUtil = getTwitterDate(dateString);
            java.sql.Timestamp dateSql = new java.sql.Timestamp(dateUtil.getTime());

            preparedStatement.setTimestamp(13, dateSql);

            preparedStatement.setString(14, jsonObject.get("source").toString());
            preparedStatement.setLong(15, (Long) jsonObject.get("retweet_count"));
            preparedStatement.setBoolean(16, (boolean) jsonObject.get("retweeted"));
            preparedStatement.setLong(17, (Long) jsonObject.get("favorite_count"));
            //preparedStatement.setString(18, jsonObject.toString());
            //preparedStatement.setInt(19, idsearch);
            // added columns
            

            if (jsonObject.get("retweeted_status") != null) {
                    //System.out.println("new retweeted_status");
                    String retweetString = jsonObject.get("retweeted_status").toString();
                    JSONObject jORetweet = (JSONObject) parser.parse(retweetString);

                    String userRetweetString = jORetweet.get("user").toString();
                    jOUser2 = (JSONObject) parser.parse(userRetweetString);
                    preparedStatement.setLong(18, (Long) jOUser2.get("id"));
                    preparedStatement.setString(19, jOUser2.get("screen_name").toString());

            } else {
                    preparedStatement.setNull(18, 0);
                    preparedStatement.setNull(19, 0);
            }

            String entitiesString = jsonObject.get("entities").toString();
            JSONObject jOEntities = (JSONObject) parser.parse(entitiesString);

            if (jOEntities.get("user_mentions") != null) {
                    //System.out.println("new user_mentions");
                    String idsTemp = "";
                    String screennamesTemp = "";
                    String userMentionesString = jOEntities.get("user_mentions").toString();
                    JSONArray jAUsers = (JSONArray) parser.parse(userMentionesString);

                    for (int i = 0; i < jAUsers.size(); i++) {
                            String tempUserMentionId = ((JSONObject) jAUsers.get(i)).get("id").toString();

                            idsTemp += ((JSONObject) jAUsers.get(i)).get("id").toString() + ",";
                            screennamesTemp += ((JSONObject) jAUsers.get(i)).get("screen_name").toString() + ",";
                    }

                    //System.out.println("temp values :" + idsTemp + " --- " + screennamesTemp);
                    if (idsTemp.length() > 0 && screennamesTemp.length() > 0) {
                            idsTemp = idsTemp.substring(0, idsTemp.length() - 1);
                            screennamesTemp = screennamesTemp.substring(0, screennamesTemp.length() - 1);
                            preparedStatement.setString(20, idsTemp);
                            preparedStatement.setString(21, screennamesTemp);
                    } else {
                            preparedStatement.setNull(20, 0);
                            preparedStatement.setNull(21, 0);
                    }
            } else {
                    preparedStatement.setNull(20, 0);
                    preparedStatement.setNull(21, 0);
            }
            //preparedStatement.setLong(22, (long) jsonObject.get("id"));
            //preparedStatement.setString(25, jsonObject.get("id_str").toString());
            preparedStatement.setString(22, IDTweet);
            
            
            status = preparedStatement.executeUpdate();

        } catch (Exception ex) {
                System.out.println("ERROR Update tweet DB: " + ex.getMessage());
                throw ex;
        }

    }
	
	public void fixTweets(int IdSearch,int delete,int from,int total){
        try{    
            connect();
            String sql = "";
            //DELETE nodes and edges related to the search
            if(delete==1){
                System.out.println("Delete nodes starting...");
                sql = "DELETE FROM nodes WHERE IDSearch = ? ";
                preparedStatement = connect.prepareStatement(sql);
                preparedStatement.setInt(1, IdSearch);
                preparedStatement.execute();

                System.out.println("Delete nodes finished");

                System.out.println("Delete edges starting...");
                sql = "DELETE FROM edges WHERE IDSearch = ? ";
                preparedStatement = connect.prepareStatement(sql);
                preparedStatement.setInt(1, IdSearch);
                preparedStatement.execute();
                System.out.println("Delete edges finished");
            }
            //preparedStatement.close();
            System.out.println("Update tweets starting...");
            if (total == -1)
                sql = "SELECT id_str,tweet "
                    + "FROM tweet WHERE idsearch = " + IdSearch + " AND updated=0;";
            else
                sql = "SELECT id_str,tweet "
                    + "FROM tweet WHERE idsearch = " + IdSearch + " AND updated=0 LIMIT " +from + "," + total + ";";

            System.out.println(sql);
            //Statement stmt = connectRes.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,java.sql.ResultSet.CONCUR_READ_ONLY);
            
            

            preparedStatement = connect.prepareStatement(sql);

            ResultSet rs =preparedStatement.executeQuery(); //reparedStatement.executeQuery();
            System.out.println("Update tweets resulted got ");
            float i=from;
            while(rs.next()){
                i++;
                String IDTweet = rs.getString("id_str");
                String Tweet = rs.getString("tweet");
                //Update tweet and relationships
                JSONParser parser = new JSONParser();
                Object obj = null;
                JSONObject jsonObject;
                obj = parser.parse(Tweet);
                
                jsonObject = (JSONObject) obj;
                java.util.Date dt2 = new java.util.Date();
                //System.out.println("Tweet #" + i + " Time:" + dt2.toString());
                //updateTweetDB(jsonObject, RelationshipSearch.REPLIED, IdSearch,IDTweet);
                insertEdgeDB(Tweet, RelationshipSearch.REPLIED, IdSearch);
                insertNodeDB(Tweet, RelationshipSearch.REPLIED, IdSearch);
                dt2 = new java.util.Date();
                //System.out.println("Tweet #" + i + " Time:" + dt2.toString());
                if(i%1000==0 ||i%100==0){
                	java.util.Date dt = new java.util.Date();
                    System.out.println("Tweet #" + i + " Time:" + dt.toString());
                }
            }
            System.out.println("Update tweets finished");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
	
	public ResultSet getPopularTweetBySearch(int idSearch,String orderBy, int limit){
		int i = 0;
		try {
			connect();
			String sql = "";
			if (limit == -1)
				sql = "SELECT id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
						+ "truncated,hashtags,usr_id_str,location,created_at,"
						+ "source,retweet_count,retweeted,favorite_count,"
						+ "retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names "
						+ "FROM tweet WHERE idsearch = " + idSearch + " ORDER BY " + orderBy + " desc, created_at ;";
			else
				sql = "SELECT id_str,screen_name,in_reply_to_user_id,in_reply_to_screen_name,text,lang,possibly_sensitive,"
						+ "truncated,hashtags,usr_id_str,location,created_at,"
						+ "source,retweet_count,retweeted,favorite_count,"
						+ "retweeted_user_id,retweeted_user_screen_name,mentioned_users_ids,mentioned_users_screen_names "
						+ "FROM tweet WHERE idsearch = " + idSearch + " ORDER BY " + orderBy + " desc, created_at LIMIT " + limit + ";";

			System.out.println(sql);
			Statement stmt = connect.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			// preparedStatement.setFetchSize(Integer.MIN_VALUE);
			stmt.setFetchSize(Integer.MIN_VALUE);

			// preparedStatement = connect.prepareStatement(sql);

			ResultSet rs = stmt.executeQuery(sql); // reparedStatement.executeQuery();
			System.out.println("Result set!!");
			return rs;
			
		} catch (Exception ex) {
			System.out.println("ERROR getTweets(" + i + "): " + ex.getMessage());
			ex.printStackTrace();
			return null;
		}

	}
}
