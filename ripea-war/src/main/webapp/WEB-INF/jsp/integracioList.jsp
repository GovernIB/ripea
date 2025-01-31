<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="integracio.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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

<c:url value="organgestor/filtrar" var="formAction"/>
	<form:form id="filtre" action="" method="post" cssClass="well" commandName="integracioFiltreCommand">
		<div class="row">
		
			<div class="col-md-2">
				<rip:inputText name="entitatCodi" inline="true" placeholderKey="integracio.list.codi.entitat"/>
			</div>
			<div class="col-md-3">
				<rip:inputDate name="dataInici" inline="true" placeholderKey="integracio.list.data.inici"/>
			</div>
			<div class="col-md-3">
				<rip:inputDate name="dataFi" inline="true" placeholderKey="integracio.list.data.fi"/>
			</div>		
			<div class="col-md-4">
				<rip:inputSelect  name="tipus"  optionEnum="IntegracioAccioTipusEnumDto" emptyOption="true" placeholderKey="integracio.list.tipus" inline="true"/>
			</div>					
			<div class="col-md-4">
				<rip:inputText name="descripcio" inline="true" placeholderKey="integracio.list.descripcio"/>
			</div>
			<div class="col-md-4">
				<rip:inputSelect  name="estat"  optionEnum="IntegracioAccioEstatEnumDto" emptyOption="true" placeholderKey="integracio.list.estat" inline="true"/>
			</div>
			
			<div class="col-md-2 pull-right">
				<div class="pull-right">
					<a href="<c:url value="/integracio/diagnostic"/>" class="btn btn-success" data-toggle="modal" data-refresh-pagina="false">
						<span class="fa fa-th-list"></span>&nbsp;<spring:message code="integracio.boto.diag"/>
					</a>
					<button id="btnNetejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<ul class="nav nav-tabs" role="tablist">
		<c:forEach var="integracio" items="${integracions}">
			<li<c:if test="${integracio.codi == codiActual}"> class="active"</c:if>><a href="<c:url value="/integracio/${integracio.codi}"/>"><spring:message code="sistema.extern.codi.${integracio.codi}"/></a></li>
		</c:forEach>
	</ul>
	<br/>
	<script id="rowhrefTemplate" type="text/x-jsrender">../nodeco/integracio/${codiActual}/{{:timestamp}}</script>
	<table id="missatges-integracions" data-toggle="datatable" data-url="<c:url value="/integracio/datatable"/>" class="table table-striped table-bordered" style="width:100%"
	data-rowhref-template="#rowhrefTemplate" data-rowhref-toggle="modal">
		<thead>
			<tr>
				<th data-col-name="excepcioMessage" data-visible="false"></th>
				<th data-col-name="excepcioStacktrace" data-visible="false"></th>
				<th data-col-name="data" data-orderable="false" data-converter="datetime"><spring:message code="integracio.list.columna.data"/></th>
				<th data-col-name="descripcio" data-orderable="false"><spring:message code="integracio.list.columna.descripcio"/></th>
				<th data-col-name="tipus" data-orderable="false"><spring:message code="integracio.list.columna.tipus"/></th>
				<th data-col-name="endpoint" data-orderable="false"><spring:message code="integracio.list.codi.endpoint"/></th>
				<th data-col-name="entitatCodi" data-orderable="false"><spring:message code="integracio.list.codi.entitat"/></th>
				<th data-col-name="tempsResposta" data-template="#cellTempsTemplate" data-orderable="false">
					<spring:message code="integracio.list.columna.temps.resposta"/>
					<script id="cellTempsTemplate" type="text/x-jsrender">{{:tempsResposta}} ms</script>
				</th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" data-orderable="false">
					<spring:message code="integracio.list.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'OK'}}
							<span class="label label-success"><span class="fa fa-check"></span>&nbsp;{{:estat}}</span>
						{{else}}
							<span class="label label-danger" title="{{:excepcioMessage}}"><span class="fa fa-warning"></span>&nbsp;{{:estat}}</span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="timestamp" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/integracio"/>/${codiActual}/{{:timestamp}}" class="btn btn-default" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>