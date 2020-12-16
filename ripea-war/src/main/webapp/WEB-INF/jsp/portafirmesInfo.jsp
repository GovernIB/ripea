<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="isTasca" value="${not empty tascaId}"/>
<html>
<head>
	<title><spring:message code="firma.info.titol"/></title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<rip:modalHead/>
<style type="text/css">

.buttons_container {
	border: 1px solid #ddd;
	margin: 1% 0 1% 0;
	padding: 1%;
	text-align: right;
}

.portafirmes_container {
	display: flex;
	justify-content: center;
}

.table_container {
	width: 50%;
	display: flex;
	justify-content: center;
}

.table_container_full {
	width: 100%;
	display: flex;
	justify-content: center;
}

.blocks_container {
	width: 50%;
	display: flex;
	justify-content: center;
	margin-left: 1%;
}

.signers_container {
	width: 75%;
	display: flex;
	flex-flow: column;
	align-items: center;
	padding: 2%;
	margin-bottom: 1%;
}

.block_container {
	width: 100%;
	display: flex;
	flex-flow: wrap;
	justify-content: center;
	border: 2px solid #1f20fd;
	align-items: center;
	border-radius: 4px;
	padding: 1%;
}

.signer_container {
	display: flex;
	border-radius: 4px;
	padding: 2%;
	margin: 1%;
	border: 1px solid #2FF000;
}

.pendent_color {
	background-color: #FFFFFF;
}

.proces_color {
	background-color: #C0F7FE;
}

.signed_color {
	background-color: #BBFFBB;
}

.leyenda {
	width: 25%;
	height: auto;
	display: flex;
	flex-flow: column;
	margin: 2%;
}

.leyenda_title {
	font-weight: bold;
}

.leyenda_container {
	height: auto;
	display: flex;
	flex-flow: column;
	border: 1px solid #c7f1f1;
	border-radius: 6px;
	padding: 3%;
	font-size: 10px;
}
.leyenda_container div {
	display: flex;
	align-items: center;
}
.leyenda_container > div > span {
	margin-left: 3%;
}

.leyenda_block > div {
	background-color: #1f20fd;
}

.leyenda_pendent > div {
	background-color: #FFFFFF;
	border: 1px solid #2FF000;
}

.leyenda_proces > div {
	background-color: #C0F7FE;
	border: 1px solid #2FF000;
}

.leyenda_firmat > div {
	background-color: #BBFFBB;
	border: 1px solid #2FF000;
}

.leyenda_container > div > div:nth-child(1) {
	width: 10px;
	height: 10px;
}

.block_arrow {
	color: #1f20fd;
	font-size: 10px;
}

