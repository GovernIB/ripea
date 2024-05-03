<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
pageContext.setAttribute(
		"interessatsTipusEnum",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
				 es.caib.ripea.core.api.dto.InteressatTipusEnumDto.class,
				"interessat.tipus.enum."));

%>
<c:choose>
	<c:when test="${empty documentNotificacionsCommand.id}"><c:set var="titol"><spring:message code="notificacio.form.titol.crear"/> ${document.nom}</c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="notificacio.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/autoNumeric/1.9.30/autoNumeric.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/bootbox.all.min.js"/>"></script>
	<rip:modalHead/>
	
<style type="text/css">

.title {
	margin-top: 2%;
	font-size: larger;
}
.title > label {
	color: #ff9523;
}
.title > hr {
	margin-top: 0%;
}
.title-envios {
	color: #ffffff;
	margin-top: 1%;
	font-size: larger;
}
.title-envios > hr {
	margin-top: 0%;
	height: 0.4px;
	background-color: #696666;
}

.title-container {
	text-align: center;
	background-color: #696666;
	width: 12%;
}
</style>

<script>
var interessatTipusEnum = [];
<c:forEach var="tipus" items="${interessatsTipusEnum}">
	interessatTipusEnum["${tipus.value}"] = "<spring:message code="${tipus.text}"/>";
</c:forEach>

//################################################## document ready START ##############################################################
$(document).ready(function() {
	

	
	//select and checkbox elements dont have readonly attribute that allows elements to be greyed out but submitted
	//in order to send disabled values in POST we need to enable them on submit
	$('#notificacioForm').on('submit', function () {
	  $(this).find('select').prop('disabled', false);
	  $(this).find( ".checkbox input" ).prop('disabled', false);
	});
	
	$('#interessatsIds').on("change", function (e) {
		var interessatsSelected = $(this).val();
		var notificacions = eval(${notificacions});
		var notificacionsSeleccionades = [];
		var noNif = false;
		var administracioSir = false;
		$(notificacions).each(function(index, notificacio) {
			//if selected
			if (interessatsSelected != null && notificacio.titular != null && interessatsSelected.includes(notificacio.titular.id.toString())) {
	    		notificacionsSeleccionades.push(notificacio);
	    		var titular = notificacio.titular;
	    		var destinatari = notificacio.destinatari;
	    		if (titular.personaFisica) {
		    		if ((destinatari == null && titular.documentTipus!='NIF' && titular.documentTipus!='DOCUMENT_IDENTIFICATIU_ESTRANGERS')|| (destinatari != null && destinatari.documentTipus!='NIF' && destinatari.documentTipus!='DOCUMENT_IDENTIFICATIU_ESTRANGERS')) {
		    			noNif = true;
					}
				}
	    		
				if (titular.administracio && titular.ambOficinaSir) {
					administracioSir = true;
				}
			}
	    });
		
		if (administracioSir) {
			$("#alertComunicacioSir").show();
			$('#tipus').val("COMUNICACIO");
			$('#tipus').trigger('change');
		} else {
			$("#alertComunicacioSir").hide();
		}
		
		if (noNif) {
			$("#alertNoNif").show();
		} else {
			$("#alertNoNif").hide();
		}
		

		mostrarNotificacions(notificacionsSeleccionades);
		$('#entregaPostal').trigger('change');
	});


	$(".notificarSubmit").click(function(event) {

		event.stopImmediatePropagation();
		event.preventDefault();

		let continuar = true;


		const notificacioSenseNifMsg = "<spring:message code="notificacio.form.notificacio.sense.nif.confirm" />";

		let enviamentsSenseNif = getNotificacionsSenseNif();


		if (enviamentsSenseNif.length > 0) {
			continuar = false;
			let missatge = "<spring:message code="notificacio.form.notificacio.sense.nif.confirm" />";

			missatge += "<ul>";
			enviamentsSenseNif.forEach(function(notificacio, index) {
				missatge += "<li>Notificació " + (index + 1) + " - Titular : " + notificacio.titular.nom + " " + notificacio.titular.llinatge1 + "</li>";
			});
			missatge += "</ul>";

			bootbox.confirm({
				title: "Enviar?",
				size: "large",
				message: missatge,
				locale: 'ca',
				callback: function(result){
					if (result) {
						submitNotificacio();
					}
				}
			})
		}
		
		if (continuar) {
			submitNotificacio();
		}
	});

	
	$('#entregaPostal').on('change', function() {
		if ($(this).is(':checked')) {
			$('.entrega_postal').removeClass('hidden');
		} else {
			$('.entrega_postal').addClass('hidden');
		}
	})
	$('#interessatsIds').trigger('change');


	// Data caducitat
	$("#dataCaducitat").change( () => updateCaducitatDiesNaturals($("#dataCaducitat").val()));

	$("#caducitatDiesNaturals").keyup(function () {
		let val = $("#caducitatDiesNaturals").val();
		val = val > 99999999 ? 99999999 : val;
		$("#caducitatDiesNaturals").val(val);
		updateCaducitatAmbDies($("#caducitatDiesNaturals").val());
	});


	let procedimentSenseCodiSia = ${procedimentSenseCodiSia};
	if (procedimentSenseCodiSia) {
		$("#tipus option[value='']").remove();
		$("#tipus option[value='NOTIFICACIO']").remove();
	}


	<c:if test="${fn:length(interessats) == 1}">
		let interessatId = ${interessats[0].id};
		$('#interessatsIds').val(interessatId);
		$('#interessatsIds').trigger('change');
	</c:if>
	


	
});//################################################## document ready END ##############################################################


