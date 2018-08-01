<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="metaexpedient.metadada.titol"/></title>
	<meta name="subtitle" content="${metaExpedient.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('#metadades').on('dragupdate.dataTable', function (event, itemId, index) {
		$.ajax({
			url: "../../ajax/metaExpedient/${metaExpedient.id}/metaDada/" + itemId + "/move/" + index,
			async: false
		});
	});
});
</script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="metaDada/new" data-toggle="modal" data-datatable-id="metadades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.metadada.boto.afegir"/></a>
	</div>
	<table id="metadades" data-toggle="datatable" data-url="<c:url value="/metaExpedient/${metaExpedient.id}/metaDada/datatable"/>" data-drag-enabled="true" data-info-type="search" data-default-order="0" data-default-dir="asc" class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="ordre" data-visible="false"></th>
				<th data-col-name="metaDada.codi" data-orderable="false"><spring:message code="metaexpedient.metadada.columna.codi"/></th>
				<th data-col-name="metaDada.nom" data-orderable="false"><spring:message code="metaexpedient.metadada.columna.nom"/></th>
				<th data-col-name="metaDada.tipus" data-renderer="enum(MetaDadaTipusEnumDto)" data-orderable="false"><spring:message code="metaexpedient.metadada.columna.tipus"/></th>
				<th data-col-name="multiplicitat" data-renderer="enum(MultiplicitatEnumDto)" data-orderable="false"><spring:message code="metaexpedient.metadada.columna.multiplicitat"/></th>
				<th data-col-name="metaDada.activa" data-template="#cellActivaTemplate" data-orderable="false">
					<spring:message code="metaexpedient.metadada.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if metaDada.activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="metaDada/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="metaDada/{{:id}}/up" data-toggle="ajax"><span class="fa fa-arrow-up"></span>&nbsp;&nbsp;<spring:message code="comu.boto.amunt"/></a></li>
								<li><a href="metaDada/{{:id}}/down" data-toggle="ajax"><span class="fa fa-arrow-down"></span>&nbsp;&nbsp;<spring:message code="comu.boto.avall"/></a></li>
								<li><a href="metaDada/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metaexpedient.metadada.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<c:if test="${not empty metaDadesGlobals}">
		<div class="panel panel-default">
			<div class="panel-heading" data-toggle="collapse" data-target="#metadadesGlobalsTable">
				<spring:message code="metaexpedient.metadada.globals"/> <span class="badge">${fn:length(metaDadesGlobals)}</span>
			</div>
  			<table id="metadadesGlobalsTable" class="table table-striped table-bordered collapse" style="width:100%">
  			<thead>
  				<tr>
  					<th><spring:message code="metaexpedient.metadada.columna.codi"/></th>
  					<th><spring:message code="metaexpedient.metadada.columna.nom"/></th>
  					<th><spring:message code="metaexpedient.metadada.columna.tipus"/></th>
  					<th><spring:message code="metaexpedient.metadada.columna.multiplicitat"/></th>
  					<th><spring:message code="metaexpedient.metadada.columna.activa"/></th>
  				</tr>
  			</thead>
  			<tbody>
  				<c:forEach var="metaDada" items="${metaDadesGlobals}">
	  				<tr>
	  					<td>${metaDada.codi}</td>
	  					<td>${metaDada.nom}</td>
	  					<td><spring:message code="meta.dada.tipus.enum.${metaDada.tipus}"/></td>
	  					<td><spring:message code="multiplicitat.enum.${metaDada.globalMultiplicitat}"/></td>
	  					<td><c:if test="${metaDada.activa}"><span class="fa fa-check"></span></c:if></td>
	  				</tr>
  				</c:forEach>
  			</tbody>
  			</table>
  		</div>
	</c:if>
	<a href="<c:url value="/metaExpedient"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
</body>