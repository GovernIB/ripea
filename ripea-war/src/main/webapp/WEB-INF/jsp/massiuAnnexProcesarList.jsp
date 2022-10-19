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
	<title><spring:message code="massiu.procesar.annexos.pendents"/></title>
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
	
	$('#taula').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"procesarAnnexosPendents/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	$('#taula').one('draw.dt', function () {
		$('#seleccioAll').on('click', function() {
			$.get(
					"procesarAnnexosPendents/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taula').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"procesarAnnexosPendents/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taula').webutilDatatable('select-none');
						$('#taula').webutilDatatable('refresh');
					}
			);
			return false;
		});


		
	});


	$('#taula').on('draw.dt', function () {

		var metaExpedientId = $('#metaExpedientId').val();
		$('thead tr th:nth-child(1)', $('#taula')).each(function() {
			enableDisableSelection($(this), metaExpedientId);
		});
		$('tbody tr td:nth-child(1)', $('#taula')).each(function() {
			enableDisableSelection($(this), metaExpedientId);
		});

	});	

							

});

	
function enableDisableSelection($this, tipus) {
    if (tipus != undefined && tipus != "") {
    	$this.removeClass('selection-disabled');
    	$('thead tr:nth-child(1) th:nth-child(1)').removeClass('selection-disabled');
    	$('.botons .btn-group button').removeAttr('disabled');
    } else {
    	$this.addClass('selection-disabled');
    	$('thead tr:nth-child(1) th:nth-child(1)').addClass('selection-disabled');
		$.get(
				"deselect",
				function(data) {
					$("#seleccioCount").html(data);
					$('#taula').webutilDatatable('select-none');
				}
			);
		$('.botons .btn-group button').attr('disabled','disabled');
	}
}



</script>


</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="massiuAnnexProcesarFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="nom" inline="true" placeholderKey="accio.massiva.list.filtre.nom"/>
			</div>
			<div class="col-md-4">
				<rip:inputText name="numero" inline="true" placeholderKey="accio.massiva.list.column.numero"/>
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataInici" inline="true" placeholderKey="accio.massiva.list.filtre.datainici"/>
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataFi" inline="true" placeholderKey="accio.massiva.list.filtre.datafi"/>
			</div>
			
			<div class="col-md-4">					
				<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionMinimumResultsForSearch="1" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="expedient.peticio.list.placeholder.metaExpedient" inline="true"/>
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
			<button id="seleccioAll" title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
			<button id="seleccioNone" title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>

			<button id="processar" class="btn btn-default" href="./procesarAnnexosPendents/adjuntarExpedient" data-toggle="modal" data-refresh-pagina="true">
				<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="massiu.list.column.btn.crear.db"/>
			</button>

		</div>
	</script>
	<div id="taulaDiv">
		<table id="taula" 
			data-toggle="datatable" 
			data-url="<c:url value="/massiu/procesarAnnexosPendents/datatable"/>"
			class="table table-bordered table-striped" 
			data-default-order="5" 
			data-default-dir="desc"
			data-botons-template="#botonsTemplate"
			data-selection-enabled="true"
			style="width:100%">

			<thead>
				<tr>
					<th data-col-name="expedientId" data-visible="false"></th>
					<th data-col-name="expedientPeticioId" data-visible="false"></th>
					<th data-col-name="titol"><spring:message code="accio.massiva.list.column.nom"/></th>
					<th data-col-name="registreNumero" data-orderable="false"><spring:message code="accio.massiva.list.column.numero"/></th>
					
					<th data-col-name="expedientNumeroNom" data-template="#cellExpedientLink" data-orderable="false"><spring:message code="accio.massiva.list.column.expedient"/>
						<script id="cellExpedientLink" type="text/x-jsrender">
						<a href="<c:url value="/contingut/{{:expedientId}}"/>">{{:expedientNumeroNom}}</a>	
					</script>
					</th>
					<th data-col-name="expedientCreatedDate" data-converter="datetime" width="15%"><spring:message code="accio.massiva.list.column.expedientCreatEl"/></th>

					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						{{if documentId==null}}
							<a href="<c:url value="/expedientPeticio/{{:id}}/{{:expedientPeticioId}}/reintentar"/>" class="btn btn-default" data-toggle="modal" data-refresh-pagina="true"><spring:message code="massiu.list.column.btn.crear.db"/></a>	
						{{/if}}
					</script>
					</th>
				</tr>
			</thead>
		</table>
	</div>
	
</body>
</html>