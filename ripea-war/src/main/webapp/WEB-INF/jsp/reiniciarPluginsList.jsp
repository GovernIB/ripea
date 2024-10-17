<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/ripea" prefix="rip"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="titol"><spring:message code="decorator.menu.reinici.plugin"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<rip:modalHead/>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#pluginsTable').on('draw.dt', function (e, settings) {
				webutilModalAdjustHeight();
			});
		});
		function reiniciarPlugin(codi) {
			if (!codi || codi==null) codi='xx';
			$.ajax({
				type: 'GET',
				url: '<c:url value="/plugin/restart/' + codi + '"/>',
				async: true,
				success: function(data) {
					webutilRefreshMissatges();
					$('#pluginsTable').webutilDatatable('refresh');
				},
				error: function() {
					console.log("Error en la petició AJAX reiniciant el plugin.");
				}
			});
		}
	</script>
</head>

<body>

	<table 
		id="pluginsTable" 
		data-toggle="datatable"
		data-url="<c:url value="/plugin/datatable"/>" 
		data-search-enabled="false"
		data-botons-template="#pluginsTableBotoTots"
		data-save-state="true"
		data-default-order="0"
		data-default-dir="asc"
		data-paging-enabled="false"
		class="table table-striped table-bordered" 
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="codi" data-visible="false"></th>
				<th data-col-name="texte" data-orderable="false" width="90%"><spring:message code="decorator.menu.reinici.plugin.plugin"/></th>
				<th data-col-name="codi" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<a href="Javascript:reiniciarPlugin('{{:codi}}');" class="btn btn-warning"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="decorator.menu.reinici.plugin.boto"/></a>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="pluginsTableBotoTots" type="text/x-jsrender">
	<div style="float: right;">
		<a href="Javascript:reiniciarPlugin();" class="btn btn-warning" style="margin-right: 20px;"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="decorator.menu.reinici.plugin.boto.tots"/></a>
	</div>
	</script>
	<div id="modal-botons">
		<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="accio.massiva.boto.tancar"/></a>
	</div>
</body>
</html>