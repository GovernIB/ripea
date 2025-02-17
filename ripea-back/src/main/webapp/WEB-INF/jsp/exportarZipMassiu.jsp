<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="expedient.list.user.recuperar.exportacio.mass.titol" arguments="${expedientExportarZipOptions.numExps}"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
	<rip:modalHead/>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/expedient/exportarZipMassiu"/></c:set>
	<form:form action="" method="post" cssClass="form-horizontal" modelAttribute="expedientExportarZipOptions">
		
		<rip:inputHidden name="numExps"/>		
		<rip:inputCheckbox
			name="carpetes"
			textKey="expedient.controller.exportacio.mass.carpetes"
			comment="expedient.controller.exportacio.mass.carpetes.info"></rip:inputCheckbox>
		<rip:inputCheckbox
			name="versioImprimible"
			textKey="expedient.controller.exportacio.mass.vImprimible"
			comment="expedient.controller.exportacio.mass.vImprimible.info"
			></rip:inputCheckbox>
		<rip:inputSelect	
			name="nomFitxer"
			textKey="expedient.controller.exportacio.mass.nomDocs"
			
			optionEnum="FileNameOption"></rip:inputSelect>
		
		<div id="modal-botons">
			<button type="submit" class="btn btn-success">
				<span class="fa fa-file-archive-o"></span>
				<spring:message code="comu.boto.exportar"/>
			</button>
			<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
		</div>
	</form:form>
</body>