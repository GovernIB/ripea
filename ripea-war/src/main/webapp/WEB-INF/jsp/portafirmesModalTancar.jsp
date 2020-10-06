<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
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
	<script src="<c:url value="/webjars/jquery/1.12.4/dist/jquery.min.js"/>"></script>
<script>
let fluxIframe = window.frameElement;

if (fluxIframe) {
	let idFlux = "${fluxId}";
	let FluxError = "${FluxError}";
	let FluxCreat = "${FluxCreat}";
	let FluxNom = "${FluxNom}";
	let alertDiv;
	
	if (idFlux != null && idFlux != '') {
		//$(fluxIframe.parentElement.parentElement).prev().find('#portafirmesFluxId').val(idFlux);
		$(fluxIframe.parentElement.parentElement).prev().find('#portafirmesFluxId').append("<option value=\"" + idFlux + "\" selected>" + FluxNom + "</option>");
	} else if (FluxError != null && FluxError != '') {
		alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + FluxError + '</span></div>';
		if (localStorage.getItem('transaccioId') == null && localStorage.getItem('transaccioId') == '') 
			$(fluxIframe.parentElement.parentElement).prev().find('#portafirmesFluxId').attr('disabled', false);
	}
	if (FluxCreat != null && FluxCreat != '') {
		//
		$(fluxIframe.parentElement.parentElement).prev().find('#portafirmesEnviarFluxId').attr('disabled', true);
		alertDiv = '<div class="alert alert-success" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + FluxCreat + '</span>';
		if ((FluxNom != null && FluxNom != '')) {
			let $comentari = $(fluxIframe.parentElement.parentElement).prev().find('.comentari');
			$comentari = $(fluxIframe.parentElement.parentElement).prev().find('.comentari');
			$comentari.text('');
			//if flux success text exists
			$(fluxIframe.parentElement.parentElement).prev().find('#portafirmesEnviarFluxId').closest('.form-group').prev('p').remove();
			
			$(fluxIframe.parentElement.parentElement).prev().find('#portafirmesEnviarFluxId').closest('form').find('.success-label').removeClass('hidden');
			$(fluxIframe.parentElement.parentElement).prev().find('#portafirmesEnviarFluxId').closest('.form-group').before('<p class="success col-xs-8"></p>');
			let $success =  $(fluxIframe.parentElement.parentElement).prev().find('.success');
			
			var text = '<spring:message code='contingut.document.form.camp.portafirmes.flux.seleccionat'/>';
			$success.html(text + " <span>" + FluxNom + "</span>");
			$success.css('color', '#3c763d');
			$success.find('span').css('font-weight', 'bold');
		}
	}
	$(fluxIframe.parentElement.parentElement).prev().removeClass('hidden');
	$(fluxIframe.parentElement.parentElement).prev().find('.alert').remove();
	$(fluxIframe.parentElement.parentElement).prev().prepend(alertDiv);
	
	//Adjust modal width/height
	adjustModalPerFluxRemove(FluxNom);
	$(fluxIframe.parentElement).trigger('remove');
}

function adjustModalPerFluxRemove(FluxNom) {
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

	$iframe.parent().next().removeClass('hidden');
	if ($iframe.parent().next().find('button').hasClass('disabled') && (FluxNom != null && FluxNom != '')) {
		$iframe.parent().next().find('button').removeClass("disabled");
	}
	localStorage.removeItem('currentIframeHeight');
}

</script>
</head>
</html>