function submitNotificacio(){
	$("#notificacioForm").submit();
	$('.notificarSubmit', parent.document).parent().parent().find(".modal-body iframe").hide();
	$('.modal-body .datatable-dades-carregant', parent.document).css('padding-bottom', '0px');
	$('.modal-body .datatable-dades-carregant', parent.document).css('padding-top', '60px');
	$('.modal-body .datatable-dades-carregant', parent.document).show();
	$('.notificarSubmit', parent.document).attr('disabled', true);
	
}


function getNotificacionsSenseNif() {
	var interessatsSelected = $('#interessatsIds').val();
	var notificacions = eval(${notificacions});
	var notificacionsSenseNif = [];
	
	$(notificacions).each(function(index, notificacio) {
		if (notificacio.titular.personaFisica) {
			//if selected
			if (interessatsSelected != null && notificacio.titular != null && interessatsSelected.includes(notificacio.titular.id.toString())) {
	    		if ((notificacio.destinatari == null && notificacio.titular.documentTipus!='NIF' && notificacio.titular.documentTipus!='DOCUMENT_IDENTIFICATIU_ESTRANGERS')|| (notificacio.destinatari != null && notificacio.destinatari.documentTipus!='NIF' && notificacio.destinatari.documentTipus!='DOCUMENT_IDENTIFICATIU_ESTRANGERS')) {
	    			notificacionsSenseNif.push(notificacio);
				}
			}
		}
    });
    return notificacionsSenseNif;
}

