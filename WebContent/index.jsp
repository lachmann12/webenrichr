<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Enrichment</title>
</head>
<body>

<%@include file="login.jsp" %>

<hr>

<h2>Geneset Enrichment</h2>

<form action="enrichment" method="post">
<input type="hidden" name="mock" value="nothing"/>
<textarea rows="10" cols="30" name="text" default="Enter gene symbols">
Nsun3
Polrmt
Nlrx1
Sfxn5
Zc3h12c
Slc25a39
Arsg
Defb29
Ndufb6
Zfand1
Tmem77
5730403B10Rik
RP23-195K8.6
Tlcd1
Psmc6
Slc30a6
LOC100047292
Lrrc40
Orc5l
Mpp7
Unc119b
Prkaca
Tcn2
Psmc3ip
</textarea> <- Gene symbols<br>
<input type="text" name="description" /> <- Description<br>
<input type="submit" />
</form>

<%@include file="footer.jsp" %>

</body>
</html>