<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="contingut.boto.crear.document.multiple"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value='/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css'/>" rel="stylesheet"/>
	<link href="<c:url value='/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css'/>" rel="stylesheet"/>
	<script src="<c:url value='/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js'/>"></script>
	<script src="<c:url value='/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js'/>"></script>
	<link href="<c:url value='/css/jasny-bootstrap.min.css'/>" rel="stylesheet">
	<script src="<c:url value='/js/jasny-bootstrap.min.js'/>"></script>
	<script src="<c:url value='/webjars/moment/2.15.1/min/moment.min.js'/>"></script>
	<script src="<c:url value='/webjars/moment/2.15.1/min/locales.min.js'/>"></script>
	<script src="<c:url value='/webjars/moment/2.15.1/locale/${requestLocale}.js'/>"></script>
	<link href="<c:url value='/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build/css/bootstrap-datetimepicker.min.css'/>" rel="stylesheet"/>
	<script src="<c:url value='/webjars/eonasdan-bootstrap-datetimepicker/4.7.14/build/js/bootstrap-datetimepicker.min.js'/>"></script>
	<script src="<c:url value='/js/webutil.common.js'/>"></script>
	<script src="<c:url value='/webjars/autoNumeric/1.9.30/autoNumeric.js'/>"></script>
	<script src="<c:url value='/js/jquery.maskedinput.min.js'/>"></script>
	<rip:modalHead/>
	
<style>
	.progressContainer {
	    margin-top: 15px;
	    text-align: center;
	    width: 95%;
	    margin: auto;
	}
	.progressText {
	    margin-top: 10px;
	}
	.help-block {
	    color: #a94442;
	}
</style>

