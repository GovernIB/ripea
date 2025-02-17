<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="charSearch" value='"' />
<c:set var="charReplace" value='\\"' />

<%
pageContext.setAttribute(
		"metadocumentSeqtipEnumOptions",
		es.caib.ripea.back.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto.class,
		"metadocument.seqtip.enum."));
%>
<c:set var="titol"><spring:message code="contenidor.document.portafirmes.titol"/></c:set>
<c:set var="isTasca" value="${not empty tascaId}"/>
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
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<rip:modalHead/>
<style type="text/css">
.rmodal {
    display:    none;
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
.rmodal_carrecs {
    display:    none;
    position:   absolute;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
body.loading {
    overflow: hidden;   
}
body.loading .rmodal {
    display: block;
}
.modal-lg {
	width: 100%;
}
.modal-dialog {
	width: 95%;
	height: 100%;
	margin: 5% auto;
	padding: 0;
}

.modal-content {
	height: auto;
}

.iframe_container {
	position: relative;
	width: 100%;
	height: 97vh;
	padding-bottom: 0;
}

.iframe_content {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
}
#fluxModal {
	margin: 1%;
}
.portafirmesEnviarFluxId_btn:hover {
	cursor: pointer;
}
.btn-flux {
	color: #fff; 
	background-color: #bfbfbf;
	border-color: #bfbfbf;
}

.btn-flux:hover {
	color: #fff; 
}
.disabled {
	pointer-events: none;
	cursor: not-allowed;
	opacity: .65;
	box-shadow: none;
}
.flux_container {
	margin-top: 7%;
}
.espai {
	height: 20px;
}
.carrec-selected {
	font-weight: bold;
	background-color: #1a3d5c;
	border-radius: 2px;
}
div[class^="carrec_"] > a {
	color: black;
}
.carrec-selected > a {
	color: #FFF !important;
}
div[class^="carrec_"] {
	padding: 1%;
	margin: 2px;
}
div[class^="carrec_"]:hover{
	background-color: #4b80af;
}
div[class^="carrec_"]:hover {
	background-color: #1a3d5c;;
}
div[class^="carrec_"]:hover a{
	color: #FFF;
}
div[class^="carrec_"] > a:hover {
	text-decoration: none;
	cursor: pointer;
}
div.dropdown-menu {
	left: auto;
	right: 0;
	padding: 1%;
	width: 70%;
}
div.dropdown-menu.loading {
    overflow: hidden;   
    height: 100px;
}
div.dropdown-menu.loading .rmodal_carrecs {
    display: block;
}
a.btn.input-group-addon.portafirmesResponsables_btn {
	background-color: #fff;
}
a.btn.input-group-addon.portafirmesResponsables_btn2 {
	background-color: #fff;
}

#nifResponsable:focus {
    border: 1px solid #66afe9 !important;
}

#nifResponsable {
	width: 89%; 
	padding: 6px 12px; 
	color: #555; 
	border: 1px solid #ccc; 
	border-radius: 4px; 
	float: left;
	outline: none;
	border-top-right-radius: 0;
    border-bottom-right-radius: 0;
}

#nifResponsableBtn {
	border-top-left-radius: 0;
    border-bottom-left-radius: 0;
    outline: none;
}

.select2-container--bootstrap .select2-results__group {
    font-size: 1.5rem;
    background: #dadada;
}
</style>

