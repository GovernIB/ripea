\<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


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
var metaExpedientGrup = {};
<c:forEach var="metaExpedient" items="${metaExpedients}">
<c:if test="${not empty metaExpedient.organGestor}">metaExpedientOrgan['${metaExpedient.id}'] = {id: ${metaExpedient.organGestor.id}, codi: '${metaExpedient.organGestor.codi}', nom: '${fn:escapeXml(metaExpedient.organGestor.nom)}'};</c:if>
metaExpedientGrup['${metaExpedient.id}'] = {gestioAmbGrupsActiva: ${metaExpedient.gestioAmbGrupsActiva}};
</c:forEach>



function refrescarOrgan() {
	const metaExpedientId = $('#metaExpedientId').val();
	const organ = metaExpedientOrgan[metaExpedientId];
	if (organ) {
		$('#organFixed').show();
		$('#organSelect').hide();
		$('#organFixedNom').text(organ.codi + ' - ' + organ.nom);
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
						selOrgans.append('<option value="' + organ.id + '"' + selected + '>' + organ.nomComplet + '</option>');
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


//################################################## document ready START ##############################################################
$(document).ready(function(){


	$('#expedientPeticioAcceptarForm').on('submit', function () {
	  $(this).find('select#grupId').prop('disabled', false);
	});
	
	if ('${expedientPeticioAcceptarCommand.accio}' == 'CREAR') {
		$('#input-accio-crear').removeClass('hidden');
		$('#input-accio-incorporar').addClass('hidden');
	} else {
		$('#input-accio-incorporar').removeClass('hidden');
		$('#input-accio-crear').addClass('hidden');
	}

	$('#metaExpedientId').on('change', function() {
		var tipus = $(this).val();
		var expedientId = $('#expedientId').val();

		$('#expedientId').select2('val', '', true);
		$('#expedientId option[value!=""]').remove();
		
		$('#expedientId').next().find('span.select2-selection__arrow').addClass('fa fa-spin fa-circle-o-notch  fa-1x').removeClass('select2-selection__arrow');
		
		if (tipus != "") {
			$.get("<c:url value="/expedientPeticio/expedients/"/>"+${entitatId}+"/"+tipus)
			.done(function(data){
				for (var i = 0; i < data.length; i++) {
					if (data[i].id == expedientId) {
						$('#expedientId').append('<option value="' + data[i].id + '" selected>' + data[i].numeroINom + '</option>');
					} else {
						$('#expedientId').append('<option value="' + data[i].id + '">' + data[i].numeroINom + '</option>');
					}
				}
				$('#expedientId').next().find('span.fa-circle-o-notch').addClass('select2-selection__arrow').removeClass('fa fa-spin fa-circle-o-notch  fa-1x');
			})
			.fail(function() {
				$('#expedientId').next().find('span.fa-circle-o-notch').addClass('select2-selection__arrow').removeClass('fa fa-spin fa-circle-o-notch  fa-1x');
				alert("<spring:message code="error.jquery.ajax"/>");
			});
		}

		refrescarOrgan();
		refrescarSequencia();
		refrescarGrups();

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

	$("button#btnSave").submit(function (e) {
	    e.preventDefault();
	    $("button#btnSave").attr("disabled", true);
	    
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
			
});//################################################## document ready END ##############################################################



function refrescarSequencia() {
	let metaExpedientId = $('#metaExpedientId').val();
	let any = $('input#any').val();
	if (metaExpedientId != undefined && metaExpedientId != "" && any != undefined && any != "") {
		$.ajax({
			type: 'GET',
			url: '<c:url value="/expedient/metaExpedient"/>/' + metaExpedientId + '/proximNumeroSequencia/' + any,
			success: function(sequencia) {
				$('input#sequencia').val(sequencia);
			}
		});
	} else {
		$('input#sequencia').val(undefined);
	}
}

function refrescarGrups() {

	let metaExpedientId = $('#metaExpedientId').val();
	if (metaExpedientId != undefined && metaExpedientId != "") {
		const gestioAmbGrupsActiva = metaExpedientGrup[metaExpedientId].gestioAmbGrupsActiva;
		$("#gestioAmbGrupsActiva").val(gestioAmbGrupsActiva);
		if (gestioAmbGrupsActiva) {
			$("#grupsActiu").removeClass("hidden");

			<c:choose>
				<c:when test="${not empty expedientPeticioAcceptarCommand.grupId}">
					let grupId = ${expedientPeticioAcceptarCommand.grupId};
				</c:when>
				<c:otherwise>
					let grupId = '';
				</c:otherwise>
			</c:choose>

			if (typeof firstTime === 'undefined' && grupId) {
				$('#grupId').val(grupId);
				grupIdDefault = grupId;
				firstTime = 'defined';	
			} else {
				grupIdDefault = '';
			}

			if (${rolActual == 'tothom'}) {

				if (grupIdDefault) {

					$.ajax({
						type: 'GET',
						url: '<c:url value="/expedientPeticio/findGrupById"/>/' + grupIdDefault,
						success: function(data) {
							
							var newOption = new Option(data.descripcio, data.id, false, false);
							$('#grupId').append(newOption);
							$('#grupId').val(grupIdDefault);
							$('#grupId').trigger('change');
						}
					});
					
				} else {
					
					$.ajax({
						type: 'GET',
						url: '<c:url value="/expedientPeticio/findGrupByProcedimentId"/>/' + ${expedientPeticioAcceptarCommand.id} + '/' + metaExpedientId,
						success: function(data) {

							var newOption = new Option(data.descripcio, data.id, false, false);
							$('#grupId').append(newOption);
							$('#grupId').val(data.id);
							$('#grupId').trigger('change');

							$('#grupId').closest('.form-group').show();
						}
					});
				}
			
			} else {

				$.ajax({
					type: 'GET',
					url: '<c:url value="/expedient/metaExpedient"/>/' + metaExpedientId + '/grup',
					success: function(data) {
						$('#grupId').closest('.form-group').show();
						$('#grupId option[value!=""]').remove();
						for (var i = 0; i < data.length; i++) {
							$('#grupId').append('<option value="' + data[i].id + '">' + data[i].descripcio + '</option>');
						}
						if (grupIdDefault) {
							$('#grupId').val(grupIdDefault);
						}
					}
				});
				
			}
			
		} else {
			$('#grupId option[value!=""]').remove();
			$("#grupsActiu").addClass("hidden");
		}
	}

}


</script>

</head>
<body>
	<c:set var="formAction">
		<rip:modalUrl value="/expedientPeticio/acceptar/${expedientPeticioId}/getFirstAnnex" />
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
	 			optionItems="${expedients}" optionValueAttribute="id" optionTextAttribute="numeroINom" emptyOption="true" optionMinimumResultsForSearch="6"/> 
	 		<rip:inputCheckbox name="agafarExpedient" textKey="expedient.peticio.form.acceptar.camp.agafarExpedient"/> 
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
			<rip:inputText name="sequencia" textKey="contingut.expedient.form.camp.sequencia" required="false" disabled="true"/>
			<rip:inputText name="any" textKey="expedient.peticio.form.acceptar.camp.any" required="true"/> 	
			<form:hidden path="gestioAmbGrupsActiva"/>
			<div id="grupsActiu" class="<c:if test="${not expedientCommand.gestioAmbGrupsActiva}">hidden</c:if>">
				<rip:inputSelect name="grupId" optionItems="${grups}" required="true" optionValueAttribute="id" optionTextAttribute="descripcio" textKey="contingut.expedient.form.camp.grup" disabled="${rolActual == 'tothom'}"/>
			</div>					
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

