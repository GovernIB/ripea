<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
pageContext.setAttribute(
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
pageContext.setAttribute(
		"principalTipusEnumOptions",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
				es.caib.ripea.core.api.dto.PrincipalTipusEnumDto.class,
				"principal.tipus.enum."));
%>
	<c:set var="titol"><spring:message code="metaexpedient.arxiu.form.titol.crear"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/webjars/jstree/3.2.1/dist/themes/default/style.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
	<rip:modalHead titol="${titol}" buttonContainerId="botons"/>
<script>

	var metaExpedientId = ${metaExpedient.id};

	$(document).ready(function() {
		$("#modal-botons button[type='submit']").on('click', function() {
			$("form#metaExpedientArxiuCommand *:disabled").attr('readonly', 'readonly');
			$("form#metaExpedientArxiuCommand *:disabled").removeAttr('disabled');
		});		
	});

	function changedCallback(e, data) {
		// Buida les opcions del select2
		$('#arxiuId').select2('val', '', true);
		$('#arxiuId option[value!=""]').remove();
		// Obté la llista d'arxius per a aquell node contenidor
		var baseUrl = "<c:url value="/metaExpedient/${metaExpedient.id}/unitat/"/>" + data.node.id + "/arxiu/findAll";
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
	}

</script>
</head>
<body>

	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaExpedient.id}/arxiu"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaExpedientArxiuCommand">
		<rip:arbre id="arbreUnitats" arbre="${arbreUnitatsOrganitzatives}" atributId="codi" atributNom="denominacio" changedCallback="changedCallback" seleccionatId="${unitatCodi}" height="400px" />
		<rip:inputSelect name="arxiuId" textKey="metaexpedient.arxiu.form.camp.arxiu" required="true" optionItems="${arxius}" optionValueAttribute="id" optionTextAttribute="nom"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/arxiuAdmin/${arxiu.id}/metaExpedient"/>" class="btn btn-default modal-tancar"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>

</body>
</html>
