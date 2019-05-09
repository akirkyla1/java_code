package com.bridgit.export.export_data;


//import com.bridgit.export.export_data.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;




public class ManageExport {

	/** Start test
	 * @param args the name of the file to export to.
	 */
	public static void main(String[] args) {


		String dbURL = "jdbc:postgresql:/databaseURL/dspace";
		String userName = "dspace";
		String password = "dsapce";
		String fileName = args[0];

		DBConnection db = new DBConnection();
		db.setJdbcUrl(dbURL);
		db.setUserPassword(password);
		db.setUserName(userName);
		BoneCPConfig config = db.newBPConfig();
		BoneCP bcp = db.newConnectionPool(config);
		Connection con = db.newConnection(bcp);
		DspaceItem di = new DspaceItem();

		try {
			// load the database driver (make sure this is in your classpath!)
			Class.forName("org.postgresql.Driver");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}



		try {


			if (con != null){
				System.out.println("Connection successful!");

				String[] columns = di.getColumnHeads();

				//String fileName = "export-07-26-16.tab";
				byte[] headers = di.convertColumns(columns);
				di.createFileWithHeader(headers, fileName);


				ArrayList<Integer> items = DspaceItem.retrieveItemIds(con, "I");
				for(int i = 0; i < items.size(); i++){
					Map<String, String> bookMap = di.populateRow(con, items.get(i).intValue());
					byte[] bookRow = di.convertRowData(bookMap);
					di.addRowData(bookRow, fileName);
				}



			}
			System.out.println("Export Completed!");
			bcp.shutdown(); // shutdown connection pool.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
