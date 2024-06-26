<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty expedientCommand.id}"><c:set var="titol"><spring:message code="contingut.expedient.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="contingut.expedient.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<body>
<c:choose>
	<c:when test="${empty expedientCommand.id}"><c:set var="formAction"><rip:modalUrl value="/expedient/new"/></c:set></c:when>
	<c:otherwise><c:set var="formAction"><rip:modalUrl value="/expedient/${expedientCommand.id}/update"/></c:set></c:otherwise>
</c:choose>
<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="expedientCommand">
	<form:hidden path="id"/>
	<form:hidden path="entitatId"/>
	<form:hidden path="pareId"/>
	<c:choose>
		<c:when test="${empty expedientCommand.id}">
			<rip:inputSelect name="metaNodeId" textKey="contingut.expedient.form.camp.metanode" required="true" emptyOption="${fn:length(metaExpedients) > 1 ? true : false}" emptyOptionTextKey="contingut.document.form.camp.nti.cap" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="codiSiaINom" labelSize="2" optionMinimumResultsForSearch="0"/>
		</c:when>
		<c:otherwise>
			<form:hidden path="metaNodeId"/>
		</c:otherwise>
	</c:choose>
	<rip:inputText name="nom" textKey="contingut.expedient.form.camp.nom" required="true" labelSize="2"/>
	<div id="organFixed">
		<rip:inputFixed textKey="contingut.expedient.form.camp.organ" required="true" labelSize="2"><span id="organFixedNom"></span></rip:inputFixed>
	</div>
	<div id="organSelect">
		<rip:inputSelect name="organGestorId" textKey="contingut.expedient.form.camp.organ" required="true" labelSize="2"/>
	</div>
	<rip:inputText name="sequencia" textKey="contingut.expedient.form.camp.sequencia" required="false" labelSize="2" disabled="true"/>
	<rip:inputText name="any" textKey="contingut.expedient.form.camp.any" required="true" labelSize="2"/>
	<form:hidden path="gestioAmbGrupsActiva"/>
	<div id="grupsActiu" style="display:none;">
		<rip:inputSelect name="grupId" optionItems="${grups}" required="true" optionValueAttribute="id" optionTextAttribute="descripcio" textKey="contingut.expedient.form.camp.grup" labelSize="2"/>
	</div>
	<div id="modal-botons" class="well">
		<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>
			<c:choose>
				<c:when test="${empty expedientCommand.id}"><spring:message code="comu.boto.crear"/></c:when>
				<c:otherwise><spring:message code="comu.boto.modificar"/></c:otherwise>
			</c:choose>
		</button>
		<a href="<c:url value="/expedient"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
	</div>
</form:form>
</body>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script type="text/javascript">
var metaExpedientOrgan = {};
var metaExpedientGrup = {};
<c:forEach var="metaExpedient" items="${metaExpedients}">
<c:if test="${not empty metaExpedient.organGestor}">metaExpedientOrgan['${metaExpedient.id}'] = {id: ${metaExpedient.organGestor.id}, codi: '${metaExpedient.organGestor.codi}', nom: '${fn:escapeXml(metaExpedient.organGestor.nom)}'};</c:if>
metaExpedientGrup['${metaExpedient.id}'] = {gestioAmbGrupsActiva: ${metaExpedient.gestioAmbGrupsActiva}};
</c:forEach>
function refrescarSequencia() {
	let metaExpedientId = $('#metaNodeId').val();
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

function recuperarDominisMetaExpedient() {
	let metaExpedientId = $('select#metaNodeId').val();
	if (metaExpedientId != undefined) {
		$.ajax({
			type: 'GET',
			url: '<c:url value="/expedient/metaExpedient"/>/' + metaExpedientId + '/findMetaExpedientDominis',
			success: function(dominis) {
				var selDominis = $('select#metaNodeDominiId');
				var selDominisOriginal = ${expedientCommand.metaNodeDominiId}
				selDominis.empty();
				selDominis.append("<option value=\"\"><spring:message code='contingut.expedient.form.camp.domini.cap'/></option>");
				if (dominis && dominis.length > 0) {
					$.each(dominis, function(i, domini) {
						if (domini.id == selDominisOriginal) {

							console.log(selDominisOriginal);
							console.log(domini.id);
							
							selDominis.append("<option value=\"" + domini.id + "\" selected>" + domini.nom + "</option>");
						} else {
							selDominis.append("<option value=\"" + domini.id + "\">" + domini.nom + "</option>");
						}
					});
				}
				var select2Options = {
						theme: 'bootstrap',
						width: 'auto'};
				selDominis.select2(select2Options);
			},
			error: function(e) {
				console.log("error recuperant els dominis..." + e);
			}
		});
	}
}

function refrescarGrups() {
	let expedientId = $('#id').val();
	let metaExpedientId = $('#metaNodeId').val();
	if (metaExpedientId) {
		let gestioAmbGrupsActiva = metaExpedientGrup[metaExpedientId].gestioAmbGrupsActiva;
		$("#gestioAmbGrupsActiva").val(gestioAmbGrupsActiva);
		if (gestioAmbGrupsActiva) {
			$("#grupsActiu").show();
			if (!expedientId) {
				$.ajax({
					type: 'GET',
					url: '<c:url value="/expedient/metaExpedient"/>/' + metaExpedientId + '/grup',
					success: function(data) {
						$('#grupId').closest('.form-group').show();
						$('#grupId option[value!=""]').remove();
						for (var i = 0; i < data.length; i++) {
							$('#grupId').append('<option value="' + data[i].id + '">' + data[i].descripcio + '</option>');
						}
					}
				});
			}
		} else {
			$('#grupId option[value!=""]').remove();
			$("#grupsActiu").hide();
		}
	}
}

function refrescarOrgan() {
	const metaExpedientId = $('#metaNodeId').val();
	if (metaExpedientId != undefined && metaExpedientId != "") {
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
				url: '<c:url value="/expedient/metaExpedient"/>/' + metaExpedientId + '/organsGestorsPermesos/${expedientCommand.id!=null ? expedientCommand.id : ''}',
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
	} else {
		const selOrgans = $('select#organGestorId');
		selOrgans.empty();
		selOrgans.select2({ theme: 'bootstrap', width: 'auto' });
		$('#organFixed').hide();
		$('#organSelect').show();
		$('input', $('#organFixedNom').parent()).remove();
	}
}

//################################################## document ready START ##############################################################
$(document).ready(function() {
	$('select#metaNodeId').change(function(event) {
		refrescarSequencia();
		refrescarGrups();
		refrescarOrgan();
	});
	$('input#any').change(function(event) {
		refrescarSequencia();
	});
	refrescarSequencia();
	refrescarGrups();
	refrescarOrgan();
	$('input#any').trigger('change');
});//################################################## document ready END ##############################################################
</script>
</head>
</html>