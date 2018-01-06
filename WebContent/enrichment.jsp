<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
  
<%@ page import="jsp.*" %>
<%@ page import="serv.*" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>

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
HashSet<GMT> gmts = (HashSet<GMT>) session.getAttribute("gmtinfo");

out.println("<br>List ID: "+enrichment.listid+"<br>Description: "+enrichment.description+"<br>Number of genes: "+enrichment.genes.size()+"<br><hr>");

TreeSet<String> sortedGenes = new TreeSet<String>(enrichment.genes);
for(String gene : sortedGenes){
	out.println("<a href=\"http://amp.pharm.mssm.edu/archs4/search/genepage.php?search=go&gene="+gene+"\" target=\"_blank\">"+gene+"</a> - ");
}
out.println("<hr>");

Integer[] ids = enrichment.enrichment.keySet().toArray(new Integer[0]);
HashMap<Integer, GMT> gmtset = new HashMap<Integer, GMT>();
for(GMT gg : gmts){
	gmtset.put(gg.id, gg);
}

for(Integer id : ids){
	HashMap<Integer, Overlap> over = enrichment.enrichment.get(id);
	Integer[] lids = over.keySet().toArray(new Integer[0]);
	
	out.println("<hr>");
	out.println("GMT: "+gmtset.get(id).id+" - "+gmtset.get(id).name+" - "+gmtset.get(id).description+"<br>");
	out.println("<hr>");
	
	for(Integer i : lids){
		if(over.get(i).pval < 0.0001){
			out.println(gmtset.get(id).genelists.get(i)+" - "+over.get(i).overlap.size()+" - "+over.get(i).pval+"<br>");
		}
	}
	
}



%>


<%@include file="footer.jsp" %>
</body>
</html>