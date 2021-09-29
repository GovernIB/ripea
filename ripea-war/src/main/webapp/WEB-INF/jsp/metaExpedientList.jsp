<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="metaexpedient.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	#metaExpedientFiltreForm {
		margin-bottom: 15px;
	}
	.badge {
  	 	background-color: #333;
  	 }
	</style>
	<script type="text/javascript">
	$(function() {
	    $("form input").keypress(function (e) {
	        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
	            $("#metaExpedientFiltreForm").submit()
	            return false;
	        } else {
	            return true;
	        }
	    });
	});
	
	var myHelpers = {
	        hlpIsAdministradorOrgan: isRolAdminOrgan};
	
	$.views.helpers(myHelpers);
	
    function isRolAdminOrgan() {
        return ${isRolAdminOrgan};
    }
	</script>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="metaExpedient/new" data-toggle="modal" data-datatable-id="metaexpedients"><span class="fa fa-plus"></span>&nbsp;<spring:message code="metaexpedient.list.boto.nou"/></a>
	</div>
	<c:url value="metaExpedient/filtrar" var="formAction"/>
	<form:form id="metaExpedientFiltreForm" action="${ formAction }" method="post" cssClass="well" commandName="metaExpedientFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="codi" inline="true" placeholderKey="metaexpedient.list.filtre.camp.codi"/>
			</div>		
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="metaexpedient.list.filtre.camp.nom"/>
			</div>
			<div class="col-md-4">
				<rip:inputText name="classificacioSia" inline="true" placeholderKey="metaexpedient.list.filtre.camp.codiSia"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<rip:inputSelect 
						name="actiu" 
						optionEnum="MetaExpedientActiuEnumDto" 
						emptyOption="true" 
						placeholderKey="metaexpedient.list.filtre.camp.actiu" inline="true"/>
			</div>
			<div class="col-md-4">
				<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
 					name="organGestorId"  
 					inline="true"
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					placeholderKey="metaexpedient.list.filtre.camp.organGestor"
 					suggestValue="id"
 					suggestText="codiINom" />
			</div>
			<div class="col-md-4">
				<c:if test="${not isRolAdminOrgan}">
					<rip:inputSelect name="ambit" optionEnum="MetaExpedientAmbitEnumDto" 
									 emptyOption="true" 
									 placeholderKey="metaexpedient.list.filtre.camp.ambit" inline="true"/>
				</c:if>
			</div>	
		</div>
		
		<div class="row">		
			<div class="col-md-4">
				<rip:inputSelect name="revisioEstat" optionEnum="MetaExpedientRevisioEstatEnumDto" emptyOption="true" placeholderKey="metaexpedient.list.filtre.camp.revisioEstat" inline="true"/>
			</div>	
			<div class="col-md-4 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<script id="rowhrefTemplate" type="text/x-jsrender">nodeco/metaExpedient/{{:id}}</script>
	<table 
		id="metaexpedients" 
		data-toggle="datatable" 
		data-url="<c:url value="/metaExpedient/datatable"/>" 
		data-info-type="search" 
		data-default-order="2" 
		data-default-dir="asc" 
		class="table table-striped table-bordered"
		data-rowhref-template="#rowhrefTemplate" 
		data-rowhref-toggle="modal"
		data-save-state="true"
		data-search-enabled="false"
		data-mantenir-paginacio="${mantenirPaginacio}">
		<thead>
			<tr>
				<th data-col-name="codi" width="1%"><spring:message code="metaexpedient.list.columna.codi"/></th>
				<th data-col-name="classificacioSia" width="1%"><spring:message code="metaexpedient.list.columna.codiSia"/></th>	
				<th data-col-name="nom" width="20%"><spring:message code="metaexpedient.list.columna.nom"/></th>			
				<th data-col-name="serieDocumental" width="1%"><spring:message code="metaexpedient.list.columna.serieDocumental"/></th>				
				<th data-col-name="organGestor.codiINom" width="20%"><spring:message code="metaexpedient.list.columna.organGestor"/></th>
				<th data-col-name="comu" data-orderable="false" data-template="#cellComuTemplate" width="1%">
					<spring:message code="metaexpedient.list.columna.comu"/>
					<script id="cellComuTemplate" type="text/x-jsrender">
						{{if comu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="actiu" data-template="#cellActiuTemplate" width="1%">
					<spring:message code="metaexpedient.list.columna.actiu"/>
					<script id="cellActiuTemplate" type="text/x-jsrender">
						{{if actiu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<c:if test="${isRevisioActiva}">
					<th data-col-name="revisioEstat" data-template="#cellRevisioEstatTemplate" data-orderable="false" width="10%">
						<spring:message code="metaexpedient.list.columna.revisioEstat"/>
						<script id="cellRevisioEstatTemplate" type="text/x-jsrender">
							{{if revisioEstat == 'DISSENY'}}
								<spring:message code="meta.expedient.revisio.estat.enum.DISSENY"/>
							{{else revisioEstat == 'PENDENT'}}
								<spring:message code="meta.expedient.revisio.estat.enum.PENDENT"/>
							{{else revisioEstat == 'REVISAT'}}
								<spring:message code="meta.expedient.revisio.estat.enum.REVISAT"/>
							{{else revisioEstat == 'REBUTJAT'}}
								<spring:message code="meta.expedient.revisio.estat.enum.REBUTJAT"/>
							{{/if}}
						</script>
					</th>
				</c:if>
				
				<!--  <th data-col-name="gestioAmbGrupsActiva" data-template="#cellGestioAmbGrupsActivaTemplate" width="1%">
					<spring:message code="metaexpedient.list.columna.gestioAmbGrupsActiva"/>
					<script id="cellGestioAmbGrupsActivaTemplate" type="text/x-jsrender">
						{{if gestioAmbGrupsActiva}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>	-->
				<th data-col-name="metaDocumentsCount" data-visible="false"></th>
				<th data-col-name="metaDadesCount" data-visible="false"></th>
				<th data-col-name="expedientEstatsCount" data-visible="false"></th>
				<th data-col-name="expedientTasquesCount" data-visible="false"></th>
				<th data-col-name="grupsCount" data-visible="false"></th>
				
				<th data-col-name="permisosCount" data-template="#cellPermisosTemplate" data-orderable="false" width="1%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="metaExpedient/{{:id}}/permis" class="btn btn-default"><spring:message code="metaexpedient.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				
				<th data-col-name="id" data-template="#cellElementsTemplate" data-orderable="false" width="1%">
					<script id="cellElementsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-default" data-toggle="dropdown"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.elements"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="metaExpedient/{{:id}}/metaDocument"><span class="badge">{{:metaDocumentsCount}}</span>&nbsp;<spring:message code="metaexpedient.list.boto.meta.documents"/></a></li>
								<li><a href="metaExpedient/{{:id}}/metaDada"><span class="badge">{{:metaDadesCount}}</span>&nbsp;<spring:message code="metaexpedient.list.boto.meta.dades"/></a></li>
								<li><a href="expedientEstat/{{:id}}"><span class="badge">{{:expedientEstatsCount}}</span>&nbsp;<spring:message code="metaexpedient.list.boto.estats"/></a></li>
								<li><a href="metaExpedient/{{:id}}/tasca"><span class="badge">{{:expedientTasquesCount}}</span>&nbsp;<spring:message code="metaexpedient.list.boto.tasques"/></a></li>
								<li><a href="metaExpedient/{{:id}}/grup"><span class="badge">{{:grupsCount}}</span>&nbsp;<spring:message code="metaexpedient.list.boto.grups"/></a></li>
							</ul>
						</div>					
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="metaExpedient/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{if !actiu}}
								<li><a href="metaExpedient/{{:id}}/enable" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.activar"/></a></li>
								{{else}}
								<li><a href="metaExpedient/{{:id}}/disable" data-toggle="ajax"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="comu.boto.desactivar"/></a></li>
								{{/if}}
								<li><a href="metaExpedient/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="metaexpedient.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								{^{if ~hlpIsAdministradorOrgan() && revisioEstat == 'DISSENY'}}
									<li><a href="metaExpedient/{{:id}}/marcarPendentRevisio" data-toggle="ajax"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="metaexpedient.list.accio.boto.pendent"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>