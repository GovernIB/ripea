<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="seguiment.portafirmes.list.titol"/></title>
	
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

	<c:url value="seguimentPortafirmes/filtrar" var="formAction"/>
	<form:form id="seguimentFiltreForm" action="${ formAction }" method="post" cssClass="well" commandName="seguimentFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="expedientNom" inline="true" placeholderKey="seguiment.list.filtre.camp.expedientNom"/>
			</div>		
			<div class="col-md-4">
				<rip:inputText name="documentNom" inline="true" placeholderKey="seguiment.list.filtre.camp.documentNom"/>
			</div>			
			<div class="col-md-4">
				<rip:inputSelect name="portafirmesEstat" optionEnum="DocumentEnviamentEstatEnumDto" emptyOption="true" placeholderKey="seguiment.list.filtre.camp.estatEnviament" inline="true"/>
			</div>
			<div class="col-md-4">
				<rip:inputDate name="dataEnviamentInici" inline="true" placeholderKey="seguiment.list.filtre.camp.dataEnviamentInici"/>
			</div>
			<div class="col-md-4">
				<rip:inputDate name="dataEnviamentFinal" inline="true" placeholderKey="seguiment.list.filtre.camp.dataEnviamentFinal"/>
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
		id="permisos" 
		data-toggle="datatable" 
		data-url="<c:url value="seguimentPortafirmes/datatable"/>" 
		data-search-enabled="false"
		data-default-order="5" 
		data-default-dir="desc" 
		class="table table-striped table-bordered" 
		data-rowhref-toggle="modal"
		style="width:100%">
		<thead> 
			<tr>
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="documentId" data-visible="false"></th>
				<th data-col-name="expedientNom" data-template="#cellExpedientLink"><spring:message code="seguiment.list.columna.expedientNom"/>
					<script id="cellExpedientLink" type="text/x-jsrender">
						<a href="<c:url value="/contingut/{{:expedientId}}"/>">{{:expedientNom}}</a>	
					</script>
				</th>
				<th data-col-name="documentNom"><spring:message code="seguiment.list.columna.documentNom"/></th>
				<th data-col-name="portafirmesEstat" data-renderer="enum(DocumentEnviamentEstatEnumDto)"><spring:message code="seguiment.list.columna.estatEnviament"/></th>
				<th data-col-name="dataEnviament" data-type="datetime" data-converter="datetime" nowrap><spring:message code="seguiment.list.columna.dataEnviament"/></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/document/{{:documentId}}/portafirmes/info?readOnly=true"/>" class="btn btn-default" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>
