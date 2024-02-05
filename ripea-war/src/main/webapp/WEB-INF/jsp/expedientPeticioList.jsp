<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="expedient.peticio.list.titol"/></title>
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
	<script type="application/javascript">

		function showEstat(element) {
			
			if (element.id == 'PENDENT') {
				return $('<span class="fa fa-spinner"></span><span> ' + element.text + '</span>');
			} else if(element.id == 'ACCEPTAT'){
				return $('<span class="fa fa-check"></span><span> ' + element.text + '</span>');
			} else if(element.id == 'REBUTJAT'){
				return $('<span class="fa fa-close"></span><span> ' + element.text + '</span>');
			}
		}
	</script>
</head>
<body>
	<form:form id="expedientPeticioFiltreForm" action="" method="post" cssClass="well" commandName="expedientPeticioFiltreCommand">

		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="numero" inline="true" placeholderKey="expedient.peticio.list.columna.numero.registre"/>
			</div>	
			<div class="col-md-4">
				<rip:inputText name="extracte" inline="true" placeholderKey="expedient.peticio.list.placeholder.extracte"/>
			</div>	
			<div class="col-md-4">					
				<c:url value="/organgestorajax/organgestorcodi" var="urlConsultaInicial"/>
				<c:url value="/organgestorajax/organgestorcodi" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
						name="destinacioCodi"  
						urlConsultaInicial="${urlConsultaInicial}"
						urlConsultaLlistat="${urlConsultaLlistat}"
						placeholderKey="expedient.peticio.list.placeholder.destinacio"
						suggestValue="codi"
						suggestText="codiINom"
						inline="true"/>	
			</div>	
		</div>
		<div class="row">
		<!--  optionMinimumResultsForSearch -->		
			<div class="col-md-4">					
				<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionMinimumResultsForSearch="1" optionValueAttribute="id" emptyOption="true" optionTextAttribute="codiSiaINom" placeholderKey="expedient.peticio.list.placeholder.metaExpedient" inline="true"/>
			</div>
