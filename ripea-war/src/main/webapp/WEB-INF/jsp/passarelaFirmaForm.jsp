<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="passarelafirma.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
</head>
<body>
	<c:if test="${documentVersio.arxiuNom != documentVersio.portafirmesConversioArxiuNom}">
		<div class="alert well-sm alert-warning alert-dismissable">
			<span class="fa fa-exclamation-triangle"></span>
			<spring:message code="passarelafirma.form.conversio.avis"/>
			<a data-rdt-link-modal="true" class="btn btn-xs btn-default pull-right" href="pdf">
				<spring:message code="passarelafirma.form.conversio.boto.previsualitzar"/>
			</a>
		</div>
	</c:if>
	<c:set var="formAction"><rip:modalUrl value="/document/${document.id}/firmaPassarela"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="passarelaFirmaEnviarCommand" role="form">
		<rip:inputText name="motiu" textKey="passarelafirma.form.camp.motiu" required="true"/>
		<rip:inputText name="lloc" textKey="passarelafirma.form.camp.lloc" required="true"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-play"></span> <spring:message code="passarelafirma.form.iniciar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
