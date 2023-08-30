<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
<head>
	<title><spring:message code="user.tasca.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">
	</script>
	
	<style type="text/css">
		li[class*="disabled"]{
			pointer-events: none;
		}
	</style>
</head>
<body>


	<c:url value="usuariTasca/filtrar" var="formAction"/>
	<form:form id="filtreForm" action="${ formAction }" method="post" cssClass="well" commandName="usuariTascaFiltreCommand">
		<div class="row">
		
			<div class="col-md-4">
				<rip:inputSelect name="estat" optionEnum="TascaEstatEnumDto" emptyOption="true" placeholderKey="expedient.tasca.list.columna.estat" inline="true"/>
			</div>
			
			<div class="col-md-4 pull-right">
				<div class="pull-right">
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
					<button type="submit" name="accio" value="filtrar" class="btn btn-primary default"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>


	<script id="rowhrefTemplate" type="text/x-jsrender">contingut/{{:expedient.id}}?tascaId={{:id}}</script>
	<table 
		data-toggle="datatable" 
		data-url="<c:url value="/usuariTasca/datatable"/>" 
		data-save-state="true"
		data-default-order="5" 
		data-default-dir="desc" 
		class="table table-striped table-bordered" 
		data-rowhref-template="#rowhrefTemplate"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="expedient.id" data-visible="false"></th>
				<th data-col-name="expedient.nomINumero" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.expedient"/></th>
				<th data-col-name="metaExpedientTasca.nom" data-orderable="false" width="15%"><spring:message code="expedient.tasca.list.columna.metaExpedientTasca"/></th>
				<th data-col-name="metaExpedientTasca.descripcio" data-orderable="false" width="30%" data-template="#cellTascaDescripcio" >
					<spring:message code="expedient.tasca.list.columna.metaExpedientDescripcio"/>
					<script id="cellTascaDescripcio" type="text/x-jsrender">
						<span title="{{:metaExpedientTasca.descripcio}}">{{:metaExpedientTascaDescAbrv}}</span>
					</script>
				</th>
				<th data-col-name="metaExpedientTascaDescAbrv" data-orderable="false" data-visible="false"></th>
				<th data-col-name="dataInici" data-converter="datetime" width="20%"><spring:message code="expedient.tasca.list.columna.dataInici"/></th>
				<th data-col-name="shouldNotifyAboutDeadline" data-visible="false"></th>
				<th data-col-name="agafada" data-visible="false"></th>
				<th data-col-name="responsableActual.codi" data-orderable="false" width="20%"><spring:message code="expedient.tasca.list.columna.responsable.actual"/></th>
				<th data-col-name="dataLimitString" width="20%" data-orderable="false" data-template="#cellTascaDeadlineTemplate" >
					<spring:message code="expedient.tasca.list.columna.dataLimit"/>
					<script id="cellTascaDeadlineTemplate" type="text/x-jsrender">
					{{if dataLimitString}}	
							{{if shouldNotifyAboutDeadline}}
									<span style="color: red;">
										{{:dataLimitString}}
										<span class="fa fa-clock-o"></span>
									</span>
							{{else}}
								{{:dataLimitString}}
							{{/if}}
					{{/if}}
						
					</script>
				</th>
				<th data-col-name="estat" data-template="#cellTascaEstatTemplate" data-orderable="false" width="10%">
					<spring:message code="expedient.tasca.list.columna.estat"/>
					<script id="cellTascaEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<spring:message code="expedient.tasca.estat.enum.PENDENT"/>
						{{else estat == 'INICIADA'}}
							<spring:message code="expedient.tasca.estat.enum.INICIADA"/>
						{{else estat == 'FINALITZADA'}}
							<spring:message code="expedient.tasca.estat.enum.FINALITZADA"/>
						{{else estat == 'CANCELLADA'}}
							<spring:message code="expedient.tasca.estat.enum.CANCELLADA"/>
						{{else estat == 'REBUTJADA'}}
							<spring:message code="expedient.tasca.estat.enum.REBUTJADA"/>
						{{/if}}
					</script>
				</th>
				<th data-col-name="numComentaris" data-orderable="false" data-template="#cellComentarisTemplate" width="1%">
					<script id="cellComentarisTemplate" type="text/x-jsrender">
						<a href="expedientTasca/{{:id}}/comentaris" data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAnotacioAccionsTemplate" width="1%">
					<script id="cellAnotacioAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url value="/contingut/{{:expedient.id}}?tascaId={{:id}}"/>"><span class="fa fa-folder-open-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.tramitar"/></a></li>
								{{if estat == 'PENDENT'}}					
									<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url value="/usuariTasca/{{:id}}/iniciar"/>"><span class="fa fa-play"></span>&nbsp;&nbsp;<spring:message code="comu.boto.iniciar"/></a></li>
									<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url value="/usuariTasca/{{:id}}/rebutjar"/>" data-maximized="true" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-reply"></span>&nbsp;&nbsp;<spring:message code="comu.boto.rebutjar"/></a></li>	 
								{{else}}						
									<li {{if agafada && usuariActualResponsable}}class="disabled"{{/if}}><a href="<c:url value="/usuariTasca/{{:id}}/finalitzar"/>" data-confirm="<spring:message code="expedient.tasca.finalitzar"/>"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="comu.boto.finalitzar"/></a></li>	
								{{/if}}						
							</ul>
						</div>
					</script>
				</th>
				
			</tr>
		</thead>
	</table>
</body>