import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;


class GMT{
	public int id = 1;
	public String name = "";
	public String description = "";
	public String text = "";
	public HashSet<GMTGeneList> genelists;
	public SQLmanager sql;
	Connection  connection;
	
	public GMT() {
		
	}
	
	public GMT(int _id, String _name, String _desc, String _text) {
		id = _id;
		name = _name;
		description = _desc;
		text = _text;
		genelists = new HashSet<GMTGeneList>();
	}
	
	public void loadGMT(SQLmanager _sql, int _id) {
		id = _id;
		sql = _sql;
		loadGMTInfo();
	}
	
	public void loadGMTInfo() {
		
		try { 
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database, sql.user, sql.password);

			// create the java statement and execute
			String query = "SELECT * FROM gmtinfo WHERE id='"+id+"'";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				name = rs.getString("gmtname");
				description = rs.getString("gmtdesc");
				text = rs.getString("gmttext");
			}
			stmt.close();
			
			loadGeneLists();
			connection.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadGeneLists() {
		genelists = new HashSet<GMTGeneList>();
		Integer[] ids = getGeneListIds(id);
		
		for(Integer i : ids) {
			GMTGeneList genelist = new GMTGeneList();
			genelist.loadGMTGeneList(connection, i);
			genelists.add(genelist);
		}
	}
	
	public Integer[] getGeneListIds(int _gmtid) {
		Connection  connection;
		HashSet<Integer> genelistids = new HashSet<Integer>();
		try { 
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database, sql.user, sql.password);

			// create the java statement and execute
			String query = "SELECT gmtgenelistid FROM gmt WHERE gmtid='"+_gmtid+"'";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()){
			    int listid = rs.getInt("gmtgenelistid");
			    genelistids.add(listid);
			}
			
			stmt.close();
			connection.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return genelistids.toArray(new Integer[0]);
	}
	
	public String toString() {
		
		String text = name+" - "+description+" - size: "+genelists.size();
		for(GMTGeneList gl : genelists) {
			text += "\n -> " +gl.toString(); 
		}
		
		return text;
	}
	
	public void writeGMT(SQLmanager _sql) {
		sql = _sql;
		
		Connection  connection;

		try { 
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database, sql.user, sql.password);

			String query = " INSERT INTO gmtinfo (gmtname, gmtdesc, gmttext)"
			        + " VALUES (?, ?, ?);";

			// create the mysql insert pre-paredstatement
			PreparedStatement preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString(1, name);
			preparedStmt.setString(2, description);
			preparedStmt.setString(3, text);
			preparedStmt.executeQuery();
			
			ResultSet rs = preparedStmt.getGeneratedKeys();
			
			if (rs.next()) {
			    id = rs.getInt(1);
			}
			
			for(GMTGeneList gl : genelists) {
				int key = gl.writeGMTGeneList(sql, id);
				
			}
			
			connection.commit();
			connection.close();	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}