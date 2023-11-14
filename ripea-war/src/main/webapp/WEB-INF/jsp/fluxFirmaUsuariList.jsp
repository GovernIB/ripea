<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title><spring:message code="flux.firma.usuari.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<style type="text/css">

#firma-obligat {
	color: red;
	font-weight: bold;
}

</style>
<script type="text/javascript">

$(document).ready(function() {

	$.views.helpers({
        replaceText: function(destinataris) {
            return destinataris.replaceAll('OBLIGATORI_TEXT', '<spring:message code="flux.firma.usuari.list.columna.destinatari.obligatori"/>');
        }
    });
	
});

function closeModal(idModal) {
	
	$('#' + idModal).on('hidden.bs.modal', function (e) {
		
		var fluxSucces = localStorage.getItem('fluxSuccesDesc');
		var fluxError = localStorage.getItem('fluxErrorDesc');
		var alertDiv;
		
		if (fluxSucces) {
			alertDiv = '<div class="alert alert-success" role="alert"> \
						<a class="close" data-dismiss="alert">×</a> \
						<span>' + fluxSucces + '</span> \
					</div>';
		}
		
		if (fluxError) {
			alertDiv = '<div class="alert alert-danger" role="alert"> \
						<a class="close" data-dismiss="alert">×</a> \
						<span>' + fluxError + '</span> \
					</div>';
		}
		
		$('#contingut-missatges').find('.alert').remove();
		$('#contingut-missatges').prepend(alertDiv);
		
		$('#fluxosFirma').DataTable().ajax.reload();
		
		localStorage.removeItem('fluxSuccesDesc');
		localStorage.removeItem('fluxErrorDesc');
	});
		
}

</script>
</head>
<body>
	<table 
		id="fluxosFirma" 
		data-toggle="datatable" 
		data-url="<c:url value="/fluxusuari/datatable"/>" 
		data-search-enabled="false" 
		data-default-order="2" 
		data-default-dir="asc" 
		data-botons-template="#botonsTemplate" 
		class="table table-striped table-bordered" 
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="nom" width="25%"><spring:message code="flux.firma.usuari.list.columna.nom"/></th>
				<th data-col-name="destinataris" data-template="#cellDestinatarisTemplate">
					<spring:message code="flux.firma.usuari.list.columna.destinataris"/>
					<script id="cellDestinatarisTemplate" type="text/x-jsrender">
						{{:~replaceText(destinataris)}}
					</script>
				</th>
				<th data-col-name="createdDate" width="10%" data-type="datetime" data-converter="datetime"><spring:message code="flux.firma.usuari.list.columna.createl"/></th>
				
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="fluxusuari/{{:id}}" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-pencil"></span>&nbsp;&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="fluxusuari/{{:id}}/delete" data-confirm="<spring:message code="flux.firma.usuari.list.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="botonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="fluxusuari/new" data-toggle="modal" data-refresh-pagina="true"><span class="fa fa-plus"></span>&nbsp;<spring:message code="flux.firma.usuari.list.boto.nou"/></a></p>
	</script>
</body>