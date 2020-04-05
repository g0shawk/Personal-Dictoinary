package Data;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DataManagment {

	private static Connection conn;
	public static List<String> languageList() {	
		List<String> lst = new ArrayList<String>();
		connect();
		DatabaseMetaData metaData = null;
		ResultSet rs = null;
		try {
			 metaData = conn.getMetaData();
			 rs = metaData.getTables(null, conn.getSchema(), null, new String[]{"TABLE"});
			 while (rs.next()){
					lst.add(rs.getString(3));
			 }
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		if(disconnect())
			return lst;
		else 
			return null;
	}

	public static boolean connect() {
		File dbfile=new File("");
		String URL="jdbc:sqlite:" + dbfile.getAbsolutePath() + "\\Dictionaries.db";
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(URL);
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean disconnect() {
		try {
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		  }
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public static boolean createTable(String TableName) {
		String sql = "CREATE TABLE " + TableName
				+ " (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ " Word VARCHAR(255) NOT NULL,"
				+ " Translation VARCHAR(255))";
		try {
			connect();
			Statement st = conn.createStatement();
			st.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		if(disconnect())
			return true;
		else 
			return false;
	}

	public static boolean dropTable(String TableName) {
		String sql = "DROP TABLE " + TableName;
		boolean success = false;
		try {
			connect();
			Statement st = conn.createStatement();
			st.execute(sql);
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (disconnect() && success == true) {
			return true;
		}
		else
			return false;
	}

	public static boolean insert(String expression, String translation, String tableName) {
		String sql = "INSERT INTO " + tableName 
				+ " ( Word , Translation) "
				+ " VALUES( " + "'"  + expression + "'" +"," + "'"  + translation + "'" + " )";
		try {
			connect();
			Statement st = conn.createStatement();
			st.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		if(disconnect())
			return true;
		else 
			return false;
	}
	
	public static boolean insertSpecificExpression(String record, int col, String tableName) {
		String sql1 = "INSERT INTO " + tableName + " ( Word) " + " VALUES( " + "'"  + record + "'" + " )";
		String sql2 = "INSERT INTO " + tableName + " (Translation) " + " VALUES( " + "'"  + record + "'" + " )";
		try {
			connect();
			Statement st = conn.createStatement();
			if(col == 1)
				st.execute(sql1);
			else if(col == 2)
				st.execute(sql2);
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		if(disconnect())
			return true;
		else 
			return false;
	}
	
	public static boolean delete_entry(String expression, String transaltion, String tableName) {
		String sql = "SELECT * FROM " + tableName + " WHERE Word = '" + expression +"'" + " AND Translation = '" + transaltion + "'";
		Integer id;
			connect();	
			try {
				Statement stm = conn.createStatement();
				ResultSet rs = stm.executeQuery(sql);
			    // Returns only one ID, therefore this method deletes only one record in the database
				id = rs.getInt("ID");
				stm.execute("DELETE FROM " + tableName + " WHERE ID = '" + id.toString() +"'");
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		if(disconnect())
			return true;
		else 
			return false;
	}

	public static boolean dropAllTables() {
		for (String table : languageList()) {
			if(dropTable(table) == false)
			return false;
		}
		return true;
	}

	public static JTable readEntireDictionary(String tableName) {
		JTable tbl = new JTable();
		tbl.setModel(new DefaultTableModel(null, new String[] {"No#", "Foreign Expression", "Pronounciation & Translation"}));
		connect();
		try {
			Statement stm = conn.createStatement();
			String sql = "SELECT * FROM " + tableName + " ORDER BY Word ASC";
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				((DefaultTableModel)tbl.getModel()).addRow(new Object[] {rs.getRow(), rs.getString("Word"), rs.getString("Translation") });
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return tbl;
	}
	
	public static JTable readWantedExpressions(String match, String tableName) {
		JTable tbl = new JTable();
		tbl.setModel(new DefaultTableModel(null, new String[] {"No#", "Foreign Expression", "Pronounciation & Translation"}));
		try {
			connect();
			Statement stm = conn.createStatement();
			String sql1 = "SELECT * FROM " + tableName + " WHERE Word  LIKE '" + match + "%'" + " ORDER BY Word ASC ";
			String sql2 = "SELECT Word, Translation FROM " + tableName + " WHERE (Word  LIKE '" + match + "%')" 
					+ " UNION SELECT Word, Translation FROM " + tableName + " WHERE (Translation  LIKE '" + match + "%')";
			ResultSet rs;
			if(match.startsWith("%"))
			   rs = stm.executeQuery(sql2);
			else 
				rs = stm.executeQuery(sql1);
			while (rs.next()) {
				((DefaultTableModel)tbl.getModel()).addRow(new Object[] {rs.getRow(), rs.getString("Word"), rs.getString("Translation") });
			}
			disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return tbl;
	}
	
	public static boolean exists(String match, String tableName) {
		boolean b = false;
		try {
			connect();
			Statement stm = conn.createStatement();
			String sql = "SELECT Word FROM " + tableName + " WHERE Word = '" + match + "'";
			ResultSet rs = stm.executeQuery(sql);
				if((rs.next()) && (rs.getString("Word").isEmpty() == false ) && (match.length() > 0))
				  b = true;
			rs.close();	  
			disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return b;
	}
	
}