<script>
	'use strict';

	var intervalProgres;
	var mostrarConfirmacio = true;
	
	var preparantZipMsg = "<spring:message code='contingut.boto.crear.document.multiple.preparant'/>";
	var tancarModalMsg = "<spring:message code='contingut.boto.crear.document.multiple.tancar'/>";
	var cancelarModalMsg = "<spring:message code='contingut.boto.crear.document.multiple.cancelar'/>";

	var resultatProcesTitol = "<spring:message code='contingut.boto.crear.document.multiple.resultat.proces.titol'/>";
	var resultatProcesErrorTitol = "<spring:message code='contingut.boto.crear.document.multiple.resultat.proces.error.titol'/>";
	var resultatProcesTotal = "<spring:message code='contingut.boto.crear.document.multiple.resultat.proces.total'/>";
	var resultatProcesTotalOk = "<spring:message code='contingut.boto.crear.document.multiple.resultat.proces.total.ok'/>";
	var resultatProcesTotalError = "<spring:message code='contingut.boto.crear.document.multiple.resultat.proces.total.error'/>";
	var resultatProcesFirmaError = "<spring:message code='contingut.boto.crear.document.multiple.resultat.proces.total.firma.error'/>";
	var resultatProcesTotalCarpetes = "<spring:message code='contingut.boto.crear.document.multiple.resultat.proces.total.carpetes'/>";
	var resultatProcesTamany = "<spring:message code='contingut.boto.crear.document.multiple.resultat.proces.tamany'/>";

	var selectors = {
		form: 'form',
		formContent: '.form-content',
		esborranys: '.esborranys',
		progressZip: '.progressZip',
		btnProcessar: '#processarDocumentsBtn',
		btnCancelar: '#cancelarBtn',
		btnCancelarProces: '#cancelarProcessarDocumentsBtn'
	};

	var $form;
	var $formContent;
	var $esborranys;
	var $progressZip;
	var $btnProcessar;
	var $btnCancelar;
	var $btnCancelarProces;

	$(document).ready(function () {
		inicialitzarSelectors();
		inicialitzarFormulari();
		inicialitzarProcesZip();
		mostrarBarraProgresExistent();
	});

	function inicialitzarSelectors() {
		$form = $(selectors.form);
		$formContent = $(selectors.formContent);
		$esborranys = $(selectors.esborranys);
		$progressZip = $(selectors.progressZip);
		$btnProcessar = $(selectors.btnProcessar);
		$btnCancelar = $(selectors.btnCancelar);
		$btnCancelarProces = $(selectors.btnCancelarProces);
	}

	function inicialitzarFormulari() {
		$btnCancelar.hide();
		$btnCancelarProces.hide();
		$btnProcessar.prop('disabled', false);
	    clearInterval(intervalProgres);
	}
	
	function processarFormulari() {
		$btnProcessar = $(parent.document).find(selectors.btnProcessar);
		$btnCancelar = $(parent.document).find(selectors.btnCancelar);
		$btnCancelarProces = $(parent.document).find(selectors.btnCancelarProces);

		$btnProcessar.prop('disabled', true);
        $btnCancelar.show();
		$btnCancelarProces.show();
	}
	
	function ocultarFormulari() {
		$formContent.hide();
		$esborranys.hide();
	}

	function mostrarFormulari() {
		$formContent.show();
		$esborranys.show();
	}

	function resetErrors() {
		$form.find('.help-block').remove();
		$progressZip.empty();
	}

	function inicialitzarProcesZip() {
		$btnProcessar.on('click', function () {
			
			processarFormulari();
			ocultarFormulari();
			mostrarBarraProgres();

			var data = new FormData($form[0]);

	        
			$.ajax({
				url: $form[0].action,
				type: 'POST',
				data: data,
				processData: false,
				contentType: false,
				error: function (jqXHR) {
					restaurarFormulariError(jqXHR.responseText);
				}
			});
		});
	}

	function restaurarFormulariError(errorText) {
		mostrarFormulari();

		inicialitzarFormulari();

		resetErrors();

		$form.find('.fileinput').after(
			'<p class="help-block"><span class="fa fa-exclamation-triangle"></span> ' +
			errorText +
			'</p>'
		);
	}

	 function cancelarProcessamentZip() {
		if (confirm(cancelarModalMsg)) {
			$.post("<c:url value='/contingut/${command.pareId}/zip/importacio/cancelar/'/>", function () {
				window.top.location.reload();
			});
		}
	};

	function tancarModalImportacio() {
		if (mostrarConfirmacio ? confirm(tancarModalMsg) : true) {
			window.top.location.reload();
		}
	};

	function mostrarBarraProgres() {
		$progressZip.html(
			'<div class="progressContainer">' +
				'<div class="progress">' +
					'<div class="progress-bar progress-bar-striped active" style="width:0%">0%</div>' +
				'</div>' +
				'<div class="progressText">' + preparantZipMsg + '</div>' +
			'</div>'
		);

		intervalProgres = setInterval(consultarProgreso, 250);
	}

	function consultarProgreso() {
		$.get("<c:url value='/contingut/${command.pareId}/zip/importacio/progres/'/>", function (res) {
			if (!res) return;

			var prog = Math.round(res.progres || 0);
			$('.progress-bar').css('width', prog + '%').html(prog + '%');

			if (res.error) {
				$('.progress-bar').addClass('progress-bar-danger');
				$('.progressText').html(res.errorMsg);
			} else if (res.info && res.info.length) {
				$('.progressText').html(res.info[res.info.length - 1].text);
			}

			if (res.finished) {
				clearInterval(intervalProgres);
				mostrarResultatFinal(res);
				mostrarConfirmacio = false;
				webutilModalAdjustHeight();
			}
		});
	}

	function mostrarResultatFinal(res) {
		$btnCancelarProces.hide();

		var html =
			'<h5><strong>' + resultatProcesTitol + '</strong></h5>' +
			'<div class="alert alert-success"><ul>' +
				'<li>' + resultatProcesTotal + ' <strong>' + res.numOperacions + '</strong></li>' +
				'<li>' + resultatProcesTotalOk + ' <strong>' + res.documentsCorrectes + '</strong></li>' +
				'<li>' + resultatProcesTotalError + ' <strong>' + res.documentsError + '</strong></li>' +
				'<li>' + resultatProcesFirmaError + ' <strong>' + res.documentsFirmaError + '</strong></li>' +
				'<li>' + resultatProcesTotalCarpetes + ' <strong>' + res.carpetesCreades + '</strong></li>' +
				'<li>' + resultatProcesTamany + ' <strong>' + formatBytes(res.tamanyTotal) + '</strong></li>' +
			'</ul></div>';

		if (res.errorsDetall && res.errorsDetall.length) {
			html +=
				'<h5><strong>' + resultatProcesErrorTitol + '</strong></h5>' +
				'<div class="alert alert-danger"><ul>' +
				$.map(res.errorsDetall, function (err) {
					return '<li>' + err + '</li>';
				}).join('') +
				'</ul></div>';
		}

		$progressZip.html(html);
	}

	function mostrarBarraProgresExistent() {
		$.get("<c:url value='/contingut/${command.pareId}/zip/importacio/progres/'/>", function (res) {
			if (res && !res.finished) {
				processarFormulari();
				ocultarFormulari();
				mostrarBarraProgres();
			}
		});
	}

	function formatBytes(bytes) {
		if (!bytes) return '0 B';
		var sizes = ['B', 'KB', 'MB', 'GB'];
		var i = Math.floor(Math.log(bytes) / Math.log(1024));
		return (bytes / Math.pow(1024, i)).toFixed(2) + ' ' + sizes[i];
	}
</script>

</head>

<body>

	<c:set var="formAction">
	    <rip:modalUrl value="${action}"/>
	</c:set>
	
	<div class="esborranys alert alert-info">
	    <spring:message code="contingut.boto.crear.document.multiple.info"/>
	</div>
	
	<div class="progressZip"></div>
	
	<form:form action="${formAction}"
	           method="post"
	           cssClass="form-horizontal"
	           commandName="command"
	           enctype="multipart/form-data">
	
	    <form:hidden path="tascaId"/>
	    <form:hidden path="metaExpedientId"/>
	
		<div class="form-content">
		    <rip:inputFile name="arxiuZip"
		                   textKey="contingut.document.zip.form.camp.arxiu"
		                   required="true"/>
		</div>
	    <div id="modal-botons" class="well">
	
	        <button type="button"
	                id="cancelarProcessarDocumentsBtn"
	                onclick="cancelarProcessamentZip();"
	                class="btn btn-danger">
	            <spring:message code="comu.boto.cancelar"/>
	        </button>
	
	        <button type="button"
	                id="processarDocumentsBtn"
	                class="btn btn-success">
	            <spring:message code="comu.boto.processar"/>
	        </button>
	
	        <button type="button"
	        		id="cancelarBtn"
	                onclick="tancarModalImportacio();"
	                class="btn btn-default">
	            <spring:message code="comu.boto.tanca"/>
	        </button>
	
	    </div>
	</form:form>

</body>
</html>
