<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="integracio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<ul class="nav nav-tabs" role="tablist">
		<c:forEach var="integracio" items="${integracions}">
			<li<c:if test="${integracio.codi == codiActual}"> class="active"</c:if>><a href="<c:url value="/integracio/${integracio.codi}"/>"><spring:message code="${integracio.nom}"/></a></li>
		</c:forEach>
	</ul>
	<br/>
	<script id="rowhrefTemplate" type="text/x-jsrender">../nodeco/integracio/${codiActual}/{{:index}}</script>	
	<table id="missatges-integracions" data-toggle="datatable" data-url="<c:url value="/integracio/datatable"/>" data-search-enabled="false" class="table table-striped table-bordered" style="width:100%"
	data-rowhref-template="#rowhrefTemplate" data-rowhref-toggle="modal">
		<thead>
			<tr>
				<th data-col-name="excepcioMessage" data-visible="false"></th>
				<th data-col-name="excepcioStacktrace" data-visible="false"></th>
				<th data-col-name="data" data-orderable="false" data-converter="datetime"><spring:message code="integracio.list.columna.data"/></th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="integracio.list.columna.descripcio"/></th>
				<th data-col-name="tipus" data-orderable="false"><spring:message code="integracio.list.columna.tipus"/></th>
				<th data-col-name="tempsResposta" data-template="#cellTempsTemplate" data-orderable="false">
					<spring:message code="integracio.list.columna.temps.resposta"/>
					<script id="cellTempsTemplate" type="text/x-jsrender">{{:tempsResposta}} ms</script>
				</th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" data-orderable="false">
					<spring:message code="integracio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'OK'}}
							<span class="label label-success"><span class="fa fa-check"></span>&nbsp;{{:estat}}</span>
						{{else}}
							<span class="label label-danger" title="{{:excepcioMessage}}"><span class="fa fa-warning"></span>&nbsp;{{:estat}}</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="index" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="../integracio/${codiActual}/{{:index}}" class="btn btn-default" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>