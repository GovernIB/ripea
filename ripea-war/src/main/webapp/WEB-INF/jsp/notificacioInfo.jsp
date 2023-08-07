<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	
	<c:choose>
		<c:when test="${notificacio.tipus == 'NOTIFICACIO'}">
			<title><spring:message code="notificacio.info.titol"/></title>
		</c:when>
		<c:otherwise>
			<title><spring:message code="notificacio.info.titol.comunicacio"/></title>
		</c:otherwise>
	</c:choose>
	
	
	
	<rip:modalHead/>
	
<style type="text/css">
.panel>.table-bordered>tbody>tr:last-child>td{
border: 1px solid #ddd;
}
</style>	

<script type="text/javascript">
$(document).ready(function(){
	//$('.numRegistre').text('tetete'); 
	//recuperar informaciço
	$('.registre').on('click', function(){
		$(document).on({
			ajaxStart: function() {
				console.log("loading...");   
			},
			ajaxStop: function() {
				console.log("loaded...");   
			}    
		});
		
//		$.ajax({
//			type: 'GET',
//			url: "<c:url value="/document/${notificacio.document.id}/notificacio/${notificacio.id}/registre/info"/>",
//			success: function(data) {
//				$('.numRegistre').text(data.numRegistreFormatat); 
//				$('.dataRegistre').text(data.dataRegistre); 
//			}
//		});
//	});
	
});
</script>
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

				<!----------------- NOTIFICACIO INFO ------------------->
				<div class="panel panel-default">
					<div class="panel-heading">
						<c:choose>
							<c:when test="${notificacio.tipus == 'NOTIFICACIO'}">
								<h3 class="panel-title"><strong><spring:message code="notificacio.info.panel.notificacio"/></strong></h3>
							</c:when>
							<c:otherwise>
								<h3 class="panel-title"><strong><spring:message code="notificacio.info.panel.comunicacio"/></strong></h3>
							</c:otherwise>
						</c:choose>
					</div>				
				
					<table class="table table-bordered">
						<tbody>
						<tr>						
							<td><strong><spring:message code="notificacio.info.camp.emisor"/></strong></td>
							<td>${notificacio.emisor.nom} <br> <small>${notificacio.emisor.codi}</small> </td>				
						</tr>						
						<tr>						
							<td><strong><spring:message code="notificacio.info.camp.concepte"/></strong></td>
							<td>${notificacio.assumpte}</td>				
						</tr>						
						<tr>						
							<td><strong><spring:message code="notificacio.info.camp.descripcio"/></strong></td>
							<td>${notificacio.observacions}</td>				
						</tr>
						<tr>						
							<td><strong><spring:message code="notificacio.info.camp.estat"/></strong></td>
							<td><spring:message code="notificacio.notificacioEstat.enum.${notificacio.notificacioEstat}"/></td>
						</tr>						
						
						<tr>				
							<td><strong><spring:message code="notificacio.info.camp.data"/></strong></td>
							<td><fmt:formatDate value="${notificacio.createdDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						</tr>
						<c:if test="${notificacio.notificacioEstat=='FINALITZADA' || notificacio.notificacioEstat=='PROCESSADA'}">						
							<tr>				
								<td><strong><spring:message code="notificacio.info.camp.dataFinalitzada"/></strong></td>
								<td><fmt:formatDate value="${notificacio.processatData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
							</tr>	
						</c:if>									
						<tr>						
							<td><strong><spring:message code="notificacio.info.camp.tipus"/></strong></td>
							<td><spring:message code="notificacio.tipus.enum.${notificacio.tipus}"/></td>
						</tr>				
						</tr>
						<c:if test="${notificacio.notificacioEstat!='PENDENT'}">
							<tr><td colspan="2">
								<a href="<rip:modalUrl value='/document/${notificacio.document.id}/notificacio/${notificacio.id}/descarregarJustificantEnviamentNotib'/>" onerror="location.reload();" class="btn btn-default btn-sm pull-right">
									<spring:message code="notificacio.info.camp.justificant.enviament.notib.boto"/> <span class="fa fa-download"></span>
								</a>
							</td></tr>
						</c:if>					
						</tbody>
					</table>	
				</div>
				
				
				
				
				<!----------------- DOCUMENT ------------------->
				<div class="panel panel-default">
					<div class="panel-heading">
						<c:choose>
							<c:when test="${notificacio.tipus == 'NOTIFICACIO'}">
								<h3 class="panel-title"><strong><spring:message code="notificacio.info.panel.document.notificacio"/></strong></h3>
							</c:when>
							<c:otherwise>
								<h3 class="panel-title"><strong><spring:message code="notificacio.info.panel.document.comunicacio"/></strong></h3>
							</c:otherwise>
						</c:choose>
					</div>				
				
					<table class="table table-bordered">
						<tbody>
						<tr>
							<td><strong><spring:message code="notificacio.info.camp.arxiu.nom"/></strong></td>
							<td>${notificacio.document.fitxerNom} 
								<a href="<c:url value="/contingut/${notificacio.document.pareId}/document/${notificacio.document.id}/descarregar"/>" class="btn btn-default btn-sm pull-right">
									<spring:message code="notificacio.info.camp.document.btn"/> <span class="fa fa-download"></span>
								</a>
							</td>
						</tr>
										
						</tbody>
					</table>	
				</div>				
				
				
				<!----------------- ENVIAMENTS INFO ------------------->
				<c:forEach var="enviament" items="${notificacio.documentEnviamentInteressats}" varStatus="status">
					<c:set var="interessat" value="${enviament.interessat}"/>				
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><strong><spring:message code="notificacio.info.camp.enviament"/> ${status.index+1}</strong></h3>
						</div>
						<table class="table table-bordered" width="100%">
						  <!------- INTERESSAT ------->							
						  <tr>
						    <td style="width: 15%">
						      <strong><spring:message code="notificacio.info.camp.interessat"/></strong>
						    </td>
						    <td>
								<table class="table table-bordered" style="margin-bottom: 0px; border: none;">
									<tbody>
										<tr>
											<td style="border: none;">
												<c:choose>
													<c:when test="${interessat.tipus == 'PERSONA_FISICA'}">
														${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}
													</c:when>
													<c:when test="${interessat.tipus == 'PERSONA_JURIDICA'}">
														${interessat.raoSocial}
													</c:when>										
													<c:when test="${interessat.tipus == 'ADMINISTRACIO'}">
														${interessat.organNom}
													</c:when>
												</c:choose>
												<c:if test="${not empty interessat.documentNum}">
													(${interessat.documentNum})
												</c:if>												
											</td>
											<td style="border: none; width:1%">
												<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
													<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}">
														<span class="fa fa-caret-down"></span>
													</button>
												</c:if>
											</td>
										</tr>
										<tr class="collapse detall" id="detalls_${status.index}">
											<td colspan="4" style="border: none;">
												<div class="row">
													<div class="col-xs-6">
														<dl class="dl-horizontal">
															<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${interessat.paisNom}</dd>
															<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${interessat.provinciaNom}</dd>											
															<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${interessat.municipiNom}</dd>
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
									</tbody>
								</table>
								
								
								
						   	</td>
						  </tr>
						  <!------- ESTAT ------->	
						  <tr>
						    <td style="width: 15%">
						      	<strong><spring:message code="notificacio.info.camp.estat"/></strong>
						    </td>
						    <td>
						    	<c:if test="${not empty enviament.enviamentDatatEstat}">
						    	 	<spring:message code="notificacio.enviamentEstat.enum.${enviament.enviamentDatatEstat}"/>
						    	 </c:if>						    
						   	</td>					    
						  </tr>
						  <!------- ESTAT DATA ------->	
						  <c:if test="${not empty enviament.enviamentDatatData}">
							  <tr>
							    <td style="width: 15%">
							    	<strong><spring:message code="notificacio.info.camp.estatData"/></strong>
							    </td>
							    <td>
							    	${enviament.enviamentDatatData}
							   	</td>					    
							  </tr>	
						  </c:if>	
						  <!------- ESTAT ORIGEN ------->	
						  <c:if test="${not empty enviament.enviamentDatatOrigen}">
							  <tr>
							    <td style="width: 15%">
							      	<strong><spring:message code="notificacio.info.camp.estatOrigen"/></strong>
							    </td>
							    <td>
							    	${enviament.enviamentDatatOrigen}
							   	</td>					    
							  </tr>		
						  </c:if>
						  
						  <!------- REGISTRE ------->	
						  <tr>
						    <td style="width: 15%">
						      	<strong><spring:message code="notificacio.info.camp.registre"/></strong>
						    </td>
						    <td>
								<c:choose>
									<c:when test="${not empty enviament.registreNumeroFormatat}">						  
										<table class="table table-striped">
											<tbody>
												<tr>
													<td><strong><spring:message code="notificacio.info.camp.num.registre"/></strong></td>
													<td>${enviament.registreNumeroFormatat}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="notificacio.info.camp.data.registre"/></strong></td>
													<td><fmt:formatDate value="${enviament.registreData}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
												</tr>
											</tbody>
								 		</table>
								 	</c:when>
		  							<c:otherwise>
			    						<spring:message code="notificacio.info.camp.registre.noregistrat"/>
			    					</c:otherwise>
			    				</c:choose>
						   	</td>					    
						  </tr>		

	    				
						  <!------- CERTIFICACIO ------->	
						  <tr>
						    <td style="width: 15%">
						      	<strong><spring:message code="notificacio.info.camp.certifiacio"/></strong>
						    </td>
						    <td>
								<c:choose>
									<c:when test="${not empty enviament.enviamentCertificacioData}">
										<table class="table table-striped" style="margin-bottom: 0px">
											<tbody>
												<tr>
													<td><strong><spring:message code="notificacio.info.camp.certifiacioData"/></td>
													<td>${enviament.enviamentCertificacioData}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="notificacio.info.camp.certifiacioOrigen"/></td>
													<td>${enviament.enviamentCertificacioOrigen}</td>
												</tr>
												<tr>	
													<td><strong><spring:message code="notificacio.info.camp.certifiacioDecarregar"/></td>												
													<td>
														<a href="<c:url value="/document/${enviament.id}/descarregarCertificacio"/>" class="btn btn-default btn-sm pull-right"> <span class="fa fa-download"></span></a>
													</td>
												</tr>
											</tbody>
										</table>		
									</c:when>
									<c:otherwise>
										<spring:message code="notificacio.info.camp.certifiacio.sensecertifiacio"/>
									</c:otherwise>
								</c:choose>					    
						   	</td>					    
						  </tr>	
						  <!------- ERROR ------->	
						  <c:if test="${not empty enviament.errorDescripcio}">
							  <tr>
							    <td style="width: 15%">
									<div class="alert well-sm alert-danger alert-dismissable">
										<span class="fa fa-exclamation-triangle"></span>
										<strong><spring:message code="notificacio.info.camp.error"/></strong>
					 				</div> 							    
							    </td>
							    <td>
							    	${enviament.errorDescripcio}
							   	</td>					    
							  </tr>	
						  </c:if>						  				  
					</table>	
					</div>
				</c:forEach>	
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
