<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="arxiu.metaexpedient.titol"/></title>
	<meta name="subtitle" content="${arxiu.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="../../arxiuAdmin/${arxiu.id}/metaExpedient/new" data-toggle="modal" data-datatable-id="metaexpedients"><span class="fa fa-plus"></span>&nbsp;<spring:message code="arxiu.metaexpedient.boto.nova.relacio.metaexpedient"/></a>
	</div>
	<table id="metaexpedients" data-toggle="datatable" data-url="<c:url value="/arxiuAdmin/${arxiu.id}/metaExpedient/datatable"/>" data-search-enabled="false" data-paging-enabled="false" data-default-order="1" data-default-dir="asc" class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="codi"><spring:message code="arxiu.metaexpedient.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="arxiu.metaexpedient.list.columna.nom"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a class="btn btn-default" href="../../arxiuAdmin/${arxiu.id}/metaExpedient/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="arxiu.metaexpedient.confirmacio.deslligar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="/arxiuAdmin/unitat/${arxiu.unitatCodi}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
</body>
</html>
