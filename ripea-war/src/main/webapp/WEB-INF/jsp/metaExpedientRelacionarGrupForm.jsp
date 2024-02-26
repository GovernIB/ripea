<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="metaexpedient.relacionar.grup.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<rip:modalHead/>

<script>

//################################################## document ready START ##############################################################
$(document).ready(function() {
	$('select#grupId').change(function(event) {

		refrescarOrgan();
	});

});//################################################## document ready END ##############################################################


var grupOrgan = {};
<c:forEach var="grup" items="${grups}">
<c:if test="${not empty grup.organGestor}">grupOrgan['${grup.id}'] = {id: ${grup.organGestor.id}, codiINom: '${fn:escapeXml(grup.organGestor.codiINom)}'};</c:if>
</c:forEach>

function refrescarOrgan() {
	const grupId = $('#grupId').val();
	if (grupId != undefined && grupId != "" && grupOrgan[grupId]) {
		const grup = grupOrgan[grupId];
		
		var newOption = new Option(grup.codiINom, grup.id, false, false);
		$('#organId').append(newOption);
		$('#organId').val(grup.id);
		$('#organId').trigger('change');

	} else {
		$("#organId").empty();
	}
}


function showGrup(element) {

	let text = element.text;

	if (text) {
		let isEntitat = false;
		if (text.startsWith('isEntitat')) {
			isEntitat = true;
			text = text.slice(9);
		}

		if (isEntitat) {
			return $('<button class="btn btn-info btn-xs" style="pointer-events: none; padding: 0px 5px; font-size: 11px;">E</button><span> ' + text + '</span>');
		} else {
			return $('<button class="btn btn-info btn-xs" style="pointer-events: none; padding: 0px 5px; font-size: 11px;">O</button><span> ' + text + '</span>');
		}
	}
 
}


</script>

</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaExpedientId}/grup/relacionar/save"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="relacionarGrupCommand">


		<rip:inputSelect 
			name="grupId" 
			optionItems="${grups}" 
			required="true"
			optionValueAttribute="id" 
			optionTextAttribute="codiDescripcioIsEntitat"
			textKey="metaexpedient.relacionar.grup.form.camp.grup" 
			emptyOption="true"
			templateResultFunction="showGrup"/>

		<rip:inputSelect 
			name="organId" 
			optionItems="${organs}" 
			optionValueAttribute="id" 
			optionTextAttribute="codiINom"
			textKey="metaexpedient.relacionar.grup.form.camp.organGestor"
			disabled="true"/>
				
		<rip:inputCheckbox name="perDefecte" textKey="metaexpedient.relacionar.grup.form.camp.perDefecte" disabled="${bloquejarCamps}"/>
		
		<div style="min-height: 100px;"></div>
		
		<div id="modal-botons" class="well">
			<c:if test="${!consultar}">
				<button type="submit" class="btn btn-success" <c:if test="${bloquejarCamps}">disabled</c:if>><span class="fa fa-save"></span>
					<spring:message code="comu.boto.vincular"/>
				</button>	
			</c:if>	
			<a href="<c:url value="/metaExpedient/${metaExpedientId}/grup"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