function mostrarNotificacions(notificacions) {
	var notificacions_container = document.getElementById('container-envios');
	$(notificacions_container).empty();
	var notificacio_div = "";
	$(notificacions).each(function(index, notificacio) {
		var numCurrentNotificacio = index + 1;
		notificacio_div += '\
			<div class="row enviamentsForm formEnviament" style="margin-bottom: 30px"> \
				<div class="col-md-12"> \
					<label class="badge badge-light"><spring:message code="notificacio.form.label.notificacio"/> ' + numCurrentNotificacio + '</label> \
				</div> \
				<div class="titular"> \
				<div class="col-md-12 title-envios"> \
					<div class="title-container"> \
						<label><spring:message code="enviament.label.titular"/></label> \
					</div> \
					<hr/> \
				</div> \
				<div class="personaForm"> \
						<!----  TIPUS INTERESSAT ----> \
						<div class="col-md-6"> \
							<div class="form-group"> \
								<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.tipus"><spring:message code="interessat.form.camp.tipus"/></label> \
								<div class="controls col-xs-8"> \
									<select disabled="true" id="enviaments[#num_notificacio#].titular.tipus" name="enviaments[#num_notificacio#].titular.tipus" class="form-control interessat" style="width:100%"> \
										<option value="' + notificacio.titular.tipus + '">' + interessatTipusEnum[notificacio.titular.tipus] + '</option> \
									</select> \
								</div> \
							</div> \
						</div> \
						<!----  NUM. DOCUMENT ----> \
						<div class="col-md-6"> \
							<div class="form-group"> \
								<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.documentNum"><spring:message code="interessat.nifCifDni"/></label> \
								<div class="col-xs-8"> \
									<input disabled="true" id="enviaments[#num_notificacio#].titular.documentNum" name="enviaments[#num_notificacio#].titular.documentNum" class="form-control " type="text" value="' + (notificacio.titular.documentNum != null ? notificacio.titular.documentNum : "") +'"> \
								</div> \
							</div> \
						</div> \
						<!---- NOM / RAÓ SOCIAL ----> \
						<div class="col-md-6"> \
							<div class="form-group"> ' +
								getTipusInteressat(notificacio.titular) + 
							'</div> \
						</div>' + 
						checkIfPersonaFisica(notificacio.titular) +
						'<!---- EMAIL ----> \
						<div class="col-md-6"> \
							<div class="form-group"> \
								<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.email1"><spring:message code="interessat.form.camp.email"/></label> \
								<div class="col-xs-8"> \
									<input disabled="true" id="enviaments[#num_notificacio#].titular.email1" name="enviaments[#num_notificacio#].titular.email1" class="form-control " type="text" value="' + (notificacio.titular.email != null ? notificacio.titular.email : "") +'"> \
								</div> \
							</div> \
						</div> \
						<!---- TELÈFON ----> \
						<div class="col-md-6"> \
							<div class="form-group"> \
								<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.telefon"><spring:message code="interessat.form.camp.telefon"/></label> \
								<div class="col-xs-8"> \
									<input disabled="true" id="enviaments[#num_notificacio#].titular.telefon" name="enviaments[#num_notificacio#].titular.telefon" class="form-control " type="text" value="' + (notificacio.titular.telefon != null ? notificacio.titular.telefon : "") +'"> \
								</div> \
							</div> \
						</div>' +
						checkIfAdministracio(notificacio.titular) +
						checkIfPersonafisicaIncapacitat(notificacio) +
				'</div> \
			</div>' +
			setDestinatari(notificacio) +
			setEntregaPostal(notificacio) +
		'</div>';
			
		notificacio_div = replaceAll(notificacio_div, "#num_notificacio#", index);
	});
	$(notificacions_container).append(notificacio_div);
}

function setDestinatari(notificacio) {
	if (notificacio.destinatari != null) {
		return '<!---------------------------------------  DESTINATARI  ---------------------------------------> \
				<div class="destinatari"> \
					<div class="col-md-12 title-envios"> \
						<div class="title-container"> \
							<label><spring:message code="enviament.label.destinatari"/></label> \
						</div> \
						<hr/> \
					</div> \
					<div class="personaForm"> \
							<!----  TIPUS INTERESSAT ----> \
							<div class="col-md-6"> \
								<div class="form-group"> \
									<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].destinatari.tipus"><spring:message code="interessat.form.camp.tipus"/></label> \
									<div class="controls col-xs-8"> \
										<select disabled="true" id="enviaments[#num_notificacio#].destinatari.tipus" name="enviaments[#num_notificacio#].destinatari.tipus" class="form-control interessat" style="width:100%"> \
											<option value="' + notificacio.destinatari.tipus + '">' + interessatTipusEnum[notificacio.destinatari.tipus] + '</option> \
										</select> \
									</div> \
								</div> \
							</div> \
							<!----  NUM. DOCUMENT ----> \
							<div class="col-md-6"> \
								<div class="form-group"> \
									<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].destinatari.documentNum"><spring:message code="interessat.nifCifDni"/></label> \
									<div class="col-xs-8"> \
										<input disabled="true" id="enviaments[#num_notificacio#].destinatari.documentNum" name="enviaments[#num_notificacio#].destinatari.documentNum" class="form-control " type="text" value="' + (notificacio.destinatari.documentNum != null ? notificacio.destinatari.documentNum : "") +'"> \
									</div> \
								</div> \
							</div> \
							<!---- NOM / RAÓ SOCIAL ----> \
							<div class="col-md-6"> \
								<div class="form-group"> ' +
									getTipusInteressat(notificacio.destinatari) + 
								'</div> \
							</div>' + 
							checkIfPersonaFisica(notificacio.destinatari) +
							'<!---- EMAIL ----> \
							<div class="col-md-6"> \
								<div class="form-group"> \
									<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].destinatari.email1"><spring:message code="interessat.form.camp.email"/></label> \
									<div class="col-xs-8"> \
										<input disabled="true" id="enviaments[#num_notificacio#].destinatari.email1" name="enviaments[#num_notificacio#].destinatari.email1" class="form-control " type="text" value="' + (notificacio.destinatari.email != null ? notificacio.destinatari.email : "") +'"> \
									</div> \
								</div> \
							</div> \
							<!---- TELÈFON ----> \
							<div class="col-md-6"> \
								<div class="form-group"> \
									<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].destinatari.telefon"><spring:message code="interessat.form.camp.telefon"/></label> \
									<div class="col-xs-8"> \
										<input disabled="true" id="enviaments[#num_notificacio#].destinatari.telefon" name="enviaments[#num_notificacio#].destinatari.telefon" class="form-control " type="text" value="' + (notificacio.destinatari.telefon != null ? notificacio.destinatari.telefon : "") +'"> \
									</div> \
								</div> \
							</div>' +
							checkIfAdministracio(notificacio.destinatari) +
							checkIfPersonafisicaIncapacitat(notificacio) +
					'</div> \
				</div>';
	} else {
		return '';
	}
}

