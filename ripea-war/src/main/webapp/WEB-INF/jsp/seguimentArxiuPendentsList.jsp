<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="seguiment.arxiu.pendents.list.titol"/></title>
	
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
	$('#arxiuPendentsExpedients').on('draw.dt', function (e, settings) {
		$('.disabledMsg').tooltip();
	});
	$('#arxiuPendentsDocuments').on('draw.dt', function (e, settings) {
		$('.disabledMsg').tooltip();
	});
	$('#arxiuPendentsInteressats').on('draw.dt', function (e, settings) {
		$('.disabledMsg').tooltip();
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
						<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionMinimumResultsForSearch="0" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="seguiment.list.filtre.camp.metaExpedient" inline="true"/>
					</div>
					<div class="col-md-4 pull-right">
						<div class="pull-right">
							<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
							<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
						</div>
					</div>
				</div>
			</form:form>
		
			<table 
				id="arxiuPendentsExpedients" 
				data-toggle="datatable" 
				data-url="<c:url value="/seguimentArxiuPendents/expedients"/>" 
				data-search-enabled="false"
				data-default-order="2"
				data-default-dir="desc" 
				class="table table-striped table-bordered" 
				data-rowhref-toggle="modal"
				style="width:100%">
				<thead> 
					<tr>
						<th data-col-name="expedientNumeroNom"><spring:message code="seguiment.list.columna.expedient"/></th>
						<th data-col-name="metaExpedientNom" data-orderable="false"><spring:message code="seguiment.list.columna.metaExpedient"/></th>
						<th data-col-name="dataDarrerIntent" data-type="datetime" data-converter="datetime"><spring:message code="seguiment.list.columna.dataDarrerIntent"/></th>
						<th data-col-name="id" data-orderable="false" data-template="#cellAccionsExpedientsTemplate" width="10%">
							<script id="cellAccionsExpedientsTemplate" type="text/x-jsrender">
								<a href="<c:url value="/expedient/{{:id}}/guardarExpedientArxiu?origin=seguiment"/>" class="btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.reintentar"/></a>
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
						<rip:inputSelect name="metaExpedientId2" optionItems="${metaExpedients}" optionMinimumResultsForSearch="0" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="seguiment.list.filtre.camp.metaExpedient" inline="true"/>
					</div>
					<div class="col-md-4 pull-right">
						<div class="pull-right">
							<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
							<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
						</div>
					</div>
				</div>
			</form:form>
		
			<table 
				id="arxiuPendentsDocuments" 
				data-toggle="datatable" 
				data-url="<c:url value="/seguimentArxiuPendents/documents"/>" 
				data-search-enabled="false"
				data-default-order="5"
				data-default-dir="desc" 
				class="table table-striped table-bordered" 
				data-rowhref-toggle="modal"
				style="width:100%">
				<thead> 
					<tr>
						<th data-col-name="expedientId" data-visible="false"></th>
						<th data-col-name="expedientArxiuPropagat" data-visible="false"></th>
						<th data-col-name="elementNom"><spring:message code="seguiment.list.columna.document"/></th>
						<th data-col-name="expedientNumeroNom"><spring:message code="seguiment.list.columna.expedient"/></th>
						<th data-col-name="metaExpedientNom" data-orderable="false"><spring:message code="seguiment.list.columna.metaExpedient"/></th>
						<th data-col-name="dataDarrerIntent" data-type="datetime" data-converter="datetime"><spring:message code="seguiment.list.columna.dataDarrerIntent"/></th>
						<th data-col-name="id" data-orderable="false" data-template="#cellAccionsDocumentsTemplate" width="10%">
							<script id="cellAccionsDocumentsTemplate" type="text/x-jsrender">
								{{if !expedientArxiuPropagat}}
									<div class="disabledMsg" title="<spring:message code="disabled.button.primerGuardarExpedientArxiu"/>"><a class="disabled btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.reintentar"/></a></div>
								{{else}}
									<a href="<c:url value="/contingut/{{:expedientId}}/document/{{:id}}/guardarDocumentArxiu?origin=seguiment"/>" class="btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.reintentar"/></a>
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
						<rip:inputSelect name="metaExpedientId3" optionItems="${metaExpedients}" optionMinimumResultsForSearch="0" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="seguiment.list.filtre.camp.metaExpedient" inline="true"/>
					</div>
					<div class="col-md-4 pull-right">
						<div class="pull-right">
							<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
							<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
						</div>
					</div>
				</div>
			</form:form>
		
			<table 
				id="arxiuPendentsInteressats" 
				data-toggle="datatable" 
				data-url="<c:url value="/seguimentArxiuPendents/interessats"/>" 
				data-search-enabled="false"
				data-default-order="5"
				data-default-dir="desc" 
				class="table table-striped table-bordered" 
				data-rowhref-toggle="modal"
				style="width:100%">
				<thead> 
					<tr>
						<th data-col-name="expedientId" data-visible="false"></th>
						<th data-col-name="expedientArxiuPropagat" data-visible="false"></th>
						<th data-col-name="elementNom"><spring:message code="seguiment.list.columna.interessat"/></th>
						<th data-col-name="expedientNumeroNom"><spring:message code="seguiment.list.columna.expedient"/></th>
						<th data-col-name="metaExpedientNom" data-orderable="false"><spring:message code="seguiment.list.columna.metaExpedient"/></th>
						<th data-col-name="dataDarrerIntent" data-type="datetime" data-converter="datetime"><spring:message code="seguiment.list.columna.dataDarrerIntent"/></th>
						<th data-col-name="id" data-orderable="false" data-template="#cellAccionsInteressatsTemplate" width="10%">
							<script id="cellAccionsInteressatsTemplate" type="text/x-jsrender">
								{{if !expedientArxiuPropagat}}
									<div class="disabledMsg" title="<spring:message code="disabled.button.primerGuardarExpedientArxiu"/>"><a class="disabled btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.reintentar"/></a></div>
								{{else}}
									<a href="<c:url value="/expedient/{{:expedientId}}/guardarInteressatsArxiu?origin=seguiment"/>" class="btn btn-default"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.reintentar"/></a>
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
