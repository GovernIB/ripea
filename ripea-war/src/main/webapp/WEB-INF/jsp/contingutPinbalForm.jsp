<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="contingut.pinbal.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<script>
const metaDocumentServeiScsp = [];
<c:forEach var="metaDocument" items="${metaDocuments}">
metaDocumentServeiScsp[${metaDocument.id}] = "${metaDocument.pinbalServei}";
</c:forEach>
$(document).ready(function() {
	$('#metaDocumentId').on('change', function() {
		if (metaDocumentServeiScsp[$(this).val()] === "SVDCCAACPASWS01") {
			$('#bloc-datos-especificos').css('display', 'bloc');
		} else {
			$('#bloc-datos-especificos').css('display', 'none');
		}
	});
	$('#metaDocumentId').trigger('change');
});
</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/contingut/${pinbalConsultaCommand.pareId}/pinbal/new"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="pinbalConsultaCommand">
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<rip:inputSelect name="metaDocumentId" textKey="contingut.pinbal.form.camp.metanode" required="true" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom"/>
		<rip:inputSelect name="interessatId" textKey="contingut.pinbal.form.camp.interessat" required="true" optionItems="${interessats}" optionValueAttribute="id" optionTextAttribute="identificador" />
		<rip:inputSelect name="consentiment" textKey="contingut.pinbal.form.camp.consentiment" required="true" optionItems="${consentimentOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<rip:inputTextarea name="finalitat" textKey="contingut.pinbal.form.camp.finalitat" required="true" maxlength="256"/>
		<div id="bloc-datos-especificos">
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="active">
					<a href="#datos-especificos" aria-controls="fitxer" role="tab" data-toggle="tab"><spring:message code="contingut.pinbal.form.datos.especificos"/></a>
				</li>
			</ul>
			<br/>
			<div class="tab-content">
				<div role="tabpanel" class="tab-pane active" id="datos-especificos">
					<rip:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="value" optionTextAttribute="text"/>
					<rip:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="value" optionTextAttribute="text"/>
				</div>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.enviar"/></button>
			<a href="<c:url value="/contingut/${documentCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>