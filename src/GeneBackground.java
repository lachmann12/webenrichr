import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GeneBackground extends GeneList{
	
	public GeneBackground(int _id, String _name, String _description, String _gmthash, HashSet<String> _genes) {
		id = _id;
		name = _name;
		description = _description;
		genes = _genes;
		hash = _gmthash;
	}
	
	public void write(SQLmanager _sql) {
		sql = _sql;
		Connection  connection;
		HashSet<String> genemap = new HashSet<String>();
		try { 
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database, sql.user, sql.password);

			// create the java statement and execute
			String query = "SELECT * FROM genemapping";
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()){
			    String gene = rs.getString("genesymbol");
			    genemap.add(gene);
			}
			stmt.close();
			
			HashSet<String> temp = new HashSet<String>(genes);
			temp.removeAll(genemap);
			
			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO genemapping (genesymbol) VALUES (?)");
			String[] genearr = temp.toArray(new String[0]); 
			
			for(String g : genearr) {
				pstmt.setString(1, g);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			
			query = "SELECT * FROM genemapping";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			
			HashMap<String, Integer> genemapping = new HashMap<String, Integer>();
			while (rs.next()){
			    String gene = rs.getString("genesymbol");
			    Integer geneid = rs.getInt("geneid");
			    genemapping.put(gene, geneid);
			}
			
			pstmt = connection.prepareStatement("INSERT INTO genebackgroundinfo (listname, listdesc, hash) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, name);
			pstmt.setString(2, description);
			pstmt.setString(3, md5hash(Arrays.toString(genes.toArray(new String[0]))));
			pstmt.addBatch();
			pstmt.executeBatch();
			
			rs = pstmt.getGeneratedKeys();
			int key = 0;
			if (rs.next()) {
			    key = rs.getInt(1);
			}
			
			pstmt = connection.prepareStatement("INSERT INTO genebackground (listid, geneid) VALUES (?, ?)");
			
			for(String g : genes) {
				pstmt.setInt(1, key);
				pstmt.setInt(2, genemapping.get(g));
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			connection.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void load() {
		
	}
	
}