<script type="text/javascript">
$(document).ready(function() {
	var parentIframe = window.frameElement;
	var idModal = $(parentIframe.closest("[id^='modal_']")).attr('id');
	var currentHeight = window.frameElement.contentWindow.document.body.scrollHeight;
	localStorage.setItem("currentIframeHeight", currentHeight);


	if (typeof window.parent.removeLoading === "function") { 
		window.parent.removeLoading(idModal); // todo: check what removeLoading(), addLoading() do and if it is necessary
		$('form').on('submit', function(){
			window.parent.addLoading(idModal);
		});
	}

	// Tancar transacció i esborrar localstorage
	window.parent.removeTransactionId(idModal);
	
	//crear nou flux
	$(".portafirmesEnviarFluxId_btn_edicio").on('click', function() {
		crearFlux(false);
	});	
	
	//mostrar flux actual
	$(".portafirmesEnviarFluxId_btn_addicional").on('click', function() {
		$(this).find('i').toggleClass('fa-eye fa-eye-slash');
		if ($(this).find('i').hasClass('fa-eye-slash')) {
			var portafirmesEnviarFluxId = $("#portafirmesEnviarFluxId").val();
			$(".portafirmesEnviarFluxId_btn_addicional").attr("title", "<spring:message code="contenidor.document.portafirmes.boto.hide"/>");
			recuperarFluxSeleccionat(portafirmesEnviarFluxId);
		} else {
			$(".portafirmesEnviarFluxId_btn_addicional").attr("title", "<spring:message code="contenidor.document.portafirmes.boto.show"/>");
			amagarFluxSeleccionat(portafirmesEnviarFluxId);
		}
	});	
					
	$.ajax({
		type: 'GET',
		dataType: "json",
		async: false,
		url: "<c:url value="/document/${document.id}/portafirmes/flux/plantilles"/>",
		success: function(data) {
			var defaultPortafirmesFluxId = "${portafirmesFluxId}";
			var plantillaActual = "${portafirmesFluxSeleccionat}";
			var selPlantilles = $("#portafirmesEnviarFluxId");
			selPlantilles.empty();
			selPlantilles.append("<option value=\"\"></option>");
			if (data) {
				var items = [];
				var itemsUsuari = [];
				
				selPlantilles.append("<optgroup label='<spring:message code='metadocument.form.camp.portafirmes.flux.group.comun'/>'>");
				$.each(data, function(i, val) {
					if (val.usuariActual) {
						itemsUsuari.push({
							"id": val.fluxId,
							"text": val.nom
						});
					}
					if (!val.usuariActual) {
						if (defaultPortafirmesFluxId != '' && defaultPortafirmesFluxId === val.fluxId) {
							selPlantilles.append("<option selected value=\"" + val.fluxId + "\">" + val.nom + "</option>");
						} else {
							selPlantilles.append("<option value=\"" + val.fluxId + "\">" + val.nom + "</option>");
						}
					}
				});
				selPlantilles.append("</optgroup>");
				
				if (itemsUsuari.length > 0) {
					selPlantilles.append("<optgroup label='<spring:message code='metadocument.form.camp.portafirmes.flux.group.usuari'/>'>");
					$.each(itemsUsuari, function(i, val) {
						if (defaultPortafirmesFluxId != '' && defaultPortafirmesFluxId === val.id) {
							selPlantilles.append("<option selected value=\"" + val.id + "\">" + val.text + "</option>");
						} else {
							selPlantilles.append("<option value=\"" + val.id + "\">" + val.text + "</option>");
						}
					});
					selPlantilles.append("</optgroup>");
				}
			
			}
			var select2Options = {
					theme: 'bootstrap',
					minimumResultsForSearch: "4"
				};
			selPlantilles.select2(select2Options);
			if (plantillaActual != '') {
				selPlantilles.val(plantillaActual);
				selPlantilles.change();
				$(".portafirmesEnviarFluxId_btn_edicio").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.flux.editar"/>");
			}
		},
		error: function (error) {
			var selPlantilles = $("#portafirmesEnviarFluxId");
			selPlantilles.empty();
			selPlantilles.append("<option value=\"\"></option>");
			var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
			selPlantilles.select2(select2Options);
		}
	});
	$("#portafirmesEnviarFluxId").on('change', function () {
		var portafirmesEnviarFluxId = $(this).val();
		if(portafirmesEnviarFluxId != null && portafirmesEnviarFluxId != '') {
			recuperarFluxSeleccionat(portafirmesEnviarFluxId);
			$(".portafirmesEnviarFluxId_btn_addicional").removeClass('disabled');
			$(".portafirmesEnviarFluxId_btn_addicional").attr("title", "<spring:message code="contenidor.document.portafirmes.boto.hide"/>");
		} else {
			$(".portafirmesEnviarFluxId_btn_addicional").addClass('disabled');
			$(".portafirmesEnviarFluxId_btn_edicio").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>");
		}
	});
	
	$("#portafirmesEnviarFluxId").trigger('change');
	$(".portafirmesResponsables_btn").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.carrecs"/>");
	$(".portafirmesResponsables_btn2").attr("title", "<spring:message code="contingut.portafirmes.afegir.reponsable.manual"/>");
	
	$("#portafirmesResponsables").on('select2:unselecting', function (e) {
		var optionRemoved = e.params.args.data.id;
		$("#portafirmesResponsables option[value='" + optionRemoved + "']").remove();
	});


	$(".portafirmesResponsables_btn2").on('click', function() {
		toggleDropdownResponsable();
	});	
		
	
});

