<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="metaexpedient.actualitzacio.titol"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.css">
	<link rel="stylesheet" href="<c:url value="/css/sync.css"/>">
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.js"></script>
	<rip:modalHead/>

	<script>
		var itervalProgres;
		var writtenBlocs = 0;
		var title="<spring:message code="metaexpedient.actualitzacio.titol"/>";
		var content="<spring:message code="metaexpedient.actualitzacio.cancelarActu"/>";
		var acceptar="<spring:message code="comu.boto.acceptar"/>";
		var cancelar="<spring:message code="comu.boto.cancelar"/>";
		var lnom="<spring:message code="metaexpedient.form.camp.nom"/>";
		var ldesc="<spring:message code="metaexpedient.form.camp.descripcio"/>";
		var lcomu="<spring:message code="metaexpedient.form.camp.comu"/>";
		var lorgan="<spring:message code="metaexpedient.form.camp.organgestor"/>";
		var sense="<spring:message code="metaexpedient.actualitzacio.sense.canvis"/>";
		<c:if test="${not isUpdatingProcediments}">
			var isUpdating = false;
			$(document).ready(function() {
				$('#formUpdateAuto').on("submit", function(){
					console.log("submitting...");
					$('.loading').fadeIn();
					$('#actualitzacioInfo').fadeIn();
					$('.confirmacio').fadeOut();
					$('.autobtn', parent.document).prop('disabled', true);
					$('#cancelbtn', parent.document).toggle(true);
					$.post($(this).attr('action'));
					isUpdating = true;
					refreshProgres();
					return false;
				});
				$('.close', parent.document).on('click',function(){
					$.confirm({
						title: title,
						content: content,
						buttons: {
							confirm: {
								text: acceptar,
								action: function () {
									window.top.location.reload();
								}
							},
							cancel: {
								text: cancelar,
								action: function () {
								}
							}
						}
					});
				});
			});
		</c:if>
		function refreshProgres() {
			console.log("refreshProgres");
			itervalProgres =  setInterval(function(){ getProgres(); }, 250);
		}

		function getProgres() {
			console.log("getProgres");
			$('.close', parent.document).prop('disabled', true);
			$.ajax({
				type: 'GET',
				url: "<c:url value='/metaExpedient/sincronitzar/progres'/>",
				success: function(data) {
					if (data) {
						console.log("Progres:", data);
						writeInfo(data);
						$('#cancelbtn', parent.document).toggle(true);
						if (data.progres == 100) {
							clearInterval(itervalProgres);
							isUpdating = false;
							$('#bar').css('width', '100%');
							$('#bar').attr('aria-valuenow', 100);
							$('#bar').html('100%');
// 							$('.modal-footer', parent.document).show();
							$('.close', parent.document).prop('disabled', false);
							$('.loading').hide();
						} else {
							if (data.progres > 0) {
								$('.loading').hide();
								$('.progress').show();
								$('#bar').css('width', data.progres + '%');
								$('#bar').attr('aria-valuenow', data.progres);
								$('#bar').html(data.progres + '%');
							}else if(data.progres == 0 && data.numProcedimentsActualitzats == 0 ){
								$('.close', parent.document).prop('disabled', false);
								$('.loading').hide();
							}
						}
					}
				},
				error: function() {
					console.log("error obtenint progrés...");
					clearInterval(itervalProgres);
					$('.loading').hide();
// 					$('.modal-footer', parent.document).show();
					$('.close', parent.document).prop('disabled', false);
				}
			});
		}

		function writeInfo(data) {
			let info = data.info;
			let index;

			for (index = writtenBlocs; index < info.length; index++) {
				// $("#bcursor").before("<p class='info-" + info[index].tipus + "'>" + info[index].text + "</p>");
				let blocContent = '';
				if (info[index].hasError) {
					blocContent = info[index].errorText;
				} else if (info[index].hasInfo) {
					blocContent = info[index].infoText;
				} else if (!info[index].hasCanvis) {
					blocContent = sense;
				} else {
					blocContent = '<ul>';
					if (info[index].nomModificat) {
						blocContent += '<li><strong>' + lnom + ':</strong>' + info[index].nomAntic + ' <span class="fa fa-long-arrow-right fa-2x"></span> ' + info[index].nomNou +'</li>';
					}
					if (info[index].descripcioModificada) {
						blocContent += '<li><strong>' + ldesc + ':</strong>' + info[index].descripcioAntiga + ' <span class="fa fa-long-arrow-right fa-2x"></span> ' + info[index].descripcioNova +'</li>';
					}
					if (info[index].comuModificat) {
						blocContent += '<li><strong>' + lcomu + ':</strong> <span class="fa ' + (info[index].comuAntic ? 'fa-check' : 'fa-times') + '"></span> <span class="fa fa-long-arrow-right fa-2x"></span> <span class="fa ' + (info[index].comuNou? 'fa-check' : 'fa-times') +'"></span></li>';
					}
					if (info[index].organModificat) {
						blocContent += '<li><strong>' + lorgan + ':</strong>' + info[index].organAntic + ' <span class="fa fa-long-arrow-right fa-2x"></span> ' + info[index].organNou +'</li>';
					}
					blocContent += '</ul>';
				}
				$("#bcursor").before(
						'<div class="panel ' + (info[index].hasError ? 'panel-danger' : info[index].hasCanvis ? 'panel-info' : 'panel-default') + '">' +
						'  <div class="panel-heading">' +
						'    <h3 class="panel-title">' + (info[index].hasInfo ? info[index].infoTitol : 'Procediment: ' + info[index].codiSia + ' - ' + info[index].nomAntic) + '</h3>' +
						'  </div>' +
						'  <div class="panel-body">' + blocContent + '</div>' +
						'</div>');
			}
			writtenBlocs = index;
			if (data.error) {
				$("#bcursor").before("<p class='info-ERROR'>" + data.errorMsg + "</p>");
			}
			//scroll to the bottom of "#actualitzacioInfo"
			let scroll = writtenBlocs < info.length;
			if (scroll) {
				var infoDiv = document.getElementById("actualitzacioInfo");
				infoDiv.scrollTop = infoDiv.scrollHeight;
			}
		}
		function cancela() {
			if (!isUpdating) {
				window.top.location.reload();
				return;
			}
			$.confirm({
				title: title,
			    content: content,
			    buttons: {
			    	confirm: {
			            text: acceptar,
			            action: function () {
			            	window.top.location.reload();
			            }
			        },
			        cancel: {
			            text: cancelar,
			            action: function () {
			            }
			        }
			    }
			});
		}

	</script>
