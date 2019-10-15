<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="user.tasca.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">
	</script>
</head>
<body>
	<script id="rowhrefTemplate" type="text/x-jsrender">usuariTasca/{{:id}}/tramitar</script>
	<table 
		data-toggle="datatable" 
		data-url="<c:url value="/usuariTasca/datatable"/>" 
		data-save-state="true"
		class="table table-striped table-bordered" 
		data-rowhref-template="#rowhrefTemplate"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="expedient.nomINumero" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.expedient"/></th>
				<th data-col-name="metaExpedientTasca.nom" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.metaExpedientTasca"/></th>
				<th data-col-name="dataInici" data-converter="datetime" width="20%"><spring:message code="expedient.tasca.list.columna.dataInici"/></th>
				<th data-col-name="estat" data-template="#cellTascaEstatTemplate" data-orderable="false" width="10%">
					<spring:message code="expedient.tasca.list.columna.estat"/>
					<script id="cellTascaEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<spring:message code="expedient.tasca.estat.enum.PENDENT"/>
						{{else estat == 'INICIADA'}}
							<spring:message code="expedient.tasca.estat.enum.INICIADA"/>
						{{else estat == 'FINALITZADA'}}
							<spring:message code="expedient.tasca.estat.enum.FINALITZADA"/>
						{{else estat == 'CANCELLADA'}}
							<spring:message code="expedient.tasca.estat.enum.CANCELLADA"/>
						{{else estat == 'REBUTJADA'}}
							<spring:message code="expedient.tasca.estat.enum.REBUTJADA"/>
						{{/if}}
					</script>
				</th>	
				<th data-col-name="id" data-orderable="false" data-template="#cellAnotacioAccionsTemplate" width="1%">
					<script id="cellAnotacioAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/usuariTasca/{{:id}}/tramitar"/>"><span class="fa fa-folder-open-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.tramitar"/></a></li>
								{{if estat == 'PENDENT'}}					
									<li><a href="<c:url value="/usuariTasca/{{:id}}/iniciar"/>"><span class="fa fa-play"></span>&nbsp;&nbsp;<spring:message code="comu.boto.iniciar"/></a></li>
									<li><a href="<c:url value="/usuariTasca/{{:id}}/rebutjar"/>" data-maximized="true" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-reply"></span>&nbsp;&nbsp;<spring:message code="comu.boto.rebutjar"/></a></li>	 
								{{else}}						
									<li><a href="<c:url value="/usuariTasca/{{:id}}/finalitzar"/>" data-confirm="<spring:message code="expedient.tasca.finalitzar"/>"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.finalitzar"/></a></li>	
								{{/if}}								
							</ul>
						</div>
					</script>
				</th>
				
			</tr>
		</thead>
	</table>
</body>