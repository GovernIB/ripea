<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
pageContext.setAttribute(
		"dadesUsuariActual",
		es.caib.ripea.war.helper.SessioHelper.getUsuariActual(request));
%>
<c:set var="titol">
	<spring:message code="expedientPeticio.form.acceptar.titol" />
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

	if ('${accio}' != 'CREAR') {
		$('#input-accio-crear').removeClass('hidden');
		$('#input-accio-incorporar').addClass('hidden');
	} else {
		$('#input-accio-incorporar').removeClass('hidden');
		$('#input-accio-crear').addClass('hidden');
	}

	$('#metaExpedientId').on('change', function() {
		var tipus = $(this).val();
		$('#expedientId').select2('val', '', true);
		$('#expedientId option[value!=""]').remove();
		
		$('#expedientId').next().find('span.select2-selection__arrow').addClass('fa fa-spin fa-circle-o-notch  fa-1x').removeClass('select2-selection__arrow');
		
		if (tipus != "") {
			$.get("<c:url value="/expedientPeticio/expedients/"/>"+${entitatId}+"/"+tipus)
			.done(function(data){
				for (var i = 0; i < data.length; i++) {
					if (data[i].agafat && data[i].agafatPer.codi === '${dadesUsuariActual.codi}')
						$('#expedientId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					else
						$('#expedientId').append('<option value="' + data[i].id + '" disabled>' + data[i].nom + '</option>');
				}
				$('#expedientId').next().find('span.fa-circle-o-notch').addClass('select2-selection__arrow').removeClass('fa fa-spin fa-circle-o-notch  fa-1x');
			})
			.fail(function() {
				$('#expedientId').next().find('span.fa-circle-o-notch').addClass('select2-selection__arrow').removeClass('fa fa-spin fa-circle-o-notch  fa-1x');
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		}
	});	
	

	$('input[type=radio][name=expedientPeticioAccioEnumDto]').on('change', function() {
		if ($(this).val() == 'CREAR') {
			$('#input-accio-crear').removeClass('hidden');
			$('#input-accio-incorporar').addClass('hidden');
		} else {
			$('#input-accio-incorporar').removeClass('hidden');
			$('#input-accio-crear').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});
});

</script>

</head>
<body>
	<c:set var="formAction">
		<rip:modalUrl value="/expedientPeticio/acceptar/${expedientPeticioId}" />
	</c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal"
		commandName="expedientPeticioAcceptarCommand">
		<form:hidden path="id" />

		<rip:inputRadio name="expedientPeticioAccioEnumDto" textKey="expedientPeticio.form.acceptar.camp.accio" botons="true" optionItems="${accios}" optionValueAttribute="value" optionTextKeyAttribute="text"/>

		<rip:inputSelect name="metaExpedientId" textKey="expedientPeticio.form.acceptar.camp.metaExpedient"
			required="true" optionItems="${metaExpedients}" optionValueAttribute="id" emptyOption="true"
			optionTextAttribute="nom" optionMinimumResultsForSearch="6"/>
			
		<div id="input-accio-incorporar" class="hidden">
			<rip:inputSelect name="expedientId"
				textKey="expedientPeticio.form.acceptar.camp.expedient" required="true" 
	 			optionItems="${expedients}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="6"/> 
		</div>
		<div id="input-accio-crear" class="hidden">
			<rip:inputText name="newExpedientTitol" textKey="expedientPeticio.form.acceptar.camp.newExpedientTitol"
				required="true" />
			<rip:inputText name="any" textKey="expedientPeticio.form.acceptar.camp.any" required="true"/> 			
		</div>
			
		<rip:inputCheckbox name="associarInteressats"
 			textKey="expedientPeticio.form.acceptar.camp.associarInteressats"/> 
			
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success">
				<span class="fa fa-save"></span>
				<spring:message code="comu.boto.guardar" />
			</button>
			<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default"
				data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>
</body>
</html>

