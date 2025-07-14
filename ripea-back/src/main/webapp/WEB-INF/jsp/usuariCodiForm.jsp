<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<rip:modalHead/>
	<title><spring:message code="decorator.menu.canvi.usuari.codi"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script type="application/javascript">

		var usuariActualCodi = '${usuariCodiCommand.usuariActualCodi}';
	
		$(document).ready(function() {
			$('#enviarFormCodis').click(function () {
				processCodis();
			});
		});

		function addContentToProgress(lineRow) {
			var documentHeight = 200;
			if (lineRow) {
				$('#change-process').append(lineRow);
				documentHeight = Math.max(document.body.scrollHeight, document.documentElement.scrollHeight)+10;
			} else {
				$('#change-process').empty();
				documentHeight = document.forms[0].scrollHeight+10;
			}
			
			window.frameElement.parentNode.style.height=documentHeight+"px";
			window.frameElement.style.height=documentHeight+"px";
		}
		
		function processCodis() {
			// Deshabilitar els botons mentre es processa
			toggleButtons(false);

			// Obtenir les línies del "textarea"
			const lines = $('#usuarisBatch').val().split('\n').filter(line => line.trim() !== '');
			addContentToProgress();

			// Processar línia a línia
			const processNextLine = (index) => {
				if (index >= lines.length) {
					toggleButtons(true); // Habilitar els botons un cop acabat el processament
					return; // Finalitzar si totes les línies ja s'han processat
				}

				const line = lines[index];
				const lineRow = $('<div class="col-xs-12 row mb-2"></div>');
				const parseResult = /(.+)=(.+)/.exec(line);

				if (!parseResult) {
					// Format incorrecte
					lineRow.html(
							'<div class="col-xs-6 processing-line">' +
							'<span><spring:message code="usuari.codi.processant"/> (' + line + ')</span>' +
							'</div>' +
							'<div class="col-xs-6 result">' +
							'<i class="fa fa-times text-danger"></i> ' +
							'<span class="result-info text-danger"><spring:message code="usuari.codi.format.error"/></span>' +
							'</div>'
					);
					addContentToProgress(lineRow);
					processNextLine(index + 1); // Saltar a la següent línia
					return;
				}

				const [_, codiAntic, codiNou] = parseResult;
				lineRow.html(
						'<div class="col-xs-6 processing-line">' +
						'<span><spring:message code="usuari.codi.processant"/> <strong>' + codiAntic + ' -> ' + codiNou + '</strong></span> ' +
						'</div>' +
						'<div class="col-xs-6 result">' +
						'<i class="fa fa-spinner fa-spin status-icon"></i> ' +
						'</div>' );
				addContentToProgress(lineRow);

				// Validar els codis via AJAX
				$.post('<c:url value="/usuari/username/"/>' + codiAntic + '/validateTo/' + codiNou)
						.done(function (response) {
							if (!response.usuariAnticExists) {
								// Codi antic no existeix
								lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
								lineRow.find('.result').append('<span class="result-info text-danger"><spring:message code="usuari.antic.not.found"/></span>');
								processNextLine(index + 1);
							} else if (usuariActualCodi==codiAntic) {
								lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
								lineRow.find('.result').append('<span class="result-info text-danger"><spring:message code="usuari.antic.es.actual"/></span>');
								processNextLine(index + 1);								
							} else if (response.usuariNouExists) {
								// Codi nou ja existeix
								confirmAction('usuari.nou.exists', codiAntic, codiNou, function () {
									executeCanviCodi(lineRow, codiAntic, codiNou, () => processNextLine(index + 1));
								}, function () {
									lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
									lineRow.find('.result').append('<span class="result-info text-warning"><spring:message code="usuari.canvi.cancelat"/></span>');
									processNextLine(index + 1);
								});
							} else {
								// Validació correcta, realitzar el canvi
								executeCanviCodi(lineRow, codiAntic, codiNou, () => processNextLine(index + 1));
							}
						})
						.fail(function () {
							lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
							lineRow.find('.result').append('<span class="result-info text-danger"><spring:message code="usuari.codi.format.error"/></span>');
							processNextLine(index + 1);
						});
			};

			// Inicia el processament per la primera línia
			processNextLine(0);
		}

		function executeCanviCodi(lineRow, codiAntic, codiNou, callback) {
			$.post('<c:url value="/usuari/username/"/>' + codiAntic + '/changeTo/' + codiNou)
					.done(function (response) {
						lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass(response.estat === 'OK' ? 'fa-check text-success' : 'fa-times text-danger');
						lineRow.find('.result').append('<span class="badge">Durada: ' + response.duracio + ' ms</span>');

						if (response.estat === 'OK') {
							lineRow.find('.result').append(' <span class="result-info text-success">' + response.registresModificats + ' <spring:message code="usuari.codi.processats"/></span>');
						} else {
							lineRow.find('.result').append(' <span class="result-info text-danger">' + response.errorMessage + '</span>');
						}
						callback(); // Continuar amb la següent línia
					})
					.fail(function () {
						lineRow.find('.status-icon').removeClass('fa-spinner fa-spin').addClass('fa-times text-danger');
						lineRow.find('.result').append(`<span class="result-info text-danger">Error al processar la línia</span>`);
						callback(); // Continuar amb la següent línia
					});
		}

		function confirmAction(messageCode, codiAntic, codiNou, onContinue, onCancel) {
			const modal = $('<div class="modal" tabindex="-1" role="dialog"></div>');
			modal.html(
					'<div class="modal-dialog" role="document">' +
					'<div class="modal-content">' +
					'<div class="modal-header">' +
					'<h5 class="modal-title"><spring:message code="usuari.nou.exists"/> (' + codiAntic + ' -> ' + codiNou + ')</h5>' +
					'</div>' +
					'<div class="modal-body">' +
					'<p><spring:message code="usuari.nou.exists.detall"/></p>' +
					'</div>' +
					'<div class="modal-footer">' +
					'<button type="button" class="btn btn-primary confirm-continue"><spring:message code="comu.boto.continuar"/></button>' +
					'<button type="button" class="btn btn-secondary confirm-cancel" data-dismiss="modal"><spring:message code="comu.boto.cancelar"/></button>' +
					'</div>' +
					'</div>' +
					'</div>'
			);

			$('body').append(modal);
			modal.modal('show');

			modal.find('.confirm-continue').click(function () {
				modal.modal('hide');
				modal.remove();
				if (onContinue) onContinue();
			});

			modal.find('.confirm-cancel').click(function () {
				modal.modal('hide');
				modal.remove();
				if (onCancel) onCancel();
			});
		}

		function toggleButtons(enable) {
			$(window.frameElement.parentNode.parentNode).find('.modal-footer > a').attr('disabled', !enable);
			$(window.frameElement.parentNode.parentNode).find('.modal-footer > button').attr('disabled', !enable);
// 			$('div .modal-footer > button').attr('disabled', !enable);
// 			$('div .modal-footer > a').attr('disabled', !enable)
		}

	</script>
	<style>
		/* Capa semitransparent */
		#loading-overlay {
			position: fixed;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background-color: rgba(0, 0, 0, 0.1);
			z-index: 9999; /* Assegura que estigui per sobre de tot */
			display: flex;
			justify-content: center;
			align-items: center;
		}

		#loading-overlay .fa-spinner {
			color: white; /* Color blanc per al spinner */
		}
	</style>	
</head>
<body>
	<c:url value="/usuari/username" var="formAction"/>
	<form:form action="${formAction}" method="post" cssClass="form-horizontal" modelAttribute="usuariCodiCommand" role="form">
		<div class="row">
			<div class="col-md-12">
				<rip:inputTextarea name="usuarisBatch" textKey="usuari.form.camp.antic" comment="usuari.codi.form.text"/>
			</div>
		</div>
		<div id="change-process"></div>
		<div id="modal-botons" class="well">
			<button type="button" id="enviarFormCodis" class="btn btn-success"><spring:message code="comu.boto.executar"/></button>
			<a href="<c:url value="/contenidor/${document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>