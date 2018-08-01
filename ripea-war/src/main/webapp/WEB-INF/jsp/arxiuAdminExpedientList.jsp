<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title><spring:message code="arxiu.expedient.list.titol"/></title>
	<meta name="subtitle" content="${arxiu.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	<form:form action="" method="post" cssClass="form-inline well" commandName="expedientFiltreCommand">
		<rip:inputText name="nom" inline="true" placeholderKey="arxiu.expedient.list.placeholder.nom"/>
		<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="arxiu.expedient.list.placeholder.metaexp" inline="true"/>
		<rip:inputDate name="dataCreacioInici" inline="true" placeholderKey="arxiu.expedient.list.placeholder.creacio.inici"/>
		<rip:inputDate name="dataCreacioFi" inline="true" placeholderKey="arxiu.expedient.list.placeholder.creacio.fi"/>
		<div class="pull-right">
			<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
			<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
		</div>
	</form:form>
	<table id="taulaDades" data-toggle="datatable" data-url="<c:url value="/arxiuAdmin/${arxiu.id}/expedient/datatable"/>" data-default-order="3" data-default-dir="desc" data-filter="#expedientFiltreCommand" class="table table-bordered table-striped">
		<thead>
			<tr>
				<th data-col-name="codiPropietariEscriptoriPare" data-visible="false"></th>
				<th data-col-name="nom" width="35%"><spring:message code="arxiu.expedient.list.columna.nom"/></th>
				<th data-col-name="metaNode.nom" width="20%"><spring:message code="arxiu.expedient.list.columna.tipus"/></th>
				<th data-col-name="createdDate" data-converter="datetime" width="15%"><spring:message code="arxiu.expedient.list.columna.createl"/></th>
				<th data-col-name="nomPropietariEscriptoriPare" data-orderable="false" width="20%"><spring:message code="arxiu.expedient.list.columna.agafatper"/></th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									{{if !codiPropietariEscriptoriPare}}
									<li><a href="../../arxiuAdmin/${arxiu.id}/expedient/{{:id}}/agafar" data-toggle="modal"><span class="fa fa-lock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.agafar"/></a></li>
									{{else}}
									<li><a href="../../arxiuAdmin/${arxiu.id}/expedient/{{:id}}/alliberar" data-toggle="ajax" data-confirm="<spring:message code="arxiu.expedient.list.confirmacio.alliberar"/>"><span class="fa fa-unlock"></span>&nbsp;&nbsp;<spring:message code="comu.boto.alliberar"/></a></li>
									{{/if}}
								</ul>
							</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="/arxiuAdmin/unitat/${arxiu.unitatCodi}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
</body>