function setEntregaPostal(notificacio) {
	var paisNom, provinciaNom, municipiNom;
	var paisos = eval(${paisos});
	var provincies = eval(${provincies});

	$(paisos).each(function(index, pais) {
		//if selected
		if (pais.codi == notificacio.titular.pais) {
    		paisNom = pais.nom
		}
    });
	
	$(provincies).each(function(index, provincia) {
		//if selected
		if (provincia.codi == notificacio.titular.provincia) {
    		provinciaNom = provincia.nom
    		$.ajax({
				type: 'GET',
				url: "<c:url value="/expedient/municipis/"/>" + provincia.codi,
				async: false,
				success: function(municipis) {
					if (municipis && municipis.length > 0) {
						municipis.forEach(function(municipi) {
							if(municipi.codi == notificacio.titular.municipi) {
								municipiNom = municipi.nom
							}
						})
					}
				}
			});
		}
    });
	
	return '<!---------------------------------------  ENTREGA POSTAL  ---------------------------------------> \
			<div class="entrega_postal hidden"> \
				<div class="col-md-12 title-envios"> \
					<div class="title-container"> \
						<label><spring:message code="enviament.label.entregapostal"/></label> \
					</div> \
					<hr/> \
				</div> \
				<div class="personaForm"> \
					<!----  PAIS ----> \
					<div class="col-md-6"> \
						<div class="form-group"> \
							<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.paisNom"><spring:message code="notificacio.form.entregapostal.camp.pais"/></label> \
							<div class="controls col-xs-8"> \
								<input disabled="true" id="enviaments[#num_notificacio#].titular.paisNom" name="enviaments[#num_notificacio#].titular.paisNom" class="form-control " type="text" value="' + paisNom +'"> \
							</div> \
						</div> \
					</div> \
					<!----  PROVINCIA ----> \
					<div class="col-md-6"> \
						<div class="form-group"> \
							<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.provinciaNom"><spring:message code="notificacio.form.entregapostal.camp.provincia"/></label> \
							<div class="controls col-xs-8"> \
								<input disabled="true" id="enviaments[#num_notificacio#].titular.provinciaNom" name="enviaments[#num_notificacio#].titular.provinciaNom" class="form-control " type="text" value="' + provinciaNom +'"> \
							</div> \
						</div> \
					</div> \
					<!---- MUNICIPI ----> \
					<div class="col-md-6"> \
						<div class="form-group"> \
							<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.municipiNom"><spring:message code="notificacio.form.entregapostal.camp.municipi"/></label> \
							<div class="controls col-xs-8"> \
								<input disabled="true" id="enviaments[#num_notificacio#].titular.municipiNom" name="enviaments[#num_notificacio#].titular.municipiNom" class="form-control " type="text" value="' + municipiNom +'"> \
							</div> \
						</div> \
					</div> \
					<!---- POSTAL ----> \
					<div class="col-md-6"> \
						<div class="form-group"> \
							<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.codiPostal"><spring:message code="notificacio.form.entregapostal.camp.codipostal"/></label> \
							<div class="controls col-xs-8"> \
								<input disabled="true" id="enviaments[#num_notificacio#].titular.codiPostal" name="enviaments[#num_notificacio#].titular.codiPostal" class="form-control " type="text" value="' + (notificacio.titular.codiPostal != null ? notificacio.titular.codiPostal : "") +'"> \
							</div> \
						</div> \
					</div> \
					<!---- ADRECA ----> \
					<div class="col-md-12"> \
						<div class="form-group"> \
							<label class="control-label col-xs-2 " for="enviaments[#num_notificacio#].titular.adresa"><spring:message code="notificacio.form.entregapostal.camp.adresa"/></label> \
							<div class="controls col-xs-10"> \
								<textarea disabled="true" id="enviaments[#num_notificacio#].titular.adresa" name="enviaments[#num_notificacio#].titular.adresa" class="form-control ">' + (notificacio.titular.adresa != null ? notificacio.titular.adresa : "") +'</textarea> \
							</div> \
						</div> \
					</div> \
				</div> \
			</div>';
}

