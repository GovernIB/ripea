<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="accio.massiva.titol"/>: <spring:message code="decorator.menu.pendents.arxiu"/></title>
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
.disabledMsg:hover {
    cursor: not-allowed;
}
.tab-content {
    margin-top: .8em;
}
</style>
<script>
$(document).ready(function() {
	$('#arxiuPendentsExpedients').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"<c:url value='/seguimentArxiuPendents/expedients/'/>" + accio,
				{ids: ids},
				function(data) {
					$("#expSeleccioCount").html(data);
				}
		);
	});
	$('#arxiuPendentsExpedients').on('draw.dt', function (e, settings) {
		$('.disabledMsg').tooltip();
		$('#expSeleccioAll').on('click', function() {
			$.get(
					"<c:url value='/seguimentArxiuPendents/expedients/select'/>",
					function(data) {
						$("#expSeleccioCount").html(data);
						$('#arxiuPendentsExpedients').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#expSeleccioNone').on('click', function() {
			$.get(
					"<c:url value='/seguimentArxiuPendents/expedients/deselect'/>",
					function(data) {
						$("#expSeleccioCount").html(data);
						$('#arxiuPendentsExpedients').webutilDatatable('select-none');
						$('#arxiuPendentsExpedients').webutilDatatable('refresh');
					}
			);
			return false;
		});
	});

	$('#arxiuPendentsDocuments').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"<c:url value='/seguimentArxiuPendents/documents/'/>" + accio,
				{ids: ids},
				function(data) {
					$("#docSeleccioCount").html(data);
				}
		);
	});
	$('#arxiuPendentsDocuments').on('draw.dt', function (e, settings) {
		$('.disabledMsg').tooltip();
		$('#docSeleccioAll').on('click', function() {
			$.get(
					"<c:url value='/seguimentArxiuPendents/documents/select'/>",
					function(data) {
						$("#docSeleccioCount").html(data);
						$('#arxiuPendentsDocuments').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#docSeleccioNone').on('click', function() {
			$.get(
					"<c:url value='/seguimentArxiuPendents/documents/deselect'/>",
					function(data) {
						$("#docSeleccioCount").html(data);
						$('#arxiuPendentsDocuments').webutilDatatable('select-none');
						$('#arxiuPendentsDocuments').webutilDatatable('refresh');
					}
			);
			return false;
		});
	});

	$('#arxiuPendentsInteressats').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"<c:url value='/seguimentArxiuPendents/interessats/'/>" + accio,
				{ids: ids},
				function(data) {
					$("#intSeleccioCount").html(data);
				}
		);
	});
	$('#arxiuPendentsInteressats').on('draw.dt', function (e, settings) {
		$('.disabledMsg').tooltip();
		$('#intSeleccioAll').on('click', function() {
			$.get(
					"<c:url value='/seguimentArxiuPendents/interessats/select'/>",
					function(data) {
						$("#intSeleccioCount").html(data);
						$('#arxiuPendentsInteressats').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#intSeleccioNone').on('click', function() {
			$.get(
					"<c:url value='/seguimentArxiuPendents/interessats/deselect'/>",
					function(data) {
						$("#intSeleccioCount").html(data);
						$('#arxiuPendentsInteressats').webutilDatatable('select-none');
						$('#arxiuPendentsInteressats').webutilDatatable('refresh');
					}
			);
			return false;
		});
	});		

	if (/#expedients/.test(window.location.href)) {
		$('.nav-tabs a[href$="#expedients"]').trigger('click');	
	} else if (/#documents/.test(window.location.href)) {
		$('.nav-tabs a[href$="#documents"]').trigger('click');	
	} else if (/#interessats/.test(window.location.href)) {
		$('.nav-tabs a[href$="#interessats"]').trigger('click');	
	}
	$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
		  var target = $(e.target).attr("href")
		  window.history.replaceState('','', window.location.href.substr(0, window.location.href.indexOf("#")) + target);  
	});
});
</script>
	
