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
	<title><spring:message code="accio.massiva.titol.canviEstat"/></title>
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


//################################################## document ready START ##############################################################
$(document).ready(function() {

	
	
	$('#metaExpedientId').on('change', function() {
		
		$('button[type="submit"][value="filtrar"]')[0].click();
		$('#seleccioNone').click();
	});
					
	
	$('#taulaDades').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"canviEstat/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	
	$('#taulaDades').one('draw.dt', function () {
		
		$('#seleccioAll').on('click', function() {
			$.get(
					"canviEstat/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"canviEstat/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taulaDades').webutilDatatable('select-none');
						$('#taulaDades').webutilDatatable('refresh');
					}
			);
			return false;
		});
	});

	
	$('#taulaDades').on('draw.dt', function () {

		var metaExpedientId = $('#metaExpedientId').val();
		$('thead tr th:nth-child(1)', $('#taulaDades')).each(function() {
			enableDisableSelection($(this), metaExpedientId);
		});
		$('tbody tr td:nth-child(1)', $('#taulaDades')).each(function() {
			enableDisableSelection($(this), metaExpedientId);
		});

	});	



	$('#metaExpedientId').on('change', function() {
		metaExpedientId = $(this).val();

		if (metaExpedientId) {
			$("#expedientId").data('urlParamAddicional', metaExpedientId);
		} else {
			$("#expedientId").data('urlParamAddicional', null);
			metaExpedientId = 0;
		}
		
		$.get("<c:url value="/expedient/estatValues/"/>" + metaExpedientId)
		.done(function(data) {
			
			$('#expedientEstatId').select2('val', '', true);
			$('#expedientEstatId option[value!=""]').remove();

			let listSize = data.length > 1 ? data.length - 1 : data.length; // don't add last estat 'TANCAT'
			for (var i = 0; i < listSize; i++) {
				$('#expedientEstatId').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
			}
		})
		.fail(function() {
			alert("<spring:message code="error.jquery.ajax"/>");
		});
	});


});//################################################## document ready END ##############################################################

	
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
					$('#taulaDades').webutilDatatable('select-none');
				}
			);
		$('.botons .btn-group button').attr('disabled','disabled');
	}
}



</script>

</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="contingutMassiuFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="codiSiaINom" optionMinimumResultsForSearch="3" emptyOption="true" placeholderKey="accio.massiva.list.filtre.tipusexpedient" inline="true"/>
			</div>
			<div class="col-md-4">					
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
					inline="true"
					urlParamAddicional="${contingutMassiuFiltreCommand.metaExpedientId}"/>	
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataInici" inline="true" placeholderKey="accio.massiva.list.filtre.datainici"/>
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataFi" inline="true" placeholderKey="accio.massiva.list.filtre.datafi"/>
			</div>			
		</div>
		<div class="row">

			<div class="col-md-4">
				<rip:inputSelect 
					name="expedientEstatId" 
					optionItems="${expedientEstatsOptions}"
					optionValueAttribute="id"
					emptyOption="true" 
					optionTextAttribute="nom"
					placeholderKey="expedient.list.user.placeholder.estat" 
					inline="true" />
			</div>

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
			<button type="button" id="seleccioAll" title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></a>
			<button type="button" id="seleccioNone" title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></a>
			<button type="button" class="btn btn-default" href="./canviEstat/canviarEstat" data-toggle="modal" data-refresh-pagina="false" data-maximized="true">
				<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="accio.massiva.boto.canviEstat"/>
			</button>
		</div>
	</script>
		
	<table id="taulaDades" 
		data-toggle="datatable" 
		data-url="<c:url value="/massiu/canviEstat/datatable"/>"
		data-filter="#contingutMassiuFiltreCommand"
		class="table table-bordered table-striped" 
		data-default-order="5" 
		data-default-dir="desc"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="expedientEstat" data-visible="false"></th>
				
				<th data-col-name="numeroINom" data-template="#cellExpedientLink" data-orderable="false" width="15%"><spring:message code="accio.massiva.list.column.expedient"/>
					<script id="cellExpedientLink" type="text/x-jsrender">
						<a href="<c:url value="/contingut/{{:id}}"/>">{{:numeroINom}}</a>	
					</script>
				</th>
				<th data-col-name="metaExpedient.codiSiaINom" data-orderable="true" width="15%"><spring:message code="accio.massiva.list.column.metaexpedient"/></th>
				<th data-col-name="estat" data-orderable="false" data-template="#cellEstatTemplate" width="11%">
					<spring:message code="expedient.list.user.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if expedientEstat != null && estat != 'TANCAT'}}
							<span class="fa fa-folder-open"></span>&nbsp;{{:expedientEstat.nom}}
						{{else}}
							{{if estat == 'OBERT'}}
								<span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.estat.enum.OBERT"/>
							{{else}}
								<span class="fa fa-folder"></span>&nbsp;<spring:message code="expedient.estat.enum.TANCAT"/>
							{{/if}}
						{{/if}}

						{{if expedientEstat != null && expedientEstat.color!=null}}
							<span class="stateColor-{{:expedientEstat.color}}"></span>
						{{/if}}
					</script>
				</th>				
				<th data-col-name="createdDate" data-ordenable="true" data-converter="datetime" width="15%"><spring:message code="accio.massiva.list.column.datacreacio"/></th>
				<th data-col-name="createdBy.codiAndNom" data-ordenable="true" width="15%"><spring:message code="accio.massiva.list.column.creatper"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/expedient/{{:id}}/canviarEstat"/>" class="btn btn-default" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.boto.canviarEstat"/></a>	
					</script>
				</th>				
			</tr>
		</thead>
	</table>
	
</body>
</html>