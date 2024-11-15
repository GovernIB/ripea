<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="expedient.list.user.recuperar.exportacio.mass"/></title>
	<rip:modalHead/>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/expedient/exportarZipMassiu"/></c:set>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="expedientExportarZipOptions">
		
		<rip:inputCheckbox	name="carpetes" textKey="expedient.controller.exportacio.mass.carpetes"></rip:inputCheckbox>
		<rip:inputCheckbox	name="versioImprimible" textKey="expedient.controller.exportacio.mass.vImprimible"></rip:inputCheckbox>
		<rip:inputSelect	name="nomFitxer" textKey="expedient.controller.exportacio.mass.nomDocs" optionEnum="PrincipalTipusEnumDto"></rip:inputSelect>
		
		<div id="modal-botons">
			<button type="submit" class="btn btn-success">
				<span class="fa fa-file-archive-o"></span>
				<spring:message code="comu.boto.exportar"/>
			</button>
			<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>