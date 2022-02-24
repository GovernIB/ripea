<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="registre" value="${peticio.registre}"/>
<c:set var="titol"><spring:message code="registre.detalls.titol" arguments="${registre.identificador}"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/webjars/pdf-js/2.5.207/build/pdf.js"/>"></script>
	<rip:modalHead/>
<style>
body {
	min-height: 400px;
}
.tab-content {
    margin-top: 0.8em;
}
.icona-doc {
	color: #666666
}
.file-dt {
	margin-top: 9px;
}
.file-dd {
	margin-top: 3px;
}
tr.odd {
	background-color: #f9f9f9;
}
tr.detall {
/* 	background-color: cornsilk; */
}
tr.clicable {
	cursor: pointer;
}
#resum-annexos .dl-horizontal dt {
    width: 250px;
}

#resum-annexos .dl-horizontal dd {
    margin-left: 280px;
}

#dropAccions ul.dropdown-menu {
	left: auto;
    right: 0;
    margin-right: -10px;
}
#resum-annexos-container {
	display: flex;
}

#resum-viewer {
	display: none;
	margin-left: 1%;
	width: 100%;
}

.invalid-format td {
	cursor: auto !important;
	opacity: 0.4;
}

.invalid-format td:nth-child(7), .invalid-format td:nth-child(8) {
	opacity: 1;
}

#container {
	padding-top: 1%;
}

#resum-annexos > table > tbody td {
	cursor: pointer;
}

.viewer-content {
	width: 100%;
	padding-top: 1% !important;
}

.viewer-content > .dl-horizontal, .viewer-firmes-container > .dl-horizontal {
	margin-bottom: 0;
}

.viewer-firmes hr {
	margin-top: 5px !important;
	margin-bottom: 5px !important;
}

.viewer-padding {
	padding: 0% 2% 0% 2%;
}

.line {
	width: 90px;
	height: 3px;
	background-color: black;
	margin-top: -6px;
}

.rmodal_loading {
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
#avanzarPagina:focus {
	outline:0;
}