function crearFlux(isPlantilla) {
	let documentNom = "${fn:replace(document.nom, charSearch, charReplace)}";

	$.ajax({
		type: 'GET',
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		data: {
			nom: documentNom,
		    isPlantilla: isPlantilla
		},
		url: "<c:url value="/modal/document/portafirmes/iniciarTransaccio"/>",
		success: function(transaccioResponse, textStatus, XmlHttpRequest) {
			if (transaccioResponse != null && !transaccioResponse.error) {
				localStorage.setItem('tmpTransaccioId', transaccioResponse.idTransaccio);
				$('.content').addClass("hidden");
				var fluxIframe = '<div class="iframe_container">' +
									'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe>' +
								  '</div>';
				$('.flux_container').html(fluxIframe);	
				adjustModalPerFlux(true);
				$body = $("body");
				$body.addClass("loading");
			} else if (transaccioResponse != null && transaccioResponse.error) {
				let currentIframe = window.frameElement;
				var alertDiv = '<div class="alert alert-danger" role="alert">' +
									'<a class="close" data-dismiss="alert">×</a><span>' + transaccioResponse.errorDescripcio + '</span>' +
								'</div>';
				$('form').prev().find('.alert').remove();
				$('form').prev().prepend(alertDiv);
				webutilModalAdjustHeight();
			}
		},
		error: function(error) {
			if (error != null && error.responseText != null) {
				let currentIframe = window.frameElement;
				var alertDiv = '<div class="alert alert-danger" role="alert">' + 
									'<a class="close" data-dismiss="alert">×</a><span>' + error.responseText + '</span>' + 
								'</div>';
				$('form').prev().find('.alert').remove();
				$('form').prev().prepend(alertDiv);
				webutilModalAdjustHeight();
			}
		}
	});
}

function crearModalConfirmacio() {
	return '<div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="confirmModalLabel" aria-hidden="true">\
				<div class="modal-dialog" role="document">\
				<div class="modal-content">\
				<div class="modal-header">\
					<h5 class="modal-title" id="confirmModalLabel"><spring:message code="metadocument.form.camp.portafirmes.flux.plantilla.confirmacio"/></h5>\
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">\
							<span aria-hidden="true">&times;</span>\
						</button>\
				</div>\
				<div class="modal-body">\
					<spring:message code="metadocument.form.camp.portafirmes.flux.plantilla.confirmacio.text"/>\
				</div>\
				<div class="modal-footer">\
					<button type="button" class="btn btn-secondary" id="confirmNoButton">No</button>\
					<button type="button" class="btn btn-secondary" id="confirmYesButton">Sí</button>\
				</div>\
			</div>\
		</div>\
	</div>';	
}

function toggleCarrecs() {
	var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu.btn1');
	if (dropdown.length === 0) {
		$(".portafirmesResponsables_btn").parent().append(recuperarCarrecs());
		$(".portafirmesResponsables_btn").parent().find('.dropdown-menu.btn1').show();
		$(".portafirmesResponsables_btn").css("background-color", "#ddd");
		$(".portafirmesResponsables_btn").addClass("active");
		
	} else {
		if (dropdown.is(":visible")) {
			dropdown.hide();
			$(".portafirmesResponsables_btn").css("background-color", "#fff");
			$(".portafirmesResponsables_btn").removeClass("active");
		} else {
			dropdown.show();
			$(".portafirmesResponsables_btn").css("background-color", "#ddd");
			$(".portafirmesResponsables_btn").addClass("active");
		}		
	}

}

function recuperarCarrecs() {
	var llistatCarrecs = "<div class='loading dropdown-menu btn1'>";
	$.ajax({
		type: 'GET',
		dataType: "json",
		url: "<c:url value="/metaExpedient/metaDocument/carrecs"/>",
		success: function(carrecs) {
			var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu.btn1');
			dropdown.removeClass('loading');
			if (carrecs) {
				llistatCarrecs += '<div class="carrecsList">';
				$.each(carrecs, function(i, carrec) {
					var persona = '';
					if (carrec.usuariPersonaNom) {
						persona = ' (' + carrec.usuariPersonaNom + ' - ' + carrec.usuariPersonaNif + ' - ' + carrec.usuariPersonaId + ')';
					}
					var nomCarrec = carrec.carrecName + persona;
					llistatCarrecs += "<div class='carrec_" + carrec.carrecId + "'><a onclick='seleccionarCarrec(" + JSON.stringify(carrec) + ")'>" + nomCarrec + "</a></div>";	
					
					$('#portafirmesResponsables option').each(function(i, responsable) {
						if (responsable.value === carrec.carrecId) {
							llistatCarrecs = llistatCarrecs.replace('carrec_' + carrec.carrecId, 'carrec_' + carrec.carrecId + ' carrec-selected');
						}
					});
				});
			}
			dropdown.append(llistatCarrecs);
		},
		error: function (error) {
			var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu.btn1');
			dropdown.removeClass('loading');
			dropdown.empty();
			dropdown.append("Hi ha hagut un problema recuperant els càrrecs " + error.statusText);
		},
		statusCode: {
	        500: function(error) {
	        	var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu.btn1');
				dropdown.removeClass('loading');
	        	dropdown.empty();
				dropdown.append("Hi ha hagut un problema recuperant els càrrecs: " + error.statusText);
	        }
	   	}
	});
	llistatCarrecs += "<div class='rmodal_carrecs'></div></div>";
	return llistatCarrecs;
}

