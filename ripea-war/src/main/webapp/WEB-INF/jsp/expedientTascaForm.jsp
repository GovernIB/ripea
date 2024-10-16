<%@ page import="es.caib.ripea.war.helper.EnumHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="expedient.tasca.form.titol.crear"/></c:set>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

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
	
		var userTriggered = true;
		function aturaSpin() { $(".fa-refresh").removeClass("fa-spin"); }

		$(document).ready(function() {
		
			let errorsValidacio = parseBoolean('${errorsValidacio}');
		
			$('#duracio').on('blur', function(event) { 
				$('#titol').focus();
				event.preventDefault();
				return false;
			});
		
			$('#duracio').on('change', function(event) {
				if ($('#duracio').val()!='' && $('#duracio').val()!='0') {
					$(".fa-refresh").addClass("fa-spin");
					$.post('<c:url value="/expedientTasca/changedDuracio"/>', $('#tascaform').serialize())
					.done(function(data) {
						//Ara canviam el valor del camp dataLimit, pero no volem executar el onchange
						userTriggered = false;
						$('#dataLimit').val(data.dataLimitString);
						$('#dataLimit').datepicker('update', data.dataLimitString);
						setTimeout(aturaSpin, 500);
						remarcaElement($('#dataLimit'), '#d9edf7');
					})
					.fail(function() {
						alert("<spring:message code="error.jquery.ajax"/>");
					});
				} else {
					$('#duracio').val('');
					$('#dataLimit').val('');
					$('#dataLimit').datepicker('update', '');
				}						
			});
		
			$('#dataLimit').on('change', function(event) {
				if ($('#dataLimit').val()!='') {
					if (userTriggered) {
						$(".fa-refresh").addClass("fa-spin");
						$.post('<c:url value="/expedientTasca/changedDataLimit"/>', $('#tascaform').serialize())
						.done(function(data){
							if (data.duracio>0) {
								$('#duracio').val(data.duracio);
							} else {
								$('#duracio').val('');
								$('#dataLimit').val('');
								$('#dataLimit').datepicker('update', '');
							}
							setTimeout(aturaSpin, 500);
							remarcaElement($('#duracio'), '#d9edf7');
						})
						.fail(function() {
							alert("<spring:message code="error.jquery.ajax"/>");
						});
					} else {
						userTriggered = true;
					}
				} else {
					$('#duracio').val('');
				}
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
			
		<div class="form-group">
			<label class="control-label col-xs-4" for="duracio"><spring:message code="tasca.list.column.duracio"/></label>
			<div class="col-xs-3" style="padding-right: 0px;">
				<form:input id="duracio" path="duracio" cssClass="form-control"/>
				<p class="comentari"><spring:message code="tasca.list.column.duracio.tip"/></p>
			</div>
			<c:set var="tipSincroDuracio"><spring:message code="expedient.tasca.form.tipSincroDuracio"/></c:set>
			<div class="col-xs-1" style=" text-align: center; padding: 6px 0 0 0;" title="${tipSincroDuracio}">
				<span class="fa fa-lg  fa-refresh" style="color: darkgrey;"></span>
			</div>
			<div class="col-xs-4" style="padding-left: 0px;">
				<div class="input-group" style="width:100%">
					<form:input id="dataLimit" path="dataLimit" cssClass="form-control datepicker" data-toggle="datepicker" data-idioma="${requestLocale}"/>
					<span class="input-group-addon" style="width:1%"><span class="fa fa-calendar"></span></span>
				</div>
				<p class="comentari"><spring:message code="expedient.tasca.list.columna.dataLimit"/></p>
			</div>
		</div>

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
