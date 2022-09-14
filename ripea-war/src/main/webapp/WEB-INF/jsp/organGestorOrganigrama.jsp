<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="organgestor.list.titol"/></title>
	<meta name="subtitle" content="${entitat.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<link href="<c:url value="/webjars/jstree/3.2.1/dist/themes/default/style.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/webjars/jstree/3.2.1/dist/jstree.min.js"/>"></script>
	
	
	<script type="text/javascript">


	function changedCallback(e, data) {
		$('#panellInfo').css('visibility', '');
		$('#panellInfo').css('display', 'none');
		$(".datatable-dades-carregant").css("display", "block");
	
		var organId = data.node.id;

		var permisUrl = "<c:url value='/organgestor/'/>" + organId + "/permis";
		$('#permis-boto-nou').attr('href', permisUrl + '/new');
		$('#permisos').webutilDatatable('refresh-url', permisUrl  + '/datatable');
	
		$('#permisos').off('draw.dt');
		$('#permisos').on( 'draw.dt', function () {
	 		$.each($('#permisos .dropdown-menu a'), function( key, permisionLink ) {
		 		var link = $(permisionLink).attr('href');
		 		var replaced = link.replace("organIdString", organId);
		 		$(permisionLink).attr('href', replaced);
		 	});
		});

        $('#panellInfo').css('display', 'block');
        $(".datatable-dades-carregant").css("display", "none");
	};



	$(document).ready(function() {
		$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();

		if ($('#codi').val() || $('#nom').val() || $('#pareId').val()) {
			$('#arbreOrgans').jstree('open_all');
		}

	});


	
</script>	
	
