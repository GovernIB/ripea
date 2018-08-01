<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:choose>
	<c:when test="${contingut.expedient}"><c:set var="titol"><spring:message code="contingut.errors.titol.expedient"/></c:set></c:when>
	<c:when test="${contingut.document}"><c:set var="titol"><spring:message code="contingut.errors.titol.document"/></c:set></c:when>
</c:choose>
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
	
	<script type="text/javascript">
		
		$( document ).ready(function() {
		    
			$( document ).ajaxComplete(function() {
				webutilModalAdjustHeight();
			});
			
			$('#taulaAlertes').on('rowinfo.dataTable', function(e, td, rowData) {
				$(td).append(rowData.error);
				webutilModalAdjustHeight();
			});
			
		});
	
	</script>
</head>
<body>	
	<table 
		id="taulaAlertes"
		data-toggle="datatable"
		data-url="<c:url value="/bustiaUser/${bustiaId}/pendent/${contingutId}/alertes/datatable"/>"
		data-paging-enabled="false"
		class="table table-bordered table-striped"
		data-row-info="true"
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="error" data-visible="false">#</th>
				<th data-col-name="text" data-orderable="false" width="90%"><spring:message code="contingut.error.llista.text"/></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsRegistreTemplate" width="10%">
					<script id="cellAccionsRegistreTemplate" type="text/x-jsrender">
						<button class="btn btn-default" href="alertes/{{:id}}/llegir" data-toggle="ajax" data-confirm="<spring:message code="contingut.error.confirmacio.llegir"/>">
							<span class="fa fa-file-text-o"></span>&nbsp;&nbsp;<spring:message code="contingut.error.boto.llegir"/>
						</button>
					</script>
				</th>
			</tr>
		</thead>
	</table>
		
	<div id="modal-botons" class="well">
		<a href="<c:url value="/bustiaUser"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>
