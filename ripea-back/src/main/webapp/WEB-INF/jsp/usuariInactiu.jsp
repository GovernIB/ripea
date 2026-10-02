<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%-- Pàgina per a l'usuari donat de baixa. Pàgina pública i sense decorar: no depèn de cap dada de sessió. --%>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<title><spring:message code="usuari.inactiu.titol"/></title>
	<link rel="shortcut icon" href="<c:url value="/img/favicon.png"/>" type="image/x-icon"/>
	<link href="<c:url value="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css"/>" rel="stylesheet"/>
</head>
<body>
	<main class="container" style="margin-top: 80px; max-width: 640px;">
		<div class="alert alert-warning" role="alert">
			<h1 class="h4"><span class="fa fa-user-times" aria-hidden="true"></span> <spring:message code="usuari.inactiu.titol"/></h1>
			<p><spring:message code="usuari.inactiu.missatge"/></p>
		</div>
		<a class="btn btn-default" href="<c:url value="/usuari/logout"/>">
			<span class="fa fa-sign-out" aria-hidden="true"></span> <spring:message code="usuari.inactiu.sortir"/>
		</a>
	</main>
</body>
</html>
