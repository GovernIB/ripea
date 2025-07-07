<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="seguiment.pinbal.list.titol"/></title>
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
	<c:url value="seguimentPinbal/filtrar" var="formAction"/>
	<form:form id="seguimentFiltreForm" action="${ formAction }" method="post" cssClass="well" modelAttribute="seguimentConsultaFiltreCommand">
		<div class="row">
			<div class="col-md-4">
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
			<div class="col-md-4">
				<rip:inputSelect 
					name="metaExpedientId" 
					optionItems="${metaExpedients}"
					optionValueAttribute="id" 
					optionTextAttribute="codiSiaINom"
					optionMinimumResultsForSearch="3" 
					emptyOption="true"
					placeholderKey="accio.massiva.list.filtre.tipusexpedient" 
					inline="true" />
			</div>	
			<div class="col-md-4">
				<rip:inputSelect 
					name="servei" 
					optionItems="${pinbalServeiEnumOptions}"
					optionValueAttribute="codi" 
					optionTextAttribute="codiNom"
					emptyOption="true" 
					placeholderKey="seguiment.list.filtre.camp.serveiSCSP"
					inline="true" 
					optionMinimumResultsForSearch="0"/>
			</div>		
			<div class="col-md-4">					
				<c:url value="/userajax/usuari" var="urlConsultaInicial"/>
				<c:url value="/userajax/usuaris" var="urlConsultaLlistat"/>
				<rip:inputSuggest 
						name="createdByCodi"  
						urlConsultaInicial="${urlConsultaInicial}"
						urlConsultaLlistat="${urlConsultaLlistat}"
						placeholderKey="seguiment.list.filtre.camp.iniciatPer"
						suggestValue="codi"
						suggestText="codiAndNom"
						inline="true"/>	
			</div>						
			<div class="col-md-4">
				<rip:inputDate name="dataInici" inline="true" placeholderKey="seguiment.list.filtre.camp.dataInici"/>
			</div>
			<div class="col-md-4">
				<rip:inputDate name="dataFinal" inline="true" placeholderKey="seguiment.list.filtre.camp.dataFinal"/>
			</div>
			<div class="col-md-4">
				<rip:inputSelect 
					name="estat" 
					optionEnum="ConsultaPinbalEstatEnumDto"
					emptyOption="true" 
					placeholderKey="seguiment.list.filtre.camp.estat" 
					inline="true" />
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
		data-url="<c:url value="seguimentPinbal/datatable"/>" 
		data-search-enabled="false"
		data-default-order="7" 
		data-default-dir="desc" 
		class="table table-striped table-bordered" 
		data-rowhref-toggle="modal"
		style="width:100%">
		<thead> 
			<tr>
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="documentId" data-visible="false"></th>
				<th data-col-name="error" data-visible="false"></th>
				<th data-col-name="expedientNumeroTitol" data-template="#cellExpedientLink"><spring:message code="seguiment.list.columna.expedient"/>
					<script id="cellExpedientLink" type="text/x-jsrender">
						<a href="<c:url value="/contingut/{{:expedientId}}"/>">{{:expedientNumeroTitol}}</a>	
					</script>
				</th>
				<th data-col-name="procedimentCodiNom"><spring:message code="seguiment.list.columna.procediment"/></th>
				<th data-col-name="servei"><spring:message code="seguiment.list.columna.serveiSCSP"/></th>
				<th data-col-name="createdBy"><spring:message code="seguiment.list.columna.iniciatPer"/></th>
				<th data-col-name="createdDate" data-type="datetime" data-converter="datetime" nowrap><spring:message code="seguiment.list.columna.data"/></th>
				<th data-col-name="estat" width="6%" data-template="#cellEstat"><spring:message code="seguiment.list.columna.estat"/>
					<script id="cellEstat" type="text/x-jsrender">
						{{if estat == 'TRAMITADA'}}
						 <span class="fa fa fa-check"></span> <spring:message code="consulta.pinbal.estat.enum.TRAMITADA"/> 
						{{else estat == 'ERROR'}}
							<span title="{{:error}}"><span class="fa fa-exclamation-triangle"></span> <spring:message code="consulta.pinbal.estat.enum.ERROR"/></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="documentTitol" data-orderable="false" data-template="#cellDocumentLink"><spring:message code="seguiment.list.columna.document"/>
					<script id="cellDocumentLink" type="text/x-jsrender">
						<a href="<c:url value="/contingut/{{:documentId}}"/>">{{:documentTitol}}</a>	
					</script>
				</th>								
			</tr>
		</thead>
	</table>
</body>
</html>