</head>
<body>
	<div class="text-right" data-toggle="botons-titol" data-btn-title-col-size="4">
		<a id="organgestor-boto-nou" class="btn btn-default" data-toggle="modal" href="organgestor/sync/dir3">
			<span class="fa fa-refresh"></span>&nbsp; <spring:message code="organgestor.list.boto.actualitzar"/>
		</a>
		<a id="organgestor-boto-organigrama" class="btn btn-primary" href="<c:url value="/organgestor"/>">
			<spring:message code="organgestor.list.boto.canvi.vista"/>
		</a>
	</div>

	<!------------------------- FILTER ------------------------>
	<c:url value="organGestorOrganigrama/filtrar" var="formAction"/>
	<form:form id="organGestorFiltreForm" action="${ formAction }" method="post" cssClass="well" commandName="organGestorFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<rip:inputText name="codi" inline="true" placeholderKey="organgestor.list.filtre.camp.codi"/>
			</div>		
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="organgestor.list.filtre.camp.nom"/>
			</div>
			<div class="col-md-4">
				<rip:inputSelect name="pareId" optionItems="${organsSuperior}" optionValueAttribute="id" emptyOption="true" optionTextAttribute="codiINom" optionMinimumResultsForSearch="3" placeholderKey="organgestor.list.filtre.camp.pare" inline="true"/>
			</div>	
			<div class="col-md-2">
				<rip:inputSelect name="estat"  optionEnum="OrganEstatEnumDto" placeholderKey="organgestor.list.filtre.camp.estat" emptyOption="true" inline="true"/>
			</div>			

			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none;"></button>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	
	<div class="row">
		<!------------------------- TREE ----------------------->
		<div class="col-md-5">
			<div style="padding-bottom: 10px;">
 				<button class="btn btn-default" onclick="$('#arbreOrgans').jstree('open_all');"><span class="fa fa-caret-square-o-down"></span> <spring:message code="unitat.arbre.expandeix"/></button> 
 				<button class="btn btn-default" onclick="$('#arbreOrgans').jstree('close_all');"><span class="fa fa-caret-square-o-up"></span> <spring:message code="unitat.arbre.contreu"/></button> 
			</div>
 			<c:set var="fillsAtributInfoText"><span style="padding-top: 4.5px; padding-left: 2px;" class="fa fa-warning text-danger pull-right" title="<spring:message code="organgestor.list.obsolet"/>"></span></c:set>
			<rip:arbre 
				id="arbreOrgans" 
				atributId="id" 
				atributNom="nomICodi" 
				arbre="${arbreOrgans}"
				isArbreSeleccionable="${true}" 
				isFullesSeleccionable="${true}"
				isOcultarCounts="${true}" 
				changedCallback="changedCallback"
				fillsAtributInfoCondition="obsolet" 
				fillsAtributInfoText="${fillsAtributInfoText}" />
		</div>
		
		
		
		
		
		<!------------------------- PERMISOS ------------------------>
		<div class="col-md-7" id="panellInfo" style="visibility:hidden">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h2><spring:message code="organgestor.permis.titol"/></h2>
				</div>
				<div class="panel-body">
					<div class="text-right boto-nou-permis-organigrama">
						<a id="permis-boto-nou" class="btn btn-default" href="" data-toggle="modal" data-datatable-id="permisos"><span class="fa fa-plus"></span>&nbsp;<spring:message code="organgestor.permis.form.titol.crear"/></a>
					</div>
	
					<table 
						id="permisos" 
						data-toggle="datatable" 
						data-url="<c:url value="/permis/datatable"/>"  <%-- this incorrect url, to do: find way to not specify url  --%>
						data-default-order="1" 
						data-default-dir="asc" 
						class="table table-striped table-bordered" 
						data-paging-enabled="false"
						style="width: 100%">
						<thead>
							<tr>
								<th data-col-name="principalTipus"><spring:message code="organgestor.permis.columna.tipus" /></th>
								<th data-col-name="principalCodiNom"><spring:message code="organgestor.permis.columna.principal" /></th>
								<th data-col-name="create" data-template="#cellPermisCreateTemplate"><spring:message code="organgestor.permis.columna.creacio" /> 
									<script id="cellPermisCreateTemplate" type="text/x-jsrender">
								{{if create}}<span class="fa fa-check"></span>{{/if}}
								</script>
								</th>
								<th data-col-name="read" data-template="#cellPermisReadTemplate"><spring:message code="organgestor.permis.columna.consulta" /> 
									<script id="cellPermisReadTemplate" type="text/x-jsrender">
									{{if read}}<span class="fa fa-check"></span>{{/if}}
								</script>
								</th>
								<th data-col-name="write" data-template="#cellPermisWriteTemplate"><spring:message code="organgestor.permis.columna.modificacio" /> 
									<script id="cellPermisWriteTemplate" type="text/x-jsrender">
									{{if write}}<span class="fa fa-check"></span>{{/if}}
								</script>
								</th>
								<th data-col-name="delete" data-template="#cellPermisDeleteTemplate"><spring:message code="organgestor.permis.columna.eliminacio" /> 
									<script id="cellPermisDeleteTemplate" type="text/x-jsrender">
									{{if delete}}<span class="fa fa-check"></span>{{/if}}
								</script>
								</th>
							 	<th data-col-name="procedimentsComuns" data-template="#cellProcedimentsComunsTemplate" width="5%"><spring:message code="organgestor.permis.columna.procedimentsComuns" /> 
									<script id="cellProcedimentsComunsTemplate" type="text/x-jsrender">
 									{{if procedimentsComuns}}<span class="fa fa-check"></span>{{/if}}
								</script>
								</th>
								<th data-col-name="administration" data-template="#cellPermisAdministrationTemplate" width="5%"><spring:message code="organgestor.permis.columna.administracio" /> 
									<script id="cellPermisAdministrationTemplate" type="text/x-jsrender">
 									{{if administration}}<span class="fa fa-check"></span>{{/if}}
								</script>
								</th>
								<th data-col-name="administrationComuns" data-template="#cellPermisAdministrationComunsTemplate" width="5%"><spring:message code="organgestor.permis.columna.administracio.comuns" /> 
									<script id="cellPermisAdministrationComunsTemplate" type="text/x-jsrender">
 									{{if administrationComuns}}<span class="fa fa-check"></span>{{/if}}
								</script>
								</th>
								<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
									<script id="cellAccionsTemplate" type="text/x-jsrender">
									<div class="dropdown"> 
										<button class="btn btn-primary" data-toggle="dropdown">
											<span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span>
										</button> 
										<ul class="dropdown-menu">
											<li><a href="organgestor/organIdString/permis/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
											<li><a href="organgestor/organIdString/permis/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="entitat.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
										</ul>
									</div>
								</script>
								</th>
							</tr>
						</thead>
					</table>
	
				</div>
			</div>
		</div>
			
		
		<div class="col-md-7 datatable-dades-carregant" style="display: none; text-align: center; margin-top: 100px;">
			<span class="fa fa-circle-o-notch fa-spin fa-3x"></span>
		</div>
	</div>

</body>
</html>
