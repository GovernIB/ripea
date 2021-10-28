<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="metaNodeNom" value="${metaExpedient.nom}"/>
<c:if test="${not empty metaDocument}"><c:set var="metaNodeNom" value="${metaDocument.nom}"/></c:if>
<html>
<head>
	<title>
	    <c:choose>
			<c:when test="${not empty metaDocument}"><spring:message code="metadada.tipdoc.list.titol"/>: ${metaNodeNom}</c:when>
			<c:otherwise><spring:message code="metadada.tipexp.list.titol"/>: ${metaNodeNom}</c:otherwise>
		</c:choose>
	</title>
	<meta name="subtitle" content="${metaNodeNom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	
<script type="text/javascript">
$(document).ready(function() {
	$('#metadades').on('dragupdate.dataTable', function (event, itemId, index) {
		$.ajax({

		    <c:choose>
				<c:when test="${not empty metaDocument}">
				url: "<c:url value="/ajax/metaExpedient/metaDada/"/>" + ${metaDocument.id} + "/" + itemId + "/move/" + index,
				</c:when>
				<c:otherwise>
					url: "<c:url value="/ajax/metaExpedient/metaDada/"/>" + ${metaExpedient.id} + "/" + itemId + "/move/" + index,				
				</c:otherwise>
			</c:choose>
			
			async: false
		});
	});
});
</script>	
</head>
<body>
	<c:if test="${!esRevisor && !bloquejarCamps}">
		<div class="text-right" data-toggle="botons-titol">
			<a class="btn btn-default" href="metaDada/new" data-toggle="modal" data-datatable-id="metadades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metadada.list.boto.nova"/></a>
		</div>
	</c:if>
	<table id="metadades" data-toggle="datatable" data-url="<c:url value="metaDada/datatable"/>" data-default-order="0" data-default-dir="asc" data-info-type="search" ${!esRevisor ? 'data-drag-enabled="true"' : ''} class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="ordre" data-visible="false"></th>
				<th data-col-name="codi" data-orderable="false"><spring:message code="metadada.list.columna.codi"/></th>
				<th data-col-name="nom" data-orderable="false"><spring:message code="metadada.list.columna.nom"/></th>
				<th data-col-name="tipus"  data-orderable="false" data-renderer="enum(MetaDadaTipusEnumDto)">
					<spring:message code="metadada.list.columna.tipus"/>
				</th>
				<th data-col-name="activa" data-orderable="false" data-template="#cellActivaTemplate" >
					<spring:message code="metadada.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if activa}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<c:if test="${!esRevisor}">
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="metaDada/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									<c:if test="${!bloquejarCamps}">
									{{if !activa}}
									<li><a href="metaDada/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
									{{else}}
									<li><a href="metaDada/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
									{{/if}}
									<li><a href="metaDada/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metadada.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
									</c:if>
								</ul>
							</div>
						</script>
					</th>
				</c:if>
			</tr>
		</thead>
	</table>
	<c:choose>
		<c:when test="${not empty metaDocument}">
			<a href="<c:url value="${header.referer}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
		</c:when>
		<c:otherwise>
			<a href="<c:url value="${!esRevisor ? '/metaExpedient?mantenirPaginacio=true' : '/metaExpedientRevisio?mantenirPaginacio=true'}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
		</c:otherwise>
	</c:choose>
	<div class="clearfix"></div>
</body>