#avanzarPagina:focus[aria-pressed="false"] {
	background-color: #fff;
}
</style>
<script type="text/javascript">
	//<![CDATA[
	$(document).ready(function() {
		
		$(".desplegable").click(function(){
			$(this).find("span").toggleClass("fa-caret-up");
			$(this).find("span").toggleClass("fa-caret-down");
		});
 	});
	// ]]>
	var previousAnnex;
	function showViewer(event, annexId, observacions, dataCaptura, origen) {
		if (event.target.cellIndex === undefined || event.target.cellIndex === 6 || event.target.cellIndex === 7) return;
        var resumViewer = $('#resum-viewer');
        var resumAnnexos = $('#resum-annexos');
		// Mostrar/amagar visor
		if (!resumViewer.is(':visible')) {
			resumViewer.slideDown(500);
			resumAnnexos.removeAttr("style");
		} else if (previousAnnex == undefined || previousAnnex == annexId) {
			closeViewer();
			event.srcElement.parentElement.style = "background: #fffff";
    		previousAnnex = annexId;
			return;
		}
		resetBackground();
		event.srcElement.parentElement.style = "background: #f9f9f9";
		previousAnnex = annexId;
		
        // Mostrar contingut capçalera visor
        resumViewer.find('*').not('#container').remove();
        var viewerContent = '<div class="panel-heading"><spring:message code="registre.detalls.pipella.previsualitzacio"/> \
        					 <span class="fa fa-close" style="float: right; cursor: pointer;" onClick="closeViewer()"></span>\
        					 </div>\
        					 <div class="viewer-content viewer-padding">\
        						<dl class="dl-horizontal">\
		        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.eni.data.captura"/>: </dt><dd>' + dataCaptura + '</dd>\
		        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.eni.origen"/>: </dt><dd>' + origen + '</dd>\
		        					<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.observacions"/>: </dt><dd>' + observacions + '</dd>\
	        					</dl>\
        					 </div>';
        resumViewer.prepend(viewerContent);
        
        // Recupera i mostrar contingut firmes
        $.get(
				"<c:url value="/expedientPeticio/firmaInfo/"/>" + annexId + "/content",
				function(firmes) {
					if (firmes && firmes.length > 0) {
						var nieList = "", nomList = "";
						var viewerContent = '<div class="viewer-firmes viewer-padding">\
												<hr>\
					    						<div class="viewer-firmes-container">';
					    firmes.forEach(function(firma) {
	    					nieList += '[';
	    					firma.detalls.forEach(function(firmaDetall, index) {
								if (firmaDetall.responsableNif != undefined && firmaDetall.responsableNif != null)	
									nieList += firmaDetall.responsableNif + (index !== (firma.detalls.length -1) ? ', ' : '');
								if (firmaDetall.responsableNom != undefined && firmaDetall.responsableNom != null)
									nomList += firmaDetall.responsableNom + (index !== (firma.detalls.length -1) ? ', ' : '');
								if (firmaDetall.responsableNif == null && firma.autofirma != null)
									nieList += '<spring:message code="registre.annex.detalls.camp.firma.autoFirma"/> <span class="fa fa-info-circle" title="<spring:message code="registre.annex.detalls.camp.firma.autoFirma.info" />"></span>';
								
							});
	    					nieList += ']';
					    });

    					viewerContent += '<dl class="dl-horizontal">\
							   				<dt style="text-align: left;"><spring:message code="registre.annex.detalls.camp.firmants"/>:</dt>\
							   				<dd>' + nieList + (nomList != "" ? ' - ' +  nomList : '') + '</dd>\
							   			  </dl>\
							   			  </div><hr></div>';
   						$(viewerContent).insertAfter('.viewer-content');
					}
				}
		);

	    // Amagar columnes taula
	    var tableAnnexos = resumAnnexos.find('table');
	    tableAnnexos.find('tr').each(function() {
	    	$(this).children("th:eq(2), th:eq(3), th:eq(4), td:eq(2), td:eq(3), td:eq(4)").hide();
	    });

	    // Recuperar i mostrar document al visor
		var urlDescarrega = "<c:url value="/expedientPeticio/annex/"/>" + annexId + "/content";
		$('#container').attr('src', '');
		$('#container').addClass('rmodal_loading');
		showDocument(urlDescarrega);
	}

	function showDocument(arxiuUrl) {
		// Fa la petició a la url de l'arxiu
		$.ajax({
			type: 'GET',
			url: arxiuUrl,
			responseType: 'arraybuffer',
			success: function(response) {
	            var blob = base64toBlob(response.contingut, response.contentType);
	            var file = new File([blob], response.contentType, {type: response.contentType});
	            link = URL.createObjectURL(file);
	            
	            var viewerUrl = "<c:url value="/webjars/pdf-js/2.5.207/web/viewer.html"/>" + '?file=' + encodeURIComponent(link);
			    $('#container').removeClass('rmodal_loading');
			    $('#container').attr('src', viewerUrl);
			},
			error: function(xhr, ajaxOptions, thrownError) {
				$('#container').removeClass('rmodal_loading');
				alert(thrownError);
			}
		});
	}

	// Amagar visor
	function closeViewer() {
		var resumAnnexos = $('#resum-annexos');
		$('#resum-viewer').slideUp(500, function(){
			resumAnnexos.css('width', '100%');
		
			// Mostrar columnes taula
			var tableAnnexos = resumAnnexos.find('table');
		    tableAnnexos.find('tr').each(function() {
		    	$(this).children("th:eq(2), th:eq(3), th:eq(4), td:eq(2), td:eq(3), td:eq(4)").show();
		    	$(this).removeAttr('style');
		    });
		});
	}
	
	function resetBackground() {
		var tableAnnexos = $('#resum-annexos').find('table');
		tableAnnexos.find('tr').each(function() {
	    	$(this).removeAttr('style');
	    });
	}
