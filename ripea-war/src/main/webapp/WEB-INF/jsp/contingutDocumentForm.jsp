<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty documentCommand.id}"><c:set var="titol"><spring:message code="contingut.document.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="contingut.document.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="isTasca" value="${not empty tascaId}"/>
<c:set var="isCreate" value="${empty documentCommand.id}"/>

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<style type="text/css">
.tooltip {
  font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;
  font-size: 14px;
  display: block;
}
.tooltip-arrow {
}
.tooltip-inner {
  max-width: 700px;
  padding: 2% 1% 1% 1%;
  background-color: #fff;
  color: black;
  padding: 1%;
  border: 1px solid black;
  border-radius: 3px;
}
</style>
<script>
function mostrarDocument(fileName) {
	$fileinput = $('#arxiu').closest('.fileinput');
	$fileinput.removeClass('fileinput-new');
	$fileinput.addClass('fileinput-exists');
	$('.fileinput-filename', $fileinput).append(fileName);
}

$(document).ready(function() {
	let fileName = "${nomDocument}";
	if (fileName !== '') {
		mostrarDocument(fileName);
	}

	// METADOCUMENT CHANGE
	$('#metaNodeId').on('change', function() {
		if ($(this).val()) {
			if ($('#id').val() == '') { // if creating new document
				$.get("/ripea/modal/contingut/${contingutId}/metaDocument/" +  $(this).val() + "/dadesnti")
				.done(function(data) {			
					$('#ntiOrigen').val(data.ntiOrigen).trigger('change');
					$('#ntiOrigen option[value='+ data.ntiOrigen +']').attr('selected','selected');
					$('#ntiEstadoElaboracion').val(data.ntiEstadoElaboracion).trigger('change');
					$('#ntiEstadoElaboracion option[value='+ data.ntiOrigen +']').attr('selected','selected');
					$('#ntiTipoDocumental').val(data.ntiTipoDocumental).trigger('change');
					$('#ntiTipoDocumental option[value='+ data.ntiOrigen +']').attr('selected','selected');
				})
			} else { // if modifying existing document 
				if(confirm("<spring:message code="contingut.document.misatge.avis"/>")){
					$.get("/ripea/modal/contingut/${contingutId}/metaDocument/" +  $(this).val() + "/dadesnti")
					.done(function(data) {			
						$('#ntiOrigen').val(data.ntiOrigen).trigger('change');
						$('#ntiOrigen option[value='+ data.ntiOrigen +']').attr('selected','selected');
						$('#ntiEstadoElaboracion').val(data.ntiEstadoElaboracion).trigger('change');
						$('#ntiEstadoElaboracion option[value='+ data.ntiOrigen +']').attr('selected','selected');
						$('#ntiTipoDocumental').val(data.ntiTipoDocumental).trigger('change');
						$('#ntiTipoDocumental option[value='+ data.ntiOrigen +']').attr('selected','selected');
					})
				}
			}
			$.get("/ripea/modal/contingut/${contingutId}/metaDocument/" +  $(this).val())
			.done(function(data) {
				if (data.plantillaNom) {
					$('#info-plantilla-si').removeClass('hidden');
					$('#info-plantilla-si a').attr('href', '../metaDocument/' + data.id + '/plantilla');
				} else {
					$('#info-plantilla-si').addClass('hidden');
				}
				webutilModalAdjustHeight();
			})
			.fail(function() {
				alert("<spring:message code="contingut.document.form.alert.plantilla"/>");
			});
		} else {
			$('#info-plantilla-si').addClass('hidden');
		}
	});
	$('input[type=checkbox][name=ambFirma]').on('change', function() {
		if($(this).prop("checked") == true){
			$('#input-firma').removeClass('hidden');
			if($('input[type=radio][name=tipusFirma]').val() != 'SEPARAT') {
				$('#input-firma-arxiu').addClass('hidden');
			}
		} else {
			$('#input-firma').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});
	$('input[type=radio][name=tipusFirma]').on('change', function() {
		if ($(this).val() == 'SEPARAT') {
			$('#input-firma-arxiu').removeClass('hidden');
		} else {
			$('#input-firma-arxiu').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});

	
	if($('#id').val() == '') {
		$('#metaNodeId').trigger('change');
	}
	$('input[type=radio][name=origen][value=${documentCommand.origen}]').trigger('change');
	$('input[type=checkbox][name=ambFirma').trigger('change');
	$('input[type=radio][name=tipusFirma][value=${documentCommand.tipusFirma}]').trigger('change');
	let droppedFiles = window.parent.document.getElementById('dropped-files');
	if (droppedFiles) {
		let droppedFilesFiles = droppedFiles.files;
		if (droppedFilesFiles && droppedFilesFiles.length == 1) {
			document.querySelector('#arxiu').files = droppedFilesFiles;
			mostrarDocument(droppedFilesFiles[0].name);
			
		}
	}
	
	var nom = $('#nom');
	var invalid = new RegExp('[\/:*?\"<>|]');
	$(nom).keypress(function(e) {
		var s = String.fromCharCode(e.which);
		//Comprovar cada lletra
	    if (invalid.test(s)) {
	    	$('#nom').tooltip("show");
	    }
	    //Comprovar títol
	    if (invalid.test($(nom).val())) {
	    	$('#nom').tooltip('enable');
	    	$('#nom').tooltip("show");
	    } else {
	    	$('#nom').tooltip('disable');
	    	$('#nom').tooltip("hide");
	    }
	});
});
</script>
</head>
<body>



	<c:choose>
		<c:when test="${isTasca}">
			<c:set var="formAction"><rip:modalUrl value="/usuariTasca/${tascaId}/pare/${documentCommand.pareId}/document${isCreate ? '/docNew' : '/docUpdate'}"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="formAction"><rip:modalUrl value="/contingut/${documentCommand.pareId}/document${isCreate ? '/docNew' : '/docUpdate'}"/></c:set>
		</c:otherwise>
	</c:choose>
	
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="documentCommand" enctype="multipart/form-data">
		<div id="info-plantilla-si" class="alert well-sm alert-info hidden">
			<span class="fa fa-info-circle"></span>
			<spring:message code="contingut.document.form.info.plantilla.si"/>
			<a href="#" class="btn btn-xs btn-default pull-right"><spring:message code="comu.boto.descarregar"/></a>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<form:hidden path="documentTipus"/>

		<c:choose>
			<c:when test="${documentCommand.documentTipus == 'IMPORTAT'}">
				<c:set var="readOnlyValue" value="true"/>
				<p class="comentari col-xs-10 col-xs-offset-2"><spring:message code="contingut.document.form.importat"/></p><br><br>
			</c:when>
			<c:otherwise>
				<c:set var="readOnlyValue" value="false"/>
			</c:otherwise>
		</c:choose>

		<rip:inputText name="nom" textKey="contingut.document.form.camp.nom" required="true" tooltip="true" tooltipMsg="contingut.document.form.camp.nom.caracters"/>
		<rip:inputDate name="data" textKey="contingut.document.form.camp.data" required="true" readonly="${readOnlyValue}"/>
		<rip:inputSelect name="metaNodeId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom"/>
		<rip:inputSelect name="ntiEstadoElaboracion" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.estela" required="true" optionItems="${ntiEstatElaboracioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>

		<c:if test="${!readOnlyValue}">
			<rip:inputFile name="arxiu" textKey="contingut.document.form.camp.arxiu" required="${empty documentCommand.id}" />
	
			<rip:inputCheckbox name="ambFirma" textKey="contingut.document.form.camp.amb.firma"></rip:inputCheckbox>
			<div id="input-firma" class="hidden">
				<rip:inputRadio name="tipusFirma" textKey="contingut.document.form.camp.tipus.firma" botons="true" optionItems="${tipusFirmaOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<div id="input-firma-arxiu" class="hidden">
					<rip:inputFile name="firma" textKey="contingut.document.form.camp.firma" required="${empty documentCommand.id}"/>
				</div>
			</div>
		</c:if>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/contingut/${documentCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
