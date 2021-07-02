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
		"idioma",
		org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage());
pageContext.setAttribute(
		"multiplicitatEnumOptions",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.MultiplicitatEnumDto.class,
		"multiplicitat.enum."));
pageContext.setAttribute(
		"metadocumentSequenciatipEnumOptions",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto.class,
		"metadocument.seqtip.enum."));
pageContext.setAttribute(
		"metadocumentFluxtipEnumOptions",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
		es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto.class,
		"metadocument.fluxtip.enum."));
%>

<c:choose>
	<c:when test="${empty metaDocumentCommand.id}"><c:set var="titol"><spring:message code="metadocument.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="metadocument.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/jquery-ui/1.12.1/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${idioma}.js"/>"></script>
	<link href="<c:url value="/css/jasny-bootstrap.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jasny-bootstrap.min.js"/>"></script>
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

.ui-dialog {
	z-index: 1000;
}
.modal-dialog {
	width: 100%;
	height: 100%;
	margin: 0;
	padding: 0;
}

.modal-content {
	height: auto;
	min-height: 100%;
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
.portafirmesFluxId_btn_edicio:hover {
	cursor: pointer;
}
.flux_disabled {
	pointer-events: none;
	cursor: not-allowed;
}
.flux_disabled:hover {
	cursor: not-allowed;
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
</style>	
<script type="text/javascript">
	$(document).ready(function() {
		if (window.frameElement != null) {
			let currentHeight = window.frameElement.contentWindow.document.body.scrollHeight;
			localStorage.setItem("currentIframeHeight", currentHeight);
		}
		$("#biometricaCallbackActiu").on('change', function(){
			if($(this).prop("checked") == true){
				$(".callback").removeClass("hidden");
			} else if($(this).prop("checked") == false){ 
				$(".callback").addClass("hidden");
			}
		});
        if($("#firmaPortafirmesActiva").prop("checked") == true){
        	$("label[for='portafirmesDocumentTipus']").append( " *" );
        	$($("label[for='portafirmesResponsables']")[1]).append( " *" );
        }

		$("#firmaPortafirmesActiva").on('change', function(){
	            if($(this).prop("checked") == true){
	            	$("label[for='portafirmesDocumentTipus']").append( " *" );
	            	$($("label[for='portafirmesResponsables']")[1]).append( " *" );
	            }
	            else if($(this).prop("checked") == false){
	            	$("label[for='portafirmesDocumentTipus']").text( $("label[for='portafirmesDocumentTipus']").text().replace(' *', '') );
	            	$($("label[for='portafirmesResponsables']")[1]).text( $($("label[for='portafirmesResponsables']")[1]).text().replace(' *', '') );
	            }			
		});
		
		$("#portafirmesFluxTipus").on('change', function(){
			if($(this).val() == 'SIMPLE') {
				$('.flux_portafib').hide();
				$('.flux_simple').show();
			} else {
				$('.flux_portafib').show();
				$('.flux_simple').hide();
			}
		});
		
		$("#portafirmesFluxTipus").trigger('change');
		
		$(".portafirmesFluxId_btn_edicio").on('click', function(){
			var metaDocumentNom = "${fn:replace(metaDocumentCommand.nom, charSearch, charReplace)}";
			$.ajax({
				type: 'GET',
				dataType: "json",
				data: {nom: metaDocumentNom, plantillaId: $("#portafirmesFluxId").val()},
				url: "<c:url value="/modal/metaDocument/iniciarTransaccio"/>",
				success: function(transaccioResponse) {
					if (transaccioResponse != null && !transaccioResponse.error) {
						localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
						$('#metaDocumentCommand').addClass("hidden");
						var fluxIframe = '<div class="iframe_container">' + 
											'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe>' + 
							  			 '</div>';
						$('.flux_container').html(fluxIframe);	
						adjustModalPerFlux();
						$body = $("body");
						$body.addClass("loading");
					} else if (transaccioResponse != null && transaccioResponse.error) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert">' + 
											'<a class="close" data-dismiss="alert">×</a>' + 
											'<span>' + transaccioResponse.errorDescripcio + '</span>' +
									   '</div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						webutilModalAdjustHeight();
					}
				},
				error: function(error) {
					if (error != null && error.responseJSON != null) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert">' + 
											'<a class="close" data-dismiss="alert">×</a>' + 
											'<span>' + error.responseJSON.message + '</span>' + 
									   '</div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						webutilModalAdjustHeight();
					}
				}
			});
		});
		
		$.ajax({
			type: 'GET',
			dataType: "json",
			url: "<c:url value="/metaExpedient/metaDocument/flux/plantilles"/>",
			success: function(data) {
				var plantillaActual = "${portafirmesFluxSeleccionat}";
				var selPlantilles = $("#portafirmesFluxId");
				selPlantilles.empty();
				selPlantilles.append("<option value=\"\"></option>");
				if (data) {
					var items = [];
					$.each(data, function(i, val) {
						items.push({
							"id": val.fluxId,
							"text": val.nom
						});
						selPlantilles.append("<option value=\"" + val.fluxId + "\">" + val.nom + "</option>");
					});
				}
				var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
				selPlantilles.select2(select2Options);
				if (plantillaActual != '') {
					selPlantilles.val(plantillaActual);
					selPlantilles.change();
					$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.flux.editar"/>");
				}
			},
			error: function (error) {
				var selPlantilles = $("#portafirmesFluxId");
				selPlantilles.empty();
				selPlantilles.append("<option value=\"\"></option>");
				var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
				selPlantilles.select2(select2Options);
			}
		});
		
		$(".portafirmesFluxId_btn_addicional").on('click', function () {
			if (confirm("<spring:message code="metadocument.form.camp.portafirmes.flux.esborrar.confirm"/>")) {
				var portafirmesFluxId = $("#portafirmesFluxId").val();
				var successAlert = "<div class='alert alert-success' role='alert'>" +
										"<a class='close' data-dismiss='alert'>×</a>" + 
										"<span><spring:message code='metadocument.form.camp.portafirmes.flux.esborrar.ok'/></span>" +
								   "</div>";
				var errorAlert = "<div class='alert alert-danger' role='alert'>" + 
									 "<a class='close' data-dismiss='alert'>×</a>" + 
									 "<span><spring:message code='metadocument.form.camp.portafirmes.flux.esborrar.ko'/></span>" + 
								 "</div>";
				$.ajax({
					type: 'GET',
					dataType: "json",
					url: "<c:url value="/metaExpedient/metaDocument/flux/esborrar/"/>" + portafirmesFluxId,
					success: function(esborrat) {
						if (esborrat) {
							$('form').prev().find('.alert').remove();
							$('form').prev().prepend(successAlert);
							$("#portafirmesFluxId option[value='" + portafirmesFluxId + "']").remove();
						} else {
							$('form').prev().find('.alert').remove();
							$('form').prev().prepend(errorAlert);
						}
						webutilModalAdjustHeight();
					},
					error: function (error) {
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(errorAlert);
						webutilModalAdjustHeight();		
					}
				});
			}
		});
		$("#portafirmesFluxId").on('change', function () {
			var portafirmesFluxId = $(this).val();
			if(portafirmesFluxId != null && portafirmesFluxId != '') {
				$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.flux.editar"/>");
				$(".portafirmesFluxId_btn_addicional").removeClass("flux_disabled");
			} else {
				$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>");
				$(".portafirmesFluxId_btn_addicional").addClass("flux_disabled");
			}
		});
		
		$("#portafirmesFluxId").trigger('change');

		$('.modal-cancel').on('click', function(){
			localStorage.removeItem('transaccioId');
		});
		
		$(".portafirmesResponsables_btn").attr("title", "<spring:message code="metadocument.form.camp.portafirmes.carrecs"/>");
		$("#portafirmesResponsables").on('select2:unselecting', function (e) {
			var optionRemoved = e.params.args.data.id;
			$("#portafirmesResponsables option[value='" + optionRemoved + "']").remove();
		});

		$("#pinbalActiu").on('change', function() {
			if ($(this).prop("checked")) {
				$('#pinbalServei').removeAttr('disabled');
				$('#pinbalServei').parent().parent().css('display', 'block');
			} else {
				$('#pinbalServei').attr('disabled', 'disabled');
				$('#pinbalServei').parent().parent().css('display', 'none');
			}
		});
		$("#pinbalActiu").trigger('change');
	});
	
