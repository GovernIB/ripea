<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="isTasca" value="${not empty tascaId}"/>
<html>
<head>
	<title><spring:message code="firmasimpleweb.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<script type="text/javascript">
$(document).ready(function() {
	$('#passarela-form').css('height', '300px');
	webutilModalAdjustHeight();
});
</script>
</head>
<body>


	<c:set var="formAction"><rip:modalUrl value="/massiu/firmasimpleweb/firmaSimpleWebStart"/></c:set>

	<form:form id="passarela-form" action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="firmaSimpleWebCommand" role="form" enctype="multipart/form-data">
		<rip:inputTextarea name="motiu" textKey="firmasimpleweb.form.camp.motiu" required="true"/>

		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-play"></span> <spring:message code="firmasimpleweb.form.iniciar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
