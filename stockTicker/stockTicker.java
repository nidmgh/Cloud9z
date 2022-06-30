import java.math.BigDecimal;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Java MySQL Connector
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement; 
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


// to run bash sh
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// JSON support for Java

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class stockTicker {

  /**
   * Simple method to fetch MySQL query result
   * @param con mysql connection
   * @param query string of query text
   * @return JSONArray all rows of the result with the total row count at the end
   */
  public static JSONArray getResultsFromMySQL(Connection con, String query)
    throws SQLException, JSONException {
    JSONArray jsonA = new JSONArray();
    Statement stmt = con.createStatement();
    try {
      ResultSet rs = stmt.executeQuery(query);
      ResultSetMetaData rsmd = rs.getMetaData();
      int numColumns = rsmd.getColumnCount();
      JSONObject obj;
      while (rs.next()) {
        obj = new JSONObject();
        for (int i = 1; i <= numColumns; i++) {
          String column_name = rsmd.getColumnName(i);
          obj.put(column_name, rs.getObject(column_name));
        }
        jsonA.put(obj);
      }
      obj = new JSONObject();
      obj.put("total row count", jsonA.length());
      jsonA.put(obj);
    } finally {
      stmt.close();
    }
    return jsonA;
  }

  /**
   * Scanner input the following environment parameters for Redis and MySQL.
   * DBHOST   - MySQL end point, and DBUSER, DBUSERPW, DBNAME
   */
  public static void main(String[] args) {

    final int sleepMin = 2;


    String DBHOST = System.getenv("DBHOST");
    String DBNAME = System.getenv("DBNAME");
    String DBUSER = System.getenv("DBUSER");
    String DBUSERPW = System.getenv("DBUSERPW");

    System.out.println("DBHOST: " + DBHOST);
    System.out.println("DBNAME: " + DBNAME);
    System.out.println("DBUSER: " + DBUSER);
    System.out.println("DBUSERPW: " + DBUSERPW);

    // Create the console object
    Console cnsl
            = System.console();
  
    if (cnsl == null) {
            System.out.println(
                "No console available");
            return;
    }
  
    // Read PW
    DBUSERPW = String.copyValueOf( 
	  cnsl.readPassword( "Enter password : "));
    // Print password
    System.out.println("DBUSERPW: " + DBUSERPW);


    /**
      *  Set up MySQL connection
      */ 
    Connection conn = null; 
    PreparedStatement preStmt = null;
    ResultSet rs = null;


    String myUrl = "jdbc:mysql://" + DBHOST + ":3306/" + DBNAME;

    // SQL Query, and Result
    String query, result;
    query = "SELECT * FROM SECURITY";

    // get MySQL connection
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      conn = DriverManager.getConnection(
        myUrl,
        DBUSER,
        DBUSERPW
      );

			//Get existing symbol     
      query = "SELECT SYMBOL FROM SECURITY";
      preStmt = conn.prepareStatement(query);
      rs = preStmt.executeQuery();
      ArrayList<String> symbols = new ArrayList<String>();
      
      // Extract data from result set
      while (rs.next()) {
     		symbols.add(rs.getString("SYMBOL"));
      } 
      System.out.println("=== Will query the following symbols  === ");
      System.out.println(symbols);

    /*** start ticker loop , temp for 100**/
    for (int i = 0; i<100; i++) {   
			//Run ticker.sh
			List<String> cmdList = new ArrayList<String>();
      // adding command and args to the list
      cmdList.add("sh");
      cmdList.add("./ticker.sh");
			cmdList.addAll(symbols);
      Process p;

 			try {
							ProcessBuilder pb = new ProcessBuilder(cmdList);
							p = pb.start();
							BufferedReader reader=new BufferedReader(new InputStreamReader(
							 p.getInputStream()));
							String line;
							ArrayList<String[]> output = new ArrayList<String[]>();
							while((line = reader.readLine()) != null) {
								output.add(line.split("\\s+"));
							}
							int exitVal = p.waitFor();
							if (exitVal == 0) {
								System.out.println("ticker Success!");

								// INSERT INTO TICKER(SYMBOL,PRICE,PRICE_DIFF) VALUES
								query = "INSERT INTO TICKER(SYMBOL,PRICE,PRICE_DIFF) VALUES(?,?,?)";

								preStmt = conn.prepareStatement(query);
								for (String res[]: output) {
									 preStmt.setString(1,res[0]);
									 preStmt.setBigDecimal(2,(new BigDecimal(res[1])));
									 preStmt.setBigDecimal(3,(new BigDecimal(res[2])));
									 preStmt.execute();
								}
								//System.exit(0);
							} else {
								System.out.println("ticker Fail!");
								System.exit(0);
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
      Thread.sleep(1000*60*sleepMin); // sleep N min
     }  /*** end ticker loop, will  change to continues background run **/

			

    } catch (SQLException ex) {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
    try { rs.close(); } catch (Exception e) { /* Ignored */ }
    try { preStmt.close(); } catch (Exception e) { /* Ignored */ }
    try { conn.close(); } catch (Exception e) { /* Ignored */ }
    }





  }
}