function toggleCarrecs() {
	var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu');
	if (dropdown.length === 0) {
		$(".portafirmesResponsables_btn").parent().append(recuperarCarrecs());
		$(".portafirmesResponsables_btn").parent().find('.dropdown-menu').toggle();
		
	} else {
		dropdown.toggle();
	}
}

function recuperarCarrecs() {
	var llistatCarrecs = "<div class='loading dropdown-menu'>";
	$.ajax({
		type: 'GET',
		dataType: "json",
		url: "<c:url value="/metaExpedient/metaDocument/carrecs"/>",
		success: function(carrecs) {
			var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu');
			dropdown.removeClass('loading');
			if (carrecs) {
				llistatCarrecs += '<div class="carrecsList">';
				$.each(carrecs, function(i, carrec) {
					var nomCarrec = carrec.carrecName + ' (' + carrec.usuariPersonaNom + ' - ' + carrec.usuariPersonaNif + ' - ' + carrec.usuariPersonaId + ')';
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
			var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu');
			dropdown.removeClass('loading');
			dropdown.empty();
			dropdown.append("Hi ha hagut un problema recuperant els càrrecs " + error.statusText);
		},
		statusCode: {
	        500: function(error) {
	        	var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu');
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
		var nomCarrec = carrec.carrecName + ' (' + carrec.usuariPersonaNif + ')';
		var items = [];
		items.push({
			"id": carrec.carrecId,
			"text": nomCarrec
		});
	    var newOption = new Option(items[0].text, items[0].id, true, true);
	    $("#portafirmesResponsables").append(newOption).trigger('change');

		$('.carrec_' + carrec.carrecId).addClass('carrec-selected');
	}
}
	
function adjustModalPerFlux() {
	var $iframe = $(window.frameElement);
	$iframe.css('height', '100%');
	$iframe.parent().css('height', '600px');
	$iframe.closest('div.modal-content').css('height',  'auto');
	$iframe.closest('div.modal-dialog').css({
		'height':'auto',
		'height': '100%',
		'margin': '3% auto',
		'padding': '0'
	});
	$iframe.closest('div.modal-lg').css('width', '95%');
	$iframe.parent().next().addClass('hidden');
}

function removeLoading() {
	$body = $("body");
	$body.removeClass("loading");
}
</script>
	
</head>
<body>
	<c:set var="formAction"><rip:modalUrl value="/metaExpedient/${metaDocumentCommand.metaExpedientId}/metaDocument"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="metaDocumentCommand" enctype="multipart/form-data">
		<ul class="nav nav-tabs" role="tablist">
			<li role="presentation" class="active"><a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.dades"/></a></li>
			<li role="presentation"><a href="#dades-nti" aria-controls="dades-nti" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.dadesnti"/></a></li>
			<li role="presentation"><a href="#firma-portafirmes" aria-controls="firma-portafirmes" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.portafirmes"/></a></li>
			<li role="presentation"><a href="#firma-passarela" aria-controls="firma-passarela" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.passarela"/></a></li>
			<c:if test="${isFirmaBiometrica}">
				<li role="presentation"><a href="#firma-biometrica" aria-controls="firma-biometrica" role="tab" data-toggle="tab"><spring:message code="metadocument.form.camp.tab.firma.biometrica"/></a></li>
			</c:if>
			<li role="presentation"><a href="#pinbal" aria-controls="pinbal" role="tab" data-toggle="tab"><spring:message code="metadocument.form.tab.pinbal"/></a></li>
		</ul>
		<form:hidden path="id"/>
		<form:hidden path="entitatId"/>
		<form:hidden path="metaExpedientId"/>
		<form:hidden path="comu"/>
		<br/>
		<div class="tab-content content">
			<div role="tabpanel" class="tab-pane active" id="dades">
				<rip:inputText name="codi" textKey="metadocument.form.camp.codi" required="true"/>
				<rip:inputText name="nom" textKey="metadocument.form.camp.nom" required="true"/>
				<rip:inputTextarea name="descripcio" textKey="metadocument.form.camp.descripcio"/>
				<rip:inputSelect name="multiplicitat" textKey="metadocument.form.camp.multiplicitat" optionItems="${multiplicitatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<rip:inputFile name="plantilla" textKey="metadocument.form.camp.plantilla" fileName="${metaDocumentCommand.plantillaNom}"/>
			</div>
			<div role="tabpanel" class="tab-pane" id="dades-nti">
				<rip:inputSelect name="ntiOrigen" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.origen" optionItems="${ntiOrigenOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" required="true"/>
				<rip:inputSelect name="ntiTipoDocumental" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.tipdoc" optionItems="${ntiTipusDocumentalOptions}" optionValueAttribute="codi" optionTextAttribute="nom" required="true"/>
				<rip:inputSelect name="ntiEstadoElaboracion" emptyOption="true" emptyOptionTextKey="contingut.document.form.camp.nti.cap" textKey="contingut.document.form.camp.nti.estela" optionItems="${ntiEstatElaboracioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
			</div>
			<div role="tabpanel" class="tab-pane" id="firma-portafirmes">
				<rip:inputCheckbox name="firmaPortafirmesActiva" textKey="metadocument.form.camp.firma.portafirmes.activa"/>
				<c:choose>
					<c:when test="${isPortafirmesDocumentTipusSuportat}">
						<rip:inputSelect name="portafirmesDocumentTipus" textKey="metadocument.form.camp.portafirmes.document.tipus" optionItems="${portafirmesDocumentTipus}" optionValueAttribute="id" optionTextAttribute="codiNom" emptyOption="true" optionMinimumResultsForSearch="0"/>
					</c:when>
					<c:otherwise>
						<rip:inputText name="portafirmesDocumentTipus" textKey="metadocument.form.camp.portafirmes.document.tipus"/>
					</c:otherwise>
				</c:choose>
				<rip:inputSelect name="portafirmesFluxTipus" textKey="metadocument.form.camp.portafirmes.fluxtip" optionItems="${metadocumentFluxtipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				<div class="flux_portafib">
					<rip:inputSelect name="portafirmesFluxId" textKey="metadocument.form.camp.portafirmes.flux.id" emptyOption="true" botons="true" icon="fa fa-external-link" iconAddicional="fa fa-trash-o" buttonMsg="${buttonTitle}"/>
				</div>
				<div class="flux_simple">
					<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
					<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
					<rip:inputSuggest 
						name="portafirmesResponsables" 
						urlConsultaInicial="${urlConsultaInicial}" 
						urlConsultaLlistat="${urlConsultaLlistat}" 
						textKey="metadocument.form.camp.portafirmes.responsables"
						suggestValue="codi"
						suggestText="nom"
						suggestTextAddicional="nif"
						required="${!metaDocumentCommand.comu}"
						icon="fa fa-star"/>
					<rip:inputSelect name="portafirmesSequenciaTipus" textKey="metadocument.form.camp.portafirmes.seqtip" optionItems="${metadocumentSequenciatipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				</div>							
			</div>
			<div role="tabpanel" class="tab-pane" id="firma-passarela">
				<rip:inputCheckbox name="firmaPassarelaActiva" textKey="metadocument.form.camp.passarela.activa"/>
			</div>
			<c:if test="${isFirmaBiometrica}">
				<div role="tabpanel" class="tab-pane" id="firma-biometrica">
					<rip:inputCheckbox name="firmaBiometricaActiva" textKey="metadocument.form.camp.firma.biometrica.activa"/>
					<rip:inputCheckbox name="biometricaLectura" textKey="metadocument.form.camp.biometrica.lectura"/>
				</div>
			</c:if>
			<div role="tabpanel" class="tab-pane" id="pinbal">
				<rip:inputCheckbox name="pinbalActiu" textKey="metadocument.form.camp.pinbal.actiu"/>
				<rip:inputSelect name="pinbalServei" textKey="metadocument.form.camp.pinbal.servei" required="true" optionItems="${pinbalServeiEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
			</div>
		</div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/metaDocument"/>" class="btn btn-default modal-cancel" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	<div class="flux_container"></div>
	<div class="rmodal"></div>
</body>
</html>
