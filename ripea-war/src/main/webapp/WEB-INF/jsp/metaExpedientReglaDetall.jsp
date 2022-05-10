<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>

	<title><spring:message code="metaexpedient.regla.detalls.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
    <c:if test="${metaExpedient.crearReglaDistribucioEstat=='ERROR'}">
		<a href="<c:url value="/modal/metaExpedient/${metaExpedient.id}/reglaCrear"/>" class="btn btn-xs btn-default pull-right processarBtn" style="margin-right: 10px;"><span class="fa fa-refresh"></span> <spring:message code="metaexpedient.regla.detalls.reintentar"/></a>
    </c:if>	
	<div class="processamentInfo">
		<dl class="dl-horizontal">
			<dt><spring:message code="metaexpedient.regla.detalls.estat"/></dt>
			<dd>${metaExpedient.crearReglaDistribucioEstat}</dd>
		</dl>
		<c:choose>
			<c:when test="${metaExpedient.crearReglaDistribucioEstat=='ERROR'}">
				<pre style="height:300px"><c:out value="${metaExpedient.crearReglaDistribucioError}" escapeXml="true"/></pre>
			</c:when>
			<c:otherwise>
				<div style="min-height: 20px;"></div>
			</c:otherwise>
		</c:choose>
	</div>
	<div id="modal-botons">
		<a href="<c:url value="/metaExpedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
	</div>
</body>
</html>
