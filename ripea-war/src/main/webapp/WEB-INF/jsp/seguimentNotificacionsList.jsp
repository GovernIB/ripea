<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="seguiment.notificacions.list.titol"/></title>
	
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
//################################################## document ready START ##############################################################
$(document).ready(function() {
	
	$('#taula').on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"seguimentNotificacions/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	$('#taula').one('draw.dt', function () {
		$('#seleccioAll').on('click', function() {
			$.get(
					"seguimentNotificacions/select",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taula').webutilDatatable('refresh');
					}
			);
			return false;
		});
		$('#seleccioNone').on('click', function() {
			$.get(
					"seguimentNotificacions/deselect",
					function(data) {
						$("#seleccioCount").html(data);
						$('#taula').webutilDatatable('select-none');
						$('#taula').webutilDatatable('refresh');
					}
			);
			return false;
		});
	});

    $('#nomesAmbErrorBtn').click(function() {
        nomesAmbError = !$(this).hasClass('active');
        $('#nomesAmbError').val(nomesAmbError);
    })
	
});//################################################## document ready END ##############################################################


</script>	
</head>
<body>

	<c:url value="seguimentNotificacions/filtrar" var="formAction"/>
	<form:form id="seguimentFiltreForm" action="${ formAction }" method="post" cssClass="well defaultFilterEnter" commandName="seguimentNotificacionsFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<rip:inputSelect name="enviamentTipus" optionEnum="NotificaEnviamentTipusEnumDto" emptyOption="true" placeholderKey="seguiment.list.filtre.camp.enviamentTipus" inline="true"/>
			</div>		
			<div class="col-md-4">
				<rip:inputText name="concepte" inline="true" placeholderKey="seguiment.list.filtre.camp.concepte"/>
			</div>				
			<div class="col-md-2">
				<rip:inputSelect name="notificacioEstat" optionEnum="NotificacioSeguimentEstatEnumDto" emptyOption="true" placeholderKey="seguiment.list.filtre.camp.estatEnviament" inline="true"/>
			</div>			
			<div class="col-md-2">
				<rip:inputDate name="dataInici" inline="true" placeholderKey="seguiment.list.filtre.camp.dataEnviamentInici"/>
			</div>
			<div class="col-md-2">
				<rip:inputDate name="dataFinal" inline="true" placeholderKey="seguiment.list.filtre.camp.dataEnviamentFinal"/>
			</div>


		</div>
		<div class="row">	
			<div class="col-md-2">
				<rip:inputText name="interessat" inline="true" placeholderKey="seguiment.list.filtre.camp.interessat"/>
			</div>		
			<div class="col-md-2">					
				<c:url value="/expedientajax/expedient" var="urlConsultaExpInicial"/>
				<c:url value="/expedientajax/expedient" var="urlConsultaExpLlistat"/>
				<rip:inputSuggest 
 					name="expedientId"  
 					urlConsultaInicial="${urlConsultaExpInicial}"
 					urlConsultaLlistat="${urlConsultaExpLlistat}"
 					usePathVariable="false"
					placeholderKey="seguiment.list.filtre.camp.expedient"
 					suggestValue="id"
 					suggestText="nomINumero"
					inline="true"/>	
			</div>			
			<div class="col-md-2">
				<rip:inputText name="documentNom" inline="true" placeholderKey="seguiment.list.filtre.camp.documentNom"/>
			</div>			
			<div class="col-md-6">
				<c:url value="/organgestorajax/organgestor" var="urlConsultaInicial"/>
				<c:url value="/organgestorajax/organgestor" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
 					name="organId"  
 					urlConsultaInicial="${urlConsultaInicial}"
 					urlConsultaLlistat="${urlConsultaLlistat}"
 					placeholderKey="metaexpedient.form.camp.organgestor"
 					suggestValue="id"
 					suggestText="codiINom"
 					inline="true"/>				
			</div>
		</div>
		<div class="row">				
			<div class="col-md-6">
				<rip:inputSelect name="procedimentId" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="codiSiaINom" optionMinimumResultsForSearch="3" emptyOption="true" placeholderKey="accio.massiva.list.filtre.tipusexpedient" inline="true"/>
			</div>			
			<div class="col-md-2 pull-right">
				<button	id="nomesAmbErrorBtn" title="<spring:message code="notificacio.list.filtre.camp.nomesAmbError"/>" class="btn btn-default <c:if test="${seguimentNotificacionsFiltreCommand.nomesAmbError}">active</c:if>" data-toggle="button"><span class="fa fa-warning"></span></button>
          		<rip:inputHidden name="nomesAmbError"/>
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>			
		</div>
		
		
		
		
	</form:form>

	<script id="botonsTemplate" type="text/x-jsrender">
		<div class="btn-group pull-right">
			<button id="seleccioAll" title="<spring:message code="expedient.list.user.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
			<button id="seleccioNone" title="<spring:message code="expedient.list.user.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>
			<a id="processar" class="btn btn-default" href="./seguimentNotificacions/actualitzarEstatMassiu" data-refresh-pagina="true">
				<span id="seleccioCount" class="badge">${fn:length(seleccio)}</span> <spring:message code="enviament.info.accio.ectualitzar.estat"/>
			</a>
		</div>
	</script>


	<table 
		id="taula" 
		data-toggle="datatable" 
		data-url="<c:url value="seguimentNotificacions/datatable"/>" 
		data-search-enabled="false"
		data-default-order="5" 
		data-default-dir="desc" 
		class="table table-striped table-bordered" 
		data-rowhref-toggle="modal"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		style="width:100%">
		<thead> 
			<tr> 
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="documentId" data-visible="false"></th>
				<th data-col-name="notificacioIdentificador" data-visible="false"></th>
				<th data-col-name="error" data-visible="false"></th>
				<th data-col-name="enviamentDatatEstat" data-visible="false"></th>
				<th data-col-name="dataEnviament" data-type="datetime" data-converter="datetime" nowrap><spring:message code="seguiment.list.columna.dataEnviament"/></th>
				<th data-col-name="organ" data-orderable="false" width="15%"><spring:message code="seguiment.list.columna.organ"/></th>
				<th data-col-name="procediment" data-orderable="false"><spring:message code="seguiment.list.columna.procediment"/></th>			
				<th data-col-name="expedientNom" data-template="#cellExpedientLink"><spring:message code="seguiment.list.columna.expedientNom"/>
					<script id="cellExpedientLink" type="text/x-jsrender">
						<a href="<c:url value="/contingut/{{:expedientId}}"/>">{{:expedientNom}}</a>	
					</script>
				</th>
				<th data-col-name="concepte" data-orderable="false"><spring:message code="seguiment.list.columna.concepte"/></th>	
				<th data-col-name="documentNom"><spring:message code="seguiment.list.columna.documentNom"/></th>
				<th data-col-name="dataFinalitzacio" data-type="datetime" data-converter="datetime" width="5%" nowrap><spring:message code="seguiment.list.columna.dataFinalitzacio"/></th>
				<th data-col-name="notificacioEstat" data-template="#cellNotificacioEstatTemplate" data-orderable="false" width="5%">
					<spring:message code="contingut.enviament.columna.estat"/>
					<script id="cellNotificacioEstatTemplate" type="text/x-jsrender">
					{{if notificacioEstat == 'PENDENT'}}
						<span class="label label-warning"><span class="fa fa-clock-o"></span> <spring:message code="notificacio.notificacioEstat.enum.PENDENT"/></span>
						{{if error}} <span class="fa fa-warning text-danger" title="<spring:message code="contingut.enviament.error"/>"></span> {{/if}}
					{{else notificacioEstat == 'ENVIADA'}}
						{{if error}}
							<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.ENVIADA"/></span>
						{{else}}
							<span class="label label-info"><span class="fa fa-envelope-o"></span> <spring:message code="notificacio.notificacioEstat.enum.ENVIADA"/></span>
						{{/if}}
					{{else notificacioEstat == 'ENVIADA_AMB_ERRORS'}}
						{{if error}}
							<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.ENVIADA_AMB_ERRORS"/></span>
						{{else}}
							<span class="label label-info"><span class="fa fa-envelope-o"></span> <spring:message code="notificacio.notificacioEstat.enum.ENVIADA_AMB_ERRORS"/></span>
						{{/if}}
					{{else notificacioEstat == 'REGISTRADA'}}
						{{if error}}
							<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.REGISTRADA"/></span>
						{{else}}
							<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.REGISTRADA"/></span>
						{{/if}}
					{{else notificacioEstat == 'FINALITZADA'}}
						{{if error}}
								<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.FINALITZADA"/></span>
							{{else}}
								<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.FINALITZADA"/></span>
						{{/if}}
					{{else notificacioEstat == 'FINALITZADA_AMB_ERRORS'}}
						{{if error}}
								<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.FINALITZADA_AMB_ERRORS"/></span>
							{{else}}
								<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.FINALITZADA_AMB_ERRORS"/></span>
						{{/if}}
					{{else notificacioEstat == 'PROCESSADA'}}
						{{if error}}
								<span class="label label-danger"><span class="fa fa-warning"></span> <spring:message code="notificacio.notificacioEstat.enum.PROCESSADA"/></span>
							{{else}}
								<span class="label label-success"><span class="fa fa-check"></span> <spring:message code="notificacio.notificacioEstat.enum.PROCESSADA"/></span>
						{{/if}}
					{{/if}}
					{{if notificacioEstat == 'PROCESSADA' || notificacioEstat == 'FINALITZADA'}}
						<p class="estat_{{:id}}"  style="display:inline">
							{{if enviamentDatatEstat == 'NOTIB_PENDENT'}}
								(<spring:message code="notificacio.enviamentEstat.enum.NOTIB_PENDENT"/>)
							{{else enviamentDatatEstat == 'NOTIB_ENVIADA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.NOTIB_ENVIADA"/>)
							{{else enviamentDatatEstat == 'ABSENT'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ABSENT"/>)
							{{else enviamentDatatEstat == 'ADRESA_INCORRECTA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ADRESA_INCORRECTA"/>)
							{{else enviamentDatatEstat == 'DESCONEGUT'}}
								(<spring:message code="notificacio.enviamentEstat.enum.DESCONEGUT"/>)
							{{else enviamentDatatEstat == 'ENVIADA_CI'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ENVIADA_CI"/>)
							{{else enviamentDatatEstat == 'ENVIADA_DEH'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ENVIADA_DEH"/>)
							{{else enviamentDatatEstat == 'ENVIAMENT_PROGRAMAT'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ENVIAMENT_PROGRAMAT"/>)
							{{else enviamentDatatEstat == 'ENTREGADA_OP'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ENTREGADA_OP"/>)
							{{else enviamentDatatEstat == 'ERROR_ENTREGA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ERROR_ENTREGA"/>)
							{{else enviamentDatatEstat == 'EXPIRADA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.EXPIRADA"/>)
							{{else enviamentDatatEstat == 'EXTRAVIADA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.EXTRAVIADA"/>)
							{{else enviamentDatatEstat == 'LLEGIDA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.LLEGIDA"/>)
							{{else enviamentDatatEstat == 'MORT'}}
								(<spring:message code="notificacio.enviamentEstat.enum.MORT"/>)
							{{else enviamentDatatEstat == 'NOTIFICADA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.NOTIFICADA"/>)
							{{else enviamentDatatEstat == 'PENDENT'}}
								(<spring:message code="notificacio.enviamentEstat.enum.PENDENT"/>)
							{{else enviamentDatatEstat == 'PENDENT_ENVIAMENT'}}
								(<spring:message code="notificacio.enviamentEstat.enum.PENDENT_ENVIAMENT"/>	)
							{{else enviamentDatatEstat == 'PENDENT_SEU'}}
								(<spring:message code="notificacio.enviamentEstat.enum.PENDENT_SEU"/>)
							{{else enviamentDatatEstat == 'PENDENT_CIE'}}
								(<spring:message code="notificacio.enviamentEstat.enum.PENDENT_CIE"/>)
							{{else enviamentDatatEstat == 'PENDENT_DEH'}}
								(<spring:message code="notificacio.enviamentEstat.enum.PENDENT_DEH"/>)
							{{else enviamentDatatEstat == 'REBUTJADA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.REBUTJADA"/>)
							{{else enviamentDatatEstat == 'SENSE_INFORMACIO'}}
								(<spring:message code="notificacio.enviamentEstat.enum.SENSE_INFORMACIO"/>)
							{{else enviamentDatatEstat == 'ANULADA'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ANULADA"/>)
							{{else enviamentDatatEstat == 'ENVIAT_SIR'}}
								(<spring:message code="notificacio.enviamentEstat.enum.ENVIAT_SIR"/>)
							{{/if}}
						</p>
					{{/if}}
					</script>
				</th>				
				
				<th data-col-name="destinataris" data-orderable="false"><spring:message code="seguiment.list.columna.destinataris"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="1%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						{{if notificacioEstat != 'PROCESSADA'}}
							<a href="<c:url value="/document/notificacio/actualitzarEstat/{{:notificacioIdentificador}}"/>" class="btn btn-default" data-refresh-pagina="true"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="enviament.info.accio.ectualitzar.estat"/></a>	
						{{/if}}
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>
