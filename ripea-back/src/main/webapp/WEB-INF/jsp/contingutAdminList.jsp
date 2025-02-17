<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%
pageContext.setAttribute(
		"contingutAdminOpcionsEsborratEnumOptions",
		es.caib.ripea.back.helper.EnumHelper.getOptionsForEnum(
				es.caib.ripea.war.command.ContingutFiltreCommand.ContenidorFiltreOpcionsEsborratEnum.class,
				"contingut.admin.opcions.esborrat.enum."));
%>
<rip:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="contingut.admin.titol"/></title>
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
$(document).ready(function() {
	$('#tipus').on('change', function() {
		var tipus = $(this).val();
		$('#metaNodeId').select2('val', '', true);
		$('#metaNodeId option[value!=""]').remove();
		var metaNodeRefresh = function(data) {
			for (var i = 0; i < data.length; i++) {

				if (data[i].id == '${contingutFiltreCommand.metaNodeId}' ) {
					$('#metaNodeId').append('<option value="' + data[i].id + '" selected>' + data[i].nom + '</option>');
				} else {
					$('#metaNodeId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
				}
			}
		};
		if (tipus == 'EXPEDIENT') {
			$.get("<c:url value="/metaExpedient/findAll"/>")
			.done(metaNodeRefresh)
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
			$('#expedientId').prop('disabled', true);
			$('#expedientId').select2('val', '', true);
			$('#expedientId option[value!=""]').remove();
		} else if (tipus == 'DOCUMENT') {
			$.get("<c:url value="/metaDocument/findAll"/>")
			.done(metaNodeRefresh)
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
			
			$('#expedientId').removeAttr('disabled');
		} else {
			$('#expedientId').prop('disabled', true);
			$('#expedientId').select2('val', '', true);
			$('#expedientId option[value!=""]').remove();
		}
	});
	
	$('#tipus').trigger('change');


	$('#taula').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"contingutAdmin/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	$('#taula').one('draw.dt', function () {
		$('#seleccioAll').on('click', function() {
			$.get(
					"contingutAdmin/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taula').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"contingutAdmin/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taula').webutilDatatable('select-none');
						$('#taula').webutilDatatable('refresh');
					}
			);
			return false;
		});
	});			
});
</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" modelAttribute="contingutFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="contingut.admin.filtre.nom"/>
			</div>
			<div class="col-md-3">					
				<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
				<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
						name="creador"  
						urlConsultaInicial="${urlConsultaInicial}"
						urlConsultaLlistat="${urlConsultaLlistat}"
						placeholderKey="contingut.admin.filtre.creador"
						suggestValue="codi"
						suggestText="codiAndNom"
						inline="true"/>	
			</div>
			<div class="col-md-2">
				<rip:inputSelect name="tipus" optionEnum="ContingutTipusEnumDto" emptyOption="true" placeholderKey="contingut.admin.filtre.tipus" inline="true"/>
			</div>
			<div class="col-md-3">
				<rip:inputSelect name="metaNodeId" optionItems="${metaNodes}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" optionMinimumResultsForSearch="3" placeholderKey="contingut.admin.filtre.element" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<rip:inputDate name="dataEsborratInici" inline="true" placeholderKey="contingut.admin.filtre.data.esborrat.inici"/>
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataEsborratFi" inline="true" placeholderKey="contingut.admin.filtre.data.esborrat.fi"/>
			</div>
			<div class="col-md-3">
				<rip:inputSelect name="opcionsEsborrat" optionItems="${contingutAdminOpcionsEsborratEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" emptyOption="false" inline="true"/>
			</div>
			<div class="col-md-5">					
				<c:url value="/expedientajax/expedient" var="urlConsultaExpInicial"/>
				<c:url value="/expedientajax/expedient" var="urlConsultaExpLlistat"/>
				<rip:inputSuggest 
 					name="expedientId"  
 					urlConsultaInicial="${urlConsultaExpInicial}"
 					urlConsultaLlistat="${urlConsultaExpLlistat}"
 					usePathVariable="false"
					placeholderKey="contingut.admin.filtre.expedient"
 					suggestValue="id"
 					suggestText="nomINumero"
					inline="true"/>	
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<rip:inputDate name="dataCreacioInici" inline="true" placeholderKey="contingut.admin.filtre.data.inici"/>
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataCreacioFi" inline="true" placeholderKey="contingut.admin.filtre.data.fi"/>
			</div>
			<div class="col-md-5 pull-right">
				<div class="pull-right">
					<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	
	
	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="text-right">
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
				<div class="btn-group">
					<button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
  						<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="expedient.list.user.opcions"/> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu">
						<li><a href="contingutAdmin/recuperarMassiu"><span class="fa fa-undo"></span>&nbsp;&nbsp;<spring:message code="contingut.admin.boto.recuperar"/></a></li>							
						<li><a href="contingutAdmin/deleteDefinitiuMassiu"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
					</ul>
				</div>
			</div>
		</div>
	</script>	
	
	
	<script id="rowhrefTemplate" type="text/x-jsrender">contingutAdmin/{{:id}}/info</script>
	<table
		id="taula"
		data-toggle="datatable"
		data-url="<c:url value="/contingutAdmin/datatable"/>"
		data-default-order="11"
		data-default-dir="desc"
		class="table table-bordered table-striped"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-rowhref-template="#rowhrefTemplate"
		data-rowhref-toggle="modal">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="expedient" data-visible="false">#</th>
				<th data-col-name="carpeta" data-visible="false">#</th>
				<th data-col-name="hasFills" data-visible="false">#</th>
				<th data-col-name="document" data-visible="false">#</th>
				<th data-col-name="registre" data-visible="false">#</th>
				<th data-col-name="esborrat" data-visible="false">#</th>
				<th data-col-name="nom" data-template="#cellNomTemplate" width="25%">
					<spring:message code="contingut.admin.columna.nom"/>
					<script id="cellNomTemplate" type="text/x-jsrender">
						{{if expedient}}<span class="fa ${iconaExpedient}"></span>{{else document}}<span class="fa ${iconaDocument}"></span>{{else carpeta}}<span class="fa ${iconaCarpeta}"></span>{{else registre}}<span class="fa ${iconaAnotacioRegistre}"></span>{{/if}}
						{{:nom}}
					</script>
				</th>
				<th data-col-name="numero" data-orderable="false"><spring:message code="contingut.admin.columna.numero"/></th>				
				<th data-col-name="metaNode.nom" width="15%"><spring:message code="contingut.admin.columna.element"/></th>
				<th data-col-name="createdBy.codiAndNom" width="10%"><spring:message code="contingut.admin.columna.creat.per"/></th>
				<th data-col-name="createdDate" data-converter="datetime" width="10%"><spring:message code="contingut.admin.columna.creat.el"/></th>
				<th data-col-name="esborratData" data-converter="datetime" width="10%"><spring:message code="contingut.admin.columna.esborratData"/></th>
				<th data-col-name="path" data-template="#cellPathTemplate" data-orderable="false" width="30%">
					<spring:message code="contingut.admin.columna.situacio"/>
					<script id="cellPathTemplate" type="text/x-jsrender">
						{{if path}}{{for path}}/
							{{if expedient}}<span class="fa ${iconaExpedient}" title="<spring:message code="contingut.icona.expedient"/>"></span>
							{{else carpeta}}<span class="fa ${iconaCarpeta}" title="<spring:message code="contingut.icona.carpeta"/>"></span>
							{{else document}}<span class="fa ${iconaDocument}" title="<spring:message code="contingut.icona.document"/>"></span>{{/if}}
							{{:nom}}
						{{/for}}{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="contingutAdmin/{{:id}}/info" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="contingut.admin.boto.detalls"/></a></li>
								<li><a href="contingutAdmin/{{:id}}/log" data-toggle="modal"><span class="fa fa-list"></span>&nbsp;&nbsp;<spring:message code="comu.boto.historial"/></a></li>
								{{if esborrat}}
								<li><a href="contingutAdmin/{{:id}}/undelete" data-toggle="ajax"><span class="fa fa-undo"></span>&nbsp;&nbsp;<spring:message code="contingut.admin.boto.recuperar"/></a></li>
							
								<li><a href="contingutAdmin/{{:id}}/deleteDefinitiu" data-toggle="ajax" data-confirm="<spring:message code="contingut.admin.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								<%--	{{if hasFills}}
									<li class="disabled"><a data-toggle="ajax" ><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
									{{else}}
									<li><a href="contingutAdmin/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="contingut.admin.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
									{{/if}}			--%>				
								{{/if}} 
								{{if expedient}}
									<li><a href="<c:url value="contingutAdmin/{{:id}}/assignar"/>" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-user"></span>&nbsp;&nbsp;<spring:message code="comu.boto.assignar"/></a></li>
								{{/if}}								
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>