<%--
			<div class="col-md-4">					
				<c:url value="/metaexpedientajax/metaexpedient" var="urlConsultaInicial"/>
				<c:url value="/metaexpedientajax/metaexpedients" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
						name="metaExpedientId"  
						urlConsultaInicial="${urlConsultaInicial}"
						urlConsultaLlistat="${urlConsultaLlistat}"
						placeholderKey="expedient.peticio.list.placeholder.metaExpedient"
						suggestValue="codi"
						suggestText="nom"
						inline="true"/>	
			</div>
 --%>			
			<div class="col-md-4">							
				<rip:inputDate name="dataInicial" inline="true" placeholderKey="expedient.peticio.list.placeholder.dataInicial"/>
			</div>	
			<div class="col-md-4">							
				<rip:inputDate name="dataFinal" inline="true" placeholderKey="expedient.peticio.list.placeholder.dataFinal"/>
			</div>	
			<div class="col-md-4">							
				<rip:inputSelect name="estat" inline="true" optionEnum="ExpedientPeticioEstatViewEnumDto" emptyOption="true" placeholderKey="expedient.peticio.list.placeholder.estat" templateResultFunction="showEstat"/>
			</div>		
			<div class="col-md-4">
				<rip:inputText name="interessat" inline="true" placeholderKey="expedient.list.user.placeholder.creacio.interessat"/>
			</div>						
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none;"></button>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	
	<script id="rowhrefTemplate" type="text/x-jsrender">nodeco/expedientPeticio/{{:id}}</script> 
	<table
		id="taulaDades"
		data-toggle="datatable" 
		data-url="<c:url value="/expedientPeticio/datatable"/>"  <%-- URL which will load rows of datatable  --%>
		class="table table-bordered table-striped table-hover" 
		data-default-order="1" <%-- default column number to be sorted  --%>
		data-default-dir="desc" <%-- default ordering direction  --%>
		data-rowhref-template="#rowhrefTemplate"
		data-rowhref-toggle="modal"
		data-rowhref-maximized="true"
		data-save-state="true" 
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="registre.identificador" width="13%"><spring:message code="expedient.peticio.list.columna.numero.registre"/></th>
				<th data-col-name="registre.data" data-type="datetime" data-converter="datetime"><spring:message code="expedient.peticio.list.columna.data"/></th>
				<th data-col-name="registre.extracte" width="15%"><spring:message code="expedient.peticio.list.columna.extracte"/></th>
				<th data-col-name="registre.destiCodiINom"><spring:message code="expedient.peticio.list.columna.destiNom"/></th>
				<th data-col-name="procedimentCodiSiaINom" data-orderable="false"><spring:message code="expedient.peticio.list.columna.metaExpedientNom"/></th>
				<th data-col-name="pendentEnviarDistribucio" data-visible="false"></th>
				<th data-col-name="pendentEnviarDistribucio" data-visible="false"></th>
				<th data-col-name="interessatsResum" data-orderable="false"><spring:message code="expedient.peticio.list.columna.interessats"/></th>
				<th data-col-name="estatView" width="10%" data-orderable="false" data-template="#cellEstatTemplate">
					<spring:message code="expedient.peticio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">

						{{if estatView == 'PENDENT'}}
							<span class='fa fa-spinner'></span> <spring:message code="expedient.peticio.estat.view.enum.PENDENT"/>
						{{else estatView == 'ACCEPTAT'}}
							<span class='fa fa-check'></span> <spring:message code="expedient.peticio.estat.view.enum.ACCEPTAT"/>
						{{else estatView == 'REBUTJAT'}}
							<span class='fa fa-close'></span> <spring:message code="expedient.peticio.estat.view.enum.REBUTJAT"/>
						{{/if}}

						{{:dataActualitzacioStr}}
						{{if pendentEnviarDistribucio}}
							<span title="<spring:message code="expedient.peticio.controller.canviar.estat.anotacio.distribucio.avis"/>"
							class="fa fa-exclamation-triangle text-danger"></span>
						{{/if}}
					</script>
				</th>
				
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/expedientPeticio/{{:id}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
								{{if estatView == 'PENDENT' && !pendentEnviarDistribucio}}
									<li><a href="<c:url value="/expedientPeticio/acceptar/{{:id}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.acceptar"/></a></li>
									<li><a href="<c:url value="/expedientPeticio/rebutjar/{{:id}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.rebutjar"/></a></li>
									<li><a href="<c:url value="/expedientPeticio/canviarProcediment/{{:id}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								{{/if}}
								{{if estatView == 'ACCEPTAT'}}
									<li><a href="contingut/{{:expedientId}}"><span class="fa fa-folder-open-o"></span>&nbsp;&nbsp;<spring:message code="expedient.peticio.list.btn.expedient"/></a></li>
								{{/if}}
								{{if estatView == 'REBUTJAT'}}
									<li><a href="<c:url value="/expedientPeticio/rebutjadaInfo/{{:id}}"/>" data-toggle="modal"><span class="fa fa-info"></span>&nbsp;&nbsp;<spring:message code="expedient.peticio.list.btn.rebutjadaInfo"/></a></li>
								{{/if}}
								<c:if test="${isRolActualAdmin}">
									{{if pendentEnviarDistribucio}}
										<li><a href="<c:url value="/expedientPeticio/canviarEstatDistribucio/{{:id}}"/>"><span class="fa fa-mail-forward"></span>&nbsp;&nbsp;<spring:message code="expedient.peticio.list.btn.canviar.estat.anotacio.distribucio"/>{{:expedientId}}</a></li>
									{{/if}}
								</c:if>
							</ul>
						</div>
					</script>
				</th>
				<th data-col-name="dataActualitzacioStr" data-visible="false"></th>
			</tr>
		</thead>
	</table>
</body>