<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>

	<title><spring:message code="metaexpedient.import.form.titol"/></title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
	
	
	<c:if test="${hasPermisAdmComu}">
		<script type="text/javascript">
		$(document).ready(function() {
			var selectOrganGestorContainer = $("select#organGestorId").parent().parent(); 
			$( "#comu" ).change(function () {
				if(this.checked) {
					selectOrganGestorContainer.hide();
					$("select#organGestorId").val(null);
					$("select#organGestorId").trigger("change");
			    } else {
			    	selectOrganGestorContainer.show();
			    }
		  	});
			if ($("#comu").is(":checked")) {
				selectOrganGestorContainer.hide();
			}
		});
		</script>
	</c:if>	
	
	
	
	
	
<script>
//################################################## document ready START ##############################################################	
$(document).ready(function(){

	$('input[type=radio][name=tipusClassificacio]').on('change', function() {
		showHideClassificacioInput();
	})
	showHideClassificacioInput();
	$('#organGestorId').on('change', function() {
		$('input[name="tipusClassificacio"][value="ID"]').removeClass('focus');
		disableEnableClassificacioIdButton();
		calculateClassificacioId();
	})

	disableEnableClassificacioIdButton();
	if (!$('#id').val()) {
		calculateClassificacioId();
	}
	
			
});//################################################## document ready END ##############################################################


function showHideClassificacioInput() {

	let selected = $('input[name="tipusClassificacio"]:checked').val();

	if (selected == 'SIA') {
		$('#classificacioSia').parent().show();
		$('#classificacioId').parent().hide();
	} else {
		$('#classificacioSia').val('');
		$('#classificacioSia').parent().hide();
		$('#classificacioId').parent().show();
		if ($('#organGestorId').val() && !$('#classificacioId').val()) {
			calculateClassificacioId();
		}
	}
}

function disableEnableClassificacioIdButton(){
	if ($('#organGestorId').val()) {
		$('input[name="tipusClassificacio"][value="ID"]').parent().attr("disabled", false);
		$('input[name="tipusClassificacio"][value="ID"]').parent().attr("title", "");
	} else {
		$('#classificacioId').val('');
		$('input[name="tipusClassificacio"][value="ID"]').parent().attr("disabled", true);
		let disabledTitle = '<spring:message code="metaexpedient.form.camp.classificacio.id.disabled.title"/>';
		$('input[name="tipusClassificacio"][value="ID"]').parent().attr("title", disabledTitle);
		$('input[name="tipusClassificacio"][value="SIA"]').click();
		
	}
}

function disableClassificacioButtons(){
	$('input[name="tipusClassificacio"][value="SIA"]').parent().attr("disabled", true);
	$('input[name="tipusClassificacio"][value="ID"]').parent().attr("disabled", true);
}


function calculateClassificacioId() {
	let organGestorId = $('#organGestorId').val();
	$.ajax({
		type: 'GET',
		url: '<c:url value="/metaExpedient/calculateClassificacioId"/>/' + organGestorId,
		success: function(id) {
			$('#classificacioId').val(id);
		}
	});
	
}


