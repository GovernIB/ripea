<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%
pageContext.setAttribute(
		"metadocumentSeqtipEnumOptions",
		es.caib.ripea.war.helper.EnumHelper.getOptionsForEnum(
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
.portafirmesFluxId_btn:hover {
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
</style>

<script type="text/javascript">
$(document).ready(function() {
	let parentIframe = window.frameElement;
	let idModal = $(parentIframe.closest("[id^='modal_']")).attr('id');
	
	$('form').on('submit', function(){
		window.parent.addLoading(idModal);
	});
	
	let currentHeight = window.frameElement.contentWindow.document.body.scrollHeight;
	localStorage.setItem("currentIframeHeight", currentHeight);
	let fluxPredefinit = ${nouFluxDeFirma};
	
	if (fluxPredefinit != null && fluxPredefinit) {
		$('form').find('button').addClass('disabled');
	} 

	let fluxCreat = localStorage.getItem('fluxCreat');
	if (fluxCreat != null && fluxCreat != "undefined" && !"${isNouEnviament}") {
		$('.comentari').remove();
		$(".portafirmesFlux_btn").parent().before(fluxCreat);
		$('form').find('button').removeClass("disabled");
	}
	
	$(".portafirmesFlux_btn").on('click', function(){		
		let documentNom = '${document.nom}';
		$.ajax({
			type: 'GET',
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			data: {nom: documentNom},
			url: "<c:url value="/modal/document/portafirmes/iniciarTransaccio"/>",
			success: function(transaccioResponse, textStatus, XmlHttpRequest) {
				if (transaccioResponse != null && !transaccioResponse.error) {
					localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
					$('.content').addClass("hidden");
					$('.flux_container').html('<div class="iframe_container"><iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe></div>');	
					adjustModalPerFlux(true);
					$body = $("body");
					$body.addClass("loading");
				} else if (transaccioResponse != null && transaccioResponse.error) {
					let currentIframe = window.frameElement;
					var alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + transaccioResponse.errorDescripcio + '</span></div>';
					$('form').prev().find('.alert').remove();
					$('form').prev().prepend(alertDiv);
					webutilModalAdjustHeight();
				}
			},
			error: function(error) {
				if (error != null && error.responseText != null) {
					let currentIframe = window.frameElement;
					var alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + error.responseText + '</span></div>';
					$('form').prev().find('.alert').remove();
					$('form').prev().prepend(alertDiv);
					webutilModalAdjustHeight();
				}
			}
		});
	});	
					
});

function adjustModalPerFlux(amagar) {
	let $iframe = $(window.frameElement);
	$iframe.css('height', '100%');
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

function adjustModalPerFluxRemove() {
	let $iframe = $(window.frameElement);
	let height = localStorage.getItem('currentIframeHeight');
	$iframe.parent().css('height', 'auto');
	$iframe.closest('div.modal-content').css('height',  '');
	$iframe.closest('div.modal-dialog').css({
		'height':'',
		'margin': '30px auto',
		'padding': '0'
	});
	$iframe.closest('div.modal-lg').css('width', '900px');
	localStorage.removeItem('currentIframeHeight');
}

function removeLoading() {
	$body = $("body");
	$body.removeClass("loading");
}

function mostrarFlux(urlPlantilla) {
	if ($('.flux_container').html() == '') { //empty
		$('#eye').toggleClass("fa-eye", false);
		$('#eye').toggleClass("fa-eye-slash", true);
		$('#eye').attr('title', "<spring:message code="contenidor.document.portafirmes.boto.hide" />");
		adjustModalPerFlux(false);
		$('.flux_container').html('<hr><div class="iframe_container"><iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + urlPlantilla + '"></iframe></div>');	
	} else {
		$('#eye').toggleClass("fa-eye", true);
		$('#eye').toggleClass("fa-eye-slash", false);
		$('#eye').attr('title', "<spring:message code="contenidor.document.portafirmes.boto.show" />");
		adjustModalPerFluxRemove();
		$('.flux_container').empty();
	}
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
	<c:choose>
		<c:when test="${isTasca}">
			<c:set var="formAction"><rip:modalUrl value="/usuariTasca/${tascaId}/document/${document.id}/portafirmes/upload"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="formAction"><rip:modalUrl value="/document/${document.id}/portafirmes/upload"/></c:set>
		</c:otherwise>
	</c:choose>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal content" commandName="portafirmesEnviarCommand" role="form">
		<rip:inputText name="motiu" textKey="contenidor.document.portafirmes.camp.motiu" required="true"/>
		<rip:inputSelect name="prioritat" textKey="contenidor.document.portafirmes.camp.prioritat" optionEnum="PortafirmesPrioritatEnumDto" required="true"/>
		<rip:inputDate name="dataCaducitat" textKey="contenidor.document.portafirmes.camp.data.caducitat" required="true"/>
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
				required="true"/>
						
			<rip:inputSelect name="portafirmesSequenciaTipus" textKey="metadocument.form.camp.portafirmes.seqtip" optionItems="${metadocumentSeqtipEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
		</c:when>
		<c:when test="${fluxTipus == 'PORTAFIB'}">
			<rip:inputSelect name="annexos" textKey="metadocument.form.camp.portafirmes.annexos" optionValueAttribute="id" optionTextAttribute="nom" optionItems="${annexos}" multiple="true"/>
			<div class="form-group">
				<label class="control-label col-xs-4 fluxInputLabel"><spring:message code="metadocument.form.camp.portafirmes.flux" /> *</label>
				<c:if test="${!nouFluxDeFirma}">
					<p class="comentari col-xs-8"><spring:message code="metadocument.form.camp.portafirmes.flux.comment" /> <a class="btn btn-default btn-xs exemple_boto"  onclick="mostrarFlux('${urlPlantilla}')"><span id="eye" class="fa fa-eye" title="<spring:message code="contenidor.document.portafirmes.boto.show" />"></span></a></p>
				</c:if>
				<label class="col-xs-4"></label>
				<div class="col-xs-8" style="margin-top: 3px">
					<span class='btn btn-flux form-control portafirmesFlux_btn' title="<spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>"><spring:message code="metadocument.form.camp.portafirmes.flux.iniciar"/>  <i class="fa fa-external-link"></i></span>
				</div>
			</div>
		</c:when>
		</c:choose>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-success"><span class="s"></span> <spring:message code="contenidor.document.portafirmes.enviar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
	<div class="flux_container"></div>
	<div class="rmodal"></div>
</body>
</html>