</script>
</head>
<body>


	<!------------------------------ TABLIST ------------------------------------------------->
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation">
			<a href="#resum" aria-controls="resum" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.resum"/></a>
		</li>
		<li role="presentation">
			<a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.informacio"/></a>
		</li>
		<li role="presentation">
			<a href="#interessats" aria-controls="interessats" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.interessats"/>&nbsp;<span class="badge">${fn:length(registre.interessats)}</span></a>
		</li>
		<li role="presentation">
			<a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab">
				<c:if test="${isErrorDocuments}"><span class="fa fa-warning text-danger"></span></c:if>
				<spring:message code="registre.detalls.pipella.annexos"/>&nbsp;
				<span class="badge">${fn:length(registre.annexos)}</span>
			</a>
		</li>
		<c:if test="${isIncorporacioJustificantActiva}">
			<li role="presentation">
				<a href="#justificant" aria-controls="justificant" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.justificant"/></a>
			</li>
		</c:if>
		<c:if test="${not empty peticio.notificaDistError}">
			<li role="presentation">
				<a href="#error" aria-controls="error" role="tab" data-toggle="tab">
					<spring:message code="registre.detalls.pipella.error"/>
					<span class="fa fa-warning text-danger"></span>				
				</a>
			</li>		
		</c:if>

	</ul>
	<div class="tab-content">
		<!------------------------------------------- TABPANEL RESUM --------------------------------------------->
		<div class="tab-pane active in" id="resum" role="tabpanel">
			<table class="table table-bordered">
			<tbody>
				<c:if test="${isIncorporacioJustificantActiva}">
					<tr>
						<td><strong><spring:message code="registre.detalls.camp.numero"/></strong></td>
						<td colspan="3">
									${registre.identificador}
							<a href="descarregarJustificant/${registre.id}" class="btn btn-default btn-sm pull-right">
								<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
							</a>					
						</td>
						<td><strong><spring:message code="registre.detalls.camp.data"/></strong></td>
						<td><fmt:formatDate value="${registre.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>					
					</tr>
				</c:if>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.oficina"/></strong></td>
					<td colspan="5">${registre.oficinaDescripcio} (${registre.oficinaCodi})</td>					
							
				</tr>		
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.extracte"/></strong></td>
					<td colspan="5">${registre.extracte}</td>
				</tr>				
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.observacions"/></strong></td>
					<td colspan="5">${registre.observacions}</td>
				</tr>						
				<tr>
					<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.origen.num"/></strong></td>
					<td style="width:16%;">${registre.identificador}</td>
					<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.origen.data"/></strong></td>
					<td style="width:16%;"><fmt:formatDate value="${registre.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
					<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.origen.oficina"/></strong></td>
					<td style="width:17%;">${registre.oficinaDescripcio} ${registre.oficinaCodi!=null?'(':''}${registre.oficinaCodi}${registre.oficinaCodi!=null?')':''}</td>
				</tr>				
				</tbody>
			</table>
	
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><spring:message code="registre.detalls.pipella.informacio.resum"/></h3>
				</div>
				<table class="table table-bordered">
					<tbody>
						<tr>
							<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.docfis"/></strong></td>
							<td>${registre.docFisicaCodi} - ${registre.docFisicaDescripcio}</td>
						</tr>
						<tr>
							<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.desti"/></strong></td>
							<td>${registre.destiDescripcio} (${registre.destiCodi})</td>
						</tr>
						<tr>
							<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.refext"/></strong></td>
							<td>${registre.refExterna}</td>
							</tr>
						<tr>
							<td style="width:16%;"><strong><spring:message code="registre.detalls.camp.numexp"/></strong></td>
							<td>${registre.expedientNumero}</td>
						</tr>
					</tbody>
				</table>
			</div>
			<!------------------- INTERESSATS ------------------->
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><spring:message code="registre.detalls.pipella.interessats"/></h3>
				</div>
				<c:choose>
					<c:when test="${not empty registre.interessats}">
						<table class="table table-bordered">
						<thead>
							<tr>
								<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
								<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
								<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
								<th style="width: 50px;"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="interessat" items="${registre.interessats}" varStatus="status">
								<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
									<td>
										<spring:message code="peticio.registre.interessat.tipus.enum.${interessat.tipus}"/>
									</td>
									<td>${interessat.documentTipus}: ${interessat.documentNumero}</td>
									<c:choose>
										<c:when test="${interessat.tipus == 'PERSONA_FISICA'}">
											<td>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</td>
										</c:when>
										<c:otherwise>
											<td>${interessat.raoSocial}</td>
										</c:otherwise>
									</c:choose>
									<td>
										<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
											<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}_resum" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}_resum">
												<span class="fa fa-caret-down"></span>
											</button>
										</c:if>
									</td>
								</tr>
								<tr class="collapse detall" id="detalls_${status.index}_resum">
									<td colspan="4">
										<div class="row">
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${interessat.pais}</dd>
													<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${interessat.provincia}</dd>											
													<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${interessat.municipi}</dd>
													<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${interessat.adresa}</dd>
													<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${interessat.cp}</dd>
												</dl>
											</div>
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${interessat.email}</dd>
													<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
													<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty interessat.canal}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${interessat.canal}"/></c:if></dd>
													<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
												</dl>
											</div>
											
											<!-- NOU APARTAT REPRESENTANT -->
											<c:if test="${not empty interessat.representant}">
												<c:set var="representant" value="${interessat.representant}"/>
												<div class="col-xs-12">
													<table class="table table-bordered">
														<thead>
															<tr><th colspan="4"><spring:message code="registre.interessat.detalls.camp.representant"/></th></tr>
															<tr>
																<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
																<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
																<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
																<th style="width: 50px;"></th>
															</tr>
														</thead>
														<tbody>
															<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
																<td>
																	<spring:message code="peticio.registre.interessat.tipus.enum.${representant.tipus}"/>
																</td>
																<td>${representant.documentTipus}: ${representant.documentNumero}</td>
																<c:choose>
																	<c:when test="${representant.tipus == 'PERSONA_FISICA'}">
																		<td>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</td>
																	</c:when>
																	<c:otherwise>
																		<td>${representant.raoSocial}</td>
																	</c:otherwise>
																</c:choose>
																<td>
																	<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
																		<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}_rep_resum" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}_rep_resum">
																			<span class="fa fa-caret-down"></span>
																		</button>
																	</c:if>
																</td>
															</tr>
															<tr class="collapse detall" id="detalls_${status.index}_rep_resum">
																<td colspan="4">
																	<div class="row">
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${representant.pais}</dd>
																				<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${representant.provincia}</dd>											
																				<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${representant.municipi}</dd>
																				<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${representant.adresa}</dd>
																				<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${representant.cp}</dd>
																			</dl>
																		</div>
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${representant.email}</dd>
																				<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${representant.telefon}</dd>
																				<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty representant.canal}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${representant.canal}"/></c:if></dd>
																				<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${representant.observacions}</dd>
																			</dl>
																		</div>
																	</div>
																</td>						
															</tr>
														</tbody>
													</table>
												</div>
											</c:if>
										</div>
									</td>						
								</tr>
							</c:forEach>
						</tbody>
					</table>
					</c:when>
					<c:otherwise>
						<div class="panel-body">
							<spring:message code="registre.interessat.buit"/>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
			
			
			<!------------------- ANNEXOS ------------------->
			<div id="resum-annexos-container">
				<div class="panel panel-default" id="resum-annexos" style="width: 100%">
					<div class="panel-heading">
						<h3 class="panel-title"><spring:message code="registre.detalls.pipella.annexos"/></h3>
					</div>
					<c:choose>
						<c:when test="${not empty registre.annexos}">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th style="width: 300px;"><spring:message code="registre.annex.detalls.camp.titol"/></th>
									<th style="width: 180px;"><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></th>
									<th style="width: 450px;"><spring:message code="registre.annex.detalls.camp.observacions"/></th>
									<th><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></th>
									<th style="width: 250px;"><spring:message code="registre.annex.detalls.camp.eni.origen"/></th>
									<th style="width: 250px;"><spring:message code="registre.annex.detalls.camp.eni.estat.elaboracio"/></th>
									<th style="width: 50px;"><spring:message code="registre.annex.detalls.camp.fitxer"/></th>
									<th style="width: 50px;"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="annex" items="${registre.annexos}" varStatus="status">
									<tr title="<spring:message code="registre.annex.detalls.previsualitzar"/>" <c:choose><c:when test="${annex.tipusMime == 'application/pdf' }">onclick="showViewer(event, ${annex.id}, '${annex.observacions}', '${annex.ntiFechaCaptura}', '${annex.ntiOrigen}')"</c:when><c:otherwise>class="invalid-format"</c:otherwise></c:choose>>
										<td>${annex.titol}</td>
										<td><c:if test="${not empty annex.ntiTipoDocumental}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipoDocumental}"/></c:if></td>
										<td>${annex.observacions}</td>
										<td><c:if test="${not empty annex.ntiFechaCaptura}"><fmt:formatDate value="${annex.ntiFechaCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
										<td><c:if test="${not empty annex.ntiOrigen}">${annex.ntiOrigen}</c:if></td>
										<td><c:if test="${not empty annex.ntiEstadoElaboracion}"><spring:message code="registre.annex.detalls.camp.ntiElaboracioEstat.${annex.ntiEstadoElaboracion}"/></c:if></td>
										<td>
											<a href="descarregarAnnex/${annex.id}" class="btn btn-default btn-sm pull-right arxiu-download">
												<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
											</a>
										</td>												
										<td>
											<button type="button" class="btn btn-default desplegable" href="#detalls_resum_annexos_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_resum_annexos_${status.index}">
												<span class="fa fa-caret-down"></span>
											</button>
										</td>	
									</tr>	
									<tr class="collapse detall" id="detalls_resum_annexos_${status.index}">
									
										<script type="text/javascript">
											$(document).ready(function() {
												$("#detalls_resum_annexos_${status.index}").on('show.bs.collapse', function(event){
													$("#collapse-resum-firmes-<c:out value='${annex.id}'/>").collapse("show");
												});
											});
									</script>	
									
									<td colspan="8">		
										<c:if test="${annex.ambFirma}">
										<div class="panel panel-default">
												<div class="panel-heading">
													<h3 class="panel-title">
														<span class="fa fa-certificate"></span>
														<spring:message code="registre.annex.detalls.camp.firmes"/>
														<button id="collapse-resum-btn-firmes-${annex.id}" class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-resum-firmes-${annex.id}"><span class="fa fa-chevron-down"></span></button>
													</h3>
												</div>
												<div id="collapse-resum-firmes-${annex.id}" class="panel-collapse collapse collapse-resum-firmes" role="tabpanel"> 
													<script type="text/javascript">
														$(document).ready(function() {
														    $("#collapse-resum-firmes-<c:out value='${annex.id}'/>").on('show.bs.collapse', function(event){  	
															    if (!$(this).data("loaded")) {
															        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
															        $(this).load("<c:url value="/nodeco/expedientPeticio/firmaInfo/"/>" + ${annex.id});
															        $(this).data("loaded", true);
															    }
															    event.stopPropagation();
														    });
														});
													</script>													
												</div> 
											</div>
										</c:if>											
									</td>
								</tr>	
							</c:forEach>
						</tbody>
						</table>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${not empty annexosErrorMsg}">
								<div class="panel-body">
									<div class="alert alert-danger">
										${annexosErrorMsg}
									</div>
								</div>						
							</c:when>
							<c:otherwise>
								<div class="panel-body">
									<spring:message code="registre.annex.buit"/>
								</div>
							</c:otherwise>
						</c:choose>				
					</c:otherwise>
				</c:choose>				
			</div>	
			<div class="panel panel-default" id="resum-viewer">
				<iframe id="container" class="viewer-padding" width="100%" height="540" frameBorder="0"></iframe>
			</div>     
		</div>	
		</div>
		<!------------------------------ TABPANEL INFORMACIO ------------------------------------->
		<div class="tab-pane in" id="informacio" role="tabpanel">
			<table class="table table-bordered">
			<tbody>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.tipus"/></strong></td>
					<td><spring:message code="registre.detalls.entrada"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.numero"/></strong></td>
					<td>${registre.identificador}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.data"/></strong></td>
					<td><fmt:formatDate value="${registre.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.oficina"/></strong></td>
					<td>${registre.oficinaDescripcio} (${registre.oficinaCodi})</td>
				</tr>
			</tbody>
			</table>
			<div class="row">
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="registre.detalls.titol.obligatories"/></h3>
						</div>
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.llibre"/></strong></td>
									<td>${registre.llibreDescripcio} (${registre.llibreCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.extracte"/></strong></td>
									<td>${registre.extracte}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.docfis"/></strong></td>
									<td>${registre.docFisicaCodi} - ${registre.docFisicaDescripcio}</td>
								</tr>
								<tr>
									<td><strong>
										<spring:message code="registre.detalls.camp.desti"/>
									</strong></td>
									<td>${registre.destiDescripcio} (${registre.destiCodi})</td>
									
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.assumpte.tipus"/></strong></td>
									<td>${registre.assumpteTipusDescripcio} (${registre.assumpteTipusCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.idioma"/></strong></td>
									<td>${registre.idiomaDescripcio} (${registre.idiomaCodi})</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><spring:message code="registre.detalls.titol.opcionals"/></h3>
						</div>
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.assumpte.codi"/></strong></td>
									<td colspan="2">(${registre.assumpteCodiCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.refext"/></strong></td>
									<td>${registre.refExterna}</td>
									<td><strong><spring:message code="registre.detalls.camp.numexp"/></strong></td>
									<td>${registre.expedientNumero}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.transport.tipus"/></strong></td>
									<td>${registre.transportTipusDescripcio} ${registre.transportTipusCodi!=null?'(':''}${registre.transportTipusCodi}${registre.transportTipusCodi!=null?')':''}</td>
									<td><strong><spring:message code="registre.detalls.camp.transport.num"/></strong></td>
									<td>${registre.transportNumero}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.origen.num"/></strong></td>
									<td>${registre.origenRegistreNumero}</td>
									<td><strong><spring:message code="registre.detalls.camp.origen.data"/></strong></td>
									<td><fmt:formatDate value="${registre.origenData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.observacions"/></strong></td>
									<td colspan="2">${registre.observacions}</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		
		<!------------------------------ TABPANEL INTERESSATS ------------------------------------->
		<div class="tab-pane" id="interessats" role="tabpanel">
			<c:choose>
				<c:when test="${not empty registre.interessats}">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
								<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
								<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
								<th style="width: 50px;"></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="interessat" items="${registre.interessats}" varStatus="status">
								<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
									<td>
										<spring:message code="peticio.registre.interessat.tipus.enum.${interessat.tipus}"/>
									</td>
									<td>${interessat.documentTipus}: ${interessat.documentNumero}</td>
									<c:choose>
										<c:when test="${interessat.tipus == 'PERSONA_FISICA'}">
											<td>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</td>
										</c:when>
										<c:otherwise>
											<td>${interessat.raoSocial}</td>
										</c:otherwise>
									</c:choose>
									<td>
										<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
											<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}">
												<span class="fa fa-caret-down"></span>
											</button>
										</c:if>
									</td>
								</tr>
								<tr class="collapse detall" id="detalls_${status.index}">
									<td colspan="4">
										<div class="row">
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${interessat.pais}</dd>
													<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${interessat.provincia}</dd>											
													<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${interessat.municipi}</dd>
													<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${interessat.adresa}</dd>
													<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${interessat.cp}</dd>
												</dl>
											</div>
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${interessat.email}</dd>
													<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
													<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty interessat.canal}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${interessat.canal}"/></c:if></dd>
													<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
												</dl>
											</div>
											
											<!-- NOU APARTAT REPRESENTANT -->
											<c:if test="${not empty interessat.representant}">
												<c:set var="representant" value="${interessat.representant}"/>
												<div class="col-xs-12">
													<table class="table table-bordered">
														<thead>
															<tr><th colspan="4"><spring:message code="registre.interessat.detalls.camp.representant"/></th></tr>
															<tr>
																<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.tipus"/></th>
																<th style="width: 150px;"><spring:message code="registre.detalls.camp.interessat.document"/></th>
																<th><spring:message code="registre.detalls.camp.interessat.nom"/></th>
																<th style="width: 50px;"></th>
															</tr>
														</thead>
														<tbody>
															<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
																<td>
																	<spring:message code="peticio.registre.interessat.tipus.enum.${representant.tipus}"/>
																</td>
																<td>${representant.documentTipus}: ${representant.documentNumero}</td>
																<c:choose>
																	<c:when test="${representant.tipus == 'PERSONA_FISICA'}">
																		<td>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</td>
																	</c:when>
																	<c:otherwise>
																		<td>${representant.raoSocial}</td>
																	</c:otherwise>
																</c:choose>
																<td>
																	<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
																		<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}_rep" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}_rep">
																			<span class="fa fa-caret-down"></span>
																		</button>
																	</c:if>
																</td>
															</tr>
															<tr class="collapse detall" id="detalls_${status.index}_rep">
																<td colspan="4">
																	<div class="row">
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${representant.pais}</dd>
																				<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${representant.provincia}</dd>											
																				<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${representant.municipi}</dd>
																				<dt><spring:message code="interessat.form.camp.adresa"/></dt><dd>${representant.adresa}</dd>
																				<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${representant.cp}</dd>
																			</dl>
																		</div>
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${representant.email}</dd>
																				<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${representant.telefon}</dd>
																				<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty representant.canal}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${representant.canal}"/></c:if></dd>
																				<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${representant.observacions}</dd>
																			</dl>
																		</div>
																	</div>
																</td>						
															</tr>
														</tbody>
													</table>
												</div>
											</c:if>
										</div>
									</td>						
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<div class="row col-xs-12">
						<div class="well">
							<spring:message code="registre.interessat.buit"/>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		
		<!------------------------------ TABPANEL ANNEXOS ------------------------------------->
		<div class="tab-pane" id="annexos" role="tabpanel">
			<c:choose>
				<c:when test="${not empty registre.annexos}">
				
					<c:forEach var="annex" items="${registre.annexos}" varStatus="status">
					
						<script type="text/javascript">
							$(document).ready(function() {
							    $("#collapse-registre-firmes-<c:out value='${annex.id}'/>").on('show.bs.collapse', function(data){  	
								    if (!$(this).data("loaded")) {
								        var annexId = $(this).data("annexId");
								        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
								        $(this).load("<c:url value="/nodeco/expedientPeticio/firmaInfo/"/>" + ${annex.id});
								        $(this).data("loaded", true);
								    }
							    });
						 	});
						</script>
					
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<span class="fa fa-file"></span>
									${annex.titol}
									<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-annex-${status.index}"><span class="fa fa-chevron-down"></span></button>
								</h3>
							</div>
 							<div id="collapse-annex-${status.index}" class="panel-collapse collapse collapse-annex" role="tabpanel" aria-labelledby="dadesAnnex${status.index}" data-registre-id="${registre.id}"  data-fitxer-arxiu-uuid="${annex.uuid}">

								<div>
									<c:if test="${annex.estat == 'PENDENT' && not empty annex.error}">
									
										<div class="alert well-sm alert-danger alert-dismissable" style="margin-bottom: 0px;">
											<span class="fa fa-exclamation-triangle"></span>
											<spring:message code="expedient.peticio.detalls.annex.error" />
											<a href="<c:url value="/expedientPeticio/${annex.id}/${peticio.id}/reintentar"/>"
												class="btn btn-xs btn-default pull-right" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-refresh"></span>
												<spring:message code="expedient.peticio.detalls.annex.accio.reintentar" /></a>
										</div>
										<pre style="height: 200px; background-color: white; margin-bottom: 0px;">${annex.error}</pre>
									</c:if>
								</div>

								<table class="table table-bordered">
								<tbody>														
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></strong></td>
										<td><c:if test="${not empty annex.ntiFechaCaptura}"><fmt:formatDate value="${annex.ntiFechaCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.origen"/></strong></td>
										<td><c:if test="${not empty annex.ntiOrigen}">${annex.ntiOrigen}</c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></strong></td>
										<td><c:if test="${not empty annex.ntiTipoDocumental}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipoDocumental}"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.sicres.tipus.document"/></strong></td>
										<td><c:if test="${not empty annex.sicresTipoDocumento}"><spring:message code="registre.annex.detalls.camp.sicresTipusDocument.${annex.sicresTipoDocumento}"/></c:if></td>
									</tr>
								
									<c:if test="${not empty annex.observacions}">
										<tr>
											<td><strong><spring:message code="registre.annex.detalls.camp.observacions"/></strong></td>
											<td>${annex.observacions}</td>
										</tr>
									</c:if>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></td>
										<td>
											${annex.titol}
											<a href="descarregarAnnex/${annex.id}" class="btn btn-default btn-sm pull-right">
												<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
											</a>
										</td>
									</tr>
									<c:if test="${not empty annex.firmaTipus}">
										<tr>
											<td colspan="2">
												<div class="panel panel-default">
													<div class="panel-heading">
														<h3 class="panel-title">
															<span class="fa fa-certificate"></span>
															<spring:message code="registre.annex.detalls.camp.firmes"/>
															<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-registre-firmes-${annex.id}"><span class="fa fa-chevron-down"></span></button>
														</h3>
													</div>
													<div id="collapse-registre-firmes-${annex.id}" class="panel-collapse collapse collapse-annex collapse-registre-firmes" role="tabpanel" data-annex-id="${annex.id}"> 
									
													</div> 
												</div>
											</td>
										</tr>
									</c:if>
								</table>
 							</div> 
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${not empty annexosErrorMsg}">
							<div class="row col-xs-12">
								<div class="alert alert-danger">
									${annexosErrorMsg}
								</div>
							</div>						
						</c:when>
						<c:otherwise>
							<div class="row col-xs-12">
								<div class="well">
									<spring:message code="registre.annex.buit"/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>				
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${isIncorporacioJustificantActiva}">
			<!------------------------------ TABPANEL JUSTIFICANT ------------------------------------->
			<div class="tab-pane" id="justificant" role="tabpanel">
				<c:if test="${not empty registre.justificant}">
					<script type="text/javascript">
						$(document).ready(function() {
						    $("#collapse-justificant-firmes").on('show.bs.collapse', function(data){  	
							    if (!$(this).data("loaded")) {
							        var annexId = $(this).data("annexId");
							        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
							        $(this).load("<c:url value="/nodeco/expedientPeticio/justificantFirmaInfo/${registre.id}"/>");
							        $(this).data("loaded", true);
							    }
						    });
					 	});
					</script>
				
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">
								<span class="fa fa-file"></span>
									${registre.justificant.titol}
								<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-justificant"><span class="fa fa-chevron-down"></span></button>
							</h3>
						</div>
							<div id="collapse-justificant" class="panel-collapse collapse collapse-annex" role="tabpanel" aria-labelledby="dadesJustificant" data-registre-id="${registre.id}"  data-fitxer-arxiu-uuid="${registre.justificant.uuid}">
							<div>
								<c:if test="${registre.justificant.estat == 'PENDENT' && not empty registre.justificant.error}">	
										<div class="alert well-sm alert-danger alert-dismissable" style="margin-bottom: 0px;">
											<span class="fa fa-exclamation-triangle"></span>
											<spring:message code="expedient.peticio.detalls.annex.error" />
											<a href="<c:url value="/expedientPeticio/justificant/${peticio.id}/reintentar"/>"
												class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>
												<spring:message code="expedient.peticio.detalls.annex.accio.reintentar" /></a>
										</div>
										<pre style="height: 200px; background-color: white; margin-bottom: 0px;">${registre.justificant.error}</pre>
									</c:if>
								</div>
								<table class="table table-bordered">
								<tbody>														
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></strong></td>
										<td><c:if test="${not empty registre.justificant.ntiFechaCaptura}"><fmt:formatDate value="${registre.justificant.ntiFechaCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.origen"/></strong></td>
										<td><c:if test="${not empty registre.justificant.ntiOrigen}">${registre.justificant.ntiOrigen}</c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></strong></td>
										<td><c:if test="${not empty registre.justificant.ntiTipoDocumental}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${registre.justificant.ntiTipoDocumental}"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.arxiuuuid"/></strong></td>
										<td><c:if test="${not empty registre.justificant.uuid}">${registre.justificant.uuid}</c:if></td>
									</tr>
								
									<c:if test="${not empty registre.justificant.observacions}">
										<tr>
											<td><strong><spring:message code="registre.annex.detalls.camp.observacions"/></strong></td>
											<td>${registre.justificant.observacions}</td>
										</tr>
									</c:if>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></td>
										<td>
											${registre.justificant.titol}
											<a href="descarregarJustificant/${registre.id}" class="btn btn-default btn-sm pull-right">
												<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
											</a>
										</td>
									</tr>
									<c:if test="${not empty registre.justificant.firmaTipus}">
										<tr>
											<td colspan="2">
												<div class="panel panel-default">
													<div class="panel-heading">
														<h3 class="panel-title">
															<span class="fa fa-certificate"></span>
															<spring:message code="registre.annex.detalls.camp.firmes"/>
															<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-justificant-firmes"><span class="fa fa-chevron-down"></span></button>
														</h3>
													</div>
													<div id="collapse-justificant-firmes" class="panel-collapse collapse collapse-annex collapse-registre-firmes" role="tabpanel"> 
									
													</div> 
												</div>
											</td>
										</tr>
									</c:if>
								</table>
	 						</div> 
						</div>
					</c:if>
				</div>
			</c:if>
			<!------------------------------ TABPANEL ERROR ------------------------------------->
			<div class="tab-pane" id="error" role="tabpanel">
				<div>
					<div class="alert well-sm alert-danger alert-dismissable" style="margin-bottom: 0px;">
						<span class="fa fa-exclamation-triangle"></span>
						<spring:message code="expedient.peticio.detalls.errorNotifacio" />
						<a href="<c:url value="/expedientPeticio/${peticio.id}/reintentarNotificar"/>"
							class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>
							<spring:message code="expedient.peticio.detalls.annex.accio.reintentar" /></a>
					</div>
					<pre style="height: 200px; background-color: white; margin-bottom: 0px;">${peticio.notificaDistError}</pre>
				</div>
			</div>
	</div>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/bustiaUser"/>" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
