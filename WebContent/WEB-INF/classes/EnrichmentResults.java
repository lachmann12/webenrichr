import java.util.HashMap;

public class EnrichmentResults {
	
	public UserGeneList userlist;
	public HashMap<Integer, HashMap<Integer, Overlap>> enrichment;
	
	public EnrichmentResults(UserGeneList _list, HashMap<Integer, HashMap<Integer, Overlap>> _enrichment) {
		userlist = _list;
		enrichment = _enrichment;
	}
}
