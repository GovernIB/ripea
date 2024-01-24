<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script>
let rootIframe = window.frameElement;

if (rootIframe) {
	var digitalizacioError = "${digitalizacioError}";
	var digitalizacioFinalOk = "${digitalizacioFinalOk}";
	var nomDocument = "${nomDocument}";
	var idTransaccio = localStorage.getItem('transaccioId');
	var urlDescarrega = "<a class='downloadLink' href='<c:url value='/digitalitzacio/descarregarResultat/" + idTransaccio + "'/>' >" + nomDocument + "</a> <br>";
	var urlCancel = "<span class='btn btn-default scan-cancel-btn'><spring:message code='contingut.document.form.camp.escaneig.cancelar'/></span>";

	if (digitalizacioError != null && digitalizacioError != '') {
		alertDiv = '<div class="alert alert-danger" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + digitalizacioError + '</span></div>'
	} else if (digitalizacioFinalOk != null && digitalizacioFinalOk != '') {
		alertDiv = '<div class="alert alert-success" role="alert"><a class="close" data-dismiss="alert">×</a><span>' + digitalizacioFinalOk + '</span></div>'
	}
	$(window.parent.document).find('.steps').remove('.alert');
	$(window.parent.document).find('.start-scan-btn').before(alertDiv);
	if (nomDocument != null && nomDocument != '') {
		$(rootIframe.parentElement.parentElement).append(urlDescarrega);
		$(rootIframe.parentElement.parentElement).append(urlCancel);
	} else {
		$(window.parent.document).find('.start-scan-btn').show();
	}


	$(window.parent.document).find('#ntiEstadoElaboracion option').each(function() {
	    if ( $(this).val() != 'EE03' ) {
	        $(this).remove();
	    }
	});

	

	 $('.crearDocumentBtnSubmit', parent.parent.document).prop('disabled', false);
	
	//Adjust modal width/height
	adjustModalPerFlux();
	$(rootIframe.parentElement).trigger('remove');
}

function adjustModalPerFlux() {
	webutilModalAdjustHeight();
	let $iframe = $(window.parent.frameElement);
	let height = localStorage.getItem('currentIframeHeight');
	$iframe.css('min-height', '');
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
