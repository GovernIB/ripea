<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="organgestor.list.titol"/></title>
	<meta name="subtitle" content="${entitat.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<table id="permisos" data-toggle="datatable" 
			data-url="<c:url value="organgestor/datatable"/>" 
			data-default-order="1" 
			data-default-dir="asc" 
			data-botons-template="#botonsTemplate" 
			class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="codi">
					<spring:message code="organgestor.list.columna.codi"/>
				</th>
				<th data-col-name="nom">
					<spring:message code="organgestor.list.columna.nom"/>
				</th>
<!-- 				<th data-col-name="administration" data-template="#cellAdministrationTemplate"> -->
<%-- 					<spring:message code="entitat.permis.columna.administracio"/> --%>
<!-- 					<script id="cellAdministrationTemplate" type="text/x-jsrender"> -->
<!-- 						{{if administration}}<span class="fa fa-check"></span>{{/if}} -->
<!-- 					</script> -->
<!-- 				</th> -->
<!-- 				<th data-col-name="read" data-template="#cellReadTemplate"> -->
<%-- 					<spring:message code="entitat.permis.columna.usuari"/> --%>
<!-- 					<script id="cellReadTemplate" type="text/x-jsrender"> -->
<!-- 						{{if read}}<span class="fa fa-check"></span>{{/if}} -->
<!-- 					</script> -->
<!-- 				</th> -->
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
<%-- 								<li><a href="#" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li> --%>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right">
			<a id="organgestor-boto-nou" class="btn btn-warning" href="organgestor/sync/dir3">
				<span class="fa fa-refresh"></span>&nbsp; <spring:message code="organgestor.list.boto.actualitzar"/>
			</a>
		</p>
	</script>
</body>
</html>