.block_start_end {
	background-color: #f5f5f5;
	border: 1px solid #cccccc;
	border-radius: 4px;
	text-align: center;
	padding: 1%;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	let parentIframe = window.frameElement;
	if (parentIframe != null) {
		let idModal = $(parentIframe.closest("[id^='modal_']")).attr('id');
		$('#btn_cancelar').on('click', function(){
			window.parent.addLoading(idModal);
		});
	}
	
	//colors blocs
	var blocs = document.getElementsByClassName('block_container');
	var primerBlocEnProces = false;
	var signedContainer;
	
	$(blocs).each(function(index, data) {
		var $blocActual = $(this);
		var hasBlocFirmaPendent = false;
		if (!primerBlocEnProces) {
			var signatures = $blocActual.children();
			$(signatures).each(function(index, data) {
				var $signaturaActual = $(this);
				//firmat
				if ($signaturaActual.hasClass('signed_true')) {
					$signaturaActual.addClass('signed_color');
				}
				//pendent
				if (index >= 0 && $signaturaActual.hasClass('signed_false')) {
					var nextSigner = $signaturaActual;
					$(nextSigner).addClass("proces_color");
					hasBlocFirmaPendent = true;
				}
				if ((index === signatures.length - 1) && hasBlocFirmaPendent && !primerBlocEnProces) {
					primerBlocEnProces = true;
				}
			});
		}
	});
});
</script>
</head>
<body>
	<c:choose>
	<c:when test="${document.estat == 'FIRMAT' && document.gesDocFirmatId != null }">
		<c:if test="${portafirmes.error}">
			<div class="alert well-sm alert-danger alert-dismissable">
				<span class="fa fa-exclamation-triangle"></span>

				<spring:message code="firma.info.errors.processament"/>
					
				<a href="../portafirmes/reintentar" class="btn btn-xs btn-default pull-right">
					<span class="fa fa-refresh"></span>
					<spring:message code="firma.info.errors.reintentar"/>
				</a>
			</div>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h4 class="panel-title"><spring:message code="firma.info.error.custodiar"/></h4>
				</div>
				<div class="panel-body">
					<br/>
					<dl class="dl-horizontal">
						<dt><spring:message code="firma.info.camp.error.data.darrer"/></dt>
						<dd><fmt:formatDate value="${portafirmes.intentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
						<dt><spring:message code="firma.info.camp.error.intents"/></dt>
						<dd>${portafirmes.intentNum}</dd>
					</dl>
					<pre style="height:300px; margin: 12px">${portafirmes.errorDescripcio}</pre>
				</div>
			</div>
		</c:if>	
	</c:when>
	<c:otherwise>
	<c:if test="${portafirmes.error}">
		<a href="#errors" class="text-danger" aria-controls="errors" role="tab" data-toggle="tab"><span class="fa fa-exclamation-triangle"></span> <spring:message code="firma.info.pipella.errors"/></a>
	</c:if>
	<br/>
		<div class="tab-content">
			<div class="tab-pane active in" id="dades" role="tabpanel">
				<div class="portafirmes_container">
					<div class="${not empty blocks ? 'table_container' : 'table_container_full'}">
						<table class="table table-striped table-bordered">
						<tbody>
							<tr>
								<td><strong><spring:message code="firma.info.camp.document"/></strong></td>
								<td>${portafirmes.document.nom}</td>
							</tr>
							<tr>
								<td><strong><spring:message code="firma.info.camp.assumpte"/></strong></td>
								<td>${portafirmes.assumpte}</td>
							</tr>
							<tr>
								<td><strong><spring:message code="firma.info.camp.data.enviament"/></strong></td>
								<td><fmt:formatDate value="${portafirmes.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
							</tr>
							<tr>
								<td><strong><spring:message code="firma.info.camp.estat"/></strong></td>
								<td><spring:message code="portafirmes.estat.enum.${portafirmes.estat}"/></td>
							</tr>
							<tr>
								<td><strong><spring:message code="firma.info.camp.prioritat"/></strong></td>
								<td><spring:message code="portafirmes.prioritat.enum.${portafirmes.prioritat}"/></td>
							</tr>
							<%-- 
							<tr>
								<td><strong><spring:message code="firma.info.camp.data.cad"/></strong></td>
								<td><fmt:formatDate value="${portafirmes.caducitatData}" pattern="dd/MM/yyyy"/></td>
							</tr>
							--%>
							<tr>
								<td><strong><spring:message code="firma.info.camp.document.tipus"/></strong></td>
								<td>${portafirmes.documentTipus}</td> 
							</tr>
							<c:if test="${not empty portafirmes.responsables}">
								<tr>
									<td><strong><spring:message code="firma.info.camp.responsables"/></strong></td>
									<td><c:forEach var="responsable" items="${portafirmes.responsables}" varStatus="loop">${responsable}${!loop.last ? ',' : ''}</c:forEach></td>
								</tr>
							</c:if>
							<c:if test="${not empty portafirmes.fluxTipus}">
								<tr>
									<td><strong><spring:message code="firma.info.camp.flux.tipus"/></strong></td>
									<td><spring:message code="metadocument.fluxtip.enum.${portafirmes.fluxTipus}"/></td>
								</tr>
							</c:if>
							<c:if test="${not empty portafirmes.sequenciaTipus}">
								<tr>
									<td><strong><spring:message code="firma.info.camp.flux.seq"/></strong></td>
									<td><spring:message code="metadocument.seqtip.enum.${portafirmes.sequenciaTipus}"/></td>
								</tr>
							</c:if>
							<c:if test="${not empty portafirmes.portafirmesId}">
								<tr>
									<td><strong><spring:message code="firma.info.camp.portafirmes.id"/></strong></td>
									<td>${portafirmes.portafirmesId}</td>
								</tr>
							</c:if>
						</tbody>
						</table>
					</div>
					<c:if test="${not empty blocks}">
						<div class="blocks_container">
							<div class="signers_container">
								<div class="block_start_end"><spring:message code="firma.info.accio.flux.inici"/></div>
								<i class="block_arrow fa fa-ellipsis-v"></i>
								<c:forEach items="${blocks}" var="block" varStatus="status">
									<div class="block_container">
										<c:forEach items="${block.signers}" var="signer">
											<div class="signer_container signed_${signer.signed}">
												<c:choose>
													<c:when test="${not emptysigner.signerNom}">
															${signer.signerNom} <c:if test="${signer.signerCodi != ' '}">(${signer.signerCodi})</c:if>
													</c:when>
													<c:otherwise>
														${signer.signerCodi}
													</c:otherwise>
												</c:choose><br>
												${signer.signerId}
											</div>
										</c:forEach>
									</div>
									<c:if test="${!status.last}">
										<i class="block_arrow fa fa-arrow-down"></i>
									</c:if>
								</c:forEach>
								<i class="block_arrow fa fa-ellipsis-v"></i>
								<div class="block_start_end"><spring:message code="firma.info.accio.flux.final"/></div>
							</div>
							<div class="leyenda">
								<div class="leyenda_title"><span><spring:message code="firma.info.accio.flux.llegenda"/></span></div>
								<div class="leyenda_container">
									<div class="leyenda_block">
										<div></div><span><spring:message code="firma.info.accio.flux.bloc"/></span>
									</div>
									<div class="leyenda_firmat">
										<div></div><span><spring:message code="firma.info.accio.flux.firma.finalitzada"/></span>
									</div>
									<div class="leyenda_proces">
										<div></div><span><spring:message code="firma.info.accio.flux.firma.proces"/></span>
									</div>
									<div class="leyenda_pendent">
										<div></div><span><spring:message code="firma.info.accio.flux.firma.pendent"/></span>
									</div>
								</div>
							</div>
						</div>
					</c:if>
				</div>
				<div class="buttons_container">
					<c:if test="${portafirmes.estat == 'ENVIAT'}">
						<c:choose>
							<c:when test="${isTasca}">
								<a id="btn_cancelar" href="<rip:modalUrl value="/usuariTasca/${tascaId}/document/${portafirmes.document.id}/portafirmes/cancel"/>" data-confirm="<spring:message code="firma.info.accio.cancel.confirmacio"/>" class="btn btn-default"><span class="fa fa-times"></span> <spring:message code="firma.info.accio.cancel"/></a>
							</c:when>
							<c:otherwise>
								<a id="btn_cancelar" href="<rip:modalUrl value="/document/${portafirmes.document.id}/portafirmes/cancel"/>" data-confirm="<spring:message code="firma.info.accio.cancel.confirmacio"/>" class="btn btn-default"><span class="fa fa-times"></span> <spring:message code="firma.info.accio.cancel"/></a>
							</c:otherwise>
						</c:choose>
					</c:if>
				</div>
			</div>
			<%--div class="tab-pane" id="annexos" role="tabpanel">
			</div--%>
			<div class="tab-pane" id="errors" role="tabpanel">
				<c:if test="${portafirmes.error}">
					<div class="alert well-sm alert-danger alert-dismissable">
						<span class="fa fa-exclamation-triangle"></span>
						<c:choose>
							<c:when test="${portafirmes.estat == 'PENDENT'}">
								<spring:message code="firma.info.errors.enviament"/>
							</c:when>
							<c:when test="${portafirmes.estat == 'ENVIAT'}">
								<spring:message code="firma.info.errors.processament"/>
							</c:when>
							<c:when test="${portafirmes.estat == 'PROCESSAT'}">
							</c:when>
							<c:when test="${portafirmes.estat == 'REBUTJAT'}">
								${portafirmes.motiuRebuig}
							</c:when>
							<c:when test="${portafirmes.estat == 'CANCELAT'}">
								<spring:message code="firma.info.errors.cancelacio"/>
							</c:when>
						</c:choose>
						<a href="../portafirmes/reintentar" class="btn btn-xs btn-default pull-right">
							<span class="fa fa-refresh"></span>
							<spring:message code="firma.info.errors.reintentar"/>
						</a>
					</div>
					<div class="panel panel-default">
						<div class="panel-heading">
							<h4 class="panel-title"><spring:message code="firma.info.error.enviament"/></h4>
						</div>
						<div class="panel-body">
							<br/>
							<dl class="dl-horizontal">
								<dt><spring:message code="firma.info.camp.error.data.darrer"/></dt>
								<dd><fmt:formatDate value="${portafirmes.intentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
								<dt><spring:message code="firma.info.camp.error.intents"/></dt>
								<dd>${portafirmes.intentNum}</dd>
							</dl>
							<pre style="height:300px; margin: 12px">${portafirmes.errorDescripcio}</pre>
						</div>
					</div>
				</c:if>
			</div>
		</div>
		</c:otherwise>
	</c:choose>








	<div id="modal-botons" class="well">
		<a href="<c:url value="/contenidor/${portafirmes.document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
