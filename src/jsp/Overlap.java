package jsp;
import java.util.HashSet;

public class Overlap {
		public HashSet<String> overlap;
		public double pval = 0;
		
		public Overlap(HashSet<String> _overlap, double _pval) {
			pval = _pval;
			overlap = _overlap;
		}
	}
	