function getTipusInteressat(interessat) {
	switch (interessat.tipus) {
		case "PERSONA_FISICA":
			return '<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.nom"><spring:message code="interessat.nomRaoSocial"/></label> \
					<div class="col-xs-8"> \
						<input disabled="true" id="enviaments[#num_notificacio#].titular.documentNum" name="enviaments[#num_notificacio#].titular.nom" class="form-control " type="text" value="' + (interessat.nom != null ? interessat.nom : "") +'"> \
					</div>';
		case 'PERSONA_JURIDICA':
			return'<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.raoSocial"><spring:message code="interessat.nomRaoSocial"/></label> \
					<div class="col-xs-8"> \
						<input disabled="true" id="enviaments[#num_notificacio#].titular.raoSocial" name="enviaments[#num_notificacio#].titular.raoSocial" class="form-control " type="text" value="' + (interessat.raoSocial != null ? interessat.raoSocial : "") +'"> \
					</div>';
		case 'ADMINISTRACIO':
			return '<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.organNom"><spring:message code="interessat.nomRaoSocial"/></label> \
					<div class="col-xs-8"> \
						<input disabled="true" id="enviaments[#num_notificacio#].titular.organNom" name="enviaments[#num_notificacio#].titular.organNom" class="form-control " type="text" value="' + (interessat.organNom != null ? interessat.organNom : "") +'"> \
					</div>';
	}
}

function checkIfPersonaFisica (interessat) {
	if (interessat.tipus == "PERSONA_FISICA") {
			return '<!---- PRIMER LLINATGE ----> \
					<div class="col-md-6 llinatge1"> \
						<div class="form-group"> \
							<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.llinatge1"><spring:message code="interessat.form.camp.llinatge1"/></label> \
							<div class="col-xs-8"> \
								<input disabled="true" id="enviaments[#num_notificacio#].titular.llinatge1" name="enviaments[#num_notificacio#].titular.llinatge1" class="form-control " type="text" value="' + (interessat.llinatge1 != null ? interessat.llinatge1 : "") +'"> \
							</div> \
						</div> \
					</div> \
					<!---- SEGON LLINATGE ----> \
					<div class="col-md-6 llinatge2"> \
						<div class="form-group"> \
							<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.llinatge2"><spring:message code="interessat.form.camp.llinatge2"/></label> \
							<div class="col-xs-8"> \
								<input disabled="true" id="enviaments[#num_notificacio#].titular.llinatge2" name="enviaments[#num_notificacio#].titular.llinatge2" class="form-control " type="text" value="' + (interessat.llinatge2 != null ? interessat.llinatge2 : "") +'"> \
							</div> \
						</div> \
					</div>';
	} else {
		return '';
	}
}

function checkIfAdministracio(interessat) {
	if (interessat.tipus == "ADMINISTRACIO") {
		return '<!---- CODI DIR3 ----> \
				<div class="col-md-6"> \
					<div class="form-group"> \
						<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.organCodi"><spring:message code="interessat.dir3codi"/></label> \
						<div class="col-xs-8"> \
							<input disabled="true" id="enviaments[#num_notificacio#].titular.organCodi" name="enviaments[#num_notificacio#].titular.organCodi" class="form-control " type="text" value="' + (interessat.organCodi != null ? interessat.organCodi : "") +'"> \
						</div> \
					</div> \
				</div>';
	} else {
		return '';
	}
}

