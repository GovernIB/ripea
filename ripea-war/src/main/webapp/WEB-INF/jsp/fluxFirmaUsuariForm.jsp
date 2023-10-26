<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${empty FluxFirmaUsuariCommand.id}"><c:set var="titol"><spring:message code="flux.firma.usuari.form.titol.crear"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="flux.firma.usuari.form.titol.modificar"/></c:set></c:otherwise>
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
	<rip:modalHead/>
<style type="text/css">
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
</style>

<script type="text/javascript">
$(document).ready(function() {
	$('#fluxNom').closest('.form-group').hide();
	$('#fluxDescripcio').closest('.form-group').hide();
	localStorage.setItem("currentIframeHeight", "180px");
	
	if ('${urlEdicio}') {
		$('#fluxFirmaUsuariCommand').addClass("hidden");
		var fluxIframe = '<div class="iframe_container">' + 
							'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="${urlEdicio}"></iframe>' + 
			  			 '</div>';
		$('.flux_container').html(fluxIframe);	
		adjustModalPerFlux();
		$body = $("body");
		$body.addClass("loading");
	} else {
		$(".portafirmesFluxId_btn_crear").on('click', function() {
			$.ajax({
				type: 'GET',
				dataType: "json",
				url: "<c:url value="/modal/metaExpedient/metaDocument/iniciarTransaccio"/>",
				success: function(transaccioResponse) {
					if (transaccioResponse != null && !transaccioResponse.error) {
						localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
						$('#fluxFirmaUsuariCommand').addClass("hidden");
						$('.flux_container').html('<div class="iframe_container"><iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe></div>');	
						adjustModalPerFlux();
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
					if (error != null && error.responseJSON != null) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + error.responseJSON.message + '</span></div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						webutilModalAdjustHeight();
					}
				}
			});
		});
		
		var parentIframe = window.frameElement;
		var idModal = $(parentIframe.closest("[id^='modal_']")).attr('id');
		
		// Tancar transacció i esborrar localstorage
		window.parent.removePlantillaEvent(false, idModal);
		
		document.querySelector('form').addEventListener('submit', function (e) {
			localStorage.setItem('flagSubmit', '1');
		});
	}
});

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
	<c:set var="formAction"><rip:modalUrl value="/fluxusuari"/></c:set>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" commandName="fluxFirmaUsuariCommand" role="form">
		<form:hidden path="id"/>
		<rip:inputText name="nom" id="fluxNom" textKey="flux.firma.usuari.form.camp.nom" readonly="true"/>
		<rip:inputText name="descripcio" id="fluxDescripcio" textKey="flux.firma.usuari.form.camp.descripcio"  readonly="true"/>
		<div class="flux_portafib form-group text-center">
			<a class="btn btn-default portafirmesFluxId_btn_crear"><spring:message code="flux.firma.usuari.form.crear"/>  <i class="fa fa-external-link"></i></a>
			<form:hidden path="portafirmesFluxId" id="usuariPortafirmesFluxId"/>
		</div>	
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/></button>
			<button onclick="window.parent.removePlantillaCallback(${not empty urlEdicio ? true : false}); return true;" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
		</div>
	</form:form>
	<div class="flux_container"></div>
	<div class="rmodal"></div>
</body>
</html>
