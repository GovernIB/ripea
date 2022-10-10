<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:set var="titol"><spring:message code="expedient.massiu.tancar.titol"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
</head>
<body>


	<c:set var="formAction"><rip:modalUrl value="/massiu/tancament/tancar"/></c:set>

	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientMassiuTancamentCommand">
		<rip:inputTextarea name="motiu" textKey="contingut.expedient.tancar.form.camp.motiu" required="true"/>
		<c:if test="${hasEsborranys}">
			<div class="alert well-sm alert-info">
				<span class="fa fa-info-circle"></span>
				<spring:message code="contingut.expedient.tancar.esborranys.info.multiple" />
			</div>
		</c:if>

		<c:forEach items="${expedients}" varStatus="vs" var="expedient">
			<form:hidden path="expedientsTancar[${vs.index}].id" value="${expedient.id}"/>
			<c:if test="${expedient.hasNoFirmatsOAmbFirmaInvalida}">
				
					<table class="table table-bordered">
						<thead>
							<tr>
							<th colspan="5" style="font-size: 20px; background-color: #f5f5f5;">${expedient.numeroINom}</th>
							</tr>
							<tr>
								<th style="background-color: #f5f5f5;"><spring:message code="contingut.info.nom" /></th>
								<th style="background-color: #f5f5f5;"><spring:message code="contingut.info.tipus" /></th>
								<th style="background-color: #f5f5f5;"><spring:message code="contingut.info.createl" /></th>
								<th style="background-color: #f5f5f5;"><spring:message code="contingut.info.creatper" /></th>
								<th style="background-color: #f5f5f5;" width="10%">&nbsp;</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="esborrany" items="${expedient.esborranys}">
								<tr>
									<td><rip:blocIconaContingut contingut="${esborrany}" />&nbsp;${esborrany.nom}</td>
									<td>${esborrany.metaNode.nom}</td>
									<td><fmt:formatDate value="${esborrany.createdDate}" pattern="dd/MM/yyyy HH:mm" /></td>
									<td>${esborrany.createdBy.nom}</td>
									<td><form:checkbox path="expedientsTancar[${vs.index}].documentsPerFirmar" value="${esborrany.id}" disabled="${esborrany.fitxerExtension == 'zip'}"/></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
						
			</c:if>
		</c:forEach>


		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
