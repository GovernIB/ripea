<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="metadocument.list.titol"/></title>
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
		<a class="btn btn-default" href="metaDocument/new" data-toggle="modal" data-datatable-id="metadocuments"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metadocument.list.boto.nou"/></a>
	</div>
	<script id="rowhrefTemplate" type="text/x-jsrender">nodeco/metaDocument/{{:id}}</script>
	<table id="metadocuments" data-toggle="datatable" data-url="<c:url value="/metaDocument/datatable"/>" data-info-type="search" data-default-order="2" data-default-dir="asc" class="table table-striped table-bordered" style="width:100%"
	data-rowhref-template="#rowhrefTemplate" data-rowhref-toggle="modal">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				<th data-col-name="codi"><spring:message code="metadocument.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="metadocument.list.columna.nom"/></th>
				<th data-col-name="globalExpedient" data-template="#cellGlobalTemplate">
					<spring:message code="metadocument.list.columna.global"/>
					<script id="cellGlobalTemplate" type="text/x-jsrender">
						{{if globalExpedient}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="actiu" data-template="#cellActiuTemplate">
					<spring:message code="metadocument.list.columna.actiu"/>
					<script id="cellActiuTemplate" type="text/x-jsrender">
						{{if actiu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="metaDadesCount" data-template="#cellMetaDadesTemplate" data-orderable="false" width="10%">
					<script id="cellMetaDadesTemplate" type="text/x-jsrender">
						<a href="metaDocument/{{:id}}/metaDada" class="btn btn-default"><span class="fa fa-file-alt"></span>&nbsp;<spring:message code="metadocument.list.boto.meta.dades"/>&nbsp;<span class="badge">{{:metaDadesCount}}</span></a>
					</script>
				</th>
				<th data-col-name="permisosCount" data-template="#cellPermisosTemplate" data-orderable="false" width="10%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="metaDocument/{{:id}}/permis" class="btn btn-default"><span class="fa fa-file-alt"></span>&nbsp;<spring:message code="metadocument.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="metaDocument/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !actiu}}
								<li><a href="metaDocument/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="metaDocument/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="metaDocument/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metadocument.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>