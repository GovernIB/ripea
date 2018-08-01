<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="publicacio.info.titol"/></title>
	<rip:modalHead/>
</head>
<body>
	<dl class="dl-horizontal">
		<dt><spring:message code="publicacio.info.camp.document"/></dt>
		<dd>${publicacio.document.nom}</dd>
		<dt><spring:message code="publicacio.info.camp.data.enviament"/></dt>
		<dd><fmt:formatDate value="${publicacio.dataEnviament}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
		<dt><spring:message code="publicacio.info.camp.estat"/></dt>
		<dd><spring:message code="publicacio.estat.enum.${publicacio.estat}"/></dd>
		<dt><spring:message code="publicacio.info.camp.tipus"/></dt>
		<dd><spring:message code="publicacio.tipus.enum.${publicacio.tipus}"/></dd>
		<dt><spring:message code="publicacio.info.camp.assumpte"/></dt>
		<dd>${publicacio.assumpte}</dd>
		<c:if test="${not empty publicacio.observacions}">
			<dt><spring:message code="publicacio.info.camp.observacions"/></dt>
			<dd>${publicacio.observacions}</dd>
		</c:if>
	</dl>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contenidor/${publicacio.document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
