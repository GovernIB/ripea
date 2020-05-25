<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty metaDadaCommand.id}"><c:set var="titol"><spring:message code="metadada.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="metadada.form.titol.modificar"/></c:set></c:otherwise>
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
	
<script type="text/javascript">
$(document).ready(function() {
	$('select#tipus').change(function() {
		if ($(this).val() == 'DOMINI') {
			let valor = $('#valor').val();
			$('#valor').parent().parent().hide();
			$.ajax({
				type: 'GET',
				url: "<c:url value="/metaExpedient/${metaDadaCommand.metaNodeId}/metaDada/domini"/>",
				success: function(data) {
					var $selOrgan = $('#domini');
					$selOrgan.empty();
					$selOrgan.append("<option value=\"\"></option>");
					if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.nom
							});
							if (valor == val.codi) {
								$selOrgan.append("<option value=\"" + val.codi + "\" selected>" + val.nom + "</option>");
							} else {
								$selOrgan.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
							}
						});
					}
					var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
					$selOrgan.select2("destroy");
					$selOrgan.select2(select2Options);
				}
			});
		} else {
			$('#valor').parent().parent().show();
		}
	});
	$('select#domini').change(function() {
		var dominiCodiSelected = $(this).val();		
		var dominiNomSelected = $(this).text();
		if (dominiCodiSelected != null && dominiCodiSelected != '') {
			$('#codi').val(dominiCodiSelected);
			$('#nom').val(dominiNomSelected);
			//$('#valor').val(dominiSelected);
		}
	});
	$('select#domini').trigger('change');
	$('select#tipus').trigger('change');
});
</script>
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaDadaCommand.metaNodeId}/metaDada"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaDadaCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="metaNodeId"/>
		<rip:inputText name="codi" textKey="metadada.form.camp.codi" required="true"/>
		<rip:inputText name="nom" textKey="metadada.form.camp.nom" required="true"/>
		<rip:inputSelect name="tipus" textKey="metadada.form.camp.tipus" optionEnum="MetaDadaTipusEnumDto"/>
		<rip:inputSelect name="multiplicitat" textKey="metadada.form.camp.multiplicitat" optionEnum="MultiplicitatEnumDto"/>
		<rip:inputText name="valor" textKey="metadada.form.camp.valor"/>
		<rip:inputSelect name="domini"/>
		<rip:inputTextarea name="descripcio" textKey="metadada.form.camp.descripcio"/>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaDada"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
