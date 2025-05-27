<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<rip:modalHead/>
</head>	
<body>
Aquesta es una finestra ficticia que nomes hauria de aparéixer en entorns de desenvolupament.
<a href="<c:url value="/digitalitzacio/recuperarResultatMock/12345"/>" class="btn btn-default">Recuperar resultat del escaneig</a>
</body>
</html>
