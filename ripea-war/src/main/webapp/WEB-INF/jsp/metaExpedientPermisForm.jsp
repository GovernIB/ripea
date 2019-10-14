<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty permisCommand.id}"><c:set var="titol"><spring:message code="metaexpedient.permis.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="metaexpedient.permis.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<script>
	$(document).ready(function() {
		$("#modal-botons button[type='submit']").on('click', function() {
			$("form#permisCommand *:disabled").attr('readonly', 'readonly');
			$("form#permisCommand *:disabled").removeAttr('disabled');
		});

		$("#selectAll").on('change', function() {
			if ($(this).prop("checked"))
				$("div.permisosInput :checkbox").prop('checked', true);
			else
				$("div.permisosInput :checkbox").prop('checked', false);
		});

		$("div.permisosInput :checkbox").on('change', function() {
			var totsSeleccionats = true;
			$("div.permisosInput :checkbox").each(function() {
				  if(!$(this).prop('checked'))
					  totsSeleccionats = false;
			});
			$("#selectAll").prop('checked', totsSeleccionats);
		});
	});

</script>
<style>
	.permisosInput {margin-left: 45px}
</style>


</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaExpedient.id}/permis"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="permisCommand">
		<form:hidden path="id"/>
		<rip:inputSelect name="principalTipus" textKey="metaexpedient.permis.form.camp.tipus" disabled="${not empty permisCommand.id}" optionEnum="PrincipalTipusEnumDto"/>
		<rip:inputText name="principalNom" required="true" textKey="entitat.permis.form.camp.principal" disabled="${not empty permisCommand.id}" placeholderKey="entitat.permis.form.camp.principal"/>
		
		<rip:inputCheckbox name="selectAll" textKey="metaexpedient.permis.form.camp.all"/>
		<div class="permisosInput">
			<rip:inputCheckbox name="create" textKey="metaexpedient.permis.form.camp.creacio"/>
			<rip:inputCheckbox name="read" textKey="metaexpedient.permis.form.camp.consulta"/>
			<rip:inputCheckbox name="write" textKey="metaexpedient.permis.form.camp.modificacio"/>
			<rip:inputCheckbox name="delete" textKey="metaexpedient.permis.form.camp.eliminacio"/>
		</div>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaExpedient/${metaExpedient.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
