<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Login Success Page</title>
</head>
<body>

<%@include file="login.jsp" %>

<hr>

<h2>GMT upload into database</h2>

<form action="enrichment" method="post">
<input type="hidden" name="mock" value="nothing"/>
<textarea rows="10" cols="30" name="text" default="Enter gene symbols"></textarea> <- Gene symbols<br>
<input type="text" name="description" /> <- Description<br>
<input type="submit" />
</form>

<%@include file="footer.jsp" %>

</body>
</html>