function seleccionarCarrec(carrec) {
	if ($('.carrec_' + carrec.carrecId).hasClass('carrec-selected')) {
		$("#portafirmesResponsables option[value='" + carrec.carrecId + "']").remove();
		$('.carrec_' + carrec.carrecId).removeClass('carrec-selected');
	} else {

		var persona = '';
		if (carrec.usuariPersonaNif) {
			persona = ' (' + carrec.usuariPersonaNif + ')';
		}
		var nomCarrec = carrec.carrecName + persona;
		var items = [];
		items.push({
			"id": "CARREC[" + carrec.carrecId + "]",
			"text": nomCarrec
		});
	    var newOption = new Option(items[0].text, items[0].id, true, true);
	    $("#portafirmesResponsables").append(newOption).trigger('change');
		$('.carrec_' + carrec.carrecId).addClass('carrec-selected');
	}
}

function mostrarFluxSeleccionat(urlPlantilla) {
	adjustModalPerFlux(false);
	var plantilla = '<hr>' + 
					'<div class="iframe_container">' +
						'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + urlPlantilla + '"></iframe>' +
					'</div>';
	$('.flux_container').html(plantilla);	
}

function amagarFluxSeleccionat() {
	$('.flux_container').empty();
	let $iframe = $(window.frameElement);
	$iframe.removeAttr('style').css('height', "375px");
	$iframe.parent().removeAttr('style').css('height', "375px");
	$iframe.find('body').removeAttr('style').css('height', "375px");
}

function adjustModalWithoutFlux(currentHeight) {
	let $iframe = $(window.frameElement);
	$iframe.removeAttr('style').css('height', 'auto');
	$iframe.parent().removeAttr('style').css('height', currentHeight);
}

function adjustModalPerFlux(amagar) {
	let $iframe = $(window.frameElement);
	$iframe.css('height', '100%');
	$iframe.css('min-height', amagar ? '370px' : '100%');
	$iframe.find('body').css('min-height', amagar ? '370px' : '100%');
	$iframe.parent().css('height', '600px');
	$iframe.closest('div.modal-content').css('height',  'auto');
	$iframe.closest('div.modal-dialog').css({
		//'height':'auto',
		'height': '100%',
		'margin': '3% auto',
		'padding': '0'
	});
	if (amagar) {
		$iframe.closest('div.modal-lg').css('width', '95%');
		$iframe.parent().next().addClass('hidden');
	}
}	

function removeLoading() {
	$body = $("body");
	$body.removeClass("loading");
}

function recuperarFluxSeleccionat(portafirmesEnviarFluxId) {
	if (portafirmesEnviarFluxId != null && portafirmesEnviarFluxId != '') {
		$.ajax({
			type: 'GET',
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			data: {plantillaId: portafirmesEnviarFluxId},
			url: "<c:url value="/modal/document/portafirmes/flux/mostrar"/>",
			success: function(transaccioResponse, textStatus, XmlHttpRequest) {
				if (transaccioResponse != null && !transaccioResponse.error) {
					mostrarFluxSeleccionat(transaccioResponse.urlRedireccio);
				} else if (transaccioResponse != null && transaccioResponse.error) {
					let currentIframe = window.frameElement;
					var alertDiv = '<div class="alert alert-danger" role="alert">' + 
										'<a class="close" data-dismiss="alert">×</a><span>' + transaccioResponse.errorDescripcio + '</span>' + 
									'</div>';
					$('form').prev().find('.alert').remove();
					$('form').prev().prepend(alertDiv);
					webutilModalAdjustHeight();
				}
				$body = $("body");
				$body.addClass("loading");
			},
			error: function(error) {
				if (error != null && error.responseText != null) {
					let currentIframe = window.frameElement;
					var alertDiv = '<div class="alert alert-danger" role="alert">' + 
										'<a class="close" data-dismiss="alert">×</a><span>' + error.responseText + '</span>' + 
									'</div>';
					$('form').prev().find('.alert').remove();
					$('form').prev().prepend(alertDiv);
					webutilModalAdjustHeight();
				}
			}
		});
	} else {
		alert("No s'ha seleccionat cap flux");
	}
}



