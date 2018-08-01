<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="registre.detalls.titol"/></c:set>
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
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation"><a href="#informacio" aria-controls="informacio" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.informacio"/></a>
		</li>
		<li role="presentation">
			<a href="#interessats" aria-controls="interessats" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.interessats"/>&nbsp;<span class="badge">${fn:length(registre.interessats)}</span></a>
		</li>
		<li role="presentation">
			<a href="#annexos" aria-controls="annexos" role="tab" data-toggle="tab"><spring:message code="registre.detalls.pipella.annexos"/>&nbsp;<span class="badge">${fn:length(registre.annexos)}</span></a>
		</li>
		<c:if test="${registre.procesEstat != 'NO_PROCES'}">
			<li role="presentation">
				<a href="#proces" aria-controls="proces" role="tab" data-toggle="tab">
					<spring:message code="registre.detalls.pipella.proces"/>
					<c:if test="${registre.procesEstat == 'ERROR'}"><span class="fa fa-warning text-danger"></span></c:if>
				</a>
			</li>
		</c:if>
	</ul>
	<div class="tab-content">
		<div class="tab-pane active in" id="informacio" role="tabpanel">
			<table class="table table-bordered">
			<tbody>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.tipus"/></strong></td>
					<td><spring:message code="registre.anotacio.tipus.enum.${registre.registreTipus}"/></td>
				</tr>
				<tr>
					<td><strong><spring:message code="registre.detalls.camp.numero"/></strong></td>
					<td>${registre.numero}</td>
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
									<td>${registre.documentacioFisicaCodi} - ${registre.documentacioFisicaDescripcio}</td>
								</tr>
								<tr>
									<td><strong>
										<c:if test="${registre.registreTipus == 'ENTRADA'}"><spring:message code="registre.detalls.camp.desti"/></c:if>
										<c:if test="${registre.registreTipus == 'SORTIDA'}"><spring:message code="registre.detalls.camp.origen"/></c:if>
									</strong></td>
									<td>${registre.unitatAdministrativaDescripcio} (${registre.unitatAdministrativa})</td>
									
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
									<td colspan="2">(${registre.assumpteCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.refext"/></strong></td>
									<td>${registre.referencia}</td>
									<td><strong><spring:message code="registre.detalls.camp.numexp"/></strong></td>
									<td>${registre.expedientNumero}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.transport.tipus"/></strong></td>
									<td>${registre.transportTipusDescripcio} (${registre.transportTipusCodi})</td>
									<td><strong><spring:message code="registre.detalls.camp.transport.num"/></strong></td>
									<td>${registre.transportNumero}</td>
								</tr>
								<tr>
									<td colspan="2"><strong><spring:message code="registre.detalls.camp.origen.oficina"/></strong></td>
									<td colspan="2">${registre.oficinaOrigenDescripcio} (${registre.oficinaOrigenCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="registre.detalls.camp.origen.num"/></strong></td>
									<td>${registre.numeroOrigen}</td>
									<td><strong><spring:message code="registre.detalls.camp.origen.data"/></strong></td>
									<td><fmt:formatDate value="${registre.dataOrigen}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
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
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<spring:message code="registre.detalls.titol.seguiment"/>
						<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-seguiment"><span class="fa fa-chevron-down"></span></button>
					</h3>
				</div>
				<div id="collapse-seguiment" class="panel-collapse collapse" role="tabpanel" aria-labelledby="dadesSeguiment">
					<table class="table table-bordered">
					<tbody>
						<c:if test="${not empty registre.entitatCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.entitat"/></strong></td>
								<td>${registre.entitatDescripcio} (${registre.entitatCodi})</td>
							</tr>
						</c:if>
						<c:if test="${not empty registre.aplicacioCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.aplicacio"/></strong></td>
								<td>${registre.aplicacioCodi} ${registre.aplicacioVersio}</td>
							</tr>
						</c:if>
						<c:if test="${not empty registre.usuariCodi}">
							<tr>
								<td><strong><spring:message code="registre.detalls.camp.usuari"/></strong></td>
								<td>${registre.usuariNom} (${registre.usuariCodi})</td>
							</tr>
						</c:if>
						<tr>
							<td><strong><spring:message code="registre.detalls.camp.ripea.alta"/></strong></td>
							<td><fmt:formatDate value="${registre.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						</tr>
					</tbody>
					</table>
				</div>
			</div>
			
<!-- 			JUSTIFICANT -->
			<c:if test="${not empty registre.justificant}">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:message code="registre.detalls.titol.justificant"/>
							<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-justificant"><span class="fa fa-chevron-down"></span></button>
						</h3>
					</div>
					<div id="collapse-justificant" class="panel-collapse collapse" role="tabpanel" aria-labelledby="justificant">
						<table class="table table-bordered">
						<tbody>
							<tr>
								<td><strong><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></strong></td>
								<td><c:if test="${not empty registre.justificant.dataCaptura}"><fmt:formatDate value="${registre.justificant.dataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
							</tr>
							<tr>
								<td><strong><spring:message code="registre.annex.detalls.camp.eni.origen"/></strong></td>
								<td><c:if test="${not empty registre.justificant.origenCiutadaAdmin}">${registre.justificant.origenCiutadaAdmin}</c:if></td>
							</tr>
							<tr>
								<td><strong><spring:message code="registre.annex.detalls.camp.eni.estat.elaboracio"/></strong></td>
								<td><c:if test="${not empty registre.justificant.ntiElaboracioEstat}"><spring:message code="registre.annex.detalls.camp.ntiElaboracioEstat.${registre.justificant.ntiElaboracioEstat}"/></c:if></td>
							</tr>
							<tr>
								<td><strong><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></strong></td>
								<td><c:if test="${not empty registre.justificant.ntiTipusDocument}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${registre.justificant.ntiTipusDocument}"/></c:if></td>
							</tr>
							<tr>
								<td><strong><spring:message code="registre.annex.detalls.camp.sicres.tipus.document"/></strong></td>
								<td><c:if test="${not empty registre.justificant.sicresTipusDocument}"><spring:message code="registre.annex.detalls.camp.sicresTipusDocument.${registre.justificant.sicresTipusDocument}"/></c:if></td>
							</tr>
							<c:if test="${not empty registre.justificant.localitzacio}">
								<tr>
									<td><strong><spring:message code="registre.annex.detalls.camp.localitzacio"/></strong></td>
									<td>${registre.justificant.localitzacio}</td>
								</tr>
							</c:if>
							<c:if test="${not empty registre.justificant.observacions}">
								<tr>
									<td><strong><spring:message code="registre.annex.detalls.camp.observacions"/></strong></td>
									<td>${registre.justificant.observacions}</td>
								</tr>
							</c:if>
							<tr>
								<td><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></td>
								<td>
									${registre.justificant.fitxerNom} (${registre.justificant.fitxerTamany} bytes)
									<a href="${registre.id}/justificant" class="btn btn-default btn-sm pull-right">
										<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
									</a>
								</td>
							</tr>
						</tbody>
						</table>
					</div>
				</div>
			</c:if>
<!-- 			FI JUSTIFICANT -->
			
		</div>
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
										<spring:message code="registre.interessat.tipus.enum.${interessat.tipus}"/>
									</td>
									<td>${interessat.documentTipus}: ${interessat.documentNum}</td>
									<c:choose>
										<c:when test="${interessat.tipus == 'PERSONA_FIS'}">
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
													<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${interessat.codiPostal}</dd>
												</dl>
											</div>
											<div class="col-xs-6">
												<dl class="dl-horizontal">
													<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${interessat.email}</dd>
													<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
													<dt><spring:message code="registre.interessat.detalls.camp.emailHabilitat"/></dt><dd>${interessat.emailHabilitat}</dd>
													<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty interessat.canalPreferent}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${interessat.canalPreferent}"/></c:if></dd>
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
																	<spring:message code="registre.interessat.tipus.enum.${representant.tipus}"/>
																</td>
																<td>${representant.documentTipus}: ${representant.documentNum}</td>
																<c:choose>
																	<c:when test="${representant.tipus == 'PERSONA_FIS'}">
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
																				<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${representant.codiPostal}</dd>
																			</dl>
																		</div>
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${representant.email}</dd>
																				<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${representant.telefon}</dd>
																				<dt><spring:message code="registre.interessat.detalls.camp.emailHabilitat"/></dt><dd>${representant.emailHabilitat}</dd>
																				<dt><spring:message code="registre.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty representant.canalPreferent}"><spring:message code="registre.interessat.detalls.camp.canalPreferent.${interessat.canalPreferent}"/></c:if></dd>
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
											<!-- ------------------------ -->
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
		<div class="tab-pane" id="annexos" role="tabpanel">
			<c:choose>
				<c:when test="${not empty annexos}">
					<c:forEach var="annex" items="${annexos}" varStatus="status">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<span class="fa fa-file"></span>
									${annex.titol}
									<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-annex-${status.index}"><span class="fa fa-chevron-down"></span></button>
								</h3>
							</div>
							<div id="collapse-annex-${status.index}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="dadesAnnex${status.index}">
								<table class="table table-bordered">
								<tbody>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.data.captura"/></strong></td>
										<td><c:if test="${not empty annex.dataCaptura}"><fmt:formatDate value="${annex.dataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.origen"/></strong></td>
										<td><c:if test="${not empty annex.origenCiutadaAdmin}">${annex.origenCiutadaAdmin}</c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.estat.elaboracio"/></strong></td>
										<td><c:if test="${not empty annex.ntiElaboracioEstat}"><spring:message code="registre.annex.detalls.camp.ntiElaboracioEstat.${annex.ntiElaboracioEstat}"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.eni.tipus.documental"/></strong></td>
										<td><c:if test="${not empty annex.ntiTipusDocument}"><spring:message code="registre.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipusDocument}"/></c:if></td>
									</tr>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.sicres.tipus.document"/></strong></td>
										<td><c:if test="${not empty annex.sicresTipusDocument}"><spring:message code="registre.annex.detalls.camp.sicresTipusDocument.${annex.sicresTipusDocument}"/></c:if></td>
									</tr>
									<c:if test="${not empty annex.localitzacio}">
										<tr>
											<td><strong><spring:message code="registre.annex.detalls.camp.localitzacio"/></strong></td>
											<td>${annex.localitzacio}</td>
										</tr>
									</c:if>
									<c:if test="${not empty annex.observacions}">
										<tr>
											<td><strong><spring:message code="registre.annex.detalls.camp.observacions"/></strong></td>
											<td>${annex.observacions}</td>
										</tr>
									</c:if>
									<tr>
										<td><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></td>
										<td>
											${annex.fitxerNom} (${annex.fitxerTamany} bytes)
											<a href="${registre.id}/annex/${annex.id}/arxiu/DOCUMENT" class="btn btn-default btn-sm pull-right">
												<span class="fa fa-download" title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
											</a>
										</td>
									</tr>
									<c:if test="${annex.ambFirma}">
										<tr>
											<td colspan="2">
												<c:forEach var="firma" items="${annex.firmes}" varStatus="status">
													<div class="panel panel-default">
														<div class="panel-heading">
															<h3 class="panel-title">
																<span class="fa fa-certificate"></span>
																<spring:message code="registre.annex.detalls.camp.firma"/> ${status.index + 1}
																<c:if test="${firma.autofirma}">
																	(<spring:message code="registre.annex.detalls.camp.firma.autoFirma"/> 
																		<span class="fa fa-info-circle" title="<spring:message code="registre.annex.detalls.camp.firma.autoFirma.info" />"></span>)
																</c:if>
															</h3>
														</div>
														<table class="table table-bordered">
														<tbody>
															<tr>
																<td><strong><spring:message code="registre.annex.detalls.camp.firmaTipus"/></strong></td>
																<td><spring:message code="document.nti.tipfir.enum.${firma.tipus}"/></td>
															</tr>
															<tr>
																<td><strong><spring:message code="registre.annex.detalls.camp.firmaPerfil"/></strong></td>
																<td>${firma.perfil}</td>
															</tr>
															<c:if test="${firma.tipus != 'PADES' and firma.tipus != 'CADES_ATT' and firma.tipus != 'XADES_ENV'}">
																<tr>
																	<td><strong><spring:message code="registre.annex.detalls.camp.fitxer"/></strong></td>
																	<td>
																		${firma.fitxerNom}
																		<a href="${registre.id}/annex/${annex.id}/firma/${status.index}" class="btn btn-default btn-sm pull-right">
																			<span class="fa fa-download"  title="<spring:message code="registre.annex.detalls.camp.fitxer.descarregar"/>"></span>
																		</a>
																	</td>
																</tr>
															</c:if>
															<c:if test="${not empty firma.csvRegulacio}">
																<tr>
																	<td><strong><spring:message code="registre.annex.detalls.camp.firmaCsvRegulacio"/></strong></td>
																	<td>${firma.csvRegulacio}</td>
																</tr>
															</c:if>
															<c:if test="${not empty firma.detalls}">
																<tr>
																	<td><strong><spring:message code="registre.annex.detalls.camp.firmaDetalls"/></strong></td>
																	<td>
																		<table class="table teble-striped table-bordered">
																		<thead>
																			<tr>
																				<th><spring:message code="registre.annex.detalls.camp.firmaDetalls.data"/></th>
																				<th><spring:message code="registre.annex.detalls.camp.firmaDetalls.nif"/></th>
																				<th><spring:message code="registre.annex.detalls.camp.firmaDetalls.nom"/></th>
																				<th><spring:message code="registre.annex.detalls.camp.firmaDetalls.emissor"/></th>
																			</tr>
																		<tbody>
																		<c:forEach var="detall" items="${firma.detalls}">
																			<tr>
																				<td>
																					<c:if test="${not empty detall.data}"><fmt:formatDate value="${detall.data}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if>
																					<c:if test="${empty detall.data}"><spring:message code="registre.annex.detalls.camp.firmaDetalls.data.nd"/></c:if>
																				</td>
																				<td>${detall.responsableNif}</td>
																				<td>${detall.responsableNom}</td>
																				<td>${detall.emissorCertificat}</td>
																			</tr>
																		</c:forEach>
																		</tbody>
																		</table>
																	</td>
																</tr>
															</c:if>
														</tbody>
														</table>
													</div>
												</c:forEach>
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
		<div class="tab-pane" id="proces" role="tabpanel">
			<c:if test="${registre.procesEstat == 'ERROR'}">
				<div class="alert well-sm alert-danger alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="registre.detalls.info.errors"/>
					<a href="${registre.id}/reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="registre.detalls.accio.reintentar"/></a>
				</div>
			</c:if>
			<dl class="dl-horizontal">
				<dt><spring:message code="registre.detalls.camp.proces.estat"/></dt>
				<dd>${registre.procesEstat}</dd>
				<dt><spring:message code="registre.detalls.camp.proces.data"/></dt>
				<dd><fmt:formatDate value="${registre.procesData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
				<dt><spring:message code="registre.detalls.camp.proces.intents"/></dt>
				<dd>${registre.procesIntents}</dd>
			</dl>
			<c:if test="${registre.procesEstat == 'ERROR'}">
				<pre style="height:300px">${registre.procesError}</pre>
			</c:if>
		</div>
	</div>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contenidor/${contenidor.id}"/>" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
