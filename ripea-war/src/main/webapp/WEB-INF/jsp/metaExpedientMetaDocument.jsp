<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="metadocument.list.titol"/>: ${metaExpedient.nom}</title>
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
	<c:if test="${!esRevisor && !bloquejarCamps}">
		<div class="text-right" data-toggle="botons-titol">
			<a class="btn btn-default" href="metaDocument/new" data-toggle="modal" data-datatable-id="metadocuments"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metadocument.list.boto.nou"/></a>
		</div>
	</c:if>
	
	<table
		id="metadocuments"
		data-toggle="datatable"
		data-url="<c:url value="metaDocument/datatable"/>"
		data-info-type="search"
		data-default-order="2"
		data-default-dir="asc"
		class="table table-striped table-bordered"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" width="4%">#</th>
				<th data-col-name="codi"><spring:message code="metadocument.list.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="metadocument.list.columna.nom"/></th>
				<th data-col-name="actiu" data-template="#cellActiuTemplate">
					<spring:message code="metadocument.list.columna.actiu"/>
					<script id="cellActiuTemplate" type="text/x-jsrender">
						{{if actiu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="perDefecte" data-template="#cellPerDefecteTemplate">
					<spring:message code="metadocument.list.columna.perdefecte"/>
					<script id="cellPerDefecteTemplate" type="text/x-jsrender">
						{{if perDefecte}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="metaDadesCount" data-template="#cellMetaDadesTemplate" data-orderable="false" width="10%">
					<script id="cellMetaDadesTemplate" type="text/x-jsrender">
						<a href="<c:url value="/metaDocument/{{:id}}/metaDada"/>" class="btn btn-default"><span class="fa fa-file-alt"></span>&nbsp;<spring:message code="metaexpedient.list.boto.meta.dades"/>&nbsp;<span class="badge">{{:metaDadesCount}}</span></a>
					</script>
				</th>
				<c:if test="${!esRevisor}">
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="<c:url value="metaDocument/{{:id}}"/>" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									<c:if test="${!bloquejarCamps}">
									{{if !actiu}}
									<li><a href="<c:url value="metaDocument/{{:id}}/enable"/>" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
									{{else}}
									<li><a href="<c:url value="metaDocument/{{:id}}/disable"/>" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
									{{/if}}
									<li><a href="<c:url value="metaDocument/{{:id}}/delete"/>" data-toggle="ajax" data-confirm="<spring:message code="metadocument.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
									{{if !perDefecte}}
										<li><a href="<c:url value="metaDocument/{{:id}}/default"/>" data-toggle="ajax"><span class="fa fa-check-square-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.defecte"/></a></li>
									{{else}}
										<li><a href="<c:url value="metaDocument/{{:id}}/default/remove"/>" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.defecte.remove"/></a></li>
									{{/if}}
									</c:if>
								</ul>
							</div>
						</script>
					</th>
				</c:if>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="${!esRevisor ? '/metaExpedient?mantenirPaginacio=true' : '/metaExpedientRevisio?mantenirPaginacio=true'}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
	<div class="clearfix"></div>
</body>