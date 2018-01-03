import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/createuser")

public class CreateUser extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		
    		String firstname = request.getParameter("firstname");
    		String lastname = request.getParameter("lastname");
    		String email = request.getParameter("email");
    		String password = request.getParameter("pwd");
    		String username = request.getParameter("user");
    		String salt = randomAlphaNumeric(20);

    		SQLmanager sql = new SQLmanager();
    		
    		Connection connection;
    		try { 
    			connection = DriverManager.getConnection("jdbc:mysql://"+sql.database, sql.user, sql.password);

    			// create the java statement and execute
    			String query = "SELECT * FROM userinfo WHERE username='"+username+"'";
    			Statement stmt = connection.createStatement();
    			ResultSet rs = stmt.executeQuery(query);
    			
    			if(rs.next()) {
    				RequestDispatcher rd = getServletContext().getRequestDispatcher("/createuser.html");
    				PrintWriter out= response.getWriter();
    				out.println("<font color=red>Username already in use.</font>");
    				rd.include(request, response);
        		}
    			else {
    				query = " INSERT INTO userinfo (firstname, lastname, email, password, username, salt)"
    				        + " VALUES (?, ?, ?, ?, ?, ?);";

    				// create the mysql insert pre-paredstatement
    				PreparedStatement preparedStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    				preparedStmt.setString(1, firstname);
    				preparedStmt.setString(2, lastname);
    				preparedStmt.setString(3, email);
    				preparedStmt.setString(4, md5hash(password+salt));
    				preparedStmt.setString(5, username);
    				preparedStmt.setString(6, salt);
    				preparedStmt.executeQuery();
    				
    				HttpSession session = request.getSession();
    				session.setAttribute("user", username);
    				//setting session to expiry in 30 mins
    				session.setMaxInactiveInterval(30*60);
    				Cookie userName = new Cookie("user", firstname);
    				userName.setMaxAge(30*60);
    				response.addCookie(userName);
    				response.sendRedirect("LoginSuccess.jsp");
    			}
    			stmt.close();

    			connection.close();
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    		
    		
    		
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		System.out.println("nothing here");
	}
    

	
	public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
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