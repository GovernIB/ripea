<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="metadada.list.titol"/></title>
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
		<a class="btn btn-default" href="metaDada/new" data-toggle="modal" data-datatable-id="metadades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metadada.list.boto.nova"/></a>
	</div>
	<script id="rowhrefTemplate" type="text/x-jsrender">nodeco/metaDada/{{:id}}</script>	
	<table id="metadades" data-toggle="datatable" data-url="<c:url value="/metaDada/datatable"/>" data-info-type="search" data-default-order="1" data-default-dir="asc" class="table table-striped table-bordered"
	data-rowhref-template="#rowhrefTemplate" data-rowhref-toggle="modal">
		<thead>
			<tr>
				<th data-col-name="codi"><spring:message code="metadada.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="metadada.list.columna.nom"/></th>
				<th data-col-name="tipus" data-renderer="enum(MetaDadaTipusEnumDto)">
					<spring:message code="metadada.list.columna.tipus"/>
				</th>
				<th data-col-name="globalExpedient" data-template="#cellGlobalExpedientTemplate">
					<spring:message code="metadada.list.columna.global"/>
					<script id="cellGlobalExpedientTemplate" type="text/x-jsrender">
						{{if globalExpedient}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="globalDocument" data-template="#cellGlobalDocumentTemplate" data-visible="false">
					<spring:message code="metadada.list.columna.global"/>
					<script id="cellGlobalDocumentTemplate" type="text/x-jsrender">
						{{if globalDocument}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="activa" data-template="#cellActivaTemplate">
					<spring:message code="metadada.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="metaDada/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !activa}}
								<li><a href="metaDada/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="metaDada/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="metaDada/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metadada.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>