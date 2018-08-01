<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="escaneig.seleccio.titol"/></title>
	<rip:modalHead/>
</head>
<body>
	<div class="lead" style="margin-bottom: 10px; text-align: center;">
		<h3><spring:message code="escaneig.seleccio.capsalera"/></h3>
		<div class="well" style="max-width: 400px; margin: 0 auto 10px;">
			<c:forEach items="${plugins}" var="plugin">
				<a href="../showscanwebmodule/${plugin.pluginId}/${scanWebId}" class="btn btn-large btn-block btn-primary">
					<b>${plugin.nom}</b><br />
					<small><i>${plugin.descripcioCurta}</i></small>
				</a>
			</c:forEach>
		</div>
	</div>
	<br/>
</body>
</html>
