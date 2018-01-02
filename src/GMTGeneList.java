import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GMTGeneList extends GeneList{
	
	
	public GMTGeneList() {
		
	}
	
	public GMTGeneList(int _id, String _name, String _description, String _gmthash, HashSet<String> _genes) {
		id = _id;
		name = _name;
		description = _description;
		genes = _genes;
		hash = _gmthash;
	}
	
	public void loadGMTGeneList(Connection _sql, int _id) {
		
		connection = _sql;
		id = _id;
		
		try { 
			
			// create the java statement and execute
			String query = "SELECT * FROM gmtgenelistinfo WHERE id='"+_id+"'";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			
			name = rs.getString("listname");
			description = rs.getString("listdesc");
			hash = rs.getString("hash");
			
			stmt.close();
			
			// create the java statement and execute
			query = "SELECT genemapping.genesymbol AS gene FROM gmtgenelist JOIN genemapping ON gmtgenelist.geneid = genemapping.geneid WHERE gmtgenelist.listid = '"+_id+"'";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			
			genes = new HashSet<String>();
			
			// iterate through the java resultset
			while (rs.next()){
			    String gene = rs.getString("gene");
			    genes.add(gene);
			}
			stmt.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public int writeGMTGeneList(SQLmanager _sql, int _gmtid){
		sql = _sql;
		
		int key = 0;
		
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
			
			pstmt = connection.prepareStatement("INSERT INTO gmtgenelistinfo (listname, listdesc, hash) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, name);
			pstmt.setString(2, description);
			pstmt.setString(3, md5hash(Arrays.toString(genes.toArray(new String[0]))));
			pstmt.addBatch();
			pstmt.executeBatch();
			
			rs = pstmt.getGeneratedKeys();
			key = 0;
			if (rs.next()) {
			    key = rs.getInt(1);
			}

			pstmt = connection.prepareStatement("INSERT INTO gmt (gmtid, gmtgenelistid) VALUES (?, ?)");
			pstmt.setInt(1, _gmtid);
			pstmt.setInt(2, key);
			pstmt.addBatch();
			pstmt.executeBatch();
			
			pstmt = connection.prepareStatement("INSERT INTO gmtgenelist (listid, geneid) VALUES (?, ?)");
			
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
		
		return key;
	}
	
	public String md5hash(String plaintext) {
		String hashtext = "new";
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plaintext.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext.length() < 32 ){
			  hashtext = "0"+hashtext;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return hashtext;
	}
}