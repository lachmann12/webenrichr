import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class UserGeneList extends GeneList{

	public UserGeneList() {
		
	}
	
	public UserGeneList(int _id, String _description, String _gmthash, HashSet<String> _genes) {
		id = _id;
		description = _description;
		genes = _genes;
		hash = _gmthash;
	}
	
	public void write(int _userid) {
		
		SQLmanager sql = new SQLmanager();
		int key = 0;

		try { 
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database, sql.user, sql.password);

			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO usergenelistinfo (userid, description, hash) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, _userid);
			pstmt.setString(2, description);
			pstmt.setString(3, md5hash(Arrays.toString(genes.toArray(new String[0]))));
			pstmt.addBatch();
			pstmt.executeBatch();
			
			ResultSet rs = pstmt.getGeneratedKeys();
			key = 0;
			if (rs.next()) {
			    key = rs.getInt(1);
			}
			
			HashMap<String, Integer> genemapping = getGenemapping();
			pstmt = connection.prepareStatement("INSERT INTO usergenelist (listid, geneid) VALUES (?, ?)");
			
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
