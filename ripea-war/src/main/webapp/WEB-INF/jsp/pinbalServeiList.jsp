<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="pinbalServei.list.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript">
	</script>
</head>
<body>
	<table 
		id="entitats" 
		data-toggle="datatable" 
		data-url="pinbalServei/datatable" 
		data-search-enabled="false"
		data-default-order="1" 
		data-default-dir="asc" 
		data-save-state="true"
		class="table table-striped table-bordered" 
		style="width:100%"
		data-rowhref-toggle="modal">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false">#</th>
				<th data-col-name="codi" data-orderable="false" width="5%"><spring:message code="pinbalServei.list.columna.codi"/></th>
				<th data-col-name="nom" data-orderable="false" data-template="#cellNomTemplate" width="50%">
					<spring:message code="pinbalServei.list.columna.nom"/>
					<script id="cellNomTemplate" type="text/x-jsrender">
						{{if codi == 'SVDDGPCIWS02'}}
							<spring:message code="pinbal.servei.SVDDGPCIWS02"/>
						{{else codi == 'SVDDGPVIWS02'}}
							<spring:message code="pinbal.servei.SVDDGPVIWS02"/>
						{{else codi == 'SVDCCAACPASWS01'}}
							<spring:message code="pinbal.servei.SVDCCAACPASWS01"/>
						{{else codi == 'SVDSCDDWS01'}}
							<spring:message code="pinbal.servei.SVDSCDDWS01"/>
						{{else codi == 'SCDCPAJU'}}
							<spring:message code="pinbal.servei.SCDCPAJU"/>
						{{else codi == 'SVDSCTFNWS01'}}
							<spring:message code="pinbal.servei.SVDSCTFNWS01"/>
						{{else codi == 'SVDCCAACPCWS01'}}
							<spring:message code="pinbal.servei.SVDCCAACPCWS01"/>
						{{else codi == 'Q2827003ATGSS001'}}
							<spring:message code="pinbal.servei.Q2827003ATGSS001"/>
						{{else codi == 'SVDDELSEXWS01'}}
							<spring:message code="pinbal.servei.SVDDELSEXWS01"/>
						{{else codi == 'SCDHPAJU'}}
							<spring:message code="pinbal.servei.SCDHPAJU"/>
						{{else codi == 'NIVRENTI'}}
							<spring:message code="pinbal.servei.NIVRENTI"/>
						{{else codi == 'ECOT103'}}
							<spring:message code="pinbal.servei.ECOT103"/>
						{{else codi == 'SVDDGPRESIDENCIALEGALDOCWS01'}}
							<spring:message code="pinbal.servei.SVDDGPRESIDENCIALEGALDOCWS01"/>
						{{/if}}
					</script>
				</th>
				

				<th data-col-name="docPermesosString" data-orderable="false" ><spring:message code="pinbalServei.list.columna.docs.permesos"/></th>
				<th data-col-name="id" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<a href="pinbalServei/{{:id}}" class="btn btn-default" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>

</body>