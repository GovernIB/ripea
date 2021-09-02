<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="seguiment.tasques.list.titol"/></title>
	
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

	<c:url value="seguimentTasques/filtrar" var="formAction"/>
	<form:form id="seguimentFiltreForm" action="${ formAction }" method="post" cssClass="well" commandName="seguimentFiltreCommand">
		<div class="row">
			<div class="col-md-4">
				<rip:inputText name="expedientNom" inline="true" placeholderKey="seguiment.list.filtre.camp.expedientNom"/>
			</div>
			<div class="col-md-4">
				<rip:inputSelect name="metaExpedientTascaId" placeholderKey="expedient.tasca.form.camp.metaExpedientTasca" optionItems="${metaexpTasques}" optionValueAttribute="id" optionTextAttribute="nom" optionMinimumResultsForSearch="0" emptyOption="true" inline="true" />
			</div>
			<div class="col-md-4">
				<rip:inputSelect name="tascaEstat" optionEnum="TascaEstatEnumDto" emptyOption="true" placeholderKey="seguiment.list.filtre.camp.estatEnviament" inline="true"/>
			</div>
			<div class="col-md-4">
				<c:url value="/userajax/usuariDades" var="urlConsultaInicial"/>
				<c:url value="/userajax/usuarisDades" var="urlConsultaLlistat"/>
				<rip:inputSuggest name="responsableCodi" urlConsultaInicial="${urlConsultaInicial}" urlConsultaLlistat="${urlConsultaLlistat}" placeholderKey="seguiment.list.filtre.camp.responsable" suggestValue="codi" suggestText="nom" inline="true"/>
			</div>				
			<div class="col-md-4">
				<rip:inputDate name="dataInici" inline="true" placeholderKey="seguiment.list.filtre.camp.dataInici"/>
			</div>
			<div class="col-md-4">
				<rip:inputDate name="dataFinal" inline="true" placeholderKey="seguiment.list.filtre.camp.dataFinal"/>
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
		data-url="<c:url value="seguimentTasques/datatable"/>" 
		data-search-enabled="false"
		data-default-order="5" 
		data-default-dir="desc" 
		class="table table-striped table-bordered" 
		data-rowhref-toggle="modal"
		style="width:100%">
		<thead> 
			<tr>
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="expedientNom" data-template="#cellExpedientLink"><spring:message code="seguiment.list.columna.expedientNom"/>
					<script id="cellExpedientLink" type="text/x-jsrender">
						<a href="<c:url value="/contingut/{{:expedientId}}"/>">{{:expedientNom}}</a>	
					</script>
				</th>						
				<th data-col-name="tascaNom"><spring:message code="seguiment.list.columna.tasca"/></th>
				<th data-col-name="tascaEstat" data-renderer="enum(TascaEstatEnumDto)"><spring:message code="seguiment.list.columna.estatEnviament"/></th>
				<th data-col-name="responsableNom"><spring:message code="seguiment.list.columna.responsable"/></th>
				<th data-col-name="data" data-type="datetime" data-converter="datetime" nowrap><spring:message code="seguiment.list.columna.dataInici"/></th>
				<th data-col-name="id" data-visible="false">
				</th>
			</tr>
		</thead>
	</table>
</body>
</html>
