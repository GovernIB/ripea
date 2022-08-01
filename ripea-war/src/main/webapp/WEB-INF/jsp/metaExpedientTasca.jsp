<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="metaexpedient.tasca.titol"/>: ${metaExpedient.nom}</title>
	<meta name="subtitle" content="${metaExpedient.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<c:if test="${!esRevisor && !bloquejarCamps}">
		<div class="text-right" data-toggle="botons-titol">
			<a class="btn btn-default" href="tasca/new" data-toggle="modal" data-datatable-id="metadades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.tasca.boto.afegir"/></a>
		</div>
	</c:if>
	<table id="metadades" data-toggle="datatable" data-url="<c:url value="/metaExpedient/${metaExpedient.id}/tasca/datatable"/>" data-info-type="search" data-default-order="0" data-default-dir="asc" class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="codi" data-orderable="false"><spring:message code="metaexpedient.tasca.columna.codi"/></th>
				<th data-col-name="nom" data-orderable="false"><spring:message code="metaexpedient.tasca.columna.nom"/></th>
				<th data-col-name="responsable" data-orderable="false"><spring:message code="metaexpedient.tasca.columna.responsable"/></th>
				<th data-col-name="dataLimit" data-converter="date"><spring:message code="metaexpedient.tasca.form.camp.dataLimit"/></th>
				<th data-col-name="estatColorCrearTasca" data-visible="false"></th>
				<th data-col-name="estatNomCrearTasca" data-template="#cellTascaCrearTemplate" data-orderable="false">
					<spring:message code="metaexpedient.tasca.list.camp.estat.crearTasca"/>
					<script id="cellTascaCrearTemplate" type="text/x-jsrender">
						{{if estatNomCrearTasca}}
							<span class="color-legend-min" {{if estatColorCrearTasca}}style="background-color: {{:estatColorCrearTasca}};"{{else}}style="border: dashed 1px #AAA;"{{/if}}></span>
							<span style="vertical-align: super;">{{:estatNomCrearTasca}}</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="estatColorFinalitzarTasca" data-visible="false"></th>
				<th data-col-name="estatNomFinalitzarTasca" data-template="#cellTascaFiTemplate" data-orderable="false">
					<spring:message code="metaexpedient.tasca.list.camp.estat.finalitzarTasca"/>
					<script id="cellTascaFiTemplate" type="text/x-jsrender">
						{{if estatNomFinalitzarTasca}}
							<span class="color-legend-min" {{if estatColorFinalitzarTasca}}style="background-color: {{:estatColorFinalitzarTasca}};"{{else}}style="border: dashed 1px #AAA;"{{/if}}></span>
							<span style="vertical-align: super;">{{:estatNomFinalitzarTasca}}</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="activa" data-template="#cellActivaTemplate" data-orderable="false">
					<spring:message code="metaexpedient.tasca.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<c:choose>
									<c:when test="${consultar}">
										<li><a href="tasca/{{:id}}" data-toggle="modal"><span class="fa fa-search"></span>&nbsp;&nbsp;<spring:message code="comu.boto.consultar"/></a></li>
									</c:when>
									<c:otherwise>
										<li><a href="tasca/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									</c:otherwise>
								</c:choose>
								<c:if test="${!bloquejarCamps}">
								{{if !activa}}
								<li><a href="tasca/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="tasca/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="tasca/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metaexpedient.tasca.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</c:if>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="${!esRevisor ? '/metaExpedient' : '/metaExpedientRevisio'}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
</body>