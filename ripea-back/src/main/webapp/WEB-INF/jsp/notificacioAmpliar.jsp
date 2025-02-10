<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="notificacio.info.ampliar"/></title>
	<rip:modalHead/>
</head>
<body>

	<c:set var="formAction"><rip:modalUrl value="/document/notificacio/${documentNotificacioDto.id}/ampliar"/></c:set>
	
	<form:form id="documentNotificacioDto"
		action="${formAction}"
		method="post"
		cssClass="form-horizontal"
		commandName="documentNotificacioDto"
		role="form"
		enctype="multipart/form-data">
		
		<div class="row"><div class="col-xs-12"><h4><spring:message code="notificacio.info.panel.ampliar"/></h4></div></div>
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title"><strong><spring:message code="notificacio.info.panel.ampliar.enviaments"/></strong></h3>
			</div>
			<table class="table table-bordered">
				<thead>
					<tr>
						<td width="*"><strong><spring:message code="notificacio.info.camp.interessat"/></strong></td>
						<td width="15%"><strong><spring:message code="notificacio.info.camp.estat"/></strong></td>
						<td width="10%"><strong><spring:message code="notificacio.info.camp.num.registre"/></strong></td>
						<td width="10%"><strong><spring:message code="notificacio.info.camp.data.registre"/></strong></td>
						<td width="10%"><strong><spring:message code="notificacio.info.camp.dies"/></strong></td>
						<td width="30%"><strong><spring:message code="notificacio.info.camp.motiu"/></strong></td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="enviament" varStatus="vs" items="${documentNotificacioDto.documentEnviamentInteressats}">
						<tr>
							<td>
								${enviament.interessat.nomComplet}
								<form:hidden path="documentEnviamentInteressats[${vs.index}].enviamentReferencia"/>
								<form:hidden path="documentEnviamentInteressats[${vs.index}].id"/>
							</td>
							<td><c:if test="${enviament.enviamentDatatEstat!=null}"><spring:message code="notificacio.enviamentEstat.enum.${enviament.enviamentDatatEstat}"/></c:if></td>
							<td>${enviament.registreNumeroFormatat}</td>
							<td><fmt:formatDate value="${enviament.registreData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
							<td><form:input path="documentEnviamentInteressats[${vs.index}].diesAmpliacio" class="form-control" data-m-dec="0" data-toggle="autonumeric"/></td>
							<td><form:input path="documentEnviamentInteressats[${vs.index}].motiu" class="form-control"/></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-floppy-o"></span>&nbsp;<spring:message code="comu.boto.enviar"/></button>
			<a href="<c:url value="/contenidor/${documentId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
			
	</form:form>
</body>
</html>