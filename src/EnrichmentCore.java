

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Core")
public class EnrichmentCore extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public FastFisher f;
	
	public boolean initialized = false;
	
	public HashSet<GMT> gmts;
	public HashMap<String, GeneBackground> background;
	
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
		// TODO Auto-generated method stub
		System.out.println("init2");
		f = new FastFisher(40000);
		
		System.out.println("Start buffering libraries");
		long time = System.currentTimeMillis();
		loadGMT();
		loadBackground();
		System.out.println("Background load: "+background.size()+"\nGMTs loaded: "+gmts.size()+"\nElapsed time: "+(System.currentTimeMillis() - time));		
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
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void loadBackground() {
		background = new HashMap<String, GeneBackground>();
		HashSet<Integer> backgroundids = new HashSet<Integer>();
		
		SQLmanager sql = new SQLmanager();

		Connection connection;

		try { 
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database, sql.user, sql.password);

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
	
	public void loadGMT() {
		SQLmanager sql = new SQLmanager();

		Connection connection;
		HashSet<Integer> gmtids = new HashSet<Integer>();
		gmts = new HashSet<GMT>();
		
		try { 
			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database, sql.user, sql.password);

			// create the java statement and execute
			String query = "SELECT id FROM gmtinfo";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				int id = rs.getInt("id");
				gmtids.add(id);
			}
			stmt.close();
			
			connection.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		for(Integer i : gmtids) {
			GMT gmt = new GMT();
	        gmt.loadGMT(sql, (int)i);
	        gmts.add(gmt);
		}
		
		initialized = true;
	}
}
