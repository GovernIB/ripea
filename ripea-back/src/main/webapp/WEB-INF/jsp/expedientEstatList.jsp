<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<rip:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="expedient.estat.list.admin.titol"/>: ${metaExpedient.nom}</title>
	<meta name="subtitle" content="${metaExpedient.nom}"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
		<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/webjars/Sortable/1.4.2/Sortable.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#estats').on('dragupdate.dataTable', function (event, itemId, index) {
				$.ajax({
					url: "<c:url value="/ajax/expedientEstat/"/>" + ${metaExpedient.id} + "/" + itemId + "/move/" + index,
					async: false
				});
			});
		});
	</script>
</head>
<body>

	<script id="botonsTemplate" type="text/x-jsrender">
		<c:if test="${!bloquejarCamps}">
		<p style="text-align:right"><a class="btn btn-default" href="${metaExpedient.id}/new" data-toggle="modal" data-datatable-id="regles" data-refresh-pagina="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.estat.list.boto.nou"/></a></p>
		</c:if>
	</script>
	
	<c:set var="element" scope="request" value="estat"/>
	<jsp:include page="includes/procedimentElementsMenu.jsp"/>
	
	<table id="estats" data-toggle="datatable" data-url="<c:url value="/expedientEstat/${metaExpedient.id}/datatable"/>" ${!esRevisor ? 'data-drag-enabled="true"' : ''} class="table table-striped table-bordered" style="width:100%" ${!esRevisor ? 'data-botons-template="#botonsTemplate"' : ''}>

		<thead>
			<tr>
				<th data-col-name="ordre" data-visible="false"></th>

				<th data-col-name="codi" data-orderable="false"><spring:message code="expedient.estat.form.camp.codi"/></th>
				
				<th data-col-name="nom" data-orderable="false"><spring:message code="expedient.estat.form.camp.nom"/></th>

				<th data-col-name="inicial" data-template="#cellInicialTemplate" data-orderable="false">
					<spring:message code="expedient.estat.form.camp.inicial"/>
					<script id="cellInicialTemplate" type="text/x-jsrender">
						{{if inicial}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>				
				<th data-col-name="responsableCodi" data-orderable="false"><spring:message code="expedient.estat.form.camp.responsable"/></th>
				<th data-col-name="color" data-orderable="false" data-template="#cellColorTemplate" width="80px;">
					<spring:message code="expedient.estat.form.camp.color"/>
					<script id="cellColorTemplate" type="text/x-jsrender">
						<span class="color-legend" {{if color}}style="background-color: {{:color}};"{{else}}style="border: dashed 1px #AAA;"{{/if}}></span>
					</script>
				</th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<c:choose>
									<c:when test="${consultar}">
										<li><a href="${metaExpedient.id}/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.consultar"/></a></li>
									</c:when>
									<c:otherwise>
										<li><a href="${metaExpedient.id}/{{:id}}" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									</c:otherwise>
								</c:choose>
								<c:if test="${!bloquejarCamps}">
									<li><a href="${metaExpedient.id}/{{:id}}/delete"  data-toggle="ajax" data-confirm="<spring:message code="entitat.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</c:if>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<a href="<c:url value="${!esRevisor ? '/metaExpedient' : '/metaExpedientRevisio'}"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comu.boto.tornar"/></a>
</body>