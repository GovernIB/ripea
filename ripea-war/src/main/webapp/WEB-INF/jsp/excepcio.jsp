<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="excepcio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<script id="rowhrefTemplate" type="text/x-jsrender">nodeco/excepcio/{{:index}}</script>
	<table id="excepcions" data-toggle="datatable" data-url="<c:url value="/excepcio/datatable"/>" data-search-enabled="false" class="table table-striped table-bordered" style="width:100%"
	data-rowhref-template="#rowhrefTemplate" data-rowhref-toggle="modal">
		<thead>
			<tr>
				<th data-col-name="data" data-orderable="false" data-converter="datetime"><spring:message code="excepcio.list.columna.data"/></th>
				<th data-col-name="tipus" data-orderable="false"><spring:message code="excepcio.list.columna.tipus"/></th>
				<th data-col-name="objectId" data-orderable="false"><spring:message code="excepcio.list.columna.objecte.id"/></th>
				<th data-col-name="objectClass" data-orderable="false"><spring:message code="excepcio.list.columna.objecte.class"/></th>
				<th data-col-name="message" data-orderable="false"><spring:message code="excepcio.list.columna.message"/></th>
				<th data-col-name="index" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="excepcio/{{:index}}" class="btn btn-default" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>