<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="not"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
	<title><spring:message code="enviament.info.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.min.js"/>"></script>
	<link href="<c:url value="/webjars/jquery-ui/1.12.0/jquery-ui.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
	<not:modalHead/>
<script type="text/javascript">


</script>
</head>
<body>
	<c:if test="${notificacio.error}">
		<div class="alert alert-danger well-sm">
			<span class="fa fa-warning text-danger"></span>
			<spring:message code="enviament.info.error.titol"/>
			<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapseError" aria-expanded="false" aria-controls="collapseError">
				<span class="fa fa-bars"></span>
			</button>
			<div id="collapseError" class="collapse">
				<br/>
				<textarea rows="10" style="width:100%">${fn:escapeXml(notificacio.errorDescripcio)}</textarea>
			</div>
		</div>
	</c:if>
	
	<!---------------------------------------- TABLIST ------------------------------------------>
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active">
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab">
				<spring:message code="enviament.info.tab.dades"/>
			</a>
		</li>		
		<li role="presentation">
			<a href="#estatNotifica" aria-controls="estatNotifica" role="tab" data-toggle="tab">
				<spring:message code="enviament.info.tab.estat.notifica"/>
			</a>
		</li>

	</ul>
	
	<div class="tab-content">
	
		<!------------------------------ TABPANEL DADES ------------------------------------->
		<div role="tabpanel" class="tab-pane active" id="dades">
			<br/>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<strong><spring:message code="enviament.info.seccio.dades"/></strong>
					</h3>
 				</div>
				<table class="table table-bordered" style="width:100%">
				<tbody>
					<tr>
						<c:choose>
							<c:when test="${not empty notificacio.id}">
								<td width="1%"><strong><spring:message code="enviament.info.dada.identificadors.identificador"/></strong></td>
								<td>${notificacio.notificacioIdentificador}</td>
								<td width="1%"><strong><spring:message code="enviament.info.dada.identificadors.referencia"/></strong></td>
								<td>${enviament.enviamentReferencia}</td>
							</c:when>
							<c:otherwise>
								<td colspan="2" width="1%"><strong>NOTIB</strong></td>
								<td colspan="2">${enviament.enviamentReferencia}</td>
							</c:otherwise>
						</c:choose>
					</tr>
				
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.nif"/></strong></td>
						<td colspan="4">${entregaNif}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.procediment"/></strong></td>
						<td colspan="4">${classificacioSia}</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.deh.obligada"/></strong></td>
						<td colspan="4">
							<c:choose>
								<c:when test="${enviament.interessat.entregaDehObligat}"><spring:message code="comu.si"/></c:when>
								<c:otherwise><spring:message code="comu.no"/></c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td><strong><spring:message code="enviament.info.dada.servei.tipus"/></strong></td>
						<c:choose><c:when test="${not empty notificacio.serveiTipusEnum}"><c:set var="envTip" value="${notificacio.serveiTipusEnum}"/></c:when><c:otherwise><c:set var="envTip" value="NORMAL"/></c:otherwise></c:choose>
						<td colspan="4"><spring:message code="notificacio.servei.tipus.enum.${envTip}"/></td>
					</tr>
					<tr>
						<td width="30%"><strong><spring:message code="enviament.info.dada.estat"/></strong></td>
						<c:if test="${not empty enviament.enviamentDatatEstat}">
							<td colspan="4"><spring:message code="notificacio.enviamentEstat.enum.${enviament.enviamentDatatEstat}"/></td>
						</c:if>
					</tr>
				</tbody>
				</table>
			</div>
			<div class="row">
				<c:set var="titularColSize" value="12"/>
				<c:if test="${not empty enviament.interessat}"><c:set var="titularColSize" value="12"/></c:if>
				<div class="col-sm-${titularColSize}">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">
								<strong><spring:message code="enviament.info.seccio.titular"/></strong>
							</h3>
		 				</div>

						<table class="table table-bordered" style="width: 100%">
							<tbody>
							
								<c:choose>
									<c:when test="${enviament.interessat.tipus == 'PERSONA_FISICA'}">
										<tr>
											<td><strong><spring:message code="enviament.info.titular.nif" /></strong></td>
											<td>${enviament.interessat.documentNum}</td>
										</tr>
										<c:if test="${not empty enviament.interessat.nom}">
											<tr>
												<td width="30%"><strong><spring:message
															code="enviament.info.titular.nom" /></strong></td>
												<td>${enviament.interessat.nom}</td>
											</tr>
										</c:if>
										<c:if test="${not empty enviament.interessat.llinatge1}">
											<tr>
												<td><strong><spring:message
															code="enviament.info.titular.llinatges" /></strong></td>
												<td>${enviament.interessat.llinatge1}<c:if
														test="${not empty interessat.llinatge2}">${interessat.llinatge2}</c:if>
												</td>
											</tr>
										</c:if>
									</c:when>
									<c:when test="${enviament.interessat.tipus == 'ADMINISTRACIO'}">
										<tr>
											<td><strong><spring:message code="enviament.info.titular.nif" /></strong></td>
											<td>${enviament.interessat.documentNum}</td>
										</tr>
									</c:when>
									<c:when test="${enviament.interessat.tipus == 'PERSONA_JURIDICA'}">
										<tr>
											<td><strong><spring:message code="enviament.info.titular.nif" /></strong></td>
											<td>${enviament.interessat.documentNum}</td>
										</tr>									
									</c:when>
								</c:choose>

								<c:if test="${not empty enviament.interessat.telefon}">
									<tr>
										<td><strong><spring:message code="enviament.info.titular.telefon" /></strong></td>
										<td>${enviament.interessat.telefon}</td>
									</tr>
								</c:if>
								<c:if test="${not empty enviament.interessat.email}">
									<tr>
										<td><strong><spring:message code="enviament.info.titular.email" /></strong></td>
										<td>${enviament.interessat.email}</td>
									</tr>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
				<c:if test="${not empty interessat.representant}">
					<div class="col-sm-6">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<strong><spring:message code="enviament.info.seccio.destinatari"/> nº 1</strong>
								</h3>
			 				</div>
							<table class="table table-bordered" style="width:100%">
							<tbody>
								<c:choose>
									<c:when test="${enviament.interessat.representant.tipus == 'PERSONA_FISICA'}">
										<tr>
											<td><strong><spring:message code="enviament.info.titular.nif" /></strong></td>
											<td>${enviament.interessat.representant.documentNum}</td>
										</tr>
										<c:if test="${not empty enviament.interessat.representant.nom}">
											<tr>
												<td width="30%"><strong><spring:message
															code="enviament.info.titular.nom" /></strong></td>
												<td>${enviament.interessat.representant.nom}</td>
											</tr>
										</c:if>
										<c:if test="${not empty enviament.interessat.representant.llinatge1}">
											<tr>
												<td><strong><spring:message
															code="enviament.info.titular.llinatges" /></strong></td>
												<td>${enviament.interessat.representant.llinatge1}<c:if
														test="${not empty interessat.representant.llinatge2}">${interessat.representant.llinatge2}</c:if>
												</td>
											</tr>
										</c:if>
									</c:when>
									<c:when test="${enviament.interessat.representant.tipus == 'ADMINISTRACIO'}">
										<tr>
											<td><strong><spring:message code="enviament.info.titular.nif" /></strong></td>
											<td>${enviament.interessat.representant.documentNum}</td>
										</tr>
									</c:when>
									<c:when test="${enviament.interessat.representant.tipus == 'PERSONA_JURIDICA'}">
										<tr>
											<td><strong><spring:message code="enviament.info.titular.nif" /></strong></td>
											<td>${enviament.interessat.representant.documentNum}</td>
										</tr>									
									</c:when>
								</c:choose>
								<c:if test="${not empty interessat.representant.telefon}">
									<tr>
										<td><strong><spring:message code="enviament.info.destinatari.telefon"/></strong></td>
										<td>${interessat.representant.telefon}</td>
									</tr>
								</c:if>
								<c:if test="${not empty interessat.representant.email}">
									<tr>
										<td><strong><spring:message code="enviament.info.interessat.representant.email"/></strong></td>
										<td>${interessat.representant.email}</td>
									</tr>
								</c:if>
							</tbody>
							</table>
						</div>
					</div>
				</c:if>
			</div>
		</div>

		<!------------------------------ TABPANEL NOTIFIC@ ------------------------------------->	
		<div role="tabpanel" class="tab-pane" id="estatNotifica" style="margin-top: 25px;">
			<c:if test="${notificacio.notificacioEstat == 'PENDENT'}">
				<div class="alert alert-warning well-sm" role="alert" style="margin-top: 1em">
					<spring:message code="enviament.info.notifica.no.enviada"/>
				</div>
			</c:if>
			<c:if test="${notificacio.notificacioEstat != 'PENDENT'}">

				<div class="row">
					<c:set var="datatColSize" value="12"/>
					<c:if test="${not empty enviament.enviamentCertificacioData}"><c:set var="datatColSize" value="5"/></c:if>
					<div class="col-sm-${datatColSize}">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title">
									<strong><spring:message code="enviament.info.seccio.notifica.datat"/></strong>
								</h3>
			 				</div>
							<table class="table table-bordered" style="width:100%">
							<tbody>
								<tr>
									<td width="30%"><strong><spring:message code="enviament.info.notifica.estat"/></strong></td>
									<td>
										<c:if test="${not empty notificacio.notificacioEstat}">
											<spring:message code="notificacio.notificacioEstat.enum.${notificacio.notificacioEstat}"/>
										</c:if>
									</td>
								</tr>
							</tbody>
							</table>
						</div>
					</div>
					<c:if test="${not empty enviament.enviamentCertificacioData}">
						<div class="col-sm-7">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h3 class="panel-title">
										<strong><spring:message code="enviament.info.seccio.notifica.certificacio"/></strong>
									</h3>
				 				</div>
								<table class="table table-bordered" style="width:100%">
								<tbody>
									<tr>
										<td width="30%"><strong><spring:message code="enviament.info.notifica.certificacio.data"/></strong></td>
										<td><fmt:formatDate value="${enviament.enviamentCertificacioData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
									</tr>
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.certificacio.origen"/></strong></td>
										<td><spring:message code="enviament.datat.origen.enum.${enviament.enviamentCertificacioOrigen}"/> (${enviament.enviamentCertificacioOrigen})</td>
									</tr>
									<tr>
										<td><strong><spring:message code="enviament.info.notifica.certificacio.document"/></strong></td>
										<td>
											<a href="<c:url value="/document/${enviamentId}/descarregarCertificacio"/>" class="btn btn-default btn-sm pull-right" title="<spring:message code="notificacio.info.document.descarregar"/>"> <span class="fa fa-download"></span></a>
										</td>
									</tr>
								</tbody>
								</table>
							</div>
						</div>
					</c:if>
				</div>
			</c:if>
		</div>		


	</div>
	<div id="modal-botons" class="text-right">
		<a href="<c:url value="/notificacions"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
