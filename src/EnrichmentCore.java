

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jsp.EnrichmentResults;
import jsp.Overlap;

/**
 * Servlet implementation class Test
 */
@WebServlet("/enrichment")
public class EnrichmentCore extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public FastFisher f;
	
	public boolean initialized = false;
	
	public HashSet<GMT> gmts;
	public HashMap<String, GeneBackground> background;
	
	public HashMap<String, Integer> symbolToId = new HashMap<String, Integer>();
	public HashMap<Integer, String> idToSymbol = new HashMap<Integer, String>();
	
	public Connection connection;
	public SQLmanager sql;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EnrichmentCore() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		
		// TODO Auto-generated method stub
		System.out.println("init2");
		f = new FastFisher(40000);
		
		sql = new SQLmanager();
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database+"?rewriteBatchedStatements=true", sql.user, sql.password);
		
			System.out.println("Start buffering libraries");
			long time = System.currentTimeMillis();
			loadGenemapping();
			loadGMT();
			loadBackground();
			System.out.println("Background load: "+background.size()+"\nGMTs loaded: "+gmts.size()+"\nElapsed time: "+(System.currentTimeMillis() - time));
			
			connection.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//response.getWriter().append("My servlet served at: "+fish.getFish()+" : ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
	    	String user = (String) session.getAttribute("user");
	    	String role = (String) session.getAttribute("role");
	    	
	    	String description = request.getParameter("description");
		String genetext = request.getParameter("text");
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database+"?rewriteBatchedStatements=true", sql.user, sql.password);
			
			UserGeneList list = saveUserList(user, description, genetext);
			
			long time = System.currentTimeMillis();
			
			HashMap<Integer, HashMap<Integer, Overlap>> enrichment = calculateEnrichment(list);
			EnrichmentResults enrichmentResult = new EnrichmentResults(list.id, list.description, list.genes, enrichment);
			
			
			session.setAttribute("enrichment", enrichmentResult);
			
			System.out.println("Total time: "+(System.currentTimeMillis() - time));
			connection.close();
			
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/enrichment.jsp");
			PrintWriter out = response.getWriter();
			out.println("<font color=red>Elapsed time: "+(System.currentTimeMillis() - time)+"</font>");
			rd.include(request, response);
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashMap<Integer, HashMap<Integer, Overlap>> calculateEnrichment(UserGeneList _list) {
		
		HashMap<Integer, HashMap<Integer, Overlap>> enrichment = new HashMap<Integer, HashMap<Integer, Overlap>>();
		int counter = 0;
		for(GMT gmt : gmts) {
			HashMap<Integer, Overlap> gmtenrichment = new HashMap<Integer, Overlap>();
			for(GMTGeneList gmtlist : gmt.genelists) {
				HashSet<String> overlap = new HashSet<String>();
				if(_list.genearray.length < gmtlist.genearray.length) {
					for(int i=0; i< _list.genearray.length; i++) {
						if(gmtlist.genes.contains(_list.genearray[i])) {
							overlap.add(_list.genearray[i]);
						}
					}
				}
				else {
					for(int i=0; i< gmtlist.genearray.length; i++) {
						if(_list.genes.contains(gmtlist.genearray[i])) {
							overlap.add(gmtlist.genearray[i]);
						}
					}
				}
				
				int numGenelist = _list.genearray.length;
	    			int totalBgGenes = 20000;
	    			int gmtListSize =  gmtlist.genearray.length;
	    			int numOverlap = overlap.size();
	    			//double oddsRatio = (numOverlap*1.0/(totalInputGenes - numOverlap))/(numGenelist*1.0/(totalBgGenes - numGenelist));
	    			double pvalue = f.getRightTailedP(numOverlap,(gmtListSize - numOverlap), numGenelist, (totalBgGenes - numGenelist));	
	    			
	    			if(pvalue < 0.05) {
	    				counter++;
	    			}
	    			
	    			Overlap over = new Overlap(overlap, pvalue);
	    			gmtenrichment.put(gmtlist.id, over);
			}
			enrichment.put(gmt.id,gmtenrichment);
			
		}
		
		System.out.println("Significant overlaps: "+counter);
		
		return enrichment;
	}
	
	public void loadBackground() {
		background = new HashMap<String, GeneBackground>();
		HashSet<Integer> backgroundids = new HashSet<Integer>();
		
		try { 
			

			// create the java statement and execute
			String query = "SELECT id FROM genebackgroundinfo";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				int id = rs.getInt("id");
				backgroundids.add(id);
			}
			stmt.close();
			
			for(Integer i : backgroundids) {
				GeneBackground bg = new GeneBackground();
		        bg.load(sql, (int)i);
		        background.put(bg.name, bg);
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public UserGeneList saveUserList(String _user, String _description, String _genetext) {
		
	    UserGeneList list = null;
		try { 
			int id = 0;

			if(_user != null) {
				
				// create the java statement and execute
				String query = "SELECT id FROM userinfo WHERE username='"+_user+"'";
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
	
				while(rs.next()) {
					id = rs.getInt("id");
				}
				stmt.close();
			}
			
			String[] lines = _genetext.split("\n");
	        HashSet<String> genes = new HashSet<String>();
	        
	        for(String l : lines) {
	        		String gene = l.toUpperCase().trim();
	        		if(symbolToId.keySet().contains(gene)) {
	        			genes.add(gene);
	        		}
	        }
	        
	        list = new UserGeneList(id, _description, genes);
			list.write(id, this, connection);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void loadGMT() {
		
		HashSet<Integer> gmtids = new HashSet<Integer>();
		gmts = new HashSet<GMT>();
		
		try { 
			// create the java statement and execute
			String query = "SELECT id FROM gmtinfo";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				int id = rs.getInt("id");
				gmtids.add(id);
			}
			stmt.close();
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		for(Integer i : gmtids) {
			System.out.println("Load "+i);
			GMT gmt = new GMT(this);
	        gmt.loadGMT(sql, (int)i);
	        gmts.add(gmt);
		}
	}
	
	
	public void loadGenemapping(){
		
		// create the java statement and execute
		
		try {
			
			String query = "SELECT * FROM genemapping";
			
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while (rs.next()){
			    String gene = rs.getString("genesymbol");
			    int geneid = rs.getInt("geneid");
			    symbolToId.put(gene, geneid);
			    idToSymbol.put(geneid, gene);
			}
			stmt.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
