<%@page import="es.caib.ripea.war.helper.EnumHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:set var="titol"><spring:message code="expedient.tasca.detall.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
	<table class="table table-bordered">
		<tbody>
			<tr>
				<td><strong><spring:message code="expedient.tasca.form.camp.metaExpedientTasca"/></strong></td>
				<td>${expedientTascaDto.metaExpedientTasca.nom}</td>
			</tr>
			<tr>
				<td><strong><spring:message code="expedient.tasca.form.camp.metaExpedientTascaDescripcio"/></strong></td>
				<td>${expedientTascaDto.metaExpedientTasca.descripcio}</td>
			</tr>
			<tr>
				<td><strong><spring:message code="expedient.tasca.form.camp.responsableCodiActual"/></strong></td>
				<td>${expedientTascaDto.responsableActual.codi}</td>
			</tr>			
			<tr>
				<td><strong><spring:message code="expedient.tasca.form.camp.dataInici"/></strong></td>
				<td><fmt:formatDate value="${expedientTascaDto.dataInici}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
			</tr>
			<tr>
				<td><strong><spring:message code="tasca.list.column.duracio"/></strong></td>
				<td>${expedientTascaDto.duracio}</td>
			</tr>	
			<tr>
				<td><strong><spring:message code="expedient.tasca.form.camp.dataLimit"/></strong></td>
				<td><fmt:formatDate value="${expedientTascaDto.dataLimit}" pattern="dd/MM/yyyy"/></td>
			</tr>					
			<tr>
				<td><strong><spring:message code="expedient.tasca.form.camp.estat"/></strong></td>
				<td>${expedientTascaDto.estat}</td>
			</tr>
			<c:if test="${expedientTascaDto.estat=='REBUTJADA'}">		
				<tr>
					<td><strong><spring:message code="expedient.tasca.form.camp.rebuigMotiu"/></strong></td>
					<td>${expedientTascaDto.motiuRebuig}</td>
				</tr>
			</c:if>
			<tr>
				<td><strong><spring:message code="tasca.list.column.prioritat"/></strong></td>
				<td>
					<c:set var="nomKeyPrioritat">prioritat.enum.${expedientTascaDto.prioritat}</c:set>
					<spring:message code="${nomKeyPrioritat}" />
				</td>
			</tr>
		</tbody>
	</table>
	
	<div id="modal-botons" class="well">
		<a class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>

</body>
</html>
