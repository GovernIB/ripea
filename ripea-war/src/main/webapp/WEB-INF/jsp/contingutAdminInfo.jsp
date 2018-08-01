<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="contingut.admin.info.titol"/></title>
	<rip:modalHead/>
</head>
<body>
	<rip:blocContenidorPath contingut="${contingut}"/>
	<c:if test="${contingut.registre && contingut.procesEstat == 'ERROR'}">
		<ul class="nav nav-tabs" role="tablist">
			<li class="active" role="presentation"><a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.informacio"/></a>
			</li>
			<c:if test="${contingut.procesEstat != 'NO_PROCES'}">
				<li role="presentation">
					<a href="#proces" aria-controls="proces" role="tab" data-toggle="tab">
						<spring:message code="registre.detalls.pipella.proces"/>
						<c:if test="${contingut.procesEstat == 'ERROR'}"><span class="fa fa-warning text-danger"></span></c:if>
					</a>
				</li>
			</c:if>
		</ul>
		<br/>
		<div class="tab-content">
			<div class="tab-pane active in" id="informacio" role="tabpanel">
	</c:if>
	<dl class="dl-horizontal">
		<dt><spring:message code="contingut.admin.info.camp.contingut"/></dt>
		<dd>
			<rip:blocIconaContingut contingut="${contingut}"/>
			${contingut.nom}
		</dd>
		<c:if test="${contingut.expedient or contingut.document and not empty contingut.metaNode}">
			<dt><spring:message code="contingut.admin.info.camp.metanode"/></dt>
			<dd>${contingut.metaNode.nom}</dd>
		</c:if>
		<c:if test="${contingut.expedient}">
			<dt><spring:message code="contingut.admin.info.camp.arxiu"/></dt>
			<dd>${contingut.arxiu.nom}</dd>
			<dt><spring:message code="contingut.admin.info.camp.numero"/></dt>
			<dd>${contingut.numero}</dd>
			<dt><spring:message code="contingut.admin.info.camp.estat"/></dt>
			<dd><spring:message code="expedient.estat.enum.${contingut.estat}"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.versio"/></dt>
			<dd>${contingut.ntiVersion}</dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.identificador"/></dt>
			<dd style="overflow:hidden;text-overflow:ellipsis" title="${contingut.ntiIdentificador}">${contingut.ntiIdentificador}</dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.organ"/></dt>
			<dd>${contingut.ntiOrganoDescripcio}</dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.data.obertura"/></dt>
			<dd><fmt:formatDate value="${contingut.ntiFechaApertura}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.classificacio"/></dt>
			<dd>${contingut.ntiClasificacionSia}</dd>
		</c:if>
		<c:if test="${contingut.document}">
			<dt><spring:message code="contingut.admin.info.camp.data"/></dt>
			<dd><fmt:formatDate value="${contingut.data}" pattern="dd/MM/yyyy"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.estat"/></dt>
			<dd><spring:message code="document.estat.enum.${contingut.estat}"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.versio"/></dt>
			<dd>${contingut.ntiVersion}</dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.identificador"/></dt>
			<dd style="overflow:hidden;text-overflow:ellipsis" title="${contingut.ntiIdentificador}">${contingut.ntiIdentificador}</dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.organ"/></dt>
			<dd>${contingut.ntiOrganoDescripcio}</dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.data.captura"/></dt>
			<dd><fmt:formatDate value="${contingut.dataCaptura}" pattern="dd/MM/yyyy"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.origen"/></dt>
			<dd><spring:message code="document.nti.origen.enum.${contingut.ntiOrigen}"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.estat.elab"/></dt>
			<dd><spring:message code="document.nti.estela.enum.${contingut.ntiEstadoElaboracion}"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.eni.tipus.doc"/></dt>
			<dd><spring:message code="document.nti.tipdoc.enum.${contingut.ntiTipoDocumental}"/></dd>
			<c:if test="${not empty contingut.ntiIdDocumentoOrigen}">
				<dt><spring:message code="contingut.admin.info.camp.eni.doc.origen.id"/></dt>
				<dd>${contingut.ntiIdDocumentoOrigen}</dd>
			</c:if>
		</c:if>
		<c:if test="${contingut.registre}">
			<dt><spring:message code="registre.detalls.camp.tipus"/></dt><dd><spring:message code="registre.anotacio.tipus.enum.${contingut.registreTipus}"/></dd>
			<dt><spring:message code="contingut.admin.info.camp.estat"/></dt><dd>${contingut.procesEstat}</dd>
			<dt><spring:message code="registre.detalls.camp.numero"/></dt><dd>${contingut.identificador}</dd>
			<dt><spring:message code="registre.detalls.camp.data"/></dt><dd><fmt:formatDate value="${contingut.data}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
			<c:if test="${contingut.registreTipus == 'ENTRADA'}">
				<dt><spring:message code="registre.detalls.camp.desti"/></dt><dd>${contingut.unitatAdministrativa}</dd>				
			</c:if>
			<c:if test="${contingut.registreTipus == 'SORTIDA'}">
				<dt><spring:message code="registre.detalls.camp.origen"/></dt><dd>${contingut.unitatAdministrativa}</dd>
			</c:if>
			<dt><spring:message code="registre.detalls.camp.entitat"/></dt><dd>${contingut.entitatDescripcio} (${contingut.entitatCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.oficina"/></dt><dd>${contingut.oficinaDescripcio} (${contingut.oficinaCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.llibre"/></dt><dd>${contingut.llibreDescripcio} (${contingut.llibreCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.extracte"/></dt><dd>${contingut.extracte}</dd>
			<dt><spring:message code="registre.detalls.camp.assumpte.tipus"/></dt><dd>${contingut.assumpteTipusDescripcio} (${contingut.assumpteTipusCodi})</dd>
			<dt><spring:message code="registre.detalls.camp.idioma"/></dt><dd>${contingut.idiomaDescripcio} (${contingut.idiomaCodi})</dd>
			<c:if test="${not empty contingut.transportTipusCodi}">
				<dt><spring:message code="registre.detalls.camp.transport.tipus"/></dt><dd>${contingut.transportTipusDescripcio} (${contingut.transportTipusCodi})</dd>
			</c:if>
			<c:if test="${not empty contingut.transportNumero}">
				<dt><spring:message code="registre.detalls.camp.transport.numero"/></dt><dd>${contingut.transportNumero}</dd>
			</c:if>
			<c:if test="${not empty contingut.usuariCodi}">
				<dt><spring:message code="registre.detalls.camp.usuari"/></dt><dd>${contingut.usuariNom} (${contingut.usuariCodi})</dd>
			</c:if>
			<c:if test="${not empty contingut.aplicacioCodi}">
				<dt><spring:message code="registre.detalls.camp.aplicacio"/></dt><dd>${contingut.aplicacioCodi} ${contingut.aplicacioVersio}</dd>
			</c:if>
			<c:if test="${not empty contingut.expedientNumero}">
				<dt><spring:message code="registre.detalls.camp.expedient"/></dt><dd>${contingut.expedientNumero}</dd>
			</c:if>
			<c:if test="${not empty contingut.observacions}">
				<dt><spring:message code="registre.detalls.camp.observacions"/></dt><dd>${contingut.observacions}</dd>
			</c:if>
			<dt><spring:message code="registre.detalls.camp.ripea.alta"/></dt><dd><fmt:formatDate value="${contingut.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
		</c:if>
	</dl>
	<c:if test="${contingut.registre && contingut.procesEstat == 'ERROR'}">
			</div>
			<div class="tab-pane" id="proces" role="tabpanel">
				<c:if test="${contingut.procesEstat == 'ERROR'}">
					<div class="alert well-sm alert-danger alert-dismissable">
						<span class="fa fa-exclamation-triangle"></span>
						<spring:message code="registre.detalls.info.errors"/>
						<a href="../${contingut.pare.id}/registre/${contingut.id}/reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentar"/></a>
					</div>
				</c:if>
				<dl class="dl-horizontal">
					<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
					<dd>${contingut.procesEstat}</dd>
					<dt><spring:message code="registre.detalls.camp.proces.data"/></dt>
					<dd><fmt:formatDate value="${contingut.procesData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
					<dt><spring:message code="registre.detalls.camp.proces.intents"/></dt>
					<dd>${contingut.procesIntents}</dd>
				</dl>
				<c:if test="${contingut.procesEstat == 'ERROR'}">
					<pre style="height:300px">${contingut.procesError}</pre>
				</c:if>
			</div>
		</div>
	</c:if>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contingutAdmin"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>