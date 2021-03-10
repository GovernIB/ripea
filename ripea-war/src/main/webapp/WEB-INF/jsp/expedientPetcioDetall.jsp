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
</style>
<script type="text/javascript">
	
	$(document).ready(function() {
		
		$(".desplegable").click(function(){
			$(this).find("span").toggleClass("fa-caret-up");
			$(this).find("span").toggleClass("fa-caret-down");
		});
 	});

</script>
</head>
<body>


	<!------------------------------ TABLIST ------------------------------------------------->
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation"><a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.informacio"/></a>
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
		<!------------------------------ TABPANEL INFORMACIO ------------------------------------->
		<div class="tab-pane active in" id="informacio" role="tabpanel">
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
											<spring:message code="expedientPeticio.detalls.annex.error" />
											<a href="<c:url value="/expedientPeticio/${annex.id}/${peticio.id}/reintentar"/>"
												class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>
												<spring:message code="expedientPeticio.detalls.annex.accio.reintentar" /></a>
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
		
		<!------------------------------ TABPANEL ERROR ------------------------------------->
		<div class="tab-pane" id="error" role="tabpanel">
			<div>
				<div class="alert well-sm alert-danger alert-dismissable" style="margin-bottom: 0px;">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="expedientPeticio.detalls.errorNotifacio" />
					<a href="<c:url value="/expedientPeticio/${peticio.id}/reintentarNotificar"/>"
						class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>
						<spring:message code="expedientPeticio.detalls.annex.accio.reintentar" /></a>
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
