package org.cts.hybrid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.cognizant.framework.Status;

import supportlibraries.ReusableLibrary;
import supportlibraries.ScriptHelper;



public class DBScripts extends ReusableLibrary {

	static String ApplicantKey = null;
	Connection con = null;

	public DBScripts(ScriptHelper scriptHelper) {
		super(scriptHelper);

	}

	public Connection dbConnection() {

		try {

			String driverName = dataTable.getData("DataBase_Data", "Driver");
			Class.forName(driverName);
			report.updateTestLogWithNoScreenshot("Verify Driver Class load", "Driver Class Successfully loaded",
					Status.PASS);

		} catch (Exception ex) {

			report.updateTestLogWithNoScreenshot("Verify Driver Class load", "Unable to load driver Class",
					Status.FAIL);
		}

		try {
			String HostName = dataTable.getData("DataBase_Data", "HostName");
			String UName = dataTable.getData("DataBase_Data", "Uname");
			String Pass = dataTable.getData("DataBase_Data", "Pass");
			
			con = DriverManager.getConnection(HostName, UName, Pass);
			if (con != null)
				report.updateTestLogWithNoScreenshot("Verify Database Connection", "Successfully connected to Database",
						Status.PASS);
		} catch (Exception e) {

			report.updateTestLogWithNoScreenshot("Verify Database Connection", "Database connection Failed",
					Status.FAIL);
		}
		return con;
	}

	public List<String> ExecuteQuery(Connection con, String query) {

		Statement stmt = null;
		List<String> DBresults = new ArrayList<String>();
		try {

			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			int columncount = rsmd.getColumnCount();
			int rowcount = 0;
			while (rs.next()) {
				rowcount++;
				for (int i = 1; i <= columncount; i++) {
					DBresults.add(rs.getString(i));
				}

			}
			String rc = Integer.toString(rowcount);
			report.updateTestLogWithNoScreenshot("Execute Query :fetched rows ", rc, Status.PASS);

		} catch (Exception e) {

			report.updateTestLogWithNoScreenshot("Execute Query", e.getMessage(), Status.FAIL);
		}
		return DBresults;
	}

	public void getApplicantKey() {
		try {

			String query = dataTable.getData("DataBase_Data", "Query1");
			con = dbConnection();
			List<String> DBresults = ExecuteQuery(con, query);
			ApplicantKey = DBresults.get(0);
			report.updateTestLogWithNoScreenshot("Fetch Applicant Key from database", ApplicantKey, Status.PASS);
			closeDBConnection(con);
		} catch (Exception e) {
			report.updateTestLogWithNoScreenshot("Fetch Applicant Key from database", e.getMessage(), Status.FAIL);
		}
	}
	public void closeDBConnection(Connection con) {
		try {
			con.close();
			report.updateTestLogWithNoScreenshot("Close database Connection", "Closed Successfully ", Status.PASS);
		} catch (Exception e) {
			report.updateTestLogWithNoScreenshot("Close database Connection", e.getMessage(), Status.FAIL);
		}
	}
	public void compareValues() {

		try {
			System.out.println("Applicant key in compare value function"+ApplicantKey);
			int key = Integer.parseInt(ApplicantKey);
			System.out.println(ApplicantKey);
//			int key = 3622;
			String excelQuery = dataTable.getData("DataBase_Data", "Query2");
			String query = excelQuery + key;
			System.out.println(" QUERY IS  " + query);
			con = dbConnection();
			List<String> DBresults = ExecuteQuery(con, query);

			List<String> ExpectedValues = new ArrayList<String>();
			String  x = dataTable.getData("DataBase_Data", "ColumnCount");
			
			for(int i=1; i<=Integer.parseInt(x);i ++)
			{
			ExpectedValues.add(dataTable.getData("DataBase_Data", "ExpectedDBValue"+i));
			}
			
			for (int i = 0; i < DBresults.size(); i++) {

				if ((DBresults.get(i) == null) || (ExpectedValues.get(i) == "")) {
					report.updateTestLogWithNoScreenshot("Compare UI Value with database",
							" DB result or expected value in excel is Null.. Please check !! ", Status.FAIL);
					
				} else {
					if (DBresults.get(i).equalsIgnoreCase(ExpectedValues.get(i))) {

						report.updateTestLogWithNoScreenshot("Compare UI Value with database", DBresults.get(i),
								Status.PASS);
					} else {

						String errorTxt = "DB Value is " + DBresults.get(i) + " however UI Value is "
								+ ExpectedValues.get(i);
						report.updateTestLogWithNoScreenshot("Compare UI Value with database", errorTxt, Status.FAIL);
					}
				}
			}
			closeDBConnection(con);

		} catch (Exception e) {
			report.updateTestLogWithNoScreenshot("Compare UI Values with database", e.getMessage(), Status.FAIL);
		}
	}

}
