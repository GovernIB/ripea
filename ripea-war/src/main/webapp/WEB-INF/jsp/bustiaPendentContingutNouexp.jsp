<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="bustia.pendent.contingut.nouexp.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<rip:modalHead/>
<script>
	$(document).ready(function() {
		$("#metaNodeId").change(function() {
			$('#arxiuId').select2('val', '', true);
			$('#arxiuId option[value!=""]').remove();
			var metaExpedientId = $(this).val();
			if(metaExpedientId != null) {
				var baseUrl = "<c:url value="/userajax/metaExpedient/"/>" + metaExpedientId + "/arxius";
				$.get(baseUrl)
					.done(function(data) {
						for (var i = 0; i < data.length; i++) {
							$('#arxiuId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
						}	
						$('#arxiuId').change();
					})
					.fail(function() {
						alert("<spring:message code="error.jquery.ajax"/>");
					});
			} else {
				$('#arxiuId').change();
			}
		});
		$("#metaNodeId").trigger('change');
	});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="expedientCommand">
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<rip:inputSelect name="metaNodeId" textKey="contingut.expedient.form.camp.metanode" required="true" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${not empty expedientCommand.id}"/>
		<rip:inputSelect name="arxiuId" textKey="contingut.expedient.form.camp.arxiu" required="true" optionItems="${arxius}" optionValueAttribute="id" optionTextAttribute="nomAmbUnitat"/>
		<rip:inputText name="nom" textKey="contingut.expedient.form.camp.nom" required="true"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/contenidor/${expedientCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>