<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="metaexpedient.grup.titol"/>:  ${metaExpedient.nom}</title>
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

	<c:if test="${!esRevisor && !bloquejarCamps}">
		<div class="text-right" data-toggle="botons-titol">
			<a class="btn btn-default" href="grup/relacionar" data-toggle="modal" data-datatable-id="metadades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.grup.btn.relacionar"/></a>
		</div>
	</c:if>

	<c:set var="element" scope="request" value="grup"/>
	<jsp:include page="includes/procedimentElementsMenu.jsp"/>

	<table id="metadades" data-toggle="datatable" data-url="<c:url value="/metaExpedient/${metaExpedient.id}/grup/datatable"/>" data-info-type="search" data-default-order="0" data-default-dir="asc" class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="codi" data-orderable="false"><spring:message code="metaexpedient.grup.columna.codi"/></th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="metaexpedient.grup.columna.descripcio"/></th>
				<th data-col-name="organGestor.codiINom" data-orderable="false"><spring:message code="metaexpedient.grup.columna.organGestor"/></th>
				<th data-col-name="perDefecte" data-orderable="false" data-template="#cellComuTemplate" width="1%">
					<spring:message code="metaexpedient.grup.columna.perDefecte"/>
					<script id="cellComuTemplate" type="text/x-jsrender">
						{{if perDefecte}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>				

				<c:if test="${!esRevisor}">
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="grup/{{:id}}/desvincular"><span class="fa fa-unlink"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desvincular"/></a></li>
									{{if perDefecte}}
										<li><a href="grup/{{:id}}/esborrarPerDefecte"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrarPerDefecte"/></a></li>
									{{else}}
										<li><a href="grup/{{:id}}/marcarPerDefecte"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.marcarPerDefecte"/></a></li>
									{{/if}}
								</ul>
							</div>
					</script>
					</th>		
				</c:if>		
			</tr>
		</thead>
	</table>
	<a href="<c:url value="${!esRevisor ? '/metaExpedient' : '/metaExpedientRevisio'}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
</body>