function toggleDropdownResponsable() {
	
	var dropdown = $(".portafirmesResponsables_btn2").parent().find('.dropdown-menu.btn2');
	if (dropdown.length === 0) {
		$(".portafirmesResponsables_btn2").parent().append('' + 
				'<div class="dropdown-menu btn2" style="display: none;">' + 
					'<input onkeydown="preventSubmitOnEnter(event)" id="nifResponsable" type="text" placeholder="<spring:message code="contingut.portafirmes.introdueix.reponsable.nif"/>">' +
					'<button id="nifResponsableBtn" onclick="addResponable(); this.blur();" type="button" class="btn btn-default"><span class="fa fa-check"></span></button>' + 
				'</div>');
		$(".portafirmesResponsables_btn2").parent().find('.dropdown-menu').show();
		$(".portafirmesResponsables_btn2").css("background-color", "#ddd");
		$(".portafirmesResponsables_btn2").addClass("active");

	} else {
		if (dropdown.is(":visible")) {
			$('#nifResponsable').parent().find('.help-block').remove();
			$("#nifResponsable").val("");
			dropdown.hide();
			$(".portafirmesResponsables_btn2").css("background-color", "#fff");
			$(".portafirmesResponsables_btn2").removeClass("active");
		} else {
			dropdown.show();
			$(".portafirmesResponsables_btn2").css("background-color", "#ddd");
			$(".portafirmesResponsables_btn2").addClass("active");
		}


	}
}

function addResponable() {

	let value = $('#nifResponsable').val();

	let ok = isDniNie(value);

	if (ok) {
		$('#nifResponsable').parent().find('.help-block').remove();
	    var newOption = new Option(value, value, true, true);
	    $("#portafirmesResponsables").append(newOption).trigger('change');
	    $("#nifResponsable").val("");
	} else {
		$('#nifResponsable').parent().find('.help-block').remove();
		$('#nifResponsable').parent().append('<p class="help-block" style="color: #a94442;"><span class="fa fa-exclamation-triangle"></span>&nbsp;<span><spring:message code="contingut.portafirmes.introdueix.reponsable.nif.novalid"/></span></p>');
	}

}

function preventSubmitOnEnter(event) {

    if(event.keyCode == 13) {
        event.preventDefault();
        return false;
      }

}



function isDniNie(value) {

	 value = value.toUpperCase();

	 // Basic format test 
	 if ( !value.match('((^[A-Z]{1}[0-9]{7}[A-Z0-9]{1}$|^[T]{1}[A-Z0-9]{8}$)|^[0-9]{8}[A-Z]{1}$)') ) {
	  return false;
	 }

	 // Test NIF
	 if ( /^[0-9]{8}[A-Z]{1}$/.test( value ) ) {
	  return ( "TRWAGMYFPDXBNJZSQVHLCKE".charAt( value.substring( 8, 0 ) % 23 ) === value.charAt( 8 ) );
	 }
	 // Test specials NIF (starts with K, L or M)
	 if ( /^[KLM]{1}/.test( value ) ) {
	  return ( value[ 8 ] === String.fromCharCode( 64 ) );
	 }

	 // Test NIE
	 //T
	 if ( /^[T]{1}/.test( value ) ) {
	  return ( value[ 8 ] === /^[T]{1}[A-Z0-9]{8}$/.test( value ) );
	 }

	 //XYZ
	 if ( /^[XYZ]{1}/.test( value ) ) {
	  return ( 
	   value[ 8 ] === "TRWAGMYFPDXBNJZSQVHLCKE".charAt( 
	    value.replace( 'X', '0' )
	     .replace( 'Y', '1' )
	     .replace( 'Z', '2' )
	     .substring( 0, 8 ) % 23 
	   ) 
	  );
	 }

	 return false;
}





