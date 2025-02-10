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
	
	<script src="<c:url value="/webjars/datatables.net-select/1.3.1/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.2.3/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<!-- A la funció datatable del controlador, s'ha de afegir la variable id de la taula i la que guarda en sessió el llistat de seleccionats -->
	<script type="text/javascript">
		$(document).ready(function() {
			$('#avisos').on('selectionchange.dataTable', function (e, accio, ids) {
				$.get(
					"avis/toggleSelection",
					{ids: ids, accio: accio},
					function(data) {
						$("#seleccioCount").html(data);
					}
				);
			});
		});

		function avisAcccioMassiu(accio) {

			let msgAccio = '<spring:message code="avis.list.accio.massiva.activar"/>';
			if (accio=='desactivar') {
				msgAccio = '<spring:message code="avis.list.accio.massiva.desactivar"/>';
			} else if (accio=='eliminar') {
				msgAccio = '<spring:message code="avis.list.accio.massiva.eliminar"/>';
			}
			
			if (confirm(msgAccio)) {
				$.get(
					"avis/accioMassiva",
					{accio: accio},
					function(data) {
						window.location.reload();
					}
				);
			}
		}
	</script>
</head>
<body>
	<table
		id="avisos"
		data-toggle="datatable"
		data-url="<c:url value="/avis/datatable"/>"
		class="table table-striped table-bordered"
		data-default-order="1"
		data-default-dir="asc"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-search-enabled="false">
		<thead>
			<tr>
				<th data-col-name="assumpte" width="50%"><spring:message code="avis.list.columna.assumpte"/></th>
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
	<div class="pull-right">
		<div class="dropdown pull-left" style="padding-right: 15px;">
			<button class="btn btn-default" data-toggle="dropdown">
				<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span>&nbsp;
				<spring:message code="expedient.list.user.opcions"/>&nbsp;<span class="caret"></span>
			</button>
			<ul class="dropdown-menu">
				<li><a href="JavaScript:avisAcccioMassiu('activar');" data-refresh-pagina="true">
						<span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/>
				</a></li>
				<li><a href="JavaScript:avisAcccioMassiu('desactivar');" data-refresh-pagina="true">
					<span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/>
				</a></li>
				<li><a href="JavaScript:avisAcccioMassiu('eliminar');" data-refresh-pagina="true">
					<span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/>
				</a></li>
			</ul>
		</div>
		<a class="btn btn-default pull-right" href="avis/new" data-toggle="modal" data-maximized="true" data-refresh-pagina="true">
			<span class="fa fa-plus"></span>&nbsp;<spring:message code="avis.list.boto.nova.avis"/>
		</a>
	</div>
	</script>	
</body>