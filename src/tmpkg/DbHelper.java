package tmpkg;

import java.sql.*;
import java.util.ArrayList;

public class DbHelper {
		
	 	static String dbName = "E6998";
	 	static String userName = "xy2251";
	 	static String password ="19921110";
	 	static String jdbcUrl = "jdbc:mysql://e6998.ctzz3ktosjvj.us-east-1.rds.amazonaws.com:3306/" + dbName + "?user=" + userName + "&password=" + password;
 
		public void addtwits(Twitter twit) {
		  // Read RDS Connection Information from the Environment
		 
		  // Load the JDBC Driver
			 try {
				    System.out.println("Loading driver...");
				    Class.forName("com.mysql.jdbc.Driver");
				    System.out.println("Driver loaded!");
				  } catch (ClassNotFoundException e) {
				    throw new RuntimeException("Cannot find the driver in the classpath!", e);
				  }
	
		  Connection conn = null;
		  Statement setupStatement = null;

		  try {
			conn = DriverManager.getConnection(jdbcUrl);
		    setupStatement = conn.createStatement();
		    String createTable = "CREATE TABLE IF NOT EXISTS twits_table( "
		    		+ "id CHAR(50) PRIMARY KEY NOT NULL, "
		    		+ "username CHAR(50) NOT NULL, "
		    		+ "text TEXT NOT NULL,"
		    		+ "timestamp CHAR(50) NOT NULL,"
		    		+ "latitude CHAR(50) NOT NULL,"
		    		+ "longtitude CHAR(50) NOT NULL,"
		    		+ "keyword CHAR(50),"
		    		+ "url TEXT NOT NULL)";
		   String insert = "INSERT INTO `twits_table` VALUES ('"+twit.id+"','"
				   +twit.username+"','"+twit.text+"','"+twit.timestamp+"','"+twit.latitude+"','"
				   +twit.longtitude+"','"+twit.keyword+"','"+twit.url+"');";
		    
		    setupStatement.addBatch(createTable);
		    setupStatement.addBatch(insert);
		    setupStatement.executeBatch();
		    
		  } catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		  } finally {
		    System.out.println("Closing the connection.");
		    if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
		  }
		}
		
		  public ArrayList<Twitter> gettwits(){
			  ArrayList<Twitter> res = new ArrayList<Twitter>();
			  try {
				    System.out.println("Loading driver...");
				    Class.forName("com.mysql.jdbc.Driver");
				    System.out.println("Driver loaded!");
				  } catch (ClassNotFoundException e) {
				    throw new RuntimeException("Cannot find the driver in the classpath!", e);
				  }
			  Connection conn = null;			 
			  ResultSet resultSet = null;

			  try {
				    conn = DriverManager.getConnection(jdbcUrl);
				    PreparedStatement readStatement = conn.prepareStatement("SELECT id, username, "
				    		+ "text, timestamp, latitude, longtitude, keyword, url"
				    		+ " FROM twits_table");
				    resultSet = readStatement.executeQuery();
			
				    //resultSet.first();
				    while(resultSet.next()){
				    	Twitter t = new Twitter();
				    	t.id = resultSet.getString("id");
				    	t.username = resultSet.getString("username");
				    	t.text = resultSet.getString("text");
				    	t.timestamp = resultSet.getString("timestamp");
				    	t.latitude = resultSet.getString("latitude");
				    	t.longtitude = resultSet.getString("longtitude");
				    	t.keyword = resultSet.getString("keyword");
				    	t.url = resultSet.getString("url");
				    	res.add(t);
				    }
				    System.out.println();
				    
				    resultSet.close();
				    readStatement.close();
				    conn.close();
			
				  } catch (SQLException ex) {
				    // handle any errors
				    System.out.println("SQLException: " + ex.getMessage());
				    System.out.println("SQLState: " + ex.getSQLState());
				    System.out.println("VendorError: " + ex.getErrorCode());
				  } finally {
				       System.out.println("Closing the connection.");
				      if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
				  }
			  return res;
		  }
	}
