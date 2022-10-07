<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="unitat.synchronize.dialog.header" /></c:set>
<c:set var="isAllEmpty" value="${empty substMap and empty splitMap and empty mergeMap and empty unitatsVigents and empty unitatsNew}" />

<html>
<head>
	<title>${titol}</title>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet" />
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet" />
	<link href="<c:url value="/css/horizontal-tree.css"/>" rel="stylesheet" />
    <link rel="stylesheet" href="<c:url value="/css/sync.css"/>">
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<rip:modalHead />
	<script>
		var itervalProgres;
		var finished = false;
		var writtenBlocs = 0;
		var title="<spring:message code="organgestor.actualitzacio.titol"/>";
		var content="<spring:message code="organgestor.actualitzacio.cancelarActu"/>";
		var acceptar="<spring:message code="comu.boto.acceptar"/>";
		var cancelar="<spring:message code="comu.boto.cancelar"/>";
		var tancar="<spring:message code="comu.boto.tancar"/>";
		var lnouorgan="<spring:message code="organgestor.actualitzacio.nou"/>";
		var lcodi="<spring:message code="organgestor.actualitzacio.codi"/>";
		var lestat="<spring:message code="organgestor.actualitzacio.estat"/>";
		var lnom="<spring:message code="metaexpedient.form.camp.nom"/>";
		var ldesc="<spring:message code="metaexpedient.form.camp.descripcio"/>";
		var lcomu="<spring:message code="metaexpedient.form.camp.comu"/>";
		var lorgan="<spring:message code="metaexpedient.form.camp.organgestor"/>";
		var sense="<spring:message code="metaexpedient.actualitzacio.sense.canvis"/>";
		var lestats = [];
		lestats["V"]="<spring:message code="organgestor.estat.enum.V"/>";
		lestats["E"]="<spring:message code="organgestor.estat.enum.E"/>";
		lestats["A"]="<spring:message code="organgestor.estat.enum.A"/>";
		lestats["T"]="<spring:message code="organgestor.estat.enum.T"/>";

		<c:if test="${not isUpdatingProcediments}">
		var isUpdating = false;
		$(document).ready(function() {
			$('#formSync').on("submit", function(){
				console.log("submitting...");
				finished = false;
				$('.loading').fadeIn();
				let alt = $('body').height() - 60;
				$('#actualitzacioInfo').css("height", alt + "px");
				$('#actualitzacioInfo').css("max-height", alt + "px");
				$('#actualitzacioInfo').fadeIn();
				$('.prediccio').fadeOut();
				$('#autobtn', parent.document).prop('disabled', true);
				$('#cancelbtn', parent.document).toggle(true);
				$('#cancelbtn', parent.document).html(cancelar);
				$.post($(this).attr('action'));
				isUpdating = true;
				$('.close', parent.document).on('click', dismissFunction);
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
			itervalProgres =  setInterval(function(){ getProgres(); }, 500);
		}







		
		function getProgres() {
			console.log("getProgres");
			$('.close', parent.document).prop('disabled', true);
			$.ajax({
				type: 'GET',
				url: "<c:url value='/organgestor/update/auto/progres'/>",
				success: function(data) {
					if (data) {
						console.log("Progres:", data);
						if (!finished) {
							writeInfo(data);
							$('#cancelbtn', parent.document).toggle(true);
							if (data.finished) {
								finished = true;
								clearInterval(itervalProgres);
								isUpdating = false;
								$('#bar').css('width', '100%');
								$('#bar').attr('aria-valuenow', 100);
								$('#bar').html('100%');
								$('.close', parent.document).prop('disabled', false);
								$('.loading').hide();
								$('.progress').show();
								$('#cancelbtn', parent.document).html(tancar);
							} else {
								if (data.progres > 0) {
									$('.loading').hide();
									$('.progress').show();
									$('#bar').css('width', data.progres + '%');
									$('#bar').attr('aria-valuenow', data.progres);
									$('#bar').html(data.progres + '%');
								} else if (data.progres == 0 && data.numElementsActualitzats == 0) {
									$('.close', parent.document).prop('disabled', false);
									$('.loading').hide();
								}
							}
						}
					}
				},
				error: function() {
					console.log("error obtenint progrés...");
					clearInterval(itervalProgres);
					$('.loading').hide();
					$('.close', parent.document).prop('disabled', false);
				}
			});
		}

		function writeInfo(data) {
			let info = data.info;
			let index;
			console.log("Write info. Written bloks = " + writtenBlocs + ", info bloks = " + info.length);
			let scroll = writtenBlocs < info.length;
			for (index = writtenBlocs; index < info.length; index++) {
				// $("#bcursor").before("<p class='info-" + info[index].tipus + "'>" + info[index].text + "</p>");
				let blocContent = '';
				if (info[index].organ) {
					if (info[index].isNew) {
						blocContent += '<h5>' + lnouorgan + ':</h5>';
						blocContent = '<ul>';
						// blocContent += '<li><strong>' + lcodi + ':</strong>' + info[index].codiOrgan +'</li>';
						blocContent += '<li><strong>' + lnom + ':</strong>' + info[index].nomNou +'</li>';
						blocContent += '<li><strong>' + lestat + ':</strong>' + lestats[info[index].estatNou] +'</li>';
						blocContent += '</ul>';
					} else {
						blocContent = '<ul>';
						// blocContent += '<li><strong>' + lcodi + ':</strong>' + info[index].codiOrgan +'</li>';
						blocContent += '<li><strong>' + lnom + ':</strong>' + info[index].nomAntic + ' <span class="fa fa-long-arrow-right fa-2x"></span> ' + info[index].nomNou +'</li>';
						blocContent += '<li><strong>' + lestat + ':</strong>' + lestats[info[index].estatAntic] + ' <span class="fa fa-long-arrow-right fa-2x"></span> ' + lestats[info[index].estatNou] +'</li>';
						blocContent += '</ul>';
					}
				} else if (info[index].hasError) {
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
						'<div class="panel ' + (info[index].infoClass ? info[index].infoClass : (info[index].hasError ? 'panel-danger' : info[index].hasCanvis ? 'panel-info' : 'panel-default')) + '">' +
						'  <div class="panel-heading">' +
						'    <h3 class="panel-title">' + (info[index].infoTitol ? info[index].infoTitol : info[index].isOrgan ? "Òrgan gestor: " + info[index].codiOrgan + ' - ' + info[index].nomAntic : 'Procediment: ' + info[index].codiSia + ' - ' + info[index].nomAntic) + '</h3>' +
						'  </div>' +
						'  <div class="panel-body">' + blocContent + '</div>' +
						'</div>');
			}
			writtenBlocs = index;
			if (data.error) {
				$("#bcursor").before("<p class='info-ERROR'>" + data.errorMsg + "</p>");
			}
			//scroll to the bottom of "#actualitzacioInfo"
			// let scroll = writtenBlocs < info.length;
			console.log("Scroll: " + scroll);
			if (scroll) {
				var infoDiv = document.getElementById("actualitzacioInfo");
				infoDiv.scrollTop = infoDiv.scrollHeight;
			}
		}
		function dismissFunction() {
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

	<div class="panel-group prediccio">
	
		<!-- If this is first sincronization it shows all currently vigent unitats that will be created in db  -->
		<c:if test="${isFirstSincronization}">
			<div class="panel panel-default">
				<div class="panel-heading">
					<spring:message
						code="unitat.synchronize.prediction.firstSincroHeader" />
				</div>
				<div class="panel-body">

					<c:if test="${empty unitatsVigents}">
						<spring:message code="unitat.synchronize.prediction.firstSincroNoUnitatsVigent" />
					</c:if>

					<c:if test="${!empty unitatsVigents}">
						<c:forEach var="unitatVigentFirstSincro" items="${unitatsVigents}">

							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-success border-green overflow-ellipsis create-label"></span>
									<div class="branch lv1">
										<div class="entry sole">
											<span class="label bg-success border-green overflow-ellipsis" title="${unitatVigentFirstSincro.codi} - ${unitatVigentFirstSincro.denominacio}">
													${unitatVigentFirstSincro.codi} - ${unitatVigentFirstSincro.denominacio}
											</span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</c:if>
		<c:if test="${!isFirstSincronization}">

			<!-- If unitats didn't change from the last time of synchronization show message: no changes -->
			<c:if test="${isAllEmpty}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.noChanges" />
					</div>
					<div class="panel-body">
						<spring:message code="unitat.synchronize.prediction.upToDate" />
					</div>
				</div>
			</c:if>

			<!-- If they exist show unitats that splited  (e.g. unitat A splits to unitats B and C) -->
			<c:if test="${!empty splitMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.splits" />
					</div>
					<div class="panel-body">
						<c:forEach var="splitMap" items="${splitMap}">
							<c:set var="key" value="${splitMap.key}" />
							<c:set var="values" value="${splitMap.value}" />
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-danger border-red overflow-ellipsis"
										title="${key.codi} - ${key.denominacio}"> ${key.codi} -
										${key.denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<div class="entry">
												<span
													class="label bg-success border-green overflow-ellipsis"
													title="${value.codi} - ${value.denominacio}">${value.codi}
													- ${value.denominacio}</span>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>


			<!-- If they exist show unitats that merged (e.g. unitats D and E merge to unitat F) -->
			<c:if test="${!empty mergeMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.merges" />
					</div>
					<div class="panel-body">
						<c:forEach var="mergeMap" items="${mergeMap}">
							<c:set var="key" value="${mergeMap.key}" />
							<c:set var="values" value="${mergeMap.value}" />
							<div class=horizontal-right>
								<div id="wrapper">
									<span
										class="label bg-success border-green right-postion-20 overflow-ellipsis"
										title="${key.codi} - ${key.denominacio}"> ${key.codi} -
										${key.denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<div class="entry">
												<span class="label bg-danger border-red overflow-ellipsis"
													title="${value.codi} - ${value.denominacio}">
													${value.codi} - ${value.denominacio} </span>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
			
			
			<!-- If they exist show unitats that were substituted by the others  (e.g. unitat G is substituted by unitat H) -->
			<c:if test="${!empty substMap}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.substitucions" />
					</div>
					<div class="panel-body">
						<c:forEach var="substMap" items="${substMap}">
							<c:set var="key" value="${substMap.key}" />
							<c:set var="values" value="${substMap.value}" />
							<div class=horizontal-right>
								<div id="wrapper">
									<span
										class="label bg-success border-green right-postion-20 overflow-ellipsis"
										title="${key.codi} - ${key.denominacio}"> ${key.codi} -
										${key.denominacio} </span>
									<div class="branch lv1">
										<c:forEach var="value" items="${values}">
											<div class="entry sole">
												<span class="label bg-danger border-red overflow-ellipsis"
													title="${value.codi} - ${value.denominacio}">
													${value.codi} - ${value.denominacio} </span>
											</div>
										</c:forEach>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>			

			<!-- If they exist show unitats that only had some of their properties changed -->
			<c:if test="${!empty unitatsVigents}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.atributesChanged" />
					</div>
					<div class="panel-body">
						<c:forEach var="unitatVigent" items="${unitatsVigents}">

							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-success border-green overflow-ellipsis" title="${unitatVigent.codi} - ${unitatVigent.denominacio}">
										${unitatVigent.codi} - ${unitatVigent.denominacio}
									</span>
									<div class="branch lv1">
										<div class="entry sole">
											<span class="label bg-warning border-yellow overflow-ellipsis" title="${unitatVigent.codi} - ${unitatVigent.denominacio}">
												${unitatVigent.codi} - ${unitatVigent.denominacio}
											</span>
										</div>
									</div>
								</div>
							</div>

						</c:forEach>
					</div>
				</div>
			</c:if>
			
			<!-- If they exist show unitats that are new (are not transitioned from any other unitat) -->
			<c:if test="${!empty unitatsNew}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.noves" />
					</div>
					<div class="panel-body">
						<c:forEach var="unitatNew" items="${unitatsNew}">
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-success border-green overflow-ellipsis create-label"></span>
									<div class="branch lv1">
										<div class="entry sole">
											<span class="label bg-success border-green overflow-ellipsis" title="${unitatNew.codi} - ${unitatNew.denominacio}">
												${unitatNew.codi} - ${unitatNew.denominacio}
											</span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>

			<!-- If they exist show unitats that are extint (are not vigent and has not any transition to any other unitat) -->
			<c:if test="${!empty unitatsExtingides}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<spring:message code="unitat.synchronize.prediction.extingides" />
					</div>
					<div class="panel-body">
						<c:forEach var="unitatExtingida" items="${unitatsExtingides}">
							<div class=horizontal-left>
								<div id="wrapper">
									<span class="label bg-danger border-red right-postion-20 overflow-ellipsis" title="${unitatExtingida.codi} - ${unitatExtingida.denominacio}">
										${unitatExtingida.codi} - ${unitatExtingida.denominacio}
									</span>
									<div class="branch lv1">
										<div class="entry sole remove">
											<span class="label bg-success border-green overflow-ellipsis remove-label"></span>
										</div>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</c:if>

	</div>

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

	<c:set var="formAction">
		<rip:modalUrl value="/organgestor/saveSynchronize" />
	</c:set>
	<form:form id="formSync" action="${formAction}" method="post" cssClass="form-horizontal" role="form">
		<div id="modal-botons">
			<button id="autobtn" type="submit" class="btn btn-success" data-noloading="true"
				<c:if test="${isAllEmpty and !isFirstSincronization}"><c:out value="disabled='disabled'"/></c:if>>
				<span class="fa fa-save"></span>
				<spring:message code="unitat.list.boto.synchronize" />
			</button>
			<a id="cancelbtn" href="<c:url value="/organgestor"/>" class="btn btn-default" onclick="dismissFunction()" data-modal-cancel="false"><spring:message code="comu.boto.cancelar" /></a>
		</div>
	</form:form>

</body>
</html>