function checkIfPersonafisicaIncapacitat(notificacio) {
	if (notificacio.titular.tipus == "PERSONA_FISICA") {
		return '<!---- INCAPACITAT ----> \
				<div class="col-md-6"> \
					<div class="form-group"> \
						<label class="control-label col-xs-4 " for="enviaments[#num_notificacio#].titular.incapacitat"><spring:message code="interessat.form.camp.incapacitat"/></label> \
						<div class="controls col-xs-8"> \
							<div class="checkbox"> \
								<label> \
									<input disabled="true" type="checkbox" id="enviaments[#num_notificacio#].titular.incapacitat" name="enviaments[#num_notificacio#].titular.incapacitat" class="span12" type="text" value="' + (notificacio.titular.incapacitat != null ? notificacio.titular.incapacitat : "") +'"> \
								</label> \
							</div> \
						</div>' +
						checkIfIncapacitat(notificacio) +
					'</div> \
				</div>'
	} else {
		return '';
	}
}

function checkIfIncapacitat(notificacio) {
	if (notificacio.titular.incapacitat && (notificacio.destinatari == null || notificacio.destinatari == '')) {
		return '<div class="alert alert-danger"> \
					<spring:message code="interessat.form.camp.incapacitat.error.nodestinatari"/> \
				</div>'
	} else {
		return '';
	}
}

function replaceAll(string, search, replace) {
	return string.split(search).join(replace);
}



function destinitarisNoResults() {
	return '<spring:message code="notificacio.form.camp.destinatari.buit"/>';
}


function updateCaducitatDiesNaturals(data) {
	if (data) {
		var campsData = data.split("/");
		$.ajax({
			type: 'GET',
			url: "<c:url value="/document/notificacio/caducitatDiesNaturals/"/>" + campsData[0] + "/" + campsData[1] + "/" + campsData[2],
			success: function (data) {
				$("#caducitatDiesNaturals").val(data);
			},
			error: function (error) {
				console.log("error obtenint els dies de caducitat a partir de la data: " + error);
			}
		});
	} else {
		$("#caducitatDiesNaturals").val('');
	}
}

function updateCaducitatAmbDies(dies) {
	if (dies) {
		$.ajax({
			type: 'GET',
			url: "<c:url value="/document/notificacio/caducitatData/"/>" + dies,
			success: function (data) {
				$("#dataCaducitat").val(data);
			},
			error: function (error) {
				console.log("error obtenint la data de caducitat a partir dels dies:" + error);
			}
		});
	} else {
		$("#dataCaducitat").val('');
	}

}

