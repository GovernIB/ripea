<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<rip:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="bustia.user.list.titol"/></title>
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
<script>
$(document).ready(function() {
	$('#netejarFiltre').click(function(e) {
		$('#estatContingut').val('PENDENT').change();
	});
	$('#taulaDades').on( 'draw.dt', function () {
		// Quan es refresca la llista consulta els pendents
		$.get( "bustiaUser/getNumPendents")
		.done(function( data ) {
			$('#bustia-pendent-count').text(data);
		})
	} );
});
</script>
</head>
<body>
	
	<form:form action="" method="post" cssClass="well" commandName="bustiaUserFiltreCommand">
		<div class="row">
			<div class="col-md-6">
				<rip:inputSelect name="bustia" optionItems="${bustiesUsuari}" optionValueAttribute="id" optionTextAttribute="nom" optionMinimumResultsForSearch="3" emptyOption="true" placeholderKey="bustia.list.filtre.bustia" inline="true"/>
			</div>
			<div class="col-md-3">
				<rip:inputText name="contingutDescripcio" inline="true" placeholderKey="bustia.list.filtre.contingut"/>
			</div>
			<div class="col-md-3">
				<rip:inputText name="remitent" inline="true" placeholderKey="bustia.list.filtre.remitent"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-3">
				<rip:inputDate name="dataRecepcioInici" inline="true" placeholderKey="bustia.list.filtre.data.rec.inical"/>
			</div>
			<div class="col-md-3">
				<rip:inputDate name="dataRecepcioFi" inline="true" placeholderKey="bustia.list.filtre.data.rec.final"/>
			</div>
			<div class="col-md-3">
				<rip:inputSelect name="estatContingut"  netejar="false" optionEnum="BustiaContingutFiltreEstatEnumDto" placeholderKey="bustia.list.filtre.estat" emptyOption="true" inline="true"/>
			</div>
			<div class="col-md-3 pull-right">
				<div class="pull-right">
					<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none"></button>
					<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>

	<table id="taulaDades" class="table table-bordered table-striped" style="width:100%"
		data-toggle="datatable"
		data-url="<c:url value="/bustiaUser/datatable"/>"
		data-filter="#bustiaUserFiltreCommand"
		data-info-type="search"
		data-default-order="8"
		data-default-dir="desc">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="pareId" data-visible="false"></th>
				<th data-col-name="error" data-visible="false"></th>
				<th data-col-name="procesAutomatic" data-visible="false"></th>
				<th data-col-name="tipus" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="nom" data-template="#contingutTemplate">
					<spring:message code="bustia.pendent.columna.contingut"/>
					<script id="contingutTemplate" type="text/x-jsrender">
						{{if tipus == 'REGISTRE'}}<span class="fa fa-book" title="<spring:message code="bustia.pendent.tipus.enum.REGISTRE"/>"></span>{{else tipus == 'EXPEDIENT'}}<span class="fa fa-briefcase" title="<spring:message code="bustia.pendent.tipus.enum.EXPEDIENT"/>"></span>{{else tipus == 'DOCUMENT'}}<span class="fa fa-file" title="<spring:message code="bustia.pendent.tipus.enum.DOCUMENT"/>"></span>{{/if}}
						{{:nom}}
						{{if error}}<span class="fa fa-warning text-danger pull-right" title="<spring:message code="bustia.pendent.registre.estat.error"/>"></span>{{/if}}
						{{if alerta}}
							<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="contingut.errors.registre.regles.segonpla"/>"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="remitent"><spring:message code="bustia.pendent.columna.remitent"/></th>
				<th data-col-name="recepcioData" data-converter="datetime" width="15%"><spring:message code="bustia.pendent.columna.recepcio.data"/></th>
				<th data-col-name="estatContingut" data-orderable="false" width="10%" data-template="#cellEstatTemplate">
					<spring:message code="bustia.pendent.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estatContingut == 'PENDENT'}}
							<spring:message code="bustia.contingut.filtre.estat.enum.PENDENT"/>
						{{else}}
							<spring:message code="bustia.contingut.filtre.estat.enum.PROCESSAT"/>
						{{/if}}
						{{if procesAutomatic}}
							<span class="fa fa-clock-o" title="<spring:message code="bustia.pendent.tooltip.amb.regles"/>"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="path" data-template="#cellPathTemplate" data-orderable="false">
					<spring:message code="bustia.pendent.columna.localitzacio"/>
					<script id="cellPathTemplate" type="text/x-jsrender">
						{{for path}}/
							{{if escriptori}}<span class="fa ${iconaEscriptori}" title="<spring:message code="contingut.path.escriptori"/>"></span>
							{{else expedient}}<span class="fa ${iconaExpedient}" title="<spring:message code="contingut.icona.expedient"/>"></span>
							{{else carpeta}}<span class="fa ${iconaCarpeta}" title="<spring:message code="contingut.icona.carpeta"/>"></span>
							{{else document}}<span class="fa ${iconaDocument}" title="<spring:message code="contingut.icona.document"/>"></span>
							{{else arxiv}}{{if #getIndex() == 0}}<span class="fa ${iconaUnitat}" title="<spring:message code="contingut.icona.unitat"/>"></span>{{else}}<span class="fa ${iconaArxiu}" title="<spring:message code="contingut.icona.arxiu"/>"></span>{{/if}}
							{{else bustia}}{{if #getIndex() == 0}}<span class="fa ${iconaUnitat}" title="<spring:message code="contingut.icona.unitat"/>"></span>{{else}}<span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span>{{/if}}{{/if}}
							{{if escriptori}}{{:createdBy.nom}}{{else}}{{:nom}}{{/if}}
						{{/for}}
					</script>
				</th>
				
				<th data-col-name="numComentaris" data-orderable="false" data-template="#cellPermisosTemplate" width="5%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						{{if !procesAutomatic}}
							<a href="./contingut/{{:id}}/comentaris" data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
						{{/if}}
					</script>
				</th>
				
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsContingutTemplate" width="10%">
					<script id="cellAccionsContingutTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if tipus == 'REGISTRE'}}
									<li><a href="./contingut/{{:pareId}}/registre/{{:id}}" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a></li>
								{{else}}
									<li><a href="./contingut/{{:id}}"><span class="fa fa-folder-open-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.obrir"/></a></li>
								{{/if}}
								<li><a href="./contingut/{{:id}}/log" data-toggle="modal"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.historial"/></a></li>
								{{if !procesAutomatic}}
									<c:if test="${potCrearExpedient}">
										<li><a href="./bustiaUser/{{:pareId}}/pendent/{{:tipus}}/{{:id}}/nouexp" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.nou.expedient"/>...</a></li>
									</c:if>
									<c:if test="${potModificarExpedient}">
										<li><a href="./bustiaUser/{{:pareId}}/pendent/{{:tipus}}/{{:id}}/addexp" data-toggle="modal"><span class="fa fa-sign-in"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.afegir.expedient"/>...</a></li>
									</c:if>
									<li><a href="./bustiaUser/{{:pareId}}/pendent/{{:id}}/reenviar" data-toggle="modal"><span class="fa fa-send"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.reenviar"/>...</a></li>
									{{if estatContingut == 'PENDENT'}}
										<li><a href="./bustiaUser/{{:pareId}}/pendent/{{:id}}/marcarProcessat" data-toggle="modal"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.marcar.processat"/>...</a></li>
									{{/if}}			
									<%--li><a href="../../bustiaUser/${bustia.id}/pendent/contingut/{{:id}}/agafar" data-toggle="modal"><span class="fa fa-thumbs-o-up"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.agafar"/></a></li--%>
								{{/if}}
								{{if alerta}}
									<li><a href="./bustiaUser/{{:pareId}}/pendent/{{:id}}/alertes" data-toggle="modal"><span class="fa fa-exclamation-triangle"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.llistat.alertes"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
</body>
</html>