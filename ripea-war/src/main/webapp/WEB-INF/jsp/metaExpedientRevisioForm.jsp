<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="metaexpedient.revisio.form.titol.canviarEstatRevisio"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
	
	
<script>
	$(document).ready(function(){
		$('#revisioEstat').on('change', function() {
			var estat = $(this).val();
			if (estat=='REBUTJAT') {
            	$("label[for='revisioComentari']").append( " *" );
			} else {
				$("label[for='revisioComentari']").text( $("label[for='revisioComentari']").text().replace(' *', '') );
			}
		});
	});
</script>	
	
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedientRevisio"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaExpedientRevisioCommand" role="form" >
	
		<form:hidden path="id"/>
		<rip:inputSelect name="revisioEstat" optionEnum="MetaExpedientRevisioEstatEnumDto" textKey="metaexpedient.revisio.form.camp.estatRevisio"/>
		<rip:inputTextarea name="revisioComentari" textKey="metaexpedient.revisio.form.camp.comentari" required="${metaExpedientRevisioCommand.revisioEstat=='REBUTJAT'}"/>

		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/entitat"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