</script>
</head>
<body>
	<c:choose>
		<c:when test="${empty documentNotificacionsCommand.id}"><c:set var="formAction"><rip:modalUrl value="/document/${documentNotificacionsCommand.documentId}/notificar"/></c:set></c:when>
		<c:otherwise><c:set var="formAction"><rip:modalUrl value="/expedient/${expedientId}/notificacio/${documentNotificacionsCommand.id}"/></c:set></c:otherwise>
	</c:choose>
	<form:form id="notificacioForm" action="${formAction}" method="post" cssClass="form-horizontal" commandName="documentNotificacionsCommand" role="form">
		<rip:inputHidden name="id"/>
		<rip:inputHidden name="documentId"/>
		<div id="alertComunicacioSir" style="display: none;" class="alert well-sm alert-warning alert-dismissable"><span class="fa fa-exclamation-triangle"></span> <spring:message code="notificacio.form.camp.comunicacio.sir.alert"/></div>
		<!---  TIPUS (NOTIFICACIO / COMUNICACIO) ---->
		<c:choose>
			<c:when test="${empty documentNotificacionsCommand.id}">
				<rip:inputSelect labelSize="2" name="tipus" textKey="notificacio.form.camp.tipus" optionItems="${notificacioTipusEnumOptions}" emptyOption="true" emptyOptionTextKey="comu.boto.cap" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
			</c:when>
			<c:otherwise>
				<rip:inputHidden name="tipus"/>
			</c:otherwise>
		</c:choose>
		<!----  ESTAT   ------->
		<rip:inputSelect disabled="true" labelSize="2" name="estat" textKey="notificacio.form.camp.estat" optionItems="${notificacioEstatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
		<!----  TITULARS   ------->
		<rip:inputSelect required="true" labelSize="2" name="interessatsIds" multiple="true" textKey="notificacio.form.camp.destinatari" optionItems="${interessats}" optionValueAttribute="id" optionTextAttribute="identificador" placeholderKey="notificacio.form.camp.destinatari" noResultsFunction="destinitarisNoResults"/>
		<div id="alertNoNif" style="display: none;" class="alert well-sm alert-warning alert-dismissable"><span class="fa fa-exclamation-triangle"></span> <spring:message code="notificacio.form.camp.interessat.no.nif.alert"/></div>
		<!---  CONCEPTE   ---->
		<rip:inputText labelSize="2" name="assumpte" textKey="notificacio.form.camp.concepte" required="true"/>
		<!---- TIPUS DE SERVEI  ------->	
		<rip:inputSelect labelSize="2" required="true" name="serveiTipusEnum" optionItems="${serveiTipusEstats}" optionValueAttribute="value" optionTextKeyAttribute="text" textKey="notificacio.form.camp.serveiTipus" />
		<!---  OBSERVACIONS   --->
		<rip:inputTextarea labelSize="2" name="observacions" textKey="notificacio.form.camp.descripcio"/>
		<!----  DATA PROGRAMADA   ----->
		<rip:inputDate labelSize="2" name="dataProgramada" textKey="notificacio.form.camp.data.programada" comment="notificacio.form.camp.data.programada.comment"/>
		<!----  DATA CADUCITAT  ------->

		<c:set var="campErrors"><form:errors path="dataCaducitat"/></c:set>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-2" for="dataCaducitat"><spring:message code="notificacio.form.camp.data.caducitat"/> *</label>
			<div class="col-xs-2">
				<form:input path="caducitatDiesNaturals" cssClass="form-control" id="caducitatDiesNaturals" />
			</div>
			<div class="col-xs-8">
				<div class="input-group" style="width:100%">
					<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
					<form:input path="dataCaducitat" cssClass="form-control datepicker" id="dataCaducitat" data-toggle="datepicker" data-idioma="${idioma}" />
					<span class="input-group-addon" style="width:1%"><span class="fa fa-calendar"></span></span>
				</div>
			</div>
			<c:if test="${not empty campErrors}">
				<div class="col-xs-2"></div>
				<p class="help-block col-xs-10" style="padding-left: 30px"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="dataCaducitat"/></p>
			</c:if>			
			<div class="col-xs-2"></div>
			<p class="comentari col-xs-10" style="padding-left: 30px"><spring:message code="notificacio.form.camp.data.caducitat.dies.naturals.comment"/></p>
			<div class="col-xs-2"></div>
			<p class="comentari col-xs-10" style="padding-left: 30px"><spring:message code="notificacio.form.camp.data.caducitat.comment"/></p>
		</div>
		
		<!---  RETARD  ------->
		<rip:inputNumber labelSize="2" name="retard" textKey="notificacio.form.camp.retard" nombreDecimals="0" comment="notificacio.form.camp.retard.comment"/>
		<c:if test="${entregaPostal}">
			<rip:inputCheckbox labelSize="2" name="entregaPostal" textKey="notificacio.form.camp.entregaPostal"/>
		</c:if>
		<!--------------------------------------------------------  ENVIAMENTS  ------------------------------------------------------------>
		<div class="container-fluid">
			<div class="title">
				<span class="fa fa-vcard"></span> <label><spring:message code="notificacio.form.camp.enviaments" /></label>
				<hr />
			</div>
			<div id="container-envios"></div>
		</div>
		<c:choose>
			<c:when test="${empty document}"><c:set var="urlTancar"><c:url value="/contingut/${expedientId}"/></c:set></c:when>
			<c:otherwise><c:set var="urlTancar"><c:url value="/contingut/${document.id}"/></c:set></c:otherwise>
		</c:choose>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-success notificarSubmit"><span class="fa fa-floppy-o"></span> <spring:message code="comu.boto.notificar"/></button>
			<a href="${urlTancar}" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