</head>
<body>

	<!---------------------------------------- TABLIST ------------------------------------------>
	<ul class="nav nav-tabs">
		<li class="active">
			<a href="#expedients" data-toggle="tab"><spring:message code="seguiment.tab.expedients"/></a>
		</li>
		<li>
			<a href="#documents" data-toggle="tab"><spring:message code="seguiment.tab.documents"/></a>
		</li>
		<li>
			<a href="#interessats" data-toggle="tab"><spring:message code="seguiment.tab.interessats"/></a>
		</li>		
	</ul>

	<div class="tab-content">
	
		<!------------------------------ TABPANEL EXPEDIENTS ------------------------------------->
		<div class="tab-pane active in" id="expedients">
			<c:url value="/seguimentArxiuPendents/filtrar/expedients" var="formAction"/>
			<form:form id="seguimentFiltreExpedientsForm" action="${ formAction }" method="post" cssClass="well" commandName="commandExpedients">
				<div class="row">
					<div class="col-md-4">
						<rip:inputText name="elementNom" inline="true" placeholderKey="seguiment.list.filtre.camp.expedientNom"/>
					</div>		
					<div class="col-md-4">					
						<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionMinimumResultsForSearch="0" optionValueAttribute="id" emptyOption="true" optionTextAttribute="codiSiaINom" placeholderKey="seguiment.list.filtre.camp.metaExpedient" inline="true"/>
					</div>
					<div class="col-md-4 pull-right">
						<div class="pull-right">
							<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
							<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
						</div>
					</div>
				</div>
			</form:form>


			<script id="expedientsBotonsTemplate" type="text/x-jsrender">
				<div class="btn-group pull-right">
					<a id="expSeleccioAll" title="<spring:message code="seguiment.list.expedients.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></a>
					<a id="expSeleccioNone" title="<spring:message code="seguiment.list.expedients.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></a>
					<a id="expReintentar" class="btn btn-default" href="<c:url value='/seguimentArxiuPendents/expedients/reintentar'/>" >
						<span id="expSeleccioCount" class="badge">${fn:length(expSeleccio)}</span> <spring:message code="accio.massiva.boto.custodiar"/>
					</a>
				</div>
			</script>
			<table 
				id="arxiuPendentsExpedients" 
				data-toggle="datatable" 
				data-url="<c:url value="/seguimentArxiuPendents/expedients"/>" 
				data-search-enabled="false"
				data-default-order="2"
				data-default-dir="desc"
				data-botons-template="#expedientsBotonsTemplate"
				class="table table-striped table-bordered" 
				data-rowhref-toggle="modal"
				data-selection-enabled="true"
				data-save-state="true"
				style="width:100%">
				<thead> 
					<tr>
						<th data-col-name="expedientNumeroNom" data-template="#cellExpedientLink1" data-orderable="false"><spring:message code="seguiment.list.columna.expedient"/>
							<script id="cellExpedientLink1" type="text/x-jsrender">
								<a href="<c:url value="/contingut/{{:id}}"/>">{{:expedientNumeroNom}}</a>	
							</script>
						</th>
						<th data-col-name="metaExpedientNom" data-orderable="false"><spring:message code="seguiment.list.columna.metaExpedient"/></th>
						<th data-col-name="dataDarrerIntent" data-type="datetime" data-converter="datetime"><spring:message code="seguiment.list.columna.dataDarrerIntent"/></th>
						<th data-col-name="id" data-orderable="false" data-template="#cellAccionsExpedientsTemplate" width="10%">
							<script id="cellAccionsExpedientsTemplate" type="text/x-jsrender">
								<a href="<c:url value="/expedient/{{:id}}/guardarExpedientArxiu?origin=seguiment"/>" class="btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="accio.massiva.boto.custodiar"/></a>
							</script>
						</th>
					</tr>
				</thead>
			</table>
		</div>
		
		<!------------------------------ TABPANEL DOCUMENTS ------------------------------------->
		<div class="tab-pane" id="documents">
			<c:url value="/seguimentArxiuPendents/filtrar/documents" var="formAction"/>
			<form:form id="seguimentFiltreDocumentsForm" action="${ formAction }" method="post" cssClass="well" commandName="commandDocuments">
				<div class="row">
					<div class="col-md-4">
						<rip:inputText name="elementNom2" inline="true" placeholderKey="seguiment.list.filtre.camp.documentNom"/>
					</div>	
					<div class="col-md-4">	
						<c:url value="/expedientajax/expedient" var="urlConsultaInicial"/>
						<c:url value="/expedientajax/expedient" var="urlConsultaLlistat"/>
						<rip:inputSuggest 
		 					name="expedientId2"  
		 					urlConsultaInicial="${urlConsultaInicial}"
		 					urlConsultaLlistat="${urlConsultaLlistat}"
		 					placeholderKey="seguiment.list.filtre.camp.expedient"
		 					suggestValue="id"
		 					suggestText="nomINumero"
		 					inline="true"/>
					</div>				
					<div class="col-md-4">					
						<rip:inputSelect name="metaExpedientId2" optionItems="${metaExpedients}" optionMinimumResultsForSearch="0" optionValueAttribute="id" emptyOption="true" optionTextAttribute="codiSiaINom" placeholderKey="seguiment.list.filtre.camp.metaExpedient" inline="true"/>
					</div>
					<div class="col-md-4 pull-right">
						<div class="pull-right">
							<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
							<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
						</div>
					</div>
				</div>
			</form:form>

			<script id="documentsBotonsTemplate" type="text/x-jsrender">
				<div class="btn-group pull-right">
					<a id="docSeleccioAll" title="<spring:message code="seguiment.list.documents.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></a>
					<a id="docSeleccioNone" title="<spring:message code="seguiment.list.documents.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></a>
					<a id="docReintentar" class="btn btn-default" href="<c:url value='/seguimentArxiuPendents/documents/reintentar'/>" >
						<span id="docSeleccioCount" class="badge">${fn:length(docSeleccio)}</span> <spring:message code="accio.massiva.boto.custodiar"/>
					</a>
				</div>
			</script>
			<table 
				id="arxiuPendentsDocuments" 
				data-toggle="datatable" 
				data-url="<c:url value="/seguimentArxiuPendents/documents"/>" 
				data-search-enabled="false"
				data-default-order="5"
				data-default-dir="desc"
				data-botons-template="#documentsBotonsTemplate"
				class="table table-striped table-bordered" 
				data-rowhref-toggle="modal"
				data-selection-enabled="true"
				data-save-state="true"
				style="width:100%">
				<thead> 
					<tr>
						<th data-col-name="expedientId" data-visible="false"></th>
						<th data-col-name="expedientArxiuPropagat" data-visible="false"></th>
						<th data-col-name="elementNom"><spring:message code="seguiment.list.columna.document"/></th>
						<th data-col-name="expedientNumeroNom" data-template="#cellExpedientLink2" data-orderable="false"><spring:message code="seguiment.list.columna.expedient"/>
							<script id="cellExpedientLink2" type="text/x-jsrender">
								<a href="<c:url value="/contingut/{{:expedientId}}"/>">{{:expedientNumeroNom}}</a>	
							</script>
						</th>
						<th data-col-name="metaExpedientNom" data-orderable="false"><spring:message code="seguiment.list.columna.metaExpedient"/></th>
						<th data-col-name="dataDarrerIntent" data-type="datetime" data-converter="datetime"><spring:message code="seguiment.list.columna.dataDarrerIntent"/></th>
						<!--<th data-col-name="annex" data-orderable="false" data-template="#cellAnnex" width="1%">
							<spring:message code="seguiment.list.columna.annex"/>
							<script id="cellAnnex" type="text/x-jsrender">
								{{if annex}}<span class="fa fa-check"></span>{{/if}}
							</script>
						</th>-->
						<th data-col-name="annex" data-visible="false"></th>
						<th data-col-name="id" data-orderable="false" data-template="#cellAccionsDocumentsTemplate" width="10%">
							<script id="cellAccionsDocumentsTemplate" type="text/x-jsrender">
								{{if !expedientArxiuPropagat}}
									<div class="disabledMsg" title="<spring:message code="disabled.button.primerGuardarExpedientArxiu"/>"><a class="disabled btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="accio.massiva.boto.custodiar"/></a></div>
								{{else}}
									<a href="<c:url value="/contingut/{{:expedientId}}/document/{{:id}}/guardarDocumentArxiu?origin=seguiment"/>" class="btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="accio.massiva.boto.custodiar"/></a>
								{{/if}}
								{{if annex}}
									<div id="annex" style="display: none;">
								{{/if}}
							</script>
						</th>
					</tr>
				</thead>
			</table>			
		</div>
		
		<!------------------------------ TABPANEL INTERESSATS ------------------------------------->
		<div class="tab-pane" id="interessats">
			<c:url value="/seguimentArxiuPendents/filtrar/interessats" var="formAction"/>
			<form:form id="seguimentFiltreInteressatsForm" action="${ formAction }" method="post" cssClass="well" commandName="commandInteressats">
				<div class="row">
					<div class="col-md-4">
						<rip:inputText name="elementNom3" inline="true" placeholderKey="seguiment.list.filtre.camp.interessatNom"/>
					</div>		
					<div class="col-md-4">	
						<c:url value="/expedientajax/expedient" var="urlConsultaInicial"/>
						<c:url value="/expedientajax/expedient" var="urlConsultaLlistat"/>
						<rip:inputSuggest 
		 					name="expedientId3"  
		 					urlConsultaInicial="${urlConsultaInicial}"
		 					urlConsultaLlistat="${urlConsultaLlistat}"
		 					placeholderKey="seguiment.list.filtre.camp.expedient"
		 					suggestValue="id"
		 					suggestText="nomINumero"
		 					inline="true"/>
					</div>				
					<div class="col-md-4">					
						<rip:inputSelect name="metaExpedientId3" optionItems="${metaExpedients}" optionMinimumResultsForSearch="0" optionValueAttribute="id" emptyOption="true" optionTextAttribute="codiSiaINom" placeholderKey="seguiment.list.filtre.camp.metaExpedient" inline="true"/>
					</div>
					<div class="col-md-4 pull-right">
						<div class="pull-right">
							<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
							<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
						</div>
					</div>
				</div>
			</form:form>

			<script id="interessatsBotonsTemplate" type="text/x-jsrender">
				<div class="btn-group pull-right">
					<a id="intSeleccioAll" title="<spring:message code="seguiment.list.interessats.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></a>
					<a id="intSeleccioNone" title="<spring:message code="seguiment.list.interessats.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></a>
					<a id="intReintentar" class="btn btn-default" href="<c:url value='/seguimentArxiuPendents/interessats/reintentar'/>" >
						<span id="intSeleccioCount" class="badge">${fn:length(intSeleccio)}</span> <spring:message code="accio.massiva.boto.custodiar"/>
					</a>
				</div>
			</script>
			<table 
				id="arxiuPendentsInteressats" 
				data-toggle="datatable" 
				data-url="<c:url value="/seguimentArxiuPendents/interessats"/>" 
				data-search-enabled="false"
				data-default-order="5"
				data-default-dir="desc"
				data-botons-template="#interessatsBotonsTemplate"
				class="table table-striped table-bordered" 
				data-rowhref-toggle="modal"
				data-selection-enabled="true"
				data-save-state="true"
				style="width:100%">
				<thead> 
					<tr>
						<th data-col-name="expedientId" data-visible="false"></th>
						<th data-col-name="expedientArxiuPropagat" data-visible="false"></th>
						<th data-col-name="elementNom"><spring:message code="seguiment.list.columna.interessat"/></th>
						<th data-col-name="expedientNumeroNom" data-template="#cellExpedientLink3" data-orderable="false"><spring:message code="seguiment.list.columna.expedient"/>
							<script id="cellExpedientLink3" type="text/x-jsrender">
								<a href="<c:url value="/contingut/{{:expedientId}}"/>">{{:expedientNumeroNom}}</a>	
							</script>
						</th>						
						<th data-col-name="metaExpedientNom" data-orderable="false"><spring:message code="seguiment.list.columna.metaExpedient"/></th>
						<th data-col-name="dataDarrerIntent" data-type="datetime" data-converter="datetime"><spring:message code="seguiment.list.columna.dataDarrerIntent"/></th>
						<th data-col-name="id" data-orderable="false" data-template="#cellAccionsInteressatsTemplate" width="10%">
							<script id="cellAccionsInteressatsTemplate" type="text/x-jsrender">
								{{if !expedientArxiuPropagat}}
									<div class="disabledMsg" title="<spring:message code="disabled.button.primerGuardarExpedientArxiu"/>"><a class="disabled btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="accio.massiva.boto.custodiar"/></a></div>
								{{else}}
									<a href="<c:url value="/expedient/{{:expedientId}}/guardarInteressatsArxiu?origin=seguiment"/>" class="btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="accio.massiva.boto.custodiar"/></a>
								{{/if}}
							</script>
						</th>
					</tr>
				</thead>
			</table>			
		</div>				
	</div>
	
</body>
</html>
