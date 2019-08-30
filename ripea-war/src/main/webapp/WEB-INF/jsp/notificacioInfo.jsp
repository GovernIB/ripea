<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="notificacio.info.titol"/></title>
	<rip:modalHead/>
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation">
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="notificacio.info.pipella.dades"/></a>
		</li>
		<li role="presentation">
			<a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab"><spring:message code="notificacio.info.pipella.annexos"/> <span class="badge">${fn:length(notificacio.annexos)}</span></a>
		</li>
		<c:if test="${notificacio.error}">
			<li role="presentation">
				<a href="#errors" class="text-danger" aria-controls="errors" role="tab" data-toggle="tab"><span class="fa fa-exclamation-triangle"></span> <spring:message code="notificacio.info.pipella.errors"/></a>
			</li>
		</c:if>
	</ul>
	<br/>
	<div class="tab-content">
		<div class="tab-pane active in" id="dades" role="tabpanel">
			<dl class="dl-horizontal">
				<dt><spring:message code="notificacio.info.camp.document"/></dt>
				<dd>${notificacio.document.nom}</dd>
				<dt><spring:message code="notificacio.info.camp.data.enviament"/></dt>
				<dd><fmt:formatDate value="${notificacio.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				<dt><spring:message code="notificacio.info.camp.tipus"/></dt>
				<dd><spring:message code="notificacio.tipus.enum.${notificacio.tipus}"/></dd>
				<dt><spring:message code="notificacio.info.camp.estat"/></dt>
				<dd><spring:message code="notificacio.estat.enum.${notificacio.estat}"/></dd>
				<dt><spring:message code="notificacio.info.camp.interessat.document"/></dt>
				<dd>${notificacio.interessat.documentNum}</dd>
				<dt><spring:message code="notificacio.info.camp.interessat.nom"/></dt>
				<dd>${notificacio.interessat.nomComplet}</dd>
				<dt><spring:message code="notificacio.info.camp.interessat.email"/></dt>
				<dd>${notificacio.interessat.email}</dd>
				<dt><spring:message code="notificacio.info.camp.interessat.pais"/></dt>
				<dd>${notificacio.interessat.pais}</dd>
				<dt><spring:message code="notificacio.info.camp.interessat.provincia"/></dt>
				<dd>${notificacio.interessat.provincia}</dd>
				<dt><spring:message code="notificacio.info.camp.interessat.municipi"/></dt>
				<dd>${notificacio.interessat.municipi}</dd>
				<dt><spring:message code="notificacio.info.camp.interessat.representant"/></dt>
				<dd>${notificacio.interessat.representantIdentificador}</dd>
				<dt><spring:message code="notificacio.info.camp.assumpte"/></dt>
				<dd>${notificacio.assumpte}</dd>
				<dt><spring:message code="notificacio.info.camp.idioma"/></dt>
				<dd><spring:message code="interessat.idioma.enum.${notificacio.interessat.preferenciaIdioma}"/></dd>
				<c:if test="${not empty notificacio.observacions}">
					<dt><spring:message code="notificacio.info.camp.observacions"/></dt>
					<dd>${notificacio.observacions}</dd>
				</c:if>
			</dl>
		</div>
		<div class="tab-pane" id="annexos" role="tabpanel">
			<c:forEach var="annex" items="${notificacio.annexos}">
				<p>${annex.nom}</p>
			</c:forEach>
		</div>
		<div class="tab-pane" id="errors" role="tabpanel">
			<c:if test="${notificacio.error}">
				<div class="alert well-sm alert-danger alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="notificacio.info.errors.enviament"/>
					<a href="reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="notificacio.info.errors.enviament.reintentar"/></a>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title"><spring:message code="notificacio.info.error.enviament"/></h4>
					</div>
					<div class="panel-body">
						<br/>
						<dl class="dl-horizontal">
							<dt><spring:message code="notificacio.info.camp.error.data.darrer"/></dt>
							<dd><fmt:formatDate value="${notificacio.enviamentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							<dt><spring:message code="notificacio.info.camp.error.intents"/></dt>
							<dd>${notificacio.enviamentCount}</dd>
						</dl>
						<pre style="height:300px; margin: 12px">${notificacio.enviamentErrorDescripcio}</pre>
					</div>
				</div>
			</c:if>
<%--
			<c:if test="${notificacio.processamentError}">
				<div class="alert well-sm alert-danger alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="notificacio.info.errors.processament"/>
					<a href="reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="notificacio.info.errors.processament.reintentar"/></a>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title"><spring:message code="notificacio.info.error.processament"/></h4>
					</div>
					<div class="panel-body">
						<br/>
						<dl class="dl-horizontal">
							<dt><spring:message code="notificacio.info.camp.error.data.darrer"/></dt>
							<dd><fmt:formatDate value="${notificacio.processamentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							<dt><spring:message code="notificacio.info.camp.error.intents"/></dt>
							<dd>${notificacio.processamentCount}</dd>
						</dl>
						<pre style="height:300px; margin: 12px">${notificacio.processamentErrorDescripcio}</pre>
					</div>
				</div>
			</c:if>
--%>
		</div>
	</div>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contenidor/${notificacio.document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
