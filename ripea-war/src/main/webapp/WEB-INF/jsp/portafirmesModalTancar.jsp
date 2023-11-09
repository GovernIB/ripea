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
	const idTransaccioFlux = "${fluxId}";
	const fluxErrorDesc = "${FluxError}";
	const fluxSuccesDesc = "${FluxCreat}";
	const fluxCreatedNom = "${FluxNom}";
	const fluxCreatedDescripcio = "${FluxDescripcio}";
	const $modalFlux = $(fluxIframe.parentElement.parentElement).prev();
	var alertDiv;
	
	if (idTransaccioFlux != null && idTransaccioFlux != '') {
		var successMessage = "<option value=\"" + idTransaccioFlux + "\" selected>" + fluxCreatedNom + "</option>";
		$modalFlux.find('#portafirmesFluxId').append(successMessage);
	} else if (fluxErrorDesc != null && fluxErrorDesc != '') {
		alertDiv = '<div class="alert alert-danger" role="alert"> \
						<a class="close" data-dismiss="alert">×</a> \
						<span>' + fluxErrorDesc + '</span> \
					</div>';
		//desactivar selecció si s'ha creat un nou flux
		if (localStorage.getItem('transaccioId') == null && localStorage.getItem('transaccioId') == '') {
			$modalFlux.find('#portafirmesFluxId').attr('disabled', false);
		}

		$modalFlux.find(".portafirmesEnviarFluxId_btn_addicional").find('i').addClass('fa-eye').removeClass('fa-eye-slash');
	}
	if (fluxSuccesDesc != null && fluxSuccesDesc != '') {
		$modalFlux.find('#portafirmesEnviarFluxId').empty();
		$modalFlux.find('#portafirmesEnviarFluxId').attr('disabled', true);
		//desactivar botó de visualitzar
		$modalFlux.find('.portafirmesEnviarFluxId_btn_addicional').attr('disabled', true);
		alertDiv = '<div class="alert alert-success" role="alert"> \
						<a class="close" data-dismiss="alert">×</a> \
						<span>' + fluxSuccesDesc + '</span> \
					</div>';
		if ((fluxCreatedNom != null && fluxCreatedNom != '')) {
			var $comentari = $modalFlux.find('.comentari');
			$comentari = $modalFlux.find('.comentari');
			$comentari.text('');
			//if flux success text exists
			$modalFlux.find('#portafirmesEnviarFluxId').closest('.form-group').prev('p').remove();
			$modalFlux.find('#portafirmesEnviarFluxId').closest('form').find('.success-label').removeClass('hidden');
			$modalFlux.find('#portafirmesEnviarFluxId').closest('.form-group').before('<p class="success col-xs-8"></p>');
			var $success =  $modalFlux.find('.success');
			var text = '<spring:message code='contingut.document.form.camp.portafirmes.flux.seleccionat'/>';
			$success.html(text + " <span>" + fluxCreatedNom + "</span>");
			$success.css('color', '#3c763d');
			$success.find('span').css('font-weight', 'bold');
		}
	}
	
	$modalFlux.removeClass('hidden');
	$modalFlux.find('.alert').remove();
	$modalFlux.prepend(alertDiv);
	
	//Adjust modal width/height
	adjustModalPerFluxRemove(fluxCreatedNom);
	
	if ('${isEdicio}' || '${isCreacio}') {

		localStorage.setItem('fluxSuccesDesc', fluxSuccesDesc);
		localStorage.setItem('fluxErrorDesc', fluxErrorDesc);
		
		$(window.parent.frameElement).closest('.modal').find('.close').click();
		
	}
	
	$(fluxIframe.parentElement).trigger('remove');

}

function adjustModalPerFluxRemove(fluxCreatedNom) {
	webutilModalAdjustHeight();
	var $iframe = $(window.parent.frameElement);
	var height = localStorage.getItem('currentIframeHeight');

	$iframe.css('height', (height != 'null') ? height : '50vh');
	$iframe.parent().css('height', 'auto');
	$iframe.closest('div.modal-content').css('height',  '');
	$iframe.closest('div.modal-dialog').css({
		'height':'',
		'margin': '30px auto',
		'padding': '0'
	});
	$iframe.closest('div.modal-lg').css('width', '900px');

	$iframe.parent().next().removeClass('hidden');
	if ($iframe.parent().next().find('button').hasClass('disabled') && (fluxCreatedNom != null && fluxCreatedNom != '')) {
		$iframe.parent().next().find('button').removeClass("disabled");
	}
	localStorage.removeItem('currentIframeHeight');
}

</script>
</head>
</html>
