<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="regla.list.titol"/></title>
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
	$('#regles').on('dragupdate.dataTable', function (event, itemId, index) {
		$.ajax({
			url: "ajax/regla/" + itemId + "/move/" + index,
			async: false
		});
	});
});
</script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="regla/new" data-toggle="modal" data-datatable-id="regles"><span class="fa fa-plus"></span>&nbsp;<spring:message code="regla.list.boto.nova"/></a>
	</div>
	<table id="regles" data-toggle="datatable" data-url="<c:url value="/regla/datatable"/>" data-drag-enabled="true" data-info-type="search" data-default-order="0" data-default-dir="asc" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="ordre" data-visible="false"></th>
				<th data-col-name="nom" data-orderable="false"><spring:message code="regla.list.columna.nom"/></th>
				<th data-col-name="tipus" data-orderable="false" data-renderer="enum(ReglaTipusEnumDto)">
					<spring:message code="regla.list.columna.tipus"/>
				</th>
				<th data-col-name="assumpteCodi" data-orderable="false"><spring:message code="regla.list.columna.assumpte.codi"/></th>
				<th data-col-name="unitatCodi" data-orderable="false"><spring:message code="regla.list.columna.unitat"/></th>
				<th data-col-name="activa" data-template="#cellActivaTemplate" data-orderable="false">
					<spring:message code="regla.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="regla/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="regla/{{:id}}/up" data-toggle="ajax"><span class="fa fa-arrow-up"></span>&nbsp;&nbsp;<spring:message code="comu.boto.amunt"/></a></li>
								<li><a href="regla/{{:id}}/down" data-toggle="ajax"><span class="fa fa-arrow-down"></span>&nbsp;&nbsp;<spring:message code="comu.boto.avall"/></a></li>
								{{if !activa}}
								<li><a href="regla/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="regla/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="regla/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="entitat.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>