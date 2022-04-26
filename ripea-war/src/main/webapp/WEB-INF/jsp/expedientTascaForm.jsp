<%@page import="es.caib.ripea.war.helper.EnumHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="expedient.tasca.form.titol.crear"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<rip:modalHead/>
	
<script>
$(document).ready(function(){
	$('#metaExpedientTascaId').on('change', function() {
		var metaTascaId = $(this).val();
		$('#expedientId').select2('val', '', true);
		$('#expedientId option[value!=""]').remove();
		
		if (metaTascaId != null && metaTascaId != "") {
			$.get("<c:url value="/expedientTasca/"/>" + metaTascaId + "/getMetaExpedientTasca")
			.done(function(data){

				$('#metaExpedientTascaDescripcio').val(data.descripcio);
				
				$('#responsablesCodi').data('currentValue', data.responsable);
				$('#responsablesCodi').webutilInputSuggest();

				$('#dataLimit').val(data.dataLimitString);
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		}
	});	
	$('#metaExpedientTascaId').trigger('change');					
});

</script>	
</head>
<body>

	<c:set var="formAction"><rip:modalUrl value="/expedientTasca/${expedientId}/tasca"/></c:set>
	<form:form id="tascaform" action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientTascaCommand">

		<rip:inputSelect 
			name="metaExpedientTascaId" 
			textKey="expedient.tasca.form.camp.metaExpedientTasca"
			optionItems="${metaexpTasques}" 
			optionValueAttribute="id"  
			optionTextAttribute="nom"
			required="true" /> 
			
		<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
		<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
		<rip:inputSuggest 
			name="responsablesCodi" 
			urlConsultaInicial="${urlConsultaInicial}" 
			urlConsultaLlistat="${urlConsultaLlistat}" 
			textKey="expedient.tasca.form.camp.responsable"
			suggestValue="codi"
			suggestText="nom"
			required="true"
			multiple="true"/>				
		<rip:inputDate name="dataLimit" textKey="expedient.tasca.form.camp.dataLimit"/>
		<rip:inputTextarea name="comentari" textKey="expedient.tasca.form.camp.comentari" required="false" maxlength="256"/>
		<rip:inputTextarea 
			name="metaExpedientTascaDescripcio" 
			textKey="expedient.tasca.form.camp.metaExpedientTascaDescripcio" 
			disabled="true"
			required="true"/> 
		<div id="modal-botons" class="well">
			<button id="btnSave" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
 			<a href="<c:url value="/expedientTasca"/>" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>

	<div class="rmodal"></div>
</body>
</html>
