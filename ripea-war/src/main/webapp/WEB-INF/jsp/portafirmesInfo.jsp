<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="firma.info.titol"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation">
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="firma.info.pipella.dades"/></a>
		</li>
		<%--li role="presentation">
			<a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab"><spring:message code="firma.info.pipella.annexos"/></a>
		</li--%>
		<c:if test="${portafirmes.error}">
			<li role="presentation">
				<a href="#errors" class="text-danger" aria-controls="errors" role="tab" data-toggle="tab"><span class="fa fa-exclamation-triangle"></span> <spring:message code="firma.info.pipella.errors"/></a>
			</li>
		</c:if>
	</ul>
	<br/>
	<div class="tab-content">
		<div class="tab-pane active in" id="dades" role="tabpanel">
			<table class="table table-striped table-bordered">
			<tbody>
				<tr>
					<td><strong><spring:message code="firma.info.camp.document"/></strong></td>
					<td>${portafirmes.document.nom}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="firma.info.camp.assumpte"/></strong></td>
					<td>${portafirmes.assumpte}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="firma.info.camp.data.enviament"/></strong></td>
					<td><fmt:formatDate value="${portafirmes.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="firma.info.camp.estat"/></strong></td>
					<td><spring:message code="portafirmes.estat.enum.${portafirmes.estat}"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="firma.info.camp.prioritat"/></strong></td>
					<td><spring:message code="portafirmes.prioritat.enum.${portafirmes.prioritat}"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="firma.info.camp.data.cad"/></strong></td>
					<td><fmt:formatDate value="${portafirmes.caducitatData}" pattern="dd/MM/yyyy"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="firma.info.camp.document.tipus"/></strong></td>
					<td>${portafirmes.documentTipus}</td>
				</tr>
				<c:if test="${not empty portafirmes.destinatari}">
					<tr>
						<td><strong><spring:message code="firma.info.camp.responsables"/></strong></td>
						<td>${portafirmes.destinatari}</td>
					</tr>
				</c:if>
				<c:if test="${not empty portafirmes.fluxTipus}">
					<tr>
						<td><strong><spring:message code="firma.info.camp.flux.tipus"/></strong></td>
						<td><spring:message code="metadocument.fluxtip.enum.${portafirmes.fluxTipus}"/></td>
					</tr>
				</c:if>
				<c:if test="${not empty portafirmes.fluxId}">
					<tr>
						<td><strong><spring:message code="firma.info.camp.flux.id"/></strong></td>
						<td>${portafirmes.fluxId}</td>
					</tr>
				</c:if>
				<c:if test="${not empty portafirmes.portafirmesId}">
					<tr>
						<td><strong><spring:message code="firma.info.camp.portafirmes.id"/></strong></td>
						<td>${portafirmes.portafirmesId}</td>
					</tr>
				</c:if>
				<c:if test="${portafirmes.estat == 'ENVIAT'}">
					<tr>
						<td colspan="2" style="text-align:right">
							<a href="<rip:modalUrl value="/document/${portafirmes.document.id}/portafirmes/cancel"/>" data-confirm="<spring:message code="firma.info.accio.cancel.confirmacio"/>" class="btn btn-default"><span class="fa fa-times"></span> <spring:message code="firma.info.accio.cancel"/></a>
						</td>
					</tr>
				</c:if>
			</tbody>
			</table>
		</div>
		<%--div class="tab-pane" id="annexos" role="tabpanel">
		</div--%>
		<div class="tab-pane" id="errors" role="tabpanel">
			<c:if test="${portafirmes.error}">
				<div class="alert well-sm alert-danger alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>
					<c:choose>
						<c:when test="${portafirmes.estat == 'PENDENT'}">
							<spring:message code="firma.info.errors.enviament"/>
						</c:when>
						<c:when test="${portafirmes.estat == 'ENVIAT'}">
							<spring:message code="firma.info.errors.processament"/>
						</c:when>
						<c:when test="${portafirmes.estat == 'PROCESSAT'}">
						</c:when>
						<c:when test="${portafirmes.estat == 'REBUTJAT'}">
						</c:when>
						<c:when test="${portafirmes.estat == 'CANCELAT'}">
							<spring:message code="firma.info.errors.cancelacio"/>
						</c:when>
					</c:choose>
					<a href="../portafirmes/reintentar" class="btn btn-xs btn-default pull-right">
						<span class="fa fa-refresh"></span>
						<spring:message code="firma.info.errors.reintentar"/>
					</a>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title"><spring:message code="firma.info.error.enviament"/></h4>
					</div>
					<div class="panel-body">
						<br/>
						<dl class="dl-horizontal">
							<dt><spring:message code="firma.info.camp.error.data.darrer"/></dt>
							<dd><fmt:formatDate value="${portafirmes.intentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							<dt><spring:message code="firma.info.camp.error.intents"/></dt>
							<dd>${portafirmes.intentNum}</dd>
						</dl>
						<pre style="height:300px; margin: 12px">${portafirmes.errorDescripcio}</pre>
					</div>
				</div>
			</c:if>
		</div>
	</div>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contenidor/${portafirmes.document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
