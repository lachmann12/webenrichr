<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
  
<%@ page import="jsp.*" %>
<%@ page import="java.util.TreeSet" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Login Success Page</title>
</head>
<body>

<%@include file="login.jsp" %>
<hr>

<h2>Enrichment results</h2>

This is where the results will be.

<%
EnrichmentResults enrichment = (EnrichmentResults) session.getAttribute("enrichment");
out.println("<br>List ID: "+enrichment.listid+"<br>Description: "+enrichment.description+"<br>Number of genes: "+enrichment.genes.size()+"<br><hr>");

TreeSet<String> sortedGenes = new TreeSet<String>(enrichment.genes);
for(String gene : sortedGenes){
	out.println("<a href=\"http://amp.pharm.mssm.edu/archs4/search/genepage.php?search=go&gene="+gene+"\" target=\"_blank\">"+gene+"</a> - ");
}
out.println("<hr>");





%>


<%@include file="footer.jsp" %>
</body>
</html>