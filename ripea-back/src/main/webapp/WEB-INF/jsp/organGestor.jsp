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
	
</head>
<body>

	<div class="text-right" data-toggle="botons-titol" data-btn-title-col-size="4">
		<a id="organgestor-boto-nou" class="btn btn-default" data-toggle="modal" href="organgestor/sync/dir3">
				<span class="fa fa-refresh"></span>&nbsp; <spring:message code="organgestor.list.boto.actualitzar"/>
		</a>
		<a id="organgestor-boto-organigrama" class="btn btn-primary" href="<c:url value="/organGestorOrganigrama"/>">
			<spring:message code="organgestor.list.boto.canvi.vista"/>
		</a>
	</div>

	<c:url value="organgestor/filtrar" var="formAction"/>
	<form:form id="organGestorFiltreForm" action="${ formAction }" method="post" cssClass="well" modelAttribute="organGestorFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<rip:inputText name="codi" inline="true" placeholderKey="organgestor.list.filtre.camp.codi"/>
			</div>		
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="organgestor.list.filtre.camp.nom"/>
			</div>
			<div class="col-md-4">
				<rip:inputSelect name="pareId" optionItems="${organsSuperior}" optionValueAttribute="id" emptyOption="true" optionTextAttribute="codiAmbEstatINom" optionMinimumResultsForSearch="3" placeholderKey="organgestor.list.filtre.camp.pare" inline="true"/>
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
	
	
	<script id="rowhrefTemplate" type="text/x-jsrender">nodeco/organgestor/{{:id}}</script>
	<table 
		id="permisos" 
		data-toggle="datatable" 
		data-url="<c:url value="organgestor/datatable"/>" 
		data-search-enabled="false"
		data-default-order="3" 
		data-default-dir="asc" 
		class="table table-striped table-bordered" 
		data-rowhref-template="#rowhrefTemplate" 
		data-rowhref-toggle="modal"
		data-save-state="true"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="estat" data-visible="false"></th>
				<th data-col-name="tipusTransicio" data-visible="false"></th>
				
				<th data-col-name="codi" data-template="#cellCodiTemplate" width="7%">
					<spring:message code="organgestor.list.columna.codi"/>
					<script id="cellCodiTemplate" type="text/x-jsrender">
						{{:codi}} 
						{{if estat=='E'||estat=='A'||estat=='T'}}
							{{if tipusTransicio == 'DIVISIO'}}
								<span class="fa fa-warning text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="organgestor.list.obsolet.tipusTransicio.DIVISIO"/>"></span>
							{{else tipusTransicio == 'FUSIO'}}
								<span class="fa fa-warning text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="organgestor.list.obsolet.tipusTransicio.FUSIO"/>"></span>
							{{else tipusTransicio == 'SUBSTITUCIO'}}
								<span class="fa fa-warning text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="organgestor.list.obsolet.tipusTransicio.SUBSTITUCIO"/>"></span>
							{{else}}
								<span class="fa fa-warning text-danger pull-right" style="margin-top: 3px;" title="<spring:message code="organgestor.list.obsolet"/>"></span>
							{{/if}}
						{{/if}}
					</script>
				</th>					
				
				
				<th data-col-name="nom" width="40%">
					<spring:message code="organgestor.list.columna.nom" />
				</th>
				
				<th data-col-name="pareCodiNom" data-orderable="false" width="40%">
					<spring:message code="organgestor.list.columna.pare" />
				</th>
				
				<th data-col-name="cif" data-orderable="false" width="40%">
					<spring:message code="entitat.list.columna.cif" />
				</th>
				
				<th data-col-name="estat" data-renderer="enum(OrganEstatEnumDto)">
					<spring:message code="organgestor.list.columna.estat" />
				</th>
				
				<th data-col-name="id" data-template="#cellPermisosTemplate" data-orderable="false" width="1%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="organgestor/{{:id}}/permis" class="btn btn-default"><spring:message code="metaexpedient.list.boto.permisos"/>&nbsp;<span class="badge">{{:permisosCount}}</span></a>
					</script>
				</th>
				<th data-col-name="permisosCount" data-visible="false">
					<spring:message code="organgestor.list.columna.nom"/>
				</th>

				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="organgestor/{{:id}}" data-toggle="modal" data-maximized="true" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>
