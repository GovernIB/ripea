<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="expedient.peticio.comunicades.list.titol"/></title>
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

table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
	cursor: pointer;
}

table.dataTable td {
	word-wrap: break-word; max-width: 1px;
}

</style>



<script>
	$(document).ready(function() {


		$('#taulaDades').on('selectionchange.dataTable', function (e, accio, ids) {
			$.get("expedientPeticioComunicades/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
			);
		});

		$('#taulaDades').one('draw.dt', function () {

			$('#seleccioAll').on('click', function() {
				$.get("./expedientPeticioComunicades/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('refresh');
					}
				);
				return false;
			});
			$('#seleccioNone').on('click', function() {
				$.get(
					"./expedientPeticioComunicades/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('select-none');
						$('#taulaDades').webutilDatatable('refresh');
					}
				);
				return false;
			});
		});




    	$('#nomesAmbErrorsConsultaBtn').click(function() {
    		nomesAmbErrors = !$(this).hasClass('active');
    		$('#nomesAmbErrorsConsulta').val(nomesAmbErrors);
    	});

    	if ($('#nomesAmbErrorsConsulta').val() == 'true') {
    		$('#nomesAmbErrorsConsultaBtn').addClass('active')
		} else {
			$('#nomesAmbErrorsConsultaBtn').removeClass('active')
		}
		

		
	});


</script>
</head>
<body>


	<form:form id="expedientPeticioFiltreForm" action="" method="post" cssClass="well" commandName="expedientPeticioFiltreCommand">

		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="numero" inline="true" placeholderKey="expedient.peticio.list.placeholder.numero"/>
			</div>	
			<div class="col-md-2">							
				<rip:inputSelect name="estatAll" inline="true" optionEnum="ExpedientPeticioEstatEnumDto" emptyOption="true" placeholderKey="expedient.peticio.list.placeholder.estat"/>
			</div>	
			<div class="col-md-2">							
				<rip:inputDate name="dataInicial" inline="true" placeholderKey="expedient.peticio.list.placeholder.dataComunicacioInicial"/>
			</div>	
			<div class="col-md-2	">							
				<rip:inputDate name="dataFinal" inline="true" placeholderKey="expedient.peticio.list.placeholder.dataComunicacioFinal"/>
			</div>	
			<div class="col-md-2" style="padding-left: 0;">
				<button id="nomesAmbErrorsConsultaBtn" style="width: 45px;" title="<spring:message code="expedient.peticio.list.placeholder.nomesAmbErrorsConsulta"/>" class="btn btn-default" data-toggle="button"><span class="fa fa-warning"></span></button>
				<rip:inputHidden name="nomesAmbErrorsConsulta"/>
			</div>			
				
			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none;"></button>
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
			<a class="btn btn-default" href="./expedientPeticioComunicades/comunicadaConsultarMassiu" >
				<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="comu.boto.consultar"/>
			</a>

		</div>
	</script>
	
	<table
		id="taulaDades"
		data-toggle="datatable" 
		data-url="<c:url value="/expedientPeticioComunicades/datatable"/>"
		class="table table-bordered table-striped table-hover" 
		data-default-order="2"
		data-default-dir="desc" 
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="identificador" width="20%"><spring:message code="expedient.peticio.list.columna.numero"/></th>
				<th data-col-name="dataAlta" data-type="datetime" data-converter="datetime"><spring:message code="expedient.peticio.list.columna.comunicacio"/></th>
				<th data-col-name="estat" width="10%"  data-orderable="false" data-template="#cellEstatTemplate">
					<spring:message code="expedient.peticio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">

						{{if estat == 'CREAT'}}
							<spring:message code="expedient.peticio.estat.enum.CREAT"/>
						{{else estat == 'PENDENT'}}
							<spring:message code="expedient.peticio.estat.enum.PENDENT"/>
						{{else estat == 'PROCESSAT_PENDENT'}}
							<spring:message code="expedient.peticio.estat.enum.PROCESSAT_PENDENT"/>
						{{else estat == 'PROCESSAT_NOTIFICAT'}}
							<spring:message code="expedient.peticio.estat.enum.PROCESSAT_NOTIFICAT"/>
						{{else estat == 'REBUTJAT'}}
							<spring:message code="expedient.peticio.estat.enum.REBUTJAT"/>
						{{/if}}
					</script>
				</th>
				
				<th data-col-name="consultaWsError" data-template="#cellConsultaWsError" width="1%">
					<spring:message code="expedient.peticio.list.columna.consultaWsError"/>
					<script id="cellConsultaWsError" type="text/x-jsrender">
						{{if consultaWsError}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>

				<th data-col-name="consultaWsErrorDescShort" width="45%" data-orderable="false" data-template="#cellConsultaWsErrorDesc">
					<spring:message code="expedient.peticio.list.columna.consultaWsErrorDesc"/>
					<script id="cellConsultaWsErrorDesc" type="text/x-jsrender">
						<span title="{{:consultaWsErrorDesc}}">{{:consultaWsErrorDescShort}}</span>
					</script>
				</th>
				<th data-col-name="consultaWsErrorDate" data-type="datetime" data-converter="datetime"><spring:message code="expedient.peticio.list.columna.consultaWsErrorDate"/></th>
				
				<th data-col-name="pendentCanviEstatDistribucio" data-template="#cellPendentCanviEstatDistribucio" width="1%" data-orderable="false">
					<spring:message code="expedient.peticio.list.columna.pendentCanviEstatDistribucio"/>
					<script id="cellPendentCanviEstatDistribucio" type="text/x-jsrender">
						{{if pendentCanviEstatDistribucio}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
				<th data-col-name="reintentsCanviEstatDistribucio" data-orderable="false" width="1%"><spring:message code="expedient.peticio.list.columna.reintentsCanviEstatDistribucio"/></th>
				<th data-col-name="anotacioId" data-orderable="false" width="5%">Id</th>
				
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="15%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						{{if estat == 'CREAT'}}
							<a href="<c:url value="/expedientPeticioComunicades/comunicadaConsultar/{{:id}}"/>" class="btn btn-default" ><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.consultar"/></a>
						{{/if}}
					</script>
				</th>
				
				<th data-col-name="consultaWsErrorDesc" data-visible="false"></th>
			</tr>
		</thead>
	</table>
</body>