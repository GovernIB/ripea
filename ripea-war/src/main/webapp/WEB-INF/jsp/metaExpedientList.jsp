<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="metaexpedient.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="metaExpedient/new" data-toggle="modal" data-datatable-id="metaexpedients"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.list.boto.nou"/></a>
	</div>
	<script id="rowhrefTemplate" type="text/x-jsrender">nodeco/metaExpedient/{{:id}}</script>
	<table 
		id="metaexpedients" 
		data-toggle="datatable" 
		data-url="<c:url value="/metaExpedient/datatable"/>" 
		data-info-type="search" 
		data-default-order="1" 
		data-default-dir="asc" 
		class="table table-striped table-bordered"
		data-rowhref-template="#rowhrefTemplate" 
		data-rowhref-toggle="modal"
		data-save-state="true"
		data-mantenir-paginacio="${mantenirPaginacio}">
		<thead>
			<tr>
				<th data-col-name="codi"><spring:message code="metaexpedient.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="metaexpedient.list.columna.nom"/></th>
				<th data-col-name="actiu" data-template="#cellActiuTemplate">
					<spring:message code="metaexpedient.list.columna.actiu"/>
					<script id="cellActiuTemplate" type="text/x-jsrender">
						{{if actiu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="metaDocumentsCount" data-template="#cellMetaDocumentsTemplate" data-orderable="false" width="10%">
					<script id="cellMetaDocumentsTemplate" type="text/x-jsrender">
						<a href="metaExpedient/{{:id}}/metaDocument" class="btn btn-default"><span class="fa fa-file-alt"></span>&nbsp;<spring:message code="metaexpedient.list.boto.meta.documents"/>&nbsp;<span class="badge">{{:metaDocumentsCount}}</span></a>
					</script>
				</th>
				<th data-col-name="metaDadesCount" data-template="#cellMetaDadesTemplate" data-orderable="false" width="10%">
					<script id="cellMetaDadesTemplate" type="text/x-jsrender">
						<a href="metaExpedient/{{:id}}/metaDada" class="btn btn-default"><span class="fa fa-file-alt"></span>&nbsp;<spring:message code="metaexpedient.list.boto.meta.dades"/>&nbsp;<span class="badge">{{:metaDadesCount}}</span></a>
					</script>
				</th>
				<%--th data-col-name="arxiusCount" data-template="#cellArxiusTemplate" data-orderable="false" width="10%">
					<script id="cellArxiusTemplate" type="text/x-jsrender">
						<a href="metaExpedient/{{:id}}/arxiu" class="btn btn-default"><span class="fa fa-file-alt"></span>&nbsp;<spring:message code="metaexpedient.list.boto.arxius"/>&nbsp;<span class="badge">{{:arxiusCount}}</span></a>
					</script>
				</th--%>
				<th data-col-name="permisosCount" data-template="#cellPermisosTemplate" data-orderable="false" width="10%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="metaExpedient/{{:id}}/permis" class="btn btn-default"><span class="fa fa-file-alt"></span>&nbsp;<spring:message code="metaexpedient.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="metaExpedient/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !actiu}}
								<li><a href="metaExpedient/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="metaExpedient/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="metaExpedient/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metaexpedient.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="metaExpedient/new" data-rdt-link-modal="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.list.boto.nou"/></a></p>
	</script>
</body>