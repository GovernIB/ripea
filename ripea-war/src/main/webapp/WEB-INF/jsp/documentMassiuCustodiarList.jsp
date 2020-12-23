<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<rip:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="accio.massiva.titol.custodiar"/></title>
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
	table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
		background-color: #fcf8e3;
		color: #666666;
	}
	table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
		cursor: pointer;
	}
</style>
<script>
$(document).ready(function() {
		var tipus = $('#metaDocumentId').val();

		
		$('#metaExpedientId').on('change', function() {

			var tipus = $(this).val();
			
			$('#expedientId option[value!=""]').remove();
			$('#expedientId').select2('val', '', true);
			
			$('#metaDocumentId option[value!=""]').remove();
			$('#metaDocumentId').select2('val', '', true);
			if (tipus != undefined && tipus != "") {
				$.get("<c:url value="/massiu/expedients/"/>" + tipus).done(function(data){
					
					for (var i = 0; i < data.length; i++) {
						$('#expedientId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					}
				}).fail(function() {
					alert("<spring:message code="error.jquery.ajax"/>");
				});

				$.get("<c:url value="/massiu/metaDocuments/"/>" + tipus).done(function(data){
					
					for (var i = 0; i < data.length; i++) {
						$('#metaDocumentId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					}
				}).fail(function() {
					alert("<spring:message code="error.jquery.ajax"/>");
				});			
			}
		});
		
		$('#taulaDades').on('selectionchange.dataTable', function (e, accio, ids) {
			$.get(
					"custodiar/" + accio,
					{ids: ids},
					function(data) {
						$("#seleccioCount").html(data);
					}
			);
		});
		$('#taulaDades').on('draw.dt', function () {
			var tipus = $('#metaDocumentId').val();


			updateSelectionForTipusDocument(tipus);
			
			$('#seleccioAll').on('click', function() {
				$.get(
						"custodiar/select",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('refresh');
						}
				);
				return false;
			});
			$('#seleccioNone').on('click', function() {
				$.get(
						"custodiar/deselect",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('select-none');
							$('#taulaDades').webutilDatatable('refresh');
						}
				);
				return false;
			});

		    $("#custodiar").on('click', function(data){  	
			  	$("#taula").html("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x'/></div>");
		    });
			
		});
		$('#metaExpedientId').trigger('change');
		$('#metaDocumentId').trigger('change');


				





				
});

function enableDisableSelection($this, tipus) {
    if (tipus != undefined && tipus != "") {
    	$this.removeClass('selection-disabled');
    	$('thead tr:nth-child(1) th:nth-child(1)').removeClass('selection-disabled');
    } else {
    	$this.addClass('selection-disabled');
    	$('thead tr:nth-child(1) th:nth-child(1)').addClass('selection-disabled');
		$.get(
				"custodiar/deselect",
				function(data) {
					$("#seleccioCount").html(data);
					$('#taulaDades').webutilDatatable('select-none');
				}
			);
	}
}

function updateSelectionForTipusDocument(currentTipus) {
	var tipusInStorage = sessionStorage.getItem('Massiu_tipusDocument', currentTipus);

	if (tipusInStorage != null && tipusInStorage != currentTipus) {
		$.get(
				"custodiar/deselect",
				function(data) {
					$("#seleccioCount").html(data);
					$('#taulaDades').webutilDatatable('select-none');
					$('#taulaDades').webutilDatatable('refresh');
				}
		);
	}

	var tipusInStorage = sessionStorage.setItem('Massiu_tipusDocument', currentTipus);
}

</script>

</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="contingutMassiuFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<rip:inputSelect name="tipusElement"  optionEnum="ContingutTipusEnumDto" placeholderKey="accio.massiva.list.filtre.tipuselement" emptyOption="true" inline="true" disabled="${contingutMassiuFiltreCommand.bloquejarTipusElement}" netejar="${not contingutMassiuFiltreCommand.bloquejarTipusElement}"/>
			</div>
			<div class="col-md-4">
				<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="nom" optionMinimumResultsForSearch="3" emptyOption="true" placeholderKey="accio.massiva.list.filtre.tipusexpedient" inline="true" disabled="${contingutMassiuFiltreCommand.bloquejarMetaExpedient}"/>
			</div>
			<div class="col-md-4">
				<rip:inputSelect name="expedientId" optionItems="${expedients}" optionValueAttribute="id" optionTextAttribute="nom" optionMinimumResultsForSearch="3" emptyOption="true" placeholderKey="accio.massiva.list.filtre.expedient" inline="true" disabled="${contingutMassiuFiltreCommand.bloquejarMetaDocument}"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<rip:inputSelect name="metaDocumentId" optionItems="${metaDocuments}" optionValueAttribute="id" optionTextAttribute="nom" optionMinimumResultsForSearch="3" emptyOption="true" placeholderKey="accio.massiva.list.filtre.tipusdocument" inline="true" disabled="${contingutMassiuFiltreCommand.bloquejarMetaDocument}"/>
			</div>
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="accio.massiva.list.filtre.nom"/>
			</div>
<!-- 			<div class="col-md-4"> -->
<%-- 				<rip:inputSelect name="metaDada" optionItems="${metaDades}" optionValueAttribute="id" optionTextAttribute="nom" optionMinimumResultsForSearch="3" emptyOption="true" placeholderKey="accio.massiva.list.filtre.metadada" inline="true" disabled="${not empty contingutMassiuFiltreCommand.bloquejarTipusElement}"/> --%>
<!-- 			</div> -->
			<div class="col-md-2">
				<rip:inputDate name="dataInici" inline="true" placeholderKey="accio.massiva.list.filtre.datainici"/>
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataFi" inline="true" placeholderKey="accio.massiva.list.filtre.datafi"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4 pull-right">
				<div class="pull-right">
					<button style="display:none" type="submit" name="accio" value="filtrar" ><span class="fa fa-filter"></span></button>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="btn-group pull-right">
			<a id="seleccioAll" title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></a>
			<a id="seleccioNone" title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></a>
			<a id="custodiar" class="btn btn-default" href="./custodiar/custodiar" >
				<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="accio.massiva.boto.custodiar"/>
			</a>

		</div>
	</script>
	<div id="taula">
		<table id="taulaDades" 
			data-toggle="datatable" 
			data-url="<c:url value="/massiu/custodiar/datatable"/>"
			data-filter="#contingutMassiuFiltreCommand"
			class="table table-bordered table-striped" 
			data-default-order="7" 
			data-default-dir="desc"
			data-botons-template="#botonsTemplate"
			data-selection-enabled="true"
			style="width:100%">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"></th>
					<th data-col-name="expedient" data-visible="false"></th>
					<th data-col-name="carpeta" data-visible="false"></th>
					<th data-col-name="document" data-visible="false"></th>
					<th data-col-name="metaDocument.nom" data-orderable="true" width="15%"><spring:message code="accio.massiva.list.column.metadocument"/></th>
					<th data-col-name="path" data-template="#cellPathTemplate" data-orderable="false">
						<spring:message code="accio.massiva.list.column.ubicacio"/>
						<script id="cellPathTemplate" type="text/x-jsrender">
						{{if path}}{{for path}}/
							{{if expedient}}<span class="fa ${iconaExpedient}" title="<spring:message code="contingut.icona.expedient"/>"></span>
							{{else carpeta}}<span class="fa ${iconaCarpeta}" title="<spring:message code="contingut.icona.carpeta"/>"></span>
							{{else document}}<span class="fa ${iconaDocument}" title="<spring:message code="contingut.icona.document"/>"></span>{{/if}}
							{{:nom}}
						{{/for}}{{/if}}
					</script>
					</th>
					<th data-col-name="nom" data-ordenable="true"><spring:message code="accio.massiva.list.column.nom"/></th>
					<th data-col-name="createdDate" data-ordenable="true" data-converter="datetime" width="15%"><spring:message code="accio.massiva.list.column.datacreacio"/></th>
					<th data-col-name="createdBy.codi" data-ordenable="true" width="15%"><spring:message code="accio.massiva.list.column.creatper"/></th>
	<%-- 				<th data-col-name="nomPropietariEscriptoriPare" data-orderable="false" width="20%"><spring:message code="expedient.list.user.columna.agafatper"/></th> --%>
				</tr>
			</thead>
		</table>
	</div>

	
</body>
</html>