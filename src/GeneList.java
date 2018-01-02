import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.util.HashSet;

public class GeneList {

	public HashSet<String> genes = new HashSet<String>();
	public String description = "";
	public String name = "";
	public int id = 1;
	public String hash = "";
	public SQLmanager sql;
	Connection  connection;
	
	public GeneList() {
		
	}
	
	public GeneList(int _id, String _name, String _description, String _gmthash, HashSet<String> _genes) {
		id = _id;
		name = _name;
		description = _description;
		genes = _genes;
		hash = _gmthash;
	}
	
	public String toString() {
		return id+" - "+name+" - "+description+" - size: "+genes.size();
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
