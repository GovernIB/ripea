<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="titol"><spring:message code="contingut.document.form.titol.generar"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
	

<script>


$(document).ready(function() {


	// METADOCUMENT CHANGE
	$('#metaNodeId').on('change', function() {
		if ($(this).val()) {
			$.get('<c:url value="/modal/contingut/${expedientId}/metaDocument/"/>' +  $(this).val() + '/dadesnti')
			.done(function(data) {			
				$('#ntiOrigen').val(data.ntiOrigen).trigger('change');
				$('#ntiEstadoElaboracion').val(data.ntiEstadoElaboracion).trigger('change');
			})
		} else {
			$('#ntiOrigen').val('').trigger('change');
			$('#ntiEstadoElaboracion').val('').trigger('change');
		}
	});
	
});

</script>	
	
	
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/entitat"/></c:set>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="documentCommand">
	
		<rip:inputSelect name="metaNodeId" textKey="contingut.document.form.camp.metanode" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="${fn:length(metaDocuments) > 1 ? true : false}" emptyOptionTextKey="contingut.document.form.camp.nti.cap" required="true"/>
		<rip:inputSelect name="ntiOrigen" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.origen" optionEnum="NtiOrigenEnumDto" required="true"/>
		<rip:inputSelect name="ntiEstadoElaboracion" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.estela" required="true" optionEnum="DocumentNtiEstadoElaboracionEnumDto"/>
	
		
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/entitat"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