</script>
</head>
<body>
	<c:if test="${document.fitxerNom != document.fitxerNomEnviamentPortafirmes}">
		<div class="alert well-sm alert-warning alert-dismissable">
			<span class="fa fa-exclamation-triangle"></span>
			<spring:message code="contenidor.document.portafirmes.conversio.avis"/>
			<a data-rdt-link-modal="true" class="btn btn-xs btn-default pull-right" href="../pdf">
				<spring:message code="contenidor.document.portafirmes.conversio.boto.previsualitzar"/>
			</a>
		</div>
	</c:if>

	<c:set var="formAction"><rip:modalUrl value="/document/${document.id}/portafirmes/upload?tascaId=${tascaId}"/></c:set>

	<form:form action="${formAction}" method="post" cssClass="form-horizontal content" modelAttribute="portafirmesEnviarCommand" role="form">
		<rip:inputText name="motiu" textKey="contenidor.document.portafirmes.camp.motiu" required="true"/>
		<rip:inputSelect name="prioritat" textKey="contenidor.document.portafirmes.camp.prioritat" optionEnum="PortafirmesPrioritatEnumDto" required="true"/>
		<%--<rip:inputDate name="dataCaducitat" textKey="contenidor.document.portafirmes.camp.data.caducitat" required="true"/> --%>
		<c:if test="${isHabilitarAvisFirmaParcialActiu}">
			<rip:inputCheckbox name="avisFirmaParcial" textKey="contenidor.document.portafirmes.camp.avis"/>
		</c:if>
		<form:hidden name="portafirmesFluxTipus" path="portafirmesFluxTipus"/>
		<c:choose>
		<c:when test="${fluxTipus == 'SIMPLE'}">
			<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
			<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
			<rip:inputSuggest 
				name="portafirmesResponsables" 
				urlConsultaInicial="${urlConsultaInicial}" 
				urlConsultaLlistat="${urlConsultaLlistat}" 
				textKey="metadocument.form.camp.portafirmes.responsables"
				suggestValue="nif"
				suggestText="nom"
				suggestTextAddicional="nif"
				required="true"
				icon="fa fa-star"
				icon2="fa fa-plus"/>
			<rip:inputSelect name="portafirmesSequenciaTipus" textKey="metadocument.form.camp.portafirmes.seqtip" optionItems="${metadocumentSeqtipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		</c:when>
		<c:when test="${fluxTipus == 'PORTAFIB'}">
			<rip:inputSelect name="annexos" textKey="metadocument.form.camp.portafirmes.annexos" optionValueAttribute="id" optionTextAttribute="nom" optionItems="${annexos}" multiple="true"/>
			<label class="control-label success-label hidden col-xs-4"></label>
			<rip:inputSelect name="portafirmesEnviarFluxId" textKey="metadocument.form.camp.portafirmes.flux" emptyOption="true" botons="true" icon="fa fa-external-link" iconAddicional="fa fa-eye-slash"/>
			<c:if test="${!nouFluxDeFirma}">
				<label class="control-label col-xs-4"></label>
				<p class="comentari col-xs-8"><spring:message code="metadocument.form.camp.portafirmes.flux.comment" /></p>
			</c:if>
			<%--
			<div class="form-group">
				<label class="control-label col-xs-4 fluxInputLabel"><spring:message code="metadocument.form.camp.portafirmes.flux" /> *</label>
				<c:if test="${!nouFluxDeFirma}">
					<p class="comentari col-xs-8"><spring:message code="metadocument.form.camp.portafirmes.flux.comment" /> <a class="btn btn-default btn-xs exemple_boto"  onclick="mostrarFlux('${urlPlantilla}')"><span id="eye" class="fa fa-eye" title="<spring:message code="contenidor.document.portafirmes.boto.show" />"></span></a></p>
				</c:if>
				<div class="col-xs-8" style="margin-top: 3px">
					<span class='btn btn-flux form-control portafirmesFlux_btn' title="<spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>"><spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>  <i class="fa fa-external-link"></i></span>
				</div>
			</div>
			--%>
		</c:when>
		</c:choose>
		
		<c:if test="${isFirmaParcialHabilitada}">
			<div>
				<rip:inputCheckbox name="firmaParcial" textKey="contenidor.document.portafirmes.camp.parcial" comment="contenidor.document.portafirmes.camp.parcial.comentari"/>
			</div>
		</c:if>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><spring:message code="contenidor.document.portafirmes.enviar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
		
	<div class="espai"></div>
	</form:form>
	<div class="flux_container"></div>
	<div class="rmodal"></div>
</body>
</html>
