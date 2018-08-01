<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="contingut.expedient.relacionar.form.titol"/></c:set>
<html>
<head>
	<title>${titol}</title>
	<rip:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.11/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.11/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.11/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script>
	$(document).ready(function() {
		$('#netejarFiltre').click(function(e) {
			e.preventDefault();
			$('#expedientFiltreCommand').trigger('reset');
			$("#expedientFiltreCommand select").select2("val", "");
			$('#estatFiltre').val('TOTS').change();
		});
		$('#expedientFiltreCommand').submit(function(e){
			e.preventDefault();
			$('#taulaDades').webutilDatatable('refresh');
		});	
		$('#expedientFiltreCommand').css('margin-bottom', '0px');
	});
	function relacionar(relacionatId) {
		$('#relacionatId').val(relacionatId);	
		$('#expedientRelacionarCommand').submit();
	}	
	</script>
</head>
<body>
	<div style="height: 550px;">
		<form:form action="" method="post" cssClass="well" commandName="expedientFiltreCommand">
			<div class="row">
				<div class="col-sm-2">
					<rip:inputText name="numero" inline="true" placeholderKey="expedient.list.user.placeholder.numero"/>
				</div>
				<div class="col-sm-2">
					<rip:inputText name="nom" inline="true" placeholderKey="expedient.list.user.placeholder.titol"/>
				</div>
				<div class="col-sm-3">
					<rip:inputSelect name="metaExpedientId" optionItems="${metaExpedients}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="expedient.list.user.placeholder.tipusExpedient" inline="true"/>
				</div>
				<div class="col-sm-3">
					<rip:inputSelect name="arxiuId" optionItems="${arxius}" optionValueAttribute="id" optionTextAttribute="nom" emptyOption="true" placeholderKey="expedient.list.user.placeholder.arxiu" inline="true"/>
				</div>
				<div class="col-sm-2">
					<rip:inputSelect name="estat" optionItems="${expedientEstatEnumOptions}" optionValueAttribute="value" emptyOption="true" optionTextKeyAttribute="text" placeholderKey="expedient.list.user.placeholder.estat" inline="true"/>
				</div>
			</div>
			<div class="row">
				<div class="col-sm-12 pull-right">
					<div class="pull-right">
						<button type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none;"></button>
						<button id="netejarFiltre" type="button" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
						<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
					</div>
				</div>
			</div>
		</form:form>
		<form:form action="" method="post" commandName="expedientRelacionarCommand">
			<div class="row">
				<div class="col-sm-12">
					<form:hidden path="entitatId"/>
					<form:hidden path="expedientId"/>
					<rip:inputHidden name="relacionatId" required="true"/>
				</div>
			</div>
			<table id="taulaDades" 
					data-toggle="datatable" 
					data-url="<c:url value="/expedient/${expedientId}/relacio/datatable"/>" 
					class="table table-bordered table-striped" 
					data-default-order="7" 
					data-default-dir="desc"
					data-filtre="#expedientFiltreCommand"
					style="width:100%">
				<thead>
					<tr>
						<th data-col-name="id" data-visible="false">Id</th>
						<th data-col-name="codiPropietariEscriptoriPare" data-visible="false"></th>
						<th data-col-name="metaNode.usuariActualWrite" data-visible="false"></th>
						<th data-col-name="numero"><spring:message code="expedient.list.user.columna.numero"/></th>
						<th data-col-name="nom" width="30%"><spring:message code="expedient.list.user.columna.titol"/></th>
						<th data-col-name="metaNode.nom" width="15%"><spring:message code="expedient.list.user.columna.tipus"/></th>
						<th data-col-name="arxiu.nom" width="15%"><spring:message code="expedient.list.user.columna.arxiu"/></th>
						<th data-col-name="createdDate" data-type="datetime" data-converter="datetime" width="15%"><spring:message code="expedient.list.user.columna.createl"/></th>
						<th data-col-name="estat" data-template="#cellEstatTemplate" width="10%">
							<spring:message code="expedient.list.user.columna.estat"/>
							<script id="cellEstatTemplate" type="text/x-jsrender">
								{{if estat == 'OBERT'}}<span class="fa fa-folder-open" title="Obert"></span>{{else}}<span class="fa fa-folder" title="Tancat">{{/if}}
							</script>
						</th>
						<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
							<script id="cellAccionsTemplate" type="text/x-jsrender">
								<button type="button" class="btn btn-success" onClick="relacionar({{:id}})"><span class="fa fa-link"></span> <spring:message code="comu.boto.relacionar"/></button>
							</script>
						</th>
					</tr>
				</thead>
			</table>
			<div id="modal-botons" class="well">
				<a href="<c:url value="/contenidor/${expedient.pare.id}"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
			</div>
		</form:form>
	</div>
</body>
</html>
