<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="expedientPeticio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>
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


</script>
</head>
<body>
	<form:form id="expedientPeticioFiltreForm" action="" method="post" cssClass="well" commandName="expedientPeticioFiltreCommand">

		<div class="row">
			<div class="col-md-9">
				<div class="row">
					<div class="col-md-4">
						<rip:inputText name="numero" inline="true" placeholderKey="expedientPeticio.list.placeholder.numero"/>
					</div>	
					<div class="col-md-4">
						<rip:inputText name="extracte" inline="true" placeholderKey="expedientPeticio.list.placeholder.extracte"/>
					</div>	
					<div class="col-md-4">							
						<rip:inputText name="destinacio" inline="true" placeholderKey="expedientPeticio.list.placeholder.destinacio"/>
					</div>	
				</div>
				<div class="row">
					<div class="col-md-3">							
						<rip:inputSelect name="accioEnum" inline="true" optionEnum="ExpedientPeticioAccioEnumDto" emptyOption="true" placeholderKey="expedientPeticio.list.placeholder.accio"/>
					</div>	
					<div class="col-md-3">							
						<rip:inputDate name="dataInicial" inline="true" placeholderKey="expedientPeticio.list.placeholder.dataInicial"/>
					</div>	
					<div class="col-md-3">							
						<rip:inputDate name="dataFinal" inline="true" placeholderKey="expedientPeticio.list.placeholder.dataFinal"/>
					</div>	
					<div class="col-md-3">							
						<rip:inputSelect name="estat" inline="true" optionEnum="ExpedientPeticioEstatFiltreEnumDto" emptyOption="true" placeholderKey="expedientPeticio.list.placeholder.estat"/>
					</div>					
				</div>
			</div>
			
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<script id="rowhrefTemplate" type="text/x-jsrender">expedientPeticio/{{:id}}</script> 
	<table
		id="taulaDades"
		data-toggle="datatable" 
		data-url="<c:url value="/expedientPeticio/datatable"/>"  <%-- URL which will load rows of datatable  --%>
		class="table table-bordered table-striped table-hover" 
		data-default-order="3" <%-- default column number to be sorted  --%>
		data-default-dir="desc" <%-- default ordering direction  --%>
		data-save-state="true" 
		data-mantenir-paginacio="true"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="estat" data-visible="false"></th>
				<th data-col-name="metaExpedientNom"><spring:message code="expedientPeticio.list.columna.metaExpedientNom"/></th>
				<th data-col-name="expedientPeticioAccioEnumDto"><spring:message code="expedientPeticio.list.columna.accio"/></th>
				<th data-col-name="registre.identificador"><spring:message code="expedientPeticio.list.columna.numero"/></th>
				<th data-col-name="registre.data" data-type="datetime" data-converter="datetime"><spring:message code="expedientPeticio.list.columna.data"/></th>
				<th data-col-name="registre.extracte"><spring:message code="expedientPeticio.list.columna.extracte"/></th>
				<th data-col-name="registre.destiDescripcio"><spring:message code="expedientPeticio.list.columna.destiNom"/></th>
				<th data-col-name="estat"><spring:message code="expedientPeticio.list.columna.estat"/></th>

				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/expedientPeticio/{{:id}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
								{{if estat == 'PENDENT'}}
									<li><a href="<c:url value="/expedientPeticio/acceptar/{{:id}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-check"></span>&nbsp;<spring:message code="comu.boto.acceptar"/></a></li>
									<li><a href="<c:url value="/expedientPeticio/rebutjar/{{:id}}"/>" data-toggle="modal" data-maximized="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="comu.boto.rebutjar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>