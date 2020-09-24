<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="metaexpedient.arxiu.titol"/></title>
	<meta name="subtitle" content="${metaExpedient.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="../../metaExpedient/${metaExpedient.id}/arxiu/new" data-toggle="modal" data-datatable-id="arxius"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.arxiu.boto.nova.relacio.arxiu"/></a>
	</div>
	<table id="arxius" data-toggle="datatable" data-url="<c:url value="/metaExpedient/${metaExpedient.id}/arxiu/datatable"/>" data-search-enabled="false" data-paging-enabled="false" data-default-order="0" data-default-dir="asc" class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="nom"><spring:message code="metaexpedient.list.columna.nom"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a class="btn btn-default" href="../../metaExpedient/${metaExpedient.id}/arxiu/{{:id}}/delete" data-toggle="ajax=" data-confirm="<spring:message code="metaexpedient.arxiu.confirmacio.deslligar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="../../metaExpedient/${metaExpedient.id}/arxiu/new" data-rdt-link-modal="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.arxiu.boto.nova.relacio.arxiu"/></a></p>
	</script>
	<a href="<c:url value="/metaExpedient"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
</body>
</html>
