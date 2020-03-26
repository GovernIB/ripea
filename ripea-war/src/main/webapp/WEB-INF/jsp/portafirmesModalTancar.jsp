<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="fluxid">${fluxId}</c:set>

<html>
<head>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/webjars/jquery/1.12.0/dist/jquery.min.js"/>"></script>
<script>
let fluxIframe = window.frameElement;

if (fluxIframe) {
	var idFlux = "${fluxId}";
	var FluxError = "${FluxError}";
	var FluxCreat = "${FluxCreat}";
	var FluxNom = "${FluxNom}";
	var FluxDescripcio = "${FluxDescripcio}";
	var alertDiv;
	
	if (idFlux != null && idFlux != '') {
		$(fluxIframe.parentElement.parentElement).prev().find('#portafirmesFluxId').val(idFlux);
	} else if (FluxError != null && FluxError != '') {
		alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + FluxError + '</span></div>';
	}
	if (FluxCreat != null && FluxCreat != '') {
		alertDiv = '<div class="alert alert-success" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + FluxCreat + '</span>';
		
		if ((FluxNom != null && FluxNom != '') && (FluxDescripcio != null && FluxDescripcio != '')) {
			alertDiv += '<br>' +
						'<ul>' +
							'<li>Nom flux: ' + FluxNom + '</li>' +
							'<li>Descripció flux: ' + FluxDescripcio + '</li>' +
						'</ul></div>';
		}
	}
	$(fluxIframe.parentElement.parentElement).prev().removeClass('hidden');
	$(fluxIframe.parentElement.parentElement).prev().find('.alert').remove();
	$(fluxIframe.parentElement.parentElement).prev().prepend(alertDiv);
	
	//Adjust modal width/height
	adjustModalPerFluxRemove();
	
	$(fluxIframe.parentElement).trigger('remove');
}

function adjustModalPerFluxRemove() {
	webutilModalAdjustHeight();
	let $iframe = $(window.parent.frameElement);
	let height = localStorage.getItem('currentIframeHeight');
	$iframe.css('height', height =! null ? height : '50vh');
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

</script>
</head>
</html>
