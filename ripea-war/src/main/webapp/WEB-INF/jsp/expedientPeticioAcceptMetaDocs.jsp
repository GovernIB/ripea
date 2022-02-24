<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="titol">
	<spring:message code="expedient.peticio.form.acceptar.titol" /> - <spring:message code="expedient.peticio.form.acceptar.escollir.metadocs.titol" />
</c:set>
<html>
<head>
<title>${titol}</title>
<rip:modalHead />

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
<style type="text/css">
.fa-circle-o-notch {
	position: absolute;
	right: 10px;
	top: 10px;
}
</style>
<script>

$(document).ready(function(){

	$("button#btnSave").submit(function (e) {
	    e.preventDefault();
	    $("button#btnSave").attr("disabled", true);
	    return true;
	});		
});
</script>

</head>
<body>

	<c:set var="formAction">
		<rip:modalUrl value="/expedientPeticio/acceptar/${expedientPeticioId}" />
	</c:set>
	<form:form id="expedientPeticioAcceptarForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientPeticioAcceptarCommand">
		<form:hidden path="id" />
		<form:hidden path="accio"/>
		<form:hidden path="metaExpedientId"/>
		<form:hidden path="expedientId"/>
		<form:hidden path="any"/> 			
		<form:hidden path="associarInteressats"/> 
		<form:hidden path="newExpedientTitol"/>

		<c:forEach items="${expedientPeticioAcceptarCommand.annexos}" varStatus="vs">
			<div class="well"> 
				<form:hidden path="annexos[${vs.index}].id" />
				
				<rip:inputText name="annexos[${vs.index}].titol" textKey="expedient.peticio.form.acceptar.camp.annex.nom" required="true" readonly = "true"/>
				<rip:inputSelect name="annexos[${vs.index}].metaDocumentId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="${fn:length(metaDocuments) > 1 ? true : false}" emptyOptionTextKey="contingut.document.form.camp.nti.cap" required="true"/>
			</div>
		</c:forEach>
	
		<div id="modal-botons" class="well">
			<button id="btnSave" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar" /></button>
			<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>
</body>
</html>

