<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="notificacio.info.titol"/></title>
	<rip:modalHead/>
</head>
<body>
	<!---------------------------------------- TABLIST ------------------------------------------>
	<ul class="nav nav-tabs" role="tablist">
		<li class="active" role="presentation">
			<a href="#dades" aria-controls="dades" role="tab" data-toggle="tab"><spring:message code="notificacio.info.pipella.dades"/></a>
		</li>
		<c:if test="${notificacio.error}">
			<li role="presentation">
				<a href="#errors" class="text-danger" aria-controls="errors" role="tab" data-toggle="tab"><span class="fa fa-exclamation-triangle"></span> <spring:message code="notificacio.info.pipella.errors"/></a>
			</li>
		</c:if>
	</ul>
	<br/>
	<div class="tab-content">
		<!------------------------------ TABPANEL DADES ------------------------------------->
		<div class="tab-pane active in" id="dades" role="tabpanel">
			<dl class="dl-horizontal">

				<table class="table table-bordered">
					<tbody>
					<tr>
						<td><strong><spring:message code="notificacio.info.camp.document"/></strong></td>
						<td>${notificacio.document.nom}</td>
					</tr>
					<tr>				
						<td><strong><spring:message code="notificacio.info.camp.createdData"/></strong></td>
						<td><fmt:formatDate value="${notificacio.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
					</tr>
					<tr>				
						<td><strong><spring:message code="notificacio.info.camp.data"/></strong></td>
						<td><fmt:formatDate value="${notificacio.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
					</tr>
					<tr>				
						<td><strong><spring:message code="notificacio.info.camp.dataFinalitzada"/></strong></td>
						<td><fmt:formatDate value="${notificacio.processatData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
					</tr>										
					<tr>						
						<td><strong><spring:message code="notificacio.info.camp.tipus"/></strong></td>
						<td><spring:message code="notificacio.tipus.enum.${notificacio.tipus}"/></td>
					</tr>
					<tr>						
						<td><strong><spring:message code="notificacio.info.camp.estat"/></strong></td>
						<td><spring:message code="notificacio.estat.enum.${notificacio.estat}"/></td>
					</tr>
					<tr>						
						<td><strong><spring:message code="notificacio.info.camp.assumpte"/></strong></td>
						<td>${notificacio.assumpte}</td>				
					</tr>
					<tr>						
						<c:if test="${not empty notificacio.observacions}">
							<td><strong><spring:message code="notificacio.info.camp.observacions"/></strong></td>
							<td>${notificacio.observacions}</td>
						</c:if>				
					</tr>
					</tbody>
				</table>	
				
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title"><spring:message code="notificacio.info.camp.interessats"/></h3>
					</div>
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
							<c:forEach var="interessat" items="${notificacio.interessats}" varStatus="status">
								<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
									<td>
										<spring:message code="interessat.tipus.enum.${interessat.tipus}"/>
									</td>
									<td>${interessat.documentNum}</td>
									<c:choose>
										<c:when test="${interessat.tipus == 'PERSONA_FISICA'}">
											<td>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</td>
										</c:when>
										<c:when test="${interessat.tipus == 'PERSONA_JURIDICA'}">
											<td>${interessat.raoSocial}</td>
										</c:when>										
										<c:when test="${interessat.tipus == 'ADMINISTRACIO'}">
											<td>${interessat.organNom}</td>
										</c:when>
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
													<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
												</dl>
											</div>
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
																	<spring:message code="interessat.tipus.enum.${representant.tipus}"/>
																</td>
																<td>${representant.documentNum}</td>
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
																				<dt><spring:message code="interessat.form.camp.codiPostal"/></dt><dd>${representant.codiPostal}</dd>
																			</dl>
																		</div>
																		<div class="col-xs-6">
																			<dl class="dl-horizontal">
																				<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${representant.email}</dd>
																				<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${representant.telefon}</dd>
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
				</div>

			</dl>
		</div>
		<!------------------------------ TABPANEL ERRORS ------------------------------------->
		<div class="tab-pane" id="errors" role="tabpanel">
			<c:if test="${notificacio.error}">
				<div class="alert well-sm alert-danger alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="notificacio.info.errors.enviament"/>
<%-- 					<a href="reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="notificacio.info.errors.enviament.reintentar"/></a> --%>
 				</div> 
<!-- 				<div class="panel panel-default"> -->
<!-- 					<div class="panel-heading"> -->
<%-- 						<h4 class="panel-title"><spring:message code="notificacio.info.error.enviament"/></h4> --%>
<!-- 					</div> -->
<!-- 					<div class="panel-body"> -->
<!-- 						<br/> -->
<!-- 						<dl class="dl-horizontal"> -->
<%-- 							<dt><spring:message code="notificacio.info.camp.error.data.darrer"/></dt> --%>
<%-- 							<dd><fmt:formatDate value="${notificacio.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd> --%>
<%-- 							<dt><spring:message code="notificacio.info.camp.error.intents"/></dt> --%>
<%-- 							<dd>${notificacio.intentNum}</dd> --%>
<!-- 						</dl> -->
						<pre style="height:300px; margin: 12px">${notificacio.errorDescripcio}</pre>
<!-- 					</div> -->
<!-- 				</div> -->
			</c:if>
<%--
			<c:if test="${notificacio.processamentError}">
				<div class="alert well-sm alert-danger alert-dismissable">
					<span class="fa fa-exclamation-triangle"></span>
					<spring:message code="notificacio.info.errors.processament"/>
					<a href="reintentar" class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span> <spring:message code="notificacio.info.errors.processament.reintentar"/></a>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title"><spring:message code="notificacio.info.error.processament"/></h4>
					</div>
					<div class="panel-body">
						<br/>
						<dl class="dl-horizontal">
							<dt><spring:message code="notificacio.info.camp.error.data.darrer"/></dt>
							<dd><fmt:formatDate value="${notificacio.processamentData}" pattern="dd/MM/yyyy HH:mm:ss"/></dd>
							<dt><spring:message code="notificacio.info.camp.error.intents"/></dt>
							<dd>${notificacio.processamentCount}</dd>
						</dl>
						<pre style="height:300px; margin: 12px">${notificacio.processamentErrorDescripcio}</pre>
					</div>
				</div>
			</c:if>
--%>
		</div>
	</div>
	<div id="modal-botons" class="well">
		<a href="<c:url value="/contenidor/${notificacio.document.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
