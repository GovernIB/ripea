<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
pageContext.setAttribute(
		"dadesUsuariActual",
		es.caib.ripea.war.helper.SessioHelper.getUsuariActual(request));
%>
<c:set var="titol">
	<spring:message code="expedient.peticio.form.acceptar.titol" />
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
var metaExpedientOrgan = {};
<c:forEach var="metaExpedient" items="${metaExpedients}">
<c:if test="${not empty metaExpedient.organGestor}">metaExpedientOrgan['${metaExpedient.id}'] = {id: ${metaExpedient.organGestor.id}, codi: '${metaExpedient.organGestor.codi}', nom: '${metaExpedient.organGestor.nom}'};</c:if>
</c:forEach>



function refrescarOrgan() {
	const metaExpedientId = $('#metaExpedientId').val();
	const organ = metaExpedientOrgan[metaExpedientId];
	if (organ) {
		$('#organFixed').show();
		$('#organSelect').hide();
		$('#organFixedNom').text(organ.nom);
		$('#organFixedNom').after($('<input>').attr({
		    type: 'hidden',
		    name: 'organGestorId',
		    value: organ.id
		}));
	} else {
		$.ajax({
			type: 'GET',
			url: '<c:url value="/expedient/metaExpedient"/>/' + metaExpedientId + '/organsGestorsPermesos/${expedientPeticioAcceptarCommand.expedientId!=null ? expedientPeticioAcceptarCommand.expedientId : ''}',
			success: function(organs) {
				const selOrgans = $('select#organGestorId');
				const organGestorId = '${expedientCommand.organGestorId}';
				selOrgans.empty();
				if (organs && organs.length > 0) {
					$.each(organs, function(i, organ) {
						const selected = (organ.id == organGestorId) ? ' selected' : '';
						selOrgans.append('<option value="' + organ.id + '"' + selected + '>' + organ.nom + '</option>');
					});
				}
				selOrgans.select2({
					theme: 'bootstrap',
					width: 'auto'
				});
			}
		});
		$('#organFixed').hide();
		$('#organSelect').show();
		$('input', $('#organFixedNom').parent()).remove();
	}
}