</script>	
	
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/importFitxerEdit"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaExpedientImportEditCommand" role="form">

		<form:hidden path="entitatId"/>
		
		<rip:inputText name="codi" textKey="metaexpedient.form.camp.codi" required="true" />
		
		<c:set var="campErrors"><form:errors path="classificacioSia"/></c:set>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-4" for="tipusClassificacio">
				<spring:message code="metaexpedient.form.camp.classificacio"/> *
			</label>
			<div class="col-xs-8">
				<div class="controls btn-group col-xs-2" style="padding-right: 0px; padding-left: 0px" data-toggle="buttons">
					<c:forEach var="opt" items="${tipus}">
						<c:set var="labelClassActive" value=""/>
						<spring:bind path="tipusClassificacio"><c:set var="campValue" value="${status.value}"/></spring:bind>
						<c:if test="${campValue == opt.value}"><c:set var="labelClassActive" value=" active"/></c:if>
						<c:set var="labelClass" value="btn btn-default${labelClassActive}"/>
						<button class="${labelClass}" onclick="this.blur();">
							<form:radiobutton path="tipusClassificacio" value="${opt.value}"/> <spring:message code="${opt.text}"/>
						</button>
					</c:forEach>
				</div>
				<div class="col-xs-10" style="padding-right: 0px; padding-left: 10px">
					<div style="display:none;">
						<form:input path="classificacioSia" cssClass="form-control"/>
						<c:if test="${not empty campErrors}">
							<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="classificacioSia"/></p>
						</c:if>	
						   <p class="comentari col-xs-12 comentariSia"><spring:message code="metaexpedient.form.camp.classificacio.sia.comment"/></p>	
					</div>	
					<div style="display:none;">
						<form:input path="classificacioId" cssClass="form-control" readonly="true"/>
						   <p class="comentari col-xs-12"><spring:message code="metaexpedient.form.camp.classificacio.id.comment"/></p>	
					</div>							
				</div>					
			</div>
		</div>
		
		<rip:inputTextarea name="nom" textKey="metaexpedient.form.camp.nom" required="true"/>
		<rip:inputTextarea name="descripcio" textKey="metaexpedient.form.camp.descripcio" />
		<rip:inputText name="serieDocumental" textKey="metaexpedient.form.camp.serie.doc" required="true"/>

		<c:choose>
			<c:when test="${hasPermisAdmComu}">
				<rip:inputCheckbox name="comu" textKey="metaexpedient.form.camp.comu" />
			</c:when>
			<c:otherwise>
				<form:hidden path="comu" />
			</c:otherwise>
		</c:choose>
		<c:if test="${isObligarInteressatActiu}">
			<form:hidden path="interessatObligatori" />
		</c:if>
		<c:if test="${!metaExpedientImportEditCommand.comu}">
			<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
			<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
			<rip:inputSuggest 
					name="organGestorId"  
					urlConsultaInicial="${urlConsultaInicial}"
					urlConsultaLlistat="${urlConsultaLlistat}"
					textKey="metaexpedient.form.camp.organgestor"
					suggestValue="id"
					suggestText="codiINom"
					required="true"
					disabled="${bloquejarCamps}"/>
		</c:if>


		<c:if test="${!empty metaExpedientImportEditCommand.metaDocuments}"><legend><spring:message code="metaexpedient.import.form.metadocuments" /></legend>
			<c:forEach items="${metaExpedientImportEditCommand.metaDocuments}" varStatus="vs">
				<div class="well"> 
					<form:hidden path="metaDocuments[${vs.index}].id" />
					<form:hidden path="metaDocuments[${vs.index}].portafirmesFluxTipus" />
					<form:hidden path="metaDocuments[${vs.index}].firmaPortafirmesActiva" />
					<rip:inputText name="metaDocuments[${vs.index}].codi" textKey="metaexpedient.form.camp.codi" required="true" readonly = "true"/>
					
					<c:choose>
						<c:when test="${metaExpedientImportEditCommand.metaDocuments[vs.index].portafirmesFluxTipus=='SIMPLE'}">
							<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
							<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>		
							<rip:inputSuggest 
								name="metaDocuments[${vs.index}].portafirmesResponsables" 
								urlConsultaInicial="${urlConsultaInicial}" 
								urlConsultaLlistat="${urlConsultaLlistat}" 
								textKey="metaexpedient.import.form.camp.portafirmes.responsables"
								suggestValue="codi"
								suggestText="nom"
								suggestTextAddicional="nif"
								required="${metaExpedientImportEditCommand.metaDocuments[vs.index].firmaPortafirmesActiva}"/>							
						</c:when>
						<c:otherwise>
						</c:otherwise>
					
					</c:choose>
							

				</div>
			</c:forEach>
		</c:if>
		
		<c:if test="${!empty metaExpedientImportEditCommand.estats}"><legend><spring:message code="metaexpedient.import.form.estats"/></legend>
			<c:forEach items="${metaExpedientImportEditCommand.estats}" varStatus="vs">
				<div class="well"> 
					<form:hidden path="estats[${vs.index}].id" />
					<rip:inputText name="estats[${vs.index}].codi" textKey="expedient.estat.form.camp.codi" required="true" readonly="true"/>		
					<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
					<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
					<rip:inputSuggest 
						name="estats[${vs.index}].responsableCodi" 
						urlConsultaInicial="${urlConsultaInicial}" 
						urlConsultaLlistat="${urlConsultaLlistat}" 
						textKey="expedient.estat.form.camp.responsable"
						suggestValue="codi"
						suggestText="nom"
						placeholderKey="expedient.estat.form.camp.responsable"/>
				</div>
			</c:forEach>
		</c:if>
			
		<c:if test="${!empty metaExpedientImportEditCommand.tasques}"><legend><spring:message code="metaexpedient.import.form.tasques"/></legend>
			<c:forEach items="${metaExpedientImportEditCommand.tasques}" varStatus="vs">
				<div class="well"> 		
					<form:hidden path="tasques[${vs.index}].id" />
					<rip:inputText name="tasques[${vs.index}].codi" textKey="metaexpedient.tasca.form.camp.codi" required="true" readonly="true"/>	

					<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
					<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
					<rip:inputSuggest 
						name="tasques[${vs.index}].responsable" 
						urlConsultaInicial="${urlConsultaInicial}" 
						urlConsultaLlistat="${urlConsultaLlistat}" 
						textKey="metaexpedient.tasca.form.camp.responsable"
						suggestValue="codi"
						suggestText="nom"
						placeholderKey="metaexpedient.tasca.form.camp.responsable"/>							
				</div>
			</c:forEach>	
		</c:if>

		<div id="modal-botons">
			<button type="submit" data-toggle="modal" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.importar"/></button>
			<a href="<c:url value="/metaExpedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
