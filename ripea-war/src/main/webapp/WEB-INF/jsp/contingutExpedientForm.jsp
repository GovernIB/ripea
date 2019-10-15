<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty expedientCommand.id}"><c:set var="titol"><spring:message code="contingut.expedient.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="contingut.expedient.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script type="text/javascript">
function refrescarSequencia() {
	let metaExpedientId = $('select#metaNodeId').val();
	let any = $('input#any').val();
	if (metaExpedientId !== undefined && any !== undefined) {
		$.ajax({
			type: 'GET',
			url: '<c:url value="/metaExpedient"/>/' + metaExpedientId + '/proximNumeroSequencia/' + any,
			success: function(sequencia) {
				$('input#sequencia').val(sequencia);
			}
		});
	} else {
		$('input#sequencia').val(undefined);
	}
}
$(document).ready(function() {
	$('select#metaNodeId').change(function(event) {
		refrescarSequencia();
	});
	$('input#any').change(function(event) {
		refrescarSequencia();
	});
	$('input#any').trigger('change');
});
</script>
</head>
<body>
	<c:choose>
		<c:when test="${empty expedientCommand.id}"><c:set var="formAction"><rip:modalUrl value="/expedient/new"/></c:set></c:when>
		<c:otherwise><c:set var="formAction"><rip:modalUrl value="/expedient/${expedientCommand.id}/update"/></c:set></c:otherwise>
	</c:choose>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<rip:inputText name="nom" textKey="contingut.expedient.form.camp.nom" required="true" labelSize="2"/>
		<c:choose>
			<c:when test="${empty expedientCommand.id}">
				<rip:inputSelect name="metaNodeId" textKey="contingut.expedient.form.camp.metanode" required="true" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="nom" labelSize="2"/>
			</c:when>
			<c:otherwise>
				<rip:inputSelect name="metaNodeId" textKey="contingut.expedient.form.camp.metanode" required="true" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="nom" disabled="true" labelSize="2"/>
			</c:otherwise>
		</c:choose>
		<rip:inputText name="sequencia" textKey="contingut.expedient.form.camp.sequencia" required="false" labelSize="2"/>
		<rip:inputText name="any" textKey="contingut.expedient.form.camp.any" required="true" labelSize="2"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>