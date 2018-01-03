
<%
//allow access only if session exists
String user = null;
String role = null;
if(session.getAttribute("user") == null){
	
	%>
	<form action="LoginServlet" method="post">
	Username: <input type="text" name="user">
	Password: <input type="password" name="pwd">
	<input type="submit" value="Login">
	</form>
	<%
	
}else{
	user = (String) session.getAttribute("user");
	role = (String) session.getAttribute("role");

	String userName = null;
	String sessionID = null;
	Cookie[] cookies = request.getCookies();
	if(cookies !=null){
		for(Cookie cookie : cookies){
			if(cookie.getName().equals("user")) userName = cookie.getValue();
			if(cookie.getName().equals("role")) role = cookie.getValue();
			if(cookie.getName().equals("JSESSIONID")) sessionID = cookie.getValue();
		}
		%>
		<form action="LogoutServlet" method="post">
		Hi, <%=userName %>!
		<input type="submit" value="Logout" >
		</form>
		<%
	}
}
%>

