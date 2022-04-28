<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="avis.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>
	<table id="avisos" data-toggle="datatable" data-url="<c:url value="/avis/datatable"/>" data-search-enabled="false" data-default-order="2" data-default-dir="asc" data-botons-template="#botonsTemplate" class="table table-striped table-bordered" style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="assumpte" width="10px"><spring:message code="avis.list.columna.assumpte"/></th>
				<th data-col-name="dataInici" data-converter="date"><spring:message code="avis.list.columna.dataInici"/></th>
				<th data-col-name="dataFinal" data-converter="date"><spring:message code="avis.list.columna.dataFinal"/></th>
				<th data-col-name="actiu" data-template="#cellActivaTemplate">
					<spring:message code="avis.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if actiu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="avisNivell" data-orderable="false" width="10%" data-template="#cellAvisNivellTemplate">
					<spring:message code="avis.list.columna.avisNivell"/>
					<script id="cellAvisNivellTemplate" type="text/x-jsrender">
						{{if avisNivell == 'INFO'}}
							<spring:message code="avis.nivell.enum.INFO"/>
						{{else avisNivell == 'WARNING'}}
							<spring:message code="avis.nivell.enum.WARNING"/>
						{{else avisNivell == 'ERROR'}}
							<spring:message code="avis.nivell.enum.ERROR"/>
						{{/if}}
					</script>
				</th>			
				
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="avis/{{:id}}" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !actiu}}
									<li><a href="avis/{{:id}}/enable"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
									<li><a href="avis/{{:id}}/disable"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="avis/{{:id}}/delete" data-confirm="<spring:message code="avis.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="avis/new" data-toggle="modal" data-maximized="true" data-refresh-pagina="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="avis.list.boto.nova.avis"/></a></p>
	</script>
</body>