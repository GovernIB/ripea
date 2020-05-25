<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title><spring:message code="domini.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>

</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="domini/new" data-toggle="modal" data-datatable-id="domini"><span class="fa fa-plus"></span>&nbsp;<spring:message code="domini.boto.afegir"/></a>
	</div>
	<table 
		id="domini"
		data-toggle="datatable" 
		data-url="<c:url value="/domini/datatable"/>" 
		data-info-type="search" 
		data-default-order="0" 
		data-default-dir="asc"
		class="table table-striped table-bordered">
		<thead>
			<tr>
				<th data-col-name="codi" data-orderable="false"><spring:message code="domini.columna.codi"/></th>
				<th data-col-name="nom" data-orderable="false"><spring:message code="domini.columna.nom"/></th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="domini.columna.descripcio"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="domini/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="domini/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metaexpedient.metadada.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>