<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${empty metaDadaCommand.id}"><c:set var="titol"><spring:message code="metadada.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="metadada.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
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
	
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/clamp.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jquery.filedrop.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>

<style type="text/css">
.disabled {
	pointer-events: none;
    opacity: 0.4; 
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	$('select#tipus').change(function() {
		if ($(this).val() == 'DOMINI') {
			let valor = '${selectedMetaDada}';
			$('#codi').addClass('disabled');
			//la implementació actual no permet multiplicitat per camps tipus select
			$('#multiplicitat').val('M_0_1');
			$('#multiplicitat').trigger('change');
			$('#multiplicitat').nextAll('span:first').addClass('disabled');
			
			$('#domini').parent().parent().show();
			$('#valorString').parent().parent().hide();
			$('#valorSencer').parent().parent().hide();
			$('#valorFlotant').parent().parent().hide();
			$('#valorImport').parent().parent().hide();
			$('#valorData').parent().parent().hide();
			$('#valorBoolea').parent().parent().hide();
			
			$.ajax({
				type: 'GET',
				url: "<c:url value="/metaExpedient/${metaDadaCommand.metaNodeId}/metaDada/domini"/>",
				success: function(data) {
					var $selOrgan = $('#domini');
					$selOrgan.empty();
					$selOrgan.append("<option value=\"\"></option>");
					if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.nom
							});
							if (valor == val.codi) {
								$selOrgan.append("<option value=\"" + val.codi + "\" selected>" + val.nom + "</option>");
							} else {
								$selOrgan.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
							}
						});
					}
					var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
					$selOrgan.select2("destroy");
					$selOrgan.select2(select2Options);
				}
			});
		} else if ($(this).val() == 'BOOLEA') {
			$('#valorSencer').parent().parent().hide();
			$('#valorFlotant').parent().parent().hide();
			$('#valorImport').parent().parent().hide();
			$('#valorData').parent().parent().hide();
			$('#valorBoolea').parent().parent().show();
			$('#valorString').parent().parent().hide();
			$('#domini').parent().parent().hide();
			$('#codi').removeClass('disabled');
			$('#multiplicitat').nextAll('span:first').removeClass('disabled');
			
		} else if ($(this).val() == 'DATA') {
			$('#valorSencer').parent().parent().hide();
			$('#valorFlotant').parent().parent().hide();
			$('#valorImport').parent().parent().hide();
			$('#valorData').parent().parent().show();
			$('#valorBoolea').parent().parent().hide();
			$('#valorString').parent().parent().hide();
			$('#domini').parent().parent().hide();
			$('#codi').removeClass('disabled');
			$('#multiplicitat').nextAll('span:first').removeClass('disabled');
			
		} else if ($(this).val() == 'FLOTANT') {
			$('#valorSencer').parent().parent().hide();
			$('#valorFlotant').parent().parent().show();
			$('#valorImport').parent().parent().hide();
			$('#valorData').parent().parent().hide();
			$('#valorBoolea').parent().parent().hide();
			$('#valorString').parent().parent().hide();
			$('#domini').parent().parent().hide();
			$('#codi').removeClass('disabled');
			$('#multiplicitat').nextAll('span:first').removeClass('disabled');
			
		} else if ($(this).val() == 'IMPORT') {
			$('#valorSencer').parent().parent().hide();
			$('#valorFlotant').parent().parent().hide();
			$('#valorImport').parent().parent().show();
			$('#valorData').parent().parent().hide();
			$('#valorBoolea').parent().parent().hide();
			$('#valorString').parent().parent().hide();
			$('#domini').parent().parent().hide();
			$('#codi').removeClass('disabled');
			$('#multiplicitat').nextAll('span:first').removeClass('disabled');
			
		} else if ($(this).val() == 'SENCER') {
			$('#valorSencer').parent().parent().show();
			$('#valorFlotant').parent().parent().hide();
			$('#valorImport').parent().parent().hide();
			$('#valorData').parent().parent().hide();
			$('#valorBoolea').parent().parent().hide();
			$('#valorString').parent().parent().hide();
			$('#domini').parent().parent().hide();
			$('#codi').removeClass('disabled');
			$('#multiplicitat').nextAll('span:first').removeClass('disabled');
			
		}  else if ($(this).val() == 'TEXT') {
			$('#valorSencer').parent().parent().hide();
			$('#valorFlotant').parent().parent().hide();
			$('#valorImport').parent().parent().hide();
			$('#valorData').parent().parent().hide();
			$('#valorBoolea').parent().parent().hide();
			$('#valorString').parent().parent().show();
			$('#domini').parent().parent().hide();
			$('#codi').removeClass('disabled');
			$('#multiplicitat').nextAll('span:first').removeClass('disabled');
		}	


				
		
	});

		


	$('select#domini').change(function() {
		var dominiCodiSelected = $(this).val();		
		var dominiNomSelected = $(this).text();
		if (dominiCodiSelected != null && dominiCodiSelected != '') {
			$('#codi').val(dominiCodiSelected);
			//$('#valorString').val(dominiCodiSelected);
		}
	});
	$('select#domini').trigger('change');
	$('select#tipus').trigger('change');
});
</script>
</head>
<body>
	<form:form action="." method="post" cssClass="form-horizontal" commandName="metaDadaCommand">
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="metaNodeId"/>
		<rip:inputText name="codi" textKey="metadada.form.camp.codi" required="true" readonly="${bloquejarCamps}"/>
		<rip:inputText name="nom" textKey="metadada.form.camp.nom" required="true" readonly="${bloquejarCamps}"/>
		<rip:inputSelect name="tipus" textKey="metadada.form.camp.tipus" optionEnum="MetaDadaTipusEnumDto" disabled="${bloquejarCamps}"/>
		<rip:inputSelect name="multiplicitat" textKey="metadada.form.camp.multiplicitat" optionEnum="MultiplicitatEnumDto" disabled="${bloquejarCamps}"/>
		
		
		<c:set var="displaySencer"></c:set>
		<c:if test="${metaDadaCommand.tipus != 'SENCER'}">
			<c:set var="displaySencer">display:none;</c:set>
		</c:if>
		<div class="form-group" style="${displaySencer}">
			<label class="control-label col-xs-4">
				<spring:message code="metadada.form.camp.valor"/>
			</label>
			<div class="col-xs-8">	
				<form:input path="valorSencer" id="valorSencer" data-toggle="autonumeric" data-a-dec="," data-a-sep="" data-m-dec="0" class="form-control text-right${multipleClass}" disabled="${bloquejarCamps}"></form:input>
			</div>		
		</div>

		<c:set var="displayFlotant"></c:set>
		<c:if test="${metaDadaCommand.tipus != 'FLOTANT'}">
			<c:set var="displayFlotant">display:none;</c:set>
		</c:if>
		<div class="form-group" style="${displayFlotant}">
			<label class="control-label col-xs-4">
				<spring:message code="metadada.form.camp.valor"/>
			</label>
			<div class="col-xs-8">	
				<form:input path="valorFlotant" id="valorFlotant" data-toggle="autonumeric" data-a-dec="," data-a-sep="" data-m-dec="10" data-a-pad="false" class="form-control text-right${multipleClass}" disabled="${bloquejarCamps}"></form:input>
			</div>
		</div>

		<c:set var="displayImport"></c:set>
		<c:if test="${metaDadaCommand.tipus != 'IMPORT'}">
			<c:set var="displayImport">display:none;</c:set>
		</c:if>
		<div class="form-group" style="${displayImport}">
			<label class="control-label col-xs-4">
				<spring:message code="metadada.form.camp.valor"/>
			</label>
			<div class="col-xs-8">	
				<form:input path="valorImport" id="valorImport" data-toggle="autonumeric" data-a-dec="," data-a-sep="." data-m-dec="2" class="form-control text-right${multipleClass}" disabled="${bloquejarCamps}"></form:input>
			</div>
		</div>
		
		<c:set var="displayData"></c:set>
		<c:if test="${metaDadaCommand.tipus != 'DATA'}">
			<c:set var="displayData">display:none;</c:set>
		</c:if>	
		<div class="form-group" style="${displayData}">
			<label class="control-label col-xs-4">
				<spring:message code="metadada.form.camp.valor"/>
			</label>
			<div class="col-xs-8">	
				<form:input path="valorData" id="valorData" data-toggle="datepicker" data-idioma="${requestLocale}" cssClass="form-control text-right${multipleClass}"  disabled="${bloquejarCamps}"></form:input>
			</div>
		</div>

		<c:set var="displayBoolea"></c:set>
		<c:if test="${metaDadaCommand.tipus != 'BOOLEA'}">
			<c:set var="displayBoolea">display:none;</c:set>
		</c:if>
		<div class="form-group" style="${displayBoolea}">
			<label class="control-label col-xs-4">
				<spring:message code="metadada.form.camp.valor"/>
			</label>
			<div class="col-xs-8">	
				<form:checkbox path="valorBoolea" id="valorBoolea" name="valorBoolea" disabled="${bloquejarCamps}"></form:checkbox>
			</div>
		</div>

		<c:set var="displayString"></c:set>
		<c:if test="${metaDadaCommand.tipus != 'TEXT' && metaDadaCommand.tipus != 'DOMINI'}">
			<c:set var="displayString">display:none;</c:set>
		</c:if>
		<div class="form-group" style="${displayString}">
			<label class="control-label col-xs-4" for="valorString">
				<spring:message code="metadada.form.camp.valor"/>
			</label>
			<div class="col-xs-8">	
				<form:input path="valorString" id="valorString" cssClass="form-control${multipleClass}" disabled="${bloquejarCamps}"></form:input>
			</div>
		</div>
	
		<rip:inputSelect name="domini" textKey="metadada.form.camp.domini" disabled="${bloquejarCamps}"/>
		<rip:inputTextarea name="descripcio" textKey="metadada.form.camp.descripcio" disabled="${bloquejarCamps}"/>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success" <c:if test="${bloquejarCamps}">disabled</c:if>><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaDada"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
