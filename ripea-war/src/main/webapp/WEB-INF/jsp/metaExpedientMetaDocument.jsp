<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="metaexpedient.metadocument.titol"/></title>
	<meta name="subtitle" content="${metaExpedient.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script>
var multiplicitatText = new Array();
<c:forEach var="option" items="${multiplicitatEnumOptions}">
multiplicitatText["${option.value}"] = "<spring:message code="${option.text}"/>";
</c:forEach>
</script>
</head>
<body>
	<div class="pull-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="../../metaExpedient/${metaExpedient.id}/metaDocument/new" data-toggle="modal" data-datatable-id="metadocuments">
			<span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.metadocument.boto.afegir"/>
		</a>
	</div>
	<table id="metadocuments" data-toggle="datatable" data-url="<c:url value="/metaExpedient/${metaExpedient.id}/metaDocument/datatable"/>" data-search-enabled="false" data-paging-enabled="false" data-ordering="false" class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="metaDocument.codi"><spring:message code="metaexpedient.metadada.columna.codi"/></th>
				<th data-col-name="metaDocument.nom"><spring:message code="metaexpedient.metadada.columna.nom"/></th>
				<th data-col-name="multiplicitat" data-renderer="enum(MultiplicitatEnumDto)"><spring:message code="metaexpedient.metadada.columna.multiplicitat"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="../../metaExpedient/${metaExpedient.id}/metaDocument/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="../../metaExpedient/${metaExpedient.id}/metaDocument/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metaexpedient.metadocument.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="/metaExpedient?mantenirPaginacio=true"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
</body>