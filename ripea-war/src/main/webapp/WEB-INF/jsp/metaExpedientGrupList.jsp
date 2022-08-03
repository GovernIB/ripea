<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="metaexpedient.grup.titol"/></title>
	<meta name="subtitle" content="${metaExpedient.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>

	<table id="metadades" data-toggle="datatable" data-url="<c:url value="/metaExpedient/${metaExpedient.id}/grup/datatable"/>" data-info-type="search" data-default-order="0" data-default-dir="asc" class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="rol" data-orderable="false"><spring:message code="metaexpedient.grup.columna.rol"/></th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="metaexpedient.grup.columna.descripcio"/></th>

				<th data-col-name="relacionat" data-template="#cellRelacionatTemplate" data-orderable="false">
					<spring:message code="metaexpedient.grup.columna.relacionat"/>
					<script id="cellRelacionatTemplate" type="text/x-jsrender">
						{{if relacionat}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<c:if test="${!esRevisor}">
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
							<c:if test="${!bloquejarCamps}">
							{{if !relacionat}}
								<a href="grup/{{:id}}/relacionar" class="btn btn-success"><span class="fa fa-link"></span>&nbsp;&nbsp;<spring:message code="comu.boto.relacionar"/></a>
								
							{{else}}
								<a href="grup/{{:id}}/desvincular" class="btn btn-success"><span class="fa fa-unlink"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desvincular"/></a>
							{{/if}}
							</c:if>
						</script>
					</th>
				</c:if>
				
			</tr>
		</thead>
	</table>
	<a href="<c:url value="${!esRevisor ? '/metaExpedient' : '/metaExpedientRevisio'}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
</body>