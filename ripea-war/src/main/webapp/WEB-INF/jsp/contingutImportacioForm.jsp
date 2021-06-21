<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="contingut.document.form.titol.importar"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/min/moment.min.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/min/locales.min.js"/>"></script>
	<script src="<c:url value="/webjars/moment/2.15.1/locale/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build//js/bootstrap-datetimepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.min.js"/>"></script>
	<rip:modalHead/>
<script type="text/javascript">
$(document).ready(function() {
	$("#dataPresentacio").mask("99/99/9999 99:99:99",{ 
		placeholder:"_"
	});
	$('#carpetaNom').closest('.form-group').hide();
	$('input[type=radio][name=destiTipus]').on('change', function() {
		if ($(this).val() == 'CARPETA_NOVA') {
			$('#carpetaNom').closest('.form-group').show();
		} else {
			$('#carpetaNom').closest('.form-group').hide();
		}
		webutilModalAdjustHeight();
	});
	$('input[type=radio][name=destiTipus][value=${importacioCommand.destiTipus}]').trigger('change');
})
</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/contingut/${importacioCommand.pareId}/importacio/new"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="importacioCommand">
		<br/>
		<rip:inputText name="numeroRegistre" textKey="contingut.importacio.form.camp.nom" required="true"/>
		<rip:inputText name="dataPresentacio" textKey="contingut.importacio.form.camp.data" required="true" placeholder="__/__/____  __:__:__"/>
		<rip:inputRadio name="destiTipus" textKey="contingut.importacio.form.camp.desti" botons="true" optionItems="${tipusDestiOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		<rip:inputText name="carpetaNom" textKey="contingut.importacio.form.camp.carpeta" required="true"/>
		<%-- <rip:inputDateTime name="dataPresentacio" textKey="contingut.importacio.form.camp.data" required="true"/>--%>
		<br/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.importar"/></button>
			<a href="<c:url value="/contingut/${carpetaCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
