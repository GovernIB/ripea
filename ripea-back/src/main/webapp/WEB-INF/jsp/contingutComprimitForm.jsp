<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.document.form.titol.compresio.crear"/></c:set>

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
				$.get('<c:url value="/modal/contingut/${contingutId}/metaDocument/"/>' +  $(this).val() + '/dadesnti')
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
					$.get('<c:url value="/modal/contingut/${contingutId}/metaDocument/"/>' +  $(this).val() + '/dadesnti')
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
			$.get('<c:url value="/modal/contingut/${contingutId}/metaDocument/"/>' +  $(this).val())
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
});
</script>
</head>
<body>
	<div class="alert alert-warning" role="alert">
	  <div><spring:message code="contingut.document.form.titol.compresio.info"/></div>
	</div>
	<c:set var="formAction"><rip:modalUrl value="/contingut/${documentConcatenatCommand.pareId}/generarZip"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="documentConcatenatCommand" enctype="multipart/form-data">
		<div id="info-plantilla-si" class="alert well-sm alert-info hidden">
			<span class="fa fa-info-circle"></span>
			<spring:message code="contingut.document.form.info.plantilla.si"/>
			<a href="#" class="btn btn-xs btn-default pull-right"><spring:message code="comu.boto.descarregar"/></a>
		</div>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="pareId"/>
		<form:hidden path="documentTipus"/>

		<rip:inputText name="nom" textKey="contingut.document.form.camp.nom" required="true"/>
		<rip:inputDate name="data" textKey="contingut.document.form.camp.data" required="true"/>
		<rip:inputSelect name="metaNodeId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom"/>
		<rip:inputSelect name="ntiEstadoElaboracion" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.estela" required="true" optionItems="${ntiEstatElaboracioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>

		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardarnotificar"/></button>
			<a href="<c:url value="/contingut/${documentConcatenatCommand.pareId}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