</head>

<body>
	<c:if test="${not isUpdatingProcediments}">
		<div class="confirmacio">
			<h4><spring:message code="metaexpedient.actualitzacio.confirmacio"/></h4>
		</div>

		<c:set var="formAction"><rip:modalUrl value="/metaExpedient/sincronitzar"/></c:set>
		<form:form id="formUpdateAuto" action="${formAction}" method="post" cssClass="form-horizontal">
			<div class="loading">
				<div class="loading-gif">
					<span class="fa fa-circle-o-notch fa-2x fa-spin fa-fw"></span>
				</div>
			</div>
			<div class="progress">
				<div id="bar" class="progress-bar" role="progressbar progress-bar-striped active" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">0%</div>
			</div>
			<div id="actualitzacioInfo" class="info">
				<span id="bcursor" class="blinking-cursor">|</span>
			</div>
			<div id="modal-botons" class="well">
				<button type="submit"
						class="btn btn-success autobtn"
						data-noloading="true">
					<span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.actualitzar"/>
				</button>
				<a id="cancelbtn" href="#"
				   style="display: none !important;" class="btn btn-default"
				   data-modal-cancel="false" onclick="cancela()" ><spring:message code="comu.boto.tancar"/></a>
			</div>
		</form:form>
	</c:if>
	<c:if test="${isUpdatingProcediments}">
		<div class="confirmacio">
			<h4><spring:message code="metaexpedient.actualitzacio.procesActiu"/></h4>
		</div>
	</c:if>
</body>
</html>
