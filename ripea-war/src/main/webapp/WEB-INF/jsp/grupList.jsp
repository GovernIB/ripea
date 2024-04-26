<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="grup.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.3.1/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.2.3/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
<style>
table.dataTable tbody tr.selected a, table.dataTable tbody th.selected a, table.dataTable tbody td.selected a {
    color: #333;
}	
</style>
<script>

//################################################## document ready START ##############################################################
$(document).ready(function() {

	$('#grup').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"grup/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	
	$('#grup').one('draw.dt', function () {
		
		$('#seleccioAll').on('click', function() {
			$.get(
					"grup/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"grup/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#grup').webutilDatatable('select-none');
						$('#grup').webutilDatatable('refresh');
					}
			);
			return false;
		});
	});

	


});//################################################## document ready END ##############################################################

	


</script>
	
</head>
<body>

	<c:url value="grup/filtrar" var="formAction"/>
	<form:form id="grupFiltreForm" action="${formAction}" method="post" cssClass="well" commandName="grupFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="codi" inline="true" placeholderKey="grup.list.filtre.camp.codi"/>
			</div>					
			<div class="col-md-4">
				<rip:inputText name="descripcio" inline="true" placeholderKey="grup.list.filtre.camp.descripcio"/>
			</div>
			<div class="col-md-4">
				<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
 					name="organGestorAscendentId"  
 					inline="true"
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					placeholderKey="grup.list.filtre.camp.organGestor"
 					suggestValue="id"
 					suggestText="codiINom" />
			</div>	
		</div>
		<div class="row">
			<div class="col-md-4 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	
	<script id="botonsTemplate" type="text/x-jsrender">
    	<div class="btn-group pull-right">
        	<a id="seleccioAll" title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></a>
       	 	<a id="seleccioNone" title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></a>
        	<a class="btn btn-default" href="./grup/esborrar" >
            	<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="comu.boto.esborrar"/>
        	</a>
    	</div>
	</script>	
	
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="grup/new" data-toggle="modal" data-datatable-id="grup"><span class="fa fa-plus"></span>&nbsp;<spring:message code="grup.boto.nou"/></a>
	</div>
	<table 
		id="grup"
		data-toggle="datatable" 
		data-url="<c:url value="/grup/datatable"/>" 
		class="table table-striped table-bordered"
		data-default-order="0" <%-- default column number to be sorted  --%>
		data-default-dir="asc" <%-- default ordering direction  --%>
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true">
		<thead>
			<tr>
				<th data-col-name="codi" data-orderable="false"><spring:message code="grup.columna.codi"/></th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="grup.columna.descripcio"/></th>
				<th data-col-name="organGestor.codiINom" data-orderable="false"><spring:message code="metaexpedient.grup.columna.organGestor"/></th>
				<c:if test="${not isRolAdminOrgan || isActiveGestioPermisPerAdminOrgan}">
					<th data-col-name="permisosCount" data-orderable="false" data-template="#cellPermisosTemplate" width="10%">
						<script id="cellPermisosTemplate" type="text/x-jsrender">
							<a href="grupPermis/{{:id}}/permis" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="grup.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
						</script>
					</th>
				</c:if>>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="grup/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="grup/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="grup.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>