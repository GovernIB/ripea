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
		var fluxIframe = '<div class="iframe_container">' + 
							'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="${urlEdicio}"></iframe>' + 
			  			 '</div>';
		$('.flux_container').html(fluxIframe);	
		adjustModalPerFlux();
		$body = $("body");
		$body.addClass("loading");
		
		var parentIframe = window.frameElement;
		var idModal = $(parentIframe.closest("[id^='modal_']")).attr('id');
		
		// Tancar transacció i esborrar localstorage
		window.parent.closeModal(idModal);
	} else {
		var fluxIframe = '<div class="iframe_container">' + 
							'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="${urlCreacio}"></iframe>' + 
					     '</div>';
		$('.flux_container').html(fluxIframe);	
		adjustModalPerFlux();
		$body = $("body");
		$body.addClass("loading");
		
		var parentIframe = window.frameElement;
		var idModal = $(parentIframe.closest("[id^='modal_']")).attr('id');
		
		// Tancar transacció i esborrar localstorage
		window.parent.closeModal(idModal);
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
	<div class="flux_container"></div>
	<div class="rmodal"></div>
</body>
</html>