$(document).ready(function(){

	if ('${expedientPeticioAcceptarCommand.accio}' == 'CREAR') {
		$('#input-accio-crear').removeClass('hidden');
		$('#input-accio-incorporar').addClass('hidden');
	} else {
		$('#input-accio-incorporar').removeClass('hidden');
		$('#input-accio-crear').addClass('hidden');
	}

	$('#metaExpedientId').on('change', function() {
		var tipus = $(this).val();
		$('#expedientId').select2('val', '', true);
		$('#expedientId option[value!=""]').remove();
		
		$('#expedientId').next().find('span.select2-selection__arrow').addClass('fa fa-spin fa-circle-o-notch  fa-1x').removeClass('select2-selection__arrow');
		
		if (tipus != "") {
			$.get("<c:url value="/expedientPeticio/expedients/"/>"+${entitatId}+"/"+tipus)
			.done(function(data){
				for (var i = 0; i < data.length; i++) {
					if (data[i].agafat && data[i].agafatPer.codi === '${dadesUsuariActual.codi}')
						$('#expedientId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					else
						$('#expedientId').append('<option value="' + data[i].id + '" disabled>' + data[i].nom + '</option>');
				}
				$('#expedientId').next().find('span.fa-circle-o-notch').addClass('select2-selection__arrow').removeClass('fa fa-spin fa-circle-o-notch  fa-1x');
			})
			.fail(function() {
				$('#expedientId').next().find('span.fa-circle-o-notch').addClass('select2-selection__arrow').removeClass('fa fa-spin fa-circle-o-notch  fa-1x');
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		}

		refrescarOrgan();

		var createPermis;
		var writePermis;

		$.ajax({
			type: 'GET',
			url: '<c:url value="/expedientPeticio/comprovarPermisCreate/"/>' + tipus,
			async: false,
			success: function(data) {
				createPermis = data;
			}
		});
		$.ajax({
			type: 'GET',
			url: '<c:url value="/expedientPeticio/comprovarPermisWrite/"/>' + tipus,
			async: false,
			success: function(data) {
				writePermis = data;
			}
		});

		if (createPermis && writePermis) {
		    $('#accio1').parent().show();
		    $('#accio2').parent().show();

		} else if (createPermis) {
		    $('#accio1').parent().show();
		    $('#accio2').parent().hide();
		    $("#accio1").click();

		} else if (writePermis) {
		    $('#accio2').parent().show();
		    $('#accio1').parent().hide();
		    $("#accio2").click();
		}

		

	});	

	$('#metaExpedientId').trigger('change');


	$('input[type=radio][name=accio]').on('change', function() {
		if ($(this).val() == 'CREAR') {
			$('#input-accio-crear').removeClass('hidden');
			$('#input-accio-incorporar').addClass('hidden');
		} else {
			$('#input-accio-incorporar').removeClass('hidden');
			$('#input-accio-crear').addClass('hidden');
		}
		webutilModalAdjustHeight();
	});

	$('#btnSave').on('click', function() {

		var showConfirm = false;
		var expedientId = $('#expedientId').val();
		if (expedientId) {
			var associarInteressats = $('#associarInteressats').is(":checked");
			if (associarInteressats) {
				$.ajax({
					type: 'GET',
					url: '<c:url value="/expedientPeticio/comprovarInteressatsPeticio/"/>' + expedientId + '/${expedientPeticioId}',
					async: false,
					success: function(data) {
						showConfirm = data;
					}
				});
			}
		}
		if (showConfirm) {
			var overrideInteressat = confirm("<spring:message code="expedient.peticio.form.acceptar.confirm"/>");
			if (overrideInteressat) {
				$('#expedientPeticioAcceptarForm').submit();
			}
		} else {
			$('#expedientPeticioAcceptarForm').submit();
		}

	});

	$("button#btnSave").submit(function (e) {
	    e.preventDefault();
	    $("button#btnSave").attr("disabled", true);
	    return true;
	});		
			
});
</script>

</head>
<body>
	<c:set var="formAction">
		<rip:modalUrl value="/expedientPeticio/acceptar/${expedientPeticioId}/next" />
	</c:set>
	<form:form id="expedientPeticioAcceptarForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientPeticioAcceptarCommand">
		<form:hidden path="id" />

		<rip:inputRadio name="accio" textKey="expedient.peticio.form.acceptar.camp.accio" botons="true" optionItems="${accios}" optionValueAttribute="value" optionTextKeyAttribute="text"/>

		<rip:inputSelect name="metaExpedientId" textKey="expedient.peticio.form.acceptar.camp.metaExpedient"
			required="true" optionItems="${metaExpedients}" optionValueAttribute="id" emptyOption="true"
			optionTextAttribute="codiSiaINom" optionMinimumResultsForSearch="6"/>
			
		<div id="input-accio-incorporar" class="hidden">
			<rip:inputSelect name="expedientId"
				textKey="expedient.peticio.form.acceptar.camp.expedient" required="true" 
	 			optionItems="${expedients}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="6"/> 
		</div>
		<div id="input-accio-crear" class="hidden">
			<rip:inputText name="newExpedientTitol" textKey="expedient.peticio.form.acceptar.camp.newExpedientTitol"
				required="true" />
			<div id="organFixed" style="display: none;">
				<rip:inputFixed textKey="contingut.expedient.form.camp.organ" required="true"><span id="organFixedNom"></span></rip:inputFixed>
			</div>
			<div id="organSelect" style="display: none;">
				<rip:inputSelect name="organGestorId" textKey="contingut.expedient.form.camp.organ" required="true"/>
			</div>			
			<rip:inputText name="any" textKey="expedient.peticio.form.acceptar.camp.any" required="true"/> 			
		</div>
		
		<rip:inputCheckbox name="associarInteressats"
 			textKey="expedient.peticio.form.acceptar.camp.associarInteressats"/> 
 			
		<div id="modal-botons" class="well">
			<button id="btnSave" type="submit" data-toggle="modal" class="btn btn-success">
				<span class="fa fa-arrow-right"></span>
				<spring:message code="comu.boto.next" />
			</button>
			<a href="<c:url value="/expedientPeticio"/>" class="btn btn-default"
				data-modal-cancel="true"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>
</body>
</html>

