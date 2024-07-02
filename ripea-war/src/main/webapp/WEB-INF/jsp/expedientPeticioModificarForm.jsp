<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="titol">
	<spring:message code="expedient.peticio.canviar.procediment.form.titol" />
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

//################################################## document ready START ##############################################################
$(document).ready(function() {


	$('#expedientPeticioModificarForm').on('submit', function () {
		  $(this).find('select#grupId').prop('disabled', false);
		});
	
	$('select#metaExpedientId').change(function(event) {

		refrescarGrup(false);
	});
	
	refrescarGrup(true);

});//################################################## document ready END ##############################################################



function refrescarGrup(firstTime) {
	$("#grupId").empty();
	debugger
	const metaExpedientId = $('#metaExpedientId').val();
	if (metaExpedientId != undefined ) {

		var findUrl = '<c:url value="/expedientPeticio/findGrups?procedimentId="/>' + metaExpedientId;
		$.ajax({
	        type: "GET",
	        url: findUrl,
	        success: function (data) {
		        debugger

	        	if (data != undefined && data != 0) {

	        		var emptyOption = new Option('', '', false, false);
	        		$('#grupId').append(emptyOption);
		        	
	    		    $.each(data, function(i, val) {

		        		var newOption = new Option(val.descripcio, val.id, false, false);
		        		$('#grupId').append(newOption);
	    		    });

	    		    <c:if test="${expedientPeticioModificarCommand.grupId != null}">
					if (firstTime) {
		        		$('#grupId').val(${expedientPeticioModificarCommand.grupId});
					}
					</c:if>

	    			$('#grupId').trigger('change');
	    		    $("#grupId").parent().parent().show();
	    		    


	        	} else {
	        		$("#grupId").parent().parent().hide();
	        	}
	        }
		});

	} else {
		$("#grupId").parent().parent().hide();
	}


}




</script>

</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/expedientPeticio/canviarProcediment"/></c:set>
	<form:form id="expedientPeticioModificarForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientPeticioModificarCommand">
		<form:hidden path="id" />

		<rip:inputText name="numero" textKey="expedient.peticio.list.columna.numero" readonly = "true"/>
		<rip:inputText name="extracte" textKey="expedient.peticio.list.columna.extracte" readonly = "true"/>

		<rip:inputSelect 
			name="metaExpedientId"
			textKey="expedient.peticio.form.acceptar.camp.metaExpedient" 
			required="true"
			optionItems="${metaExpedients}" 
			optionValueAttribute="id" 
			emptyOption="true"
			optionTextAttribute="codiSiaINom" 
			optionMinimumResultsForSearch="6" />
	
		<rip:inputSelect 
			name="grupId" 
			optionItems="${grups}" 
			optionValueAttribute="id" 
			optionTextAttribute="descripcio"
			textKey="metaexpedient.relacionar.grup.form.camp.grup" 
			emptyOption="true"
			disabled="${rolActual == 'tothom'}"/>
	


		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>

