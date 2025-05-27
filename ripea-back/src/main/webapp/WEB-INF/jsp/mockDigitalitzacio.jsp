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
<c:if test="${idExpedient==null}">
	<a href="<c:url value="/digitalitzacio/recuperarResultatMock/12345"/>" class="btn btn-default">Recuperar resultat del escaneig</a>
</c:if>
<c:if test="${idExpedient!=null}">
	<a href="<c:url value="/modal/digitalitzacio/event/resultatScan/${idExpedient}/${idTransaccio}"/>" class="btn btn-default">Recuperar resultat del escaneig</a>
</c:if>
</body>
</html>
