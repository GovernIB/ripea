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
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<rip:modalHead/>
	
	<script type="text/javascript">
		$(document).ready(function() {
		
			let errorsValidacio = parseBoolean('${errorsValidacio}');
		
			$('#duracio').on('blur', function(event) { 
				$('#titol').focus();
				event.preventDefault();
				return false;
			});
		
			$('#duracio').on('change', function() {
				$.post("<c:url value="/expedientTasca/"/>" + $('#metaExpedientTascaId').val() + "/changedDuracio",
				$("#tascaform").serialize())
				.done(function(data){
					$('#dataLimit').val(data.dataLimitString);
					remarcaElement($('#dataLimit'));
				})
				.fail(function() {
					alert("<spring:message code="error.jquery.ajax"/>");
				});
			});
		
			$('#dataLimit').on('change', function() {
				$.post("<c:url value="/expedientTasca/"/>" + $('#metaExpedientTascaId').val() + "/changedDataLimit",
				$("#tascaform").serialize())
				.done(function(data){
					$('#duracio').val(data.duracio);
					remarcaElement($('#duracio'));
				})
				.fail(function() {
					alert("<spring:message code="error.jquery.ajax"/>");
				});
			});
			
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
						$('#duracio').val(data.duracio);
						$('#dataLimit').val(data.dataLimitString);
						$('#prioritat').val(data.prioritat).change();
					})
					.fail(function() {
						alert("<spring:message code="error.jquery.ajax"/>");
					});
				}
			});
			//Si venim de guardar amb errors, no volem recarregar les dades, sino mantenir les del command
			//Al carregar una tasca existent, tampoc volem carregar dades de la meta-tasca, sino les del command
			if (!errorsValidacio) {
				$('#metaExpedientTascaId').trigger('change');
			}
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

		<rip:inputTextarea 
			name="metaExpedientTascaDescripcio" 
			textKey="expedient.tasca.form.camp.metaExpedientTascaDescripcio" 
			disabled="true"
			required="true"
			textareaRows="2"/> 
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
		<rip:inputSuggest 
			name="observadorsCodi" 
			urlConsultaInicial="${urlConsultaInicial}" 
			urlConsultaLlistat="${urlConsultaLlistat}" 
			textKey="expedient.tasca.form.camp.observador"
			suggestValue="codi"
			suggestText="nom"
			comment="expedient.tasca.form.camp.observador.comentari"
			multiple="true"/>
		<rip:inputText
				name="duracio"
				textKey="tasca.list.column.duracio"
				tooltip="true"
				comment="tasca.list.column.duracio.tip"
				tooltipMsg="tasca.list.column.duracio.tip"/>
		<rip:inputDate
			name="dataLimit"
			textKey="expedient.tasca.list.boto.dataLimit"/>
		<rip:inputText 
			name="titol"
			textKey="expedient.tasca.form.camp.titol"
			required="false"/>
		<rip:inputTextarea 
			name="observacions" 
			textKey="expedient.tasca.form.camp.observacions" 
			required="false"/>
		<rip:inputSelect
				name="prioritat"
				optionEnum="PrioritatEnumDto"
				emptyOption="false"
				textKey="contingut.expedient.form.camp.prioritat"
				templateResultFunction="showColorPriritats"/>
		<div id="modal-botons" class="well">
			<button id="btnSave" type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.crear"/></button>
 			<a href="<c:url value="/expedientTasca"/>" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>

	<div class="rmodal"></div>
</body>
</html>
