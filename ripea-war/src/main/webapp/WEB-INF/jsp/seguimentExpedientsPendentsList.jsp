<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="seguiment.expedientsPendents.list.titol"/></title>
	
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
	
	
	
</head>
<body>

	<c:url value="seguimentExpedientsPendents/filtrar" var="formAction"/>
	<form:form id="expedientPeticioFiltreForm" action="${ formAction }" method="post" cssClass="well" commandName="expedientPeticioFiltreCommand">

		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="numero" inline="true" placeholderKey="expedient.peticio.list.placeholder.numero"/>
			</div>	
			<div class="col-md-4">
				<rip:inputText name="extracte" inline="true" placeholderKey="expedient.peticio.list.placeholder.extracte"/>
			</div>	
			<div class="col-md-4">					
				<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionValueAttribute="id" emptyOption="true" optionTextAttribute="nom" placeholderKey="expedient.peticio.list.placeholder.metaExpedient" inline="true"/>
			</div>			
		</div>
		<div class="row">
			<div class="col-md-4">							
				<rip:inputDate name="dataInicial" inline="true" placeholderKey="expedient.peticio.list.placeholder.dataInicial"/>
			</div>	
			<div class="col-md-4">							
				<rip:inputDate name="dataFinal" inline="true" placeholderKey="expedient.peticio.list.placeholder.dataFinal"/>
			</div>					
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<table 
		id="permisos" 
		data-toggle="datatable" 
		data-url="<c:url value="seguimentExpedientsPendents/datatable"/>" 
		data-search-enabled="false"
		data-default-order="3" 
		data-default-dir="desc" 
		class="table table-striped table-bordered" 
		data-rowhref-toggle="modal"
		style="width:100%">
		<thead> 
			<tr>
				<th data-col-name="registre.identificador"><spring:message code="expedient.peticio.list.columna.numero"/></th>
				<th data-col-name="registre.extracte"><spring:message code="expedient.peticio.list.columna.extracte"/></th>
				<th data-col-name="metaExpedientNom" data-orderable="false"><spring:message code="expedient.peticio.list.columna.metaExpedientNom"/></th>
				<th data-col-name="registre.data" data-type="datetime" data-converter="datetime"><spring:message code="expedient.peticio.list.columna.data"/></th>
			</tr>
		</thead>
	</table>
</body>
</html>
